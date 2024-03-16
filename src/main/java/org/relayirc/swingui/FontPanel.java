//-----------------------------------------------------------------------------
// $RCSfile: FontPanel.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.swingutil.FontChooser;
import org.relayirc.swingutil.ITab;
import org.relayirc.util.Debug;

import javax.swing.*;
import java.awt.*;

///////////////////////////////////////////////////////////////////////

/**
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
public class FontPanel extends JPanel implements ITab {
   BorderLayout borderLayout1 = new BorderLayout();
   FontChooser _fontChooser = new FontChooser();

   //---------------------------------------------------------------
   public FontPanel() {
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   //---------------------------------------------------------------
   public FontPanel(ChatOptions options) {
      try {
         jbInit();
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
   //---------------------------------------------------------------
   public boolean checkValues() {
      return true;
   }
   //---------------------------------------------------------------
   public void loadValues() {
      try {
         ChatOptions opt = ChatApp.getChatApp().getOptions();
         String fname = opt.getProperty("gui.channel.font.name");
         int fstyle = Integer.parseInt(
            opt.getProperty("gui.channel.font.style"));
         int fsize = Integer.parseInt(
            opt.getProperty("gui.channel.font.size"));
         _fontChooser.setSelectedFont(new Font(fname,fstyle,fsize));
      }
      catch (Exception e) {
         Debug.printStackTrace(e);
      }
   }
   //---------------------------------------------------------------
   public void saveValues() {
      Font selFont = _fontChooser.getSelectedFont();
      ChatOptions opt = ChatApp.getChatApp().getOptions();
      opt.setProperty("gui.channel.font.name",selFont.getName() );
      opt.setProperty("gui.channel.font.style",
         Integer.toString(selFont.getStyle()) );
      opt.setProperty("gui.channel.font.size",
         Integer.toString(selFont.getSize()) );
      ChatApp.setChatFont(selFont);
   }
   //---------------------------------------------------------------
   public String getName() {
      return "Font";
   }
   //---------------------------------------------------------------
   public void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      this.add(_fontChooser);
   }
}

