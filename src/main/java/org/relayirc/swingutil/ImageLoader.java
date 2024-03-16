
package org.relayirc.swingutil;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;

public class ImageLoader {

   //------------------------------------------------------------------- 
   public static Image loadImage(InputStream is) {
      return Toolkit.getDefaultToolkit().createImage(readInputStream(is));
   }

   //------------------------------------------------------------------- 
   public static ImageIcon loadImageIcon(Class cls, String imgName) {
	   URL imgURL = cls.getResource(imgName);
	   if (imgURL == null) {
         return new ImageIcon(readInputStream(cls.getResourceAsStream(imgName)));
	   }
	   else {
         return new ImageIcon(imgURL);
	   } 
	}

   //------------------------------------------------------------------- 
   private static byte[] readInputStream(InputStream input) {
      try {
         int CHUNK = 10240;
         int i, count, max = 10 * CHUNK;
         byte[] tmp = new byte [CHUNK], bytesArray = new byte [max];

         // Read it all into a byte array
         for (i=0,count=0; (count = input.read(tmp)) != -1; i+=count) {
            if (i+count > max) {
               byte[] ba = new byte [ i + count + CHUNK ];
               System.arraycopy( bytesArray, 0, ba, 0, i );
               max = i + count + CHUNK;
               bytesArray = ba;
            }
            System.arraycopy( tmp, 0, bytesArray, i, count );
         }
         tmp = null;

         // Now reduce the size of bytesArray to its actual size
         tmp = new byte [i];
         System.arraycopy( bytesArray, 0, tmp, 0, i);
         bytesArray = tmp;
         //DebugMsg.println("Read image of size = "+i);

         return bytesArray;
      }
      catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }
}

