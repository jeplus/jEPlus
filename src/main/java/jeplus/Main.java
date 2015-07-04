/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010,2011  Yi Zhang <yizhanguk@googlemail.com>          *
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
 ***************************************************************************
 *                                                                         *
 * Change log:                                                             *
 *                                                                         *
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus;

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
import org.apache.commons.cli.OptionBuilder;
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
     * @param args Program arguments
     */
    public void mainFunction (CommandLine commandline) {
        
        String cfgfile = "jeplus.cfg";
        if (commandline.hasOption("cfg")) {
            cfgfile = commandline.getOptionValue("cfg");
        }
        int nthread = Runtime.getRuntime().availableProcessors();
        if (commandline.hasOption("local")) {
            try {
                nthread = Integer.parseInt(commandline.getOptionValue("local"));
            }catch (Exception ex) {
            }
        }
        String prjfile = null;
        if (commandline.hasOption("job")) {
            prjfile = commandline.getOptionValue("job");
        }
        String output = null;
        if (commandline.hasOption("output")) {
            output = commandline.getOptionValue("output");
        }
        
        boolean showGUI = true;
        // Prepare simulation manager
        if (prjfile != null) {
            JEPlusProject project = new JEPlusProject (prjfile);
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
                commandline.hasOption("file")) {
                    showGUI = false;
                    // load E+ configuration
                    JEPlusConfig.setDefaultInstance(new JEPlusConfig (cfgfile));
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
                                batch.runRandomSample(njobs, RandomSource.getRandomGenerator(randomseed));
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
                                batch.runLHSample(njobs, RandomSource.getRandomGenerator(randomseed));
                        }else if (commandline.hasOption("index")) {
                                batch.prepareJobSet(EPlusBatch.JobStringType.INDEX, commandline.getOptionValue("index"));
                                batch.start();
                        }else if (commandline.hasOption("value")) {
                                batch.prepareJobSet(EPlusBatch.JobStringType.VALUE, commandline.getOptionValue("value"));
                                batch.start();
                        }else if (commandline.hasOption("id")) {
                                batch.prepareJobSet(EPlusBatch.JobStringType.ID, commandline.getOptionValue("id"));
                                batch.start();
                        }else if (commandline.hasOption("file")) {
                                batch.prepareJobSet(EPlusBatch.JobStringType.FILE, commandline.getOptionValue("file"));
                                batch.start();
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
        }
        
        if (showGUI == true) {
            JEPlusFrameMain.startGUI(new JEPlusFrameMain(), cfgfile, prjfile);
        }
    }
    
    /** 
     * Create command line options with Apache Commons Cli
     * @param opts Options object to which new args to be appended
     * @return Options object
     */
    protected Options getCommandLineOptions (Options opts) {
        Option help = new Option( "help", "Show this message" );
        
        Option cfg = OptionBuilder.withArgName( "config file" )
                                        .hasArg()
                                        .withDescription(  "Load jEPlus configuration file. Default=./jeplus.cfg" )
                                        .create( "cfg" );

        Option log = OptionBuilder.withArgName( "log config file" )
                                        .hasArg()
                                        .withDescription(  "Specify the configuration file for logs. Default=./log4j.cfg" )
                                        .create( "log" );

        Option job = OptionBuilder.withArgName( "project file" )
                                        .hasArg()
                                        .withDescription(  "Open project file" )
                                        .create( "job" );
        
        Option run_all = new Option( "all", "Execute all jobs in project" );
        
        Option run_sample   = OptionBuilder.withArgName( "sample size" )
                                        .hasArg()
                                        .withDescription(  "Execute a random sample in project. Project size limit applies. Effective with -job" )
                                        .create( "sample" );
        Option run_lhs   = OptionBuilder.withArgName( "sample size" )
                                        .hasArg()
                                        .withDescription(  "Execute a Latin Hypercube sample in project. Effective with -job" )
                                        .create( "lhs" );
        Option random_seed   = OptionBuilder.withArgName( "random seed" )
                                        .hasArg()
                                        .withDescription(  "Use the given random seed for sampling. If seed is not specified, jEPlus uses the seed saved in the project. This option is effective only with -sample and -lhs" )
                                        .create( "seed" );
        Option run_index   = OptionBuilder.withArgName( "job indexes" )
                                        .hasArg()
                                        .withDescription(  "Execute selected jobs in project using specified parameter value indexes. Effective with -job" )
                                        .create( "index" );
        Option run_value   = OptionBuilder.withArgName( "job values" )
                                        .hasArg()
                                        .withDescription(  "Execute selected jobs in project using specified parameter values. Effective with -job" )
                                        .create( "value" );
        Option run_id   = OptionBuilder.withArgName( "job ids" )
                                        .hasArg()
                                        .withDescription(  "Execute selected jobs in project using specified job id strings. Effective with -job" )
                                        .create( "id" );
        Option run_file   = OptionBuilder.withArgName( "job list file" )
                                        .hasArg()
                                        .withDescription(  "Execute selected jobs in project using a job list file. Effective with -job" )
                                        .create( "file" );
        Option output_folder   = OptionBuilder.withArgName( "output folder" )
                                        .hasArg()
                                        .withDescription(  "Use the specified folder for outputs." )
                                        .create( "output" );
        Option local   = OptionBuilder.withArgName( "number of threads" )
                                        .hasArg()
                                        .withDescription(  "Use specified number of local threads for parallel execution." )
                                        .create( "local" );
        Option post   = OptionBuilder.withArgName( "post-process script" )
                                        .hasArg()
                                        .withDescription(  "Python script file for post-processing after simulation." )
                                        .create( "post" );

        Options options = (opts == null) ? new Options() : opts;

        options.addOption( help );
        options.addOption( cfg );
        options.addOption(log);
        options.addOption( job );
        options.addOption( run_all );
        options.addOption( run_sample );
        options.addOption( run_lhs );
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
