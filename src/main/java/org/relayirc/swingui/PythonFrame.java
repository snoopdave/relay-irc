//-----------------------------------------------------------------------------
// $RCSfile: PythonFrame.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;
import org.relayirc.util.*;

import org.python.util.PythonInterpreter;
import org.python.core.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

///////////////////////////////////////////////////////////////////////
/**
 * An MDI client frame that holds a PythonPanel.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code: Relay-JFC Chat Client <br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class PythonFrame extends JInternalFrame implements MDIClientFrame {

   private _PythonPanel _pythonPanel = null; //new _PythonPanel();

   // Ugh, we need a PythonPanel that implements MDIClientPanel
   class _PythonPanel extends PythonPanel implements MDIClientPanel {
      public _PythonPanel(PythonInterpreter pi) {super(pi);}
      public String getDockState() {return MDIPanel.DOCK_NONE;}
      public void   setDockState(String dockState) {}
      public JPanel getPanel() {return _PythonPanel.this;}
   }

   //------------------------------------------------------------------
   public PythonFrame(PythonInterpreter interp) {
      // closable, maximizable, iconifiable, resizable
      super("Interactive JPython Console",true,true,true,true);

      _pythonPanel = new _PythonPanel(interp);

      setFrameIcon(IconManager.getIcon("Users"));

      // Despite this setting, this window gets closed
      // when the user hits the close button
      //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(_pythonPanel,BorderLayout.CENTER);

      // Listen for window close event
      addInternalFrameListener(new InternalFrameAdapter() {
         public void internalFrameClosed(InternalFrameEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  ChatApp.getChatApp().setShowPython(false);
               }
            });
         }
      });
      validate();
   }
   //------------------------------------------------------------------
   public MDIClientPanel getClientPanel() {
      return _pythonPanel;
   }
   //------------------------------------------------------------------
   public JInternalFrame getFrame() {
      return this;
   }
   //------------------------------------------------------------------
   public void setClientPanel(MDIClientPanel clientPanel) {
      // We can safely ignore this
   }
}

