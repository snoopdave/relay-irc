//----------------------------------------------------------------------------
// $RCSfile: ChannelSearch.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/02/09 03:46:33 $
//----------------------------------------------------------------------------

package org.relayirc.chatengine;
import org.relayirc.util.*;

import java.util.Vector;

/** 
 * Channel searcher. Works closely with a Server to search for channels 
 * that match the search criteria, notify listeners of each such matching 
 * channel as it is found and build a collection of matching channels.
 * @author David M. Johnson.
 * @version $Revision: 1.1.2.1 $
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
public class ChannelSearch {

	private Server       _server = null;
	private String       _name = null;
   private int          _minUsers = Integer.MIN_VALUE;
   private int          _maxUsers = Integer.MAX_VALUE;
   private Vector       _results = new Vector();
   private boolean      _complete = false;
   private Vector       _listeners = new Vector();

   /** Channel search needs a server. */
   public ChannelSearch(Server server) {
      _server = server;
   }

   /** Get channel name search string. */ 
   public String getName() {return _name;}

   /** Set channel name search string. */ 
   public void setName(String name) {_name=name;}

   /** Get minimum user-count criteria. */
   public int getMinUsers() {return _minUsers;}

   /** Set minimum user-count criteria. */
   public void setMinUsers(int min) {_minUsers=min;}

   /** Get maximum user-count criteria. */
   public int getMaxUsers() {return _maxUsers;}
   
	/** Set maximum user-count criteria. */
   public void setMaxUsers(int min) {_maxUsers=min;}

   /** Internal use. */
   void setComplete(boolean complete) {_complete=complete;}

   /** True if seach has completed. */
   public boolean isComplete() {return _complete;}

   /** Number of channels found in most recent search, or -1 on error. */ 
   public int getChannelCount() {
	   if (_results == null) return -1;
	   return _results.size();
	}

   /** Number of channels found in most recent search, or null on error. */ 
   public Channel getChannelAt(int index) {
	   if (_results == null) return null;
      return (Channel)_results.elementAt(index);
   }

   /** Start the channel search with the current criteria. */
   public void start() {
      _results = new Vector();
      _server.startChannelSearch(this);
   }

   /** Add search listener. */
   public void addChannelSearchListener(ChannelSearchListener listener) {
      _listeners.addElement(listener);
   }

   /** Remove search listener. */
   public void removeChannelSearchListener(ChannelSearchListener listener) {
      _listeners.removeElement(listener);
   }

   /** Internal use. */
   void processChannel(Channel chan) {
      if (chan.getUserCount()>_minUsers && chan.getUserCount()<_maxUsers) {
         _results.addElement(chan);
         for (int i=0; i<_listeners.size(); i++) {
            ((ChannelSearchListener)_listeners.elementAt(i)).searchFound(chan);
         }
      }
   }

   /** Internal use. */
   void searchStarted(int channels) {
      for (int i=0; i<_listeners.size(); i++) {
         ((ChannelSearchListener)_listeners.elementAt(i)).searchStarted(channels);
      }
   }

   /** Internal use. */
   void searchEnded() {
      for (int i=0; i<_listeners.size(); i++) {
         ((ChannelSearchListener)_listeners.elementAt(i)).searchEnded();
      }
   }
}


