
/*
 * FILE: GridBagConstraints2.java
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is Relay IRC chat client.
 *
 * The Initial Developer of the Original Code is David M. Johnson.
 * Portions created by David M. Johnson are Copyright (C) 1998.
 * All Rights Reserved.
 *
 * Contributor(s): No contributors to this file.
 */
package org.relayirc.swingutil;

import java.awt.*;

/**
 * Convenience class for creating grid bag constraints.
 */
public class GridBagConstraints2 extends GridBagConstraints {
    public GridBagConstraints2() {
        super();
    }

    public GridBagConstraints2(
            int gridx, int gridy, int gridw, int gridh, double weightx, double weighty,
            int anchor, int fill, Insets insets, int ipadx, int ipady) {

        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridw;
        this.gridheight = gridh;
        this.weightx = weightx;
        this.weighty = weighty;
        this.anchor = anchor;
        this.fill = fill;
        this.insets = insets;
        this.ipadx = ipadx;
        this.ipady = ipady;
    }
}
