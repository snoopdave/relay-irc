//-----------------------------------------------------------------------------
// $RCSfile: IComparable.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/03/27 11:36:49 $
//-----------------------------------------------------------------------------


package org.relayirc.util;

/** 
 * Objects that implement this interface are sortable by QuickSort.
 * @see QuickSort
 */
public interface IComparable {
	/**
	 *	Compare to other object. Works like String.compareTo()
	 */
	public int compareTo(IComparable c);
}
