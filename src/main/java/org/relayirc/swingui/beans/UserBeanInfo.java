//-----------------------------------------------------------------------------
// $RCSfile: UserBeanInfo.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 04:07:25 $
//-----------------------------------------------------------------------------


package org.relayirc.swingui.beans;

import java.beans.*;
import org.relayirc.util.*;
import org.relayirc.chatengine.*;
import org.relayirc.swingutil.propsheet.*;

//////////////////////////////////////////////////////////////////////////

/**
 * BeanInfo for org.relayirc.chatengine.User class. Exposes only those 
 * properties that should be displayed the user's property dialog. 
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
public class UserBeanInfo extends SimpleBeanInfo {

   private final PropertyDescriptor[] _descriptors;

   /** Collection of property names and display names. */
   private static final String[][] _propNames = {
      {"nick","Nick Name"},
      {"hostName","User Host Name"},
      {"userName","User Name"},
      {"fullName","Full Name"},
      {"serverName","Server Host Name"},
      {"serverDesc","Server Description"},
      {"channels","Channels"},
      {"online","Online"},
      {"signonTime","Sign-on Time"},
      {"updateTime","Time of last update"},
      {"idleTime","Idle Time"}
      //{"favorite","Favorite"}
   };

   /** Construct BeanInfo by creating array of property descriptors. */
   public UserBeanInfo() {
      _descriptors = new PropertyDescriptor[_propNames.length]; 
      try {
         for (int i=0; i<_propNames.length; i++) {
            PropertyDescriptor desc;
            desc = new PropertyDescriptor(_propNames[i][0],User.class);
            desc.setDisplayName(_propNames[i][1]);
            _descriptors[i] = desc; 
         }
      }
      catch (Exception e) {
         Debug.println("<"+e+"> in UserBeanInfo ctor");
      }
   }

   /** Return array of property descriptors. */ 
   public PropertyDescriptor[] getPropertyDescriptors() {
      return _descriptors;
   }
}

