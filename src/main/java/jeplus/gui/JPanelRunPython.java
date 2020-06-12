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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.ConfigFileNames;
import jeplus.EPlusConfig;
import jeplus.EPlusTask;
import jeplus.JEPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.ScriptConfig;
import jeplus.JEPlusVersion;
import jeplus.event.IF_ConfigChangedEventHandler;
import jeplus.util.ScriptTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanelRunPython extends javax.swing.JPanel implements IF_ConfigChangedEventHandler {
  
    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanelRunPython.class);
    
    protected JEPlusFrameMain MainGUI = null;
    protected JFileChooser fc = new JFileChooser("./");
    protected EPlusTextPanelOld OutputViewer = null;
    protected String CurrentWorkDir = "./";
    protected JEPlusConfig Config = null;

    private DocumentListener DL = null;
    
    /**
     * Creates new form JPanelRunPython
     * @param hostframe
     * @param config
     * @param workdir
     */
    public JPanelRunPython(JEPlusFrameMain hostframe, JEPlusConfig config, String workdir) {
        initComponents();
        initDL();
        MainGUI = hostframe;
        OutputViewer = MainGUI.getOutputPanel();
        Config = config;
        Config.addListener(this);
        CurrentWorkDir = workdir;
        updateDisplay();
    }

    public void setCurrentWorkDir(String CurrentWorkDir) {
        this.CurrentWorkDir = CurrentWorkDir;
        updateDisplay();
    }

    
    /**
     * initialises the parameter tree, by setting up tree nodes and tree model
     */
    private void initDL () {
        // Set listeners to text fields
        DL = new DocumentListener () {
            Document DocProjectBase = txtProjectBase.getDocument();
            Document DocWorkDir = txtWorkDir.getDocument();
            Document DocJobList = txtJobList.getDocument();
            Document DocOutputFile = txtOutputFile.getDocument();
            Document DocMoreArguments = txtMoreArguments.getDocument();
            Document DocScriptFileName = txtScriptFileName.getDocument();
            @Override
            public void insertUpdate(DocumentEvent e) {
                Document src = e.getDocument();
                if(src == DocProjectBase) {
                    
                }else if(src == DocWorkDir) {
                    
                }else if (src == DocJobList) {
                    
                }else if (src == DocOutputFile) {
                    
                }else if (src == DocMoreArguments) {
                    
                }else if (src == DocScriptFileName) {
                    
                }
                txaCmdLn.setText(updateSampleCommandLine());
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
        txtProjectBase.getDocument().addDocumentListener(DL);
        txtWorkDir.getDocument().addDocumentListener(DL);
        txtJobList.getDocument().addDocumentListener(DL);
        txtOutputFile.getDocument().addDocumentListener(DL);
        txtMoreArguments.getDocument().addDocumentListener(DL);
        txtScriptFileName.getDocument().addDocumentListener(DL);
    }
    
    private String updateSampleCommandLine () {
        if (cboLang.getSelectedItem() != null) {
            ScriptConfig cfg = Config.getScripConfigs().get(cboLang.getSelectedItem().toString());
            StringBuilder buf = new StringBuilder (cfg.getExec());
            buf.append(" ").append(cfg.getArgs()).append(" \"");
            buf.append(txtScriptFileName.getText().trim()).append("\" ");
            if (chkPassProjectBase.isSelected()) {
                buf.append("\"").append(txtProjectBase.getText().trim()).append("\" ");
            }
            if (chkPassWorkDir.isSelected()) {
                buf.append("\"").append(txtWorkDir.getText().trim()).append("\" ");
            }
            if (chkPassJobList.isSelected()) {
                buf.append("\"").append(txtJobList.getText().trim()).append("\" ");
            }
            if (chkPassOutputFile.isSelected()) {
                buf.append("\"").append(txtOutputFile.getText().trim()).append("\" ");
            }
            if (chkMoreArguments.isSelected()) {
                buf.append("\"").append(txtMoreArguments.getText().trim()).append("\" ");
            }
            return buf.toString();
        }
        return "";
    } 
    
    public final void updateDisplay () {
        this.cboLang.setModel(new DefaultComboBoxModel (JEPlusConfig.getDefaultInstance().getScripConfigs().keySet().toArray()));
        this.txtWorkDir.setText(CurrentWorkDir);
        this.txtProjectBase.setText(MainGUI.getProject().getBaseDir());
        this.cmdSyncJobListActionPerformed(null);
        this.txtMoreArguments.setText(Config.getPythonArgv() == null ? "" : Config.getPythonArgv());
        this.txtScriptFileName.setText(Config.getPythonScript() == null ? "" : Config.getPythonScript());
        this.repaint();
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        buttonGroup1 = new javax.swing.ButtonGroup();
        cmdOpenConsole = new javax.swing.JButton();
        cmdRunScript = new javax.swing.JButton();
        cmdEditScript = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cmdSelectWorkDir = new javax.swing.JButton();
        chkPassWorkDir = new javax.swing.JCheckBox();
        txtWorkDir = new javax.swing.JTextField();
        cmdSelectScriptFile = new javax.swing.JButton();
        txtMoreArguments = new javax.swing.JTextField();
        chkMoreArguments = new javax.swing.JCheckBox();
        txtScriptFileName = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        chkPassJobList = new javax.swing.JCheckBox();
        txtJobList = new javax.swing.JTextField();
        chkPassOutputFile = new javax.swing.JCheckBox();
        txtOutputFile = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmdSyncJobList = new javax.swing.JButton();
        cmdViewOutputFile = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaCmdLn = new javax.swing.JTextArea();
        chkPassProjectBase = new javax.swing.JCheckBox();
        txtProjectBase = new javax.swing.JTextField();
        cmdSelectProjectBase = new javax.swing.JButton();
        cboLang = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cmdConfig = new javax.swing.JButton();

        cmdOpenConsole.setText("Open a Console");
        cmdOpenConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOpenConsoleActionPerformed(evt);
            }
        });

        cmdRunScript.setText("Run");
        cmdRunScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRunScriptActionPerformed(evt);
            }
        });

        cmdEditScript.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditScript.setToolTipText("View and edit the weather file. This may take a long time to load.");
        cmdEditScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditScriptActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Script file: ");
        jLabel1.setOpaque(true);

        cmdSelectWorkDir.setText("...");
        cmdSelectWorkDir.setToolTipText("Select the the working directory");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkPassWorkDir, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cmdSelectWorkDir, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cmdSelectWorkDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectWorkDirActionPerformed(evt);
            }
        });

        chkPassWorkDir.setSelected(true);
        chkPassWorkDir.setText("Pass Work dir:");
        chkPassWorkDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPassWorkDirActionPerformed(evt);
            }
        });

        txtWorkDir.setText("./");
        txtWorkDir.setToolTipText("The working directory serves as the root to all the sub-directories to be generated during the simulation.");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkPassWorkDir, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtWorkDir, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cmdSelectScriptFile.setText("...");
        cmdSelectScriptFile.setToolTipText("Select weather file(s) (.epw) or list file (.lst). Hold 'Ctrl' key for multiple selections.");
        cmdSelectScriptFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectScriptFileActionPerformed(evt);
            }
        });

        txtMoreArguments.setPreferredSize(new java.awt.Dimension(6, 23));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkMoreArguments, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtMoreArguments, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        chkMoreArguments.setSelected(true);
        chkMoreArguments.setText("More arguments (separate by ';' ):");
        chkMoreArguments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMoreArgumentsActionPerformed(evt);
            }
        });

        txtScriptFileName.setText("select a script file ...");

        chkPassJobList.setText("Pass the list of jobs (job ids separated by ';' ):");
        chkPassJobList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPassJobListActionPerformed(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkPassJobList, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtJobList, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        chkPassOutputFile.setSelected(true);
        chkPassOutputFile.setText("Output csv table file name: ");
        chkPassOutputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPassOutputFileActionPerformed(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkPassOutputFile, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtOutputFile, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel2.setBackground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("Arguments:");
        jLabel2.setOpaque(true);

        cmdSyncJobList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/view-refresh.png"))); // NOI18N
        cmdSyncJobList.setToolTipText("Sync job list with project");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkPassJobList, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cmdSyncJobList, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cmdSyncJobList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSyncJobListActionPerformed(evt);
            }
        });

        cmdViewOutputFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/folder_explore.png"))); // NOI18N
        cmdViewOutputFile.setToolTipText("View output file in folder");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkPassOutputFile, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cmdViewOutputFile, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cmdViewOutputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdViewOutputFileActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("Example command-line:");
        jLabel4.setOpaque(true);

        txaCmdLn.setColumns(20);
        txaCmdLn.setLineWrap(true);
        txaCmdLn.setRows(4);
        jScrollPane1.setViewportView(txaCmdLn);

        chkPassProjectBase.setSelected(true);
        chkPassProjectBase.setText("Pass Project base: ");

        txtProjectBase.setText("./");

        cmdSelectProjectBase.setText("...");
        cmdSelectProjectBase.setToolTipText("Select the the working directory");
        cmdSelectProjectBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectProjectBaseActionPerformed(evt);
            }
        });

        cboLang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setBackground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("Script Language: ");
        jLabel3.setOpaque(true);

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
                    .addComponent(chkMoreArguments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(chkPassProjectBase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(chkPassWorkDir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtProjectBase)
                                    .addComponent(txtWorkDir))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmdSelectWorkDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmdSelectProjectBase, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(txtJobList)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmdSyncJobList, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(chkPassOutputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtOutputFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmdViewOutputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkPassJobList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(txtScriptFileName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSelectScriptFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEditScript, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPane1))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(cmdRunScript, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdOpenConsole))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(txtMoreArguments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(cboLang, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLang, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmdEditScript, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdSelectScriptFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtScriptFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPassProjectBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdSelectProjectBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtProjectBase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdSelectWorkDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkPassWorkDir, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtWorkDir, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkPassJobList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdSyncJobList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtJobList, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtOutputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdViewOutputFile)
                    .addComponent(chkPassOutputFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkMoreArguments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMoreArguments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdOpenConsole)
                    .addComponent(cmdRunScript))
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void chkPassWorkDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPassWorkDirActionPerformed
        txaCmdLn.setText(updateSampleCommandLine());
    }//GEN-LAST:event_chkPassWorkDirActionPerformed

    private void cmdSelectWorkDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectWorkDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File (CurrentWorkDir));
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            CurrentWorkDir = fc.getSelectedFile().getAbsolutePath() + File.separator;
            txtWorkDir.setText(CurrentWorkDir);
        }
    }//GEN-LAST:event_cmdSelectWorkDirActionPerformed

    private void cmdEditScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditScriptActionPerformed
        // Test if the template file is present
        String templfn = txtScriptFileName.getText();
        File ftmpl = new File(templfn);
        String fn = ftmpl.getName();
        if (!ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                    this,
                    "<html><p><center>The weather file " + templfn + " does not exist."
                    + "Do you want to select one?</center></p><p> Select 'NO' to create this file. </p>",
                    "Script file not available",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                this.cmdSelectScriptFileActionPerformed(null);
                templfn = txtScriptFileName.getText();
            }
        }
        int idx = MainGUI.getTpnEditors().indexOfTab(fn);
        if (idx >= 0) {
            MainGUI.getTpnEditors().setSelectedIndex(idx);
        } else {
            EPlusEditorPanel WthrFilePanel = new EPlusEditorPanel(
                    MainGUI.getTpnEditors(),
                    fn,
                    templfn,
                    EPlusEditorPanel.FileType.PYTHON,
                    null);
            int ti = MainGUI.getTpnEditors().getTabCount();
            WthrFilePanel.setTabId(ti);
            MainGUI.getTpnEditors().addTab(fn, WthrFilePanel);
            MainGUI.getTpnEditors().setSelectedIndex(ti);
            MainGUI.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(MainGUI.getTpnEditors(), WthrFilePanel));
            MainGUI.getTpnEditors().setToolTipTextAt(ti, templfn);
        }
//        // Open it in associated application
//        try {
//            Desktop.getDesktop().open(new File(txtScriptFileName.getText()));
//        } catch (Exception ex) {
//            logger.error("Failed to open " + txtScriptFileName.getText() + " with the default editor.");
//            JOptionPane.showMessageDialog(this, "Failed to open " + txtScriptFileName.getText() + " with default editor associated to this type of files.");
//        }
    }//GEN-LAST:event_cmdEditScriptActionPerformed

    private void cmdSelectScriptFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectScriptFileActionPerformed
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.PYTHON));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(txtScriptFileName.getText().trim().length() > 0 ? new File (txtScriptFileName.getText()).getParentFile() : new File("./"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            txtScriptFileName.setText(file.getAbsolutePath());
            Config.setPythonScript(file.getAbsolutePath());
        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFiles(null);
    }//GEN-LAST:event_cmdSelectScriptFileActionPerformed

    private void cmdOpenConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOpenConsoleActionPerformed
        String path = CurrentWorkDir;
        if (JEPlusVersion.OsName.toLowerCase().startsWith("windows")) {
            try {
                Runtime.getRuntime().exec(new String[] { "cmd.exe", "/C", "\"start; cd "+path+"\"" });
            } catch (IOException ex) {
                logger.error("Cannot open command window.", ex);
            }
        }else {
            JOptionPane.showMessageDialog(this, "Open Linux shell terminal is yet to be implemented.");
        }
    }//GEN-LAST:event_cmdOpenConsoleActionPerformed

    private void cmdRunScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRunScriptActionPerformed
        if (this.txtMoreArguments.getText().trim().length() > 0) {
            Config.setPythonArgv(txtMoreArguments.getText());
        }
        // Switch to output tab
        MainGUI.getTpnEditors().setSelectedIndex(0);
        // Set arguments
        String arg0 = chkPassProjectBase.isSelected()? txtProjectBase.getText().trim(): null;
        String arg1 = chkPassWorkDir.isSelected()? txtWorkDir.getText().trim(): null;
        String arg2 = chkPassJobList.isSelected()? txtJobList.getText().trim(): null;
        String arg3 = chkPassOutputFile.isSelected()? txtOutputFile.getText().trim(): null;
        String moreargs = chkMoreArguments.isSelected()? txtMoreArguments.getText().trim(): null;
        // Start running
        ScriptTools.runScript(
                Config.getScripConfigs().get(cboLang.getSelectedItem().toString()), 
                txtScriptFileName.getText().trim(), 
                arg0, 
                arg1, 
                arg2, 
                arg3, 
                moreargs, 
                OutputViewer.getPrintStream()
        );
    }//GEN-LAST:event_cmdRunScriptActionPerformed

    private void cmdSyncJobListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSyncJobListActionPerformed
        
        try {
            // Get finished jobs
            List <EPlusTask> JobQueue = MainGUI.getBatchManager().getAgent().getFinishedJobs();
            // Collect Job List
            StringBuilder buf = new StringBuilder ();
            for (EPlusTask job : JobQueue) {
                buf.append(job.getJobID()).append(";");
            } // done with loading
            this.txtJobList.setText(buf.toString());
        }catch (NullPointerException npe) {
        }
        
    }//GEN-LAST:event_cmdSyncJobListActionPerformed

    private void cmdViewOutputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdViewOutputFileActionPerformed
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            try {
                File output = new File (this.txtWorkDir.getText());
                if (output.exists()) {
                    Desktop.getDesktop().open(output);
                }else {
                    JOptionPane.showMessageDialog(this, "Output folder " + output.getAbsolutePath() + " does not exist.");
                }
            } catch (IOException ex) {
                logger.error ("Failed to open folder " + this.txtWorkDir.getText(), ex);
            }
        }else {
            JOptionPane.showMessageDialog(this, "Open folder is not supported, or the current job record is not valid.", "Operation failed", JOptionPane.CLOSED_OPTION);
        }       
    }//GEN-LAST:event_cmdViewOutputFileActionPerformed

    private void chkPassJobListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPassJobListActionPerformed
        txaCmdLn.setText(updateSampleCommandLine());
    }//GEN-LAST:event_chkPassJobListActionPerformed

    private void chkPassOutputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPassOutputFileActionPerformed
        txaCmdLn.setText(updateSampleCommandLine());
    }//GEN-LAST:event_chkPassOutputFileActionPerformed

    private void chkMoreArgumentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMoreArgumentsActionPerformed
        txaCmdLn.setText(updateSampleCommandLine());
    }//GEN-LAST:event_chkMoreArgumentsActionPerformed

    private void cmdSelectProjectBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectProjectBaseActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File (CurrentWorkDir));
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            CurrentWorkDir = fc.getSelectedFile().getAbsolutePath() + File.separator;
            txtWorkDir.setText(CurrentWorkDir);
        }
    }//GEN-LAST:event_cmdSelectProjectBaseActionPerformed

    private void cmdConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdConfigActionPerformed
        MainGUI.showConfigDialog();
    }//GEN-LAST:event_cmdConfigActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboLang;
    private javax.swing.JCheckBox chkMoreArguments;
    private javax.swing.JCheckBox chkPassJobList;
    private javax.swing.JCheckBox chkPassOutputFile;
    private javax.swing.JCheckBox chkPassProjectBase;
    private javax.swing.JCheckBox chkPassWorkDir;
    private javax.swing.JButton cmdConfig;
    private javax.swing.JButton cmdEditScript;
    private javax.swing.JButton cmdOpenConsole;
    private javax.swing.JButton cmdRunScript;
    private javax.swing.JButton cmdSelectProjectBase;
    private javax.swing.JButton cmdSelectScriptFile;
    private javax.swing.JButton cmdSelectWorkDir;
    private javax.swing.JButton cmdSyncJobList;
    private javax.swing.JButton cmdViewOutputFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea txaCmdLn;
    private javax.swing.JTextField txtJobList;
    private javax.swing.JTextField txtMoreArguments;
    private javax.swing.JTextField txtOutputFile;
    private javax.swing.JTextField txtProjectBase;
    private javax.swing.JTextField txtScriptFileName;
    private javax.swing.JTextField txtWorkDir;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void configChanged(ConfigFileNames config) {
        if (Config != config) {
            Config = (JEPlusConfig)config;
        }
        updateDisplay();
    }
}
