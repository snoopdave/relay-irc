//-----------------------------------------------------------------------------
// $RCSfile: ITab.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 16:07:53 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;

/**
 * Interface for a tab for use in a TabbedDialog.
 * @see org.relayirc.swingutil.TabbedDialog
 * @author David M. Johnson
 * @version $Revision: 1.1.2.2 $
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
public interface ITab {

   /** Returns name that should appear on tab. */
   String getName();

   /** Loads values into this tab's GUI. */
   void loadValues();

   /** Saves values from this tab's GUI. */
   void saveValues();

   /** Check values from this tab's GUI. */
   boolean checkValues();
}

