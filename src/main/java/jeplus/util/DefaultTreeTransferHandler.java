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

import java.awt.*;
import javax.swing.tree.*;
import java.awt.dnd.*;

/**
 * Tree node transfer handler
 * @author Yi
 */
public class DefaultTreeTransferHandler extends AbstractTreeTransferHandler {

	public DefaultTreeTransferHandler(DNDTree tree, int action) {
		super(tree, action, true);
	}

        @Override
	public boolean canPerformAction(DNDTree target, DefaultMutableTreeNode draggedNode, int action, Point location) {
		TreePath pathTarget = target.getPathForLocation(location.x, location.y);
		if (pathTarget == null) {
			target.setSelectionPath(null);
			return(false);
		}
		target.setSelectionPath(pathTarget);
		if(action == DnDConstants.ACTION_COPY) {
			return(true);
		}
		else
		if(action == DnDConstants.ACTION_MOVE) {
			DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)pathTarget.getLastPathComponent();
			if (draggedNode.isRoot() || parentNode == draggedNode.getParent() || draggedNode.isNodeDescendant(parentNode)) {
				return(false);
			}
			else {
				return(true);
			}
		}
		else {
			return(false);
		}
	}

        @Override
	public boolean executeDrop(DNDTree target, DefaultMutableTreeNode draggedNode, DefaultMutableTreeNode newParentNode, int action) {
		if (action == DnDConstants.ACTION_COPY) {
			DefaultMutableTreeNode newNode = DNDTree.makeDeepCopy(draggedNode);
			((DefaultTreeModel)target.getModel()).insertNodeInto(newNode,newParentNode,newParentNode.getChildCount());
			TreePath treePath = new TreePath(newNode.getPath());
			target.scrollPathToVisible(treePath);
			target.setSelectionPath(treePath);
			return(true);
		}
		if (action == DnDConstants.ACTION_MOVE) {
			draggedNode.removeFromParent();
			((DefaultTreeModel)target.getModel()).insertNodeInto(draggedNode,newParentNode,newParentNode.getChildCount());
			TreePath treePath = new TreePath(draggedNode.getPath());
			target.scrollPathToVisible(treePath);
			target.setSelectionPath(treePath);
			return(true);
		}
		return(false);
	}
}
