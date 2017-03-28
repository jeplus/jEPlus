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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import jeplus.util.PythonTools;
import jeplus.util.RelativeDirUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

/**
 * EPlusTask class encapsulates the details of an EnergyPlus job to be run.
 * @author Yi Zhang
 * @version 0.5c
 * @since 0.1
 */
public class EPlusTask extends Thread implements EPlusJobItem, Serializable {

    /** Logger */
    final static private org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusTask.class);

    static final long serialVersionUID = 1587629823039332802L;
    
    public static class PythonFunc implements Serializable {
        String PyVersion = null;
        String ScriptFile = null;
        ArrayList<String> Args = null;
        
        public PythonFunc (String ver, String script, ArrayList<String> args) {
            PyVersion = ver;
            ScriptFile = script;
            Args = args;
        }

        public String getPyVersion() {
            return PyVersion;
        }

        public void setPyVersion(String PyVersion) {
            this.PyVersion = PyVersion;
        }

        public String getScriptFile() {
            return ScriptFile;
        }

        public void setScriptFile(String ScriptFile) {
            this.ScriptFile = ScriptFile;
        }

        public ArrayList<String> getArgs() {
            return Args;
        }

        public void setArgs(ArrayList<String> Args) {
            this.Args = Args;
        }
        
        /**
         * String that has been trimmed off "call(" and ")"
         * @param entry 
         * @return A new PythonFunc instance
         */
        public static PythonFunc fromString (String entry) {
            String [] fields = entry.split("\\s*,\\s*");
            ArrayList<String> args = new ArrayList<> ();
            for (int i=2; i<fields.length; i++) {
                args.add(fields[i]);
            }
            return new PythonFunc (fields[0], fields[1], args);
        }
        
        /**
         * 
         * @return 
         */
        @Override
        public String toString () {
            StringBuilder buf = new StringBuilder("call(");
            buf.append(PyVersion);
            buf.append(", ").append(ScriptFile);
            for (String Arg : Args) {
                buf.append(", ").append(Arg);
            }
            buf.append(")");
            return buf.toString();
        }
    }
    
    /** ID of this task */
    protected String TaskID = null;
    /** EnergyPlus working environment */
    protected EPlusWorkEnv WorkEnv = null;
    /** Search strings ArrayList */
    protected ArrayList<String> SearchStringList;
    /** Alt values ArrayList */
    protected ArrayList<String> AltValueList;
    /** List of Python scripts to execute before calling E+ binary */
    protected ArrayList<PythonFunc> Scripts;
    /** Results can be attached to this Task */
    protected ArrayList<String> AttachedResults = new ArrayList<>();
    /** Flag for execution status of this task */
    protected boolean Executed = false;
    /** Flag for the availability of the result */
    protected boolean ResultAvailable = false;

    /**
     * Create an instance of the EnergyPlus task
     * @param env Data packet including template and weather files
     * @param label Label of the task
     * @param id ID of the task
     * @param prevkey Search string list
     * @param prevval Alt value list
     */
    public EPlusTask(EPlusWorkEnv env, String label, int id, ArrayList<String> prevkey, ArrayList<String> prevval) {
        //TaskNumber = id;
        TaskID = label + "_" + id;
        WorkEnv = env;
        //WorkingDir = WorkEnv.ParentDir + TaskID + "/";
        //ParameterMap = params;
        parseTagsAndVals(prevkey, prevval);
    }

    /**
     * Create an instance of the EnergyPlus task
     * @param env Data packet including template and weather files
     * @param job_id Externally defined job_ID string of this task
     * @param prevkey Search string list
     * @param prevval Alt value list
     */
    public EPlusTask(EPlusWorkEnv env, String job_id, ArrayList<String> prevkey, ArrayList<String> prevval) {
        TaskID = job_id;
        WorkEnv = env;
        //WorkingDir = WorkEnv.ParentDir + TaskID + "/";
        //ParameterMap = params;
        parseTagsAndVals(prevkey, prevval);
    }

    protected final void parseTagsAndVals (ArrayList<String> prevkey, ArrayList<String> prevval) {
        // Parse search strings for hybrid parameters "@@a@@|@@b@@"
        SearchStringList = new ArrayList<> ();
        AltValueList = new ArrayList<> ();
        Scripts = new ArrayList<> ();
        // Initialize Jython script engine
        ScriptEngine engine = JEPlusProject.getScript_Engine();
        
        // size of keys and vals must be the same
        for (int i=0; i<prevkey.size(); i++) {
            String [] sstrs = prevkey.get(i).split("\\s*\\|\\s*");
            SearchStringList.addAll(Arrays.asList(sstrs));
            String [] vstrs = prevval.get(i).split("\\s*\\|\\s*");
            for (int j=0; j<Math.min(vstrs.length, sstrs.length); j++) {
                // for each val string, perform calculations if it starts with "?="
                String formula = vstrs[j];
                if (formula.startsWith("?=")) {
                    formula = formula.substring(2);
                    // Create a new parser
//                    Parser parser = new Parser();
                    // Map search tags to variables and then assign values to variables
                    HashMap<String, String> map = new HashMap<> ();
                    for (int k=0; k<SearchStringList.size()-sstrs.length; k++) {
                        String var = "p" + k;
                        map.put(SearchStringList.get(k), var);
                        String statement = var + " = " + AltValueList.get(k);
                        try {
                            engine.eval(statement);
                        }catch (ScriptException spe) {
                            logger.error("Error evaluating expression " + statement + ".");
                        }
                    }
                    for (String tag: map.keySet()) {
                        formula = formula.replaceAll(tag, map.get(tag));
                    }
                    try {
                        vstrs[j] = engine.eval(formula).toString();
                    }catch (ScriptException spe) {
                        logger.error("Error evaluating expression " + formula + ".");
                    }
                // If starts with "call(", create a script record and add "script" to vstrs
                } else if (formula.startsWith("call(")) {
                    formula = formula.substring(5, formula.length() - 1);
                    Scripts.add(PythonFunc.fromString(formula));
                    vstrs[j] = "script";
                }
                // Add the result string to the list
                AltValueList.add(vstrs[j]);
            }
            // If val strings set is shorter than search strings set, repeating the last val string
            for (int j=vstrs.length; j<sstrs.length; j++) {
                AltValueList.add(vstrs[vstrs.length - 1]);
            }
        }
    }
    
    
    @Override
    public String getJobID() {
        return TaskID;
    }

    public EPlusWorkEnv getWorkEnv() {
        return this.WorkEnv;
    }

    @Override
    public void setJobID(String id) {
        TaskID = id;
    }

    public ArrayList<String> getAltValueList() {
        return AltValueList;
    }

    public ArrayList<String> getAttachedResults() {
        return AttachedResults;
    }

    public ArrayList<String> getSearchStringList() {
        return SearchStringList;
    }

    /**
     * Working Directory of this job is created on the fly, to save memory
     * @return A directory name comprises of the parent dir and the job id
     */
    public String getWorkingDir() {
        return WorkEnv.ParentDir + TaskID + "/";
    }

    /**
     * Extract results from the meter file in the output of E+. Records are
     * searched using the given id string. Results are stored in a HashMap.
     * @param MeterID
     * @return Results found in E+ output file
     */
    public HashMap<String, double[][]> getResults(String[] MeterID) {
        HashMap<String, double[][]> map = new HashMap<>();
        for (String MeterID1 : MeterID) {
            map.put(MeterID1, EPlusWinTools.getMeter(getWorkingDir(), MeterID1));
        }
        return map;
    }

    /**
     * Read result file and put the contents in a string
     * @return Results from output file
     */
    public String getResults() {
        if (Executed && WorkEnv.UseReadVars) {
            StringBuilder buf = new StringBuilder();
            File csv = new File(this.getWorkingDir() + EPlusConfig.getEPDefOutCSV());
            if (csv.exists()) {
                try (BufferedReader fr = new BufferedReader(new FileReader(csv))) {
                    String line = fr.readLine();
                    while (line != null) {
                        buf.append(line).append("\n");
                        line = fr.readLine();
                    }
                } catch (Exception ex) {
                    logger.error("", ex);
                }
                return buf.toString();
            }
        }
        return null;
    }

    /**
     * Read result info file ("eplusout.end") and put the contents in a string
     * @return Result information from "eplusout.end"
     */
    public String getResultInfo() {
        return getResultInfo(null);
    }

    /**
     * Read result info file ("eplusout.end") and put the contents in a string
     * @param parentdir The parent directory where all results are located; use default dir if 'null' is provided
     * @return Result information from "eplusout.end"
     */
    public String getResultInfo(String parentdir) {
        return getResultInfo(parentdir, this, false);
    }

     /**
     * Read result info file ("eplusout.end") and put the contents in a string
     * @param parentdir The parent directory where all results are located; use default dir if 'null' is provided
     * @param job The EPlus job whose id is used to locate the report file
     * @param removefile Remove the file after reads
     * @return Result information from "eplusout.end"
     */
    public static String getResultInfo(String parentdir, EPlusTask job, boolean removefile) {
        String fulldir = (parentdir == null)? job.getWorkingDir() : parentdir + job.getJobID() + "/";
        File end = new File(fulldir + EPlusConfig.getEPDefOutEND());
        StringBuilder buf = new StringBuilder();
        if (end.exists()) {
            try (BufferedReader fr = new BufferedReader(new FileReader(end))) {
            String line = fr.readLine();
            while (line != null && line.trim().length() > 0) {
                buf.append(line).append("\n");
                line = fr.readLine();
            }
            }catch (Exception ex) {
                logger.error("", ex);
            }
            if (removefile) end.delete();
            return buf.toString();
        }
        return "! " + end.getPath() + " is not available";
    }


   /**
     * TODO: the purpose of this method is to be cleared
     * @param result
     */
    public void attachResult(String result) {
        AttachedResults.add(result);
    }

    /**
     * Attach collected results to this task object
     * @param results The collected results in a string vector
     */
    public void attachResults(List<String> results) {
        AttachedResults.clear();
        AttachedResults.addAll(results);
        AttachedResults.trimToSize();
    }

    /**
     * Has the task been executed or not
     * @return true if executed
     */
    public boolean isExecuted() {
        return Executed;
    }

    /**
     * Retrieve flag showing whether simulation has been completed successfully or not
     * @return ResultAvailable flag
     */
    public boolean isResultAvailable() {
        return ResultAvailable;
    }

    public void setExecuted(boolean Executed) {
        this.Executed = Executed;
    }

    public void setResultAvailable(boolean ResultAvailable) {
        this.ResultAvailable = ResultAvailable;
    }

    /**
     * Preprocess the input file (.imf/.idf), including calling EP-Macro
     * @param config
     * @return Operation successful or not
     */
    public boolean preprocessInputFile (JEPlusConfig config) {
        boolean ok = true;
        String[] SearchStrings = SearchStringList.toArray(new String[0]);
        String[] Newvals = AltValueList.toArray(new String[0]);
        if (WorkEnv.isIMF()) {
            // If the input template file is for EP-Macro (.imf)
            // Check if #fileprefix is available
            String fprefixstr = IDFmodel.getIncludeFilePrefix(WorkEnv.IDFDir + WorkEnv.IDFTemplate);
            if (fprefixstr != null) {
                File fprefix = new File(fprefixstr);
                if (! fprefix.isAbsolute()) {
                    fprefix = new File (WorkEnv.IDFDir.concat(fprefixstr));
                }
                if (! (fprefix.isDirectory() && fprefix.exists())) {
                    System.err.println("IMF processing error: ##fileprefix (" + fprefix.getAbsolutePath() + ") is not a folder or does not exist. Current folder is assumed.");
                    fprefix = new File (WorkEnv.IDFDir);
                }
                try {
                    fprefixstr = fprefix.getCanonicalPath();
                } catch (IOException ex) {
                    logger.error("", ex);
                    ok = false;
                }
            }
            // Update the imf template
            ok = ok && EPlusWinTools.updateIMFFile(WorkEnv.IDFDir + WorkEnv.IDFTemplate, 
                    fprefixstr, SearchStrings, Newvals, getWorkingDir());
            if (ok) {
                // If imf template was successfully updated ("in.imf" is created), run EP-Macro
                EPlusWinTools.runEPMacro(config, getWorkingDir());
                // Test EP-Macro was successful or not by checking the presence of "out.idf"
                ok = EPlusWinTools.isFileAvailable("out.idf", getWorkingDir());
                // If ok, update the out.idf with the search strings and values again.
                ok = (ok && EPlusWinTools.updateIDFFile(getWorkingDir() + "out.idf", SearchStrings, Newvals,
                        getWorkingDir()));
            }
        }else {
            // Otherwise it is for EPlus (.idf)
            ok = EPlusWinTools.updateIDFFile(WorkEnv.IDFDir + WorkEnv.IDFTemplate, SearchStrings, Newvals,
                    getWorkingDir());
        }
        if (ok) {
            // Collect E+ include files (e.g. in Schedule:File objects)
            ArrayList<String> incfiles = IDFmodel.getScheduleFiles(getWorkingDir() + EPlusConfig.getEPDefIDF());
            if (! incfiles.isEmpty()) {
                try (PrintWriter outs = (config.getScreenFile() == null) ? null : new PrintWriter (new FileWriter (getWorkingDir() + config.getScreenFile(), true));) {
                    if (outs != null) {
                        outs.println("# Copying dependant files - " + (new SimpleDateFormat()).format(new Date()));
                        outs.flush();
                    }
                    // Copy them to working dir
                    for (String incfile : incfiles) {
                        File ori = new File(RelativeDirUtil.checkAbsolutePath(incfile, WorkEnv.IDFDir));
                        File dest = new File (getWorkingDir() + ori.getName());
                        try {
                            // Log to console.log
                            if (outs != null) {
                                outs.println(incfile);
                                outs.flush();
                            }
                            FileUtils.copyFile(ori, dest);
                        }catch (IOException ex) {
                            logger.error("", ex);
                            // Log to console.log
                            if (outs != null) {
                                outs.println(ex.getMessage());
                                outs.flush();
                            }
                            ok = false;
                            break;
                        }
                    }
                }catch (Exception ex) {
                    logger.error("", ex);
                }
            }
            // Update idf
            if (ok && incfiles.size() > 0) {
                ok = IDFmodel.replaceScheduleFiles(getWorkingDir() + EPlusConfig.getEPDefIDF(), incfiles);
            }
        }
        return ok;
    }

    protected boolean runPythonScriptOnIDF () {
        boolean ok = true;
        // Get path to job folder
        String job_dir = getWorkingDir();
        // Run Python script
        JEPlusConfig config = JEPlusConfig.getDefaultInstance();
        // Create an inverted index
        HashMap<String, Integer> index = new HashMap<> ();
        for (int i=0; i<SearchStringList.size(); i++) {
            index.put (SearchStringList.get(i), i);
        }
        // Run scripts
        for (PythonFunc script : Scripts) {
            // Get args
            StringBuilder buf = new StringBuilder ();
            for (int i=0; i<script.getArgs().size(); i++) {
                if (i > 0) {
                    buf.append(",");
                }
                if (index.containsKey(script.getArgs().get(i))) {
                    buf.append(AltValueList.get(index.get(script.getArgs().get(i))));
                }else {
                    buf.append(script.getArgs().get(i));
                }
            }
            String args = buf.toString();
            // Call Python
            try (PrintStream outs = (config.getScreenFile() == null) ? System.err : new PrintStream (new FileOutputStream (job_dir + config.getScreenFile(), true));) {
                PythonTools.runPython(
                        config, 
                        RelativeDirUtil.checkAbsolutePath(script.getScriptFile(), WorkEnv.getProjectBaseDir()), 
                        script.getPyVersion(), 
                        WorkEnv.getProjectBaseDir(), 
                        job_dir, 
                        JEPlusConfig.getDefaultInstance().getEPlusBinDir(),
                        args,
                        outs);
            }catch (IOException ioe) {
                logger.error("Error writing to log steam.", ioe);
            }
        }
        return ok;
    }
    
    /**
     * Execute this task in local machine
     */
    @Override
    public void run() {
        Executed = true;
        // Prepare work directory
        boolean ok = EPlusWinTools.prepareWorkDir(JEPlusConfig.getDefaultInstance(), getWorkingDir());
        // Copy weather and rvi files
        ok = ok && EPlusWinTools.copyWorkFiles(getWorkingDir(), WorkEnv.WeatherDir + WorkEnv.WeatherFile, WorkEnv.isRVX() ? null : WorkEnv.RVIDir + WorkEnv.RVIFile);
        // Write IDF file
        ok = ok && this.preprocessInputFile(JEPlusConfig.getDefaultInstance());
        // Run Python script 
        ok = ok && this.runPythonScriptOnIDF ();
        // Ready to run EPlus
        if (ok) {
            int code = EPlusWinTools.runEPlus(JEPlusConfig.getDefaultInstance(), getWorkingDir(), false);
            ok = (code >= 0) && EPlusWinTools.isEsoAvailable(getWorkingDir());
        }
        // Remove temperory files/dir if required
        if (ok) {
            ok = ok && EPlusWinTools.cleanupWorkDir(getWorkingDir(), WorkEnv.KeepEPlusFiles, WorkEnv.KeepJEPlusFiles, WorkEnv.KeepJobDir, WorkEnv.SelectedFiles);
        }
        ResultAvailable = ok;
    }

    /**
     * Execute this task in specific dir rather than the one specify in the WorkEnv.
     * A new instance of WorkEnv is created and associated with this job.
     * The ParentDir in the new WorkEnv will then be updated with the new dir
     * @param parentdir New directory in which this task to be executed
     */
    public void run(String parentdir) {
        WorkEnv = new EPlusWorkEnv (WorkEnv);
        WorkEnv.ParentDir = parentdir;
        //this.WorkingDir = WorkEnv.ParentDir + TaskID + "/";
        this.run();
    }

    /**
     * 
     * @return 
     */
    @Override
    public String toString () {
        return this.TaskID;
    }
}
