//-----------------------------------------------------------------------------
// $RCSfile: ChatPanel.java,v $
// $Revision: 1.1.2.3 $
// $Author: snoopdave $
// $Date: 2001/04/08 22:44:16 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.Server;
import org.relayirc.swingutil.MDIClientPanel;
import org.relayirc.swingutil.MDIPanel;
import org.relayirc.util.Debug;
import org.relayirc.util.ParsedToken;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

///////////////////////////////////////////////////////////////////////
/**
 * A panel which provides a text pane for the (color coded) display of
 * messages from the IRC server and, below that, a text field where the
 * user may type in messages to be delivered to the IRC server.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.3 $
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
public class ChatPanel extends JPanel implements MDIClientPanel {

   protected ChatOptions _options = null;
   protected Channel     _channel = null;
   protected Container   _container = null;
   private JScrollPane   _scrollpane = null;
   private JTextPane     _display = null;
   private final StyleContext  _styles = new StyleContext();
   private JTextField    _input = null;
   private int           _adjustsb=0;
   private String        _dockState = MDIPanel.DOCK_NONE;
   private final boolean       _isConsole = false;

   //------------------------------------------------------------------
   /** For chat console */
   ChatPanel(Container container) {
      this(null,container);
   }

   //------------------------------------------------------------------
   /** For chat channel */
   ChatPanel(Channel chan, Container container) {
      _channel = chan;
      _options = ChatApp.getChatApp().getOptions();
      _container = container;

      setLayout(new BorderLayout());

      // Add scrollable text pane for display of incoming messages
      _display = new JTextPane();
      _display.setDocument(new DefaultStyledDocument());
      _display.setEditable(false);
      _display.setBackground(Color.white);
      _scrollpane = new JScrollPane(_display,
         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
      add(_scrollpane,BorderLayout.CENTER);

      // Add text field for command/message entry
      add( _input = new JTextField(),BorderLayout.SOUTH);

      try {
         ChatOptions opt = ChatApp.getChatApp().getOptions();
         String fname = opt.getProperty("gui.channel.font.name");
         int fstyle = Integer.parseInt(
            opt.getProperty("gui.channel.font.style"));
         int fsize = Integer.parseInt(opt.getProperty("gui.channel.font.size"));
         _input.setFont(new Font(fname,fstyle,fsize));
      }
      catch (Exception e) {
         Debug.printStackTrace(e);
      }

      // Listen for the sound of the user entering test into the
      // input text area on the bottom of the chat panel.
      _input.addActionListener( new ActionListener() {

         public void actionPerformed( ActionEvent e ) {

            // TODO: This is ugly. Maybe it should be moved into a
            // processUserInput method within the server object? Maybe it
            // should be broken up and moved down into ChannelPanel and
            // ConsolePanel classes?

            Server server = ChatApp.getChatApp().getServer();
            String input = _input.getText();

			if (input.startsWith("/msg")) {

				ParsedToken[] tokens = 
					ParsedToken.stringToParsedTokens(input," "); 

				if (tokens.length > 2) {
					ChatApp.getChatApp().getServer().sendPrivateMessage( 
						tokens[1].token, input.substring(tokens[2].index));
				}
			}
				
            else if (_channel != null) {

               // A non-null channel means we are in a channel window

               if (!input.startsWith("/")) {

                  // User entered a regular old message
                  _channel.sendPrivMsg(_input.getText());
                  print(
                     ChatApp.getChatApp().getServer().getNick()+"> ","Bold-"
                     +_options.getProperty("gui.channel.color.messages"));

                  println(_input.getText(),
                      _options.getProperty("gui.channel.color.messages"));
               }
               else if (input.length()>2
                  && input.toLowerCase().startsWith("/me")) {

                  // User entered /me action command
                  _channel.sendAction(input.substring(3));
                  println("* "+ChatApp.getChatApp().getServer().getNick()
                     +" "+input.substring(3).trim(),
                     _options.getProperty("gui.channel.color.actions"));
               }
               else if (input.toLowerCase().startsWith("/part")) {

                  // User entered part command
                  _channel.sendPart();
               }
               else if (input.length()>4
                  && input.toLowerCase().startsWith("/nick")) {

                  // User entered nick command
                  server.sendNick(input.substring(5).trim());
               }
               else {
                  Debug.println("Ignoring unrecognized command ["+input+"]");
               }
            }

            else if (input.startsWith("/")) {

               // In a console window, there is no channel

               if (input.length()>4
                  && input.toLowerCase().startsWith("/quit")) {

                  // User entered quit command
                  server.sendQuit( input.substring(5).trim() );
               }
               else if (input.length()>4
                  && input.toLowerCase().startsWith("/nick")) {

                  // User entered nick command
                  server.sendNick(input.substring(5).trim());
               }
               else if (input.length() > 1) {
                  // User entered whatever... send it on
                  server.sendCommand( input.substring(1) );
               }
            }

            _input.setText("");
            _adjustsb = 5;
         }
      });

      // Listen for any scroll bar movement
      _scrollpane.getVerticalScrollBar().getModel().addChangeListener(
         new ChangeListener() {
         public synchronized void stateChanged( ChangeEvent ec ) {

            // Force scrollpane to bottom of display, Part 3/3
            if (_adjustsb-- > 0) {
               JScrollBar sb = _scrollpane.getVerticalScrollBar();
               int max = sb.getMaximum();
               int ext = sb.getModel().getExtent();
               sb.setValue(max-ext);
               //RCTest.println("listener: setting scrollbar to "
					   //+(max-ext)+" result "+sb.getValue());
            }
         }
      });
   }
   //------------------------------------------------------------------
   public void addMouseListener(MouseListener listener) {
      _display.addMouseListener(listener);
   }
   //------------------------------------------------------------------
   public void removeMouseListener(MouseListener listener) {
      _display.removeMouseListener(listener);
   }
   //------------------------------------------------------------------
   public void println(String txt, String style) {
      print(txt+"\n",style);
   }
   //------------------------------------------------------------------
   public void print(String txt, String style) {

      Document doc = _display.getDocument();
      try {
         doc.insertString(doc.getLength(),txt,ChatApp.getChatStyle(style));
      }
      catch (Exception e) {}

      // Force scrollpane to bottom of display, Part 1/3
		JScrollBar sb = _scrollpane.getVerticalScrollBar();
      int max = sb.getMaximum();
      int ext = sb.getModel().getExtent();
      sb.setValue(max-ext);

      // Force scrollpane to bottom of display, Part 2/3
      _adjustsb = 5;
   }
   //------------------------------------------------------------------
   public void shutdown() {
      _container.remove(this);
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

