//-----------------------------------------------------------------------------
// $RCSfile: CustomListenerEditPanel.java,v $
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
public class CustomListenerEditPanel extends JPanel implements IPanel {

    private final JPanel _listenerPanel = new JPanel();
    private final JLabel _promptLabel = new JLabel();
    private final JRadioButton _pythonRadio = new JRadioButton();
    private final JRadioButton _javaRadio = new JRadioButton();
    private final JTextField _listenerField = new JTextField();
    private final JButton _browseButton = new JButton();
    private final JPanel _buttonPanel = new JPanel();
    private final JButton _okButton = new JButton();
    private final JButton _cancelButton = new JButton();
    private final BorderLayout borderLayout1 = new BorderLayout();
    private final GridBagLayout gridBagLayout1 = new GridBagLayout();
    JTextField _titleField = new JTextField();
    JLabel _titleLabel = new JLabel();
    JTextField _subjectField = new JTextField();
    JLabel _subjectLabel = new JLabel();
    private boolean _isOk = false;
    private CustomListenersPanel.ListenerHolder _listenerHolder;
    private TitledBorder titledBorder1;
    private Border border1;

    //-------------------------------------------------------------------------
    public CustomListenerEditPanel(CustomListenersPanel.ListenerHolder holder) {
        this();
        _listenerHolder = holder;
    }

    //-------------------------------------------------------------------------
    public CustomListenerEditPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-------------------------------------------------------------------------

    /**
     * Show as dialog, return true if user hit Ok button.
     */
    public boolean showDialog(Component parent) {
        PanelDlg dlg = new PanelDlg(parent, this, "Add / Edit Listener");
        dlg.setVisible(true);
        return _isOk;
    }
    //-------------------------------------------------------------------------

    /**
     * Loads values into this tab's GUI.
     */
    public void loadValues() {

        switch (_listenerHolder.getType()) {
            case CustomListener.JPYTHON_SCRIPT: {
                _pythonRadio.setSelected(true);
                break;
            }
            case CustomListener.JAVA_CLASS: {
                _javaRadio.setSelected(true);
                break;
            }
            default: {
                break;
            }
        }

        _listenerField.setText(_listenerHolder.getListenerString());
        _titleField.setText(_listenerHolder.getTitle());
    }
    //-------------------------------------------------------------------------

    /**
     * Saves values from this tab's GUI.
     */
    public void saveValues() {
        _isOk = true;
    }
    //-------------------------------------------------------------------------

    /**
     * Returns name that should appear on tab.
     */
    public String getName() {
        return "CustomListenerEditPanel";
    }

    /**
     * Check values from this tab's GUI.
     */
    public boolean checkValues() {
        return true;
    }

    // IPanel Implementation

    /**
     * Get panel's Ok button.
     */
    public JButton getOkButton() {
        return _okButton;
    }

    /**
     * Get panel's Cancel button.
     */
    public JButton getCancelButton() {
        return _cancelButton;
    }

    /**
     * Get panel itself.
     */
    public JPanel getPanel() {
        return this;
    }

    /**
     * Inform panel of cancellation.
     */
    public void onCancel() {
        _isOk = false;
    }

    //-------------------------------------------------------------------------
    void onBrowseButtonPressed(ActionEvent e) {
    }

    //-------------------------------------------------------------------------
    void okButtonPressed(ActionEvent e) {
        _isOk = true;
    }

    //-------------------------------------------------------------------------
    void onCancelButtonPressed(ActionEvent e) {
    }

    //-------------------------------------------------------------------------
    private void jbInit() throws Exception {
        titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(134, 134, 134)), "Specify either a JPython script, Java class or IRC command");
        border1 = BorderFactory.createCompoundBorder(titledBorder1, BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(borderLayout1);
        _pythonRadio.setText("JPython script");
        _browseButton.setText("Browse...");
        _browseButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onBrowseButtonPressed(e);
            }
        });
        _javaRadio.setText("Java class");
        _listenerField.setColumns(20);
        _okButton.setText("OK");
        _okButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                okButtonPressed(e);
            }
        });
        _cancelButton.setText("Cancel");
        _cancelButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onCancelButtonPressed(e);
            }
        });
        _listenerPanel.setLayout(gridBagLayout1);
        _listenerPanel.setBorder(border1);
        titledBorder1.setTitle("Choose JPython or Java class to server as listener");
        _promptLabel.setToolTipText("");
        _promptLabel.setDisplayedMnemonic('0');
        _promptLabel.setText("Full path to JPython script file");
        _titleLabel.setText("Display Name");
        _subjectLabel.setToolTipText("");
        _subjectLabel.setText("Subject name");
        _subjectField.setToolTipText("Name of channel, server or user to which to listen");
        this.add(_buttonPanel, BorderLayout.SOUTH);
        _buttonPanel.add(_okButton, null);
        _buttonPanel.add(_cancelButton, null);
        this.add(_listenerPanel, BorderLayout.CENTER);
        _listenerPanel.add(_listenerField, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        _listenerPanel.add(_titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        _listenerPanel.add(_promptLabel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        _listenerPanel.add(_browseButton, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        _listenerPanel.add(_subjectField, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        _listenerPanel.add(_subjectLabel, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
                , GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        _listenerPanel.add(_titleField, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
        _listenerPanel.add(_pythonRadio, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0
                , GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 0), 0, 0));
        _listenerPanel.add(_javaRadio, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0
                , GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 15, 0, 0), 0, 0));
    }

}


