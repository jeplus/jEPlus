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
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import jeplus.EPlusConfig;
import jeplus.EPlusTask;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class EPlusOutputReader implements IFReportReader, IFResultReader {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusOutputReader.class);
    
    protected String EPlusReportFile = EPlusConfig.getEPDefOutEND();
    protected String EPlusResultFile = EPlusConfig.getEPDefOutCSV();
    
    /**
     * Default constructor, does nothing
     */
    public EPlusOutputReader () {
        
    }

    @Override
    public int readReport(EPlusBatch manager, String dir, ArrayList<String> header, ArrayList<ArrayList<String>> table) {
        if (header == null || table == null) return 0;
        // Example eplusout.end:
        // "EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        int nResCollected = 0;
        // Header
        String[] hdrs = {   "#",
                            "Job_ID",
                            "Message",
                            "Warnings",
                            "Errors",
                            "Hours",
                            "Minutes",
                            "Seconds"};
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
        // Example eplusout.end:
        // "EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        try {
            // Jobs Data
                // Get result information
            String fullpath = dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/";
            File end = new File(fullpath + EPlusReportFile);

            StringBuilder buf = new StringBuilder();
            if (end.exists()) {
                try (BufferedReader fr = new BufferedReader(new FileReader(end))) {
                    String line = fr.readLine();
                    while (line != null && line.trim().length() > 0) {
                        buf.append(line).append("\n");
                        line = fr.readLine();
                    }
                }catch (Exception ex) {
                    logger.error("Error reading " + end.getAbsolutePath());
                }
            }
            String info = buf.toString();
            if (info != null && info.length() > 0 && ! info.startsWith("!")) {
                String [] vals = new String [8];
                vals[0] = Integer.toString(table.size());
                vals[1] = job_id;
                int marker = info.indexOf("--");
                vals[2] = info.substring(0, marker);
                info = info.substring(marker + 2).trim();
                String [] segment = info.split(";");
                for (int j=0; j<segment.length; j++) {
                    String thisseg = segment[j].trim();
                    if (thisseg.endsWith("Warning")) {
                        vals[3] = thisseg.substring(0, thisseg.indexOf(" "));
                    }else if (thisseg.endsWith("Severe Errors")) {
                        vals[4] = thisseg.substring(0, thisseg.indexOf(" "));
                    }else if (thisseg.startsWith("Elapsed Time")) {
                        vals[5] = Integer.valueOf(thisseg.substring(thisseg.indexOf("=")+1, thisseg.indexOf("hr "))).toString();
                        vals[6] = Integer.valueOf(thisseg.substring(thisseg.indexOf("hr ")+3, thisseg.indexOf("min"))).toString();
                        vals[7] = thisseg.substring(thisseg.indexOf("min ")+4, thisseg.indexOf("sec")).trim();
                    }
                }
                table.add(new ArrayList<>(Arrays.asList(vals)));
                return 1;
            }
        }catch (Exception ex) {
            logger.error("Error during parsing E+ end report for " + job_id, ex);
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
        // Read job result file
        File csv = new File(dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/" + EPlusResultFile);
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
