//-----------------------------------------------------------------------------
// $RCSfile: JoinPanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.Server;
import org.relayirc.swingutil.IconManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

///////////////////////////////////////////////////////////////////////

/**
 * Allows user to specify which channel is to be joined and to edit the
 * favorite channel list.
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
public class JoinPanel extends JPanel {

    BorderLayout borderLayout1 = new BorderLayout();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JPanel _channelPanel = new JPanel();
    JPanel _leftPanel = new JPanel();
    JLabel _nameLabel = new JLabel();
    JList _channelList = new JList();
    JScrollPane _channelScroller = new JScrollPane();
    JTextField _channelField = new JTextField();
    JLabel _listLabel = new JLabel();
    JPanel _buttonPanel = new JPanel();
    JPanel _rightPanel = new JPanel();
    JButton _addButton = new JButton("Add");
    JButton _joinButton = new JButton("Join");
    JButton _removeButton = new JButton("Remove");
    BorderLayout borderLayout2 = new BorderLayout();
    BorderLayout borderLayout3 = new BorderLayout();
    private ChatOptions _options;
    private Server _server;
    private String _channel = null;
    private _ChannelListModel _channelListModel = null;

    //-----------------------------------------------------------------------
    public JoinPanel() {
        try {
            _options = ChatApp.getChatApp().getOptions();
            _server = _options.getCurrentServer();
            jbInit();
            initEvents();
            loadValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------------
    public JButton getJoinButton() {
        return _joinButton;
    }

    //----------------------------------------------------------------------
    public String getSelectedChannel() {
        return _channel;
    }

    //----------------------------------------------------------------------
    public void saveValues() {
        _channel = _channelField.getText().trim();

        _options.getFavoriteChannels().setObjects(
                _channelListModel.getFaveChannels());
    }

    //----------------------------------------------------------------------
    public void loadValues() {
        Vector faveChannels = new Vector();
        for (int i = 0; i < _options.getFavoriteChannels().getChannelCount(); i++) {
            faveChannels.addElement(_options.getFavoriteChannels().getChannel(i));
        }
        _channelListModel = new _ChannelListModel(faveChannels);
        _channelList.setModel(_channelListModel);
    }

    //----------------------------------------------------------------------
    public boolean checkValues() {
        return _channelField.getText().trim().length() > 0
                && _channelField.getText().trim().startsWith("#");
    }

    //----------------------------------------------------------------------
    private void onTextFieldChanged() {
        _removeButton.setEnabled(false);

        if (_channelField.getText().trim().length() > 1
                && _channelField.getText().startsWith("#")) {

            // Appears to be valid channel name
            _addButton.setEnabled(true);

            // Enable join button only if connected
            _joinButton.setEnabled(_server.isConnected());
        } else {
            // Not valid channel name
            _addButton.setEnabled(false);
            _joinButton.setEnabled(false);
        }
    }

    //-----------------------------------------------------------------------
    private void onAddButtonClicked() {

        // Add new channel to favorite channels list
        Channel chan = new Channel(_channelField.getText());
        _channelListModel.addChannel(chan);

        // Select the new channel and enable join button so user may join it
        _channelList.setSelectedValue(chan, true);
        _channelField.setText(chan.getName());
        _joinButton.setEnabled(_server.isConnected());

        // Disable add button so user is less likely to add it twice
        _addButton.setEnabled(false);
    }

    //-----------------------------------------------------------------------
    private void onRemoveButtonClicked() {
        _channelListModel.removeChannel((Channel) _channelList.getSelectedValue());
        _channelField.setText("");
    }

    //-----------------------------------------------------------------------
    private void onChannelSelectionChanged() {
        if (_channelList.getSelectedValue() != null) {
            Channel channel = (Channel) _channelList.getSelectedValue();
            _channelField.setText(channel.getName());
            _addButton.setEnabled(false);
            _removeButton.setEnabled(true);

            // Enable join button only if connected
            _joinButton.setEnabled(_server.isConnected());
        } else {
            _removeButton.setEnabled(false);
        }
    }

    //-----------------------------------------------------------------------
    private void jbInit() throws Exception {

        this.setLayout(borderLayout1);

        // Channel panel

        _nameLabel.setText("Choose channel to be joined");
        _nameLabel.setIcon(IconManager.getIcon("Users"));

        _channelField.setText("");

        _listLabel.setText("Favorites");
        _listLabel.setToolTipText("");
        _listLabel.setIcon(IconManager.getIcon("Favorite"));

        _channelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        _channelScroller.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        _channelScroller.setViewportView(_channelList);

        _leftPanel.setLayout(borderLayout3);
        _rightPanel.setLayout(borderLayout2);
        this.add(_leftPanel, BorderLayout.CENTER);
        _leftPanel.add(_channelPanel, BorderLayout.NORTH);
        _channelPanel.setLayout(gridBagLayout2);
        _channelPanel.add(_channelScroller,
                new GridBagConstraints(0, 3, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 136));
        _channelPanel.add(_nameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 60), 0, 0));
        _channelPanel.add(_channelField,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        _channelPanel.add(_listLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));

        // Button panel

        _removeButton.setEnabled(false);
        _removeButton.setToolTipText(
                "Remove selected channel from list of favorites");
        _removeButton.setHorizontalAlignment(SwingConstants.LEFT);
        _removeButton.setIcon(IconManager.getIcon("Delete"));

        _joinButton.setIcon(IconManager.getIcon("ReplyAll"));
        _joinButton.setHorizontalAlignment(SwingConstants.LEFT);
        _joinButton.setToolTipText("Join channel");
        _joinButton.setEnabled(false);

        _addButton.setEnabled(false);
        _addButton.setToolTipText("Add channel to list of favorites");
        _addButton.setHorizontalAlignment(SwingConstants.LEFT);
        _addButton.setIcon(IconManager.getIcon("Plus"));

        this.add(_rightPanel, BorderLayout.EAST);
        _rightPanel.add(_buttonPanel, BorderLayout.NORTH);
        _buttonPanel.setLayout(gridBagLayout1);
        _buttonPanel.add(_joinButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        _buttonPanel.add(_removeButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        _buttonPanel.add(_addButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    //-----------------------------------------------------------------------
    private void initEvents() {

        // Text field for entering channel name
        _channelField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                onTextFieldChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                onTextFieldChanged();
            }

            public void changedUpdate(DocumentEvent e) {
                onTextFieldChanged();
            }
        });
        // Button for adding new channel using text in text field
        _addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddButtonClicked();
            }
        });
        // Button for removing channel selected in channel list
        _removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRemoveButtonClicked();
            }
        });
        // React to changes in channel list selection
        _channelList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                onChannelSelectionChanged();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////

    private class _ChannelListModel extends AbstractListModel {
        public Vector _faveChannels = null;

        public _ChannelListModel(Vector faveChannels) {
            _faveChannels = faveChannels;
        }

        public int getSize() {
            return _faveChannels.size();
        }

        public Vector getFaveChannels() {
            return _faveChannels;
        }

        public Object getElementAt(int index) {
            return _faveChannels.elementAt(index);
        }

        public void addChannel(Channel channel) {
            _faveChannels.addElement(channel);
            int end = _faveChannels.size();
            fireIntervalAdded(this, 0, end > 0 ? end : 0);
        }

        public void removeChannel(Channel channel) {
            _faveChannels.removeElement(channel);
            int end = _faveChannels.size();
            fireIntervalRemoved(this, 0, end > 0 ? end : 0);
        }
    }
}

