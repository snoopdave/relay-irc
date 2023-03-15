//----------------------------------------------------------------------------
// $RCSfile: ChannelAdapter.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//----------------------------------------------------------------------------

package org.relayirc.chatengine;

///////////////////////////////////////////////////////////////////////

/**
 * Provides a default do-nothing implementation of ChannelListener.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.2 $
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
public class ChannelAdapter implements ChannelListener {
   public void onActivation(ChannelEvent event) {}
   public void onAction(ChannelEvent event) {}
   public void onConnect(ChannelEvent event) {}
   public void onDisconnect(ChannelEvent event) {}
   public void onMessage(ChannelEvent event) {}
   public void onJoin(ChannelEvent event) {}
   public void onJoins(ChannelEvent event) {}
   public void onPart(ChannelEvent event) {}
   public void onBan(ChannelEvent event) {}
   public void onKick(ChannelEvent event) {}
   public void onNick(ChannelEvent event) {}
   public void onOp(ChannelEvent event) {}
   public void onDeOp(ChannelEvent event) {}
   public void onQuit(ChannelEvent event) {} 
   public void onTopicChange(ChannelEvent event) {}
}

