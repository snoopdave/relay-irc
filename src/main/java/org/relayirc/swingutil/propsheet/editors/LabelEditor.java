//-----------------------------------------------------------------------------
// $RCSfile: LabelEditor.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil.propsheet.editors;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyEditorSupport;

////////////////////////////////////////////////////////////////////////////////
/**
 * PropertyEditor that provides custom editor for a read-only string value.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay IRC Chat Engine<br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong> No contributors to this file
 * <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class LabelEditor extends PropertyEditorSupport {
   private JLabel _label = null; 

   //---------------------------------------------------------------------------
   public LabelEditor() {
      super();
   }
   //---------------------------------------------------------------------------
   public boolean isPaintable() {
      return true;
   }
   //---------------------------------------------------------------------------
   public void paintValue(Graphics gfx, Rectangle rect) {
      // This seems to align perfectly with the TextEditor's text
      gfx.drawString(getAsText(),0,(int)(rect.height*0.8));
   }
   //---------------------------------------------------------------------------
   public Component getCustomEditor() {
      if (_label == null) {
         _label = new JLabel();
      }
      _label.setText(getAsText());
      return _label; 
   }
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7         
*/

