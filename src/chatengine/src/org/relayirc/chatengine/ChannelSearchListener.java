//----------------------------------------------------------------------------
// $RCSfile: ChannelSearchListener.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//----------------------------------------------------------------------------

package org.relayirc.chatengine;

/** 
 * Inteface for listening to progress of a channel search. Implement
 * this interface to be notified of beginning of search, each channel
 * that is found to meet the search criteria and the end of the search.
 * @author David M. Johnson
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
public interface ChannelSearchListener {

   /** Called when channel is found that meets search criteria. */
	public void searchFound(Channel chan);

   /** Called when seach begins. */
   public void searchStarted(int channels);

   /** Called when seach ends. */
   public void searchEnded();
}
