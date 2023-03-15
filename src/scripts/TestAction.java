
import java.awt.*;
import javax.swing.*;

import org.relayirc.swingui.*;

public class TestAction implements IRelayRunnable {

   public void run( Object context ) {

      JFrame frame = new JFrame();
      frame.setSize(200,200);
      frame.setVisible(true);

      frame.getContentPane().setLayout( new BorderLayout() );

      JTextArea tarea = new JTextArea();
      frame.getContentPane().add( tarea, BorderLayout.CENTER );

      tarea.append("TestAction");
      tarea.append("Context = "+context);
   }

}

