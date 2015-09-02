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
import jeplus.EPlusTask;
import jeplus.data.RVX_CSVitem;
import org.slf4j.LoggerFactory;

/**
 * This result reader reads CSV tables produced by E+, e.g. eplustbl.csv, eplusssz.csv and epluszsz.csv
 * User specifies the csv file name (in E+ output folder), the table name (can be empty), the column heading and the row heading.
 * The class will locate the cell in each csv file and put them in a result table indexed by the Job IDs
 * This is a simplistic implementation, supports extracting only one cell at a time.
 * @author zyyz
 */
public class EPlusCsvReader implements IFResultReader {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusCsvReader.class);

    /** Transient spreadsheet file name */
    transient RVX_CSVitem CsvSpecs = null;
    
    /**
     * Construct reader with item specs
     * @param specs 
     */
    public EPlusCsvReader (RVX_CSVitem specs) {
        CsvSpecs = specs;
    }

    @Override
    public int readResult(EPlusBatch manager, String dir, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        // Initiate header
        header.clear();
        header.put("#", 0);
        header.put("Job_ID", 1);
        header.put("Reserved", 2);
        String cols = CsvSpecs.getColumnHeaders();
        if (cols != null && cols.length()>0) {
            String [] col = cols.split("\\s*,\\s*");
            for (int i=0; i<col.length; i++) {
                header.put(col[i], i+3);
            }
        }else {
            header.put(CsvSpecs.getFromColumn(), 3);
        }
        // Get finished jobs
        List <EPlusTask> JobQueue = manager.getAgent().getFinishedJobs();
        // Collect Job results
        int counter = 0;
        for (EPlusTask job : JobQueue) {
            String job_id = job.getJobID();
            if (readResult(dir, job_id, header, table) > 0) counter ++;
        } // done with loading
        return counter;
    }

    @Override
    public int readResult(String dir, String job_id, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        // Number of jobs whose results have been collected.
        int nResCollected = 0;
        // Get path to job folder
        String job_dir = dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/";
        // Check if the requested csv file is available
        try (BufferedReader fr = new BufferedReader (new FileReader (job_dir + CsvSpecs.getSourceCsv()))) {            
            // Load the whole CSV contents
            ArrayList <String []> spreadsheet = new ArrayList <> ();
            String line = fr.readLine();
            while (line != null) {
                // Add only non-empty lines to the spreadsheet
                if (line.trim().length() > 0) {
                    spreadsheet.add (line.split("\\s*,\\s*"));
                }
                line = fr.readLine();
            }
            // Locate table, then Column, then Row
            boolean found_report = true, found_table = true, found_column = false, found_row = false;
            int ColId = 0;
            String Cell = "-";
            if (CsvSpecs.getFromReport() != null && CsvSpecs.getFromReport().trim().length()>0) {
                found_report = false;
            }
            if (CsvSpecs.getFromTable() != null && CsvSpecs.getFromTable().trim().length()>0) {
                found_table = false;
            }
            for (String [] row : spreadsheet) {
                if (found_report) {
                    if (found_table) {
                        // Check if the requested column is in the header
                        if (! found_column) {
                            for (int i=0; i<row.length; i++) {
                                if (row[i].equalsIgnoreCase(CsvSpecs.getFromColumn())) {
                                    ColId = i;
                                    found_column = true;
                                }
                            }
                        }else {
                            // Now find the requested row with the row header
                            if (row.length>2 && row.length > ColId && row[1].equalsIgnoreCase(CsvSpecs.getFromRow())) {
                                found_row = true;
                                Cell = row[ColId];
                                break;
                            }
                        }
                    }else {
                        if (row.length > 0 && row[0]!=null && row[0].equalsIgnoreCase(CsvSpecs.getFromTable())) {
                            found_table = true;
                        }
                    }
                }else {
                    if (row.length > 1 && 
                        row[0]!=null && row[0].equalsIgnoreCase("REPORT:") &&
                        row[1]!=null && row[1].equalsIgnoreCase(CsvSpecs.getFromReport())) {
                            found_report = true;
                    }
                }
            }
            // Add to table
            ArrayList<String> DataRow = new ArrayList<> ();
            DataRow.add(Integer.toString(table.size()));
            DataRow.add(job_id);
            DataRow.add("");
            // Cell is always added, found or not
            DataRow.add(Cell);
            if (found_row) {
                nResCollected ++;
            }
            table.add(DataRow);
        }catch (Exception ex) {
            logger.error("Error when reading " + CsvSpecs.getSourceCsv() + " from " + job_dir, ex);
        }
        return nResCollected;
    }

    @Override
    public JPanel getOptionPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
