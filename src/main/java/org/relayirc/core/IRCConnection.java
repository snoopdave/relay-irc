//----------------------------------------------------------------------------
// $RCSfile: IRCConnection.java,v $
// $Revision: 1.1.2.9 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//----------------------------------------------------------------------------

package org.relayirc.core;

// Only dependency on other Relay package
import org.relayirc.util.Debug;
import org.relayirc.util.ParsedToken;

import java.net.*;
import java.io.*;
import java.util.*;
/**
 * <p>A socket connection to a RFC-1459 compatible IRC server.
 * Parses incoming IRC messages, replies, commands and errors
 * and notifies all listeners of such. Also provides a writeln()
 * method for sending commands to the IRC server.</p>
 * FIX: Currently, only one listener is allowed.<br>
 * @see IRCConnectionListener
 * @see IRCConnectionAdapter
 * @see IdentServer
 * @author David M. Johnson
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
public class IRCConnection implements Runnable, IRCConstants {

   public static final int CONNECTED     =  0;
   public static final int CONNECTING    =  1;
   public static final int DISCONNECTED  =  2;
   public static final int DISCONNECTING =  3;

   private int       _state = DISCONNECTED;
   private String    _server;
   private int       _port;
   private String    _nick;
   private String    _altNick;
   private String    _userName;
   private String    _fullName;
   private Socket    _socket;
   private String    _localHost;
   private Thread    _messageLoopThread;
   private BufferedReader        _inputStream;  // Input from server socket
   private DataOutputStream      _outputStream; // Output to server socket
   private IRCConnectionListener _listener;     // FIX: allow more than one!

   // MGENT FIX (bug #211402) - previous connection logic:
   // Previous handler thread which should be finishing. 
   private Thread _previousMessageLoopThread; 

   private transient IdentServer _identd = null;

   // Don't expose an IRCConnectionListener interface,
   // but do use one internally.
   private _IRCConnectionMux  _mux = new _IRCConnectionMux();

   //------------------------------------------------------------------
   /**
    * Constructs, but does not open, an IRC connection by specifying
    * server hostname and port of a IRC server as well as user registration
    * information.
    * @param server     DNS-resolvable hostname of server.
    * @param port       Server port number to connect to.
    * @param nick       User's IRC nick name (e.g. Mortz).
    * @param userName   User's login/user name (e.g. mps).
    * @param fullName   User's full name (e.g. Mortimer P. Snerd)
    */
   public IRCConnection(String server, int port,
      String nick, String altNick, String userName, String fullName)
   {
      _server     = server;
      _port       = port;
      _nick       = nick;
      _altNick    = altNick;
      _userName   = userName;
      _fullName   = fullName;

      // Use do-nothing listener until we get a real one
      _listener = new IRCConnectionAdapter();
   }
   //------------------------------------------------------------------
   /**
    * Get engine's state (see ChatEngine.DISCONNECTED,
    * ChatEngine.CONNECTED, etc.
    */
   public int getState() {
      return _state;
   }
   //------------------------------------------------------------------
   /**
    * Set engine's state (see ChatEngine.DISCONNECTED,
    * ChatEngine.CONNECTED, etc.
    */
   public void setState(int state) {
      _state = state;
   }
   //------------------------------------------------------------------
   /**
    * Opens socket connection to IRC server. Starts message loop thread
    * and starts firing events to listeners.
    */
   public void open() {
      if (getState() == DISCONNECTED) {
         setState(CONNECTING);
         _messageLoopThread = new Thread(this);
         _messageLoopThread.start();
      }
   }
	//------------------------------------------------------------------ 
   /** 
    * Opens socket connection to IRC server. Starts message loop thread 
    * and starts firing events to listeners. Syncs with previousConnection
    * as suggested in bug #211402.  
    */ 
   public void open(IRCConnection previousConnection) 
   { 
      // MGENT FIX (bug #211402) - previous connection logic:
      // prepare to wait for any previous _messageLoopThread to finish 
      if (previousConnection != null && previousConnection._identd != null) { 
         previousConnection._identd.stop(); 
         _previousMessageLoopThread = previousConnection._messageLoopThread; 
      } 
      open(); 
   } 
   //------------------------------------------------------------------
   /**
    * Close socket connection to IRC server and close down message
    * loop thread.
    */
   public void close() {

      _mux.onStatus("Closing connection...");
      setState(DISCONNECTING);

      // Stop IdentServer, in case it is still running
      if (_identd != null) _identd.stop();

      // Send QUIT command to server
      try { _outputStream.writeBytes("QUIT"); } catch (Exception e) {}

      // Close socket
      try { _socket.close(); } catch (Exception e) {}

      // Wait for message loop thread to die
      Debug.println("Waiting for message thread to die...");
      try { _messageLoopThread.join(); } catch (Exception e) {}
      Debug.println("...Thread is dead");

      _socket = null;
      setState(DISCONNECTED);
      _mux.onDisconnect();
   }
   //------------------------------------------------------------------
   /** For now, only one listener is supported. */
   public void setIRCConnectionListener(IRCConnectionListener listener) {
      _listener = listener;
   }
   //------------------------------------------------------------------
   /** Get nick name currently in use. */
   public String getNick() {
      return _nick;
   }
   //------------------------------------------------------------------
   /**
    * Send change-nickname request to IRC server and save value as
    * the nick name currently in use.
    */
   public void sendNick(String nick) {
      _nick = nick;
      writeln("NICK "+_nick+"\r\n");
   }
   //------------------------------------------------------------------
   /** Parses nick name of origin */
   private String parseOrgnick( String origin ) {
      String orgnick = null;
      if (origin.length() > 0) {
         StringTokenizer toker2 = new StringTokenizer(origin,"!");
         try {
            orgnick = toker2.nextToken();
         }
         catch (NoSuchElementException e) {
            orgnick = null;
         }
      }
      return orgnick;
   }
   //------------------------------------------------------------------
   /**
    * Write directly to the IRC chat server, refer to RFC-1459
    * for valid commands.
    */
   public void writeln(String message) {

      // MGENTFIX (bug #111583)
      // "Connection dropped while sending message is not detected"
      // If the connection is dropped while sendPrivMsg is being called
      // there is no way of detecting that the send operation failed or that
      // the connection was dropped. 
	   //
      // Because of this it is currently not feasable to write a piece of 
      // code that buffers outgoing messages until the server connection is 
      // restored, specifically while sending a message consisting of 
      // multiple lines.
      //
      // As a temporary solution I hacked the code in IRCConnection.writeln 
      // as follows (the reason for the 'synchronized' line is covered
      // in another feature request):

      try { 
         // send every line in one transaction 
         synchronized (this) { 
            _outputStream.writeBytes(message + "\r\n"); 
         } 
      } 
      catch (SocketException e) { 
         _mux.onStatus("Connection error: " + e.getMessage()); 
         setState(DISCONNECTED); 
         _mux.onDisconnect(); 
      } 
      catch (Exception e) {
		  e.printStackTrace();
      }
	   // MGENT (continued)  
      // This is not exactly neatly designed in, and the message loop of 
      // IRCConnection._messageLoopThread doesn't get notified of the 
      // connection being dropped, but it currently gives me a 'good 
      // enough' solution to this problem: after trying to write a 
      // line my code checks if the connection was dropped via a 
      // call to isConnected, and if not connected it assumes the 
      // line was not sent, so it goes in the buffer for unsent 
      // lines. In the worst case this would mean that one line 
      // might be listed twice if the connection goes down, but 
      // that's not an issue for me.
   }
   //------------------------------------------------------------------
   /**
    * The main message loop. Opens a socket connection to the IRC
    * server, sends logon information and enters message loop. The
    * message loop parses each incoming message into a command string
    * or opcode and arguments and calls the appropriate method on the mux.
    */
   public void run() {

      // MGENT FIX (bug #211402) - previous connection logic:
      // wait for any previous _messageLoopThread to finish 
      if (_previousMessageLoopThread != null) { 
         try { 
            _previousMessageLoopThread.join(); 
         } 
         catch(InterruptedException e) { 
            // ignore 
         } 
      }
       
      // Start IdentServer
      try {
         if (_identd != null) _identd.stop();
         _identd = new IdentServer(_userName);
      }
      catch (Exception e) {
         _mux.onStatus("Unable to start Ident server");
         //setState(DISCONNECTED);
         //_mux.onDisconnect();
         //return;
      }

      // Open socket connection to IRC server
      try {
         _mux.onStatus("Contacting server ["+_server+":"+_port+"]");
         _socket = new Socket(_server,_port);
      }
      catch (Exception e) {
         // UnknownHostException or IOException, so disconnect
         _mux.onStatus("Unable to contact server ["+_server+":"+_port+"]");
         setState(DISCONNECTED);
         _mux.onDisconnect();
         return;
      }
      _mux.onStatus("Contacted server ["+_server+":"+_port+"]");

      try {

         // OPEN IO STREAMS AND LOG IN TO SERVER.
         _mux.onStatus("Opening IO streams to server ["+_server+":"+_port+"]");
         _inputStream = new BufferedReader(
            new InputStreamReader(
               new DataInputStream(_socket.getInputStream())));
         _outputStream = new DataOutputStream(_socket.getOutputStream());

         // Register nick
         try {
            // Need local host name to register
            _localHost = _socket.getLocalAddress().getHostName();
         }
         catch (Exception e) {
            // We are probably running within an applet
            _localHost = "localhost";
         }
         _mux.onStatus("Registering nick ["+_nick+"] "
                      +"with server ["+_server+":"+_port+"]");
         writeln("NICK "+getNick()+"\r\n");

         // Register user name
         _mux.onStatus("Registering user name ["+_userName
                      +"] with server ["+_server+":"+_port+"]");
         writeln("USER "+_userName+" "+_localHost+" "
                 +_server+" :"+_fullName+"\r\n");
      }
      catch (Exception e) { // IOException
         if (getState() != DISCONNECTING) {
            _mux.onStatus("Exception: "+e);
            setState(DISCONNECTED);
            _mux.onDisconnect();
         }
         else {
            _mux.onStatus("Disconnected.");
         }
         return;
      }

      _mux.onStatus("Waiting for response from server ["
                    +_server+":"+_port+"]");

      try {
         String message;  // The full message
         String origin;   // User who sent the message (nick!email@host)
         String command;  // Message's command
         String response; // Response to be sent

         // MESSAGE LOOP. Loops until either there is no more to read,
         // the user requests disconnection or an exception we can't
         // handle blows us out of the loop.
         while ((message = _inputStream.readLine()) != null) {
            Debug.println("message="+message);

            int pos=0;
            origin = new String("");
            command = new String("");

            // Parse message into a vector of strings
			ParsedToken[] tokens = 
				ParsedToken.stringToParsedTokens(message," ");

            // Get origin, if there is one, and the command
            if (tokens.length > 0) {
               ParsedToken tok = tokens[0];
               if (tok.token.substring(0,1).equals(":")) {

                  // token 0 begins with ":" so assume it's the origin
                  origin = tokens[0].token.substring(1);
                  command = tokens[1].token;
               }
               else {
                  // assume token 0 is the command
                  command = tokens[0].token;
               }

               //--------------------------------------
               //
               // Try to handle message as a command
               //
               if (!handleCommand(message,command,origin,tokens)) {

                  //-------------------------------------
                  //
                  // Try to handle message as a reply
                  //
                  if (!handleReply(message,command,origin,tokens)) {

                      Debug.println("ERROR: message not handled\n");
                  }
               }
            }
            else {
               Debug.println("ERROR: ill-formed message\n");
            }
         }
      }
      catch (IRCException ie) {
         _mux.onStatus(ie.getMessage());
         _mux.onStatus("Closing connection...");
      }
      catch (Exception e) {
         if (getState() != DISCONNECTING) {
            _mux.onStatus("Closing connection due to uncaught exception.");
            e.printStackTrace();
         }
      }
      setState(DISCONNECTED);
      _mux.onDisconnect();
   }
   //------------------------------------------------------------------
   /**
    * Handle commands. Parse the command arguments and pass them
    * off to the appropriate handler method.
    */
   private boolean handleCommand( String message,
      String command, String origin, ParsedToken[] tokens) {

      boolean handled = false;

      if (command.equals("PING")) {
         String params = message.substring( message.indexOf("PING")+4 );
         _mux.onPing(params);
         handled = true;
      }

      else if (command.equals("PRIVMSG")) {
         String channel = tokens[2].token;
         String text = message.substring(tokens[3].index ).trim();
         String orgnick = parseOrgnick(origin);
         if ( orgnick != null) {
            handled = true;
            if (text.indexOf("\001VERSION") != -1) {
               _mux.onClientVersion(orgnick);
            }
            else if (text.indexOf("\001SOURCE") != -1) {
               _mux.onClientSource(orgnick);
            }
            else if (text.indexOf("\001CLIENTINFO") != -1) {
               _mux.onClientInfo(orgnick);
            }
            else if (text.indexOf("ACTION") != -1) {
               _mux.onAction(orgnick,channel,text.substring(9));
            }
            else {
               // Somebody has messaged us, use their name as the channel name
               _mux.onPrivateMessage(orgnick,channel,text.substring(1));
            }
         }
      }

      else if (command.equals("NOTICE")) {

          String orgnick = parseOrgnick(origin);
          String text = message.substring( tokens[4].index ).trim();

          try {
             handled = true;
             if (text.substring(0,9).equals(":\001VERSION")) {
                _mux.onVersionNotice(orgnick,origin,text.substring(9));
             }
            else {
               _mux.onNotice(text);
            }
         }
         catch (StringIndexOutOfBoundsException e) {
            // ignore
         }
      }

      else if (command.equals("MODE")) {
         _mux.onStatus("MODE: "+message);
         String orgnick = parseOrgnick(origin);
         if ( orgnick != null) {
            String chan = tokens[2].token;
            String mode = tokens[3].token;
            if (mode.equals("+o")) {
               String oped = tokens[4].token;
               _mux.onOp(orgnick,chan,oped);
               handled = true;
            }
            else if (mode.equals("+b")) {
               String banned = tokens[4].token;
               _mux.onBan(banned,chan,orgnick);
               handled = true;
            }
         }
      }

      else if (command.equals("INVITE")) {
         String target = tokens[2].token;
         String channel = tokens[3].token;
         String orgnick = parseOrgnick(origin);
         if ( orgnick != null) {
            _mux.onInvite(origin,orgnick,target,channel.substring(1));
            handled = true;
         }
      }

      else if (command.equals("JOIN")) {
         String channel = tokens[2].token;
         String orgnick = parseOrgnick(origin);
         if ( orgnick != null) {
            _mux.onJoin(origin,orgnick,channel.substring(1),false);
            handled = true;
         }
      }

      else if (command.equals("PART")) {
         Debug.println("PART: "+message);
         String channel = tokens[2].token;
         String orgnick = parseOrgnick(origin);
         if ( orgnick != null ) {
            _mux.onPart(origin,orgnick,channel);
            handled = true;
         }
      }

      else if (command.equals("KICK")) {
         Debug.println("KICK: "+message);
         try {
            String orgnick = parseOrgnick(origin);
            String channel = tokens[2].token;
            String kicked = tokens[3].token;
            String reason = tokens[4].token;
            if ( orgnick != null) {
               _mux.onKick(kicked,channel,orgnick,reason);
               handled = true;
            }
         }
         catch ( NoSuchElementException e ) {} // is ok
         catch ( Exception e ) {e.printStackTrace(); } // is not ok
      }

      else if (command.equals("QUIT")) {
         String channel = tokens[2].token;
         String text = message.substring( tokens[2].index+1 );
         String orgnick = parseOrgnick(origin);
         if ( orgnick != null) {
            _mux.onQuit(origin,orgnick,text);
			//Debug.println("--- QUIT --- "+origin+"|"+orgnick+"|"+text);
            handled = true;
         }
      }

      else if (command.equals("NICK")) {
         String channel = tokens[2].token;
         String orgnick = parseOrgnick(origin);
         if ( orgnick != null) {
            _mux.onNick(origin,orgnick,channel.substring(1));
            handled = true;
         }
      }

      else if (command.equals("TOPIC")) {
         String channel = tokens[2].token;
         String topic = tokens[3].token.substring(1);
         _mux.onTopic(channel,topic);
         handled = true;
      }

      else if (command.equals("MSG")) {
         _mux.onMessage(message);
         handled = true;
      }

      return handled;
   }
   //------------------------------------------------------------------
   /**
    * Handle replies and errors. Parse the reply arguments and pass
    * them off to the appropriate handler method.
    */
   private boolean handleReply( String message,
      String command, String origin, ParsedToken[] tokens) throws IRCException {

      // Parse command ID into integer
      int cmdid = -1;
      try {cmdid = Integer.parseInt(command);}
      catch (Exception e) {
         cmdid = -1;
         _mux.onParsingError(message);
         return true;
      }

      boolean handled = false;

      switch (cmdid) {

         case RPL_VERSION:
            _mux.onReplyVersion(tokens[3].token);
            handled = true;
            break;

         case RPL_LUSERCHANNELS:
            int channelCount = 0;
            try {
               channelCount = Integer.parseInt( tokens[3].token);
               _mux.onReplyListUserChannels(channelCount);
            }
            catch (Exception e) {
               _mux.onParsingError(message);
            }
            handled = true;
            break;

         case RPL_LISTSTART:
            _mux.onReplyListStart();
            handled = true;
            break;

         case RPL_LIST: {
            // "<channel> <# visible> :<topic>"
            String channel = tokens[3].token;
            int userCount = 0;
            try {
               userCount = Integer.parseInt(tokens[4].token);
            }
            catch (Exception e) {}
            String topic = message.substring(tokens[5].index+1);
            _mux.onReplyList(channel,userCount,topic);
            handled = true;
            break;
         }
         case RPL_LISTEND:
            _mux.onReplyListEnd();
            handled = true;
            break;

         case RPL_LUSERCLIENT: {
               String msg = message.substring( tokens[3].index+1);
               _mux.onReplyListUserClient(msg);
               handled = true;
               break;
            }

         case RPL_WHOISUSER: {
           // "<srv> <cmd> <to_nick> <nick> <uname> <host> * :<fname>"
            String nick = tokens[3].token;
            String user = tokens[4].token;
            String host = tokens[5].token;
            String fullName = message.substring( tokens[7].index+1); 
            _mux.onReplyWhoIsUser(nick,user,fullName,host);
            handled = true;
            break;
         }
         case RPL_WHOISSERVER: {
            // "<srv> <cmd> <to_nick> <nick> <server> :<server info>"
            String nick = tokens[3].token;
            String server = tokens[4].token;
            String info = message.substring( tokens[5].index+1); 
            _mux.onReplyWhoIsServer(nick,server,info);
            handled = true;
            break;
         }
         case RPL_WHOISOPERATOR:
            // "<srv> <cmd> <to_nick> <nick> :is an IRC operator"
            String opmsg = message.substring( tokens[3].index);
            _mux.onReplyWhoIsOperator(opmsg);
            handled = true;
            break;

         case RPL_WHOISIDLE: {
          // "<srv> <cmd> <to_nick> <nick> <idle> <signon> :<comments>

            String nick = tokens[3].token;

            // Parse integer: idle time in seconds
            int idle = 0;
            String secStr = tokens[4].token;
            try {
               idle = Integer.parseInt(secStr);
            } catch (Exception e) {}

            // Parse long: signon time in seconds-since-epoc time
            long signon = 0;
            String signonStr = tokens[5].token;
            try {
               signon = Long.parseLong(signonStr)*1000;
            } catch (Exception e) {}
            Date signonTime = new Date(signon);

            String comments = message.substring( tokens[6].index+1);

            _mux.onReplyWhoIsIdle(nick,idle,signonTime);
            handled = true;
            break;
         }
         case RPL_ENDOFWHOIS: {
            // "<srv> <cmd> <to_nick> <nick> :End of /WHOIS list"
            String nick = tokens[3].token;
            _mux.onReplyEndOfWhoIs(nick);
            handled = true;
            break;
         }
         case RPL_WHOISCHANNELS:
            // "<srv> <cmd> <to_nick> <nick> :{[@|+]<channel><space>}"
            String nick = tokens[3].token;
            String chans = message.substring(
                                 tokens[4].index+1);

            _mux.onReplyWhoIsChannels(nick,chans);
            handled = true;
            break;

         case RPL_MOTDSTART:
            // Assume that MOTD indicates we are connected,
            // registered and ready to start chatting.
            _mux.onConnect();
            _mux.onReplyMOTDStart();
            handled = true;
            break;

         case RPL_MOTD: {
            String msg = message.substring(
                                   tokens[3].index);
            _mux.onReplyMOTD(msg);
            handled = true;
            break;
         }

         case RPL_ENDOFMOTD: {
            String msg = message.substring(
                                   tokens[3].index);
            _mux.onReplyMOTDEnd();
            handled = true;
            break;
         }

         case RPL_TOPIC: {
            String chan1 = tokens[3].token;
            String top = message.substring(
                      tokens[4].index).substring(1);
            _mux.onReplyTopic(chan1,top);
            handled = true;
            break;
         }
         case RPL_NAMREPLY: {
            // "<channel> :[[@|+]<nick> [[@|+]<nick> [...]]]"
            String chn = tokens[4].token; 
            String users = message.substring( tokens[5].index);
            _mux.onReplyNameReply(chn,users.substring(1));
            handled = true;
            break;
         }
         case ERR_NOMOTD:
            _mux.onConnect();
            _mux.onErrorNoMOTD();
            handled = true;
            break;

         case ERR_NONICKNAMEGIVEN:
            _mux.onErrorNoNicknameGiven();
            handled = true;
            break;

          case ERR_NEEDMOREPARAMS:
            _mux.onErrorNeedMoreParams();
            handled = true;
            break;

         case ERR_NICKNAMEINUSE:
            _mux.onErrorNickNameInUse();
            handled = true;
            break;

         case ERR_NICKCOLLISION:
            _mux.onErrorNickCollision();
            handled = true;
            break;

         case ERR_ERRONEUSNICKNAME:
            _mux.onErrorErroneusNickname();
            handled = true;
            break;

         case ERR_ALREADYREGISTRED:
            _mux.onErrorAlreadyRegistered();
            handled = true;
            break;

         case ERR_YOUREBANNEDCREEP:
            throw new IRCException("ERR_YOURBANNEDCREEP: "+message);

         // Some IRC servers use 001 - 004 for welcome message
         case 001:
         case 002:
         case 003:
         case 004:
            // Welcome message indicates that we are connected.
            _mux.onConnect();
            if (tokens.length > 3) {
               _mux.onStatus(message.substring( tokens[3].index+1) );
            }
            handled = true;
            break;

         // Unsupported commands and replies
         case RPL_ISON: {
            int userCount = tokens.length - 1;
            String[] users = new String[userCount];
            for (int i=1; i<tokens.length; i++) {
               users[i-1] = tokens[i].token;
            }
            _mux.onIsOn( users );
            break;
         }

         case RPL_LUSERME:
         case RPL_ENDOFNAMES:
         case 250:
         case 252:
         case 333:
            _mux.onErrorUnsupported(message+"\n");
            handled = true;
            break;

         // Unknown command or reply
         default: {
            _mux.onErrorUnknown(message+"\n");
            handled = true;
            break;
         }
      }
      return handled;
   }

   ///////////////////////////////////////////////////////////////////////////

   private class _IRCConnectionMux extends IRCConnectionAdapter {

      public void onAction( String user, String chan, String txt ) {
         Debug.println("_IRCConnectionMux.onAction("+user+","+chan+","+txt+")");
         _listener.onAction( user, chan, txt );
      }
      public void onBan( String banned, String chan, String banner ) {
         _listener.onBan( banned, chan, banner );
      }
      public void onClientInfo(String orgnick) {
         _listener.onClientInfo(orgnick);
      }
      public void onClientSource(String orgnick) {
         _listener.onClientSource(orgnick);
      }
      public void onClientVersion(String orgnick) {
         _listener.onClientVersion(orgnick);
      }
      public void onConnect() {
         if (getState() != CONNECTED) {
            setState(CONNECTED);
            onStatus("Connected to server ["+_server+":"+_port+"].\n");
            _listener.onConnect() ;
         }
      }
      public void onDisconnect() {
         _listener.onDisconnect();
      }
      public void onIsOn( String[] usersOn ) { 
         _listener.onIsOn( usersOn );
      }
      public void onInvite( 
         String origin, String originNick, String target, String chan) {
         _listener.onInvite( origin, originNick, target, chan );
      }
      public void onJoin( 
         String user, String nick, String chan, boolean create ) {
         _listener.onJoin( user, nick, chan, create );
      }
      public void onJoins( String users, String chan) {
         _listener.onJoins( users, chan);
      }
      public void onKick( 
         String kicked, String chan, String kicker, String txt ) {
         _listener.onKick( kicked, chan, kicker, txt );
      }
      public void onMessage(String message) {
         _listener.onMessage(message);
      }
      public void onPrivateMessage(String orgnick, String chan, String txt) {
         _listener.onPrivateMessage(orgnick, chan, txt);
      }
      public void onNick( String user, String oldnick, String newnick ) {
         _listener.onNick( user, oldnick, newnick );
      }
      public void onNotice(String text) {
         _listener.onNotice(text);
      }
      public void onPart( String user, String nick, String chan ) {
         _listener.onPart( user, nick, chan );
      }
      public void onOp( String oper, String chan, String oped ) {
         _listener.onOp( oper, chan, oped );
      }
      public void onParsingError(String message) {
         _listener.onParsingError(message);
      }
      public void onPing(String params) {
         _listener.onPing(params);
      }
      public void onStatus(String msg) {
         _listener.onStatus(msg);
      }
      public void onTopic(String chanName, String newTopic) {
         _listener.onTopic(chanName, newTopic);
      }
      public void onVersionNotice(String orgnick, String origin,
                                                              String version) {
         _listener.onVersionNotice(orgnick, origin, version);
      }
      public void onQuit( String user, String nick, String txt ) {
         _listener.onQuit( user, nick, txt );
      }
      public void onReplyVersion(String version) {
         _listener.onReplyVersion(version);
      }
      public void onReplyListUserChannels(int channelCount) {
         _listener.onReplyListUserChannels(channelCount);
      }
      public void onReplyListStart() {
         _listener.onReplyListStart();
      }
      public void onReplyList(String channel, int userCount, String topic) {
         _listener.onReplyList(channel, userCount, topic);
      }
      public void onReplyListEnd() {
         _listener.onReplyListEnd();
      }
      public void onReplyListUserClient(String msg) {
         _listener.onReplyListUserClient(msg);
      }
      public void onReplyWhoIsUser(String nick, String user,
                                   String name, String host) {
         _listener.onReplyWhoIsUser(nick,user,name,host);
      }
      public void onReplyWhoIsServer(String nick, String server, String info) {
         _listener.onReplyWhoIsServer(nick,server,info);
      }
      public void onReplyWhoIsOperator(String info) {
         _listener.onReplyWhoIsOperator(info);
      }
      public void onReplyWhoIsIdle(String nick, int idle, Date signon) {
         _listener.onReplyWhoIsIdle(nick,idle,signon);
      }
      public void onReplyEndOfWhoIs(String nick) {
         _listener.onReplyEndOfWhoIs(nick);
      }
      public void onReplyWhoIsChannels(String nick, String channels) {
         _listener.onReplyWhoIsChannels(nick, channels);
      }
      public void onReplyMOTDStart() {
         _listener.onReplyMOTDStart();
      }
      public void onReplyMOTD(String msg) {
         _listener.onReplyMOTD(msg);
      }
      public void onReplyMOTDEnd() {
         _listener.onReplyMOTDEnd();
      }
      public void onReplyNameReply(String channel, String users) {
         _listener.onReplyNameReply(channel, users);
      }
      public void onReplyTopic(String channel, String topic) {
         _listener.onReplyTopic(channel, topic);
      }
      public void onErrorNoMOTD() {
         _listener.onErrorNoMOTD();
      }
      public void onErrorNeedMoreParams() {
         _listener.onErrorNeedMoreParams();
      }
      public void onErrorNoNicknameGiven() {
         _listener.onErrorNoNicknameGiven();
      }
      public void onErrorNickNameInUse() {
         _listener.onErrorNickNameInUse(_nick);
      }
      public void onErrorNickCollision() {
         _listener.onErrorNickCollision(_nick);
      }
      public void onErrorErroneusNickname() {
         _listener.onErrorErroneusNickname(_nick);
      }
      public void onErrorAlreadyRegistered() {
         _listener.onErrorAlreadyRegistered();
      }
      public void onErrorUnknown(String message) {
         _listener.onErrorUnknown(message);
      }
      public void onErrorUnsupported(String message) {
         _listener.onErrorUnsupported(message);
      }
   }
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7
*/
