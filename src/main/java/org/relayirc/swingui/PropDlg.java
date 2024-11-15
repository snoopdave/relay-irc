//-----------------------------------------------------------------------------
// $RCSfile: PropDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.IChatObject;
import org.relayirc.swingutil.StandardDlg;

import java.awt.*;

////////////////////////////////////////////////////////////////////

/**
 * Property dialog for IChatObjects. Uses PropPanel.
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
 * @see org.relayirc.chatengine.IChatObject
 */
public class PropDlg extends StandardDlg {
    private final PropPanel _panel = new PropPanel();
    private IChatObject _chatObject = null;

    //--------------------------------------------------------
    public PropDlg(Frame frame, String title, IChatObject chatObject) {
        super(frame, title, true);
        _chatObject = chatObject;
        _panel.setObject(_chatObject);
        _panel.setDescription(_chatObject.getDescription());
        initGUI();
    }

    //--------------------------------------------------------
    public boolean onOk() {
        _chatObject.setDescription(_panel.getDescription());
        return true;
    }

    //--------------------------------------------------------
    public void initGUI() {
        getContentPane().add(_panel, BorderLayout.CENTER);
        pack(); //setSize(350,450);
        centerOnScreen();
        setVisible(true);
    }
}


