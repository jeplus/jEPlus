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

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import jeplus.EPlusTask;
import jeplus.EPlusWorkEnv;
import jeplus.JEPlusFrameMain;
import jeplus.data.ExecutionOptions;
import jeplus.data.RVX;
import jeplus.gui.EPlusTextPanelOld;
import jeplus.gui.JEPlusPrintablePanel;
import jeplus.gui.JFrameAgentMonitor;
import jeplus.postproc.CsvResultCollector;
import jeplus.postproc.DefaultReportCollector;
import jeplus.postproc.EsoResultCollector;
import jeplus.postproc.PythonResultCollector;
import jeplus.postproc.ResultCollector;
import jeplus.postproc.SQLiteResultCollector;
import jeplus.postproc.TrnsysResultCollector;
import jeplus.postproc.UserResultCollector;
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
    protected List<Thread> Processors = null;

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
        ResultCollector rc = new DefaultReportCollector ("Standard report collector");
        ResultCollectors.add(rc);
        rc = new EsoResultCollector ("ESO result collector");
        ResultCollectors.add(rc);
        rc = new SQLiteResultCollector ("SQLite result collector");
        ResultCollectors.add(rc);
        rc = new CsvResultCollector ("E+ CSV tables result collector");
        ResultCollectors.add(rc);
        rc = new UserResultCollector ("User supplied result collector");
        ResultCollectors.add(rc);
        rc = new PythonResultCollector ("Python script result collector");
        ResultCollectors.add(rc);
        rc = new TrnsysResultCollector ("TRNSYS result collector");
        ResultCollectors.add(rc);
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
     * @param hostframe The host frame in which this panel is going to reside
     * @return editor as a JPanel
     */
    abstract public JPanel getSettingsPanel (JEPlusFrameMain hostframe);

    /**
     * Create and return an options panel for editing the agent options
     * @param hostframe The host frame in which this panel is going to reside
     * @return editor as a JPanel
     */
    abstract public JPanel getOptionsPanel (JEPlusFrameMain hostframe);

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
     * @return True if
     */
    abstract public boolean isAvailable();

    /**
     * Add a job to be handled immediately
     * @param job a new job to be executed
     * @return The total number jobs in the queue
     */
    public abstract int addJob(EPlusTask job);

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
    public String getStatus () {
        return this.AgentID;
    }

    /**
     * Clear job queue
     */
    public void purgeJobQueue () {
        if (JobQueue.size() > 0) JobQueue.clear();
    }
    
    /**
     * Clear all lists
     */
    public void purgeAllLists () {
        if (FinishedJobs.size() > 0) FinishedJobs.clear();
        if (RunningJobs.size() > 0) RunningJobs.clear();
        if (RejectedJobs.size() > 0) RejectedJobs.clear();
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
