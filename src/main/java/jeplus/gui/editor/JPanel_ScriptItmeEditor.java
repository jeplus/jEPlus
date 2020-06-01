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

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.ConfigFileNames;
import jeplus.EPlusConfig;
import jeplus.JEPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.JEPlusProjectV2;
import jeplus.data.RVX_ScriptItem;
import jeplus.event.IF_ConfigChangedEventHandler;
import jeplus.gui.ButtonTabComponent;
import jeplus.gui.EPlusEditorPanel;
import jeplus.util.RelativeDirUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanel_ScriptItmeEditor extends javax.swing.JPanel implements IF_ConfigChangedEventHandler  {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanel_ScriptItmeEditor.class);

    JEPlusFrameMain MainGUI = null;
    JTree HostTree = null;
    protected JEPlusProjectV2 Project = null;
    protected RVX_ScriptItem Script = null;
    protected DocumentListener DL = null;
    private boolean DLActive = false;

    /**
     * Creates new form JPanel_RVXEditor
     */
    public JPanel_ScriptItmeEditor() {
        initComponents();
    }

    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     * @param frame
     * @param tree
     * @param prj
     * @param script
     */
    public JPanel_ScriptItmeEditor(JEPlusFrameMain frame, JTree tree, JEPlusProjectV2 prj) {
        initComponents();
        MainGUI = frame;
        HostTree = tree;
        Project = prj;
        this.cboLanguage.setModel(new DefaultComboBoxModel (JEPlusConfig.getDefaultInstance().getScripConfigs().keySet().toArray()));
        // setScriptItem (script);
        
        DL = new DocumentListener () {
            Document DocRviFile = txtScriptFile.getDocument();
            Document DocResultTable = txtResultTable.getDocument();
            Document DocArgs = txtArgs.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (DLActive && Script != null) {
                    Document src = e.getDocument();
                    if(src == DocRviFile) {
                        Script.setFileName(txtScriptFile.getText().trim());
                    }else if (src == DocArgs) {
                        Script.setArguments(txtArgs.getText().trim());
                    }else if (src == DocResultTable) {
                        Script.setTableName(txtResultTable.getText().trim());
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
        txtScriptFile.getDocument().addDocumentListener(DL);
        txtArgs.getDocument().addDocumentListener(DL);
        txtResultTable.getDocument().addDocumentListener(DL);
        DLActive = true;
        
        // Listen to config changed events
        JEPlusConfig.getDefaultInstance().addListener(this);
    }
    
    protected final void setScriptItem (RVX_ScriptItem script) {
        DLActive = false;
        Script = script;
        txtScriptFile.setText(Script.getFileName());
        if (Script.getLanguage().equalsIgnoreCase("jython")) {
            Script.setLanguage("python2");
        }
        if (JEPlusConfig.getDefaultInstance().getScripConfigs().containsKey(Script.getLanguage())) {
            cboLanguage.setSelectedItem(Script.getLanguage());
            lblWarning.setVisible(false);
        }else {
            lblWarning.setVisible(true);
        }
        txtArgs.setText(Script.getArguments());
        txtResultTable.setText(Script.getTableName());
        chkOnEachJob.setSelected(Script.isOnEachJob());
        DLActive = true;
    }

        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdSelectFile = new javax.swing.JButton();
        cmdEditScript = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtResultTable = new javax.swing.JTextField();
        chkOnEachJob = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboLanguage = new javax.swing.JComboBox();
        txtScriptFile = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtArgs = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lblWarning = new javax.swing.JLabel();
        cmdConfig = new javax.swing.JButton();

        cmdSelectFile.setText("...");
        cmdSelectFile.setToolTipText("Select rvi file (.rvi/.mvi). Single selection only.");
        cmdSelectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectFileActionPerformed(evt);
            }
        });

        cmdEditScript.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditScript.setToolTipText("Edit the contents of my.rvi");
        cmdEditScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditScriptActionPerformed(evt);
            }
        });

        jLabel2.setText("Result Table:");

        txtResultTable.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtResultTable.setText("SimResults");

        chkOnEachJob.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        chkOnEachJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOnEachJobActionPerformed(evt);
            }
        });

        jLabel3.setText("Run in each job folder: ");

        jLabel4.setText("Script file:");

        jLabel5.setText("Script language: ");

        cboLanguage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Annual", "Monthly", "Daily", "Hourly", "Timesteip" }));
        cboLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLanguageActionPerformed(evt);
            }
        });

        txtScriptFile.setText("Select a script file ...");

        jLabel6.setText(".csv");

        jLabel7.setText("Additional args: ");

        txtArgs.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Run a script to extract results from simulation outputs");
        jLabel1.setOpaque(true);

        lblWarning.setForeground(java.awt.Color.red);
        lblWarning.setText("<-- Check language");

        cmdConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/hammer_screwdriver.png"))); // NOI18N
        cmdConfig.setToolTipText("Configure tools");
        cmdConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdConfigActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6))
                            .addComponent(chkOnEachJob))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtScriptFile, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSelectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEditScript, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblWarning, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtArgs)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdEditScript)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cmdSelectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtScriptFile))
                        .addGap(13, 13, 13)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdConfig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboLanguage)
                    .addComponent(jLabel5)
                    .addComponent(lblWarning, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtArgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkOnEachJob)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectFileActionPerformed
        // Select a file to open
        MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.PYTHON));
        MainGUI.getFileChooser().setMultiSelectionEnabled(false);
        MainGUI.getFileChooser().setSelectedFile(new File(""));
        String rvidir = RelativeDirUtil.checkAbsolutePath(txtScriptFile.getText(), Project.getBaseDir());
        MainGUI.getFileChooser().setCurrentDirectory(new File(rvidir).getParentFile());
        if (MainGUI.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = MainGUI.getFileChooser().getSelectedFile();
            txtScriptFile.setText(file.getAbsolutePath());
        }
        MainGUI.getFileChooser().resetChoosableFileFilters();
        MainGUI.getFileChooser().setSelectedFile(new File(""));
    }//GEN-LAST:event_cmdSelectFileActionPerformed

    private void cmdEditScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditScriptActionPerformed

        // Test if the template file is present
        String fn = txtScriptFile.getText();
        String templfn = RelativeDirUtil.checkAbsolutePath(txtScriptFile.getText(), Project.getBaseDir());
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
                templfn = txtScriptFile.getText();
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
                EPlusEditorPanel.FileType.PYTHON,
                null);
            int ti = MainGUI.getTpnEditors().getTabCount();
            MainGUI.getTpnEditors().addTab(fn, ScriptFilePanel);
            ScriptFilePanel.setTabId(ti);
            MainGUI.getTpnEditors().setSelectedIndex(ti);
            MainGUI.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(MainGUI.getTpnEditors(), ScriptFilePanel));
            MainGUI.getTpnEditors().setToolTipTextAt(ti, templfn);
        }
    }//GEN-LAST:event_cmdEditScriptActionPerformed

    private void cboLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLanguageActionPerformed
        if (DLActive) {
            Script.setLanguage(cboLanguage.getSelectedItem().toString());
            lblWarning.setVisible(false);
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_cboLanguageActionPerformed

    private void chkOnEachJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOnEachJobActionPerformed
        if (DLActive) {
            Script.setOnEachJob(chkOnEachJob.isSelected());
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_chkOnEachJobActionPerformed

    private void cmdConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdConfigActionPerformed
        MainGUI.showConfigDialog();
    }//GEN-LAST:event_cmdConfigActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboLanguage;
    private javax.swing.JCheckBox chkOnEachJob;
    private javax.swing.JButton cmdConfig;
    private javax.swing.JButton cmdEditScript;
    private javax.swing.JButton cmdSelectFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblWarning;
    private javax.swing.JTextField txtArgs;
    private javax.swing.JTextField txtResultTable;
    private javax.swing.JTextField txtScriptFile;
    // End of variables declaration//GEN-END:variables

    @Override
    public void configChanged(ConfigFileNames config) {
        this.cboLanguage.setModel(new DefaultComboBoxModel (JEPlusConfig.getDefaultInstance().getScripConfigs().keySet().toArray()));
        if (Script != null && JEPlusConfig.getDefaultInstance().getScripConfigs().containsKey(Script.getLanguage())) {
            cboLanguage.setSelectedItem(Script.getLanguage());
            lblWarning.setVisible(false);
        }else {
            lblWarning.setVisible(true);
        }
    }
}
