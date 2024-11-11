//-----------------------------------------------------------------------------
// $RCSfile: PropPanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.propsheet.BeanModel;
import org.relayirc.swingutil.propsheet.PropSheet;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusEvent;

//////////////////////////////////////////////////////////////////////////////

/**
 * Generic property panel with a description field.
 * <p>
 * Currently, only the description field is editable, the property
 * sheet component is read-only!
 * <p>
 * Doesn't know about chatengine package or even IChatObject.
 *
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code: Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class PropPanel extends JPanel {

    private static final String _prompt = "<enter your comments here>";
    private final PropSheet _sheet = new PropSheet();

    // JBuilder mess
    private final JTextArea _commentField = new JTextArea();
    private final JPanel _topPanel = new JPanel();
    private final JPanel _bottomPanel = new JPanel();
    private final JScrollPane _scrollPane = new JScrollPane();
    private final BorderLayout borderLayout1 = new BorderLayout();
    private final BorderLayout borderLayout3 = new BorderLayout();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    private Object _bean;
    private Border border1;
    private Border border2;
    private Border border3;
    private TitledBorder titledBorder1;
    private TitledBorder titledBorder2;

    //---------------------------------------------------------------------

    /**
     * Default constructor facilitates use within GUI builder.
     */
    public PropPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //---------------------------------------------------------------------

    /**
     * Construct prop panel for editing a bean and its description.
     */
    public PropPanel(Object bean, String desc) {
        this();
        setObject(bean);
        setDescription(desc);
    }
    //---------------------------------------------------------------------

    /**
     * Get the object that is currently in the property sheet.
     */
    public Object getObject() {
        return _bean;
    }
    //---------------------------------------------------------------------

    /**
     * Set the object that is to appear in the property sheet.
     */
    public void setObject(Object bean) {
        _bean = bean;
        _sheet.setPropModel(new BeanModel(bean));
    }
    //---------------------------------------------------------------------

    /**
     * Get contents of description field. Returns empty string if
     * user has not set a description.
     */
    public String getDescription() {
        String ret = "";
        if (!_commentField.getText().equals(_prompt)) {
            ret = _commentField.getText();
        }
        return ret;
    }
    //-------------------------------------------------------------------------

    /**
     * Set description field. Only works if desc length is > 0.
     */
    public void setDescription(String desc) {
        if (desc != null) {
            if (desc.length() > 0) {
                _commentField.setText(desc);
            }
        }
    }

    //---------------------------------------------------------------------
    private void jbInit() throws Exception {

        // JBuilder mess

        titledBorder1 = new TitledBorder(
                BorderFactory.createEtchedBorder(
                        Color.white, new Color(134, 134, 134)), "Properties (Read-Only)");

        titledBorder2 = new TitledBorder(
                BorderFactory.createEtchedBorder(
                        Color.white, new Color(134, 134, 134)), "Comments");

        border1 = BorderFactory.createEmptyBorder();

        border2 = BorderFactory.createCompoundBorder(
                titledBorder1, BorderFactory.createEmptyBorder(5, 5, 5, 5));

        border3 = BorderFactory.createCompoundBorder(
                titledBorder2, BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.setLayout(gridBagLayout1);

        _topPanel.setLayout(borderLayout1);
        _topPanel.setBorder(border2);
        _commentField.addFocusListener(new java.awt.event.FocusAdapter() {

            public void focusGained(FocusEvent e) {
                _commentField_focusGained(e);
            }
        });
        _topPanel.add(_sheet, BorderLayout.CENTER);

        this.add(_topPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        _commentField.setColumns(20);
        _commentField.setRows(4);
        _commentField.setText(_prompt);
        _scrollPane.setViewportView(_commentField);

        _bottomPanel.setBorder(border3);
        _bottomPanel.setLayout(borderLayout3);
        _bottomPanel.add(_scrollPane, BorderLayout.CENTER);
        this.add(_bottomPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    //-------------------------------------------------------------------------
    private void _commentField_focusGained(FocusEvent e) {
        _commentField.selectAll();
    }
}

