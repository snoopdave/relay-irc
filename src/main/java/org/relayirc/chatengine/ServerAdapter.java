//-----------------------------------------------------------------------
// $RCSfile: ServerAdapter.java,v $
// $Revision: 1.1.2.4 $
// $Author: snoopdave $ 
// $Date: 2001/04/08 22:44:16 $
//-----------------------------------------------------------------------

package org.relayirc.chatengine;

/**
 * Provides a default do-nothing implementation of ServerListener.
 * @see Server
 * @see ServerEvent
 * @see ServerListener
 * @author David M. Johnson
 * @version $Revision: 1.1.2.4 $
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
public class ServerAdapter implements ServerListener {
   public void onConnect(     ServerEvent event) {}
   public void onDisconnect(  ServerEvent event) {}
   public void onChannelAdd(  ServerEvent event) {}
   public void onChannelJoin( ServerEvent event) {}
   public void onChannelPart( ServerEvent event) {}
   public void onInvite(      ServerEvent event) {}
   public void onIsOn(        ServerEvent event) {}
   public void onStatus(      ServerEvent event) {}
   public void onWhoIs(       ServerEvent event) {}
}
