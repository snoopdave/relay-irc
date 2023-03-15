//-----------------------------------------------------------------------------
// $RCSfile: PropSheetEditor.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:54:15 $
//-----------------------------------------------------------------------------


package org.relayirc.swingutil.propsheet;

import org.relayirc.util.Debug;

import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

////////////////////////////////////////////////////////////////////////////////
/**
 * Base class for property editors 
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
public class PropSheetEditor extends PropertyEditorSupport {

	protected boolean _isWritable = true;
	protected String _toolTipText = null;

	public boolean isWritable() {return _isWritable;}
	public void setWritable( boolean iswritable ) {_isWritable=iswritable;}

	public String getToolTipText() {return _toolTipText;}
	public void setToolTipText( String text ) {_toolTipText=text;}
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7         
*/

