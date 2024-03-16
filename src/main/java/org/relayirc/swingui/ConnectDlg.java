//-----------------------------------------------------------------------------
// $RCSfile: ConnectDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;
import org.relayirc.util.Debug;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

///////////////////////////////////////////////////////////////////////////////
/**
 * Connection dialog allows user to specify IRC server to which to connect
 * and user information for connection. Uses ServerPanel and UserPanel.
 *
 * @see org.relayirc.swingui.ServerPanel
 * @see org.relayirc.swingui.UserPanel
 * @author David M. Johnson
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL</p>
 * Original Code: Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class ConnectDlg extends JDialog {

   private final ServerPanel  _serverPanel = new ServerPanel();
   private final UserPanel    _userPanel = new UserPanel();

   private ChatOptions            _chatOptions;
   private ChatOptions.ServerList _serverList;

   private boolean      _isOk = true;
   private TitledBorder _userBorder;
   private TitledBorder _serverBorder;
   private final JPanel       _panel = new JPanel();
   private final JButton      _connectButton = new JButton();
   private final JButton      _cancelButton = new JButton();
   private final BorderLayout borderLayout1 = new BorderLayout();
   private final GridLayout   gridLayout1 = new GridLayout();
   private final JPanel       jPanel2 = new JPanel();
   JButton _applyButton = new JButton();

   //--------------------------------------------------------------------------
   /**
    * Construct a ConnectDlg by passing in standard dialog arguments
    * a ServerList object and a ChatOptions object.
    */
   public ConnectDlg(Frame parent) {
      super(parent,true);
      _chatOptions = ChatApp.getChatApp().getOptions();
      _serverList = _chatOptions.getAllServers();

      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }

      _serverPanel.loadValues();
      _userPanel.loadValues();

      pack();
      // Increate width by 20%
      setSize((int)(getSize().getWidth()*1.2),(int)(getSize().getHeight()));
      StandardDlg.centerOnScreen(this);
   }
   //--------------------------------------------------------------------------
   /** Just for JBuilder. */
   public ConnectDlg() {
   }
   //--------------------------------------------------------------------------
   /** Returns true if dialog has been displayed and user hit the OK. */
   public boolean isOk() {
      return _isOk;
   }
   //--------------------------------------------------------------------------
   /**
    * This callback is called when then user hits the Connect button.
    * If a valid server is specified, then OK flag and exit.  If spec
    * is bad then allow user to correct it or exit.
    */
   public void connectButtonPressed(ActionEvent ae) {
      // If values don't check out, then do nothing
      if (!_userPanel.checkValues()) {
         return;
      }
      if (!_serverPanel.checkValues()) {
         return;
      }

      // Values are good so save them
      _serverPanel.saveValues();
      _userPanel.saveValues();

      // Set all-systems-go flag
      _isOk = true;

      setVisible(false);
      dispose();
   }
   //--------------------------------------------------------------------------
   /**
    * Respond to apply button press by saving user and server edits,
    * but only if they are valid.
    */
   void onApplyButtonPressed(ActionEvent e) {
      // If values don't check out, then do nothing
      if (!_userPanel.checkValues()) {
         return;
      }
      if (!_serverPanel.checkValues()) {
         return;
      }

      // Values are good so save them
      _serverPanel.saveValues();
      _userPanel.saveValues();
   }
   //--------------------------------------------------------------------------
   /**
    * This callback is called with the user hits the Cancel button.
    * It sets the OK flag to false, hides and disposes of the dialog.
    */
   public void cancelButtonPressed(ActionEvent ae) {
      _isOk = false;
      setVisible(false);
      dispose();
   }
   //--------------------------------------------------------------------------
   public void onWindowClosing(WindowEvent we) {
      cancelButtonPressed(null);
   }
   //--------------------------------------------------------------------------
   private void jbInit() throws Exception {
      this.setTitle("Connect");
      _userBorder = new TitledBorder("");
      _serverBorder = new TitledBorder("");
      this.getContentPane().setLayout(borderLayout1);
      _userBorder.setTitle("Enter your user information");
      _serverBorder.setTitle("Choose a server to connect to");
      _panel.setLayout(gridLayout1);
      gridLayout1.setColumns(1);
      gridLayout1.setRows(2);
      _serverPanel.setBorder(_serverBorder);
      _userPanel.setBorder(_userBorder);
      _connectButton.setIcon(IconManager.getIcon("Plug"));
      _connectButton.setText("Connect to Server");
      _connectButton.addActionListener(
         new java.awt.event.ActionListener() {

         public void actionPerformed(ActionEvent e) {
            connectButtonPressed(e);
         }
      });
      _cancelButton.setIcon( IconManager.getIcon("Delete"));
      _cancelButton.setText("Cancel");
      _cancelButton.addActionListener(new java.awt.event.ActionListener() {

         public void actionPerformed(ActionEvent e) {
            cancelButtonPressed(e);
         }
      });


      _applyButton.addActionListener(new java.awt.event.ActionListener() {

         public void actionPerformed(ActionEvent e) {
            onApplyButtonPressed(e);
         }
      });
      _applyButton.setText("Apply");
      _applyButton.setToolTipText("Save edits to servers and user");
      _applyButton.setIcon(IconManager.getIcon("Check"));
      this.getContentPane().add(_panel, BorderLayout.CENTER);
      _panel.add(_serverPanel, null);
      _panel.add(_userPanel, null);
      this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(_connectButton, null);
      jPanel2.add(_applyButton, null);
      jPanel2.add(_cancelButton, null);

      this.addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            onWindowClosing(e);
         }
      });
   }
   //--------------------------------------------------------------------------
   /** Main function, used for testing only. */
   public static void main(java.lang.String[] args) {

      try {
         Debug.setDebug(true);

         /*final ChatOptions options = new ChatOptions();
         ChatOptions.ServerList list = new ChatOptions.ServerList();
         list.importMircFile(new File("servers.ini"));

         ConnectDlg dlg = new ConnectDlg(null);

         dlg.pack();
         StandardDlg.centerOnScreen(dlg);
         dlg.setVisible(true);*/
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}

