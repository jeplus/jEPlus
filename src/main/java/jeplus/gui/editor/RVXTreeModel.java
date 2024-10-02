/*
 * Copyright (C) 2017 Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jeplus.gui.editor;

import java.awt.Component;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import jeplus.data.RVX;
import jeplus.data.RVX_CSVitem;
import jeplus.data.RVX_Constraint;
import jeplus.data.RVX_ESOitem;
import jeplus.data.RVX_MTRitem;
import jeplus.data.RVX_Objective;
import jeplus.data.RVX_RVIitem;
import jeplus.data.RVX_SQLitem;
import jeplus.data.RVX_ScriptItem;
import jeplus.data.RVX_TRNSYSitem;
import jeplus.data.RVX_UserSuppliedItem;
import jeplus.data.RVX_UserVar;

/**
 * The JTree TreeModel represents the structure of the RVX object:
 * Root - RVX
 * Level 1 -    RVIs
 *              SQLs
 *              Scripts
 *              CSVs
 *              UserSupplied
 *              TRNs
 *              UserVars
 *              Constraints (Optional)
 *              Objectives (Optional)
 * Level 2 -        Members
 * @author Yi
 */
public class RVXTreeModel extends DefaultTreeModel {
    
    public static class GroupType {
        String Name = null;
        String Tooltip = null;
        List List = null;
        Class ItemClass = null;
        
        public GroupType (String name, String info, List list, Class itemClass) {
            Name = name;
            Tooltip = info;
            List = list;
            ItemClass = itemClass;
        }

        public String getName() {
            return Name;
        }

        public String getTooltip() {
            return Tooltip;
        }

        public Class getItemClass() {
            return ItemClass;
        }

        public List getList() {
            return List;
        }
        
        @Override
        public String toString() {
            return Name + " [" +(List == null ? 0 : List.size()) + "]";
        }
    }
    
    public static class RVXTreeRenderer extends DefaultTreeCellRenderer {
        Icon LeafIcon;

        public RVXTreeRenderer() {
            LeafIcon = new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/tool.png"));
        }

        @Override
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
            if (! (((DefaultMutableTreeNode)value).getUserObject() instanceof GroupType)) {
                setIcon(LeafIcon);
                setToolTipText("Tooltip ...");
            } else {
                setToolTipText(((GroupType)((DefaultMutableTreeNode)value).getUserObject()).Tooltip); //no tool tip
            } 

            return this;
        }
    }

    RVXTreeRenderer Renderer = new RVXTreeRenderer ();
    RVX Rvx = null;
    
    
    public RVXTreeModel (RVX rvx) {
        super(buildTree(rvx));
        Rvx = rvx;
    }

    public static DefaultMutableTreeNode buildTree (RVX rvx) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new GroupType ("RVX", "Result collection configuration", null, null));
                
        DefaultMutableTreeNode CSVs = new DefaultMutableTreeNode (new GroupType ("CSVs", "Result collection configuration", rvx.getCSVs(), RVX_CSVitem.class));
        for (RVX_CSVitem item : rvx.getCSVs()) {
            CSVs.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(CSVs);
        
        DefaultMutableTreeNode ESOs = new DefaultMutableTreeNode(new GroupType ("ESOs", "Result collection configuration", rvx.getESOs(), RVX_ESOitem.class));
        for (RVX_ESOitem item : rvx.getESOs()) {
            ESOs.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(ESOs);
        
        DefaultMutableTreeNode MTRs = new DefaultMutableTreeNode(new GroupType ("MTRs", "Result collection configuration", rvx.getMTRs(), RVX_MTRitem.class));
        for (RVX_MTRitem item : rvx.getMTRs()) {
            MTRs.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(MTRs);
        
        DefaultMutableTreeNode Scripts = new DefaultMutableTreeNode (new GroupType ("Scripts", "Result collection configuration", rvx.getScripts(), RVX_ScriptItem.class));
        for (RVX_ScriptItem item : rvx.getScripts()) {
            Scripts.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(Scripts);
        
        DefaultMutableTreeNode RVIs = new DefaultMutableTreeNode(new GroupType ("RVIs", "Result collection configuration", rvx.getRVIs(), RVX_RVIitem.class));
        for (RVX_RVIitem item : rvx.getRVIs()) {
            RVIs.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(RVIs);
        
        DefaultMutableTreeNode SQLs = new DefaultMutableTreeNode (new GroupType ("SQLs", "Result collection configuration", rvx.getSQLs(), RVX_SQLitem.class));
        for (RVX_SQLitem item : rvx.getSQLs()) {
            SQLs.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(SQLs);
        
        DefaultMutableTreeNode UserSupplied = new DefaultMutableTreeNode (new GroupType ("UserSupplied", "Result collection configuration", rvx.getUserSupplied(), RVX_UserSuppliedItem.class));
        for (RVX_UserSuppliedItem item : rvx.getUserSupplied()) {
            UserSupplied.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(UserSupplied);
        
        DefaultMutableTreeNode TRNs = new DefaultMutableTreeNode (new GroupType ("TRNs", "Result collection configuration", rvx.getTRNs(), RVX_TRNSYSitem.class));
        for (RVX_TRNSYSitem item : rvx.getTRNs()) {
            TRNs.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(TRNs);
        
        DefaultMutableTreeNode UserVars = new DefaultMutableTreeNode (new GroupType ("ReportVars", "Result collection configuration", rvx.getUserVars(), RVX_UserVar.class));
        for (RVX_UserVar item : rvx.getUserVars()) {
            UserVars.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(UserVars);
        
        DefaultMutableTreeNode Constraints = new DefaultMutableTreeNode (new GroupType ("Constraints (Optional)", "Result collection configuration", rvx.getConstraints(), RVX_Constraint.class));
        for (RVX_Constraint item : rvx.getConstraints()) {
            Constraints.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(Constraints);
        
        DefaultMutableTreeNode Objectives = new DefaultMutableTreeNode (new GroupType ("Objectives (Optional)", "Result collection configuration", rvx.getObjectives(), RVX_Objective.class));
        for (RVX_Objective item : rvx.getObjectives()) {
            Objectives.add(new DefaultMutableTreeNode (item, false));
        }
        root.add(Objectives);
        
        return root;
    }
    
    // ====== Getters and Setters =======

    public RVXTreeRenderer getRenderer() {
        return Renderer;
    }

    public RVX getRvx() {
        return Rvx;
    }
    
    
}
