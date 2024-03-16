//----------------------------------------------------------------------------
// $RCSfile: RadarSpinner.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/01 16:07:53 $
//----------------------------------------------------------------------------

package org.relayirc.swingutil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Radar-style progress indicator with rotating chord inside circle. 
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
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class RadarSpinner extends Spinner {

   private double  _angle = 0.0;
   private final double  _inc = (2.0*Math.PI)/30.0;

   //--------------------------------------------------------------------------
   public RadarSpinner(int delay) {
      super(delay);
   }
   //--------------------------------------------------------------------------
   /** Paint the offscreen image to the screen. */
   public void paint(Graphics g) {

      double w = getSize().width;  // Width of component
      double h = getSize().height;
      int    xc = (int)(w/2.0);    // Center of component 
      int    yc = (int)(h/2.0);

      // Black background
      g.setColor(Color.black);
      g.fillRect(0,0,(int)w,(int)h);
      
      // Draw green circle
      double circle_sz = 0.70; 
      double radius = w < h ? (w*circle_sz)/2.0: (h*circle_sz)/2.0; 

      int xo = (int)(xc - radius); // Top left corner of circle
      int yo = (int)(yc - radius); 
      int diameter = (int)(2.0*radius);

      g.setColor(Color.green);
      g.drawOval(xo,yo,diameter,diameter);

      // Draw green square around circle
      double square_sz = 0.90;  
      double width = w < h ? (w*square_sz): (h*square_sz); 
      int xb = (int)(xc - width/2.0);
      int yb = (int)(yc - width/2.0);
      g.setColor(Color.green);
      g.drawRect(xb,yb,(int)width,(int)width);

      // Draw rotating line within circle
      int ax = (int)(radius*Math.cos(_angle));
      int ay = (int)(radius*Math.sin(_angle));
      g.drawLine(xc,yc,xc+ax,yc+ay);
      _angle = (_angle > 2.0*Math.PI) ? 0.0 : _angle + _inc;
   }
   //--------------------------------------------------------------------------
   public static void main(String[] args) {

      System.out.println("ProgressMeter test program");

      final RadarSpinner progMeter = new RadarSpinner(5);

      JFrame frame = new JFrame();
      frame.getContentPane().setLayout(new BorderLayout());

      frame.getContentPane().add(progMeter,BorderLayout.CENTER);

      JButton toggleButton = new JButton("Start/Stop");
      frame.getContentPane().add(toggleButton,BorderLayout.SOUTH);
      toggleButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent event) {
            if (progMeter.isRunning()) 
               progMeter.stop();
            else
               progMeter.start();
         }
      });

      frame.setSize(400,300);
      StandardDlg.centerOnScreen(frame);
      frame.setVisible(true);
   }
}

/*
01234567890123456789012345678901234567890123456789012345678901234567890123456789
0         1         2         3         4         5         6         7         
*/

