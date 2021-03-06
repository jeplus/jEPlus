/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeplus.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.io.File;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import jeplus.ConfigFileNames;
import jeplus.EPlusConfig;
import jeplus.EPlusWinTools;
import jeplus.JEPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.ScriptConfig;
import jeplus.TRNSYSConfig;
import jeplus.event.IF_ConfigChangedEventHandler;
import jeplus.util.ScriptTools;

/**
 *
 * @author Yi
 */
public class JPanelProgConfiguration extends javax.swing.JPanel implements IF_ConfigChangedEventHandler {

    protected String title = "External Executables Configuration";
    protected final JFileChooser fc = new JFileChooser("./");
    protected JEPlusConfig Config = JEPlusConfig.getDefaultInstance();
    protected JPanel_EPlusSettings EpPanel = new JPanel_EPlusSettings ();
    protected JPanel_ScriptSettings ScriptPanel = new JPanel_ScriptSettings ();

    protected Window HostWindow = null;
    private boolean DLActive = false;
    
    public enum Layout { 
        TALL,
        WIDE
    };
    
    
    /**
     * Creates new form JPanelProgConfiguration
     * @param host
     * @param config
     * @param layout
     */
    public JPanelProgConfiguration(Window host, JEPlusConfig config, Layout layout) {
        initComponents();
        HostWindow = host;
        jplEpPanelHolder.add(EpPanel, BorderLayout.CENTER);
        jplScriptHolder.add(ScriptPanel, BorderLayout.CENTER);
        setConfig (config);
        switch (layout) {
            case TALL:
                this.remove(jplInfo);
                this.add(jplInfo, BorderLayout.SOUTH);
                break;
            case WIDE:
            default:
                // This is the default. No need to do anything
        }
    }

    public String getConfigFile() {
        return JEPlusConfig.getDefaultConfigFile();
    }

    /** 
     * Set an alternative configuration to this panel
     * @param config
     */
    public final void setConfig(JEPlusConfig config) {
        if (Config != config) {
            if (Config != null) {
                Config.removeListener(this);
            }
            Config = config;
        }
        Config.addListener(this);
        initSettings();
        checkSettings();
    }

    public Window getHostWindow() {
        return HostWindow;
    }

    public void setHostWindow(Window HostWindow) {
        this.HostWindow = HostWindow;
    }

    
    /**
     * initialise display from data records
     */
    public final void initSettings () {
//        if (! Config.getEPlusConfigs().isEmpty()) {
//            cboEPlusVersion.setModel(new DefaultComboBoxModel (Config.getEPlusConfigs().values().toArray(new EPlusConfig [0])));
//            cboEPlusVersion.setSelectedIndex(0);
//            txtEPlusBinDir.setText(((EPlusConfig)cboEPlusVersion.getSelectedItem()).getEPlusBinDir());
//        }else {
//            txtEPlusBinDir.setText("Select an EnergyPlus installation ...");
//        }
        DLActive = false;
        EpPanel.setConfig(Config);
        if (! Config.getTRNSYSConfigs().isEmpty()) {
            cboTrnVersion.setModel(new DefaultComboBoxModel (Config.getTRNSYSConfigs().keySet().toArray(new String [0])));
            cboTrnVersion.setSelectedIndex(0);
            cboTrnVersionActionPerformed(null);
            Config.getCurrentTRNSYS().removeAllListeners();
            Config.getCurrentTRNSYS().addListener(this);
        }else {
            txtTrnsysBinDir.setText("Select a TRNSYS installation ...");
        }
        ScriptPanel.setConfig(Config);
        this.txtVerConvDir.setText(Config.getEPlusVerConvDir() == null ? "Select Version Converter dir ..." : Config.getEPlusVerConvDir());
        DLActive = true;
    }

    /**
     * check validity of directory and command/file names
     */
    public final void checkSettings () {
        SwingWorker worker = new SwingWorker<Boolean, Void>() {
            @Override
            public Boolean doInBackground() {
                boolean errors = false;
                StringBuilder buf = new StringBuilder ("<html>");

                // EnergyPlus binary
                buf.append("<p><em>Found EnergyPlus installations:</em></p>");
                for (EPlusConfig cfg : Config.getEPlusConfigs().values()) {
                    buf.append("<p>V").append(cfg.toString());
                    List<String> unfound = cfg.validate();
                    if (cfg.isValid()) {
                        buf.append(" => OK</p>");
                    }else {
                        buf.append(" contains errors: </p>");
                        for (String item : unfound) {
                            buf.append("<p> - ").append(item).append(" is not found </p>");
                        }
                    }
                }
                buf.append("<p></p>");

                // TRNSYS binary
                TRNSYSConfig trn = Config.getTRNSYSConfigs().get("TRNSYS");
                if (trn != null) {
                    File dir = new File (trn.getTRNSYSBinDir());
                    buf.append("<p><em>Found TRNSYS installation:</em></p>");
                    if (! (dir.exists() && dir.isDirectory())) {
                        txtTrnsysBinDir.setForeground(Color.red);
                        buf.append("<p>TRNSYS folder ").append(dir.getAbsolutePath()).append(" does not exist!</p>");
                    } else {
                        txtTrnsysBinDir.setForeground(Color.black);
                    }
                    File f = new File(trn.getTRNSYSEXE());
                    if (! f.exists()) {
                        txtTrnsysEXE.setForeground(Color.red);
                        buf.append("<p>TRNSYS Executable ").append(f.getAbsolutePath()).append(" is missing!</p>");
                    } else {
                        txtTrnsysEXE.setForeground(Color.black);
                        buf.append("<p>TRNSYS => ").append(trn.getTRNSYSEXE()).append("</p>");
                    }
                    buf.append("<p></p>");
                }

                // Scripts
                buf.append("<p><em>Available Script Interpreters:</em></p>");
                for (String name : Config.getScripConfigs().keySet()) {
                    ScriptConfig cfg = Config.getScripConfigs().get(name);
                    buf.append("<p>").append(name).append(" => ");
                    // Get script version
                    String ver = ScriptTools.getVersion(cfg);
                    if (ver.startsWith("Error:")) {
                        buf.append(ver);
                    }else {
                        buf.append("Found ").append(ver);
                    }
                    buf.append("</p>");
                }
                buf.append("<p></p>");

                // E+ version converter folder
                buf.append("<p><em>E+ Version Converter:</em></p>");
                if (Config.getEPlusVerConvDir() != null) {
                    File dir = new File (Config.getEPlusVerConvDir());
                    if (! (dir.exists() && dir.isDirectory())) {
                        txtVerConvDir.setForeground(Color.red);
                        buf.append("<p>E+ Version Converter dir ").append(dir.getAbsolutePath()).append(" does not exist!</p>");
                    }else {
                        List<String> versions = EPlusWinTools.getInstalledTransitionVersions(dir.getAbsolutePath());
                        buf.append("<p>");
                        if (versions.size() >= 2) {
                            txtVerConvDir.setForeground(Color.black);
                            buf.append("Converters found => ");
                            int cnt = 0;
                            for (String item : versions) {
                                if (cnt % 4 == 0) {
                                    buf.append("<br>");
                                }
                                buf.append(item).append(", ");
                                cnt ++;
                            }
                        }else {
                            txtVerConvDir.setForeground(Color.red);
                            buf.append("Failed to find valid IDDs for converters");
                        }
                        buf.append("</p>");
                    }
                }else {
                    buf.append("<p>Not specified</p>");
                }
                buf.append("<p></p>");

                buf.append("<p></p></html>");
                lblInformation.setText(buf.toString());
                
                return errors;
            }

            @Override
            public void done() {
            }
        };
        
        worker.execute();
    }
    
    private EPlusConfig getSelectedEPlusConfig () {
        return (EPlusConfig)cboEPlusVersion.getSelectedItem();
    }

    private TRNSYSConfig getSelectedTrnsysConfig () {
        return Config.getTRNSYSConfigs().get(cboTrnVersion.getSelectedItem().toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdSelectEPlusDir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cmdEnergyPlusDetails = new javax.swing.JButton();
        txtEPlusBinDir = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cboEPlusVersion = new javax.swing.JComboBox();
        cmdEnergyPlusDetails1 = new javax.swing.JButton();
        jplConfig = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        cmdSelectVerConvDir = new javax.swing.JButton();
        txtVerConvDir = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtTrnsysBinDir = new javax.swing.JTextField();
        cmdSelectTrnsysDir = new javax.swing.JButton();
        cmdSelectTRNexe = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtTrnsysEXE = new javax.swing.JTextField();
        cboTrnVersion = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jplEpPanelHolder = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        cmdSave = new javax.swing.JButton();
        jplScriptHolder = new javax.swing.JPanel();
        jplInfo = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lblInformation = new javax.swing.JLabel();

        cmdSelectEPlusDir.setText("...");
        cmdSelectEPlusDir.setToolTipText("Select the folder where EnergyPlus.exe and Energy+.idd are located");
        cmdSelectEPlusDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectEPlusDirActionPerformed(evt);
            }
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Energy+ Diretory:");

        cmdEnergyPlusDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/tool.png"))); // NOI18N
        cmdEnergyPlusDetails.setToolTipText("Check and specify individual E+ tools");
        cmdEnergyPlusDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEnergyPlusDetailsActionPerformed(evt);
            }
        });

        txtEPlusBinDir.setText("C:/EnergyPlusV2-2-0/");
        txtEPlusBinDir.setToolTipText("This is the directory where 'Energy+.idd' is located");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Select version:");

        cboEPlusVersion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "7.2", "8.1", "8.2" }));
        cboEPlusVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboEPlusVersionActionPerformed(evt);
            }
        });

        cmdEnergyPlusDetails1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/cross.png"))); // NOI18N
        cmdEnergyPlusDetails1.setToolTipText("Check and specify individual E+ tools");
        cmdEnergyPlusDetails1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEnergyPlusDetails1ActionPerformed(evt);
            }
        });

        setMinimumSize(new java.awt.Dimension(550, 450));
        setPreferredSize(new java.awt.Dimension(550, 500));
        setLayout(new java.awt.BorderLayout());

        jplConfig.setMinimumSize(new java.awt.Dimension(400, 520));
        jplConfig.setPreferredSize(new java.awt.Dimension(400, 500));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("E+ Version Converter"));

        cmdSelectVerConvDir.setText("...");
        cmdSelectVerConvDir.setToolTipText("Select the root working directory");
        cmdSelectVerConvDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectVerConvDirActionPerformed(evt);
            }
        });

        txtVerConvDir.setText("Select E+ version converter ...");
        txtVerConvDir.setToolTipText("Select the location where the version converter is installed");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("Version Converter Dir: ");
        jLabel8.setMaximumSize(new java.awt.Dimension(116, 14));
        jLabel8.setMinimumSize(new java.awt.Dimension(116, 14));
        jLabel8.setPreferredSize(new java.awt.Dimension(116, 14));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVerConvDir, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdSelectVerConvDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVerConvDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectVerConvDir)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("TRNSYS"));

        txtTrnsysBinDir.setText("C:/Program Files/Trnsys16_1/");
        txtTrnsysBinDir.setToolTipText("This is the directory where the folders 'Exe and UserLib' are located");

        cmdSelectTrnsysDir.setText("...");
        cmdSelectTrnsysDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectTrnsysDirActionPerformed(evt);
            }
        });

        cmdSelectTRNexe.setText("...");
        cmdSelectTRNexe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectTRNexeActionPerformed(evt);
            }
        });

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("TRNSYS Executable:");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("TRNSYS Diretory:");

        txtTrnsysEXE.setText("C:/Program Files/Trnsys16_1/Exe/TRNExe.exe");
        txtTrnsysEXE.setToolTipText("The command may vary within different projects of TRNSYS. Edit this field if necessary. If the executable is located in a different location, please specify the relative diretory to the TRNSYS binary directory above.");

        cboTrnVersion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16", "17", "18" }));
        cboTrnVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrnVersionActionPerformed(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Select version:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTrnsysBinDir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(txtTrnsysEXE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cmdSelectTRNexe, 0, 1, Short.MAX_VALUE)
                            .addComponent(cmdSelectTrnsysDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cboTrnVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTrnVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtTrnsysBinDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectTrnsysDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTrnsysEXE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cmdSelectTRNexe))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jplEpPanelHolder.setBorder(javax.swing.BorderFactory.createTitledBorder("EnergyPlus"));
        jplEpPanelHolder.setLayout(new java.awt.BorderLayout());

        cmdSave.setText("Save Configuration");
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });
        jPanel3.add(cmdSave);

        jplScriptHolder.setBorder(javax.swing.BorderFactory.createTitledBorder("Scripts"));
        jplScriptHolder.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jplConfigLayout = new javax.swing.GroupLayout(jplConfig);
        jplConfig.setLayout(jplConfigLayout);
        jplConfigLayout.setHorizontalGroup(
            jplConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplConfigLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jplConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jplEpPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .addComponent(jplScriptHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jplConfigLayout.setVerticalGroup(
            jplConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplConfigLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jplEpPanelHolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jplScriptHolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(jplConfig, java.awt.BorderLayout.CENTER);

        jplInfo.setMinimumSize(new java.awt.Dimension(220, 150));
        jplInfo.setName(""); // NOI18N
        jplInfo.setPreferredSize(new java.awt.Dimension(300, 300));

        lblInformation.setBackground(new java.awt.Color(204, 204, 204));
        lblInformation.setText("jLabel2");
        lblInformation.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jScrollPane1.setViewportView(lblInformation);

        javax.swing.GroupLayout jplInfoLayout = new javax.swing.GroupLayout(jplInfo);
        jplInfo.setLayout(jplInfoLayout);
        jplInfoLayout.setHorizontalGroup(
            jplInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );
        jplInfoLayout.setVerticalGroup(
            jplInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jplInfo, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectEPlusDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectEPlusDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(getSelectedEPlusConfig().getEPlusBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            getSelectedEPlusConfig().setNewEPlusBinDir(bindir);
//            Config.setEPlusEPMacroEXE(bindir + EPlusConfig.getDefEPlusEPMacro());
//            Config.setEPlusExpandObjectsEXE(bindir + EPlusConfig.getDefEPlusExpandObjects());
//            Config.setEPlusEXE(bindir + EPlusConfig.getDefEPlusEXEC());
//            Config.setEPlusReadVarsEXE(bindir + EPlusConfig.getDefEPlusReadVars());
//            initSettings();
//            checkSettings();
            Config.saveAsJSON(new File(getConfigFile()));
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }//GEN-LAST:event_cmdSelectEPlusDirActionPerformed

    private void cmdEnergyPlusDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEnergyPlusDetailsActionPerformed
        JDialog dialog = new JDialog (JEPlusFrameMain.getCurrentMainGUI(), "Set EnergyPlus binaries", true);
        final EPlusConfig config = getSelectedEPlusConfig();
        final JPanel_EPlusSettingsDetailed detPanel = new JPanel_EPlusSettingsDetailed (dialog, config);
        config.addListener(detPanel);
        dialog.getContentPane().add(detPanel);
        // Add dialog closing listener
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                config.removeListener(detPanel);
            }
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                config.removeListener(detPanel);
            }
        });
        dialog.setSize(500, 260);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_cmdEnergyPlusDetailsActionPerformed

    private void cmdSelectTrnsysDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectTrnsysDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            getSelectedTrnsysConfig().setNewTRNSYSBinDir(bindir);
//            Config.setTRNSYSEXE(new File (bindir + TRNSYSConfig.getDefTRNSYSEXEC()).getAbsolutePath());
//            initSettings();
//            checkSettings();
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }//GEN-LAST:event_cmdSelectTrnsysDirActionPerformed

    private void cmdSelectTRNexeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectTRNexeActionPerformed
        // Select a file to open
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(new File(getSelectedTrnsysConfig().getTRNSYSBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String name = file.getPath();
            txtTrnsysEXE.setText(name);
            txtTrnsysEXE.setForeground(Color.black);
            getSelectedTrnsysConfig().setTRNSYSEXE(name);
        }
        fc.resetChoosableFileFilters();
        checkSettings ();
    }//GEN-LAST:event_cmdSelectTRNexeActionPerformed

    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
        Config.saveAsJSON(new File(getConfigFile()));
        if (HostWindow != null) {
            HostWindow.dispose();
        }
    }//GEN-LAST:event_cmdSaveActionPerformed

    private void cmdSelectVerConvDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectVerConvDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            Config.setEPlusVerConvDir(bindir);
            txtVerConvDir.setText(bindir);
            checkSettings();
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

    }//GEN-LAST:event_cmdSelectVerConvDirActionPerformed

    private void cboEPlusVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboEPlusVersionActionPerformed
        this.txtEPlusBinDir.setText(((EPlusConfig)cboEPlusVersion.getSelectedItem()).getEPlusBinDir());
    }//GEN-LAST:event_cboEPlusVersionActionPerformed

    private void cboTrnVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrnVersionActionPerformed
        Config.setCurrentTRNSYS(Config.getTRNSYSConfigs().get(cboTrnVersion.getSelectedItem().toString()));
        txtTrnsysBinDir.setText(Config.getCurrentTRNSYS().getTRNSYSBinDir());
        txtTrnsysEXE.setText(Config.getCurrentTRNSYS().getTRNSYSEXE());
        if (DLActive) {
            Config.fireConfigChangedEvent();
        }
    }//GEN-LAST:event_cboTrnVersionActionPerformed

    private void cmdEnergyPlusDetails1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEnergyPlusDetails1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmdEnergyPlusDetails1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboEPlusVersion;
    private javax.swing.JComboBox cboTrnVersion;
    private javax.swing.JButton cmdEnergyPlusDetails;
    private javax.swing.JButton cmdEnergyPlusDetails1;
    private javax.swing.JButton cmdSave;
    private javax.swing.JButton cmdSelectEPlusDir;
    private javax.swing.JButton cmdSelectTRNexe;
    private javax.swing.JButton cmdSelectTrnsysDir;
    private javax.swing.JButton cmdSelectVerConvDir;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jplConfig;
    private javax.swing.JPanel jplEpPanelHolder;
    private javax.swing.JPanel jplInfo;
    private javax.swing.JPanel jplScriptHolder;
    private javax.swing.JLabel lblInformation;
    private javax.swing.JTextField txtEPlusBinDir;
    private javax.swing.JTextField txtTrnsysBinDir;
    private javax.swing.JTextField txtTrnsysEXE;
    private javax.swing.JTextField txtVerConvDir;
    // End of variables declaration//GEN-END:variables

    @Override
    public void configChanged(ConfigFileNames config) {
        initSettings();
        checkSettings();
    }
}
