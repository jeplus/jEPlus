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

import java.util.Enumeration;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.tree.*;
import jeplus.JEPlusProjectV2;
import jeplus.data.ParameterItemV2;
import jeplus.util.DNDTree;

/**
 *
 * @author yzhang
 */
public class JPanel_ParameterTree extends javax.swing.JPanel implements TitledJPanel {

    protected String Title = "Parameter Tree";
    protected ParameterItemV2 CurrentItem = null;
    protected DefaultMutableTreeNode ParamTreeRoot = null;
    protected DefaultTreeModel ParamTreeModel = null;
    protected JTree jTreeParams = null;
    private DocumentListener DL = null;
    private boolean DLactive = true;
    /** Reference to project in order to pass on access to parameter items */
    protected JEPlusProjectV2 Project;

    
    /** Creates new form JPanel_ParameterTree */
    public JPanel_ParameterTree() {
        initComponents();
        setParameterTree(null);
    }
    
    /** 
     * Creates new form JPanel_ParameterTree
     * @param project 
     */
    public JPanel_ParameterTree(JEPlusProjectV2 project) {
        initComponents();
        setParameterTree(Project);
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
    public final void setParameterTree (JEPlusProjectV2 project) {
        Project = project;
        if (Project != null) {
            ParamTreeRoot = Project.getParamTree();
            initParamTree();
        }
    }

    /**
     * Get the parameter tree
     * @return Root node of the current tree
     */
    public DefaultMutableTreeNode getParameterTree () {
        return ParamTreeRoot;
    }

    /**
     * initialises the parameter tree, by setting up tree nodes and tree model
     */
    protected void initParamTree () {
        if (ParamTreeRoot == null) {
            ParamTreeRoot = new DefaultMutableTreeNode(new ParameterItemV2());
        }
        ParamTreeModel = new DefaultTreeModel(ParamTreeRoot);
        // Set tree
        jTreeParams = new DNDTree(DNDTree.createTree());
        jTreeParams.setModel(ParamTreeModel);
        jTreeParams.setEditable(false);
        jTreeParams.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeParams.setShowsRootHandles(true);
        jTreeParams.setExpandsSelectedPaths(true);
        jTreeParams.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeParamsValueChanged(evt);
            }
        });
        jTreeParams.setSelectionPath(new TreePath (ParamTreeRoot.getLastLeaf().getPath()));
        this.jScroll.setViewportView(jTreeParams);
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
                    jTreeParams.update(jTreeParams.getGraphics());
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
     * Handles the event that a new tree node is selected
     * @param evt not in use
     */
    private void jTreeParamsValueChanged(javax.swing.event.TreeSelectionEvent evt) {
    //Returns the last path element of the selection.
        //This method is useful only when the selection model allows a single selection.
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           jTreeParams.getLastSelectedPathComponent();

        if (node == null) {
            //Nothing is selected.
            CurrentItem = null;
        }else {
            // Selection available
            Object nodeInfo = node.getUserObject();
            if (node.isLeaf()) {
                CurrentItem = (ParameterItemV2)nodeInfo;
            } else {
                CurrentItem = (ParameterItemV2)nodeInfo;
            }
        }
        displayParamDetails();
    }

    /**
     * Updates the preview of the alternative values of the parameter item
     * @return A string to be set to the label where the preview is displayed
     */
    protected String getAltValuesPreview () {
        StringBuilder buf = new StringBuilder("{");
        if (CurrentItem != null) {
            String [] list = CurrentItem.getAlternativeValues();
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
//        // Disable text doc listeners
//        if (DL != null) {
//            txtShortName.getDocument().removeDocumentListener(DL);
//            txtName.getDocument().removeDocumentListener(DL);
//            txtDescript.getDocument().removeDocumentListener(DL);
//            txtSearchString.getDocument().removeDocumentListener(DL);
//            txtAltValues.getDocument().removeDocumentListener(DL);
//        }
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
            cboParamType.setSelectedIndex(CurrentItem.getParamType());
            cboType.setSelectedIndex(CurrentItem.getType());
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
//        // Reset text doc listeners
//        if (DL != null) {
//            txtShortName.getDocument().addDocumentListener(DL);
//            txtName.getDocument().addDocumentListener(DL);
//            txtDescript.getDocument().addDocumentListener(DL);
//            txtSearchString.getDocument().addDocumentListener(DL);
//            txtAltValues.getDocument().addDocumentListener(DL);
//        }
        DLactive = true;
    }

    /**
     * Reads information from the text fields and combo-boxes to update the
     * currently selected parameter item
     */
    protected void updateParamDetails() {
        if (CurrentItem != null) {
            CurrentItem.setName(txtName.getText().trim());
            CurrentItem.setID(txtShortName.getText().trim());
            CurrentItem.setDescription(txtDescript.getText().trim());
            CurrentItem.setSearchString(txtSearchString.getText().trim());
            txpPreview.setText(getAltValuesPreview());
            resetAltValueNumbers ();
            CurrentItem.setValuesString(txtAltValues.getText().trim());
            CurrentItem.setType(cboType.getSelectedIndex());
        }
        //??ParamTreeModel.nodeChanged(ParamTreeRoot);
        //jTreeParams.repaint();
    }

    private void resetAltValueNumbers () {
        if (CurrentItem != null) {
            String [] idx = new String [CurrentItem.getNAltValues() + 1];
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
     * Move the selected item up one level
     * @return The node being moved
     */
    public DefaultMutableTreeNode moveUpParameterItem() {
        DefaultMutableTreeNode node, parent;
        TreePath path = jTreeParams.getSelectionPath();

        if (path != null) {
            int len = path.getPathCount();
            Object [] paths = path.getPath();
            if (len >= 3) {
                node = (DefaultMutableTreeNode)(path.getLastPathComponent());
                ParamTreeModel.removeNodeFromParent(node);
                path = path.getParentPath().getParentPath();
                parent = (DefaultMutableTreeNode)paths[len-3];
                ParamTreeModel.insertNodeInto(node, parent, parent.getChildCount());
                jTreeParams.scrollPathToVisible(path);
                jTreeParams.setSelectionPath(path);
                return node;
            }
        }
        return null;
    }

    /**
     * Remove the selected node from the tree
     * @return the node being removed
     */
    public DefaultMutableTreeNode removeParameterItem() {
        DefaultMutableTreeNode node, parent;
        TreePath path = jTreeParams.getSelectionPath();

        if (path != null) {
            node = (DefaultMutableTreeNode)(path.getLastPathComponent());
            path = path.getParentPath();
            if (path != null) {
                // graft children of the node-to-remove to the parent
                parent = (DefaultMutableTreeNode)(path.getLastPathComponent());
                for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                    ParamTreeModel.insertNodeInto((MutableTreeNode)e.nextElement(), parent,
                                 parent.getChildCount());
                }
                ParamTreeModel.removeNodeFromParent(node);
                jTreeParams.scrollPathToVisible(path);
                jTreeParams.setSelectionPath(path);
                return node;
            }
        }
        return null;
    }

    /**
     * Delete the whole branch of the selected node from the tree
     * @return the root node of the branch being removed
     */
    public DefaultMutableTreeNode deleteParameterBranch () {
        int n = JOptionPane.showConfirmDialog(
            this,
            "Are you sure that you want to delete the whole branch?",
            "Deleting branch",
            JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION) {
            return null;
        }

        DefaultMutableTreeNode node;
        TreePath path = jTreeParams.getSelectionPath();

        if (path != null) {
            node = (DefaultMutableTreeNode)(path.getLastPathComponent());
            path = path.getParentPath();
            if (path != null) {
                ParamTreeModel.removeNodeFromParent(node);
                jTreeParams.scrollPathToVisible(path);
                jTreeParams.setSelectionPath(path);
                return node;
            }else { // if selection is the root
                this.ParamTreeRoot.removeAllChildren();
                this.ParamTreeRoot.setUserObject(new ParameterItemV2());
                this.initParamTree();
                return this.ParamTreeRoot;
            }
        }
        return null;
    }

    /**
     * Make a copy of the selected item and insert it at the same level.
     * @return The copy of the item being inserted
     */
    public DefaultMutableTreeNode copyParameterItem() {
        if (CurrentItem != null) {
            ParameterItemV2 child = new ParameterItemV2 (CurrentItem);
            DefaultMutableTreeNode parentNode;
            TreePath parentPath = jTreeParams.getSelectionPath();

            if (parentPath != null) {
                parentPath = parentPath.getParentPath();
                if (parentPath != null) {
                    parentNode = (DefaultMutableTreeNode)
                                (parentPath.getLastPathComponent());
                } else {
                    // selection is the root node
                    parentNode = ParamTreeRoot;
                }
            } else {
                //There is no selection. Default to the root node.
                parentNode = null;
            }
            return addObject(parentNode, child, true);
        }
        return null;
    }

    /**
     * Add a new item to the tree
     * @param child The ParameterItem to be store the node-to-add
     * @return The new node
     */
    public DefaultMutableTreeNode addParameterItem(ParameterItemV2 child) {
        DefaultMutableTreeNode parentNode;
        TreePath parentPath = jTreeParams.getSelectionPath();

        if (parentPath == null) {
            //There is no selection. Default to the root node.
            parentNode = ParamTreeRoot;
        } else {
            parentNode = (DefaultMutableTreeNode)
                         (parentPath.getLastPathComponent());
        }
        return addObject(parentNode, child, true);
    }

    /**
     * Utility function for inserting a node into the tree
     * @param parent The parent location where the child is to be inserted
     * @param child The child user object to be added
     * @param shouldBeVisible Adjust display to show the new insertion if set true
     * @return The new node of the tree
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child,
                                            boolean shouldBeVisible) {
        if (parent == null) return null;

        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child);

        ParamTreeModel.insertNodeInto(childNode, parent,
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            TreePath path = new TreePath(childNode.getPath());
            jTreeParams.scrollPathToVisible(path);
            jTreeParams.setSelectionPath(path);
        }
        return childNode;
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
        cmdMoveUp = new javax.swing.JButton();
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
        cmdDeleteBranch = new javax.swing.JButton();
        cmdDuplicate = new javax.swing.JButton();
        cmdRemove = new javax.swing.JButton();

        jLabel1.setText("Version requirement");

        cmdMoveUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/swap_up.png"))); // NOI18N
        cmdMoveUp.setToolTipText("Move the selected item up one level.");
        cmdMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdMoveUpActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Parameter item", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(102, 102, 102))); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Name: ");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel19.setText("Description: ");

        txtName.setText("parameter1");
        txtName.setToolTipText("Memorable name of the parameter");
        txtName.setEnabled(false);
        txtName.setMinimumSize(new java.awt.Dimension(120, 20));
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNameFocusLost(evt);
            }
        });

        txtDescript.setToolTipText("Description of the parameter");
        txtDescript.setEnabled(false);
        txtDescript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescriptActionPerformed(evt);
            }
        });
        txtDescript.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDescriptFocusLost(evt);
            }
        });

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
        txtShortName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtShortNameActionPerformed(evt);
            }
        });
        txtShortName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtShortNameFocusLost(evt);
            }
        });

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel22.setText("Search tag: ");

        txtSearchString.setToolTipText("The search tag to be put in the model template");
        txtSearchString.setEnabled(false);
        txtSearchString.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchStringActionPerformed(evt);
            }
        });
        txtSearchString.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSearchStringFocusLost(evt);
            }
        });

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel24.setText("Values: ");

        txtAltValues.setToolTipText("Specify a list of alternative values. For detailed syntax, please refer to users guide.");
        txtAltValues.setEnabled(false);
        txtAltValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAltValuesActionPerformed(evt);
            }
        });
        txtAltValues.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAltValuesFocusLost(evt);
            }
        });

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel26.setText("Preview: ");

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBar(null);

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
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboParamType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                    .addComponent(txtAltValues, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(txtSearchString, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(cboFixValue, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(txtDescript, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE))
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
                    .addComponent(jLabel26)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboFixValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jScroll.setToolTipText("Drag&Drop to edit the parameter tree. Hold 'Ctrl' key to copy a branch.");

        cmdAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/plus1.png"))); // NOI18N
        cmdAdd.setToolTipText("Add a new item under the current item.");
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        cmdDeleteBranch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/cross.png"))); // NOI18N
        cmdDeleteBranch.setToolTipText("Delete the whole branch");
        cmdDeleteBranch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteBranchActionPerformed(evt);
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdRemove, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(cmdDuplicate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(cmdAdd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdDeleteBranch, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(cmdAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdDuplicate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdRemove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdDeleteBranch)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScroll)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        ParameterItemV2 item = new ParameterItemV2();
        this.addParameterItem(item);
        this.CurrentItem = item;
        this.displayParamDetails();
}//GEN-LAST:event_cmdAddActionPerformed

    private void cmdRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveActionPerformed
        removeParameterItem();
}//GEN-LAST:event_cmdRemoveActionPerformed

    private void cmdMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdMoveUpActionPerformed
        moveUpParameterItem();
}//GEN-LAST:event_cmdMoveUpActionPerformed

    private void cmdDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDuplicateActionPerformed
        copyParameterItem();
}//GEN-LAST:event_cmdDuplicateActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        //updateParamDetails();
}//GEN-LAST:event_txtNameActionPerformed

    private void txtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusLost
        //updateParamDetails();
}//GEN-LAST:event_txtNameFocusLost

    private void txtDescriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescriptActionPerformed
        //updateParamDetails();
}//GEN-LAST:event_txtDescriptActionPerformed

    private void txtDescriptFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDescriptFocusLost
        //updateParamDetails();
}//GEN-LAST:event_txtDescriptFocusLost

    private void cboTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTypeActionPerformed
        CurrentItem.setType(cboType.getSelectedIndex());
        // Trigger ValueStringChanged
        CurrentItem.setValuesString(CurrentItem.getValuesString());
        txpPreview.setText(getAltValuesPreview());
        this.resetAltValueNumbers();
}//GEN-LAST:event_cboTypeActionPerformed

    private void txtShortNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtShortNameActionPerformed
        //updateParamDetails();
}//GEN-LAST:event_txtShortNameActionPerformed

    private void txtShortNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtShortNameFocusLost
        //updateParamDetails();
}//GEN-LAST:event_txtShortNameFocusLost

    private void txtSearchStringActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchStringActionPerformed
        //updateParamDetails();
}//GEN-LAST:event_txtSearchStringActionPerformed

    private void txtSearchStringFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSearchStringFocusLost
        //updateParamDetails();
}//GEN-LAST:event_txtSearchStringFocusLost

    private void txtAltValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAltValuesActionPerformed
        //updateParamDetails();
}//GEN-LAST:event_txtAltValuesActionPerformed

    private void txtAltValuesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAltValuesFocusLost
        //updateParamDetails();
}//GEN-LAST:event_txtAltValuesFocusLost

    private void cmdDeleteBranchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteBranchActionPerformed
        deleteParameterBranch();
    }//GEN-LAST:event_cmdDeleteBranchActionPerformed

    private void cboFixValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFixValueActionPerformed
        CurrentItem.setSelectedAltValue(cboFixValue.getSelectedIndex());
    }//GEN-LAST:event_cboFixValueActionPerformed

    private void cboParamTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboParamTypeActionPerformed
        CurrentItem.setParamType(cboParamType.getSelectedIndex());
    }//GEN-LAST:event_cboParamTypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboFixValue;
    private javax.swing.JComboBox cboParamType;
    private javax.swing.JComboBox cboType;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdDeleteBranch;
    private javax.swing.JButton cmdDuplicate;
    private javax.swing.JButton cmdMoveUp;
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

}
