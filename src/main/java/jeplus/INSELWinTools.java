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
import java.util.*;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: jEPlus - EnergyPlus execution utilities </p>
 * <p>Description: Utilities for updating INSEL dirs/files and triggering INSEL simulation </p>
 * <p>Copyright: Copyright (c) 2013, Yi Zhang</p>
 * <p>Company: IESD, De Montfort University</p>
 * @author Yi Zhang
 * @version 1.4
 * @since 1.4
 */
public class INSELWinTools {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(INSELWinTools.class);

    /**
     * Put the name of Output printers files in a list
     * @param printers String with the name of Output printers files in jEPlus format
     * @return the name of the Output printers files in a list
     */
     public static ArrayList<String> getPrintersFunc(String printers) {
        String [] PrintList = printers.split("\\s*[,;:\"]\\s*");
        ArrayList<String> Printers = new ArrayList<>();
        for (int i = 0; i < PrintList.length; i++) {
            if (!PrintList[i].trim().isEmpty()) {
                int cont = PrintList[i].indexOf(".");                   
                if (PrintList[i].indexOf(".", cont+1) != -1) {
                    String [] aux = PrintList[i].trim().split("\\s*[ ]\\s*");
                    for (String aux1 : aux) {
                        if (!Printers.contains(aux1)) {
                            Printers.add(aux1.toLowerCase());
                        }
                    }    
                }else {
                    if (!Printers.contains(PrintList[i].trim())) {
                        Printers.add(PrintList[i].trim().toLowerCase());
                    }                      
                }
            }
        }
        return Printers;
    }    
     
    
    /**
     * Update the problem setup by replacing the search strings in the model template
     * file with the new value strings, and save the new file in the working directory
     * with the default model input file name ("in.insel" for INSEL).
     * This function changes all references to other files found in the model to the absolute path
     * form, except for the printers (output blocks)
     * @param ModelIn The template model file that contains the search strings
     * @param ModelDir The directory of template 
     * @param ModelOut The target model file in the working directory (in.insel by default)
     * @param WorkDir The working directory in which the new model file will be saved
     * @param searchstrings The list of search strings to be looked up
     * @param newvals The list of new values to be used to replace the search strings
     * @param printers The printers in the model. Printers file name will not be changed into absolute form
     * @return state of execution
     */  
    public static boolean updateModelFile(String ModelIn, String ModelDir, String ModelOut, String WorkDir, String[] searchstrings, String[] newvals, String printers) {

        boolean success = true;
        // Split printer file names
        ArrayList<String> Printers = getPrintersFunc(printers);
        // Load and edit the template file
        try {
            File workdir = new File (WorkDir);
            if (! workdir.exists()) success = workdir.mkdirs();
            if (success && workdir.isDirectory()) {
                BufferedReader ins = new BufferedReader(new InputStreamReader(new FileInputStream(ModelDir + ModelIn), "ISO-8859-1"));
                PrintWriter outs = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WorkDir + ModelOut), "ISO-8859-1"));
                String line = ins.readLine();
                while (line != null) {
                    // Replace search tags
                    for (int i = 0; i < searchstrings.length; i++) {
                        line = line.replaceAll(searchstrings[i], newvals[i]);
                    }
                    // Handles relative paths in the model (to be implements)
//                    if (line.trim().startsWith("ASSIGN")) {
//                        
//                        line = TRNSYSUpdateAssign(line, ModelDir, WorkDir, searchstrings, newvals, Printers);
//                        
//                    }else if (line.trim().startsWith("INCLUDE")) {
//                        
//                        boolean ok = false;
//                        String [] include = new String[0];
//                        if (line.indexOf("\"") != -1) {
//                            include = line.trim().split("\\s*\"\\s*");
//                        }else {
//                            include = line.trim().split("\\s* \\s*");
//                        }
//                        String cmd = RelativeDirUtil.checkAbsolutePath(include[1].trim(), ModelDir);                        
//                        String incluname = new File(include[1].trim()).getName();
//                        try {    
//                            BufferedReader inc = new BufferedReader(new InputStreamReader(new FileInputStream(cmd), "ISO-8859-1"));
//                            PrintWriter outc = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WorkDir + incluname), "ISO-8859-1"));
//                            String line2 = inc.readLine();
//                            while (line2 != null) {
//                                for (int j = 0; j < searchstrings.length; j++) {
//                                    if ((!ok) && (line2.indexOf(searchstrings[j]) != -1)){
//                                        ok = true;
//                                    }
//                                    line2 = line2.replaceAll(searchstrings[j], newvals[j]);                                        
//                                }
//                                if (line2.trim().startsWith("ASSIGN")) {  
//                                    line2 = TRNSYSUpdateAssign(line2, ModelDir, WorkDir, searchstrings, newvals, Printers);
//                                    ok = true;
//                                }
//                                outc.println(line2);
//                                line2 = inc.readLine();
//                            }
//                            outc.flush();
//                            outc.close();
//                            inc.close();
//                        }catch (Exception ex) {
//                            ex.printStackTrace();
//                            success = false;
//                        }
//                        if (ok){
//                            line = include[0].trim() + " \"" + incluname + "\"";
//                            TRNSYSTask.setjeplusfile(incluname);
//                        }else {
//                            line = include[0].trim() + " \"" + cmd + "\"";
//                            new File(WorkDir + incluname).delete();
//                        }
//                    }
                    // Write to file
                    outs.println(line);
                    // Next line
                    line = ins.readLine();
                }
                outs.flush();
                outs.close();
                ins.close();
            } else {
                success = false;
            }
            
        } catch (Exception e) {
            logger.error("Error updating model file.", e);
            success = false;
        }

        return success;
    }
    
    public static boolean cleanupWorkDir(String workdir, boolean keepeplus, boolean keepjeplus, boolean keepdir, String filesToDelete, String printers) {
        boolean success = true;

        // Create the directory
        File dir = new File(workdir);
        if (dir.exists()) {
            if (!keepdir) {
                File [] files = dir.listFiles();
                for (int i=0; i<files.length; i++) files[i].delete();
                success = dir.delete();
            } else {
                if (filesToDelete != null) {
                    String [] patterns = filesToDelete.split("\\s*[,;: ]\\s*");
                    OrFileFilter filter = new OrFileFilter ();
                    for (int i=0; i<patterns.length; i++) {
                        filter.addFileFilter(new WildcardFileFilter (patterns[i]));
                    }
                    File [] files = dir.listFiles((FileFilter)filter);
                    for (int i=0; i<files.length; i++) success &= files[i].delete();
                }
            }
        }
        return success;
    }

    /**
     * Create working directory and prepare input files for simulation
     * @param workdir The directory to be created
     * @return Preparation successful or not
     */
    public static boolean prepareWorkDir(String workdir) {
        boolean success = true;
        // Create the directory
        File dir = new File(workdir);
        if (!dir.exists()) {
            success = dir.mkdirs();
        } else if (!dir.isDirectory()) {
            System.err.println(dir.toString() + " is present but not a directory.");
            success = false;
        }
        if (success) {
            // Copying all include and external files to the work directory
            // success = success && fileCopy(weatherfile, workdir + EPlusConfig.getEPDefEPW());
            // if (! success)
                // System.err.println("TRNSYSWinTools.prepareWorkDir(): cannot copy all neccessray files to the working directory.");
            File [] files = dir.listFiles();
            for (int i=0; i<files.length; i++) files[i].delete();
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
            logger.error("Error writing the marker " + marker, ex);
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
            logger.error("Error removing marker " + marker, ex);
        }
        return false;
    }

    /**
     * Call INSEL executable file to run the simulation
     * @param config INSEL Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param useReadVars Whether or not to use readvars after simulation
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runINSEL(INSELConfig config, String WorkDir, String modelfile) {

        int ExitValue = -99;

        try {
            // Trace simulation time
            Date start = new Date ();

            // Run EnergyPlus executable
            String CmdLine = config.getResolvedInselEXEC() + " " + modelfile;
            Process EPProc = Runtime.getRuntime().exec(CmdLine, null, new File(WorkDir));

            BufferedReader ins = new BufferedReader(new InputStreamReader(EPProc.getInputStream()));
            // Use console output as the report file
            BufferedWriter outs = new BufferedWriter(new FileWriter(WorkDir + config.ScreenFile, false));
            outs.newLine();
            outs.write("Calling insel.exe - " + (new SimpleDateFormat()).format(start));
            outs.newLine();
            outs.write("Command line: " + WorkDir + ">" + CmdLine);
            outs.newLine();

            int res = ins.read();
            while (res != -1) {
                outs.write(res);
                res = ins.read();
            }
            ins.close();
            outs.newLine();
            outs.write("Simulation time: " + Long.toString((new Date().getTime() - start.getTime())/1000) + " seconds");
            outs.flush();
            outs.close();

            EPProc.waitFor();
            ExitValue = EPProc.exitValue();
        } catch (IOException | InterruptedException e) {
            logger.error("Exception during INSEL execution.", e);
        }

        // Return Radiance exit value
        return ExitValue;
    }

    /**
     * Test if any of the given files in jEPlus format is available in the given  
     * directory as an indicator of successful run.
     * @return boolean True if the file is present at the specified location
     */
    public static boolean isAnyFileAvailable(String FileNames, String workdir) {
      ArrayList<String> filename = getPrintersFunc(FileNames);
      for (int i = 0; i < filename.size(); i++) {
          if (new File (workdir + filename.get(i)).exists()) {
              return true;
          }
      }  
      return false;
    }
}
