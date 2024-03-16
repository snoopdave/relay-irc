
/* 
 * FILE: GuiListCellRenderer.java 
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is Relay IRC chat client.
 * 
 * The Initial Developer of the Original Code is David M. Johnson.
 * Portions created by David M. Johnson are Copyright (C) 1998. 
 * All Rights Reserved.
 *
 * Contributor(s): No contributors to this file.
 */

package org.relayirc.swingutil;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/** 
 * GuiObject-aware implementation ListCellRenderer.
 * @see org.relayirc.swingutil.GuiObject
 */ 
public class GuiListCellRenderer extends JLabel implements ListCellRenderer {
   private final JComponent _component;

   public GuiListCellRenderer(JComponent component) {
      _component = component;
      setOpaque(true);
   }

   public Component getListCellRendererComponent(
       JList listbox, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if(UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
          setOpaque(index != -1);
      } 
      else {
         setOpaque(true);            
      } 

      if(value == null) {
         setText("");
         setIcon(null);
      } 
      else {
         if(isSelected) {
            if (_component instanceof JComboBox) {
               setBackground(UIManager.getColor("ComboBox.selectionBackground"));
               setForeground(UIManager.getColor("ComboBox.selectionForeground"));               
            }
            else {
               setBackground(UIManager.getColor("List.selectionBackground"));
               setForeground(UIManager.getColor("List.selectionForeground"));               
            }
         } 
         else {
            if (_component instanceof JComboBox) {
               setBackground(UIManager.getColor("ComboBox.background"));
               setForeground(UIManager.getColor("ComboBox.foreground"));
            }
            else {
               setBackground(UIManager.getColor("List.background"));
               setForeground(UIManager.getColor("List.foreground"));
            }
         }
         if (value instanceof GuiObject) setIcon(((GuiObject)value).getIcon());
         setText(value.toString());
      }
      return this;
   }
}
