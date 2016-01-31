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

/**
 * Post processor interface
 */
public interface IFPostProcessor {

    /**
     * Process the result table from the batch
     * @param header Result table header in a HashMap
     * @param table Table content
     */
    public void postProcess(HashMap<String, Integer> header, ArrayList<ArrayList<String>> table);

    /**
     * Process each job's csv file and return results in a string. The results can be
     * identified by the job_id if included.
     * @param jobid The job_id can be used to identify the current csv file
     * @param csvfile The csv file to processJobResult
     * @return Results in a string.
     */
    public abstract String processJobResult(String jobid, String csvfile);

    /**
     * Get option panel for configuring this post processor
     * @return an instance of option panel
     */
    public abstract JPanel getOptionPanel();
    
}
