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

import java.awt.datatransfer.*;
import javax.swing.tree.*;
import java.util.*;

public class TransferableNode implements Transferable {
	public static final DataFlavor NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Node");
	private DefaultMutableTreeNode node;
	private DataFlavor[] flavors = { NODE_FLAVOR };

	public TransferableNode(DefaultMutableTreeNode nd) {
		node = nd;
	}

        @Override
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor == NODE_FLAVOR) {
			return node;
		}
		else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

        @Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

        @Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return Arrays.asList(flavors).contains(flavor);
	}

}
