
README: Relay-IRC

Copyright 1999-2023 by David M. Johnson 


   TABLE OF CONTENTS
   -----------------
   1.0 INTRODUCTION
   2.0 RELAY-JFC FEATURES
   3.0 RELAY-JFC REQUIREMENTS
   4.0 INSTALLING & RUNNING RELAY-JFC
   5.0 BUILDING RELAY-JFC


1.0 INTRODUCTION
----------------
Relay-JFC is an Open Source IRC chat program with an easy-to-use graphical user
interface. Relay-JFC is written in Java, so it will run on just about any 
computer.

Relay-JFC is currently available as a beta release in both executable and 
source code forms under the terms of the Mozilla public license (see the file 
LICENSE.txt or the Mozilla web site for more on this license).

There is an article about Relay-JFC in Dr. Dobb's: https://www.drdobbs.com/jvm/comparing-wfc-and-jfc/184410855

2.0 RELAY-JFC FEATURES
----------------------
The significant features of Relay-JFC are:

   * GUI with multiple window interface
   * Support for most IRC commands and replies
   * Color-coded message display
   * Persistent user settings
   * Tree-view of favorite servers and channels
   * Console window for server messages
   * Support for client scripting with Python
   * Support for client programming with Java 
   * IRC class library or "Chat Engine" for Java programmers

There are also some significant limitations:

   * Currently cannot be used as an applet
   * Does not support DCC file transfer


3.0 RELAY-JFC REQUIREMENTS
--------------------------
Before you install and run Relay-JFC you need to install a Java 2 (Standard 
Edition, v1.3) runtime environment on your computer.  Below are some links to 
help you find Java 2 for your operating system:

   Solaris and Windows - http://www.javasoft.com/products/jdk/1.3/   
   Linux/x86 - http://www.blackdown.org   
   AIX - http://www.ibm.com/java/jdk/aix/index.html
   HPUX - http://www.enterprisecomputing.hp.com/java/

NOTE: Relay-JFC should also work with Java 1.1.8 as long as the JFC/Swing 1.1 
classes are in your classpath. However, Java 1.1.8 support has not been tested 
since the Relay-IRC 0.8 release.


4.0 INSTALLING & RUNNING RELAY-JFC 
----------------------------------

There are two types of Relay-JFC downloads: releases and snapshots. A release 
is intended for normal users and a snapshot is intended for developers. 

   ****************************************************************************
   HOW TO DOWNLOAD, INSTALL AND RUN A RELAY-JFC RELEASE
   ****************************************************************************

   If you just want to use the Relay-JFC chat program and you have no interest
   in examining or modifying the source code, then download a release. Follow
   the instructions for your operating system:

   For Windows: Download and run the relayjXX.exe installer (where XX is the 
      version number of the release - for example relayj08.exe).  The installer
	  will create an icon on your start menu for starting Relay-JFC.
   
   For Linux (and other UNIX systems): Download and run the relayXX.bin shell 
      script (Where XX is the version number of the release - for example
	  relay08.bin). The installer will install Relay-JFC in the directory of 
	  your choice and will create a script in your home directory that you can 
	  use to start Relay-JFC.
   
   For other Java environments: Download the relayXX.zip file (where XX is 
      the version number of the release - for example relay08.zip), unzip it 
	  and follow the instructions in the ReadMe.htm file contained therein.

   Releases are also available in source code form.

   
   ****************************************************************************
   HOW TO INSTALL AND RUN A RELAY-JFC SOURCE RELEASE
   ****************************************************************************

   Source releases include both binaries and source code. 

   Installing Relay-JFC: 
   
      Install a source code release by unzipping or untarring the release into 
      a directory of your choice. 
   
   Running Relay-JFC:

      Windows: double-click on executable jar

         Double-click on the relay-jfc.jar file in the Relay 'lib' directory,
         if you have Java 2 set up propery, Relay-JFC will start.

      Windows: run the relay.bat batch file

         Open a Command Prompt window, cd to the Relay 'bin' directory and then
         type 'relay' to start Relay-JFC. 
          
      UNIX: run the relay.sh shell script

         Open an X Windows shell window, cd to the Relay 'bin' directory and 
         then type 'relay.sh' start Relay-JFC.


   ****************************************************************************
   HOW TO INSTALL AND RUN A RELAY-JFC SNAPSHOT RELEASE 
   ****************************************************************************

   If you want to try the latest (and possibly unstable) version of Relay-JFC 
   then download a snapshot. Snapshot releases are available with source code 
   and are available in formats for Windows and UNIX. Snapshot releases are 
   named relay-YYYYMMDD (where YYYYMMDD is the date of the release - for 
   example relay-20010331.zip) and snapshot releases come in zip or tar.gz 
   formats.  

   Snapshot release are source code releases, so follow the instructions above
   for installing and running a Relay-JFC source code release.

   
5.0 BUILDING RELAY-JFC
----------------------
   
If you downloaded the source code to Relay-JFC, then you can build Relay-JFC 
yourself. All you need is a Java Develoment Kit, preferably JDK 1.3.

   Building Relay-JFC:

      Windows: run the relay.bat batch file

         Open a Command Prompt window, cd to the Relay directory and then 
         type 'build' to build Relay-JFC. 
          
      UNIX: run the relay.sh shell script

         Open an X Windows shell window, cd to the Relay directory and then 
         type 'build.sh' build Relay-JFC.

   Build targets: 

      The build scripts support the following targets:

      build          - Builds a runnable Relay-JFC in the dist directory
      build release  - Builds a full set of Relay-JFC release zip and tar files
      build javadocs - Builds javadocs in dist/javadocs
      build clean    - Removes all build and dist directories 

