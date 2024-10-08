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

import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.JEPlusFrameMain;
import jeplus.JEPlusProjectV2;
import jeplus.data.RVX_ESOitem;
import jeplus.data.RVX_RVIitem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanel_ESOitmeEditor extends javax.swing.JPanel {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanel_ESOitmeEditor.class);

    JEPlusFrameMain MainGUI = null;
    JTree HostTree = null;
    protected JEPlusProjectV2 Project = null;
    protected RVX_ESOitem Eso = null;
    protected DocumentListener DL = null;
    private boolean DLActive = false;

    /**
     * Creates new form JPanel_RVXEditor
     */
    public JPanel_ESOitmeEditor() {
        initComponents();
    }

    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     * @param frame
     * @param tree
     * @param prj
     * @param eso
     */
    public JPanel_ESOitmeEditor(JEPlusFrameMain frame, JTree tree, JEPlusProjectV2 prj, RVX_ESOitem eso) {
        initComponents();
        MainGUI = frame;
        HostTree = tree;
        Project = prj;
        this.cboFrequency.setModel(new DefaultComboBoxModel (RVX_RVIitem.Frequencies.values()));
        setEsoItem (eso);
        
        DL = new DocumentListener () {
            Document DocVariables = txaVariables.getDocument();
            Document DocResultTable = txtResultTable.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (DLActive) {
                    Document src = e.getDocument();
                    if(src == DocVariables) {
                        String [] vars = txaVariables.getText().trim().split("\n");
                        Eso.setVariables(Arrays.asList(vars));
                    }else if (src == DocResultTable) {
                        Eso.setTableName(txtResultTable.getText().trim());
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
        txaVariables.getDocument().addDocumentListener(DL);
        txtResultTable.getDocument().addDocumentListener(DL);
        DLActive = true;
    }
    
    protected final void setEsoItem (RVX_ESOitem eso) {
        Eso = eso;
        txaVariables.setText(String.join("\n", Eso.getVariables()));
        cboFrequency.setSelectedItem(RVX_RVIitem.Frequencies.valueOf(Eso.getFrequency()));
        txtResultTable.setText(Eso.getTableName());
        chkAggregate.setSelected(Eso.isUsedInCalc());
    }

        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        txtResultTable = new javax.swing.JTextField();
        chkAggregate = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboFrequency = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaVariables = new javax.swing.JTextArea();

        jLabel2.setText("Result Table:");

        txtResultTable.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtResultTable.setText("SimResults");

        chkAggregate.setText(" ");
        chkAggregate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAggregateActionPerformed(evt);
            }
        });

        jLabel3.setText("Aggregate:");

        jLabel4.setText("Variables:");

        jLabel5.setText("Frequency:");

        cboFrequency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Annual", "Monthly", "Daily", "Hourly", "Timesteip" }));
        cboFrequency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFrequencyActionPerformed(evt);
            }
        });

        jLabel6.setText(".csv");

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Extract report variables from eplusout.eso file");
        jLabel1.setOpaque(true);

        txaVariables.setColumns(20);
        txaVariables.setRows(5);
        jScrollPane2.setViewportView(txaVariables);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkAggregate)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6))
                                    .addComponent(cboFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkAggregate)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboFrequencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFrequencyActionPerformed
        if (DLActive) {
            Eso.setFrequency(cboFrequency.getSelectedItem().toString());
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_cboFrequencyActionPerformed

    private void chkAggregateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAggregateActionPerformed
        if (DLActive) {
            Eso.setUsedInCalc(chkAggregate.isSelected());
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_chkAggregateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboFrequency;
    private javax.swing.JCheckBox chkAggregate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txaVariables;
    private javax.swing.JTextField txtResultTable;
    // End of variables declaration//GEN-END:variables
}
