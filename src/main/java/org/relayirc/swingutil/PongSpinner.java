//----------------------------------------------------------------------------
// $RCSfile: PongSpinner.java,v $
// $Revision: 1.1.2.4 $
// $Author: snoopdave $
// $Date: 2001/04/07 18:55:09 $
//----------------------------------------------------------------------------

package org.relayirc.swingutil;

import org.relayirc.util.Debug;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pong-style progress indicator with little box that bounces from left
 * to right leaving a trail.
 *
 * @author David M. Johnson
 * @version $Revision: 1.1.2.4 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * <strong>Original Code:</strong>Relay-JFC Chat Client<br>
 * <strong>Initial Developer:</strong> David M. Johnson <br>
 * <strong>Contributor(s):</strong> No contributors to this file <br>
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class PongSpinner extends JPanel {
    private final _PongSpinner _spinner;

    //--------------------------------------------------------------------------
    public PongSpinner(int delay, int numBoxes) {
        _spinner = new _PongSpinner(delay, numBoxes);
        setLayout(new BorderLayout());
        add(_spinner, BorderLayout.CENTER);
    }

    //--------------------------------------------------------------------------
    public static void main(String[] args) {

        Debug.println("ProgressMeter test program");

        final PongSpinner progMeter = new PongSpinner(30, 12);
        progMeter.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(progMeter, BorderLayout.CENTER);

        JButton toggleButton = new JButton("Start/Stop");
        frame.getContentPane().add(toggleButton, BorderLayout.SOUTH);
        toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (progMeter.isRunning())
                    progMeter.stop();
                else
                    progMeter.start();
            }
        });

        frame.setSize(400, 300);
        StandardDlg.centerOnScreen(frame);
        frame.setVisible(true);
    }

    //--------------------------------------------------------------------------
    public Dimension getMinimumSize() {
        return new Dimension(60, 10);
    }

    //--------------------------------------------------------------------------
    public Dimension getPreferredSize() {
        return new Dimension(60, 15);
    }

    //--------------------------------------------------------------------------
    public boolean isRunning() {
        return _spinner.isRunning();
    }

    //--------------------------------------------------------------------------
    public void start() {
        _spinner.start();
    }

    ////////////////////////////////////////////////////////////////////////////

    //--------------------------------------------------------------------------
    public void stop() {
        _spinner.stop();
    }

    private class _PongSpinner extends Spinner {

        private final Color _green3 = new Color(0, 50, 0);
        private final Color _green2 = new Color(0, 100, 0);
        private final Color _green1 = new Color(0, 150, 0);
        private final Color _green0 = new Color(50, 255, 50);
        private int _numBoxes = 12;
        private final double _inc = _numBoxes / 50.0;
        private double _boost = 4.0 * _inc;
        private double _counter = 0.0;
        private boolean _goingUp = true;
        private double _w;
        private double _h;
        private double _boxW;
        private double _innerBoxW;
        private double _innerBoxH;

        //----------------------------------------------------------------------
        public _PongSpinner(int delay, int numBoxes) {
            super(delay);
            _numBoxes = numBoxes;
        }

        //----------------------------------------------------------------------
        private void drawBox(Graphics g, int boxNum, boolean fill, Color color) {

            double xc = (int) (boxNum * _boxW + _boxW / 2.0);
            double yc = (int) (_h / 2.0);
            int bx = (int) (xc - _innerBoxW / 2.0);
            int by = (int) (yc - _innerBoxH / 2.0);

            g.setColor(color);
            if (fill) {
                g.fillRect(bx, by, (int) _innerBoxW, (int) _innerBoxH);
                g.setColor(Color.green);
                //g.drawRect(bx, by, (int)_innerBoxW, (int)_innerBoxH);
            } else {
                //g.drawRect(bx, by, (int)_innerBoxW, (int)_innerBoxH);
            }
        }

        //----------------------------------------------------------------------
        public void paint(Graphics g) {

            _w = getSize().width;
            _h = getSize().height;

            _boxW = _w / _numBoxes;
            _innerBoxW = _boxW * 0.80;
            _innerBoxH = _h * 0.80;

            // Black background
            g.setColor(Color.black);
            g.fillRect(0, 0, (int) _w, (int) _h);

            if (super.isRunning()) {

                g.setColor(Color.green);

                // Repeat once for each box
                for (int i = 0; i < _numBoxes; i++) {
                    int counter = (int) _counter;
                    drawBox(g, i, (i == counter), _green0);
                    if (_goingUp) {
                        if (counter - 3 > 0) drawBox(g, counter - 3, true, _green3);
                        if (counter - 2 > 0) drawBox(g, counter - 2, true, _green2);
                        if (counter - 1 > 0) drawBox(g, counter - 1, true, _green1);
                    } else {
                        if (counter + 3 < _numBoxes) drawBox(g, counter + 3, true, _green3);
                        if (counter + 2 < _numBoxes) drawBox(g, counter + 2, true, _green2);
                        if (counter + 1 < _numBoxes) drawBox(g, counter + 1, true, _green1);
                    }
                }

                // Determine if it is time to bounce back in the other direction
                if (_goingUp && _counter > _numBoxes) {
                    // Bounce off of right end
                    _goingUp = false;
                    _boost = 4.0 * _inc;
                } else if (!_goingUp && _counter < 0) {
                    // Bounce off of left end
                    _goingUp = true;
                    _boost = 4.0 * _inc;
                }

                // Go faster right after bounce, then slow down
                if (_goingUp) {
                    _counter += _inc;
                    if (_boost > 0) _counter += _boost;
                    _boost -= _inc * 0.5;
                } else {
                    _counter -= _inc;
                    if (_boost > 0) _counter -= _boost;
                    _boost -= _inc * 0.5;
                }
            }
        }
    }
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7         
*/

