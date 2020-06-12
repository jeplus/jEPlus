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
import java.awt.event.ItemEvent;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.ConfigFileNames;
import jeplus.JEPlusConfig;
import jeplus.ScriptConfig;
import jeplus.event.IF_ConfigChangedEventHandler;

/**
 * JPanel_EPlusSettings.java - This is the view of EPlusConfig record
 * @author zyyz
 * @version 0.6
 * @since 0.5b
 */
public class JPanel_ScriptSettings extends javax.swing.JPanel implements TitledJPanel, IF_ConfigChangedEventHandler {

    protected String title = "Script interpreters";
    protected final JFileChooser fc = new JFileChooser("./");
    protected JEPlusConfig Config;
    protected String CurrentKey = null;
    protected ScriptConfig CurrentObj = null;
    
    protected DocumentListener DL = null;
    private boolean DLActive = false;
    
    /** 
     * Creates new form JPanel_EPlusSettings
     */
    public JPanel_ScriptSettings() {
        initComponents();
        // Add listener
        DL = new DocumentListener () {
            Document DocExt = txtExt.getDocument();
            Document DocExec = txtExec.getDocument();
            Document DocArgs = txtArgs.getDocument();
            Document DocVerCmd = txtVerCmd.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (DLActive) {
                    Document src = e.getDocument();
                    if(src == DocExt) {
                        CurrentObj.setScriptExt(txtExt.getText().trim());
                    }else if (src == DocExec) {
                        CurrentObj.setExec(txtExec.getText().trim());
                    }else if (src == DocArgs) {
                        CurrentObj.setArgs(txtArgs.getText().trim());
                    }else if (src == DocVerCmd) {
                        CurrentObj.setVerCmd(txtVerCmd.getText().trim());
                    }
                    lblCmdLn.setText(CurrentObj.toString());
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
        txtExt.getDocument().addDocumentListener(DL);
        // txtExec.getDocument().addDocumentListener(DL);
        txtArgs.getDocument().addDocumentListener(DL);
        txtVerCmd.getDocument().addDocumentListener(DL);
        DLActive = true;
    }

//    /** 
//     * Creates new form JPanel_EPlusSettings
//     * @param config 
//     */
//    public JPanel_ScriptSettings(JEPlusConfig config) {
//        initComponents();
//        // Set config object
//        setConfig(config);
//    }

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
//        if (Config != config) {
//            if (Config != null) {
//                Config.removeListener(this);
//            }
//            Config = config;
//            Config.addListener(this);
//        }
        Config = config;
        initSettings();
    }

    /**
     * initialise display from data records
     */
    public final void initSettings () {
        DLActive = false;
        cboScript.setModel(new DefaultComboBoxModel (Config.getScripConfigs().keySet().toArray(new String [0])));
        cboScript.setEditable(false);
        if (CurrentKey != null && Config.getScripConfigs().containsKey(CurrentKey)) {
            cboScript.setSelectedItem(CurrentKey);
            CurrentObj = Config.getScripConfigs().get(CurrentKey);
            this.setScriptConfig(CurrentObj);
        }else if (cboScript.getModel().getSize() > 0) {
            // cboScript.setSelectedIndex(0);
            String name = (String)cboScript.getSelectedItem();
            CurrentKey = name;
            CurrentObj = Config.getScripConfigs().get(name);
            this.setScriptConfig(CurrentObj);
        }
        DLActive = true;
    }
    
    private void setScriptConfig (ScriptConfig script) {
        DLActive = false;
        txtExt.setText(script.getScriptExt());
        txtExec.setText(script.getExec());
        txtArgs.setText(script.getArgs());
        txtVerCmd.setText(script.getVerCmd());
        if (! script.isValid()) {
            txtExec.setForeground(Color.red);
            this.lblCmdLn.setText("The executable does not exist or is not executable!");
        }else {
            txtExec.setForeground(Color.black);
            this.lblCmdLn.setText(script.toString());
        }
        DLActive = true;
    }
    
    private ScriptConfig getSelectedConfig () {
        return Config.getScripConfigs().get((String)cboScript.getSelectedItem());    
    }
    
    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdSelect = new javax.swing.JButton();
        txtExec = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmdAdd = new javax.swing.JButton();
        lblCmdLn = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cboScript = new javax.swing.JComboBox();
        cmdDelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtArgs = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtExt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtVerCmd = new javax.swing.JTextField();
        cmdCheck = new javax.swing.JButton();

        cmdSelect.setText("...");
        cmdSelect.setToolTipText("Select the executable of the script interpreter");
        cmdSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectActionPerformed(evt);
            }
        });

        txtExec.setEditable(false);
        txtExec.setForeground(new java.awt.Color(255, 0, 0));
        txtExec.setText("Click on the Add button to create a new script  entry...");
        txtExec.setToolTipText("");

        jLabel6.setText("Interpreter Excutable: ");

        cmdAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/plus1.png"))); // NOI18N
        cmdAdd.setToolTipText("Add a new script interpreter");
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        lblCmdLn.setText(" ");
        lblCmdLn.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblCmdLn.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel1.setText("Script Language: ");

        cboScript.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboScript.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboScriptItemStateChanged(evt);
            }
        });
        cboScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboScriptActionPerformed(evt);
            }
        });

        cmdDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/cross.png"))); // NOI18N
        cmdDelete.setToolTipText("Remove the selected script interpreter");
        cmdDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteActionPerformed(evt);
            }
        });

        jLabel2.setText("Default Exec Arguments:");

        txtArgs.setToolTipText("These arguments are always included when calling the interpreter");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Script Extension: ");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtExt.setColumns(6);
        txtExt.setToolTipText("Script file extension, e.g. py (without a dot)");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Version Command: ");

        txtVerCmd.setColumns(12);
        txtVerCmd.setToolTipText("Command-line args for version info.");

        cmdCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/check.png"))); // NOI18N
        cmdCheck.setToolTipText("Remove the selected script interpreter");
        cmdCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCheckActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCmdLn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboScript, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtExt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(txtArgs)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtVerCmd, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtExec))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmdSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmdCheck, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboScript)
                    .addComponent(txtExt)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtExec, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmdSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtArgs, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVerCmd, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(cmdCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCmdLn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (getSelectedConfig() != null) {
            fc.setCurrentDirectory(new File(getSelectedConfig().getExec()).getParentFile());
        }
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            this.txtExec.setText(fn);
            CurrentObj.setExec(fn);
            Config.fireConfigChangedEvent();
            // this.setScriptConfig(CurrentObj);
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
}//GEN-LAST:event_cmdSelectActionPerformed

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        String name = "Script";
        int count = 1;
        while (Config.getScripConfigs().containsKey(name)) {
            name = "Script" + (count++);
        }
        Config.getScripConfigs().put(name, new ScriptConfig());
        cboScript.addItem(name);
        cboScript.setSelectedItem(name);
        cboScript.setEditable(true);
    }//GEN-LAST:event_cmdAddActionPerformed

    private void cboScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboScriptActionPerformed
        int i = 0;
        i ++;
    }//GEN-LAST:event_cboScriptActionPerformed

    private void cmdDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteActionPerformed
        int idx = cboScript.getSelectedIndex();
        String name = (String)cboScript.getSelectedItem();
        Config.getScripConfigs().remove(name);
        // cboScript.setSelectedIndex(Math.max(0, --idx));
        cboScript.removeItem(name);
        Config.fireConfigChangedEvent();
    }//GEN-LAST:event_cmdDeleteActionPerformed

    private void cboScriptItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboScriptItemStateChanged
        String name = (String)evt.getItem();
        if (cboScript.isEditable() && !Config.getScripConfigs().containsKey(name)) {
            Config.getScripConfigs().remove(CurrentKey);
            Config.getScripConfigs().put(name, CurrentObj);
            cboScript.removeItem(CurrentKey);
            cboScript.addItem(name);
            cboScript.setEditable(false);
            Config.fireConfigChangedEvent();
        }else if (evt.getStateChange() == ItemEvent.SELECTED){
            CurrentKey = name;
            CurrentObj = Config.getScripConfigs().get(name);
            this.setScriptConfig(CurrentObj);
        }
    }//GEN-LAST:event_cboScriptItemStateChanged

    private void cmdCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCheckActionPerformed
        Config.fireConfigChangedEvent();
    }//GEN-LAST:event_cmdCheckActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboScript;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdCheck;
    private javax.swing.JButton cmdDelete;
    private javax.swing.JButton cmdSelect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lblCmdLn;
    private javax.swing.JTextField txtArgs;
    private javax.swing.JTextField txtExec;
    private javax.swing.JTextField txtExt;
    private javax.swing.JTextField txtVerCmd;
    // End of variables declaration//GEN-END:variables

    @Override
    public void configChanged(ConfigFileNames config) {
        this.initSettings();
    }
    
    public static void main (String [] args) {
        JFrame frame = new JFrame ("IDF Version Converter");
        JPanel_ScriptSettings panel = new JPanel_ScriptSettings ();
        panel.setConfig(JEPlusConfig.getDefaultInstance());
        frame.getContentPane().add(panel);
        frame.setSize(500, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
