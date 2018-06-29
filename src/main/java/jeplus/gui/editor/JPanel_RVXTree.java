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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import jeplus.gui.*;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.*;
import jeplus.JEPlusFrameMain;
import jeplus.data.RVX;
import jeplus.data.RVX_CSVitem;
import jeplus.data.RVX_Constraint;
import jeplus.data.RVX_Objective;
import jeplus.data.RVX_RVIitem;
import jeplus.data.RVX_SQLitem;
import jeplus.data.RVX_ScriptItem;
import jeplus.data.RVX_TRNSYSitem;
import jeplus.data.RVX_UserSuppliedItem;
import jeplus.data.RVX_UserVar;
import jeplus.gui.editor.RVXTreeModel.GroupType;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yzhang
 */
public class JPanel_RVXTree extends javax.swing.JPanel implements TitledJPanel {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JPanel_RVXTree.class);

    JEPlusFrameMain MainGUI = null;
    String BaseDir = null;
    protected RVX Rvx;

    protected String Title = "RVX Editor";
    protected DefaultMutableTreeNode CurrentGroup = null;
    protected RVXTreeModel RvxTreeModel = null;
    protected JTree jTreeRvx = null;

    
    /** Creates new form JPanel_ParameterTree */
    public JPanel_RVXTree() {
        initComponents();
        setRvx(new RVX());
    }
    
    /** 
     * Creates new form JPanel_ParameterTree
     * @param frame
     * @param rvx
     * @param basedir
     */
    public JPanel_RVXTree(JEPlusFrameMain frame, String basedir, RVX rvx) {
        initComponents();
        MainGUI = frame;
        BaseDir = basedir;
        setRvx(rvx);
    }
    
    public void setContents (JEPlusFrameMain frame, String basedir, RVX rvx) {
        MainGUI = frame;
        BaseDir = basedir;
        setRvx(rvx);
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
     * @param rvx the RVX object to be represented as the tree
     */
    public final void setRvx (RVX rvx) {
        Rvx = rvx;
        if (Rvx != null) {
            initRVXTree();
        }
    }

    /**
     * initialises the parameter tree, by setting up tree nodes and tree model
     */
    protected void initRVXTree () {
        RvxTreeModel = new RVXTreeModel (Rvx);
        jTreeRvx = new JTree (RvxTreeModel);
        jTreeRvx.setCellRenderer(RvxTreeModel.getRenderer());
        
        jTreeRvx.setEditable(false);
        jTreeRvx.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTreeRvx.setShowsRootHandles(true);
        jTreeRvx.setExpandsSelectedPaths(true);
        jTreeRvx.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeRvxValueChanged(evt);
            }
        });
//        jTreeRvx.setSelectionPath(new TreePath (RVXTreeRoot.getLastLeaf().getPath()));
        this.jScroll.setViewportView(jTreeRvx);
    }

    /**
     * Handles the event that a new tree node is selected
     * @param evt not in use
     */
    private void jTreeRvxValueChanged(javax.swing.event.TreeSelectionEvent evt) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTreeRvx.getLastSelectedPathComponent();
        if (node != null) {
            if (node.getUserObject() instanceof GroupType) {
                if (! node.isLeaf()) {
                    TreePath path = new TreePath(((DefaultMutableTreeNode)node.getFirstChild()).getPath());
                    jTreeRvx.scrollPathToVisible(path);
                    jTreeRvx.setSelectionPath(path);
                }
                CurrentGroup = node;
            }else {
                CurrentGroup = (DefaultMutableTreeNode)node.getParent();
                displayRvxItemDetails(node.getUserObject());
            }
        }
    }

    /**
     * Displays details of the selected parameter item in the relevant text fields
     * and combo boxes. If no item is selected, this function disables the text
     * fields and boxes.
     * @param item
     */
    protected void displayRvxItemDetails(Object item) {
        jplItemEditorHolder.removeAll();
        if (item instanceof RVX_RVIitem) {
            jplItemEditorHolder.add(new JPanel_RVIitmeEditor(MainGUI, jTreeRvx, BaseDir, (RVX_RVIitem)item));
        }else if (item instanceof RVX_SQLitem) {
            jplItemEditorHolder.add(new JPanel_SQLitmeEditor(MainGUI, jTreeRvx, BaseDir, (RVX_SQLitem)item));
        }else if (item instanceof RVX_ScriptItem) {
            jplItemEditorHolder.add(new JPanel_ScriptItmeEditor(MainGUI, jTreeRvx, BaseDir, (RVX_ScriptItem)item));
        }else if (item instanceof RVX_CSVitem) {
            jplItemEditorHolder.add(new JPanel_CSVitmeEditor(MainGUI, jTreeRvx, BaseDir, (RVX_CSVitem)item));
        }else if (item instanceof RVX_UserSuppliedItem) {
            jplItemEditorHolder.add(new JPanel_UserSuppliedItmeEditor(MainGUI, jTreeRvx, BaseDir, (RVX_UserSuppliedItem)item));
        }else if (item instanceof RVX_TRNSYSitem) {
            jplItemEditorHolder.add(new JPanel_TRNSYSitemEditor(MainGUI, jTreeRvx, BaseDir, (RVX_TRNSYSitem)item));
        }else if (item instanceof RVX_UserVar) {
            jplItemEditorHolder.add(new JPanel_UserVariableEditor(jTreeRvx, (RVX_UserVar)item));
        }else if (item instanceof RVX_Constraint) {
            jplItemEditorHolder.add(new JPanel_ConstraintEditor(jTreeRvx, (RVX_Constraint)item));
        }else if (item instanceof RVX_Objective) {
            jplItemEditorHolder.add(new JPanel_ObjectiveEditor(jTreeRvx, (RVX_Objective)item));
        }else {
            
        }
        jplItemEditorHolder.revalidate();
    }

    /**
     * Remove the selected node from the tree
     * @return the node being removed
     */
    public DefaultMutableTreeNode removeRvxItem() {
        TreePath path = jTreeRvx.getSelectionPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        path = path.getParentPath();
        if (node != null && CurrentGroup != null) {
            int idx = CurrentGroup.getIndex(node);
            if (idx >= 0) {
                ((RVXTreeModel.GroupType)CurrentGroup.getUserObject()).getList().remove(idx);
                RvxTreeModel.removeNodeFromParent(node);
                jTreeRvx.scrollPathToVisible(path);
                jTreeRvx.setSelectionPath(path);
                return node;
            }
        }
        return null;
    }

    /**
     * Add a new item to the tree
     * @return The new node
     */
    public DefaultMutableTreeNode addRvxItem() {
        if (CurrentGroup != null) {
            RVXTreeModel.GroupType curgroup = (RVXTreeModel.GroupType)CurrentGroup.getUserObject();
            if (curgroup.getItemClass() != null) {
                try {
                    Object newitem = curgroup.getItemClass().newInstance();
                    curgroup.getList().add(newitem);
                    return addObject(CurrentGroup, newitem, true);
                } catch (InstantiationException | IllegalAccessException ex) {
                    logger.error("Error", ex);
                }
            }
        }
        return null;
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

        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        RvxTreeModel.insertNodeInto(childNode, parent,
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            TreePath path = new TreePath(childNode.getPath());
            jTreeRvx.scrollPathToVisible(path);
            jTreeRvx.setSelectionPath(path);
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

        jplItemEditorHolder = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScroll = new javax.swing.JScrollPane();
        cmdAdd = new javax.swing.JButton();
        cmdImport = new javax.swing.JButton();
        cmdRemove = new javax.swing.JButton();
        cmdNew = new javax.swing.JButton();
        cmdUndo = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        jplItemEditorHolder.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RVX Item", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(102, 102, 102))); // NOI18N
        jplItemEditorHolder.setMinimumSize(new java.awt.Dimension(12, 250));
        jplItemEditorHolder.setLayout(new java.awt.BorderLayout());

        jScroll.setToolTipText("Drag&Drop to edit the parameter tree. Hold 'Ctrl' key to copy a branch.");

        cmdAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/plus1.png"))); // NOI18N
        cmdAdd.setToolTipText("Add a new item under the current item.");
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        cmdImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/folder_open.png"))); // NOI18N
        cmdImport.setToolTipText("Import from an external RVX file");
        cmdImport.setEnabled(false);
        cmdImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdImportActionPerformed(evt);
            }
        });

        cmdRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/minus.png"))); // NOI18N
        cmdRemove.setToolTipText("Remove the selected item.");
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveActionPerformed(evt);
            }
        });

        cmdNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_lightning.png"))); // NOI18N
        cmdNew.setToolTipText("Reset the RVX tree");
        cmdNew.setEnabled(false);
        cmdNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNewActionPerformed(evt);
            }
        });

        cmdUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/edit-undo.png"))); // NOI18N
        cmdUndo.setToolTipText("Undo last change");
        cmdUndo.setEnabled(false);
        cmdUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdUndoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmdAdd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdImport, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdNew, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdUndo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmdNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdUndo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(cmdAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdRemove))
                    .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jplItemEditorHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jplItemEditorHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        this.addRvxItem();
}//GEN-LAST:event_cmdAddActionPerformed

    private void cmdRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveActionPerformed
        removeRvxItem();
}//GEN-LAST:event_cmdRemoveActionPerformed

    private void cmdImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdImportActionPerformed
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        try {
            RVX rvx = mapper.readValue(new File ("D:\\4\\jEPlus_v1.7.0_beta\\example_3-RVX_v1.6_E+v8.3\\my.rvx"), RVX.class);
            setRvx (rvx);
        } catch (IOException ex) {
            logger.error("Failed to open RVX file!", ex);
        }
        
    }//GEN-LAST:event_cmdImportActionPerformed

    private void cmdNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNewActionPerformed
        setRvx (new RVX());
    }//GEN-LAST:event_cmdNewActionPerformed

    private void cmdUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUndoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmdUndoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdImport;
    private javax.swing.JButton cmdNew;
    private javax.swing.JButton cmdRemove;
    private javax.swing.JButton cmdUndo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel jplItemEditorHolder;
    // End of variables declaration//GEN-END:variables

    public static void main (String [] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        RVX rvx = mapper.readValue(new File ("D:\\4\\jEPlus_v1.7.0_beta\\example_3-RVX_v1.6_E+v8.3\\my.rvx"), RVX.class);
        JFrame frame = new JFrame("RVXTree tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JPanel_RVXTree(null, "D:\\4\\jEPlus_v1.7.0_beta\\example_3-RVX_v1.6_E+v8.3\\", rvx));
        frame.pack();
        frame.setVisible(true);
        
        
    }

}