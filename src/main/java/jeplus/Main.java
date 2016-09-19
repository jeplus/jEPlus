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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import jeplus.agent.EPlusAgent;
import jeplus.agent.EPlusAgentLocal;
import jeplus.data.RandomSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

/**
 * Main entry point to jEPlus. Options to redirect err stream to jeplus.err file
 * @author yi zhang
 * @version 1.3
 * @since 0.1
 */
public class Main {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    /** Flag for redirecting error stream */
    public static final boolean REDIRECT_ERR = false;
    
    /** Default agent to use  */ 
    protected static EPlusAgent DefaultAgent;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        if (REDIRECT_ERR) {
//            try {
//                // Redirect err
//                System.setErr(new PrintStream(new FileOutputStream("jeplus.err")));
//            } catch (FileNotFoundException ex) {
//                //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//                ex.printStackTrace();
//            }
//        }
        
        try {
            // Set cross-platform Java L&F (also called "Metal")
            //            UIManager.setLookAndFeel(
            //                UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            //            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            //                if ("Nimbus".equals(info.getName())) {
            //                    UIManager.setLookAndFeel(info.getClassName());
            //                    break;
            //                }
            //            }
        }catch (UnsupportedLookAndFeelException e) {
            System.err.println("Unsupported Look-And-Feel Option. Reverting to the default UI.");
        }catch (ClassNotFoundException e) {
            System.err.println("Specified Look-And-Feel class cannot be found. Reverting to the default UI.");
        }catch (InstantiationException e) {
            System.err.println("Fialed to instantiate the specified Look-And-Feel. Reverting to the default UI.");          
        }catch (IllegalAccessException e) {
            System.err.println("Cannot access the specified Look-And-Feel. Reverting to the default UI."); 
        }

        // Set locale to UK
        Locale.setDefault(Locale.UK);
        // Set line end to DOS style
        System.setProperty("line.separator", "\r\n");
        // System.setProperty("file.separator", "/");  // seemed to have no effect

        // create the parser
        CommandLineParser parser = new GnuParser();
        Options options = new Main().getCommandLineOptions(null);
        CommandLine commandline = null;
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(80);
        try {
            // parse the command line arguments
            commandline = parser.parse( options, args );
            if (commandline.hasOption("help")) {
                // automatically generate the help statement
                formatter.printHelp( "java -Xmx1000m -jar jEPlus.jar [OPTIONS]", options );    
                System.exit(-1);
            }
            // Set log4j configuration
            if (commandline.hasOption("log")) {
                PropertyConfigurator.configure(commandline.getOptionValue("log"));
            }else {
                PropertyConfigurator.configure("log4j.cfg");
            }
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            // automatically generate the help statement
            formatter.printHelp( "java -Xmx1000m -jar jEPlus.jar [OPTIONS]", options );    
            System.exit(-1);
        }        

        // Call main fuction with commandline
        new Main().mainFunction(commandline);
    }

    /** 
     * Instance main function so it can be inherited
     * @param commandline CommandLine arguments
     */
    public void mainFunction (CommandLine commandline) {
        // jE+ Configuration file
        String cfgfile = "jeplus.cfg";
        if (commandline.hasOption("cfg")) {
            cfgfile = commandline.getOptionValue("cfg");
        }
        // load E+ configuration
        boolean showSplash = false;
        if (! new File (cfgfile).exists()) { showSplash = true; }
        JEPlusConfig.setDefaultInstance(new JEPlusConfig (cfgfile));
        // Set local threads
        int nthread = Runtime.getRuntime().availableProcessors();
        if (commandline.hasOption("local")) {
            try {
                nthread = Integer.parseInt(commandline.getOptionValue("local"));
            }catch (Exception ex) {
            }
        }
        // Get project file
        String prjfile = null;
        if (commandline.hasOption("job")) {
            prjfile = commandline.getOptionValue("job");
        }
        // Get output folder
        String output = null;
        if (commandline.hasOption("output")) {
            output = commandline.getOptionValue("output");
        }
        
        boolean showGUI = true;
        // Prepare simulation manager
        if (prjfile != null) {
            JEPlusProject project = null;
            if (prjfile.endsWith(".jep")) {
                project = JEPlusProject.loadAsXML(new File(prjfile));
            }else if (prjfile.endsWith(".json")) {
                try {
                    project = JEPlusProject.loadFromJSON(new File(prjfile));
                }catch (IOException ioe) {
                    logger.error("Cannot open " + prjfile, ioe);
                }
            }
            if (project != null) {
                EPlusBatch batch = new EPlusBatch (null, project);
                // override output folder and number of threads
                if (output != null) {
                    project.getExecSettings().setParentDir(output);
                }
                project.getExecSettings().setNumThreads(nthread);
                if (DefaultAgent == null) { // Default agent may have been set by external code
                    // set execution agent
                    DefaultAgent = new EPlusAgentLocal (project.getExecSettings());
                }
                batch.setAgent(DefaultAgent);

                // The following are batch mode options. Only one is effective at a time...
                if (commandline.hasOption("all") || commandline.hasOption("sample") || commandline.hasOption("lhs") || 
                    commandline.hasOption("index") || commandline.hasOption("value") || commandline.hasOption("id") || 
                    commandline.hasOption("sobol") || commandline.hasOption("file")) {
                        showGUI = false;
                        // validate project
                        EPlusBatchInfo info = batch.validateProject();
                        System.err.println(info.getValidationErrorsText());
                        if (info.isValidationSuccessful()) {
                            if (commandline.hasOption("all")) {
                                    batch.buildJobs();
                                    batch.start();
                            }else if (commandline.hasOption("sample")) {
                                    long randomseed;
                                    if (commandline.hasOption("seed")) {
                                        try {
                                            randomseed = Long.parseLong(commandline.getOptionValue("seed"));
                                        }catch (NumberFormatException nfe) {
                                            logger.error("Random seed is not a number. Seed is set to 0 (zero).", nfe);
                                            randomseed = 0;
                                        }
                                        project.getExecSettings().setRandomSeed(randomseed);
                                    }else {
                                        randomseed = project.getExecSettings().getRandomSeed();
                                    }
                                    int njobs = Integer.parseInt(commandline.getOptionValue("sample"));
                                    batch.runSample(EPlusBatch.SampleType.SHUFFLE, njobs, RandomSource.getRandomGenerator(randomseed));
                            }else if (commandline.hasOption("lhs")) {
                                    long randomseed;
                                    if (commandline.hasOption("seed")) {
                                        try {
                                            randomseed = Long.parseLong(commandline.getOptionValue("seed"));
                                        }catch (NumberFormatException nfe) {
                                            logger.error("Random seed is not a number. Seed is set to 0 (zero).", nfe);
                                            randomseed = 0;
                                        }
                                        project.getExecSettings().setRandomSeed(randomseed);
                                    }else {
                                        randomseed = project.getExecSettings().getRandomSeed();
                                    }
                                    int njobs = Integer.parseInt(commandline.getOptionValue("lhs"));
                                    batch.runSample(EPlusBatch.SampleType.LHS, njobs, RandomSource.getRandomGenerator(randomseed));
                            }else if (commandline.hasOption("sobol")) {
                                    long randomseed;
                                    if (commandline.hasOption("seed")) {
                                        try {
                                            randomseed = Long.parseLong(commandline.getOptionValue("seed"));
                                        }catch (NumberFormatException nfe) {
                                            logger.error("Random seed is not a number. Seed is set to 0 (zero).", nfe);
                                            randomseed = 0;
                                        }
                                        project.getExecSettings().setRandomSeed(randomseed);
                                    }else {
                                        randomseed = project.getExecSettings().getRandomSeed();
                                    }
                                    int njobs = Integer.parseInt(commandline.getOptionValue("sobol"));
                                    batch.runSample(EPlusBatch.SampleType.SOBOL, njobs, RandomSource.getRandomGenerator(randomseed));
                            }else if (commandline.hasOption("index")) {
                                    batch.runJobSet(EPlusBatch.JobStringType.INDEX, commandline.getOptionValue("index"));
                            }else if (commandline.hasOption("value")) {
                                    batch.runJobSet(EPlusBatch.JobStringType.VALUE, commandline.getOptionValue("value"));
                            }else if (commandline.hasOption("id")) {
                                    batch.runJobSet(EPlusBatch.JobStringType.ID, commandline.getOptionValue("id"));
                            }else if (commandline.hasOption("file")) {
                                    batch.runJobSet(EPlusBatch.JobStringType.FILE, commandline.getOptionValue("file"));
                            }
                            do {
                                try {
                                    Thread.sleep(1000);
                                }catch (InterruptedException intex) {
                                    logger.info("Interruption detected. Simulation is still running.");
                                }
                            }while (batch.isSimulationRunning());
                            System.out.println("All jobs finished. jEPlus terminated normally.");
                        }else {
                            System.err.println("jEPlus cannot execute the jobs. Please make sure the project is valid.");
                        }
                }
            }else {
                System.err.println("jEPlus cannot open project from " + prjfile + ". Please make sure the project file is accessible and in either .jep or .json format.");
            }
        }
        
        if (showGUI == true) {
            JEPlusFrameMain.startGUI(new JEPlusFrameMain(), prjfile, showSplash);
        }
    }
    
    /** 
     * Create command line options with Apache Commons Cli
     * @param opts Options object to which new args to be appended
     * @return Options object
     */
    protected Options getCommandLineOptions (Options opts) {
        Option help = new Option( "help", "Show this message" );
        
        Option cfg = Option.builder("cfg").argName( "config file" )
                                        .hasArg()
                                        .desc(  "Load jEPlus configuration file. Default=./jeplus.cfg" )
                                        .build();

        Option log = Option.builder("log").argName( "log config file" )
                                        .hasArg()
                                        .desc(  "Specify the configuration file for logs. Default=./log4j.cfg" )
                                        .build();

        Option job = Option.builder("job").argName( "project file" )
                                        .hasArg()
                                        .desc(  "Open project file in either XML (.jep) or JSON (.json) format" )
                                        .build();
        
        Option run_all = new Option( "all", "Execute all jobs in project" );
        
        Option run_sample   = Option.builder("sample").argName( "sample size" )
                                        .hasArg()
                                        .desc(  "Execute a random sample in project. Project size limit applies. Effective with -job" )
                                        .build();
        Option run_lhs   = Option.builder("lhs").argName( "sample size" )
                                        .hasArg()
                                        .desc(  "Execute a Latin Hypercube sample in project. Effective with -job" )
                                        .build();
        Option run_sobol   = Option.builder("sobol").argName( "sample size" )
                                        .hasArg()
                                        .desc(  "Execute a Sobol sample in project. Effective with -job" )
                                        .build();
        Option random_seed   = Option.builder("seed").argName( "random seed" )
                                        .hasArg()
                                        .desc(  "Use the given random seed for sampling. If seed is not specified, jEPlus uses the seed saved in the project. This option is effective only with -sample and -lhs" )
                                        .build();
        Option run_index   = Option.builder("index").argName( "job indexes" )
                                        .hasArg()
                                        .desc(  "Execute selected jobs in project using specified parameter value indexes. Effective with -job" )
                                        .build();
        Option run_value   = Option.builder("value").argName( "job values" )
                                        .hasArg()
                                        .desc(  "Execute selected jobs in project using specified parameter values. Effective with -job" )
                                        .build();
        Option run_id   = Option.builder("id").argName( "job ids" )
                                        .hasArg()
                                        .desc(  "Execute selected jobs in project using specified job id strings. Effective with -job" )
                                        .build();
        Option run_file   = Option.builder("file").argName( "job list file" )
                                        .hasArg()
                                        .desc(  "Execute selected jobs in project using a job list file. Effective with -job" )
                                        .build();
        Option output_folder   = Option.builder("output").argName( "output folder" )
                                        .hasArg()
                                        .desc(  "Use the specified folder for outputs." )
                                        .build();
        Option local   = Option.builder("local").argName( "number of threads" )
                                        .hasArg()
                                        .desc(  "Use specified number of local threads for parallel execution." )
                                        .build();
        Option post   = Option.builder("post").argName( "post-process script" )
                                        .hasArg()
                                        .desc(  "Python script file for post-processing after simulation." )
                                        .build();

        Options options = (opts == null) ? new Options() : opts;

        options.addOption( help );
        options.addOption( cfg );
        options.addOption(log);
        options.addOption( job );
        options.addOption( run_all );
        options.addOption( run_sample );
        options.addOption( run_lhs );
        options.addOption( run_sobol );
        options.addOption( random_seed );
        options.addOption( run_index );
        options.addOption( run_value );
        options.addOption( run_id );
        options.addOption( run_file );
        options.addOption( output_folder );
        options.addOption( local );
        options.addOption(post);
        
        return options;
    }

}
