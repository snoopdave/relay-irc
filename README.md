
# Relay-IRC

*Copyright 1997 - 2024 by David M. Johnson*

### TABLE OF CONTENTS

* 1.0 INTRODUCTION
* 2.0 RELAY-IRC FEATURES
* 3.0 RELAY-IRC REQUIREMENTS
* 4.0 INSTALLING & RUNNING RELAY-IRC
* 5.0 BUILDING RELAY-IRC

## 1.0 INTRODUCTION

Relay-IRC is an Open Source IRC chat program with an easy-to-use graphical user interface. 
Written in Java and Swing (aka JFC), it should run on just about any computer.
The software is available under the terms of the Mozilla public license (see the file LICENSE or the Mozilla web site for more on this license).

Relay-IRC was once known as Relay-JFC. 
There is an article about Relay-JFC in Dr. Dobb's, February 1999 issue: [Comparing WFC and JFC](https://www.drdobbs.com/jvm/comparing-wfc-and-jfc/184410855)

Here's the old Relay-JFC in action:

![screenshot if Relay-JFC](docs/screenshot1.jpg "Relay-JFC running on Windows NT")

# 2.0 RELAY-JFC FEATURES

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

# 3.0 RELAY-JFC REQUIREMENTS

Relay-JFC requires a Java Development Kit (JDK) and is known to run on Java 21.

# 4.0 INSTALLING & RUNNING RELAY-JFC 

Currently, there are no releases of Relay-JFC. So, the only way to run it is to clone this Github repo, build and run the code as follows:
   
    git clone https://github.com/snoopdave/relay-irc.git
    cd relay-irc
    mvn install
    mvn exec:java
