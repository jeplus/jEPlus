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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import jeplus.EPlusTask;
import jeplus.data.RVX_UserSuppliedItem;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * This result reader reads a user-supplied spreadsheet to fill in information of simulation jobs. The spreadsheet must follow the following
 * format:
 *   - in CSV format
 *   - first row contains column headings
 *   - first column is auto-generated index, e.g. 0, 1, 2, ...
 *   - second column contains regular expression patterns for job IDs (see: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html)
 *   - third column and beyond contains user-supplied model information, such as embodied carbon etc.
 * Information of each job is retrieved by the specific job ID. Data in the specified columns of all the rows by which the job ID patterns are
 * matched by the given job are collected.
 * 
 * @author zyyz
 */
public class EPlusUserSpreadsheetReader implements IFResultReader {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusUserSpreadsheetReader.class);

    /** Transient spreadsheet file name */
    transient String Spreadsheet = null;
    /** Transient column list */
    transient int HeaderRow = 0;
    /** Transient column list */
    transient int JobIndexColumn = 1;
    /** Transient column list */
    transient int [] Columns = null;
    /** Transient default value for missing records */
    transient double MissingValue = 0;
    
    /**
     * Construct reader by filling the three fields from the passed-in string
     * @param specs 
     */
    public EPlusUserSpreadsheetReader (RVX_UserSuppliedItem specs) {
        Spreadsheet = specs.getFileName();
        HeaderRow = specs.getHeaderRow();
        JobIndexColumn = specs.getJobIdColumn();
        String columns = specs.getDataColumns();
        // Add user specified headers
        String [] cstrs = columns.split("\\s*,\\s*");
        Columns = new int [cstrs.length];
        for (int i=0; i<cstrs.length; i++) {
            try {
                Columns[i] = Integer.parseInt(cstrs[i]);
            }catch (NumberFormatException nfe) {
                Columns[i] = -1;
            }
        }
        MissingValue = specs.getMissingValue();
    }

    @Override
    public int readResult(EPlusBatch manager, String dir, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        // Initiate header, adding staple items
        header.clear();
        header.put("#", 0);
        header.put("Job_ID", 1);
        header.put("Reserved", 2);
        try (BufferedReader fr = new BufferedReader (new FileReader (RelativeDirUtil.checkAbsolutePath(Spreadsheet, manager.getProject().getBaseDir())))) {
            // Read first row of spreadsheet
            String line = "";
            for (int i=0; i<=HeaderRow; i++) { line = fr.readLine(); }
            String [] HeaderStrings = line.split("\\s*,\\s*");
            // Read in the rest of the spreadsheet
            ArrayList <String []> spreadsheet = new ArrayList <> ();
            line = fr.readLine();
            while (line != null) {
                spreadsheet.add (line.split("\\s*,\\s*"));
                line = fr.readLine();
            }
            // Add user specified headers
            for (int i=0; i<HeaderStrings.length; i++) {
                if (! header.containsKey(HeaderStrings[i])) {
                    header.put(HeaderStrings[i], i+3);
                }
            }
            // Get finished jobs
            List <EPlusTask> JobQueue = manager.getAgent().getFinishedJobs();
            // Collect Job results
            int counter = 0;
            for (EPlusTask job : JobQueue) {
                String job_id = job.getJobID();
                // Scan spreadsheet, stop matching after first is found
                boolean found = false;
                for (String[] row : spreadsheet) {
                    Matcher m = Pattern.compile(row[this.JobIndexColumn]).matcher(job_id);
                    if (m.matches()) {
                        ArrayList<String> newdatarow = new ArrayList<>();
                        newdatarow.add(Integer.toString(counter));
                        newdatarow.add(job_id);
                        newdatarow.add("-");
                        for (int k=0; k<Columns.length; k++) {
                            newdatarow.add(row[Columns[k]]);
                        }
                        table.add(newdatarow);
                        counter ++;
                        found = true;
                        break;
                    }
                }
                if (! found) {
                    ArrayList<String> newdatarow = new ArrayList<>();
                    newdatarow.add(Integer.toString(counter));
                    newdatarow.add(job_id);
                    newdatarow.add("-");
                    for (int k=0; k<Columns.length; k++) {
                        newdatarow.add(Double.toString(this.MissingValue));
                    }
                    table.add(newdatarow);
                    counter ++;
                }
            } // done with loading
            return counter;
        }catch (Exception ex) {
            logger.error ("Error when parsing results.", ex);
        }
        return 0;
    }

    @Override
    public int readResult(String dir, String job_id, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        return 0;
    }

    @Override
    public JPanel getOptionPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
