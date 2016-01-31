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

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class TextPanel extends javax.swing.JPanel implements JEPlusPrintableGUI, Runnable, DocumentListener {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(TextPanel.class);

    // Use a pair of piped streams to communicate with external processes
    PipedInputStream PIS = null;
    PrintStream POS = null;

    // Signal to stop monitoring the streams
    boolean Stop = false;

    /** Has the content been changed by editing or not */
    protected boolean ContentChanged = false;
    /** Current line wrapping status of the text area */
    protected boolean CurrentLineWrap = false;
    /** Current viewing/editing file name */
    protected String CurrentFileName = null;

    /** Model for the text content */
    protected DefaultStyledDocument Document = null;
    /** Stream tokenizer */
    protected StreamTokenizer Tokenizer = null;
    /** Normal text attributes */
    SimpleAttributeSet AttrNormal = null;
    /** Comment text attributes */
    SimpleAttributeSet AttrComment = null;
    /** Highlighted text attributes */
    SimpleAttributeSet AttrHighlight = null;

    /** Creates new form TextPanel */
    public TextPanel() {
        initComponents();
        PIS = new PipedInputStream();
        try {
            POS = new PrintStream(new PipedOutputStream(PIS));
        } catch (IOException ex) {
            logger.error("Error linking outputstream to inputstream", ex);
        }
    }

    /**
     * Return the print stream
     * @return
     */
    @Override
    public PrintStream getPrintStream() {
        return POS;
    }

    public void appendContent (String line) {
        try {
            // Disable document listener
            Document.removeDocumentListener(this);

            if (line != null) {
                int idx = line.indexOf('!');
                if (idx < 0) idx = line.length();
                Document.insertString(Document.getLength(), line.substring(0, idx), AttrNormal);
//                Document.insertString(Document.getLength(), line.substring(idx) + "\n", AttrComment);
                Document.insertString(Document.getLength(), line.substring(idx), AttrComment);
                Document.insertString(Document.getLength(), System.getProperty("line.separator"), AttrComment);
            }

            // Enable document listener
            Document.addDocumentListener(this);

        }catch (Exception ex) {
            logger.error("", ex);
        }
    }

    /**
     * Using a thread to keep reading input stream
     */
    @Override
    public void run() {
        BufferedReader BR = new BufferedReader (new InputStreamReader(PIS));
        while (! Stop) {
            try {
                while (BR.ready()) {
                    this.appendContent(BR.readLine());
                    this.appendContent("\n");
                }
                Thread.sleep(1000);
            } catch (IOException ex) {
                logger.error("", ex);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

    /** This method is called from within the constructor to 
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuPopup = new javax.swing.JPopupMenu();
        menuClear = new javax.swing.JMenuItem();
        menuSelectAll = new javax.swing.JMenuItem();
        menuCopy = new javax.swing.JMenuItem();
        menuSearch = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuAutoFormat = new javax.swing.JCheckBoxMenuItem();
        menuWrapText = new javax.swing.JCheckBoxMenuItem();
        menuSave = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        txpContent = new javax.swing.JTextPane();

        menuClear.setText("Clear");
        menuClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuClearActionPerformed(evt);
            }
        });
        menuPopup.add(menuClear);

        menuSelectAll.setText("Select all");
        menuSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSelectAllActionPerformed(evt);
            }
        });
        menuPopup.add(menuSelectAll);

        menuCopy.setText("Copy");
        menuCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCopyActionPerformed(evt);
            }
        });
        menuPopup.add(menuCopy);

        menuSearch.setText("Search ...");
        menuSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSearchActionPerformed(evt);
            }
        });
        menuPopup.add(menuSearch);
        menuPopup.add(jSeparator1);

        menuAutoFormat.setSelected(true);
        menuAutoFormat.setText("Auto format");
        menuAutoFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAutoFormatActionPerformed(evt);
            }
        });
        menuPopup.add(menuAutoFormat);

        menuWrapText.setSelected(true);
        menuWrapText.setText("Wrap text");
        menuWrapText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuWrapTextActionPerformed(evt);
            }
        });
        menuPopup.add(menuWrapText);

        menuSave.setText("Save ...");
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        menuPopup.add(menuSave);

        txpContent.setComponentPopupMenu(menuPopup);
        jScrollPane1.setViewportView(txpContent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
        JFileChooser fc = new JFileChooser(new File("."));
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(CurrentFileName, false))) {
                out.write(this.txpContent.getText());
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
        }
    }//GEN-LAST:event_menuSaveActionPerformed

    private void menuClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuClearActionPerformed
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Are you sure to clear all contents?",
                "Please confirm",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            this.txpContent.setText("");
        }
    }//GEN-LAST:event_menuClearActionPerformed

    private void menuSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSelectAllActionPerformed
        txpContent.selectAll();
    }//GEN-LAST:event_menuSelectAllActionPerformed

    private void menuCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCopyActionPerformed
        txpContent.copy();
    }//GEN-LAST:event_menuCopyActionPerformed

    private void menuWrapTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuWrapTextActionPerformed

    }//GEN-LAST:event_menuWrapTextActionPerformed

    private void menuSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSearchActionPerformed
        IncSearchPanel Search = new IncSearchPanel (this.txpContent);
        JDialog SearchDialog = new JDialog((JFrame)null, "Text search", false);
        SearchDialog.setSize(400, 100);
        SearchDialog.getContentPane().add(Search);
        SearchDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        SearchDialog.setVisible(true);
    }//GEN-LAST:event_menuSearchActionPerformed

    private void menuAutoFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAutoFormatActionPerformed
        if (menuAutoFormat.isSelected()) {
            
        }else {
            
        }
    }//GEN-LAST:event_menuAutoFormatActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JCheckBoxMenuItem menuAutoFormat;
    private javax.swing.JMenuItem menuClear;
    private javax.swing.JMenuItem menuCopy;
    private javax.swing.JPopupMenu menuPopup;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JMenuItem menuSearch;
    private javax.swing.JMenuItem menuSelectAll;
    private javax.swing.JCheckBoxMenuItem menuWrapText;
    protected javax.swing.JTextPane txpContent;
    // End of variables declaration//GEN-END:variables


    public static void main (String [] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame Pad = new JFrame("TextPanel test frame");
                Pad.setSize(600, 500);
                Pad.getContentPane().add(new TextPanel());
                Pad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Pad.setVisible(true);
            }
        });
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
