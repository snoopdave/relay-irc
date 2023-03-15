
import org.relayirc.core.*;
import org.relayirc.core.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/////////////////////////////////////////////////////////////////////////////
/**
 * A single-server, single-channel IRC chat client in an Java 1.1 compatible
 * AWT panel. To be as light as possible, this class relies only upon the
 * IRCConnection and IRCConnectionAdapter classes and carefully avoids the
 * rest of the chat engine architecture.
 *
 * @author David M. Johnson
 * @verion $Revision: 1.1.2.1 $
 *
 * @see org.relayirc.core.IRCConnection
 * @see org.relayirc.core.IRCConnectionAdapter
 */
public class ChatPanel extends Panel {

   private IRCConnection _irc = null;
   private _IRCListener _listener = null;

   // JBuilder mess
   BorderLayout _mainLayout = new BorderLayout();
   Panel _messsagePanel = new Panel();
   TextArea _messageArea = new TextArea();
   TextField _messageField = new TextField();
   Button _sendButton = new Button();
   BorderLayout _messagePanelLayoutt = new BorderLayout();
   //List _userList = new List();

   //------------------------------------------------------------------------
   /** Construct a ChatPanel for a ChatApplet. */
   public ChatPanel(ChatApplet applet) {
      this();
      print(
          "Relay IRC Applet Example 0.1\n"
         +"Mozilla Public License (MPL) applies\n"
         +"http://relayirc.sourceforge.net\n"
         +"\n"
         +"READY\n"
         +"\n");
   }
   //------------------------------------------------------------------------
   /**
    * Only for design-time use in a GUI builder.
    * @deprecated Use ChatPanel(ChatApplet applet) constructor instead.
    */
   public ChatPanel() {
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }

   //------------------------------------------------------------------------
   /** Connect to specified. */
   public void connect(String server, int port, String channel,
      String nick, String altNick, String userName, String fullName) {

      _listener = new _IRCListener();
      _irc = new IRCConnection(server, port, nick, altNick, userName, fullName);
      _irc.setIRCConnectionListener(_listener);
      _irc.open();
   }

   //------------------------------------------------------------------------
   private void jbInit() throws Exception {
      this.setLayout(_mainLayout);
      //_messageField.setText("<enter message here>");
      _sendButton.setLabel("Send Message");
      _sendButton.addActionListener(new java.awt.event.ActionListener() {

         public void actionPerformed(ActionEvent e) {
            onSendButtonPressed(e);
         }
      });
      _messsagePanel.setLayout(_messagePanelLayoutt);
      this.add(_messsagePanel, BorderLayout.SOUTH);
      _messsagePanel.add(_messageField, BorderLayout.CENTER);
      _messsagePanel.add(_sendButton, BorderLayout.EAST);
      this.add(_messageArea, BorderLayout.CENTER);
      //this.add(_userList, BorderLayout.EAST);
   }

   //------------------------------------------------------------------------
   void onSendButtonPressed(ActionEvent e) {
   }

   //------------------------------------------------------------------------
   private void print(String text) {
      _messageArea.append(text);
   }

   //------------------------------------------------------------------------
   private void println(String text) {
      _messageArea.append(text+"\n");
   }

   //------------------------------------------------------------------------

   private class _IRCListener extends IRCConnectionAdapter {

      public void onAction( String user, String chan, String txt ) {
         println("* "+user+" "+txt);
      }
      public void onBan( String banned, String chan, String banner ) {
         println("# "+banned+" banned from "+chan+" by "+banner);
      }
      public void onConnect()  {
         println("onConnect");
      }
      public void onDisconnect() {
         println("onDisconnect");
      }
      public void onJoin( String user, String nick, String chan, boolean create ) {
         println("onJoin");
      }
      public void onJoins( String users, String chan) {
         println("onJoins");
      }
      public void onKick( String kicked, String chan, String kicker, String txt ) {
         println("onKick");
      }
      public void onMessage(String message) {
         println("onMessage");
      }
      public void onPrivateMessage(String orgnick, String chan, String txt) {
         println("onPrivateMessage");
      }
      public void onNick( String user, String oldnick, String newnick ) {
         println("onNick");
      }
      public void onNotice(String text) {
         println("onNotice");
      }
      public void onPart( String user, String nick, String chan ) {
         println("onPart");
      }
      public void onOp( String oper, String chan, String oped ) {
         println("onOp");
      }
      public void onParsingError(String message) {
         println("onParsingError");
      }
      public void onPing(String params) {
         println("onPing");
      }
      /** Display status in text area. */
      public void onStatus(String msg) {
         println("STATUS: "+msg);
      }
      public void onTopic(String chanName, String newTopic) {
         println("onTopic");
      }
      public void onVersionNotice(String orgnick, String origin, String version) {
         println("onVersionNotice");
      }
      public void onQuit(String user, String nick, String txt ) {
         println("onQuit");
      }
      public void onReplyVersion(String version) {
         println("onReplyVersion");
      }
      public void onReplyListUserChannels(int channelCount) {
         println("onReplyListUserChannels");
      }
      public void onReplyListStart() {
         println("onReplyListStart");
      }
      public void onReplyList(String channel, int userCount, String topic) {
         println("onReplyList");
      }
      public void onReplyListEnd() {
         println("onReplyListEnd");
      }
      public void onReplyListUserClient(String msg) {
         println("onReplyListUserClient");
      }
      public void onReplyWhoIsUser(String nick, String user, String name, String host) {
         println("onReplyWhoIsUser");
      }
      public void onReplyWhoIsServer(String nick, String server, String info) {
         println("onReplyWhoIsServer");
      }
      public void onReplyWhoIsOperator(String info) {
         println("onReplyWhoIsOperator");
      }
      public void onReplyWhoIsIdle(String nick, int idle, Date signon) {
         println("onReplyWhoIsIdle");
      }
      public void onReplyEndOfWhoIs(String nick) {
         println("onReplyEndOfWhoIs");
      }
      public void onReplyWhoIsChannels(String nick, String channels) {
         println("onReplyWhoIsChannels");
      }
      public void onReplyMOTDStart() {
         println("onReplyMOTDStart");
      }
      public void onReplyMOTD(String msg) {
         println("onReplyMOTD");
      }
      public void onReplyMOTDEnd() {
         println("onReplyMOTDEnd");
      }
      public void onReplyNameReply(String channel, String users) {
         println("onReplyNameReply");
      }
      public void onReplyTopic(String channel, String topic) {
         println("onReplyTopic");
      }
      public void onErrorNoMOTD() {
         println("onErrorNoMOTD");
      }
      public void onErrorNeedMoreParams() {
         println("onErrorNeedMoreParams");
      }
      public void onErrorNoNicknameGiven() {
         println("onErrorNoNicknameGiven");
      }
      public void onErrorNickNameInUse(String badNick) {
         println("onErrorNickNameInUse");
      }
      public void onErrorNickCollision(String badNick) {
         println("onErrorNickCollision");
      }
      public void onErrorErroneusNickname(String badNick) {
         println("onErrorErroneusNickname");
      }
      public void onErrorAlreadyRegistered() {
         println("onErrorAlreadyRegistered");
      }
      public void onErrorUnknown(String message) {
         println("onErrorUnknown");
      }
      public void onErrorUnsupported(String messag) {
         println("onErrorUnsupported");
      }
   }
}
