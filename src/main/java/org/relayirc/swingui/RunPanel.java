
/**
 * Title:        Relay IRC Project<p>
 * Description:  <p>
 * Copyright:    Copyright (c) David M. Johnson<p>
 * Company:      Relay IRC Project<p>
 * @author David M. Johnson
 * @version 1.0
 */
package org.relayirc.swingui;

import org.relayirc.swingutil.IconManager;

import javax.swing.*;
import java.awt.*;

public class RunPanel extends JPanel {
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel _buttonPanel = new JPanel();
   JPanel _runPanel = new JPanel();
   JButton _runButton = new JButton();
   JButton _cancelButton = new JButton();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JLabel _runLabel = new JLabel();
   JTextField jTextField1 = new JTextField();
   JButton _browseButton = new JButton();
   JRadioButton jRadioButton1 = new JRadioButton();
   JRadioButton jRadioButton2 = new JRadioButton();
   JLabel jLabel1 = new JLabel();

   public RunPanel() {
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      _runButton.setIcon(IconManager.getIcon("Plus"));
      _runButton.setText("Run!");
      _cancelButton.setIcon(IconManager.getIcon("Delete"));
      _cancelButton.setText("Cancel");
      _runPanel.setLayout(gridBagLayout1);
      _runLabel.setDisplayedMnemonic('0');
      _runLabel.setText("Choose a Python script or Java class to be run");
      jTextField1.setColumns(30);
      _browseButton.setIcon(IconManager.getIcon("Favorite"));
      _browseButton.setText("Browse...");
      jRadioButton1.setText("JPython script");
      jRadioButton2.setText("Java class");
      jLabel1.setText("Note that the script or class must support the IRelayRunnable interface");
      this.add(_buttonPanel, BorderLayout.SOUTH);
      _buttonPanel.add(_runButton, null);
      _buttonPanel.add(_cancelButton, null);
      this.add(_runPanel, BorderLayout.CENTER);
      _runPanel.add(_runLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      _runPanel.add(jTextField1, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
      _runPanel.add(jLabel1, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      _runPanel.add(jRadioButton1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
      _runPanel.add(jRadioButton2, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
      _runPanel.add(_browseButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
   }
}
