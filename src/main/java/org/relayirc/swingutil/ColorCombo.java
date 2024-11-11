
/*
 * FILE: ColorCombo.java
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is Relay IRC chat client.
 *
 * The Initial Developer of the Original Code is David M. Johnson.
 * Portions created by David M. Johnson are Copyright (C) 1998.
 * All Rights Reserved.
 *
 * Contributor(s): No contributors to this file.''
 */
package org.relayirc.swingutil;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * Color combobox for choosing one of the 13 named colors in AWT.
 */
public class ColorCombo extends JComboBox implements Serializable {

    // This seems stupid
    private final Hashtable _colorsByName = new Hashtable();
    private final Hashtable _namesByColor = new Hashtable();

    private final Color[] _colors = {
            Color.black,
            Color.darkGray,
            Color.gray,
            Color.lightGray,
            Color.blue,
            Color.cyan,
            Color.magenta,
            Color.pink,
            Color.red,
            Color.orange,
            Color.green,
            Color.yellow,
            Color.white
    };

    //---------------------------------------------------------------
    public ColorCombo() {
        super();
        setRenderer(new _ColorRenderer());
        for (int i = 0; i < _colors.length; i++) addItem(_colors[i]);
        _colorsByName.put("Black", Color.black);
        _colorsByName.put("DarkGray", Color.darkGray);
        _colorsByName.put("Gray", Color.gray);
        _colorsByName.put("LightGray", Color.lightGray);
        _colorsByName.put("Blue", Color.blue);
        _colorsByName.put("Cyan", Color.cyan);
        _colorsByName.put("Magenta", Color.magenta);
        _colorsByName.put("Pink", Color.pink);
        _colorsByName.put("Red", Color.red);
        _colorsByName.put("Orange", Color.orange);
        _colorsByName.put("Yellow", Color.yellow);
        _colorsByName.put("Green", Color.green);
        _colorsByName.put("White", Color.white);

        _namesByColor.put(Color.black, "Black");
        _namesByColor.put(Color.darkGray, "DarkGray");
        _namesByColor.put(Color.gray, "Gray");
        _namesByColor.put(Color.lightGray, "LightGray");
        _namesByColor.put(Color.blue, "Blue");
        _namesByColor.put(Color.cyan, "Cyan");
        _namesByColor.put(Color.magenta, "Magenta");
        _namesByColor.put(Color.pink, "Pink");
        _namesByColor.put(Color.red, "Red");
        _namesByColor.put(Color.orange, "Orange");
        _namesByColor.put(Color.green, "Green");
        _namesByColor.put(Color.yellow, "Yellow");
        _namesByColor.put(Color.white, "White");
    }

    //---------------------------------------------------------------
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension((int) (size.width * 1.5), size.height);
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    //---------------------------------------------------------------
    public Color getSelectedColor() {
        return (Color) getSelectedItem();
    }

    //---------------------------------------------------------------
    public void setSelectedColor(Color col) {
        setSelectedItem(col);
    }

    //---------------------------------------------------------------
    public String getSelectedColorName() {
        return (String) _namesByColor.get(getSelectedColor());
    }

    //---------------------------------------------------------------
    public void setSelectedColorName(String name) {
        setSelectedColor((Color) _colorsByName.get(name));
    }

    //---------------------------------------------------------------
    class _ColorRenderer extends JPanel implements ListCellRenderer {
        private Color _val = Color.black;
        private boolean _sel = false;

        public _ColorRenderer() {
            this.setPreferredSize(new Dimension(60, 20));
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object val, int idx, boolean isSel, boolean hasFocus) {
            _val = (Color) val;
            _sel = isSel;
            return this;
        }

        public void paint(Graphics g) {

            g.setColor(Color.white);
            g.fillRect(0, 0, this.getSize().width, this.getSize().height);

            g.setColor(_val);
            g.fillRect(5, 5, this.getSize().height - 10, this.getSize().height - 10);

            g.setColor(Color.black);
            g.drawRect(5, 5, this.getSize().height - 10, this.getSize().height - 10);

            String colorName = (String) _namesByColor.get(_val);
            if (colorName == null) colorName = "Custom";
            g.drawString(colorName, this.getSize().height, this.getSize().height - 5);

            if (_sel) {
                g.setColor(Color.red);
                g.drawRect(0, 0, this.getSize().width - 1, this.getSize().height - 1);
            }
        }
    }
}

