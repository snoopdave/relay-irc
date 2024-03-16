//-----------------------------------------------------------------------------
// $RCSfile: IChatAction.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////////////
/** 
 * Actions returned by ChatApp.getAction() all implement this. 
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code: Relay-JFC Chat Client <br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public interface IChatAction {

   /** Set enabled or disabled, depending on chat app state. */
   void update();

   /** Get the actual action object. */
   AbstractAction getActionObject();

   /** Set context for action. */
   void setContext(Object context);

   /** Get context for action. */
   Object getContext();
}
