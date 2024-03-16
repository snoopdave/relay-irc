
package org.relayirc.awtui;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.WindowEvent;

//////////////////////////////////////////////////////////////////////////////

public class ChatApplet extends Applet {
   boolean isStandalone = false;
   String _hostName;
   int    _port;
   String _channel;
   CardLayout _cardLayout = new CardLayout();

   //-----------------------------------------------------------------------
   //Construct the applet
   public ChatApplet() {
   }

   //-----------------------------------------------------------------------
   public void login(String nick, String channel) {
      _cardLayout.next(this);
   }

   //-----------------------------------------------------------------------
   //Get a parameter value
   public String getParameter(String key, String def) {
      return isStandalone ? System.getProperty(key, def) :
         (getParameter(key) != null ? getParameter(key) : def);
   }

   //-----------------------------------------------------------------------
   //Initialize the applet
   public void init() {

      // Parse applet parameters
      try { _hostName = this.getParameter("hostname", "localhost");}
      catch(Exception e) { e.printStackTrace(); }

      try { _port = Integer.parseInt(this.getParameter("port", "6667")); }
      catch(Exception e) { e.printStackTrace(); }

      try { _channel = this.getParameter("channel", "#relay"); }
      catch(Exception e) { e.printStackTrace(); }

      // Get channel0 through channel50
      for (int i=0; i<50; i++) {
         String chan = null;
         try { 
            chan = this.getParameter("channel"+i, null); 
            if (chan == null) break;
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
   //Component initialization
   private void jbInit() throws Exception {
      this.setLayout(_cardLayout);
      this.add("LoginPanel",new LoginPanel(this));
      this.add("ChatPanel",new ChatPanel());
      _cardLayout.show(this,"LoginPanel");
   }

   //-----------------------------------------------------------------------
   //Start the applet
   public void start() {
   }

   //-----------------------------------------------------------------------
   //Stop the applet
   public void stop() {
   }

   //-----------------------------------------------------------------------
   //Destroy the applet
   public void destroy() {
   }

   //-----------------------------------------------------------------------
   //Get Applet information
   public String getAppletInfo() {
      return "Applet Information";
   }

   //-----------------------------------------------------------------------
   //Get parameter info
   public String[][] getParameterInfo() {
      String[][] pinfo =
         {
         {"hostname", "String", "Host name of IRC server"},
         {"port", "int", "Port number on host"},
         {"channel", "String", "Channel "},
         };
      return pinfo;
   }

   //-----------------------------------------------------------------------
   //Main method
   public static void main(String[] args) {
      ChatApplet applet = new ChatApplet();
      applet.isStandalone = true;
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
