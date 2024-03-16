//-----------------------------------------------------------------------------
// $RCSfile: DefaultPropModel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil.propsheet;

import org.relayirc.util.Debug;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Enumeration;
import java.util.Hashtable;

////////////////////////////////////////////////////////////////////////////////
/**
 * Default implementation that provides default editors for strings.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.2 $
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
public class DefaultPropModel implements IPropModel {
   private final Hashtable _values = new Hashtable();
   private final Hashtable _editors = new Hashtable();

   //---------------------------------------------------------------------------
   public DefaultPropModel() {
   }
   //---------------------------------------------------------------------------
   public int getPropertyCount() {
      return _values.size();
   }
   //---------------------------------------------------------------------------
   public Enumeration propertyNames() {
      return _values.keys();
   }
   //---------------------------------------------------------------------------
   public Object getProperty(String key) {
      return _values.get(key);
   } 
   //---------------------------------------------------------------------------
   public String getPropertyDisplayName(String key) {
      return key; 
   } 
   //---------------------------------------------------------------------------
   public Object setProperty(String key, Object value) {
      if (key != null && value != null) {
         return _values.put(key,value);
      } 
      else {
         Debug.println("DefaultPropModel.setProperty: null propname or value");
         return null;
      }
   }
   //---------------------------------------------------------------------------
   public void setEditor(String propName, PropertyEditor editor) {
      if (propName != null && editor != null) {
         _editors.put(propName,editor);
      }
      else {
         Debug.println("DefaultPropModel.setEditor: null propname or editor");
      }
   }
   //---------------------------------------------------------------------------
   public PropertyEditor getEditor(String propName) { 
      PropertyEditor ret = null;

      // If editor has been set for this propName, then return that editor 
      if (_editors.get(propName) != null) {
         ret = (PropertyEditor)_editors.get(propName);
      }
      else {
         // Use a registered editor from PropertyEditorManager
         Object propVal = getProperty(propName);
         if (propVal != null) {
            ret = PropertyEditorManager.findEditor(propVal.getClass());
            if (ret != null) {
               setEditor(propName,ret);
            }
            else {
               Debug.println("DefaultPropModel.getEditor: can't find editor");
            }
         }
         else {
            Debug.println("DefaultPropModel.getEditor: can't find property");
         }
      }
      return ret;
   }
}


