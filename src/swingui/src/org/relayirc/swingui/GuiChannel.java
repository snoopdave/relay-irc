//-----------------------------------------------------------------------------
// $RCSfile: GuiChannel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/03/29 04:58:15 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;

/**
 * A GuiObject that represents a channel.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.2 $
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
public class GuiChannel extends GuiObject {

   private Channel _channel = null;
   private Hashtable _actions = null;
   private Object[][] _actionArray = {
     //
     // Action Class                 Icon Name   Command Name
     // ------------                 ---------   ------------
      { _JoinAction.class,           "ReplyAll", JOIN },
      { _PartAction.class,           "Delete",   PART },
      { _AddChannelAction.class,     "Favorite", ADD_CHANNEL },
      { _RemoveChannelAction.class,  "Delete",   REMOVE_CHANNEL },
      { _ShowPropertiesAction.class, "Help",     SHOW_PROPERTIES },
   };

   /** Action name for the JOIN action. If you put a property
    * named Channel with a Channel object as its value to this
    * Action, then the action will connect to the specified server.
    * Otherwise, it will pop up the Join Dialog (JoinDlg). */
   public static final String JOIN = "Join";

   /** Action name for the PART action. */
   public static final String PART = "Part";

   /** Action name for the SHOW_PROPERTIES action. */
   public static final String SHOW_PROPERTIES = "ShowProperties";

   /** Action name for the ADD_CHANNEL action. */
   public static final String ADD_CHANNEL = "AddChannel";

   /** Action name for the REMOVE_CHANNEL action. Requires
     * a property named Channel with a Channel as its value. */
   public static final String REMOVE_CHANNEL = "RemoveChannel";

   //-----------------------------------------------------------------
   public GuiChannel(Channel chan) {
      super(chan.getName(),chan);
      _channel = chan;
   }
   //-----------------------------------------------------------------
   public GuiChannel(Channel chan, Icon icon) {
      super(chan.getName(),chan,icon);
      _channel = chan;
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

      Server svr = ChatApp.getChatApp().getServer();

      // If server is connected but channel is not then show Join option
      if ( svr != null && svr.isConnected() ) {

         // Server is connected, now check channel 
		 Channel channel = svr.getChannel(_channel.getName(),false);
		 if (channel == null || !channel.isConnected()) {

            // Channel is not connected so show Join option
            Action joinAction = getAction( JOIN ).getActionObject();
            popup.add(joinAction);
		 }
		 else {
			 // Channel is connected, so show Part option
			 _channel = channel;
             Action partAction = getAction( PART ).getActionObject();
             popup.add(partAction);
		 }
      }

      // Always show Properties option
      Action propAction = getAction( SHOW_PROPERTIES ).getActionObject();
      popup.add(propAction);

      // Always show a Remove option
      Action remAction = getAction( REMOVE_CHANNEL ).getActionObject();
      popup.add(remAction);

      // Add custom menu items

      opts.getCustomChannelActions().addActionsToMenu(popup,_channel);

      return popup;
   }
   ////////////////////////////////////////////////////////////////////////
   private class _ShowPropertiesAction  extends ChatAction {

      public _ShowPropertiesAction(Icon icon) {
         super("Properties...",icon);
      }
      public void actionPerformed(ActionEvent e) {
         new PropDlg(ChatApp.getChatApp(),"Channel",_channel);
      }
   }
   ////////////////////////////////////////////////////////////////////////
   private class _JoinAction extends ChatAction {
      public _JoinAction(Icon icon) {
         super("Join...",icon);
      }
      public void actionPerformed(ActionEvent e) {
         ChatApp.getChatApp().getServer().sendJoin(_channel.getName());
      }
      public void update() {
         if (_channel.isConnected()) {
            setEnabled(true);
         }
         else {
            setEnabled(false);
         }
      }
   }
   ////////////////////////////////////////////////////////////////////////
   private class _PartAction extends ChatAction {
     public _PartAction(Icon icon) {
         super("Part",icon);
      }
      public void actionPerformed(ActionEvent e) {
         _channel.disconnect();
      }
      public void update() {
         if (_channel.isConnected()) {
            setEnabled(true);
         }
         else {
            setEnabled(false);
         }
      }
   }
   ////////////////////////////////////////////////////////////////////////
   private class _AddChannelAction extends ChatAction {

      public _AddChannelAction(Icon icon) {
         super("Add to Favorites",icon);
      }
      public void actionPerformed(ActionEvent e) {
         ChatApp.getChatApp().getOptions().getFavoriteChannels().addChannel(_channel);
      }
   }
   ////////////////////////////////////////////////////////////////////////
   private class _RemoveChannelAction extends ChatAction {

      public _RemoveChannelAction(Icon icon) {
         super("Remove Channel",icon);
      }
      public void actionPerformed(ActionEvent e) {

         int ret = JOptionPane.showConfirmDialog(ChatApp.getChatApp(),
            "Are you sure you want to remove channel "+_channel.getName());

         if (ret==JOptionPane.YES_OPTION || ret==JOptionPane.OK_OPTION) {
             ChatApp.getChatApp().getOptions().getFavoriteChannels().removeChannel(_channel);
         }
      }
   }
}

