/*
 * FILE: MDIPanel.java
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
 * Contributor(s): No contributors to this file.
 */

package org.relayirc.swingutil;

import org.relayirc.util.Debug;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.Vector;

///////////////////////////////////////////////////////////////////////

/**
 * Multi-Document Interface (MDI) panel that supports dockable frames.
 * Panels may be docked in the top, bottom, left or right of the panel.
 * Each panel must implement MDIClientPanel and have a frame that
 * implements MDIClientFrame.
 * @see org.relayirc.swingutil.MDIClientPanel
 * @see org.relayirc.swingutil.MDIClientFrame
 */
public class MDIPanel extends JPanel {

   public static final String DOCK_NONE   = "DOCK_NONE";
   public static final String DOCK_TOP    = "DOCK_TOP";
   public static final String DOCK_BOTTOM = "DOCK_BOTTOM";
   public static final String DOCK_LEFT   = "DOCK_LEFT";
   public static final String DOCK_RIGHT  = "DOCK_RIGHT";

   private final JDesktopPane _desktop = new JDesktopPane();
	private final Vector       _clientFrames = new Vector();
	private final Vector       _clientPanels = new Vector();
   private final Hashtable    _dockedFrames = new Hashtable();
   private final Hashtable    _framesByPanel = new Hashtable();
   private int          _lastX = 0;
   private int          _lastY = 0;

   //-----------------------------------------------------------------
   public MDIPanel() {
      setLayout(new MDILayout());
      _desktop.setDesktopManager(new MDIDesktopManager());
      add(_desktop,"desktop");
      _desktop.addMouseMotionListener(new MouseMotionListener() {
         public void mouseMoved(MouseEvent event) {
            setCursor(Cursor.getDefaultCursor());
         }
         public void mouseDragged(MouseEvent event) {
         }
      });
   }
   //------------------------------------------------------------------
   private void addInternalFrame(JInternalFrame frame) {
      int w = (int)(_desktop.getSize().width*0.6);
      int h = (int)(0.6*w);

      int newX = _lastX==0 ? 0 : _lastX + 20;
      int newY = _lastY==0 ? 0 : _lastY + 20;

      if (newX + frame.getSize().width > _desktop.getSize().width
       || newY + frame.getSize().height > _desktop.getSize().height) {
         newX = 0;
         newY = 0;
      }
      _lastX = newX;
      _lastY = newY;

      _desktop.add(frame);
      frame.setSize(new Dimension(w,h));
      frame.setLocation(newX,newY);
      frame.show();

      Debug.println("MDIPanel adding frame "+frame.getSize());
   }
   //-----------------------------------------------------------------
   /** If frame is not docked, then bring it to the front */
   public void activateFrame(MDIClientFrame frame) {
      // If frame is not docked then bring it to the front
      if (frame.getClientPanel().getDockState().equals(DOCK_NONE)) {
         //_desktop.getDesktopManager().activateFrame(frame.getFrame());
         try {
            frame.getFrame().setSelected(true);
         }
         catch (PropertyVetoException e) {
            // Ignored by design
         }
      }
   }
   //-----------------------------------------------------------------
   /** Submit frame for management by this MDI panel. */
   public void addClientFrame(MDIClientFrame frame) {

      // Only add frame if it is not already present
      if (!_clientFrames.contains(frame)) {
         _clientFrames.addElement(frame);
         _clientPanels.addElement(frame.getClientPanel());
         _framesByPanel.put(frame.getClientPanel(),frame);

         // New frames should not be maximized, closed or iconified
         try {
            frame.getFrame().setMaximum(false);
            frame.getFrame().setClosed(false);
            frame.getFrame().setIcon(false);
         }
         catch (PropertyVetoException pve) {
            // Ignore vetos
         }

         registerDockState(frame.getClientPanel());
      }
   }
   //-----------------------------------------------------------------
   /** Remove panel from management by this MDI panel. */
   public void removeClientFrame(MDIClientFrame frame) {

      _clientFrames.removeElement(frame);
      _clientPanels.removeElement(frame.getClientPanel());
      _framesByPanel.remove(frame.getClientPanel());
      _dockedFrames.remove(frame.getClientPanel().getDockState());

      if (frame.getClientPanel().getDockState().equals(DOCK_NONE)) {
         Debug.println("Removing undocked frame");

         // Delete does not work if frame is iconified
         try {
            frame.getFrame().setIcon(false);
         }
         catch (PropertyVetoException pve) {
            // Ignore vetos
         }

         _desktop.remove(frame.getFrame());
         _desktop.repaint(0);
      }
      else {
         Debug.println("Removing docked panel");
         remove(frame.getClientPanel().getPanel());
         doLayout();
      }
   }
   //-----------------------------------------------------------------
   /**
    * A panel whose frame has been added to the MDIPanel using the
    * addClientFrame() function should call this function after
    * changing its dock state.
    */
   public void registerDockState(MDIClientPanel panel) {

      // If docking
      if (!panel.getDockState().equals(DOCK_NONE)) {

         // Hide panel's current frame
         MDIClientFrame frame = (MDIClientFrame)_framesByPanel.get(panel);
         _desktop.remove(frame.getFrame());
         frame.getFrame().setVisible(false);
         frame.getFrame().getContentPane().remove(panel.getPanel());

         // Add panel's frame into its requested dock site
         _dockedFrames.put(panel.getDockState(),frame);
         add(panel.getPanel(),panel.getDockState());
      }

      // Else if not docking
      else {
         // Remove panel from layout
         remove(panel.getPanel());

         // Remove panel's frame from docked frames map
         MDIClientFrame frame = (MDIClientFrame)_framesByPanel.get(panel);
         _dockedFrames.remove(frame);

         // Add panel's frame to desktop
         frame.getFrame().getContentPane().add(panel.getPanel(),BorderLayout.CENTER);
         frame.getFrame().getContentPane().doLayout();

         //Debug.println("layout="+frame.getFrame().getContentPane().getLayout());
         //Debug.println("count="+frame.getFrame().getContentPane().getComponentCount());

         addInternalFrame(frame.getFrame());
      }
      doLayout();
   }
   //------------------------------------------------------------------
   /** Tile four client panels in a grid. */
   public void tileFour() {
      int winwidth = (int)(_desktop.getSize().width/2.0);
      // The -30 leaves some space at the bottom for icons
      int winheight = (int)((_desktop.getSize().height-30)/2.0);
      int count = 0;
      for (int j=0; j<_desktop.getAllFrames().length; j++) {
         JInternalFrame cwin = _desktop.getAllFrames()[j];
         if (!cwin.isIcon()) {
            cwin.setSize(new Dimension(winwidth,winheight));
            switch (count) {
               case 0:
                  cwin.setLocation(0,0);
                  break;
               case 1:
                  cwin.setLocation(winwidth,0);
                  break;
               case 2:
                  cwin.setLocation(0,winheight);
                  break;
               case 3:
                  cwin.setLocation(winwidth,winheight);
                  break;
               default:
                  break;
            }
            count++;
         }
      }
   }
   //------------------------------------------------------------------
   /** Tile client frames in a cascade pattern. */
   public void cascade() {
      // Count the not iconified internal frames
      int wincount = 0;
      for (int i=0; i<_desktop.getAllFrames().length; i++) {
         JInternalFrame cwin = _desktop.getAllFrames()[i];
         if (!cwin.isIcon()) wincount++;
      }
      if (wincount > 0) {

         int w = (int)(_desktop.getSize().width*0.6);
         int h = (int)(0.6*w);

         int xpos = 0;
         int ypos = 0;
         for (int j=0; j<_desktop.getAllFrames().length; j++) {
            JInternalFrame cwin = _desktop.getAllFrames()[j];
            if (!cwin.isIcon()) {
               if (xpos + cwin.getSize().width > _desktop.getSize().width
                || ypos + cwin.getSize().height > _desktop.getSize().height) {
                  ypos = 0;
                  xpos = 0;
               } else {
                  ypos += 20;
                  xpos += 20;
               }
               cwin.setSize(new Dimension(w,h));
               cwin.setLocation(xpos,ypos);
            }
         }
      }
   }
   //------------------------------------------------------------------
   /** Tile client frames horizontally. */
   public void tileHorizontal() {

      // Count the not iconified internal frames
      int wincount = 0;
      for (int i=0; i<_desktop.getAllFrames().length; i++) {
         JInternalFrame cwin = _desktop.getAllFrames()[i];
         if (!cwin.isIcon()) wincount++;
      }
      // Tile those frames
      if (wincount==4)
         tileFour();
      else if (wincount > 0) {
         int winwidth = _desktop.getSize().width;
         // The -30 leaves some space at the bottom for icons
         int winheight = (_desktop.getSize().height-30)/wincount;
         int ypos = 0;
         for (int j=0; j<_desktop.getAllFrames().length; j++) {
            JInternalFrame cwin = _desktop.getAllFrames()[j];
            if (!cwin.isIcon()) {
               cwin.setSize(new Dimension(winwidth,winheight));
               cwin.setLocation(0,ypos);
               ypos += winheight;
            }
         }
      }
   }
   //------------------------------------------------------------------
   /** Tile client frames vertically. */
   public void tileVertical() {

      // Count the currently active internal frames
      int wincount = 0;
      for (int i=0; i<_desktop.getAllFrames().length; i++) {
         JInternalFrame cwin = _desktop.getAllFrames()[i];
         if (!cwin.isIcon()) wincount++;
      }

      // If there are any active internal frames then tile them
       if (wincount==4)
         tileFour();
      else if (wincount > 0) {
         int winwidth = _desktop.getSize().width/wincount;
         // The -30 leaves some space at the bottom for icons
         int winheight = _desktop.getSize().height-30;
         int xpos = 0;
         for (int j=0; j<_desktop.getAllFrames().length; j++) {
            JInternalFrame cwin = _desktop.getAllFrames()[j];
            if (!cwin.isIcon()) {
               cwin.setSize(new Dimension(winwidth,winheight));
               cwin.setLocation(xpos,0);
               xpos += winwidth;
            }
         }
      }
   }
   //-----------------------------------------------------------------
   /** For testing/debugging. */
   public static void main(String[] args) {

      MDIPanel mdi;

      JFrame frame = new JFrame();
      frame.getContentPane().setLayout(new BorderLayout());
      frame.getContentPane().add(mdi = new MDIPanel(),BorderLayout.CENTER);
      frame.setLocation(200,200);
      frame.setSize(500,400);
      frame.setVisible(true);

      ClientPanel panel1 = new ClientPanel(mdi, MDIPanel.DOCK_TOP,    MDIPanel.DOCK_TOP);
      ClientPanel panel2 = new ClientPanel(mdi, MDIPanel.DOCK_BOTTOM, MDIPanel.DOCK_BOTTOM);
      ClientPanel panel3 = new ClientPanel(mdi, MDIPanel.DOCK_RIGHT,  MDIPanel.DOCK_RIGHT);
      ClientPanel panel4 = new ClientPanel(mdi, MDIPanel.DOCK_LEFT,   MDIPanel.DOCK_LEFT);

      ClientFrame frame1 = new ClientFrame();
      frame1.setClientPanel(panel1);

      ClientFrame frame2 = new ClientFrame();
      frame2.setClientPanel(panel2);

      ClientFrame frame3 = new ClientFrame();
      frame3.setClientPanel(panel3);

      ClientFrame frame4 = new ClientFrame();
      frame4.setClientPanel(panel4);

      mdi.addClientFrame(frame1);
      mdi.addClientFrame(frame2);
      mdi.addClientFrame(frame3);
      mdi.addClientFrame(frame4);

      frame.doLayout();
   }
}


///////////////////////////////////////////////////////////////////////

class MDILayout implements LayoutManager {

   private Component _desktop = null;
   private Component _top     = null;
   private Component _bottom  = null;
   private Component _left    = null;
   private Component _right   = null;

   private MDISplitter _vSplitterTop    = null;
   private MDISplitter _vSplitterBottom = null;
   private MDISplitter _hSplitterLeft   = null;
   private MDISplitter _hSplitterRight  = null;

   //-----------------------------------------------------------------
   public void addLayoutComponent(String name, Component comp) {
      if      (name.equals("desktop"))            _desktop = comp;
      else if (name.equals(MDIPanel.DOCK_TOP))    _top     = comp;
      else if (name.equals(MDIPanel.DOCK_BOTTOM)) _bottom  = comp;
      else if (name.equals(MDIPanel.DOCK_LEFT))   _left    = comp;
      else if (name.equals(MDIPanel.DOCK_RIGHT))  _right   = comp;
   }
   //-----------------------------------------------------------------
   public void removeLayoutComponent(Component comp) {
      if      (comp == _desktop) _desktop = null;
      else if (comp == _top)     _top = null;
      else if (comp == _bottom)  _bottom = null;
      else if (comp == _left)    _left = null;
      else if (comp == _right)   _right = null;
   }
   //-----------------------------------------------------------------
   public Dimension preferredLayoutSize(Container parent) {
      return new Dimension(100,100);
   }
   //-----------------------------------------------------------------
   public Dimension minimumLayoutSize(Container parent) {
      return new Dimension(100,100);
   }
   //-----------------------------------------------------------------
   public void layoutContainer(Container parent) {
      if (_desktop != null) {

         int height = parent.getSize().height;
         int width = parent.getSize().width;
         if (height==0 || width==0) {
            return;
         }

         // Bounds of desktop pane
         int minX = 0;
         int minY = 0;
         int maxX = width;
         int maxY = height;

         // Splitter positions
         float tpos = 0.0f;
         float bpos = 0.0f;
         float lpos = 0.0f;
         float rpos = 0.0f;

         // Remove splitters
         try {parent.remove(_vSplitterTop);}    catch (NullPointerException npe) {}
         try {parent.remove(_vSplitterBottom);} catch (NullPointerException npe) {}
         try {parent.remove(_hSplitterLeft);}   catch (NullPointerException npe) {}
         try {parent.remove(_hSplitterRight);}  catch (NullPointerException npe) {}

         // Add splitters back as needed
         if (_top!=null && _vSplitterTop==null) {
            _vSplitterTop = new MDISplitter(MDISplitter.VERTICAL,0.2f);
            parent.add(_vSplitterTop);
         }
         if (_bottom!=null && _vSplitterBottom==null) {
            _vSplitterBottom = new MDISplitter(MDISplitter.VERTICAL,0.8f);
            parent.add(_vSplitterBottom);
         }
         if (_left!=null && _hSplitterLeft==null) {
            _hSplitterLeft = new MDISplitter(MDISplitter.HORIZONTAL,0.2f);
            parent.add(_hSplitterLeft);
         }
         if (_right!=null && _hSplitterRight==null) {
            _hSplitterRight= new MDISplitter(MDISplitter.HORIZONTAL,0.8f);
            parent.add(_hSplitterRight);
         }

         // Figure desktop pane boundaries based on splitter positions
         if (_top!=null) {
            tpos = _vSplitterTop.getPosition();
            minY = (int)(height*tpos) + 5;
            minY = minY<5 ? 5 : minY;
            minY = minY>height-5 ? height-5 : minY;
         }
         if (_bottom!=null) {
            bpos = _vSplitterBottom.getPosition();
            maxY = (int)(height*bpos);
            maxY = maxY<5 ? 5 : maxY;
            maxY = maxY>height-5 ? height-5 : maxY;
         }
         if (_left!=null) {
            lpos = _hSplitterLeft.getPosition();
            minX = (int)(width*lpos) + 5;
            minX = minX<5 ? 5 : minX;
            minX = minX>width-5 ? width-5 : minX;
         }
         if (_right!=null) {
            rpos = _hSplitterRight.getPosition();
            maxX = (int)(width*rpos);
            maxX = maxX<5 ? 5 : maxX;
            maxX = maxX>width-5 ? width-5 : maxX;
         }

         // Lay out top and bottom components

         if (_top!=null && _bottom!=null) {
            if (_vSplitterTop.isDragging() && minY>maxY) {
               maxY = minY;
            }
            else if (maxY<minY) {
            //else if (_vSplitterBottom.isDragging() && maxY<minY) {
               minY = maxY;
            }
         }
         if (_top!=null) {
            _top.setBounds(0,0,width,minY-5);
            _top.doLayout();

            parent.add(_vSplitterTop);
            _vSplitterTop.setBounds(0,minY-5,width,5);
         }
         if (_bottom!=null) {
            _bottom.setBounds(0,maxY+5,width,height-maxY-5);
            _bottom.doLayout();

            parent.add(_vSplitterBottom);
            _vSplitterBottom.setBounds(0,maxY,width,5);
         }

         // Lay out left and right components

         if (_left!=null && _right!=null) {
            if (_hSplitterLeft.isDragging() && minX>maxX) {
               maxX = minX;
            }
            else if (maxX<minX) {
            //else if (_hSplitterRight.isDragging() && maxX<minX) {
               minX = maxX;
            }
         }
         if (_left!=null) {
            _left.setBounds(0,minY,minX-5,maxY-minY);
            _left.doLayout();

            parent.add(_hSplitterLeft);
            _hSplitterLeft.setBounds(minX-5,minY,5,maxY-minY);
         }
         if (_right!=null) {
            _right.setBounds(maxX+5,minY,width-maxX-5,maxY-minY);
            _right.doLayout();

            parent.add(_hSplitterRight);
            _hSplitterRight.setBounds(maxX,minY,5,maxY-minY);
         }

         // Position desktop pane in middle
         _desktop.setBounds(minX,minY,maxX-minX,maxY-minY);
      }
   }
}

////////////////////////////////////////////////////////////////////////

class MDIDesktopManager extends DefaultDesktopManager {

   /** Don't allow internal frames to be dragged over bounds */
   public void dragFrame(JComponent f, int newX, int newY) {

      if (f instanceof JInternalFrame frame) {
          JDesktopPane desktop = frame.getDesktopPane();

         if (newX + frame.getSize().width > desktop.getSize().width) {
            newX = desktop.getSize().width - frame.getSize().width;
         }
         if (newX < 0) {
            newX = 0;
         }
         if (newY + frame.getSize().height > desktop.getSize().height) {
            newY = desktop.getSize().height - frame.getSize().height;
         }
         if (newY < 0) {
            newY = 0;
         }
         super.dragFrame(f,newX,newY);
      }
   }

   /** Don't allow internal frames to be resized over bounds */
   public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {

      if (f instanceof JInternalFrame frame) {
          JDesktopPane desktop = frame.getDesktopPane();

         if (newX < 0) {
            newX = 0;
            newWidth = frame.getSize().width;
         }
         else if (newX + newWidth > desktop.getSize().width) {
            newWidth = desktop.getSize().width - newX;
         }

         if (newY < 0) {
            newY = 0;
            newHeight = frame.getSize().height;
         }
         else if (newY + newHeight > desktop.getSize().height) {
            newHeight = desktop.getSize().height - newY;
         }

         super.resizeFrame(f,newX,newY,newWidth,newHeight);
      }
   }
}

///////////////////////////////////////////////////////////////////////

class MDISplitter extends JPanel {

   public final static int VERTICAL = 1;
   public final static int HORIZONTAL = 2;

   private int _orientation = VERTICAL;
   private float _position = 0.2f;
   private boolean _isDragging = false;

   //-----------------------------------------------------------------
   public MDISplitter(int orientation, float position) {
      _orientation = orientation;
      _position = position;
      setBorder(new BevelBorder(BevelBorder.RAISED));

      addMouseMotionListener(new MouseMotionListener() {
         public void mouseMoved(MouseEvent event) {
            if (_orientation == VERTICAL)
               setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            else
               setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
         }
         public void mouseDragged(MouseEvent event) {
            if (_orientation == VERTICAL) {
               int newDevPos = getBounds().y + event.getY();
               _position = (float)newDevPos / (float)getParent().getSize().height;
            }
            else {
               int newDevPos = getBounds().x + event.getX();
               _position = (float)newDevPos / (float)getParent().getSize().width;
            }
            if (_position < 0.0f) _position = 0.0f;
            if (_position > 1.0f) _position = 1.0f;
            _isDragging = true;
            getParent().doLayout();
            _isDragging = false;
         }
      });
   }
   //-----------------------------------------------------------------
   public void setBounds(int x, int y, int w, int h) {
      if (_orientation == VERTICAL) {
         _position = (float)y / (float)getParent().getSize().height;
      }
      else {
         _position = (float)x / (float)getParent().getSize().width;
      }
      if (_position < 0.0f) _position = 0.0f;
      if (_position > 1.0f) _position = 1.0f;
      super.setBounds(x,y,w,h);
   }
   //-----------------------------------------------------------------
   public float getPosition() {
      return _position;
   }
   //-----------------------------------------------------------------
   public void setPosition(float position) {
      _position = position;
   }
   //-----------------------------------------------------------------
   public boolean isDragging() {
      return _isDragging;
   }
}

///////////////////////////////////////////////////////////////////////

class ClientPanel extends JPanel implements MDIClientPanel {
   private String _dockSite = MDIPanel.DOCK_TOP;
   private String _dockState = MDIPanel.DOCK_NONE;
   private MDIPanel _mdi = null;

   public String getDockState() {return _dockState;}
   public void   setDockState(String dockState) {_dockState=dockState;}

   public JPanel getPanel() {return this;}

   public ClientPanel(MDIPanel mdi, String dockSite, String dockState) {
      _dockSite = dockSite;
      _dockState = dockState;
      _mdi = mdi;

      setLayout(new BorderLayout());
      JButton btn = new JButton("Hello World");
      add(btn,BorderLayout.CENTER);
      add(new JLabel("SOUTH"),BorderLayout.SOUTH);

      btn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            if (_dockState.equals(_dockSite)) {
               _dockState = MDIPanel.DOCK_NONE;
               //Debug.println("Undocking="+_dockState);
               _mdi.registerDockState(ClientPanel.this);
            }
            else {
               _dockState = _dockSite;
               //Debug.println("Docking="+_dockState);
               _mdi.registerDockState(ClientPanel.this);
            }
         }
      });
   }
}

///////////////////////////////////////////////////////////////////////

class ClientFrame extends JInternalFrame implements MDIClientFrame {
   private MDIClientPanel _panel = null;

   public ClientFrame() {
      // closable, maximizable, iconifiable, resizable
      super("ClientFrame",true,true,true,true);
      getContentPane().setLayout(new BorderLayout());
      setVisible(true);
   }
   public MDIClientPanel getClientPanel() {
      return _panel;
   }
   public void setClientPanel(MDIClientPanel clientPanel) {
      _panel = clientPanel;
   }
   public JInternalFrame getFrame() {
      return this;
   }
}

