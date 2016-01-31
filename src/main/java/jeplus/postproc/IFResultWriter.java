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
package jeplus.postproc;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;
import jeplus.EPlusBatch;

/**
 * Result writer interface
 */
public interface IFResultWriter {

    /**
     * Write result table and header to file
     * @param manager Simulation manager contains jobs and paths information
     * @param header Table header
     * @param table Table content
     */
    public void writeResult(EPlusBatch manager, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table);

    /**
     * Get the last written result file in full path
     * @return The last written result file
     */
    public String getResultFile();

    /**
     * Get the file name only of the result file to be written
     * @return File name of the result file
     */
    public String getResultFileName();

    /**
     * Get option panel for configuring this post processor
     * @return an instance of option panel
     */
    public abstract JPanel getOptionPanel();
    
}
