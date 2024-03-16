//-----------------------------------------------------------------------------
// $RCSfile: UserDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.User;
import org.relayirc.swingutil.StandardDlg;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * Displays WhoIs information for an IRC user.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay-JFC Chat Client <br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong> No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class UserDlg extends StandardDlg {
   private User _user = null;

   public UserDlg(Frame parent, User user) {
      super(parent,"User Information",true);
      _user = user;
      getContentPane().add(new _UserPanel(_user));
      setSize(350,350);
      centerOnScreen();
      setVisible(true);
   }
   /** For testing only. */
   public static void main(String[] args) {
      UserDlg dlg = new UserDlg(null,new User("fred"));
   }
}

class _UserPanel extends JPanel {
   private User _user = null;
   private JTextArea _textArea = new JTextArea();

   public _UserPanel(User user) {
      _user = user;
      initGUI();
   }
   public void initGUI() {
      setLayout(new BorderLayout());
      _textArea = new JTextArea();
      _textArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
      _textArea.setText(_user.toDescription());
      add(new JScrollPane(_textArea),BorderLayout.CENTER);
   }
}

