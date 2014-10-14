/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@googlemail.com>               *
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
 ***************************************************************************
 *                                                                         *
 * Change log:                                                             *
 *                                                                         *
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus.gui;

import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.JEPlusProject;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import org.slf4j.LoggerFactory;

/**
 * Text editor panel for editing various E+, TRNSYS, INSEL model files. This editor is based on 
 * Fifesoft's RSyntaxTextArea.
 * 
 * @author Yi Zhang
 * @version 1.5
 * @since 1.5
 */
public class EPlusEditorPanel extends JPanel implements DocumentListener, ActionListener, IFJEPlusEditorPanel {

    /**
     * Logger
     */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusEditorPanel.class);

    /** The container component of this panel */
    protected Container ContainerComponent = null;

    /** Title of this TextPanel */
    protected String Title = null;

    /** File chooser */
    JFileChooser FC = new JFileChooser("./");

    /** tab index for easy access */
    protected int TabId = 0;

    /** Reference to the JEPlus Project, for up-to-date search strings */
    protected JEPlusProject Project = null;

    /** Current viewing/editing file name */
    protected String CurrentFileName = null;

    /** Has the content been changed by editing or not */
    protected boolean ContentChanged = false;

    /** Model for the text content */
    private Document Document = null;

    public boolean isContentChanged() {
        return ContentChanged;
    }

    public void setContentChanged(boolean ContentChanged) {
        this.ContentChanged = ContentChanged;
        this.notifyContentChange(ContentChanged);
    }

    @Override
    public String getTitle() {
        return Title;
    }

    @Override
    public void setTitle(String Title) {
        this.Title = Title;
    }

    @Override
    public int getTabId() {
        return TabId;
    }

    @Override
    public void setTabId(int TabId) {
        this.TabId = TabId;
    }
    
    /**
     * Creates new form EPlusTextPanel
     */
    public EPlusEditorPanel() {
        initComponents();
        initRSTA("text/EPlusIDF");
    }

    /**
     * Create text panel with the specified title, text, and mode
     *
     * @param container Reference to the container of this panel
     * @param title Title for this panel, to appear in the title field of a frame, or title of a tab
     * @param filefilter 
     * @param filename The name of the file to be openned
     * @param style Syntax style string
     * @param project
     */
    public EPlusEditorPanel(Container container, String title, FileFilter filefilter, String filename, String style, JEPlusProject project) {
        initComponents();
        initRSTA(style);
        this.ContainerComponent = container;
        this.cmdLoad.setEnabled(true);
        this.cmdSave.setEnabled(true);
        // this.cboSearchStrings.setEditable(true);
        FC.setFileFilter(filefilter);
        FC.setMultiSelectionEnabled(false);
        CurrentFileName = filename;
        Project = project;
        updateSearchStrings ();
        this.Title = title;
        String content = getFileContent(CurrentFileName);
        this.rsTextArea.setText(content);
        this.ContentChanged = false;
        Document = rsTextArea.getDocument();
        Document.addDocumentListener(this);
    }

    private void initRSTA(String syntaxstyle) {
        
        rsTextArea.setCodeFoldingEnabled(true);
        rsTextArea.setAntiAliasingEnabled(true);
        // rsTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        rsTextArea.setSyntaxEditingStyle(syntaxstyle);
        
        rTextScrollPane1.setFoldIndicatorEnabled(true);
        rTextScrollPane1.setLineNumbersEnabled(true);

        // Create a toolbar with searching options.
        nextButton.setActionCommand("FindNext");
        nextButton.addActionListener(this);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextButton.doClick(0);
            }
        });
        prevButton.setActionCommand("FindPrev");
        prevButton.addActionListener(this);
        
        try {
//           Theme theme = Theme.load(getClass().getResourceAsStream("/jeplus/gui/themes/eclipse.xml"));
           Theme theme = Theme.load(new FileInputStream("RSyntaxTheme.xml"));
           theme.apply(rsTextArea);
        } catch (IOException ioe) { // Never happens
           logger.error("Failed to apply theme from RSyntaxTheme.xml. Default is used.", ioe);
        }
        setFont(rsTextArea, rsTextArea.getFont().deriveFont(13f));
        
    }

    /**
     * Set the font for all token types.
     *
     * @param textArea The text area to modify.
     * @param font The font to use.
     */
    public static void setFont(RSyntaxTextArea textArea, Font font) {
        if (font != null) {
            SyntaxScheme ss = textArea.getSyntaxScheme();
            ss = (SyntaxScheme) ss.clone();
            for (int i = 0; i < ss.getStyleCount(); i++) {
                if (ss.getStyle(i) != null) {
                    ss.getStyle(i).font = font;
                }
            }
            textArea.setSyntaxScheme(ss);
            textArea.setFont(font);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // "FindNext" => search forward, "FindPrev" => search backward
        String command = e.getActionCommand();
        if (command != null && command.toLowerCase().equals("tab closing")) {
            closeTextPanel();
        }else {
            boolean forward = "FindNext".equals(command);

            // Create an object defining our search parameters.
            SearchContext context = new SearchContext();
            String text = searchField.getText();
            if (text.length() == 0) {
                return;
            }
            context.setSearchFor(text);
            context.setMatchCase(matchCaseCB.isSelected());
            context.setRegularExpression(regexCB.isSelected());
            context.setSearchForward(forward);
            context.setWholeWord(false);

            SearchResult found = SearchEngine.find(rsTextArea, context);
            if (!found.wasFound()) {
                rsTextArea.setCaretPosition(forward ? 0 : rsTextArea.getDocument().getLength()-1);
                found = SearchEngine.find(rsTextArea, context);
                if (!found.wasFound()) {
                    JOptionPane.showMessageDialog(this, "Text not found");
                }
            }
        }
    }

    /**
     * Utility to load content of a file into a String object
     *
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

    public final void updateSearchStrings () {
        String [] searchstrings = (Project == null)? null : Project.getSearchStrings();
        if (searchstrings != null) {
            Vector<String> SSs = new Vector<>();
            for (String searchstring : searchstrings) {
                String[] sstrs = searchstring.split("\\s*\\|\\s*");
                SSs.addAll(Arrays.asList(sstrs));
            }
            this.cboSearchStrings.setModel(new DefaultComboBoxModel (SSs));
            this.cboSearchStrings.setEnabled(true);
        }else {
            this.cboSearchStrings.setModel(new DefaultComboBoxModel ());
            this.cboSearchStrings.setEnabled(false);
        }
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
     * This method is called from within the constructor to initialise the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rTextScrollPane1 = new org.fife.ui.rtextarea.RTextScrollPane();
        rsTextArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        jToolBar1 = new javax.swing.JToolBar();
        cmdLoad = new javax.swing.JButton();
        cmdSave = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        cboSearchStrings = new javax.swing.JComboBox();
        cmdRefresh = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        prevButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        regexCB = new javax.swing.JCheckBox();
        matchCaseCB = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        rsTextArea.setColumns(60);
        rsTextArea.setRows(20);
        rsTextArea.setToolTipText("");
        rsTextArea.setCodeFoldingEnabled(true);
        rTextScrollPane1.setViewportView(rsTextArea);

        add(rTextScrollPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMinimumSize(new java.awt.Dimension(0, 0));

        cmdLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/folder.png"))); // NOI18N
        cmdLoad.setToolTipText("Open a new file or reload the current file.");
        cmdLoad.setFocusable(false);
        cmdLoad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdLoad.setMargin(new java.awt.Insets(2, 2, 2, 2));
        cmdLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLoadActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdLoad);

        cmdSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/save1.png"))); // NOI18N
        cmdSave.setToolTipText("Save the current file");
        cmdSave.setFocusable(false);
        cmdSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdSave);

        jSeparator2.setForeground(new java.awt.Color(153, 153, 153));
        jSeparator2.setMaximumSize(new java.awt.Dimension(60, 32767));
        jSeparator2.setPreferredSize(new java.awt.Dimension(30, 0));
        jToolBar1.add(jSeparator2);

        jLabel2.setText("Tags: ");
        jToolBar1.add(jLabel2);

        cboSearchStrings.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboSearchStrings.setToolTipText("Select a search string to locate it in the file.");
        cboSearchStrings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSearchStringsActionPerformed(evt);
            }
        });
        jToolBar1.add(cboSearchStrings);

        cmdRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/view-refresh.png"))); // NOI18N
        cmdRefresh.setToolTipText("Update search strings from the project.");
        cmdRefresh.setFocusable(false);
        cmdRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdRefresh);

        jSeparator1.setMaximumSize(new java.awt.Dimension(60, 32767));
        jSeparator1.setPreferredSize(new java.awt.Dimension(30, 0));
        jToolBar1.add(jSeparator1);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Text search: ");
        jLabel1.setMaximumSize(new java.awt.Dimension(80, 14));
        jToolBar1.add(jLabel1);

        searchField.setColumns(30);
        searchField.setToolTipText("Text search");
        searchField.setMaximumSize(new java.awt.Dimension(100, 25));
        searchField.setPreferredSize(new java.awt.Dimension(200, 20));
        jToolBar1.add(searchField);

        prevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/go-previous.png"))); // NOI18N
        prevButton.setToolTipText("Search backwards from the cursor.");
        prevButton.setFocusable(false);
        prevButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolBar1.add(prevButton);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/go-next.png"))); // NOI18N
        nextButton.setToolTipText("Search forward from the cursor.");
        nextButton.setFocusable(false);
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolBar1.add(nextButton);

        jSeparator3.setMaximumSize(new java.awt.Dimension(60, 32767));
        jSeparator3.setPreferredSize(new java.awt.Dimension(30, 0));
        jToolBar1.add(jSeparator3);

        regexCB.setText("Regex");
        regexCB.setToolTipText("Use Regular Expression. For an example, try search \"@@.*@@\"");
        regexCB.setFocusable(false);
        regexCB.setMaximumSize(new java.awt.Dimension(120, 31));
        regexCB.setPreferredSize(new java.awt.Dimension(80, 31));
        jToolBar1.add(regexCB);

        matchCaseCB.setText("Match case");
        matchCaseCB.setToolTipText("Match cases in search");
        matchCaseCB.setFocusable(false);
        matchCaseCB.setMaximumSize(new java.awt.Dimension(120, 15));
        matchCaseCB.setPreferredSize(new java.awt.Dimension(90, 15));
        jToolBar1.add(matchCaseCB);

        add(jToolBar1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
        saveFileContent(this.CurrentFileName, rsTextArea.getText());
        setContentChanged(false);
    }//GEN-LAST:event_cmdSaveActionPerformed

    private void cmdLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLoadActionPerformed
        // Confirm save before open another file
        if (this.isContentChanged()) {
            int ans = JOptionPane.showConfirmDialog(this,
                "The contents of " + CurrentFileName + " has been modified. Would you like to save the changes first?",
                "Confirm saving ...",
                JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) {
                saveFileContent(this.CurrentFileName, rsTextArea.getText());
            }
        }
        // Select a file to open
        if (FC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            CurrentFileName = FC.getSelectedFile().getPath();
            String name = FC.getSelectedFile().getName();
            // Open idf/imf file
            rsTextArea.setText(getFileContent(CurrentFileName));
            setContentChanged (false);
            this.Title = name;
            notifyContentChange(false);
        }
    }//GEN-LAST:event_cmdLoadActionPerformed

    private void cboSearchStringsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSearchStringsActionPerformed
        this.searchField.setText(cboSearchStrings.getSelectedItem().toString());
        this.nextButton.doClick(0);
    }//GEN-LAST:event_cboSearchStringsActionPerformed

    private void cmdRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRefreshActionPerformed
        updateSearchStrings();
    }//GEN-LAST:event_cmdRefreshActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboSearchStrings;
    private javax.swing.JButton cmdLoad;
    private javax.swing.JButton cmdRefresh;
    private javax.swing.JButton cmdSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JCheckBox matchCaseCB;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private org.fife.ui.rtextarea.RTextScrollPane rTextScrollPane1;
    private javax.swing.JCheckBox regexCB;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rsTextArea;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
            // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        JFrame frame = new JFrame("E+ idf TextPanel test");
        frame.getContentPane().add(new EPlusEditorPanel());
        //frame.getContentPane().add(new EPlusTextPanel (null, null, 1, null, null, null));
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        setContentChanged (true);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        setContentChanged (true);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    
    @Override
    public void closeTextPanel() {
        // Confirm save before open another file
        if (this.isContentChanged()) {
            int ans = JOptionPane.showConfirmDialog(this,
                "The contents of " + CurrentFileName + " has been modified. \nDo you want to save the changes?",
                "Save to file?",
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (ans == JOptionPane.CANCEL_OPTION) {
                return;
            }else if (ans == JOptionPane.YES_OPTION) {
                this.cmdSaveActionPerformed(null);
            }
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
    }

}
