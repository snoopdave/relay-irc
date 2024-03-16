//-----------------------------------------------------------------------------
// $RCSfile: TriggeredEditor.java,v $
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
 * Label wih a trigger button. 
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 *
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay IRC Chat Engine<br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong>
 * <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public abstract class TriggeredEditor extends PropSheetEditor {

   private _TriggeredEditor _editor = null;
   public JButton getButton() {return _editor.getButton();}

   //---------------------------------------------------------------------------
   public TriggeredEditor() {
      super();
   }
   //--------------------------------------------------------------------------
   public abstract void triggered();
   //---------------------------------------------------------------------------
   public boolean isPaintable() {
      return true;
   }
   //---------------------------------------------------------------------------
   public void paintValue(Graphics gfx, Rectangle rect) {
      // This seems to align perfectly with the TriggeredEditor's text
      gfx.drawString(getAsText(),0,(int)(rect.height*0.8));
   }
   //--------------------------------------------------------------------------
   public Component getCustomEditor() {
		if (_editor == null) {
		 _editor = new _TriggeredEditor();
		}
		_editor.setEnabled( isWritable() );

		if (getToolTipText() != null) {
		 _editor.setToolTipText(getToolTipText());
		}
		_editor.getLabel().setText(getAsText());
		return _editor;
   }
   //===========================================================================
   private class _TriggeredEditor extends JComponent {

      public JButton getButton() {return _button;}
      private JButton _button;

      public JLabel getLabel() {return _label;}
      private JLabel _label;

      public void setEnabled(boolean flag) {
		  getButton().setEnabled(flag);
      }
      public void setToolTipText(String text) {
		  getButton().setToolTipText(text);
		  getLabel().setToolTipText(text);
      }	

      public _TriggeredEditor() {

         setLayout(new BorderLayout());

         // Text label displays property as text
         _label = new JLabel();
         _label.setText(getAsText());
         _label.setBorder(null);
         add( _label, BorderLayout.CENTER );

		 _button = new JButton("...");
		 _button.setToolTipText("Click to edit list");
		 add( _button, BorderLayout.EAST );

         // Listen to trigger button
         _button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event) {
				triggered();
				_label.setText(getAsText());
            }
         });
      }
   }
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7         
*/

