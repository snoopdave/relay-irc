//-----------------------------------------------------------------------
// $RCSfile: ServerListener.java,v $
// $Revision: 1.1.2.5 $
// $Author: snoopdave $ 
// $Date: 2001/04/08 22:44:16 $
//-----------------------------------------------------------------------

package org.relayirc.chatengine;

/**
 * Implement this interface to listen for server connection and  
 * disconnection, channel joins and parts and status messages.  
 * @see Server
 * @see ServerEvent
 * @see ServerListener
 * @author David M. Johnson
 * @version $Revision: 1.1.2.5 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code:     Relay IRC Chat Engine <br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s):    No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */

public interface ServerListener {

   /** 
    * Engine has connected to IRC server. 
    * event.getServer() will be valid.
    */
   abstract public void onConnect(ServerEvent event);

   /** 
    * Engine has disconnected from IRC server. 
    * event.getServer() will be valid.
    */
   abstract public void onDisconnect(ServerEvent event);

   /** 
    * Channel object has been added to list of those managed by Server object.
    * May be followed by an onChannelJoin event. 
    * event.getChannel() will be valid.
	 */
   abstract public void onChannelAdd(ServerEvent event);

   /** 
    * User has joined a channel.
    * Will always follow an onChannelAdd event. 
    * event.getUser() and event.getChannel() will be valid.
	 */
   abstract public void onChannelJoin(ServerEvent event);

   /** 
    * Query results for an ISON query. 
    * Use event.getUsers() to get an array of users that are online.
    */
   abstract public void onIsOn(ServerEvent event);

   /** 
    * User has invited us to join a channel. 
    * event.getUser() and event.getChannel() will be valid.
    */
   abstract public void onInvite(ServerEvent event);

   /** 
    * User has parted from a channel. 
    * event.getUser() and event.getChannel() will be valid.
    */
   abstract public void onChannelPart(ServerEvent event);

   /** 
    * Status message from engine. 
    * event.getMessage() will be valid.
    */
   abstract public void onStatus(ServerEvent event);

   /** 
    * Query result from an WHOIS query.
    * event.getUser() will be valid and full of requested data. 
    */
   abstract public void onWhoIs(ServerEvent event);
}
