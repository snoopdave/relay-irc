//-----------------------------------------------------------------------------
// $RCSfile: ServerListPanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.Server;
import org.relayirc.swingutil.GuiObject;
import org.relayirc.swingutil.IconManager;
import org.relayirc.swingutil.SortedHeaderRenderer;
import org.relayirc.swingutil.TableSorter;
import org.relayirc.util.Debug;
import org.relayirc.util.Utilities;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

///////////////////////////////////////////////////////////////////////

/**
 * Relay's server list editor panel.
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
public class ServerListPanel extends JPanel {

    private final ServerButtonPanel _buttonPanel;
    private final ServerTable _table;

    //-----------------------------------------------------------------
    public ServerListPanel() {
        this(ChatApp.getChatApp().getOptions().getAllServers());
    }

    /**
     * Construct server list panel for editing a server list.
     */
    public ServerListPanel(ChatOptions.ServerList list) {
        setLayout(new BorderLayout());

        // Query panel to specify search parameters
        _buttonPanel = new ServerButtonPanel(this, list);
        add(_buttonPanel, BorderLayout.NORTH);

        // Channel table to show results
        _table = new ServerTable(this, list);
        add(new JScrollPane(_table), BorderLayout.CENTER);

        doLayout();
    }

    //-----------------------------------------------------------------
    public static void main(String[] args) {

        Debug.setDebug(true);

        JFrame frame = new JFrame();

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new ServerListPanel(), BorderLayout.CENTER);
        frame.setLocation(200, 200);
        frame.setSize(500, 400);
        frame.setVisible(true);


        // Add listener to quit on frame close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    //-----------------------------------------------------------------

    public ServerButtonPanel getServerButtonPanel() {
        return _buttonPanel;
    }

    public ServerTable getServerTable() {
        return _table;
    }
}

///////////////////////////////////////////////////////////////////////////////////

/**
 * ServerListPanel's button panel.
 * <br>
 * TODO:
 * <ul>
 *    <li>Icons on the buttons.</li>
 *    <li>Finish export-to-mirc-format</li>
 *    <li>Import-from-mirc should not spew exceptions</li>
 *    <li>Remove should ask user for confirmation</li>
 * </ul>
 */
class ServerButtonPanel extends JPanel {

    private final ServerListPanel _serverListPanel;
    private final ChatOptions.ServerList _list;
    private JButton _addButton;
    private JButton _modButton;
    private JButton _remButton;
    private JButton _impButton;
    private JButton _expButton;
    private Server _selectedServer = null;    // Currently selected server

    //-----------------------------------------------------------------------------
    public ServerButtonPanel(ServerListPanel serverListPanel, ChatOptions.ServerList list) {
        _serverListPanel = serverListPanel;
        _list = list;
        initGUI();
        initListeners();
    }

    //-----------------------------------------------------------------------------
    private void initGUI() {

        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setLayout(new BorderLayout());

        Box box = Box.createHorizontalBox();

        // Add button adds server specified by text fields to the list of servers
        _addButton = new JButton("Add");
        _addButton.setToolTipText("Add server to list");
        _addButton.setIcon(IconManager.getIcon("UpdateRow"));
        box.add(_addButton);

        // Remove button removes the currently selected server from the list
        _remButton = new JButton("Remove");
        _remButton.setEnabled(false);
        _remButton.setToolTipText("Remove selected server");
        _remButton.setIcon(IconManager.getIcon("DeleteRow"));
        box.add(_remButton);

        // Import button for importing contents of a MIRC servers.ini file
        _impButton = new JButton("Import...");
        _impButton.setToolTipText("Import MIRC servers.ini format file");
        _impButton.setIcon(IconManager.getIcon("Open"));
        box.add(_impButton);

        // Export button for exporting to MIRC servers.ini format file
        _expButton = new JButton("Export...");
        _expButton.setToolTipText("Export to MIRC servers.ini format file");
        _expButton.setEnabled(false);
        _expButton.setIcon(IconManager.getIcon("Save"));
        box.add(_expButton);

        add(box, BorderLayout.CENTER);
    }

    //-----------------------------------------------------------------------------
    private void initListeners() {

        // Add button adds new server to list
        _addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                _serverListPanel.getServerTable().addServer(
                        new Server("Server1", 6667, "", ""));
            }
        });

        // Remove button removes the currently selected server from the list
        _remButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    _serverListPanel.getServerTable().removeSelectedServers();
                    noServerSelected();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ServerButtonPanel.this,
                            "Error removing server, no server selected.", "Remove Server Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Import button for importing contents of a MIRC servers.ini file
        _impButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser chooser = new JFileChooser(new File("."));
                int choice = chooser.showOpenDialog(ServerButtonPanel.this);

                if (choice == JFileChooser.APPROVE_OPTION) {

                    _list.importMircFile(chooser.getSelectedFile());
                    _serverListPanel.getServerTable().serverListUpdated();
                }
            }
        });

        // Export button for exporting contents to a MIRC servers.ini format file
        _expButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser chooser = new JFileChooser();
                chooser.showSaveDialog(ServerButtonPanel.this);
            }
        });
    }

    //----------------------------------------------------------------------
    public void noServerSelected() {
        _selectedServer = null;
        _remButton.setEnabled(false);
    }

    //----------------------------------------------------------------------
    public void multipleServersSelected() {
        _selectedServer = null;
        _remButton.setEnabled(true);
    }

    //----------------------------------------------------------------------
    public void singleServerSelected(Server server) {
        _selectedServer = server;
        _remButton.setEnabled(true);
    }
}

///////////////////////////////////////////////////////////////////////////////////


/**
 * ServerListPanel's editable JTable of servers. Each row represents one
 * server and the columns are Name, Title, Network, Ports and Favorite.
 *
 * <br>
 * <strong>Columns</strong>
 * <ul>
 *    <li>Name - hostname of the IRC server (i.e. irc.mindspring.com)</li>
 *    <li>Title - descriptive text (optional)</li>
 *    <li>Network - name of server's chat network (optional)</li>
 *    <li>Ports - list of port numbers, separated by commas</li>
 * </ul>
 *
 * <br>
 * TODO:
 * <ul>
 *    <li>Add-server should select name field of new server for editing</li>
 * </ul>
 */
class ServerTable extends JTable {

    public static final int NAME_COLUMN = 0;
    public static final int DESC_COLUMN = 1;
    public static final int NETWORK_COLUMN = 2;
    public static final int PORTS_COLUMN = 3;
    public static final int FAVE_COLUMN = 4;
    public static final int CLICKS = 2;
    private final ServerListPanel _serverListPanel;
    private final ServerTableModel _serverModel;
    private final TableSorter _sortModel;


    //-----------------------------------------------------------------
    public ServerTable(ServerListPanel serverListPanel, ChatOptions.ServerList list) {
        _serverListPanel = serverListPanel;

        _serverModel = new ServerTableModel(list);
        _sortModel = new TableSorter(_serverModel);
        _sortModel.addMouseListenerToHeaderInTable(this);
        setModel(_sortModel);

        setRowHeight(20);
        setShowGrid(false);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        getColumn("Name").setHeaderRenderer(new SortedHeaderRenderer());
        getColumn("Description").setHeaderRenderer(new SortedHeaderRenderer());
        getColumn("Network").setHeaderRenderer(new SortedHeaderRenderer());
        getColumn("Favorite").setHeaderRenderer(new SortedHeaderRenderer());
        getColumn("Port").setHeaderRenderer(new SortedHeaderRenderer());
        getColumn("Network").setCellEditor(new NetworkCellEditor(list));
        getColumn("Favorite").setCellEditor(new FavoriteCellEditor());

        // Notify server list panel of selection changes
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {

                if (getSelectionModel().isSelectionEmpty()) {
                    // Nothing selected
                    _serverListPanel.getServerButtonPanel().noServerSelected();
                } else if (getSelectionModel().getMinSelectionIndex()
                        == getSelectionModel().getMaxSelectionIndex()) {
                    // One server selected
                    GuiObject guiObject = (GuiObject) getValueAt(getSelectionModel().getMinSelectionIndex(), 0);
                    Server server = (Server) guiObject.getObject();
                    _serverListPanel.getServerButtonPanel().singleServerSelected(server);
                } else {
                    // Multiple servers selected
                    _serverListPanel.getServerButtonPanel().multipleServersSelected();
                }
            }
        });
    }

    //-----------------------------------------------------------------
    public void serverListUpdated() {
        _serverModel.fireTableDataChanged();
    }

    //-----------------------------------------------------------------
    public void addServer(Server server) {
        _serverModel.addServer(server);

        // select new server in the table
        getSelectionModel().setSelectionInterval(0, 0);

        // and put the focus on the server name
        editCellAt(0, 0);
    }

    //-----------------------------------------------------------------
    public void removeSelectedServers() {

        // Work backwards when deleting items
        for (int i = getSelectionModel().getMaxSelectionIndex();
             i >= getSelectionModel().getMinSelectionIndex(); i--) {

            // Selection can be non-contiguous, so check each item in selection range
            if (getSelectionModel().isSelectedIndex(i)) {
                GuiObject guiObject = (GuiObject) getValueAt(i, 0);
                Server server = (Server) guiObject.getObject();
                _serverModel.removeServer(server);
            }
        }

        getSelectionModel().clearSelection();
        _serverModel.fireTableDataChanged();
    }
}

///////////////////////////////////////////////////////////////////////////////////

/**
 * ServerList table model.
 */
class ServerTableModel extends DefaultTableModel {
    private ChatOptions.ServerList _list = null;

    //----------------------------------------------------------------------
    public ServerTableModel(ChatOptions.ServerList list) {
        _list = list;
    }

    //-----------------------------------------------------------------
    public void addServer(Server server) {
        _list.addServer(server);
        fireTableDataChanged();
    }

    //-----------------------------------------------------------------
    public void removeServer(Server server) {
        _list.removeServer(server);
        fireTableDataChanged();
    }

    //----------------------------------------------------------------------
    public int getRowCount() {
        if (_list != null)
            return _list.getServerCount();
        else
            return 0;
    }

    //-------------------------------------------------------------------
    public Class getColumnClass(int c) {
        if (getValueAt(0, c) != null)
            return getValueAt(0, c).getClass();
        else
            return "".getClass();
    }

    //----------------------------------------------------------------------
    public int getColumnCount() {
        return 6; // Network, Title, Server, Favorite
    }

    //----------------------------------------------------------------------
    public String getColumnName(int column) {
        switch (column) {

            case ServerTable.NAME_COLUMN:
                return "Name";

            case ServerTable.DESC_COLUMN:
                return "Description";

            case ServerTable.NETWORK_COLUMN:
                return "Network";

            case ServerTable.PORTS_COLUMN:
                return "Port";

            case ServerTable.FAVE_COLUMN:
                return "Favorite";

            default:
                return "";
        }
    }


    //----------------------------------------------------------------------
    public void setValueAt(Object value, int row, int column) {

        // Do nothing for null values
        if (value == null) return;

        switch (column) {

            case ServerTable.NAME_COLUMN:
                _list.getServer(row).setName(value.toString());
                break;

            case ServerTable.DESC_COLUMN:
                _list.getServer(row).setDescription(value.toString());
                break;

            case ServerTable.NETWORK_COLUMN:
                if (value.toString().equals("<None>"))
                    _list.getServer(row).setNetwork("");
                else
                    _list.getServer(row).setNetwork(value.toString());
                break;

            case ServerTable.PORTS_COLUMN:
                // parse comma delimited port string into int array
                Vector ports = new Vector();
                StringTokenizer toker = new StringTokenizer(value.toString(), ",");
                while (toker.hasMoreTokens()) {
                    try {
                        String portStr = toker.nextToken();
                        ports.addElement(Integer.valueOf(Integer.parseInt(portStr)));
                    } catch (Exception e) {
                    }
                }
                int[] portArray = new int[ports.size()];
                for (int i = 0; i < portArray.length; i++) {
                    portArray[i] = ((Integer) ports.elementAt(i)).intValue();
                }
                _list.getServer(row).setPorts(portArray);
                break;

            case ServerTable.FAVE_COLUMN:
                _list.getServer(row).setFavorite(((Boolean) value).booleanValue());
                break;

            default:
        }
    }

    //----------------------------------------------------------------------
    public Object getValueAt(int row, int column) {

        switch (column) {

            case ServerTable.NAME_COLUMN:
                return new GuiObject(
                        _list.getServer(row).getName(), _list.getServer(row),
                        IconManager.getIcon("Workstation"));

            case ServerTable.DESC_COLUMN:
                return _list.getServer(row).getDescription();

            case ServerTable.NETWORK_COLUMN:
                return _list.getServer(row).getNetwork();

            case ServerTable.PORTS_COLUMN:
                return Utilities.intArrayToString(_list.getServer(row).getPorts());

            case ServerTable.FAVE_COLUMN:
                return Boolean.valueOf(_list.getServer(row).isFavorite());

            default:
                return "";
        }
    }

    //----------------------------------------------------------------------
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}

///////////////////////////////////////////////////////////////////////////

class NetworkCellEditor extends DefaultCellEditor {
    private ChatOptions.ServerList _list = null;
    private JComboBox _combo = null;

    //--------------------------------------------------------------------
    public NetworkCellEditor(ChatOptions.ServerList list) {
        super(new JComboBox());
        setClickCountToStart(ServerTable.CLICKS);
        _list = list;
    }

    //--------------------------------------------------------------------
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {

        // If this is the 1st time, init the combobox
        if (_combo == null) {
            _combo = (JComboBox) super.getTableCellEditorComponent(
                    table, value, isSelected, row, column);

            _combo.setEditable(true);

            // Listen for combo change
            final int frow = row;
            final int fcol = column;
            _combo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ev) {
                    if (ev.getStateChange() == ItemEvent.SELECTED)
                        Debug.println("Network selected = " + ev.getItem());
                }
            });
        }

        // Refresh combo every time as network names list may change
        _combo.removeAllItems();

        // Load the network combo with network names
        Hashtable networkHash = new Hashtable();
        for (int i = 0; i < _list.getServerCount(); i++) {
            String nname = _list.getServer(i).getNetwork();
            if (nname.length() > 0) networkHash.put(nname, "dummy");
        }
        for (Enumeration e = networkHash.keys(); e.hasMoreElements(); ) {
            _combo.addItem(e.nextElement());
        }

        return _combo;
    }
}


///////////////////////////////////////////////////////////////////////////

class FavoriteCellEditor extends DefaultCellEditor {

    //--------------------------------------------------------------------
    public FavoriteCellEditor() {
        super(new JCheckBox());
        setClickCountToStart(ServerTable.CLICKS);
    }
}


