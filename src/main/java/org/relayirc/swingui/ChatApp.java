//-----------------------------------------------------------------------------
// $RCSfile: ChatApp.java,v $
// $Revision: 1.1.2.7 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.python.util.PythonInterpreter;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;
import org.relayirc.util.Debug;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.beans.Introspector;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.util.Enumeration;
import java.util.Hashtable;

//////////////////////////////////////////////////////////////////////////////

/**
 * Main class of the Relay-JFC chat application.
 *
 * @author David M. Johnson
 * @version $Revision: 1.1.2.7 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL</p>
 * Original Code: Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class ChatApp extends JFrame implements ServerListener {

    public static final String APP_VERSION = "1.0.0-SNAPSHOT";
    /**
     * Action name for the ABOUT action.
     */
    public static final String ABOUT = "About";
    /**
     * Action name for the CASCADE action.
     */
    public static final String CASCADE = "Cascade";
    /**
     * Action name for CONNECT action. You may supply a server
     * value by setting the action's "Server" property to the Server
     * object that you wish to connect. If this value is not set,
     * then the ConnectDlg dialog will be shown so that the user may
     * choose a server.
     */
    public static final String CONNECT = "Connect";
    /**
     * Action name for the CUSTOMIZER_ACTIONS action.
     */
    public static final String CUSTOMIZE_ACTIONS = "CustomizeActions";
    /**
     * Action name for the CUSTOMIZE_LISTENERS action.
     */
    public static final String CUSTOMIZE_LISTENERS = "CustomizeListeners";
    /**
     * Action name for the DISCONNECT action. This action will
     * trigger a disconnect from the current server.
     */
    public static final String DISCONNECT = "Disconnect";
    /**
     * Action name for the EDIT_SERVER_LIST action.
     */
    public static final String EDIT_SERVER_LIST = "EditServerList";
    /**
     * Action name for the EXIT action.
     */
    public static final String EXIT = "Exit";
    /**
     * Action name for the JOIN_CHANNEL action.
     */
    public static final String JOIN_CHANNEL = "JoinChannel";
    /**
     * Action name for the LIST_CHANNELS action.
     */
    public static final String LIST_CHANNELS = "ListChannels";
    /**
     * Action name for the SETUP action. This action
     * will pop-up the ChatOptionsDlg setup dialog will be shown.
     */
    public static final String SETUP = "Setup";
    /**
     * Action name for the TILE_HORZ action.
     */
    public static final String TILE_HORZ = "TileHorizontal";
    /**
     * Action name for the TILE_VERT action.
     */
    public static final String TILE_VERT = "TileVertical";
    /**
     * Action name for the WHOIS action.
     */
    public static final String WHOIS = "WhoIs";
    private static final String _appname = "Relay-JFC";
    private static ChatApp _chatApp = null;
    private static _StyleContext _styles;

    // Class constructor
    static {
        // Set up beans environment: add Relay-JFC BeanInfo package to path
        String[] path = Introspector.getBeanInfoSearchPath();
        String[] newPath = new String[path.length + 1];
        System.arraycopy(path, 0, newPath, 0, path.length);
        newPath[newPath.length - 1] = "org.relayirc.swingui.beans";
        Introspector.setBeanInfoSearchPath(newPath);
    }

    private final StatusBar _statusBar = new StatusBar();

    // ------------
    // Action names
    // ------------
    // Names for the getAction(String name) method.
    //
    /**
     * MDIFrames keyed by MDIPanels.
     */
    private final Hashtable<MDIClientPanel, MDIClientFrame> _framesByPanel = new Hashtable<>();
    /**
     * User queue. Collection of user objects each waiting to be
     * populated with data from an incoming WHOIS reply from the server.
     * The hashtable is keyed user objects and the values are Booleans.
     * If a user's boolean is true, then ChatApp will popup a dialog to
     * show the WHOIS information for the user as soon as the reply comes
     * in. Otherwise, no dialog will be shown.
     */
    private final Hashtable<User, Boolean> _userQueueHash = new Hashtable<>();
    /**
     * Action initialization array. The items in this array will be
     * used to initialized the _actions hashtable on the first time that
     * getAction(String name) is called.<p/>
     * To add a new action:<ul>
     * <li>Create an action class that implements IChatAction
     * <li>Pick an icon for the action in the images directory
     * <li>Add a public static final String action name for it
     * <li>Add action class, icon name and command name to this array
     * <li>Now your action is available via the getAction method</ul>
     */
    private final Object[][] _actionArray = {
            //
            // Action Class            Icon Name         Command Name
            // ------------            ---------         ------------
            {_AboutAction.class, "Inform", ABOUT},
            {_CascadeAction.class, "TileCascade", CASCADE},
            {_ConnectAction.class, "Plug", CONNECT},
            {_CustomizeActions.class, "Hammer", CUSTOMIZE_ACTIONS},
            {_CustomizeListeners.class, "Hammer", CUSTOMIZE_LISTENERS},
            {_DisconnectAction.class, "UnPlug", DISCONNECT},
            {_ExitAction.class, "ReplyAll", EXIT},
            {_JoinChannelAction.class, "ReplyAll", JOIN_CHANNEL},
            {_ListAction.class, "Binocular", LIST_CHANNELS},
            {_ServerListAction.class, "Workstation", EDIT_SERVER_LIST},
            {_SetupAction.class, "Hammer", SETUP},
            {_TileHAction.class, "TileHorizontal", TILE_HORZ},
            {_TileVAction.class, "TileVertical", TILE_VERT},
            {_WhoIsAction.class, "Help", WHOIS},
    };
    protected Server _server;
    private PythonInterpreter _python = null;
    private ChatOptions _options;
    private ConsoleFrame _console;
    private FavoritesFrame _favorites;
    private PythonFrame _pythonFrame;
    private _MenuBar _menuBar;
    private _ToolBar _toolBar;
    private boolean _statusBarEnabled = true;
    private MDIPanel _mdiPanel;
    private JSplitPane _vertSplitter;


    //------------------------------------------------------------------
    /**
     * Action collection. Hashtable of IChatAction values keyed by
     * action name.
     */
    private Hashtable<String, IChatAction> _actions = null;
    //------------------------------------------------------------------

    /**
     * Construct a chat application.
     */

    public ChatApp() {
        super(_appname + " " + APP_VERSION);
        //_python.exec("zzz=1"); // warm-up the interpreter...
    }
    //------------------------------------------------------------------

    /**
     * Main method for the Relay-JFC chat application.
     */

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-d")) {
            Debug.setDebug(true);
        }
        _chatApp = new ChatApp();
        _chatApp.run();

    }
    //------------------------------------------------------------------

    /**
     * Returns the one-and-only chat application object.
     */
    public static ChatApp getChatApp() {
        return _chatApp;
    }

    /**
     * Returns the one-and-only chat application object.
     */
    public static void setChatApp(ChatApp a) {
        _chatApp = a;
    }
    //------------------------------------------------------------------

    //------------------------------------------------------------------
    public static Hashtable<String, IChatAction> initActions(
            Object[][] actionArray, Object declaringObject) {

        // Create all action objects in action array
        Hashtable<String, IChatAction> actions = new Hashtable<>();
        for (Object[] objects : actionArray) {

            Class clz = (Class) objects[0];
            String iconName = (String) objects[1];
            String name = (String) objects[2];
            try {

                ImageIcon icon = IconManager.getIcon(iconName);

                Constructor ctor = null;
                Object[] args = null;

                // Find ctor for this action class.
                if (clz.getDeclaringClass() != null) {

                    // Action is an inner class, so constructor's first
                    // argument will be the declaring class.
                    Class[] argClasses = new Class[2];
                    argClasses[0] = clz.getDeclaringClass();
                    argClasses[1] = Class.forName("javax.swing.Icon");
                    ctor = clz.getConstructor(argClasses);

                    args = new Object[2];
                    args[0] = declaringObject;
                    args[1] = icon;
                } else {
                    // Action is not an inner class
                    Class[] argClasses = new Class[1];
                    argClasses[0] = Class.forName("javax.swing.Icon");
                    ctor = clz.getConstructor(argClasses);

                    args = new Object[1];
                    args[0] = icon;
                }

                // Construct action
                IChatAction action = (IChatAction) ctor.newInstance(args);

                // Hash action by action name
                actions.put(name, action);
            } catch (Exception e) {
                Debug.println("Exception creating action [" + name + "]");
                e.printStackTrace();
            }
        }
        return actions;
    }
    //------------------------------------------------------------------

    //------------------------------------------------------------------
    public static Style getChatStyle(String st) {
        return _styles.getStyle(st);
    }

    //------------------------------------------------------------------
    public static void setChatFont(Font font) {
        _styles.setFont(font);
    }

    //==================================================================
    // Accessors
    //==================================================================

    /**
     * The main application thread, for internal use only.
     */

    public void run() {

        // Read the chat options file
        String fileName = "";
        try {
            String home = System.getProperty("user.home");
            String sep = FileSystems.getDefault().getSeparator();
            fileName = home + sep + "relay.dat";
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            _options = (ChatOptions) ois.readObject();
            fis.close();
            Debug.println("Read options from file " + fileName);
        } catch (Exception e) {
            //Debug.printStackTrace(e);

            // Problem finding options file, so create new options
            Debug.println("First time user has used Relay-JFC");
            Debug.println("Creating new user preferences files at: " + fileName);
            _options = new ChatOptions();
            _options.initNewOptions();
        }

        // Create, init and layout GUI
        initGUI();
        layoutGUI();

        // Fake a disconnect event to disable most icons
        onDisconnect(null);

        // Set initial size and center on screen.
        setSize(800, 600);
        Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((ssize.width / 2) - (getSize().width / 2),
                (ssize.height / 2) - (getSize().height / 2));

        // Go live
        setVisible(true);
        toFront();
    }
    //------------------------------------------------------------------

    /**
     * Get action by name. See action name fields ChatApp.CONNECT
     * ChatApp.DISCONNECT, etc. for possible values.
     */
    public IChatAction getAction(String actionName) {
        if (_actions == null) {
            _actions = initActions(_actionArray, this);
        }
        return _actions.get(actionName);
    }

    /**
     * Returns the name of the chat application.
     */
    public String getAppName() {
        return _appname;
    }

    /**
     * Returns the version of the chat application.
     */
    public String getAppVersion() {
        return APP_VERSION;
    }

    //------------------------------------------------------------------
    public boolean isConnected() {
        if (getServer() == null) {
            return false;
        } else {
            return getServer().isConnected();
        }
    }

    //------------------------------------------------------------------
    public boolean isConnecting() {
        if (getServer() == null) {
            return false;
        } else {
            return getServer().isConnecting();
        }
    }

    //------------------------------------------------------------------
    public Server getServer() {
        return _server;
    }

    //------------------------------------------------------------------
    public void setServer(Server server) {
        _server = server;
    }

    //------------------------------------------------------------------
    public ChatOptions getOptions() {
        return _options;
    }

    //------------------------------------------------------------------
    public void setOptions(ChatOptions options) {
        _options = options;
    }

    //------------------------------------------------------------------
    public PythonInterpreter getPythonInterpreter() {
        if (_python == null) {
            _python = new PythonInterpreter();
        }
        return _python;
    }

    //------------------------------------------------------------------
    public void setStatusBarEnabled(boolean flag) {
        if (_statusBarEnabled != flag) {
            _statusBarEnabled = flag;
            if (_statusBarEnabled) {
                getContentPane().add(_statusBar, BorderLayout.SOUTH);
                validate();
            } else {
                getContentPane().remove(_statusBar);
                validate();
            }
        }
    }

    //------------------------------------------------------------------
    public void setShowConsole(boolean show) {
        _menuBar.getConsoleMenuItem().setState(show);
        if (!show) {
            _mdiPanel.removeClientFrame(_console);
        } else {
            _mdiPanel.addClientFrame(_console);
        }
    }

    //------------------------------------------------------------------
    public void setShowFavorites(boolean show) {
        _menuBar.getFavoritesMenuItem().setState(show);
        if (!show) {
            _mdiPanel.removeClientFrame(_favorites);
        } else {
            _mdiPanel.addClientFrame(_favorites);
        }
    }

    //------------------------------------------------------------------
    public void setShowPython(boolean show) {
        _menuBar.getPythonMenuItem().setState(show);
        if (_pythonFrame == null) {
            _pythonFrame = new PythonFrame(getPythonInterpreter());
        }
        if (!show) {
            _pythonFrame.setVisible(false);
            _mdiPanel.removeClientFrame(_pythonFrame);
        } else {
            _pythonFrame.setVisible(true);
            _mdiPanel.addClientFrame(_pythonFrame);
        }
    }

    //==================================================================
    // Methods
    //==================================================================

    /**
     * Connect to the specified chat server. If already connected to that
     * then say so and do nothing. If already connected to a different
     * prompt user if it is OK to disconnect and reconnect to the specified
     * server.
     */
    public synchronized void connect(Server server) {
        if (server == null) connect();

        if (isConnecting()) {
            // Silently ignore connection request if connecting
            return;
        } else if (isConnected() && server == _options.getCurrentServer()) {
            JOptionPane.showMessageDialog(ChatApp.getChatApp(),
                    "You are already connected to [" + server + "]",
                    "Already Connected", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (isConnected()) {
            int ret = JOptionPane.showConfirmDialog(ChatApp.getChatApp(),
                    "You are already connected to server [" + _options.getCurrentServer() + "]\n"
                            + "Would you like to disconnect and connect to [" + server + "]");
            if (ret != JOptionPane.YES_OPTION) {
                return;
            }
        } else if (isConnecting()) {
            return;
        } else if (_options.isFresh()) {
            _options.setCurrentServer(server);
            ConnectDlg dlg = new ConnectDlg(this);
            if (!dlg.isOk()) return;
        }

        _options.setCurrentServer(server);
        setServer(server);

        // Create and initialize the chat server/IRC connection
        getServer().setAppName(_appname);
        getServer().setAppVersion(APP_VERSION);
        getServer().addServerListener(this);

        _statusBar.startSpinner();
        getServer().connect(_options.getCurrentUser());
        updateActions();
    }
    //----------------------------------------------------------------------

    /**
     * Present connection dialog to user and allow connection.
     */
    public void connect() {

        if (isConnecting()) {
            // We're currently in the process of connecting, so ignore request.
            return;
        } else if (isConnected()) {
            // Already connected. Want to quit and reconnect to another server?
            int ret = JOptionPane.showConfirmDialog(ChatApp.getChatApp(),
                    "You are already connected to server [" + _options.getCurrentServer() + "]\n"
                            + "Would you like to disconnect and connect to some other server?");
            if (ret != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Show connect dialog, so user may pick server to connect to
        ConnectDlg dlg = new ConnectDlg(this);
        dlg.setVisible(true);

        if (dlg.isOk()) {
            Server server = ChatApp.getChatApp().getOptions().getCurrentServer();
            if (server != null) connect(server);
        }
    }
    //------------------------------------------------------------------

    /**
     * Display text on the main console.
     */
    public void display(String str) {
        _console.getChatPanel().println(str, _options.getProperty("gui.channel.color.messages"));
    }
    //------------------------------------------------------------------

    /**
     * Disconnect from the IRC server, save options and exit applicatin.
     */
    public void closeApp() {

        if (getServer() != null) {

            // Start a thread to disconnect from IRC server
            Thread disconnectThread = new Thread(new Runnable() {
                public void run() {
                    getServer().disconnect();
                }
            });
            // Start thread and wait three seconds for it to end
            disconnectThread.start();
            try {
                disconnectThread.join(3000);
            } catch (Exception ignored) {
            }
        }

        // Save chat options to a file
        String fileName = "";
        try {
            String home = System.getProperty("user.home");
            String sep = FileSystems.getDefault().getSeparator();
            fileName = home + sep + "relay.dat";
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_options);
            fos.close();
            System.out.println("Wrote options to file " + fileName);
        } catch (Exception ex) {
            System.out.println("Unable to write options to file " + fileName);
            ex.printStackTrace();
        }
        System.exit(0);
    }
    //------------------------------------------------------------------

    /**
     * Present the join channel dialog and if the user specifies a
     * channel then send join command to chat server.
     */
    public void showJoinChannelDlg() {
        JoinDlg dlg = new JoinDlg(this);
        if (dlg.isOk()) {
            getServer().sendJoin(dlg.getSelectedChannel());
        }
    }
    //------------------------------------------------------------------

    /**
     * Present the server list dialog.
     */
    public void showServerListDlg() {
        ServerListDlg dlg = new ServerListDlg(
                this, ChatApp.getChatApp().getOptions().getAllServers());
    }
    //------------------------------------------------------------------

    /**
     * Arrange windows in cascade formation.
     */
    public void cascade() {
        _mdiPanel.cascade();
    }
    //------------------------------------------------------------------

    /**
     * Tile windows horizontally.
     */
    public void tileHorizontal() {
        _mdiPanel.tileHorizontal();
    }
    //------------------------------------------------------------------

    /**
     * Tile windows vertically.
     */
    public void tileVertical() {
        _mdiPanel.tileVertical();
    }
    //------------------------------------------------------------------

    /**
     * Currenly, does nothing.
     */
    public void shutdown() {
    }

    //==================================================================
    // ServerListener implementation
    //==================================================================

    /**
     * Respond to onStatus() event from chat engine by displaying
     * status message on console.
     */
    public void onStatus(ServerEvent event) {
        display(event.getMessage());
    }
    //------------------------------------------------------------------

    /**
     * Handle channel object addition.
     */
    public void onChannelAdd(ServerEvent event) {
        Debug.println("ChatApp.onChannelAdd(" + event + ")");
        Channel chan = event.getChannel();

        ChannelFrame chatWin = new ChannelFrame(chan);
        _mdiPanel.addClientFrame(chatWin);
        _framesByPanel.put(chatWin.getChannelPanel(), chatWin);
    }
    //------------------------------------------------------------------

    /**
     * Create channel window for this new channel
     */
    public void onChannelJoin(ServerEvent event) {
        Debug.println("ChatApp.onChannelJoin(" + event + ")");
    }
    //------------------------------------------------------------------

    /**
     * Display information from ISON reply.
     */
    public void onIsOn(ServerEvent event) {
        display("ISON " + event.getUsers());
    }
    //------------------------------------------------------------------

    /**
     * Display information from INVITE message.
     */
    public void onInvite(ServerEvent event) {
        display(
                event.getOriginNick() + " has invited " +
                        event.getTargetNick() + " to join channel " +
                        event.getChannelName());
    }
    //------------------------------------------------------------------

    /**
     * Does nothing as channel objects take care of parting.
     */
    public void onChannelPart(ServerEvent event) {
    }
    //------------------------------------------------------------------

    /**
     * On connection, ask actions to update themselves.
     */
    public void onConnect(ServerEvent event) {
        _statusBar.stopSpinner();
        _statusBar.startTimer();

        updateActions();
        _menuBar.reloadCommandsMenu();
    }
    //------------------------------------------------------------------

    /**
     * On disconnection, ask actions to update themselves.
     */
    public void onDisconnect(ServerEvent event) {
        _statusBar.stopSpinner();
        _statusBar.stopTimer();
        updateActions();
        _menuBar.reloadCommandsMenu();

        // Try to remove self from server's listener list
        try {
            _server.removeServerListener(this);
        } catch (NullPointerException e) {
        }
    }
    //------------------------------------------------------------------

    /**
     * Request whois information for user specified by nick name.
     */
    public void sendWhoIs(String nick, boolean popup) {
        sendWhoIs(new User(nick), popup);
    }
    //------------------------------------------------------------------

    /**
     * Request whois information for user specified by user object.
     */
    public void sendWhoIs(User user, boolean popup) {
        if (getServer() == null) return;

        // Add user request to waiting list
        _userQueueHash.put(user, popup);

        // Send WHOIS command to chat server
        getServer().sendWhoIs(user);
    }
    //------------------------------------------------------------------

    /**
     * Respond to WhoIs message.
     */
    public void onWhoIs(ServerEvent event) {

        // If this is a user-requested WHOIS, then show User properties dialog.
        Boolean popup = _userQueueHash.get(event.getUser());
        if (popup != null) {
            if (popup) {
                new PropDlg(this, "User", event.getUser());
            }
        }

        // Either way, we are done with this user request
        _userQueueHash.remove(event.getUser());
    }

    //==================================================================
    // Implementation
    //==================================================================

    public void initGUI() {

        updateLookAndFeel();

        getContentPane().setLayout(new BorderLayout());
        _mdiPanel = new MDIPanel();
        _console = new ConsoleFrame(new ChatPanel(this));
        _favorites = new FavoritesFrame(this);
        setJMenuBar(_menuBar = new _MenuBar());

        try {
            String fname = _options.getProperty("gui.channel.font.name");

            int fstyle = Integer.parseInt(
                    _options.getProperty("gui.channel.font.style"));

            int fsize = Integer.parseInt(
                    _options.getProperty("gui.channel.font.size"));

            _styles = new _StyleContext(new Font(fname, fstyle, fsize));
        } catch (Exception e) {
            Debug.printStackTrace(e);
        }

        // Add listener to quit on frame close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeApp();
            }
        });
    }
    //------------------------------------------------------------------

    public void layoutGUI() {

        getContentPane().removeAll();

        getContentPane().add(_toolBar = new _ToolBar(), BorderLayout.NORTH);
        getContentPane().add(_statusBar, BorderLayout.SOUTH);
        getContentPane().add(_mdiPanel, BorderLayout.CENTER);

        _mdiPanel.addClientFrame(_favorites);
        _mdiPanel.addClientFrame(_console);

        _statusBarEnabled =
                _options.getProperty("gui.statusBar.enabled").equals("true");
        if (_statusBarEnabled) {
            getContentPane().add(_statusBar, BorderLayout.SOUTH);
        }
    }
    //------------------------------------------------------------------

    /**
     * To dock/undock a MDIClientPanel, first set the panel's dock state then
     * call this function to register that dock state. Simply calls the
     * MDIPanel.registerDockState() method for the specified panel.
     *
     * @see org.relayirc.swingutil.MDIPanel
     */
    public void dock(MDIClientPanel panel) {
        _mdiPanel.registerDockState(panel);

        //JPanel jp = panel.getPanel();
        //System.out.println("dock jp layout="+jp.getLayout());
        //System.out.println("dock jp count="+jp.getComponentCount());
    }
    //------------------------------------------------------------------

    /**
     * Remove chat channel panel from MDI panel and  close, hide and
     * dispose of channel's frame as well.
     */
    public void removeChatPanel(ChannelPanel panel) {
        ChannelFrame win = (ChannelFrame) _framesByPanel.get(panel.getPanel());

        _mdiPanel.removeClientFrame(win);
        try {
            win.setVisible(false);
            win.setClosed(true);
            win.dispose();
        } catch (Exception e) {
            // TODO: is that code above really the best way to kill a frame.
            Debug.printStackTrace(e);
        }
    }
    //------------------------------------------------------------------

    /**
     * Show the channel search window.
     */
    public void listChannels() {
        ChannelSearch search =
                new ChannelSearch(ChatApp.getChatApp().getServer());
        ChannelSearchFrame frame = new ChannelSearchFrame(search);
        _mdiPanel.addClientFrame(frame);
    }
    //--------------------------------------------------------------------------

    /**
     * Sets look and feel according to current ChatOptions settings.
     */
    public void updateLookAndFeel() {
        loadLookAndFeel(_options.getProperty("gui.lookAndFeel"));
    }
    //--------------------------------------------------------------------------

    /**
     * Load a Swing pluggable look and feel specified by name
     */
    public void loadLookAndFeel(String name) {
        boolean foundLaf = false;

        // The Skin Look and Feel v0.21 is a little too raw
        // If Skin PLAF requested then load Skin file
      /*if (name.equals(InterfacePanel.SKIN_PLAF_NAME))  {
         try {
            String skinFile = _options.getProperty("gui.lookAndFeel.skinFile");
            Skin sk = SkinLookAndFeel.loadSkin(skinFile);
            SkinLookAndFeel.setSkin(sk);
         }
         catch (Exception e) {
            // ignored by design
            e.printStackTrace();
         }
      }*/

        try {
            // Loop through installed PLAFs until we find requested PLAF
            UIManager.LookAndFeelInfo[] lafArray =
                    UIManager.getInstalledLookAndFeels();

            for (UIManager.LookAndFeelInfo lookAndFeelInfo : lafArray) {

                String lafName = lookAndFeelInfo.getName();
                if (name.equals(lookAndFeelInfo.getName())) {

                    // Load requested PLAF
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    validate();
                    foundLaf = true;
                    break;
                }
            }
        } catch (Exception e) {
            // ignored by design
            e.printStackTrace();
        }

        try {
            // Could't find requested look and feel, so use default
            if (!foundLaf) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                SwingUtilities.updateComponentTreeUI(this);
                validate();
                JOptionPane.showMessageDialog(
                        this, "Unable to load " + name + " look and feel, using default.",
                        "Relay-JFC Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this, "Unable to load " + name + " or default look and feel.",
                    "Relay-JFC Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //------------------------------------------------------------------
    public void updateActions() {
        for (Enumeration e = _actions.elements(); e.hasMoreElements(); ) {
            IChatAction action = (IChatAction) e.nextElement();
            action.update();
        }
    }

    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    private static class _CustomizeListeners extends ChatAction {

        public _CustomizeListeners(Icon icon) {
            super("Customize Listeners", icon);
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    private static class _StyleContext extends StyleContext {
        private final Hashtable<String, Color> _colors = new Hashtable<>();

        public _StyleContext(Font font) {
            super();

            _colors.put("Black", Color.black);
            _colors.put("Blue", Color.blue);
            _colors.put("Cyan", Color.cyan);
            _colors.put("DarkGray", Color.darkGray);
            _colors.put("Gray", Color.gray);
            _colors.put("Green", Color.green);
            _colors.put("LightGray", Color.lightGray);
            _colors.put("Magenta", Color.magenta);
            _colors.put("Orange", Color.orange);
            _colors.put("Pink", Color.pink);
            _colors.put("Red", Color.red);
            _colors.put("White", Color.white);
            _colors.put("Yellow", Color.yellow);

            Style def = this.getStyle(StyleContext.DEFAULT_STYLE);

            for (Enumeration<String> e = _colors.keys(); e.hasMoreElements(); ) {

                // One style for normal weight font
                String key = e.nextElement();
                Color col = _colors.get(key);
                Style s = addStyle(key, def);
                StyleConstants.setForeground(s, col);
                StyleConstants.setFontFamily(s, font.getName());
                StyleConstants.setFontSize(s, font.getSize());
                StyleConstants.setItalic(s, font.isItalic());
                StyleConstants.setBold(s, font.isBold());

                // One style for bold font
                s = addStyle("Bold-" + key, def);
                StyleConstants.setForeground(s, col);
                StyleConstants.setFontFamily(s, font.getName());
                StyleConstants.setFontSize(s, font.getSize());
                StyleConstants.setItalic(s, font.isItalic());
                StyleConstants.setBold(s, true);
            }
        }

        public void setFont(Font font) {
            for (Enumeration<String> e = _colors.keys(); e.hasMoreElements(); ) {
                Style s = this.getStyle(e.nextElement());
                StyleConstants.setFontFamily(s, font.getName());
                StyleConstants.setFontSize(s, font.getSize());
                StyleConstants.setItalic(s, font.isItalic());
                StyleConstants.setBold(s, font.isBold());
            }
        }
    }

    private class _MenuBar extends JMenuBar {

        private final JMenu _commandsMenu = new JMenu("Commands", false);

        private final JCheckBoxMenuItem _consoleItem;
        private final JCheckBoxMenuItem _favoritesItem;
        private final JCheckBoxMenuItem _pythonItem;

        public _MenuBar() {

            // FILE MENU
            JMenu fileMenu = new JMenu("File", false);
            this.add(fileMenu);

            fileMenu.add(getAction(
                    SETUP).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

            fileMenu.add(getAction(
                    CUSTOMIZE_ACTIONS).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));

            fileMenu.addSeparator();

            fileMenu.add(getAction(
                    CONNECT).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));

            fileMenu.add(getAction(
                    DISCONNECT).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));

            fileMenu.addSeparator();

            fileMenu.add(getAction(
                    EXIT).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));

            // COMMANDS MENU
            this.add(_commandsMenu);
            reloadCommandsMenu();

            // WINDOW MENU
            JMenu windowMenu = new JMenu("Window", false);
            this.add(windowMenu);

            windowMenu.add(getAction(
                    CASCADE).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));

            windowMenu.add(getAction(
                    TILE_HORZ).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));

            windowMenu.add(getAction(
                    TILE_VERT).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));

            windowMenu.addSeparator();

            _consoleItem = new JCheckBoxMenuItem("Show Console");
            _consoleItem.setState(true);
            windowMenu.add(_consoleItem).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
            _consoleItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setShowConsole(_consoleItem.getState());
                }
            });

            _favoritesItem = new JCheckBoxMenuItem("Show Favorites");
            _favoritesItem.setState(true);
            windowMenu.add(_favoritesItem).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
            _favoritesItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setShowFavorites(_favoritesItem.getState());
                }
            });

            _pythonItem = new JCheckBoxMenuItem("Show Python Console");
            _pythonItem.setState(false);
            windowMenu.add(_pythonItem).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
            _pythonItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setShowPython(_pythonItem.getState());
                }
            });

            // HELP MENU
            JMenu helpMenu = new JMenu("Help", false);
            this.add(helpMenu);

            helpMenu.add(getAction(
                    ABOUT).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        }

        //-----------------------------------------------------
        public void reloadCommandsMenu() {

            _commandsMenu.removeAll();

            _commandsMenu.add(getAction(
                    JOIN_CHANNEL).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK));

            _commandsMenu.add(getAction(
                    LIST_CHANNELS).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));

            _commandsMenu.add(getAction(
                    WHOIS).getActionObject()).setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));

            _commandsMenu.addSeparator();

            // Add custom menu items

            ChatOptions.ActionList customActions = _options.getCustomMenuActions();
            customActions.addActionsToMenu(_commandsMenu, null);

        }

        //-----------------------------------------------------
        public JCheckBoxMenuItem getPythonMenuItem() {
            return _pythonItem;
        }

        //-----------------------------------------------------
        public JCheckBoxMenuItem getConsoleMenuItem() {
            return _consoleItem;
        }

        //-----------------------------------------------------
        public JCheckBoxMenuItem getFavoritesMenuItem() {
            return _favoritesItem;
        }
    }

    private class _ToolBar extends JToolBar {

        public _ToolBar() {

            setFloatable(false);
            this.setMargin(new Insets(0, 0, 0, 0));

            createButton(getAction(SETUP).getActionObject()).setMargin(new Insets(3, 3, 3, 3));

            addSeparator();
            createButton(getAction(CONNECT).getActionObject()).setMargin(new Insets(3, 3, 3, 3));
            createButton(getAction(DISCONNECT).getActionObject()).setMargin(new Insets(3, 3, 3, 3));

            addSeparator();
            createButton(getAction(LIST_CHANNELS).getActionObject()).setMargin(new Insets(3, 3, 3, 3));
            createButton(getAction(JOIN_CHANNEL).getActionObject()).setMargin(new Insets(3, 3, 3, 3));

            addSeparator();
            createButton(getAction(CASCADE).getActionObject()).setMargin(new Insets(3, 3, 3, 3));
            createButton(getAction(TILE_HORZ).getActionObject()).setMargin(new Insets(3, 3, 3, 3));
            createButton(getAction(TILE_VERT).getActionObject()).setMargin(new Insets(3, 3, 3, 3));
        }

        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        private JButton createButton(Action action) {
            JButton button = this.add(action);
            String tip = (String) action.getValue(Action.SHORT_DESCRIPTION);
            button.setMargin(new Insets(1, 1, 1, 1));
            button.setToolTipText(tip);
            button.setText("");
            return button;
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _AboutAction extends ChatAction {

        public _AboutAction(Icon icon) {
            super("About...", icon);
        }

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new AboutDlg(ChatApp.this);
                }
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _CascadeAction extends ChatAction {

        public _CascadeAction(Icon icon) {
            super("Cascade", icon);
        }

        public void actionPerformed(ActionEvent e) {
            cascade();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _ConnectAction extends ChatAction {

        public _ConnectAction(Icon icon) {
            super("Connect", icon);
        }

        public void actionPerformed(ActionEvent e) {
            connect();
        }

        public void update() {
            setEnabled(!isConnected() && !isConnecting());
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _CustomizeActions extends ChatAction {

        public _CustomizeActions(Icon icon) {
            super("Customize Actions...", icon);
        }

        public void actionPerformed(ActionEvent e) {
            new CustomActionsDlg(ChatApp.this);
            _menuBar.reloadCommandsMenu();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _DisconnectAction extends ChatAction {

        public _DisconnectAction(Icon icon) {
            super("Disconnect", icon);
        }

        public void actionPerformed(ActionEvent e) {
            int ret = JOptionPane.showConfirmDialog(ChatApp.getChatApp(),
                    "Are you sure you want to disconnect?");

            if (ret == JOptionPane.YES_OPTION) { // or JOptionPane.OK_OPTION
                getServer().disconnect();
            }
        }

        public void update() {
            setEnabled(isConnected() || isConnecting());
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _ExitAction extends ChatAction {
        public _ExitAction(Icon icon) {
            super("Exit", icon);
        }

        public void actionPerformed(ActionEvent e) {
            closeApp();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _JoinChannelAction extends ChatAction {
        public _JoinChannelAction(Icon icon) {
            super("Join Channel...", icon);
        }

        public void actionPerformed(ActionEvent e) {
            showJoinChannelDlg();
        }

        public void update() {
            setEnabled(isConnected());
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _ListAction extends ChatAction {
        public _ListAction(Icon icon) {
            super("List Channels...", icon);
        }

        public void actionPerformed(ActionEvent e) {
            listChannels();
        }

        public void update() {
            setEnabled(isConnected());
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _ServerListAction extends ChatAction {

        public _ServerListAction(Icon icon) {
            super("Edit Server List...", icon);
        }

        public void actionPerformed(ActionEvent ae) {
            showServerListDlg();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _SetupAction extends ChatAction {

        public _SetupAction(Icon icon) {
            super("Setup...", icon);
        }

        public void actionPerformed(ActionEvent e) {
            Debug.println("setup");
            new ChatOptionsDlg(ChatApp.this);
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _TileHAction extends ChatAction {

        public _TileHAction(Icon icon) {
            super("Tile Horizontal", icon);
        }

        public void actionPerformed(ActionEvent ae) {
            tileHorizontal();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _TileVAction extends ChatAction {

        public _TileVAction(Icon icon) {
            super("Tile Vertical", icon);
        }

        public void actionPerformed(ActionEvent ae) {
            tileVertical();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    private class _WhoIsAction extends ChatAction {

        public _WhoIsAction(Icon icon) {
            super("WhoIs User", icon);
        }

        public void actionPerformed(ActionEvent ae) {
            Object value = getValue("user");
            if (value instanceof User) {
                sendWhoIs((User) value, true);
            } else {
                String userNick =
                        JOptionPane.showInputDialog("Enter nick name to query");
                sendWhoIs(userNick, true);
            }
        }

        public void update() {
            setEnabled(isConnected());
        }
    }
}

/*
================================================================================
01234567890123456789012345678901234567890123456789012345678901234567890123456789
--------------------------------------------------------------------------------
0         1         2         3         4         5         6         7
////////////////////////////////////////////////////////////////////////////////
*/

