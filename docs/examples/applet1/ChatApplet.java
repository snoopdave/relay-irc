
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.Vector;

import org.relayirc.core.*;

//////////////////////////////////////////////////////////////////////////////

public class ChatApplet extends Applet {

   private boolean    _isStandalone = false;    // Standalone = not run as applet
   private String     _hostName = null;         // IRC server host name
   private int        _port = 6667;             // IRC server port number
   private String     _channel = "#default";    // Channel to join
   private Vector     _channels = new Vector(); // Available channels

   private ChatPanel  _chatPanel = new ChatPanel();
   private LoginPanel _loginPanel = new LoginPanel();
   private CardLayout _cardLayout = new CardLayout();

   //-----------------------------------------------------------------------
   // Construct the applet
   public ChatApplet() {
   }

   //-----------------------------------------------------------------------
   public void login(String nick, String channel) {
      _cardLayout.next(this);

      // If we are running as an applet then we can only connect to the 
      // web server host from which we originated. 
      if (_isStandalone == false) {
         // Use applet code base to get the web server's host name 
         _hostName = getCodeBase().getHost();
      }

      _chatPanel.connect(_hostName,_port,channel,nick,nick,nick,"Relay-AWT User");
   }

   //-----------------------------------------------------------------------
   public Vector getChannels() {
      return _channels;
   }
   //-----------------------------------------------------------------------
   // Get a parameter value
   public String getParameter(String key, String def) {
      return _isStandalone ? System.getProperty(key, def) :
         (getParameter(key) != null ? getParameter(key) : def);
   }

   //-----------------------------------------------------------------------
   // Initialize the applet
   public void init() {

      // Parse applet parameters
      try { _port = Integer.parseInt(this.getParameter("port", "6667")); }
      catch(Exception e) { e.printStackTrace(); }

      try { _channel = this.getParameter("channel", "#relay"); }
      catch(Exception e) { e.printStackTrace(); }

      // Get channel parameter and then channel0 through channel50
      for (int i=-1; i<50; i++) {
         String chan = null;
         try {
            if (i==-1) {
               // First, look for channel with no number
               chan = getParameter("channel",null);
            }
            else {
               // Then look for channel0, channel1, channel2, etc.
               chan = getParameter("channel"+i, null);
            }
            // Stop looking as soon as one is missing, but allow
            // channel0 to be missing since users might start with
            // channel1 instead of channel0.
            if (i>0 && chan == null) {
               break;
            }
            else if (chan!=null) {
               _channels.addElement(chan);
            }
         }
         catch(Exception e) {
            e.printStackTrace();
         }
      }

      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }

   //-----------------------------------------------------------------------
   // Component initialization
   private void jbInit() throws Exception {

      _loginPanel = new LoginPanel(this);
      _chatPanel = new ChatPanel(this);

      setLayout(_cardLayout);
      add("LoginPanel",_loginPanel);
      add("ChatPanel",_chatPanel);

      _cardLayout.show(this,"LoginPanel");
   }

   //-----------------------------------------------------------------------
   // Start the applet
   public void start() {
   }

   //-----------------------------------------------------------------------
   // Stop the applet
   public void stop() {
   }

   //-----------------------------------------------------------------------
   // Destroy the applet
   public void destroy() {
   }

   //-----------------------------------------------------------------------
   // Get Applet information
   public String getAppletInfo() {
      return "Applet Information";
   }

   //-----------------------------------------------------------------------
   // Get parameter info
   public String[][] getParameterInfo() {
      String[][] pinfo =
         {
         {"hostname", "String", "Host name of IRC server"},
         {"port",     "int",    "Port number on host"},
         {"channel",  "String", "Channel "},
         };
      return pinfo;
   }

   //-----------------------------------------------------------------------
   // Main method
   public static void main(String[] args) {
      ChatApplet applet = new ChatApplet();
      applet._isStandalone = true;
      Frame frame;
      frame = new Frame() {

         protected void processWindowEvent(WindowEvent e) {
            super.processWindowEvent(e);
            if (e.getID() == WindowEvent.WINDOW_CLOSING) {
               System.exit(0);
            }
         }

         public synchronized void setTitle(String title) {
            super.setTitle(title);
            enableEvents(AWTEvent.WINDOW_EVENT_MASK);
         }
      };
      frame.setTitle("Applet Frame");
      frame.add(applet, BorderLayout.CENTER);
      applet.init();
      applet.start();
      frame.setSize(400,320);
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      frame.setLocation( (d.width - frame.getSize().width) / 2,
                         (d.height - frame.getSize().height) / 2);
      frame.setVisible(true);
   }
}

