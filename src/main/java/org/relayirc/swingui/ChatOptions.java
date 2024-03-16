//----------------------------------------------------------------------------
// $RCSfile: ChatOptions.java,v $
// $Revision: 1.1.2.1 $
// $Author $
// $Date: 2001/02/09 03:46:33 $
//----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.Server;
import org.relayirc.chatengine.User;
import org.relayirc.util.Debug;
import org.relayirc.util.Utilities;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.Properties;
import java.util.Vector;

//////////////////////////////////////////////////////////////////////////////
/**
 * User's option settings such as nick name, font and colors
 * as well as favorite servers, channels and users.
 *
 * <br/>Properties supported by getProperty and setProperty.
 * <ul>
 * <li>gui.lookAndFeel</li>
 * <li>gui.statusBar.enabled</li>
 * <li>gui.channel.font.name</li>
 * <li>gui.channel.font.style</li>
 * <li>gui.channel.font.size</li>
 * <li>gui.channel.color.messages</li>
 * <li>gui.channel.color.actions</li>
 * <li>gui.channel.color.joins</li>
 * <li>gui.channel.color.parts</li>
 * <li>gui.channel.color.ops</li>
 * <li>gui.channel.color.kicks</li>
 * <li>gui.channel.color.bans</li>
 * <li>gui.channel.color.nicks</li>
 * <li></li>
 * </ul>
 *
 * <br/>Coming soon...
 * <li>gui.console.dock</li>
 * <li>gui.console.show</li>
 * <li>gui.favoritesTree.dock</li>
 * <li>gui.favoritesTree.show</li>
 * <li>gui.pythonWindow.show</li>
 * <li></li>
 * </ul>
 *
 * @author David M. Johnson.
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code:     Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s):    No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class ChatOptions implements Serializable {

   static final long serialVersionUID = 4852286325666585787L;

   private User        _currentUser = null;
   private Server      _currentServer = null;
   private final Properties  _properties = new Properties();

   private final ChannelList _favoriteChannels = new ChannelList();
   private final UserList    _favoriteUsers = new UserList();
   private final ServerList  _allServers = new ServerList();

   private final ActionList  _userActions = new ActionList();
   private final ActionList  _channelActions = new ActionList();
   private final ActionList  _serverActions = new ActionList();
   private final ActionList  _menuActions = new ActionList();

   private final ListenerList _serverListeners = new ListenerList();
   private final ListenerList _channelListeners = new ListenerList();

   private boolean     _isFresh = false;


   // Property change support
   // TODO: Should these be transient? It would be easier to serialize them.
   private transient PropertyChangeSupport   _propChangeSupport = null;
   private transient PropertyChangeListener  _serverListener = null;
   private transient PropertyChangeListener  _channelListener = null;
   private transient PropertyChangeListener  _userListener = null;


   //------------------------------------------------------------------
   public ChatOptions() {
      initPropertyChangeSupport();
   }
   //------------------------------------------------------------------

   // Simple accessors

   //----> Current user

      /** Get the current user. */
      public User getCurrentUser() {return _currentUser;}

      /** Set the current user. */
      public void setCurrentUser(User u) {_currentUser=u;}

   //----> Current server

      /** Get the current server. */
      public Server getCurrentServer() {return _currentServer;}

      /** Set the current server. */
      public void setCurrentServer(Server s) {_currentServer=s;}

   //----> User, channel and server collections

      /** Get favorite users. */
      public UserList getFavoriteUsers() {return _favoriteUsers;}

      /** Get favorite channels. */
      public ChannelList getFavoriteChannels() {return _favoriteChannels;}

      /** Get all servers, favorites have isFavorite = true. */
      public ServerList getAllServers() {return _allServers;}

   //----> Custom user, channel and server actions

      /** Get custom user actions. */
      public ActionList getCustomUserActions() {return _userActions;}

      /** Get custom channel actions. */
      public ActionList getCustomChannelActions() {return _channelActions;}

      /** Get custom server actions. */
      public ActionList getCustomServerActions() {return _serverActions;}

      /** Get custom menu actions. */
      public ActionList getCustomMenuActions() {return _menuActions;}

   //----> Custom server and channel listeners

      /** Get custom server listeners. */
      public ListenerList getCustomServerListeners() {return _serverListeners;}

      /** Get custom channel listeners. */
      public ListenerList getCustomChannelListeners(){return _channelListeners;}

   //----> Named string properties

      /** Set a named string property, fires property change event. */
      public void setProperty(String name, String value) {
         _properties.setProperty(name,value);
         _propChangeSupport.firePropertyChange("name",null,null);
      }

      /** Get a named string property, or empty string if propety
       * is undefined.
       */
      public String getProperty(String name) {
         String prop = _properties.getProperty(name);
         return (prop != null) ? prop : "";
      }

   //----> Misc. properties

      /** Determine if user has edited options before. */
      public boolean isFresh() { return _isFresh; }

      /** Set to false if user has edited options. */
      public void setFresh(boolean fresh) { _isFresh=fresh; }

   //------------------------------------------------------------------

   // Property change support

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.removePropertyChangeListener(listener);
   }

   //------------------------------------------------------------------
   /**
    * Initialize options with default values.
    */
   public void initNewOptions() {

      // Default current user

      String userName = null;
      try {
         userName = System.getProperty("user.name");
      }
      catch (Exception e) {
         userName = "username";
      }
      _currentUser = new User(userName);
      _currentUser.setUserName(userName);
      _currentUser.setAltNick(_currentUser.getUserName()+"1");
      _currentUser.setFullName(_currentUser.getUserName());

      // Default favorite servers

      _allServers.addServer(
         new Server("irc.mcs.net",6667,"EFNet","MSC Net"));
      _allServers.addServer(
         new Server("irc.linux.com",6667,"Open Projects","Linux.com"));
      _allServers.addServer(
         new Server("irc.dal.net",7000,"DALNet","DALNet"));
      _allServers.addServer(
         new Server("irc.slashnet.org",6667,"SlashNet","SlashNet"));

      for (int i=0; i<_allServers.getServerCount(); i++) {
         _allServers.getServer(i).setFavorite(true);
      }

      // Default properties

      _properties.setProperty("gui.lookAndFeel","Metal");

      _properties.setProperty("gui.statusBar.enabled","true");
      _properties.setProperty("gui.console.show","true");
      _properties.setProperty("gui.favorites.show","true");

      _properties.setProperty("gui.channel.font.name","Dialog");
      _properties.setProperty(
         "gui.channel.font.style",Integer.toString(Font.PLAIN));
      _properties.setProperty("gui.channel.font.size","12");

      _properties.setProperty("gui.channel.color.messages","Black");
      _properties.setProperty("gui.channel.color.actions","Red");
      _properties.setProperty("gui.channel.color.joins","Blue");
      _properties.setProperty("gui.channel.color.parts","Blue");
      _properties.setProperty("gui.channel.color.ops","Magenta");
      _properties.setProperty("gui.channel.color.kicks","Orange");
      _properties.setProperty("gui.channel.color.bans","Orange");
      _properties.setProperty("gui.channel.color.nicks","DarkGray");
   }
   //-------------------------------------------------------------------
   /**
    * Create listeners and add them to existing servers, channels and users.
    * Used by this class's implementation of the readObject() method to 
    * ensure that property change support is re-initialized when serialized
    * objects of this class are de-serialized.
    */
   private void initPropertyChangeSupport() {
      _propChangeSupport = new PropertyChangeSupport(this);

      // Add property change listener to all SERVERS 
      _serverListener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            _propChangeSupport.firePropertyChange("Servers",null,null);
         }
      };
      for (int j=0; j<_favoriteChannels.getChannelCount(); j++) {
         _favoriteChannels.getChannel(j).addPropertyChangeListener(
            _channelListener);
      }

      // Add property change listener to all CHANNELS
      _channelListener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            _propChangeSupport.firePropertyChange("Channels",null,null);
         }
      };
      for (int j=0; j<_favoriteChannels.getChannelCount(); j++) {
         _favoriteChannels.getChannel(j).addPropertyChangeListener(
            _channelListener);
      }

      // Add property change listener to all USERS 
      _userListener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            _propChangeSupport.firePropertyChange("Users",null,null);
         }
      };
      for (int k=0; k<_favoriteUsers.getUserCount(); k++) {
         _favoriteUsers.getUser(k).addPropertyChangeListener(_userListener);
      }
   }

   //-------------------------------------------------------------------
   /** Ensures that propety change support gets initialized. */
   private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
      try {
         in.defaultReadObject();
         initPropertyChangeSupport();
      } 
      catch (NotActiveException e) {
         e.printStackTrace();
      }
   }

   ////////////////////////////////////////////////////////////////////

   /** Base class for internal collections. */
   public class ObjectList implements Serializable {

      // org.relayirc.swingui.ChatOptions$ObjectList:    
      static final long serialVersionUID = -2676646196691919755L;

      private Vector _objects = new Vector();

      public ObjectList() {
      }
      public void addObject(Object obj) {
         _objects.removeElement(obj); // disallow double entries
         _objects.addElement(obj);
      }
      public void insertObjectAt(Object obj, int index) {
         _objects.removeElement(obj); // disallow double entries
         _objects.insertElementAt(obj,index);
      }
      public void removeObject(Object obj) {
         _objects.removeElement(obj);
      }
      public Object getObject(int index) {
         return _objects.elementAt(index);
      }
      public int getObjectCount() {
         return _objects.size();
      }
      public boolean contains(Object obj) {
         return _objects.contains(obj);
      }
      public void setObjects(Vector objects) {
         _objects = objects;
      }
      public Vector getObjects() {
         return _objects;
      }
   }
   ////////////////////////////////////////////////////////////////////

   public class UserList extends ObjectList implements Serializable {

      // org.relayirc.swingui.ChatOptions$UserList:    
      static final long serialVersionUID = 7528475328438526786L;

      //---------------------------------------------------------------
      /** Construct empty server list. */
      public UserList() {
      }
      //---------------------------------------------------------------
      /** Determine if user is a favorite. */
      public boolean isFavorite(User user) {
         return contains(user);
      }
      //---------------------------------------------------------------
      /** Add user to list of users. */
      public void addUser(User user) {
         addObject(user);
         user.addPropertyChangeListener(_userListener);
         _propChangeSupport.firePropertyChange("User",null,null);
      }
      //---------------------------------------------------------------
      /** Get number of users. */
      public int getUserCount() {
         return getObjectCount();
      }
      //---------------------------------------------------------------
      /** Get user by index. */
      public User getUser(int index) {
         return (User)getObject(index);
      }
      //---------------------------------------------------------------
      /** Remove user from user's list of favorite users. */
      public void removeUser(User user) {
         user.removePropertyChangeListener(_userListener);
         removeObject(user);
         _propChangeSupport.firePropertyChange("User",null,null);
      }
   }

   ////////////////////////////////////////////////////////////////////

   public class ChannelList extends ObjectList implements Serializable {

      // org.relayirc.swingui.ChatOptions$ChannelList:    
      static final long serialVersionUID = 6760437893221673062L;

      //---------------------------------------------------------------
      /** Construct empty server list. */
      public ChannelList() {
      }
      //---------------------------------------------------------------
      /** Add channel to list of IRC channels frequented by user. */
      public void addChannel(Channel chan) {
         chan.addPropertyChangeListener(_channelListener);
         addObject(chan);
         _propChangeSupport.firePropertyChange("Channel",null,null);
      }
      //---------------------------------------------------------------
      /** Get number of channels. */
      public int getChannelCount() {
         return getObjectCount();
      }
      //---------------------------------------------------------------
      /** Get channel by index. */
      public Channel getChannel(int index) {
         return (Channel)getObject(index);
      }
      //---------------------------------------------------------------
      /** Remove channel. */
      public void removeChannel(Channel channel) {
         channel.removePropertyChangeListener(_channelListener);
         removeObject(channel);
         _propChangeSupport.firePropertyChange("Channel",null,null);
      }
   }

   ////////////////////////////////////////////////////////////////////

   public class ServerList extends ObjectList implements Serializable {

      // org.relayirc.swingui.ChatOptions$ServerList:    
      static final long serialVersionUID = 6859663849958046108L;

      //----------------------------------------------------------------
      /** Construct empty server list. */
      public ServerList() {
      }
      //----------------------------------------------------------------
      /** Construct server list from vector of servers. */
      public ServerList(Vector servers) {
         this();
         setObjects(servers);
      }
      //----------------------------------------------------------------
      /** Construct server list from MIRC servers.ini file. */
      public ServerList(File file) {
         this();
         importMircFile(file);
      }
      //----------------------------------------------------------------
      public int getServerCount() {
         return getObjectCount();
      }
      //----------------------------------------------------------------
      public Server getServer(int index) {
         return (Server)getObject(index);
      }
      //---------------------------------------------------------------
      /** Add server to user's list of IRC servers. */
      public void addServer(Server svr) {
         svr.addPropertyChangeListener(_serverListener);
         insertObjectAt(svr,0);
         _propChangeSupport.firePropertyChange("Server",null,null);
      }
      //---------------------------------------------------------------
      /** Remove server from user's list of IRC servers. */
      public void removeServer(Server svr) {
         removeObject(svr);
         svr.removePropertyChangeListener(_serverListener);
         _propChangeSupport.firePropertyChange("Server",null,null);
      }
      //----------------------------------------------------------------
   	  /** Import a MIRC format servers file. */
      public void importMircFile(File file) {

         // A typical line from a MIRC servers.ini files looks like this:
         // n12=Austnet: CanadaSERVER:ca.austnet.org:6667,6668GROUP:austnet

   	   try {
	         RandomAccessFile servers = new RandomAccessFile(file,"r");
            String line = null;
            while ((line=servers.readLine()) != null) {

               if (line.startsWith("[servers]")) continue;

               try {

                  int equalTag   = line.indexOf("=");
                  int serverTag  = line.indexOf("SERVER:");
                  int groupTag   = line.indexOf("GROUP:");

                  String desc    = line.substring(equalTag+1,serverTag);
                  String address = line.substring(serverTag+7,groupTag);
                  String group   = line.substring(groupTag+6).trim();

                  // Parse network and title
                  String network = "";
                  String title = "";
                  int descColon = desc.indexOf(":");
                  if (descColon != -1) {
                     network = desc.substring(0,descColon);
                     title = desc.substring(descColon+1);
                  }
                  else {
                     title = desc;
                  }

                  // Parse server host name
                  String server = "";
                  int port = 0;
                  int addressColon = address.indexOf(":");
                  server = address.substring(0,addressColon);

                  // Parse port numbers
                  String portStr = address.substring(addressColon+1);
                  int[] portsArray = Utilities.stringToIntArray(portStr,",");

                  Server svr = new Server(server,0,network,desc);
                  svr.setPorts(portsArray);
                  addObject(svr);

                  //RCTest.println("Server: "+svr);
               }
               catch (Exception e) {
                  // TODO: Need to report all errors! 
                  e.printStackTrace();
               }
            }
         }
         catch (Exception e) {
            // TODO: Need to report all errors! 
            e.printStackTrace();
            return;
         }
         Debug.println("ServerList: read "+getObjectCount()+" servers");
      }

      //----------------------------------------------------------------
      /** TODO: Export server list file to MIRC format */
      public void exportMircFile(String fileName) {
      }
   }

   ////////////////////////////////////////////////////////////////////

   public class ActionList extends ObjectList implements Serializable {

      // org.relayirc.swingui.ChatOptions$ActionList:    
      static final long serialVersionUID = 709765889497335445L;

      //---------------------------------------------------------------
      public ActionList() {
      }
      //---------------------------------------------------------------
      public void addAction(CustomAction obj) {
         addObject(obj);
      }
      //---------------------------------------------------------------
      public void insertActionAt(CustomAction obj, int index) {
          insertObjectAt(obj,index);
      }
      //---------------------------------------------------------------
      public void removeAction(CustomAction obj) {
          removeObject(obj);
      }
      //---------------------------------------------------------------
      public CustomAction getAction(int index) {
          return (CustomAction)getObject(index);
      }
      //---------------------------------------------------------------
      public int getActionCount() {
          return getObjectCount();
      }
      //---------------------------------------------------------------
      public boolean contains(CustomAction obj) {
          return super.contains(obj);
      }
      //---------------------------------------------------------------
      public void addActionsToMenu(JPopupMenu menu, Object context) {
         for (int i=0; i<getActionCount(); i++) {
            CustomAction ca = getAction(i);
            ca.setContext(context);
            ca.update();
            menu.add(ca.getActionObject());
         }
      }
      //---------------------------------------------------------------
      public void addActionsToMenu(JMenu menu, Object context) {
         for (int i=0; i<getActionCount(); i++) {
            CustomAction ca = getAction(i);
            ca.setContext(context);
            ca.update();
            menu.add(ca.getActionObject());
         }
      }
   }

   ////////////////////////////////////////////////////////////////////

   public class ListenerList extends ObjectList implements Serializable {

      // org.relayirc.swingui.ChatOptions$ListenerList:    
      static final long serialVersionUID = 5721985822783423459L;

      //---------------------------------------------------------------
      public ListenerList() {
      }
      //---------------------------------------------------------------
      public void addCustomListener(CustomListener obj) {
         addObject(obj);
      }
      //---------------------------------------------------------------
      public void insertCustomListener(CustomListener obj, int index) {
          insertObjectAt(obj,index);
      }
      //---------------------------------------------------------------
      public void removeCustomListener(CustomListener obj) {
          removeObject(obj);
      }
      //---------------------------------------------------------------
      public CustomListener getCustomListener(int index) {
          return (CustomListener)getObject(index);
      }
      //---------------------------------------------------------------
      public int getCustomListener() {
          return getObjectCount();
      }
      //---------------------------------------------------------------
      public boolean contains(CustomListener obj) {
          return super.contains(obj);
      }
   }
}


