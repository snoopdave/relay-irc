//-----------------------------------------------------------------------------
// $RCSfile: IconManager.java,v $
// $Revision: 1.1.2.4 $
// $Author: snoopdave $
// $Date: 2001/04/07 18:55:09 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.relayirc.util.Debug;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Loads icons in ./images and hashes them by file name.
 * Assumes that images are in images sub-directory.
 *
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
 * Copyright (C) 1997-2024 by David M. Johnson <br>
 * All Rights Reserved.
 */
public class IconManager {

    private static final HashMap<String, ImageIcon> _icons = new HashMap<>();
    /**
     * Array of available icon names.
     */
    public static String[] iconNames = {
            "binocular.svg",
            "bold.svg",
            "check.svg",
            "delete.svg",
            "deleterow.svg",
            "documentdraw.svg",
            "down.svg",
            "favorite.svg",
            "foldericon.svg",
            "greenflag.svg",
            "hammer.svg",
            "inform.svg",
            "help.svg",
            "inform.svg",
            "open.svg",
            "palette.svg",
            "plug.svg",
            "plus.svg",
            "replyall.svg",
            "save.svg",
            "tilehorizontal.svg",
            "tilevertical.svg",
            "tilecascade.svg",
            "up.svg",
            "unplug.svg",
            "updaterow.svg",
            "user.svg",
            "users.svg",
            "workstation.svg"
    };

    static {
        for (String iconName : iconNames) {
            String fileName = "/svg/" + iconName.toLowerCase();
            try {
                ImageIcon icon = svgToImageIcon(Class.forName("org.relayirc.swingutil.IconManager")
                        .getResourceAsStream(fileName), 16, 16);
                _icons.put(iconName, icon);
            } catch (Throwable e) {
                Debug.println("Icon manager: error reading image [" + fileName + "]");
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetch icon by name.
     */
    public static ImageIcon getIcon(String name) {
        ImageIcon icon = _icons.get(name.toLowerCase() + ".svg");
        return icon;
    }

    public static ImageIcon svgToImageIcon(InputStream svgInputStream, float width, float height) throws TranscoderException, IOException {
        // Set up the SVG input
        TranscoderInput input = new TranscoderInput(svgInputStream);

        // Create a byte array output stream to hold the PNG data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outputStream);

        // Set up the PNG transcoder
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);

        // Perform the transcoding
        transcoder.transcode(input, output);
        outputStream.flush();

        // Convert the byte array to a BufferedImage
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));

        // Return the BufferedImage as an ImageIcon
        return new ImageIcon(image);
    }
}

