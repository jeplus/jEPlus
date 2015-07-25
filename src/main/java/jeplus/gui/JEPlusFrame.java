/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@googlemail.com>               *
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
 *  - Created 2010                                                         *
 *                                                                         *
 ***************************************************************************/
package jeplus.gui;

import javax.swing.JFrame;
import jeplus.EPlusBatch;
import jeplus.JEPlusProject;

/**
 * Wrapper class adds an <code>JEPlusProject</code> instance to the <code>JFrame
 * </code> class
 * @author yzhang
 */
public abstract class JEPlusFrame extends JFrame {

    protected JEPlusProject Project = new JEPlusProject ();

    public JEPlusProject getProject() {
        return Project;
    }

    public abstract void setProject(JEPlusProject Project, EPlusBatch batch);

}