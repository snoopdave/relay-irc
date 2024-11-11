//-----------------------------------------------------------------------------
// $RCSfile: CustomAction.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.python.util.PythonInterpreter;
import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.Server;
import org.relayirc.chatengine.User;
import org.relayirc.util.Debug;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.Serializable;

///////////////////////////////////////////////////////////////////////////////

/**
 * An IChatAction implementation that executes a JPython script, a Java
 * class that implements IRelayRunnable or an IRC command.
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
public class CustomAction implements IChatAction, Serializable {

    public static final int JPYTHON_SCRIPT = 0;
    public static final int JAVA_CLASS = 1;
    public static final int IRC_COMMAND = 2;
    static final long serialVersionUID = 5391640469485922480L;
    private int _type;   // See types above
    private String _title;  // Name to be shown in menu, lists, etc.
    private String _action; // Action to be performed
    private boolean _enabled = true;

    // Arbitrary data serves as "context" for action
    private transient Object _context = null;

    private transient IRelayRunnable _runnable = null;

    //--------------------------------------------------------------------------

    /**
     * Construct CustomAction by specifying its title, type and action string.
     * <br/>
     * If type is JPYTHON_SCRIPT, then action must be the full path
     * to a JPython script to be run.
     * <br/>
     * If type is JAVA_CLASS, then action must be the fully qualified Java
     * class name of a class that implements IRelayPlugin.
     * <br/>
     * If the type is IRC_COMMAND, then action must be an IRC command. The
     * command can contains a %1 to substitute the name of the channel,
     * server or user that is the context of the command.
     * <br/>
     *
     * @param title  Name to be displayed on menus, lists, etc.
     * @param type   Type of command, see static final int fields.
     * @param action Action to be performed (see above).
     */
    public CustomAction(String title, int type, String action) {
        _title = title;
        _type = type;
        _action = action;
    }
    //--------------------------------------------------------------------------
    // Simple accessors

    public String getTitle() {
        return _title;
    }

    public void setTitle(String t) {
        _title = t;
    }

    public String getAction() {
        return _action;
    }

    public void setAction(String a) {
        _action = a;
    }

    public int getType() {
        return _type;
    }

    public void setType(int t) {
        _type = t;
    }

    public Object getContext() {
        return _context;
    }

    public void setContext(Object context) {
        _context = context;
    }

    //----------------------------------------------------------------------

    /**
     * If action is an IRC command and we are disconnected, then disable
     * action, otherwise enable it.
     */
    public void update() {
        _enabled = ChatApp.getChatApp().isConnected() || _type != IRC_COMMAND;
    }
    //--------------------------------------------------------------------------

    /**
     * Get the actual action object.
     */
    public AbstractAction getActionObject() {
        _CustomAction action = new _CustomAction(_title);
        action.setEnabled(_enabled);
        return action;
    }

    //--------------------------------------------------------------------------

    /**
     * Send action's IRC command to the server. Context will be ignored if it
     * is null or if it is not a Server, Channel or User object. If the global
     * ChatApp is not connected, then this is a no op.
     *
     * @param context Object that provides context for command.
     */
    private void runIRCCommand(Object context) {
        if (!ChatApp.getChatApp().isConnected()) return;

        String command = _action;

        // Get context name, if there is a context
        String name = null;
        if (context == null) {
        } else if (context instanceof Server) {
            name = ((Server) context).getName();
        } else if (context instanceof Channel) {
            name = ((Channel) context).getName();
        } else if (context instanceof User) {
            name = ((User) context).getName();
        }

        // Replace %s, %c or %u with name of server, channel or user context.
        if (name != null) {
            int index = -1;
            String start = "";
            String end = "";
            if ((index = _action.indexOf("%s")) != -1) {
            } else if ((index = _action.indexOf("%c")) != -1) {
            } else if ((index = _action.indexOf("%u")) != -1) {
            }
            if (index != -1) {
                start = _action.substring(0, index);
                end = _action.substring(index + 2);
                command = start + name + end;
            }
        }

        // Send command to server
        ChatApp.getChatApp().getServer().sendCommand(command);
    }
    //--------------------------------------------------------------------------

    /**
     * Run the action's JPython script. Uses the global ChatApp's built-in
     * JPython interpreter. Because of this, action scripts share the same
     * name space as the Interactive JPython Console.
     */
    private void runJPythonScript(Object context) {
        PythonInterpreter python = ChatApp.getChatApp().getPythonInterpreter();
        try {
            python.set("context", context);
            python.execfile(_action);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ChatApp.getChatApp(),
                    "Exception [" + e + "] attempting to run JPython script [" + _action + "]",
                    "Error running custom action",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    //--------------------------------------------------------------------------

    /**
     * Run Java action.
     */
    private void runJavaClass(Object context) {

        if (_runnable == null) {
            try {
                Class runClass = Class.forName(_action);
                _runnable = (IRelayRunnable) runClass.newInstance();
            } catch (ClassNotFoundException nfe) {
                JOptionPane.showMessageDialog(ChatApp.getChatApp(),
                        "Unable to find class [" + _action + "] in class path",
                        "Error running custom action",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalAccessException iae) {
                JOptionPane.showMessageDialog(ChatApp.getChatApp(),
                        "Unable to access class [" + _action + "]",
                        "Error running custom action",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (InstantiationException ie) {
                JOptionPane.showMessageDialog(ChatApp.getChatApp(),
                        "Unable to instantiate object of class [" + _action + "]",
                        "Error running custom action",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ChatApp.getChatApp(),
                        "Exception [" + e + "] attempting to run Java class [" + _action + "]",
                        "Error running custom action",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if (_runnable != null) _runnable.run(context);
    }
    ////////////////////////////////////////////////////////////////////////////

    private class _CustomAction extends AbstractAction {

        public _CustomAction(String title) {
            super(title);
        }

        //----------------------------------------------------------------------
        public void actionPerformed(ActionEvent ae) {
            Debug.println("Running action [" + _action + "]");
            switch (_type) {
                case CustomAction.JPYTHON_SCRIPT: {
                    runJPythonScript(_context);
                    break;
                }
                case CustomAction.JAVA_CLASS: {
                    runJavaClass(_context);
                    break;
                }
                case CustomAction.IRC_COMMAND: {
                    runIRCCommand(_context);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

}

