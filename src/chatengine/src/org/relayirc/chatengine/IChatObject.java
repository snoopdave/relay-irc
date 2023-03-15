//----------------------------------------------------------------------------
// $RCSfile: IChatObject.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//----------------------------------------------------------------------------

package org.relayirc.chatengine;

///////////////////////////////////////////////////////////////////////

/**
 * Interface for a chat data object, created for use by PropDlg and PropPanel.
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
public interface IChatObject {

   /** Get object's description. */  
   abstract public String getDescription();

   /** Set object's description. */  
   abstract public void setDescription(String desc);
}
