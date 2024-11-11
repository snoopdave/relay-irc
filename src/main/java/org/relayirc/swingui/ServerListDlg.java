//-----------------------------------------------------------------------------
// $RCSfile: ServerListDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.StandardDlg;

import java.awt.*;

///////////////////////////////////////////////////////////////////////

/**
 * Server list dialog allows user to edit the server list
 * using the ServerListPanel component.
 *
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
public class ServerListDlg extends StandardDlg {

    public ServerListDlg(Frame parent, ChatOptions.ServerList list) {
        super(parent, "Server List", true, false);

        getContentPane().add(new ServerListPanel(list), BorderLayout.CENTER);

        setSize(600, 350);
        centerOnScreen();
        setVisible(true);
    }

    public boolean onOk() {
        //ChatApp.getChatApp().getOptions().fireUpdate();
        return super.onOk();
    }
}
