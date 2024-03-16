// ErrorDlg.java   
package org.relayirc.swingutil;

import org.relayirc.util.Debug;

import java.awt.*;
import java.awt.event.*;
import java.io.*; 
import java.net.*; 
import java.util.*;                    
import java.applet.*;
                    
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////
/**
 * A simple error/exception display dialog.
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
public class ErrorUtils {

   private static boolean showDlg = true;
  
	//------------------------------------------------------------------
   /** Error message for an Exception object. */
   public static void displayErrorMessage( JFrame frame, 
   						String title, String shortDesc, Throwable ex) {

      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      ex.printStackTrace(printWriter);

      if (showDlg) 
         new ErrorDlg( frame, title, shortDesc, stringWriter.toString());
      else { 
         Debug.println("EXCEPTION: "+shortDesc);
         Debug.println("EXCEPTION: "+ stringWriter);
      }
   }

	//------------------------------------------------------------------
   /** Error message that shows short and long description strings. */
   public static void displayErrorMessage( JFrame frame, 
   						String title, String shortDesc, String longDesc) {
      if (showDlg) 
         new ErrorDlg( frame, title, shortDesc, longDesc);
      else 
         Debug.println(shortDesc+": "+longDesc);
   }
   
	//------------------------------------------------------------------
   /** Error message that shows only a short description string. */
   public static void displayErrorMessage( JFrame frame, 
   						String title, String shortDesc) {
      if (showDlg) 
         new ErrorDlg(frame, title, shortDesc);
      else 
         Debug.println(shortDesc);
   }
   
	//------------------------------------------------------------------
   /** A static method enable the dialog for all ErrorDlg instances */
   static public void setUseDialog(boolean usedlg) {
      showDlg=usedlg;
   }
}

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

class ErrorDlg extends JDialog {
   private final JButton     _okBtn = new JButton("OK");
   private String      _shortDesc = null;
   private String      _longDesc = null;

   //----------------------------------------------------------------------
   public ErrorDlg(JFrame parent, String title, String shortDesc) {
      super(parent,title,true);
      _shortDesc = shortDesc;
      initGUI();
   }

   //----------------------------------------------------------------------
   public ErrorDlg( JFrame parent, String title, String shortDesc, 
   					String longDesc) {
      super(parent,title,true);
      _shortDesc = shortDesc;
      _longDesc = longDesc;
      initGUI();
   }
      
   //----------------------------------------------------------------------
   public void initGUI() {
      JPanel southPanel = new JPanel();
      southPanel.add(_okBtn);
      getContentPane().add(southPanel, BorderLayout.SOUTH);

      if (_longDesc!=null) {
         JTabbedPane tabPane = new JTabbedPane();
         tabPane.addTab("Description",new TextPanel());
         tabPane.addTab("Details",new DetailsPanel());
         getContentPane().add(tabPane, BorderLayout.CENTER);
      }
      else {
         getContentPane().add(new TextPanel(), BorderLayout.CENTER);
      }
      
      _okBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            dispose();
         }
      });
      
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            dispose();
         }
      });

      getRootPane().setDefaultButton(_okBtn);
      
      setSize(new Dimension(350, 200)); 
      centerOnScreen(this);       
      show();
   }
   //----------------------------------------------------------------
   public static void centerOnScreen(Component obj) {
      Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
      obj.setLocation(
         (ssize.width/2) - (obj.getSize().width/2),
         (ssize.height/2) - (obj.getSize().height/2) );
   }

   //----------------------------------------------------------------------
   /** Explains the error. */
   private class TextPanel extends JPanel {
      public TextPanel() {

			JTextArea explain = new JTextArea(_shortDesc);
			explain.setFont(getFont());
			explain.setBorder(new EmptyBorder(15, 2, 5, 15));
			explain.setWrapStyleWord(true);
			explain.setEditable(false);
			explain.setLineWrap(true);
			explain.setOpaque(false);

         setLayout(new BorderLayout());
         add(new JScrollPane(explain),BorderLayout.CENTER);
      }
   }

   //----------------------------------------------------------------------
   /** Shows the exception. */
   private class DetailsPanel extends JPanel {
      public DetailsPanel() {

         JEditorPane htmlpane = new JEditorPane("text/html",_longDesc);
         htmlpane.setEditable(false);
        
         setLayout(new BorderLayout());
         add(new JScrollPane(htmlpane),BorderLayout.CENTER);
      }
   }
}
