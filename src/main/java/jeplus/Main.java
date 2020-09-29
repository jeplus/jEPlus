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
import java.net.URISyntaxException;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import jeplus.agent.EPlusAgent;
import jeplus.agent.EPlusAgentLocal;
import jeplus.data.RandomSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
        CommandLineParser parser = new DefaultParser();
        Options options = new Main().getCommandLineOptions(null);
        CommandLine commandline = null;
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(80);
        try {
            // parse the command line arguments
            commandline = parser.parse( options, args );
            if (commandline.hasOption("help")) {
                // automatically generate the help statement
                formatter.printHelp( "java -jar jEPlus.jar [OPTIONS] {project file (.jep or .json)}", options );    
                System.exit(-1);
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
        String cfgfile = JEPlusConfig.getDefaultConfigFile();
        String base = new File ("./").getAbsolutePath();
        try {
            base = new File(JEPlusConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        }catch (URISyntaxException ex) {}
        // load E+ configuration
        boolean showSplash = false;
        if (new File (cfgfile).exists()) {
            JEPlusConfig.setDefaultInstance(JEPlusConfig.getNewInstance(cfgfile));
            logger.info("Configuration loaded from " + new File (cfgfile).getAbsolutePath());
        }else if (new File (base + File.separator + cfgfile).exists()) { 
            JEPlusConfig.setDefaultInstance(JEPlusConfig.getNewInstance(base + File.separator + cfgfile));
            logger.info("Configuration loaded from " + new File (base + File.separator + cfgfile).getAbsolutePath());
        }else {
            logger.info(cfgfile + " not found.");
            showSplash = true; 
        }
        // Set local threads
        int nthread = Runtime.getRuntime().availableProcessors();
        if (commandline.hasOption("parallel")) {
            try {
                int nt = Integer.parseInt(commandline.getOptionValue("parallel"));
                if (nt > 0) nthread = Math.min (nt, nthread);
            }catch (Exception ex) {
            }
        }
        // Get project file
        String prjfile = null;
        if (commandline.hasOption("job")) {
            prjfile = commandline.getOptionValue("job");
        }else if (commandline.getArgs().length > 0) {
            prjfile = commandline.getArgs()[0];
        }
        // Get output folder
        String output = null;
        if (commandline.hasOption("out")) {
            output = commandline.getOptionValue("out");
        }
        
        boolean showGUI = true;
        // Prepare simulation manager
        if (prjfile != null) {
            JEPlusProjectV2 project = null;
            if (prjfile.endsWith(".jep")) {
                project = new JEPlusProjectV2(JEPlusProject.loadAsXML(new File(prjfile)));
            }else if (prjfile.endsWith(".json")) {
                try {
                    project = JEPlusProjectV2.loadFromJSON(new File(prjfile));
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
                if (commandline.hasOption("timeout")) {
                    try {
                        int to = Integer.parseInt(commandline.getOptionValue("timeout"));
                        project.getExecSettings().setTimeout(to);
                    }catch (NumberFormatException ex) {
                        logger.warn("Timeout option is not a number: " + commandline.getOptionValue("timeout"));
                    }
                }
                if (DefaultAgent == null) { // Default agent may have been set by external code
                    // set execution agent
                    DefaultAgent = new EPlusAgentLocal (JEPlusConfig.getDefaultInstance(), project.getExecSettings());
                }
                batch.setAgent(DefaultAgent);

                // The following are batch mode options. Only one is effective at a time...
                if (commandline.hasOption("all") || commandline.hasOption("shuffle") || commandline.hasOption("lhs") || 
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
                            }else if (commandline.hasOption("shuffle")) {
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
                                    int njobs = Integer.parseInt(commandline.getOptionValue("shuffle"));
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
        
        Option job = Option.builder("job").argName( "project file" )
                                        .hasArg()
                                        .desc(  "Open project file in either XML (.jep) or JSON (.json) format" )
                                        .build();
        
        Option run_all = new Option( "all", "Execute all jobs in project" );
        
        Option run_sample   = Option.builder("shuffle").argName( "sample size" )
                                        .hasArg()
                                        .desc(  "Execute a random sample in project. Project size limit applies. Effective only if a project is specified" )
                                        .build();
        Option run_lhs   = Option.builder("lhs").argName( "sample size" )
                                        .hasArg()
                                        .desc(  "Execute a Latin Hypercube sample in project. Effective only if a project is specified" )
                                        .build();
        Option run_sobol   = Option.builder("sobol").argName( "sample size" )
                                        .hasArg()
                                        .desc(  "Execute a Sobol sample in project. Effective only if a project is specified" )
                                        .build();
        Option random_seed   = Option.builder("seed").argName( "random seed" )
                                        .hasArg()
                                        .desc(  "Use the given random seed for sampling. If seed is not specified, jEPlus uses the seed saved in the project. This option is effective only with -shuffle, -lhs and -sobol" )
                                        .build();
        Option run_index   = Option.builder("index").argName( "job indexes" )
                                        .hasArg()
                                        .desc(  "Execute selected jobs in project using specified parameter value indexes. Effective only if a project is specified" )
                                        .build();
        Option run_value   = Option.builder("value").argName( "job values" )
                                        .hasArg()
                                        .desc(  "Execute selected jobs in project using specified parameter values. Effective only if a project is specified" )
                                        .build();
        Option run_id   = Option.builder("id").argName( "job ids" )
                                        .hasArg()
                                        .desc(  "Execute selected jobs in project using specified job id strings. Effective only if a project is specified" )
                                        .build();
        Option run_file   = Option.builder("file").argName( "job list file" )
                                        .hasArg()
                                        .desc(  "Execute selected jobs in project using a job list file. Effective only if a project is specified" )
                                        .build();
        Option output_folder   = Option.builder("out").argName( "output folder" )
                                        .hasArg()
                                        .desc(  "Use the specified folder for outputs. If relative form is used, it is relative to the location of the project file" )
                                        .build();
        Option local   = Option.builder("parallel").argName( "number of threads" )
                                        .hasArg()
                                        .desc(  "Use specified number of local threads for parallel simulations. If a non-possitive number is supplied, all available processor threads will be used." )
                                        .build();

        Option timeout   = Option.builder("timeout").argName( "TRNSYS timeout seconds" )
                                        .hasArg()
                                        .desc(  "Timeout setting for TRNSYS only. In case of simulation error, the the job will be terminated after the set amount of time." )
                                        .build();

        Options options = (opts == null) ? new Options() : opts;

        options.addOption( help );
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
        options.addOption( timeout );
        
        return options;
    }

}
