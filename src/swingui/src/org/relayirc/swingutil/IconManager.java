//-----------------------------------------------------------------------------
// $RCSfile: IconManager.java,v $
// $Revision: 1.1.2.4 $
// $Author: snoopdave $
// $Date: 2001/04/07 18:55:09 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;
import org.relayirc.util.Debug;

import javax.swing.ImageIcon;
import java.util.Hashtable;

/** 
 * Loads icons in ./images and hashes them by file name.
 * Assumes that images are in images sub-directory.
 * @author David M. Johnson
 * @version $Revision: 1.1.2.4 $
 *
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/</p>
 * Original Code: Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson <br>
 * Contributor(s): No contributors to this file <br>
 * Copyright (C) 1997-2000 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class IconManager {

   /** Fetch icon by name. */
   public static ImageIcon getIcon(String name) {
      ImageIcon icon = (ImageIcon)_icons.get(name+".gif");
      return icon; 
   }   

   /** Array of available icon names. */
   public static String[] iconNames = {
      "Binocular.gif",
      "Bold.gif",
      "Check.gif",
      "Delete.gif",
      "DeleteRow.gif",
      "DocumentDraw.gif",
      "Down.gif",
      "Favorite.gif",
      "Folder.gif",
      "GreenFlag.gif",
      "Hammer.gif",
      "Help.gif",
      "Inform.gif",
      "Open.gif",
      "Palette.gif",
      "Plug.gif",
      "Plus.gif",
      "ReplyAll.gif",
      "Save.gif",
      "TileHorizontal.gif",
      "TileVertical.gif",
      "TileCascade.gif",
      "Up.gif",
      "UnPlug.gif",
      "UpdateRow.gif",
      "User.gif",
      "Users.gif",
      "Workstation.gif"
   };

   private static Hashtable _icons = new Hashtable();

   static {
      for (int i=0; i<iconNames.length; i++) {
         String fileName = "images/"+iconNames[i];
         try {
            ImageIcon icon = 
              new ImageIcon(Class.forName(
                 "org.relayirc.swingutil.IconManager").getResource(fileName));
            _icons.put(iconNames[i],icon);
         }
         catch (Exception e) {
            Debug.println(
               "Icon manager: error reading image ["+fileName+"]");
            e.printStackTrace();
         }
      }
   }

}

