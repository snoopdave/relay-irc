//----------------------------------------------------------------------------
// $RCSfile: Spinner.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 16:07:53 $
//----------------------------------------------------------------------------

package org.relayirc.swingutil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** 
 * Pong-style progress indicator with little box that bounces from left
 * to right leaving a trail.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.2 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay-JFC Chat Client<br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong> No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class Spinner extends JPanel {
   private Timer _timer = null;
   private final int _delay;

   private final ActionListener _timerListener = new ActionListener() {
      public void  actionPerformed(ActionEvent event) {
         repaint(5);
      }
   };

   //--------------------------------------------------------------------------
   public Spinner(int delay) {
      _delay = delay;
   }
   //--------------------------------------------------------------------------
   public boolean isRunning() {
      if (_timer != null) 
         return _timer.isRunning();
      else 
         return false;
   }
   //--------------------------------------------------------------------------
   public synchronized void stop() {
      if (_timer != null) {
         _timer.removeActionListener(_timerListener);
         _timer.stop();
         _timer = null;
         repaint(5);
      }
   }
   //--------------------------------------------------------------------------
   public synchronized void start() {
      if (_timer == null) {
         _timer = new Timer(20,_timerListener);
         _timer.setDelay(_delay);
         _timer.setRepeats(true);
         _timer.start();
      }
   }
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7         
*/

