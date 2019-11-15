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
package jeplus.gui.editor;

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.EPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.JEPlusProjectV2;
import jeplus.data.RVX_RVIitem;
import jeplus.gui.ButtonTabComponent;
import jeplus.gui.EPlusEditorPanel;
import jeplus.util.RelativeDirUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanel_RVIitmeEditor extends javax.swing.JPanel {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanel_RVIitmeEditor.class);

    JEPlusFrameMain MainGUI = null;
    JTree HostTree = null;
    protected JEPlusProjectV2 Project = null;
    protected RVX_RVIitem Rvi = null;
    protected DocumentListener DL = null;
    private boolean DLActive = false;

    /**
     * Creates new form JPanel_RVXEditor
     */
    public JPanel_RVIitmeEditor() {
        initComponents();
    }

    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     * @param frame
     * @param tree
     * @param prj
     * @param rvi
     */
    public JPanel_RVIitmeEditor(JEPlusFrameMain frame, JTree tree, JEPlusProjectV2 prj, RVX_RVIitem rvi) {
        initComponents();
        MainGUI = frame;
        HostTree = tree;
        Project = prj;
        this.cboFrequency.setModel(new DefaultComboBoxModel (RVX_RVIitem.Frequencies.values()));
        setRviItem (rvi);
        
        DL = new DocumentListener () {
            Document DocRviFile = txtRviFile.getDocument();
            Document DocResultTable = txtResultTable.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (DLActive) {
                    Document src = e.getDocument();
                    if(src == DocRviFile) {
                        Rvi.setFileName(txtRviFile.getText().trim());
                    }else if (src == DocResultTable) {
                        Rvi.setTableName(txtResultTable.getText().trim());
                    }
                    Project.setContentChanged(true);
                    HostTree.update(HostTree.getGraphics());
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
        txtRviFile.getDocument().addDocumentListener(DL);
        txtResultTable.getDocument().addDocumentListener(DL);
        DLActive = true;
    }
    
    protected final void setRviItem (RVX_RVIitem rvi) {
        Rvi = rvi;
        txtRviFile.setText(Rvi.getFileName());
        cboFrequency.setSelectedItem(RVX_RVIitem.Frequencies.valueOf(Rvi.getFrequency()));
        txtResultTable.setText(Rvi.getTableName());
        chkAggregate.setSelected(Rvi.isUsedInCalc());
    }

        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdSelectRVIFile = new javax.swing.JButton();
        cmdEditRVI = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtResultTable = new javax.swing.JTextField();
        chkAggregate = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboFrequency = new javax.swing.JComboBox();
        txtRviFile = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        cmdSelectRVIFile.setText("...");
        cmdSelectRVIFile.setToolTipText("Select rvi file (.rvi/.mvi). Single selection only.");
        cmdSelectRVIFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectRVIFileActionPerformed(evt);
            }
        });

        cmdEditRVI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditRVI.setToolTipText("Edit the contents of my.rvi");
        cmdEditRVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditRVIActionPerformed(evt);
            }
        });

        jLabel2.setText("Result Table:");

        txtResultTable.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtResultTable.setText("SimResults");

        chkAggregate.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        chkAggregate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAggregateActionPerformed(evt);
            }
        });

        jLabel3.setText("Aggregate:");

        jLabel4.setText("RVI file:");

        jLabel5.setText("Frequency:");

        cboFrequency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Annual", "Monthly", "Daily", "Hourly", "Timesteip" }));
        cboFrequency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFrequencyActionPerformed(evt);
            }
        });

        txtRviFile.setText("Select a RVI/MVI file ...");

        jLabel6.setText(".csv");

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Run EPlus ReadVarsEso with the specified RVI/MVI file");
        jLabel1.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRviFile, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSelectRVIFile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdEditRVI, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chkAggregate)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(23, 23, 23)
                                .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRviFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(cmdSelectRVIFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(cmdEditRVI))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkAggregate)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectRVIFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectRVIFileActionPerformed
        // Select a file to open
        MainGUI.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.RVI));
        MainGUI.getFileChooser().setMultiSelectionEnabled(false);
        MainGUI.getFileChooser().setSelectedFile(new File(""));
        String rvidir = RelativeDirUtil.checkAbsolutePath(txtRviFile.getText(), Project.getBaseDir());
        MainGUI.getFileChooser().setCurrentDirectory(new File(rvidir).getParentFile());
        if (MainGUI.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = MainGUI.getFileChooser().getSelectedFile();
            txtRviFile.setText(file.getAbsolutePath());
        }
        MainGUI.getFileChooser().resetChoosableFileFilters();
        MainGUI.getFileChooser().setSelectedFile(new File(""));
    }//GEN-LAST:event_cmdSelectRVIFileActionPerformed

    private void cmdEditRVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditRVIActionPerformed

        // Test if the template file is present
        String fn = txtRviFile.getText();
        String templfn = RelativeDirUtil.checkAbsolutePath(txtRviFile.getText(), Project.getBaseDir());
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
                templfn = txtRviFile.getText();
            }
        }
        int idx = MainGUI.getTpnEditors().indexOfTab(fn);
        if (idx >= 0) {
            MainGUI.getTpnEditors().setSelectedIndex(idx);
        } else {
            EPlusEditorPanel RviFilePanel;
            RviFilePanel = new EPlusEditorPanel(
                MainGUI.getTpnEditors(),
                fn,
                templfn,
                EPlusEditorPanel.FileType.RVX,
                null);
            int ti = MainGUI.getTpnEditors().getTabCount();
            MainGUI.getTpnEditors().addTab(fn, RviFilePanel);
            RviFilePanel.setTabId(ti);
            MainGUI.getTpnEditors().setSelectedIndex(ti);
            MainGUI.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(MainGUI.getTpnEditors(), RviFilePanel));
            MainGUI.getTpnEditors().setToolTipTextAt(ti, templfn);
        }
    }//GEN-LAST:event_cmdEditRVIActionPerformed

    private void cboFrequencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFrequencyActionPerformed
        if (DLActive) {
            Rvi.setFrequency(cboFrequency.getSelectedItem().toString());
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_cboFrequencyActionPerformed

    private void chkAggregateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAggregateActionPerformed
        if (DLActive) {
            Rvi.setUsedInCalc(chkAggregate.isSelected());
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_chkAggregateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboFrequency;
    private javax.swing.JCheckBox chkAggregate;
    private javax.swing.JButton cmdEditRVI;
    private javax.swing.JButton cmdSelectRVIFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField txtResultTable;
    private javax.swing.JTextField txtRviFile;
    // End of variables declaration//GEN-END:variables
}
