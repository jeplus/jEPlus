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
package jeplus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import jeplus.JEPlusConfig;
import jeplus.ScriptConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run Python scripts
 * @author Yi
 */
public class ScriptTools {
  
    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(ScriptTools.class);
    
    /**
     * Run Python script
     * @param config Config file of the executables
     * @param scriptfile Name of the Python file
     * @param arg0 This must be the path of the project base. If null or empty is supplied, the current dir is assumed.
     * @param arg1 This must be the path of the working directory of the script. If null or empty is supplied, the current dir is assumed.
     * @param arg2 This is reserved for job list
     * @param arg3 This is reserved for output file name
     * @param moreargs More arguments
     * @param stream Log stream
     */
    public static void runScript (ScriptConfig config, String scriptfile, String arg0, String arg1, String arg2, String arg3, String moreargs, PrintStream stream) {

        String CurrentWorkDir = (arg1 != null && arg1.trim().length()>0) ? arg1 : "./";
        try {
            StringBuilder buf = new StringBuilder (config.getExec());
            buf.append(" ").append(config.getArgs()).append(" ");
            buf.append(" \"").append(scriptfile).append("\" ");
            buf.append(" \"").append(arg0).append("\" ");
            buf.append("\"").append(CurrentWorkDir).append("\" ");
            buf.append(arg2).append(" ").append(arg3).append(" ");
            buf.append("\"").append(moreargs).append("\" ");

            List<String> command = new ArrayList<> ();
            command.add(config.getExec());
            command.add(config.getArgs());
            command.add(scriptfile);
            if (arg0 != null && arg0.trim().length()>0) command.add(arg0);
            command.add(CurrentWorkDir);
            if (arg2 != null && arg2.trim().length()>0) command.add(arg2);
            if (arg3 != null && arg3.trim().length()>0) command.add(arg3);
            if (moreargs != null && moreargs.trim().length()>0) command.add(moreargs);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File (CurrentWorkDir));
            builder.redirectErrorStream(true);
            Process proc = builder.start();
            // int ExitValue = proc.waitFor();
            try (BufferedReader ins = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                PrintStream logstream = stream;
                if (logstream != null) {
                    logstream.println();
                    logstream.println(buf.toString());
                    logstream.println("-==-");
                    String res = ins.readLine();
                    while (res != null) {
                        logstream.println(res);
                        res = ins.readLine();
                    }
                    logstream.println("-==-");
                    // logstream.println("Python exit value = " + ExitValue);
                }else {
                    int res = ins.read();
                    while (res != -1) {
                        res = ins.read();
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Cannot open command window.", ex);
        }
    }
    
    /**
     * Run script interpreter for pre-processing. Job list and output file name args 
     * are not needed for this function.
     * @param config Config file of the executables
     * @param scriptfile Name of the Python file
     * @param arg0 This must be the path of the project base. If null or empty is supplied, the current dir is assumed.
     * @param arg1 This must be the path of the working directory of the script. If null or empty is supplied, the current dir is assumed.
     * @param arg2 This is an additional path to be passed to the script. One use case is for passing the location of Energy+.idd
     * @param param_args an array of parameter args supplied in a ',' delimited string
     * @param stream Log stream
     */
    public static void runScript (ScriptConfig config, String scriptfile, String arg0, String arg1, String arg2, String param_args, PrintStream stream) {

        String CurrentWorkDir = (arg1 != null && arg1.trim().length()>0) ? arg1 : "./";
        String AdditionalDir = (arg2 != null && arg2.trim().length()>0) ? arg2 : "./";
        try {
            StringBuilder buf = new StringBuilder (config.getExec());
            buf.append(" ").append(config.getArgs()).append(" ");
            buf.append("\"").append(scriptfile).append("\" ");
            buf.append("\"").append(arg0).append("\" ");
            buf.append("\"").append(CurrentWorkDir).append("\" ");
            buf.append("\"").append(param_args).append("\" ");
            buf.append("\"").append(AdditionalDir).append("\" ");

            List<String> command = new ArrayList<> ();
            command.add(config.getExec());
            command.add(config.getArgs());
            command.add(scriptfile);
            if (arg0 != null && arg0.trim().length()>0) command.add(arg0);
            command.add(CurrentWorkDir);
            if (param_args != null && param_args.trim().length()>0) command.add(param_args);
            command.add(AdditionalDir);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File (CurrentWorkDir));
            builder.redirectErrorStream(true);
            Process proc = builder.start();
            // int ExitValue = proc.waitFor();
            try (BufferedReader ins = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                PrintStream logstream = stream;
                if (logstream != null) {
                    logstream.println();
                    logstream.println(buf.toString());
                    logstream.println("-==-");
                    String res = ins.readLine();
                    while (res != null) {
                        logstream.println(res);
                        res = ins.readLine();
                    }
                    logstream.println("-==-");
                    // logstream.println("Python exit value = " + ExitValue);
                }else {
                    int res = ins.read();
                    while (res != -1) {
                        res = ins.read();
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Cannot open command window.", ex);
        }
    }
    
    /**
     * Run Script interpreter executable to get its version info
     * @param config Config file of the executables
     * @return 
     */
    public static String getVersion (ScriptConfig config) {
        String exec = config.getExec();
        if (new File(exec).exists()) {
            try {
                List<String> command = new ArrayList<> ();
                command.add(exec);
                command.add(config.getVerCmd());
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.directory(new File ("./"));
                builder.redirectErrorStream(true);
                Process proc = builder.start();
                // int ExitValue = proc.waitFor();
                StringBuilder buf = new StringBuilder();
                try (BufferedReader ins = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    String res = ins.readLine();
                    // Read just the first line
                    buf.append(res);
                    while (res != null) {
    //                    buf.append(res);
                        res = ins.readLine();
                    }
                }
                if (proc.exitValue() != 0) {
                    return "Error: " + buf.toString();
                }
                return buf.toString();
            } catch (IOException ex) {
                logger.error("Cannot open command window.", ex);
            }
        }else {
            return "Cannot run version command with " + config.getExec();
        }
        return "Error: unknown";
    }
    
}
