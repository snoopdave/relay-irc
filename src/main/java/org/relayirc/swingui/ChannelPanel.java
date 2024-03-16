//-----------------------------------------------------------------------------
// $RCSfile: ChannelPanel.java,v $
// $Revision: 1.1.2.4 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;
import org.relayirc.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

///////////////////////////////////////////////////////////////////////

/**
 * A three-panel GUI for one IRC chat channel. The main panel shows messages,
 * joins, parts, bans, kicks and etc. occuring in the channel. The bottom
 * panel is a text input field where the user may enter messages/commands
 * for the channel. And, the right panel is a list of the users currently
 * in the chat channel with channel operators shown first.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.4 $
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
public class ChannelPanel extends ChatPanel 
   implements ChannelListener, MDIClientPanel {

   private final UserList _userList;
   private int      _initialized=3; // kludge to force a resize
   private String   _dockState = MDIPanel.DOCK_NONE;

   //------------------------------------------------------------------
   public ChannelPanel(Channel chan, Container container) {

      super(chan,container);

      // Add list of users in east part of layout
      _userList = new UserList(chan);
      _userList.setBackground(Color.white);
      JScrollPane scrollPane = new JScrollPane(_userList,
         ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
      add(scrollPane,"East");

      validate();

      // Listen to the channel
      chan.addChannelListener(this);
   }

   //------------------------------------------------------------------
   /** Channel has been activated, so bring window to front. */
   public void onActivation(ChannelEvent event) {
      if (_container instanceof JInternalFrame) {
         ((JInternalFrame)_container).toFront();
      }
   }

   //------------------------------------------------------------------
   /** Display user action. */
   public void onAction(ChannelEvent event) {
      println("* "+event.getSubjectNick()+" "+event.getValue(),
         _options.getProperty("gui.channel.color.actions"));
   }

   //------------------------------------------------------------------
   /** Display userA-was-banned-by-operatorB message. */
   public void onBan(ChannelEvent event) {
      println(event.getSubjectNick()+" was banned by "+event.getOriginNick(),
         _options.getProperty("gui.channel.color.bans"));
      _userList.remove(event.getSubjectNick());
   }

   //------------------------------------------------------------------
   /** Ignore our own join/connect notification. */
   public void onConnect(ChannelEvent event) {
   }
   //------------------------------------------------------------------
   /** Upon part/disconnect, remove self from chat app and commit guicide. */
   public void onDisconnect(ChannelEvent event) {
      Debug.println("ChannelPanel.onDisconnect("+event+")\n");
      ChatApp.getChatApp().removeChatPanel(this);
      _channel.removeChannelListener(this);
   }
   //------------------------------------------------------------------
   public void onJoin(ChannelEvent event) {
      forceLayout();
      println(event.getOriginNick()+" has joined "+_channel.getName(),
         _options.getProperty("gui.channel.color.joins"));
      _userList.addItem(event.getOriginNick());
   }

   //------------------------------------------------------------------
   public void onJoins(ChannelEvent event) {
      Debug.println(
         "ChatChannelView: users joined - "+ event.getValue() +"\n");
      forceLayout();
      StringTokenizer toker = new StringTokenizer((String)event.getValue());
      try {
         String user;
         while ( (user=toker.nextToken()) != null ) {
            _userList.addItem(user);
         }
      }
      catch ( NoSuchElementException e ) {
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
   }

   //------------------------------------------------------------------
   public void onKick(ChannelEvent event) {
      println(event.getSubjectNick()+" was kicked by "+event.getOriginNick(),
         _options.getProperty("gui.channel.color.kicks"));
      _userList.remove(event.getSubjectNick());
   }

   //------------------------------------------------------------------
   public void onMessage(ChannelEvent event) {
      print(event.getOriginNick()+"> ",
         "Bold-"+_options.getProperty("gui.channel.color.messages"));
      println((String)event.getValue(),
         _options.getProperty("gui.channel.color.messages"));
   }

   //------------------------------------------------------------------
   public void onNick(ChannelEvent event) {
      if (_userList.contains(event.getOriginNick())) {
         println(event.getOriginNick()
            +" is now known as "+ event.getValue(),
            _options.getProperty("gui.channel.color.nicks"));
         _userList.remove(event.getOriginNick());
         _userList.addItem((String)event.getValue());
      }
   }

   //------------------------------------------------------------------
   public void onDeOp(ChannelEvent event) {

      if ( ((DefaultListModel)_userList.getModel()).contains(
         event.getSubjectNick()) ) {

         println(event.getOriginNick()
            +" took operator rights from "+event.getSubjectNick(),
            _options.getProperty("gui.channel.color.ops"));

         // Remove user with op rights from user list
         _userList.remove("@"+event.getSubjectNick());

         // Add user without op rights from user list
         _userList.addItem(event.getSubjectNick());
      }
   }

   //------------------------------------------------------------------
   public void onOp(ChannelEvent event) {

      if ( ((DefaultListModel)_userList.getModel()).contains(
         event.getSubjectNick()) ) {

         println(event.getOriginNick()
            +" give operator rights to "+event.getSubjectNick(),
            _options.getProperty("gui.channel.color.ops"));

         // Remove user without op rights from user list
         _userList.remove(event.getSubjectNick());

         // Add user with op rights from user list
         _userList.addItem("@"+event.getSubjectNick());
      }
   }

   //------------------------------------------------------------------
   public void onPart(ChannelEvent event) {

      println(event.getOriginNick()+" has left "+_channel,
         _options.getProperty("gui.channel.color.parts"));

      _userList.remove(event.getOriginNick());
   }

   //------------------------------------------------------------------
   public void onQuit(ChannelEvent event) {

      println(event.getOriginNick()+" has quit IRC ("
         + event.getValue() +")",
         _options.getProperty("gui.channel.color.parts"));

      _userList.remove(event.getOriginNick());
   }

   //------------------------------------------------------------------
   public void onTopicChange(ChannelEvent event) {
      Debug.println("Need to set title to "
         +_channel+" - "+ event.getValue());
      //setTitle(_channel+" - "+topic);
   }

   //------------------------------------------------------------------
   /**
    * Kludge to force a layout. Added this because UserList is
    * initially sized too wide and corrects itself upon layout.
    */
   public void forceLayout() {
      if (_initialized-- > 0) {
         setSize(getSize().width+1,getSize().height+1);
         setSize(getSize().width+1,getSize().height+1);
         setVisible(true);
      }
   }
   //------------------------------------------------------------------
   public void part() {
      _channel.sendPart();
   }
   //------------------------------------------------------------------
   public String getDockState() {
      return _dockState;
   }
   //------------------------------------------------------------------
   public void setDockState(String dockState) {
      _dockState = dockState;
   }
   //------------------------------------------------------------------
   public JPanel getPanel() {
      return this;
   }
}

