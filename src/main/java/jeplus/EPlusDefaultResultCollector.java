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
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.slf4j.LoggerFactory;

/**
 * EnergyPlus batch simulation result collector
 * @author yzhang
 * @deprecated Replaced by Agent's result collectors
 */
public class EPlusDefaultResultCollector {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusDefaultResultCollector.class);
    
    /** Job's owner provide information of the jobs run */
    protected EPlusBatch JobOwner = null;
    /** The output folder of simulation results */
    protected String OutputDir = null;
    /** Report header list */
    protected ArrayList<String> ReportHeader = null;
    /** Table for report data */
    protected ArrayList <ArrayList <String>> ReportTable = null;
    /** Header map - column header is mapped to column number */
    protected HashMap <String, Integer> ResultHeader = null;
    /** Table for result data */
    protected ArrayList <ArrayList <String>> ResultTable = null;

    /** Process data on-the-fly instead of storing it memory */
    protected boolean OnTheFly = false;

    public interface ReportReader {
        /**
         * Read result from the named files within the given dir. New data will 
         * be added to Header and Data table. This function returns the number
         * of files read.
         * @param manager The simulation manager holds information on the jobs
         * @param dir Folder path in which reports are stored. This is normally the working directory of the batch.
         * @param file Name of the report file(s), e.g. eplusout.end in the case of E+ simulations. This field may not contain the full file name.
         * @param header Table header of the report data
         * @param table Table content of the report data
         * @return Number of files read
         */
        public int readReport (EPlusBatch manager, String dir, String file, ArrayList<String> header, ArrayList <ArrayList <String>> table);
        /**
         * This method read result from the named file in the given dir. New data will 
         * be added to Header and Data table with the assigned job_id. This function returns the number
         * of lines read.
         * @param dir Folder path in which reports are stored. This is normally the working directory of the batch.
         * @param file Name of the report file(s), e.g. eplusout.end in the case of E+ simulations. This field may not contain the full file name.
         * @param job_id ID string of the job
         * @param header Table header of the report data
         * @param table Table content of the report data
         * @return Number of files read
         */
        public int readReport (String dir, String file, String job_id, ArrayList<String> header, ArrayList <ArrayList <String>> table);
    }
    
    public interface ResultReader {
        /**
         * Read result from the named files within the given dir. New data will 
         * be added to Header and Data table. This function returns the number
         * of files read.
         * @param manager The simulation manager holds information on the jobs
         * @param dir Folder's path in which reports are stored. This is normally the working directory of the batch.
         * @param file Name of the result file(s), e.g. eplusout.csv in the case of E+ simulations. This field may not contain the full file name.
         * @param header Table header of the report data
         * @param table Table content of the report data
         * @return Number of files read
         */
        public int readResult (EPlusBatch manager, String dir, String file, HashMap <String, Integer> header, ArrayList <ArrayList <String>> table);
        /**
         * This method read result from the named file in the given dir. New data will 
         * be added to Header and Data table with the assigned job_id. The header map is 
         * maintained to preserve consistency of the columns of the table. This function returns the number
         * of lines read.
         * @param dir Folder path in which reports are stored. This is normally the working directory of the batch.
         * @param file Name of the result file(s), e.g. eplusout.csv in the case of E+ simulations. This field may not contain the full file name.
         * @param job_id ID string of the job
         * @param header Table header of the report data
         * @param table Table content of the report data
         * @return Number of files read
         */
        public int readResult (String dir, String file, String job_id, HashMap <String, Integer> header, ArrayList <ArrayList <String>> table);
    }
    
    public interface ResultWriter {
        public void writeResult (String file, HashMap <String, Integer> header, ArrayList <ArrayList <String>> table);
    }
    
    public interface ReportWriter {
        public void writeResult (String file, HashMap <String, Integer> header, ArrayList <ArrayList <String>> table);
    }
    
    public interface IndexWriter {
        public void writeResult (String file, EPlusBatch manager);
    }
    
    public interface PostProcessor {
        public void postProcess (HashMap <String, Integer> header, ArrayList <ArrayList <String>> table);
    }

    
    /**
     * Create collector with assigned job owner
     * @param batch Job owner
     */
    public EPlusDefaultResultCollector (EPlusBatch batch) {
        JobOwner = batch;
        OutputDir = JobOwner.getResolvedEnv().getParentDir();
    }

    // ================== Getters and Setters ===================
    
    public void setOutputDir (String dir) {
        OutputDir = dir;
    }

    public EPlusBatch getJobOwner() {
        return JobOwner;
    }

    public void setJobOwner(EPlusBatch JobOwner) {
        this.JobOwner = JobOwner;
    }

    public boolean isOnTheFly() {
        return OnTheFly;
    }

    public void setOnTheFly(boolean OnTheFly) {
        this.OnTheFly = OnTheFly;
    }

    public ArrayList<String> getReportHeader() {
        return ReportHeader;
    }

    public void setReportHeader(ArrayList<String> ReportHeader) {
        this.ReportHeader = ReportHeader;
    }

    public ArrayList<ArrayList<String>> getReportTable() {
        return ReportTable;
    }

    public void setReportTable(ArrayList<ArrayList<String>> ReportTable) {
        this.ReportTable = ReportTable;
    }

    public HashMap<String, Integer> getResultHeader() {
        return ResultHeader;
    }

    public void setResultHeader(HashMap<String, Integer> ResultHeader) {
        this.ResultHeader = ResultHeader;
    }

    public ArrayList<ArrayList<String>> getResultTable() {
        return ResultTable;
    }

    public void setResultTable(ArrayList<ArrayList<String>> ResultTable) {
        this.ResultTable = ResultTable;
    }
    
    // ================== Getters and Setters ===================

    public void collectReports (ReportReader reader, ReportWriter writer) {
        
    }
    
    public void collectResutls (ResultReader reader, PostProcessor processor, ResultWriter writer) {
        
    }
    
    public void collectIndexes (IndexWriter writer) {
        
    }
    
    
    /**
     * Collect the Run end information from "eplusout.end" file in each directory
     */
    public int collectReportsCSV(String filename, String dir) {
        return collectReportsCSV(filename, dir, false);
    }

    /**
     * Collect the Run end information from "eplusout.end" file in each directory
     */
    public int collectReportsCSV(String filename, String dir, boolean remove) {
        // Example eplusout.end:
        // "EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        int nResCollected = 0;
        try {
            File fn = new File (OutputDir + filename);
            PrintWriter fw = new PrintWriter(new FileWriter(fn));
            // Header
            String[] header = {"Id",
                               "JobID",
                               "Message",
                               "Warnings",
                               "Errors",
                               "Hours",
                               "Minutes",
                               "Seconds"};

            StringBuffer buf = new StringBuffer(header[0]);
            for (int i = 1; i < header.length; i++) {
                buf.append(", ").append(header[i]);
            }
            fw.println(buf.toString());

            // Jobs
            List <EPlusTask> JobQueue = JobOwner.getAgent().getFinishedJobs();
            for (int i = 0; i < JobQueue.size(); i++) {
                // For each job, do:
                EPlusTask job = JobQueue.get(i);
                String[] vals = new String [header.length];
                vals[0] = Integer.toString(i);
                vals[1] = job.getJobID();
                // Get result information
                String info = EPlusTask.getResultInfo(dir, job, remove);
                if (info != null && ! info.startsWith("!")) {
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
                            vals[5] = thisseg.substring(thisseg.indexOf("=")+1, thisseg.indexOf("hr "));
                            vals[6] = thisseg.substring(thisseg.indexOf("hr ")+3, thisseg.indexOf("min"));
                            vals[7] = thisseg.substring(thisseg.indexOf("min ")+4, thisseg.indexOf("sec"));
                        }
                    }
                }
                buf = new StringBuffer();
                buf.append(vals[0]);
                for (int j = 1; j < vals.length; j++) {
                    buf.append(", ").append(vals[j]);
                }
                fw.println(buf.toString());
                nResCollected ++;
            }

            // Done
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nResCollected;

    }

    /**
     * Collect the Run end information from "eplusout.end" file in each directory
     */
    public int collectReportsCSV(String filename, Vector<String> reports) {
        // Example report in the reports list collected by the agent:
        // "[this_job_id] EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        int nResCollected = 0;
        try {
            PrintWriter fw = new PrintWriter(new FileWriter(OutputDir + filename));
            // Header
            String[] header = {"Id",
                               "JobID",
                               "Message",
                               "Warnings",
                               "Errors",
                               "Hours",
                               "Minutes",
                               "Seconds"};

            StringBuffer buf = new StringBuffer(header[0]);
            for (int i = 1; i < header.length; i++) {
                buf.append(", ").append(header[i]);
            }
            fw.println(buf.toString());

            // Jobs
            for (int i = 0; i < reports.size(); i++) {
                // For each record, do:
                // Get result information
                String info = reports.get(i);
                String[] vals = new String [header.length];
                vals[0] = Integer.toString(i);
                vals[1] = info.substring(info.indexOf("[")+1, info.indexOf("]"));
                buf = new StringBuffer();
                buf.append(vals[0]).append(", ").append(vals[1]);
                // remove the job_id part
                info = info.substring(info.indexOf("] ") + 2);
                if (info.length()>0 && ! info.startsWith("!") && info.contains("--")) {
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
                            vals[5] = thisseg.substring(thisseg.indexOf("=")+1, thisseg.indexOf("hr "));
                            vals[6] = thisseg.substring(thisseg.indexOf("hr ")+3, thisseg.indexOf("min"));
                            vals[7] = thisseg.substring(thisseg.indexOf("min ")+4, thisseg.indexOf("sec"));
                        }
                    }
                    for (int j = 2; j < vals.length; j++) {
                        buf.append(", ").append(vals[j]);
                    }
                }
                fw.println(buf.toString());
                nResCollected ++;
            }

            // Done
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nResCollected;

    }

    /**
     * Collect the Run end information from "eplusout.end" file in each directory
     */
    public int collectReportsSQL(String filename, String tablename, String dir) {
        return this.collectReportsSQL(filename, tablename, dir, false);
    }

    /**
     * Collect the Run end information from "eplusout.end" file in each directory
     */
    public int collectReportsSQL(String filename, String tablename, String dir, boolean remove) {
        // Example eplusout.end:
        // "EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        int nResCollected = 0;
        try {
            PrintWriter fw = new PrintWriter(new FileWriter(OutputDir + filename));
            // Create table
            fw.println("CREATE TABLE `" + tablename + "` (");
            fw.println("`Id` int NOT NULL,");
            fw.println("`JobID` varchar(255) NOT NULL,");
            fw.println("`Message` varchar(255) default NULL,");
            fw.println("`Warnings` int default NULL,");
            fw.println("`Errors` int default NULL,");
            fw.println("`Hours` smallint default NULL,");
            fw.println("`Minutes` smallint default NULL,");
            fw.println("`Seconds` float default NULL,");
            fw.println("PRIMARY KEY  (`ID`),");
            fw.println("KEY `JobID` (`JobID`)");
            fw.println(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            fw.println();
            // Insert command
            fw.println("INSERT INTO `" + tablename + "` (`ID`, `JobID`, `Message`, `Warnings`, `Errors`, `Hours`, `Minutes`, `Seconds`) VALUES");

            // Jobs
            List <EPlusTask> JobQueue = JobOwner.getAgent().getFinishedJobs();
            boolean emptyline = false;
            for (int i = 0; i < JobQueue.size(); i++) {

                if (i > 0 && ! emptyline) fw.println(",");

                // For each job, do:
                EPlusTask job = JobQueue.get(i);
                // Get result information
                String info = EPlusTask.getResultInfo(dir, job, remove);
                if (info != null && ! info.startsWith("!")) {
                    fw.print("(" + i + ", ");
                    fw.print("'" + job.getJobID() + "', ");
                    int marker = info.indexOf("--");
                    fw.print("'" + info.substring(0, marker) + "'");
                    info = info.substring(marker + 2).trim();
                    String [] segment = info.split(";");
                    String [] vals = new String [5];
                    for (int j=0; j<segment.length; j++) {
                        String thisseg = segment[j].trim();
                        if (thisseg.endsWith("Warning")) {
                            vals[0] = thisseg.substring(0, thisseg.indexOf(" "));
                        }else if (thisseg.endsWith("Severe Errors")) {
                            vals[1] = thisseg.substring(0, thisseg.indexOf(" "));
                        }else if (thisseg.startsWith("Elapsed Time")) {
                            vals[2] = thisseg.substring(thisseg.indexOf("=")+1, thisseg.indexOf("hr "));
                            vals[3] = thisseg.substring(thisseg.indexOf("hr ")+3, thisseg.indexOf("min"));
                            vals[4] = thisseg.substring(thisseg.indexOf("min ")+4, thisseg.indexOf("sec"));
                        }
                    }
                    for (int j=0; j<vals.length; j++) fw.print(", " + vals[j]);
                    fw.print(")");
                    emptyline = false;
                }else {
                    emptyline = true;
                }
                nResCollected ++;
            }
            fw.println(";");
            fw.println();

            // Done
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nResCollected;

    }

    /**
     * Collect the Run end information from "eplusout.end" file in each directory
     */
    public int collectReportsSQL(String filename, String tablename, Vector<String> reports) {
        // Example eplusout.end:
        // "EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        int nResCollected = 0;
        try {
            PrintWriter fw = new PrintWriter(new FileWriter(OutputDir + filename));
            // Create table
            fw.println("CREATE TABLE `" + tablename + "` (");
            fw.println("`Id` int NOT NULL,");
            fw.println("`JobID` varchar(255) NOT NULL,");
            fw.println("`Message` varchar(255) default NULL,");
            fw.println("`Warnings` int default NULL,");
            fw.println("`Errors` int default NULL,");
            fw.println("`Hours` smallint default NULL,");
            fw.println("`Minutes` smallint default NULL,");
            fw.println("`Seconds` float default NULL,");
            fw.println("PRIMARY KEY  (`ID`),");
            fw.println("KEY `JobID` (`JobID`)");
            fw.println(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            fw.println();
            // Insert command
            fw.println("INSERT INTO `" + tablename + "` (`ID`, `JobID`, `Message`, `Warnings`, `Errors`, `Hours`, `Minutes`, `Seconds`) VALUES");

            // Jobs
            for (int i = 0; i < reports.size(); i++) {
                // close previous record
                if (i > 0) fw.println(",");
                // index
                fw.print("(" + i + ", ");
                // job_id
                String info = reports.get(i);
                String job_id = info.substring(info.indexOf("[")+1, info.indexOf("]"));
                fw.print("'" + job_id + "', ");
                // remove the job_id part
                info = info.substring(info.indexOf("] ") + 2);
                // rest of the record
                if (info.length()>0 && ! info.startsWith("!")) {
                    int marker = info.indexOf("--");
                    fw.print("'" + info.substring(0, marker) + "'");
                    info = info.substring(marker + 2).trim();
                    String [] segment = info.split(";");
                    String [] vals = new String [5];
                    for (int j=0; j<segment.length; j++) {
                        String thisseg = segment[j].trim();
                        if (thisseg.endsWith("Warning")) {
                            vals[0] = thisseg.substring(0, thisseg.indexOf(" "));
                        }else if (thisseg.endsWith("Severe Errors")) {
                            vals[1] = thisseg.substring(0, thisseg.indexOf(" "));
                        }else if (thisseg.startsWith("Elapsed Time")) {
                            vals[2] = thisseg.substring(thisseg.indexOf("=")+1, thisseg.indexOf("hr "));
                            vals[3] = thisseg.substring(thisseg.indexOf("hr ")+3, thisseg.indexOf("min"));
                            vals[4] = thisseg.substring(thisseg.indexOf("min ")+4, thisseg.indexOf("sec"));
                        }
                    }
                    for (int j=0; j<vals.length; j++) fw.print(", " + vals[j]);
                    fw.print(")");
                }else {
                    fw.print("'" + info + "', , , , )");
                }
                nResCollected ++;
            }
            fw.println(";");
            fw.println();

            // Done
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nResCollected;

    }

    /**
     * Collect results in individual csv files and assemble a table of all results.
     * The name of individual job result files is expected to be [job_id].csv
     * @param filename The output file name
     * @param dir The directory where all results are located
     * @return Number of results collected
     */
    public int collectResultsCSV(String filename, String dir) {
        return collectResultsCSV(filename, dir, false);
    }

    /**
     * Collect results in individual csv files and assemble a table of all results.
     * The name of individual job result files is expected to be [job_id].csv
     * @param filename The output file name
     * @param dir The directory where all results are located
     * @param remove whether or not to remove individual csv files after collection
     * @return Number of results collected
     */
    public int collectResultsCSV(String filename, String dir, boolean remove) {
        // Example [job_id].csv:
        // row 1 - Collumn heading: comma delimitted text
        // row 2 and on - data: comma delimitted numerial text

        // Number of jobs whose results have been collected.
        int nResCollected = 0;
        try {
            // Data table to hold all data
            Vector <Vector <String>> DataTable = new Vector <Vector <String>> ();
            // Number of rows of the data table
            int NumRows = 0;
            // Header map - column header is mapped to column number
            HashMap <String, Integer> HeaderMap = new HashMap <String, Integer>();
            // List of headers for convenience
            Vector <String> Headers = new Vector<String>();
            // List of job_ids for each row
            Vector <String> Job_Ids = new Vector<String>();

            List <EPlusTask> JobQueue = JobOwner.getAgent().getFinishedJobs();
            // Collect Job results
            for (int i = 0; i < JobQueue.size(); i++) {
                // For each job, do:

                // Get job_id
                EPlusTask job = JobQueue.get(i);
                String job_id = job.getJobID();
                // Read job result file
                try {
                    File csv = new File(dir + job_id + "/eplusout.csv");
                    if (csv.exists()) {
                        BufferedReader fr = new BufferedReader(new FileReader(csv));
                        String line = fr.readLine();
                        if (line != null) {
                            // process first line, the column header
                            String [] headings = line.split("\\s*,\\s*");
                            int [] index = new int [headings.length];
                            for (int j=0; j<headings.length; j++) {
                                headings[j] = headings[j].trim();
                                if (! HeaderMap.containsKey(headings[j])) {
                                    index[j] = HeaderMap.size();
                                    HeaderMap.put(headings[j], index[j]);
                                    Headers.add(headings[j]);
                                    Vector<String> newcolumn = new Vector<String> ();
                                    newcolumn.setSize(NumRows);
                                    Collections.fill(newcolumn, "-");
                                    DataTable.add(newcolumn);
                                }else {
                                    index[j] = HeaderMap.get(headings[j]).intValue();
                                }
                            }
                            // the rest is data
                            line = fr.readLine();
                            while (line != null && line.trim().length() > 0) {
                                // add job_id to the list
                                Job_Ids.add(job_id);
                                // add a new row in the data table
                                for (int j=0; j<DataTable.size(); j++) {
                                    DataTable.get(j).add("-");
                                }
                                // fill in data from the result file
                                String [] data = line.split(",");
                                for (int j=0; j<data.length; j++) {
                                    DataTable.get(index[j]).set(NumRows, data[j]);
                                }
                                NumRows ++;
                                line = fr.readLine();
                            }
                        }
                        fr.close();
                        if (remove) csv.delete();
                        nResCollected ++;
                        job.setResultAvailable(true);
                    }else {
                        job.setResultAvailable(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } // done with loading

            // Write to result file
            PrintWriter fw = new PrintWriter (new FileWriter (OutputDir + filename));
            // get headings, first 2 are index and job_id
            fw.print("Id,Job_Id");
            for (int j=0; j<Headers.size(); j++) {
                fw.print(",");
                fw.print(Headers.get(j));
            }
            fw.println();
            // write data
            for (int i=0; i<NumRows; i++) {
                fw.print("" + i + "," + Job_Ids.get(i));
                for (int j=0; j<DataTable.size(); j++) {
                    fw.print(",");
                    fw.print(DataTable.get(j).get(i));
                }
                fw.println();
            }
            fw.flush();
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nResCollected;

    }

    /**
     * Assemble a table of all results from tables collected by each task object.
     * The results from each task is collected and stored in a Vector of string Vectors.
     * @param filename The output file name
     * @param results A list of the contents of the result files
     * @return Number of results collected
     */
    public int collectResultsCSV(String filename, Vector<ArrayList<String>> results) {
        // Example results list content:
        // row 0 - job_id
        // row 1 - Collumn heading: comma delimitted text
        // row 2 and on - data: comma delimitted numerial text

        // Number of jobs whose results have been collected.
        int nResCollected = 0;
        try {
            // Data table to hold all data
            Vector <Vector <String>> DataTable = new Vector <Vector <String>> ();
            // Number of rows of the data table
            int NumRows = 0;
            // Header map - column header is mapped to column number
            HashMap <String, Integer> HeaderMap = new HashMap <String, Integer>();
            // List of headers for convenience
            Vector <String> Headers = new Vector<String>();
            // List of job_ids for each row
            Vector <String> Job_Ids = new Vector<String>();

            // Collect Job results
            for (int i = 0; i < results.size(); i++) {
                // For each job, do:
                ArrayList<String> job = results.get(i);
                // Get job_id
                if (job != null && job.size() > 0) {
                    String job_id = job.get(0);
                    if (job.size() > 2) {
                        // process first line, the column header
                        String [] headings = job.get(1).split("\\s*,\\s*");
                        int [] index = new int [headings.length];
                        for (int j=0; j<headings.length; j++) {
                            headings[j] = headings[j].trim();
                            if (! HeaderMap.containsKey(headings[j])) {
                                index[j] = HeaderMap.size();
                                HeaderMap.put(headings[j], index[j]);
                                Headers.add(headings[j]);
                                Vector<String> newcolumn = new Vector<String> ();
                                newcolumn.setSize(NumRows);
                                Collections.fill(newcolumn, "-");
                                DataTable.add(newcolumn);
                            }else {
                                index[j] = HeaderMap.get(headings[j]).intValue();
                            }
                        }
                        for (int k=2; k<job.size(); k++) {
                            // the rest is data
                            String line = job.get(k);
                            if (line != null && line.trim().length() > 0) {
                                // add job_id to the list
                                Job_Ids.add(job_id);
                                // add a new row in the data table
                                for (int j=0; j<DataTable.size(); j++) {
                                    DataTable.get(j).add("-");
                                }
                                // fill in data from the result file
                                String [] data = line.split(",");
                                for (int j=0; j<data.length; j++) {
                                    DataTable.get(index[j]).set(NumRows, data[j]);
                                }
                                NumRows ++;
                            }
                        }
                        nResCollected ++;
                    }
                }
            } // done with loading

            // Write to result file
            PrintWriter fw = new PrintWriter (new FileWriter (OutputDir + filename));
            // get headings, first 2 are index and job_id
            fw.print("Id,Job_Id");
            for (int j=0; j<Headers.size(); j++) {
                fw.print(",");
                fw.print(Headers.get(j));
            }
            fw.println();
            // write data
            for (int i=0; i<NumRows; i++) {
                fw.print("" + i + "," + Job_Ids.get(i));
                for (int j=0; j<DataTable.size(); j++) {
                    fw.print(",");
                    fw.print(DataTable.get(j).get(i));
                }
                fw.println();
            }
            fw.flush();
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nResCollected;
    }

    /**
     * Collect results in the summary csv file and save a sql file for importing
     * into MySQL. This function takes the first row of the csv file to create a
     * table, then write a "LOAD DATA INFILE" command to import all data. The
     * structure of the database table is assume to be [int] [string] [string]
     * [double] ... [double].
     * @param sql_file SQL file name to be generated
     * @param tbl_name The name of the table to be generated
     * @param csv_file The CSV file that contains all results and a header row for column names
     * @param null_val_char The character (or string) used in the CSV file for missing values
     * @return Number of columns that the table contains
     */
    /**
     *
     * @return
     */
    public int collectResultsSQL(String sql_file, String tbl_name, String csv_file, String null_val_char) {
        // Example results.csv:
        // row 1 - Collumn heading: comma delimitted text [id] [job_id] [date/time] [data] ... [data]
        // row 2 and on - data: comma delimitted numerial text

        // LOAD DATA INFILE syntax example:
        // LOAD DATA INFILE 'simresults.csv' INTO TABLE result_table_name
        //      FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
        //      LINES TERMINATED BY '\n'
        //      IGNORE 1 LINES

        try {
            PrintWriter fw = new PrintWriter(new FileWriter(OutputDir + sql_file, false));
            // Create table
            fw.println("CREATE TABLE `" + tbl_name + "` (");
            fw.println("`Id` int NOT NULL,");
            fw.println("`JobID` varchar(255) NOT NULL,");
            fw.println("`Period` varchar(255) default NULL,");
            for (int i=0; i<100; i++)
                fw.println("`Data" + i + "` double default NULL,");
            fw.println("PRIMARY KEY  (`ID`),");
            fw.println("KEY `JobID` (`JobID`)");
            fw.println(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            fw.println();
            // Insert command
            fw.println("LOAD DATA INFILE `" + csv_file + "` INTO TABLE " + tbl_name + " FIELDS TERMINATED BY `,` OPTIONALLY ENCLOSED BY `\"` LINES TERMINATED BY `\\n` IGNORE 1 LINES");
            fw.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    /**
     * Collect results in the data table and save a sql file for importing
     * into MySQL. This function takes the first row of the data table to create a
     * database table, then insert all data rows into the table.
     * @param sql_file SQL file name to be generated
     * @param tbl_name The name of the table to be generated
     * @param results The data table
     * @param null_val_char The character (or string) used in the data table for missing values
     * @return Number of columns that the table contains
     */
    public int collectResultsSQL(String sql_file, String tbl_name, Vector<Vector<String>> results, String null_val_char) {
        // Example results list content:
        // row 0 - job_id
        // row 1 - Collumn heading: comma delimitted text
        // row 2 and on - data: comma delimitted numerial text

        // @todo collectResultsSQL is not implemented yet
        return -1;

//        // Number of jobs whose results have been collected.
//        int nResCollected = 0;
//        try {
//            // Data table to hold all data
//            Vector <Vector <String>> DataTable = new Vector <Vector <String>> ();
//            // Number of rows of the data table
//            int NumRows = 0;
//            // Header map - column header is mapped to column number
//            HashMap <String, Integer> HeaderMap = new HashMap <String, Integer>();
//            // List of headers for convenience
//            Vector <String> Headers = new Vector<String>();
//            // List of job_ids for each row
//            Vector <String> Job_Ids = new Vector<String>();
//
//            // Collect Job results
//            for (int i = 0; i < results.size(); i++) {
//                // For each job, do:
//                Vector<String> job = results.get(i);
//                // Get job_id
//                if (job != null && job.size() > 0) {
//                    String job_id = job.get(0);
//                    if (job.size() > 2) {
//                        // process first line, the column header
//                        String [] headings = job.get(1).split(",");
//                        int [] index = new int [headings.length];
//                        for (int j=0; j<headings.length; j++) {
//                            if (! HeaderMap.containsKey(headings[j])) {
//                                index[j] = HeaderMap.size();
//                                HeaderMap.put(headings[j], index[j]);
//                                Headers.add(headings[j]);
//                                Vector<String> newcolumn = new Vector<String> ();
//                                newcolumn.setSize(NumRows);
//                                Collections.fill(newcolumn, "-");
//                                DataTable.add(newcolumn);
//                            }else {
//                                index[j] = HeaderMap.get(headings[j]).intValue();
//                            }
//                        }
//                        for (int k=2; k<job.size(); k++) {
//                            // the rest is data
//                            String line = job.get(k);
//                            if (line != null && line.trim().length() > 0) {
//                                // add job_id to the list
//                                Job_Ids.add(job_id);
//                                // add a new row in the data table
//                                for (int j=0; j<DataTable.size(); j++) {
//                                    DataTable.get(j).add("-");
//                                }
//                                // fill in data from the result file
//                                String [] data = line.split(",");
//                                for (int j=0; j<data.length; j++) {
//                                    DataTable.get(index[j]).set(NumRows, data[j]);
//                                }
//                                NumRows ++;
//                            }
//                        }
//                        nResCollected ++;
//                    }
//                }
//            } // done with loading
//
//            // Write to result file
//            PrintWriter fw = new PrintWriter (new FileWriter (filename));
//            // get headings, first 2 are index and job_id
//            fw.print("ID,JOB_ID");
//            for (int j=0; j<Headers.size(); j++) {
//                fw.print(",");
//                fw.print(Headers.get(j));
//            }
//            fw.println();
//            // write data
//            for (int i=0; i<NumRows; i++) {
//                fw.print("" + i + "," + Job_Ids.get(i));
//                for (int j=0; j<DataTable.size(); j++) {
//                    fw.print(",");
//                    fw.print(DataTable.get(j).get(i));
//                }
//                fw.println();
//            }
//            fw.flush();
//            fw.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return nResCollected;
    }


}
