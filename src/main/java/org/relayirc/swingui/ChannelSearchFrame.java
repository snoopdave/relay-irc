//-----------------------------------------------------------------------------
// $RCSfile: ChannelSearchFrame.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import org.relayirc.chatengine.ChannelSearch;
import org.relayirc.swingutil.MDIClientFrame;
import org.relayirc.swingutil.MDIClientPanel;

import javax.swing.*;
import java.awt.*;

///////////////////////////////////////////////////////////////////////

/** 
 * Internal frame/MDI client frame that holds ChannelSeachPanel. 
 * @see org.relayirc.swingui.ChannelSearchPanel 
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
public class ChannelSearchFrame extends JInternalFrame implements MDIClientFrame {
   ChannelSearchPanel _panel;
    
	public ChannelSearchFrame(ChannelSearch search) {
      super("Channel Search",true,true,true,true);
      getContentPane().setLayout(new BorderLayout());
      _panel = new ChannelSearchPanel(search);
      getContentPane().add(_panel,BorderLayout.CENTER);
	}
   public MDIClientPanel getClientPanel() {
      return _panel;
   }
   public void setClientPanel(MDIClientPanel clientPanel) {
      //_panel = clientPanel;
   }
   public JInternalFrame getFrame() {
      return this;
   }
}
