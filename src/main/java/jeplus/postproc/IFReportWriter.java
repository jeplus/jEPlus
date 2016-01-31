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
import javax.swing.JPanel;
import jeplus.EPlusBatch;

/** Report writer interface */
public interface IFReportWriter {

    /**
     * Write report table to file
     * @param manager Simulation manager contains jobs and paths information
     * @param header Table header
     * @param table Table content
     */
    public void writeReport(EPlusBatch manager, ArrayList<String> header, ArrayList<ArrayList<String>> table);

    /**
     * Get the last written report file in full path
     * @return The last written report file
     */
    public String getReportFile();

    /**
     * Get the file name only of the report file to be written
     * @return File name of the report file
     */
    public String getReportFileName();

    /**
     * Get option panel for configuring this post processor
     * @return an instance of option panel
     */
    public abstract JPanel getOptionPanel();
    
}
