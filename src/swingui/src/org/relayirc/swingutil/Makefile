#
# GNU make style makefile for package 
#
# This makefiles is designed to be run by the main
# Relay makefile in the Relay root directory.
# 

CLASSES = \
   GuiObject.class \
   GridBagConstraints2.class \
   GuiListCellRenderer.class \
   ColorCombo.class \
   FontChooser.class \
   GuiTableCellRenderer.class \
   IconManager.class \
   IPanel.class \
   ITab.class \
   ListEditorPanel.class \
   MDIClientFrame.class \
   MDIClientPanel.class \
   MDIPanel.class \
   PanelDlg.class \
   PongSpinner.class \
   PythonPanel.class \
   RadarSpinner.class \
   Spinner.class \
   SortedHeaderRenderer.class \
   StatusBar.class \
   StandardDlg.class \
   TabbedDialog.class \
   TableMap.class \
   TableSorter.class

package: $(CLASSES) package_propsheet

package_propsheet:
	cd propsheet; make

clean:
	rm -f *.class

tidy:
	rm -f *~
	rm -f *.*~
	rm -f *.bak

readonly:
	chmod -f -w *.java
	chmod -f -w *.html 
	chmod -f -w Makefile 

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $<

