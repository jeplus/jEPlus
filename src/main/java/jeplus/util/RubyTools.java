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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run Ruby scripts
 * @author Yi
 */
public class RubyTools {
  
    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(RubyTools.class);
    
    
    /**
     * @todo: !This is not working yet!
     * Run Ruby script 
     * @param config Config file of the executables
     * @param scriptfile Name of the Python file
     * @param version Python version string
     * @param arg0 This must be the path of the working directory of the script. If null or empty is supplied, the current dir is assumed.
     * @param arg1 This is reserved for job list
     * @param arg2 This is reserved for output file name
     * @param moreargs More arguments
     * @param stream Log stream
     */
    public static void runRuby (JEPlusConfig config, String scriptfile, String version, String arg0, String arg1, String arg2, String moreargs, PrintStream stream) {

        String CurrentWorkDir = (arg0 != null && arg0.trim().length()>0) ? arg0 : "./";
        if (version.equalsIgnoreCase("jruby")) {
//            StringBuilder buf = new StringBuilder (scriptfile);
//            buf.append(", ").append(CurrentWorkDir);
//            if (arg1 != null && arg1.trim().length()>0) buf.append(", ").append(arg1);
//            if (arg2 != null && arg2.trim().length()>0) buf.append(", ").append(arg2);
//            if (moreargs != null && moreargs.trim().length()>0) buf.append(", ").append(moreargs);
//            String [] args = buf.toString().split("\\s*,\\s*");
//            PythonInterpreter.initialize(System.getProperties(), System.getProperties(), args);
//            PySystemState state = new PySystemState();
//            state.argv.clear();
//            for (String arg : args) {
//                state.argv.append (new PyString (arg));
//            }
//            PythonInterpreter interp = new PythonInterpreter(null, state);
//            interp.setOut(stream);
//            interp.setErr(stream);
//            try {
//                interp.execfile(scriptfile);
//            }catch (PyException pye) {
//                stream.println();
//                stream.println(pye.toString());
//            }
//            interp.cleanup();
            stream.println("JRuby is no longer supported! Use external Ruby instead.");
            logger.error("JRuby is no longer supported! Use external Ruby instead.");
            
        }else {
            String PythonExe;
            if (version.equalsIgnoreCase("ruby")) {
                PythonExe = config.getPython2EXE() == null ? null : config.getPython2EXE();
            }else {
                PythonExe = config.getPython3EXE() == null ? null : config.getPython3EXE();
            }
            try {
                StringBuilder buf = new StringBuilder (PythonExe);
                buf.append(" \"").append(scriptfile).append("\" ");
                buf.append("\"").append(CurrentWorkDir).append("\" ");
                buf.append(arg1).append(" ").append(arg2).append(" ");
                buf.append("\"").append(moreargs).append("\" ");

                List<String> command = new ArrayList<> ();
                command.add(PythonExe);
                command.add(scriptfile);
                command.add(CurrentWorkDir);
                if (arg1 != null && arg1.trim().length()>0) command.add(arg1);
                if (arg2 != null && arg2.trim().length()>0) command.add(arg2);
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
                        // logstream.println("Ruby exit value = " + ExitValue);
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
    }
}
