//-----------------------------------------------------------------------------
// $RCSfile: PythonPanel.java,v $
// $Revision: 1.1.2.2 $
// $Author: snoopdave $
// $Date: 2001/04/07 18:55:09 $
//-----------------------------------------------------------------------------

package org.relayirc.swingutil;

import org.python.util.PythonInterpreter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.StringWriter;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Interactive Jython panel that emulates the one in PythonWin IDE.
 *
 * @author David M Johnson
 * @version $Revision: 1.1.2.2 $
 * <p>The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL</p>
 * Original Code: Relay IRC Chat Engine<br>
 * Initial Developer: David M. Johnson<br>
 * Contributor(s): No contributors to this file (yet)<br>
 * Copyright (C) 1997-2024 by David M. Johnson<br>
 * All Rights Reserved.
 */
public class PythonPanel extends JPanel {

    public final static int INDENT = 3;
    public final static int PROMPT_LENGTH = 3;
    public final static String START_PROMPT = ">>>";
    public final static String CONTINUE_PROMPT = "...";
    private final JTextArea _textArea = new JTextArea();
    private PythonInterpreter _python = null;

    //----------------------------------------------------------------------

    /**
     * Construct PythonPanel and its embedded Jython interpreter.
     */
    public PythonPanel() {
        _python = new PythonInterpreter();
        _python.exec("zzz=1"); // warm-up the new interpreter
        init();
    }

    //----------------------------------------------------------------------

    /**
     * Construct PythonPanel to use existing interpreter
     */
    public PythonPanel(PythonInterpreter pi) {
        _python = pi;
        init();
    }

    /**
     * For testing: turn on debugging and show a JFrame with PythonPanel.
     */

    public static void main(String[] args) {
        debug.setDebug(true);
        JFrame frame = new JFrame("PythonPanel Test Frame");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(-1);
            }
        });
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(new PythonPanel()));
        frame.setSize(500, 300);
        frame.setLocation(250, 400);
        frame.setVisible(true);
    }
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    private void init() {

        _textArea.setFont(new Font("Courier", Font.PLAIN, 12));
        _textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent de) {
                String text = "";
                int len = de.getLength();
                try {
                    text = de.getDocument().getText(de.getOffset(), len);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
                if (text.equals("\n")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            handleNewLine();
                        }
                    });
                }
            }

            public void changedUpdate(DocumentEvent de) {
            }

            public void removeUpdate(DocumentEvent de) {
            }
        });

        firstLine();

        setLayout(new BorderLayout());
        add(new JScrollPane(_textArea), BorderLayout.CENTER);
    }
    //----------------------------------------------------------------------

    /**
     * This is where the emulate-PythonWin logic lives.
     */

    private void handleNewLine() {

        int pos = _textArea.getCaretPosition();
        Document doc = _textArea.getDocument();

        try {
            // Get line entered by user
            int prevline = searchBack("\n", pos - 2);
            String line = doc.getText(prevline + 1, (pos - prevline));
            line = line.trim();

            boolean isCodeLine = line.startsWith(START_PROMPT)
                    || line.startsWith(CONTINUE_PROMPT);

            int prevPrompt = searchBack(START_PROMPT, doc.getLength());
            int nextPrompt = searchForth(START_PROMPT, pos);

            debug.println("[" + line + "]");
            debug.println(
                    "pos =" + pos + " prevline=" + prevline + " iscode=" + isCodeLine);

            // On empty start prompt before prev prompt -> send caret to new prompt
            if (line.equals(START_PROMPT) && pos < prevPrompt) {
                debug.println("A");
                removeNewLine(pos);
                newLine();
            }
            // On empty start prompt line at end -> send caret to new prompt
            else if (line.equals(START_PROMPT) && pos > prevPrompt) {
                debug.println("B");
                newPrompt();
            }
            // On empty continue prompt line -> execute script then new prompt
            else if (line.equals(CONTINUE_PROMPT) && pos > prevPrompt) {
                debug.println("C");
                executeScript();
                newPrompt();
            }
            // On non-code line before last prompt -> send caret to new prompt
            else if (!isCodeLine && pos < prevPrompt) {
                debug.println("D");
                removeNewLine(pos);
                newLine();
            }
            // On code line before last start prompt -> grab script, put it at end
            else if (isCodeLine && pos < prevPrompt) {
                debug.println("E");
                removeNewLine(pos);
                grabCode(pos);
            }
            // Within code after last start prompt -> execute code
            else if (isCodeLine && nextPrompt == -1 && pos < doc.getLength() - 1) {
                debug.println("F");
                removeNewLine(pos);
                executeScript();
                newPrompt();
            }
            // Within text in current script -> send caret to new prompt
            else if (isCodeLine && pos < doc.getLength() - 1) {
                debug.println("G");
                newPrompt();
            }
            // At end of code line ending with a colon -> increate indent
            else if (isCodeLine && line.endsWith(":")) {
                debug.println("H");
                indentLine(_textArea.getCaretPosition(), figureIndent(line) + INDENT);
            }
            // At end of start prompt line with no colon -> execute line
            else if (line.startsWith(START_PROMPT)) {
                debug.println("I");
                executeScript();
                newPrompt();
            }
            // At end of ... prompt line with no colon -> indent
            else if (line.startsWith(CONTINUE_PROMPT)) {
                debug.println("J");
                indentLine(_textArea.getCaretPosition(), figureIndent(line));
            } else {
                debug.println("K");
                newPrompt();
            }
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    //----------------------------------------------------------------------

    /**
     * Remove newline at specified position.
     */

    private void removeNewLine(int pos) {
        try {
            Document doc = _textArea.getDocument();
            doc.remove(pos - 1, 1);
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    //----------------------------------------------------------------------

    /**
     * Grab the script at specified postion, copy it and paste it to the
     * end of the text area.
     */
    private void grabCode(int pos) {

        int prevPrompt = 0;
        try {
            // Script begins at last start prompt before pos and ends
            // at last continue prompt after pos.
            Document doc = _textArea.getDocument();
            int bos = searchBack(START_PROMPT, pos) + PROMPT_LENGTH + 1;

            int nextNewLine = searchForth("\n", pos);
            int eos = searchForth(START_PROMPT, pos) - 1;
            int lastCont = searchBack(CONTINUE_PROMPT, eos) - 1;
            if (lastCont < bos) {
                eos = nextNewLine;
            } else {
                eos = lastCont;
            }

            // Put script at end
            String script = doc.getText(bos, eos - bos);
            debug.println("<GRABBED>\n" + script + "\n</GRABBED>\n");
            doc.insertString(doc.getLength() - 1, script, null);
            _textArea.setCaretPosition(doc.getLength() - 1);
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    //----------------------------------------------------------------------

    /**
     * Execute the script at end of text area.
     */

    private void executeScript() {
        try {
            // Script begins a last start prompt and ends at end of text area
            Document doc = _textArea.getDocument();
            int eos = doc.getLength() - 1;
            int bos = searchBack(START_PROMPT, eos);
            debug.println("bos=" + bos + " eos=" + eos);

            // Grab the script, strip off start and continue prompts
            String script = "";
            String rawScript = doc.getText(bos, eos - bos);
            StringTokenizer toker = new StringTokenizer(rawScript, "\n");
            while (toker.hasMoreTokens()) {
                try {
                    String token = toker.nextToken();
                    script += token.substring(PROMPT_LENGTH + 1) + "\n";
                } catch (NoSuchElementException nsee) {
                    // Ignored by design
                }
            }

            // Execute the script
            debug.println("<SCRIPT>\n" + script + "\n</SCRIPT>\n");
            StringWriter outw = new StringWriter();
            StringWriter errw = new StringWriter();
            _python.setOut(outw);
            _python.setErr(errw);
            try {
                _python.exec(script);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Print out theo script results
            if (errw.toString().length() > 0) {
                doc.insertString(doc.getLength() - 1, errw.toString(), null);
            } else {
                doc.insertString(doc.getLength() - 1, outw.toString(), null);
            }
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    //----------------------------------------------------------------------

    /**
     * Creates new line at end of text area and sends caret there.
     */

    private void newLine() {
        Document doc = _textArea.getDocument();
        int pos = doc.getLength() > 0 ? doc.getLength() - 1 : 0;
        try {
            doc.insertString(pos, "\n", null);
            _textArea.setCaretPosition(doc.getLength() - 1);
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    //----------------------------------------------------------------------

    /**
     * Creates banner and initial start prompt.
     */

    private void firstLine() {
        Document doc = _textArea.getDocument();
        int pos = doc.getLength() > 0 ? doc.getLength() - 1 : 0;
        try {
            String banner = "Jython is Copyright (c) 1997-2001 CNRI\n";
            banner += "https://www.jython.org\n";
            doc.insertString(pos, banner, null);
            doc.insertString(doc.getLength() - 1, "\n", null);
            _textArea.setCaretPosition(doc.getLength() - 1);
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    //----------------------------------------------------------------------

    /**
     * Creates new prompt at end of text area and sends caret there.
     */

    private void newPrompt() {
        Document doc = _textArea.getDocument();
        int pos = doc.getLength() > 0 ? doc.getLength() - 1 : 0;
        String prefix = START_PROMPT + " ";
        try {
            doc.insertString(pos, prefix, null);
            _textArea.setCaretPosition(pos + prefix.length());
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    //----------------------------------------------------------------------

    /**
     * Determine indent level of a string.
     */

    private int figureIndent(String inputstr) {
        String line = inputstr.substring(PROMPT_LENGTH + 1);
        StringBuffer strbuf = new StringBuffer(line);
        int indent = 0;
        for (int i = 0; i < line.length(); i++) {
            if (Character.isSpaceChar(strbuf.charAt(i)))
                indent++;
            else
                break;
        }
        return indent;
    }
    //----------------------------------------------------------------------

    /**
     * Indents a new line of code at a specified caret position and sends
     * caret to end of new line.
     *
     * @param pos         Position of line to be indented
     * @param indentLevel Number of spaces to indent
     */
    private void indentLine(int pos, int indentLevel) {
        Document doc = _textArea.getDocument();

        char[] indentChars = new char[indentLevel];
        for (int i = 0; i < indentLevel; i++) indentChars[i] = ' ';
        String prefix = CONTINUE_PROMPT + " " + new String(indentChars);

        try {
            doc.insertString(pos, prefix, null);
            _textArea.setCaretPosition(pos + prefix.length());
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    //----------------------------------------------------------------------

    /**
     * Search text area backwards for str, starting at pos.
     *
     * @param str Search string
     * @param pos Starting point of search
     * @return Index of str
     */
    private int searchBack(String str, int pos) {
        int result = -1;
        Document doc = _textArea.getDocument();

        while (pos >= 0) {
            try {
                String curstr = doc.getText(pos, str.length());
                if (curstr.equals(str)) {
                    result = pos;
                    break;
                }
            } catch (BadLocationException ble) {
                // Ignored by design
            }
            pos--;
        }
        return result;
    }

    /**
     * Seach text area forwards for str, starting at pos.
     *
     * @param str Search string
     * @param pos Starting point of search
     * @return Index of str, or -1 on error.
     */
    private int searchForth(String str, int pos) {
        int result = -1;
        Document doc = _textArea.getDocument();

        while (pos < doc.getLength()) {
            try {
                String curstr = doc.getText(pos, str.length());
                if (curstr.equals(str)) {
                    result = pos;
                    break;
                }
            } catch (BadLocationException ble) {
                // Ignored by design
            }
            pos++;
        }
        return result;
    }
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    static class debug {
        private static boolean _on = false;

        public static void setDebug(boolean flag) {
            _on = flag;
        }

        public static void println(String txt) {
            if (_on) System.out.println(txt);
        }
    }
}


