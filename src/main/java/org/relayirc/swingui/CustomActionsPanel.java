//-----------------------------------------------------------------------------
// $RCSfile: CustomActionsPanel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/06 11:40:37 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.ITab;
import org.relayirc.swingutil.ListEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/////////////////////////////////////////////////////////////////////////////

/**
 * Provides UI for editing a collection of CustomActions. Uses
 * a ListEditorPanel for editing the collection and a CustomActionEditPanel
 * for editing the actions in the collection.
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
public class CustomActionsPanel extends JPanel implements ITab {

    private final Vector _holders = null;
    private final BorderLayout _borderLayout = new BorderLayout();
    private final _ActionListEditorModel _model = new _ActionListEditorModel();
    private String _name = "CustomActionsPanel";
    private Vector _actions = new Vector();
    private ListEditorPanel _editorPanel = new ListEditorPanel();

    //-----------------------------------------------------------------------

    /**
     * Contruct UI for editing a collection of CustomActions.
     *
     * @param parent  Needed because panel launches a modal dialog
     * @param name    Display name of panel.
     * @param prompt  Prompt string.
     * @param actions Collection of CustomActions to be edited.
     */
    public CustomActionsPanel(
            JDialog parent, String name, String prompt, Vector actions) {

        _name = name;
        _actions = actions;
        _editorPanel = new ListEditorPanel(parent, prompt, _model);

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-----------------------------------------------------------------------

    /**
     * @deprecated Only for design-time use!
     */
    public CustomActionsPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------
    private void jbInit() throws Exception {
        this.setLayout(_borderLayout);
        add(_editorPanel, BorderLayout.CENTER);
    }
    //-----------------------------------------------------------------------

    /**
     * ITab implementation. @see ITab
     */
    public String getName() {
        return _name;
    }
    //-----------------------------------------------------------------------

    /**
     * ITab implementation. @see ITab
     */
    public boolean checkValues() {
        return true;
    }
    //-----------------------------------------------------------------------

    /**
     * ITab implementation. @see ITab
     */
    public void loadValues() {
        _editorPanel.loadValues();
    }
    //-----------------------------------------------------------------------

    /**
     * ITab implementation. @see ITab
     */
    public void saveValues() {
        _editorPanel.saveValues();
    }

    ////////////////////////////////////////////////////////////////////////

    private class _ActionListEditorModel implements ListEditorPanel.IModel {

        public _ActionListEditorModel() {
        }

        public Vector getObjects() {
            return _actions;
        }

        public ListEditorPanel.IHolder createNewObjectHolder(Object obj) {
            return new ActionHolder((CustomAction) obj);
        }

        public ListEditorPanel.IHolder createNewObjectHolder() {
            CustomAction action = new CustomAction(
                    "New action", CustomAction.JPYTHON_SCRIPT, "");
            return new ActionHolder(action);
        }
    }

    ////////////////////////////////////////////////////////////////////////

    public class ActionHolder implements ListEditorPanel.IHolder {

        private CustomAction _customAction = null; // the action

        private int _state = ListEditorPanel.UNTOUCHED;
        private int _type;
        private String _title;
        private String _action;

        public ActionHolder(CustomAction customAction) {
            _customAction = customAction;
            _type = _customAction.getType();
            _title = _customAction.getTitle();
            _action = _customAction.getAction();
        }

        /**
         * IHolder implementation.
         */
        public void saveValues() {
            _customAction.setTitle(_title);
            _customAction.setType(_type);
            _customAction.setAction(_action);
        }

        /**
         * IHolder implementation.
         */
        public Object getObject() {
            return _customAction;
        }

        /**
         * IHolder implementation.
         */
        public String getName() {
            return _title;
        }

        /**
         * IHolder implementation.
         */
        public boolean showDialog(Component parent) {

            // Show edit dialog for this action holder, return result
            CustomActionEditPanel editor;
            editor = new CustomActionEditPanel(this);
            return editor.showDialog(parent);
        }

        public CustomAction getCustomAction() {
            return _customAction;
        }

        // Simple accessors

        public int getState() {
            return _state;
        }

        /**
         * IHolder implementation.
         */
        public void setState(int s) {
            _state = s;
        }

        public String toString() {
            return _title;
        }

        public String getTitle() {
            return _title;
        }

        public void setTitle(String t) {
            _title = t;
        }

        public String getAction() {
            return _action;
        }

        public void setAction(String a) {
            _action = a;
        }

        public int getType() {
            return _type;
        }

        public void setType(int t) {
            _type = t;
        }
    }

}



