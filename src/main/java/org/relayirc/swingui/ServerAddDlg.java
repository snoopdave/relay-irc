//-----------------------------------------------------------------------------
// $RCSfile: ServerAddDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.StandardDlg;

import java.awt.*;
import java.util.Vector;

//////////////////////////////////////////////////////////////////////////////

/**
 * Dialog for editing a new or existing server object via a server holder:
 * uses ServerAddPanel.
 *
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL</p>
 * Original Code: Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class ServerAddDlg extends StandardDlg {
    private ServerAddPanel _serverAddPanel = null;

    /**
     * Construct dialog for editing a server held in a ServerHolder.
     *
     * @param holder  server holder to be edited
     * @param holders other servers to consider
     */
    public ServerAddDlg(String title,
                        ServerPanel.ServerHolder holder, Vector holders) {

        super(null, title, true, true);
        _serverAddPanel = new ServerAddPanel(holder, holders);
        getContentPane().add(_serverAddPanel, BorderLayout.CENTER);
        pack();
        centerOnScreen();
    }

    //----------------------------------------------------------------
    public boolean onOk() {
        if (_serverAddPanel.checkValues()) {
            _serverAddPanel.saveValues();
            return true;
        } else {
            return false;
        }
    }
}

