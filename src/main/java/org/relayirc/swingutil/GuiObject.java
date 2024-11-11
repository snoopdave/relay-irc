//-----------------------------------------------------------------------------
// $RCSfile: GuiObject.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 16:07:53 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;

import javax.swing.*;

/**
 * Gui object for use in Swing lists, trees, etc.
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
 * @see org.relayirc.swingutil.GuiListCellRenderer
 */
public class GuiObject {
    private int _stringLength = -1;
    private String _stringValue;
    private Object _object;
    private Icon _icon;

    //--------------------------------------------------------------------------
    public GuiObject(String stringValue, Object object) {
        _stringValue = stringValue;
        _object = object;
    }

    //--------------------------------------------------------------------------
    public GuiObject(String stringValue, Object object, Icon icon) {
        this(stringValue, object);
        _icon = icon;
    }
    //--------------------------------------------------------------------------

    /**
     * Sets the length of strings returned by toString(). Set
     * to -1 to disable. Only allows length > 3.
     */
    public void setStringLength(int len) {
        if (len > 3 || len == -1) _stringLength = len;
    }
    //--------------------------------------------------------------------------

    public Icon getIcon() {
        return _icon;
    }

    public void setIcon(Icon icon) {
        _icon = icon;
    }

    public Object getObject() {
        return _object;
    }

    public void setObject(Object object) {
        _object = object;
    }

    public String getString() {
        return _stringValue;
    }

    public void setString(String stringValue) {
        _stringValue = stringValue;
    }

    //--------------------------------------------------------------------------
    public String toString() {
        if (_stringLength == -1) return _stringValue;

        if (_stringValue.length() > _stringLength) {
            return _stringValue.substring(0, _stringLength - 3) + "...";
        } else if (_stringValue.length() > _stringLength) {
            int len = _stringValue.length() - _stringLength;
            return _stringValue + new String(new StringBuffer(len));
        } else {
            return _stringValue;
        }
    }
}

