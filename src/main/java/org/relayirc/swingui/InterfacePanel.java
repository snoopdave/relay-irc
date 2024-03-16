//-----------------------------------------------------------------------------
// $RCSfile: InterfacePanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.ITab;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;

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
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class InterfacePanel extends JPanel implements ITab {

   public static final String SKIN_PLAF_NAME =
      "Skin Look and Feel";
   public static final String SKIN_PLAF_CLASS =
      "com.l2fprod.gui.plaf.skin.SkinLookAndFeel";

   private final EmptyBorder _emptyBorder = new EmptyBorder(20,50,20,50); // tlbr
   private final JCheckBox _barCheck = new JCheckBox("Show Status Bar");
   private final JComboBox _lafCombo = new JComboBox();
   private final JLabel lafLabel = new JLabel("Look and Feel");
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JLabel _skinLabel = new JLabel();
   JTextField _skinField = new JTextField();
   JButton _skinBrowseButton = new JButton();
   JCheckBox _favoritesCheck = new JCheckBox();
   JCheckBox _consoleCheck = new JCheckBox();

   //--------------------------------------------------------------------------
   public InterfacePanel() {
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   //--------------------------------------------------------------------------
   public String getName() {
      return "Interface";
   }
   //--------------------------------------------------------------------------
   void updateGUI() {
      try {
         String sel = (String)_lafCombo.getSelectedItem();
         if (sel.equals(SKIN_PLAF_NAME)) {
            _skinField.setEnabled(true);
            _skinBrowseButton.setEnabled(true);
         }
         else {
            _skinField.setEnabled(false);
            _skinBrowseButton.setEnabled(false);
         }
      }
      catch (Exception e) {
        // ignored by design
      }
   }
   //--------------------------------------------------------------------------
   void onLAFComboChange(ItemEvent e) {
      updateGUI();
   }
   //--------------------------------------------------------------------------
   public void loadValues() {
      ChatOptions opts = ChatApp.getChatApp().getOptions();

      _barCheck.setSelected(
         opts.getProperty("gui.statusBar.enabled").equals("true") );
      _consoleCheck.setSelected(
         opts.getProperty("gui.console.show").equals("true") );
      _favoritesCheck.setSelected(
         opts.getProperty("gui.favorites.show").equals("true") );

      _lafCombo.setSelectedItem(opts.getProperty("gui.lookAndFeel"));
      _skinField.setText(opts.getProperty("gui.lookAndFeel.skinFile"));

      // Get list of available look and feels from Swing's UIManager
      boolean skinLoaded = false;
      UIManager.LookAndFeelInfo[] lafArray =
         UIManager.getInstalledLookAndFeels();
      for (int i=0; i < lafArray.length; i++) {
         _lafCombo.addItem(lafArray[i].getName());

         if (lafArray[i].getName().equals(SKIN_PLAF_NAME)) {
            skinLoaded = true; // Skinn L&F is available
         }
      }

      // If the Skin L&F is available, then install it so that it will show
      // up in the _lafCombo.
      /*if (!skinLoaded) {
         try {
            UIManager.installLookAndFeel(SKIN_PLAF_NAME,SKIN_PLAF_CLASS);
            _lafCombo.addItem(SKIN_PLAF_NAME);
         }
         catch (Exception e) {
         }
      }*/

      updateGUI();
   }
   //--------------------------------------------------------------------------
   public void saveValues() {
      ChatOptions opts = ChatApp.getChatApp().getOptions();

      String flag = _barCheck.isSelected() ? "true" : "false";
       opts.setProperty("gui.statusBar.enabled",flag);
      opts.setProperty("gui.lookAndFeel.skinFile",_skinField.getText());
      opts.setProperty("gui.lookAndFeel",(String)_lafCombo.getSelectedItem());


      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            ChatApp.getChatApp().updateLookAndFeel();
         }
      });
   }
   //--------------------------------------------------------------------------
   public boolean checkValues() {
      return true;
   }
   //--------------------------------------------------------------------------
   public void jbInit() throws Exception {
      setLayout(gridBagLayout1);

      _skinLabel.setEnabled(false);
      _skinLabel.setToolTipText("");
      _skinLabel.setText("Skin to use");
      _skinBrowseButton.setEnabled(false);
      _skinBrowseButton.setText("Browse...");
      _skinBrowseButton.addActionListener(new java.awt.event.ActionListener() {

         public void actionPerformed(ActionEvent e) {
            onSkinBrowseButtonPress(e);
         }
      });
      _favoritesCheck.setText("Show Favorites");
      _consoleCheck.setText("Show Console");

      _lafCombo.addItemListener(new java.awt.event.ItemListener() {

         public void itemStateChanged(ItemEvent e) {
            onLAFComboChange(e);
         }
      });
      _lafCombo.setToolTipText("");
      _skinField.setBackground(Color.lightGray);
      _skinField.setEnabled(false);
      _skinField.setToolTipText("Skin Look & Feel currently unsupported");
      add(_barCheck, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      add(_lafCombo, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
      add(lafLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      this.add(_skinLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      this.add(_skinField, new GridBagConstraints(1, 4, 1, 1, 0.9, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 189, 0));
      this.add(_skinBrowseButton, new GridBagConstraints(2, 4, 1, 1, 0.1, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      this.add(_favoritesCheck, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      this.add(_consoleCheck, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
   }

   //--------------------------------------------------------------------------
   void onSkinBrowseButtonPress(ActionEvent e) {

      JFileChooser chooser = new JFileChooser(new File("."));
      int choice = chooser.showOpenDialog(this);

      if(choice == JFileChooser.APPROVE_OPTION) {
         _skinField.setText(chooser.getSelectedFile().getPath());
      }
   }
}

