//-----------------------------------------------------------------------------
// $RCSfile: IPanel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 16:07:53 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;

import javax.swing.*;


//////////////////////////////////////////////////////////////////////////////
/**
 * @see PanelDlg 
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
public interface IPanel extends ITab {
   
   /** Get panel's Ok button. */
   JButton getOkButton();
   
   /** Get panel's Cancel button. */
   JButton getCancelButton();

   /** Get panel itself. */
   JPanel getPanel();

   /** Inform panel of cancellation. */
   void onCancel();
}

