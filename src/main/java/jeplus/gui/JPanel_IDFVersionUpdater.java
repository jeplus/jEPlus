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

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jeplus.EPlusWinTools;
import jeplus.IDFmodel;
import jeplus.JEPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.JEPlusProject;
import jeplus.data.RVX_RVIitem;
import jeplus.data.RVX;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class JPanel_IDFVersionUpdater extends javax.swing.JPanel {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JPanel_IDFVersionUpdater.class);

    protected JEPlusFrameMain MainFrame = null;
    protected EPlusTextPanelOld LogPanel = null;
    protected JFileChooser fc = new JFileChooser("./");
    protected JEPlusConfig Config = null;
    protected JEPlusProject Project = null;
    protected String CurrentFolder = "./";
    protected boolean ConverterAvailable = false;

    /**
     * Creates new form JPanel_IDFVersionUpdater
     * @param hostframe
     * @param config
     * @param project
     */
    public JPanel_IDFVersionUpdater(JEPlusFrameMain hostframe, JEPlusConfig config, JEPlusProject project) {
        MainFrame = hostframe;
        initComponents();
        LogPanel = MainFrame.getOutputPanel();
        Config = config;
        if (Config.getEPlusVerConvDir() != null) {
            this.setConverterFolder(Config.getEPlusVerConvDir());
        }
        setProject (project);
    }

    public final void setProject (JEPlusProject project) {
        if (project != null) {
            Project = project;
            if (fc == null) {
                fc = new JFileChooser(Project.getBaseDir());
            }else {
                fc.setCurrentDirectory(new File (Project.getBaseDir()));
            }
            CurrentFolder = Project.getBaseDir();
            this.rdoProjectFiles.setSelected(true);
            rdoProjectFilesActionPerformed(null);
        }
    }

    protected final void setConverterFolder (String dir) {
        txtConverterFolder.setText (dir);
        List<String> versions = EPlusWinTools.getInstalledTransitionVersions(dir);
        if (versions.size() < 2) {
            txtConverterFolder.setForeground(Color.red);
            ConverterAvailable = false;
        }else {
            cboStartVersion.setModel(new DefaultComboBoxModel (versions.toArray(new Object[0])));
            cboTargetVersion.setModel(new DefaultComboBoxModel (versions.toArray(new Object[0])));
            cboTargetVersion.setSelectedIndex(cboTargetVersion.getItemCount() - 1);
            txtConverterFolder.setForeground(Color.black);
            ConverterAvailable = true;
            Config.setEPlusVerConvDir(dir);
        }        
    }
    
    /**
     * Start a separate thread for the log panel
     */
    public void startLogThread() {
        new Thread (LogPanel, "LogThread").start();
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
     * this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel3 = new javax.swing.JLabel();
        jProgressBar = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        cboStartVersion = new javax.swing.JComboBox();
        rdoScanFolder = new javax.swing.JRadioButton();
        cmdSelectConverterFolder = new javax.swing.JButton();
        txtStatus = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        chkBackup = new javax.swing.JCheckBox();
        txtConverterFolder = new javax.swing.JTextField();
        txtFileFilter = new javax.swing.JTextField();
        rdoProjectFiles = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        txtFolder = new javax.swing.JTextField();
        cmdRescan = new javax.swing.JButton();
        cmdSelectFolder = new javax.swing.JButton();
        chkKeepInterim = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        cboTargetVersion = new javax.swing.JComboBox();
        cmdOpenFolder = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        cmdStartConvert = new javax.swing.JButton();

        jLabel3.setText("Progress: ");

        jProgressBar.setStringPainted(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        cboStartVersion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "---" }));

        buttonGroup1.add(rdoScanFolder);
        rdoScanFolder.setSelected(true);
        rdoScanFolder.setText("Scan folder");
        rdoScanFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoScanFolderActionPerformed(evt);
            }
        });

        cmdSelectConverterFolder.setText("...");
        cmdSelectConverterFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectConverterFolderActionPerformed(evt);
            }
        });

        txtStatus.setEditable(false);
        txtStatus.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtStatus.setText("?? files found ...");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("From EnergyPlus version: ");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("To EnergyPlus version: ");

        chkBackup.setSelected(true);
        chkBackup.setText("Backup the original files");
        chkBackup.setToolTipText("If selected, the original files will be kept with the extension of .ori");

        txtConverterFolder.setForeground(new java.awt.Color(255, 0, 0));
        txtConverterFolder.setText("Select the location of the converter program...");

        txtFileFilter.setText("*.idf *.imf *.rvi *.mvi");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rdoScanFolder, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtFileFilter, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        txtFileFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFileFilterActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoProjectFiles);
        rdoProjectFiles.setText("The model files and the RVI file in the current project");
        rdoProjectFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoProjectFilesActionPerformed(evt);
            }
        });

        jLabel5.setBackground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("E+ Version Updater program: ");
        jLabel5.setOpaque(true);

        txtFolder.setText("Select the folder containing files to convert ...");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rdoScanFolder, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtFolder, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cmdRescan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/view-refresh.png"))); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rdoScanFolder, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cmdRescan, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cmdRescan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRescanActionPerformed(evt);
            }
        });

        cmdSelectFolder.setText("...");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rdoScanFolder, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cmdSelectFolder, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cmdSelectFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectFolderActionPerformed(evt);
            }
        });

        chkKeepInterim.setText("Keep intermediate versions");
        chkKeepInterim.setToolTipText("If selected, intermediate versions will be kept with extensions such as .v7-2-0");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("For these files: ");

        cboTargetVersion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "---" }));

        cmdOpenFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/open.png"))); // NOI18N
        cmdOpenFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOpenFolderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtStatus)
                        .addGap(6, 6, 6)
                        .addComponent(cmdRescan, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboTargetVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboStartVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chkBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkKeepInterim))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtConverterFolder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSelectConverterFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(txtFolder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSelectFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFileFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdOpenFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdoProjectFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rdoScanFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(33, 33, 33)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdoProjectFiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(rdoScanFolder)
                                .addGap(3, 3, 3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmdSelectFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFileFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(cmdOpenFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdRescan)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtConverterFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectConverterFolder))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(cboStartVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(cboTargetVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkBackup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkKeepInterim)))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        cmdStartConvert.setText("Convert");
        cmdStartConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdStartConvertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdStartConvert)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdStartConvert))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(123, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdOpenFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOpenFolderActionPerformed
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            try {
                File output = new File (CurrentFolder);
                if (output.exists()) {
                    Desktop.getDesktop().open(output);
                }else {
                    JOptionPane.showMessageDialog(this, "Folder " + output.getAbsolutePath() + " does not exist.");
                }
            } catch (IOException ex) {
                logger.error("", ex);
            }
        }else {
            JOptionPane.showMessageDialog(this, "Open folder is not supported, or the current job record is not valid.", "Operation failed", JOptionPane.CLOSED_OPTION);
        }
    }//GEN-LAST:event_cmdOpenFolderActionPerformed

    private void cmdSelectFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectFolderActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtFolder.setText(fc.getSelectedFile().getAbsolutePath());
            CurrentFolder = txtFolder.getText();
            cmdRescanActionPerformed (null);
        }

    }//GEN-LAST:event_cmdSelectFolderActionPerformed

    private void cmdRescanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRescanActionPerformed
        LogPanel.getPrintStream().println("Scanning folder...");
        ArrayList <String> list = new ArrayList<> ();
        EPlusWinTools.scanFolderForFiles(new File (txtFolder.getText()), txtFileFilter.getText(), list);
        txtStatus.setText(list.size() + " files found ...");
        jProgressBar.setMaximum(list.size());
        try (PrintWriter fw = new PrintWriter (new FileWriter (CurrentFolder + File.separator + "convlist.lst"))) {
            for (int i=0; i<list.size(); i++) {
                fw.println(list.get(i));
                LogPanel.getPrintStream().println(list.get(i));
            }
        }catch (IOException ioe) {
            logger.error("", ioe);
        }
    }//GEN-LAST:event_cmdRescanActionPerformed

    private void cmdStartConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartConvertActionPerformed
        if (! ConverterAvailable) {
            JOptionPane.showMessageDialog(this,
                "<html>The converter folder is not valid. Please check again. </html>",
                "Check converter",
                JOptionPane.OK_OPTION);
            return;
        }
        int op = JOptionPane.showConfirmDialog(this,
            "<html>This function is still under testing. Please make sure you have backed up your files before proceed. Press OK when you are ready. </html>",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            LogPanel.getPrintStream().println("Start conversion ...");
            new Thread (new Runnable () {
                @Override
                public void run () {
                    EPlusWinTools.updateVersion(cboStartVersion.getSelectedItem().toString(),
                        cboTargetVersion.getSelectedItem().toString(),
                        txtConverterFolder.getText(),
                        CurrentFolder + File.separator + "convlist.lst",
                        chkBackup.isSelected(),
                        chkKeepInterim.isSelected(),
                        LogPanel.getPrintStream());
                }
            } ).start();
        }
    }//GEN-LAST:event_cmdStartConvertActionPerformed

    private void rdoProjectFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoProjectFilesActionPerformed
        CurrentFolder = Project.getBaseDir();
        // Get IDF files in the project
        String idfdir = Project.resolveIDFDir();
        List <String> list = Project.parseFileListString(idfdir, Project.getIDFTemplate());
        for (int i=0; i<list.size(); i++) {
            list.set(i, idfdir + list.get(i));
        }
        if (Project.getRVIFile() != null) {
            String rvifile = Project.resolveRVIDir() + Project.getRVIFile();
            try {
                RVX rvx = RVX.getRVX(rvifile);
                if (rvx.getRVIs() != null && rvx.getRVIs().length > 0) {
                    for (RVX_RVIitem item : rvx.getRVIs()) {
                        list.add(RelativeDirUtil.checkAbsolutePath(item.getFileName(), Project.resolveRVIDir()));
                    }
                }
            } catch (IOException ex) {
                logger.error("Error loading rvi/rvx file " + rvifile, ex);
            }
        }
        txtStatus.setText(list.size() + " files found ...");
        LogPanel.getPrintStream().println("The project contains: ");
        jProgressBar.setMaximum(list.size());
        try (PrintWriter fw = new PrintWriter (new FileWriter (CurrentFolder + File.separator + "convlist.lst"))) {
            for (int i=0; i<list.size(); i++) {
                fw.println(list.get(i));
                LogPanel.getPrintStream().println(list.get(i));
            }
        }catch (IOException ioe) {
            logger.error("", ioe);
        }
        // version info
        if (list.size() > 0) {
            String ver = IDFmodel.getEPlusVersionInIDF (list.get(0));
        }
    }//GEN-LAST:event_rdoProjectFilesActionPerformed

    private void txtFileFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFileFilterActionPerformed
        cmdRescanActionPerformed (evt);
    }//GEN-LAST:event_txtFileFilterActionPerformed

    private void cmdSelectConverterFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectConverterFolderActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setConverterFolder(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_cmdSelectConverterFolderActionPerformed

    private void rdoScanFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoScanFolderActionPerformed
        cmdRescanActionPerformed (evt);
    }//GEN-LAST:event_rdoScanFolderActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboStartVersion;
    private javax.swing.JComboBox cboTargetVersion;
    private javax.swing.JCheckBox chkBackup;
    private javax.swing.JCheckBox chkKeepInterim;
    private javax.swing.JButton cmdOpenFolder;
    private javax.swing.JButton cmdRescan;
    private javax.swing.JButton cmdSelectConverterFolder;
    private javax.swing.JButton cmdSelectFolder;
    private javax.swing.JButton cmdStartConvert;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton rdoProjectFiles;
    private javax.swing.JRadioButton rdoScanFolder;
    private javax.swing.JTextField txtConverterFolder;
    private javax.swing.JTextField txtFileFilter;
    private javax.swing.JTextField txtFolder;
    private javax.swing.JTextField txtStatus;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public static void main (String [] args) {
        JFrame frame = new JFrame ("IDF Version Converter");
        JPanel_IDFVersionUpdater panel = new JPanel_IDFVersionUpdater (null, JEPlusConfig.getDefaultInstance(), new JEPlusProject ());
        frame.getContentPane().add(panel);
        panel.startLogThread();
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}