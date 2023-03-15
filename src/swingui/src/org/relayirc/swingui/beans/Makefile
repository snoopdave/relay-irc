#
# GNU make style makefile for package 
#
# This makefiles is designed to be run by the main
# Relay makefile in the Relay root directory.
# 

CLASSES = \
   UserBeanInfo.class \
   ChannelBeanInfo.class \
   ServerBeanInfo.class

package: $(CLASSES)

clean:
	rm -f *.class

tidy:
	rm -f *~
	rm -f *.*~
	rm -f *.bak

readonly:
	chmod -f -w *.java
	chmod -f -w Makefile 

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $<

