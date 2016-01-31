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
import jeplus.data.ExecutionOptions;
import jeplus.data.RandomSource;
import jeplus.gui.*;
import jeplus.postproc.*;
import jeplus.util.CsvUtil;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class EPlusPostProcessor implements Runnable {

    /**
     * Logger
     */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusPostProcessor.class);
    protected JEPlusPrintablePanel OutputPanel = null;
    protected EPlusBatch Batch = null;
    String ResultDir = null;
    protected boolean ProjectJobsOnly = true;
    protected String CmdRvi = null;
    protected String IntermediateFile = null;
    protected int ProcFunc;
    protected String ExportDir = null;
    protected boolean Individual = false;
    protected String IndividualFilePrefix = null;
    protected boolean CombineResult = true;
    protected String CombinedFileName = null;
    protected boolean Stats = true;
    protected String StatsFilePrefix = null;
    protected boolean CollectTimes = false;

    public EPlusPostProcessor() {
    }

    ;

    public EPlusPostProcessor(JEPlusPrintablePanel outputpanel,
            EPlusBatch batch,
            String resdir,
            boolean projonly,
            String cmdRvi,
            String intermfile,
            int func,
            String expdir,
            boolean ind, String indfile,
            boolean combine, String cmbfile,
            boolean stats, String statsfile,
            boolean colltimes) {
        this.OutputPanel = outputpanel;
        this.Batch = batch;
        ResultDir = resdir;
        this.ProjectJobsOnly = projonly;
        this.CmdRvi = cmdRvi;
        this.IntermediateFile = intermfile;
        this.ProcFunc = func;
        this.ExportDir = new File(expdir).getPath() + (new File(expdir).getPath().endsWith(File.separator) ? "" : File.separator);
        this.Individual = ind;
        this.IndividualFilePrefix = indfile;
        this.CombineResult = combine;
        this.CombinedFileName = cmbfile;
        this.Stats = stats;
        this.StatsFilePrefix = statsfile;
        this.CollectTimes = colltimes;
    }

    /**
     * Main post-processJobResult function
     */
    public void postProcess() {

        if (Batch != null) {

            StringBuilder buf = new StringBuilder();
            CsvUtil.reset();
            List<EPlusTask> Jobs;

            if (ProjectJobsOnly) {
                if (Batch.getProject().ExecSettings.getSubSet() == ExecutionOptions.ALL) {
                    // Build all jobs
                    Batch.buildJobs();
                } else {
                    switch (Batch.getProject().ExecSettings.getSubSet()) {
                        case ExecutionOptions.CHAINS:
                            Batch.buildJobs(Batch.getTestJobList());
                            break;
                        case ExecutionOptions.RANDOM:
                            RandomSource.setSeed(Batch.getProject().ExecSettings.getRandomSeed());
                            if (Batch.getProject().ExecSettings.isUseLHS()) {
                                Batch.buildJobs(Batch.getProject().getLHSJobList(Batch.getProject().ExecSettings.getNumberOfJobs(),
                                        RandomSource.getRandomGenerator()));
                            } else {
                                Batch.buildJobs(Batch.getRandomJobList(Batch.getProject().ExecSettings.getNumberOfJobs(),
                                        RandomSource.getRandomGenerator()));
                            }
                            break;
                        case ExecutionOptions.FILE:
                            Batch.prepareJobSet(EPlusBatch.JobStringType.FILE, Batch.getProject().ExecSettings.getJobListFile());
                    }
                }
                Jobs = Batch.getJobQueue();
            
            } else {
                // Sweep the directory and create jobs using the job folder names
                File file = new File("/path/to/directory");
                File[] directories = file.listFiles(new FilenameFilter() {
                  @Override
                  public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                  }
                });
                Jobs = new ArrayList<> ();
                for (File dir : directories) {
                    EPlusTask job = new EPlusTask (Batch.getResolvedEnv(), dir.getName(), new ArrayList<String>(), new ArrayList<String>());
                    job.getWorkEnv().setParentDir(dir.getAbsolutePath());
                    Jobs.add(job);
                }
                Jobs = Batch.getJobQueue();
            }
            
            writeLog("Start collecting results from " + Jobs.size() + " jobs.");

            String BaseExportDir = RelativeDirUtil.checkAbsolutePath(ExportDir, this.Batch.getProject().getBaseDir()) + "/";
            File dir = new File(BaseExportDir);
            if (!dir.exists()) {
                dir.mkdir();
            } else if (!dir.isDirectory()) {
                writeLog(dir.getAbsolutePath() + " is present but not a directory.");
                writeLog("No result has been collected!");
                return;
            }
            writeLog("Result tables will be written to " + dir.getAbsolutePath());

            // Go to each job directory
            for (int i = 0; i < Jobs.size(); i++) {

                EPlusTask CurJob = Jobs.get(i);
//                if ((Batch.Project.getExecSettings().isUseLHS()) && (Batch.Project.getExecSettings().getSubSet() == ExecutionOptions.RANDOM)) {
//                    CurJob.setJobID(new Formatter().format("LHS-%06d", i).toString());                    
//                }
                String CurDir = RelativeDirUtil.checkAbsolutePath(ResultDir, this.Batch.getProject().getBaseDir()) + "/" + CurJob.getJobID() + "/";

                // New csv file should be named as "postporcess.csv" - check the rvi file
                String CurrentCSV = CurDir + IntermediateFile;

                if (callReadVars(CmdRvi, CurDir)) {

                    // Call customized post processJobResult step
                    DefaultPostProcFunc func = new DefaultPostProcFunc();
                    String PostResult = func.processJobResult(CurJob.getJobID(), CurrentCSV, ProcFunc);

                    // Call LinkingTRNSYS function   
                    if (ProcFunc == 1) {
                        String[][] table = CsvUtil.parseCSV(PostResult);
                        PostResult = CsvUtil.writeTRNSYSCSV(table);
                        String dirCSV = BaseExportDir + CurJob.getJobID() + ".csv";
                        // Save to file
                        try (FileWriter fw = new FileWriter(dirCSV)) {
                            fw.write(PostResult);
                        } catch (IOException ex) {
                            logger.error("", ex);
                        }
                        LinkingTRNSYSFunc link = new LinkingTRNSYSFunc();
                        link.processJobResult(this.Batch.getProject().getBaseDir(), BaseExportDir, CurJob.getJobID());
                    }

                    // if (PostResult.length() != 0) 
                    // Move or merge file to export directory
                    // Save individual files
                    if (this.Individual) {
                        // Save to file
                        try (FileWriter fw = new FileWriter(BaseExportDir + IndividualFilePrefix + "-" + CurJob.getJobID() + ".csv")) {
                            fw.write(PostResult);
                        } catch (IOException ex) {
                            logger.error("", ex);
                        }
                    }
                    // Save combined results
                    if (this.CombineResult) {
                        buf.append(PostResult);
                    }

                    // Calculate stats
                    if (this.Stats) {
                        CsvUtil.addData(CsvUtil.parseCSV(PostResult));
                    }
                }

                // report progress
                if (i > 0 && i % 200 == 0) {
                    writeLog("Scanned " + i + " folders so far.");
                }
            }

            if (this.CombineResult) {
                writeLog("Writing results to " + BaseExportDir + CombinedFileName + ".csv");
                try (FileWriter fw = new FileWriter(BaseExportDir + CombinedFileName + ".csv")) {
                    fw.write(buf.toString());
                } catch (IOException ex) {
                    logger.error("", ex);
                }
            }
            if (this.Stats) {
                writeLog("Writing stats to " + BaseExportDir + this.StatsFilePrefix + "-mean.csv");
                try (FileWriter fw = new FileWriter(BaseExportDir + StatsFilePrefix + "-mean.csv")) {
                    fw.write(CsvUtil.writeCSV(CsvUtil.getStatMean()));
                } catch (IOException ex) {
                    logger.error("", ex);
                }
                writeLog("Writing stats to " + BaseExportDir + this.StatsFilePrefix + "-variance.csv");
                try (FileWriter fw = new FileWriter(BaseExportDir + StatsFilePrefix + "-variance.csv")) {
                    fw.write(CsvUtil.writeCSV(CsvUtil.getStatVariance()));
                } catch (IOException ex) {
                    logger.error("", ex);
                }
                writeLog("Writing stats to " + BaseExportDir + this.StatsFilePrefix + "-min.csv");
                try (FileWriter fw = new FileWriter(BaseExportDir + StatsFilePrefix + "-min.csv")) {
                    fw.write(CsvUtil.writeCSV(CsvUtil.getStatMin()));
                } catch (IOException ex) {
                    logger.error("", ex);
                }
                writeLog("Writing stats to " + BaseExportDir + this.StatsFilePrefix + "-max.csv");
                try (FileWriter fw = new FileWriter(BaseExportDir + StatsFilePrefix + "-max.csv")) {
                    fw.write(CsvUtil.writeCSV(CsvUtil.getStatMax()));
                } catch (IOException ex) {
                    logger.error("", ex);
                }
            }

            // Collecting run times
            if (CollectTimes) {
                writeLog("Writing simulation reports to " + BaseExportDir + "RunTimes.csv");
                collectReportsCSV(Jobs, BaseExportDir + "RunTimes.csv",
                        RelativeDirUtil.checkAbsolutePath(ResultDir, this.Batch.getProject().getBaseDir()) + "/", false);
            }

            writeLog("Done!");
        }
    }

    protected boolean callReadVars(String CmdRvi, String CurDir) {
        boolean ok = true;
        // Run ReadVarsESO with new rvi file
        try {
            Process EPProc = Runtime.getRuntime().exec(CmdRvi, null, new File(CurDir));
            try (BufferedReader ins = new BufferedReader(new InputStreamReader(EPProc.getInputStream()));
                    BufferedWriter outs = new BufferedWriter(new FileWriter("postproc.log", true));) {

                outs.newLine();
                outs.write("Calling ReadVarsEso - " + (new SimpleDateFormat()).format(new Date()));
                outs.newLine();
                outs.write("Command line: " + CurDir + ">" + CmdRvi);
                outs.newLine();
                int res = ins.read();
                while (res != -1) {
                    outs.write(res);
                    res = ins.read();
                }
                outs.newLine();
                outs.flush();
            }
            EPProc.waitFor();
        } catch (IOException | InterruptedException ex) {
            ok = false;
        }
        return ok;
    }

    /**
     * Collect the Run end information from "eplusout.end" file in each directory
     */
    public int collectReportsCSV(List<EPlusTask> jobs, String filename, String dir, boolean remove) {
        // Example eplusout.end:
        // "EnergyPlus Completed Successfully-- 21329 Warning; 0 Severe Errors; Elapsed Time=00hr 43min 53.75sec"
        // ""
        int nResCollected = 0;
        File fn = new File(filename);
        try (PrintWriter fw = new PrintWriter(new FileWriter(fn))) {
            String[] header = {"Id",
                "JobID",
                "Message",
                "Warnings",
                "Errors",
                "Hours",
                "Minutes",
                "Seconds"};

            StringBuffer buf = new StringBuffer(header[0]);
            for (int i = 1; i < header.length; i++) {
                buf.append(", ").append(header[i]);
            }
            fw.println(buf.toString());

            // Jobs
            for (int i = 0; i < jobs.size(); i++) {
                // For each job, do:
                EPlusTask job = jobs.get(i);
                String[] vals = new String[header.length];
                vals[0] = Integer.toString(i);
                vals[1] = job.getJobID();
                // Get result information
                String info = EPlusTask.getResultInfo(dir, job, remove);
                if (info != null && !info.startsWith("!")) {
                    int marker = info.indexOf("--");
                    vals[2] = info.substring(0, marker);
                    info = info.substring(marker + 2).trim();
                    String[] segment = info.split(";");
                    for (int j = 0; j < segment.length; j++) {
                        String thisseg = segment[j].trim();
                        if (thisseg.endsWith("Warning")) {
                            vals[3] = thisseg.substring(0, thisseg.indexOf(" "));
                        } else if (thisseg.endsWith("Severe Errors")) {
                            vals[4] = thisseg.substring(0, thisseg.indexOf(" "));
                        } else if (thisseg.startsWith("Elapsed Time")) {
                            vals[5] = thisseg.substring(thisseg.indexOf("=") + 1, thisseg.indexOf("hr "));
                            vals[6] = thisseg.substring(thisseg.indexOf("hr ") + 3, thisseg.indexOf("min"));
                            vals[7] = thisseg.substring(thisseg.indexOf("min ") + 4, thisseg.indexOf("sec"));
                        }
                    }
                    buf = new StringBuffer();
                    buf.append(vals[0]);
                    for (int j = 1; j < vals.length; j++) {
                        buf.append(", ").append(vals[j]);
                    }
                    fw.println(buf.toString());
                    nResCollected++;
                }
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return nResCollected;
    }

    /**
     * A specific function for collecting time stamps (in time_stamps.txt for each job) generated by an agent. For each job, time spent 
     * at each step are converted from milliseconds to seconds, and written to the specified export file. The sum of time for each step of 
     * all jobs in the batch is then calculated and put in the returning array.
     * @param Batch Reference to the current batch
     * @param ResDir The location of the result files
     * @param exportfile Name of the export file for the list of execution times
     * @return Average execution time for each operation step in an array
     */
    public static double[] collectTimeStamps(EPlusBatch Batch, String ResDir, String exportfile) {
        double[] times = null;
        if (Batch != null) {
            times = new double[20];
            Arrays.fill(times, 0);
            /* Example: Wed Jul 21 23:08:47 UTC 2010 */
            SimpleDateFormat DateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
            StringBuilder buf = new StringBuilder("Job_ID,HostName,StartTime,Step 1(s),Step 2(s),Step 3(s),Step 4(s),Step 5(s),Step 6(s),Step 7(s),Step 8(s),...\n");
            List<EPlusTask> Jobs = Batch.getJobQueue();
            // Go to each job directory
            for (int i = 0; i < Jobs.size(); i++) {

                EPlusTask CurJob = Jobs.get(i);
                String CurDir = ResDir + CurJob.getJobID() + "/";

                buf.append(CurJob.getJobID());
                buf.append(",").append(getHostNameFromStampFile(CurDir + "time_stamps.txt"));
                ArrayList<Date> Stamps = getTimeFromStampFile(CurDir + "time_stamps.txt", DateFormat);
                if (Stamps != null) {
                    if (Stamps.size() > 0) {
                        buf.append(",").append(DateFormat.format(Stamps.get(0)));
                    }
                    for (int j = 1; j < Stamps.size() && j <= times.length; j++) {
                        double t = (Stamps.get(j).getTime() - Stamps.get(j - 1).getTime()) / 1000;
                        times[j - 1] += t;
                        buf.append(",").append(t);
                    }
                }
                buf.append("\n");
                // Cleaniing up when necessary
                // ...
            }
            try (FileWriter fw = new FileWriter(exportfile)) {
                fw.write(buf.toString());
            } catch (IOException ex) {
                logger.error("", ex);
            }
//            for (int i = 0; i < times.length; i++) {
//                times[i] = times[i] / Jobs.size();
//            }
        }
        return times;
    }

    private static String getHostNameFromStampFile(String filename) {
        String line;
        try (BufferedReader fr = new BufferedReader(new FileReader(filename))) {
            line = fr.readLine();
            while (line != null && !line.trim().startsWith("@")) {
                line = fr.readLine();
            }
            return line;
        } catch (FileNotFoundException ex1) {
        } catch (IOException ex2) {
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    private static ArrayList<Date> getTimeFromStampFile(String filename, SimpleDateFormat formatter) {
        ArrayList<Date> dates = new ArrayList<>();
        try (BufferedReader fr = new BufferedReader(new FileReader(filename))) {
            String line = fr.readLine();
            while (line != null) {
                // Filter comments ("#")
                if (line.contains("#")) {
                    line = line.substring(0, line.indexOf("#")).trim();
                }
                // Stamp file contains only one line of time stamp, such as "Wed Jul 21 23:08:47 UTC 2010" written by Linux date command
                if (!line.startsWith("@")) {
                    dates.add(formatter.parse(line));
                }
                line = fr.readLine();
            }
            return dates;
        } catch (FileNotFoundException ex1) {
        } catch (IOException ex2) {
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    /**
     * Write a line of log, including date and source, to output panel
     *
     * @param entry The entry text of the event
     */
    public void writeLog(String entry) {
        String date = new SimpleDateFormat("d MMM yyyy HH:mm:ss z").format(new Date());
        if (OutputPanel != null) {
            OutputPanel.appendContent(date + " [Post-processor] " + entry + "\n");
        }
    }

    /**
     * Run in a thread
     */
    @Override
    public void run() {
        if (ProcFunc == 1) {
            TRNSYSWinTools.prepareWorkDir(this.Batch.getProject().getBaseDir() + this.ExportDir);
            try (FileWriter out = new FileWriter(this.Batch.getProject().getBaseDir() + LinkingTRNSYSFunc.LinkFileName)) {
                out.write("! ********************************************* !");
                out.write("\n! These are the absolute directories of the     !");
                out.write("\n! E+ outputs obtained from EnergyPlus project.  !");
                out.write("\n! The purpose of this file is to be read in     !");
                out.write("\n! jEPlus like a branch which represents for     !");
                out.write("\n! different input files of a TRNSYS project.    !");
                out.write("\n! ********************************************* !");
                out.flush();
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
        this.postProcess();
    }
}
