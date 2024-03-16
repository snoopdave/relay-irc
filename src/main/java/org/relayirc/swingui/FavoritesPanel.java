//-----------------------------------------------------------------------------
// $RCSfile: FavoritesPanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.Server;
import org.relayirc.chatengine.User;
import org.relayirc.swingutil.IconManager;
import org.relayirc.swingutil.MDIClientPanel;
import org.relayirc.swingutil.MDIPanel;
import org.relayirc.util.Debug;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// FavoritesPanel extends JPanel
//   FavoritesTree extends JTree
//   |
//   +- FavoritesNode ext. DefaultMutableTreeNode, imp. FavoritesTreeNode
//      |
//      +- ServersFolderNode ext. DefaultMutableTreeNode, imp. FavoriteTreeNode
//      |  +- ServerNode
//      |  +- ServerNode
//      |  +- ...
//      |
//      +- ChannelsFolderNode
//      |  +- ChannelsNode
//      |  +- ChannelsNode
//      |  +- ...
//      |
//      +- UsersFolderNode
//         +- UsersNode
//         +- UsersNode
//         +- ...

/**
 * Panel that holds a Favorites tree of favorite servers, channels and users.
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
public class FavoritesPanel extends JPanel implements MDIClientPanel {
   private String  _dockState = MDIPanel.DOCK_LEFT;
   private final JTree   _tree;

   /** Row height of items in favorites tree. */
   public static final int ROW_HEIGHT = 20;

   //-----------------------------------------------------------------
   public FavoritesPanel(ChatApp app) {

      setLayout(new BorderLayout());
      setBorder(new BevelBorder(BevelBorder.LOWERED));

      _tree = new FavoritesTree(this);
      _tree.setRowHeight(ROW_HEIGHT);
      add(new JScrollPane(_tree),BorderLayout.CENTER);
   }
   public String getDockState() {return _dockState;}
   public void   setDockState(String dockState) {_dockState=dockState;}
   public JPanel getPanel() {return this;}
}

///////////////////////////////////////////////////////////////////////

interface FavoritesTreeNode {
   JPopupMenu createPopupMenu();
   void handleDoubleClick();
   void update();
}

///////////////////////////////////////////////////////////////////////

/** Tree with root that is a FavoritesNode. */
class FavoritesTree extends JTree {

   private final FavoritesNode _favoritesNode;
   private final FavoritesPanel _favoritesPanel;

   public static final String SERVERS_FOLDER  = "Servers";
   public static final String CHANNELS_FOLDER = "Channels";
   public static final String USERS_FOLDER    = "Users";

   //-----------------------------------------------------------------
   /** Construct favorites tree for a favorites panel.  */
   public FavoritesTree(FavoritesPanel favePanel) {

      _favoritesPanel = favePanel;
      _favoritesNode = new FavoritesNode();

      putClientProperty("JTree.lineStyle", "Angled");
      setShowsRootHandles(true);
      setCellRenderer(new FavoritesTreeCellRenderer());

      DefaultTreeModel model = new DefaultTreeModel(_favoritesNode);
      setModel(model);

      // Listen for pop-up menu clicks
      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent me) { showPopup(me); }
         public void mouseReleased(MouseEvent me) { showPopup(me); }
         public void mouseClicked(MouseEvent me) {

            // If user double-clicked on a tree node then...
            if (me.getClickCount() == 2) {

               DefaultMutableTreeNode treeNode = null;
               Point pt = me.getPoint();
               TreePath treePath = getPathForLocation(pt.x,pt.y);
               if (treePath!=null) {

                  int pathLength = treePath.getPath().length;
                  treeNode =
                      (DefaultMutableTreeNode)treePath.getPath()[pathLength-1];
                  setSelectionPath(treePath);

                  if (treeNode instanceof FavoritesTreeNode faveTreeNode) {

                      // ...ask tree node to handle it.
                     faveTreeNode.handleDoubleClick();
                  }
               }
            }
            else showPopup(me);
         }
      });

      // Update tree when chat options change in any way
      ChatApp.getChatApp().getOptions().addPropertyChangeListener(
         new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
               update();
            }
         }
      );
      update();
   }

   //-----------------------------------------------------------------
   public void showPopup(MouseEvent me) {

      if (me.isPopupTrigger()) {

         // If user right-clicked on a tree node...
         DefaultMutableTreeNode treeNode = null;
         Point pt = me.getPoint();
         TreePath treePath = getPathForLocation(pt.x,pt.y);
         if (treePath!=null) {

            int pathLength = treePath.getPath().length;
            treeNode = (DefaultMutableTreeNode)
               treePath.getPath()[pathLength-1];
            setSelectionPath(treePath);

            if (treeNode instanceof FavoritesTreeNode faveTreeNode) {

                // ...then present tree node's popup menu
               JPopupMenu popup = faveTreeNode.createPopupMenu();
               if (popup != null) {
                  add(popup);
                  popup.show(FavoritesTree.this,me.getX(),me.getY());
               }
            }
         }
         else {

            // User right-clicked on the background...
            Point pt2 = SwingUtilities.convertPoint(
               (Component)me.getSource(),
               new Point(me.getX(),me.getY()),_favoritesPanel);

            // ...show dock/undock popup.
            JPopupMenu popup = createDockMenu();
            popup.show(_favoritesPanel,pt2.x,pt2.y);
	      }
      }
   }
   //-----------------------------------------------------------------
   /** Create popup menu with Dock/Undock menu item. */
   public JPopupMenu createDockMenu() {

      JPopupMenu popup = new JPopupMenu();

      JMenuItem mi2 = new JMenuItem("Dock / Undock");
      mi2.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent ae) {

            // Set new dock state
            String dockState = _favoritesPanel.getDockState();
            if (dockState.equals(MDIPanel.DOCK_NONE))
               _favoritesPanel.setDockState(MDIPanel.DOCK_LEFT);
            else
               _favoritesPanel.setDockState(MDIPanel.DOCK_NONE);

            // and register it with the MDIPanel
            ChatApp.getChatApp().dock(_favoritesPanel);
         }
      });
      popup.add(mi2);

      return popup;
   }
   //-----------------------------------------------------------------
   public void update() {
      _favoritesNode.update();
      try {((DefaultTreeModel)getModel()).reload();} catch (Exception e) {
         Debug.println("Internal Swing error");
         Debug.printStackTrace(e);
      }
      expandPath(new TreePath(_favoritesNode.getServersNode().getPath()));
      expandPath(new TreePath(_favoritesNode.getChannelsNode().getPath()));
      expandPath(new TreePath(_favoritesNode.getUsersNode().getPath()));
      setRowHeight(FavoritesPanel.ROW_HEIGHT);
   }
}

///////////////////////////////////////////////////////////////////////

/**
 * Tree node with three subnodes Servers, Users and Channels.
 */
class FavoritesNode extends DefaultMutableTreeNode {

   private final ServersFolderNode  _favServers;
   private final ChannelsFolderNode _favChannels;
   private final UsersFolderNode    _favUsers;

   public ServersFolderNode  getServersNode()  {return _favServers;}
   public ChannelsFolderNode getChannelsNode() {return _favChannels;}
   public UsersFolderNode    getUsersNode()    {return _favUsers;}

   //-------------------------------------------------------------
   public FavoritesNode() {
      super("Favorites");

      _favServers = new ServersFolderNode();
      _favUsers = new UsersFolderNode();
      _favChannels = new ChannelsFolderNode();

      add(_favServers);
      add(_favUsers);
      add(_favChannels);

      update();
   }

   //-------------------------------------------------------------
   public void update() {
      _favServers.update();
      _favChannels.update();
      _favUsers.update();
   }
}

///////////////////////////////////////////////////////////////////////

class ServersFolderNode
   extends DefaultMutableTreeNode implements FavoritesTreeNode {

   //-----------------------------------------------------------------
   public ServersFolderNode() {
      super("Servers");
      setAllowsChildren(true);
   }
   //-----------------------------------------------------------------
   public void update() {
      removeAllChildren();
      ChatOptions opt = ChatApp.getChatApp().getOptions();
      for (int i=0; i<opt.getAllServers().getServerCount(); i++) {
        if (opt.getAllServers().getServer(i).isFavorite())
          add(new ServerNode(opt.getAllServers().getServer(i)));
      }
   }
   //-----------------------------------------------------------------
   public JPopupMenu createPopupMenu() {

      JPopupMenu popup = new JPopupMenu();

      popup.add(ChatApp.getChatApp().getAction(
         ChatApp.CONNECT).getActionObject());

      popup.add(ChatApp.getChatApp().getAction(
         ChatApp.DISCONNECT).getActionObject());

      popup.add(ChatApp.getChatApp().getAction(
         ChatApp.EDIT_SERVER_LIST).getActionObject());

      return popup;
   }
   //-----------------------------------------------------------------
   public void handleDoubleClick() {
   }
}

///////////////////////////////////////////////////////////////////////

class ServerNode
   extends DefaultMutableTreeNode implements FavoritesTreeNode {

   private Server _server = null;

   //-----------------------------------------------------------------
   public ServerNode(Server server) {
      super(server);
      _server = server;
   }
   //-----------------------------------------------------------------
   public JPopupMenu createPopupMenu() {
      GuiServer guiServer = new GuiServer(_server);
      return guiServer.createPopupMenu();
   }
   //-----------------------------------------------------------------
   public void update() {
   }
   //-----------------------------------------------------------------
   public void handleDoubleClick() {
      //if (!ChatApp.getChatApp().isConnected()) {
         ChatApp.getChatApp().connect(_server);
      //}
   }
}

///////////////////////////////////////////////////////////////////////

class ChannelsFolderNode extends DefaultMutableTreeNode
   implements FavoritesTreeNode {

   //-----------------------------------------------------------------
   public ChannelsFolderNode() {
      super("Channels");
      setAllowsChildren(true);
   }
   //-----------------------------------------------------------------
   public void update() {
      removeAllChildren();
      ChatOptions opt = ChatApp.getChatApp().getOptions();
      for (int j=0; j<opt.getFavoriteChannels().getChannelCount(); j++) {
        add(new ChannelNode(opt.getFavoriteChannels().getChannel(j)));
      }
   }
   //-----------------------------------------------------------------
   public JPopupMenu createPopupMenu() {

      JPopupMenu popup = new JPopupMenu();

      popup.add(ChatApp.getChatApp().getAction(
         ChatApp.JOIN_CHANNEL ).getActionObject());

      popup.add(ChatApp.getChatApp().getAction(
         ChatApp.LIST_CHANNELS ).getActionObject());

      return popup;
   }
   //-----------------------------------------------------------------
   public void handleDoubleClick() {
      Debug.println("ChannelsFolderNode: handleDoubleClick()");
   }
}

///////////////////////////////////////////////////////////////////////

/** Tree node for a channel, provides pop-up menu for channel. */
class ChannelNode extends DefaultMutableTreeNode implements FavoritesTreeNode {
   private Channel _channel = null;

   //-----------------------------------------------------------------
   public ChannelNode(Channel channel) {
      super(channel);
      _channel = channel;
      setAllowsChildren(true);
   }
   //-----------------------------------------------------------------
   public void update() {
   }
   //-----------------------------------------------------------------
   public JPopupMenu createPopupMenu() {
      GuiChannel guiChan = new GuiChannel(_channel);
      return guiChan.createPopupMenu();
   }
   //-----------------------------------------------------------------
   public void handleDoubleClick() {
      //RCTest.println("ChannelsNode: handleDoubleClick()");

      // If channel is currently connected/joined then bring it to the front
      if (_channel.isConnected()) {
         _channel.activate();
      }

      // Otherwise, join channel
      else if (ChatApp.getChatApp().isConnected()) {
         _channel.setServer(ChatApp.getChatApp().getServer());
         _channel.sendJoin();
      }
   }
}

///////////////////////////////////////////////////////////////////////

class UsersFolderNode
   extends DefaultMutableTreeNode implements FavoritesTreeNode {

   //-----------------------------------------------------------------
   public UsersFolderNode() {
      super("Users");
      setAllowsChildren(true);
   }
   //-----------------------------------------------------------------
   public void update() {
      removeAllChildren();
      ChatOptions opt = ChatApp.getChatApp().getOptions();
      for (int k=0; k < opt.getFavoriteUsers().getUserCount(); k++) {
         add(new UserNode(opt.getFavoriteUsers().getUser(k)));
      }
   }
   //-----------------------------------------------------------------
   public JPopupMenu createPopupMenu() {

      JPopupMenu popup = new JPopupMenu();
      popup.add(ChatApp.getChatApp().getAction(
         ChatApp.WHOIS).getActionObject());

      return popup;
   }
   //-----------------------------------------------------------------
   public void handleDoubleClick() {
   }
}

///////////////////////////////////////////////////////////////////////

class UserNode
   extends DefaultMutableTreeNode implements FavoritesTreeNode {

   private User _user = null;

   //-----------------------------------------------------------------
   public UserNode(User user) {
      super(user);
      _user = user;
      setAllowsChildren(true);
   }
   //-----------------------------------------------------------------
   public void update() {
   }
   //-----------------------------------------------------------------
   public void handleDoubleClick() {
   }
   //-----------------------------------------------------------------
   public JPopupMenu createPopupMenu() {
      GuiUser guiUser = new GuiUser(_user);
      return guiUser.createPopupMenu();
   }
}

///////////////////////////////////////////////////////////////////////

/** Tree cell renderer that knows how to draw servers, users and channels. */
class FavoritesTreeCellRenderer extends DefaultTreeCellRenderer {

   //--------------------------------------------------------------------------
   public FavoritesTreeCellRenderer() {
      setClosedIcon(IconManager.getIcon("FolderIcon"));
      setOpenIcon(IconManager.getIcon("Open"));
      setLeafIcon(IconManager.getIcon("Folder"));
   }
   //--------------------------------------------------------------------------
   public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      Component comp = super.getTreeCellRendererComponent(
          tree,value,sel,expanded,leaf,row,hasFocus);

      JLabel label = (JLabel)comp;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

      // By default, use plain font
      if (label.getFont() != null) {
         Font oldFont = label.getFont();
         label.setFont(
            new Font(oldFont.getName(),Font.PLAIN,oldFont.getSize()));
      }

      if (node.getUserObject() instanceof User) {
         label.setIcon(IconManager.getIcon("User"));
      }
      else if (node.getUserObject() instanceof Channel) {
         label.setIcon(IconManager.getIcon("ReplyAll"));
      }
      else if (node.getUserObject() instanceof Server) {
         label.setIcon(IconManager.getIcon("Workstation"));

         // Use bold font if the server is the selected server
         Font newFont = null;
         Font oldFont = label.getFont();
         Server selServer = ChatApp.getChatApp().getOptions().getCurrentServer();
         if ( node.getUserObject() == selServer)
            newFont = new Font(oldFont.getName(),Font.BOLD,oldFont.getSize());
         else
            newFont = new Font(oldFont.getName(),Font.PLAIN,oldFont.getSize());
         label.setFont(newFont);
      }
      return comp;
   }
}

