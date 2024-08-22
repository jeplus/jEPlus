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
package jeplus.agent;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import jeplus.EPlusTask;
import jeplus.EPlusWorkEnv;
import jeplus.data.ExecutionOptions;
import jeplus.data.RVX;
import jeplus.gui.EPlusTextPanelOld;
import jeplus.gui.JEPlusPrintablePanel;
import jeplus.gui.JFrameAgentMonitor;
import jeplus.postproc.ResultCollector;
import org.slf4j.LoggerFactory;

/**
 * The agent type is designed to allow simulation managers to be implemented as
 * plug-ins. The framework will eventually provide interactions such as:
 *  - Adding jobs (on the fly)
 *  - Monitoring jobs
 *  - Recording historical performance in order to balance load when multiple
 *    agents are available
 *  - Starting, pausing and stopping agent, including recycling jobs when necessary
 *  - Configuring agent to manage its own log or use a central log
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.5b
 */
public abstract class EPlusAgent implements Runnable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusAgent.class);

    /** Constants */
    public static final int AUTO = 0;
    public static final int WINDOWS = 1;
    public static final int LINUX = 2;
    public static final int LINUX_PBS = 3;

    /** Thread pool for certain post-processes */
    public static ExecutorService PostExecService = null;

    
    // Agent information and settings

    /** AgentID */
    protected String AgentID = "AgentUnknown";
    /** Agent Settings */
    protected ExecutionOptions Settings = null;
    /** Command text to be shown on the 'start' button on the GUI */
    protected String StartButtonText = "Start simulation";
    /** Command text to be shown on the 'stop' button on the GUI */
    protected String StopButtonText = "Stop simulation";

    /** Platform type */
    protected int PlatformType = AUTO;

    /** Job queue capacity */
    protected int QueueCapacity = Integer.MAX_VALUE;

    /** Job queue */
    protected List<EPlusTask> JobQueue = new ArrayList<>();
    /** Jobs submitted */
    protected List<EPlusTask> RunningJobs = new ArrayList<>();
    /** Jobs rejected (e.g. due to error) */
    protected List<EPlusTask> RejectedJobs = new ArrayList<>();
    /** Jobs completed */
    protected List<EPlusTask> FinishedJobs = new ArrayList<>();

    /** List of currently employed processors */
    protected static List<Thread> Processors = Collections.synchronizedList(new ArrayList()); // This didn't work 
//    protected List<Thread> Processors = null;

    // States and signals

    /** Flag for that the agent is ready to serve */
    protected boolean Ready = false;

    /** Switch to force stop the agent if it is running in a thread */
    protected boolean StopAgent = false;

    public enum AgentState {READY, RUNNING, PAUSED, CANCELLED, FINISHED, ERROR};

    protected AgentState State = AgentState.READY;


    /** The batch controller who owns the jobs */
    protected EPlusBatch JobOwner = null;
    
    /** Result collectors */
    protected ArrayList <ResultCollector> ResultCollectors = new ArrayList <> ();
    
    /** Flag for whether simulation manager should validate jobs before send for this agent */
    protected boolean ValidationRequired = true;

    /** Server log monitor/external logger */
    private PrintWriter LogWriter = null;
    /** Attached output GUI */
    protected JEPlusPrintablePanel GUIPanel = null;
    /** Simulation start time */
    protected Date StartTime = null;
    // Local fields
    protected SimpleDateFormat DateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    /** Agent's monitor GUI */
    JFrameAgentMonitor MonitorGUI = null;
    
    JPanel SettingsPanel = null;
    JPanel OptionsPanel = null;

    // Constructor(s)

    /**
     * Construct with an id string and Exec settings
     * @param id the String id
     * @param settings Reference to an existing Exec settings instance
     */
    public EPlusAgent (String id, ExecutionOptions settings) {
        AgentID = id;
        this.Settings = (settings == null) ? new ExecutionOptions () : settings;
    }

    /**
     * Construct Agent with specified max capacity
     * @param id
     * @param capacity Max capacity of this agent's job queue. -1 means no limit
     * @param nproc Number of parallel processes
     */
    public EPlusAgent (String id, int capacity, int nproc) {
        AgentID = id;
        QueueCapacity = capacity;
        Settings = new ExecutionOptions ();
        Settings.setNumThreads(Math.max(1, nproc));
    }

    /**
     * This function assigns all the available result collectors to the performing
     * agent. The order of the RCs is significant.
     */
    protected void attachDefaultCollector () {
        // clear existing collectors
        ResultCollectors.clear();
        
        // attach default result collector
        ResultCollectors.addAll(ResultCollector.getDefaultCollectors());
    }

    // Setter and getters

    public ExecutionOptions getSettings() {
        return Settings;
    }

    public void setSettings(ExecutionOptions Settings) {
        this.Settings = Settings;
    }

    /**
     * Create and return a settings panel for editing the agent settings
     * @return editor as a JPanel
     */
    public JPanel getSettingsPanel () {
        return this.SettingsPanel;
    }

    /**
     * Create and return an options panel for editing the agent options
     * @return editor as a JPanel
     */
    abstract public JPanel getOptionsPanel ();

    public String getStartButtonText() {
        return StartButtonText;
    }

    public String getStopButtonText() {
        return StopButtonText;
    }

    public EPlusBatch getJobOwner() {
        return JobOwner;
    }

    public JEPlusPrintablePanel getGUIPanel() {
        return GUIPanel;
    }

    public List<Thread> getProcessors() {
        return Processors;
    }

    public boolean isReady() {
        return Ready;
    }

    /**
     * Retrieve state of the agent
     * @return 
     */
    public AgentState getState() {
        return State;
    }

    abstract public void setState(AgentState State);

    public String getAgentID() {
        return AgentID;
    }

    public List<EPlusTask> getFinishedJobs() {
        return FinishedJobs;
    }

    public List<EPlusTask> getJobQueue() {
        return JobQueue;
    }

    public int getPlatformType() {
        return PlatformType;
    }

    public int getQueueCapacity() {
        return QueueCapacity;
    }

    public List<EPlusTask> getRejectedJobs() {
        return RejectedJobs;
    }

    public List<EPlusTask> getRunningJobs() {
        return RunningJobs;
    }

    public void setJobOwner(EPlusBatch JobOwner) {
        this.JobOwner = JobOwner;
    }

    public void setGUIPanel(EPlusTextPanelOld GUIPanel) {
        this.GUIPanel = GUIPanel;
    }

    public void setStopAgent(boolean StopAgent) {
        this.StopAgent = StopAgent;
    }

    public ArrayList<ResultCollector> getResultCollectors () {
        return this.ResultCollectors;
    }
    
    public void addResultCollector (ResultCollector collector) {
        if (ResultCollectors == null) {
            ResultCollectors = new ArrayList<> ();
        }
        ResultCollectors.add(collector);
    }
    
    public void setGUIPanel(JEPlusPrintablePanel GUIPanel) {
        this.GUIPanel = GUIPanel;
    }

    public PrintWriter getLogWriter() {
        return LogWriter;
    }

    public void setLogWriter(PrintWriter LogWriter) {
        this.LogWriter = LogWriter;
    }

    public boolean isValidationRequired() {
        return ValidationRequired;
    }

    public void setValidationRequired(boolean ValidationRequired) {
        this.ValidationRequired = ValidationRequired;
    }

    public JFrameAgentMonitor getMonitorGUI() {
        return MonitorGUI;
    }

    public void setMonitorGUI(JFrameAgentMonitor MonitorGUI) {
        this.MonitorGUI = MonitorGUI;
    }

    /**
     * Test if there is more capacity available
     * @return True if there are processors available
     */
    public boolean isAvailable() {
        return this.JobQueue.size() < this.QueueCapacity;
    }


    /**
     * Add a job to the job queue. Note the capacity of the queue is NOT strictly
     * observed.
     * @param job a new job to be executed
     * @return Total number of jobs in the queue
     */
    public int addJob(EPlusTask job) {
        JobQueue.add(job);
        return JobQueue.size();
    }

    /**
     * Add a group of jobs at once
     * @param jobs The list of jobs to add
     * @return Total number of jobs in the queue
     */
    public int addJobs(List<EPlusTask> jobs) {
        this.JobQueue.addAll(jobs);
        return JobQueue.size();
    }

    /**
     * Report the status of the Agent
     * @return A string describing the status of the agent
     */
    public String getStatus() {
        StringBuilder buf = new StringBuilder (getAgentID());
        buf.append(" is ").append(State);
        buf.append(" [Que=").append(this.getJobQueue().size());
        buf.append(", Run=").append(this.getProcessors().size()); // or RunningJobs?
        buf.append(", Fin=").append(this.getFinishedJobs().size());
        buf.append("]");
        //buf.append(" Elapsed time = ").append(DateUtility.showElapsedTime(StartTime.getTime(), true));
        return buf.toString();
    }

    /**
     * Report the status of the Agent in a shorter form
     * @return A string describing the status of the agent
     */
    public String getShortStatus() {
        StringBuilder buf = new StringBuilder ();
        buf.append("Q=").append(this.getJobQueue().size());
        buf.append(", R=").append(this.getProcessors().size()); // or RunningJobs?
        buf.append(", F=").append(this.getFinishedJobs().size());
        return buf.toString();
    }

    /**
     * Clear job queue
     */
    public void purgeJobQueue () {
        if (!JobQueue.isEmpty()) JobQueue.clear();
    }
    
    /**
     * Clear all lists
     */
    public void purgeAllLists () {
        if (!FinishedJobs.isEmpty()) FinishedJobs.clear();
        if (!RunningJobs.isEmpty()) RunningJobs.clear();
        if (!RejectedJobs.isEmpty()) RejectedJobs.clear();
    }

    /**
     * Start the agent 
     * @param panel
     */
    public void initializeAgent(JEPlusPrintablePanel panel) {
        GUIPanel = panel;
        Ready = true;
    }
    
    /**
     * Write the job queue as a jobs list to the specified file
     * @param filename 
     */
    public void writeJobListToFile (String filename) {
        if (this.JobQueue != null) {
            try (PrintWriter fw = new PrintWriter (new FileWriter (filename))) {
                // Write a header
                if (! JobQueue.isEmpty()) {
                    StringBuilder buf = new StringBuilder ("#Job_ID,Weather File, Model File");
                    for (String tag : JobQueue.get(0).getSearchStringList()) {
                        buf.append(",").append(tag);
                    }
                    fw.println(buf.toString());
                    // Write jobs
                    for (EPlusTask job : JobQueue) {
                        buf = new StringBuilder (job.getJobID());
                        buf.append(",").append(job.getWorkEnv().WeatherDir).append(job.getWorkEnv().WeatherFile);
                        buf.append(",").append(job.getWorkEnv().IDFDir).append(job.getWorkEnv().IDFTemplate);
                        for (String val : job.getAltValueList()) {
                            buf.append(",").append(val);
                        }
                        fw.println(buf.toString());
                    }
                }else {
                    logger.warn("Job queue is empty!");
                    fw.println("#Job queue is empty!");
                }
            }catch (IOException ioe) {
                logger.error("Failed to write jobs list to file " + filename);
            }
        }
    }

    /**
     * Run result collection procedure in each attached result collectors
     * @param compile Compile combined data tables or not
     */
    public void runResultCollection (boolean compile) {
        // Get work environment
        EPlusWorkEnv Env = this.getJobOwner().getResolvedEnv();
        // For each result collector
        int nres;
        for (ResultCollector rc : ResultCollectors) {
            if (Env.KeepJobDir) {
                // collect reports
                nres = rc.collectReports(getJobOwner(), false);
                if (nres >= 0) {
                    writeLog(rc.getDescription() + " collected " + nres + " simulation reports in " + rc.getRepWriter().getReportFile());
                }
                // collect results
                nres = rc.collectResutls(getJobOwner(), false);
                if (nres > 0) {
                    StringBuilder buf = new StringBuilder (rc.getDescription());
                    buf.append(" collected ").append(nres).append(" rows of simulation results in ");
                    for (String item: rc.getResultFiles()) {
                        buf.append(item).append(" ");
                    }
                    writeLog(buf.toString());
                }
                // Generate job index
                boolean ok = rc.collectIndexes(getJobOwner());
                if (ok) {
                    writeLog(rc.getDescription() + " collected job index table in " + rc.getIdxWriter().getIndexFile());
                }
            }
        }
        if (compile) {
            RVX rvx = this.getJobOwner().getProject().getRvx();
            // Combine results into combined table and derivatives table
            EPlusBatch.writeCombinedResultTable(getResultCollectors(), this.getJobOwner().getResolvedEnv().getParentDir(), rvx, "AllCombinedResults.csv");
            writeLog("Combined result table AllCombinedResults.csv is created.");
            EPlusBatch.writeDerivedResultTable(getResultCollectors(), this.getJobOwner().getResolvedEnv().getParentDir(), rvx, "AllDerivedResults.csv");
            writeLog("Derivative result table AllDerivedResults.csv is created.");
        }       
    }
    
    /**
     * Write a line of log, including date and source, to output panel and log file
     * @param entry The entry text of the event
     */
    public void writeLog (String entry) {
        // Always write to the default logger
        logger.info(entry);
        // Additional logs to GUI or file log writer
        String date = new SimpleDateFormat("d MMM yyyy HH:mm:ss z").format(new Date());
        if (GUIPanel != null) {
            GUIPanel.appendContent(date + " [" + this.getAgentID()  + "] " + entry + "\n");
        }
        if (LogWriter != null) {
            LogWriter.println(date + " [" + this.getAgentID()  + "] " + entry);
            LogWriter.flush();
        }
    }

    abstract public int getExecutionType();

    abstract public boolean checkAgentSettings ();
    
    /**
     * Show or hide Agent's monitor GUI
     * @param show  Show window or not
     * @param reset Reset monitor info or not
     */
    abstract public void showAgentMonitorGUI (boolean show, boolean reset);

}
