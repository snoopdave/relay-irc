//-----------------------------------------------------------------------------
// $RCSfile: ServerAddPanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.Server;
import org.relayirc.swingutil.*;
import org.relayirc.util.*;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

//////////////////////////////////////////////////////////////////////////////
/**
 * GUI for editing a Server object held by a ServerPanel.ServerHolder.
 * GUI Generated by JBuilder Foundation 3.500.24.0.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
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
public class ServerAddPanel extends JPanel implements ITab {

   private final Server _server = null;
   private ServerPanel.ServerHolder _serverHolder = null;
   private Vector _serverHolders = null;

   GridBagLayout  gridBagLayout1 = new GridBagLayout();
   JTextField     _titleField = new JTextField();
   JLabel         _titleLabel = new JLabel();
   JTextField     _nameField = new JTextField();
   JLabel         _nameLabel = new JLabel();
   JTextField     _portsField = new JTextField();
   JLabel         _portsLabel = new JLabel();
   JLabel         _networkLabel = new JLabel();
   JComboBox      _networkCombo = new JComboBox();
   JCheckBox _favoriteCheckBox = new JCheckBox();

   //--------------------------------------------------------------------------
   /**
    * @deprecated For internal debuggin use only
    */
   public ServerAddPanel() {
      try {
         jbInit();
         initListeners();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   //--------------------------------------------------------------------------
   /**
    * Construct and initialize panel for editing a server.
    * @param holder Holder of server to be edited.
    * @param holders Other servers to consider
    */
   public ServerAddPanel( ServerPanel.ServerHolder holder, Vector holders) {

      try {
         jbInit();
         initListeners();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
      setServerHolder(holder,holders);
   }
   //--------------------------------------------------------------------------
   private void initListeners() {

      FocusListener fl = new FocusListener() {
         public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField field) {
                field.selectAll();
            }
         }
         public void focusLost(FocusEvent e) {
         }
      };

      _nameField.addFocusListener(fl);
      _portsField.addFocusListener(fl);
      _titleField.addFocusListener(fl);
   }
   //--------------------------------------------------------------------------
   public boolean checkValues() {

      if (_nameField.getText().trim().length() < 1) {
         return false;
      }
      else if (_titleField.getText().trim().length() < 1) {
         return false;
      }
      else {
         try {
            int[] intArray = null;
            intArray = org.relayirc.util.Utilities.stringToIntArray(_portsField.getText(),",");
         }
         catch (Exception e) {
            return false;
         }
      }

      return true;
   }
   //--------------------------------------------------------------------------
   /** Initialize panel with server values. */
   public void setServerHolder(
      ServerPanel.ServerHolder holder, Vector holders) {

      _serverHolder = holder;
      _serverHolders = holders;
      loadValues();
   }
   //--------------------------------------------------------------------------
   /** Returns name that should appear on tab */
   public String getName() {
      return "Server";
   }
   //--------------------------------------------------------------------------
   /** Loads values into this tab's GUI. */
   public void loadValues() {
      _titleField.setText(_serverHolder.getTitle());
      _nameField.setText(_serverHolder.getName());

      String ports = "";
      for (int i=0; i<_serverHolder.getPorts().length; i++) {
         ports += _serverHolder.getPorts()[i];
         if (i+1 < _serverHolder.getPorts().length) {
            ports += ",";
         }
      }
      _portsField.setText(ports);

      // Use hashtable to create list of unique network names
      Hashtable networkHash = new Hashtable();
      for (int i=0; i<_serverHolders.size(); i++) {
         ServerPanel.ServerHolder holder;
         holder = (ServerPanel.ServerHolder)_serverHolders.elementAt(i);
         networkHash.put( holder.getNetwork().trim(), "dummy" );
      }
      // Create vector of unique network names
      Vector networks = new Vector();
      for (Enumeration e = networkHash.keys() ; e.hasMoreElements() ;) {
         networks.addElement(new ComparableString((String)e.nextElement()));
      }
      // Populate _networkCombo with sorted list of unique network names
      QuickSort.quicksort(networks,true);
      for (Enumeration e = networks.elements() ; e.hasMoreElements() ;) {
         String nname = ((ComparableString)e.nextElement()).getString();
         _networkCombo.addItem(nname);
      }
      _networkCombo.setSelectedItem(_serverHolder.getNetwork());

      _favoriteCheckBox.setSelected(_serverHolder.isFavorite());
   }
   //--------------------------------------------------------------------------
   /** Saves values from GUI */
   public void saveValues() {
      _serverHolder.setName(_nameField.getText());
      _serverHolder.setTitle(_titleField.getText());
      _serverHolder.setNetwork((String)_networkCombo.getSelectedItem());
      _serverHolder.setFavorite(_favoriteCheckBox.isSelected());

      _serverHolder.setPorts(
         Utilities.stringToIntArray(_portsField.getText(),","));
   }
   //--------------------------------------------------------------------------
   private void jbInit() throws Exception {

      this.setLayout(gridBagLayout1);

      _titleField.setToolTipText("Descriptive title for server");
      _titleField.setText("Title");
      _titleField.setColumns(20);
      _titleLabel.setText("Title");

      _nameField.setToolTipText("Internet address of server");
      _nameField.setText("Host Name");
      _nameField.setColumns(20);
      _nameLabel.setText("Host Name");

      _portsField.setToolTipText("Server ports separated by commas");
      _portsField.setText("6667");
      _portsField.setColumns(20);
      _portsLabel.setText("Ports");

      _networkLabel.setText("Network");
      _networkCombo.setToolTipText("Network name, used for grouping");
      _networkCombo.setEditable(true);

      _favoriteCheckBox.setText("Is Favorite");
      _favoriteCheckBox.setActionCommand("");
      this.add(_titleField, new GridBagConstraints(
         1, 0, 1, 1, 0.0, 0.0,GridBagConstraints.WEST,
         GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 1, 0));

      this.add(_titleLabel, new GridBagConstraints(
         0, 0, 1, 1, 0.0, 0.0,GridBagConstraints.WEST,
         GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

      this.add(_nameField, new GridBagConstraints(
         1, 1, 1, 1, 0.0, 0.0,GridBagConstraints.WEST,
         GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 1, 0));

      this.add(_nameLabel, new GridBagConstraints(
         0, 1, 1, 1, 0.0, 0.0,GridBagConstraints.WEST,
         GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

      this.add(_portsField, new GridBagConstraints(
         1, 2, 1, 1, 0.0, 0.0,GridBagConstraints.WEST,
         GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 1, 0));

      this.add(_portsLabel, new GridBagConstraints(
         0, 2, 1, 1, 0.0, 0.0,GridBagConstraints.WEST,
         GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

      this.add(_networkLabel,new GridBagConstraints(
         0, 3, 1, 1, 0.0, 0.0,GridBagConstraints.WEST,
         GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

      this.add(_networkCombo, new GridBagConstraints(
         1, 3, 1, 1, 0.0, 0.0,GridBagConstraints.CENTER,
         GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
      this.add(_favoriteCheckBox, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
   }
}

