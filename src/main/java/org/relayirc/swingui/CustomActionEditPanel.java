//-----------------------------------------------------------------------------
// $RCSfile: CustomActionEditPanel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/06 11:40:37 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.IPanel;
import org.relayirc.swingutil.PanelDlg;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/////////////////////////////////////////////////////////////////////////////
/**
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
public class CustomActionEditPanel extends JPanel implements IPanel {

   private boolean _isOk = true;
   private int _type; 
   private String _jpythonString = "";
   private String _javaString = "";
   private String _commandString = "";

   private CustomActionsPanel.ActionHolder _actionHolder = null;

   private final JPanel        _actionPanel = new JPanel();
   private final JLabel        _titleLabel = new JLabel();
   private final JTextField    _titleField = new JTextField();
   private final JLabel        _actionLabel = new JLabel();
   private final JTextField    _actionField = new JTextField();
   private final ButtonGroup   _radioGroup = new ButtonGroup();
   private final JRadioButton  _pythonRadio = new JRadioButton();
   private final JRadioButton  _javaRadio = new JRadioButton();
   private final JRadioButton  _commandRadio = new JRadioButton();
   private final JButton       _browseButton = new JButton();

   private final JPanel        _buttonPanel = new JPanel();
   private final JButton       _okButton = new JButton();
   private final JButton       _cancelButton = new JButton();

   private final GridBagLayout gridBagLayout1 = new GridBagLayout();
   private TitledBorder  titledBorder1;
   private Border        border1;
   private final BorderLayout  borderLayout1 = new BorderLayout();


   //-------------------------------------------------------------------------
   /** Create edit panel for specified ActionHolder. */
   public CustomActionEditPanel(CustomActionsPanel.ActionHolder holder) {
      this();
      _actionHolder = holder;
      loadValues();
   }
   //-------------------------------------------------------------------------
   /** @deprecated Used only for JBuilder compatibility. */
   public CustomActionEditPanel() {
      try {
         jbInit();
         _radioGroup.add( _pythonRadio  );
         _radioGroup.add( _javaRadio    );
         _radioGroup.add( _commandRadio );
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   //-------------------------------------------------------------------------
   /** Update enable/disable status of UI. */ 
   void onUpdate() {

      // Save current value of action string
      switch (_type) {
         case CustomAction.JPYTHON_SCRIPT: {
            _jpythonString = _actionField.getText();
            break;
         }
         case CustomAction.JAVA_CLASS: {
            _javaString = _actionField.getText();
            break;
         }
         case CustomAction.IRC_COMMAND: {
            _commandString = _actionField.getText();
            break;
         }
         default: {
         }
      }

       // Update GUI to reflect type of action
      if (_pythonRadio.isSelected()) {
         _type = CustomAction.JPYTHON_SCRIPT;
         _actionField.setText(_jpythonString);
         _actionLabel.setText("Full path to JPython script"); 
      }
      else if (_javaRadio.isSelected()) {
         _type = CustomAction.JAVA_CLASS;
         _actionField.setText(_javaString);
         _actionLabel.setText("Full class name of an IRelayRunnable class"); 
      }
      else if (_commandRadio.isSelected()) {
         _type = CustomAction.IRC_COMMAND;
         _actionField.setText(_commandString);
         _actionLabel.setText("IRC command to be executed"); 
      }

      // Browse button only available for JPython type actions
      _browseButton.setEnabled( _pythonRadio.isSelected() );
   }
   //-------------------------------------------------------------------------
   /** Allow user to choose JPython script using file chooser dialog. */
   void onBrowseButtonPressed(ActionEvent e) {

      JFileChooser chooser = new JFileChooser(new File("."));
      int choice = chooser.showOpenDialog(this);

      if(choice == JFileChooser.APPROVE_OPTION) { 
         _actionField.setText(chooser.getSelectedFile().getPath()); 
      } 
   }
   //-------------------------------------------------------------------------
   /** Show as dialog, return true if user hit Ok button. */
   public boolean showDialog(Component parent) {
      PanelDlg dlg = new PanelDlg(parent,this,"Custom Action");
      dlg.setVisible(true);
      return _isOk;
   }
   //-------------------------------------------------------------------------
   /** Returns name that should appear on tab. */
   public String getName() {return "CustomActionEditPanel";}
   //-------------------------------------------------------------------------
   /** Loads values into this tab's GUI. */
   public void loadValues() {
      _actionField.setText(_actionHolder.getAction());
      _titleField.setText(_actionHolder.getTitle());
      _type = _actionHolder.getType();
      switch (_type) {
         case CustomAction.JPYTHON_SCRIPT: {
            _jpythonString = _actionHolder.getAction();
            _pythonRadio.setSelected(true);
            break;
         }
         case CustomAction.JAVA_CLASS: {
            _javaString = _actionHolder.getAction();
            _javaRadio.setSelected(true);
            break;
         }
         case CustomAction.IRC_COMMAND: {
            _commandString = _actionHolder.getAction();
            _commandRadio.setSelected(true);
            break;
         }
         default: { break; }
      }
   }
   //-------------------------------------------------------------------------
   /** Saves values from this tab's GUI. */
   public void saveValues() {
      _isOk = true;

      _actionHolder.setTitle(_titleField.getText());
      _actionHolder.setAction(_actionField.getText());

      if (_pythonRadio.isSelected())
         _actionHolder.setType(CustomAction.JPYTHON_SCRIPT);

      else if (_javaRadio.isSelected())
         _actionHolder.setType(CustomAction.JAVA_CLASS);

      else if (_commandRadio.isSelected())
         _actionHolder.setType(CustomAction.IRC_COMMAND);

   }
   //-------------------------------------------------------------------------

   /** Check values from this tab's GUI. */
   public boolean checkValues() {return true;}

   /** Get panel's Ok button. */
   public JButton getOkButton() {return _okButton;}

   /** Get panel's Cancel button. */
   public JButton getCancelButton() {return _cancelButton;}

   /** Get panel itself. */
   public JPanel getPanel() {return this;}

   /** Inform panel of cancellation. */
   public void onCancel() {_isOk = false;}

   //-------------------------------------------------------------------------
   private void jbInit() throws Exception {

      this.setLayout(borderLayout1);

      titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
         Color.white,new Color(134, 134, 134)),
         "Specify either a JPython script, Java class or IRC command");

      border1 = BorderFactory.createCompoundBorder(
         titledBorder1,BorderFactory.createEmptyBorder(5,5,5,5));


      _actionPanel.setLayout(gridBagLayout1);
      _actionPanel.setBorder(border1);

      _actionPanel.add(_titleField,
         new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
         GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
         new Insets(5, 5, 5, 5), 0, 0));

      _titleLabel.setText("Display Name ");
      _actionPanel.add(_titleLabel,
         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
         GridBagConstraints.CENTER, GridBagConstraints.NONE,
         new Insets(5, 5, 5, 5), 0, 0));

      _pythonRadio.setText("JPython script");
      _actionPanel.add(_pythonRadio,
         new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
         GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
         new Insets(0, 10, 0, 0), 0, 0));

      _javaRadio.setText("Java class");
      _actionPanel.add(_javaRadio,
         new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
         GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
         new Insets(0, 10, 0, 0), 0, 0));

      _commandRadio.setText("IRC Command");
      _actionPanel.add(_commandRadio,
         new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0,
         GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
         new Insets(0, 10, 0, 0), 0, 0));

      _actionLabel.setText("Full path to JPython script file");
      _actionPanel.add(_actionLabel,
         new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
         GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
         new Insets(5, 5, 0, 5), 0, 0));

      _actionField.setColumns(20);
      _actionPanel.add(_actionField,
         new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,
         GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
         new Insets(5, 5, 5, 5), 0, 0));

      _actionPanel.add(_browseButton,
         new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
         GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
         new Insets(5, 5, 5, 5), 0, 0));
      _browseButton.setText("Browse...");
      _browseButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
            onBrowseButtonPressed(e);
         }
      });

      _pythonRadio.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            onUpdate();
         }
      });

      _javaRadio.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            onUpdate();
         }
      });

      _commandRadio.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            onUpdate();
         }
      });

      _okButton.setText("OK");
      _cancelButton.setText("Cancel");
      _buttonPanel.add(_okButton, null);
      _buttonPanel.add(_cancelButton, null);

      this.add(_actionPanel, BorderLayout.CENTER);
      this.add(_buttonPanel, BorderLayout.SOUTH);
   }
}

