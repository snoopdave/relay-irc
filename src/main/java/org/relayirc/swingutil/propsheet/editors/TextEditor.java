//-----------------------------------------------------------------------------
// $RCSfile: TextEditor.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------


package org.relayirc.swingutil.propsheet.editors;

import org.relayirc.swingutil.propsheet.*;
import org.relayirc.util.Debug;

import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

////////////////////////////////////////////////////////////////////////////////
/**
 * PropertyEditor that provides custom editor for editing a string value. 
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
public class TextEditor extends PropSheetEditor {
   private _TextEditor _editor = null;

   //---------------------------------------------------------------------------
   public TextEditor() {
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
      if (_editor == null) {
         _editor = new _TextEditor();
         _editor.getTextField().setText(getAsText());
		 _editor.setEnabled( isWritable() );
		 if (getToolTipText() != null) {
			 _editor.setToolTipText(getToolTipText());
		 }
      }
      return _editor;
   }
   //===========================================================================
   private class _TextEditor extends JComponent {

      private final JTextField _field;
      public JTextField getTextField() {return _field;}

      public void setEnabled(boolean flag) {
		  getTextField().setEnabled(flag);
      }
      public void setToolTipText( String text) {
		  getTextField().setToolTipText(text);
	  }

      public _TextEditor() {

         _field = new JTextField();
         _field.setText(getAsText());
         _field.setBorder(null);
         setLayout(new BorderLayout());
         add( _field, BorderLayout.CENTER );

         // Listen for caret changes, update property on each such change 
         _field.addCaretListener(new CaretListener() {
			 public void caretUpdate( CaretEvent event) {
               TextEditor.this.setAsText( _field.getText() );
			 }
		 });

         // Listen for focus changes, select whole string on focus gain
         _field.addFocusListener(new FocusListener() {
            public void focusGained( FocusEvent event ) {
               _field.setText(getAsText());
               _field.selectAll(); 
            }
            public void focusLost( FocusEvent ignored ) {}
         });
      }
   }
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7         
*/

