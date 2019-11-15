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

import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import jeplus.JEPlusProjectV2;
import jeplus.util.FastDefaultStyledDocument;
import org.slf4j.LoggerFactory;

/**
 * Text panel for editing E+ .idf/.imf files with basic syntax highlighting
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.1
 */
public class EPlusTextPanel extends javax.swing.JPanel implements DocumentListener, ActionListener, IFJEPlusEditorPanel {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusTextPanel.class);

    /** Constant for viewer-only mode */
    public static final int VIEWER_MODE = 0;
    /** Constant for editing mode */
    public static final int EDITOR_MODE = 1;//	/** Constant for stream viewer mode */
//	public static final int STREAM_VIEWER_MODE = 2;
    /** Constant for minimalist viewer mode */
//    public static final int MINIMUM_MODE = 3;

    
    /** Mode of this text panel. Select between viewer and editor */
    protected int Mode = 0;
    /** Title of this TextPanel */
    protected String Title = null;

    /** File chooser */
    JFileChooser FC = new JFileChooser("./");

    /** tab index for easy access */
    protected int TabId = 0;

    /** The container component of this panel */
    protected Container ContainerComponent = null;

    /** Has the content been changed by editing or not */
    protected boolean ContentChanged = false;
    /** Current line wrapping status of the text area */
    protected boolean CurrentLineWrap = false;
    /** Current viewing/editing file name */
    protected String CurrentFileName = null;
    /** Current viewing stream */
    protected BufferedReader CurrentStream = null;    // members related to the contents and 
    /** Reference to the JEPlus Project, for up-to-date search strings */
    protected JEPlusProjectV2 Project = null;
    protected Timer AutoUpdateTimer = null;
    
    /** Model for the text content */
    private FastDefaultStyledDocument Document = null;
    private StreamTokenizer Tokenizer = null;

    /**
     * Predefined text attribute sets for syntax highlighting
     */
    protected static class AttributeSets {
        public static SimpleAttributeSet AttrNormal = null;
        public static SimpleAttributeSet AttrComment = null;
        public static SimpleAttributeSet AttrHighlight = null;
        public static SimpleAttributeSet AttrHighlight2 = null;
        public static SimpleAttributeSet AttrHighlight3 = null;
        public static SimpleAttributeSet AttrMacro = null;
        public static SimpleAttributeSet AttrVersion = null;

        public static SimpleAttributeSet getNormalAttr () {
            if (AttrNormal == null) {
                AttrNormal = new SimpleAttributeSet();
                AttrNormal.addAttribute(StyleConstants.CharacterConstants.Family,
                    "Courier New");
                AttrNormal.addAttribute(StyleConstants.CharacterConstants.Bold,
                    Boolean.FALSE);
                AttrNormal.addAttribute(StyleConstants.CharacterConstants.Italic,
                    Boolean.FALSE);
                AttrNormal.addAttribute(StyleConstants.CharacterConstants.Foreground,
                    Color.darkGray);
                AttrNormal.addAttribute(StyleConstants.CharacterConstants.Size,
                    new Integer(14));
            }
            return AttrNormal;
        }
        
         public static SimpleAttributeSet getCommentAttr () {
             if (AttrComment == null) {
                AttrComment = new SimpleAttributeSet();
                AttrComment.addAttribute(StyleConstants.CharacterConstants.Family,
                    "Courier New");
                AttrComment.addAttribute(StyleConstants.CharacterConstants.Bold,
                    Boolean.FALSE);
                AttrComment.addAttribute(StyleConstants.CharacterConstants.Italic,
                    Boolean.TRUE);
                AttrComment.addAttribute(StyleConstants.CharacterConstants.Foreground,
                    Color.gray);
                AttrComment.addAttribute(StyleConstants.CharacterConstants.Size,
                    new Integer(14));
             }
             return AttrComment;
         }

         public static SimpleAttributeSet getHighlightAttr () {
             if (AttrHighlight == null) {
                AttrHighlight = new SimpleAttributeSet();
                AttrHighlight.addAttribute(StyleConstants.CharacterConstants.Family,
                    "Courier New");
                AttrHighlight.addAttribute(StyleConstants.CharacterConstants.Bold,
                    Boolean.TRUE);
                AttrHighlight.addAttribute(StyleConstants.CharacterConstants.Italic,
                    Boolean.FALSE);
                AttrHighlight.addAttribute(StyleConstants.CharacterConstants.Foreground,
                    Color.black);
                AttrHighlight.addAttribute(StyleConstants.CharacterConstants.Size,
                    new Integer(14));
             }
             return AttrHighlight;
         }

         public static SimpleAttributeSet getHighlight2Attr () {
             if (AttrHighlight2 == null) {
                AttrHighlight2 = new SimpleAttributeSet();
                AttrHighlight2.addAttribute(StyleConstants.CharacterConstants.Family,
                    "Courier New");
                AttrHighlight2.addAttribute(StyleConstants.CharacterConstants.Bold,
                    Boolean.TRUE);
                AttrHighlight2.addAttribute(StyleConstants.CharacterConstants.Italic,
                    Boolean.FALSE);
                AttrHighlight2.addAttribute(StyleConstants.CharacterConstants.Foreground,
                    Color.blue);
                AttrHighlight2.addAttribute(StyleConstants.CharacterConstants.Size,
                    new Integer(14));
             }
             return AttrHighlight2;
         }

         public static SimpleAttributeSet getHighlight3Attr () {
             if (AttrHighlight3 == null) {
                AttrHighlight3 = new SimpleAttributeSet();
                AttrHighlight3.addAttribute(StyleConstants.CharacterConstants.Family,
                    "Courier New");
                AttrHighlight3.addAttribute(StyleConstants.CharacterConstants.Bold,
                    Boolean.TRUE);
                AttrHighlight3.addAttribute(StyleConstants.CharacterConstants.Italic,
                    Boolean.FALSE);
                AttrHighlight3.addAttribute(StyleConstants.CharacterConstants.Foreground,
                    Color.red);
                AttrHighlight3.addAttribute(StyleConstants.CharacterConstants.Size,
                    new Integer(14));
             }
             return AttrHighlight3;
         }

         public static SimpleAttributeSet getMacroAttr () {
             if (AttrMacro == null) {
                AttrMacro = new SimpleAttributeSet();
                AttrMacro.addAttribute(StyleConstants.CharacterConstants.Family,
                    "Courier New");
                AttrMacro.addAttribute(StyleConstants.CharacterConstants.Bold,
                    Boolean.TRUE);
                AttrMacro.addAttribute(StyleConstants.CharacterConstants.Italic,
                    Boolean.TRUE);
                AttrMacro.addAttribute(StyleConstants.CharacterConstants.Foreground,
                    Color.green);
                AttrMacro.addAttribute(StyleConstants.CharacterConstants.Size,
                    new Integer(14));
             }
             return AttrMacro;
         }

         public static SimpleAttributeSet getVersionAttr () {
             if (AttrVersion == null) {
                AttrVersion = new SimpleAttributeSet();
                AttrVersion.addAttribute(StyleConstants.CharacterConstants.Family,
                    "Courier New");
                AttrVersion.addAttribute(StyleConstants.CharacterConstants.Bold,
                    Boolean.TRUE);
                AttrVersion.addAttribute(StyleConstants.CharacterConstants.Italic,
                    Boolean.FALSE);
                AttrVersion.addAttribute(StyleConstants.CharacterConstants.Foreground,
                    Color.black);
                AttrVersion.addAttribute(StyleConstants.CharacterConstants.Background,
                    Color.yellow);
                AttrVersion.addAttribute(StyleConstants.CharacterConstants.Size,
                    new Integer(14));
             }
             return AttrVersion;
         }
    }

    /** List of search strings objects */
    public ArrayList<MarkedSearchTag> SearchStrings = new ArrayList<> ();

    public class IDFRenderer extends Thread implements ActionListener {
        public IDFRenderer () {
            super();
        }

        @Override
        public void run() {
            boolean nomorechanges = false;
            try {
                while (! nomorechanges) {
                    // reset knwon search strings
                    for (int i=0; i<SearchStrings.size(); i++) {
                        SearchStrings.get(i).resetMarkers ();
                    }

                    // Loop through lines
                    String all = Document.getText(0, Document.getLength());
                    int cursor = 0;
                    int linenum = 0;
                    try (BufferedReader rd = new BufferedReader (new StringReader (all))) {
                        String line = rd.readLine();
                        while (line != null && !this.isInterrupted()) {
                            // Setting line/comment/macro/version attributes
                            int idx = line.indexOf('!');
                            if (idx < 0) idx = line.length();
                            try {
                                if (line.startsWith("##")) {
                                    Document.setCharacterAttributes(cursor, idx, AttributeSets.getMacroAttr(), true);
                                }else if (line.trim().startsWith("Version,")) {
                                    Document.setCharacterAttributes(cursor, idx, AttributeSets.getVersionAttr(), true);
                                }else {
                                    Document.setCharacterAttributes(cursor, idx, AttributeSets.getNormalAttr(), true);
                                }
                                Document.setCharacterAttributes(cursor+idx, line.length()-idx+1, AttributeSets.getCommentAttr(), true);

                                // Look for known search strings
                                if (SearchStrings != null) {
                                    for (int i=0; i<SearchStrings.size(); i++) {
                                        MarkedSearchTag ss = SearchStrings.get(i);
                                        idx = line.indexOf(ss.getEntry());
                                        if (idx >= 0) {
                                            ss.addMarker(cursor + idx);
                                            Document.setCharacterAttributes(cursor + idx, ss.getEntry().length(), ss.getAttributeSet(), true);
                                        }
                                    }
                                }
                            }catch (Exception ex) {
                                // do nothing
                            }
                            linenum ++;
                            cursor += line.length() + 1; // do nothing
                            line = rd.readLine();
                        }
                        if (! IDFRenderer.interrupted()) { // exit
                            nomorechanges = true;
                        }
                    }
                }
            }catch (BadLocationException | IOException ex) {
                logger.error("", ex);
            }
            cboSearchStrings.setModel(new DefaultComboBoxModel(SearchStrings.toArray(new MarkedSearchTag[0])));
        }

        public void updateSearchStringsNow() {
            updateSearchStrings();
            //if (! this.isAlive()) this.start();
            run ();
            // scanSearchStrings();
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            updateSearchStrings();
            // scanSearchStrings();
        }
    }

    /** Scans keywords (search strings etc.) in a separate thread */
    protected IDFRenderer Renderer = new IDFRenderer ();

    /** Text finder */
    IncSearchPanel TextFinder = null;

    /** Creates new form EPlusTextPanel */
    public EPlusTextPanel() {
        initComponents();
        JPanel_ComboBoxRenderer.setHostPanel(this);
        this.cboSearchStrings.setRenderer(new JPanel_ComboBoxRenderer ());
        this.cboSearchStrings.setEditor(new JPanel_ComboBoxRenderer ());
        Document = new FastDefaultStyledDocument();
        txpContent.setDocument(Document);
        TextFinder = new IncSearchPanel (txpContent);
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(TextFinder, BorderLayout.CENTER);
    }

    /**
     * Create text panel with the specified title, text, and mode
     * @param title Title for this panel, to appear in the title field of a frame, or title of a tab
     * @param mode Viewer/Editor Mode of the panel
     * @param filename The name of the file to be openned
     */
    public EPlusTextPanel(Container container, String title, int mode, FileFilter filefilter, String filename, JEPlusProjectV2 project) {
        this();
        try {
            if (mode == VIEWER_MODE) {
                this.txpContent.setEditable(false);
                this.cmdLoad.setEnabled(true);
                this.cmdSave.setEnabled(false);
                this.cboSearchStrings.setEditable(false);
            } else if (mode == EDITOR_MODE) {
                this.txpContent.setEditable(true);

                this.cmdLoad.setEnabled(true);
                this.cmdSave.setEnabled(true);
                this.cboSearchStrings.setEditable(true);
            }
            FC.setFileFilter(filefilter);
            FC.setMultiSelectionEnabled(false);
            CurrentFileName = filename;
            CurrentStream = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "ISO-8859-1"));
            Tokenizer = new StreamTokenizer(CurrentStream);
            Tokenizer.commentChar('!');
            
            Project = project;
            updateSearchStrings ();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error("", e);
        }
        this.ContainerComponent = container;
        this.Title = title;
        this.showStream();
        this.notifyContentChange(false);
        // Add an auto updator
        int delay = 3000; //milliseconds
        AutoUpdateTimer = new Timer(delay, Renderer);
        AutoUpdateTimer.start();
        //this.scanSearchStrings();
//        for (int i=0; i < SearchStrings.size(); i++) {
//            this.cboSearchStrings.addItem(SearchStrings.get(i));
//        }
    }
    
    @Override
    public int getTabId() {
        return TabId;
    }

    @Override
    public void setTabId(int TabId) {
        this.TabId = TabId;
    }

    /** Access the container component */
    public Container getContainer () {
        return ContainerComponent;
    }

    public IDFRenderer getRenderer() {
        return Renderer;
    }

 
    public void disableSearchStringBox () {
        this.cboSearchStrings.setEditable(false);
        this.cboSearchStrings.setEnabled(false);
    }

    public final void updateSearchStrings () {
        String [] searchstrings = (Project == null)? null : Project.getSearchStrings();
        if (searchstrings != null) {
            SearchStrings.clear();
            for (int i=0; i < searchstrings.length; i++) {
                String [] sstrs = searchstrings[i].split("\\s*\\|\\s*");
                for (int j=0; j<sstrs.length; j++) {
                    SearchStrings.add(new MarkedSearchTag(sstrs[j], true));
                }
            }
        }
    }
    
    private void showStream() {
        // Detach Document from GUI
        DefaultStyledDocument blank = new DefaultStyledDocument ();
        txpContent.setDocument(blank);

        // Disable document listener
        Document.removeDocumentListener(this);

//        // Run search in a thread
//        new Thread () {
//            public void run () {
                try {
                    // Clear document
                    Document.remove(0, Document.getLength());
                    // Insert lines from the stream
                    String line = CurrentStream.readLine();
                    while (line != null) {
                        Document.appendBatchString(line, AttributeSets.getNormalAttr());
                        Document.appendBatchLineFeed(AttributeSets.getNormalAttr());
//                        int idx = line.indexOf('!');
//                        if (idx < 0) idx = line.length();
//                        if (line.startsWith("##")) {
//                            Document.appendBatchString(line.substring(0, idx), AttributeSets.getMacroAttr());
//                        }else if (line.trim().startsWith("Version,")) {
//                            Document.appendBatchString(line.substring(0, idx), AttributeSets.getVersionAttr());
//                        }else {
//                            Document.appendBatchString(line.substring(0, idx), AttributeSets.getNormalAttr());
//                        }
//                        Document.appendBatchString(line.substring(idx), AttributeSets.getCommentAttr());
//                        //Document.appendBatchString(Document.getLength(), System.getProperty("line.separator"), AttrComment);
//                        Document.appendBatchLineFeed(AttributeSets.getCommentAttr());
                        line = CurrentStream.readLine();
                    }
                    // Batch update from beginning of the text
                    Document.processBatchUpdates(0);
                    CurrentStream.close(); // Is this correct?
                }catch (BadLocationException | IOException ex) {
                    logger.error("", ex);
                }
//            }
//        }.start();
        // Enable document listener
        Document.addDocumentListener(this);

        // Attach Document to GUI
        txpContent.setDocument(Document);

        // Start renderer
        if (Renderer.isAlive()) {
            Renderer.interrupt();
        }else {
            Renderer = new IDFRenderer();
            Renderer.start();
        }

    }

    private ArrayList<Integer> scanText (String ss) {
        ArrayList<Integer> pos = new ArrayList <> ();
        try {
            if (ss != null && ss.length() > 0) {
                String text = Document.getText(0, Document.getLength());
                int lastIndex = 0;
                int wordSize = ss.length();

                while ((lastIndex = text.indexOf(ss, lastIndex)) != -1) {
                    pos.add(new Integer (lastIndex));
                    lastIndex += wordSize;
                }
            }
        }catch (Exception ex) {
        }
        return pos;
    }

    private void scanSearchStrings() {
        // Disable document listener
        Document.removeDocumentListener(this);

        // Scan required search strings
        if (SearchStrings != null) {
            for (int i=0; i<SearchStrings.size(); i++) {
                MarkedSearchTag ss = SearchStrings.get(i);
                ss.addAllMarkers(scanText(ss.getEntry()));
            }
        }

        // Scan possible search strings in text

//        // Run search in a thread
//        new Thread () {
//            public void run () {
                //SearchString occ = null;
                try {
                    // Clear the content of SearchStrings
                    SearchStrings.clear();

                    // Loop through lines
                    String all = Document.getText(0, Document.getLength());
                    int cursor = 0;
                    int linenum = 0;
                    BufferedReader rd = new BufferedReader (new StringReader (all));
                    String line = rd.readLine();
                    while (line != null) {
                        // find tag and get location
                        if (line.matches(".*\\@\\@\\w+\\@\\@.*")) {
                            int start = line.indexOf("@");
                            int end = line.lastIndexOf("@");
                            String tag = line.substring(start, end+1);

                            // change text attributes
                            Document.setCharacterAttributes(cursor+start, end-start+1, AttributeSets.getHighlight2Attr(), false);

                            // add this tag
                            MarkedSearchTag s = new MarkedSearchTag (tag, false);
                            s.addMarker(cursor+start);
                            SearchStrings.add(s);

        //                    // update search string list
        //                    occ = new SearchString (tag, false, cursor+start);
        //                    int i = SearchStrings.indexOf(occ);
        //                    if (i >= 0) {
        //                        SearchStrings.get(i).setLineNumber(cursor+start);
        //                    }else {
        //                        SearchStrings.add(occ);
        //                    }
                        }
                        // next line
                        linenum ++;
                        cursor += line.length() + 1;
                        line = rd.readLine();
                    }
                    // update combobox
                }catch (BadLocationException | IOException ex) {
                    logger.error("", ex);
                }
//            }
//        }.start();

        // Enable document listener
        Document.addDocumentListener(this);
            
    }
    
    public ArrayList<MarkedSearchTag> getSearchStrings () {
        return this.SearchStrings;
    }
            
    /** Get present Title of this viewer */
    @Override
    public String getTitle() {
        return this.Title;
    }

    /** Set title of this viewer to the given string */
    @Override
    public void setTitle(String title) {
        this.Title = title;
    }

    public JTextPane getContentPane () {
        return txpContent;
    }

    /** Get content in the text area */
    public String getContent() {
        return this.txpContent.getText();
    }

    /** Replace content in the text area with the given text */
    public void setContent(String text) {
        if (text != null) {
            this.txpContent.setText(text);
            this.ContentChanged = false;
        }
    }

    /** Get the "content changed" flag */
    public boolean isContentChanged() {
        return this.ContentChanged;
    }

    /** Make changes to UI to notify content change */
    public final void notifyContentChange (boolean contentchanged) {
        if (contentchanged) {
            this.cmdSave.setEnabled(true);
            if (ContainerComponent instanceof Frame) {
                ((Frame)ContainerComponent).setTitle(getTitle() + "*");
            }else if (ContainerComponent instanceof Dialog) {
                ((Dialog)ContainerComponent).setTitle(getTitle() + "*");
            }else if (ContainerComponent instanceof JTabbedPane) {
                if (TabId > 0) {
                    //((JTabbedPane)ContainerComponent).setTitleAt(getTabId(), getTitle() + "*");
                    JTabbedPane jtb = (JTabbedPane)ContainerComponent;
                    jtb.setTitleAt(jtb.indexOfComponent(this), getTitle() + "*");
                }
            }
            if (Renderer.isAlive()) Renderer.interrupt();
            else {
                Renderer = new IDFRenderer();
                Renderer.start();
            }
        }else {
            this.cmdSave.setEnabled(false);
            if (ContainerComponent instanceof Frame) {
                ((Frame)ContainerComponent).setTitle(getTitle());
            }else if (ContainerComponent instanceof Dialog) {
                ((Dialog)ContainerComponent).setTitle(getTitle());
            }else if (ContainerComponent instanceof JTabbedPane) {
                if (TabId > 0) {
                    //((JTabbedPane)ContainerComponent).setTitleAt(getTabId(), getTitle());
                    JTabbedPane jtb = (JTabbedPane)ContainerComponent;
                    jtb.setTitleAt(jtb.indexOfComponent(this), getTitle());
                }
            }
        }
    }

    /**
     * Utility to load content of a file into a String object
     * @param filename String
     * @return String
     */
    public static String getFileContent(String filename) {
        String content = null;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "ISO-8859-1"))) {
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
    public void saveFileContent() {
        this.cmdSaveActionPerformed(null);
    }

    /**
     * Utility to load content of a file into a String object
     * @param filename String
     * @return String
     */
    public static void saveFileContent(String filename, String content) {
        try (OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(filename), "ISO-8859-1")) {
            w.write(content);
            w.flush();
        } catch (IOException ioe) {
            logger.error("", ioe);
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent arg0) {
        ContentChanged = true;
        notifyContentChange(true);
        //System.out.println("Doc insert update");
    }

    @Override
    public void removeUpdate(DocumentEvent arg0) {
        ContentChanged = true;
        notifyContentChange(true);
        //System.out.println("Doc remove update");
    }

    @Override
    public void changedUpdate(DocumentEvent arg0) {
        // System.out.println("Doc change update");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd != null && cmd.toLowerCase().equals("tab closing")) {
            closeTextPanel();
        }
    }

    /**
     * Confirm closing the current file. User will be prompted to save if the
     * content of the file has been changed.
     * @return cancel flag
     */
    @Override
    public boolean closeTextPanel () {
        // Confirm save before open another file
        if (this.isContentChanged()) {
            int ans = JOptionPane.showConfirmDialog(this,
                "The contents of " + CurrentFileName + " has been modified. \nDo you want to save the changes?",
                "Save to file?",
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (ans == JOptionPane.CANCEL_OPTION) {
                return true;
            }else if (ans == JOptionPane.YES_OPTION) {
                this.cmdSaveActionPerformed(null);
            }
        }
        if (CurrentStream != null) try {
            CurrentStream.close();
        } catch (IOException ex) {
            logger.error("", ex);
        }
        if (ContainerComponent instanceof Frame) {
            ((Frame)ContainerComponent).dispose();
        }else if (ContainerComponent instanceof Dialog) {
            ((Dialog)ContainerComponent).dispose();
        }else if (ContainerComponent instanceof JTabbedPane && TabId > 0) {
            //((JTabbedPane)ContainerComponent).remove(this.TabId);
            ((JTabbedPane)ContainerComponent).remove(this);
            TabId = 0;
        }
        // Stop timer
        if (AutoUpdateTimer != null) {
            AutoUpdateTimer.stop();
        }
        return false;
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdDiscard = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txpContent = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        cmdLoad = new javax.swing.JButton();
        cmdSave = new javax.swing.JButton();
        cboSearchStrings = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();

        cmdDiscard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/discard.png"))); // NOI18N
        cmdDiscard.setToolTipText("Discard changes");
        cmdDiscard.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmdDiscard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDiscardActionPerformed(evt);
            }
        });

        txpContent.setDoubleBuffered(true);
        jScrollPane1.setViewportView(txpContent);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        cmdLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/open.png"))); // NOI18N
        cmdLoad.setToolTipText("Load");
        cmdLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLoadActionPerformed(evt);
            }
        });

        cmdSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/save.png"))); // NOI18N
        cmdSave.setToolTipText("Save");
        cmdSave.setEnabled(false);
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });

        cboSearchStrings.setEditable(true);
        cboSearchStrings.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cboSearchStrings.setMinimumSize(new java.awt.Dimension(200, 18));
        cboSearchStrings.setPreferredSize(new java.awt.Dimension(200, 18));
        cboSearchStrings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cboSearchStringsMouseEntered(evt);
            }
        });
        cboSearchStrings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSearchStringsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 153, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdSave, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboSearchStrings, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmdLoad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cboSearchStrings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmdSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(11, 11, 11))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void cboSearchStringsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSearchStringsActionPerformed
    try {
        MarkedSearchTag sstr = (MarkedSearchTag)cboSearchStrings.getSelectedItem();
        txpContent.requestFocus();
        int pos = -1;
        try {
            pos = sstr.Markers.get(sstr.getCurrentSelection() - 1).intValue(); // because it is 1-based
        }catch (IndexOutOfBoundsException iob) {
            // does nothing
        }
        if (pos >= 0) {
            txpContent.setCaretPosition(pos);
            txpContent.moveCaretPosition(pos + sstr.Entry.length());
        }
    }catch (ClassCastException cce) {
        logger.error("", cce);
    }catch (Exception ex) {
        logger.error("", ex);
    }
}//GEN-LAST:event_cboSearchStringsActionPerformed

private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
    saveFileContent(this.CurrentFileName, txpContent.getText());
    ContentChanged = false;
    notifyContentChange(false);
    this.scanSearchStrings();
    this.cboSearchStrings.repaint();
}//GEN-LAST:event_cmdSaveActionPerformed

private void cmdLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLoadActionPerformed
    // Confirm save before open another file
    if (this.isContentChanged()) {
        int ans = JOptionPane.showConfirmDialog(this,
            "The contents of " + CurrentFileName + " has been modified. Would you like to save the changes first?",
            "Confirm saving ...",
            JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            saveFileContent(this.CurrentFileName, txpContent.getText());
        }
    }
    // Choose an IDF/IMF file to open
    // Select a file to open
    if (FC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        CurrentFileName = FC.getSelectedFile().getPath();
        String name = FC.getSelectedFile().getName();
        // Open idf/imf file
        try {
            //CurrentStream = new BufferedReader(new FileReader(name));
            CurrentStream = new BufferedReader(new FileReader(CurrentFileName));
            this.showStream();
            ContentChanged = false;
            this.Title = name;
            notifyContentChange(false);
            cboSearchStrings.repaint();
        }catch (IOException ioe) {
            logger.error("", ioe);
        }
    }
}//GEN-LAST:event_cmdLoadActionPerformed

private void cmdDiscardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDiscardActionPerformed
    closeTextPanel ();
}//GEN-LAST:event_cmdDiscardActionPerformed

    private void cboSearchStringsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cboSearchStringsMouseEntered

    }//GEN-LAST:event_cboSearchStringsMouseEntered

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboSearchStrings;
    private javax.swing.JButton cmdDiscard;
    private javax.swing.JButton cmdLoad;
    private javax.swing.JButton cmdSave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane txpContent;
    // End of variables declaration//GEN-END:variables


    public static void main (String [] args) {
        JFrame frame = new JFrame ("E+ idf TextPanel test");
        frame.getContentPane().add(new EPlusTextPanel ());
        //frame.getContentPane().add(new EPlusTextPanel (null, null, 1, null, null, null));
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
