//-----------------------------------------------------------------------------
// $RCSfile: TriggeredListEditor.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil.propsheet.editors;

import org.relayirc.swingutil.DefaultListEditorDlg;
import org.relayirc.util.Debug;

import java.awt.*;
import java.util.Vector;

////////////////////////////////////////////////////////////////////////////////

/**
 * Editor wih a trigger button that launches a simple list editor dialog.
 *
 * @author David M Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay IRC Chat Engine<br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong>HAHT Commerce, Inc.
 * <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class TriggeredListEditor extends TriggeredEditor {

    Frame _parent;

    //--------------------------------------------------------------------------
    public TriggeredListEditor(Frame parent) {
        super();
        _parent = parent;
    }

    //--------------------------------------------------------------------------
    public void triggered() {
        Vector list = (Vector) getValue();

        DefaultListEditorDlg dlg = new DefaultListEditorDlg(_parent, list);

        if (dlg.isOk()) {
            Debug.println("Setting prop value = " + dlg.getList());
            setValue(dlg.getList());
        }
    }

    //---------------------------------------------------------------------------
    public String getAsText() {
        return getValue().toString();
    }

    //--------------------------------------------------------------------------
    public void setAsText(String str) {
        // We can ignore this
    }
}

