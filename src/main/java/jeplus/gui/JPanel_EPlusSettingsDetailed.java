/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@googlemail.com>               *
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
 *  - Created
 *  - 2010-06-09 Corrected a bug that ExpandObjects' directory does not
 *               update with the E+ root directory
 *  - 2010-11-01 Split initSection1() into 3 functions, ie. initSettings(),
 *               checkSettings(), and updateSettings(); added a member to
 *               identify the title of this panel
 *                                                                         *
 ***************************************************************************/
package jeplus.gui; //

import java.awt.Color;
import java.awt.Window;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import jeplus.EPlusConfig;
import jeplus.JEPlusConfig;

/**
 * JPanel_EPlusSettings.java - This is the view of EPlusConfig record
 * @author zyyz
 * @version 0.6
 * @since 0.5b
 */
public class JPanel_EPlusSettingsDetailed extends javax.swing.JPanel implements TitledJPanel {

    protected String title = "E+ Executables";
    protected final JFileChooser fc = new JFileChooser("./");
    protected JEPlusConfig Config = JEPlusConfig.getDefaultInstance();
    public void setConfig(JEPlusConfig config) {
        Config = config;
        initSettings();
    }
    protected Window HostWindow = null;

    /** Creates new form JPanel_EPlusSettings */
    public JPanel_EPlusSettingsDetailed(Window hostwindow) {
        initComponents();
        initSettings();
        checkSettings();
        HostWindow = hostwindow;
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
        txtBinDir.setText(Config.getEPlusBinDir());
        txtEPlusEXE.setText(Config.getEPlusEXEC());
        txtReadVarEXE.setText(Config.getEPlusReadVars());
        txtEPMacroEXE.setText(Config.getEPlusEPMacro());
        txtExpandObjectsEXE.setText(Config.getEPlusExpandObjects());
        txtScreenLog.setText(Config.getScreenFile());
    }

    /**
     * check validity of directory and command/file names
     */
    public final void checkSettings () {
        File dir = new File (txtBinDir.getText());
        if (! (dir.exists() && dir.isDirectory())) txtBinDir.setForeground(Color.red);
        else txtBinDir.setForeground(Color.black);

        if (! new File(txtEPlusEXE.getText()).exists()) txtEPlusEXE.setForeground(Color.red);
        else txtEPlusEXE.setForeground(Color.black);

        if (! new File(txtReadVarEXE.getText()).exists()) txtReadVarEXE.setForeground(Color.red);
        else txtReadVarEXE.setForeground(Color.black);

        if (! new File(txtEPMacroEXE.getText()).exists()) txtEPMacroEXE.setForeground(Color.red);
        else txtEPMacroEXE.setForeground(Color.black);

        if (! new File(txtExpandObjectsEXE.getText()).exists()) txtExpandObjectsEXE.setForeground(Color.red);
        else txtExpandObjectsEXE.setForeground(Color.black);

        File log = new File(txtScreenLog.getText());
        if (! ((log.exists() && log.isFile() && log.canWrite()) || ! log.exists())) txtScreenLog.setForeground(Color.red);
        else txtScreenLog.setForeground(Color.black);
    }

    /**
     * update record for directory and file names
     */
    protected final void updateSettings () {
        Config.setEPlusBinDir(txtBinDir.getText());
        Config.setEPlusEXEC(txtEPlusEXE.getText());
        Config.setEPlusReadVars(txtReadVarEXE.getText());
        Config.setEPlusEPMacro(txtEPMacroEXE.getText());
        Config.setEPlusExpandObjects(txtExpandObjectsEXE.getText());
        Config.setScreenFile(txtScreenLog.getText());
    }


    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel16 = new javax.swing.JLabel();
        txtScreenLog = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cmdSelectEPlusDir = new javax.swing.JButton();
        txtExpandObjectsEXE = new javax.swing.JTextField();
        cmdSelectReadVars = new javax.swing.JButton();
        txtBinDir = new javax.swing.JTextField();
        cmdSelectEPexe = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cmdSelectExpandObjects = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtReadVarEXE = new javax.swing.JTextField();
        txtEPlusEXE = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmdSelectEPMacro = new javax.swing.JButton();
        txtEPMacroEXE = new javax.swing.JTextField();
        cmdSave = new javax.swing.JButton();

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel16.setText("Console log file");

        txtScreenLog.setText("jeplus.log");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Energy+ ExpandObjects");

        cmdSelectEPlusDir.setText("...");
        cmdSelectEPlusDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectEPlusDirActionPerformed(evt);
            }
        });

        txtExpandObjectsEXE.setEditable(false);
        txtExpandObjectsEXE.setText("ExpandObjects.exe");

        cmdSelectReadVars.setText("...");
        cmdSelectReadVars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectReadVarsActionPerformed(evt);
            }
        });

        txtBinDir.setEditable(false);
        txtBinDir.setText("C:/EnergyPlusV2-2-0/");
        txtBinDir.setToolTipText("This is the directory where 'Energy+.idd' is located");

        cmdSelectEPexe.setText("...");
        cmdSelectEPexe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectEPexeActionPerformed(evt);
            }
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Energy+ binary diretory");

        cmdSelectExpandObjects.setText("...");
        cmdSelectExpandObjects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectExpandObjectsActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Energy+ ReadVars");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText("Energy+ EP-Macro");

        jLabel15.setText("command:");

        txtReadVarEXE.setEditable(false);
        txtReadVarEXE.setText("ReadVarsESO.exe");
        txtReadVarEXE.setToolTipText("The command may vary with different versions of EnergyPlus. Edit this field if necessary. If the executable is located in a different location, please specify the relative diretory to the Energy+ binary directory above. Case-sensitive in Linux.");

        txtEPlusEXE.setEditable(false);
        txtEPlusEXE.setText("EnergyPlus.exe");
        txtEPlusEXE.setToolTipText("The command may vary with different versions of EnergyPlus. Edit this field if necessary. If the executable is located in a different location, please specify the relative diretory to the Energy+ binary directory above. Case-sensitive in Linux.");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Energy+ executable");

        cmdSelectEPMacro.setText("...");
        cmdSelectEPMacro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectEPMacroActionPerformed(evt);
            }
        });

        txtEPMacroEXE.setEditable(false);
        txtEPMacroEXE.setText("EPMacro.exe");
        txtEPMacroEXE.setToolTipText("The command may vary with different versions of EnergyPlus. Edit this field if necessary. If the executable is located in a different location, please specify the relative diretory to the Energy+ binary directory above. Case-sensitive in Linux.");

        cmdSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/save1.png"))); // NOI18N
        cmdSave.setText("Save to jeplus.cfg");
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtEPlusEXE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(txtReadVarEXE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(txtExpandObjectsEXE, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(txtEPMacroEXE, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(txtBinDir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cmdSelectEPexe, 0, 1, Short.MAX_VALUE)
                            .addComponent(cmdSelectEPMacro, 0, 1, Short.MAX_VALUE)
                            .addComponent(cmdSelectEPlusDir, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(cmdSelectReadVars, javax.swing.GroupLayout.Alignment.LEADING, 0, 1, Short.MAX_VALUE)
                            .addComponent(cmdSelectExpandObjects, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cmdSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtBinDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectEPlusDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdSelectEPexe)
                    .addComponent(txtEPlusEXE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEPMacroEXE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(cmdSelectEPMacro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtExpandObjectsEXE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectExpandObjects))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdSelectReadVars)
                    .addComponent(txtReadVarEXE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(cmdSave)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectExpandObjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectExpandObjectsActionPerformed
        // Select a file to open
        fc.resetChoosableFileFilters();
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(new File(Config.getEPlusBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String name = file.getPath();
            txtExpandObjectsEXE.setText(name);
            txtExpandObjectsEXE.setForeground(Color.black);
            Config.setEPlusExpandObjects(name);
        }
        fc.resetChoosableFileFilters();
}//GEN-LAST:event_cmdSelectExpandObjectsActionPerformed

    private void cmdSelectEPexeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectEPexeActionPerformed
        // Select a file to open
        fc.resetChoosableFileFilters();
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(new File(Config.getEPlusBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String name = file.getPath();
            txtEPlusEXE.setText(name);
            txtEPlusEXE.setForeground(Color.black);
            Config.setEPlusEXEC(name);
        }
        fc.resetChoosableFileFilters();
}//GEN-LAST:event_cmdSelectEPexeActionPerformed

    private void cmdSelectReadVarsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectReadVarsActionPerformed
        // Select a file to open
        fc.resetChoosableFileFilters();
        fc.setMultiSelectionEnabled(false);
        File bindir = new File(Config.getEPlusBinDir());
        fc.setCurrentDirectory(bindir);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String name = file.getPath();
            //String name = RelativeDirUtil.getRelativePath(bindir, file);
            txtReadVarEXE.setText(name);
            txtReadVarEXE.setForeground(Color.black);
            Config.setEPlusReadVars(name);
        }
        fc.resetChoosableFileFilters();
}//GEN-LAST:event_cmdSelectReadVarsActionPerformed

    private void cmdSelectEPlusDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectEPlusDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(Config.getEPlusBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            Config.setEPlusBinDir(bindir);
            Config.setEPlusEPMacro(bindir + EPlusConfig.getDefEPlusEPMacro());
            Config.setEPlusExpandObjects(bindir + EPlusConfig.getDefEPlusExpandObjects());
            Config.setEPlusEXEC(bindir + EPlusConfig.getDefEPlusEXEC());
            Config.setEPlusReadVars(bindir + EPlusConfig.getDefEPlusReadVars());
            initSettings();
            checkSettings();
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
}//GEN-LAST:event_cmdSelectEPlusDirActionPerformed

    private void cmdSelectEPMacroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectEPMacroActionPerformed
        // Select a file to open
        fc.resetChoosableFileFilters();
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(new File(Config.getEPlusBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String name = file.getPath();
            txtEPMacroEXE.setText(name);
            txtEPMacroEXE.setForeground(Color.black);
            Config.setEPlusEPMacro(name);
        }
        fc.resetChoosableFileFilters();
}//GEN-LAST:event_cmdSelectEPMacroActionPerformed

    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
        Config.saveToFile("jEPlus configuration generated at " + SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(new Date()));
        HostWindow.dispose();
    }//GEN-LAST:event_cmdSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdSave;
    private javax.swing.JButton cmdSelectEPMacro;
    private javax.swing.JButton cmdSelectEPexe;
    private javax.swing.JButton cmdSelectEPlusDir;
    private javax.swing.JButton cmdSelectExpandObjects;
    private javax.swing.JButton cmdSelectReadVars;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField txtBinDir;
    private javax.swing.JTextField txtEPMacroEXE;
    private javax.swing.JTextField txtEPlusEXE;
    private javax.swing.JTextField txtExpandObjectsEXE;
    private javax.swing.JTextField txtReadVarEXE;
    private javax.swing.JTextField txtScreenLog;
    // End of variables declaration//GEN-END:variables

}
