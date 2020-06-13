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

import javax.swing.JFrame;
import jeplus.EPlusBatch;
import jeplus.JEPlusProjectV2;

/**
 * Wrapper class adds an <code>JEPlusProjectV2</code> instance to the <code>JFrame
 * </code> class
 * @author yzhang
 */
public abstract class AbstractJEPlusFrame extends JFrame {

    protected JEPlusProjectV2 Project = new JEPlusProjectV2 ();

    public JEPlusProjectV2 getProject() {
        return Project;
    }

    public abstract void setProject(JEPlusProjectV2 Project, EPlusBatch batch);

}
