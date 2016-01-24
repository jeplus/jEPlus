/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@gmail.com>               *
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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.EPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.JEPlusProject;
import jeplus.TRNSYSConfig;
import jeplus.util.RelativeDirUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class JPanel_TrnsysProjectFiles extends javax.swing.JPanel {

    /**
     * Logger
     */
    final private static org.slf4j.Logger logger = LoggerFactory.getLogger(JPanel_TrnsysProjectFiles.class);

    JEPlusFrameMain MainGUI = null;
    protected JEPlusProject Project = null;
    protected DocumentListener DL = null;
    
    /**
     * Creates new empty form JPanel_EPlusProjectFiles
     */
    public JPanel_TrnsysProjectFiles() {
        initComponents();
    }
    
    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     */
    public JPanel_TrnsysProjectFiles(JEPlusFrameMain frame, JEPlusProject project) {
        initComponents();
        MainGUI = frame;
        Project = project;

        this.txtGroupID.setText(Project.getProjectID());
        this.txtGroupNotes.setText(Project.getProjectNotes());
        txtDCKDir.setText(Project.getDCKDir());
        if (Project.getDCKTemplate() != null) {
            cboTemplateFile.setModel(new DefaultComboBoxModel (Project.getDCKTemplate().split("\\s*;\\s*")));
        }else {
            cboTemplateFile.setModel(new DefaultComboBoxModel (new String [] {"Select files..."}));
        }
        txtOutputFileNames.setText(Project.getOutputFileNames());
        txtRviDir.setText(Project.getRVIDir());
        if (Project.getRVIFile() != null) {
            cboRviFile.setModel(new DefaultComboBoxModel (new String [] {Project.getRVIFile()}));
        }else {
            cboRviFile.setModel(new DefaultComboBoxModel (new String [] {"Select a file..."}));
        }
        
        // Set listeners to text fields
        DL = new DocumentListener () {
            Document DocProjID = txtGroupID.getDocument();
            Document DocProjNotes = txtGroupNotes.getDocument();
            Document DocDCKDir = txtDCKDir.getDocument();
            Document DocOutputFiles = txtOutputFileNames.getDocument();
            Document DocRviDir = txtRviDir.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                Document src = e.getDocument();
                if(src == DocProjID) {
                    Project.setProjectID(txtGroupID.getText());
                }else if (src == DocProjNotes) {
                    Project.setProjectNotes(txtGroupNotes.getText());
                }else if (src == DocDCKDir) {
                    Project.setDCKDir(txtDCKDir.getText());
                }else if (src == DocOutputFiles) {
                    Project.setOutputFileNames(txtOutputFileNames.getText());
                }else if (src == DocRviDir) {
                    Project.setRVIDir(txtRviDir.getText());
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
        txtGroupID.getDocument().addDocumentListener(DL);
        txtGroupNotes.getDocument().addDocumentListener(DL);
        txtDCKDir.getDocument().addDocumentListener(DL);
        txtOutputFileNames.getDocument().addDocumentListener(DL);
        txtRviDir.getDocument().addDocumentListener(DL);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtDCKDir = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtOutputFileNames = new javax.swing.JTextField();
        txtGroupNotes = new javax.swing.JTextField();
        cboTemplateFile = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtGroupID = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cmdSelectTemplateFile = new javax.swing.JButton();
        cmdEditTemplate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        chkReadVar = new javax.swing.JCheckBox();
        cmdEditRVI = new javax.swing.JButton();
        txtRviDir = new javax.swing.JTextField();
        cmdSelectRVIFile = new javax.swing.JButton();
        cboRviFile = new javax.swing.JComboBox();

        txtDCKDir.setText("./");
        txtDCKDir.setToolTipText("Location of the DCK/TRD files. To use a relative path, edit this field manually.");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Notes:");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel16.setText("DCK/TRD template(s) / list: ");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("TR_");

        txtOutputFileNames.setText("trnsysout.csv");
        txtOutputFileNames.setToolTipText("Name of the ouput files to be collected after simulation. Check 'printers in the TRNSYS model.");

        txtGroupNotes.setToolTipText("Notes about this project");

        cboTemplateFile.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select files ..." }));
        cboTemplateFile.setToolTipText("Pull down to see all your selected files");

        jLabel6.setText("Folder:");

        txtGroupID.setText("G");
        txtGroupID.setToolTipText("You can use Project ID to tell apart jobs from different projects or batches.");
        txtGroupID.setMinimumSize(new java.awt.Dimension(20, 20));

        jLabel7.setText("File(s) / list file:");

        cmdSelectTemplateFile.setText("...");
        cmdSelectTemplateFile.setToolTipText("Select template file(s) (.dck/.trd) or list file (.lst). Hold 'Ctrl' key for multiple selections.");
        cmdSelectTemplateFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectTemplateFileActionPerformed(evt);
            }
        });

        cmdEditTemplate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditTemplate.setToolTipText("View and edit the template file. If you have a big model, it may take a while to load.");
        cmdEditTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditTemplateActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Printer file name(s):");

        chkReadVar.setSelected(true);
        chkReadVar.setText("Use RVX");
        chkReadVar.setToolTipText("Select to use the E+ ReadVarsESO untility. ");
        chkReadVar.setEnabled(false);
        chkReadVar.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chkReadVar.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        cmdEditRVI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditRVI.setToolTipText("Edit the contents of my.rvi");
        cmdEditRVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditRVIActionPerformed(evt);
            }
        });

        txtRviDir.setText("./");
        txtRviDir.setToolTipText("Location of the RVI file. To use a relative path, edit this field manually.");

        cmdSelectRVIFile.setText("...");
        cmdSelectRVIFile.setToolTipText("Select rvi file (.rvi/.mvi). Single selection only.");
        cmdSelectRVIFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectRVIFileActionPerformed(evt);
            }
        });

        cboRviFile.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select file ..." }));
        cboRviFile.setToolTipText("You can only specify one RVI or MVI file, here");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtGroupID, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel10)
                        .addGap(6, 6, 6)
                        .addComponent(txtGroupNotes))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(chkReadVar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtDCKDir, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(cboTemplateFile, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmdSelectTemplateFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmdEditTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(txtOutputFileNames)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRviDir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboRviFile, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSelectRVIFile, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEditRVI, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGroupID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10)
                    .addComponent(txtGroupNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboTemplateFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDCKDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)
                        .addComponent(cmdSelectTemplateFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmdEditTemplate))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutputFileNames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cmdSelectRVIFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboRviFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRviDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkReadVar)))
                    .addComponent(cmdEditRVI))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectTemplateFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectTemplateFileActionPerformed
        // Select a file to open
        MainGUI.getFileChooser().setFileFilter(TRNSYSConfig.getFileFilter(TRNSYSConfig.TRNINPUT));
        MainGUI.getFileChooser().setMultiSelectionEnabled(true);
        String idfdir = RelativeDirUtil.checkAbsolutePath(txtDCKDir.getText(), Project.getBaseDir());
        MainGUI.getFileChooser().setCurrentDirectory(new File(idfdir));
        if (MainGUI.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = MainGUI.getFileChooser().getSelectedFiles();
            String dir = files[0].getParent() + File.separator;
            txtDCKDir.setText(dir);
            String[] names = new String[files.length];
            names[0] = files[0].getName();
            String namestr = files[0].getName();
            for (int i = 1; i < files.length; i++) {
                names[i] = files[i].getName();
                namestr = namestr + "; " + names[i];
            }
            cboTemplateFile.setModel(new DefaultComboBoxModel(names));
            Project.setDCKDir(dir);
            Project.setDCKTemplate(namestr);
        }
        MainGUI.getFileChooser().resetChoosableFileFilters();
        MainGUI.getFileChooser().setSelectedFiles(null);
    }//GEN-LAST:event_cmdSelectTemplateFileActionPerformed

    private void cmdEditTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditTemplateActionPerformed

        // Test if the template file is present
        String fn = (String) cboTemplateFile.getSelectedItem();
        String templfn = RelativeDirUtil.checkAbsolutePath(txtDCKDir.getText() + fn, Project.getBaseDir());
        File ftmpl = new File(templfn);
        if (!ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                    this,
                    "<html><p><center>The template file " + templfn + " does not exist."
                    + "Do you want to select one?</center></p><p> Select 'NO' to create this file. </p>",
                    "Template file not available",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                this.cmdSelectTemplateFileActionPerformed(null);
                templfn = txtDCKDir.getText() + (String) cboTemplateFile.getSelectedItem();
            }
        }
        int idx = MainGUI.getTpnEditors().indexOfTab(fn);
        if (idx >= 0) {
            MainGUI.getTpnEditors().setSelectedIndex(idx);
        } else {
            EPlusTextPanel TemplFilePanel = new EPlusTextPanel(
                    MainGUI.getTpnEditors(),
                    fn,
                    EPlusTextPanel.EDITOR_MODE,
                    TRNSYSConfig.getFileFilter(TRNSYSConfig.TRNINPUT),
                    templfn,
                    Project);
            int ti = MainGUI.getTpnEditors().getTabCount();
            TemplFilePanel.setTabId(ti);
            MainGUI.getTpnEditors().addTab(fn, TemplFilePanel);
            MainGUI.getTpnEditors().setSelectedIndex(ti);
            MainGUI.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(MainGUI.getTpnEditors(), TemplFilePanel));
            MainGUI.getTpnEditors().setToolTipTextAt(ti, templfn);
        }
    }//GEN-LAST:event_cmdEditTemplateActionPerformed

    private void cmdSelectRVIFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectRVIFileActionPerformed
        // Select a file to open
        if (this.chkReadVar.isSelected()) {
            MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.RVX));
        }else {
            MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.RVI));
        }
        MainGUI.getFileChooser().setMultiSelectionEnabled(false);
        MainGUI.getFileChooser().setSelectedFile(new File(""));
        String rvidir = RelativeDirUtil.checkAbsolutePath(txtRviDir.getText(), Project.getBaseDir());
        MainGUI.getFileChooser().setCurrentDirectory(new File(rvidir));
        if (MainGUI.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = MainGUI.getFileChooser().getSelectedFile();
            String dir = file.getParent() + File.separator;
            String reldir = RelativeDirUtil.getRelativePath(dir, Project.getBaseDir(), "/");
            txtRviDir.setText(reldir);
            String name = file.getName();
            cboRviFile.setModel(new DefaultComboBoxModel(new String[]{name}));
            Project.setRVIDir(reldir);
            Project.setRVIFile(name);
        }
        MainGUI.getFileChooser().resetChoosableFileFilters();
        MainGUI.getFileChooser().setSelectedFile(new File(""));
        // TODO add your handling code here:
    }//GEN-LAST:event_cmdSelectRVIFileActionPerformed

    private void cmdEditRVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditRVIActionPerformed

        // Test if the template file is present
        String fn = (String) cboRviFile.getSelectedItem();
        if (fn.startsWith("Select ")) {
            fn = "my.rvx";
        }
        String templfn = RelativeDirUtil.checkAbsolutePath(txtRviDir.getText() + fn, Project.getBaseDir());
        File ftmpl = new File(templfn);
        if (!ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                    this,
                    "<html><p><center>" + templfn + " does not exist."
                    + "Do you want to copy one from an existing file?</center></p>"
                    + "<p> Alternatively, select 'NO' to create this file. </p>",
                    "RVI file not available",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                // Select a file to open
                if (this.chkReadVar.isSelected()) {
                    MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.RVX));
                }else {
                    MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.RVI));
                }
                MainGUI.getFileChooser().setMultiSelectionEnabled(false);
                MainGUI.getFileChooser().setSelectedFile(new File(""));
                String rvidir = RelativeDirUtil.checkAbsolutePath(txtRviDir.getText(), Project.getBaseDir());
                MainGUI.getFileChooser().setCurrentDirectory(new File(rvidir));
                if (MainGUI.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = MainGUI.getFileChooser().getSelectedFile();
                    try {
                        FileUtils.copyFile(file, new File(templfn));
                        cboRviFile.setModel(new DefaultComboBoxModel(new String[]{fn}));
                        Project.setRVIDir(txtRviDir.getText());
                        Project.setRVIFile(fn);
                    } catch (IOException ex) {
                        logger.error("Error copying RVX from source.", ex);
                    }
                }
                MainGUI.getFileChooser().resetChoosableFileFilters();
                MainGUI.getFileChooser().setSelectedFile(new File(""));
            }else if (n == JOptionPane.NO_OPTION) {
                
            }else {
                return;
            }
        }
        int idx = MainGUI.getTpnEditors().indexOfTab(fn);
        if (idx >= 0) {
            MainGUI.getTpnEditors().setSelectedIndex(idx);
        } else {
            EPlusEditorPanel RviFilePanel;
            if (FilenameUtils.getExtension(fn).equals("rvx")) {
                RviFilePanel = new EPlusEditorPanel(
                        MainGUI.getTpnEditors(),
                        fn,
                        templfn,
                        EPlusEditorPanel.FileType.RVX,
                        null);
            }else {
                RviFilePanel = new EPlusEditorPanel(
                        MainGUI.getTpnEditors(),
                        fn,
                        templfn,
                        EPlusEditorPanel.FileType.RVI,
                        null);
            }
            int ti = MainGUI.getTpnEditors().getTabCount();
            MainGUI.getTpnEditors().addTab(fn, RviFilePanel);
            RviFilePanel.setTabId(ti);
            MainGUI.getTpnEditors().setSelectedIndex(ti);
            MainGUI.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(MainGUI.getTpnEditors(), RviFilePanel));
            MainGUI.getTpnEditors().setToolTipTextAt(ti, templfn);
        }

    }//GEN-LAST:event_cmdEditRVIActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboRviFile;
    private javax.swing.JComboBox cboTemplateFile;
    private javax.swing.JCheckBox chkReadVar;
    private javax.swing.JButton cmdEditRVI;
    private javax.swing.JButton cmdEditTemplate;
    private javax.swing.JButton cmdSelectRVIFile;
    private javax.swing.JButton cmdSelectTemplateFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField txtDCKDir;
    private javax.swing.JTextField txtGroupID;
    private javax.swing.JTextField txtGroupNotes;
    private javax.swing.JTextField txtOutputFileNames;
    private javax.swing.JTextField txtRviDir;
    // End of variables declaration//GEN-END:variables
}
