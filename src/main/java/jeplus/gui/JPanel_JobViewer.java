/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@gmail.com>               *
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
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import jeplus.EPlusBatch;
import jeplus.EPlusTask;
import jeplus.JEPlusProject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class JPanel_JobViewer extends javax.swing.JPanel {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JPanel_JobViewer.class);

    protected EPlusBatch Manager = null;
    protected EPlusTask [] Jobs = null;
    protected boolean [] Selections = null;
    
    TableModelListener tblListener = new TableModelListener () {
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 3) {
                TableModel model = (TableModel)e.getSource();
                // String columnName = model.getColumnName(column);
                Boolean data = (Boolean) model.getValueAt(row, column);
                Selections [row] = data;
            }
        }
    };
    
    /**
     * Creates new form JPanel_JobViewer
     */
    public JPanel_JobViewer(EPlusBatch manager) {
        setJobManager(manager);
        initComponents();
    }
    
    protected final void setJobManager (EPlusBatch manager) {
        Manager = manager;
        Jobs = Manager.getJobQueue().toArray(new EPlusTask[0]);
    }
    
    protected void updateJobSummaryTable (String filter, boolean showParams, boolean showReport, boolean showResult) {
        String [] columns = getJobSummaryTableHeader (showParams, showReport, showResult);
        DefaultTableModel tableModel = new DefaultTableModel (columns, 0);
        for (int i=0; i<Jobs.length; i++) {
            Object [] row = {new Integer (i+1), Jobs[i].getJobID(), "unknown", Selections[i]};
            tableModel.addRow(row);
        }
        
        this.tblJobs.setModel(tableModel);
        tableModel.addTableModelListener(tblListener);
        tblJobs.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblJobs.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor (new JCheckBox ()));
        tblJobs.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer () {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Boolean) {
                    JCheckBox box = new JCheckBox ();
                    box.setSelected((Boolean)value);
                    box.setBackground(Color.WHITE);
                    return box;
                }
                JLabel lbl = new JLabel();
                lbl.setText(value == null ? "" : value.toString());
                return lbl;
            }
        });
        tblJobs.getColumnModel().getColumn(1).setPreferredWidth(20);
        tblJobs.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblJobs.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblJobs.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblJobs.setPreferredScrollableViewportSize(tblJobs.getPreferredSize());
    }

    /**
     * Writes an index table of the current job queue, listing selected alt values
     * of each job against search strings
     */
    public String [] getJobSummaryTableHeader (boolean showParams, boolean showReport, boolean showResult) {
        String [] header = {"Select", "ID", "Job", "Summary"};
        ArrayList<String> hr = new ArrayList<>();
        hr.addAll(Arrays.asList(header));
        if (showParams) {
            
        }
        if (showReport) {
            
        }
        if (showResult) {
            
        }
        return hr.toArray(new String [0]);
    }

    /**
     * Writes an index table of the current job queue, listing selected alt values
     * of each job against search strings
     */
    public int getJobSummaryTable (String filter, boolean showParams, boolean showReport, boolean showResult) {
        int nResCollected = 0;
        try {
            // Header
            ArrayList<String> headers = new ArrayList <> ();
            headers.add("Id");
            headers.add("JobID");
            headers.add("WeatherFile");
            headers.add("ModelFile");
            headers.addAll(Manager.getBatchInfo().getSearchStrings());
            // Create a map for sorting alt values
            HashMap<String, String> map = new HashMap<> ();
            for (String str : headers) {
                map.put(str, "");
            }

            // Write table header
            StringBuffer buf = new StringBuffer(headers.get(0));
            for (int i = 1; i < headers.size(); i++) {
                buf.append(", ").append(headers.get(i));
            }
            
            // Print Jobs
            for (int i = 0; i < Jobs.length; i++) {
                // For each job, do:
                EPlusTask job = Manager.getJobQueue().get(i);
                if (job.getWorkEnv().getProjectType() == JEPlusProject.EPLUS) {
                    if (i==0) {
                        // Print table header
                    }
                    buf = new StringBuffer();
                    buf.append(i).append(", ");
                    buf.append(job.getJobID()).append(", ");
                    buf.append(job.getWorkEnv().getWeatherFile()).append(", ");
                    buf.append(job.getWorkEnv().getIDFTemplate()).append(", ");
                }else if (job.getWorkEnv().getProjectType() == JEPlusProject.TRNSYS){
                    if (i==0) {
                        // Print table header
                        buf.delete(11,24);
                    }
                    buf = new StringBuffer();
                    buf.append(i).append(", ");
                    buf.append(job.getJobID()).append(", ");
                    buf.append(job.getWorkEnv().getDCKTemplate()).append(", ");
                }else if (job.getWorkEnv().getProjectType() == JEPlusProject.INSEL){
                    if (i==0) {
                        // Print table header
                        buf.delete(11,24);
                    }
                    buf = new StringBuffer();
                    buf.append(i).append(", ");
                    buf.append(job.getJobID()).append(", ");
                    buf.append(job.getWorkEnv().getDCKTemplate()).append(", ");
                }
                for (int j=0; j<job.getSearchStringList().size(); j++) {
                    map.put(job.getSearchStringList().get(j), job.getAltValueList().get(j));
                }
                for (int j=0; j<Manager.getBatchInfo().getSearchStrings().size(); j++) {
                    buf.append(map.get(Manager.getBatchInfo().getSearchStrings().get(j)));
                    if (j < Manager.getBatchInfo().getSearchStrings().size()-1) buf.append(", ");
                }
                nResCollected ++;
            }
            // Done
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return nResCollected;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJobs = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();

        jLabel1.setText("Project summary: ");

        tblJobs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblJobs);

        jTextField1.setText("jTextField1");

        jButton1.setText("Apply");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(94, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Filter", jPanel2);

        jLabel2.setText("xEsoView Executable: ");

        jCheckBox1.setText("Show Parameters");

        jLabel3.setText("Table contents: ");

        jTextField2.setText("jTextField2");

        jButton2.setText("jButton2");

        jCheckBox2.setText("Show more job result details");

        jCheckBox3.setText("Show Simulation results");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox3)
                            .addComponent(jCheckBox2)
                            .addComponent(jCheckBox1))
                        .addGap(0, 382, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Options", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTable tblJobs;
    // End of variables declaration//GEN-END:variables
}
