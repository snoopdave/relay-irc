//----------------------------------------------------------------------------
// $RCSfile: StatusBar.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 16:07:53 $
//----------------------------------------------------------------------------

package org.relayirc.swingutil;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * Simple status bar with a progress spinner, a timer and a clock display.
 *
 * @author David M. Johnson
 * @version $Revision: 1.1.2.2 $
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
public class StatusBar extends JPanel {

    private final TimerLabel _timer = new TimerLabel();
    private final ClockLabel _clock = new ClockLabel();
    private final PongSpinner _spinner = new PongSpinner(30, 12);

    //-------------------------------------------------------------------
    public StatusBar() {

        setLayout(new BorderLayout());

        _spinner.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(_spinner, BorderLayout.WEST);

        _timer.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(_timer, BorderLayout.CENTER);

        _clock.start();
        _clock.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(_clock, BorderLayout.EAST);
    }

    //-------------------------------------------------------------------
    public static void main(String[] args) {

        StatusBar bar = null;

        JFrame frm = new JFrame();
        frm.getContentPane().setLayout(new BorderLayout());
        frm.getContentPane().add(bar = new StatusBar(), BorderLayout.SOUTH);
        bar.startTimer();
        bar.setStatusText("Connected to IRC.SPRYNET.COM time:", true);
        frm.setSize(500, 500);
        frm.setVisible(true);
    }

    //-------------------------------------------------------------------
    public void setStatusText(String text) {
        _timer.setStatusText(text, false);
    }

    //-------------------------------------------------------------------
    public void setStatusText(String text, boolean timer) {
        _timer.setStatusText(text, timer);
    }

    //-------------------------------------------------------------------
    public void startTimer() {
        _timer.start();
    }

    //-------------------------------------------------------------------
    public void stopTimer() {
        _timer.stop();
    }

    //-------------------------------------------------------------------
    public void startSpinner() {
        _spinner.start();
    }

    //-------------------------------------------------------------------
    public void stopSpinner() {
        _spinner.stop();
    }
}

/////////////////////////////////////////////////////////////////////////

class TimerLabel extends JLabel implements ActionListener {
    protected long _startTime = 0;
    protected javax.swing.Timer _timer = null;
    protected String _text = " ";
    protected boolean _showTimer = false;

    public TimerLabel() {
        super(" ", SwingConstants.CENTER);
        _timer = new javax.swing.Timer(1000, this);
    }

    public void start() {
        _startTime = System.currentTimeMillis();
        _timer.start();
    }

    public void stop() {
        _timer.stop();
        _startTime = 0;
    }

    public void setStatusText(String text, boolean timer) {
        _text = text;
        _showTimer = timer;
    }

    public void actionPerformed(ActionEvent e) {
        if (_showTimer) {
            long now = System.currentTimeMillis() - _startTime;
            long hours = (now / (60 * 60 * 1000));
            long minutes = (now - hours * (60 * 60 * 1000)) / (60 * 1000);
            long seconds = (now - hours * (60 * 60 * 1000) - minutes * (60 * 1000)) / 1000;
            setHorizontalAlignment(SwingConstants.LEFT);
            setText(_text + " " + hours + ":" + minutes + ":" + seconds);
        } else {
            setText(_text);
        }
    }
}

/////////////////////////////////////////////////////////////////////////

class ClockLabel extends TimerLabel {
    public ClockLabel() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        setText(Calendar.getInstance().getTime().toString());
    }
}

