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
import java.util.*;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import jeplus.EPlusTask;
import jeplus.JEPlusConfig;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class INSELOutputReader implements IFReportReader, IFResultReader {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(INSELOutputReader.class);
    
    protected String ReportFile = JEPlusConfig.getDefaultInstance().getScreenFile();
    String ResultFile;
    
    /**
     * Default constructor, does nothing
     */
    public INSELOutputReader (String resfile) {
        ResultFile = resfile;
    }

    public String getReportFile() {
        return ReportFile;
    }

    public void setReportFile(String ReportFile) {
        this.ReportFile = ReportFile;
    }

    public String getResultFile() {
        return ResultFile;
    }

    public void setResultFile(String ResultFile) {
        this.ResultFile = ResultFile;
    }

    @Override
    public int readReport(EPlusBatch manager, String dir, ArrayList<String> header, ArrayList<ArrayList<String>> table) {
        if (header == null || table == null) return 0;
        // Example eplusout.end:
        // "EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        int nResCollected = 0;
        // Header
        String[] hdrs = {   "Id",
                            "Job_ID",
                            "Message",
                            "Errors",
                            "Warnings",
                            "Calculation Time"
                            };
        header.clear();
        header.addAll(Arrays.asList(hdrs));
        // Jobs Data
        List <EPlusTask> JobQueue = manager.getAgent().getFinishedJobs();
        for (int i = 0; i < JobQueue.size(); i++) {
            // For each job, do:
            if (readReport(dir, JobQueue.get(i).getJobID(), header, table) > 0) {
                nResCollected ++;
            }
        }
        return nResCollected;
    }

    @Override
    public int readReport(String dir, String job_id, ArrayList<String> header, ArrayList<ArrayList<String>> table) {
        if (header == null || table == null) return 0;
        // Example console.txt:
        //      12 errors, 10 warnings
        //      Normal end of run
        //      Simulation time: 1200 seconds
        try {
            // Jobs Data
            // Get result information
            String fullpath = dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/";
            File end = new File(fullpath + ReportFile);
            
            int errors = 0;
            int warnings = 0;
            String message = "Unknown";
            long timesim = 0;

            if (end.exists()) {
                try (BufferedReader fr = new BufferedReader(new FileReader(end))) {
                    String line = fr.readLine();
                    while (line != null ) {
                        if (line.contains("errors,") && line.contains("warnings")) {
                            errors = Integer.parseInt(line.trim().substring(0, line.indexOf("errors,")));
                            warnings = Integer.parseInt(line.trim().substring(line.indexOf("errors,")+7, line.indexOf("warnings")));
                        }else if (line.contains("end of run")) {
                            message = line.trim();
                        }else if (line.trim().startsWith("Simulation time:")) {
                            timesim = Long.parseLong(line.trim().substring(17, line.indexOf(" seconds")));
                        }
                        line = fr.readLine();
                    }
                }catch (Exception e) {
                    logger.error("", e);
                }
            }
            String [] vals = new String [header.size()];
            vals[0] = table.size() + "";
            vals[1] = job_id;
            vals[2] = message;
            vals[3] = Integer.toString(errors);
            vals[4] = Integer.toString(warnings);
            vals[5] = Long.toString(timesim);
            
            table.add(new ArrayList<>(Arrays.asList(vals)));
            return 1;
        }catch (NumberFormatException ex) {
            logger.error("", ex);
        }
        return 0;
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
            if (readResult(dir, job_id, header, table) > 0) {
                counter ++;
            }
            // done with loading  
        } 
        return counter;
    }

    @Override
    public int readResult(String dir, String job_id, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        // Example [job_id].csv:
        // row 1 - Collumn heading: comma delimitted text
        // row 2 and on - data: comma delimitted numerial text

        // Number of jobs whose results have been collected.
        int nResCollected = 0;
        // Read job result file
        File csv = new File(dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/" + ResultFile);
        if (csv.exists()) {
            try (BufferedReader fr = new BufferedReader(new FileReader(csv))) {
                String line = fr.readLine();
                if (line != null) {
//                    // process first line, the column header
//                    String [] headings = line.trim().split("\\s*[, ]\\s*");
//                    int [] index = new int [headings.length];
//                    for (int j=0; j<headings.length; j++) {
//                        if (! header.containsKey(headings[j])) {
//                            index[j] = header.size();
//                            header.put(headings[j], index[j]);
//                            for (int k=0; k<table.size(); k++) table.get(k).add("-");
//                        }else {
//                            index[j] = header.get(headings[j]).intValue();
//                        }
//                    }
//                    // the rest is data
//                    line = fr.readLine();
                    // Add artificial hearder
                    String [] headings = line.trim().split("[, \t]+");
                    int [] index = new int [headings.length];
                    for (int j=0; j<headings.length; j++) {
                        headings[j] = "C" + j;
                        if (! header.containsKey(headings[j])) {
                            index[j] = header.size();
                            header.put(headings[j], index[j]);
                            for (int k=0; k<table.size(); k++) table.get(k).add("-");
                        }else {
                            index[j] = header.get(headings[j]).intValue();
                        }
                    }
                    // Start reading data
                    while (line != null && line.trim().length() > 0) {
                        ArrayList<String> row = new ArrayList<> ();
                        row.add(Integer.toString(table.size()));
                        row.add(job_id);
                        // add a new row in the data table
                        for (int j=2; j<header.size(); j++) row.add("-");
                        // fill in data from the result file
                        String [] data = line.trim().split("[, \t]+");
                        for (int j=0; j<data.length; j++) {
                            row.set(index[j], data[j]);
                        }
                        nResCollected ++;
                        table.add(row);
                        line = fr.readLine();
                    }
                }
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
        return nResCollected;
    }

    @Override
    public JPanel getOptionPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
