//-----------------------------------------------------------------------------
// $RCSfile: Channel.java,v $
// $Revision: 1.1.2.9 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//-----------------------------------------------------------------------------

package org.relayirc.chatengine;

import org.relayirc.core.IRCConnectionAdapter;
import org.relayirc.core.IRCConnectionListener;
import org.relayirc.util.Debug;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.Serial;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * An IRC channel class that includes methods for joining, parting,
 * kicking, banning, adding/removing channel listeners and property
 * change support.
 *
 * @author David M. Johnson
 * @version $Revision: 1.1.2.9 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">...</a></p>
 * Original Code:     Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s):    No contributors to this file <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class Channel implements IChatObject, Serializable {

    @Serial
    private static final long serialVersionUID = -7306475367688154363L;
    private final transient StringBuffer _messageLineBuffer = new StringBuffer(500);
    private String _name = "";
    private String _desc = "";
    private String _topic = "";
    private int _userCount = 0;
    private int _maxNumBufferedUnsentMessages = 0; // 0 = don't buffer, -1 = buffer all
    private transient Vector _unsentMessagesBuffer = null;
    private transient int _numDroppedMessages = 0;

    private transient PropertyChangeSupport _propChangeSupport = null;
    private transient Server _server = null;
    private transient Vector<ChannelListener> _listeners = new Vector<>();
    private transient boolean _isConnected = false;
    private transient _ChannelMux _mux = new _ChannelMux();

    //------------------------------------------------------------------

    /**
     * Construct channel with name alone.
     */
    public Channel(String name) {
        _name = name;
        _propChangeSupport = new PropertyChangeSupport(this);
    }
    //------------------------------------------------------------------

    /**
     * Construct channel with server.
     */
    public Channel(String name, Server server) {
        this(name);
        _server = server;
    }
    //------------------------------------------------------------------

    /**
     * Channel with name, topic, user count and a server.
     */
    public Channel(String name, String topic, int ucount, Server server) {
        this(name);
        _topic = topic;
        _userCount = ucount;
        _server = server;
    }

    //------------------------------------------------------------------
    public boolean equals(Object object) {
        if (object instanceof Channel) {
            return ((Channel) object).getName().equals(getName());
        }
        return false;
    }

    private synchronized void notifyListeners(_ChannelEventNotifier notifier) {
        for (int i = 0; i < _listeners.size(); i++) {
            ChannelListener listener = _listeners.elementAt(i);
            notifier.notify(listener);
        }
    }

    /**
     * Channel listener support.
     */
    public synchronized void addChannelListener(ChannelListener listener) {
        _listeners.addElement(listener);
    }

    /**
     * Channel listener support.
     */
    public synchronized void removeChannelListener(ChannelListener listener) {
        _listeners.removeElement(listener);
    }

    /**
     * Property change support.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        _propChangeSupport.addPropertyChangeListener(listener);
    }
    //==================================================================

    /**
     * Property change support.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        _propChangeSupport.removePropertyChangeListener(listener);
    }
    //------------------------------------------------------------------

    /**
     * True if channel is connected/joined.
     */
    public boolean isConnected() {
        return _isConnected;
    }
    //------------------------------------------------------------------

    /**
     * Set connection status.
     */
    // MGENTFIX (bug #111587): made this package protected
    void setConnected(boolean value) {

        _isConnected = value;

        final ChannelEvent event2 = new ChannelEvent(Channel.this);
        notifyListeners(new _ChannelEventNotifier() {
            public void notify(ChannelListener listener) {
                listener.onConnect(event2);
            }
        });
    }
    //------------------------------------------------------------------

    /**
     * Get the channel's server, which may be null.
     */
    public Server getServer() {
        return _server;
    }
    //------------------------------------------------------------------

    /**
     * Set the server to be used by this channel.
     */
    public void setServer(Server server) {
        _server = server;
    }
    //------------------------------------------------------------------

    /**
     * Get channel's IRCConnectionListener, for internal use only.
     */
    IRCConnectionListener getChannelMux() {
        if (_mux == null) {
            _mux = new _ChannelMux();
        }
        return _mux;
    }
    //---------------------------------------------------------------

    /**
     * Connect/join this channel, does nothing if channel has no server.
     */
    public void connect() {
        if (_server != null) {
            _server.sendJoin(this);
        }
    }
    //------------------------------------------------------------------

    /**
     * Request channel disconnection by sending PART command to server.
     */
    public void disconnect() {
        if (_server != null) {
            _server.sendPart(this);
        }
    }
    //------------------------------------------------------------------

    /**
     * Get name of channel (e.g. "#java" or "#raleigh"
     */
    public String getName() {
        return _name;
    }
    //------------------------------------------------------------------

    /**
     * Set channel name, with property change support. Property
     * change event will include new and old values.
     */
    public void setName(String name) {
        String old = _name;
        _name = name;
        _propChangeSupport.firePropertyChange("Name", old, _name);
    }
    //------------------------------------------------------------------

    /**
     * Get channe's topic.
     */
    public String getTopic() {
        return _topic;
    }
    //------------------------------------------------------------------

    /**
     * Set channel topic, with property change support. Property
     * change event will include new and old values.
     */
    public void setTopic(String topic) {
        String old = _topic;
        _topic = topic;
        _propChangeSupport.firePropertyChange("Topic", old, _topic);
    }

    //---------------------------------------------------------------
    public String getDescription() {
        return _desc;
    }

    public void setDescription(String desc) {
        _desc = desc;
    }
    //------------------------------------------------------------------

    /**
     * Number of users currently on channel.
     */
    public int getUserCount() {
        return _userCount;
    }
    //------------------------------------------------------------------

    /**
     * String representation is channel name.
     */
    public String toString() {
        return _name;
    }
    //------------------------------------------------------------------

    /**
     * Request that this channel be activated, given-focus or
     * brought-to-front.
     */
    public void activate() {
        final ChannelEvent event = new ChannelEvent(this);
        notifyListeners(new _ChannelEventNotifier() {
            public void notify(ChannelListener listener) {
                listener.onActivation(event);
            }
        });
    }
    //------------------------------------------------------------------

    //-------------------------------------------------------------------
    @Serial
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {

        try {
            in.defaultReadObject();
        } catch (NotActiveException e) {
            e.printStackTrace();
        }
        _propChangeSupport = new PropertyChangeSupport(this);
        _listeners = new Vector<>();
    }

    /**
     * Send a NOTICE to this channel.
     */
    public void sendNotice(String msg) {
        _server.sendCommand("NOTICE " + _name + " :" + msg);
    }
    //------------------------------------------------------------------

    /**
     * Send an action to this channel.
     */
    public void sendAction(String msg) {
        sendPrivMsg("\001ACTION " + msg + "\001");
    }
    //------------------------------------------------------------------

    /**
     * Sends any unsent messages to the channel.
     * Also handles any disconnections that might happen while sending
     * these messages.
     */
    private void sendAnyUnsentMessages() {
        if (_unsentMessagesBuffer == null)
            return;

        // first send any unsent messages if there are any
        int len = _unsentMessagesBuffer.size();
        if (len > 0) {
            StringBuilder sb = new StringBuilder(100);
            sb.append("<Connection dropped");
            if (isConnected()) {
                if (_numDroppedMessages > 0)
                    sb.append("; number of dropped messages: ").append(_numDroppedMessages);

                sb.append('>');
                sendPrivMsgNoBuffer(sb.toString());

                if (isConnected()) {
                    _numDroppedMessages = 0;

                    for (int i = 0; i < len; i++) {
                        sendPrivMsgNoBuffer(
                                (String) _unsentMessagesBuffer.firstElement());

                        if (!isConnected())
                            break;

                        _unsentMessagesBuffer.removeElementAt(0);
                    }
                }
            }
        }
    }
    //------------------------------------------------------------------

    /**
     * Ban a user from this channel.
     */
    public void sendBan(String nick) {
        _server.sendCommand("MODE " + _name + " +b " + nick);
    }
    //------------------------------------------------------------------

    /**
     * Take operator rights from a user.
     */
    public void sendDeop(String nick) {
        _server.sendCommand("MODE " + _name + " -o " + nick);
    }
    //------------------------------------------------------------------

    /**
     * Join this channel, requires a server. Before you call
     * this method, make sure you have set the server for this
     * channel.
     */
    public synchronized void sendJoin() {
        if (!_isConnected) {
            _server.sendJoin(this);
        }
    }
    //------------------------------------------------------------------

    /**
     * Kick a user from the channel.
     */
    public void sendKick(String nick) {
        _server.sendCommand("KICK " + _name + " " + nick);
    }
    //------------------------------------------------------------------

    /**
     * Send a one line or multiline message to this channel.
     * Every line to be sent must end in a newline character.
     * Lines not ending in a newline character are buffered until
     * a newline is received.
     */
    // MGENT FIX (bug #211498: Request for a multiline sendPrivMsg)
    public void sendMessage(String message) {

        // multiline message, so send as several separate messages
        StringTokenizer lines = new StringTokenizer(message, "\n", true);

        while (lines.hasMoreTokens()) {

            String token = lines.nextToken();

            if (token.equals("\n")) {
                sendPrivMsg(_messageLineBuffer.toString());
                _messageLineBuffer.setLength(0);
            } else {
                _messageLineBuffer.append(token);
            }

        }
    }

    //------------------------------------------------------------------

    /**
     * Give operator rights to a user.
     */
    public void sendOp(String nick) {
        _server.sendCommand("MODE " + _name + " +o " + nick);
    }
    //------------------------------------------------------------------

    /**
     * Part (leave) this channel.
     */
    public void sendPart() {
        _server.sendPart(_name);
    }
    //------------------------------------------------------------------

    /**
     * Send PRIVMSG to this channel.
     */
    public void sendPrivMsg(String str) {
        if (isConnected())
            _server.sendPrivateMessage(_name, str);

        if (!isConnected() && _maxNumBufferedUnsentMessages != 0) {
            // assume the line went down before the message was sent,
            // so add it to the unsent message buffer
            if (_unsentMessagesBuffer == null) {
                // give it a bit of slack
                _unsentMessagesBuffer =
                        new Vector(_maxNumBufferedUnsentMessages + 100);
            }

            _unsentMessagesBuffer.addElement(str);
            if (_maxNumBufferedUnsentMessages > 0) {
                // should only execute once when needed, but I made
                // it a while loop just in case.
                while (_unsentMessagesBuffer.size() >
                        _maxNumBufferedUnsentMessages) {
                    // if the vector is overflowing, then remove the oldest lines
                    _unsentMessagesBuffer.removeElementAt(0);
                    _numDroppedMessages++;
                }
            }
        }
    }
    //------------------------------------------------------------------

    /**
     * Send PRIVMSG to this channel.
     */
    private void sendPrivMsgNoBuffer(String str) {
        _server.sendCommand("PRIVMSG " + _name + " " + ":" + str);
    }
    //------------------------------------------------------------------

    /**
     * Insert the method's description here.
     * Creation date: (14/08/00 14:30:58)
     *
     * @param newMaxNumBufferedUnsentMessages i
     *                                        int  0 is don't buffer, -1 is umlimited size, n is number of lines
     *                                        buffered
     */
    public void setMaxNumBufferedUnsentMessages(
            int newMaxNumBufferedUnsentMessages) {
        _maxNumBufferedUnsentMessages = newMaxNumBufferedUnsentMessages;
    }
    //------------------------------------------------------------------

    //------------------------------------------------------------------
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (_name == null ? 0 : _name.hashCode());
        hash = 31 * hash + (_topic == null ? 0 : _topic.hashCode());
        hash = 31 * hash + (_server == null ? 0 : _server.hashCode());
        return hash;
    }

    //////////////////////////////////////////////////////////////////////

    //==================================================================
    //
    // Thread safe notification architecture
    //
    interface _ChannelEventNotifier {
        void notify(ChannelListener listener);
    }

    private class _ChannelMux extends IRCConnectionAdapter {

        //------------------------------------------------------------------
        public void onAction(String user, String channel, String txt) {

            final ChannelEvent event = new ChannelEvent(Channel.this, user, "", txt);
            notifyListeners(new _ChannelEventNotifier() {
                public void notify(ChannelListener listener) {
                    listener.onAction(event);
                }
            });
        }

        //------------------------------------------------------------------
        public void onBan(String banned, String chan, String banner) {

            final ChannelEvent event =
                    new ChannelEvent(Channel.this, banner, "", banned, "", "");

            notifyListeners(listener -> listener.onBan(event));

            // Was it something I said?
            if (banned.equals(_server.getNick())) {
                _server.fireStatusEvent("\nYou were banned from " + _name + "\n");
                disconnect();
            }
        }
        //------------------------------------------------------------------

        /**
         * Called when channel is parted.
         */
        public void onDisconnect() {

            Debug.println("Channel.onDisconnect(): listeners = "
                    + _listeners.size());

            setConnected(false);
        }

        //------------------------------------------------------------------
        public void onJoin(String user, String nick, String chan, boolean create) {

            // Were we the user that joined?
            if (nick.equals(_server.getNick())) {

                // Yes, so this is not a join, it is really a connect.
                _server.fireStatusEvent(nick + " joined channel " + _name + "\n");

                // Notify listeners that we've joined/connected-to this channel
                setConnected(true);
                sendAnyUnsentMessages();
            } else { // No, somebody else joined

                // Notify listeners that somebody else joined the channel
                final ChannelEvent event =
                        new ChannelEvent(Channel.this, nick, user, "");
                notifyListeners(listener -> listener.onJoin(event));
            }
        }

        //------------------------------------------------------------------
        public void onJoins(String users, String chans) {

            final ChannelEvent event = new ChannelEvent(Channel.this, users);
            notifyListeners(listener -> listener.onJoins(event));
        }

        //------------------------------------------------------------------
        public void onKick(String kicked, String chan, String kicker, String txt) {
            // Notify listeners
            final ChannelEvent event =
                    new ChannelEvent(Channel.this, kicker, "", kicked, "", txt);
            notifyListeners(listener -> listener.onKick(event));

            // Were we the user that was kicked?
            if (kicked.equals(_server.getNick())) {

                // Yes, send out a status message
                String reason = (String) event.getValue();
                _server.fireStatusEvent(
                        "\nYou were kicked from " + _name + "(" + reason + ")\n");

                // Notify listeners that we've parted/disconnected this channel
                setConnected(false);
            }
        }

        //------------------------------------------------------------------
        public void onTopic(String channel, String txt) {
            setTopic(txt);
        }

        //------------------------------------------------------------------
        public void onMessage(String txt) {
        }

        //------------------------------------------------------------------
        public void onPrivateMessage(String orgnick, String chan, String txt) {

            final ChannelEvent event =
                    new ChannelEvent(Channel.this, orgnick, "", txt);

            notifyListeners(new _ChannelEventNotifier() {
                public void notify(ChannelListener listener) {
                    listener.onMessage(event);
                }
            });
        }

        //------------------------------------------------------------------
        public void onNick(String user, String oldnick, String newnick) {

            final ChannelEvent event =
                    new ChannelEvent(Channel.this, oldnick, user, newnick);

            notifyListeners(new _ChannelEventNotifier() {
                public void notify(ChannelListener listener) {
                    listener.onNick(event);
                }
            });
        }

        //------------------------------------------------------------------
        public void onPart(String user, String nick, String chan) {
            final ChannelEvent event = new ChannelEvent(Channel.this, nick, user, "");

            // Notify listeners
            notifyListeners(new _ChannelEventNotifier() {
                public void notify(ChannelListener listener) {
                    listener.onPart(event);
                }
            });

            // Did we part the channel?
            if (nick.equals(_server.getNick())) {

                // Yes, send out a status message
                _server.fireStatusEvent(nick + " parted channel " + _name + ")\n");

                // Notify listeners that we've parted/disconnected this channel
                setConnected(false);
            }
        }

        //------------------------------------------------------------------
        public void onOp(String oper, String chan, String oped) {

            final ChannelEvent event =
                    new ChannelEvent(Channel.this, oper, "", oped, "", "");

            notifyListeners(new _ChannelEventNotifier() {
                public void notify(ChannelListener listener) {
                    listener.onOp(event);
                }
            });
        }

        //------------------------------------------------------------------
        public void onQuit(String user, String nick, String txt) {

            final ChannelEvent event = new ChannelEvent(Channel.this, nick, "", txt);
            notifyListeners(new _ChannelEventNotifier() {
                public void notify(ChannelListener listener) {
                    listener.onQuit(event);
                }
            });
        }
    }

}

