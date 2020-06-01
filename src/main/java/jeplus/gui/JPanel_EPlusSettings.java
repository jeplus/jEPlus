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
package jeplus.gui; //

import java.awt.Color;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import jeplus.ConfigFileNames;
import jeplus.EPlusConfig;
import jeplus.JEPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.event.IF_ConfigChangedEventHandler;
import org.slf4j.LoggerFactory;

/**
 * JPanel_EPlusSettings.java - This is the view of EPlusConfig record
 * @author zyyz
 * @version 0.6
 * @since 0.5b
 */
public class JPanel_EPlusSettings extends javax.swing.JPanel implements TitledJPanel, IF_ConfigChangedEventHandler {

    /** Logger */
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(JPanel_EPlusSettings.class);

    protected String title = "E+ Executables";
    protected final JFileChooser fc = new JFileChooser("./");
    protected JEPlusConfig Config;
    
    /** 
     * Creates new form JPanel_EPlusSettings
     */
    public JPanel_EPlusSettings() {
        initComponents();
    }

    /** 
     * Creates new form JPanel_EPlusSettings
     * @param config 
     */
    public JPanel_EPlusSettings(JEPlusConfig config) {
        initComponents();
        setConfig(config);
    }

    /**
     * Get title of this panel
     * @return Title of this panel instance
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * Set title to this panel
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
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
            Config.addListener(this);
        }
        initSettings();
        checkSettings();
    }

    /**
     * initialise display from data records
     */
    public final void initSettings () {
        cboEPlusVersions.setModel(new DefaultComboBoxModel (Config.getEPlusConfigs().values().toArray(new EPlusConfig [0])));
        if (cboEPlusVersions.getModel().getSize() > 0) {
            cboEPlusVersions.setSelectedIndex(0);
            txtBinDir.setText(getSelectedConfig().getEPlusBinDir());
            if (! getSelectedConfig().isValid()) {
                txtBinDir.setForeground(Color.red);
                this.lblInformation.setText("This configuration of E+ contains errors!");
            }else {
                txtBinDir.setForeground(Color.black);
                this.lblInformation.setText("");
            }
        }
    }
    
    private EPlusConfig getSelectedConfig () {
        if (cboEPlusVersions.getModel().getSize() > 0) {
            return (EPlusConfig)cboEPlusVersions.getSelectedItem();
        }
        return null;
    }
    
    public void setSelectedConfig (EPlusConfig cfg) {
        this.cboEPlusVersions.setSelectedItem(cfg);
    }

    /**
     * check validity of directory and command/file names
     * @return 
     */
    public final boolean checkSettings () {
        boolean error = false;
        EPlusConfig config = getSelectedConfig();
        if (config != null) {
            List<String> missing_parts = config.validate();
            if (config.isValid()) {
                txtBinDir.setForeground(Color.black);
                lblInformation.setText("<html>EnergyPlus V" + config.toString() + " is available</html>");
            }else {
                txtBinDir.setForeground(Color.red);
                lblInformation.setText("<html>Configuration of EnergyPlus V" + config.toString() + " contains errors!</html>");
                error = true;
            }
        }else {
            lblInformation.setText("<html>Select an EnergyPlus binary folder to add new configurations.</html>");
            error = true;
        }
        return !error;
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdSelectEPlusDir = new javax.swing.JButton();
        txtBinDir = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmdEnergyPlusDetails = new javax.swing.JButton();
        lblInformation = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cboEPlusVersions = new javax.swing.JComboBox();
        cmdDelete = new javax.swing.JButton();
        cmdScan = new javax.swing.JButton();

        cmdSelectEPlusDir.setText("...");
        cmdSelectEPlusDir.setToolTipText("Select the folder where EnergyPlus.exe and Energy+.idd are located");
        cmdSelectEPlusDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectEPlusDirActionPerformed(evt);
            }
        });

        txtBinDir.setEditable(false);
        txtBinDir.setForeground(new java.awt.Color(255, 0, 0));
        txtBinDir.setText("Select EnergyPlus folder...");
        txtBinDir.setToolTipText("This is the directory where 'Energy+.idd' is located");

        jLabel6.setText("E+ binary directory:");

        cmdEnergyPlusDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/tool.png"))); // NOI18N
        cmdEnergyPlusDetails.setToolTipText("Check and specify individual E+ tools");
        cmdEnergyPlusDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEnergyPlusDetailsActionPerformed(evt);
            }
        });

        lblInformation.setText("jLabel1");
        lblInformation.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblInformation.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel1.setText("Available E+ versions: ");

        cboEPlusVersions.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboEPlusVersions.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboEPlusVersionsItemStateChanged(evt);
            }
        });
        cboEPlusVersions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboEPlusVersionsActionPerformed(evt);
            }
        });

        cmdDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/cross.png"))); // NOI18N
        cmdDelete.setToolTipText("Remove the selected version");
        cmdDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteActionPerformed(evt);
            }
        });

        cmdScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/folder_explore.png"))); // NOI18N
        cmdScan.setText("Scan");
        cmdScan.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        cmdScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdScanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInformation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboEPlusVersions, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmdScan))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtBinDir, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSelectEPlusDir, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEnergyPlusDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cboEPlusVersions, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cmdDelete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(cmdScan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdSelectEPlusDir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBinDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cmdEnergyPlusDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInformation)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectEPlusDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectEPlusDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (getSelectedConfig() != null) {
            fc.setCurrentDirectory(new File(getSelectedConfig().getEPlusBinDir()).getParentFile());
        }
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            EPlusConfig cfg = new EPlusConfig();
            cfg.setNewEPlusBinDir(bindir);
            EPlusConfig cur = getSelectedConfig();
            Config.getEPlusConfigs().put(cfg.getVersion(), cfg);
            Config.setCurrentEPlus(cfg);
            Config.fireConfigChangedEvent ();
//            cboEPlusVersions.setModel(new DefaultComboBoxModel (Config.getEPlusConfigs().values().toArray(new EPlusConfig [0])));
//            cboEPlusVersions.setSelectedItem(cfg);
//            txtBinDir.setText(cfg.getEPlusBinDir());
//            checkSettings();
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
}//GEN-LAST:event_cmdSelectEPlusDirActionPerformed

    private void cmdEnergyPlusDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEnergyPlusDetailsActionPerformed
        JDialog dialog = new JDialog (JEPlusFrameMain.getCurrentMainGUI(), "Set EnergyPlus binaries", true);
        dialog.setLocationRelativeTo(this);
        final JPanel_EPlusSettingsDetailed detPanel = new JPanel_EPlusSettingsDetailed (dialog, getSelectedConfig());
        dialog.getContentPane().add(detPanel);
        // Add dialog closing listener
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
//                initSettings();
//                checkSettings();
            }
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
//                initSettings();
//                checkSettings();
            }
        });
        dialog.setSize(500, 260);
        dialog.setVisible(true);
    }//GEN-LAST:event_cmdEnergyPlusDetailsActionPerformed

    private void cboEPlusVersionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboEPlusVersionsActionPerformed
//        EPlusConfig cfg = getSelectedConfig();
//        if (cfg != null) {
//            txtBinDir.setText(cfg.getEPlusBinDir());
//        }else {
//            txtBinDir.setText("Select an EnergyPlus installation...");
//        }
    }//GEN-LAST:event_cboEPlusVersionsActionPerformed

    private void cmdDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteActionPerformed
        EPlusConfig item = (EPlusConfig)cboEPlusVersions.getSelectedItem();
        Config.getEPlusConfigs().remove(item.getVersion());
        Config.setCurrentEPlus(null);
        Config.fireConfigChangedEvent();
//        this.cboEPlusVersions.removeItem(item);
    }//GEN-LAST:event_cmdDeleteActionPerformed

    private void cboEPlusVersionsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboEPlusVersionsItemStateChanged
        EPlusConfig cfg = getSelectedConfig();
        if (cfg != null) {
            Config.setCurrentEPlus(cfg);
            txtBinDir.setText(cfg.getEPlusBinDir());
            if (cfg.isValid()) {
                txtBinDir.setForeground(Color.black);
                lblInformation.setText("<html>EnergyPlus V" + cfg.toString() + " is available</html>");
            }else {
                txtBinDir.setForeground(Color.red);
                lblInformation.setText("<html>Configuration of EnergyPlus V" + cfg.toString() + " contains errors!</html>");
            }
        }else {
            txtBinDir.setText("Select an EnergyPlus installation...");
            lblInformation.setText("<html>No EnergyPlus installation is selected.</html>");
        }
    }//GEN-LAST:event_cboEPlusVersionsItemStateChanged

    private void cmdScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdScanActionPerformed
        
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (getSelectedConfig() != null) {
            fc.setCurrentDirectory(new File(getSelectedConfig().getEPlusBinDir()).getParentFile());
        }else {
            fc.setCurrentDirectory(new File("/"));
        }
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String base = file.getAbsolutePath();
            logger.info("Scanning E+ installations in " + base);
            // List sub-directories
            String[] sub_dirs = file.list(new FilenameFilter() {
              @Override
              public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
              }
            });            
            // Detect E+ folders
            boolean found = false;
            for (String sub : sub_dirs) {
                String dir = base + File.separator + sub + File.separator;
                if (EPlusConfig.detectEPlusDir(dir)) {
                    EPlusConfig cfg = new EPlusConfig();
                    cfg.setNewEPlusBinDir(dir);
                    Config.getEPlusConfigs().put(cfg.getVersion(), cfg);
                    Config.setCurrentEPlus(cfg);
                    found = true;
                }
            }
            // Check seletect folder as well
            if (EPlusConfig.detectEPlusDir(base + File.separator)) {
                EPlusConfig cfg = new EPlusConfig();
                cfg.setNewEPlusBinDir(base + File.separator);
                Config.getEPlusConfigs().put(cfg.getVersion(), cfg);
                Config.setCurrentEPlus(cfg);
                found = true;
            }
            // fire config changed event if found new bin folders
            if (found) {
                Config.fireConfigChangedEvent ();
            }
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }//GEN-LAST:event_cmdScanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboEPlusVersions;
    private javax.swing.JButton cmdDelete;
    private javax.swing.JButton cmdEnergyPlusDetails;
    private javax.swing.JButton cmdScan;
    private javax.swing.JButton cmdSelectEPlusDir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lblInformation;
    private javax.swing.JTextField txtBinDir;
    // End of variables declaration//GEN-END:variables

    @Override
    public void configChanged(ConfigFileNames config) {
        cboEPlusVersions.setModel(new DefaultComboBoxModel (Config.getEPlusConfigs().values().toArray(new EPlusConfig [0])));
        EPlusConfig cfg = ((JEPlusConfig)config).getCurrentEPlus();
        if (cfg != null) {
            cboEPlusVersions.setSelectedItem(cfg);
        }else if (cboEPlusVersions.getItemCount() > 0) {
            cboEPlusVersions.setSelectedIndex(cboEPlusVersions.getItemCount() - 1);
            cfg = (EPlusConfig)cboEPlusVersions.getSelectedItem();
            Config.setCurrentEPlus(cfg);
        }else {
            cboEPlusVersions.setSelectedIndex(-1);
        }
        if (cfg != null) {
            txtBinDir.setText(cfg.getEPlusBinDir());
        }else {
            txtBinDir.setText("No E+ executable selected!");
        }
        checkSettings();
    }
}
