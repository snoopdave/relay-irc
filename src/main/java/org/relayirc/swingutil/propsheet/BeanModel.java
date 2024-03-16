//-----------------------------------------------------------------------------
// $RCSfile: BeanModel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil.propsheet;

import org.relayirc.swingutil.propsheet.editors.LabelEditor;
import org.relayirc.util.Debug;

import java.beans.*;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * PropModel that wraps a bean. 
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
 public class BeanModel implements IPropModel {
   private final Object _bean;
   private final Hashtable _propDescs = new Hashtable();
   private final Hashtable _editors = new Hashtable();
   private final boolean _isReadOnly = true;

   //-----------------------------------------------------------------
   public BeanModel(Object bean) {
      _bean = bean;

      // Hash the bean's property descriptors by property name for fast access
      BeanInfo info = null;
      try {
         info = Introspector.getBeanInfo(bean.getClass());
         Debug.println(info.getClass().toString());
         PropertyDescriptor[] descs = info.getPropertyDescriptors();

         for (int i=0; i<descs.length; i++) {
            _propDescs.put(descs[i].getName(),descs[i]);
         }
      }
      catch (IntrospectionException e) {
      }
   }
   //-----------------------------------------------------------------
   public int getPropertyCount() {
      return _propDescs.size();
   }
   //-----------------------------------------------------------------
   public Enumeration propertyNames() {
      return _propDescs.keys();
   }
   //-----------------------------------------------------------------
   public String getPropertyDisplayName(String propName) {
      String ret = null; 
      try {
         PropertyDescriptor desc = (PropertyDescriptor)_propDescs.get(propName);
         ret = desc.getDisplayName();
      }
      catch (Exception e) {
         //Debug.println("("+e+") in BeanModel.getProperty()");
      }
      return ret != null ? ret : propName;
   }
   //-----------------------------------------------------------------
   public Object getProperty(String propName) {
      Object ret = null;
      try {
         PropertyDescriptor desc = (PropertyDescriptor)_propDescs.get(propName);
         ret = desc.getReadMethod().invoke(_bean,(Object[])null);
      }
      catch (Exception e) {
         //Debug.println("("+e+") in BeanModel.getProperty()");
      }
      return ret;
   }
   //-----------------------------------------------------------------
   public Object setProperty(String key, Object value) {
      Object prevVal = null;
      if (!_isReadOnly) {
         try {
            PropertyDescriptor desc = (PropertyDescriptor)_propDescs.get(key);
            Object[] values = new Object[1];
            values[0] = value;
            prevVal = desc.getWriteMethod().invoke(_bean,values);
         }
         catch (Exception e) {
            //Debug.println("("+e+") in BeanModel.setProperty()");
         }
      }
      return prevVal;
   }
   //-----------------------------------------------------------------
   public void setEditor(String propName, PropertyEditor editor) {
      _editors.put(propName,editor);
   }
   //-----------------------------------------------------------------
   public PropertyEditor getEditor(String propName) {
      PropertyEditor ret = null;

      if (!_isReadOnly) {

         // Look up property's editor in our editor collection 
         try {
            ret = (PropertyEditor)_editors.get(propName);
         }
         catch (Exception e) {
         }
   
         if (ret == null) {
            // Look up property's PropertyDescriptor, return its editor
            try {
               PropertyDescriptor desc = (PropertyDescriptor)_propDescs.get(propName);
               Class editorClass = desc.getPropertyEditorClass();
               ret = (PropertyEditor)editorClass.newInstance();
            }
            catch (Exception e) {
            }
         }
   
         if (ret==null) {
            // Use ProperyEditorManager to find editor for property
            try {
               Object propVal = getProperty(propName);
               ret = PropertyEditorManager.findEditor(propVal.getClass());
            }
            catch (Exception e) {
            }
         }
      }

      if (ret == null) {
         // Default to TextEditor
         //Debug.println("BeanModel.getEditor: returning LabelEditor for "+propName);
         ret = new LabelEditor();
      }

      return ret;
   }
   //-----------------------------------------------------------------
   public static void main(String[] args) {
      Debug.setDebug(true);
      Debug.println("BeanModel test method");
   }
}

