//-----------------------------------------------------------------------------
// $RCSfile: FavoritesFrame.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

///////////////////////////////////////////////////////////////////////
/**
 * MDI client frame that holds FavoritesPanel.
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
public class FavoritesFrame extends JInternalFrame implements MDIClientFrame {
   FavoritesPanel _panel;

	public FavoritesFrame(ChatApp app) {
      super("Favorites",true,true,true,true);
      getContentPane().setLayout(new BorderLayout());
      setFrameIcon(IconManager.getIcon("Favorite"));
      _panel = new FavoritesPanel(app);
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
