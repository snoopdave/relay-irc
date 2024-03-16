//-----------------------------------------------------------------------------
// $RCSfile: ComboEditor.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------


package org.relayirc.swingutil.propsheet.editors; 

import org.relayirc.swingutil.GuiObject;
import org.relayirc.swingutil.propsheet.*;
import org.relayirc.util.Debug;

import java.util.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

////////////////////////////////////////////////////////////////////////////////
/**
 * PropertyEditor that provides custom editor for editing a string value
 * by selecting from a list of possible values.
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
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class ComboEditor extends PropSheetEditor {
   private _ComboEditor _editor;
   private GuiObject[] _tags = null;
   private Hashtable _namesByValues = new Hashtable();

   //---------------------------------------------------------------------------
   public ComboEditor() {
      super();
   }
   //---------------------------------------------------------------------------
   public boolean isPaintable() {
      return true;
   }
   //---------------------------------------------------------------------------
   public void paintValue(Graphics gfx, Rectangle rect) {

      String text = (String)_namesByValues.get( getAsText() );

      // This seems to align perfectly with the ComboEditor's text
      gfx.drawString(text,0,(int)(rect.height*0.8));
   }
   //---------------------------------------------------------------------------
   public void setTags(GuiObject[] tags) {
      _namesByValues = new Hashtable();
      _tags = tags;
      if (_editor != null) {
         _editor.getComboBox().setModel(new DefaultComboBoxModel(_tags));
      }
      for (int i=0; i<tags.length; i++) {
		  _namesByValues.put( _tags[i].getObject(), _tags[i].getString() );
	  }
   }
   //---------------------------------------------------------------------------
   public void setTags(String[] tags) {
		GuiObject[] tagos = new GuiObject[tags.length];
		for (int i=0; i<tags.length; i++) {
			tagos[i] = new GuiObject(tags[i],tags[i]);
		}
		setTags( tagos );
   }
   //---------------------------------------------------------------------------
   public Component getCustomEditor() {

      if (_editor == null) {

         _editor = new _ComboEditor();
         _editor.getComboBox().setSelectedItem(getAsText());
		 _editor.setEnabled( isWritable() );

		 if (getToolTipText() != null) {
			 _editor.setToolTipText(getToolTipText());
		 }

         if (_tags != null) {
            _editor.getComboBox().setModel(new DefaultComboBoxModel(_tags));
         }
      }
      return _editor;
   }
   //===========================================================================
   private class _ComboEditor extends JComponent {
      private final JComboBox _combo;

      public JComboBox getComboBox() {return _combo;}

      public void setEnabled(boolean flag) {
		  getComboBox().setEnabled(flag);
      }
      public void setToolTipText(String text) {
		  getComboBox().setToolTipText(text);
      }

      public _ComboEditor() {
         _combo = new JComboBox();
         setLayout( new BorderLayout() );
         add( _combo, BorderLayout.CENTER );

         _combo.addItemListener(new ItemListener() {
            public void itemStateChanged( ItemEvent event ) {
               Object item = _combo.getSelectedItem();
			   if (item != null && item instanceof GuiObject go) {
                   ComboEditor.this.setAsText( go.getObject().toString() );
               }
               else {
                  Debug.println("JComboBox listener: selected item is null");
               }
            } 
         });
      }
   }
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7         
*/
