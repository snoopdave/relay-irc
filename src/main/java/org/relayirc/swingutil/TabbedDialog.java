
/*
 * FILE: ChatOptionsDlg.java
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is Relay IRC chat client.
 *
 * The Initial Developer of the Original Code is David M. Johnson.
 * Portions created by David M. Johnson are Copyright (C) 1998.
 * All Rights Reserved.
 *
 * Contributor(s): No contributors to this file.
 */
package org.relayirc.swingutil;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Tabbed dialog that manages tabs that implement the ITab interface.
 * @see org.relayirc.swingutil.ITab
 */
public class TabbedDialog extends StandardDlg {

   private final JTabbedPane _tabbedPane = new JTabbedPane();
   private final EmptyBorder _emptyBorder = new EmptyBorder(10,10,10,10);

   //---------------------------------------------------------------
   public TabbedDialog() {
      super(null,"Tabbed Dialog",true);
   }
   //---------------------------------------------------------------
   public TabbedDialog( JFrame parent, String title, boolean modal ) {
      super(parent,title,modal);
      getContentPane().add(_tabbedPane,BorderLayout.CENTER);
      _tabbedPane.setBorder(_emptyBorder);
   }
   //---------------------------------------------------------------
   public void addTab(JComponent tab) {
      addTab(tab,null);
   }
   //---------------------------------------------------------------
   public void addTab(JComponent tab, ImageIcon icon) {
      if (tab instanceof ITab itab) {
          _tabbedPane.addTab(itab.getName(),icon,tab);
         itab.loadValues();
      }
   }
   //---------------------------------------------------------------
   public boolean onOk() {
      for (int i=0; i<_tabbedPane.getComponentCount(); i++) {
         ITab itab = (ITab)_tabbedPane.getComponent(i);
         itab.saveValues();
      }
      return true;
   }
}
