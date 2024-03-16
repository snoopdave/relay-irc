//-----------------------------------------------------------------------------
// $RCSfile: JoinDlg.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.StandardDlg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

//////////////////////////////////////////////////////////////////////////////
/**
 * Displays a JoinPanel.
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
public class JoinDlg extends JDialog {
   JoinPanel _joinPanel = new JoinPanel();

   private boolean _isOk = false;
   public boolean isOk() {return _isOk;}


   //-------------------------------------------------------------------------
   public JoinDlg() {
      this(null);
   }
   //-------------------------------------------------------------------------
   public JoinDlg(Frame parent) {
      super(parent,"Join Channel",true);

      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }

      // Make it 10% larger than tightly packed
      pack();
      /*setSize(
         (int)(getSize().getWidth()*1.1),
         (int)(getSize().getHeight()*1.1));*/

      StandardDlg.centerOnScreen(this);
      setVisible(true);
   }
   //-------------------------------------------------------------------------
   public void onJoinButtonPressed() {
      if (_joinPanel.checkValues()) {
         _isOk = true;
         _joinPanel.saveValues();
         setVisible(false);
         dispose();
      }
   }
   //-------------------------------------------------------------------------
   public String getSelectedChannel() {
       return _joinPanel.getSelectedChannel();
   }
   //-------------------------------------------------------------------------
   private void jbInit() throws Exception {
      getContentPane().add(_joinPanel,BorderLayout.CENTER);

      // Hitting the join button is the same as hitting OK
      _joinPanel.getJoinButton().addActionListener( new ActionListener() {
         public void actionPerformed( ActionEvent e ) {
            onJoinButtonPressed();
         }
      });
      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            _isOk = false;
            setVisible(false);
            dispose();
         }
      });
   }
   //-------------------------------------------------------------------------
   /** For testing only. */
   public static void main(String[] args) {

      /*try {
         Debug.setDebug(true);

         final ChatOptions options = new ChatOptions();
         final Server server = new Server(
            "dummy-hostname",6667,"dummy_network","dummy_title");
         options.setServer(server);

         JoinDlg frame = new JoinDlg(null,options);
         frame.pack();
         StandardDlg.centerOnScreen(frame);
         frame.setVisible(true);
      }
      catch (Exception e) {
         e.printStackTrace();
      }*/
   }
}


