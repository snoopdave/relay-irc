//-----------------------------------------------------------------------------
// $RCSfile: Utilities.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/03/30 12:35:43 $
//-----------------------------------------------------------------------------


package org.relayirc.util;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Utility functions.
 *
 * @author David M. Johnson.
 */
public class Utilities {

    //--------------------------------------------------------------------------

    /**
     * Convert string to integer array.
     */
    public static int[] stringToIntArray(String instr, String delim)
            throws NoSuchElementException, NumberFormatException {
        int[] intArray = null;

        // Tokenize string, build vector of tokens
        StringTokenizer toker = new StringTokenizer(instr, delim);
        Vector<Integer> ints = new Vector<>();
        while (toker.hasMoreTokens()) {
            String sInt = toker.nextToken();
            int nInt = Integer.parseInt(sInt);
            ints.addElement(nInt);
        }

        // Allocate and fill array of ints
        intArray = new int[ints.size()];
        for (int i = 0; i < ints.size(); i++) {
            intArray[i] = ints.elementAt(i);
        }
        return intArray;
    }
    //-------------------------------------------------------------------

    /**
     * Convert integer array to a string.
     */
    public static String intArrayToString(int[] intArray) {
        String ret = "";
        for (int j : intArray) {
            if (!ret.isEmpty())
                ret = ret + "," + j;
            else
                ret = Integer.toString(j);
        }
        return ret;
    }
}


