#!/bin/sh
if [ "$RELAYSRC" = "" ] ; then
RELAYSRC=.
export RELAYSRC
fi
CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar:$RELAYSRC/lib/jaxp-1.1.jar:$RELAYSRC/lib/crimson-1.1.jar:$RELAYSRC/lib/ant-1.3.jar

java -classpath $CLASSPATH -Drelay.root=$RELAYSRC org.apache.tools.ant.Main $1 $2 $3 

