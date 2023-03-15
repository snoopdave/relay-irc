package org.relayirc.swingutil;

import org.relayirc.util.Debug;

import java.util.Vector;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

public class ListSelectionEditorDlg extends StandardDlg {

    JPanel _listPanel = new JPanel();
    JList _list = new JList();
	Vector _allValues = null;
	Vector _selection = null;

	//----------------------------------------------------------------
    public ListSelectionEditorDlg( Frame parent, 
	    Vector allValues, Vector selection ) {

		super( parent, "List Selection Editor", true );
		_allValues = allValues;
		_selection = selection;

		_listPanel.setLayout(new BorderLayout());

		// Add all values to list component
		_list.setListData( _allValues );
		_list.setSelectionMode(
			ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// Select the selected items in the list 
		for (int i=0; i<_selection.size(); i++) {
			int indexOf = _allValues.indexOf( _selection.elementAt(i) );
			_list.addSelectionInterval( indexOf, indexOf );
		}
		_listPanel.add( new JScrollPane(_list),BorderLayout.CENTER); 

		getContentPane().add( _listPanel, BorderLayout.CENTER );

		pack();
		centerOnScreen(this);
		setVisible( true );
    }
	//----------------------------------------------------------------
	public Vector getSelection() {
		Vector selected = new Vector();
		for (int i=0; i<_list.getSelectedValues().length; i++) {
		   selected.addElement(_list.getSelectedValues()[i]);
		}
		return selected;
	}
}

