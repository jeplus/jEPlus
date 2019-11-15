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

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.data.ExecutionOptions;

/**
 *
 * @author yzhang
 */
public class JPanel_LocalControllerOptions extends javax.swing.JPanel {

    protected ExecutionOptions Settings = new ExecutionOptions(ExecutionOptions.INTERNAL_CONTROLLER);
    protected JFileChooser fc = new JFileChooser(Settings.getParentDir());
    protected int MaxThreads = Runtime.getRuntime().availableProcessors();
    
    protected DocumentListener DL = null;

    /** Creates new form JPanel_LocalControllerOptions */
    public JPanel_LocalControllerOptions() {
        initComponents();
        initSettings();
    }

    /** 
     * Creates new form JPanel_LocalControllerOptions
     * @param settings 
     */
    public JPanel_LocalControllerOptions(ExecutionOptions settings) {
        initComponents();
        setSettings(settings);
    }

    public ExecutionOptions getSettings() {
        return Settings;
    }

    public final void setSettings(ExecutionOptions Settings) {
        this.Settings = Settings;
        initSettings();
    }

    protected final void initSettings () {
        setThreadOptions();
        this.txtThreadDelay.setText(Integer.toString(Settings.getDelay()));
        this.txtFileDir.setText(Settings.getParentDir());
        this.chkKeepJobDir.setSelected(Settings.isKeepJobDir());
        this.chkKeepJEPlusFiles.setSelected(Settings.isKeepJEPlusFiles());
        this.chkKeepEPlusFiles.setSelected(Settings.isKeepEPlusFiles());
        this.chkDeleteSelected.setSelected(Settings.isDeleteSelectedFiles());
        this.txtSelectedFiles.setText(Settings.getSelectedFiles());
        this.txtEPlusThreads.setText(Integer.toString(Settings.getOMPThreads()));

        // Set listeners to text fields
        DL = new DocumentListener () {
            Document DocFileDir = txtFileDir.getDocument();
            Document DocThreadDelay = txtThreadDelay.getDocument();
            Document DocEPlusThreads = txtEPlusThreads.getDocument();
            Document DocSelectedFiles = txtSelectedFiles.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                Document src = e.getDocument();
                if(src == DocFileDir) {
                    Settings.setWorkDir(txtFileDir.getText());
                }else if (src == DocThreadDelay) {
                    Settings.setDelay(Integer.parseInt(txtThreadDelay.getText()));
                }else if(src == DocEPlusThreads) {
                    Settings.setOMPThreads(Integer.parseInt(txtEPlusThreads.getText()));
                }else if(src == DocSelectedFiles) {
                    Settings.setSelectedFiles(txtSelectedFiles.getText());
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
        txtFileDir.getDocument().addDocumentListener(DL);
        txtThreadDelay.getDocument().addDocumentListener(DL);
        txtEPlusThreads.getDocument().addDocumentListener(DL);
        txtSelectedFiles.getDocument().addDocumentListener(DL);
    }

    protected final void setThreadOptions () {
        String [] list = new String [MaxThreads];
        for (int i=0; i<MaxThreads; i++) list[i] = Integer.toString(i + 1);
        this.cboNThreads.setModel(new DefaultComboBoxModel (list));
        // this.cboNThreads.setSelectedIndex(list.length - 1);
        this.cboNThreads.setSelectedIndex(Math.min(Settings.getNumThreads(), MaxThreads) - 1);
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel18 = new javax.swing.JLabel();
        txtFileDir = new javax.swing.JTextField();
        chkKeepJobDir = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        txtThreadDelay = new javax.swing.JTextField();
        chkKeepEPlusFiles = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        chkKeepJEPlusFiles = new javax.swing.JCheckBox();
        cmdSelectWorkDir = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboNThreads = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        txtEPlusThreads = new javax.swing.JTextField();
        chkDeleteSelected = new javax.swing.JCheckBox();
        txtSelectedFiles = new javax.swing.JTextField();

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Working dir: ");

        txtFileDir.setText("./");
        txtFileDir.setToolTipText("The working directory serves as the root to all the sub-directories to be generated during the simulation.");

        chkKeepJobDir.setSelected(true);
        chkKeepJobDir.setText("Keep job directories, including eplusout.err/end/csv");
        chkKeepJobDir.setEnabled(false);
        chkKeepJobDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkKeepJobDirActionPerformed(evt);
            }
        });

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel15.setText("Start delay: ");

        txtThreadDelay.setEditable(false);
        txtThreadDelay.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtThreadDelay.setText("1000");
        txtThreadDelay.setToolTipText("Time delay (in milliseconds) between the start of each thread.");

        chkKeepEPlusFiles.setSelected(true);
        chkKeepEPlusFiles.setText("Keep all EnergyPlus or TRNSYS output files, e.g. eplusout.eso");
        chkKeepEPlusFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkKeepEPlusFilesActionPerformed(evt);
            }
        });

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Parallel jobs: ");

        chkKeepJEPlusFiles.setSelected(true);
        chkKeepJEPlusFiles.setText("Keep jEPlus intermediate files, e.g. in.idf, in.epw etc.");
        chkKeepJEPlusFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkKeepJEPlusFilesActionPerformed(evt);
            }
        });

        cmdSelectWorkDir.setText("...");
        cmdSelectWorkDir.setToolTipText("Select the root working directory");
        cmdSelectWorkDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectWorkDirActionPerformed(evt);
            }
        });

        jLabel1.setText(" ms");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Clean up: ");

        cboNThreads.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2" }));
        cboNThreads.setToolTipText("Number of jobs to be run in parallel.");
        cboNThreads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNThreadsActionPerformed(evt);
            }
        });

        jLabel3.setText("E+ OMP threads: ");

        txtEPlusThreads.setEditable(false);
        txtEPlusThreads.setText("1");
        txtEPlusThreads.setToolTipText("E+ v7.1 OMP is not recommended. If it is enabled in the model, please adjust the number of parallel jobs accordingly.");

        chkDeleteSelected.setText("Delete selected files: ");
        chkDeleteSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDeleteSelectedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboNThreads, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEPlusThreads, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtThreadDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFileDir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSelectWorkDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkDeleteSelected)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSelectedFiles))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkKeepJobDir)
                            .addComponent(chkKeepJEPlusFiles)
                            .addComponent(chkKeepEPlusFiles))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel1)
                    .addComponent(txtThreadDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(cboNThreads, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtEPlusThreads, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtFileDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectWorkDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkKeepJobDir)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkKeepJEPlusFiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkKeepEPlusFiles)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkDeleteSelected)
                    .addComponent(txtSelectedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectWorkDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectWorkDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File (Settings.getParentDir()));
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Settings.setParentDir(fc.getSelectedFile().getAbsolutePath() + File.separator);
            txtFileDir.setText(Settings.getParentDir());
        }
    }//GEN-LAST:event_cmdSelectWorkDirActionPerformed

    private void cboNThreadsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNThreadsActionPerformed
        Settings.setNumThreads(this.cboNThreads.getSelectedIndex() + 1);
    }//GEN-LAST:event_cboNThreadsActionPerformed

    private void chkKeepJobDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkKeepJobDirActionPerformed
        Settings.setKeepJobDir(this.chkKeepJobDir.isSelected());
    }//GEN-LAST:event_chkKeepJobDirActionPerformed

    private void chkKeepJEPlusFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkKeepJEPlusFilesActionPerformed
        Settings.setKeepJEPlusFiles(this.chkKeepJEPlusFiles.isSelected());
    }//GEN-LAST:event_chkKeepJEPlusFilesActionPerformed

    private void chkKeepEPlusFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkKeepEPlusFilesActionPerformed
        Settings.setKeepEPlusFiles(this.chkKeepEPlusFiles.isSelected());
    }//GEN-LAST:event_chkKeepEPlusFilesActionPerformed

    private void chkDeleteSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDeleteSelectedActionPerformed
        Settings.setDeleteSelectedFiles(this.chkDeleteSelected.isSelected());
    }//GEN-LAST:event_chkDeleteSelectedActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboNThreads;
    private javax.swing.JCheckBox chkDeleteSelected;
    private javax.swing.JCheckBox chkKeepEPlusFiles;
    private javax.swing.JCheckBox chkKeepJEPlusFiles;
    private javax.swing.JCheckBox chkKeepJobDir;
    private javax.swing.JButton cmdSelectWorkDir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField txtEPlusThreads;
    private javax.swing.JTextField txtFileDir;
    private javax.swing.JTextField txtSelectedFiles;
    private javax.swing.JTextField txtThreadDelay;
    // End of variables declaration//GEN-END:variables

}
