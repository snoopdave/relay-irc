//-----------------------------------------------------------------------------
// $RCSfile: TriggeredListSelectionEditor.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil.propsheet.editors;

import org.relayirc.swingutil.propsheet.*;
import org.relayirc.swingutil.ListSelectionEditorDlg;
import org.relayirc.swingutil.DefaultListEditorDlg;
import org.relayirc.util.Debug;

import java.util.Vector;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

////////////////////////////////////////////////////////////////////////////////
/**
 * Editor wih a trigger button that launches a simple list selection dialog.
 * @author David M Johnson
 * @version $Revision: 1.1.2.1 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay IRC Chat Engine<br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong>HAHT Commerce, Inc.
 * <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class TriggeredListSelectionEditor extends TriggeredEditor {

   Frame _parent;
   Vector _allValues;

   //--------------------------------------------------------------------------
   public TriggeredListSelectionEditor( Frame parent, Vector allValues ) {
      super();
	  _parent = parent;
	  _allValues = allValues;
   }
   //--------------------------------------------------------------------------
   public void triggered() {
      Vector list = (Vector)getValue();

      ListSelectionEditorDlg dlg = 
	     new ListSelectionEditorDlg( _parent, _allValues, list);

      if (dlg.isOk()) {
		  Debug.println("Setting prop value = "+dlg.getSelection());
	  	  setValue( dlg.getSelection() );
      }
   }
   //--------------------------------------------------------------------------
   public void setAsText( String str ) {
	   // We can ignore this 
   }
   //---------------------------------------------------------------------------
   public String getAsText() {
	   return getValue().toString();
   }
}

