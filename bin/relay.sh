#!/bin/sh 
# This is the UNIX version.

# Starts Relay-JFC from the Relay-JFC jar file and other required jars. 
# "run -d" will start with debugging turned on

if [ "$RELAYHOME" = "" ] ; then
echo Using RELAYHOME=.. to start Relay-JFC
RELAYHOME=..
fi

CLASSPATH=$CLASSPATH:$RELAYHOME/lib/chatengine.jar:$RELAYHOME/lib/relay-jfc.jar:$RELAYHOME/lib/jython.jar:$RELAYHOME/scripts
export CLASSPATH

java -classpath $CLASSPATH org.relayirc.swingui.ChatApp $1 $2 $3 &

