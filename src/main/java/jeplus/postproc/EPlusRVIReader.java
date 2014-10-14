/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@gmail.com>                    *
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
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus.postproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import jeplus.EPlusConfig;
import jeplus.EPlusTask;
import jeplus.EPlusWinTools;
import jeplus.JEPlusConfig;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class EPlusRVIReader implements IFResultReader {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusRVIReader.class);
    
    protected String RVIFile = EPlusConfig.getEPDefRvi();
    protected String CSVFile = EPlusConfig.getEPDefOutCSV();
    
    /**
     * Default constructor, does nothing
     * @param rvi
     * @param csv
     */
    public EPlusRVIReader (String rvi, String csv) {
        RVIFile = rvi;
        CSVFile = csv;
    }

    @Override
    public int readResult(EPlusBatch manager, String dir, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        // Initiate header
        header.clear();
        header.put("#", Integer.valueOf(0));
        header.put("Job_ID", Integer.valueOf(1));
        // Get finished jobs
        List <EPlusTask> JobQueue = manager.getAgent().getFinishedJobs();
        // Collect Job results
        int counter = 0;
        for (int i = 0; i < JobQueue.size(); i++) {
            // For each job, do:
            EPlusTask job = JobQueue.get(i);
            String job_id = job.getJobID();
            if (readResult(dir, job_id, header, table) > 0) counter ++;
        } // done with loading
        return counter;
    }

    @Override
    public int readResult(String dir, String job_id, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        // Example [job_id].csv:
        // row 1 - Collumn heading: comma delimitted text
        // row 2 and on - data: comma delimitted numerial text

        // Number of jobs whose results have been collected.
        int nResCollected = 0;
        // Get path to job folder
        String job_dir = dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/";
        // Run ReadVars
        EPlusWinTools.runReadVars(JEPlusConfig.getDefaultInstance(), job_dir, RVIFile, CSVFile);
        // Read job result file
        File csv = new File(job_dir + CSVFile);
        if (csv.exists()) {
            try (BufferedReader fr = new BufferedReader(new FileReader(csv))) {
                String line = fr.readLine();
                if (line != null) {
                    // process first line, the column header
                    String [] headings = line.split("\\s*,\\s*");
                    int [] index = new int [headings.length];
                    for (int j=0; j<headings.length; j++) {
                        headings[j] = headings[j].trim();
                        if (! header.containsKey(headings[j])) {
                            index[j] = header.size();
                            header.put(headings[j], index[j]);
                            for (int k=0; k<table.size(); k++) table.get(k).add("-");
                        }else {
                            index[j] = header.get(headings[j]).intValue();
                        }
                    }
                    // the rest is data
                    line = fr.readLine();
                    while (line != null && line.trim().length() > 0) {
                        ArrayList<String> row = new ArrayList<> ();
                        row.add(Integer.toString(table.size()));
                        row.add(job_id);
                        // add a new row in the data table
                        for (int j=2; j<header.size(); j++) row.add("-");
                        // fill in data from the result file
                        String [] data = line.split(",");
                        for (int j=0; j<data.length; j++) {
                            row.set(index[j], data[j]);
                        }
                        nResCollected ++;
                        table.add(row);
                        line = fr.readLine();
                    }
                }
            }catch (Exception ex) {
                logger.error("Error reading or parsing E+ result for " + job_id, ex);
            }
        }
        return nResCollected;
    }

    @Override
    public JPanel getOptionPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
