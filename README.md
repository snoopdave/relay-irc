
# Relay-IRC

*Copyright 1997 - 2024 by David M. Johnson*

## Contents

* [1.0 Introduction](#10-introduction)
* [2.0 RELAY-IRC Features](#20-relay-irc-features)
* [3.0 RELAY-IRC Requirements](#30-relay-irc-requirements)
* [4.0 Running Relay-IRC](#30-running-relay-irc)

## 1.0 Introduction

Relay-IRC is an Open Source IRC chat program with an easy-to-use graphical user interface (GUI). 
Written in Java and Swing (aka JFC), it should run on just about any computer.
It's not just a GUI, there is also a Chat Engine API for creating bots the ability to script the system via Python.
The software is available under the terms of the Mozilla public license (see the file LICENSE or the Mozilla web site for more on this license).

### 1.1 History

Relay-IRC is 90s vintage software brought back to life by nostalia and a Maven build.
Relay-IRC was once known as Relay-JFC and there was an article based on it  **Dr. Dobb's Journal** February 1999 issue: [Comparing WFC and JFC](https://snoopdave.github.io/relay-irc/wfc-vs-jfc/html/showdown.htm). 
You can also find the old project website up and running at SourceForge https://relayirc.sourceforge.net, but most of the links are 404 now.

You can also find a [Relay IRC Developer's Guide](https://snoopdave.github.io/relay-irc/docs/devnotes.html) that explains the internals and how to write a chat-bot with the Chat Engine API, [complete Javadocs](https://snoopdave.github.io/relay-irc/javadocs/org/relayirc/chatengine/package-summary.html) for the Chat Engine and some [screenshots of the Python features in the Relay-IRC UI](https://snoopdave.github.io/relay-irc/docs/preview1.html).

Here's the old Relay-JFC in action:

![screenshot of Relay-JFC](/docs/docs/screenshots/screenshot1.jpg "Relay-JFC running on Windows NT")

## 2.0 RELAY-IRC Features

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
   * Currently cannot be used as an applet 🤣
   * Does not support DCC file transfer

## 3.0 RELAY-IRC Requirements

Relay-JFC requires a Java Development Kit (JDK) and some recent refactoprings have been done to the code-base using Java 17 features like enhanced switch statements and text blocks. 
The latest version of the code-base has only been tested on Java 21.

## 4.0 Running Relay-IRC

Currently, there are no releases of Relay-JFC. So, the only way to run it is to clone this Github repo, build and run the code as follows:
   
    git clone https://github.com/snoopdave/relay-irc.git
    cd relay-irc
    mvn install
    mvn exec:java
