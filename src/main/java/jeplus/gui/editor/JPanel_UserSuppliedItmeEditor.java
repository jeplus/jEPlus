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
package jeplus.gui.editor;

import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.EPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.JEPlusProjectV2;
import jeplus.data.RVX_UserSuppliedItem;
import jeplus.gui.ButtonTabComponent;
import jeplus.gui.EPlusEditorPanel;
import jeplus.util.RelativeDirUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanel_UserSuppliedItmeEditor extends javax.swing.JPanel {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanel_UserSuppliedItmeEditor.class);

    JEPlusFrameMain MainGUI = null;
    JTree HostTree = null;
    protected JEPlusProjectV2 Project = null;
    protected RVX_UserSuppliedItem UserCsv = null;
    protected DocumentListener DL = null;
    private boolean DLActive = false;

    /**
     * Creates new form JPanel_RVXEditor
     */
    public JPanel_UserSuppliedItmeEditor() {
        initComponents();
    }

    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     * @param frame
     * @param tree
     * @param prj
     * @param csv
     */
    public JPanel_UserSuppliedItmeEditor(JEPlusFrameMain frame, JTree tree, JEPlusProjectV2 prj, RVX_UserSuppliedItem csv) {
        initComponents();
        MainGUI = frame;
        HostTree = tree;
        Project = prj;
        setItem (csv);
        
        DL = new DocumentListener () {
            Document DocCsvFile = txtCsvFile.getDocument();
            Document DocHeaderRowNo = txtHeaderRowNo.getDocument();
            Document DocJobIDColumnNo = txtJobIDColumnNo.getDocument();
            Document DocDataColumns = txtDataColumns.getDocument();
            Document DocMissingValue = txtMissingValue.getDocument();
            Document DocResultTable = txtResultTable.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (DLActive) {
                    Document src = e.getDocument();
                    if(src == DocCsvFile) {
                        UserCsv.setFileName(txtCsvFile.getText());
                    }else if (src == DocHeaderRowNo) {
                        try {
                            UserCsv.setHeaderRow(Integer.parseInt(txtHeaderRowNo.getText().trim()));
                            txtHeaderRowNo.setForeground(Color.black);
                        }catch (NumberFormatException nfe) {
                            txtHeaderRowNo.setForeground(Color.red);
                            UserCsv.setHeaderRow(0);
                        }
                    }else if (src == DocJobIDColumnNo) {
                        try {
                            UserCsv.setJobIdColumn(Integer.parseInt(txtJobIDColumnNo.getText().trim()));
                            txtJobIDColumnNo.setForeground(Color.black);
                        }catch (NumberFormatException nfe) {
                            txtJobIDColumnNo.setForeground(Color.red);
                            UserCsv.setJobIdColumn(0);
                        }
                    }else if (src == DocDataColumns) {
                        UserCsv.setDataColumns(txtDataColumns.getText().trim());
                    }else if (src == DocMissingValue) {
                        try {
                            UserCsv.setMissingValue(Double.parseDouble(txtMissingValue.getText().trim()));
                            txtMissingValue.setForeground(Color.black);
                        }catch (NumberFormatException nfe) {
                            txtMissingValue.setForeground(Color.red);
                            UserCsv.setMissingValue(0);
                        }
                    }else if (src == DocResultTable) {
                        UserCsv.setTableName(txtResultTable.getText().trim());
                    }
                    Project.setContentChanged(true);
                    HostTree.update(HostTree.getGraphics());
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // not applicable
            }
        };
        txtCsvFile.getDocument().addDocumentListener(DL);
        txtHeaderRowNo.getDocument().addDocumentListener(DL);
        txtJobIDColumnNo.getDocument().addDocumentListener(DL);
        txtDataColumns.getDocument().addDocumentListener(DL);
        txtMissingValue.getDocument().addDocumentListener(DL);
        txtResultTable.getDocument().addDocumentListener(DL);
        DLActive = true;
    }
    
    protected final void setItem (RVX_UserSuppliedItem csv) {
        UserCsv = csv;
        txtCsvFile.setText(UserCsv.getFileName());
        txtHeaderRowNo.setText(Integer.toString(UserCsv.getHeaderRow()));
        txtJobIDColumnNo.setText(Integer.toString(UserCsv.getJobIdColumn()));
        txtDataColumns.setText(UserCsv.getDataColumns());
        txtMissingValue.setText(Double.toString(UserCsv.getMissingValue()));
        txtResultTable.setText(UserCsv.getTableName());
    }

        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        txtResultTable = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtCsvFile = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtHeaderRowNo = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtJobIDColumnNo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtDataColumns = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtMissingValue = new javax.swing.JTextField();
        cmdSelectFile = new javax.swing.JButton();
        cmdEditCsv = new javax.swing.JButton();

        jLabel2.setText("Result Table:");

        txtResultTable.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtResultTable.setText("table");

        jLabel4.setText("Custom CSV file: ");

        jLabel6.setText(".csv");

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Get data of jobs from a user supplied CSV file");
        jLabel1.setOpaque(true);

        txtCsvFile.setText("jTextField1");

        jLabel7.setText("Header Row #:");

        txtHeaderRowNo.setText("jTextField1");

        jLabel8.setText("Job ID Column #: ");

        txtJobIDColumnNo.setText("jTextField1");

        jLabel9.setText("Data Columns: ");

        txtDataColumns.setText("jTextField1");

        jLabel10.setText("Default Value if missing: ");

        txtMissingValue.setText("jTextField1");

        cmdSelectFile.setText("...");
        cmdSelectFile.setToolTipText("Select rvi file (.rvi/.mvi). Single selection only.");
        cmdSelectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectFileActionPerformed(evt);
            }
        });

        cmdEditCsv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditCsv.setToolTipText("Edit the contents of my.rvi");
        cmdEditCsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditCsvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDataColumns)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6))
                                    .addComponent(txtMissingValue, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtJobIDColumnNo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHeaderRowNo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 92, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCsvFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSelectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEditCsv, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCsvFile, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmdSelectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(4, 4, 4)))
                    .addComponent(cmdEditCsv))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtHeaderRowNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtJobIDColumnNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtDataColumns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtMissingValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectFileActionPerformed
        // Select a file to open
        MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.CSV));
        MainGUI.getFileChooser().setMultiSelectionEnabled(false);
        MainGUI.getFileChooser().setSelectedFile(new File(""));
        String rvidir = RelativeDirUtil.checkAbsolutePath(txtCsvFile.getText(), Project.getBaseDir());
        MainGUI.getFileChooser().setCurrentDirectory(new File(rvidir).getParentFile());
        if (MainGUI.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = MainGUI.getFileChooser().getSelectedFile();
            txtCsvFile.setText(file.getAbsolutePath());
        }
        MainGUI.getFileChooser().resetChoosableFileFilters();
        MainGUI.getFileChooser().setSelectedFile(new File(""));
    }//GEN-LAST:event_cmdSelectFileActionPerformed

    private void cmdEditCsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditCsvActionPerformed

        // Test if the template file is present
        String fn = txtCsvFile.getText();
        String templfn = RelativeDirUtil.checkAbsolutePath(txtCsvFile.getText(), Project.getBaseDir());
        File ftmpl = new File(templfn);
        if (!ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                this,
                "<html><p><center>The script file " + templfn + " does not exist."
                + "Do you want to select one?</center></p><p> Select 'NO' to create this file. </p>",
                "File not available",
                JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                this.cmdSelectFileActionPerformed(null);
                templfn = txtCsvFile.getText();
            }
        }
        int idx = MainGUI.getTpnEditors().indexOfTab(fn);
        if (idx >= 0) {
            MainGUI.getTpnEditors().setSelectedIndex(idx);
        } else {
            EPlusEditorPanel ScriptFilePanel;
            ScriptFilePanel = new EPlusEditorPanel(
                MainGUI.getTpnEditors(),
                fn,
                templfn,
                EPlusEditorPanel.FileType.CSV,
                null);
            int ti = MainGUI.getTpnEditors().getTabCount();
            MainGUI.getTpnEditors().addTab(fn, ScriptFilePanel);
            ScriptFilePanel.setTabId(ti);
            MainGUI.getTpnEditors().setSelectedIndex(ti);
            MainGUI.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(MainGUI.getTpnEditors(), ScriptFilePanel));
            MainGUI.getTpnEditors().setToolTipTextAt(ti, templfn);
        }
    }//GEN-LAST:event_cmdEditCsvActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdEditCsv;
    private javax.swing.JButton cmdSelectFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txtCsvFile;
    private javax.swing.JTextField txtDataColumns;
    private javax.swing.JTextField txtHeaderRowNo;
    private javax.swing.JTextField txtJobIDColumnNo;
    private javax.swing.JTextField txtMissingValue;
    private javax.swing.JTextField txtResultTable;
    // End of variables declaration//GEN-END:variables
}
