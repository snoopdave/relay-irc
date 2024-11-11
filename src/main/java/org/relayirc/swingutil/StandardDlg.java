//-----------------------------------------------------------------------------
// $RCSfile: StandardDlg.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 16:07:53 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;

import org.relayirc.util.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>Modal dialog with border layout and Ok, Cancel and Help
 * buttons in the south. Used as a base class for the rest of
 * the dialogs in the Relay program.</p>
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay IRC Chat Engine<br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong> No contributors to this file <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */

public class StandardDlg extends JDialog {
    private final JPanel _buttonPanel = new JPanel();
    private final JButton _cancelButton = new JButton();
    private final BorderLayout _borderLayout1 = new BorderLayout();
    private final FlowLayout _flowLayout1 = new FlowLayout();
    JButton _okButton = new JButton();
    private boolean _isOk = false;
    private boolean _showCancelButton = true;

    //----------------------------------------------------------------
    public StandardDlg() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------
    public StandardDlg(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------
    public StandardDlg(Frame parent, String title, boolean modal, boolean showCancel) {
        super(parent, title, modal);
        _showCancelButton = showCancel;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------------------

    //----------------------------------------------------------------
    public static void centerOnScreen(Component obj) {
        Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
        obj.setLocation(
                (ssize.width / 2) - (obj.getSize().width / 2),
                (ssize.height / 2) - (obj.getSize().height / 2));
    }
    //----------------------------------------------------------------

    /**
     * Call this function to see if user hit OK to close the dialog.
     */
    public boolean isOk() {
        return _isOk;
    }
    //----------------------------------------------------------------

    /**
     * You should override this function. Return true if it
     * is OK to close the dialog, false if not.
     */
    public boolean onOk() {
        return true;
    }

    /**
     * You should override this function. Return true if it
     * is OK to close the dialog, false if not.
     */
    public boolean onCancel() {
        return true;
    }

    //----------------------------------------------------------------
    public void jbInit() throws Exception {
        Debug.println("StandardDlg.jbInit()");

        getContentPane().setLayout(_borderLayout1);

        // Button panel with OK and optional Cancel buttons
        _buttonPanel.setLayout(_flowLayout1);
        _okButton.setText("OK");
        _okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _okButton_actionPerformed(e);
            }
        });
        getContentPane().add(_buttonPanel, BorderLayout.SOUTH);
        _buttonPanel.add(_okButton, null);

        // Only show canel button if user asked for it
        if (_showCancelButton) {
            _cancelButton.setText("Cancel");
            _buttonPanel.add(_cancelButton);
            _cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    _cancelButton_actionPerformed(e);
                }
            });
        }


        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (onCancel()) {
                    _isOk = false;
                    setVisible(false);
                    dispose();
                }
            }
        });
    }

    //----------------------------------------------------------------
    void _okButton_actionPerformed(ActionEvent e) {
        if (onOk()) {
            _isOk = true;
            setVisible(false);
            dispose();
        }
    }

    //----------------------------------------------------------------
    void _cancelButton_actionPerformed(ActionEvent e) {
        if (onCancel()) {
            _isOk = false;
            setVisible(false);
            dispose();
        }
    }

    //----------------------------------------------------------------
    public void centerOnScreen() {
        centerOnScreen(this);
    }
}

