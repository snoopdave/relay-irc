//-----------------------------------------------------------------------------
// $RCSfile: PropSheet.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 05:53:57 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil.propsheet;

import org.relayirc.util.Debug;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.PropertyEditor;
import java.util.Enumeration;
import java.util.Vector;

//////////////////////////////////////////////////////////////////////////

/**
 * Simple property sheet component designed for editing the properties
 * of an object that implements IPropModel.
 *
 * @author David M. Johnson
 * @version $Revision: 1.1.2.2 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay IRC Chat Engine<br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong> No contributors to this file
 * <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class PropSheet extends JComponent {

    static {
        //PropertyEditorManager.registerEditor(String.class,TextEditor.class);
        //PropertyEditorManager.registerEditor(Integer.class,IntegerEditor.class);
    }

    private final JScrollPane _spane = null;
    private JTable _jtable = null;
    private _CellEditor _editor = null;
    private _CellRenderer _renderer = null;

    /**
     * Constructs a property sheet for editing a specified prop holder.
     */
    public PropSheet(IPropModel holder) {
        createEmptyTable();
        setPropModel(holder);
    }
    //--------------------------------------------------------------------

    /**
     * Constructs an empty property sheet.
     */
    public PropSheet() {
        createEmptyTable();
        clear();
    }

    //--------------------------------------------------------------------
    private IPropModel createDummyPropModel() {

        IPropModel holder = new DefaultPropModel();
        holder.setProperty("", "");
        return holder;
    }

    //--------------------------------------------------------------------
    private void createEmptyTable() {

        _jtable = new JTable(new _TableModel(createDummyPropModel()));
        //_jtable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        _jtable.setColumnSelectionAllowed(false);
        _jtable.setRowSelectionAllowed(false);
        _jtable.getTableHeader().setResizingAllowed(true);
        _jtable.getTableHeader().setReorderingAllowed(false);

        // Create editor and renderer and assign them to property column
        _editor = new _CellEditor();
        _editor.setClickCountToStart(1);
        _jtable.getColumnModel().getColumn(1).setCellEditor(_editor);

        // Make table rows 5 pixels taller
        Font font = _jtable.getFont();
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int h = fm.getHeight();
        _jtable.setRowHeight(h + 5);

        _renderer = new _CellRenderer();
        _jtable.getColumnModel().getColumn(1).setCellRenderer(_renderer);
        // TODO: should I give prop name column its own renderer?
        //_jtable.getColumnModel().getColumn(0).setCellRenderer(_renderer);

        // Add scrollpane for table
        JScrollPane scrollpane = new JScrollPane(_jtable);
        setLayout(new BorderLayout());
        add(scrollpane, "Center");
        //invalidate();
    }

    //--------------------------------------------------------------------
    public void setReadOnly(boolean ro) {
        ((_TableModel) _jtable.getModel()).setReadOnly(ro);
    }
    //--------------------------------------------------------------------
    //public void setDefaultEditing(boolean defaultEditing) {
    //((_TableModel)_jtable.getModel()).setDefaultEditing(defaultEditing);
    //}
    //--------------------------------------------------------------------

    /**
     * Clear property sheet.
     */
    public void clear() {
        setPropModel(createDummyPropModel());
    }
    //--------------------------------------------------------------------

    /**
     * Preferred size.
     */
    public Dimension getPreferredSize() {
        return new Dimension(300, 250);
    }
    //--------------------------------------------------------------------

    /**
     * Minimum size is same as perferred size.
     */
    public Dimension getMinimumSize() {
        return getPreferredSize();
        //return new Dimension(100,200);
    }
    //--------------------------------------------------------------------

    /**
     * Access to internal table, use it wisely.
     */
    public JTable getTable() {
        return _jtable;
    }
    //--------------------------------------------------------------------

    /**
     * Sets prop holder to be edited by property sheet.
     */
    public void setPropModel(IPropModel holder) {
        if (_editor != null)
            _editor.stopCellEditing();
        _TableModel model = (_TableModel) _jtable.getModel();
        model.setPropModel(holder);

        // re-enable editor and renderer
        if (_editor != null) {
            _jtable.getColumnModel().getColumn(1).setCellEditor(_editor);
        }
        _jtable.getColumnModel().getColumn(1).setCellRenderer(_renderer);

        invalidate();
        _jtable.invalidate();
        _jtable.getTableHeader().invalidate();

        validate();
        _jtable.validate();
        _jtable.getTableHeader().validate();

    }
}

//////////////////////////////////////////////////////////////////////////

class _TableModel extends AbstractTableModel {
    private final boolean _defaultEditing = true;
    private int _rows, _cols;
    private IPropModel _holder;
    private boolean _readOnly = false;
    private Vector _propertyNames = new Vector();

    //--------------------------------------------------------------------
    public _TableModel(IPropModel holder) {
        setPropModel(holder);
    }

    //--------------------------------------------------------------------
    public IPropModel getPropModel() {
        return _holder;
    }

    //--------------------------------------------------------------------
    public void setPropModel(IPropModel holder) {
        _holder = holder;
        _propertyNames = new Vector();
        for (Enumeration e = holder.propertyNames(); e.hasMoreElements(); ) {
            _propertyNames.addElement(e.nextElement());
        }
        update();
    }

    //--------------------------------------------------------------------
    public void setReadOnly(boolean ro) {
        _readOnly = ro;
    }

    //--------------------------------------------------------------------
    /*
     * Determines if standard JTable editing takes place. Should be disabled
     * for a Bean property sheet that uses its own editors.
     */
    //public void setDefaultEditing(boolean defaultEditing) {
    //_defaultEditing=defaultEditing;
    //}
    //--------------------------------------------------------------------
    public int getRowCount() {
        return _holder.getPropertyCount();
    }

    //--------------------------------------------------------------------
    public String getColumnName(int i) {
        if (i == 0)
            return " ";
        else if (i == 1)
            return " ";
        else {
            return null;
        }
    }

    //--------------------------------------------------------------------
    public int getColumnCount() {
        return 2;
    }

    //--------------------------------------------------------------------
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (!_readOnly && _defaultEditing) {
            // I'm assuming columnIndex is 1 because column 0 is not editable
            String name = (String) _propertyNames.elementAt(rowIndex);
            _holder.setProperty(name, value);
        }
    }

    //--------------------------------------------------------------------
    public PropertyEditor getEditorComponent(int rowIndex) {
        PropertyEditor editor = _holder.getEditor(getPropertyNameAt(rowIndex));
        editor.setValue(getPropertyNameAt(rowIndex));

        if (editor instanceof PropSheetEditor) {
            ((PropSheetEditor) editor).setWritable(!_readOnly);
        }
        return editor;
    }

    //--------------------------------------------------------------------
    public String getPropertyNameAt(int rowIndex) {
        return (String) _propertyNames.elementAt(rowIndex);
    }

    //--------------------------------------------------------------------
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object ret = null;
        try {
            String name = (String) _propertyNames.elementAt(rowIndex);
            if (columnIndex == 1) {
                ret = _holder.getProperty(name);
            } else if (columnIndex == 0) {
                ret = _holder.getPropertyDisplayName(name);
            }
        } catch (Exception e) {
            Debug.println("TableModel: error in getValueAt()");
        }

        return ret;
    }

    //--------------------------------------------------------------------
    public boolean isCellEditable(int row, int col) {
        if (col == 0)
            return false;
        return col == 1;
    }

    //--------------------------------------------------------------------
    public void update() {
        fireTableDataChanged();
    }
}

//////////////////////////////////////////////////////////////////////////

class _CellRenderer implements TableCellRenderer {
    private final _RendererComponent _renderer = new _RendererComponent();
    private PropertyEditor _currentEditor = null;

    //--------------------------------------------------------------------
    public _CellRenderer() {
    }

    //--------------------------------------------------------------------
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int rowIndex, int colIndex) {

        Component comp = null;

        try { // sometimes rowIndex is out of bounds
            _TableModel tm = (_TableModel) table.getModel();
            _currentEditor = tm.getEditorComponent(rowIndex);
            _currentEditor.setValue(value);
            return _renderer;
        } catch (Exception e) {
            e.printStackTrace();
            comp = null;
        }
        return _renderer;
    }
    //--------------------------------------------------------------------

    /**
     * Cell render that allows the editor to do the painting.
     */
    private class _RendererComponent extends JComponent {
        public _RendererComponent() {
            super();
        }

        public void paint(Graphics gfx) {
            if (_currentEditor != null) {
                Rectangle rect = new Rectangle();
                _currentEditor.paintValue(gfx, getBounds(rect));
            }
        }
    }
    //--------------------------------------------------------------------
    // TODO: use button renderer in PropSheet?

    /**
     * Renderer that looks like a button, because it IS a button
     */
    private class _ButtonRendererComponent extends JButton {
        public _ButtonRendererComponent() {
            super();
            setHorizontalAlignment(SwingConstants.LEFT);
            Font font = getFont();
            font = new java.awt.Font(font.getName(), Font.PLAIN, font.getSize());
            setFont(font);
        }

        public void paint(Graphics gfx) {
            if (_currentEditor != null) {
                setText(_currentEditor.getAsText());
                paintComponent(gfx);
                paintBorder(gfx);
            }
        }
    }
}

/////////////////////////////////////////////////////////////////////////

class _CellEditor extends DefaultCellEditor {
    private PropertyEditor _currentEditor = null;

    public _CellEditor() {
        super(new JTextField()); // a dummy
    }

    //--------------------------------------------------------------------
    public Object getCellEditorValue() {
        Object ret = null;
        if (_currentEditor != null) {
            ret = _currentEditor.getValue();
        }
        return ret;
    }

    //--------------------------------------------------------------------
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected, int rowIndex, int colIndex) {

        Component ret = null;
        try {
            _TableModel tm = (_TableModel) table.getModel();
            _currentEditor = tm.getEditorComponent(rowIndex);
            _currentEditor.setValue(value);
            ret = _currentEditor.getCustomEditor();
        } catch (Exception e) {
            _currentEditor = null;
            ret = new JLabel("No editor available");
            e.printStackTrace();
        }
        return ret;
    }
}


