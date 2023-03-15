//----------------------------------------------------------------------------
// $RCSfile: User.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//----------------------------------------------------------------------------

package org.relayirc.chatengine;
import org.relayirc.util.*;
import java.io.*;
import java.beans.*;
import java.util.Date;

/**
 * <p>
 * Represents the current user or a user who has been queried via WHOIS.
 * Although this class claims to have property change support, listeners
 * will only be notified when you call the onUpdateComplete method.
 * </p>
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL
 * </p>
 * Original Code:     Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson<br>
 * Contributor(s):    No contributors to this file<br>
 * <br>
 * Copyright (C) 1997-2000 by David M. Johnson<br>
 * All Rights Reserved.
 */
public class User implements IChatObject, Serializable {

   static final long serialVersionUID = 7267124067453694537L;

   private String   _nick = "";
   private String   _altNick = "";
   private String   _desc = "";
   private String   _userName = "Unknown";
   private String   _fullName = "Unknown";
   private String   _hostName = "Unknown";
   private String   _serverName = "Unknown";
   private String   _serverDesc = "Unknown";
   private String   _channels = "Unknown";
   private boolean  _isOnline = false;
   private int      _idleTime = 0;
   private Date     _signonTime = new Date();
   private Date     _updateTime = new Date();

   private transient PropertyChangeSupport _propChangeSupport = null;


   //------------------------------------------------------------------
   /** Construct user object for user specified by nick. */
   public User(String nick) {
      this(nick,"Unknown","Unknown","Unknown");
   }

   //------------------------------------------------------------------
   /** Construct user object for user specified by nick. */
   public User(
      String nick, String altNick, String userName, String fullName) {

      _nick = nick;
      _altNick = altNick;
      _userName = userName;
      _fullName = fullName;

      _updateTime = new Date();
      _propChangeSupport = new PropertyChangeSupport(this);
   }

   //------------------------------------------------------------------
   /** User objects are equal if they have the same nick name. */
   public boolean equals(Object obj) {
      if (obj instanceof User) {
         if ( ((User)obj).getNick().equals(getNick()) ) {
            return true;
         }
      }
      return false;
   }

   //==================================================================
   // Property change support
   //==================================================================

   /** Notify listeners that user has been updated. */
   public void onUpdateComplete() {
      setUpdateTime(new Date());
      _propChangeSupport.firePropertyChange("updated",null,null);
   }
   //------------------------------------------------------------------
   /** Property change support. */
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.addPropertyChangeListener(listener);
   }
   //------------------------------------------------------------------
   /** Property change support. */
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.removePropertyChangeListener(listener);
   }

   //==================================================================
   // Simple accessors
   //==================================================================

   /** Get current user's comments on this user. */
   public String getDescription() {return _desc;}
   /** Set current user's comments on this user. */
   public void setDescription(String desc) {_desc = desc;}

   /** Get user's alternate nickname, only valid for current user. */
   public String getAltNick() {return _altNick;}
   /** Set user's alternate nickname, only valid for current user. */
   public void setAltNick(String altnick) {_altNick=altnick;}

   /** Get user's IRC handle. */
   public String getNick() {return _nick;}
   /** Set user's IRC handle. */
   public void setNick(String nick) {_nick=nick;}

   /** Get user's IRC handle (same as nick). */
   public String getName() {return _nick;}
   /** Set user's IRC handle (same as nick). */
   public void setName(String nick) {_nick=nick;}

   /** Get login name reported to IRC by user's ident daemon. */
   public String getUserName() {return _userName;}
   /** Set login name reported to IRC by user's ident daemon. */
   public void setUserName(String userName) {_userName=userName;}

   /** Get user's full-name. */
   public String getFullName() {return _fullName;}
   /** Set user's full-name. */
   public void setFullName(String fullName) {_fullName=fullName;}

   /** Get name of computer on which user's client is running. */
   public String getHostName() {return _hostName;}
   /** Set name of computer on which user's client is running. */
   public void setHostName(String hostName) {_hostName=hostName;}

   /** Get name of IRC server user is using. */
   public String getServerName() {return _serverName;}
   /** Set name of IRC server user is using. */
   public void setServerName(String serverName) {_serverName=serverName;}

   /** Get description of user's server as reported by last WHOIS reply. */
   public String getServerDesc() {return _serverDesc;}
   /** Set description of user's server as reported by last WHOIS reply. */
   public void setServerDesc(String serverDesc) {_serverDesc=serverDesc;}

   /** Get list of user's channels as reported by last WHOIS reply. */
   public String getChannels() {return _channels;}
   /** Set list of user's channels as reported by last WHOIS reply. */
   public void setChannels(String channels) {_channels=channels;}

   /** Get user's online status (TODO!) */
   public boolean isOnline() {return _isOnline;}
   /** Set user's online status (TODO!) */
   public void setOnline(boolean isOnline) {_isOnline=isOnline;}

   /** Get user's idle time as reported by last WHOIS reply. */
   public int getIdleTime() {return _idleTime;}
   /** Set user's idle time as reported by last WHOIS reply. */
   public void setIdleTime(int idleTime) {_idleTime=idleTime;}

   /** Get user's signon time as reported by last WHOIS reply. */
   public Date getSignonTime() {return _signonTime;}
   /** Set user's signon time as reported by last WHOIS reply. */
   public void setSignonTime(Date signonTime) {_signonTime=signonTime;}

   /** Get time of last WHOIS reply. */
   public Date getUpdateTime() {return _updateTime;}
   /** Set time of last WHOIS reply. */
   public void setUpdateTime(Date updateTime) {_updateTime = updateTime;}

   //==================================================================
   // String representations
   //==================================================================

   /** Return string representation of user for display purpses. */
   public String toString() {return _nick;}

   /** 
    * Creates detailed multi-line description of user. 
    * JDK 1.1 compatible thanks to Mark Gent.
    */ 
   public String toDescription() { 

      StringBuffer sb = new StringBuffer(500); 
      sb.append("WHOIS Information for ").append(getNick()).append('\n'); 
      sb.append("As of ").append(getUpdateTime()).append('\n'); 
      sb.append("").append('\n'); 
      sb.append("Nick: ").append(getNick()).append('\n'); 
      sb.append("Address: ").append(getUserName()).append('@').append(getHostName()).append('\n'); 
      sb.append("Server: ").append(getServerName()).append(" (").append(getServerDesc()).append(')').append('\n'); 
      sb.append('\n'); 
      sb.append("Channels: ").append(getChannels()).append('\n'); 
      sb.append('\n'); 

      if (_isOnline) { 
         sb.append("Seconds Idle: ").append(getIdleTime()).append('\n'); 
         sb.append("On since: ").append(getSignonTime()).append('\n'); 
      } 
      else { 
         sb.append("User is currently OFFLINE").append('\n'); 
      } 
      return sb.toString(); 
   } 
   //-------------------------------------------------------------------
   private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      try {in.defaultReadObject();}
      catch (NotActiveException e) {e.printStackTrace();}

      _propChangeSupport = new PropertyChangeSupport(this);
   }
}

