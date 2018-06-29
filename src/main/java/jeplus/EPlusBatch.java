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
package jeplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import jeplus.agent.*;
import jeplus.data.ParameterItemV2;
import jeplus.data.Counter;
import jeplus.data.ExecutionOptions;
import jeplus.data.FileList;
import jeplus.data.RVX;
import jeplus.data.RVX_Constraint;
import jeplus.data.RVX_Objective;
import jeplus.data.RVX_UserVar;
import jeplus.postproc.ResultCollector;
import jeplus.simpleparser.Parser;
import jeplus.simpleparser.SimpleParserError;
import jeplus.util.CsvUtil;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * <p>EPlusBatch is the execution manager of the tasks. It is implemented
 * a java Thread. </p>
 * <p>Copyright (c) 2008</p>
 * <p>Company: IESD, DMU</p>
 * @author Yi Zhang
 * @version 1.0
 * @since 0.1
 */
public class EPlusBatch extends Thread {

    /** Logger */
    final static private org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusBatch.class);

    /**
     * Type of job strings used for identifying input format of the jobs strings
     */
    public enum JobStringType {
        INDEX,
        VALUE,
        ID,
        FILE
    };
    
    public static enum SampleType {
        SHUFFLE,
        LHS,
        SOBOL
    };

    protected String IDprefixEP = "EP";
    protected String IDprefixTRN = "TR";
    protected String IDprefix = IDprefixEP;
    public String BatchId = null;

    /** Link to the GUI */
    protected JEPlusFrameMain GUI = null;

    /** The job queue */
    protected List<EPlusTask> JobQueue = new ArrayList<>();
    
    /** The job counter for compiling jobs */
    protected Counter JobCounter = new Counter ();

    /** jE+ project (to replace EPlusWorkEnv) */
    protected JEPlusProjectV2 Project = null;

    /** E+ work environment for individual jobs */
    //protected EPlusWorkEnv Env = new EPlusWorkEnv();
    /** Idf template file list */
    protected FileList IdfFiles = null;
    /** Weather file list */
    protected FileList WthrFiles = null;
    /** Parameter tree */
    //protected DefaultMutableTreeNode ParamTree = null;

    /** Top level task group */
    // protected EPlusTaskGroup TaskGroup = null;
    /** Information of the batch jobs, including progress and results */
    protected EPlusBatchInfo Info = null;

    /** Execution agent */
    protected EPlusAgent Agent = null;

    /** Flag for simulation running status */
    protected boolean SimulationRunning = false;

    /** Whether or not to use Job Archive to avoid repetition of simulation */
    protected boolean EnableArchive = false;

    /** Archive of simulated jobs, for avoiding repeating simulations */
    private HashMap <String, ArrayList<ArrayList<double []>>> JobArchive = null;

    public EPlusBatch () {
        Info = new EPlusBatchInfo();
    }
    
    /**
     * Constructor
     * @param gui Reference to Main GUI
     * @param project The project definition object
     */
    public EPlusBatch(JFrame gui, JEPlusProjectV2 project) {
        // BatchId = IDprefix + "_" + (EPlusBatch.NextID++);
        GUI = (JEPlusFrameMain) gui;
        Project = project;
        if (Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS) {
            IDprefix = IDprefixEP;
        }else {
            IDprefix = IDprefixTRN;
        }
        BatchId = IDprefix + "_" + Project.getProjectID();
        //Project.resolveToEnv(Env);
        Info = new EPlusBatchInfo();
    }


//    /**
//     * Clone constructor with specified number of jobs
//     * @param batch Source of Batch
//     * @param njobs Number of jobs to put in queue
//     * @param randomsrc Random generator source. If null is received, jobs are executed without shuffling
//     */
//    public EPlusBatch(EPlusBatch batch, int njobs, Random randomsrc) {
//        this (null, batch.Project);
//        this.GUI = batch.GUI;
//        this.BatchId = batch.BatchId;
//        this.Agent = batch.Agent;
//        this.Collector = batch.Collector;
//        this.IDprefix = batch.IDprefix;
//        this.Info = batch.Info;
//        //this.TaskGroup = batch.TaskGroup;
//        this.WthrFiles = batch.WthrFiles;
//        this.IdfFiles = batch.IdfFiles;
//        if (this.JobQueue == null) 
//            JobQueue = new Vector<EPlusTask>();
//        else
//            JobQueue.removeAllElements();
//        if (njobs <=0) { // sample the first job in each idf/chain combination
//        }
//        
//    }

    public JEPlusProjectV2 getProject() {
        return Project;
    }

    public void setProject(JEPlusProjectV2 Project) {
        this.Project = Project;
        if (Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS) {
            IDprefix = IDprefixEP;
        }else {
            IDprefix = IDprefixTRN;
        }
        BatchId = IDprefix + "_" + Project.getProjectID();
    }

    public boolean isEnableArchive() {
        return EnableArchive;
    }

    /**
     * Enable or disable archive. Archive allows simulation being bypassed if
     * result is already available.
     * @param EnableArchive
     */
    public void setEnableArchive(boolean EnableArchive) {
        this.EnableArchive = EnableArchive;
        if (this.EnableArchive && JobArchive == null)
            JobArchive = new HashMap <> ();
    }

    public HashMap<String, ArrayList<ArrayList<double[]>>> getJobArchive() {
        return JobArchive;
    }

    public JEPlusFrameMain getGUI() {
        return GUI;
    }

    public void setGUI(JEPlusFrameMain GUI) {
        this.GUI = GUI;
    }

    public EPlusAgent getAgent() {
        return Agent;
    }

    public void setAgent(EPlusAgent Agent) {
        this.Agent = Agent;
        this.Agent.setJobOwner(this);
    }

    public boolean isSimulationRunning() {
        return SimulationRunning;
    }

    public void setSimulationRunning(boolean SimulationRunning) {
        this.SimulationRunning = SimulationRunning;
        if (this.getGUI() != null) getGUI().setSimulationRunning(SimulationRunning);
    }
    
    public int getRemainingJobs () {
        return SimulationRunning ? Agent.getJobQueue().size() : -1;
    }

    /**
     * Decode IDF files string and store them, with directory, in an array
     * @param dir Default directory for IDF/IMF/DCK/LST files. Entries in the LST files should contain only relative paths to this directory (??)
     * @param files Input files string. ';' delimited list of IDF/IMF/DCK/LST files
     * @return Validation result: true if all files are available
     */
    public boolean setIdfFiles(String dir, String files) {
        if (dir != null && files != null) {
            IdfFiles = new FileList(this.BatchId, "T", dir);
            IdfFiles.addAll(Project.parseFileListString(dir, files));
            if (IdfFiles.size() > 0) {
                if (!IdfFiles.validate(Info.ValidationErrors)) {
                    Info.addValidationError("Some of model template files are missing. Check file directory.");
                    Info.ValidationSuccessful = false;
                    return false;
                }
                Info.setModels(IdfFiles);
                return true;
            }
        }
        IdfFiles = null;
        Info.setModels(null);
        Info.addValidationError("Cannot extract model template file. Check input string.");
        Info.ValidationSuccessful = false;
        return false;
    }

    /**
     * Decode Weather files string and store them, with directory, in an array
     * @param dir Default directory for EPW/LST files. Entries in the LST files should contain only relative paths to this directory (??)
     * @param files Input files string. ';' delimited list of EPW/LST files
     * @return Validation result: true if all files are available
     */
    public boolean setWthrFiles(String dir, String files) {
        if (dir != null && files != null) {
            WthrFiles = new FileList(this.BatchId, "W", dir);
            WthrFiles.addAll(Project.parseFileListString(dir, files));
            if (WthrFiles.size() > 0) {
                if (!WthrFiles.validate(Info.ValidationErrors)) {
                    Info.addValidationError("Some of Weather files are missing. Check file directory.");
                    Info.ValidationSuccessful = false;
                    return false;
                }
                Info.setWeatherFiles(WthrFiles);
                return true;
            }
        }
        WthrFiles = null;
        Info.setWeatherFiles(null);
        Info.addValidationError("Cannot extract Weather file. Check input string.");
        Info.ValidationSuccessful = false;
        return false;
    }

    /**
     * Copy project information to a work env object; also resolves relative paths
     * @return Work Env object containing all job details
     */
    public EPlusWorkEnv getResolvedEnv() {
        EPlusWorkEnv Env = new EPlusWorkEnv ();
        Project.resolveToEnv(Env);
        return Env;
    }

    /**
     * Validate the composition of the project. Validation includes 
     * @return a batch info object containing descriptions and error messages
     */
    public EPlusBatchInfo validateProject() {
        Info = new EPlusBatchInfo();
        if (Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS) {
            // Parse and validate IDF files
            setIdfFiles(Project.resolveIDFDir(), Project.getIDFTemplate());
            // Pares and validate Weather files
            setWthrFiles(Project.resolveWeatherDir(), Project.getWeatherFile());
            // Check RVX
            if (Project.getRVIFile() != null) {
                try {
                    Project.setRvx(RVX.getRVX(Project.getRVIFile(), Project.getBaseDir()));
                } catch (IOException ex) {
                    logger.error("Error loading rvi/rvx file.", ex);
                    Info.addValidationError("Cannot read rvi/rvx file " + Project.getRVIFile());
                    Info.ValidationSuccessful = false;
                    Project.setRvx(null);
                }
            }
            if (Project.getRvx() == null) {
                Info.addValidationError("RVX is not available. No simulation result will be collected.");
                Info.ValidationSuccessful = false;
            }
        }else if (Project.getProjectType() == JEPlusProjectV2.ModelType.TRNSYS) {
            // Parse and validate DCK files
            setIdfFiles(Project.resolveDCKDir(), Project.getDCKTemplate());
        }else if (Project.getProjectType() == JEPlusProjectV2.ModelType.INSEL) {
            // Parse and validate DCK files
            setIdfFiles(Project.resolveINSELDir(), Project.getINSELTemplate());
        }
        // Check ExecSettings
        validateExecSettings ();
        // Validate Parameter tree
        validateParamTree();
        // done
        return Info;
    }

    /**
     * Validate the execution agent's settings including work dir
     * @return Validation successful or not
     */
    protected boolean validateExecSettings () {
        boolean ok = true;
        File dir = new File (Project.resolveWorkDir());
        try { dir.mkdirs(); } catch (Exception ex) {}
        if (! (dir.isDirectory() && dir.canWrite())) {
            Info.addValidationError("Specified Work Directory " + dir.getParent() + " is not present or cannot be written to.");
            ok = false;
        }
        if (Project.getExecSettings().getExecutionType() == ExecutionOptions.LOCAL_PBS_CONTROLLER) {
            ok &= checkFile (Project.resolvePBSscriptFile(), Info);
        }else if (Project.getExecSettings().getExecutionType() == ExecutionOptions.NETWORK_JOB_SERVER) {
            ok &= checkFile (Project.resolveServerConfigFile(), Info);
        }else if (Project.getExecSettings().getExecutionType() == ExecutionOptions.REMOTE_SERVER) {
            // check remote server?
        }
        Info.ValidationSuccessful &= ok;
        return ok;
    }

    /**
     * Check read accessibility of a file
     * @param fn File to be checked
     * @param info Validation error details holder
     * @return File is accessible or not
     */
    protected static boolean checkFile (String fn, EPlusBatchInfo info) {
        File file = new File (fn);
        if (file.canRead() && file.isFile()) return true;
        info.addValidationError("File " + fn + " is not accessible.");
        return false;
    }

    /**
     * Validate the parameter tree
     * @return successful or else
     */
    public boolean validateParamTree() {

        EPlusBatchInfo info = this.Info;
        boolean ok = true;

        DefaultMutableTreeNode ParaTree = Project.getParamTree();
        if (ParaTree != null) {
            DefaultMutableTreeNode thisleaf = ParaTree.getFirstLeaf();

            // get this leaf's parent path. Each path has to be of equal length and
            // contains the same set of search strings
            // ArrayList<ParameterItemV2> thischain = new ArrayList<ParameterItemV2> ();
            try {
                // First leaf
                Object[] path = thisleaf.getUserObjectPath();
                this.Info.ParamChains.add(new ArrayList (Arrays.asList(path)));
                int chainlen = path.length;
                for (int i = 0; i < chainlen; i++) {
                    if (!info.SearchStrings.contains(((ParameterItemV2) path[i]).getSearchString())) {
                        info.SearchStrings.add(((ParameterItemV2) path[i]).getSearchString());
                    } else {// Error
                        info.addValidationError("Duplicate search string: Level " + i + " - Node " + path[i].toString());
                        ok = false;
                    }
                }
                // other leaves
                thisleaf = thisleaf.getNextLeaf();
                while (thisleaf != null) {
                    path = thisleaf.getUserObjectPath();
                    this.Info.ParamChains.add(new ArrayList (Arrays.asList(path)));
                    if (chainlen != path.length) {
                        info.addValidationError("Uneven parameter chain: to Node " + path[path.length - 1].toString());
                        ok = false;
                    }
                    ArrayList<String> thisset = new ArrayList<>();
                    for (int i = 0; i < path.length; i++) {
                        // test against the first chain
                        if (!info.SearchStrings.contains(((ParameterItemV2) path[i]).getSearchString())) {
                            // Found new search string
                            info.addValidationError("Found new search string: Level " + i + " - Node " + path[i].toString());
                            ok = false;
                        }
                        // test for duplication
                        if (!thisset.contains(((ParameterItemV2) path[i]).getSearchString())) {
                            thisset.add(((ParameterItemV2) path[i]).getSearchString());
                        } else {// Error
                            info.addValidationError("Duplicate search string: Level " + i + " - Node " + path[i].toString());
                            ok = false;
                        }
                    }

                    thisleaf = thisleaf.getNextLeaf();
                }
            } catch (ClassCastException cce) {
                logger.error("", cce);
            }

            // Check duplication in Item Ids. All items in the tree should have
            // a unique id.
            try {
                for (Enumeration e = ParaTree.breadthFirstEnumeration(); e.hasMoreElements();) {
                    ParameterItemV2 item = (ParameterItemV2) ((DefaultMutableTreeNode) e.nextElement()).getUserObject();
                    if (!info.ShortNames.contains(item.getID())) {
                        info.ShortNames.add(item.getID());
                        info.ParamList.add(item);
                    } else {// Error
                        info.addValidationError("Duplicate Parameter ID found in: Node " + item.toString());
                        ok = false;
                    }
                }
            } catch (ClassCastException cce) {
                logger.error("", cce);
            }
        } else {
            info.addValidationError("Parameter tree doesn't exist.");
            ok = false;
        }
        info.ValidationSuccessful = info.ValidationSuccessful && ok;
        return ok;
    }

    /**
     * Writes an index table of the current job queue, listing selected alt values
     * of each job against search strings
     * @param filename File name
     * @param dir The path where the output file should be stored
     * @return Number of jobs written
     */
    public int writeJobIndexCSV (String filename, String dir) {
        int nResCollected = 0;
        File fn = new File (dir + filename);
        try (PrintWriter fw = new PrintWriter(new FileWriter(fn))) {
            ArrayList<String> headers = new ArrayList <> ();
            headers.add("#");
            headers.add("Job_ID");
            headers.add("WeatherFile");
            headers.add("ModelFile");
            ArrayList<String> paramsstrs = this.Info.getSearchStrings();
            ArrayList<String> allsstrs = new ArrayList<> ();
            for (String paramsstr : paramsstrs) {
                allsstrs.addAll(Arrays.asList(paramsstr.split("\\s*\\|\\s*")));
            }
            headers.addAll(allsstrs);

            // Create a map for sorting alt values
            HashMap<String, String> map = new HashMap<> ();
            for (String str : headers) {
                map.put(str, "");
            }

            // Write table header
            StringBuffer buf = new StringBuffer(headers.get(0));
            for (int i = 1; i < headers.size(); i++) {
                buf.append(",").append(headers.get(i));
            }

            // Print Jobs
            for (int i = 0; i < JobQueue.size(); i++) {
                // For each job, do:
                EPlusTask job = JobQueue.get(i);
                switch (job.getWorkEnv().getProjectType()) {
                    case TRNSYS:
                        if (i==0) {
                            // Print table header
                            buf.delete(11,24);
                            fw.println(buf.toString());                        
                        }
                        buf = new StringBuffer();
                        buf.append(i).append(",");
                        buf.append(job.getJobID()).append(",");
                        buf.append(job.getWorkEnv().getDCKTemplate()).append(",");
                        break;
                    case INSEL:
                        if (i==0) {
                            // Print table header
                            buf.delete(11,24);
                            fw.println(buf.toString());                        
                        }
                        buf = new StringBuffer();
                        buf.append(i).append(",");
                        buf.append(job.getJobID()).append(",");
                        buf.append(job.getWorkEnv().getINSELTemplate()).append(",");
                        break;
                    case EPLUS:
                    default:
                        if (i==0) {
                            // Print table header
                            fw.println(buf.toString());                        
                        }
                        buf = new StringBuffer();
                        buf.append(i).append(",");
                        buf.append(job.getJobID()).append(",");
                        buf.append(job.getWorkEnv().getWeatherFile()).append(",");
                        buf.append(job.getWorkEnv().getIDFTemplate()).append(",");
                }
                for (int j=0; j<job.SearchStringList.size(); j++) {
                    map.put(job.SearchStringList.get(j), job.AltValueList.get(j));                        
                }
                for (int j=0; j<allsstrs.size(); j++) {
                    buf.append(map.get(allsstrs.get(j)));
                    if (j < allsstrs.size()-1) buf.append(",");                        
                }
                fw.println(buf.toString());
                nResCollected ++;
            }
            fw.close();
        } catch (Exception ex) {
            logger.error("", ex);
            nResCollected = -1;
        }
        return nResCollected;
    }
    
    /**
     * Writes an index table of the current job queue, listing selected alt values
     * of each job against search strings
     * @param filename File name
     * @param dir The path where the output file should be stored
     * @return Number of jobs written
     */
    public int writeJobListFile (String filename, String dir) {
        int nResCollected = 0;
        File fn = new File (dir + filename);
        try (PrintWriter fw = new PrintWriter(new FileWriter(fn))) {
            // Write notes
            fw.println("# This file can be used as an example job list file for specify your own job set. Please note the generated");
            fw.println("# file may be incorrect if combinatorial parameters ('|' separated search and value strings) are used.");
            fw.println("# ");

            // Get header
            ArrayList<String> headers = new ArrayList <> ();
            headers.add("# Job_ID");
            if (this.getResolvedEnv().getProjectType() == JEPlusProjectV2.ModelType.EPLUS) {
                headers.add("WeatherFile");
            }
            headers.add("ModelFile");
            headers.addAll(this.Info.getSearchStrings());

            // Create a map for sorting alt values
            HashMap<String, String> map = new HashMap<> ();
            for (String str : headers) {
                map.put(str, "");
            }

            // Write table header
            StringBuffer buf = new StringBuffer(headers.get(0));
            for (int i = 1; i < headers.size(); i++) {
                buf.append(", ").append(headers.get(i));
            }
            // Print table header
            fw.println(buf.toString());

            // Print Jobs
            for (EPlusTask job : JobQueue) {
                buf = new StringBuffer();
                buf.append(job.getJobID()).append(", ");
                switch (job.getWorkEnv().getProjectType()) {
                    case TRNSYS:
                        buf.append(job.getWorkEnv().getDCKTemplate()).append(", ");
                        break;
                    case INSEL:
                        buf.append(job.getWorkEnv().getINSELTemplate()).append(", ");
                        break;
                    case EPLUS:
                    default:
                        buf.append(job.getWorkEnv().getWeatherFile()).append(", ");
                        buf.append(job.getWorkEnv().getIDFTemplate()).append(", ");
                }
                
                for (int j=0; j<job.SearchStringList.size(); j++) {
                    map.put(job.SearchStringList.get(j), job.AltValueList.get(j));                        
                }
                for (Iterator it=Info.getSearchStrings().iterator(); it.hasNext();) {
                    buf.append(map.get((String)it.next()));
                    if (it.hasNext()) buf.append(", ");                        
                }
                fw.println(buf.toString());
                nResCollected ++;
            }
        } catch (Exception ex) {
            logger.error("", ex);
            nResCollected = -1;
        }
        return nResCollected;
    }
    
//    /**
//     * Create and write full parameter indexes to .csv files in the output directory
//     * @return A report of the list of files and in which directory they've been written
//     */
//    public String writeProjectIndexCSV() {
//        StringBuilder buf = new StringBuilder ("Writing project index. The following files: \n");
//        String dir = this.getResolvedEnv().getParentDir();
//        String fn;
//        if (Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS) {
//            // idf index
//            fn = "IndexIDF.csv";
//            if (IdfFiles.exportCSV(dir + fn)) buf.append("\t").append(fn).append("\n");
//            // Weather index
//            fn = "IndexWthr.csv";
//            if (WthrFiles.exportCSV(dir + fn)) buf.append("\t").append(fn).append("\n");
//        }else if (Project.getProjectType() == JEPlusProjectV2.ModelType.TRNSYS) {
//            // idf index
//            fn = "IndexDCK.csv";
//            if (IdfFiles.exportCSV(dir + fn)) buf.append("\t").append(fn).append("\n");
//        }else if (Project.getProjectType() == JEPlusProjectV2.ModelType.INSEL) {
//            // idf index
//            fn = "IndexINSEL.csv";
//            if (IdfFiles.exportCSV(dir + fn)) buf.append("\t").append(fn).append("\n");
//        }
//        // Parameter index
//        if (Info.isValid() && Info.ParamList != null) {
//            for (ParameterItemV2 item : Info.ParamList) {
//                fn = "Index" + item.getID() + ".csv";
//                if (item.exportCSV(dir + fn)) buf.append("\t").append(fn).append("\n");
//            }
//        }
//        // Job index
//        if (JobQueue != null) {
//            fn = "IndexJobs.csv";
//            if (this.exportCSV(dir + fn)) buf.append("\t").append(fn).append("\n");
//        }
//        buf.append("have been created in ").append(dir).append("\n");
//        // done
//        return buf.toString();
//    }
//
//
//    /**
//     * Create and write full parameter indexes to a sql script file in the output directory
//     * @param dbname
//     * @param tableprefix
//     * @return A report of the list of tables and in which directory they've been written
//     */
//    public String writeProjectIndexSQL(String dbname, String tableprefix) {
//        // idf index
//        String sqlfile = "CreateJobDB.sql";
//        StringBuilder buf = new StringBuilder ("Creating project index SQL script " + sqlfile);
//        String dir = this.getResolvedEnv().getParentDir();
//        buf.append(" in ").append(dir).append("...\n");
//        sqlfile = dir + sqlfile;
//        boolean ok = true;
//        // Create and use db
//        try (PrintWriter fw = new PrintWriter(new FileWriter(sqlfile))) {
//            fw.println("--\n-- Database: `" + dbname + "`\n-- Created by jEPlus\n--");
//            fw.println("CREATE DATABASE `" + dbname + "` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;");
//            fw.println("USE `" + dbname + "`;");
//            fw.println("-- --------------------------------------------------------");
//            fw.println();
//            buf.append("The script creates a database named ").append(dbname).append(", with the following tables: \n");
//        }catch (Exception ex) {
//            logger.error("", ex);
//            buf.append(" failed. Please check the file is writable.\n");
//            return buf.toString();
//        }
//
//        String tn;
//        if (Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS) {
//            tn = tableprefix+"IndexIDF";
//            // write idf index table
//            if (IdfFiles.exportSQL(sqlfile, tn)) buf.append("\t").append(tn).append("\n");
//            // Weather index
//            tn = tableprefix+"IndexWthr";
//            if (WthrFiles.exportSQL(sqlfile, tn)) buf.append("\t").append(tn).append("\n");
//        }else if (Project.getProjectType() == JEPlusProjectV2.ModelType.TRNSYS) {
//            tn = tableprefix+"IndexDCK";
//            // write idf index table
//            if (IdfFiles.exportSQL(sqlfile, tn)) buf.append("\t").append(tn).append("\n");
//        }else if (Project.getProjectType() == JEPlusProjectV2.ModelType.INSEL) {
//            tn = tableprefix+"IndexINSEL";
//            // write idf index table
//            if (IdfFiles.exportSQL(sqlfile, tn)) buf.append("\t").append(tn).append("\n");
//        }
//        // Parameter index
//        if (Info.isValid() && Info.ParamList != null) {
//            for (ParameterItemV2 item : Info.ParamList) {
//                tn = tableprefix+"Index" + item.getID();
//                if (item.exportSQL(sqlfile, tn)) buf.append("\t").append(tn).append("\n");
//            }
//        }
////        // Job index
////        if (JobQueue != null) {
////            tn = tableprefix+"IndexJobs";
////            if (this.exportSQL(sqlfile, tn)) buf.append("\n").append(tn).append("\n");
////        }
//        // done
//        buf.append("Done!\n");
//        return buf.toString();
//    }
//
    public boolean exportCSV(String fn) {
        try (PrintWriter fw = new PrintWriter(new FileWriter(fn))) {
            String[] header = new String[8 + Info.ShortNames.size()];
            ArrayList<String> tag = new ArrayList<>();
            int idx = 0;
            header[idx++] = "INDEX";
            header[idx++] = "GROUP_ID";
            tag.add(this.IDprefix);
            int tag_idx_offset = 1;
            header[idx++] = "IDF_ID";
            tag.add(IdfFiles.getIDprefix());
            header[idx++] = "WTHR_ID";
            tag.add(WthrFiles.getIDprefix());
            for (String ParID : Info.ShortNames) {
                header[idx++] = ParID;
                tag.add(ParID);
            }
            header[idx++] = "JOBNAME";
            header[idx++] = "WORKDIR";
            header[idx++] = "EXECUTED";
            header[idx++] = "RESULT_AVAILABLE";

            StringBuffer buf = new StringBuffer(header[0]);
            for (int i = 1; i < header.length; i++) {
                buf.append(", ").append(header[i]);
            }
            fw.println(buf.toString());

            // Jobs
            for (int i = 0; i < JobQueue.size(); i++) {
                // For each job, do:
                EPlusTask job = JobQueue.get(i);
                String taskid = job.TaskID;
                String[] pairs = taskid.split("-");
                String[] vals = new String [header.length];
                vals[0] = Integer.toString(i);
                for (String pair : pairs) {
                    String[] val = pair.split("_");
                    idx = tag.indexOf(val[0]);
                    if (idx >= 0) {
                        vals[idx + tag_idx_offset] = val[1];
                    }
                }
                vals[vals.length - 4] = taskid;
                vals[vals.length - 3] = job.getWorkingDir();
                vals[vals.length - 2] = Boolean.toString(job.isExecuted());
                vals[vals.length - 1] = Boolean.toString(job.isResultAvailable());
                buf = new StringBuffer(vals[0]);
                for (int j = 1; j < vals.length; j++) {
                    buf.append(", ").append(vals[j]);
                }
                fw.println(buf.toString());
            }
            return true;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return false;
    }

    public boolean exportSQL(String fn, String tablename) {
        try (PrintWriter fw = new PrintWriter(new FileWriter(fn, true))) {
            fw.println("CREATE TABLE `" + tablename + "` (");
            // Compile Table Header
            String[] header = new String[8 + Info.ShortNames.size()];
            ArrayList<String> tag = new ArrayList<>();
            int idx = 0;
            header[idx++] = "ID";
            fw.println("`ID` INT NOT NULL,");
            header[idx++] = "Group_ID";
            fw.println("`Group_ID` SMALLINT NOT NULL,");
            tag.add(this.IDprefix);
            int tag_idx_offset = 1;
            header[idx++] = "IDF_ID";
            fw.println("`IDF_ID` SMALLINT NOT NULL,");
            tag.add(IdfFiles.getIDprefix());
            header[idx++] = "Weather_ID";
            fw.println("`Weather_ID` SMALLINT NOT NULL,");
            tag.add(WthrFiles.getIDprefix());
            for (String ParID : Info.ShortNames) {
                header[idx++] = ParID;
                tag.add(ParID);
                fw.println("`" + ParID + "` SMALLINT default NULL,");
            }
            header[idx++] = "JobName";
            fw.println("`JobName` varchar(255) NOT NULL,");
            header[idx++] = "WorkDir";
            fw.println("`WorkDir` varchar(255) NOT NULL,");
            header[idx++] = "Executed";
            fw.println("`Executed` BOOL default false,");
            header[idx++] = "ResultReady";
            fw.println("`ResultReady` BOOL default false,");
            fw.println("PRIMARY KEY  (`ID`)");
            fw.println(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            fw.println();

            // Insert command
            fw.print("INSERT INTO `" + tablename + "` (");
            for (int i = 0; i < header.length; i++) {
                if (i > 0) fw.print(",");
                fw.print(" `" + header[i] + "`");
            }
            fw.println(") VALUES");

            // Insert records for Jobs
            for (int i = 0; i < JobQueue.size(); i++) {
                // For each job, do:
                EPlusTask job = JobQueue.get(i);
                String taskid = job.TaskID;
                String[] pairs = taskid.split("-");
                String[] vals = new String [header.length];
                vals[0] = Integer.toString(i);
                for (String pair : pairs) {
                    String[] val = pair.split("_");
                    idx = tag.indexOf(val[0]);
                    if (idx >= 0) {
                        vals[idx + tag_idx_offset] = val[1];
                    }
                }
                vals[vals.length - 4] = taskid;
                vals[vals.length - 3] = job.getWorkingDir();
                vals[vals.length - 2] = Boolean.toString(job.isExecuted());
                vals[vals.length - 1] = Boolean.toString(job.isResultAvailable());

                if (i > 0) fw.println(",");
                fw.print("(");
                for (int j = 0; j < vals.length-3; j++) {
                    fw.print("" + vals[j] + ",");
                }
                fw.print("'" + vals[vals.length - 3] + "', ");
                fw.print("'" + vals[vals.length - 2] + "', ");
                fw.print(vals[vals.length - 1] + ")");
            }
            fw.println(";");
            fw.println();
            return true;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return false;
    }

    /**
     * Building the jobs of the project and keeping only those in the index list.
     * @param indexlist
     * @return Number of jobs found
     */
    public long buildJobs(long [] indexlist) {
        boolean largeproject = false; 
        // If building whole project and tree depth is deeper than 6, use the automatic job names
        if (indexlist == null) largeproject = this.Info.getParamTreeDepth() > 6; 
        // Clear JobQueue (?)
        JobQueue.clear();
        // Reset TaskGroup's job counter anyway
        this.JobCounter.reset();
        Counter ptr = new Counter();
        if (Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS) {
            for (int j = 0; j < WthrFiles.size(); j++) {
                for (int i = 0; i < IdfFiles.size(); i++) {
                    // env is needed to carry individual job settings
                    EPlusWorkEnv env = new EPlusWorkEnv();
                    Project.resolveToEnv(env);
                    env.IDFTemplate = IdfFiles.get(i);
                    env.EPlusVersion = IDFmodel.getEPlusVersionInIDF (env.IDFDir + env.IDFTemplate);
                    env.WeatherFile = WthrFiles.get(j);
                    String tag = BatchId + "-" + IdfFiles.getIDprefix() + "_" + i +
                            "-" + WthrFiles.getIDprefix() + "_" + j;
                    EPlusTaskGroup TaskGroup = new EPlusTaskGroup(env, largeproject? BatchId : tag, null, null);
                    TaskGroup.compile(Project, JobQueue, JobCounter, Project.getParamTree(), largeproject, indexlist, ptr);
                }
            }
        }else if (Project.getProjectType() == JEPlusProjectV2.ModelType.TRNSYS) {
            for (int i = 0; i < IdfFiles.size(); i++) {
                // env is needed to carry individual job settings
                EPlusWorkEnv env = new EPlusWorkEnv();
                Project.resolveToEnv(env);
                env.DCKTemplate = IdfFiles.get(i);
                String tag = BatchId + "-" + IdfFiles.getIDprefix() + "_" + i;
                EPlusTaskGroup TaskGroup = new EPlusTaskGroup(env, largeproject? BatchId : tag, null, null);
                TaskGroup.compile(Project, JobQueue, JobCounter, Project.getParamTree(), largeproject, indexlist, ptr);
            }
        }else if (Project.getProjectType() == JEPlusProjectV2.ModelType.INSEL) {
            for (int i = 0; i < IdfFiles.size(); i++) {
                // env is needed to carry individual job settings
                EPlusWorkEnv env = new EPlusWorkEnv();
                Project.resolveToEnv(env);
                env.INSELTemplate = IdfFiles.get(i);
                String tag = BatchId + "-" + IdfFiles.getIDprefix() + "_" + i;
                EPlusTaskGroup TaskGroup = new EPlusTaskGroup(env, largeproject? BatchId : tag, null, null);
                TaskGroup.compile(Project, JobQueue, JobCounter, Project.getParamTree(), largeproject, indexlist, ptr);
            }
        }
        // Force garbage collection
        System.gc();
        // Done
        return JobQueue.size();
    }

    /**
     * Build a task group from the variable tree in the GUI
     * @return Number of jobs created
     */
    public long buildJobs() {
        long [] ind = null;
        return buildJobs(ind);
    }

    /**
     * A Primitive form of buildJobs() that creates a task group from the input
     * material. The definition of the jobs relies on the Project object attached
     * to this Batch. The input job array contains only parameter values organised
     * in 2-D array, in which each row representing one job.
     * - The first element of each row is the job id string used for identifying the job;
     * - The second element is the index of weather file, i.e. the i-th weather file in the project's weather list
     * - The third element of each row is the index of the IDF template to be used;
     * The width of the array equals the depth of the parameter tree plus 3.
     * @param jobArray The 2-d array contains the parameter values (as String)
     * for the jobs.
     * @return Number of jobs created
     */
    public int buildJobs(String [][] jobArray) {
        // Clear job queue first
        JobQueue.clear();
        if (Info == null || ! Info.isValid()) {
            logger.error("EPlusBatch.buildJobs(): Project has not been validated or is invalid.");
            return -4;
        }
        if (jobArray == null || jobArray.length < 1 || jobArray[0] == null) {
            logger.error("EPlusBatch.buildJobs(): Invalid job array.");
            return -5;
        }
        String [] searchstr = Info.getSearchStringsArray();
        if (searchstr.length != jobArray[0].length - 3) {
            logger.error("EPlusBatch.buildJobs(): Supplied job array does not comply with project definition. "
                    + searchstr.length + " variables are present, whereas " + jobArray[0].length + " values are given.");
            return -6;
        }
        // env is needed to carry individual job settings
        EPlusWorkEnv env = new EPlusWorkEnv();
        Project.resolveToEnv(env);
        if (Info != null && Info.isValid()) {
            for (int i=0; i<jobArray.length; i++) {
                String tag = jobArray[i][0];
                if (isEnableArchive() && JobArchive.containsKey(tag)) {
                    continue;
                }else {
                    // Create a new instance of env by duplication
                    env = new EPlusWorkEnv(env);
                    EPlusTask Task = null;
                    ArrayList<String> keys = new ArrayList<> ();
                    ArrayList<String> vals = new ArrayList<> ();
                    switch (env.getProjectType()) {
                        case TRNSYS:
                            try {
                                env.DCKTemplate = IdfFiles.get(Integer.valueOf(jobArray[i][2]));
                            }catch (NumberFormatException nfe) {
                                env.DCKTemplate = jobArray[i][2];
                            }
                            // Collect search strings
                            for (int j=0; j<searchstr.length; j++) {
                                keys.add(searchstr[j]);
                                vals.add(jobArray[i][j+3]);
                            }
                            keys.trimToSize();
                            vals.trimToSize();
                            Task = new TRNSYSTask(env, tag, keys, vals);
                            break;
                        case INSEL:
                            try {
                                env.INSELTemplate = IdfFiles.get(Integer.valueOf(jobArray[i][2]));
                            }catch (NumberFormatException nfe) {
                                env.INSELTemplate = jobArray[i][2];
                            }
                            // Collect search strings
                            for (int j=0; j<searchstr.length; j++) {
                                keys.add(searchstr[j]);
                                vals.add(jobArray[i][j+3]);
                            }
                            keys.trimToSize();
                            vals.trimToSize();
                            Task = new INSELTask(env, tag, keys, vals);
                            break;
                        case EPLUS:
                        default:
                            try {
                                env.WeatherFile = WthrFiles.get(Integer.valueOf(jobArray[i][1]));
                            }catch (NumberFormatException nfe) {
                                env.WeatherFile = jobArray[i][1];
                            }
                            try {
                                env.IDFTemplate = IdfFiles.get(Integer.valueOf(jobArray[i][2]));
                            }catch (NumberFormatException nfe) {
                                env.IDFTemplate = jobArray[i][2];
                            }
                            env.EPlusVersion = IDFmodel.getEPlusVersionInIDF (env.IDFDir + env.IDFTemplate);
                            // Collect search strings
                            for (int j=0; j<searchstr.length; j++) {
                                keys.add(searchstr[j]);
                                vals.add(jobArray[i][j+3]);
                            }
                            keys.trimToSize();
                            vals.trimToSize();
                            Task = new EPlusTask(env, tag, keys, vals);
                    }
                    JobQueue.add(Task);
                }
            }
        }
        return JobQueue.size();
    }

    /**
     * A Primitive form of buildJobs() that creates a task group from the input
     * material. The definition of the jobs relies on the Project object attached
     * to this Batch. The input job array contains only parameter values organised
     * in 2-D array, in which each row representing one job.
     * - The first element of each row is the job id string used for identifying the job;
     * - The second element is the index of weather file, i.e. the i-th weather file in the project's weather list
     * - The third element of each row is the index of the IDF template to be used;
     * The width of the array equals the depth of the parameter tree plus 3.
     * @param jobs The map contains the job names and the parameter values (as Object)
     * for the jobs.
     * @return Number of jobs created
     */
    public int buildJobs(Map<String, Map<String, Object>> jobs) {
        // Clear job queue first
        JobQueue.clear();
        if (Info == null || ! Info.isValid()) {
            logger.error("EPlusBatch.buildJobs(): Project has not been validated or is invalid.");
            return -4;
        }
        if (jobs == null || jobs.size() < 1) {
            logger.error("EPlusBatch.buildJobs(): Invalid job map.");
            return -5;
        }
        
        // env is needed to carry individual job settings
        EPlusWorkEnv env = new EPlusWorkEnv();
        Project.resolveToEnv(env);
        if (Info != null && Info.isValid()) {
            Object [] params = Project.getParamTree().getFirstLeaf().getUserObjectPath();
            for (String jobid : jobs.keySet()) {
                Map<String, Object> job = jobs.get(jobid);
                // Create a new instance of env by duplication
                env = new EPlusWorkEnv(env);
                EPlusTask Task = null;
                // Weather file first
                if (job.containsKey("W")) {
                    env.WeatherFile = job.get("W").toString();
                }else {
                    // If not specified, default to the first weather file
                    env.WeatherFile = WthrFiles.get(0);
                }
                // Model file next
                if (job.containsKey("T")) {
                    env.IDFTemplate = job.get("T").toString();
                }else {
                    // If not specified, default to the first weather file
                    env.IDFTemplate = IdfFiles.get(0);
                }
                env.EPlusVersion = IDFmodel.getEPlusVersionInIDF (env.IDFDir + env.IDFTemplate);
                // Parameters now
                ArrayList<String> keys = new ArrayList<> ();
                ArrayList<String> vals = new ArrayList<> ();
                for (Object param : params) {
                    ParameterItemV2 item = (ParameterItemV2) param;
                    keys.add(item.getSearchString());
                    if (job.containsKey(item.getID())) {
                        vals.add(job.get(item.getID()).toString());
                    }else {
                        vals.add(item.getAlternativeValues(Project)[0]);
                    }
                }
                keys.trimToSize();
                vals.trimToSize();
                switch (env.getProjectType()) {
                    case TRNSYS:
                        Task = new TRNSYSTask(env, jobid, keys, vals);
                        break;
                    case INSEL:
                        Task = new INSELTask(env, jobid, keys, vals);
                        break;
                    case EPLUS:
                    default:
                        Task = new EPlusTask(env, jobid, keys, vals);
                }
                JobQueue.add(Task);
            }
        }
        return JobQueue.size();
    }

    /**
     * Filter the current job queue with the supplied list of job IDs. If the 
     * option (keep) is set to true, the IDs are for the jobs to keep in the queue; 
     * otherwise they are for the jobs to be removed from the queue.
     * @param jobids An array of job IDs
     * @param keep Option to keep the jobs in the list (true) or to keep the jobs not in the list (false)
     * @return Number of jobs remain in the list
     */
    public int filterJobs (String [] jobids, boolean keep) {
        Arrays.sort(jobids);
        for (int i=JobQueue.size()-1; i>=0; i--) {
            EPlusTask job = JobQueue.get(i);
            if (Arrays.binarySearch(jobids, job.getJobID()) >= 0) { // job id found in the list
                if (! keep) JobQueue.remove(i);
            }else { // wasn't found
                if (keep) JobQueue.remove(i);
            }
        }
        return JobQueue.size();
    }

    public List<EPlusTask> getJobQueue() {
        return this.JobQueue;
    }

    public int getNumberOfJobs() {
        return (JobQueue == null) ? 0 : JobQueue.size();
    }

    /**
     *
     * @return
     */
    public EPlusBatchInfo getBatchInfo() {
        return Info;
    }

    public int getNumberOfIDFs () {
        return this.IdfFiles.size();
    }

    public int getNumberOfWeathers () {
        return this.WthrFiles.size();
    }
    
    /**
     * Simulate the given set of jobs, wait for simulations to finish, and return
     * results in a HashMap. For the details of the jobArray, see <code>
     * buildJobs(String [][] jobArray) </code>
     * @param jobArray An array of jobs, see <code>buildJobs(String [][] jobArray) </code>
     * @return 
     * @deprecated Not used?
     */
    public HashMap<String, ArrayList<ArrayList<double []>>>  simulateJobSet (String [][] jobArray) {
        runJobSet (jobArray);
        // wait for jobs to finish
        try {
            do {
                Thread.sleep(2000);
            }while (this.isSimulationRunning());
        }catch (InterruptedException iex) {
            this.getAgent().setStopAgent(true);
        }
//        RVX rvx = null;
//        try {
//            rvx = RVX.getRVX(Project.getRVIDir() + Project.getRVIFile());
//        }catch (IOException ioe) {
//            logger.error("Error reading the project RVI/RVX file...", ioe);
//        }
        RVX rvx = Project.getRvx();
        return getSimulationResults(this.getAgent().getResultCollectors(), this.getResolvedEnv().getParentDir(), rvx, (this.isEnableArchive()?JobArchive:null));
    }

    /**
     * Build the given set of jobs but not start running. For the details of the jobArray, see <code>
     * buildJobs(String [][] jobArray) </code>
     * @param type Type of job string (INDEX, FILE, VALUE, or ID)
     * @param jobstr The string containing the list of jobs
     */
    public void prepareJobSet (JobStringType type, String jobstr) {
        String [][] jobArray;
        switch (type) {
            case INDEX:
                String [] jobs = jobstr.split("\\s*;\\s*");
                jobArray = new String [jobs.length][];
                Object [] items = Project.getParamTree().getFirstLeaf().getUserObjectPath();
                for (int i=0; i<jobs.length; i++) {
                    jobArray[i] = jobs[i].split("\\s*,\\s*");
                    for (int j=3; j<jobArray[i].length; j++) {
                        jobArray[i][j] = ((ParameterItemV2)items[j-3]).getAlternativeValues(Project)[Integer.parseInt(jobArray[i][j])];
                    }
                }
                this.buildJobs(jobArray);
                break;
            case FILE: // in an external file, job strings must be in a VALUE form. Each end-of-line is treated as a ";". '#' or '!' Comment lines are filtered.
//                try {
//                    BufferedReader fr = new BufferedReader (new FileReader (jobstr));
//                    StringBuilder buf = new StringBuilder ();
//                    String line = fr.readLine();
//                    while (line !=  null) {
//                        if (line.contains("#")) {
//                            line = line.substring(0, line.indexOf("#"));
//                        }
//                        if (line.contains("!")) {
//                            line = line.substring(0, line.indexOf("!"));
//                        }
//                        line = line.trim();
//                        if (line.length() > 0)
//                            buf.append(line).append(line.endsWith(";") ? "" : ";");
//                        line = fr.readLine();
//                    }
//                }catch (Exception ex) {
//                    logger.error("", ex);
//                    jobstr = "";
//                }
                // no break here, to have the new jobStr processed as VALUE
                jobArray = CsvUtil.parseCSVwithComments(new File (jobstr));
                this.buildJobs(jobArray);
                break;
            case VALUE:
                jobs = jobstr.split("\\s*;\\s*");
                jobArray = new String [jobs.length][];
                for (int i=0; i<jobs.length; i++) {
                    jobArray[i] = jobs[i].split("\\s*,\\s*");
                }
                this.buildJobs(jobArray);
                break;
            case ID:
                jobs = jobstr.split("\\s*;\\s*");
                this.buildJobs();
                this.filterJobs(jobs, true);
                break;
            default:
        }
    }

    /**
     * Run simulation on all jobs in the project 
     */
    public void runAll () {
        validateProject();
        if (getBatchInfo().isValidationSuccessful()) {
            this.buildJobs();
            // Start simulation
            new Thread(this).start();
        }
    }

    /**
     * Run simulation on the jobs in the given job list string or file
     * @param type Type of job string (INDEX, FILE, VALUE, or ID)
     * @param jobstr The string containing the list of jobs
     */
    public void runJobSet (JobStringType type, String jobstr) {
        validateProject();
        if (getBatchInfo().isValidationSuccessful()) {
            this.prepareJobSet(type, jobstr);
            // Start simulation
            new Thread(this).start();
        }
    }

    /**
     * Simulate the given set of jobs. For the details of the jobArray, see <code>
     * buildJobs(String [][] jobArray) </code>
     * @param jobArray An array of jobs, see <code>
     * buildJobs(String [][] jobArray) </code>
     */
    public void runJobSet (String [][] jobArray) {
        validateProject();
        if (getBatchInfo().isValidationSuccessful()) {
            this.buildJobs(jobArray);
            new Thread(this).start();
        }
    }

    /**
     * Simulate the given set of jobs. For the details of the jobArray, see <code>
     * buildJobs(String [][] jobArray) </code>
     * @param jobs A map of jobs
     */
    public void runJobSet (Map<String, Map<String, Object>> jobs) {
        validateProject();
        if (getBatchInfo().isValidationSuccessful()) {
            this.buildJobs(jobs);
            new Thread(this).start();
        }
    }

    /**
     * Run simulation on a Latin Hypercube sample. For each parameter, if 
     * probability distribution function is not defined, it is treated as 
     * uniform discrete distribution.
     * @param opt
     * @param n Sample size of LHS. 5 x number of variables is recommended
     * @param randomsrc
     */
    public void runSample (SampleType opt, int n, Random randomsrc) {
        validateProject();
        if (getBatchInfo().isValidationSuccessful()) {
            // Get the sample set
            String [][] sample;
            long [] samplen;
            switch (opt) {
                case LHS:
                    sample = Project.getLHSJobList(n, randomsrc);
                    this.buildJobs(sample);
                    break;
                case SOBOL:
                    sample = Project.getSobolJobList(n, randomsrc);
                    this.buildJobs(sample);
                    break;
                case SHUFFLE:
                default:
                    samplen = this.getRandomJobList(n, randomsrc);
                    this.buildJobs(samplen);
            }
            // Run jobs
            new Thread(this).start();
        }
    }

//    /**
//     * Run simulation on a Latin Hypercube sample. For each parameter, if 
//     * probability distribution function is not defined, it is treated as 
//     * uniform discrete distribution.
//     * @param n Sample size of LHS. 5 x number of variables is recommended
//     * @param randomsrc
//     */
//    public void runLHSample (int n, Random randomsrc) {
//        validateProject();
//        if (getBatchInfo().isValidationSuccessful()) {
//            // Run jobs
//            this.buildJobs(Project.getLHSJobList(n, randomsrc));
//            new Thread(this).start();
//        }
//    }
//
//    /**
//     * Run simulation on a set of randomly selected samples. 
//     * @param njobs Sample size
//     * @param randomsrc Random generator
//     */
//    public void runRandomSample (int njobs, Random randomsrc) {
//        validateProject();
//        if (getBatchInfo().isValidationSuccessful()) {
//            this.buildJobs(getRandomJobList(njobs, randomsrc));
//            // Start simulation
//            new Thread(this).start();
//        }
//    }

    /**
     * Create a random sample by shuffling existing jobs
     * @param njobs Sample size
     * @param randomsrc Random generator
     * @return Index list of selected jobs
     */
    public long [] getRandomJobList (int njobs, Random randomsrc) {
        long alljobs = Info.getTotalNumberOfJobs(Project);
        // Check njobs not exceeding total number of jobs
        njobs = (int)Math.min((long)njobs, alljobs);
        long [] list = new long [njobs];
        if (randomsrc != null) {
            ArrayList<Long> alist = new ArrayList<> ();
            if (alljobs <= 1000000) {
                for (int i=0; i<alljobs; i++) alist.add(new Long (i));
                Collections.shuffle(alist, randomsrc);
                for (int i=0; i<njobs; i++) list[i] = alist.get(i);
            }else {
                while (alist.size() < njobs) {
                    Long newjob = (long)Math.floor(randomsrc.nextDouble() * alljobs - 0.00001);
                    if (! alist.contains(newjob)) alist.add(newjob);
                }
                for (int i=0; i<njobs; i++) list[i] = alist.get(i);
            }
        }else {
            for (int i=0; i<njobs; i++) list[i] = i;
        }
        Arrays.sort(list);
        return list;
    }

    public String [][] getTestJobList () {
        int m = this.getNumberOfIDFs();
        int n = Info.ParamChains.size();
        String [][] jobs = new String [m * n] [Info.getSearchStrings().size() + 3];
        // For each IDF first
        for (int i=0; i<m; i++) {
            // Collect jobs from parameter chains
            for (int j=0; j<n; j++) {
                ArrayList chain = Info.ParamChains.get(j);
                int jobidx = i * n + j;
                jobs[jobidx][0] = BatchId + "-" + IdfFiles.getIDprefix() + "_" + i + 
                        (Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS ? 
                        "-" + WthrFiles.getIDprefix() + "_" + 0 : "");  // job_id
                jobs[jobidx][1] = Integer.toString(0);  // weather id
                jobs[jobidx][2] = Integer.toString(i);  // idf id
                for (int k=0; k<chain.size(); k++) {
                    ParameterItemV2 item = (ParameterItemV2)chain.get(k);
                    jobs[jobidx][k+3] = item.getAlternativeValues(Project)[0];
                    jobs[jobidx][0] += "-" + item.getID() + "_0";
                }
            }
        }
        return jobs;
    }
    
    public void runTest () {
        validateProject();
        if (getBatchInfo().isValidationSuccessful()) {
            this.buildJobs(getTestJobList());
            // Start simulation
            new Thread(this).start();
        }
    }
    
    /**
     * Run as a thread
     */ 
    @Override
    public void run() {
        if (this.Agent != null) {
            Agent.setJobOwner(this);
            Agent.purgeJobQueue(); //??
            if (GUI != null) Agent.setGUIPanel(GUI.getOutputPanel());
            Agent.addJobs(JobQueue);
            new Thread (Agent).start();
        }
    }

    /**
     * Load result headers from "SimResults.csv" to an array
     * @param rcs
     * @param result_folder
     * @param rvx
     * @return An array containing merged [table-column]
     */
    public static ArrayList<String> getSimulationResultHeaders(ArrayList<ResultCollector> rcs, String result_folder, RVX rvx) {
        // Example SimResults.csv:
        // row 1 - Collumn heading: comma delimitted text
        // row 2 and on - data: comma delimitted text, starting with line id, job id, date, data ....

        ArrayList<String> Results = new ArrayList<> ();
        int cCount = 0;
        for (ResultCollector rc : rcs) {
            ArrayList<String> fns;
//            if (rc.getResultFiles().isEmpty()) {
//                fns = rc.getExpectedResultFiles(rvx);
//            } else {
//                fns = rc.getResultFiles();
//            }
            if (rvx != null) {
                fns = rc.getExpectedResultFiles(rvx);
            } else {
                fns = rc.getResultFiles();
            }
            for (String fn : fns) {
                try (BufferedReader fr = new BufferedReader (new FileReader (result_folder + fn))) {
                    String line = fr.readLine();    // Read only the first line (headers)
                    if (line != null && line.trim().length() > 0) {
                        String [] items = line.split("\\s*,\\s*");
                        for (int k=3; k<items.length; k++) {
                            Results.add("c" + cCount + ": " + items[k].trim());
                            cCount ++;
                        }
                    }
                }catch (Exception ex) {
                    logger.error("", ex);
                }
            }
        }
        return Results;
    }

    /**
     * Load results from "SimResults.csv" to a double array
     * @param rcs
     * @param result_folder
     * @param rvx
     * @param archive
     * @return A HashMap containing [table][row][column] referenced by job_id. 
     */
    public static HashMap<String, ArrayList<ArrayList<double []>>> getSimulationResults(ArrayList<ResultCollector> rcs, String result_folder, RVX rvx, HashMap<String, ArrayList<ArrayList<double []>>> archive) {
        // Example SimResults.csv:
        // row 1 - Collumn heading: comma delimitted text
        // row 2 and on - data: comma delimitted text, starting with line id, job id, date, data ....

        HashMap<String, ArrayList<ArrayList<double []>>> Results = (archive != null) ? archive : new HashMap<String, ArrayList<ArrayList<double []>>> ();
        int tabidx = 0;
        for (ResultCollector rc : rcs) {
            ArrayList<String> fns;
//            if (rc.getResultFiles().isEmpty()) {
//                fns = rc.getExpectedResultFiles(rvx);
//            } else {
//                fns = rc.getResultFiles();
//            }
            // Changed to RVX defined
            if (rvx != null) {
                fns = rc.getExpectedResultFiles(rvx);
            } else {
                fns = rc.getResultFiles();
            }
            for (String fn : fns) {
                try (BufferedReader fr = new BufferedReader (new FileReader (new File(result_folder, fn)))) {
                    // Read the header row and work out how many data columns are expected
                    int NDataCols = 0;
                    String line = fr.readLine();    // the first line (headers)
                    if (line != null && line.trim().length() > 0) {
                        String [] items = line.split("\\s*,\\s*");
                        NDataCols = Math.max(items.length - 3, 0);
                        if (NDataCols > 0) {
                            // continue reading
                            line = fr.readLine();
                            while (line != null && line.trim().length() > 0) {
                                items = line.split("\\s*,\\s*");
                                if (items.length > 3) {
                                    String job_id = items[1];
                                    ArrayList<ArrayList<double []>> JobResult;
                                    if (Results.containsKey(job_id)) {
                                        JobResult = Results.get(job_id);
                                    }else {
                                        JobResult = new ArrayList<>();
                                        Results.put(job_id, JobResult);
                                    }
                                    ArrayList<double []> rec;
                                    if (JobResult.size() > tabidx) {
                                        rec = JobResult.get(tabidx);
                                        if (rec == null) {
                                            rec = new ArrayList<> ();
                                            JobResult.set(tabidx, rec);
                                        }
                                    }else {
                                        rec = new ArrayList<> ();
                                        JobResult.add(rec);
                                    }
                                    double [] data = new double [NDataCols];
                                    for (int k=0; k<NDataCols; k++) {
                                        try {
                                            data[k] = Double.parseDouble(items[k + 3]);
                                        }catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
                                            data[k] = 0;
                                        }
                                    }
                                    rec.add(data);
                                }
                                line = fr.readLine();
                            }
                        }
                    }
                }catch (Exception ex) {
                    logger.error("", ex);
                }
                tabidx ++;
            }
        }
        return Results;
    }
    
    /**
     * 
     * @param rvx
     * @return 
     */
    public static LinkedHashMap<String, String> getDerivativeResultHeaders (RVX rvx) {
        LinkedHashMap<String, String> Results = new LinkedHashMap<> ();
        if (rvx != null) {
            // User variables
            if (rvx.getUserVars() != null) {
                for (RVX_UserVar item : rvx.getUserVars()) {
                    if (item.isReport()) {
                        Results.put(item.getIdentifier(), item.getIdentifier() + ": " + item.getCaption());
                    }
                }
            }
            // Constraints
            if (rvx.getConstraints() != null) {
                for (RVX_Constraint item : rvx.getConstraints()) {
                    Results.put(item.getIdentifier(), item.getIdentifier() + ": " + item.getCaption());
                    if (item.isScaling()) {
                        Results.put("_" + item.getIdentifier(), item.getIdentifier() + ": normalized");
                    }
                }
            }
            // Objectives
            if (rvx.getObjectives() != null) {
                for (RVX_Objective item : rvx.getObjectives()) {
                    Results.put(item.getIdentifier(), item.getIdentifier() + ": " + item.getCaption());
                    if (item.isScaling()) {
                        Results.put("_" + item.getIdentifier(), item.getIdentifier() + ": normalized");
                    }
                }
            }
        }
        return  Results;
    }
    
    /**
     * 
     * @param rvx
     * @param data
     * @return 
     * @deprecated Replaced by getDerivedResultsJython()
     */
    public static HashMap<String, ArrayList<Double>> getDerivativeResults (RVX rvx, HashMap<String, ArrayList<ArrayList<double []>>> data) {
        if (rvx != null) {
            HashMap<String, ArrayList<Double>> derived = new HashMap<> ();
            if (data != null && !data.isEmpty()) {
                for (String job_id : data.keySet()) {
                    ArrayList<ArrayList<double []>> job_data = data.get(job_id);
                    // First pass to work out total number of columns
                    int ncol = 0;
                    for (ArrayList<double[]> table : job_data) {
                        ncol += table.get(table.size() - 1).length;
                    }
                    // Create a new parser
                    Parser parser = new Parser();
                    // First pass to collect original outputs, taking only the last row of the results, put each value to an variable name c??
                    int idx = 0;
                    for (ArrayList<double[]> table : job_data) {
                        double[] lastrow = table.get(table.size() - 1);
                        for (int i = 0; i < lastrow.length; i++) {
                            String statement = "c" + idx + "=" + lastrow[i];
                            try {
                                parser.resolve(statement);
                            }catch (SimpleParserError spe) {
                                logger.error("Error parsing conversion formula " + statement + ".");
                            }
                            idx ++;
                        }
                    }
                    ArrayList<Double> derived_row = new ArrayList<>();
                    // User variables
                    if (rvx.getUserVars() != null) {
                        for (RVX_UserVar item : rvx.getUserVars()) {
                            String statement = item.getIdentifier() + "=" + item.getFormula();
                            double val = 0;
                            try {
                                val = parser.resolve(statement);
                            }catch (SimpleParserError spe) {
                                logger.error("Error parsing conversion formula " + statement + ".");
                            }
                            if (item.isReport()) {
                                derived_row.add(val);
                            }
                        }
                    }
                    // Constraints
                    if (rvx.getConstraints() != null) {
                        for (RVX_Constraint item : rvx.getConstraints()) {
                            String statement = item.getIdentifier() + "=" + item.getFormula();
                            double val = 0;
                            try {
                                val = parser.resolve(statement);
                            }catch (SimpleParserError spe) {
                                logger.error("Error parsing conversion formula " + statement + ".");
                            }
                            derived_row.add(val);
                            if (item.isScaling()) {
                                derived_row.add(item.scale(val));
                            }
                        }
                    }
                    // Objectives
                    if (rvx.getObjectives() != null) {
                        for (RVX_Objective item : rvx.getObjectives()) {
                            String statement = item.getIdentifier() + "=" + item.getFormula();
                            double val = 0;
                            try {
                                val = parser.resolve(statement);
                            }catch (SimpleParserError spe) {
                                logger.error("Error parsing conversion formula " + statement + ".");
                            }
                            derived_row.add(val);
                            if (item.isScaling()) {
                                derived_row.add(item.scale(val));
                            }
                        }
                    }
                    // Add to derived results table
                    derived.put(job_id, derived_row);
                }
            }
            return derived;
        }
        return null;
    }
    
    /**
     * 
     * @param rvx
     * @param data
     * @return 
     */
    public static HashMap<String, HashMap<String, Double>> getDerivativeResultsJython (RVX rvx, HashMap<String, ArrayList<ArrayList<double []>>> data) {
        if (rvx != null) {
            // Initialize Jython script engine
            ScriptEngine engine = JEPlusProjectV2.getScript_Engine();

            HashMap<String, HashMap<String, Double>> derived = new HashMap<> ();
            if (data != null && !data.isEmpty()) {
                for (String job_id : data.keySet()) {
                    ArrayList<ArrayList<double []>> job_data = data.get(job_id);
                    // Process data
                    HashMap<String, Double> derived_row = calculateUserMetrics (rvx, job_data, engine);
                    // Add to derived results table
                    derived.put(job_id, derived_row);
                }
            }
            return derived;
        }
        return null;
    }
    
    /**
     * 
     * @param rvx
     * @param job_data
     * @param engine
     * @return 
     */
    public static HashMap<String, Double> calculateUserMetrics (RVX rvx, ArrayList<ArrayList<double []>> job_data, ScriptEngine engine) {
        HashMap<String, Double> metrics = new HashMap<> ();
        
        // First pass to collect original outputs, taking only the last row of the results, put each value to an variable name c??
        int idx = 0;
        for (ArrayList<double[]> table : job_data) {
            double[] lastrow = table.get(table.size() - 1);
            for (int i = 0; i < lastrow.length; i++) {
                String statement = "c" + idx + "=" + lastrow[i];
                try {
                    engine.eval(statement);
                }catch (ScriptException spe) {
                    logger.error("Error parsing expression " + statement + ".");
                }
                idx ++;
            }
        }
        
        // User variables
        if (rvx.getUserVars() != null) {
            for (RVX_UserVar item : rvx.getUserVars()) {
                String statement = item.getIdentifier() + "=" + item.getFormula();
                double val = 0;
                try {
                    engine.eval(statement);
                    val = Double.valueOf(engine.get(item.getIdentifier()).toString());
                }catch (ScriptException spe) {
                    logger.error("Error parsing expression " + statement + ".");
                }
                metrics.put(item.getIdentifier(), val);
            }
        }
        // Constraints
        if (rvx.getConstraints() != null) {
            for (RVX_Constraint item : rvx.getConstraints()) {
                String statement = item.getIdentifier() + "=" + item.getFormula();
                double val = 0;
                try {
                    engine.eval(statement);
                    val = Double.valueOf(engine.get(item.getIdentifier()).toString());
                }catch (ScriptException spe) {
                    logger.error("Error parsing expression " + statement + ".");
                }
                metrics.put(item.getIdentifier(), val);
                if (item.isScaling()) {
                    metrics.put("_" + item.getIdentifier(), item.scale(val));
                }
            }
        }
        // Objectives
        if (rvx.getObjectives() != null) {
            for (RVX_Objective item : rvx.getObjectives()) {
                String statement = item.getIdentifier() + "=" + item.getFormula();
                double val = 0;
                try {
                    engine.eval(statement);
                    val = Double.valueOf(engine.get(item.getIdentifier()).toString());
                }catch (ScriptException spe) {
                    logger.error("Error parsing expression " + statement + ".");
                }
                metrics.put(item.getIdentifier(), val);
                if (item.isScaling()) {
                    metrics.put("_" + item.getIdentifier(), item.scale(val));
                }
            }
        }

        // Done
        return metrics;
    }

    /** 
     * Write a combined table to a file with CSV format. The table contains indexes, simulation report, and all results (last row for each 
     * job.
     * 
     * @param rcs
     * @param result_folder
     * @param rvx
     * @param fname File name
     */
    public static void writeCombinedResultTable (ArrayList<ResultCollector> rcs, String result_folder, RVX rvx, String fname) {
        TreeMap<String, StringBuilder> table = new TreeMap<> ();
        int cCount = 0;
        // Get index table and report tables together
        for (ResultCollector rc : rcs) {
            if (rc.getIdxWriter() != null) {
                String fn = rc.getIdxWriter().getIndexFile();
                try (BufferedReader fr = new BufferedReader (new FileReader (fn));) {
                    String line = fr.readLine();
                    while (line != null && line.trim().length() > 0) {
                        String [] items = line.split("\\s*,\\s*", 3);
                        if (items.length == 3) {
                            if (table.containsKey(items[1])) {
                                table.get(items[1]).append(items[0]).append(",").append(items[1]).append(",").append(items[2]);
                            }else {
                                table.put(items[1], new StringBuilder (items[0]).append(",").append(items[1]).append(",").append(items[2]));
                            }
                        }
                        line = fr.readLine();
                    }
                }catch (Exception ex) {
                    logger.error("", ex);
                }
            }
            if (rc.getRepReader() != null && rc.getRepWriter() != null) {
                String fn = rc.getRepWriter().getReportFile();
                try (BufferedReader fr = new BufferedReader (new FileReader (fn));) {
                    String line = fr.readLine();
                    while (line != null && line.trim().length() > 0) {
                        String [] items = line.split("\\s*,\\s*", 3);
                        if (items.length == 3) {
                            if (table.containsKey(items[1])) {
                                table.get(items[1]).append(",").append(items[2]);
                            }else {
                                table.put(items[1], new StringBuilder (items[2]));
                            }
                        }
                        line = fr.readLine();
                    }
                }catch (Exception ex) {
                    logger.error("", ex);
                }
            }
            // Result table headers
//            if (rc.getResReader() != null && rc.getResWriter() != null) {
//                for (int j = 0; j < rc.getResultFiles().size(); j++) {
//                    String fn = result_folder + rc.getResultFiles().get(j);
//                    try (BufferedReader fr = new BufferedReader (new FileReader (fn));) {
//                        String line = fr.readLine();
//                        if (line != null && line.trim().length() > 0) {
//                            String [] items = line.split("\\s*,\\s*");
//                            if (items.length >= 2) { // Contains job_id
//                                StringBuilder row;
//                                if (table.containsKey(items[1])) {
//                                    row = table.get(items[1]);
//                                }else {
//                                    row = new StringBuilder ();
//                                    table.put(items[1], row);
//                                }
//                                for (int k=3; k<items.length; k++) {
//                                    row.append(",").append("c").append(cCount).append(": ").append(items[k]);
//                                    cCount ++;
//                                }
//                            }
//                        }
//                    }catch (Exception ex) {
//                        logger.error("", ex);
//                    }
//                }
//            }
        }
        
        ArrayList <String> res_header = EPlusBatch.getSimulationResultHeaders(rcs, result_folder, rvx);
        StringBuilder header_row = null;
        if (table.containsKey("Job_ID")) {
            header_row = table.get("Job_ID");
        }else {
            header_row = new StringBuilder ("Job_ID");
            table.put("Job_ID", header_row);
        }
        for (String col : res_header) {
            header_row.append(",").append(col);
        }
        
        // Fill in results
        HashMap<String, ArrayList<ArrayList<double []>>> data = getSimulationResults(rcs, result_folder, rvx, null);
        for (String key: data.keySet()) {
            ArrayList<ArrayList<double []>> jobdata = data.get(key);
            StringBuilder rec = table.get(key);
            for (ArrayList<double[]> jobtable : jobdata) {
                double[] row = jobtable.get(jobtable.size() - 1);
                for (int j=0; j<row.length; j++) {
                    rec.append(",").append(row[j]);
                }
            }
        }
        // Write to file
        try (PrintWriter fw = new PrintWriter (new FileWriter (RelativeDirUtil.checkAbsolutePath(fname, result_folder)))) {
            fw.println(table.remove("Job_ID").toString());
            for (String key: table.keySet()) {
                fw.println(table.get(key).toString());
            }
        }catch (Exception ex) {
            logger.error("", ex);
        }
    }
    
    /** 
     * Write the derived results table to a file in CSV format. 
     * @param rcs
     * @param result_folder
     * @param rvx
     * @param tablefile
     */
    public static void writeDerivedResultTable (ArrayList<ResultCollector> rcs, String result_folder, RVX rvx, String tablefile) {
        // Get headers
        LinkedHashMap<String, String> header = getDerivativeResultHeaders (rvx);
        // Fill in results
        HashMap<String, ArrayList<ArrayList<double []>>> data = getSimulationResults(rcs, result_folder, rvx, null);
        HashMap<String, HashMap<String, Double>> derived = getDerivativeResultsJython (rvx, data);
        // Write to file
        try (PrintWriter fw = new PrintWriter (new FileWriter (RelativeDirUtil.checkAbsolutePath(tablefile, result_folder)))) {
            fw.print("#,Job_ID,Reserved");
            for (String item : header.keySet()) {
                fw.print("," + header.get(item));
            }
            fw.println();
            int counter = 0;
            for (String jobid : derived.keySet()) {
                fw.print(counter);
                fw.print("," + jobid + ",");
                HashMap <String, Double> row = derived.get(jobid);
                for (String item : header.keySet()) {
                    fw.print(",");
                    fw.print(row.get(item));
                }
                fw.println();
            }
        }catch (Exception ex) {
            logger.error("Error write derived results table to " + tablefile, ex);
        }
    }
   
}
