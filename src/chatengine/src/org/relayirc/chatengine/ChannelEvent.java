//----------------------------------------------------------------------------
// $RCSfile: ChannelEvent.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/03/27 11:36:48 $
//----------------------------------------------------------------------------

package org.relayirc.chatengine;
import org.relayirc.util.*;

import java.util.EventObject;

///////////////////////////////////////////////////////////////////////
/**
 * Event fired by a channel. The source of a ChannelEvent is always
 * a Channel object.
 * @see Channel
 * @see ChannelListener
 * @author David M. Johnson
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code:     Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s):    No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class ChannelEvent extends EventObject {

   private String _originNick = null;
   private String _originAddress = null;
   private String _subjectNick = null;
   private String _subjectAddress = null;
   private String _newNick = null;
   private Object _value = null;

   //------------------------------------------------------------------
	/** Event with no associated values. */
   public ChannelEvent(Channel src) {
      super(src);
      Debug.println("ChannelEvent("+src+")");
   }
   //------------------------------------------------------------------
	/** Event with an optional arbitrary value. */
   public ChannelEvent(Channel src, Object value) {
      super(src);
      _value = value;
      Debug.println("ChannelEvent("+src+","+value);
   }
   //------------------------------------------------------------------
	/** Event with originating user and an optional arbitrary value. */
   public ChannelEvent(Channel src,
      String originNick, String originAddress, Object value) {

      super(src);
      _originNick = originNick;
      _originAddress = originAddress;
      _value = value;
      Debug.println("ChannelEvent("+src+","+originNick+","+value);
   }
   //------------------------------------------------------------------
	/** Event with originating user, destination user and an optional
	 * arbitrary value. */
   public ChannelEvent(Channel src,
      String originNick, String originAddress,
      String subjectNick, String subjectAddress, Object value) {

      this(src,originNick,originAddress,value);
      _subjectNick = subjectNick;
      _subjectAddress = subjectAddress;
      Debug.println("ChannelEvent("+src+","+originNick+","+subjectNick+","+value);
   }
   //------------------------------------------------------------------

	/** Nick name of the originating chat user or null if not applicable. */
   public String getOriginNick() {return _originNick;}

	/** Address of the originating chat user or null if not applicable. */
   public String getOriginAddress() {return _originAddress;}

	/** Nick name of the chat user or null if not applicable. */
   public String getSubjectNick() {return _subjectNick;}

	/** Address of the destination chat user or null if not applicable. */
   public String getSubjectAddress() {return _subjectAddress;}

	/** Arbitrary value associated with event. */
   public Object getValue() {return _value;}
}
