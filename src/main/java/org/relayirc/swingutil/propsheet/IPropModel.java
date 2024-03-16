//-----------------------------------------------------------------------------
// $RCSfile: IPropModel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------


package org.relayirc.swingutil.propsheet; 

import java.beans.*;
import java.awt.Component;
import java.util.Enumeration;

//////////////////////////////////////////////////////////////////////////

/**
 * Holder of properties and finder of property editors. The PropSheet 
 * component using this simple abstraction so that 1) it does not have to
 * deal with the complexity of the beans API and 2) so that objects that
 * are not beans may be edited. 
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
public interface IPropModel {

   int getPropertyCount();
   Enumeration propertyNames();

   String getPropertyDisplayName(String key);

   Object getProperty(String key);
   Object setProperty(String key, Object value);

   void setEditor(String propName, PropertyEditor editor);
   PropertyEditor getEditor(String propName);
}

