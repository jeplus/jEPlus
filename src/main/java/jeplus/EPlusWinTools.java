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
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *:x
 *                                                                         *
 ***************************************************************************
 *                                                                         *
 * Change log:                                                             *
 *                                                                         *
 *  - Created
 *  - 2010-06-09 Addressed an issue that updateIDFFile() and updateIMFFile()
 *               would not create the new file if the directory does not
 *               exist
 *                                                                         *
 ***************************************************************************/
package jeplus;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: jEPlus - EnergyPlus execution utilities </p>
 * <p>Description: Utilities for updating E+ dirs/files and triggering E+ simulation </p>
 * <p>Copyright: Copyright (c) 2005-2010</p>
 * <p>Company: IESD, De Montfort University</p>
 * @author Yi Zhang
 * @version 0.5c
 * @since 0.1
 */
public class EPlusWinTools {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusWinTools.class);
    
    /**
     * Read IDD_Version from the IDD file specified in EPlusConfig
     * @param config EPlus Configuration file specifies where to find the binaries
     * @return Version number is a string
     */
    public String getEPlusIDDVersion (EPlusConfig config) {
        String idd = config.getResolvedEPlusBinDir() + EPlusConfig.getEPDefIDD();
        try (BufferedReader fr = new BufferedReader (new FileReader (idd))) {
            String line = fr.readLine();
            while (line != null) {
                if (line.startsWith("!IDD_Version ")) {
                    return line.substring(13).trim();
                }
                line = fr.readLine();
            }
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return "unknown";
    }

    /**
     * Update the problem setup by replacing the searchstrings in the IDF template
     * file with the newvals strings, and save the new file in the working directory
     * with the given name + ".idf".
     * @param fn The target IDF file without the extension
     * @param IDFTemplate The template IDF file that contains the search strings
     * @param searchstrings The list of search strings to be looked up
     * @param newvals The list of new values to be used to replace the search strings
     * @param TargetDir The working directory in which the new IDF file will be saved.
     * @return state of execution
     * @deprecated Use the one with the same name
     */
    public static boolean updateIDFFile(String fn, String IDFTemplate, String[] searchstrings, String[] newvals, String TargetDir) {

        boolean success = true;

        // Load and edit the template file
        try (
            BufferedReader ins = new BufferedReader(new FileReader(IDFTemplate));
            PrintWriter outs = new PrintWriter(new FileWriter(TargetDir + fn)) ) {
            
            String line = ins.readLine();
            int n = searchstrings.length;
            while (line != null) {
                for (int i = 0; i < n; i++) {
                    line = line.replaceFirst(searchstrings[i], newvals[i]);
                }
                outs.println(line);
                line = ins.readLine();
            }
            outs.flush();
        } catch (Exception ex) {
            logger.error("", ex);
            success = false;
        }

        return success;
    }

    /**
     * Update the problem setup by replacing the searchstrings in the IDF template
     * file with the newvals strings, and save the new file in the working directory
     * with the default Eplus input file name ("in.idf" at present).
     * @param IDFTemplate The template IDF file that contains the search strings
     * @param searchstrings The list of search strings to be looked up
     * @param newvals The list of new values to be used to replace the search strings
     * @param WorkDir The working directory in which the new IDF file will be saved.
     * @return state of execution
     */ 
    public static boolean updateIDFFile(String IDFTemplate, String[] searchstrings, String[] newvals, String WorkDir) {

        boolean success = true;

        // Load and edit the template file
        try (BufferedReader ins = new BufferedReader(new FileReader(IDFTemplate))) {
            File workdir = new File (WorkDir);
            if (! workdir.exists()) success = workdir.mkdirs();
            if (success && workdir.isDirectory()) {
                try (PrintWriter outs = new PrintWriter(new FileWriter(WorkDir + EPlusConfig.getEPDefIDF()))) {
                    String line = ins.readLine();
                    int n = searchstrings.length;
                    // Compile pattern and matchers
                    Pattern [] patterns = new Pattern [n];
                    Matcher [] matchers = new Matcher [n];
                    for (int i = 0; i < n; i++) {
                        patterns[i] = Pattern.compile(searchstrings[i]);
                        matchers[i] = patterns[i].matcher(line);
                    }
                    // Start scanning
                    while (line != null) {
                        for (int i = 0; i < n; i++) {
                            line = matchers[i].reset(line).replaceAll(newvals[i]);
                        }
                        outs.println(line);
                        line = ins.readLine();
                    }
                    outs.flush();
                }
            } else {
                success = false;
            }
        } catch (Exception ex) {
            logger.error("", ex);
            success = false;
        }

        return success;
    }

    /**
     * Update the problem setup by replacing the searchstrings in the IDF template
     * file with the newvals strings, and save the new file in the working directory
     * with the default EP-Macro input file name ("in.imf" at present).
     * @param IDFTemplate The template IDF file that contains the search strings
     * @param fileprefix FilePrefix string used in "##fileprefix" of IMF
     * @param searchstrings The list of search strings to be looked up
     * @param newvals The list of new values to be used to replace the search strings
     * @param WorkDir The working directory in which the new IDF file will be saved.
     * @return state of execution
     */
    public static boolean updateIMFFile(String IDFTemplate, String fileprefix, String[] searchstrings, String[] newvals, String WorkDir) {

        boolean success = true;

        // Load and edit the template file
        try (BufferedReader ins = new BufferedReader(new FileReader(IDFTemplate))) {
            File workdir = new File (WorkDir);
            if (! workdir.exists()) success = workdir.mkdirs();
            if (success && workdir.isDirectory()) {
                try (PrintWriter outs = new PrintWriter(new FileWriter(WorkDir + EPlusConfig.getEPDefIMF()))) {
                    if (fileprefix != null)
                        outs.println("##fileprefix " + fileprefix + (fileprefix.endsWith("/") ? "" : "/"));
                    // Filter and copy lines
                    String line = ins.readLine();
                    int n = searchstrings.length;
                    // Compile pattern and matchers
                    Pattern [] patterns = new Pattern [n];
                    Matcher [] matchers = new Matcher [n];
                    for (int i = 0; i < n; i++) {
                        patterns[i] = Pattern.compile(searchstrings[i]);
                        matchers[i] = patterns[i].matcher(line);
                    }
                    // Start scanning
                    while (line != null) {
                        line = line.trim();
                        if (! line.startsWith("##fileprefix ")) {
                            for (int i = 0; i < n; i++) {
                                line = matchers[i].reset(line).replaceAll(newvals[i]);
                            }
                            outs.println(line);
                        }
                        line = ins.readLine();
                    }
                    outs.flush();
                }
            } else {
                success = false;
            }
        } catch (Exception ex) {
            logger.error("", ex);
            success = false;
        }

        return success;
    }

    /**
     * Write the EnergyPlus .INI file in the working directory
     * @param EPlusDir EnergyPlus directory
     * @return -1 if failed
     */
    public static int writeEPlusINI(String EPlusDir) {
        try (PrintWriter fo = new PrintWriter(new FileWriter("Energy+.ini"))) {
            fo.println("[program]");
            fo.println(EPlusDir);
            fo.println("[spark]");
            fo.println("dir=" + EPlusDir + "sparklink/packages");
            fo.println("[BasementGHT]");
            fo.println("dir=PreProcess/GrndTempCalc");
            fo.println("[SlabGHT]");
            fo.println("dir=PreProcess/GrndTempCalc");
        } catch (IOException ioe) {
            logger.error("", ioe);
            return -1;
        }
        return 0;
    }

    /**
     * Write the EnergyPlus .INI file in the working directory
     * @param workdir The location where Energy+.ini to be written
     * @param EPlusDir EnergyPlus directory
     * @return -1 if failed
     */
    public static int writeMinimumEPlusINI(String workdir, String EPlusDir) {
        try (PrintWriter fo = new PrintWriter(new FileWriter(workdir + "Energy+.ini"))) {
            fo.println("[program]");
            fo.println("dir=" + EPlusDir);
        } catch (IOException ioe) {
            logger.error("", ioe);
            return -1;
        }
        return 0;
    }

    /**
     * Clean up working directory after simulation, based on the options to keep
     * working files and the list of files to delete.
     * @param workdir The working directory to be cleared
     * @param keepeplus Flag for keeping all EnergyPlus output files
     * @param keepjeplus Flag for keeping all intermediate jEPlus files
     * @param keepdir Flag for keeping the working directory
     * @param filesToDelete A [,;: ] separated list of file names to be deleted from the directory
     * @return Clean up successful or not. False is return if error occurs when deleting any file 
     */
    public static boolean cleanupWorkDir(String workdir, boolean keepeplus, boolean keepjeplus, boolean keepdir, String filesToDelete) {
        boolean success = true;

        // Create the directory
        File dir = new File(workdir);
        if (dir.exists()) {
            if (!keepdir) {
                File [] files = dir.listFiles();
                for (File file : files) {
                    file.delete();
                }
                success = dir.delete();
            } else {
                if (! keepjeplus) {
                    File [] files = dir.listFiles(EPlusConfig.getIOFileFilter(EPlusConfig.JEPLUS_INTERM));
                    for (File file : files) {
                        success &= file.delete();
                    }
                }
                if (! keepeplus) {
                    File [] files = dir.listFiles(EPlusConfig.getIOFileFilter(EPlusConfig.EPOUTPUT));
                    for (File file : files) {
                        success &= file.delete();
                    }
                }
            }
            if (filesToDelete != null) {
                String [] patterns = filesToDelete.split("\\s*[,;: ]\\s*");
                OrFileFilter filter = new OrFileFilter ();
                for (String pattern : patterns) {
                    filter.addFileFilter(new WildcardFileFilter(pattern));
                }
                File [] files = dir.listFiles((FileFilter)filter);
                for (File file : files) {
                    success &= file.delete();
                }
            }
        }
        return success;
    }

    /**
     * Create working directory and prepare input files for simulation
     * @param config Not used in this function
     * @param workdir The directory to be created
     * @return Preparation successful or not
     */
    public static boolean prepareWorkDir(EPlusConfig config, String workdir) {
        boolean success = true;
        // Create the directory
        File dir = new File(workdir);
        if (!dir.exists()) {
            success = dir.mkdirs();
        } else if (!dir.isDirectory()) {
            logger.error(dir.toString() + " is present but not a directory.");
            return false;
        }
        return success;
    }

    /**
     * Create working directory and prepare input files for simulation
     * @param workdir The directory to be created
     * @param weatherfile
     * @param rvifile
     * @return Preparation successful or not
     */
    public static boolean copyWorkFiles(String workdir, String weatherfile, String rvifile) {
        boolean success = true;
        // Copying IDD files from EnergyPlus bin directory - 30-9-2012: copying IDD represent an overhead. This is done at
        // the simulation stage (runEPlus()) instead of preparation stage.
        // success = fileCopy(config.getResolvedEPlusBinDir() + EPlusConfig.getEPDefIDD(), workdir + EPlusConfig.getEPDefIDD());
        // Copying INI files from EnergyPlus bin directory - this is not needed when IDD file is copied
        // success = fileCopy(EPlusConfig.getEPlusBinDir() + EPlusConfig.getEPDefINI(), workdir + EPlusConfig.getEPDefINI());
        // Copying weather files to the work directory
        //success = fileCopy(weatherfile + EPlusConfig.getEPlusWeatherExt(), workdir + EPlusConfig.getEPDefEPW());
        if (weatherfile != null) {
            success &= fileCopy(weatherfile, workdir + EPlusConfig.getEPDefEPW());
        }
        // success = fileCopy(weatherfile + EPlusConfig.getEPlusWeatherStatExt(), workdir + EPlusConfig.getEPDefSTAT());
        if (rvifile != null) {
            success &= fileCopy(rvifile, workdir + EPlusConfig.getEPDefRvi());
        }
        if (! success)
            logger.error("EPlusWinTools.prepareWorkDir(): cannot copy all neccessray files to the working directory.");
        return success;
    }

    /**
     * Copy a file from one location to another
     * @param from The source file to be copied
     * @param to The target file to write
     * @return Successful or not
     */
    public static boolean fileCopy(String from, String to) {
        boolean success = true;
        try (
            // Getting file channels
            FileChannel in = new FileInputStream(from).getChannel();
            FileChannel out = new FileOutputStream(to).getChannel() ) {

            // JavaVM does its best to do this as native I/O operations.
            in.transferTo (0, in.size(), out);

            // Closing file channels will close corresponding stream objects as well.
        } catch (Exception ex) {
            logger.error("", ex);
            success = false;
        }
        return success;
    }
    
    /**
     * Write a Job Done marker, typically a file named 'job.done!' in the given folder
     * @param dir The folder in which the marker is written
     * @param marker The marker file name. The file contains a string "Job done!"
     * @return Marker is written successfully or not
     */
    public static boolean writeJobDoneMarker (String dir, String marker) {
        try (FileWriter fw = new FileWriter (dir + (dir.endsWith(File.separator)?"":File.separator) + marker)) {
            fw.write("Job done!");
            return true;
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return false;
    }

    /**
     * Remote the Job Done marker if exists. The marker file is typically named 'job.done!' in the given folder
     * @param dir The folder in which the marker is written
     * @param marker The marker file name. The file contains a string "Job done!"
     * @return Marker is removed successfully or not
     */
    public static boolean removeJobDoneMarker (String dir, String marker) {
        try {
            File markerfile = new File(dir + (dir.endsWith(File.separator)?"":File.separator) + marker);
            if (markerfile.exists()) markerfile.delete();
            return true;
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return false;
    }

    /**
     * Call EPlus executable file to run the simulation
     * @param config EPlus Configuration containing info of the binaries
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @return the exit code
     */
    public static int runEPMacro(EPlusConfig config, String WorkDir) {

        int ExitValue = -99;

        try {
            Process EPProc;
            // Run EP-Macro executable
            String CmdLine = config.getResolvedEPMacro();
            EPProc = Runtime.getRuntime().exec(CmdLine, null, new File(WorkDir));
            // Console logger
            try (PrintWriter outs = (config.getScreenFile() == null) ? null : new PrintWriter (new FileWriter (WorkDir + "/" + config.getScreenFile(), true));) {
                if (outs != null) {
                    outs.println("# Calling EP-Macro - " + (new SimpleDateFormat()).format(new Date()));
                    outs.println("# Command line: " + WorkDir + ">" + CmdLine);
                    outs.flush();
                }
                StreamPrinter p_out = new StreamPrinter (EPProc.getInputStream(), "OUTPUT", outs);
                StreamPrinter p_err = new StreamPrinter (EPProc.getErrorStream(), "ERROR");
                p_out.start();
                p_err.start();
                ExitValue = EPProc.waitFor();
                p_out.join();
                p_err.join();
                if (outs != null) {
                    outs.println("# EP-Macro returns: " + ExitValue);
                    outs.flush();
                }
            }
        } catch (Exception ex) {
            logger.error("Error running EP-Macro.", ex);
        }

        // Return Radiance exit value
        return ExitValue;
    }

    /**
     * 
     * @param xesoview
     * @param esofile
     * @return the exit code
     */
    public static int runXEsoView(String xesoview, String esofile) {

        try {
            // Run EP-Macro executable
            String CmdLine = xesoview + " " + esofile;
            Process EPProc = Runtime.getRuntime().exec(CmdLine);

            BufferedReader ins = new BufferedReader(new InputStreamReader(EPProc.getInputStream()));

            int res = ins.read();
            if (res != -1) {
                do {
                    res = ins.read();
                }while (res != -1);
                ins.close();
            }

            EPProc.waitFor();
            
        } catch (IOException | InterruptedException ex) {
            logger.error("", ex);
        }

        // Return Radiance exit value
        return 0;
    }

    /**
     * Call EPlus executable file to run the simulation
     * @param config
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param useReadVars Whether or not to use readvars after simulation
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runEPlus(EPlusConfig config, String WorkDir, boolean useReadVars) {
        
        // Copy IDD, or better, create an INI pointing to the correct IDD. INI will work only on Windows systems
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            EPlusWinTools.writeMinimumEPlusINI(WorkDir, config.getResolvedEPlusBinDir());
        }else {
            fileCopy(config.getResolvedEPlusBinDir() + EPlusConfig.getEPDefIDD(), WorkDir + EPlusConfig.getEPDefIDD());
        }

        int ExitValue = -99;

        try {
            Process EPProc;

            // Run EnergyPlus ExpandObjects
            String CmdLine = config.getResolvedExpandObjects();
            EPProc = Runtime.getRuntime().exec(CmdLine, null, new File(WorkDir));
            // Console logger
            try (PrintWriter outs = (config.getScreenFile() == null) ? null : new PrintWriter (new FileWriter (WorkDir + "/" + config.getScreenFile(), true));) {
                if (outs != null) {
                    outs.println("# Calling ExpandObjects - " + (new SimpleDateFormat()).format(new Date()));
                    outs.println("# Command line: " + WorkDir + ">" + CmdLine);
                    outs.flush();
                }
                StreamPrinter p_out = new StreamPrinter (EPProc.getInputStream(), "OUTPUT", outs);
                StreamPrinter p_err = new StreamPrinter (EPProc.getErrorStream(), "ERROR", outs);
                p_out.start();
                p_err.start();
                ExitValue = EPProc.waitFor();
                p_out.join();
                p_err.join();
                if (outs != null) {
                    outs.println("# ExpandObjects returns: " + ExitValue);
                    outs.flush();
                }
            }

            // Copy expanded.idf to in.idf
            if (new File (WorkDir + EPlusConfig.getEPDefExpandedIDF()).exists()) {
                if (! fileCopy (WorkDir + EPlusConfig.getEPDefExpandedIDF(), WorkDir + EPlusConfig.getEPDefIDF())) {
                    // File copy failed
                    logger.warn("Failed to copy " + WorkDir + EPlusConfig.getEPDefExpandedIDF() +
                            " to " + WorkDir + EPlusConfig.getEPDefIDF() + ". Simulation is aborted.");
                    return(ExitValue);
                }
            }

            // Run EnergyPlus executable
            CmdLine = config.getResolvedEPlusEXEC();
            // EP_OMP_NUM_THREADS forced to 1. This may be overridden by the following block if present in the model
            // ProgramControl,
            //    1 ; !- Number of Threads Allowed
            EPProc = Runtime.getRuntime().exec(CmdLine, new String [] {"EP_OMP_NUM_THREADS=1"}, new File(WorkDir));
            // Console logger
            try (PrintWriter outs = (config.getScreenFile() == null) ? null : new PrintWriter (new FileWriter (WorkDir + "/" + config.getScreenFile(), true));) {
                if (outs != null) {
                    outs.println("# Calling EnergyPlus - " + (new SimpleDateFormat()).format(new Date()));
                    outs.println("# Command line: " + WorkDir + ">" + CmdLine);
                    outs.flush();
                }
                StreamPrinter p_out = new StreamPrinter (EPProc.getInputStream(), "OUTPUT", outs);
                StreamPrinter p_err = new StreamPrinter (EPProc.getErrorStream(), "ERROR", outs);
                p_out.start();
                p_err.start();
                ExitValue = EPProc.waitFor();
                p_out.join();
                p_err.join();
                if (outs != null) {
                    outs.println("# EnergyPlus returns: " + ExitValue);
                    outs.flush();
                }
            }

            // Run EnergyPlus ReadVarsESO
            if (useReadVars) {
                CmdLine = config.getResolvedReadVars() + " " + EPlusConfig.getEPDefRvi();
                EPProc = Runtime.getRuntime().exec(CmdLine, null, new File(WorkDir));
                // Console logger
                try (PrintWriter outs = (config.getScreenFile() == null) ? null : new PrintWriter (new FileWriter (WorkDir + "/" + config.getScreenFile(), true));) {
                    if (outs != null) {
                        outs.println("# Calling ReadVarsESO - " + (new SimpleDateFormat()).format(new Date()));
                        outs.println("# Command line: " + WorkDir + ">" + CmdLine);
                        outs.flush();
                    }
                    StreamPrinter p_out = new StreamPrinter (EPProc.getInputStream(), "OUTPUT", outs);
                    StreamPrinter p_err = new StreamPrinter (EPProc.getErrorStream(), "ERROR", outs);
                    p_out.start();
                    p_err.start();
                    ExitValue = EPProc.waitFor();
                    p_out.join();
                    p_err.join();
                    if (outs != null) {
                        outs.println("# ReadVarsESO returns: " + ExitValue);
                        outs.flush();
                    }
                }
            }
            // set it to successful
            ExitValue = 0;
        } catch (Exception ex) {
            logger.error("Error executing E+ binaries.", ex);
        }

        // Return Radiance exit value
        return ExitValue;
    }
    
    public static int runReadVars (EPlusConfig config, String WorkDir, String rvifile, String freq, String csvfile) {
        int ExitValue = -99;
        try {
            Process EPProc;

            // Run EnergyPlus ReadVarsESO
            String CmdLine = config.getResolvedReadVars() + " \"" + rvifile + "\" " + freq + " unlimited";
            EPProc = Runtime.getRuntime().exec(
                    new String [] {config.getResolvedReadVars(), rvifile, freq, "unlimited"}, 
                    null, 
                    new File(WorkDir));
            // Console logger
            try (PrintWriter outs = (config.getScreenFile() == null) ? null : new PrintWriter (new FileWriter (WorkDir + "/" + config.getScreenFile(), true));) {
                if (outs != null) {
                    outs.println("# Calling ReadVarsESO - " + (new SimpleDateFormat()).format(new Date()));
                    outs.println("# Command line: " + WorkDir + ">" + CmdLine);
                    outs.flush();
                }
                StreamPrinter p_out = new StreamPrinter (EPProc.getInputStream(), "OUTPUT", outs);
                StreamPrinter p_err = new StreamPrinter (EPProc.getErrorStream(), "ERROR");
                p_out.start();
                p_err.start();
                ExitValue = EPProc.waitFor();
                p_out.join();
                p_err.join();
                // Copy the result file (eplusout.csv) to the target csv file name
                File csv = new File (WorkDir + EPlusConfig.getEPDefOutCSV());
                boolean ok = false;
                // Clear the existing output csv file
                File csvout = new File (WorkDir + csvfile);
                if (csvout.exists()) csvout.delete();
                // Rename the new csv to the output csv
                if (csv.exists() && csv.renameTo(csvout)) {
                    ok = true;
                }
                if (outs != null) {
                    outs.println("# ReadVarsESO returns: " + ExitValue);
                    outs.println(ok ? csv.getPath() + " renamed to " + csvfile : "Processing " + rvifile + " has failed.");
                    outs.flush();
                }
            }
            // set it to successful
            ExitValue = 0;
        } catch (IOException | InterruptedException ex) {
            logger.error("Error executing ReadVars.", ex);
        }
        return ExitValue;
        
    }
    
    public static boolean updateVersion (String startversion, String targetversion, String binfolder, String listfile, boolean backup, boolean keepversions, PrintStream logstream) {
        
        boolean ok = true;
        // Get files to be converted
        List<String> files = getFilesToBeConverted (listfile);
        // Get installed versions
        List<String> installed = getInstalledTransitionVersions (binfolder);
        
        // Find start and end points
        int start = installed.indexOf(startversion);
        int end = installed.indexOf(targetversion);
        
        if (start >=0 && end >=0 && end > start) {
            // Create backup if required. The backup files will be put in a bak/ folder next to the original files
            if (backup) {
                for (String file : files) {
                    File thisfile = new File (file);
                    if (thisfile.exists()) {
                        fileCopy (thisfile.getAbsolutePath(), thisfile.getAbsolutePath() + ".ori");
                    }
                }
            }
            // Start conversion
            for (int i=start; i<end; i++) {
                // Store interim version if required
                if (keepversions) {
                    for (String file : files) {
                        File thisfile = new File (file);
                        if (thisfile.exists()) {
                            fileCopy (thisfile.getAbsolutePath(), thisfile.getAbsolutePath() + "." + installed.get(i));
                        }
                    }
                }
                // Call converter
                ok &= runVersionTransition (installed.get(i), installed.get(i + 1), binfolder, listfile, logstream);
            }
            // remove temporary files
            for (String file : files) {
                File thisfile = new File (file + "_transition.audit");
                if (thisfile.exists()) {
                    thisfile.delete();
                }
                thisfile = new File (file + "old");
                if (thisfile.exists()) {
                    thisfile.delete();
                }
                thisfile = new File (file + "new");
                if (thisfile.exists()) {
                    thisfile.delete();
                }
            }
        }else {
            // Some error message
            logger.error("Either the start or the target version is wrong. Please make sure the relevant transition files are available, and the target version is higher than the start version");
            ok = false;
        }
        
        return ok;
    }
    
    public static boolean runVersionTransition (String fromVer, String toVer, String binfolder, String listfile, PrintStream logstream) {
        boolean ok = true;
        try {
            Process EPProc;
            // Run EnergyPlus ExpandObjects
            String CmdLine = binfolder + File.separator + "Transition-" + fromVer + "-to-" + toVer + " \"" + listfile + "\"";
            EPProc = Runtime.getRuntime().exec(CmdLine, null, new File(binfolder));

            try (BufferedReader ins = new BufferedReader(new InputStreamReader(EPProc.getInputStream()))) {
                if (logstream != null) {
                    logstream.println("Calling VersionTransition with Command line: " + binfolder + ">" + CmdLine);
                    
                    String res = ins.readLine();
                    while (res != null) {
                        logstream.println(res);
                        res = ins.readLine();
                    }
                }else {
                    int res = ins.read();
                    while (res != -1) {
                        res = ins.read();
                    }
                }
            }
            EPProc.waitFor();
            int ExitValue = EPProc.exitValue(); 
            if (logstream != null) {
                logstream.println("Exit value = " + ExitValue);
                logstream.println();
            }
        } catch (IOException | InterruptedException ex) {
            logger.error("", ex);
            ok = false;
        }
        return ok;
    }
    
    protected static List<String> getFilesToBeConverted (String listfile) {
        ArrayList <String> list = new ArrayList<> ();
        try (BufferedReader fr = new BufferedReader (new FileReader (listfile))) {
            String line = fr.readLine();
            while (line != null) {
                list.add(line.trim());
                line = fr.readLine();
            }
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return list;
    }
    
    public static List<String> getInstalledTransitionVersions (String binfolder) {
        ArrayList <String> list = new ArrayList<> ();
        File dir = new File (binfolder);
        if (dir.exists() && dir.isDirectory()) {
            String pattern = "V?-?-?-Energy+.idd";
            OrFileFilter filter = new OrFileFilter ();
            filter.addFileFilter(new WildcardFileFilter (pattern));
            File [] files = dir.listFiles((FileFilter)filter);
            for (File file : files) {
                list.add(file.getName().substring(0, 6));
            }
        }
        return list;
    }
    
    public static void scanFolderForFiles (File folder, String filters, List<String> list) {
        if (list == null) {
            list = new ArrayList<> ();
        }
        if (folder.exists() && folder.isDirectory()) {
            // Scan files that satisfy the filters
            if (filters != null) {
                String [] patterns = filters.split("\\s*[,;: ]\\s*");
                OrFileFilter filter = new OrFileFilter ();
                for (String pattern : patterns) {
                    filter.addFileFilter(new WildcardFileFilter(pattern));
                }
                File [] files = folder.listFiles((FileFilter)filter);
                for (File file : files) {
                    list.add(file.getAbsolutePath());
                }
            }
            // Scan sub-folders
            File[] listOfAllFiles = folder.listFiles();
            for (File file : listOfAllFiles) {
                if (file.isDirectory()) {
                    scanFolderForFiles(file, filters, list);
                }
            }            
        }
    }

    /**
     * Test if a given file is available in the given directory as an indicator
     * of successful run.
     * @param filename File name to test
     * @param workdir Expected location of the file
     * @return boolean True if the file is present at the specified location
     */
    public static boolean isFileAvailable(String filename, String workdir) {
      return (new File (workdir + filename).exists());
    }

    /**
     * Test if eplusout.eso is available in the given directory as an indicator
     * of successful run.
     * @param workdir Expected location of the file
     * @return boolean True if eplusout.eso is present
     */
    public static boolean isEsoAvailable(String workdir) {
      File eso = new File (workdir + EPlusConfig.getEPDefOutESO());
      return eso.exists();
    }

    /**
     * Test if eplusout.csv is available in the given directory as an indicator
     * of successful run.
     * @param workdir Expected location of the file
     * @return boolean True if eplusout.csv is present
     */
    public static boolean isCsvAvailable(String workdir) {
      File csv = new File (workdir + EPlusConfig.getEPDefOutCSV());
      return csv.exists();
    }

    /**
     * Copy eplusout.csv from currentdir to destdir, and rename it to [newname].csv
     * @param newname
     * @param currentdir
     * @param destdir
     * @return
     */
    public static boolean copyCsv(String newname, String currentdir, String destdir) {
        boolean success = true;
        try(BufferedReader in = new BufferedReader(new FileReader(currentdir + EPlusConfig.getEPDefOutCSV()));
            PrintWriter out = new PrintWriter(new FileWriter(destdir + newname + EPlusConfig.getEPlusCsvExt()))) {
            String line = in.readLine();
            while (line != null) {
                // Do not insert job_id column when copying
                // out.println(newname + "," + line);
                out.println(line);
                line = in.readLine();
            }
        } catch (Exception ex) {
            logger.error("", ex);
            success = false;
        }
        return success;
    }

    /**
     * Get meter readings from the EPlus output
     * @param WorkDir The working dir where the output files are located
     * @param RecIndex The index the particular record to read
     * @return All data associated with the given index
     */
    public static double[][] getMeter(String WorkDir, String RecIndex) {

        int ErrorValue = -99;

        try {
            //TODO: not working, check
            BufferedReader ins = new BufferedReader(new FileReader(WorkDir + EPlusConfig.getEPDefOutESO() + EPlusConfig.getEPlusMtrExt()));

            String dataDict = null;
            boolean inDataDict = true;
            ArrayList<String> digest = new ArrayList<>();
            String line = ins.readLine();
            while (line != null) {
                if (line.trim().startsWith("End of Data Dictionary")) {
                    inDataDict = false;
                }
                if (line.trim().startsWith(RecIndex)) {
                    if (inDataDict) {
                        dataDict = line;
                    } else {
                        digest.add(line);
                    }
                }
                line = ins.readLine();
            }
            ins.close();

            // Parse numbers
            double[][] table = new double[digest.size()][];
            for (int j = 0; j < digest.size(); j++) {
                String row = (String) digest.get(j);
                String[] nums = row.split(",|:");
                double[] vals = new double[nums.length];
                for (int i = 0; i < nums.length; i++) {
                    try {
                        vals[i] = Double.parseDouble(nums[i]);
                    } catch (NumberFormatException nfe) {
                        logger.error("", nfe);
                        vals[i] = ErrorValue;
                    }
                }
                table[j] = vals;
            }
            return table;
        } catch (Exception ex) {
            logger.error("", ex);
            return null;
        }
    }

    /**
     * Get the list of output file generated by the last run
     * @return An array of files
     */
    public static File[] getOutputFiles(String outdir) {
        return new File(outdir).listFiles();
    }
}

class StreamPrinter extends Thread {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(StreamPrinter.class);
    
    InputStream is;
    String type;
    PrintWriter pw;

    StreamPrinter(InputStream is, String type) {
        this(is, type, null);
    }

    StreamPrinter(InputStream is, String type, PrintWriter redirect) {
        this.is = is;
        this.type = type;
        this.pw = redirect;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                    pw.flush();
                }
            }
        } catch (IOException ioe) {
            logger.error("Error redirecting stream.", ioe);
        }
    }
}
