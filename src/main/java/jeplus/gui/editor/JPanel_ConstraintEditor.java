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
import jeplus.data.RVX_Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanel_ConstraintEditor extends javax.swing.JPanel {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanel_ConstraintEditor.class);

    JTree HostTree = null;
    protected JEPlusProjectV2 Project = null;
    protected RVX_Constraint Constraint = null;
    protected DocumentListener DL = null;
    private boolean DLActive = false;

    /**
     * Creates new form JPanel_RVXEditor
     */
    public JPanel_ConstraintEditor() {
        initComponents();
    }

    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     * @param tree
     * @param prj
     * @param cons
     */
    public JPanel_ConstraintEditor(JTree tree, JEPlusProjectV2 prj, RVX_Constraint cons) {
        initComponents();
        HostTree = tree;
        Project = prj;
        setConstraint (cons);
        
        DL = new DocumentListener () {
            Document DocName = txtName.getDocument();
            Document DocCaption = txtCaption.getDocument();
            Document DocFormula = txaFormula.getDocument();
            Document DocMin = txtMin.getDocument();
            Document DocLB = txtLB.getDocument();
            Document DocUB = txtUB.getDocument();
            Document DocMax = txtMax.getDocument();
            Document DocWeight = txtWeight.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (DLActive) {
                    Document src = e.getDocument();
                    if(src == DocName) {
                        Constraint.setIdentifier(txtName.getText().trim());
                    }else if (src == DocCaption) {
                        Constraint.setCaption(txtCaption.getText().trim());
                    }else if (src == DocFormula) {
                        Constraint.setFormula(txaFormula.getText().trim());
                    }else if (src == DocMin) {
                        Constraint.setMin(Double.parseDouble(txtMin.getText().trim()));
                    }else if (src == DocLB) {
                        Constraint.setLB(Double.parseDouble(txtLB.getText().trim()));
                    }else if (src == DocUB) {
                        Constraint.setUB(Double.parseDouble(txtUB.getText().trim()));
                    }else if (src == DocMax) {
                        Constraint.setMax(Double.parseDouble(txtMax.getText().trim()));
                    }else if (src == DocWeight) {
                        Constraint.setWeight(Double.parseDouble(txtWeight.getText().trim()));
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
        txtMin.getDocument().addDocumentListener(DL);
        txtLB.getDocument().addDocumentListener(DL);
        txtUB.getDocument().addDocumentListener(DL);
        txtMax.getDocument().addDocumentListener(DL);
        txtWeight.getDocument().addDocumentListener(DL);
        DLActive = true;
    }
    
    protected final void setConstraint (RVX_Constraint var) {
        Constraint = var;
        txtName.setText(Constraint.getIdentifier());
        txtCaption.setText(Constraint.getCaption());
        txaFormula.setText(Constraint.getFormula());
        txtMin.setText(Double.toString(Constraint.getMin()));
        txtLB.setText(Double.toString(Constraint.getLB()));
        txtUB.setText(Double.toString(Constraint.getUB()));
        txtMax.setText(Double.toString(Constraint.getMax()));
        txtWeight.setText(Double.toString(Constraint.getWeight()));
        chkEnabled.setSelected(Constraint.isEnabled());
        chkScale.setSelected(Constraint.isScaling());
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

        canvas1 = new java.awt.Canvas();
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
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtMin = new javax.swing.JTextField();
        txtLB = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtUB = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtMax = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtWeight = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtInfeas = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        chkScale = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        chkEnabled = new javax.swing.JCheckBox();
        lblErrorMsg = new javax.swing.JLabel();

        jLabel4.setText("Formula: ");

        jLabel5.setText("Identifier: ");

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Objective function definition:");
        jLabel1.setOpaque(true);

        txtName.setText("T1");

        jLabel7.setText("Caption [Unit]: ");

        txtCaption.setText("Some constraint");

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

        jLabel13.setText("Scaling:");

        jLabel14.setText("Min");

        txtMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMin.setText("0");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkScale, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtMin, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        txtLB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtLB.setText("0");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkScale, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtLB, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel15.setText("LB");

        txtUB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtUB.setText("0");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkScale, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtUB, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel16.setText("UB");

        txtMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMax.setText("0");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkScale, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtMax, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel17.setText("Max");

        txtWeight.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtWeight.setText("0");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chkScale, org.jdesktop.beansbinding.ELProperty.create("${selected}"), txtWeight, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel18.setText("Weight");

        txtInfeas.setEditable(false);
        txtInfeas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtInfeas.setText("0.555");

        jLabel21.setText("Infeasibility = ");

        chkScale.setSelected(true);
        chkScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScaleActionPerformed(evt);
            }
        });

        jLabel19.setText("Enabled: ");

        chkEnabled.setSelected(true);
        chkEnabled.setText(" ");
        chkEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEnabledActionPerformed(evt);
            }
        });

        lblErrorMsg.setText("Message");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(chkScale)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtMin)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtLB, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtUB, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtMax, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtWeight)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 20, Short.MAX_VALUE))
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkEnabled)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCaption))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cmdCalc)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel10))
                                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(txtInfeas)
                                            .addComponent(txtResult, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(lblErrorMsg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEnabled)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(txtCaption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel12))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkScale))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdCalc)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtResult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtInfeas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblErrorMsg)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCalcActionPerformed
        lblErrorMsg.setText("");
        ScriptEngine engine = JEPlusProjectV2.getScript_Engine();
        try {
            engine.eval(this.txaInputVars.getText());
            Double res = (Double)engine.eval(this.txaFormula.getText());
            txtResult.setText(res.toString());
            txtInfeas.setText(Double.toString(Constraint.scale(res)));
        }catch (ScriptException spe) {
            logger.error("Error evaluating expression.", spe);
            lblErrorMsg.setText("Error evaluating formula! Check logs for details.");
        }
    }//GEN-LAST:event_cmdCalcActionPerformed

    private void chkEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEnabledActionPerformed
        if (DLActive) {
            Constraint.setEnabled(chkEnabled.isSelected());
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_chkEnabledActionPerformed

    private void chkScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScaleActionPerformed
        if (DLActive) {
            Constraint.setScaling(chkScale.isSelected());
            Project.setContentChanged(true);
            HostTree.update(HostTree.getGraphics());
        }
    }//GEN-LAST:event_chkScaleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Canvas canvas1;
    private javax.swing.JCheckBox chkEnabled;
    private javax.swing.JCheckBox chkScale;
    private javax.swing.JButton cmdCalc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblErrorMsg;
    private javax.swing.JTextArea txaFormula;
    private javax.swing.JTextArea txaInputVars;
    private javax.swing.JTextField txtCaption;
    private javax.swing.JTextField txtInfeas;
    private javax.swing.JTextField txtLB;
    private javax.swing.JTextField txtMax;
    private javax.swing.JTextField txtMin;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtResult;
    private javax.swing.JTextField txtUB;
    private javax.swing.JTextField txtWeight;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
