//-----------------------------------------------------------------------------
// $RCSfile: Utilities.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/03/30 12:35:43 $
//-----------------------------------------------------------------------------


package org.relayirc.util;
import java.util.*;

/**
 * Utility functions.
 * @author David M. Johnson.
 */
public class Utilities {

   //--------------------------------------------------------------------------
   /** Convert string to integer array. */
   public static String[] stringToStringArray(String instr, String delim) {
      String[] sa = null;

      try {
         // Tokenize string, build vector of tokens
         StringTokenizer toker = new StringTokenizer(instr,delim);
         Vector v = new Vector();
         while (toker.hasMoreTokens()) {
            String s = (String)toker.nextToken();
            v.addElement( s );
         }
   
         // Allocate and fill array of ints
         sa = new String[v.size()];
         for (int i=0; i<v.size(); i++) {
            sa[i] = (String)v.elementAt(i);
         }
	  }
	  catch (Exception e) {
		  e.printStackTrace();
      }
      return sa;
   }

   //--------------------------------------------------------------------------
   /** Convert string to integer array. */
   public static int[] stringToIntArray(String instr, String delim)
      throws NoSuchElementException, NumberFormatException {

      int intArray[] = null;

      // Tokenize string, build vector of tokens
      StringTokenizer toker = new StringTokenizer(instr,delim);
      Vector ints = new Vector();
      while (toker.hasMoreTokens()) {
         String sInt = (String)toker.nextToken();
         int nInt = Integer.parseInt(sInt);
         ints.addElement(new Integer(nInt));
      }

      // Allocate and fill array of ints
      intArray = new int[ints.size()];
      for (int i=0; i<ints.size(); i++) {
         intArray[i] = ((Integer)ints.elementAt(i)).intValue();
      }
      return intArray;
   }
   //-------------------------------------------------------------------
   /** Convert integer array to a string. */
   public static String intArrayToString(int[] intArray) {
      String ret = new String();
      for (int i=0; i<intArray.length; i++) {
         if (ret.length()>0)
            ret = ret+","+Integer.toString(intArray[i]);
         else
            ret = Integer.toString(intArray[i]);
      }
      return ret;
   }
}


