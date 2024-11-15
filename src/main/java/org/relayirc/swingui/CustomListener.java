//-----------------------------------------------------------------------------
// $RCSfile: CustomListener.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import java.io.Serializable;

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
public class CustomListener implements Serializable {

    public static final int JPYTHON_SCRIPT = 0;
    public static final int JAVA_CLASS = 1;
    private final String _listenerString; // File name or full class name of listener
    private int _type;        // See types above
    private String _title;       // Name to be displayed in menu, lists, etc.
    private String _subject;     // Name of listener subject
    private String _implements;  // Interface implemented by _listener
    private Object _listener;    // Actual listener object

    // Class name of listener interface that listener implements.
    // For example "org.relayirc.chatengine.ChannelListener"
    private String _listenerClassName;

    //--------------------------------------------------------------------------

    /**
     * Construct CustomListener by specifying its title, type and listener
     * string.
     * <br/>
     * If type is JPYTHON_SCRIPT, then action must be the full path
     * to a JPython script to be run.
     * <br/>
     * If type is JAVA_CLASS, then action must be the fully qualified Java
     * class name of a class that implements IRelayPlugin.
     * <br/>
     *
     * @param title    Name to be displayed on menus, lists, etc.
     * @param type     Type of command, see static final int fields.
     * @param subject  Name of object to be listened to.
     * @param listener File name or class name or listener.
     * @param imps     Class name of listener interface implemented by listener.
     */
    public CustomListener(
            String title, int type, String subject, String listener, String imps) {

        _title = title;
        _type = type;
        _subject = subject;
        _listenerString = listener;
    }
    //--------------------------------------------------------------------------

    public int getType() {
        return _type;
    }

    public void setType(int t) {
        _type = t;
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
        _subject = s;
    }

    //--------------------------------------------------------------------------
    public Object getListener() {
        // TODO
        // Instantiate listener if necessary
        return null;
    }
}

