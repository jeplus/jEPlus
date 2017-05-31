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
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jeplus.*;
import jeplus.data.ExecutionOptions;
import jeplus.data.RVX;
import jeplus.gui.JFrameAgentLocalMonitor;
import jeplus.gui.JPanel_LocalControllerOptions;
import jeplus.postproc.*;

/**
 * Local simulation manager to replace the run() functions in EPlusBatch and 
 * EPlusTask. This agent supports multiple threads, which is configurable on
 * the fly.
 * @todo AgentLocal to be implemented
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.5b
 */
public class TrnsysAgentLocal extends EPlusAgent {
    
    protected TRNSYSConfig Config = null;

    /**
     * Construct with Exec settings
     * @param settings Reference to an existing Exec settings instance
     */
    public TrnsysAgentLocal (ExecutionOptions settings) {
        super("Local batch simulation controller", settings);
        this.QueueCapacity = 10000;
        this.attachDefaultCollector();
        Config = JEPlusConfig.getDefaultInstance().getTRNSYSConfigs().get("TRNSYS");
        SettingsPanel = new jeplus.gui.JPanel_TrnsysSettings (Config);
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
    public int getExecutionType() {
        return ExecutionOptions.INTERNAL_CONTROLLER;
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
        // Show monitor GUI
        this.State = AgentState.RUNNING;
        if (this.getJobOwner().getGUI() != null) showAgentMonitorGUI(true, true);
        // Start process
        if (Processors == null) Processors = new ArrayList<> ();
        // Clear all lists before run
        //if (FinishedJobs.size() > 0) FinishedJobs.removeAllElements();
        purgeAllLists();
        // Timingif (FinishedJobs.size() > 0) FinishedJobs.removeAllElements();
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
            for (int i=0; i<Processors.size(); i++) {
                Thread proc = Processors.get(i);
                if (! proc.isAlive()) {
                    Processors.remove(proc);
                    RunningJobs.remove((EPlusTask)proc);
                    FinishedJobs.add((EPlusTask)proc);
                    i --;                    
                    if (!((EPlusTask)proc).isResultAvailable()){
                        if (GUIPanel != null) {
                            GUIPanel.appendContent("AN ERROR OCCURRED IN THE JOB " + ((EPlusTask)proc).getJobID() + ". NO OUTPUTS WERE CREATED. Please, check DCK/TRD TEMPLATE or the OUPUT FILE NAME(S)\n");
                        }
                        if (this.getLogWriter() != null) {
                            this.getLogWriter().println("AN ERROR OCCURRED IN THE JOB " + ((EPlusTask)proc).getJobID() + ". NO OUTPUTS WERE CREATED. Please, check DCK/TRD TEMPLATE or the OUPUT FILE NAME(S)");
                            this.getLogWriter().flush();
                        }                        
                    }
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
                    "There are still " + Processors.size() + " TRNSYS simulations running. Do you want to wait till they finish?",
                    "Stop signal received",
                    JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    // Wait for the last few jobs to finish
                    while (Processors.size() > 0) {
                        // Check if any of the processors have finished
                        for (int i=0; i<Processors.size(); i++) {
                            Thread proc = Processors.get(i);
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
                for (int i=0; i<Processors.size(); i++) {
                    Thread proc = Processors.get(i);
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

        // Start collecting results
        writeLog("Collecting results ...");
        runResultCollection(true);
        
        // Done
        writeLog("Done!");
        // write start time
        Date EndTime = new Date ();
        writeLog("Simulation finished at: " + DateFormat.format(EndTime));
        writeLog("Total execution time = " + ((EndTime.getTime() - StartTime.getTime())/1000) + " seconds.\n");

        // Notify Owner
        this.getJobOwner().setSimulationRunning(false);
    }

    /**
     * Run result collection procedure. In the case for TRNSYS, collectors are constructed within this function
     */
    @Override
    public void runResultCollection (boolean compile) {
        RVX rvx = this.getJobOwner().getProject().getRvx();
        // Clear collectors list first
        ResultCollectors.clear();
        if (rvx == null || rvx.getTRNs() == null || rvx.getTRNs().length == 0) {
            // Report and index collector
            ResultCollector rc = new ResultCollector ("TRNSYS report collector");
            rc.setRepReader(new TRNSYSOutputReader (null));
            rc.setRepWriter(new DefaultCSVWriter ("RunTimes.csv", null));
            rc.setIdxWriter(new DefaultIndexWriter ("SimJobIndex.csv"));
            ResultCollectors.add(rc);
            // Result collectors
            String OutputResultFiles = this.getJobOwner().getResolvedEnv().getOutputFileNames();
            List<String> TRNSYSResultFile = TRNSYSWinTools.getPrintersFunc(OutputResultFiles);
            for (int j = 0; j < TRNSYSResultFile.size(); j++) {
                String [] name = TRNSYSResultFile.get(j).split("\\s*[.]\\s*");
                rc = new ResultCollector ("TRNSYS result collector");
                rc.setResReader(new TRNSYSOutputReader (TRNSYSResultFile.get(j)));
                rc.setResWriter(new DefaultCSVWriter (null, "SimResults" + "_" + name[0] + ".csv"));
                ResultCollectors.add(rc);
            }
        }else {
            this.attachDefaultCollector();
        }
        super.runResultCollection(compile);
    }
    
    /**
     * Check the local EnergyPlus installation settings.
     * @return True if everything is in place
     */
    @Override
    public boolean checkAgentSettings() {
        String bindir = Config.getResolvedTRNSYSBinDir();
        String exe = Config.getResolvedTRNSYSEXEC();

        boolean success = new File(exe).exists();
        if (! success) {
            try {
                this.JobOwner.getBatchInfo().setValidationSuccessful(false);
                this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Error: " + exe + " is not accessible.");
            }catch (Exception ex) {
                System.err.println("[" + this.AgentID + "]: Settings error: " + exe + " is not accessible.");
            }
        }
        return success;
    }    
}
