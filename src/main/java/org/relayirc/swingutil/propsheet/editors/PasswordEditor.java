//-----------------------------------------------------------------------------
// $RCSfile: PasswordEditor.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------


package org.relayirc.swingutil.propsheet.editors;

import org.relayirc.swingutil.propsheet.PropSheetEditor;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

////////////////////////////////////////////////////////////////////////////////
/**
 * TODO: PropertyEditor that provides custom editor for editing a password. 
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
public class PasswordEditor extends PropSheetEditor {
   private _PasswordEditor _editor = null;

   //---------------------------------------------------------------------------
   public PasswordEditor() {
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

         _editor = new _PasswordEditor();
         _editor.getTextField().setText(getAsText());
		 _editor.setEnabled( isWritable() );
		 if (getToolTipText() != null) {
			 _editor.setToolTipText(getToolTipText());
		 }
      }
      return _editor;
   }
   //===========================================================================
   private class _PasswordEditor extends JComponent {

      private final JPasswordField _field;
      public JPasswordField getTextField() {return _field;}

      public void setToolTipText( String text) {
		  getTextField().setToolTipText(text);
      }
      public void setEnabled(boolean flag) {
		  getTextField().setEnabled(flag);
      }

      public _PasswordEditor() {

         _field = new JPasswordField();
         _field.setText(getAsText());
         _field.setBorder(null);
         setLayout(new BorderLayout());
         add( _field, BorderLayout.CENTER );

         // Listen for caret changes, update property on each such change 
         _field.addCaretListener(new CaretListener() {
			 public void caretUpdate( CaretEvent event) {
               //TextEditor.this.setAsText( _field.getPassword() );
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

