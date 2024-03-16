//----------------------------------------------------------------------------
// $RCSfile: Server.java,v $
// $Revision: 1.1.2.8 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//----------------------------------------------------------------------------

package org.relayirc.chatengine;
import org.relayirc.core.*;
import org.relayirc.util.Debug;

import java.net.*;
import java.io.*;
import java.util.*;
import java.beans.*;

//////////////////////////////////////////////////////////////////////////////
/**
 * <p>Manages a connection to an IRC server and handles incoming
 * messages by creating channel objects, routing messages to channel
 * objects and routing events to server listeners.</p>
 *
 * <p>After you have constructed a Server, add a ServerListener
 * to be notified of server connection and disconnection, channel
 * joins and parts and status messages from the Server. Connect to
 * the Server using the connect() method and use the sendJoin()
 * and sendPart() commands to join and leave chat channels. When a
 * channel is joined, your listener will be informed and you may add
 * a ChannelListener to the channel object so that you can repond
 * to messages, bans, kicks, etc. from that channel.</p>
 *
 * @author David M. Johnson.
 * @version $Revision: 1.1.2.8 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code:     Relay IRC Chat Server<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s):    No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class Server implements IChatObject, Serializable {

   static final long serialVersionUID = 129734017070381266L;

   private String _appName = "Relay IRC Chat Engine";
   private String _appVersion = "0.8.1 (Unreleased)";

   private User          _user;             // *THE* user
   private String        _name =  "";       // Server host name
   private String        _title =  "";      // Display name of server
   private String        _desc =  "";       // Arbitrary description of server
   private int           _ports[] = null;   // Currently, only 1st is used
   private String        _network = "";     // Network name (e.g. EFNet)
   private boolean       _favorite = false; // Is in favorites list?
   private int           _channelCount = 0; // Num. channels on server

   private transient IRCConnection _connection;
   private transient ChannelSearch _search = null;

   /** Internal IRCConnectionListener implementaton. */
   private transient _ServerMux _mux = null;

   /** Internal ChannelListener implementation. */
   private transient _ChannelMux _channelMux = null;

	/** Channel-specific IRCConnectionListener objects, keyed by channel name */
   private transient Hashtable _channels = null;

   /** Colleciton of ServerListeners. */
   private transient Vector _listeners = null;

   /** User objects that are waiting for WHOIS information. */
   private transient Hashtable _userWaitingList = null;

   /** Property change support: not fully implemented! */
   private transient PropertyChangeSupport _propChangeSupport = null;

   //==========================================================================
   // Constructors
   //==========================================================================

   /**
     * Construct a server by specifying server name, port, network and port.
	  * @param name    IRC chat server hostname (e.g. irc.mindspring.com).
	  * @param port    Default IRC server port (e.g. 6667).
	  * @param network Name of server's network (e.g. EFNet)
	  * @param desc    Title or display name of server.
     */
   public Server(String name, int port, String network, String title) {

      _name = name;
      _network = network;
      _title = title;

      _ports = new int[1];
      _ports[0] = port;
   }
   //--------------------------------------------------------------------------
   /** String representation for display purposes. */
   public String toString() {
      if (_network!=null && _network.length()>0)
         return _name+" ("+_network+")";
      else
         return _name;
   }

   //=========================================================================
   // Accessors
   //=========================================================================

   //------------------------------------------------------------------
   // Property: AppName

   /** Get app name to be reported to version queries. */
   public String getAppName() {return _appName;}

   /** Set app name to be reported to version queries. */
   public void setAppName(String name) {_appName = name;}

   //------------------------------------------------------------------
   // Property: AppVersion

   /** Get app version to be reported to version queries. */
   public String getAppVersion() {return _appVersion;}

   /** Set app verion to be reported to version queries. */
   public void setAppVersion(String version) {_appVersion = version;}

   //------------------------------------------------------------------
   // Property: Connected

   /** Check connection status and returns true if connected. */
   public boolean isConnected() {
      if (_connection == null)
         return false;
      else
         return (_connection.getState()==IRCConnection.CONNECTED);
   }
   //------------------------------------------------------------------
   /**
    * Check connection status and returns true if server
    * is in the process of connecting.
    */
   public boolean isConnecting() {
      if (_connection == null)
         return false;
      else
         return (_connection.getState()==IRCConnection.CONNECTING);
   }
   //---------------------------------------------------------------
   // Property: Description

   public String getDescription() {
      return _desc;
   }
   public void setDescription(String desc) {
      _desc = desc;
   }
   //---------------------------------------------------------------
   // Property: Favorite

   public boolean isFavorite() {
      return _favorite;
   }
   public void setFavorite(boolean fave) {
      boolean old = _favorite;
      _favorite=fave;
      getPropChangeSupport().firePropertyChange(
         "Favorite",new Boolean(old),new Boolean(_favorite));
   }
   //---------------------------------------------------------------
   // Property: Title

   public String getTitle() {
      return _name;
   }
   public void setTitle(String title) {
      _title = title;
   }
   //---------------------------------------------------------------
   // Property: Name

   /** Host name of server. For example: irc.sprynet.com. */
   public String getName() {
      return _name;
   }
   public void setName(String name) {
      _name = name;
   }
   //---------------------------------------------------------------
   // Property: Network

   public String getNetwork() {
      return _network;
   }
   public void setNetwork(String network) {
      String old = _network;
      _network = network;
      getPropChangeSupport().firePropertyChange("Network",old,_network);
   }
   //------------------------------------------------------------------
   // Property: Nick

   /** Get nick name currently in use */
   public String getNick() {
      return _connection.getNick();
   }
   //---------------------------------------------------------------
   // Property: Port and ports

   /** Get default port: first port in port array. */
   public int getPort() {
      if (_ports==null) {
         _ports = new int[1];
         _ports[0] = 0;
      }
      return _ports[0];
   }
   /** Set default port: first port in port array. */
   public void setPort(int port) {
      int old = 0;
      if (_ports==null) {
         _ports = new int[1];
      }
      else {
         old = _ports[0];
      }
      _ports[0] = port;
      getPropChangeSupport().firePropertyChange(
         "Port",new Integer(old),new Integer(_ports[0]));
   }
   /** Get port array. */
   public int[] getPorts() {
      return _ports;
   }
   /** Set port array. */
   public void setPorts(int ports[]) {
      _ports = ports;
   }

   //================================================================
   // Listener support
   //================================================================

   /** Add a property change listener: not fully implemented. */
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      getPropChangeSupport().addPropertyChangeListener(listener);
   }
   //---------------------------------------------------------------
   /** Remove a property change listener: not fully implemented. */
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      getPropChangeSupport().removePropertyChangeListener(listener);
   }
   //---------------------------------------------------------------
   /** Add a server listener. */
   public synchronized void addServerListener(ServerListener listener) {
      getListeners().addElement(listener);
   }
   //---------------------------------------------------------------
   /** Remove a server listener. */
   public synchronized void removeServerListener(ServerListener listener) {
      getListeners().removeElement(listener);
   }

   //================================================================
   // Public methods
   //================================================================

   /** Connect to IRC server that was specified in the constructor. */
   public void connect(
      String nick, String altNick, String userName, String fullName) {

      connect(new User(nick,altNick,userName,fullName));
   }
   //------------------------------------------------------------------
   /** Connect to IRC server that was specified in the constructor. */
   public void connect(User user) {

      if (!isConnected() && !isConnecting()) { 
         _user = user; 

         // MGENT FIX (bug #211402) - previous connection logic:
         // Allow any new connection to synch to the closure of
         // the previous connection.
         IRCConnection previousConnection = _connection; 

         _connection = new IRCConnection(
            _name, 
            _ports[0], 
            _user.getName(), 
            _user.getAltNick(), 
            _user.getUserName(), 
            _user.getFullName()); 

         _connection.setIRCConnectionListener(getServerMux()); 
         _connection.open(previousConnection); 
      } 
      else { 
         fireStatusEvent("Cannot connect: already connected."); 
      } 
   }
   //------------------------------------------------------------------
   /**
    * Disconnect from server by sending a QUIT to the server, closing
    * the socket to the server and then waiting for the message loop
    * thread to die.
    */
   public void disconnect() {
      if (isConnected()) {
         fireStatusEvent("Sending quit command...");
         _connection.writeln("QUIT :goodbye!");
      }
      else if (isConnecting()) {
         fireStatusEvent("Forcing a disconnect...");
         _connection.close();
      }
      else {
         fireStatusEvent("Cannot disconnect: not connected.");
      }
   }
   //------------------------------------------------------------------
   /** Start a channel search using the specified channel search object. */
   public void startChannelSearch(ChannelSearch search) {
      _search = search;

      String cmd = "LIST ";
      /* if (search.getMinUsers()>Integer.MIN_VALUE
                                   && search.getMaxUsers()<Integer.MAX_VALUE) {
         cmd = cmd + "<" + search.getMaxUsers() + ",";
         cmd = cmd + ">" + search.getMinUsers();
      }
      else if (search.getMinUsers()>Integer.MIN_VALUE) {
         cmd = cmd + ">" + search.getMinUsers();
      }
      else if (search.getMaxUsers()<Integer.MAX_VALUE) {
         cmd = cmd + "<" + search.getMaxUsers();
      } */
      sendCommand(cmd);
   }

   //================================================================
   // Send-command methods
   //================================================================

   /** Send command string directly to server */
   public synchronized void sendCommand( String str ) {
      if (_connection.getState() != IRCConnection.CONNECTED) return;
      try {
         _connection.writeln(str+"\r\n");
      }
      catch (Exception e) {
         fireStatusEvent("Error sending command");
      }
   }
   //------------------------------------------------------------------
   /** 
    * Send private message. 
	* @param chan Channel name or user name that is target of message.
	* @param message The message to be sent.
    */ 
   public synchronized void sendPrivateMessage( String chan, String message ) {

      if (_connection.getState() != IRCConnection.CONNECTED) {
         fireStatusEvent("Ignoring PRIVMSG command, not connected.");
         return;
      }

      if (!chan.startsWith("#")) {

         // This is a /msg message, so make sure channel object exists for it
         Channel channel = getChannel(chan,false);
		 if (channel == null) {
            // Create channel object and make it look real 
            channel = getChannel(chan,true);
            channel.getChannelMux().onJoin("",getNick(),chan,false);
	        channel.getChannelMux().onJoins(chan+" "+getNick(),"");
			channel.getChannelMux().onPrivateMessage(getNick(),chan,message);
		 }
	  }
      sendCommand("PRIVMSG "+chan+" :"+message); 
   }
   //------------------------------------------------------------------
   /**
    * Join specified channel by sending JOIN command to IRC server,
    * adding channel object to server's channel collection and notifying
    * listeners of channel join.
    */
   public synchronized void sendJoin(Channel chan) {

      if (_connection.getState() != IRCConnection.CONNECTED) {
         fireStatusEvent("Ignoring JOIN command, not connected.");
         return;
      }

      if (!getChannels().contains(chan)) {

         // Add channel to server's channel collection
         addChannel(chan);

         // Send join request to server
         sendCommand("JOIN "+chan.getName());
      }
      else {
         fireStatusEvent("Ignoring JOIN command, already in channel.");
         return;
      }
   }
   //------------------------------------------------------------------
   /**
    * Join specified channel by sending a JOIN command to the IRC server
    * creating a new channel object and notifying listeners of channel join.
    */
   public synchronized void sendJoin(String name) {
      if (_connection.getState() != IRCConnection.CONNECTED) {
         fireStatusEvent("Ignoring JOIN command, not connected.");
         return;
      }

      // See if channel object already exists
      name = name.trim().toLowerCase();
      Channel chan = (Channel)getChannels().get(name);

      if (chan == null) {
         sendCommand("JOIN "+name);
      }
      else {
         fireStatusEvent("Ignoring JOIN command, already in channel.");
      }
   }
   //------------------------------------------------------------------
   /** Send new nick name request. */
   public void sendNick(String nick) {
      _connection.sendNick(nick);
   }
   //------------------------------------------------------------------
   /** Send channel part, notify listeners and remove channel. */
   public void sendPart(Channel chan) {
      sendPart(chan.getName());
   }
   //------------------------------------------------------------------
   /** Send channel part, notify listeners and remove channel. */
   public void sendPart(String chanName) {
      sendCommand("PART "+chanName);
   }
   //------------------------------------------------------------------
   /** Send version information to server */
   public void sendVersion(String user) {
      sendCommand("PRIVMSG "+user+" "+":\001VERSION\001");
   }
   //------------------------------------------------------------------
   /** Send quit message to server.
     */
   public void sendQuit( String str ) {
      sendCommand("QUIT :"+str);
   }
   //------------------------------------------------------------------
   /** Send WHIOS for user specified by nick name. */
   public void sendWhoIs(String nick) {
      sendCommand("WHOIS "+nick);
   }
   //------------------------------------------------------------------
   /** Send WHIOS for user specified by User object, object will be
     * updated when WHOIS information is received.
     */
   public void sendWhoIs(User user) {
      getUserWaitingList().put(user.getNick(),user);

      String nick = user.getNick();
      if (nick.startsWith("@") && nick.length()>1) {
         nick = nick.substring(1);
      }
      sendCommand("WHOIS "+nick);
   }

   //================================================================
   // Implementation methods
   //================================================================

   /** Send status message to all ServerListeners, used internally. */
   public void fireStatusEvent(String msg) {
      final ServerEvent event = new ServerEvent(this,msg);
      notifyListeners(new _ServerEventNotifier() {
         public void notify(ServerListener listener)
            {listener.onStatus(event);}
      });
   }
   //------------------------------------------------------------------
   /** Determine if channel view is active. */
   private boolean isChannelActive( String channel ) {
      Channel chan = (Channel)getChannels().get(channel);
      if (chan!=null)
         return true;
      else
         return false;
   }
   //------------------------------------------------------------------
   /** Returns view for specified channel, or null if there is none. */
   public Channel getChannel(String name) {
      return getChannel(name,false);
   }
   //------------------------------------------------------------------
   /**
    * Returns channel object specified by name, creates one if necessary.
    * @param name Name of channel.
    * @param force Force creation of channel if it does not exist.
    */
   public synchronized Channel getChannel(String name, boolean force) {

      name = name.trim().toLowerCase();
      Channel chan = (Channel)getChannels().get(name);

      if ( chan==null && force) {

         // Channel object does not exist and we need it, so create it
         chan = new Channel(name,this);
         addChannel(chan);
      }
      return chan;
   }
   //------------------------------------------------------------------
   /**
     * Remove user from waiting list and return to caller.
     */
   private User pullUserFromWaitingList(String nick) {
      User user = getUserFromWaitingList(nick);
      getUserWaitingList().remove(nick);
      return user;
   }
   //------------------------------------------------------------------
   /** Gets user from waiting list, creates new user if user does not
     * exist in waiting list.
     */
   private User getUserFromWaitingList(String nick) {
      User user = (User)getUserWaitingList().get(nick);
      if (user == null) {
         user = new User(nick);
         getUserWaitingList().put(nick,user);
      }
      else {
         getUserWaitingList().remove(user);
      }
      return user;
   }
   //-------------------------------------------------------------------
   private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      try {
         in.defaultReadObject();
      }
      catch (NotActiveException e) {
         e.printStackTrace();
      }
   }

   //================================================================
   // Private Accessors
   //================================================================

   /** Add channel to list of channels managed by this server. */
   private void addChannel(Channel chan) {

      // Add channel object to hash, key it by name
      getChannels().put(chan.getName(),chan);

	  chan.setConnected(true);

      // Let listeners know that channel was added
      final ServerEvent event = new ServerEvent(this,chan);
      notifyListeners(new _ServerEventNotifier() {
         public void notify(ServerListener listener)
          {listener.onChannelAdd(event);}
      });
         
      // Listen for important events
      chan.addChannelListener(getChannelMux());

   }
   //------------------------------------------------------------------
   /** Remove all channel objects from management by this server. */
   private void removeAllChannels() {
      Enumeration keys = getChannels().keys();
      while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         Channel chan = (Channel)getChannels().get(key);
         removeChannel(chan);
      }
   }
   //------------------------------------------------------------------
   /** Remove a channel object from management by this server. */
   private void removeChannel(Channel chan) {

      getChannels().remove(chan.getName());
      chan.removeChannelListener(getChannelMux());
      chan.getChannelMux().onDisconnect();
   }

   //---------------------------------------------------------------
   /** Use lazy initializers for all transients. */
   private Hashtable getChannels() {
      if (_channels != null)
         return _channels;
      else
         return _channels = new Hashtable();
   }
   //---------------------------------------------------------------
   /** Use lazy initializers for all transients. */
   private Vector getListeners() {
      if (_listeners != null)
         return _listeners;
      else
         return _listeners = new Vector();
   }
   //---------------------------------------------------------------
   /** Use lazy initializers for all transients. */
   private Hashtable getUserWaitingList() {
      if (_userWaitingList != null)
         return _userWaitingList;
      else
         return _userWaitingList = new Hashtable();
   }
   //---------------------------------------------------------------
   /** Use lazy initializers for all transients. */
   private PropertyChangeSupport getPropChangeSupport() {
      if (_propChangeSupport != null)
         return _propChangeSupport;
      else
         return _propChangeSupport = new PropertyChangeSupport(this);
   }
   //---------------------------------------------------------------
   /** Use lazy initializers for all transients. */
   private _ServerMux getServerMux() {
      if (_mux != null)
         return _mux;
      else
         return _mux = new _ServerMux();
   }
   //---------------------------------------------------------------
   /** Use lazy initializers for all transients. */
   private _ChannelMux getChannelMux() {
      if (_channelMux != null)
         return _channelMux;
      else
         return _channelMux = new _ChannelMux();
   }

   //================================================================
   // Thread safe notification architecture
   //================================================================

   interface _ServerEventNotifier {
      public void notify(ServerListener listener);
   }
   //------------------------------------------------------------------
   private synchronized void notifyListeners(
      _ServerEventNotifier notifier) {

      int count = getListeners().size();
      Debug.println("Server.notifyListeners "+count+" listeners");

      for (int i=0; i<getListeners().size(); i++) {
         ServerListener listener =
            (ServerListener)getListeners().elementAt(i);
         notifier.notify(listener);
      }
   }

   //////////////////////////////////////////////////////////////////////////
   //////////////////////////////////////////////////////////////////////////

   /** IRCConnectionListener implementation that "multiplexes" messages
     * and directs them to the right channels objects. */
   private class _ServerMux implements IRCConnectionListener {

      //------------------------------------------------------------------
      public void onAction( String user, String chan, String txt ) {
         getChannel(chan,true).getChannelMux().onAction(user,chan,txt);
      }
      //------------------------------------------------------------------
      public void onBan( String banned, String chan, String banner ) {
         getChannel(chan,true).getChannelMux().onBan(banned,chan,banner);
      }
      //------------------------------------------------------------------
      public void onClientInfo(String orgnick) {
         String response = "NOTICE "+orgnick+
            " :\001CLIENTINFO :Supported queries are VERSION and SOURCE\001";
         _connection.writeln(response);
      }
      //------------------------------------------------------------------
      public void onClientSource(String orgnick) {
         String response = "NOTICE "+orgnick+
            " :\001SOURCE :http://relayirc.netpedia.net\001";
         _connection.writeln(response);
      }
      //------------------------------------------------------------------
      public void onClientVersion(String orgnick) {
         // Tell them everything
         String osdesc =
            System.getProperty("os.name").replace(':','-')+"/"+
            System.getProperty("os.version").replace(':','-')+"/"+
            System.getProperty("os.arch").replace(':','-');
         String vmdesc = "Java "+
            System.getProperty("java.version").replace(':','-')+" ("+osdesc+")";

         String response = "NOTICE "+orgnick+" :\001VERSION "+
            _appName+":"+_appVersion+":"+vmdesc+"\001";

         fireStatusEvent("\nSending VERSION information to "+orgnick+"\n");
         _connection.writeln(response);
      }
      //----------------------------------------------------------------------
      public void onConnect() {
         final ServerEvent event = new ServerEvent(Server.this);
         notifyListeners(new _ServerEventNotifier() {
            public void notify(ServerListener listener)
				   {listener.onConnect(event);}
         });
      }
      //----------------------------------------------------------------------
      public void onDisconnect() {

         // Notify server listeners
         final ServerEvent event = new ServerEvent(Server.this);
         notifyListeners(new _ServerEventNotifier() {
            public void notify(ServerListener listener)
				   {listener.onDisconnect(event);}
         });

         // Remove all channels from our care
         removeAllChannels();

      }
      //------------------------------------------------------------------
      public void onIsOn( String[] usersOn ) {
         final ServerEvent event = new ServerEvent(Server.this,usersOn);

         notifyListeners(new _ServerEventNotifier() {
            public void notify(ServerListener listener)
				   {listener.onIsOn(event);}
         });
      }
      //------------------------------------------------------------------
      public void onInvite( 
         String origin, String originNick, String target, String chan ) {
         final ServerEvent event 
            = new ServerEvent(Server.this,originNick, target,chan);

         notifyListeners(new _ServerEventNotifier() {
            public void notify(ServerListener listener)
				   {listener.onInvite(event);}
         });
      }
      //------------------------------------------------------------------
      public void onJoin( String user, String nick,
                          String chan, boolean create ) {
         getChannel(chan,true).getChannelMux().onJoin(user,nick,chan,create);
      }
      //------------------------------------------------------------------
      public void onJoins( String users, String chan) {
         getChannel(chan,true).getChannelMux().onJoins(users,chan);
      }
      //------------------------------------------------------------------
      public void onKick( String kicked, String chan,
                          String kicker, String txt ) {
         getChannel(chan,true).getChannelMux().onKick(kicked,chan,kicker,txt);
      }
      //------------------------------------------------------------------
      public void onMessage(String message) {
         fireStatusEvent(message+"\n");
      }
      //------------------------------------------------------------------
      public void onPrivateMessage(String orgnick, String chan, String txt) {
         Channel channel = null;
         if (chan.startsWith("#")) {
            channel = getChannel(chan,true);
         }
		 else {
            // User has messaged us, use their nick as the channel name
            channel = getChannel(orgnick,true);
			channel.getChannelMux().onJoin("",orgnick,chan,false);
			channel.getChannelMux().onJoin("",getNick(),chan,false);
         }
		 channel.getChannelMux().onPrivateMessage( orgnick, chan, txt );
      }
      //------------------------------------------------------------------
      public void onNick( String user, String oldnick, String newnick ) {
         fireStatusEvent(oldnick+" now known as "+newnick);

         for (Enumeration e=getChannels().elements() ; e.hasMoreElements() ;) {
            Channel chan = (Channel)e.nextElement();
            chan.getChannelMux().onNick(user,oldnick,newnick);
         }
      }
      //------------------------------------------------------------------
      public void onNotice(String text) {
         fireStatusEvent("NOTICE: "+text);
      }
      //------------------------------------------------------------------
      public void onPart( String user, String nick, String chan ) {
         Channel channel = getChannel(chan,false);
		 if (channel != null) {
            // If user is not our user then send part message
            if (!nick.equals( getNick() )) {
		       channel.getChannelMux().onPart(user,nick,chan);
			}
			else {
               // User is our user, send part then disconnect channel
		       channel.getChannelMux().onPart(user,nick,chan);
			   channel.disconnect();
			}
         }	
      }
      //------------------------------------------------------------------
      public void onOp( String oper, String chan, String oped ) {
         getChannel(chan,true).getChannelMux().onOp(oper,chan,oped);
      }
      //------------------------------------------------------------------
      public void onParsingError(String message) {
         fireStatusEvent("Error parsing message: "+message);
      }
      //------------------------------------------------------------------
      public void onPing(String params) {
         _connection.writeln("PONG "+params.trim()+"\r\n");
      }
      //------------------------------------------------------------------
      public void onStatus(String msg) {
         fireStatusEvent(msg);
      }
      //------------------------------------------------------------------
      public void onVersionNotice(String orgnick,
                                  String origin, String version) {
         fireStatusEvent("\nVERSION Information for "+orgnick+"("+origin+")\n");
      }
      //------------------------------------------------------------------
      public void onQuit( String user, String nick, String txt ) {
         fireStatusEvent(nick+" ("+user+") has QUIT: "+txt+"\n");
         for (Enumeration e=getChannels().elements() ; e.hasMoreElements() ;) {
            Channel chan = (Channel)e.nextElement();
            chan.getChannelMux().onQuit(user,nick,txt);
         }
      }
      //------------------------------------------------------------------
      /** Respond to server version reply. */
      public void onReplyVersion(String version) {
         fireStatusEvent("Server Version: "+version);
         /*try {
            System.out.println("0: "+((_tok)tokens.elementAt(0)).token);
            System.out.println("1: "+((_tok)tokens.elementAt(1)).token);
            System.out.println("2: "+((_tok)tokens.elementAt(2)).token);
            System.out.println("3: "+((_tok)tokens.elementAt(3)).token);
            System.out.println("4: "+((_tok)tokens.elementAt(4)).token);
         } catch (Exception e) {}*/
      }
      //------------------------------------------------------------------
      /** Respond to */
      public void onReplyListUserChannels(int channelCount) {
         _channelCount = channelCount;
      }
      //------------------------------------------------------------------
      /** Respond to channel-list-start reply. */
      public void onReplyListStart() {
         if (_search!=null) _search.searchStarted(_channelCount);
      }
      //------------------------------------------------------------------
      /** Respond to channel list item reply. */
      public void onReplyList(String channel, int userCount, String topic) {
         Channel channelObject =
                          new Channel(channel,topic,userCount,Server.this);
         _search.processChannel(channelObject);
      }
      //------------------------------------------------------------------
      /** Respond to end-of-channel-list reply. */
      public void onReplyListEnd() {
         if (_search!=null) {
            _search.searchEnded();
            _search.setComplete(true);
            _search = null;
         }
      }
      //------------------------------------------------------------------
      /**
       * Respond to RPL_LUSERCLIENT messages which usually look like this:
       * "There are <int> users and <int> invisible on <int> servers"
       */
      public void onReplyListUserClient(String msg) {
         fireStatusEvent(msg);
      }
      //------------------------------------------------------------------
      /** Respond to who-is-user reply. */
      public void onReplyWhoIsUser(String nick, String user,
                                   String name, String host ) {

         fireStatusEvent(nick+" is "+user+"@"+host);

         User userObj = getUserFromWaitingList(nick);
         userObj.setUserName(user);
         userObj.setFullName(name);
         userObj.setHostName(host);
         userObj.setOnline(true);
      }
      //------------------------------------------------------------------
      /** Respond to who-is-server reply. */
      public void onReplyWhoIsServer(String nick, String server, String info) {
         fireStatusEvent(nick+" is on server "+server+" ("+info+")");

         User user = getUserFromWaitingList(nick);
         user.setServerName(server);
         user.setServerDesc(info);
      }
      //------------------------------------------------------------------
      /** Respond to who-is-operator reply. */
      public void onReplyWhoIsOperator(String info) {
         fireStatusEvent(info);
      }
      //------------------------------------------------------------------
      /** Respond to who-is-idle reply. */
      public void onReplyWhoIsIdle(String nick, int idle, Date signon) {
         fireStatusEvent(nick+" idle for "+idle+" seconds");
         fireStatusEvent(nick+" on since "+signon);

         User user = getUserFromWaitingList(nick);
         user.setIdleTime(idle);
			user.setSignonTime(signon);
      }
      //------------------------------------------------------------------
      /** Respond to end of WHOIS reply. */
      public void onReplyEndOfWhoIs(String nick) {

         // Get user object this this nick and fire an event with it
         User user = pullUserFromWaitingList(nick);
         final ServerEvent event =
                                     new ServerEvent(Server.this,user);

         // Notify those listeners
         notifyListeners(new _ServerEventNotifier() {
            public void notify(ServerListener listener) {
               listener.onWhoIs(event);
            }
         });
      }
      //------------------------------------------------------------------
      /**
       * Respond to who-is-on-channels reply. The server sends this reply
       * after you do a WHOIS command on a user. The reply lists the channels
       * that the user is current inhabiting.
       * @param nick Nick name of the user.
       * @param channels List of channels, separated by spaces.
       */
      public void onReplyWhoIsChannels(String nick, String channels) {
         fireStatusEvent(nick+" is on "+channels);
      }
      //------------------------------------------------------------------
      /** Respond to Message-Of-The-Day start reply. */
      public void onReplyMOTDStart() {
      }
      //------------------------------------------------------------------
      /** Respond to Message-Of-The-Day reply. */
      public void onReplyMOTD(String msg) {
         fireStatusEvent(msg);
      }
      //------------------------------------------------------------------
      /** Respond to Message-Of-The-Day end reply. */
      public void onReplyMOTDEnd() {
      }
      //------------------------------------------------------------------
      /**
       * Respond to name reply. This reply is sent to inform you of the
       * users that inhabit the channel that you just joined.
       * @param channel Name of channel.
       * @param users List of users, separated by spaces.
       */
      public void onReplyNameReply(String channel, String users) {
         onJoins(users,channel);
      }
      //------------------------------------------------------------------
      /** Respond to topic change. */
      public void onTopic(String channel, String topic) {
         getChannel(channel,true).getChannelMux().onTopic(channel,topic);
      }
		//------------------------------------------------------------------
      /** Respond to topic reply. */
      public void onReplyTopic(String channel, String topic) {
         getChannel(channel,true).getChannelMux().onReplyTopic(channel,topic);
      }
      //------------------------------------------------------------------
      /** Respond to no Message-Of-The-Day reply. */
      public void onErrorNoMOTD() {
		   fireStatusEvent("\nERROR: No message of the day.\n");
      }
      //------------------------------------------------------------------
      /** Respond to need more params error. */
      public void onErrorNeedMoreParams() {
		   fireStatusEvent("\nERROR: Meed more parameters.\n");
      }
      //------------------------------------------------------------------
      /** Respond to no nick name given error. */
      public void onErrorNoNicknameGiven() {
         onErrorNeedMoreParams();
      }
      //------------------------------------------------------------------
      /** Respond to nick name in use error. */
      public void onErrorNickNameInUse(String badNick) {
         onErrorNickCollision(badNick);
      }
      //------------------------------------------------------------------
      /** Respond to nick name collision error. */
      public void onErrorNickCollision(String badNick) {

         if (badNick.equals(_user.getNick())) {
		      fireStatusEvent("\nWARNING: Nick name already in use, "
                           +"using alternate...\n");
            _connection.sendNick(_user.getAltNick());
         }
         else if (badNick.equals(_user.getAltNick())) {
		      fireStatusEvent("\nERROR: Alternate nick name already "
                           +"in use, disconnecting...\n");
            _connection.close();
         }
         else {
		      fireStatusEvent("\nERROR: Nick name already in use, "
                           +"reverting to "+_user.getNick());
            _connection.sendNick(_user.getNick());
         }
      }
      //------------------------------------------------------------------
      /** Respond to erroneus nick name error. */
      public void onErrorErroneusNickname(String badNick) {

         if (badNick.equals(_user.getNick())) {
		      fireStatusEvent("\nERROR: Error in nick name, "
                           +"using alternate...\n");
            _connection.sendNick(_user.getAltNick());
         }
         else if (badNick.equals(_user.getAltNick())) {
		      fireStatusEvent("\nERROR: Error in alternate "
                           +"nick name, disconnecting...\n");
            _connection.close();
         }
         else {
		      fireStatusEvent("\nERROR: Error in nick name, "
                           +"reverting to "+_user.getNick());
            _connection.sendNick(_user.getNick());
         }
      }
      //------------------------------------------------------------------
      /** Respond to already registered error. */
      public void onErrorAlreadyRegistered() {
	      fireStatusEvent("\nERROR: you are already connected "
                        +"to this server!\n");
	      disconnect();
      }
      //------------------------------------------------------------------
      /** Respond to message not recognized by message switch. */
      public void onErrorUnknown(String message) {
	      fireStatusEvent("UNKNOWN: "+message+"\n");
      }
      //------------------------------------------------------------------
      /** Respond to message not supported by message switch. */
      public void onErrorUnsupported(String message) {
	      fireStatusEvent("UNSUPPORTED: "+message+"\n");
      }
   }

   //////////////////////////////////////////////////////////////////////////
   //////////////////////////////////////////////////////////////////////////

   /** ChannelListener implementation that informs all ServerListeners
     * of channel connects/parts and disconnects/joins.
     */
   private class _ChannelMux extends ChannelAdapter {

      /** A channel has disconnected/parted */
      public void onDisconnect(ChannelEvent event) {

         // Remove channel from management
         removeChannel((Channel)event.getSource());

         // Notify listeners that channel has been parted
         final ServerEvent ce_event =
            new ServerEvent(Server.this,(Channel)event.getSource());

         notifyListeners(new _ServerEventNotifier() {
            public void notify(ServerListener listener)
	            {listener.onChannelPart(ce_event);}
         });
      }

      /** A channel has been connected/joined */
      public void onConnect(ChannelEvent event) {

         // Notify listeners that channel has been joined
         final ServerEvent ce_event =
            new ServerEvent(Server.this,(Channel)event.getSource());

         notifyListeners(new _ServerEventNotifier() {
            public void notify(ServerListener listener)
	            {listener.onChannelJoin(ce_event);}
         });
      }
   }
}

