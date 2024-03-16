package org.relayirc.swingutil;

import org.relayirc.util.Debug;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/** 
 * List editor dialog for editing lists of strings.
 */
public class DefaultListEditorDlg extends StandardDlg {

	private Vector _list;

    protected ListEditorPanel _listEditorPanel = new ListEditorPanel();

   //----------------------------------------------------------------
    public DefaultListEditorDlg( Frame parent, Vector list ) {

		super( parent, "List Editor", true );
        try {

			_list = list;

			_listEditorPanel = new ListEditorPanel( 
			   parent, "Edit list of strings:", 
		       new DefaultListEditorDlg.ListModel());
		    Debug.println("ctor ="+_listEditorPanel);

			_listEditorPanel.loadValues();

		    getContentPane().add(_listEditorPanel,BorderLayout.CENTER);

			pack();
			centerOnScreen(this);
			setVisible(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
   //----------------------------------------------------------------
    public DefaultListEditorDlg() {
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
   //----------------------------------------------------------------
   /**
    * You should override this function. Return true if it
    * is OK to close the dialog, false if not.
    */
   public boolean onOk() {
      _listEditorPanel.saveValues();
      return true;
   }
   //----------------------------------------------------------------
   /**
    * You should override this function. Return true if it
    * is OK to close the dialog, false if not.
    */
   public boolean onCancel() {
      return true;
   }

	/////////////////////////////////////////////////////////////////////
	// Setters and getters
	/////////////////////////////////////////////////////////////////////

	/** Get edited list of strings to be edited */
	public Vector getList() { 
		return _list; 
	}

	/////////////////////////////////////////////////////////////////////
	//  
	/////////////////////////////////////////////////////////////////////

    class ListModel implements ListEditorPanel.IModel {
		
		public Vector getObjects() {
			return _list; 
		}
		public ListEditorPanel.IHolder createNewObjectHolder(Object obj) {
			return new ListObjectHolder(obj.toString());
		}
		public ListEditorPanel.IHolder createNewObjectHolder() {
			return new ListObjectHolder();
		}
	}

	/////////////////////////////////////////////////////////////////////
	//  
	/////////////////////////////////////////////////////////////////////

    class ListObjectHolder implements ListEditorPanel.IHolder {

		private String _value;
		private String _editedValue;
		private int _state = ListEditorPanel.UNTOUCHED;

		public ListObjectHolder( String obj ) {
			_value = obj;
			_editedValue = _value;
		}
		public ListObjectHolder() {
			_value = "";
			_editedValue = _value;
		}
		public String getName() {
			return _editedValue;
		}
		public Object getObject() {
			return _editedValue;
		}
		public void saveValues() {
			_value = _editedValue;
		}
		public void setState(int s) {
			_state = s;
		}
		public boolean showDialog(Component parent) {
			String val = (String)JOptionPane.showInputDialog(
									parent,
									"Enter list value:", // prompt
                                    "List Item Editor",  // title
                                    JOptionPane.PLAIN_MESSAGE,
                                    null, // Icon
                                    null, // Object[] selectionValues,
                                    _editedValue);

			if (val != null) _editedValue = val;
			return true;
		}
		public String toString() {
			return _editedValue;
		}
	}
}

