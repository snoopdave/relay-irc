//-----------------------------------------------------------------------------
// $RCSfile: BooleanEditor.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil.propsheet.editors;

import org.relayirc.swingutil.propsheet.PropSheet;

//////////////////////////////////////////////////////////////////////////
/**
 * PropertyEditor that provides custom editor for editing an integer.
 * @see PropSheet
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
               
public class BooleanEditor extends ComboEditor { 

   //---------------------------------------------------------------------
   public BooleanEditor() {
      super();
	  String[] booleanTags = { "true","false" };
	  setTags( booleanTags );
   }
   //---------------------------------------------------------------------
   public String getAsText() {
      return getValue().toString();
   }
   //---------------------------------------------------------------------
   public void setAsText(String txt) {
      setValue(Boolean.valueOf(txt));
   }
}

