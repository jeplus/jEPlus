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
package jeplus.util;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.dnd.*;
import jeplus.data.ParameterItem;
import org.slf4j.LoggerFactory;

public class DNDTree extends JTree {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(DNDTree.class);

    Insets autoscrollInsets = new Insets(20, 20, 20, 20); // insets

    public DNDTree(DefaultMutableTreeNode root) {
        setAutoscrolls(true);
        DefaultTreeModel treemodel = new DefaultTreeModel(root);
        setModel(treemodel);
        setRootVisible(true);
        setShowsRootHandles(false);//to show the root icon
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //set single selection for the Tree
        setEditable(false);
        new DefaultTreeTransferHandler(this, DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void autoscroll(Point cursorLocation) {
        Insets insets = getAutoscrollInsets();
        Rectangle outer = getVisibleRect();
        Rectangle inner = new Rectangle(outer.x + insets.left, outer.y + insets.top, outer.width - (insets.left + insets.right), outer.height - (insets.top + insets.bottom));
        if (!inner.contains(cursorLocation)) {
            Rectangle scrollRect = new Rectangle(cursorLocation.x - insets.left, cursorLocation.y - insets.top, insets.left + insets.right, insets.top + insets.bottom);
            scrollRectToVisible(scrollRect);
        }
    }

    public Insets getAutoscrollInsets() {
        return (autoscrollInsets);
    }

    public static DefaultMutableTreeNode makeDeepCopy(DefaultMutableTreeNode node) {
        Object uobj = node.getUserObject();
        if (uobj instanceof ParameterItem) {
            try {
                uobj = ((ParameterItem) uobj).clone();
            } catch (CloneNotSupportedException ex) {
                logger.error("Cannot make copy of user object.", ex);
            }
        }
        DefaultMutableTreeNode copy = new DefaultMutableTreeNode(uobj);
        for (Enumeration e = node.children(); e.hasMoreElements();) {
            copy.add(makeDeepCopy((DefaultMutableTreeNode) e.nextElement()));
        }
        return (copy);
    }

    public static DefaultMutableTreeNode createTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("node1");
        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("node2");
        root.add(node1);
        root.add(node2);
        node1.add(new DefaultMutableTreeNode("sub1_1"));
        node1.add(new DefaultMutableTreeNode("sub1_2"));
        node1.add(new DefaultMutableTreeNode("sub1_3"));
        node2.add(new DefaultMutableTreeNode("sub2_1"));
        node2.add(new DefaultMutableTreeNode("sub2_2"));
        node2.add(new DefaultMutableTreeNode("sub2_3"));
        return (root);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame();
            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new GridLayout(1, 2));
            DefaultMutableTreeNode root1 = DNDTree.createTree();
            DNDTree tree1 = new DNDTree(root1);
            DefaultMutableTreeNode root2 = DNDTree.createTree();
            DNDTree tree2 = new DNDTree(root2);
            contentPane.add(new JScrollPane(tree1));
            contentPane.add(new JScrollPane(tree2));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setVisible(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
