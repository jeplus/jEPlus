/**
 * *************************************************************************
 * jEPlus - EnergyPlus shell for parametric studies * Copyright (C) 2010 Yi Zhang <yizhanguk@googlemail.com> * * This program is free
 * software: you can redistribute it and/or modify * it under the terms of the GNU General Public License as published by * the Free
 * Software Foundation, either version 3 of the License, or * (at your option) any later version. * * This program is distributed in the
 * hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the * GNU General Public License for more details. * * You should have received a copy of the GNU General Public
 * License * along with this program. If not, see <http://www.gnu.org/licenses/>. * *
 **************************************************************************
 */
package jeplus;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import jeplus.util.RelativeDirUtil;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: jEPlus - EnergyPlus execution utilities </p>
 * <p>Description: Utilities for updating TRNSYS dirs/files and triggering TRNSYS simulation </p>
 * <p>Copyright: Copyright (c) 2005-2010</p>
 * <p>Company: IESD, De Montfort University</p>
 *
 * @author Yi Zhang, Jose Santiago
 * @version 0.5c
 * @since 0.1
 */
public class TRNSYSWinTools {

    /**
     * Logger
     */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(TRNSYSWinTools.class);

    /**
     * Put the name of Output printers files in a list
     *
     * @printers String with the name of Output printers files in jEPlus format
     * @return the name of the Output printers files in a list
     */
    public static ArrayList<String> getPrintersFunc(String printers) {
        String[] PrintList = printers.split("\\s*[,;:\"]\\s*");
        ArrayList<String> Printers = new ArrayList<>();
        for (int i = 0; i < PrintList.length; i++) {
            if (!PrintList[i].trim().isEmpty()) {
                int cont = PrintList[i].indexOf(".");
                if (PrintList[i].indexOf(".", cont + 1) != -1) {
                    String[] aux = PrintList[i].trim().split("\\s*[ ]\\s*");
                    for (int j = 0; j < aux.length; j++) {
                        if (!Printers.contains(aux[j])) {
                            Printers.add(aux[j].toLowerCase());
                        }
                    }
                } else {
                    if (!Printers.contains(PrintList[i].trim())) {
                        Printers.add(PrintList[i].trim().toLowerCase());
                    }
                }
            }
        }
        return Printers;
    }

    /**
     * Update the DCK template file with the absolute directory for ASSIGN calls
     *
     * @param line Assign line
     * @param DCKDir The directory of template DCK
     * @param WorkDir The working directory in which the new DCK file will be saved
     * @param searchstrings The list of search strings to be looked up
     * @param newvals The list of new values to be used to replace the search strings
     * @param printers The printers in the dck model
     * @return Assign line modified
     */
    public static String TRNSYSUpdateAssign(String line, String DCKDir, String WorkDir, String[] searchstrings, String[] newvals, ArrayList<String> Printers) {

        // Parse Assign statement
        String[] args = new String[0];
        if (line.indexOf("\"") != -1) {
            args = line.trim().split("\\s*\"\\s*");
        } else {
            args = line.trim().split("\\s* \\s*");
        }
        // Check if file name in the output file list
        String assigname = new File(args[1].trim()).getName();
        String fullpath = RelativeDirUtil.checkAbsolutePath(args[1].trim(), DCKDir);
        if (!Printers.contains(assigname.toLowerCase())) {
            if (new File(fullpath).exists()) {
                boolean ok = false;
                try (   BufferedReader insassign = new BufferedReader(new InputStreamReader(new FileInputStream(fullpath), "ISO-8859-1"));
                        PrintWriter outsassign = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WorkDir + assigname), "ISO-8859-1"))) {
                    String line2 = insassign.readLine();
                    while (line2 != null) {
                        for (int j = 0; j < searchstrings.length; j++) {
                            if ((!ok) && (line2.indexOf(searchstrings[j]) != -1)) {
                                ok = true;
                            }
                            line2 = line2.replaceAll(searchstrings[j], newvals[j]);
                        }
                        outsassign.println(line2);
                        line2 = insassign.readLine();
                    }
                    outsassign.flush();
                } catch (Exception ex) {
                    logger.error("Error updating file " + fullpath + " to " + WorkDir + assigname, ex);
                    ok = false;
                }
                if (ok) {
                    line = args[0].trim() + " \"" + assigname + "\" " + args[2].trim();
                    TRNSYSTask.setjeplusfile(assigname);
                } else {
                    line = args[0].trim() + " \"" + fullpath + "\" " + args[2].trim();
                    new File(WorkDir + assigname).delete();
                }
            } else {
                line = args[0].trim() + " \"" + fullpath + "\" " + args[2].trim();
            }
        } else {
            line = args[0].trim() + " \"" + assigname + "\" " + args[2].trim();
        }
        return line;
    }

    /**
     * Update the line in the DCK template file where ASSIGN call is used. This function copies the "assigned" file into the work folder
     * and strip off any paths
     *
     * @param line Assign line
     * @param DCKDir The directory of template DCK
     * @param WorkDir The working directory in which the new DCK file will be saved
     * @param searchstrings The list of search strings to be looked up
     * @param newvals The list of new values to be used to replace the search strings
     * @param Printers The printers in the dck model
     * @return Assign line modified
     */
    public static String TRNSYSUpdateAssign2(String line, String DCKDir, String WorkDir, String[] searchstrings, String[] newvals, ArrayList<String> Printers) {

        // Parse Assign statement - file name should reside in a pair of " " or immediately after "ASSIGN"
        String[] args = new String[0];
        if (line.indexOf("\"") != -1) {
            args = line.trim().split("\\s*\"\\s*");
        } else {
            args = line.trim().split("\\s* \\s*");
        }
        // Check if file name in the output file list
        String assigname = new File(args[1].trim()).getName();
        String fullpath = RelativeDirUtil.checkAbsolutePath(args[1].trim(), DCKDir);
        if (!Printers.contains(assigname.toLowerCase())) {
            if (new File(fullpath).exists()) {
                boolean ok = true;
                try (   BufferedReader insassign = new BufferedReader(new InputStreamReader(new FileInputStream(fullpath), "ISO-8859-1"));
                        PrintWriter outsassign = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WorkDir + assigname), "ISO-8859-1"))) {
                    String line2 = insassign.readLine();
                    while (line2 != null) {
                        for (int j = 0; j < searchstrings.length; j++) {
                            line2 = line2.replaceAll(searchstrings[j], newvals[j]);
                        }
                        outsassign.println(line2);
                        line2 = insassign.readLine();
                    }
                    outsassign.flush();
                } catch (Exception ex) {
                    logger.error("Error updating file " + fullpath + " to " + WorkDir + assigname, ex);
                    ok = false;
                }
                if (ok) {
                    line = args[0].trim() + " \"" + assigname + "\" " + args[2].trim();
                    TRNSYSTask.setjeplusfile(assigname);
                } else {
                    line = args[0].trim() + " \"" + fullpath + "\" " + args[2].trim();
                    new File(WorkDir + assigname).delete();
                }
            } else {
                line = args[0].trim() + " \"" + fullpath + "\" " + args[2].trim();
            }
        } else {
            line = args[0].trim() + " \"" + assigname + "\" " + args[2].trim();
        }
        return line;
    }

    /**
     * Update the DCK template file with the absolute directory for INCLUDE calls
     * @param fn The target DCK file
     * @param DCKTemplate The template DCK file that contains the search strings
     * @param TargetDir The working directory in which the new DCK file will be saved
     * @return state of execution
     */
    public static boolean TRNSYSUpdateInclude(String fn, String TargetDir, String DCKTemplate, String DCKDir, boolean a) {
        boolean success = true;

        // Load and edit the template file
        try (BufferedReader ins = new BufferedReader(new FileReader(DCKDir + DCKTemplate));
            PrintWriter outs = new PrintWriter(new FileWriter(TargetDir + fn))) {
        
            String line = ins.readLine();
            while (line != null) {
                if (line.trim().startsWith("INCLUDE")) {
                    String include = line.trim().substring(7).toString().trim().replaceAll("\"", "");
                    if (include.indexOf(" ") > 0) {
                        include = include.substring(0, include.indexOf(" ")).replaceAll("\"", "");
                    }
                    String cmd = RelativeDirUtil.checkAbsolutePath(include, DCKDir);
                    outs.println("*+*\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                    outs.println("*     --> INCLUDE FILE: " + "\"" + cmd + "\" <--");
                    outs.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                    try (BufferedReader inc = new BufferedReader(new FileReader(cmd))) {
                        String line2 = inc.readLine();
                        while (line2 != null) {
                            outs.println(line2);
                            line2 = inc.readLine();
                        }
                    }catch (Exception ex) {
                        logger.error("Error reading include file " + cmd, ex);
                        outs.println("* Error reading include file... ");
                        success = false;
                    }
                    outs.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                    outs.println("*     --> END OF INCLUDE FILE: " + "\"" + cmd + "\" <--");
                    line = "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n*+*";
                }
                outs.println(line);
                line = ins.readLine();
            }
            outs.flush();
        } catch (Exception e) {
            logger.error("Error updating include file information in " + DCKDir + DCKTemplate + " to " + TargetDir + fn, e);
            success = false;
        }
        return success;
    }

    /**
     * Update the DCK template file with the absolute directory for INCLUDE calls
     *
     * @param DCKin The template DCK file that contains the search strings
     * @return state of execution
     */
    public static boolean TRNSYSUpdateInclude(String DCKin, String TemplateBaseDir, String DCKout, String WorkingDir, String printers) {

        boolean success = true;
        // Split printer file names
        ArrayList<String> Printers = getPrintersFunc(printers);
        // Load and edit the template file
        try {
            // source
            BufferedReader ins = new BufferedReader(new InputStreamReader(new FileInputStream(TemplateBaseDir + DCKin), "ISO-8859-1"));
            // target
            File workdir = new File(WorkingDir);
            if (!workdir.exists()) {
                success = workdir.mkdirs();
            }
            if (success && workdir.isDirectory()) {
                PrintWriter outs = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WorkingDir + DCKout), "ISO-8859-1"));
                String line = ins.readLine();
                while (line != null) {
                    // Look for Include command
                    if (line.trim().startsWith("INCLUDE")) {
                        String[] include = new String[0];
                        if (line.indexOf("\"") != -1) {
                            include = line.trim().split("\\s*\"\\s*");
                        } else {
                            include = line.trim().split("\\s* \\s*");
                        }
                        String cmd = RelativeDirUtil.checkAbsolutePath(include[1].trim(), TemplateBaseDir);

                        if (!(new File(cmd).exists())) {
                            success = false;
                            outs.println("*+*\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                            outs.println("*     --> INCLUDE FILE: " + "\"" + cmd + "\" HAS NOT BEEN FOUND <--");
                            outs.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                        } else {
                            outs.println("*+*\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                            outs.println("*     --> INCLUDE FILE: " + "\"" + cmd + "\" <--");
                            outs.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                            try {
                                BufferedReader inc = new BufferedReader(new InputStreamReader(new FileInputStream(cmd), "ISO-8859-1"));
                                String line2 = inc.readLine();
                                while (line2 != null) {
                                    // Handles relative paths in the included content
                                    if (line2.trim().startsWith("ASSIGN")) {
                                        // Parse Assign statement
                                        String[] args = new String[0];
                                        if (line2.indexOf("\"") != -1) {
                                            args = line2.trim().split("\\s*\"\\s*");
                                        } else {
                                            args = line2.trim().split("\\s* \\s*");
                                        }
                                        // Check if file name in the output file list
                                        String includepath = new String();
                                        if (cmd.lastIndexOf(File.separator) == cmd.length()) {
                                            includepath = cmd.substring(0, cmd.length() - 1);
                                        } else {
                                            includepath = cmd;
                                        }
                                        includepath = includepath.substring(0, includepath.lastIndexOf(File.separator) + 1);
                                        if (!Printers.contains(new File(args[1].trim().toLowerCase()).getName().toString())) {
                                            String fullpath = RelativeDirUtil.checkAbsolutePath(args[1].trim(), includepath);
                                            line2 = args[0].trim() + " \"" + fullpath.trim() + "\" " + args[2].trim();
                                        } else {
                                            line2 = args[0].trim() + " \"" + new File(args[1].trim()).getName().toString() + "\" " + args[2].trim();
                                        }
                                    }
                                    outs.println(line2);
                                    line2 = inc.readLine();
                                }
                                inc.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                success = false;
                                outs.println("*     --> INCLUDE FILE IS NOT VALID OR NOT EDITABLE <--");
                            }
                            outs.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                            outs.println("*     --> END OF INCLUDE FILE: " + "\"" + cmd + "\" <--");
                            line = "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n*+*";
                        }
                    }
                    outs.println(line);
                    line = ins.readLine();
                }
                outs.flush();
                outs.close();
            }
            ins.close();
//            File outfile = new File(WorkingDir + DCKout);
//            File infile = new File(WorkingDir + DCKin);
//            infile.delete();
//            outfile.renameTo(new File(DCKin));
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /**
     * Save the changes of the part INCLUDE from the DCK template to the original INCLUDE file(s)
     *
     * @param DCKTemplate The template DCK file that contains the search strings
     * @return state of execution
     */
    public static boolean TRNSYSSaveIncludeChanges(String DCKTemplate, String DCKDir) {

        boolean success = true;

        // Load and edit the template file
        try {
            BufferedReader ins = new BufferedReader(new FileReader(DCKDir + DCKTemplate));
            String line = ins.readLine();
            while (line != null) {
                if (line.trim().startsWith("*     --> INCLUDE FILE: ")) {
                    String incDir = line.substring(23).trim();
                    incDir = incDir.substring(0, incDir.indexOf(" ")).trim().replaceAll("\"", "");
                    line = ins.readLine();
                    PrintWriter outs = new PrintWriter(new FileWriter(incDir));
                    while ((line != null) && (!line.trim().startsWith("*     --> END OF INCLUDE FILE: "))) {
                        if (line.indexOf("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *") == -1) {
                            outs.println(line);
                        }
                        line = ins.readLine();
                    }
                    outs.flush();
                    outs.close();
                }
                line = ins.readLine();
            }
            ins.close();

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /**
     * Update the problem setup by replacing the searchstrings in the DCK template file with the newvals strings, and save the new file in
     * the working directory with the given name + ".dck".
     *
     * @param fn The target DCK file without the extension
     * @param DCKTemplate The template DCK file that contains the search strings
     * @param searchstrings The list of search strings to be looked up
     * @param newvals The list of new values to be used to replace the search strings
     * @param TarjetDir The working directory in which the new DCK file will be saved
     * @return state of execution
     */
    public static boolean updateDCKFile(String fn, String TargetDir, String DCKTemplate, String DCKDir, String[] searchstrings, String[] newvals) {

        boolean success = true;

        // Load and edit the template file
        try {
            BufferedReader ins = new BufferedReader(new FileReader(DCKDir + DCKTemplate));
            PrintWriter outs = new PrintWriter(new FileWriter(TargetDir + fn));
            String line = ins.readLine();
            int n = searchstrings.length;
            while (line != null) {
                for (int i = 0; i < n; i++) {
                    if ((line.indexOf(searchstrings[i]) != -1) && (line.trim().startsWith("ASSIGN"))) {
                        String fileto = RelativeDirUtil.checkAbsolutePath(new File(newvals[i]).getName().toString(), TargetDir);
                        String filefrom = RelativeDirUtil.checkAbsolutePath(newvals[i], DCKDir);
                        success = fileCopy(filefrom, fileto);
                        String LogicalNumber = line.trim().substring(6).toString().trim();
                        LogicalNumber = LogicalNumber.substring(LogicalNumber.indexOf(" ")).trim();
                        if (LogicalNumber.indexOf(" ") > 0) {
                            LogicalNumber = LogicalNumber.substring(0, LogicalNumber.indexOf(" "));
                        }
                        line = "ASSIGN " + "\"" + filefrom + "\" " + LogicalNumber;
                    }
                    line = line.replaceFirst(searchstrings[i], newvals[i]);
                }
                outs.println(line);
                line = ins.readLine();
            }
            ins.close();
            outs.flush();
            outs.close();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    /**
     * Update the problem setup by replacing the searchstrings in the DCK template file with the newvals strings, and save the new file in
     * the working directory with the default TRNSYS input file name ("in.dck" at present).
     *
     * @param DCKin The template DCK file that contains the search strings
     * @param DCKDir The directory of template DCK
     * @param DCKout The target DCK file in the working directory (in.dck by default)
     * @param WorkDir The working directory in which the new DCK file will be saved
     * @param searchstrings The list of search strings to be looked up
     * @param newvals The list of new values to be used to replace the search strings
     * @param printers The printers in the dck model. Printer's file name will not be changed into absolute form
     * @return state of execution
     */
    public static boolean updateDCKFile(String DCKin, String DCKDir, String DCKout, String WorkDir, String[] searchstrings, String[] newvals, String printers) {

        boolean success = true;
        // Split printer file names
        ArrayList<String> Printers = getPrintersFunc(printers);
        // Load and edit the template file
        try {
            File workdir = new File(WorkDir);
            if (!workdir.exists()) {
                success = workdir.mkdirs();
            }
            if (success && workdir.isDirectory()) {
                BufferedReader ins = new BufferedReader(new InputStreamReader(new FileInputStream(DCKDir + DCKin), "ISO-8859-1"));
                PrintWriter outs = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WorkDir + DCKout), "ISO-8859-1"));
                String line = ins.readLine();
                while (line != null) {
                    // Replace search tags
                    for (int i = 0; i < searchstrings.length; i++) {
                        line = line.replaceAll(searchstrings[i], newvals[i]);
                    }
                    // Handles relative paths in the DCK model
                    if (line.trim().startsWith("ASSIGN")) {

                        line = TRNSYSUpdateAssign2(line, DCKDir, WorkDir, searchstrings, newvals, Printers);

                    } else if (line.trim().startsWith("INCLUDE")) {

                        boolean ok = false;
                        String[] include = new String[0];
                        if (line.indexOf("\"") != -1) {
                            include = line.trim().split("\\s*\"\\s*");
                        } else {
                            include = line.trim().split("\\s* \\s*");
                        }
                        String cmd = RelativeDirUtil.checkAbsolutePath(include[1].trim(), DCKDir);
                        String incluname = new File(include[1].trim()).getName();
                        try {
                            BufferedReader inc = new BufferedReader(new InputStreamReader(new FileInputStream(cmd), "ISO-8859-1"));
                            PrintWriter outc = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WorkDir + incluname), "ISO-8859-1"));
                            String line2 = inc.readLine();
                            while (line2 != null) {
                                for (int j = 0; j < searchstrings.length; j++) {
                                    if ((!ok) && (line2.indexOf(searchstrings[j]) != -1)) {
                                        ok = true;
                                    }
                                    line2 = line2.replaceAll(searchstrings[j], newvals[j]);
                                }
                                if (line2.trim().startsWith("ASSIGN")) {
                                    line2 = TRNSYSUpdateAssign2(line2, DCKDir, WorkDir, searchstrings, newvals, Printers);
                                    ok = true;
                                }
                                outc.println(line2);
                                line2 = inc.readLine();
                            }
                            outc.flush();
                            outc.close();
                            inc.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            success = false;
                        }
                        if (ok) {
                            line = include[0].trim() + " \"" + incluname + "\"";
                            TRNSYSTask.setjeplusfile(incluname);
                        } else {
                            line = include[0].trim() + " \"" + cmd + "\"";
                            new File(WorkDir + incluname).delete();
                        }
                    }
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
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    public static boolean cleanupWorkDir(String workdir, boolean keepeplus, boolean keepjeplus, boolean keepdir, String filesToDelete, String printers) {
        boolean success = true;

        // Create the directory
        File dir = new File(workdir);
        ArrayList<String> jeplus = TRNSYSTask.getjeplusfiles();
        TRNSYSTask.setjeplusfile(TRNSYSConfig.getTRNSYSDefDCK());
        if (dir.exists()) {
            if (!keepdir) {
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
                success = dir.delete();
            } else {
                if (!keepjeplus) {
                    File[] files = dir.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if ((jeplus.contains(files[i].getName())) && (files[i].getName().indexOf(TRNSYSConfig.getTRNSYSDefLST()) == -1)) {
                            success &= files[i].delete();
                        }
                    }
                }
                if (!keepeplus) {
                    File[] files = dir.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if ((!jeplus.contains(files[i].getName())) && (!getPrintersFunc(printers).contains(files[i].getName().toLowerCase())) && (files[i].getName().indexOf(TRNSYSConfig.getTRNSYSDefLST()) == -1)) {
                            success &= files[i].delete();
                        }
                    }
                }
                if (filesToDelete != null) {
                    String[] patterns = filesToDelete.split("\\s*[,;: ]\\s*");
                    OrFileFilter filter = new OrFileFilter();
                    for (int i = 0; i < patterns.length; i++) {
                        filter.addFileFilter(new WildcardFileFilter(patterns[i]));
                    }
                    File[] files = dir.listFiles((FileFilter) filter);
                    for (int i = 0; i < files.length; i++) {
                        success &= files[i].delete();
                    }
                }
            }
        }
        return success;
    }

    /**
     * Create working directory and prepare input files for simulation
     *
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
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
        return success;
    }

    /**
     * Copy a file from one location to another
     *
     * @param from The source file to be copied
     * @param to The target file to write
     * @return Successful or not
     */
    public static boolean fileCopy(String from, String to) {
        boolean success = true;
        try {
            FileReader in = new FileReader(from);
            FileWriter out = new FileWriter(to);
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            in.close();
            out.close();
        } catch (Exception ee) {
            ee.printStackTrace();
            success = false;
        }
        return success;
    }

    /**
     * Write a Job Done marker, typically a file named 'job.done!' in the given folder
     *
     * @param dir The folder in which the marker is written
     * @param marker The marker file name. The file contains a string "Job done!"
     * @return Marker is written successfully or not
     */
    public static boolean writeJobDoneMarker(String dir, String marker) {
        try {
            FileWriter fw = new FileWriter(dir + (dir.endsWith(File.separator) ? "" : File.separator) + marker);
            fw.write("Job done!");
            fw.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Remote the Job Done marker if exists. The marker file is typically named 'job.done!' in the given folder
     *
     * @param dir The folder in which the marker is written
     * @param marker The marker file name. The file contains a string "Job done!"
     * @return Marker is removed successfully or not
     */
    public static boolean removeJobDoneMarker(String dir, String marker) {
        try {
            File markerfile = new File(dir + (dir.endsWith(File.separator) ? "" : File.separator) + marker);
            if (markerfile.exists()) {
                markerfile.delete();
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Call TRNSYS executable file to run the simulation
     *
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param useReadVars Whether or not to use readvars after simulation
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runTRNSYS(TRNSYSConfig config, String WorkDir, String dckfile) {

        int ExitValue = -99;

        try {
            Process EPProc = null;

            // Run EnergyPlus executable
            String CmdLine = config.getResolvedTRNSYSEXEC() + " " + dckfile + " /n /h";
            // Requested by Ewen Raballand on 14-2-2014
            // String CmdLine = config.getResolvedTRNSYSEXEC() + " " + dckfile + " /n";
            EPProc = Runtime.getRuntime().exec(CmdLine, null, new File(WorkDir));

            BufferedReader ins = new BufferedReader(new InputStreamReader(EPProc.getInputStream()));
            BufferedWriter outs = new BufferedWriter(new FileWriter(config.ScreenFile, true));
            outs.newLine();
            outs.write("Calling TRNexe - " + (new SimpleDateFormat()).format(new Date()));
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
            outs.flush();
            outs.close();

            EPProc.waitFor();
            ExitValue += EPProc.exitValue(); // What's TRNSYS exit value?

            // set it to successful
            ExitValue = 0;
        } catch (Exception e) {
            System.err.println("TRNSYSWinTools.runTRNSYS(): "
                    + e.toString());
        }

        // Return Radiance exit value
        return ExitValue;
    }

    /**
     * Test if any of the given files in jEPlus format is available in the given directory as an indicator of successful run.
     *
     * @return boolean True if the file is present at the specified location
     */
    public static boolean isAnyFileAvailable(String FileNames, String workdir) {
        boolean found = true;
        ArrayList<String> filename = getPrintersFunc(FileNames);
        for (int i = 0; i < filename.size(); i++) {
            if (new File(workdir + filename.get(i)).exists()) {
                found = true & found;
            } else {
                found = false & found;
            }
        }
        return found;
    }
}
