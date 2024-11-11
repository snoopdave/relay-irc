//-----------------------------------------------------------------------------
// $RCSfile: ListEditorPanel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 16:07:53 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

/////////////////////////////////////////////////////////////////////////////

/**
 * Provides UI for editing a list of objects. Callers must implement the
 * ListEditorPanel.IModel and ListEditorPanel.IHolder interfaces.
 *
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
public class ListEditorPanel extends JPanel {

    // States of a holder
    public static final int UNTOUCHED = 0;
    public static final int ADDED = 1;
    public static final int EDITED = 2;
    public static final int DELETED = 3;
    /**
     * List model for JList.
     */
    private final _ListModel _listModel = new _ListModel();
    private final Vector<IHolder> _holders = new Vector<>(); // of IHolders
    private final JLabel _itemLabel = new JLabel();
    private final JList _itemList = new JList();
    private final JButton _addButton = new JButton();
    private final JButton _upButton = new JButton();
    private final JButton _removeButton = new JButton();
    private final JButton _editButton = new JButton();
    private final JButton _downButton = new JButton();
    private final GridBagLayout gridBagLayout1 = new GridBagLayout();
    private final JScrollPane _scrollPane = new JScrollPane();
    private Component _parent = null;
    private String _prompt = "Prompt";
    /**
     * <i>The</i> ListEditorPanel model.
     */
    private IModel _model = null;
    /**
     * Contruct UI for editing a collection of objects.
     *
     * @param parent Needed because panel launches a modal dialog
     * @param prompt Prompt string.
     * @param model  List model for list to be edited.
     */
    public ListEditorPanel(Component parent, String prompt, IModel model) {

        this();
        _parent = parent;
        _prompt = prompt;
        _model = model;
        loadIcons();
        _itemList.setModel(_listModel);
    }
    /**
     * @deprecated Only for design-time!
     */
    @Deprecated
    public ListEditorPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------

    //-----------------------------------------------------------------------
    public void loadValues() {

        for (int i = 0; i < _model.getObjects().size(); i++) {
            ListEditorPanel.IHolder holder =
                    _model.createNewObjectHolder(_model.getObjects().elementAt(i));
            _holders.addElement(holder);
        }
    }
    //-----------------------------------------------------------------------

    //-----------------------------------------------------------------------
    public void saveValues() {

        _model.getObjects().removeAllElements();

        for (int i = 0; i < _holders.size(); i++) {
            IHolder holder = _holders.elementAt(i);
            holder.saveValues();
            _model.getObjects().addElement(holder.getObject());
        }

    }

    //-----------------------------------------------------------------------
    public void loadIcons() {
        _addButton.setIcon(IconManager.getIcon("Plus"));
        _removeButton.setIcon(IconManager.getIcon("Delete"));
        _editButton.setIcon(IconManager.getIcon("DocumentDraw"));
        _upButton.setIcon(IconManager.getIcon("Up"));
        _downButton.setIcon(IconManager.getIcon("Down"));

        onListSelectionChanged(null);
    }

    /**
     * Respond to ADD button.
     */
    void onAddButtonPressed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // Create new action for user to edit
                IHolder holder = _model.createNewObjectHolder();

                // Show editor dialog
                if (holder.showDialog(_parent)) {

                    // User hit Ok button, so add new action to collection
                    holder.setState(ADDED);
                    _holders.addElement(holder);
                    _listModel.onListChanged();
                }
            }
        });
    }

    /**
     * Respond to EDIT button.
     */
    void onEditButtonPressed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // Get currently selected holder
                IHolder holder = (IHolder) _itemList.getSelectedValue();

                // Show editor dialog
                if (holder.showDialog(_parent)) {

                    holder.setState(EDITED);
                    _listModel.onListChanged();
                }
            }
        });
    }
    //-----------------------------------------------------------------------

    /**
     * Respond to REMOVE button.
     */
    void onRemoveButtonPressed(ActionEvent e) {

        int sel = _itemList.getSelectedIndex();
        _holders.remove(sel);

        if (sel < _itemList.getModel().getSize()) {
            _itemList.setSelectedIndex(sel);
        }

        _listModel.onListChanged();
    }
    //-----------------------------------------------------------------------

    /**
     * Respond to DOWN button.
     */
    void onDownButtonPressed(ActionEvent e) {
        IHolder holder = (IHolder) _itemList.getSelectedValue();
        int sel = _itemList.getSelectedIndex();
        if (sel < _holders.size() - 1) {
            _holders.remove(sel);
            _holders.insertElementAt(holder, sel + 1);
            _itemList.setSelectedIndex(sel + 1);
            _listModel.onListChanged();
        }
    }
    //-----------------------------------------------------------------------

    /**
     * Respond to UP button.
     */
    void onUpButtonPressed(ActionEvent e) {
        IHolder holder = (IHolder) _itemList.getSelectedValue();
        int sel = _itemList.getSelectedIndex();
        if (sel > 0) {
            _holders.remove(sel);
            _holders.insertElementAt(holder, sel - 1);
            _itemList.setSelectedIndex(sel - 1);
            _listModel.onListChanged();
        }
    }
    //-----------------------------------------------------------------------

    /**
     * Enable and disable buttons in response to list selection changed.
     */
    void onListSelectionChanged(ListSelectionEvent e) {
        if (_itemList.getSelectedValue() != null) {
            _editButton.setEnabled(true);
            _removeButton.setEnabled(true);
            _upButton.setEnabled(true);
            _downButton.setEnabled(true);
        } else {
            _editButton.setEnabled(false);
            _removeButton.setEnabled(false);
            _upButton.setEnabled(false);
            _downButton.setEnabled(false);
        }
    }
    //-----------------------------------------------------------------------

    //-----------------------------------------------------------------------
    private void jbInit() throws Exception {
        this.setLayout(gridBagLayout1);
        _addButton.setHorizontalAlignment(SwingConstants.LEFT);
        _addButton.setText("Add");
        _addButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onAddButtonPressed(e);
            }
        });
        _upButton.setHorizontalAlignment(SwingConstants.LEFT);
        _upButton.setText("Move Up");
        _upButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onUpButtonPressed(e);
            }
        });
        _removeButton.setHorizontalAlignment(SwingConstants.LEFT);
        _removeButton.setText("Remove");
        _removeButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onRemoveButtonPressed(e);
            }
        });
        _editButton.setHorizontalAlignment(SwingConstants.LEFT);
        _editButton.setText("Edit");
        _editButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onEditButtonPressed(e);
            }
        });
        _downButton.setHorizontalAlignment(SwingConstants.LEFT);
        _downButton.setText("Move Down");
        _downButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onDownButtonPressed(e);
            }
        });
        _itemList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                onListSelectionChanged(e);
            }
        });

        _itemLabel.setFont(new java.awt.Font("Dialog", 0, 14));
        _itemLabel.setText(_prompt);

        _scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        _scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        _scrollPane.setMinimumSize(new Dimension(150, 120));
        _scrollPane.setPreferredSize(new Dimension(150, 120));
        this.setMinimumSize(new Dimension(40, 30));
        _itemList.setPreferredSize(new Dimension(150, 105));
        _itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(_scrollPane, new GridBagConstraints(1, 1, 1, 4, 0.8, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        this.add(_removeButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        this.add(_editButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        this.add(_addButton, new GridBagConstraints(0, 1, 1, 1, 0.1, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        this.add(_upButton, new GridBagConstraints(2, 1, 1, 1, 0.1, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        this.add(_downButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        this.add(_itemLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        _scrollPane.getViewport().add(_itemList, null);

        this.setPreferredSize(new Dimension(500, 175));
    }
    //-----------------------------------------------------------------------

    /**
     * Model provides access to object list and object instantiation
     */
    public interface IModel {
        Vector getObjects(); // of Objects

        IHolder createNewObjectHolder(Object obj);

        IHolder createNewObjectHolder();
    }

    /**
     * Holder holds list object
     */
    public interface IHolder {
        String getName();

        Object getObject();

        void saveValues();

        void setState(int s);

        boolean showDialog(Component parent);
    }

    ///////////////////////////////////////////////////////////////////////////

    private class _ListModel extends AbstractListModel {
        public int getSize() {
            return _holders.size();
        }

        public Object getElementAt(int index) {
            return _holders.elementAt(index);
        }

        public void onListChanged() {
            fireContentsChanged(this, 0, _holders.size());
        }
    }
}

