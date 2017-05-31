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
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import jeplus.ConfigFileNames;
import jeplus.EPlusConfig;
import jeplus.JEPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.event.IF_ConfigChangedEventHandler;

/**
 * JPanel_EPlusSettings.java - This is the view of EPlusConfig record
 * @author zyyz
 * @version 0.6
 * @since 0.5b
 */
public class JPanel_EPlusSettings extends javax.swing.JPanel implements TitledJPanel, IF_ConfigChangedEventHandler {

    protected String title = "E+ Executables";
    protected final JFileChooser fc = new JFileChooser("./");
    protected JEPlusConfig Config;
    
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
     * initialise display from data records
     */
    public final void initSettings () {
        cboEPlusVersions.setModel(new DefaultComboBoxModel (new Vector (Config.getEPlusConfigs().keySet())));
        if (cboEPlusVersions.getModel().getSize() > 0) {
            cboEPlusVersions.setSelectedIndex(0);
            txtBinDir.setText(getSelectedConfig().getEPlusBinDir());
        }
    }
    
    private EPlusConfig getSelectedConfig () {
        if (cboEPlusVersions.getModel().getSize() > 0) {
            return Config.getEPlusConfigs().get(cboEPlusVersions.getSelectedItem().toString());
        }
        return null;
    }

    /**
     * check validity of directory and command/file names
     */
    public final boolean checkSettings () {
        boolean errors = false;
        File dir = new File (getSelectedConfig().getEPlusBinDir());
        if (! (dir.exists() && dir.isDirectory())) {
            txtBinDir.setForeground(Color.red);
            lblInformation.setText("<html>EnergyPlus binary folder " + dir.getAbsolutePath() + " does not exist!</html>");
            errors = true;
        }else {
            txtBinDir.setForeground(Color.black);
            StringBuilder buf = new StringBuilder ("<html><p>");
            File f = new File(getSelectedConfig().getEPlusBinDir() + EPlusConfig.getEPDefIDD());
            if (! f.exists()) {
                buf.append(f.getAbsolutePath());
                errors = true;
            }else {
                buf.append("Found EnergyPlus version ").append(getSelectedConfig().getEPlusVersion()).append("</p><p>");
            }
            f = new File(getSelectedConfig().getEPlusEXEC());
            if (! f.exists()) {
                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
                errors = true;
            }
            f = new File(getSelectedConfig().getEPlusEPMacro());
            if (! f.exists()) {
                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
                errors = true;
            }
            f = new File(getSelectedConfig().getEPlusExpandObjects());
            if (! f.exists()) {
                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
                errors = true;
            }
            f = new File(getSelectedConfig().getEPlusReadVars());
            if (! f.exists()) {
                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
                errors = true;
            }
//            f = new File(Config.getScreenFile());
//            if (! ((f.exists() && f.isFile() && f.canWrite()) || ! f.exists())) {
//                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
//                errors = true;
//            }
            buf.append(!errors ? "" : " are missing!").append("</p></html>");
            lblInformation.setText(buf.toString());
            txtBinDir.setForeground(errors ? Color.red : Color.black);
        }
        
        return !errors;
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

        cmdSelectEPlusDir.setText("...");
        cmdSelectEPlusDir.setToolTipText("Select the folder where EnergyPlus.exe and Energy+.idd are located");
        cmdSelectEPlusDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectEPlusDirActionPerformed(evt);
            }
        });

        txtBinDir.setEditable(false);
        txtBinDir.setText("C:/EnergyPlusV2-2-0/");
        txtBinDir.setToolTipText("This is the directory where 'Energy+.idd' is located");

        jLabel6.setText("E+ binary diretory:");

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
        cboEPlusVersions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboEPlusVersionsActionPerformed(evt);
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
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtBinDir, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSelectEPlusDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEnergyPlusDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboEPlusVersions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtBinDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectEPlusDir)
                    .addComponent(cmdEnergyPlusDetails))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInformation, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectEPlusDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectEPlusDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(getSelectedConfig().getEPlusBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            getSelectedConfig().setEPlusBinDir(bindir);
//            initSettings();
            checkSettings();
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
        txtBinDir.setText(getSelectedConfig().getEPlusBinDir());
    }//GEN-LAST:event_cboEPlusVersionsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboEPlusVersions;
    private javax.swing.JButton cmdEnergyPlusDetails;
    private javax.swing.JButton cmdSelectEPlusDir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lblInformation;
    private javax.swing.JTextField txtBinDir;
    // End of variables declaration//GEN-END:variables

    @Override
    public void configChanged(ConfigFileNames config) {
        this.setConfig((JEPlusConfig)config);
    }

}
