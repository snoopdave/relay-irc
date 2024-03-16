//-----------------------------------------------------------------------------
// $RCSfile: ChannelSearchPanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.ChannelSearch;
import org.relayirc.chatengine.ChannelSearchListener;
import org.relayirc.swingutil.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;

///////////////////////////////////////////////////////////////////////

/**
 * A panel containing fields for specifying a channel search, a search
 * button to start the search and a table in which to display the
 * search results.
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
public class ChannelSearchPanel extends JPanel
   implements MDIClientPanel, ChannelSearchListener {

   private final ChannelSearch _search;
   private String        _dockState = MDIPanel.DOCK_NONE;
   private final ChannelTable  _table;
   private final JLabel        _status;
   private final JProgressBar  _progress;
   private int           _progressValue = 0;

   /** Construct a search panel for a specified channel search object. */
   public ChannelSearchPanel(ChannelSearch search) {
      _search = search;
      _search.addChannelSearchListener(this);

      setLayout(new BorderLayout());

      // Query panel to specify search parameters
      add(new QueryPanel(this),BorderLayout.NORTH);

      // Channel table to show results
      _table = new ChannelTable(search);
      add(new JScrollPane(_table),BorderLayout.CENTER);

      // Status bar and progress meter
      JPanel statusPanel = new JPanel();
      statusPanel.setLayout(new BorderLayout());

      _status = new JLabel();
      _status.setBorder(new BevelBorder(BevelBorder.LOWERED));
      statusPanel.add(_status,BorderLayout.CENTER);

      _progress = new JProgressBar();
      _progress.setBorder(new BevelBorder(BevelBorder.LOWERED));
      statusPanel.add(_progress,BorderLayout.EAST);

      add(statusPanel,BorderLayout.SOUTH);
   }
   //-----------------------------------------------------------------
   /** Returns panel's search object. */
   public ChannelSearch getChannelSearch() {
      return _search;
   }
   //------------------------------------------------------------------
   /** Get dock-state of this panel. @see org.relayirc.swingutil.MDIClientPanel */
   public String getDockState() {
      return _dockState;
   }
   //------------------------------------------------------------------
   /** Set dock-state of this panel. @see org.relayirc.swingutil.MDIClientPanel */
   public void setDockState(String dockState) {
      _dockState = dockState;
   }
   //------------------------------------------------------------------
   /** Get panel for this MDI client. @see org.relayirc.swingutil.MDIClientPanel */
   public JPanel getPanel() {
      return this;
   }
   //----------------------------------------------------------------------
   /** Called when the search is started. */
   public void searchStarted(int channels) {
      _status.setText("Searching...");
      _progressValue = 0;
      _progress.setMinimum(0);
      _progress.setMaximum(channels);
   }
   //----------------------------------------------------------------------
   /** Called with a channel has been found by the search. */
   public void searchFound(Channel chan) {
      _progress.setValue(++_progressValue);
   }
   //----------------------------------------------------------------------
   /** Called when the search is complete. */
   public void searchEnded() {
      _status.setText("Search Complete ("+_table.getRowCount()+" channels found)");
      _progressValue = 0;
      _progress.setValue(0);
   }
}

///////////////////////////////////////////////////////////////////////////////////

/**
 * Panel of text fields for entering search criteria such as Name,
 * Min Users, Max Users, etc. Designed to be used within a
 * ChannelSearchPanel.
 * @see ChannelSearchPanel
 */
class QueryPanel extends JPanel {
   private final JTextField _nameField = new JTextField(10);
   private final JTextField _minField = new JTextField(3);
   private final JTextField _maxField = new JTextField(3);
   private final ChannelSearchPanel _searchPanel;

   public QueryPanel(ChannelSearchPanel searchPanel) {
      _searchPanel = searchPanel;

      setBorder(new EmptyBorder(10,10,10,10));
      setLayout(new GridBagLayout());

      // TODO: Implement search-by-channel name or regex against channel names
      //add(new JLabel("Name",SwingConstants.RIGHT),
         //new GridBagConstraints2(0,0,1,1, 0.0,0.0,
         //GridBagConstraints.EAST,GridBagConstraints.NONE,
         //new Insets(1,1,1,1),0,0));
      //add(_nameField,
         //new GridBagConstraints2(1,0,1,1, 1.0,0.0,
         //GridBagConstraints.WEST,GridBagConstraints.NONE,
         //new Insets(1,1,1,1),0,0));

      // Min users field
      add(new JLabel("Min Users",SwingConstants.RIGHT),
         new GridBagConstraints2(0,0,1,1, 0.0,0.0,
         GridBagConstraints.WEST,GridBagConstraints.NONE,
         new Insets(1,1,1,1),0,0));
      add(_minField,
         new GridBagConstraints2(1,0,1,1, 0.0,0.0,
         GridBagConstraints.WEST,GridBagConstraints.NONE,
         new Insets(1,1,1,1),0,0));
      // If min users is set, then put value in min field
      if (_searchPanel.getChannelSearch().getMinUsers() != Integer.MIN_VALUE) {
         _minField.setText(
           Integer.toString(_searchPanel.getChannelSearch().getMinUsers()));
      }


      // Max users field
      add(new JLabel("Max Users",SwingConstants.RIGHT),
         new GridBagConstraints2(0,1,1,1, 0.0,0.0,
         GridBagConstraints.WEST,GridBagConstraints.NONE,
         new Insets(1,1,1,1),0,0));
      add(_maxField,
         new GridBagConstraints2(1,1,1,1, 0.0,0.0,
         GridBagConstraints.WEST,GridBagConstraints.NONE,
         new Insets(1,1,1,1),0,0));
      // If max users is set, then put value in max field
      if (_searchPanel.getChannelSearch().getMaxUsers() != Integer.MAX_VALUE) {
         _maxField.setText(
           Integer.toString(_searchPanel.getChannelSearch().getMaxUsers()));
      }


      //add(new JCheckBox("Save Results"),
         //new GridBagConstraints2(2,2,1,1,0.0,0.0,
         //GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,
         //new Insets(1,1,1,1),0,0));


      // Search button starts search
      JButton searchButton = new JButton("Search");
      add(searchButton,
         new GridBagConstraints2(2,0,1,1,0.0,0.0,
         GridBagConstraints.EAST,
         GridBagConstraints.HORIZONTAL,new Insets(1,1,1,1),0,0));

      searchButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent event) {

            int ret = JOptionPane.showConfirmDialog(ChatApp.getChatApp(),
               "Some servers do not support channel searches and may \n"
               +"disconnect. Are you sure you want to run this search?");

            if (ret == JOptionPane.YES_OPTION) { // or JOptionPane.OK_OPTION
               int min = Integer.MIN_VALUE;
               int max = Integer.MAX_VALUE;

               try {min = Integer.parseInt(_minField.getText())-1;} 
               catch (Exception e) {}

               try {max = Integer.parseInt(_maxField.getText())+1;} 
               catch (Exception e) {}

               _searchPanel.getChannelSearch().setMinUsers(min);
               _searchPanel.getChannelSearch().setMaxUsers(max);
               _searchPanel.getChannelSearch().start();
            }
         }
      });

      //add(new JButton("Save"),
         //new GridBagConstraints2(2,1,1,1,0.0,0.0,
         //GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,
         //new Insets(1,1,1,1),0,0));
   }
}

///////////////////////////////////////////////////////////////////////////////////

/**
 * Table of channels that met search criteria. Uses ChannelTableModel
 * to allow editing of channel information right in the table.
 * @see ChannelTableModel
 */
class ChannelTable extends JTable {
   private final ChannelSearch _search;
   private final TableSorter _sortedTableModel;

   /**
    * Construct channel table by adding custom cell renderers 
    * and mouse listeners.
    */
   public ChannelTable(ChannelSearch search) {
      //super(new ChannelTableModel(search));

      _search = search;

      _sortedTableModel = new TableSorter(new ChannelTableModel(search));
      _sortedTableModel.addMouseListenerToHeaderInTable(this);
      setModel(_sortedTableModel);

      setRowHeight(20);
      setShowGrid(false);
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      getColumn(
              "Name").setHeaderRenderer(new SortedHeaderRenderer());
      getColumn(
              "Name").setCellRenderer(new GuiTableCellRenderer());

      getColumn(
              "Users").setHeaderRenderer(new SortedHeaderRenderer());
      getColumn(
              "Users").setCellRenderer(new TableCellRenderer() {

         public Component getTableCellRendererComponent(
            JTable table, Object value,

            boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel comp = new JLabel(value.toString(),SwingConstants.CENTER);
            comp.setOpaque(true);
            if (isSelected) {
               comp.setForeground(table.getSelectionForeground());
               comp.setBackground(table.getSelectionBackground());
            } else {
               comp.setForeground(table.getForeground());
               comp.setBackground(table.getBackground());
            }
            return comp;
         }
      });

      getColumn(
              "Topic").setHeaderRenderer(new SortedHeaderRenderer());

      // Listen for double-clicks and right-clicks
      addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent event) {
            final MouseEvent me = event;

            // Double-click?
            if (me.getClickCount()==2) {

               // Yes, join that channel!
               int row = rowAtPoint(me.getPoint());
               if (row!=-1) {
                   String chan = getModel().getValueAt(row,0).toString();
                   ChatApp.getChatApp().getServer().sendJoin(chan);
               }
            }
         }
      });
   }
   //-----------------------------------------------------------------
   /**
    * For some reason, the mouse listener does not see popup-triggers
    * so this method is necessary. This method handles right-clicks
    * the channel table. There is also a mouse listener, created in
    * the constructor, that handles double-clicks.
    */
   public void processMouseEvent( MouseEvent e ) {
      final MouseEvent me = e;

		// Right-click?
 	   if (e.isPopupTrigger()
          || (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0
          || (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {

         // Yes, show popup menu
         final JPopupMenu popup = new JPopupMenu();

         JMenuItem addFaveItem = new JMenuItem("Add channel to favorites");
         popup.add(addFaveItem);
         addFaveItem.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int row = rowAtPoint(me.getPoint());
               if (row!=-1) {
                  String chanName = getModel().getValueAt(row,0).toString();
		            Channel chan = new Channel(chanName);
                  ChatOptions opts = ChatApp.getChatApp().getOptions();
                  opts.getFavoriteChannels().addChannel(chan);
               }
            }
         });

         JMenuItem joinItem = new JMenuItem("Join channel");
         popup.add(joinItem);
         joinItem.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int row = rowAtPoint(me.getPoint());
               if (row!=-1) {
                  String chan = getModel().getValueAt(row,0).toString();
                  ChatApp.getChatApp().getServer().sendJoin(chan);
               }
            }
         });

         Point pt2 = SwingUtilities.convertPoint(
            (Component)me.getSource(),
            new Point(me.getX(),me.getY()),ChannelTable.this);

         popup.show(ChannelTable.this,pt2.x,pt2.y);
		}
      super.processMouseEvent(e);
   }
}


///////////////////////////////////////////////////////////////////////////////////

/**
 * Table model for displaying channel search results with each row
 * represents a channel.
 */
class ChannelTableModel extends DefaultTableModel 
   implements ChannelSearchListener {

   private final ChannelSearch _search;

   //----------------------------------------------------------------------
   public ChannelTableModel(ChannelSearch search) {
      _search = search;
      _search.addChannelSearchListener(this);
   }

   //----------------------------------------------------------------------
   public void searchStarted(int channels) {
   }

   //----------------------------------------------------------------------
   public void searchFound(Channel chan) {
      fireTableDataChanged();
   }

   //----------------------------------------------------------------------
   public void searchEnded() {
   }

   //----------------------------------------------------------------------
   public int getRowCount() {
      if (_search!=null)
         return _search.getChannelCount();
      else
         return 0;
   }

   //----------------------------------------------------------------------
   public Class getColumnClass(int column) {
      switch (column) {
         case 1:
            return Integer.class; 
         default:
            return String.class; 
      }
   }

   //----------------------------------------------------------------------
   public int getColumnCount() {
      return 3; // Name, User Count and Topic
   }

   //----------------------------------------------------------------------
   public String getColumnName(int column) {
      switch (column) {
         case 0:
            return "Name";
         case 1:
            return "Users";
         case 2:
            return "Topic";
         default:
            return "";
      }
   }

   //----------------------------------------------------------------------
   public Object getValueAt(int row, int column) {
      if (_search!=null) {
         switch (column) {
            case 0:
               return new GuiObject(
                  _search.getChannelAt(row).getName(),_search.getChannelAt(row),
                  IconManager.getIcon("users"));
            case 1:
               return Integer.valueOf(_search.getChannelAt(row).getUserCount());
            case 2:
               return _search.getChannelAt(row).getTopic();
            default:
               return "";
         }
      }
      else return null;
   }
   //----------------------------------------------------------------------
   public boolean isCellEditable(int rowIndex,int columnIndex) {
      return false;
   }
}


