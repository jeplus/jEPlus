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
package jeplus.gui;

import java.awt.Color;
import java.io.File;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import jeplus.EPlusConfig;
import jeplus.JEPlusProjectV2;
import jeplus.data.ParameterItemV2;
import jeplus.util.CsvUtil;

/**
 *
 * @author yzhang
 */
public class JPanel_ParameterTable extends javax.swing.JPanel implements TitledJPanel, TableModelListener {

    class ValidityCellRenderer extends DefaultTableCellRenderer {
        boolean editable = true;
        public ValidityCellRenderer (boolean editable) { 
            super(); 
            if (! editable) {
                this.setBackground(Color.lightGray);
            }
        }
        public void setValidity (boolean valid) {
            if (valid) {
                this.setForeground(Color.black);
            }else {
                this.setForeground(Color.red);
            }
        }
    }
    
    protected String Title = "Parameter Table";
    protected ParameterItemV2 CurrentItem = null;
    protected ParamTableModel ParamTableModel = null;
    protected JTable jTableParams = null;
    private DocumentListener DL = null;
    private boolean DLactive = true;
    protected JEPlusProjectV2 Project;

    
    /** Creates new form JPanel_ParameterTree */
    public JPanel_ParameterTable() {
        initComponents();
        setProject(null);
    }
    
    /** 
     * Creates new form JPanel_ParameterTree
     * @param project 
     */
    public JPanel_ParameterTable(JEPlusProjectV2 project) {
        initComponents();
        setProject(Project);
    }

    /**
     * Get title of this panel
     * @return Title of this panel instance
     */
    @Override
    public String getTitle() {
        return Title;
    }

    /**
     * Set title to this panel
     * @param Title new title
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    /**
     * Set the root of the parameter tree
     * @param project the jEPlus project containing the tree
     */
    public final void setProject (JEPlusProjectV2 project) {
        Project = project;
        if (Project != null) {
            ParamTableModel = new ParamTableModel(Project.getParameters(), Project);
            initParamTable();
        }
    }

    /**
     * initialises the parameter tree, by setting up tree nodes and tree model
     */
    protected void initParamTable () {
        cboParamType.setModel(new DefaultComboBoxModel<> (ParameterItemV2.PType.values()));
        cboType.setModel(new DefaultComboBoxModel<> (ParameterItemV2.VType.values()));
        // Set up table
        jTableParams = new JTable(ParamTableModel);
        jTableParams.setDefaultRenderer(String.class, new ValidityCellRenderer(true));
        jTableParams.setDefaultRenderer(Integer.class, new ValidityCellRenderer(false));
        // Column sizes
        TableColumn column = null;
        JComboBox comboBox = null;
        for (int i = 0; i < 7; i++) {
            column = jTableParams.getColumnModel().getColumn(i);
            if (i == 0) { //#
                column.setMinWidth(30);
                column.setPreferredWidth(20);
                column.setMaxWidth(50);
            } else if (i == 1) { // P-Type
                column.setPreferredWidth(70); 
                comboBox = new JComboBox(new DefaultComboBoxModel(ParameterItemV2.PType.values()));
                column.setCellEditor(new DefaultCellEditor(comboBox));
            } else if (i == 2) { // P-ID
                column.setPreferredWidth(30); //# column is small
            } else if (i == 3) { // Search Tag
                column.setPreferredWidth(100); //# column is small
            } else if (i == 4) { // V-Type
                column.setPreferredWidth(60); //# column is small
                comboBox = new JComboBox(new DefaultComboBoxModel(ParameterItemV2.VType.values()));
                column.setCellEditor(new DefaultCellEditor(comboBox));
            } else if (i == 5) { // Values string
                column.setPreferredWidth(200); //# column is small
            } else if (i == 6) { // N
                column.setMinWidth(30);
                column.setPreferredWidth(25);
                column.setMaxWidth(50);
            } else {
                column.setPreferredWidth(120);
            }
        }
        // Selection listener
        jTableParams.getSelectionModel().addListSelectionListener(new ListSelectionListener (){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && jTableParams.getSelectedRow() != -1) {
                    CurrentItem = Project.getParameters().get(jTableParams.getSelectedRow());
                    displayParamDetails();
                }
            }
        });        
        ParamTableModel.addTableModelListener(this);
        this.jScroll.setViewportView(jTableParams);
        // Set listeners to text fields
        DL = new DocumentListener () {
            Document DocShortName = txtShortName.getDocument();
            Document DocName = txtName.getDocument();
            Document DocDescript = txtDescript.getDocument();
            Document DocSearchString = txtSearchString.getDocument();
            Document DocAltValues = txtAltValues.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (DLactive) {
                    Document src = e.getDocument();
                    if(src == DocShortName) {
                        CurrentItem.setID(txtShortName.getText());
                    }else if (src == DocName) {
                        CurrentItem.setName(txtName.getText());
                    }else if (src == DocDescript) {
                        CurrentItem.setDescription(txtDescript.getText());
                    }else if (src == DocSearchString) {
                        CurrentItem.setSearchString(txtSearchString.getText());
                    }else if (src == DocAltValues) {
                        CurrentItem.setValuesString(txtAltValues.getText());
                        txpPreview.setText(getAltValuesPreview());
                        resetAltValueNumbers();
                    }
                    Project.setContentChanged(true);
                    ParamTableModel.fireTableDataChanged();
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
        txtShortName.getDocument().addDocumentListener(DL);
        txtName.getDocument().addDocumentListener(DL);
        txtDescript.getDocument().addDocumentListener(DL);
        txtSearchString.getDocument().addDocumentListener(DL);
        txtAltValues.getDocument().addDocumentListener(DL);

    }

    /**
     * Updates the preview of the alternative values of the parameter item
     * @return A string to be set to the label where the preview is displayed
     */
    protected String getAltValuesPreview () {
        StringBuilder buf = new StringBuilder("{");
        if (CurrentItem != null) {
            String [] list = CurrentItem.getAlternativeValues(Project);
            if (list != null) {
                for (int i=0; i<list.length; i++) {
                    if (i > 0) buf.append(", ");
                    buf.append(list[i]);
                }
            } else {
                buf.append("!Error!");
            }
        }else {
            buf.append("...");
        }
        buf.append("}");
        return buf.toString();
    }

    /**
     * Displays details of the selected parameter item in the relevant text fields
     * and combo boxes. If no item is selected, this function disables the text
     * fields and boxes.
     */
    protected void displayParamDetails() {
        DLactive = false;
        if (CurrentItem != null) {
            txtName.setEnabled(true);
            txtShortName.setEnabled(true);
            txtDescript.setEnabled(true);
            txtSearchString.setEnabled(true);
            txtAltValues.setEnabled(true);
            cboParamType.setEnabled(true);
            cboType.setEnabled(true);
            cboFixValue.setEnabled(true);

            txtName.setText(CurrentItem.getName());
            txtShortName.setText(CurrentItem.getID());
            txtDescript.setText(CurrentItem.getDescription());
            txtSearchString.setText(CurrentItem.getSearchString());
            txtAltValues.setText(CurrentItem.getValuesString());
            cboParamType.setSelectedItem(CurrentItem.getParamType());
            cboType.setSelectedItem(CurrentItem.getType());
            txpPreview.setText(getAltValuesPreview());
            resetAltValueNumbers ();
            cboFixValue.setSelectedIndex(CurrentItem.getSelectedAltValue());

        } else {
            txtName.setEnabled(false);
            txtShortName.setEnabled(false);
            txtDescript.setEnabled(false);
            txtSearchString.setEnabled(false);
            txtAltValues.setEnabled(false);
            cboParamType.setEnabled(false);
            cboType.setEnabled(false);
            cboFixValue.setEnabled(false);

            txtName.setText("");
            txtShortName.setText("");
            txtDescript.setText("");
            txtSearchString.setText("");
            txtAltValues.setText("");
            cboParamType.setSelectedIndex(0);
            cboType.setSelectedIndex(0);
            txpPreview.setText("");
            cboFixValue.setSelectedIndex(0);
        }
        DLactive = true;
    }

    private void resetAltValueNumbers () {
        if (CurrentItem != null) {
            String [] idx = new String [CurrentItem.getNAltValues(Project) + 1];
            idx[0] = "-";
            for (int i=1; i<idx.length; i++) {
                idx[i] = Integer.toString(i);
            }
            cboFixValue.setModel(new DefaultComboBoxModel (idx));
        }else {
            cboFixValue.setModel(new DefaultComboBoxModel (new String [] {"-"}));
        }
    }


    /**
     * Remove the selected node from the tree
     */
    public void importParameterItems() {
        // Select a file to open
        JFileChooser fc = new JFileChooser(this.Project.getBaseDir());
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.CSV));
        fc.setSelectedFile(new File(""));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            // import table
            String [][] table = CsvUtil.parseCSVwithComments(file);
            int counter = 0;
            if (table != null) {
                for (String[] row : table) {
                    if (row.length >= 8) {
                        this.Project.getParameters().add(new ParameterItemV2 (row));
                        counter ++;
                    }
                }
                // Mark content changed
                this.Project.setContentChanged(true);
            }
            JOptionPane.showMessageDialog(
                this,
                "Imported " + counter + " parameters from " + file.getAbsolutePath(),
                "Info",
                JOptionPane.CLOSED_OPTION
            );
            // Set current and selected item
            int last = this.Project.getParameters().size() - 1;
            CurrentItem = this.Project.getParameters().get(last);
            this.jTableParams.setRowSelectionInterval(last, last);
            ParamTableModel.fireTableDataChanged();
        }
        fc.setFileFilter(null);
    }

    /**
     * Delete the whole branch of the selected node from the tree
     * @return the root node of the branch being removed
     */
    public ParameterItemV2 removeParameterItem () {
        int n = JOptionPane.showConfirmDialog(
            this,
            "Are you sure that you want to delete the selected parameter?",
            "Deleting parameter",
            JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION) {
            return null;
        }
        
        int idx = this.Project.getParameters().indexOf(CurrentItem);
        ParameterItemV2 deleted = null;
        if (idx >= 0) {
            deleted = this.Project.getParameters().remove(idx);
            Project.setContentChanged(true);
            CurrentItem = this.Project.getParameters().get(Math.max(0, idx-1));
            ParamTableModel.fireTableDataChanged();
        }
        return deleted;
    }

    /**
     * Make a copy of the selected item and insert it at the same level.
     */
    public void copyParameterItem() {
        if (CurrentItem != null) {
            ParameterItemV2 child = new ParameterItemV2 (CurrentItem);
            child.setID("P" + this.Project.getParameters().size());
            addParameterItem(child);
        }
    }

    /**
     * Add a new item to the tree
     * @param child The ParameterItem to be store the node-to-add
     */
    public void addParameterItem(ParameterItemV2 child) {
        this.Project.getParameters().add(child);
        Project.setContentChanged(true);
        this.CurrentItem = child;
        int last = this.Project.getParameters().size() - 1;
        this.jTableParams.setRowSelectionInterval(last, last);
        this.displayParamDetails();    
        ParamTableModel.fireTableDataChanged();
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtDescript = new javax.swing.JTextField();
        cboType = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtShortName = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtSearchString = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtAltValues = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txpPreview = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        cboFixValue = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        cboParamType = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jScroll = new javax.swing.JScrollPane();
        cmdAdd = new javax.swing.JButton();
        cmdImportParams = new javax.swing.JButton();
        cmdDuplicate = new javax.swing.JButton();
        cmdRemove = new javax.swing.JButton();

        jLabel1.setText("Version requirement");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Parameter item", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(102, 102, 102))); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Name: ");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel19.setText("Description: ");

        txtName.setText("parameter1");
        txtName.setToolTipText("Memorable name of the parameter");
        txtName.setEnabled(false);
        txtName.setMinimumSize(new java.awt.Dimension(120, 20));

        txtDescript.setToolTipText("Description of the parameter");
        txtDescript.setEnabled(false);

        cboType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Integer", "Double", "Discrete" }));
        cboType.setToolTipText("Parameter value type");
        cboType.setEnabled(false);
        cboType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTypeActionPerformed(evt);
            }
        });

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel20.setText("Type: ");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel21.setText("ID: ");

        txtShortName.setText("PAR1");
        txtShortName.setToolTipText("Parameter ID is used in Job ID. Use short names e.g. p1, p2...");
        txtShortName.setEnabled(false);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel22.setText("Search tag: ");

        txtSearchString.setToolTipText("The search tag to be put in the model template");
        txtSearchString.setEnabled(false);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel24.setText("Values: ");

        txtAltValues.setToolTipText("Specify a list of alternative values. For detailed syntax, please refer to users guide.");
        txtAltValues.setEnabled(false);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel26.setText("Preview: ");

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBar(null);
        jScrollPane2.setMaximumSize(new java.awt.Dimension(32767, 200));

        txpPreview.setEditable(false);
        txpPreview.setContentType("text/html"); // NOI18N
        txpPreview.setToolTipText("Preview of the list of alternative values. Please note if sampling (@sample) is used, the preview list is NOT that to be used in simulations.");
        jScrollPane2.setViewportView(txpPreview);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Fix on the ");

        cboFixValue.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "1", "2", "3", "4", "" }));
        cboFixValue.setToolTipText("Select the i-th value for this batch of simulations. This is useful for maintain consistent job indexes for partial runs.");
        cboFixValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFixValueActionPerformed(evt);
            }
        });

        jLabel3.setText("-th value in this batch");

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel23.setText("Value type: ");

        cboParamType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Parametrics" }));
        cboParamType.setToolTipText("Parameter type");
        cboParamType.setEnabled(false);
        cboParamType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboParamTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtShortName))
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboParamType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAltValues, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDescript)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(cboFixValue, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtSearchString)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtShortName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(cboParamType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDescript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAltValues, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(0, 34, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboFixValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        jPanel1.setMinimumSize(new java.awt.Dimension(0, 150));
        jPanel1.setName(""); // NOI18N
        jPanel1.setRequestFocusEnabled(false);

        jScroll.setToolTipText("Drag&Drop to edit the parameter tree. Hold 'Ctrl' key to copy a branch.");

        cmdAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/plus1.png"))); // NOI18N
        cmdAdd.setToolTipText("Add a new item under the current item.");
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        cmdImportParams.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_go.png"))); // NOI18N
        cmdImportParams.setToolTipText("Import parameters from CSV and append to the current list");
        cmdImportParams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdImportParamsActionPerformed(evt);
            }
        });

        cmdDuplicate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_copy.png"))); // NOI18N
        cmdDuplicate.setToolTipText("Make a copy of the current item.");
        cmdDuplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDuplicateActionPerformed(evt);
            }
        });

        cmdRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/minus.png"))); // NOI18N
        cmdRemove.setToolTipText("Remove the selected item.");
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScroll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmdImportParams, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdDuplicate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(cmdAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScroll)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(cmdImportParams)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdDuplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdRemove)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        this.addParameterItem(new ParameterItemV2(Project.getParameters().size()));
}//GEN-LAST:event_cmdAddActionPerformed

    private void cmdRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveActionPerformed
        removeParameterItem();
}//GEN-LAST:event_cmdRemoveActionPerformed

    private void cmdDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDuplicateActionPerformed
        copyParameterItem();
}//GEN-LAST:event_cmdDuplicateActionPerformed

    private void cboTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTypeActionPerformed
        CurrentItem.setType((ParameterItemV2.VType)cboType.getSelectedItem());
        // Trigger ValueStringChanged
        CurrentItem.setValuesString(CurrentItem.getValuesString());
        txpPreview.setText(getAltValuesPreview());
        this.resetAltValueNumbers();
        if (DLactive) Project.setContentChanged(true);
}//GEN-LAST:event_cboTypeActionPerformed

    private void cmdImportParamsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdImportParamsActionPerformed
        importParameterItems();
    }//GEN-LAST:event_cmdImportParamsActionPerformed

    private void cboFixValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFixValueActionPerformed
        CurrentItem.setSelectedAltValue(cboFixValue.getSelectedIndex());
        if (DLactive) Project.setContentChanged(true);
    }//GEN-LAST:event_cboFixValueActionPerformed

    private void cboParamTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboParamTypeActionPerformed
        CurrentItem.setParamType((ParameterItemV2.PType)cboParamType.getSelectedItem());
        if (DLactive) Project.setContentChanged(true);
    }//GEN-LAST:event_cboParamTypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboFixValue;
    private javax.swing.JComboBox cboParamType;
    private javax.swing.JComboBox cboType;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdDuplicate;
    private javax.swing.JButton cmdImportParams;
    private javax.swing.JButton cmdRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane txpPreview;
    private javax.swing.JTextField txtAltValues;
    private javax.swing.JTextField txtDescript;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSearchString;
    private javax.swing.JTextField txtShortName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent tme) {
        int row = tme.getFirstRow();
        int column = tme.getColumn();
        ParamTableModel model = (ParamTableModel)tme.getSource();
//        String columnName = model.getColumnName(column);
//        CurrentItem = this.Project.getParameters().get(row);
        if (column >= 0) {
            displayParamDetails();
        }
    }

}
