/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jeplus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import jeplus.JEPlusConfig;
import jeplus.gui.JPanelRunPython;
import org.python.core.PyException;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class PythonTools {
  
    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(JPanelRunPython.class);
    
    /**
     * Run Python script
     * @param config Config file of the executables
     * @param scriptfile Name of the Python file
     * @param version Python version string
     * @param arg0 This must be the path of the working directory of the script. If null or empty is supplied, the current dir is assumed.
     * @param arg1 This is reserved for job list
     * @param arg2 This is reserved for output file name
     * @param moreargs More arguments
     * @param stream Log stream
     */
    public static void runPython (JEPlusConfig config, String scriptfile, String version, String arg0, String arg1, String arg2, String moreargs, PrintStream stream) {

        String CurrentWorkDir = (arg0 != null && arg0.trim().length()>0) ? arg0 : "./";
        if (version.equalsIgnoreCase("jython")) {
            StringBuilder buf = new StringBuilder (scriptfile);
            buf.append(", ").append(CurrentWorkDir);
            if (arg1 != null && arg1.trim().length()>0) buf.append(", ").append(arg1);
            if (arg2 != null && arg2.trim().length()>0) buf.append(", ").append(arg2);
            if (moreargs != null && moreargs.trim().length()>0) buf.append(", ").append(moreargs);
            String [] args = buf.toString().split("\\s*,\\s*");
            PythonInterpreter.initialize(System.getProperties(), System.getProperties(), args);
            PySystemState state = new PySystemState();
            state.argv.clear();
            for (String arg : args) {
                state.argv.append (new PyString (arg));
            }
            PythonInterpreter interp = new PythonInterpreter(null, state);
            interp.setOut(stream);
            interp.setErr(stream);
            try {
                interp.execfile(scriptfile);
            }catch (PyException pye) {
                stream.println();
                stream.println(pye.toString());
            }
            interp.cleanup();
            
        }else {
            String PythonExe;
            if (version.equalsIgnoreCase("python2")) {
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
    }
}
