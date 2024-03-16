//-----------------------------------------------------------------------------
// $RCSfile: ParsedToken.java,v $
// $Revision: 1.1.2.1 $
// $Author: snoopdave $
// $Date: 2001/03/30 12:35:43 $
//-----------------------------------------------------------------------------

package org.relayirc.util;
import java.util.*;

/**
 * Super-simple string parser. 
 * @author David M. Johnson.
 */
public class ParsedToken {
   
   /** String token */
   public String token;

   /** Position that string token was found in string */
   public int index;

   /** Parse string into array of ParsedTokens */
   public static ParsedToken[] stringToParsedTokens(String s, String delim) {
      ParsedToken tokens[] = null;
      try {
         // Tokenize string, build vector of tokens
		 int pos=0;
         StringTokenizer toker = new StringTokenizer(s,delim);
         Vector v = new Vector();
         while (toker.hasMoreTokens()) {
            ParsedToken tok = new ParsedToken();
			tok.token = (String)toker.nextToken();
			tok.index = pos;
			pos += tok.token.length()+1;
            v.addElement( tok );
         }
   
         // Allocate and fill array of tokens 
         tokens = new ParsedToken[v.size()];
         for (int i=0; i<v.size(); i++) {
            tokens[i] = (ParsedToken)v.elementAt(i); 
         }
	  }
	  catch (Exception e) {
		  e.printStackTrace();
      }
      return tokens;
   }
}
