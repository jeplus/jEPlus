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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.EPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.JEPlusProject;
import jeplus.util.RelativeDirUtil;
import org.apache.commons.io.FilenameUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 *
 * @author zyyz
 */
public class JPanel_EPlusProjectFiles extends javax.swing.JPanel {

    JEPlusFrameMain MainGUI = null;
    protected JEPlusProject Project = null;
    protected DocumentListener DL = null;
    
    /**
     * Creates new empty form JPanel_EPlusProjectFiles
     */
    public JPanel_EPlusProjectFiles() {
        initComponents();
    }
    
    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     */
    public JPanel_EPlusProjectFiles(JEPlusFrameMain frame, JEPlusProject project) {
        initComponents();
        MainGUI = frame;
        Project = project;

        this.txtGroupID.setText(Project.getProjectID());
        this.txtGroupNotes.setText(Project.getProjectNotes());
        txtIdfDir.setText(Project.getIDFDir());
        if (Project.getIDFTemplate() != null) {
            cboTemplateFile.setModel(new DefaultComboBoxModel (Project.getIDFTemplate().split("\\s*;\\s*")));
        }else {
            cboTemplateFile.setModel(new DefaultComboBoxModel (new String [] {"Select files..."}));
        }
        txtWthrDir.setText(Project.getWeatherDir());
        if (Project.getWeatherFile() != null) {
            cboWeatherFile.setModel(new DefaultComboBoxModel (Project.getWeatherFile().split("\\s*;\\s*")));
        }else {
            cboWeatherFile.setModel(new DefaultComboBoxModel (new String [] {"Select files..."}));
        }
        chkReadVar.setSelected(Project.isUseReadVars());
        txtRviDir.setText(Project.getRVIDir());
        if (Project.getRVIFile() != null) {
            cboRviFile.setModel(new DefaultComboBoxModel (new String [] {Project.getRVIFile()}));
        }else {
            cboRviFile.setModel(new DefaultComboBoxModel (new String [] {"Select a file..."}));
        }
        this.chkReadVarActionPerformed(null);
        
        // Set listeners to text fields
        DL = new DocumentListener () {
            Document DocProjID = txtGroupID.getDocument();
            Document DocProjNotes = txtGroupNotes.getDocument();
            Document DocIdfDir = txtIdfDir.getDocument();
            Document DocWthrDir = txtWthrDir.getDocument();
            Document DocRviDir = txtRviDir.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                Document src = e.getDocument();
                if(src == DocProjID) {
                    Project.setProjectID(txtGroupID.getText());
                }else if (src == DocProjNotes) {
                    Project.setProjectNotes(txtGroupNotes.getText());
                }else if (src == DocIdfDir) {
                    Project.setIDFDir(txtIdfDir.getText());
                }else if (src == DocWthrDir) {
                    Project.setWeatherDir(txtWthrDir.getText());
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
        txtIdfDir.getDocument().addDocumentListener(DL);
        txtWthrDir.getDocument().addDocumentListener(DL);
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

        txtIdfDir = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cboRviFile = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        txtWthrDir = new javax.swing.JTextField();
        chkReadVar = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        txtRviDir = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        cmdEditRVI = new javax.swing.JButton();
        txtGroupNotes = new javax.swing.JTextField();
        cmdEditWeather = new javax.swing.JButton();
        cboTemplateFile = new javax.swing.JComboBox();
        cmdSelectRVIFile = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtGroupID = new javax.swing.JTextField();
        cmdSelectWeatherFile = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        cmdSelectTemplateFile = new javax.swing.JButton();
        cboWeatherFile = new javax.swing.JComboBox();
        cmdEditTemplate = new javax.swing.JButton();

        txtIdfDir.setText("./");
        txtIdfDir.setToolTipText("Location of the IDF/IMF files. To use a relative path, edit this field manually.");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Notes:");

        cboRviFile.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select file ..." }));
        cboRviFile.setToolTipText("You can only specify one RVI or MVI file, here");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("IDF/IMF template(s):");

        txtWthrDir.setText("./");
        txtWthrDir.setToolTipText("Location of the weather files. To use a relative path, edit this field manually.");

        chkReadVar.setSelected(true);
        chkReadVar.setText("Use Extended RVI");
        chkReadVar.setToolTipText("Select to use the E+ ReadVarsESO untility. ");
        chkReadVar.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chkReadVar.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        chkReadVar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkReadVarActionPerformed(evt);
            }
        });

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("EP_");

        txtRviDir.setText("./");
        txtRviDir.setToolTipText("Location of the RVI file. To use a relative path, edit this field manually.");

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Weather file(s):");

        cmdEditRVI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditRVI.setToolTipText("Edit the contents of my.rvi");
        cmdEditRVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditRVIActionPerformed(evt);
            }
        });

        txtGroupNotes.setToolTipText("Notes about this project");

        cmdEditWeather.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditWeather.setToolTipText("View and edit the weather file. This may take a long time to load.");
        cmdEditWeather.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditWeatherActionPerformed(evt);
            }
        });

        cboTemplateFile.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select files ..." }));
        cboTemplateFile.setToolTipText("Pull down to see all your selected files");

        cmdSelectRVIFile.setText("...");
        cmdSelectRVIFile.setToolTipText("Select rvi file (.rvi/.mvi). Single selection only.");
        cmdSelectRVIFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectRVIFileActionPerformed(evt);
            }
        });

        jLabel6.setText("Folder:");

        txtGroupID.setText("G");
        txtGroupID.setToolTipText("You can use Project ID to tell apart jobs from different projects or batches.");
        txtGroupID.setMinimumSize(new java.awt.Dimension(20, 20));

        cmdSelectWeatherFile.setText("...");
        cmdSelectWeatherFile.setToolTipText("Select weather file(s) (.epw) or list file (.lst). Hold 'Ctrl' key for multiple selections.");
        cmdSelectWeatherFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectWeatherFileActionPerformed(evt);
            }
        });

        jLabel7.setText("File(s) / list file:");

        cmdSelectTemplateFile.setText("...");
        cmdSelectTemplateFile.setToolTipText("Select template file(s) (.idf/.imf) or list file (.lst). Hold 'Ctrl' key for multiple selections.");
        cmdSelectTemplateFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectTemplateFileActionPerformed(evt);
            }
        });

        cboWeatherFile.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select files ..." }));
        cboWeatherFile.setToolTipText("Pull down to see all your selected files");

        cmdEditTemplate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditTemplate.setToolTipText("View and edit the template file. If you have a big model, it may take a while to load.");
        cmdEditTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditTemplateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtGroupID, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkReadVar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRviDir, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtIdfDir, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                            .addComponent(txtWthrDir))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboRviFile, 0, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboWeatherFile, 0, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboTemplateFile, 0, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(cmdSelectWeatherFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEditWeather, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(cmdSelectTemplateFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEditTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(cmdSelectRVIFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEditRVI, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(txtGroupNotes))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdEditWeather))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmdSelectWeatherFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboWeatherFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtWthrDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboTemplateFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtIdfDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)
                        .addComponent(cmdSelectTemplateFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmdEditTemplate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmdSelectRVIFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboRviFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRviDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkReadVar))
                    .addComponent(cmdEditRVI))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkReadVarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkReadVarActionPerformed
        Project.setUseReadVars(true);
    }//GEN-LAST:event_chkReadVarActionPerformed

    private void cmdEditRVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditRVIActionPerformed

        // Test if the template file is present
        String fn = (String) cboRviFile.getSelectedItem();
        String templfn = RelativeDirUtil.checkAbsolutePath(txtRviDir.getText() + fn, Project.getBaseDir());
        File ftmpl = new File(templfn);
        if (!ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                    this,
                    "<html><p><center>The RVI/MVI file " + templfn + " does not exist."
                    + "Do you want to select one?</center></p><p> Select 'NO' to create this file. </p>",
                    "RVI file not available",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                this.cmdSelectRVIFileActionPerformed(null);
                templfn = txtRviDir.getText() + (String) cboRviFile.getSelectedItem();
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

    private void cmdEditWeatherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditWeatherActionPerformed
        // Test if the template file is present
        String fn = (String) cboWeatherFile.getSelectedItem();
        String templfn = RelativeDirUtil.checkAbsolutePath(txtWthrDir.getText() + fn, Project.getBaseDir());
        File ftmpl = new File(templfn);
        if (!ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                    this,
                    "<html><p><center>The weather file " + templfn + " does not exist."
                    + "Do you want to select one?</center></p><p> Select 'NO' to create this file. </p>",
                    "Weather file not available",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                this.cmdSelectWeatherFileActionPerformed(null);
                templfn = txtWthrDir.getText() + (String) cboWeatherFile.getSelectedItem();
            }
        }
        int idx = MainGUI.getTpnEditors().indexOfTab(fn);
        if (idx >= 0) {
            MainGUI.getTpnEditors().setSelectedIndex(idx);
        } else {
//            EPlusTextPanel WthrFilePanel = new EPlusTextPanel(
//                    MainGUI.getTpnEditors(),
//                    fn,
//                    EPlusTextPanel.VIEWER_MODE,
//                    EPlusConfig.getFileFilter(EPlusConfig.EPW),
//                    templfn,
//                    null);
            EPlusEditorPanel WthrFilePanel = new EPlusEditorPanel(
                    MainGUI.getTpnEditors(),
                    fn,
                    templfn,
                    EPlusEditorPanel.FileType.EPW,
                    null);
            int ti = MainGUI.getTpnEditors().getTabCount();
            WthrFilePanel.setTabId(ti);
            MainGUI.getTpnEditors().addTab(fn, WthrFilePanel);
            MainGUI.getTpnEditors().setSelectedIndex(ti);
            MainGUI.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(MainGUI.getTpnEditors(), WthrFilePanel));
            MainGUI.getTpnEditors().setToolTipTextAt(ti, templfn);
        }
    }//GEN-LAST:event_cmdEditWeatherActionPerformed

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
    }//GEN-LAST:event_cmdSelectRVIFileActionPerformed

    private void cmdSelectWeatherFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectWeatherFileActionPerformed
        // Select a file to open
        MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.EPW));
        MainGUI.getFileChooser().setMultiSelectionEnabled(true);
        MainGUI.getFileChooser().setSelectedFile(new File(""));
        String wdir = RelativeDirUtil.checkAbsolutePath(txtWthrDir.getText(), Project.getBaseDir());
        MainGUI.getFileChooser().setCurrentDirectory(new File(wdir));
        if (MainGUI.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = MainGUI.getFileChooser().getSelectedFiles();
            String dir = files[0].getParent() + File.separator;
            String reldir = RelativeDirUtil.getRelativePath(dir, Project.getBaseDir(), "/");
            txtWthrDir.setText(reldir);
            String[] names = new String[files.length];
            names[0] = files[0].getName();
            String namestr = files[0].getName();
            for (int i = 1; i < files.length; i++) {
                names[i] = files[i].getName();
                namestr = namestr + "; " + names[i];
            }
            cboWeatherFile.setModel(new DefaultComboBoxModel(names));
            Project.setWeatherDir(reldir);
            Project.setWeatherFile(namestr);
        }
        MainGUI.getFileChooser().resetChoosableFileFilters();
        MainGUI.getFileChooser().setSelectedFiles(null);
    }//GEN-LAST:event_cmdSelectWeatherFileActionPerformed

    private void cmdSelectTemplateFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectTemplateFileActionPerformed
        // Select a file to open
        MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.EPINPUT));
        MainGUI.getFileChooser().setMultiSelectionEnabled(true);
        MainGUI.getFileChooser().setSelectedFile(new File(""));
        String idfdir = RelativeDirUtil.checkAbsolutePath(txtIdfDir.getText(), Project.getBaseDir());
        MainGUI.getFileChooser().setCurrentDirectory(new File(idfdir));
        if (MainGUI.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = MainGUI.getFileChooser().getSelectedFiles();
            String dir = files[0].getParent() + File.separator;
            String reldir = RelativeDirUtil.getRelativePath(dir, Project.getBaseDir(), "/");
            txtIdfDir.setText(reldir);
            String[] names = new String[files.length];
            names[0] = files[0].getName();
            String namestr = files[0].getName();
            for (int i = 1; i < files.length; i++) {
                names[i] = files[i].getName();
                namestr = namestr + "; " + names[i];
            }
            cboTemplateFile.setModel(new DefaultComboBoxModel(names));
            Project.setIDFDir(reldir);
            Project.setIDFTemplate(namestr);
        }
        MainGUI.getFileChooser().resetChoosableFileFilters();
        MainGUI.getFileChooser().setSelectedFiles(null);
    }//GEN-LAST:event_cmdSelectTemplateFileActionPerformed

    private void cmdEditTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditTemplateActionPerformed

        // Test if the template file is present
        String fn = (String) cboTemplateFile.getSelectedItem();
        String templfn = RelativeDirUtil.checkAbsolutePath(txtIdfDir.getText() + fn, Project.getBaseDir());
        File ftmpl = new File(templfn);
        if (!ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                    MainGUI,
                    "<html><p><center>The template file " + templfn + " does not exist."
                    + "Do you want to select one?</center></p><p> Select 'NO' to create this file. </p>",
                    "Template file not available",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                this.cmdSelectTemplateFileActionPerformed(null);
                templfn = txtIdfDir.getText() + (String) cboTemplateFile.getSelectedItem();
            }
        }else {
            if (ftmpl.length() > 2000000) {
                int n = JOptionPane.showConfirmDialog(
                        MainGUI,
                        "<html><p><center>jEPlus editor does not handle large IDF models well.<br />Open " + templfn + " may slow down your computer considerably.<br />"
                        + "Do you want to continue?<br /> </center></p>",
                        "Template file is too big",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.NO_OPTION) {
                    return;
                }
            }
        }
        
        int idx = MainGUI.getTpnEditors().indexOfTab(fn);
        if (idx >= 0) {
            MainGUI.getTpnEditors().setSelectedIndex(idx);
        } else {
//            EPlusTextPanel TemplFilePanel = new EPlusTextPanel(
//                    MainGUI.getTpnEditors(),
//                    fn,
//                    EPlusTextPanel.EDITOR_MODE,
//                    EPlusConfig.getFileFilter(EPlusConfig.EPINPUT),
//                    templfn,
//                    Project);
            EPlusEditorPanel TemplFilePanel = new EPlusEditorPanel(
                    MainGUI.getTpnEditors(),
                    fn,
                    templfn,
                    EPlusEditorPanel.FileType.IDF,
                    Project);
            int ti = MainGUI.getTpnEditors().getTabCount();
            TemplFilePanel.setTabId(ti);
            MainGUI.getTpnEditors().addTab(fn, TemplFilePanel);
            MainGUI.getTpnEditors().setSelectedIndex(ti);
            MainGUI.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(MainGUI.getTpnEditors(), TemplFilePanel));
            MainGUI.getTpnEditors().setToolTipTextAt(ti, templfn);
        }
    }//GEN-LAST:event_cmdEditTemplateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboRviFile;
    private javax.swing.JComboBox cboTemplateFile;
    private javax.swing.JComboBox cboWeatherFile;
    private javax.swing.JCheckBox chkReadVar;
    private javax.swing.JButton cmdEditRVI;
    private javax.swing.JButton cmdEditTemplate;
    private javax.swing.JButton cmdEditWeather;
    private javax.swing.JButton cmdSelectRVIFile;
    private javax.swing.JButton cmdSelectTemplateFile;
    private javax.swing.JButton cmdSelectWeatherFile;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField txtGroupID;
    private javax.swing.JTextField txtGroupNotes;
    private javax.swing.JTextField txtIdfDir;
    private javax.swing.JTextField txtRviDir;
    private javax.swing.JTextField txtWthrDir;
    // End of variables declaration//GEN-END:variables
}
