//-----------------------------------------------------------------------------
// $RCSfile: CustomListenersDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;
import org.relayirc.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

///////////////////////////////////////////////////////////////////////////////

/**
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
public class CustomListenersDlg extends JDialog {

   private final ITab[] _tabs = new ITab[5];
   private boolean _isOk = false;

   private CustomActionsPanel _userActionsPanel = null; 
   private CustomActionsPanel _channelActionsPanel = null;
   private CustomActionsPanel _serverActionsPanel = null;

   private CustomListenersPanel _channelListenersPanel = null;
   private CustomListenersPanel _serverListenersPanel = null;

   private final JPanel         _contentPane = new JPanel();
   private final BorderLayout   _borderLayout = new BorderLayout();
   private final JTabbedPane    _tabbedPane = new JTabbedPane();
   private final JButton        _okButton = new JButton();
   private final JButton        _cancelButton = new JButton();
   private Border         _border;

   //--------------------------------------------------------------------------
   public CustomListenersDlg() {
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   //--------------------------------------------------------------------------
   public CustomListenersDlg(JFrame frame) {
      super(frame,"Customizer",true);

      try {
         ChatOptions opt = ChatApp.getChatApp().getOptions();

         _userActionsPanel = new CustomActionsPanel(
            this,"User","Add, edit and remove user actions",
            opt.getCustomUserActions().getObjects());

         _channelActionsPanel = new CustomActionsPanel(
            this,"Channel","Add, edit and remove channel actions",
            opt.getCustomChannelActions().getObjects());

         _serverActionsPanel = new CustomActionsPanel(
            this,"Server","Add, edit and remove server actions",
            opt.getCustomServerActions().getObjects());

         _channelListenersPanel = new CustomListenersPanel(
            this,"Channel","Add, edit and remove channel listeners",
            opt.getCustomChannelListeners().getObjects());

         _serverListenersPanel = new CustomListenersPanel(
            this,"Server","Add, edit and remove server listeners",
            opt.getCustomUserActions().getObjects());

         jbInit();
         loadIcons();
         initTabManagement();
      }
      catch(Exception e) {
         e.printStackTrace();
      }

      pack();
      // Make it 10% larger than tightly packed
      setSize( (int)(getSize().getWidth()*1.1),
               (int)(getSize().getHeight()));

      StandardDlg.centerOnScreen(this);
      setVisible(true);
   }
   //-----------------------------------------------------------------------
   public void loadIcons() {
      _okButton.setIcon(     IconManager.getIcon("Check"));
      _cancelButton.setIcon( IconManager.getIcon("Delete"));
   }
   //--------------------------------------------------------------------------
   public boolean isOk() {
      return _isOk;
   }
   //--------------------------------------------------------------------------
   private void initTabManagement() {

      _tabs[0] = _userActionsPanel;
      _tabs[1] = _channelActionsPanel;
      _tabs[2] = _serverActionsPanel;
      _tabs[3] = _channelListenersPanel;
      _tabs[4] = _serverListenersPanel;

      for (int i=0; i<_tabs.length; i++) {
         _tabs[i].loadValues();
      }
   }
   //--------------------------------------------------------------------------
   private void closeDialog() {
      setVisible(false);
      dispose();
   }
   //--------------------------------------------------------------------------
   private void onOkButtonPressed(ActionEvent e) {

      // If tabs all contains good values
      boolean allOk = true;
      for (int i=0; i<_tabs.length; i++) {
         if (!_tabs[i].checkValues()) {
            allOk = false;
            break;
         }
      }

      // Then save those values, set OK flag and close dialog
      if (allOk) {
         for (int j=0; j<_tabs.length; j++) {
            _tabs[j].saveValues();
         }
         _isOk = true;
         closeDialog();
      }
   }
   //--------------------------------------------------------------------------
   private void onCancelButtonPressed(ActionEvent e) {
      _isOk = false;
      closeDialog();
   }
   //--------------------------------------------------------------------------
   private void onWindowClosed(WindowEvent e) {
      onCancelButtonPressed(null);
   }
   //--------------------------------------------------------------------------
   public void jbInit() throws Exception {
      _border = BorderFactory.createEmptyBorder(10,10,10,10);

      getContentPane().setLayout(_borderLayout);

      _okButton.setText("OK");

      _cancelButton.setText("Cancel");

      getContentPane().add(_contentPane, BorderLayout.SOUTH);

      _contentPane.add(_okButton, null);
      _contentPane.add(_cancelButton, null);

      getContentPane().add(_tabbedPane, BorderLayout.CENTER);
      _tabbedPane.setBorder(_border);
      _tabbedPane.add( "User Actions",     _userActionsPanel);
      _tabbedPane.add( "Channel Actions",  _channelActionsPanel);
      _tabbedPane.add( "Server Actions",   _serverActionsPanel);
      _tabbedPane.add( "Channel Listeners",_channelListenersPanel);
      _tabbedPane.add( "Server Listeners", _serverListenersPanel);

      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosed(WindowEvent e) {
            onWindowClosed(e);
         }
      });

      _okButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            onOkButtonPressed(e);
         }
      });

      _cancelButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            onCancelButtonPressed(e);
         }
      });
   }
   //--------------------------------------------------------------------------
   public static void main(String[] args) {

      ChatApp app = new ChatApp();
      ChatApp.setChatApp(app);

      ChatOptions opts = new ChatOptions();
      app.setOptions(opts);

      //CustomListenersDlg dlg = new CustomizerDlg(app);
   }
}


