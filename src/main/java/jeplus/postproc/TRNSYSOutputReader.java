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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import jeplus.EPlusTask;
import jeplus.TRNSYSConfig;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class TRNSYSOutputReader implements IFReportReader, IFResultReader {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(TRNSYSOutputReader.class);
    
    protected String TRNSYSReportFile = TRNSYSConfig.getTRNSYSDefLST();
    String TRNSYSResultFile;
    
    /**
     * Default constructor, does nothing
     */
    public TRNSYSOutputReader (String resfile) {
        TRNSYSResultFile = resfile;
    }

    public String getTRNSYSReportFile() {
        return TRNSYSReportFile;
    }

    public void setTRNSYSReportFile(String TRNSYSReportFile) {
        this.TRNSYSReportFile = TRNSYSReportFile;
    }

    public String getTRNSYSResultFile() {
        return TRNSYSResultFile;
    }

    public void setTRNSYSResultFile(String TRNSYSResultFile) {
        this.TRNSYSResultFile = TRNSYSResultFile;
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
                            "Calculation Time",
                            "Trace %Time",
                            "Trace Components",
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
        // Example eplusout.end:
        // "EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        try {
            // Jobs Data
                // Get result information
            String fullpath = dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/";
            File end = new File(fullpath + TRNSYSReportFile);
            
            int conterror = 0;
            int contwarning = 0;
            int conttrace = 0;
            double times = 1;
            String message = "TRNSYS Completed Successfully";
            String timesim = "-";
            String attime = "-";
            String component = new String();            

            if (end.exists()) {
                try (BufferedReader fr = new BufferedReader(new FileReader(end))) {
                    String line = fr.readLine();
                    while (line != null ) {
                        if (line.trim().startsWith("*** Fatal Error at time")) {
                            fr.readLine();
                            fr.readLine();
                            line = fr.readLine().trim();
                            line = line.substring(line.indexOf(":")+1);
                            if (line.indexOf("Errors found while processing the TRNSYS input file.") == -1) {
                                conterror++;
                                if (conterror == 1) {
                                    message = "Error " + conterror + ": " + line.trim();
                                }else {
                                    message = message + " Error " + conterror + ": " + line.trim();
                                }
                            }                     
                        }
                        else if ((line.trim().indexOf("TRANSIENT SIMULATION") != -1) && (line.trim().indexOf("STARTING AT TIME") != -1)) {
                            String start = (line.trim().split("\\s* \\s*"))[6];
                            String stop = fr.readLine().trim().split("\\s* \\s*")[4];
                            String [] timestep = fr.readLine().trim().split("\\s* \\s*");
                            times = Math.abs(Double.parseDouble(stop)-Double.parseDouble(start)) * Double.parseDouble(timestep[2]) / Math.max(1, Double.parseDouble(timestep[4]));     
                        }
                        else if (line.trim().startsWith("*** Warning at time")) {
                            contwarning++;
                        }
                        else if (line.trim().startsWith("*TRACE*")) {
                            String [] traces = line.trim().split("\\s* \\s*");
                            if (component.indexOf((traces[1]+traces[2]+"-"+traces[3]+traces[4]).toString()) == -1) {
                                component = component + (traces[1]+traces[2]+"-"+traces[3]+traces[4]+" ").toString();
                            }
                            if (attime.indexOf(traces[7]) == -1){
                                attime = (attime + traces[7] + " ").toString();
                                conttrace++;
                            }
                        }
                        else if (line.trim().startsWith("Total TRNSYS Calculation Time:")) {
                            timesim = line.substring(line.indexOf(":")+1).trim();
                        }
                        line = fr.readLine();
                    }
                }catch (Exception ex) {
                    logger.error("Error reading/parsing TRNSYS report for " + job_id, ex);
                }
            }else {
                logger.warn("TRNSYS report for " + job_id + " does not exist.");
            }
            
            if (conttrace == 0) component = "-";
            double timeerror = (double) conttrace *100 / Math.max(1,times);
            
            String [] vals = new String [header.size()];
            vals[0] = table.size() + "";
            vals[1] = job_id;
            vals[2] = message.trim();
            vals[3] = conterror + "";
            vals[4] = contwarning + "";
            vals[5] = timesim.trim();
            vals[6] = Math.rint(timeerror*100)/100 + "%"; 
            vals[7] = component.trim(); 
            
            table.add(new ArrayList<>(Arrays.asList(vals)));
            return 1;
                
        }catch (Exception ex) {
            logger.error("Error inserting TRNSYS report for " + job_id, ex);
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
        File csv = new File(dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/" + TRNSYSResultFile);
        if (csv.exists()) {
            try (BufferedReader fr = new BufferedReader(new FileReader(csv))) {
                String line = fr.readLine();
                if (line != null) {
                    // process first line, the column header
                    String [] headings = line.trim().split("\\s*[, ]\\s*");
                    int [] index = new int [headings.length];
                    for (int j=0; j<headings.length; j++) {
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
                        String [] data = line.trim().split("\\s*[, ]\\s*");
                        for (int j=0; j<data.length; j++) {
                            row.set(index[j], data[j]);
                        }
                        nResCollected ++;
                        table.add(row);
                        line = fr.readLine();
                    }
                }
            }catch (Exception ex) {
                logger.error("Error reading/parsing TRNSYS result for " + job_id, ex);
            }
        }else {
            logger.warn("TRNSYS result for " + job_id + " does not exist.");
        } 
        return nResCollected;
    }

    @Override
    public JPanel getOptionPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
