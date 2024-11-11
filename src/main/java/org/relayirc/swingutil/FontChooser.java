
/*
 * FILE: FontChooser.java
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

import org.relayirc.util.Debug;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

//////////////////////////////////////////////////////////////////////////////

/**
 * Simple font selection panel.
 */
public class FontChooser extends JPanel {

    JComboBox _fontname = new JComboBox();
    JComboBox _fontsize = new JComboBox();
    _Fontpanel _fontpanel = new _Fontpanel();
    JToggleButton _boldtog = new JToggleButton("Bold");
    JToggleButton _italictog = new JToggleButton("Italic");

    //_FontStyleCombo _fontstyle = new _FontStyleCombo();

    //-------------------------------------------------------------------------
    public FontChooser() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------------------------
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new FontChooser(), "Center");
        frame.pack();
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    //-------------------------------------------------------------------------
    public Font getSelectedFont() {
        String name = (String) _fontname.getSelectedItem();
        int size = ((Integer) _fontsize.getSelectedItem()).intValue();
        //int style = _fontstyle.getStyle();
        int style = Font.PLAIN;
        style += _boldtog.isSelected() ? Font.BOLD : 0;
        style += _italictog.isSelected() ? Font.ITALIC : 0;
        return new Font(name, style, size);
    }

    //-------------------------------------------------------------------------
    public void setSelectedFont(Font font) {
        Debug.println("FontChooser.setSelectedFont " + font);
        _fontname.setSelectedItem(font.getName());
        _fontsize.setSelectedItem(Integer.valueOf(font.getSize()));
        _boldtog.setSelected(font.isBold());
        _italictog.setSelected(font.isItalic());
        //_fontstyle.setStyle(font.getStyle());
        _fontpanel.repaint(0);
    }

    /*
     //-------------------------------------------------------------------------
     private class _FontStyleCombo extends JComboBox {
        _FontStyleCombo() {
             addItem(new String("Plain"));
             addItem(new String("Bold"));
             addItem(new String("Italics"));
             setSelectedItem("Plain");
         }
         public void setStyle(int style) {
            switch (style) {
                case Font.ITALIC: setSelectedItem("Italics"); break;
                case Font.BOLD: setSelectedItem("Bold"); break;
                default: setSelectedItem("Plain"); break;
             }
         }
         public int getStyle() {
           String str = (String)getSelectedItem();
              if (str.equals("Bold")) return Font.BOLD;
              else if (str.equals("Italics")) return Font.ITALIC;
              else return Font.PLAIN;
         }
     }
    */
    //-------------------------------------------------------------------------
    private void jbInit() throws Exception {

        // GridBagConstraints2 arguments:
        //
        //int gridx, int gridy, int gridw, int gridh, double weightx, double weighty,
        //int anchor, int fill, Insets insets, int ipadx, int ipady
        //
        setLayout(new GridBagLayout());

        add(new JLabel("Name"),
                new GridBagConstraints2(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        add(_fontname,
                new GridBagConstraints2(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        add(new JLabel("Size"),
                new GridBagConstraints2(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        add(_fontsize,
                new GridBagConstraints2(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        add(new JLabel("Style"),
                new GridBagConstraints2(2, 0, 2, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        add(_boldtog,
                new GridBagConstraints2(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        add(_italictog,
                new GridBagConstraints2(3, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        add(_fontpanel,
                new GridBagConstraints2(0, 2, 4, 3, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        // A listener to update the font display panel on any change
        ItemListener listener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                FontChooser.this.repaint(0);
            }
        };
        _fontname.addItemListener(listener);
        _fontsize.addItemListener(listener);
        _italictog.addItemListener(listener);
        _boldtog.addItemListener(listener);
        //_fontstyle.addItemListener(listener);

        // Add font names to font name combo
        String[] fonts = Toolkit.getDefaultToolkit().getFontList();
        for (int i = 0; i < fonts.length; i++) {
            _fontname.addItem(fonts[i]);
        }
        // Add font sizes to font size combo
        _fontsize.addItem(Integer.valueOf(8));
        _fontsize.addItem(Integer.valueOf(10));
        _fontsize.addItem(Integer.valueOf(12));
        _fontsize.addItem(Integer.valueOf(14));
        _fontsize.addItem(Integer.valueOf(16));

        //JPanel cpanel = new JPanel();
        //cpanel.setLayout(new GridLayout(1,3));
        //new BoxLayout(cpanel,BoxLayout.X_AXIS);
        //cpanel.add(_fontname);
        //cpanel.add(_fontsize);
        //cpanel.add(_boldtog);
        //cpanel.add(_italictog);
        //cpanel.add(_fontstyle);

        //setLayout(new BorderLayout());
        //add(cpanel,"North");
        //add(_fontpanel,"Center");
    }

    //-------------------------------------------------------------------------
    private class _Fontpanel extends JPanel {
        public _Fontpanel() {
            this.setBorder(new BevelBorder(BevelBorder.LOWERED));
            setPreferredSize(new Dimension(150, 50));
        }

        public void paint(Graphics g) {
            super.paint(g);

            String str = "abc ABC 123";
            Font font = getSelectedFont();
            FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
            int w = fm.stringWidth(str);
            int h = fm.getHeight();
            int x = this.getSize().width / 2 - w / 2;
            int y = this.getSize().height / 2 - h / 2;

            g.setColor(Color.white);
            g.fillRect(2, 2, this.getSize().width - 2, this.getSize().height - 2);
            g.setColor(Color.black);
            g.setFont(font);
            g.drawString(str, x, y);
        }
    }
}

