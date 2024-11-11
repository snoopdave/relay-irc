//-----------------------------------------------------------------------------
// $RCSfile: CustomListenersPanel.java,v $
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
 * Provides UI for editing a collection of CustomListeners. Uses
 * a ListEditorPanel for editing the collection and a CustomListenerEditPanel
 * for editing the listeners in the collection.
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
public class CustomListenersPanel extends JPanel implements ITab {

    private final BorderLayout _borderLayout = new BorderLayout();
    private final _ListenerListEditorModel _model = new _ListenerListEditorModel();
    private String _name = "CustomListenersPanel";
    private Vector _listeners = new Vector();
    private ListEditorPanel _editorPanel = new ListEditorPanel();

    //-----------------------------------------------------------------------

    /**
     * Contruct UI for editing a collection of CustomListeners.
     *
     * @param parent    Needed because panel launches a modal dialog
     * @param name      Display name of panel.
     * @param prompt    Prompt string.
     * @param listeners Collection of CustomActions to be edited.
     */
    public CustomListenersPanel(
            JDialog parent, String name, String prompt, Vector listeners) {

        _name = name;
        _listeners = listeners;
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
    public CustomListenersPanel() {
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

    private class _ListenerListEditorModel implements ListEditorPanel.IModel {

        public _ListenerListEditorModel() {
        }

        public Vector getObjects() {
            return _listeners;
        }

        public ListEditorPanel.IHolder createNewObjectHolder(Object obj) {
            return new ListenerHolder((CustomListener) obj);
        }

        public ListEditorPanel.IHolder createNewObjectHolder() {
            CustomListener listener = new CustomListener(
                    "New Listener", CustomListener.JPYTHON_SCRIPT, "dummy", "newscript.py", "dummy");
            return new ListenerHolder(listener);
        }
    }

    ////////////////////////////////////////////////////////////////////////

    public class ListenerHolder implements ListEditorPanel.IHolder {

        private CustomListener _listener = null; // the action

        private int _state = ListEditorPanel.UNTOUCHED;
        private int _type;
        private String _title;
        private String _subject;
        private String _listenerString;

        public ListenerHolder(CustomListener listener) {
            _type = listener.getType();
            _title = listener.getTitle();
            _subject = listener.getSubject();
            _listenerString = listener.getListenerString();
            _listener = listener;
        }

        /**
         * IHolder implementation.
         */
        public void saveValues() {
            _listener.setTitle(_title);
            _listener.setType(_type);
            _listener.setSubject(_subject);
            _listener.setListenerString(_listenerString);
        }

        /**
         * IHolder implementation.
         */
        public Object getObject() {
            return _listener;
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
            CustomListenerEditPanel editor;
            editor = new CustomListenerEditPanel(this);
            return editor.showDialog(parent);
        }

        public CustomListener getCustomListener() {
            return _listener;
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

        public String getSubject() {
            return _subject;
        }

        public void setSubject(String s) {
            _subject = s;
        }

        public String getListenerString() {
            return _listenerString;
        }

        public void setListenerString(String s) {
            _listenerString = s;
        }

        public int getType() {
            return _type;
        }

        public void setType(int t) {
            _type = t;
        }
    }

}


