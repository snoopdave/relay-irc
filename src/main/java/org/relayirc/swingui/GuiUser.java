//-----------------------------------------------------------------------------
// $RCSfile: GuiUser.java,v $
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

////////////////////////////////////////////////////////////////////////////
/**
 * A GuiObject that represents a user: creates user actions and user popups.
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
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class GuiUser extends GuiObject {

   private User _user = null;
   private Channel _channel = null;
   private Hashtable _actions = null;
   private Object[][] _actionArray = {
      //
      // Action Class                Icon Name    Command Name
      // ------------                ---------    ------------
      { _WhoIsAction.class,          "Help",      WHOIS },
      { _ShowPropertiesAction.class, "Help",      SHOW_PROPERTIES },
      { _AddUserAction.class,        "Favorite",  ADD_USER },
      { _RemoveUserAction.class,     "Delete",    REMOVE_USER },
      { _OpUserAction.class,         "GreenFlag", OP_USER },
      { _DeopUserAction.class,       "GreenFlag", DEOP_USER },
      { _KickUserAction.class,       "GreenFlag", KICK_USER },
      { _BanUserAction.class,        "GreenFlag", BAN_USER },
      { _VersionUserAction.class,    "GreenFlag", VERSION_USER }
   };

   /** Action name for the WHOIS action. */
   public static final String WHOIS = "WhoIs";

   /** Action name for the SHOW_PROPERTIES action. This action
    * will pop-up the Propeties Dialog (PropDlg). Requires a
    * property named ChatObject whose value is an IChatObject. */
   public static final String SHOW_PROPERTIES = "Properties";

   /** Action name for the ADD_USER action. */
   public static final String ADD_USER = "AddUser";

   /** Action name for the REMOVE_USER action. Requires
     * a property named User with a User as its value. */
   public static final String REMOVE_USER = "RemoveUser";

   /** Action name for VERSION_USER action. */
   public static final String VERSION_USER = "VersionUser";

   /** Action name for OP_USER action. */
   public static final String OP_USER = "OpUser";

   /** Action name for DEOP_USER action. */
   public static final String DEOP_USER = "DeopUser";

   /** Action name for KICK_USER action. */
   public static final String KICK_USER = "KickUser";

   /** Action name for BAN_USER action. */
   public static final String BAN_USER = "BanUser";

   //-----------------------------------------------------------------
   public GuiUser(User user) {
      super(user.getName(),user,IconManager.getIcon("User"));
      _user = user;
   }
   //-----------------------------------------------------------------
   public GuiUser(User user, Channel chan) {
      super(user.getName(),user,IconManager.getIcon("User"));
      _user = user;
      _channel = chan;
   }
   //-----------------------------------------------------------------
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

      // If connected, show a Who Is... option
      if (ChatApp.getChatApp().isConnected()) {
         Action whoIsAction = getAction( WHOIS).getActionObject();
         popup.add(whoIsAction);
         Action versionAction = getAction( VERSION_USER).getActionObject();
         popup.add(versionAction);
      }
      // Otherwise, show a Properties... optiion
      else {
         Action propAction = getAction( SHOW_PROPERTIES).getActionObject();
         popup.add(propAction);
      }

      // If we are in a valid channel, then show channel actions
      if (_channel != null) {
         popup.add( getAction( VERSION_USER ).getActionObject());
         popup.add( getAction( OP_USER ).getActionObject());
         popup.add( getAction( DEOP_USER ).getActionObject());
         popup.add( getAction( KICK_USER ).getActionObject());
         popup.add( getAction( BAN_USER ).getActionObject());
      }

      // Menu always includes remove option
      Action faveAction = null;
      if (opts.getFavoriteUsers().isFavorite(_user)) {
         faveAction = getAction( REMOVE_USER).getActionObject();
      }
      else {
         faveAction = getAction( ADD_USER).getActionObject();
      }
      popup.add(faveAction);

      // Add custom menu items

      opts.getCustomUserActions().addActionsToMenu(popup,_user);

      return popup;
   }
   ////////////////////////////////////////////////////////////////////
   private class _WhoIsAction extends ChatAction {

      public _WhoIsAction(Icon icon) {
         super("WHOIS",icon);
      }
      public void actionPerformed(ActionEvent ae) {
         ChatApp.getChatApp().sendWhoIs(_user,true);
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
   ///////////////////////////////////////////////////////////////////
   private class _ShowPropertiesAction  extends ChatAction {

      public _ShowPropertiesAction(Icon icon) {
         super("Properties...",icon);
      }
      public void actionPerformed(ActionEvent e) {
         new PropDlg(ChatApp.getChatApp(),"User",_user);
      }
   }
   ///////////////////////////////////////////////////////////////////
   private class _AddUserAction extends ChatAction {

      public _AddUserAction(Icon icon) {
         super("Add to Favorites",icon);
      }
      public void actionPerformed(ActionEvent e) {
         ChatApp.getChatApp().getOptions().getFavoriteUsers().addUser(_user);
      }
   }
   ///////////////////////////////////////////////////////////////////
   private class _RemoveUserAction extends ChatAction {

      public _RemoveUserAction(Icon icon) {
         super("Remove from favorites",icon);
      }
      public void actionPerformed(ActionEvent e) {

         int ret = JOptionPane.showConfirmDialog(ChatApp.getChatApp(),
            "Are you sure you want to remove user "+_user.getName());

         if (ret==JOptionPane.YES_OPTION || ret==JOptionPane.OK_OPTION) {
            ChatApp.getChatApp().getOptions().getFavoriteUsers().removeUser(_user);
         }
      }
   }
   ///////////////////////////////////////////////////////////////////
   private class _OpUserAction extends ChatAction {

      public _OpUserAction(Icon icon) {
         super("Op",icon);
      }
      public void actionPerformed(ActionEvent e) {
         _channel.sendOp(_user.getNick());
      }
   }
   ///////////////////////////////////////////////////////////////////
   private class _DeopUserAction extends ChatAction {

      public _DeopUserAction(Icon icon) {
         super("De-Op",icon);
      }
      public void actionPerformed(ActionEvent e) {
         _channel.sendDeop(_user.getNick());
      }
   }
   ///////////////////////////////////////////////////////////////////
   private class _KickUserAction extends ChatAction {

      public _KickUserAction(Icon icon) {
         super("Kick",icon);
      }
      public void actionPerformed(ActionEvent e) {
         _channel.sendKick(_user.getNick());
      }
   }
   ///////////////////////////////////////////////////////////////////
   private class _BanUserAction extends ChatAction {

      public _BanUserAction(Icon icon) {
         super("Ban",icon);
      }
      public void actionPerformed(ActionEvent e) {
         _channel.sendBan(_user.getNick());
      }
   }
   ///////////////////////////////////////////////////////////////////
   private class _VersionUserAction extends ChatAction {

      public _VersionUserAction(Icon icon) {
         super("Version",icon);
      }
      public void actionPerformed(ActionEvent e) {
         ChatApp.getChatApp().getServer().sendVersion(_user.getNick());
      }
   }
}

