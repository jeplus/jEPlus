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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Line ends filter utility
 * @author Yi
 */
public class LineEnds {
    
    /** Logger */
    final static Logger logger = LoggerFactory.getLogger(LineEnds.class);

    protected static Options getCommandLineOptions () {
        Option help = new Option( "help", "print this message" );
        
        Option file = OptionBuilder.withArgName( "file to convert" )
                                        .hasArg()
                                        .withDescription(  "Name of the file to be converted. If a folder name is given, all files in the folder will be converted" )
                                        .isRequired()
                                        .create( "file" );
        Option style = OptionBuilder.withArgName( "Line-end style" )
                                        .hasArg()
                                        .withDescription(  "Style of line ends. Choose between W or L" )
                                        .create( "style" );
        Option log = OptionBuilder.withArgName( "Log configuration" )
                                        .hasArg()
                                        .withDescription(  "Logger configuration file. Default=./log4j.cfg" )
                                        .create( "log" );

        Options options = new Options();

        options.addOption( help );
        options.addOption( file );
        options.addOption( style );
        options.addOption( log );
        
        return options;
    }
    
    public static void main (String [] args) {
        
        String LN = "\r\n";
        
        // create the parser
        CommandLineParser parser = new GnuParser();
        Options options = getCommandLineOptions();
        CommandLine commandline = null;
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(80);
        try {
            // parse the command line arguments
            commandline = parser.parse( options, args );
            if (commandline.hasOption("help")) {
                // automatically generate the help statement
                formatter.printHelp( "java -cp jEPlusNet.jar jeplusplus.util.LineEnds [OPTIONS]", options );    
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
            formatter.printHelp( "java -Xmx500m -jar JESS_Client.jar [OPTIONS]", options );            
            System.exit(-1);
        }        
                
        if (commandline.hasOption("style")) {
            if (commandline.getOptionValue("style").startsWith("L")) {
                LN = "\n";
            }
        }
        
        if (commandline.hasOption("file")) {
            File file = new File (commandline.getOptionValue("file"));
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] listOfFiles = file.listFiles(); 
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile()) {
                            convertFile (listOfFiles[i], LN);
                        }
                    }
                }else {
                    convertFile (file, LN);
                }
            }
        }
    }
    
    /**
     * Replace line ends in the (text) file with the given string. This function creates a temporary file then delete and rename.
     * @param file
     * @param newLN 
     */
    protected static void convertFile (File file, String newLN) {
        try {
            BufferedReader fr = new BufferedReader (new FileReader (file));
            File tempfile = new File (file.getAbsolutePath() + ".temp");
            PrintWriter fw = new PrintWriter (new FileWriter (tempfile));
            String line = fr.readLine();
            while (line != null) {
                fw.print(line);
                fw.print(newLN);
                line = fr.readLine();
            }
            fr.close();
            fw.close();
            file.delete();
            if (! tempfile.renameTo(file)) {
                throw new Exception ("Cannot rename " + tempfile.getName() + " to " + file.getName());
            }
        }catch (Exception ex) {
            logger.error("Error converting file " + file.getAbsolutePath(), ex);
        }
    }
    
}
