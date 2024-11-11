package org.relayirc.swingutil;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class ListSelectionEditorDlg extends StandardDlg {

    JPanel _listPanel = new JPanel();
    JList<String> _list = new JList<>();
    Vector<String> _allValues = null;
    Vector<String> _selection = null;

    //----------------------------------------------------------------
    public ListSelectionEditorDlg(Frame parent,
                                  Vector<String> allValues, Vector<String> selection) {

        super(parent, "List Selection Editor", true);
        _allValues = allValues;
        _selection = selection;

        _listPanel.setLayout(new BorderLayout());

        // Add all values to list component
        _list.setListData(_allValues);
        _list.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Select the selected items in the list
        for (int i = 0; i < _selection.size(); i++) {
            int indexOf = _allValues.indexOf(_selection.elementAt(i));
            _list.addSelectionInterval(indexOf, indexOf);
        }
        _listPanel.add(new JScrollPane(_list), BorderLayout.CENTER);

        getContentPane().add(_listPanel, BorderLayout.CENTER);

        pack();
        centerOnScreen(this);
        setVisible(true);
    }

    //----------------------------------------------------------------
    public Vector<String> getSelection() {
        Vector<String> selected = new Vector<>();
        for (var item : _list.getSelectedValuesList()) {
            selected.addElement(item);
        }
        return selected;
    }
}

