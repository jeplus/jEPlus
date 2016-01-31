/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yi@jeplus.org>                          *
 *                                                                         *
 *   This program is free software: you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 *                                                                         *
 ***************************************************************************/
package jeplus.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.text.DefaultCaret;
import org.slf4j.LoggerFactory;

/**
 * Text panel for displaying texts and simple editting
 * @author Yi Zhang
 * @version 0.1
 * @since 0.1
 */
public class EPlusTextPanelOld extends JEPlusPrintablePanel implements Runnable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusTextPanelOld.class);

    /** Constant for viewer-only mode */
    public static final int VIEWER_MODE = 0;
    /** Constant for editing mode */
    public static final int EDITOR_MODE = 1;//
    /** Constant for stream viewer mode */
    public static final int STREAM_VIEWER_MODE = 2;
    /** Constant for minimalist viewer mode */
    public static final int MINIMUM_MODE = 3;
    /** Mode of this text panel. Select between viewer and editor */
    protected int Mode = 0;

    /** Title of this TextPanel */
    protected String Title = null;
    /** Has the content been changed by editing or not */
    protected boolean ContentChanged = false;
    /** Current line wrapping status of the text area */
    protected boolean CurrentLineWrap = true;
    /** Current viewing/editing file name */
    protected String CurrentFileName = null;

    /** Current viewing stream */
    protected BufferedReader CurrentStream = null;
    // Use a pair of piped streams to communicate with external processes in Stream mode
    protected PipedInputStream PIS = null;
    protected PrintStream POS = null;

    BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane jscPane = new JScrollPane();
    JPanel jP1 = new JPanel();
    JTextArea txaContent = new JTextArea();
    JButton cmdReload = new JButton();
    JButton cmdSave = new JButton();
    JPopupMenu menu = new JPopupMenu();
    JMenuItem menuWrap = new JMenuItem();
    JMenuItem menuCopyAll = new JMenuItem();
    JMenuItem menuClear = new JMenuItem();
    JMenuItem menuSave = new JMenuItem();
    JMenuItem menuCopy = new JMenuItem();

    public EPlusTextPanelOld() {
        try {
            jbInit();
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    /**
     * Create text panel with the specified mode
     * @param mode Viewer/Editor Mode of the panel
     */
    public EPlusTextPanelOld(String title, int mode) {
        try {
            jbInit();
            if (mode == VIEWER_MODE) {
                this.txaContent.setEditable(false);
                this.cmdReload.setVisible(false);
                this.cmdSave.setEnabled(false);
                this.menuClear.setEnabled(true);
                this.menuSave.setEnabled(false);
            } else if (mode == EDITOR_MODE) {
                this.txaContent.setEditable(true);
                this.cmdReload.setEnabled(true);
                this.cmdSave.setEnabled(true);
                this.menuClear.setEnabled(true);
                this.menuSave.setEnabled(true);
            } else if (mode == MINIMUM_MODE) {
                this.remove(this.jP1);
                this.txaContent.setEditable(false);
                this.menuClear.setEnabled(false);
                this.menuSave.setEnabled(false);
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        this.Title = title;
    }

    /**
     * Create text panel with the specified mode
     * @param mode Viewer/Editor Mode of the panel
     */
    public EPlusTextPanelOld(String filename, boolean readonly) {
        try {
            jbInit();
            this.txaContent.setEditable(!readonly);
            this.cmdReload.setEnabled(true);
            this.cmdSave.setEnabled(true);
            this.menuClear.setEnabled(true);
            this.menuSave.setEnabled(true);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Create text panel with the specified title, text, and mode
     * @param title Title for this panel, to appear in the title field of a frame, or title of a tab
     * @param text Initial texts in the text area
     * @param mode Viewer/Editor Mode of the panel
     * @param stream The stream to be monitored
     */
    public EPlusTextPanelOld(String title, String text, int mode, BufferedReader stream) {
        try {
            jbInit();
            if (mode == VIEWER_MODE) {
                this.txaContent.setEditable(false);
                this.cmdReload.setEnabled(false);
                this.cmdSave.setEnabled(false);
                this.menuClear.setEnabled(false);
                this.menuSave.setEnabled(false);
            } else if (mode == EDITOR_MODE) {
                this.txaContent.setEditable(true);
                this.cmdReload.setEnabled(true);
                this.cmdSave.setEnabled(true);
                this.menuClear.setEnabled(true);
                this.menuSave.setEnabled(true);
            } else if (mode == MINIMUM_MODE) {
                this.remove(this.jP1);
                this.txaContent.setEditable(false);
                this.menuClear.setEnabled(false);
                this.menuSave.setEnabled(false);
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        this.Title = title;
        this.setContent(text);
        this.CurrentStream = stream;
    }

    /** Initialization function managed by jbuilder. Do not modify. */
    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        txaContent.setTabSize(4);
        txaContent.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                txaContent_mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                txaContent_mouseReleased(e);
            }
        });
        txaContent.setToolTipText("");
        txaContent.setAutoscrolls(true);
        txaContent.setLineWrap(false);
        ((DefaultCaret)txaContent.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        cmdReload.setEnabled(true);
        cmdReload.setToolTipText("Clear the content of text area");
        cmdReload.setText("Reload");
        cmdSave.setEnabled(true);
        cmdSave.setToolTipText("Save the content into a file");
        cmdSave.setText("Save");
        cmdSave.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                cmdSave_actionPerformed(e);
            }
        });
        menuWrap.setEnabled(false);
        menuWrap.setText("Wrap/Unwrap");
        menuWrap.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menuWrap_actionPerformed(e);
            }
        });
        menuCopyAll.setText("Copy All");
        menuCopyAll.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menuCopyAll_actionPerformed(e);
            }
        });
        menuSave.setEnabled(true);
        menuSave.setText("Save Content As ...");
        menuSave.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menuSave_actionPerformed(e);
            }
        });
        menuClear.setEnabled(false);
        menuClear.setText("Clear Content");
        menuClear.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menuClear_actionPerformed(e);
            }
        });
        menuCopy.setEnabled(false);
        menuCopy.setDoubleBuffered(false);
        menuCopy.setText("Copy");
        menuCopy.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menuCopy_actionPerformed(e);
            }
        });
        cmdReload.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cmdReload_actionPerformed(e);
            }
        });
        this.add(jscPane, BorderLayout.CENTER);
        jscPane.getViewport().add(txaContent, null);
        this.add(jP1, BorderLayout.SOUTH);
        jP1.add(cmdSave, null);
        jP1.add(cmdReload, null);
        menu.add(menuWrap);
        menu.add(menuCopy);
        menu.add(menuCopyAll);
        menu.addSeparator();
        menu.add(menuClear);
        menu.add(menuSave);
        txaContent.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                txaContent_documentChanged(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                txaContent_documentChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                txaContent_documentChanged(e);
            }
        });
    }

    @Override
    public String getName () {
        return getTitle();
    }
    
    /** Get present Title of this viewer */
    public String getTitle() {
        return this.Title;
    }

    /** Set title of this viewer to the given string */
    public void setTitle(String title) {
        this.Title = title;
    }

    /** Get content in the text area */
    public String getContent() {
        return this.txaContent.getText();
    }

    /** Replace content in the text area with the given text */
    public final void setContent(String text) {
        this.txaContent.setText(text);
        this.ContentChanged = false;
    }

    /** Append the given text to the content in the text area */
    @Override
    public void appendContent(String text) {
        synchronized (this) {
            if (POS != null) {
                POS.print(text);
            }else {
                this.txaContent.append(text);
            }
            this.ContentChanged = true;
            txaContent.setCaretPosition(txaContent.getDocument().getLength());
        }
    }

    /** Get the "content changed" flag */
    public boolean isContentChanged() {
        return this.ContentChanged;
    }

    /**
     * Utility to load content of a file into a String object
     * @param filename String
     * @return String
     */
    public static String getFileContent(String filename) {
        String content = null;
        try {
            BufferedReader r = new BufferedReader(new FileReader(filename));
            String line = r.readLine();
            StringBuilder buf = new StringBuilder();
            while (line != null) {
                buf.append(line).append('\n');
                line = r.readLine();
            }
            content = buf.toString();
        } catch (IOException ioe) {
            logger.error("", ioe);
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return content;
    }

    /**
     * Utility to load content of a file into a String object
     * @param filename String
     * @return String
     */
    public static void saveFileContent(String filename, String content) {
        try (FileWriter w = new FileWriter(filename, false)) {
            w.write(content);
            w.flush();
        } catch (IOException ioe) {
            logger.error("", ioe);
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    /** Load text file and display in the text area */
    public boolean viewFile(String fname) {
        this.Title = fname;
        this.CurrentFileName = fname;
        txaContent.setText("");
        this.ContentChanged = false;
        try (BufferedReader in = new BufferedReader(new FileReader(fname))) {
            String buf;
            while (true) {
                buf = in.readLine();
                if (buf != null) {
                    txaContent.append(buf + "\n");
                } else {
                    break;
                }
            }
        } catch (FileNotFoundException e1) {
            txaContent.append("Can not open " + fname + ". File not found!\n");
            logger.error("TextPanel.viewFile(): " + fname + " not found!");
            return false;
        } catch (IOException e2) {
            txaContent.append("IO Error on accessing file: " + fname + "!\n");
            logger.error("TextPanel.viewFile(): IO Error on accessing " + fname + "!");
            return false;
        } catch (Exception e3) {
            logger.error("", e3);
            return false;
        }
        return true;
    }

    /** Load text file and display in the text area */
    public boolean viewStream() {
        txaContent.setText("");
        this.ContentChanged = false;
        try {
            String buf;
            while (true) {
                buf = this.CurrentStream.readLine();
                if (buf != null) {
                    txaContent.append(buf + "\n");
                } else {
                    break;
                }
            }
        } catch (IOException e2) {
            txaContent.append("IO Error on accessing stream: " + this.Title + "!\n");
            logger.error("TextPanel.viewStream(): IO Error on accessing " + this.Title + "!");
            return false;
        } catch (Exception e3) {
            logger.error("", e3);
            return false;
        }
        return true;
    }

    /** Text area "content changed" handler */
    void txaContent_documentChanged(DocumentEvent e) {
        this.ContentChanged = true;
    }

    /** Clear the content in the text area */
    void cmdClear_actionPerformed(ActionEvent e) {
        this.txaContent.setText("");
    }

    /** Save the content in the text area to file */
    void cmdSaveAs_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(new File("."));

        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write(this.txaContent.getText());
                out.close();
            } catch (Exception ex) {
                logger.error("", ex);
//		txaContent.append("\n Saving log file failed!\n");
            }
        } else {
        }
    }

    /** Switching wrapping or unwrapping text */
    void cmdWrap_actionPerformed(ActionEvent e) {
        CurrentLineWrap = !CurrentLineWrap;
        txaContent.setLineWrap(CurrentLineWrap);
    }

    /** Copy the content in the text area to the clipboard */
    void cmdCopyAll_actionPerformed(ActionEvent e) {
        txaContent.selectAll();
        txaContent.copy();
    }

    /**
     * Run as a thread
     */
    public void run() {
        if (this.CurrentStream == null) {
            this.getPrintStream();
        }
        if (this.CurrentStream != null) {
            while (true) {
                try {
                    while (CurrentStream.ready()) {
                        this.txaContent.append(CurrentStream.readLine() + "\n");
                    }
                    Thread.sleep(1000);
                } catch (IOException ioe) {
                } catch (InterruptedException inte) {
                }
            }
        }
    }

    /** Show popup menu */
    void txaContent_mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if ((txaContent.getSelectionEnd() - txaContent.getSelectionStart()) > 0) {
                menuCopy.setEnabled(true);
            } else {
                menuCopy.setEnabled(false);
            }
            this.menu.show(txaContent, e.getX(), e.getY());
        }
    }

    /** Show popup menu */
    void txaContent_mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if ((txaContent.getSelectionEnd() - txaContent.getSelectionStart()) > 0) {
                menuCopy.setEnabled(true);
            } else {
                menuCopy.setEnabled(false);
            }
            this.menu.show(txaContent, e.getX(), e.getY());
        }
    }

    /** Wrap/Unwrap text */
    void menuWrap_actionPerformed(ActionEvent e) {
        this.cmdWrap_actionPerformed(null);
    }

    /** Copy selection */
    void menuCopy_actionPerformed(ActionEvent e) {
        this.txaContent.copy();
    }

    /** Copy all */
    void menuCopyAll_actionPerformed(ActionEvent e) {
        this.cmdCopyAll_actionPerformed(null);
    }

    /** Clear all */
    void menuClear_actionPerformed(ActionEvent e) {
        this.cmdClear_actionPerformed(null);
    }

    /** Save all */
    void menuSave_actionPerformed(ActionEvent e) {
        this.cmdSaveAs_actionPerformed(null);
    }

    public void cmdReload_actionPerformed(ActionEvent e) {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Are you sure to discard all changes? ",
                "Caution",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            this.viewFile(this.CurrentFileName);
        }
    }

    public void cmdSave_actionPerformed(ActionEvent e) {
        if (CurrentFileName != null) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(CurrentFileName, false));) {
                out.write(this.txaContent.getText());
                JOptionPane.showMessageDialog(
                        this,
                        "File " + CurrentFileName + " saved successfully!  ",
                        "Message",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | HeadlessException ex) {
                logger.error("", ex);
                JOptionPane.showMessageDialog(
                        this,
                        "File " + CurrentFileName + " cannot be saved! Please check disk space or write protection etc.",
                        "Warning",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            this.cmdSaveAs_actionPerformed(e);
        }
    }

    /**
     * Note: print stream mode only works when this panel is running in a
     * separate thread
     * @return The PrintStream to which callers can send contents
     */
    @Override
    public PrintStream getPrintStream() {
        if (POS == null) {
            if (PIS == null) PIS = new PipedInputStream();
            try {
                POS = new PrintStream(new PipedOutputStream(PIS));
                this.CurrentStream = new BufferedReader (new InputStreamReader (PIS));
            } catch (IOException ex) {
                logger.error("", ex);
                POS = null;
            }
        }
        return POS;
    }
}
