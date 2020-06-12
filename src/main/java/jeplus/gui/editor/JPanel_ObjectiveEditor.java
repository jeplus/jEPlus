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

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.JEPlusProjectV2;
import jeplus.data.RVX_Objective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanel_ObjectiveEditor extends javax.swing.JPanel {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanel_ObjectiveEditor.class);

    JTree HostTree = null;
    protected JEPlusProjectV2 Project = null;
    protected RVX_Objective Objective = null;
    protected DocumentListener DL = null;
    private boolean DLActive = false;

    /**
     * Creates new form JPanel_RVXEditor
     */
    public JPanel_ObjectiveEditor() {
        initComponents();
    }

    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     * @param tree
     * @param prj
     * @param obj
     */
    public JPanel_ObjectiveEditor(JTree tree, JEPlusProjectV2 prj, RVX_Objective obj) {
        initComponents();
        HostTree = tree;
        Project = prj;
        setObjective (obj);
        
        DL = new DocumentListener () {
            Document DocName = txtName.getDocument();
            Document DocCaption = txtCaption.getDocument();
            Document DocFormula = txaFormula.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (DLActive) {
                    Document src = e.getDocument();
                    if(src == DocName) {
                        Objective.setIdentifier(txtName.getText().trim());
                    }else if (src == DocCaption) {
                        Objective.setCaption(txtCaption.getText().trim());
                    }else if (src == DocFormula) {
                        Objective.setFormula(txaFormula.getText().trim());
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
        txtName.getDocument().addDocumentListener(DL);
        txtCaption.getDocument().addDocumentListener(DL);
        txaFormula.getDocument().addDocumentListener(DL);
        DLActive = true;
    }
    
    protected final void setObjective (RVX_Objective var) {
        Objective = var;
        txtName.setText(Objective.getIdentifier());
        txtCaption.setText(Objective.getCaption());
        txaFormula.setText(Objective.getFormula());
        chkEnabled.setSelected(Objective.isEnabled());
    }

        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCaption = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaFormula = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaInputVars = new javax.swing.JTextArea();
        cmdCalc = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtResult = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblErrorMsg = new javax.swing.JLabel();
        chkEnabled = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();

        jLabel4.setText("Formula: ");

        jLabel5.setText("Identifier: ");

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Objective function definition:");
        jLabel1.setOpaque(true);

        txtName.setText("T1");

        jLabel7.setText("Caption [Unit]:");

        txtCaption.setText("Object1");

        jLabel8.setBackground(new java.awt.Color(204, 204, 204));
        jLabel8.setText("Test formula by specifying the independent vars: ");
        jLabel8.setOpaque(true);

        txaFormula.setColumns(20);
        txaFormula.setRows(2);
        jScrollPane1.setViewportView(txaFormula);

        txaInputVars.setColumns(20);
        txaInputVars.setRows(3);
        jScrollPane2.setViewportView(txaInputVars);

        cmdCalc.setText("Evaluate");
        cmdCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCalcActionPerformed(evt);
            }
        });

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, txtName, org.jdesktop.beansbinding.ELProperty.create("${text}"), jLabel6, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtResult.setEditable(false);
        txtResult.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtResult.setText("0");

        jLabel10.setText("=");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, txtName, org.jdesktop.beansbinding.ELProperty.create("${text}"), jLabel11, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel12.setText("=");

        lblErrorMsg.setText(" ");

        chkEnabled.setText(" ");
        chkEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEnabledActionPerformed(evt);
            }
        });

        jLabel13.setText("Enabled: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(13, 13, 13)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkEnabled)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCaption))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel9))
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdCalc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txtResult, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblErrorMsg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEnabled)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(txtCaption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdCalc)
                    .addComponent(jLabel6)
                    .addComponent(txtResult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblErrorMsg)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jLabel9))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCalcActionPerformed
        lblErrorMsg.setText("");
        ScriptEngine engine = JEPlusProjectV2.getScript_Engine();
        try {
            engine.eval(this.txaInputVars.getText());
            String res = engine.eval(this.txaFormula.getText()).toString();
            txtResult.setText(res);
        }catch (ScriptException spe) {
            logger.error("Error evaluating expression.", spe);
            lblErrorMsg.setText("Error evaluating formula! Check logs for details.");
        }

    }//GEN-LAST:event_cmdCalcActionPerformed

    private void chkEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEnabledActionPerformed
        if (DLActive) {
            Objective.setEnabled(chkEnabled.isSelected());
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_chkEnabledActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkEnabled;
    private javax.swing.JButton cmdCalc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblErrorMsg;
    private javax.swing.JTextArea txaFormula;
    private javax.swing.JTextArea txaInputVars;
    private javax.swing.JTextField txtCaption;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtResult;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
