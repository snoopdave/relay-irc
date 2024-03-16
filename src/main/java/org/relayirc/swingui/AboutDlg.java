//-----------------------------------------------------------------------------
// $RCSfile: AboutDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

///////////////////////////////////////////////////////////////////////
/**
 * About dialog for the Relay-JFC chat application.
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
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class AboutDlg extends JDialog {
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   Border border1;
   JLabel _copyright2 = new JLabel();
   JLabel _copyright1 = new JLabel();
   JLabel _sep1 = new JLabel();
   JLabel _license = new JLabel();
   JLabel _appName = new JLabel();
   JButton _okButton = new JButton();
   JLabel _tagLine = new JLabel();
   JLabel _appVersion = new JLabel();

   public AboutDlg(Frame owner) {
      super(owner,"About",true);
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
      setTitle("About "+ChatApp.getChatApp().getAppName());
      _appName.setText(ChatApp.getChatApp().getAppName());
      _appVersion.setText(ChatApp.getChatApp().getAppVersion());
      pack();
      StandardDlg.centerOnScreen(this);
      setVisible(true);
   }

   private void jbInit() throws Exception {
      this.getContentPane().setLayout(gridBagLayout1);
      border1 = BorderFactory.createEmptyBorder(20,20,20,20);

      _appName.setText("Relay-JFC"); // place holder
      _appName.setFont(new java.awt.Font("Dialog", 1, 24));

      _appVersion.setText("X.Y.Z"); // place holder
      _appVersion.setFont(new java.awt.Font("Dialog", 1, 14));

      _sep1.setToolTipText("");
      _sep1.setText("--");

      _tagLine.setText("Vintage Java Swing-based IRC client");
      _tagLine.setFont(new java.awt.Font("Dialog", 2, 12));

      _copyright2.setText("Copyright 1999-2024 David M. Johnson");

      _license.setText("Distributed under the Mozilla Public License (MPL)");

      _okButton.setText("OK");
      _okButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            _okButton_actionPerformed(e);
         }
      });

      this.getContentPane().add(_appName, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      this.getContentPane().add(_appVersion, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      this.getContentPane().add(_sep1,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      this.getContentPane().add(_tagLine,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      this.getContentPane().add(_license, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 15, 0, 15), 0, 0));
      this.getContentPane().add(_copyright1, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      this.getContentPane().add(_copyright2, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      this.getContentPane().add(_okButton, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

   }

   void _okButton_actionPerformed(ActionEvent e) {
      setVisible(false);
      dispose();
   }
}


