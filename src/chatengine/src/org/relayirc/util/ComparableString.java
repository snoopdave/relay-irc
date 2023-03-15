//-----------------------------------------------------------------------------
// $RCSfile: ComparableString.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/03/27 11:36:49 $
//-----------------------------------------------------------------------------


package org.relayirc.util;

///////////////////////////////////////////////////////////////////////////

/** 
 * Sortable string that implements IComparable. 
 * @see IComparable 
 */ 
public class ComparableString implements IComparable {
   private String _str = null;
	public ComparableString(String str) {
		_str = str;
	}
	public int compareTo(IComparable other) {
		if (other instanceof ComparableString) {
			ComparableString compString = (ComparableString)other;
			String otherString = (String)compString.getString();
			return _str.compareTo(otherString);
		}
		else return -1;
	}
	public String getString() {return _str;}
	public void setString(String str) {_str = str;}
}
