//-----------------------------------------------------------------------------
// $RCSfile: PanelDlg.java,v $
// $Revision: 1.1.2.3 $
// $Author: snoopdave $
// $Date: 2001/04/06 11:40:37 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

//////////////////////////////////////////////////////////////////////////////
/**
 * @author David M. Johnson
 * @version $Revision: 1.1.2.3 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code: Relay-JFC Chat Client <br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class PanelDlg extends JDialog {
   private IPanel _panel;

   public PanelDlg(Component parent, IPanel panel, String title) {

      super(
	  	(Frame)SwingUtilities.getAncestorOfClass(Frame.class,parent),
		title,
		true);

      _panel = panel;
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(panel.getPanel(),BorderLayout.CENTER);

      panel.getOkButton().addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            if (_panel.checkValues()) {
               _panel.saveValues();
               _panel.getOkButton().removeActionListener(this);
               setVisible(false);
               dispose();
            }
         }
      });
      panel.getCancelButton().addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            _panel.getCancelButton().removeActionListener(this);
            _panel.onCancel();
            setVisible(false);
            dispose();
         }
      });

      pack();
      StandardDlg.centerOnScreen(this);
   }
}

