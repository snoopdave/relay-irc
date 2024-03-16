//-----------------------------------------------------------------------------
// $RCSfile: ChatAction.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui;

import javax.swing.*;

/** 
 * Makes it easier to implement actions. 
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
public abstract class ChatAction 
   extends AbstractAction implements IChatAction {

   private Object _context = null;

   //------------------------------------------------------------------------
   public ChatAction(String desc, Icon icon) {
      super(desc,icon);
      setEnabled(true);
      putValue(Action.SHORT_DESCRIPTION,desc);
   }
   //--------------------------------------------------------------------------
   /** Set the context for the action. */
   public Object getContext() {
      return _context;
   }

   //--------------------------------------------------------------------------
   /** Set the context for the action. */
   public void setContext(Object context) {
      _context = context;
   }
   //------------------------------------------------------------------------
   public AbstractAction getActionObject() {
      return this;
   }
   //------------------------------------------------------------------------
   public void update() {
      setEnabled(true);
   }
}

