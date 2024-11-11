//-----------------------------------------------------------------------------
// $RCSfile: ColorPanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.ColorCombo;
import org.relayirc.swingutil.GridBagConstraints2;
import org.relayirc.swingutil.ITab;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

///////////////////////////////////////////////////////////////////////

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
public class ColorPanel extends JPanel implements ITab {

    private final ColorCombo _messages = new ColorCombo();
    private final ColorCombo _actions = new ColorCombo();
    private final ColorCombo _joins = new ColorCombo();
    private final ColorCombo _parts = new ColorCombo();
    private final ColorCombo _ops = new ColorCombo();
    private final ColorCombo _kicks = new ColorCombo();
    private final ColorCombo _bans = new ColorCombo();
    private final ColorCombo _nicks = new ColorCombo();
    private final ChatOptions _options;

    private final JLabel _nicksLabel = new JLabel("Nicks");
    private final JLabel _bansLabel = new JLabel("Bans");
    private final JLabel _kicksLabel = new JLabel("Kicks");
    private final JLabel _opsLabel = new JLabel("Ops");
    private final JLabel _partsLabel = new JLabel("Parts");
    private final JLabel _joinsLabel = new JLabel("Joins");
    private final JLabel _messagesLabel = new JLabel("Messages");
    private final JLabel _actionsLabel = new JLabel("Actions");


    //---------------------------------------------------------------
    public ColorPanel() {
        _options = ChatApp.getChatApp().getOptions();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //---------------------------------------------------------------
    public boolean checkValues() {
        return true;
    }

    //---------------------------------------------------------------
    public void loadValues() {

        _messages.setSelectedColorName(
                _options.getProperty("gui.channel.color.messages"));

        _actions.setSelectedColorName(
                _options.getProperty("gui.channel.color.actions"));

        _joins.setSelectedColorName(
                _options.getProperty("gui.channel.color.joins"));

        _parts.setSelectedColorName(
                _options.getProperty("gui.channel.color.parts"));

        _ops.setSelectedColorName(
                _options.getProperty("gui.channel.color.ops"));

        _kicks.setSelectedColorName(
                _options.getProperty("gui.channel.color.kicks"));

        _bans.setSelectedColorName(
                _options.getProperty("gui.channel.color.bans"));

        _nicks.setSelectedColorName(
                _options.getProperty("gui.channel.color.nicks"));
    }
    //---------------------------------------------------------------

    public void saveValues() {

        _options.setProperty(
                "gui.channel.color.messages", _messages.getSelectedColorName());

        _options.setProperty(
                "gui.channel.color.actions", _actions.getSelectedColorName());

        _options.setProperty(
                "gui.channel.color.joins", _joins.getSelectedColorName());

        _options.setProperty(
                "gui.channel.color.parts", _parts.getSelectedColorName());

        _options.setProperty(
                "gui.channel.color.ops", _ops.getSelectedColorName());

        _options.setProperty(
                "gui.channel.color.kicks", _kicks.getSelectedColorName());

        _options.setProperty(
                "gui.channel.color.bans", _bans.getSelectedColorName());

        _options.setProperty(
                "gui.channel.color.nicks", _nicks.getSelectedColorName());

    }

    //---------------------------------------------------------------
    public String getName() {
        return "Colors";
    }

    private void jbInit() throws Exception {
        setBorder(new EmptyBorder(10, 10, 10, 10)); // tlbr
        setLayout(new GridBagLayout());

        add(_messages, new GridBagConstraints2(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_messagesLabel, new GridBagConstraints2(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_actions, new GridBagConstraints2(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_actionsLabel, new GridBagConstraints2(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_joins, new GridBagConstraints2(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_joinsLabel, new GridBagConstraints2(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_parts, new GridBagConstraints2(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_partsLabel, new GridBagConstraints2(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_ops, new GridBagConstraints2(3, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_opsLabel, new GridBagConstraints2(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_kicks, new GridBagConstraints2(3, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_kicksLabel, new GridBagConstraints2(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_bans, new GridBagConstraints2(3, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_bansLabel, new GridBagConstraints2(2, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_nicks, new GridBagConstraints2(3, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));

        add(_nicksLabel, new GridBagConstraints2(2, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(3, 3, 3, 3), 0, 0));
    }
}

