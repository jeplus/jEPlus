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
import jeplus.ConfigFileNames;
import jeplus.EPlusConfig;
import jeplus.EPlusWinTools;
import jeplus.JEPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.TRNSYSConfig;
import jeplus.event.IF_ConfigChangedEventHandler;
import jeplus.util.PythonTools;

/**
 *
 * @author Yi
 */
public class JPanelProgConfiguration extends javax.swing.JPanel implements IF_ConfigChangedEventHandler {

    protected String title = "External Executables Configuration";
    protected final JFileChooser fc = new JFileChooser("./");
    protected JEPlusConfig Config = JEPlusConfig.getDefaultInstance();
    protected JPanel_EPlusSettings EpPanel = new JPanel_EPlusSettings ();

    protected Window HostWindow = null;
    
    /**
     * Creates new form JPanelProgConfiguration
     * @param host
     * @param config
     */
    public JPanelProgConfiguration(Window host, JEPlusConfig config) {
        initComponents();
        HostWindow = host;
        jplEpPanelHolder.add(EpPanel, BorderLayout.CENTER);
        setConfig (config);
    }

    public String getConfigFile() {
        return JEPlusConfig.DefaultConfigFile;
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
        EpPanel.setConfig(Config);
        if (! Config.getTRNSYSConfigs().isEmpty()) {
            cboTrnVersion.setModel(new DefaultComboBoxModel (Config.getTRNSYSConfigs().keySet().toArray(new String [0])));
            txtTrnsysBinDir.setText(Config.getTRNSYSConfigs().get(cboTrnVersion.getSelectedItem().toString()).getTRNSYSBinDir());
            txtTrnsysEXE.setText(Config.getTRNSYSConfigs().get(cboTrnVersion.getSelectedItem().toString()).getTRNSYSEXEC());
        }else {
            txtTrnsysBinDir.setText("Select a TRNSYS installation ...");
        }
        this.txtPython2Exe.setText(Config.getPython2EXE() == null ? "Select Python 2 exe..." : Config.getPython2EXE());
        this.txtPython3Exe.setText(Config.getPython3EXE() == null ? "Select Python 3 exe..." : Config.getPython3EXE());
        this.txtVerConvDir.setText(Config.getEPlusVerConvDir() == null ? "Select Version Converter dir ..." : Config.getEPlusVerConvDir());
        this.txtJESSClientDir.setText(Config.getJESSClientDir() == null ? "Select JESS Client dir ..." : Config.getJESSClientDir());
        this.txtJEPlusEADir.setText(Config.getJEPlusEADir() == null ? "Select jEPlus+EA dir ..." : Config.getJEPlusEADir());
    }

    /**
     * check validity of directory and command/file names
     */
    public final void checkSettings () {
        boolean errors = false;
        StringBuilder buf = new StringBuilder ("<html>");
        
        // EnergyPlus binary
        buf.append("<p><em>EnergyPlus:</em></p>");
        for (EPlusConfig cfg : Config.getEPlusConfigs().values()) {
            buf.append("<p>V").append(cfg.toString());
            List<String> unfound = cfg.validate();
            if (cfg.isValid()) {
                buf.append(" is available</p>");
            }else {
                buf.append(" contains errors: </p>");
                for (String item : unfound) {
                    buf.append("<p> - ").append(item).append(" is not found </p>");
                }
            }
        }
        buf.append("<p></p>");
        
        // TRNSYS binary
        File dir = new File (txtTrnsysBinDir.getText());
        buf.append("<p><em>TRNSYS:</em></p>");
        if (! (dir.exists() && dir.isDirectory())) {
            txtTrnsysBinDir.setForeground(Color.red);
            buf.append("<p>TRNSYS folder ").append(dir.getAbsolutePath()).append(" does not exist!</p>");
        } else {
            txtTrnsysBinDir.setForeground(Color.black);
        }
        File f = new File(txtTrnsysEXE.getText());
        if (! f.exists()) {
            txtTrnsysEXE.setForeground(Color.red);
            buf.append("<p>TRNSYS Executable ").append(f.getAbsolutePath()).append(" is missing!</p>");
        } else {
            txtTrnsysEXE.setForeground(Color.black);
            buf.append("<p>Found TRNSYS ").append("</p>");
        }
        buf.append("<p></p>");
        
        // Python 2 executable 
        buf.append("<p><em>Python2:</em></p>");
        f = new File(txtPython2Exe.getText());
        if (! f.exists()) {
            txtPython2Exe.setForeground(Color.red);
            buf.append("<p>Python 2 Executable ").append(f.getAbsolutePath()).append(" is missing!</p>");
        } else {
            // Get python version with "python -V"
            String ver = PythonTools.getPythonVersion(Config, "python2");
            if (ver.startsWith("Error:")) {
                txtPython2Exe.setForeground(Color.red);
                buf.append("<p>").append(ver).append("</p>");
            }else {
                txtPython2Exe.setForeground(Color.black);
                buf.append("<p>Found ").append(ver).append("</p>");
            }
        }
        buf.append("<p></p>");

        // Python 3 executable 
        buf.append("<p><em>Python3:</em></p>");
        f = new File(txtPython3Exe.getText());
        if (! f.exists()) {
            txtPython3Exe.setForeground(Color.red);
            buf.append("<p>Python 3 Executable ").append(f.getAbsolutePath()).append(" is missing!</p>");
        } else {
            // Get python version with "python -V"
            String ver = PythonTools.getPythonVersion(Config, "python3");
            if (ver.startsWith("Error:")) {
                txtPython3Exe.setForeground(Color.red);
                buf.append("<p>").append(ver).append("</p>");
            }else {
                txtPython3Exe.setForeground(Color.black);
                buf.append("<p>Found ").append(ver).append("</p>");
            }
        }
        buf.append("<p></p>");

        // E+ version converter folder
        dir = new File (txtVerConvDir.getText());
        buf.append("<p><em>E+ Version Converter:</em></p>");
        if (! (dir.exists() && dir.isDirectory())) {
            txtVerConvDir.setForeground(Color.red);
            buf.append("<p>E+ Version Converter dir ").append(dir.getAbsolutePath()).append(" does not exist!</p>");
        }else {
            List<String> versions = EPlusWinTools.getInstalledTransitionVersions(dir.getAbsolutePath());
            buf.append("<p>");
            if (versions.size() >= 2) {
                txtVerConvDir.setForeground(Color.black);
                buf.append("Converters found for versions ");
                for (String item : versions) {
                    buf.append("<br>").append(item);
                }
            }else {
                txtVerConvDir.setForeground(Color.red);
                buf.append("Failed to find valid IDDs for converters");
            }
            buf.append("</p>");
        }
        buf.append("<p></p>");
        
        // JESS Client folder
        dir = new File (txtJESSClientDir.getText());
        buf.append("<p><em>JESS Client:</em></p>");
        if (! (dir.exists() && dir.isDirectory())) {
            txtJESSClientDir.setForeground(Color.red);
            buf.append("<p>Specified JESS client dir ").append(dir.getAbsolutePath()).append(" does not exist!</p>");
        }else {
            String version = EPlusWinTools.runJavaProgram(dir.getAbsolutePath(), "jess_client_v3.jar", new String [] {"-version"});
            buf.append("<p>");
            if (version.startsWith("Error")) {
                txtJESSClientDir.setForeground(Color.red);
                buf.append("Failed to check client version: " + version);
            }else {
                txtJESSClientDir.setForeground(Color.black);
                buf.append("Found JESS Client ");
                buf.append(version);
            }
            buf.append("</p>");
        }
        buf.append("<p></p>");

        // jEPlus+EA folder
        dir = new File (txtJEPlusEADir.getText());
        buf.append("<p><em>jEPlus+EA:</em></p>");
        if (! (dir.exists() && dir.isDirectory())) {
            txtJEPlusEADir.setForeground(Color.red);
            buf.append("<p>Specified jEPlus+EA dir ").append(dir.getAbsolutePath()).append(" does not exist!</p>");
        }else {
            buf.append("<p>");
            if (new File (dir, "jEPlus+EA.jar").exists()){
                txtJEPlusEADir.setForeground(Color.black);
                buf.append("Found jEPlus+EA.jar ");
            }else {
                txtJEPlusEADir.setForeground(Color.red);
                buf.append("Cannot find jEPlus+EA.jar");
            }
            buf.append("</p>");
        }

        buf.append("<p></p></html>");
        lblInformation.setText(buf.toString());
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
        jplEpPanelHolder = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtTrnsysBinDir = new javax.swing.JTextField();
        cmdSelectTrnsysDir = new javax.swing.JButton();
        cmdSelectTRNexe = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtTrnsysEXE = new javax.swing.JTextField();
        cboTrnVersion = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtPython3Exe = new javax.swing.JTextField();
        cmdSelectPython3Exe = new javax.swing.JButton();
        cmdSelectPython2Exe = new javax.swing.JButton();
        txtPython2Exe = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cmdSave = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        txtJEPlusEADir = new javax.swing.JTextField();
        cmdSelectJEPlusEA = new javax.swing.JButton();
        cmdSelectClient = new javax.swing.JButton();
        txtJESSClientDir = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lblInformation = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        cmdSelectVerConvDir = new javax.swing.JButton();
        txtVerConvDir = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();

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

        jplEpPanelHolder.setBorder(javax.swing.BorderFactory.createTitledBorder("EnergyPlus"));
        jplEpPanelHolder.setLayout(new java.awt.BorderLayout());

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
                            .addComponent(txtTrnsysBinDir, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTrnsysEXE, javax.swing.GroupLayout.Alignment.TRAILING))
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

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Python"));

        txtPython3Exe.setText("Select Python3 executable...");

        cmdSelectPython3Exe.setText("...");
        cmdSelectPython3Exe.setToolTipText("Select the root working directory");
        cmdSelectPython3Exe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectPython3ExeActionPerformed(evt);
            }
        });

        cmdSelectPython2Exe.setText("...");
        cmdSelectPython2Exe.setToolTipText("Select the root working directory");
        cmdSelectPython2Exe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectPython2ExeActionPerformed(evt);
            }
        });

        txtPython2Exe.setText("Select Python2 executable...");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Python 2 Executable: ");
        jLabel1.setMaximumSize(new java.awt.Dimension(116, 14));
        jLabel1.setMinimumSize(new java.awt.Dimension(116, 14));
        jLabel1.setPreferredSize(new java.awt.Dimension(116, 14));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Python 3 Executable:");
        jLabel3.setMaximumSize(new java.awt.Dimension(116, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(116, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(116, 14));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPython2Exe)
                    .addComponent(txtPython3Exe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdSelectPython2Exe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectPython3Exe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPython2Exe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectPython2Exe)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPython3Exe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectPython3Exe)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cmdSave.setText("Save Configuration and Close");
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });
        jPanel3.add(cmdSave);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("JESS Client & jEPlus+EA"));

        txtJEPlusEADir.setText("Select jEPlus+EA's location ...");
        txtJEPlusEADir.setToolTipText("Select the location where jEPlus+EA is installed");

        cmdSelectJEPlusEA.setText("...");
        cmdSelectJEPlusEA.setToolTipText("Select the root working directory");
        cmdSelectJEPlusEA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectJEPlusEAActionPerformed(evt);
            }
        });

        cmdSelectClient.setText("...");
        cmdSelectClient.setToolTipText("Select the root working directory");
        cmdSelectClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectClientActionPerformed(evt);
            }
        });

        txtJESSClientDir.setText("Select the JESS client's location ...");
        txtJESSClientDir.setToolTipText("Select the location where the JESS Client is installed");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("JESS Client Directory: ");
        jLabel2.setMaximumSize(new java.awt.Dimension(116, 14));
        jLabel2.setMinimumSize(new java.awt.Dimension(116, 14));
        jLabel2.setPreferredSize(new java.awt.Dimension(116, 14));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("jEPlus+EA Directory: ");
        jLabel4.setMaximumSize(new java.awt.Dimension(116, 14));
        jLabel4.setMinimumSize(new java.awt.Dimension(116, 14));
        jLabel4.setPreferredSize(new java.awt.Dimension(116, 14));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtJESSClientDir)
                    .addComponent(txtJEPlusEADir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdSelectClient, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectJEPlusEA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtJESSClientDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectClient)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtJEPlusEADir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectJEPlusEA)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblInformation.setBackground(new java.awt.Color(204, 204, 204));
        lblInformation.setText("jLabel2");
        lblInformation.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jScrollPane1.setViewportView(lblInformation);

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
                .addComponent(txtVerConvDir)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jplEpPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jplEpPanelHolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 94, Short.MAX_VALUE)))
                .addContainerGap())
        );
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
            getSelectedEPlusConfig().setEPlusBinDir(bindir);
//            Config.setEPlusEPMacro(bindir + EPlusConfig.getDefEPlusEPMacro());
//            Config.setEPlusExpandObjects(bindir + EPlusConfig.getDefEPlusExpandObjects());
//            Config.setEPlusEXEC(bindir + EPlusConfig.getDefEPlusEXEC());
//            Config.setEPlusReadVars(bindir + EPlusConfig.getDefEPlusReadVars());
//            initSettings();
//            checkSettings();
            Config.saveAsJSON(new File(JEPlusConfig.DefaultConfigFile));
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
            getSelectedTrnsysConfig().setTRNSYSBinDir(bindir);
//            Config.setTRNSYSEXEC(new File (bindir + TRNSYSConfig.getDefTRNSYSEXEC()).getAbsolutePath());
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
            getSelectedTrnsysConfig().setTRNSYSEXEC(name);
        }
        fc.resetChoosableFileFilters();
        checkSettings ();
    }//GEN-LAST:event_cmdSelectTRNexeActionPerformed

    private void cmdSelectPython3ExeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectPython3ExeActionPerformed
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.ALL));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(new File("./"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            txtPython3Exe.setText(file.getAbsolutePath());
            Config.setPython3EXE(file.getAbsolutePath());
        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFiles(null);
        checkSettings ();
    }//GEN-LAST:event_cmdSelectPython3ExeActionPerformed

    private void cmdSelectPython2ExeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectPython2ExeActionPerformed
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.ALL));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(new File("./"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            txtPython2Exe.setText(file.getAbsolutePath());
            Config.setPython2EXE(file.getAbsolutePath());
        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFiles(null);
        checkSettings ();
    }//GEN-LAST:event_cmdSelectPython2ExeActionPerformed

    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
        Config.saveAsJSON(new File(JEPlusConfig.DefaultConfigFile));
        if (HostWindow != null) {
            HostWindow.dispose();
        }
    }//GEN-LAST:event_cmdSaveActionPerformed

    private void cmdSelectJEPlusEAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectJEPlusEAActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            Config.setJEPlusEADir(bindir);
            txtJEPlusEADir.setText(bindir);
            checkSettings();
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

    }//GEN-LAST:event_cmdSelectJEPlusEAActionPerformed

    private void cmdSelectClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectClientActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            Config.setJESSClientDir(bindir);
            txtJESSClientDir.setText(bindir);
            checkSettings();
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

    }//GEN-LAST:event_cmdSelectClientActionPerformed

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
        txtTrnsysBinDir.setText(Config.getTRNSYSConfigs().get(cboTrnVersion.getSelectedItem().toString()).getTRNSYSBinDir());
        txtTrnsysEXE.setText(Config.getTRNSYSConfigs().get(cboTrnVersion.getSelectedItem().toString()).getTRNSYSEXEC());
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
    private javax.swing.JButton cmdSelectClient;
    private javax.swing.JButton cmdSelectEPlusDir;
    private javax.swing.JButton cmdSelectJEPlusEA;
    private javax.swing.JButton cmdSelectPython2Exe;
    private javax.swing.JButton cmdSelectPython3Exe;
    private javax.swing.JButton cmdSelectTRNexe;
    private javax.swing.JButton cmdSelectTrnsysDir;
    private javax.swing.JButton cmdSelectVerConvDir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jplEpPanelHolder;
    private javax.swing.JLabel lblInformation;
    private javax.swing.JTextField txtEPlusBinDir;
    private javax.swing.JTextField txtJEPlusEADir;
    private javax.swing.JTextField txtJESSClientDir;
    private javax.swing.JTextField txtPython2Exe;
    private javax.swing.JTextField txtPython3Exe;
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
