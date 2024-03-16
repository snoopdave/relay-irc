//-----------------------------------------------------------------------------
// $RCSfile: ConsoleFrame.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.IconManager;
import org.relayirc.swingutil.MDIClientFrame;
import org.relayirc.swingutil.MDIClientPanel;
import org.relayirc.swingutil.MDIPanel;
import org.relayirc.util.Debug;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

///////////////////////////////////////////////////////////////////////

/**
 * Provides a display area for general messages from the IRC server.
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
public class ConsoleFrame extends JInternalFrame implements MDIClientFrame {
   private ChatPanel _chatPanel = null;

   public ChatPanel getChatPanel() {return _chatPanel;}

   public ConsoleFrame(ChatPanel consolePanel) {
      // closable, maximizable, iconifiable, resizable
      super("Console",true,true,true,true);
      setFrameIcon(IconManager.getIcon("Inform"));

      //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

      _chatPanel = consolePanel;
      _chatPanel.setDockState(MDIPanel.DOCK_BOTTOM);
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(_chatPanel,BorderLayout.CENTER);

      // Listen for popup menu clicks
      _chatPanel.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {showPopup(e);}
         public void mousePressed(MouseEvent e) {showPopup(e);}
         public void mouseReleased(MouseEvent e) {showPopup(e);}

         public void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {

               final JPopupMenu popup = new JPopupMenu();

               JMenuItem mi2 = new JMenuItem("Dock / Undock");
               mi2.addActionListener( new ActionListener() {
                  public void actionPerformed(ActionEvent ae) {

                     // Set new dock state
                     String dockState = _chatPanel.getDockState();
                     if (dockState.equals(MDIPanel.DOCK_NONE))
                        _chatPanel.setDockState(MDIPanel.DOCK_BOTTOM);
                     else
                        _chatPanel.setDockState(MDIPanel.DOCK_NONE);

                     // And register it with the MDIPanel
                     ChatApp.getChatApp().dock(_chatPanel);
                  }
               });
               popup.add(mi2);

               Point pt = SwingUtilities.convertPoint(
                  (Component)e.getSource(),
                  new Point(e.getX(),e.getY()),_chatPanel);

               popup.show(_chatPanel,pt.x,pt.y);
            }
         }
      });

      // Add listener so we can become invisible on close
      addInternalFrameListener( new InternalFrameAdapter() {
         public void internalFrameClosing(InternalFrameEvent e) {

            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  ChatApp.getChatApp().setShowConsole(false);
               }
            });

         }
      });
      try {setSelected(true);} catch (Exception e) {
         Debug.println("Internal Swing error");
         Debug.printStackTrace(e);
      }
   }

   public MDIClientPanel getClientPanel() {
      return _chatPanel;
   }

   public JInternalFrame getFrame() {
      return this;
   }
}

