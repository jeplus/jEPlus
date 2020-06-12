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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jeplus.*;
import jeplus.data.ExecutionOptions;
import jeplus.data.VersionInfo;
import jeplus.gui.JFrameAgentLocalMonitor;
import jeplus.gui.JPanel_LocalControllerOptions;

/**
 * Local simulation manager to replace the run() functions in EPlusBatch and 
 * EPlusTask. This agent supports multiple threads, which is configurable on
 * the fly.
 * @todo AgentLocal to be implemented
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.5b
 */
public class EPlusAgentLocal extends EPlusAgent {
    
    JEPlusConfig Config = null;
    
    /**
     * Construct with Exec settings
     * @param config JEPlus config instance containing multiple EPlusConfig objects
     * @param settings Reference to an existing Exec settings instance
     */
    public EPlusAgentLocal (JEPlusConfig config, ExecutionOptions settings) {
        super("Local batch simulation controller", settings);
        this.Config = config;
        this.QueueCapacity = 10000;
        this.attachDefaultCollector();
        SettingsPanel = new jeplus.gui.JPanel_EPlusSettings (Config);
    }
    
    /**
     * Create and return an options panel for editing the agent options
     * @return editor as a JPanel
     */
    @Override
    public JPanel getOptionsPanel () {
        return new JPanel_LocalControllerOptions (Settings);
    }

    @Override
    public void showAgentMonitorGUI (boolean show, boolean reset) {
        if (show) {
            if (MonitorGUI == null) {
                MonitorGUI = new JFrameAgentLocalMonitor (this);
                MonitorGUI.setTitle("Simulation Agent Local");
                MonitorGUI.setSize(600, 530);
                MonitorGUI.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            }
            if (reset) {
                MonitorGUI.reset();
            }
            MonitorGUI.pack();
            if (! MonitorGUI.isVisible()) {
                MonitorGUI.setVisible(true);
            }
            if (! MonitorGUI.isActive()) {
                MonitorGUI.requestFocus();
            }
        }else {
            if (MonitorGUI != null && MonitorGUI.isVisible()) {
                MonitorGUI.setVisible(false);
            }
        }
    }
    
    /**
     * Start the agent in a separate thread
     */
    @Override
    public void run() {

        // Notify Owner
        this.getJobOwner().setSimulationRunning(true);
        
        // Write job list
        if (Settings.getSteps().isWriteJobList()) {
            String listfile = JobOwner.getProject().getBaseDir() + Settings.getSteps().getJobListFile();
            writeLog("Writing job lists to " + listfile);
            writeJobListToFile (listfile);
        }else {
            writeLog("Job list file is not requested.");
        }
        
        // Clear all lists before run
        purgeAllLists();

        // Prepare jobs and run simulations
        if (Settings.getSteps().isPrepareJobs() || Settings.getSteps().isRunSimulations()) {
            
            // show monitor GUI
            this.State = AgentState.RUNNING;
            if (this.getJobOwner().getGUI() != null || (MonitorGUI != null && MonitorGUI.isVisible())) {
                showAgentMonitorGUI(true, true);
            }

            if (Processors == null) {
                Processors = new ArrayList<> ();
            }

            // Timing
            StartTime = new Date();
            StopAgent = false;

            while ((! StopAgent) && JobQueue.size() > 0)  {
                // Fill processor threads
                while ((! StopAgent) && (State != AgentState.PAUSED) && Processors.size() < Settings.getNumThreads() && JobQueue.size() > 0) {
                    EPlusTask job = JobQueue.remove(0);
                    if (job != null) {
                        this.RunningJobs.add(job);
                        Processors.add(job);
                        job.start();
                        // GUI update
                        writeLog("Job " + job.getJobID() + " started. " + JobQueue.size() + " more to go!");
                        job.setExecuted(true);
                    }else {
                        break;
                    }
                    try { Thread.sleep(Settings.getDelay()); } catch (Exception ex) {}
                }
                // Check if any of the processors have finished
                for (int i=0; i<RunningJobs.size(); i++) {
                    Thread proc = RunningJobs.get(i);
                    if (! proc.isAlive()) {
                        Processors.remove(proc);
                        RunningJobs.remove((EPlusTask)proc);
                        FinishedJobs.add((EPlusTask)proc);
                        i --;
                    }
                }
                try { Thread.sleep(Settings.getDelay()); } catch (Exception ex) {}
            }
            // if manually stopped
            if (StopAgent) {
                writeLog("Local agent received a STOP signal. ");
                if (Processors.size() > 0) {
                    int n = JOptionPane.showConfirmDialog(
                        this.getGUIPanel(),
                        "There are still " + Processors.size() + " E+ simulations running. Do you want to wait till they finish?",
                        "Stop signal received",
                        JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        // Wait for the last few jobs to finish
                        while (Processors.size() > 0) {
                            // Check if any of the processors have finished
                            for (int i=0; i<RunningJobs.size(); i++) {
                                Thread proc = RunningJobs.get(i);
                                if (! proc.isAlive()) {
                                    Processors.remove(proc);
                                    RunningJobs.remove((EPlusTask)proc);
                                    FinishedJobs.add((EPlusTask)proc);
                                    i --;
                                }
                            }
                            try { Thread.sleep(Settings.getDelay()); } catch (Exception ex) {}
                        }
                    }else {
                        Processors.clear();
                    }
                }
                State = AgentState.CANCELLED;
                StopAgent = false;
            }else {
                // GUI update
                writeLog("Nearly there ...");
                // Wait for the last few jobs to finish
                while (Processors.size() > 0) {
                    // Check if any of the processors have finished
                    for (int i=0; i<RunningJobs.size(); i++) {
                        Thread proc = RunningJobs.get(i);
                        if (! proc.isAlive()) {
                            Processors.remove(proc);
                            RunningJobs.remove((EPlusTask)proc);
                            FinishedJobs.add((EPlusTask)proc);
                            i --;
                        }
                    }
                    try { Thread.sleep(Settings.getDelay()); } catch (Exception ex) {}
                }
                State = AgentState.FINISHED;
            }
            writeLog(this.getStatus());
            writeLog("Local agent stopped. ");

        }else {
            writeLog("Job preparation and simuation are not requested.");
            FinishedJobs.addAll(JobQueue);
        }
        
        // Start collecting results
        if (Settings.getSteps().isCollectResults()) {
            writeLog("Collecting results ...");
            runResultCollection (true);
        }else {
            writeLog("collecting results is not requested.");
        }
        
        // Done
        writeLog("All done!");
        // write start time
        Date EndTime = new Date ();
        writeLog("Simulation finished at: " + DateFormat.format(EndTime));
        writeLog("Total execution time = " + ((EndTime.getTime() - StartTime.getTime())/1000) + " seconds.\n");

        // Notify Owner
        this.getJobOwner().setSimulationRunning(false);
    }

    /**
     * Set state of the agent. Only Running and Paused are accepted
     * @param State 
     */
    @Override
    public void setState(AgentState State) {
        if (State == AgentState.RUNNING || State == AgentState.PAUSED) {
            this.State = State;
        }
    }

    @Override
    public int getExecutionType() {
        return ExecutionOptions.INTERNAL_CONTROLLER;
    }

    /**
     * Check the local EnergyPlus installation settings.
     * @return True if everything is in place
     */
    @Override
    public boolean checkAgentSettings() {
        boolean success = false;
        VersionInfo IdfVer = new VersionInfo (this.getJobOwner().getProject().getEPlusModelVersion());
        success = Config.getEPlusConfigs().containsKey(IdfVer);
        if (! success) {
            try {
                this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Cannot find an E+ installation to handle model version " + IdfVer + " of the project!");
                this.JobOwner.getBatchInfo().setValidationSuccessful(false);
            }catch (Exception ex) {
                System.err.println("[" + this.AgentID + "]: Version checking error. ");
            }
        }
        Set<String> languages = this.getJobOwner().getProject().getPythonDependency();
        for (String lang : languages) {
            ScriptConfig cfg = Config.getScripConfigs().get(lang);
            if ( cfg == null || ! new File (cfg.getExec()).exists()) {
                success = false;
                try {
                    this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Cannot find " + lang + "'s executable to handle the scripts in the project!");
                    this.JobOwner.getBatchInfo().setValidationSuccessful(false);
                }catch (Exception ex) {
                    logger.error("[" + this.AgentID + "]: Script interpreter checking error.", ex);
                }
            }            
        }
        return success;
    }
}
