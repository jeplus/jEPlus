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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import jeplus.data.DaySimModel;
import jeplus.util.ProcessWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: jEPlus - EnergyPlus execution utilities </p>
 * <p>Description: Utilities for updating Radiance dirs/files and calling Radiance executables</p>
 * <p>Copyright: Copyright (c) 2015, Yi Zhang</p>
 * @author Yi Zhang
 * @version 1.6
 * @since 1.6
 */
public class RadianceWinTools {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(RadianceWinTools.class);

    /**
     * Clean up working directory after simulation, based on the options to keep
     * working files and the list of files to delete.
     * @param workdir The working directory to be cleared
     * @param filesToDelete A [,;: ] separated list of file names to be deleted from the directory
     * @return Clean up successful or not. False is return if error occurs when deleting any file 
     */
    public static boolean cleanupWorkDir(String workdir, String filesToDelete) {
        boolean success = true;

        // Create the directory
        File dir = new File(workdir);
        if (dir.exists()) {
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
            logger.error(dir.toString() + " is present but not a directory.");
            success = false;
        }
        if (success) {
            File [] files = dir.listFiles();
            for (File file : files) {
                file.delete();
            }
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
     * Call Rtrace to run the simulation
     * @param config Radiance Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param args
     * @param model
     * @param in
     * @param out
     * @param err
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runRtrace (RadianceConfig config, String WorkDir, String args, String model, String in, String out, String err) {
        return runRtrace (config, WorkDir, args, model, in, out, err, null);
    }
    
    /**
     * Call Rtrace to run the simulation
     * @param config Radiance Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param args
     * @param model
     * @param in
     * @param out
     * @param err
     * @param process
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runRtrace (RadianceConfig config, String WorkDir, String args, String model, String in, String out, String err, ProcessWrapper process) {

        int ExitValue = -99;

        try {
            StringBuilder buf = new StringBuilder (config.getResolvedRadianceBinDir());
            buf.append(File.separator).append("rtrace");

            List<String> command = new ArrayList<> ();
            command.add(buf.toString());
            String [] arglist = args.split("\\s+");
            command.addAll(Arrays.asList(arglist));
            command.add(model);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File (WorkDir));
            builder.environment().put("RAYPATH", "." + File.pathSeparator + config.getResolvedRadianceLibDir());
            builder.redirectError(new File(WorkDir + File.separator + err));
            builder.redirectOutput(new File(WorkDir + File.separator + out));
            builder.redirectInput(new File(WorkDir + File.separator + in));

            Process proc = builder.start();
            if (process != null) {
                process.setWrappedProc(proc);
            }
            ExitValue = proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error occoured when executing Rtrace", ex);
        }

        // Return Radiance exit value
        return ExitValue;
    }
    
    /**
     * Call Rpict to run the simulation
     * @param config Radiance Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param args
     * @param model
     * @param in
     * @param out
     * @param err
     * @param png Switch for converting scene to jpg or not
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runRpict (RadianceConfig config, String WorkDir, String args, String model, String in, String out, String err, boolean png) {
        return runRpict (config, WorkDir, args, model, in, out, err, png, null);
    }
    
    /**
     * Call Rpict to run the simulation
     * @param config Radiance Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param args
     * @param model
     * @param in
     * @param out
     * @param err
     * @param png Switch for converting scene to jpg or not
     * @param process
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runRpict (RadianceConfig config, String WorkDir, String args, String model, String in, String out, String err, boolean png, ProcessWrapper process) {

        int ExitValue = -99;

        // Call rpict
        StringBuilder buf = new StringBuilder (config.getResolvedRadianceBinDir());
        buf.append(File.separator).append("rpict");

        List<String> command = new ArrayList<> ();
        command.add(buf.toString());
        String [] arglist = args.split("\\s+");
        command.addAll(Arrays.asList(arglist));
        command.add(model);
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File (WorkDir));
            builder.environment().put("RAYPATH", "." + File.pathSeparator + config.getResolvedRadianceLibDir());
            builder.redirectError(new File(WorkDir + File.separator + err));
            builder.redirectOutput(new File(WorkDir + File.separator + out));
            if (in != null) {
                builder.redirectInput(new File(WorkDir + File.separator + in));
            }
            Process proc = builder.start();
            if (process != null) {
                process.setWrappedProc(proc);
            }
            ExitValue = proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error occoured when executing Rpict", ex);
        }
        
        if (png) {
            // Sweep everything with the same extension as out. This is for handling
            // -o option in rpict
            String ext = FilenameUtils.getExtension(out);
            File [] files = new File(WorkDir).listFiles((FileFilter)new WildcardFileFilter("*." + ext));
            for (File file : files) {
                String outname = file.getName();
            
                // Filter scene
                try {
                    buf = new StringBuilder (config.getResolvedRadianceBinDir());
                    buf.append(File.separator).append("pfilt");

                    command = new ArrayList<> ();
                    command.add(buf.toString());
                    // String [] arglist = "-1 -e -3".split("\\s+");
                    // command.addAll(Arrays.asList(arglist));
                    command.add(outname);
                    ProcessBuilder builder = new ProcessBuilder(command);
                    builder.directory(new File (WorkDir));
                    builder.environment().put("RAYPATH", "." + File.pathSeparator + config.getResolvedRadianceLibDir());
                    builder.redirectError(new File(WorkDir + File.separator + err));
                    builder.redirectOutput(new File(WorkDir + File.separator + outname + ".flt"));
                    Process proc = builder.start();
                    ExitValue = proc.waitFor();
                } catch (IOException | InterruptedException ex) {
                    logger.error("Error occoured when executing pfilt", ex);
                }

                // Convert to bmp
                try {
                    buf = new StringBuilder (config.getResolvedRadianceBinDir());
                    buf.append(File.separator).append("ra_bmp");

                    command = new ArrayList<> ();
                    command.add(buf.toString());
                    //String [] arglist = "-g 1.0".split("\\s+");
                    //command.addAll(Arrays.asList(arglist));
                    command.add(outname + ".flt");
                    command.add(outname + ".bmp");
                    ProcessBuilder builder = new ProcessBuilder(command);
                    builder.directory(new File (WorkDir));
                    builder.environment().put("RAYPATH", "." + File.pathSeparator + config.getResolvedRadianceLibDir());
                    builder.redirectError(ProcessBuilder.Redirect.appendTo(new File(WorkDir + File.separator + err)));
                    Process proc = builder.start();
                    ExitValue = proc.waitFor();
                } catch (IOException | InterruptedException ex) {
                    logger.error("Error occoured when executing ra_bmp", ex);
                }

                // Convert to png
                BufferedImage input_image = null; 
                try {
                    input_image = ImageIO.read(new File(WorkDir + File.separator + outname + ".bmp")); //read bmp into input_image object
                    File outputfile = new File(WorkDir + File.separator + outname + ".png"); //create new outputfile object
                    ImageIO.write(input_image, "png", outputfile); //write PNG output to file 
                }catch (Exception ex) {
                    logger.error ("Error converting bmp to png.", ex);
                }

                // Remove flt and bmp
                new File(WorkDir + File.separator + outname + ".flt").delete();
                new File(WorkDir + File.separator + outname + ".bmp").delete();
            }
        }
        // Return Radiance exit value
        return ExitValue;
    }
    
    /**
     * Call DaySim gen_dc to run the simulation
     * @param config Radiance Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param model
     * @param in
     * @param out
     * @param err
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runGen_DC (RadianceConfig config, String WorkDir, String model, String in, String out, String err) {
        return runGen_DC(config, WorkDir, model, in, out, err, null);
    }
    
    /**
     * Call DaySim gen_dc to run the simulation
     * @param config Radiance Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param model
     * @param in
     * @param out
     * @param err
     * @param process
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runGen_DC (RadianceConfig config, String WorkDir, String model, String in, String out, String err, ProcessWrapper process) {

        int ExitValue = -99;
        
        // Manipulate header file
        HashMap <String, String> props = new HashMap<> ();
        // props.put("project_name", "");
        props.put("project_directory", "./");
        props.put("bin_directory", config.getResolvedDaySimBinDir());
        props.put("tmp_directory", "./");
        props.put("Template_File", config.getResolvedDaySimBinDir() + "../template/");
        props.put("sensor_file", in);
        try {
            FileUtils.moveFile(new File (WorkDir + File.separator + model), new File (WorkDir + File.separator + model + ".ori"));
        } catch (IOException ex) {
            logger.error("Error renaming header file to " + WorkDir + File.separator + model + ".ori", ex);
        }
        DaySimModel.updateHeaderFile(WorkDir + File.separator + model + ".ori", WorkDir + File.separator + model, props);
        
        // Run command
        try {
            StringBuilder buf = new StringBuilder (config.getResolvedDaySimBinDir());
            buf.append(File.separator).append("gen_dc");

            List<String> command = new ArrayList<> ();
            command.add(buf.toString());
            command.add(model);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File (WorkDir));
            builder.environment().put("RAYPATH", "." + File.pathSeparator + config.getResolvedDaySimLibDir());
            builder.redirectOutput(new File(WorkDir + File.separator + out));
            if (err == null || out.equals(err)) {
                builder.redirectErrorStream(true);
            }else {
                builder.redirectError(new File(WorkDir + File.separator + err));
            }
            if (in != null) {
                builder.redirectInput(new File(WorkDir + File.separator + in));
            }
            Process proc = builder.start();
            if (process != null) {
                process.setWrappedProc(proc);
            }
            ExitValue = proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error occoured when executing DaySim gen_dc", ex);
        }

        // Return Radiance exit value
        return ExitValue;
    }
    
    /**
     * Call a sequence of DaySim programs to run the simulation
     * @param config Radiance Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param model
     * @param in
     * @param out
     * @param err
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runDaySim (RadianceConfig config, String WorkDir, String model, String in, String out, String err) {
        return runDaySim(config, WorkDir, model, in, out, err, null);
    }
    
    /**
     * Call a sequence of DaySim programs to run the simulation
     * @param config Radiance Configuration
     * @param WorkDir The working directory where the input files are stored and the output files to be generated
     * @param model
     * @param in
     * @param out
     * @param err
     * @param process
     * @return the result code represents the state of execution steps. >=0 means successful
     */
    public static int runDaySim (RadianceConfig config, String WorkDir, String model, String in, String out, String err, ProcessWrapper process) {

        int ExitValue = -99;
        
        // Manipulate header file
        HashMap <String, String> props = new HashMap<> ();
        // props.put("project_name", "");
        props.put("project_directory", "./");
        props.put("bin_directory", config.getResolvedDaySimBinDir());
        props.put("tmp_directory", "./");
        props.put("Template_File", config.getResolvedDaySimBinDir() + "../template/DefaultTemplate.htm");
        props.put("sensor_file", in);
        try {
            FileUtils.moveFile(new File (WorkDir + File.separator + model), new File (WorkDir + File.separator + model + ".ori"));
        } catch (IOException ex) {
            logger.error("Error renaming header file to " + WorkDir + File.separator + model + ".ori", ex);
        }
        DaySimModel.updateHeaderFile(WorkDir + File.separator + model + ".ori", WorkDir + File.separator + model, props);
        
        // Run gen_dc command
        try {
            StringBuilder buf = new StringBuilder (config.getResolvedDaySimBinDir());
            buf.append(File.separator).append("gen_dc");

            List<String> command = new ArrayList<> ();
            command.add(buf.toString());
            command.add(model);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File (WorkDir));
            builder.environment().put("RAYPATH", "." + File.pathSeparator + config.getResolvedDaySimLibDir());
            builder.redirectError(new File(WorkDir + File.separator + err));
            builder.redirectOutput(new File(WorkDir + File.separator + out));
            if (in != null) {
                builder.redirectInput(new File(WorkDir + File.separator + in));
            }
            Process proc = builder.start();
            if (process != null) {
                process.setWrappedProc(proc);
            }
            ExitValue = proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error occoured when executing gen_dc", ex);
        }

        // Run ds_illum command
        try {
            StringBuilder buf = new StringBuilder (config.getResolvedDaySimBinDir());
            buf.append(File.separator).append("ds_illum");

            List<String> command = new ArrayList<> ();
            command.add(buf.toString());
            command.add(model);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File (WorkDir));
            builder.environment().put("RAYPATH", "." + File.pathSeparator + config.getResolvedDaySimLibDir());
            builder.redirectError(ProcessBuilder.Redirect.appendTo(new File(WorkDir + File.separator + err)));
            builder.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(WorkDir + File.separator + out)));
            if (in != null) {
                builder.redirectInput(new File(WorkDir + File.separator + in));
            }
            Process proc = builder.start();
            if (process != null) {
                process.setWrappedProc(proc);
            }
            ExitValue = proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error occoured when executing ds_illum", ex);
        }

        // Run ds_el_lighting command
        try {
            StringBuilder buf = new StringBuilder (config.getResolvedDaySimBinDir());
            buf.append(File.separator).append("ds_el_lighting");

            List<String> command = new ArrayList<> ();
            command.add(buf.toString());
            command.add(model);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File (WorkDir));
            builder.environment().put("RAYPATH", "." + File.pathSeparator + config.getResolvedDaySimLibDir());
            builder.redirectError( ProcessBuilder.Redirect.appendTo(new File(WorkDir + File.separator + err)));
            builder.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(WorkDir + File.separator + out)));
            if (in != null) {
                builder.redirectInput(new File(WorkDir + File.separator + in));
            }
            Process proc = builder.start();
            ExitValue = proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error occoured when executing ds_el_lighting", ex);
        }

        // Return Radiance exit value
        return ExitValue;
    }
    
    public static void main (String [] args) {
        
        PropertyConfigurator.configure("D:\\4\\jEPlus_v1.6.0\\log4j.cfg");
        
        JEPlusConfig Config = new JEPlusConfig ();
        RadianceConfig radcfg = new RadianceConfig ();
        radcfg.setRadianceBinDir("C:\\Program Files (x86)\\Radiance\\bin");
        radcfg.setRadianceLibDir("C:\\Program Files (x86)\\Radiance\\lib");
        Config.getRadianceConfigs().put("v4.2", radcfg);
        
//        runRtrace(
//                Config, 
//                "C:\\jess_test\\temp\\zyyz\\11718", 
//                "-ab 4 -ad 1024 -aa .22 -ar 512 -as 512 -h+ -I+ -oov -fa",
//                "dbmodel.oct",
//                "trace.in",
//                "trace.out",
//                "trace.err"
//                );
        runRpict(
                Config.getRadianceConfigs().get("v4.2"), 
                "C:\\jess_test\\temp\\zyyz\\11720", 
                /* "−vp 15.52623 22.5462 18.84981 −vd -1 −.5 -1 -ab 4 -ad 1024 -aa .22 -ar 512 -as 512", */
                "-vtv -vp -.5 -5 1.15 -vd 0.5 5 0 -vh 45 -vv 45 -pa 1.0 -pj 0.02 -pd 0.0 -pm 0.0 -ps 1 -w+ -i- -bv+ -dt 0.050 -dc 0.50 -dj 0.0 -ds 0.250 -dr 1 -dp 512 -dv+ -st 0.150 -ab 4 -ar 128 -ad 1500 -as 500 -aa 0.15 -av 0.0 0.0 0.0 -aw 0 -lw 0.004 -ss 1.0 -lr -10 -u- -x 1024 -y 1024 -t 60",
                "dbmodel.oct",
                null,
                "rpict.hdr",
                "rpict.err",
                true
                );
//        Config.setDaySimBinDir("C:\\jess_test\\EnergyPlus\\DaySim4\\bin");
//        runGen_DC(
//                Config, 
//                "D:\\4\\JESS_Client_v2.2.0\\example_DS1", 
//                "Daysim.txt",
//                "trace.in",
//                "console.out",
//                "console.err"
//                );
//        runDaySim(
//                Config, 
//                "D:\\4\\JESS_Client_v2.2.0\\example_DS1", 
//                "Daysim.txt",
//                "trace.in",
//                "console.out",
//                "console.err"
//                );
    }
}
