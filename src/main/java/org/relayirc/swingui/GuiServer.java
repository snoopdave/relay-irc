//-----------------------------------------------------------------------------
// $RCSfile: GuiServer.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;

/**
 * A GuiObject that represents a server.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code:     Relay-JFC Chat Client <br>
 * Initial Developer:  David M. Johnson <br>
 * Contributor(s):     No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class GuiServer extends GuiObject {

   private Server _server = null;
   private Hashtable _actions = null;
   private Object[][] _actionArray = {
     //
     // Action Class                Icon Name      Command Name
     // ------------                ---------      ------------
     { _ConnectAction.class,        "Plug",        CONNECT },
     { _DisconnectAction.class,     "UnPlug",      DISCONNECT },
     { _AddServerAction.class,      "Favorite",    ADD_SERVER },
     { _RemoveServerAction.class,   "Delete",      REMOVE_SERVER },
     { _ShowPropertiesAction.class, "Help",        SHOW_PROPERTIES }
   };

   /**
    * Action name for CONNECT action. If you put a property
    * named "Server" with a Server object as its value to this
    * Action, then the action will connect to the specified server.
    * Otherwise, it will pop up a dialog. */
   public static final String CONNECT = "Connect";

   /** Action name for the DISCONNECT action. This action will
    * trigger a disconnect from the current server. */
   public static final String DISCONNECT = "Disconnect";

   /** Action name for the ADD_SERVER action. */
   public static final String ADD_SERVER = "AddServer";

   /** Action name for the REMOVE_SERVER action. Requires
     * a property named Server with a Server as its value. */
   public static final String REMOVE_SERVER = "RemoveServer";

   /** Action name for the SHOW_PROPERTIES action. This action
    * will pop-up the Propeties Dialog (PropDlg). Requires a
    * property named ChatObject whose value is an IChatObject. */
   public static final String SHOW_PROPERTIES = "Properties";

   //-----------------------------------------------------------------
   public GuiServer(Server srvr) {
      super(srvr.getName(),srvr);
      _server = srvr;
   }
   //-----------------------------------------------------------------
   public GuiServer(Server srvr, Icon icon) {
      super(srvr.getName(),srvr,icon);
      _server = srvr;
   }
   //------------------------------------------------------------------
   /** Get action by name. */
   public IChatAction getAction(String actionName) {
      if (_actions == null) {
         _actions = ChatApp.initActions(_actionArray,this);
      }
      return (IChatAction)_actions.get(actionName);
   }
   //-----------------------------------------------------------------
   public JPopupMenu createPopupMenu() {
      JPopupMenu popup = new JPopupMenu();
      ChatOptions opts = ChatApp.getChatApp().getOptions();

      Server eng = ChatApp.getChatApp().getServer();
      if (eng!=null && eng.isConnected()) {
         if (opts.getCurrentServer() == _server) {

            // ChatApp is connected, so show DISCONNECT menu item
            JMenuItem disconItem = popup.add(getAction(
               DISCONNECT ).getActionObject());
         }
      }
      else {
         // CONNECT
         Action action = getAction( CONNECT ).getActionObject();
         action.putValue("Server",_server);
         popup.add(action);
      }

      // SHOW_PROPERTIES
      Action propAction = getAction( SHOW_PROPERTIES ).getActionObject();
      propAction.putValue("Server",_server);
      JMenuItem propItem = popup.add(propAction);

      // REMOVE_SERVER
      Action remAction = getAction( REMOVE_SERVER ).getActionObject();
      remAction.putValue("Server",_server);
      popup.add(remAction);

      // Add custom menu items
      opts.getCustomServerActions().addActionsToMenu(popup,_server);

      return popup;
   }
   ////////////////////////////////////////////////////////////////////////
   private class _ConnectAction extends ChatAction {

      public _ConnectAction(Icon icon) {
         super("Connect",icon);
      }
      public void actionPerformed(ActionEvent e) {
         ChatApp.getChatApp().connect(_server);
      }
      public void update() {
         if (ChatApp.getChatApp().isConnected()) {
            setEnabled(false);
         }
         else {
            setEnabled(true);
         }
      }
   }
   ////////////////////////////////////////////////////////////////////////
   private class _DisconnectAction extends ChatAction {

      public _DisconnectAction(Icon icon) {
         super("Disconnect",icon);
      }
      public void actionPerformed(ActionEvent e) {
        int ret = JOptionPane.showConfirmDialog(ChatApp.getChatApp(),
           "Are you sure you want to disconnect?");

        if (ret==JOptionPane.YES_OPTION || ret==JOptionPane.OK_OPTION) {
           ChatApp.getChatApp().getServer().disconnect();
        }
      }
      public void update() {
         if (ChatApp.getChatApp().isConnected()) {
            setEnabled(true);
         }
         else {
            setEnabled(false);
         }
      }
   }
   ////////////////////////////////////////////////////////////////////////
   private class _ShowPropertiesAction  extends ChatAction {

      public _ShowPropertiesAction(Icon icon) {
         super("Properties...",icon);
      }
      public void actionPerformed(ActionEvent e) {
         new PropDlg(ChatApp.getChatApp(),"Server",_server);
      }
   }
   ////////////////////////////////////////////////////////////////////////
   private class _AddServerAction extends ChatAction {

      public _AddServerAction(Icon icon) {
         super("Add to Favorites",icon);
      }
      public void actionPerformed(ActionEvent e) {
         _server.setFavorite(true);
      }
   }
   ////////////////////////////////////////////////////////////////////////
   private class _RemoveServerAction extends ChatAction {

      public _RemoveServerAction(Icon icon) {
         super("Remove Server",icon);
      }
      public void actionPerformed(ActionEvent e) {

         int ret = JOptionPane.showConfirmDialog(ChatApp.getChatApp(),
            "Are you sure you want to remove server "+_server.getName());

         if (ret==JOptionPane.YES_OPTION || ret==JOptionPane.OK_OPTION) {
            _server.setFavorite(false);
         }
      }
   }
}

