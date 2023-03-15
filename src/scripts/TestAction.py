
# Creates a frame that contains a text area. Prints value 
# of variable named context in the text area.

from javax import swing
from java import awt
from java import lang

# Create frame with border layout
frame = swing.JFrame("TestAction.py")
frame.getContentPane().layout = awt.BorderLayout()

# Add text field
textField = swing.JTextArea()
frame.getContentPane().add(textField,awt.BorderLayout().CENTER)

# Size and show frame 
frame.setBounds(100,100,300,200);
frame.setVisible(1);

# Print context in text area 
try: 
   textField.append("Context = "+context+"\n")
except:
   textField.append("Context not defined\n")

