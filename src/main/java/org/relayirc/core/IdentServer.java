//----------------------------------------------------------------------------
// $RCSfile: IdentServer.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/08 15:17:25 $
//----------------------------------------------------------------------------

package org.relayirc.core;
import org.relayirc.util.*;

import java.net.*;
import java.io.*;

/** 
 * Implements a "one-shot" ident authentication server.
 * <p>On UNIX, objects of this class  will not be able to create a ServerSocket 
 * on port 133 so it will fail. This is not a problem, because on UNIX
 * users are expected to set up their own ident servers.</p>
 * @author David M. Johnson.
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code:     Relay IRC Chat Server<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s):    No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class IdentServer implements Runnable {
   private String       _userName = null;
   private ServerSocket _echoServer = null;        
   private Socket       _clientSocket = null;           

   //------------------------------------------------------------------
   /** Construct identity server for a specified user name. */
   public IdentServer(String userName) {
      _userName = userName;
      Thread t = new Thread(this);
      t.start();
   }
   //------------------------------------------------------------------
   /** Try to stop ident server. */
   public void stop() {
      Debug.println("IdentServer: stopping");
      try {
         _echoServer.close();
      }
      catch (Exception e) {
         Debug.println("   _echoServer is null");
      }
      try {
         _clientSocket.close();
      }
      catch (Exception e) {
         Debug.println("   _clientSocket is null");
      }
   }
   //------------------------------------------------------------------
   /** Start running, service one ident request and then terminate. */ 
   public void run() {

      String line;
      BufferedReader is;
      DataOutputStream os;

      try {
         // Start listening on port 113
         Debug.println("IdentServer: opening server socket on port 113");
         _echoServer = new ServerSocket(113); 
         _echoServer.setSoTimeout(60000); // one minute timeout

         // Block until connection is made 
         Debug.println("IdentServer: waiting for a connection");
         _clientSocket = _echoServer.accept();

         // Got connection, now setup IO streams
         Debug.println("IdentServer: got a connection");
         is = new BufferedReader( 
                 new InputStreamReader( 
                    new DataInputStream( _clientSocket.getInputStream() )));
         os = new DataOutputStream( _clientSocket.getOutputStream() );
      }
      catch (InterruptedIOException e) {
         // InterruptedIOException occurs as part of normal operation 
         // when timeout expires or when stop() closes the sockets. 
         Debug.println("IdentServer: exiting due to InterruptedIOException");
         return;
      }
      catch (BindException e) {
         // BindException means that either the user does not
         // have permission to listen on port 113, or port 113
         // is already in use. Print the standard warning.
         printWarning();
         return;
      }
      catch (SocketException e) {
         // SocketException probably means that stop() was called. 
         Debug.println("IdentServer: exiting due to SocketException");
         return;
      }
      catch (Exception e) {
         // Something bad but non-fatal happened, complain and continue
	     e.printStackTrace();	
         return;
      }

      // Once we get a non-null line of data from the socket, fire-off 
      // an ident response and return, thus terminating this IdentServer.
      try {
         while (true) {             
            line = is.readLine();
            if (line != null) {
               String resp = line+" : USERID : UNIX : "+_userName;
               Debug.println("IdentServer: responding to query with: "+resp);
               os.writeBytes(resp); 
               stop();
               return;
            }
         }  
      }
      catch (Exception e) {
         // Something bad but non-fatal happened, complain and continue
			e.printStackTrace();	
         return;
      }
   }    
   //-------------------------------------------------------------------------
   public void printWarning() {
	   System.out.println("IdentServer: Unable to bind to port 133");
	   System.out.println("IdentServer: Unable to start");
   }
}   


