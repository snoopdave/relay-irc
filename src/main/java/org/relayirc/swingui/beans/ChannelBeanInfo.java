//-----------------------------------------------------------------------------
// $RCSfile: ChannelBeanInfo.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 04:07:25 $
//-----------------------------------------------------------------------------

package org.relayirc.swingui.beans;

import org.relayirc.chatengine.Channel;
import org.relayirc.util.Debug;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

//////////////////////////////////////////////////////////////////////////

/**
 * BeanInfo for org.relayirc.chatengine.Channel class. Exposes only those
 * properties that should be displayed the channel's property dialog.
 *
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
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class ChannelBeanInfo extends SimpleBeanInfo {

    /**
     * Collection of property names and display names.
     */
    private static final String[][] _propNames = {
            {"name", "Name"},
            {"topic", "Topic"},
            {"userCount", "User Count"},
            {"connected", "Connected"}
    };
    private final PropertyDescriptor[] _descriptors;

    /**
     * Construct BeanInfo by creating array of property descriptors.
     */
    public ChannelBeanInfo() {
        _descriptors = new PropertyDescriptor[_propNames.length];
        try {
            for (int i = 0; i < _propNames.length; i++) {
                PropertyDescriptor desc;
                desc = new PropertyDescriptor(_propNames[i][0], Channel.class);
                desc.setDisplayName(_propNames[i][1]);
                _descriptors[i] = desc;
            }
        } catch (Exception e) {
            Debug.println("<" + e + "> in ChannelBeanInfo ctor");
        }
    }

    /**
     * Return array of property descriptors.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return _descriptors;
    }
}

