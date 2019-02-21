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

import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import jeplus.JEPlusFrameMain;
import jeplus.data.RVX_CSVitem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class JPanel_CSVitmeEditor extends javax.swing.JPanel {

    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanel_CSVitmeEditor.class);

    JEPlusFrameMain MainGUI = null;
    JTree HostTree = null;
    protected String BaseDir = null;
    protected RVX_CSVitem Csv = null;
    protected DocumentListener DL = null;

    /**
     * Creates new form JPanel_RVXEditor
     */
    public JPanel_CSVitmeEditor() {
        initComponents();
    }

    /**
     * Creates new form JPanel_EPlusProjectFiles with parameters
     * @param frame
     * @param tree
     * @param basedir
     * @param csv
     */
    public JPanel_CSVitmeEditor(JEPlusFrameMain frame, JTree tree, String basedir, RVX_CSVitem csv) {
        initComponents();
        MainGUI = frame;
        HostTree = tree;
        BaseDir = basedir;
        setItem (csv);
        
        DL = new DocumentListener () {
            Document DocCsvFile = txtCsvFile.getDocument();
            Document DocReport = txtReport.getDocument();
            Document DocFor = txtFor.getDocument();
            Document DocTable = txtTable.getDocument();
            Document DocColumn = txtColumn.getDocument();
            Document DocRow = txtRow.getDocument();
            Document DocHeaders = txtHeaders.getDocument();
            Document DocResultTable = txtResultTable.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                Document src = e.getDocument();
                if(src == DocCsvFile) {
                    Csv.setSourceCsv(txtCsvFile.getText());
                }else if (src == DocReport) {
                    Csv.setFromReport(txtReport.getText());
                }else if (src == DocFor) {
                    Csv.setFromFor(txtFor.getText());
                }else if (src == DocTable) {
                    Csv.setFromTable(txtTable.getText());
                }else if (src == DocColumn) {
                    Csv.setFromColumn(txtColumn.getText());
                }else if (src == DocRow) {
                    Csv.setFromRow(txtRow.getText());
                }else if (src == DocHeaders) {
                    Csv.setColumnHeaders(txtHeaders.getText());
                }else if (src == DocResultTable) {
                    Csv.setTableName(txtResultTable.getText());
                }
                HostTree.update(HostTree.getGraphics());
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
        txtCsvFile.getDocument().addDocumentListener(DL);
        txtReport.getDocument().addDocumentListener(DL);
        txtFor.getDocument().addDocumentListener(DL);
        txtTable.getDocument().addDocumentListener(DL);
        txtColumn.getDocument().addDocumentListener(DL);
        txtRow.getDocument().addDocumentListener(DL);
        txtHeaders.getDocument().addDocumentListener(DL);
        txtResultTable.getDocument().addDocumentListener(DL);
    }
    
    protected final void setItem (RVX_CSVitem csv) {
        Csv = csv;
        txtCsvFile.setText(Csv.getSourceCsv());
        txtReport.setText(Csv.getFromReport());
        txtFor.setText(Csv.getFromFor());
        txtTable.setText(Csv.getFromTable());
        txtColumn.setText(Csv.getFromColumn());
        txtRow.setText(Csv.getFromRow());
        txtHeaders.setText(Csv.getColumnHeaders());
        txtResultTable.setText(Csv.getTableName());
        chkAggregate.setSelected(Csv.isUsedInCalc());
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
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtHeaders = new javax.swing.JTextField();
        txtCsvFile = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtReport = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTable = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtColumn = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtRow = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtFor = new javax.swing.JTextField();

        jLabel2.setText("Result Table:");

        txtResultTable.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtResultTable.setText("table");

        chkAggregate.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        chkAggregate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAggregateActionPerformed(evt);
            }
        });

        jLabel3.setText("Aggregate:");

        jLabel4.setText("EPlus CSV report: ");

        jLabel5.setText("Custom Headers: ");

        jLabel6.setText(".csv");

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Extract the specified field from EPlus's CSV tabular reports");
        jLabel1.setOpaque(true);

        txtHeaders.setText("Column [Unit]");

        txtCsvFile.setText("jTextField1");

        jLabel7.setText("From 'Report': ");

        txtReport.setText("jTextField1");

        jLabel8.setText("From Table: ");

        txtTable.setText("jTextField1");

        jLabel9.setText("From Column: ");

        txtColumn.setText("jTextField1");

        jLabel10.setText("From Row: ");

        txtRow.setText("jTextField1");

        jLabel11.setText("From 'For': ");

        txtFor.setText("jTextField1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtReport)
                            .addComponent(txtTable)
                            .addComponent(txtColumn)
                            .addComponent(txtHeaders)
                            .addComponent(txtRow)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6))
                                    .addComponent(chkAggregate)
                                    .addComponent(txtCsvFile, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtFor))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCsvFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtRow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtHeaders, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtResultTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chkAggregate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkAggregateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAggregateActionPerformed
        Csv.setUsedInCalc(chkAggregate.isSelected());
        HostTree.update(HostTree.getGraphics());
    }//GEN-LAST:event_chkAggregateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAggregate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txtColumn;
    private javax.swing.JTextField txtCsvFile;
    private javax.swing.JTextField txtFor;
    private javax.swing.JTextField txtHeaders;
    private javax.swing.JTextField txtReport;
    private javax.swing.JTextField txtResultTable;
    private javax.swing.JTextField txtRow;
    private javax.swing.JTextField txtTable;
    // End of variables declaration//GEN-END:variables
}
