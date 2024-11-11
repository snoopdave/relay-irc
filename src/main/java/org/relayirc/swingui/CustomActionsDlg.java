//-----------------------------------------------------------------------------
// $RCSfile: CustomActionsDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.ITab;
import org.relayirc.swingutil.IconManager;
import org.relayirc.swingutil.StandardDlg;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

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
public class CustomActionsDlg extends JDialog {

    private final ITab[] _tabs = new ITab[4];
    private final JPanel _contentPane = new JPanel();
    private final BorderLayout _borderLayout = new BorderLayout();
    private final JTabbedPane _tabbedPane = new JTabbedPane();
    private final JButton _okButton = new JButton();
    private final JButton _cancelButton = new JButton();
    private boolean _isOk = false;
    private CustomActionsPanel _menuActionsPanel = null;
    private CustomActionsPanel _userActionsPanel = null;
    private CustomActionsPanel _channelActionsPanel = null;
    private CustomActionsPanel _serverActionsPanel = null;
    private Border _border;

    //--------------------------------------------------------------------------
    public CustomActionsDlg() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------
    public CustomActionsDlg(JFrame frame) {
        super(frame, "Custom Actions", true);

        try {
            ChatOptions opt = ChatApp.getChatApp().getOptions();

            _menuActionsPanel = new CustomActionsPanel(
                    this, "Menu", "Add, edit and remove server actions",
                    opt.getCustomMenuActions().getObjects());

            _userActionsPanel = new CustomActionsPanel(
                    this, "User", "Add, edit and remove user actions",
                    opt.getCustomUserActions().getObjects());

            _channelActionsPanel = new CustomActionsPanel(
                    this, "Channel", "Add, edit and remove channel actions",
                    opt.getCustomChannelActions().getObjects());

            _serverActionsPanel = new CustomActionsPanel(
                    this, "Server", "Add, edit and remove server actions",
                    opt.getCustomServerActions().getObjects());

            jbInit();
            loadIcons();
            initTabManagement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pack();
        // Make it 10% larger than tightly packed
        setSize((int) (getSize().getWidth() * 1.1),
                (int) (getSize().getHeight()));

        StandardDlg.centerOnScreen(this);
        setVisible(true);
    }

    //--------------------------------------------------------------------------
    public static void main(String[] args) {

        ChatApp app = new ChatApp();
        ChatApp.setChatApp(app);

        ChatOptions opts = new ChatOptions();
        app.setOptions(opts);

        CustomActionsDlg dlg = new CustomActionsDlg(app);
    }

    //-----------------------------------------------------------------------
    public void loadIcons() {
        _okButton.setIcon(IconManager.getIcon("Check"));
        _cancelButton.setIcon(IconManager.getIcon("Delete"));
    }

    //--------------------------------------------------------------------------
    public boolean isOk() {
        return _isOk;
    }

    //--------------------------------------------------------------------------
    private void initTabManagement() {

        _tabs[0] = _menuActionsPanel;
        _tabs[1] = _userActionsPanel;
        _tabs[2] = _channelActionsPanel;
        _tabs[3] = _serverActionsPanel;

        for (int i = 0; i < _tabs.length; i++) {
            _tabs[i].loadValues();
        }
    }

    //--------------------------------------------------------------------------
    private void closeDialog() {
        setVisible(false);
        //dispose();
    }

    //--------------------------------------------------------------------------
    private void onOkButtonPressed(ActionEvent e) {

        // If tabs all contains good values
        boolean allOk = true;
        for (int i = 0; i < _tabs.length; i++) {
            if (!_tabs[i].checkValues()) {
                allOk = false;
                break;
            }
        }

        // Then save those values, set OK flag and close dialog
        if (allOk) {
            for (int j = 0; j < _tabs.length; j++) {
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
        _border = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        getContentPane().setLayout(_borderLayout);

        _okButton.setText("OK");

        _cancelButton.setText("Cancel");

        getContentPane().add(_contentPane, BorderLayout.SOUTH);

        _contentPane.add(_okButton, null);
        _contentPane.add(_cancelButton, null);

        getContentPane().add(_tabbedPane, BorderLayout.CENTER);
        _tabbedPane.setBorder(_border);
        _tabbedPane.add("Menu", _menuActionsPanel);
        _tabbedPane.add("User", _userActionsPanel);
        _tabbedPane.add("Channel", _channelActionsPanel);
        _tabbedPane.add("Server", _serverActionsPanel);


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
}


