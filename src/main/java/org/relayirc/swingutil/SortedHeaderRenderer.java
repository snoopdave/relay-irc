/*
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
 * Contributor(s): No contributors to this file.''
 */

package org.relayirc.swingutil;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

////////////////////////////////////////////////////////////////////////////////

public class SortedHeaderRenderer extends JLabel implements TableCellRenderer {

    public static final int UNSORTED = 1;
    public static final int SORTED_ASCENDING = 2;
    public static final int SORTED_DESCENDING = 3;

    private int state = UNSORTED;
    private final Icon noIcon = IconManager.getIcon("noarrow");
    private final Icon upIcon = IconManager.getIcon("up");
    private final Icon downIcon = IconManager.getIcon("down");

    //---------------------------------------------------------------------------
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case SORTED_ASCENDING:
                    setIcon(upIcon);
                    break;
                case SORTED_DESCENDING:
                    setIcon(downIcon);
                    break;
                case UNSORTED:
                default:
                    setIcon(noIcon);
                    break;
            }
        }
    }

    //---------------------------------------------------------------------------
    public int getState() {
        return state;
    }

    public SortedHeaderRenderer() {
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    //---------------------------------------------------------------------------
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setFont(table.getFont());

        if (isSelected) {
            setForeground(UIManager.getColor("Table.selectionForeground"));
            setBackground(UIManager.getColor("Table.selectionBackground"));
        } else {
            setForeground(UIManager.getColor("TableHeader.foreground"));
            setBackground(UIManager.getColor("TableHeader.background"));

        }
        if (value != null)
            setText(value.toString());
        else
            setText("");
        return this;
    }
}

