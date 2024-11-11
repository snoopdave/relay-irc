//-----------------------------------------------------------------------------
// $RCSfile: UserList.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.Server;
import org.relayirc.chatengine.User;
import org.relayirc.swingutil.GuiListCellRenderer;
import org.relayirc.util.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

/**
 * <p>List component which displays the users currently present in a
 * chat channel. Provides a popup menu so that the user may request
 * WHOIS, VERSION or PING information on specific users.</p>
 *
 * <p>Operators (users whose nicks start with @) are listed at the
 * beginnning of the list.</p>
 * <p>
 * FIX: List should be sorted alphabetically (with ops listed first).<br>
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
public class UserList extends JList {

    private final Server _server;
    private final Channel _channel;
    private final Hashtable _wrappersByName = new Hashtable();

    /**
     * Construct a user list for a specified channel.
     */
    public UserList(Channel chan) {
        super(new DefaultListModel());
        _server = ChatApp.getChatApp().getServer();
        _channel = chan;

        setCellRenderer(new GuiListCellRenderer(this));

        try {
            ChatOptions opt = ChatApp.getChatApp().getOptions();
            String fname = opt.getProperty("gui.channel.font.name");
            int fstyle = Integer.parseInt(
                    opt.getProperty("gui.channel.font.style"));
            int fsize = Integer.parseInt(opt.getProperty("gui.channel.font.size"));
            setFont(new Font(fname, fstyle, fsize));
        } catch (Exception e) {
            Debug.printStackTrace(e);
        }

        addMouseListener(new MouseAdapter() {
        });
    }
    //-----------------------------------------------------------------

    /**
     * Returns true if the specified user is in the list. Checks the user
     * name and @name in case the user is an operator.
     */
    public boolean contains(String nick) {
        return
                _wrappersByName.containsKey(nick)
                        || _wrappersByName.containsKey("@" + nick);
    }
    //-----------------------------------------------------------------

    /**
     * Adds the specified user to the list. If the user's is an operator
     * with nick name of the format @name, then user is treated as an
     * operator and added to the beginning of the list.
     */
    public void addItem(String nick) {

        if (nick.charAt(0) == '@') {

            GuiUser go = (GuiUser) _wrappersByName.get(nick);
            if (go != null) {
                ((DefaultListModel) getModel()).removeElement(go);
            }

            go = (GuiUser) _wrappersByName.get(nick.substring(1));
            if (go != null) {
                ((DefaultListModel) getModel()).removeElement(go);
            }

            go = new GuiUser(new User(nick), _channel);

            ((DefaultListModel) getModel()).insertElementAt(go, 0);

            _wrappersByName.put(nick, go);
        } else {

            // If user is not already present in list as an operator
            GuiUser go = (GuiUser) _wrappersByName.get("@" + nick);
            if (go == null) {

                // then remove and add
                go = (GuiUser) _wrappersByName.get(nick);
                if (go != null) {
                    ((DefaultListModel) getModel()).removeElement(go);
                }

                go = new GuiUser(new User(nick), _channel);
                ((DefaultListModel) getModel()).addElement(go);

                _wrappersByName.put(nick, go);
            }
        }
    }
    //-----------------------------------------------------------------

    /**
     * Removes the specified user from the list, checks both nick and @nick.
     */
    public void remove(String nick) {

        GuiUser go = (GuiUser) _wrappersByName.get(nick);
        if (go != null) {
            _wrappersByName.remove(nick);
            ((DefaultListModel) getModel()).removeElement(go);
        }

        go = (GuiUser) _wrappersByName.get("@" + nick);
        if (go != null) {
            _wrappersByName.remove("@" + nick);
            ((DefaultListModel) getModel()).removeElement(go);
        }
    }
    //-----------------------------------------------------------------

    /**
     * Used internally to handler pop-up menu mouse clicks.
     */
    public void processMouseEvent(MouseEvent e) {

        if (e.isPopupTrigger() && getSelectedValue() != null) {

            GuiUser guiUser = (GuiUser) getSelectedValue();
            JPopupMenu popup = guiUser.createPopupMenu();
            add(popup);
            popup.show(this, e.getX(), e.getY());

        } else super.processMouseEvent(e);
    }
}

