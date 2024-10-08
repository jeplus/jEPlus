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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import jeplus.agent.EPlusAgent;
import jeplus.agent.EPlusAgentLocal;
import jeplus.agent.InselAgentLocal;
import jeplus.agent.TrnsysAgentLocal;
import jeplus.data.BatchRunOptions;
import jeplus.data.ExecutionOptions;
import jeplus.data.ParameterItemV2;
import jeplus.data.RVX;
import jeplus.data.RandomSource;
import jeplus.event.IF_ProjectChangedHandler;
import jeplus.gui.*;
import jeplus.gui.editor.JPanel_RVXTree;
import jeplus.postproc.ResultCollector;
import jeplus.util.RelativeDirUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.slf4j.LoggerFactory;

/**
 * Main UI for the E+ batch shell
 * @author Yi Zhang
 * @version 1.0
 * @since 1.0
 */
public class JEPlusFrameMain extends JFrame implements IF_ProjectChangedHandler {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JEPlusFrameMain.class);

    public static final int PARAM_TAB = 0;
    public static final int EXEC_TAB = 1;
    public static final int POST_PROC_TAB = 2;
    
    protected NumberFormat LargeIntFormatter = new DecimalFormat("###,###,###,###,###,###");

//    public final static String version = "2.0.0 Beta";
//    public final static String version_ps = "_2_0";
//    public final static String year = "2018,2019";
//    public final static String osName = System.getProperty( "os.name" );
//    protected static String JEPlusVersion = "jEPlus (version " + version + ")";
    
    protected static JEPlusFrameMain CurrentMainWindow = null;
    public static JEPlusFrameMain getCurrentMainGUI () { return CurrentMainWindow; }

    protected JFileChooser fc = new JFileChooser("./");
    protected File DefaultDir = new File ("./");
    protected String CurrentProjectFile = null;
    protected JEPlusProjectV2 Project = new JEPlusProjectV2 ();
    protected JEPlusProjectV2 SavedProject = null;

    protected EPlusTextPanelOld OutputPanel = null;
    protected EPlusTextPanelOld ResultFilePanel = null;
//    protected JPanel_ParameterTree jplParameterTree = null;
    protected JPanel_ParameterTable jplParameterTable = null;
    protected JPanel_RVXTree jplRvxTree = null;
    // Project file panel for EnerygPlus
    protected JPanel_EPlusProjectFiles EPlusProjectFilesPanel = new JPanel_EPlusProjectFiles();
    // Project file panel for Trnsys
    protected JPanel_TrnsysProjectFiles TrnsysProjectFilesPanel = new JPanel_TrnsysProjectFiles();
    // Project file panel for Trnsys
    protected JPanel_InselProjectFiles InselProjectFilesPanel = new JPanel_InselProjectFiles();
    // Utility panel - External program configuration
    protected JPanelProgConfiguration jplProgConfPanel;
    // Utility panel - IDF converter
    protected JPanel_UtilIdfUpdater jplIDFConvPanel;
    // Utility panel - Run Python
    protected JPanel_UtilRunReadVars jplReadVarsPanel;
    // Utility panel - Run ReadVars
    protected JPanel_UtilRunScript jplPythonPanel;
    // Program config panel
    protected JPanelProgConfiguration ConfigPanel;

    protected EPlusBatch BatchManager = null;
    protected EPlusBatch ActingManager = null;
    protected ArrayList <EPlusAgent> EPlusExecAgents = new ArrayList <> ();
    protected ArrayList <EPlusAgent> TrnsysExecAgents = new ArrayList <> ();
    protected ArrayList <EPlusAgent> InselExecAgents = new ArrayList <> ();
    protected ArrayList <EPlusAgent> ExecAgents = EPlusExecAgents;

    protected boolean SimulationRunning = false;
    protected int FrameCloseOperation = JFrame.EXIT_ON_CLOSE;

    // Listeners
    private DocumentListener DL = null;

    
    /** 
     * Creates new form EPlusFrame 
     */
    public JEPlusFrameMain() {
        
        initComponents();

        this.setTitle(JEPlusVersion.getVersion() + " - New Project");
        
        this.cboProjectType.setModel(new DefaultComboBoxModel<>(JEPlusProjectV2.ModelType.values()));
        // Diable INSEL
        this.cboProjectType.removeItem(JEPlusProjectV2.ModelType.INSEL);

        // tabTexts.setTabComponentAt(0, new ButtonTabComponent (tabTexts));
//        jplParameterTree = new JPanel_ParameterTree ();
//        jplParamTreeHolder.add(this.jplParameterTree, BorderLayout.CENTER);
        jplParameterTable = new JPanel_ParameterTable ();
        jplParamTableHolder.add(this.jplParameterTable, BorderLayout.CENTER);
        jplRvxTree = new JPanel_RVXTree ();
        jplRVX.add(this.jplRvxTree, BorderLayout.CENTER);
        initProjectSection();
        initBatchOptions();

        EPlusExecAgents.add(new EPlusAgentLocal (JEPlusConfig.getDefaultInstance(), Project.getExecSettings()));
        TrnsysExecAgents.add(new TrnsysAgentLocal (Project.getExecSettings()));
        InselExecAgents.add(new InselAgentLocal (Project.getExecSettings()));
        String [] options = {ExecAgents.get(0).getAgentID()};
        this.cboExecutionType.setModel(new DefaultComboBoxModel (options));
        // this.setExecType(0);
        
        this.cboSampleOpt.setModel(new DefaultComboBoxModel (EPlusBatch.SampleType.values()));

        OutputPanel = new EPlusTextPanelOld ("Output", EPlusTextPanel.VIEWER_MODE);
        // Start a new thread for output panel
        new Thread (OutputPanel).start();
        OutputPanel.appendContent("Welcome to jEPlus!\n");
        TpnEditors.add(OutputPanel);
        TpnEditors.setSelectedComponent(OutputPanel);
        
        jplProgConfPanel = new JPanelProgConfiguration (null, JEPlusConfig.getDefaultInstance(), JPanelProgConfiguration.Layout.WIDE);
        jplIDFConvPanel = new JPanel_UtilIdfUpdater (this, JEPlusConfig.getDefaultInstance(), this.getProject());
        jplPythonPanel = new JPanel_UtilRunScript (this, JEPlusConfig.getDefaultInstance(), getProject() == null ? "./" : getProject().resolveWorkDir());
        jplReadVarsPanel = new JPanel_UtilRunReadVars(this, JEPlusConfig.getDefaultInstance());
//        TpnUtilities.add("Configure Programs", jplProgConfPanel);
        TpnUtilities.add("Run Python", jplPythonPanel);
        TpnUtilities.add("IDF Converter", jplIDFConvPanel);
        TpnUtilities.add("Run ReadVars", jplReadVarsPanel);
        
        this.Project.addListener(this);
        
        // put the frame in the centre of screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        int frameWidth = Math.min(1366, screenWidth);  
        int frameHeight = Math.min(900, screenHeight);  
        setMinimumSize (new Dimension (frameWidth, frameHeight));
        setSize(frameWidth, frameHeight);
        setLocation((screenWidth-frameWidth)/2,(screenHeight-frameHeight)/2);
    }

    // =============== Getters and setters =====================
    
    public JFileChooser getFileChooser() {
        return fc;
    }

    public File getDefaultDir() {
        return DefaultDir;
    }

    public int getFrameCloseOperation() {
        return FrameCloseOperation;
    }

    public void setFrameCloseOperation(int FrameCloseOperation) {
        this.FrameCloseOperation = FrameCloseOperation;
    }

    public boolean isSimulationRunning() {
        return SimulationRunning;
    }

    public void setSimulationRunning(boolean SimulationRunning) {
        this.SimulationRunning = SimulationRunning;
        if (SimulationRunning) {
            cmdStart.setActionCommand("stop");
            cmdStart.setText(BatchManager.getAgent().getStopButtonText());
            cboExecutionType.setEnabled(false);
            jMenuItemStop.setEnabled(true);
            // Enable view results
            this.jMenuViewResult.setEnabled(false);
        }else {
            cmdStart.setActionCommand("start");
            cmdStart.setText(BatchManager.getAgent().getStartButtonText());
            cboExecutionType.setEnabled(true);
            jMenuItemStop.setEnabled(false);
            // Enable view results
            this.jMenuViewResult.removeAll();
            this.jMenuViewResult.add(this.jMenuItemViewFolder);
            this.jMenuViewResult.add(new JSeparator());
            this.addMenuItemResultFile("SimJobIndex.csv");
            this.addMenuItemResultFile("RunTimes.csv");
            if (this.Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS) {
                ArrayList<ResultCollector> rcs = BatchManager.getAgent().getResultCollectors();
                for (ResultCollector rc : rcs) {
                    for (int j = 0; j < rc.getResultFiles().size(); j++) {
                        this.addMenuItemResultFile(rc.getResultFiles().get(j));
                    }
                }
            }else if (this.Project.getProjectType() == JEPlusProjectV2.ModelType.TRNSYS) {
                List<String> TRNSYSResultFile = TRNSYSWinTools.getPrintersFunc(Project.getOutputFileNames());
                for (String names : TRNSYSResultFile) {
                    String[] name = names.split("\\s*[.]\\s*");
                    this.addMenuItemResultFile("SimResults_" + name[0] + ".csv");
                }
            }else if (this.Project.getProjectType() == JEPlusProjectV2.ModelType.INSEL) {
                List<String> INSELResultFile = INSELWinTools.getPrintersFunc(Project.getOutputFileNames());
                for (String names : INSELResultFile) {
                    String[] name = names.split("\\s*[.]\\s*");
                    this.addMenuItemResultFile("SimResults_" + name[0] + ".csv");
                }
            }
            this.jMenuViewResult.add(new JSeparator());
            this.addMenuItemResultFile("AllCombinedResults.csv");
            this.addMenuItemResultFile("AllDerivedResults.csv");
            this.jMenuViewResult.setEnabled(true);
        }
    }

    public EPlusBatch getBatchManager() {
        return BatchManager;
    }

    public void setBatchManager(EPlusBatch BatchManager) {
        this.BatchManager = BatchManager;
    }

    public EPlusBatch getActingManager() {
        return ActingManager;
    }

    public void setActingManager(EPlusBatch ActingManager) {
        this.ActingManager = ActingManager;
    }


    public ArrayList<EPlusAgent> getExecAgents() {
        return ExecAgents;
    }

    public JTabbedPane getTpnEditors() {
        return TpnEditors;
    }

    /**
     * Get access to the OutputPanel
     * @return Reference to the OutputPanel, which serves as the main output screen.
     */
    public EPlusTextPanelOld getOutputPanel() {
        return OutputPanel;
    }

    public String getCurrentProjectFile() {
        return CurrentProjectFile;
    }

    public void setCurrentProjectFile(String CurrentProjectFile) {
        this.CurrentProjectFile = CurrentProjectFile;
        this.setTitle(JEPlusVersion.getVersion() + " - " + CurrentProjectFile + (Project.isContentChanged()?"*":""));
        // Update recent projects list
        List<String> recent = JEPlusConfig.getDefaultInstance().getRecentProjects();
        if (recent.contains(CurrentProjectFile)) {
            recent.remove(CurrentProjectFile);
        }
        recent.add(0, CurrentProjectFile);
        while (recent.size() > 20) {
            recent.remove(recent.size()-1);
        }
        this.updateRecentFilesMenu();
    }

    // =============== End getters and setters ===============
    
    /**
     * Show splash screen
     * @param parent Parent frame of this dialog
     */
    public static void showSplash (java.awt.Frame parent) {
        JDialog_Splash splash = new JDialog_Splash(parent, true);
        splash.setLocationByPlatform(true);
        splash.setLocationRelativeTo(parent);
        splash.setVisible(true);
    }
    
    public void showConfigDialog () {
        JDialog ConfigDialog = new JDialog (this, "Configuration file: ", true);
        if (jplProgConfPanel == null) {
            jplProgConfPanel = new JPanelProgConfiguration(ConfigDialog, JEPlusConfig.getDefaultInstance(), JPanelProgConfiguration.Layout.WIDE);
        }else {
            jplProgConfPanel.setHostWindow(ConfigDialog);
        }
        ConfigDialog.getContentPane().add(jplProgConfPanel);
        ConfigDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        ConfigDialog.setTitle(ConfigDialog.getTitle() + jplProgConfPanel.getConfigFile());
        ConfigDialog.pack();
        ConfigDialog.setSize(960, 750);
        ConfigDialog.setLocationRelativeTo(this);
        ConfigDialog.setVisible(true);        
    }

    /**
     * Retrieve the current project object
     * @return 
     */
    public JEPlusProjectV2 getProject() {
        return Project;
    }

    /**
     * Set the current Project object
     * @param Project Project object
     * @param batch Simulation manager object
     * @return true if new project is set, or false if cancelled
     */
    public boolean setProject(JEPlusProjectV2 Project, EPlusBatch batch) {
        if (Project != null) {
            if ( this.Project!= null) {
                // Detect project changes
                if (this.Project.isContentChanged() /*|| ! Objects.equals(Project, SavedProject)*/) {
                    // Save the project file before exit?
                    String cfn = this.CurrentProjectFile;
                    int n = JOptionPane.showConfirmDialog(
                        this,
                        "Do you want to save the current project to " + (cfn==null? "file" : cfn) + " before loading the new project?",
                        "Save project",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                    if (n == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }else if (n == JOptionPane.YES_OPTION) {
                        this.jMenuItemSaveActionPerformed(null);
                    }
                }
                this.Project.removeListener(this);
            }
            this.Project = Project;
            this.SavedProject = null;
            this.initProjectSection();
            this.setExecType(0);
            this.Project.addListener(this);
        }
        if (batch != null) {
            this.BatchManager = batch;
        }
        return true;
    }

    /**
     * Set project type (E+ or TRNSYS) and disable the selection box
     * @param type Constant for E+ project (0) or TRNSYS project (1)
     */
    public final void fixProjectType (JEPlusProjectV2.ModelType type) {
        setProjectType(type);
        this.cboProjectType.setEnabled(false);
    }

    /**
     * Set project type (E+ or TRNSYS). Trigger project section and execution 
     * section update
     * @param type enum for E+ project or TRNSYS project
     */
    public final void setProjectType (JEPlusProjectV2.ModelType type) {
        Project.setProjectType(type);
        initProjectSection();
        if (null != type) switch (type) {
            case EPLUS:
                this.ExecAgents = EPlusExecAgents;
                break;
            case TRNSYS:
                this.ExecAgents = TrnsysExecAgents;
                break;
            case INSEL:
                this.ExecAgents = InselExecAgents;
                break;
            default:
                break;
        }
        this.setExecType(ExecutionOptions.INTERNAL_CONTROLLER);
        // Disable post-process tab if TRNSYS
        if (type != JEPlusProjectV2.ModelType.EPLUS) { // not EPlus
            this.tpnMain.setEnabledAt(3, false);
        }else {
            this.tpnMain.setEnabledAt(3, true);
        }
    }
    
    /**
     * Update the project section in the GUI with information in the <code>Project</code>
     */
    protected final void initProjectSection () {
        this.jplProjectFilesPanelHolder.removeAll();
        this.jplProjectFilesPanelHolder.setLayout(new BorderLayout());
        if (null != Project.getProjectType()) switch (Project.getProjectType()) {
            case EPLUS:
                EPlusProjectFilesPanel = new JPanel_EPlusProjectFiles(this, Project);
                this.jplProjectFilesPanelHolder.add(EPlusProjectFilesPanel, BorderLayout.CENTER);
                break;
            case TRNSYS:
                TrnsysProjectFilesPanel = new JPanel_TrnsysProjectFiles(this, Project);
                this.jplProjectFilesPanelHolder.add(TrnsysProjectFilesPanel, BorderLayout.CENTER);
                break;
            case INSEL:
                InselProjectFilesPanel = new JPanel_InselProjectFiles(this, Project);
                this.jplProjectFilesPanelHolder.add(InselProjectFilesPanel, BorderLayout.CENTER);
                break;
            default:
                break;
        }
        this.jplProjectFilesPanelHolder.validate();
        this.jplProjectFilesPanelHolder.repaint();
        jplParameterTable.setProject(Project);
        jplRvxTree.setContents(this, Project);
    }

    /**
     * Fill in the batch execution options section in the Exec tab
     */
    private void initBatchOptions() {
        this.txtJobListFile.setText(Project.getExecSettings().getJobListFile());
        this.txtTestRandomN.setText(Integer.toString(Project.getExecSettings().getNumberOfJobs()));
        this.txtRandomSeed.setText(Long.toString(Project.getExecSettings().getRandomSeed()));
        this.cboSampleOpt.setSelectedItem(Project.getExecSettings().getSampleOpt());
        switch (Project.getExecSettings().getSubSet()) {
            case ExecutionOptions.CHAINS: 
                this.rdoTestChains.setSelected(true);
                this.rdoTestChainsActionPerformed(null);
                break;
            case ExecutionOptions.RANDOM:
                this.rdoTestRandomN.setSelected(true);
                this.rdoTestRandomNActionPerformed(null);
                break;
            case ExecutionOptions.FILE:
                this.rdoJobListFile.setSelected(true);
                this.rdoJobListFileActionPerformed(null);
                break;
            case ExecutionOptions.ALL:
                this.rdoAllJobs.setSelected(true);
                this.rdoAllJobsActionPerformed(null);
        }
        BatchRunOptions steps = Project.getExecSettings().getSteps();
        this.chkCreateList.setSelected(steps.isWriteJobList());
        this.txtSaveList.setText(steps.getJobListFile());
        this.chkPrepare.setSelected(steps.isPrepareJobs());
        this.chkRun.setSelected(steps.isRunSimulations());
        this.chkCollect.setSelected(steps.isCollectResults());

        // Set listeners to text fields
        DL = new DocumentListener () {
            Document DocJobListFile = txtJobListFile.getDocument();
            Document DocTestRandomN = txtTestRandomN.getDocument();
            Document DocRandomSeed = txtRandomSeed.getDocument();
            Document DocSaveList = txtSaveList.getDocument();

            @Override
            public void insertUpdate(DocumentEvent e) {
                Document src = e.getDocument();
                if(src == DocJobListFile) {
                    Project.getExecSettings().setJobListFile(txtJobListFile.getText());
                    if (! new File (Project.getExecSettings().getJobListFile()).exists()) {
                        txtJobListFile.setForeground(Color.red);
                    }else {
                        txtJobListFile.setForeground(Color.black);
                    }
                }else if (src == DocTestRandomN) {
                    try {
                        Project.getExecSettings().setNumberOfJobs(Integer.parseInt(txtTestRandomN.getText()));
                        txtTestRandomN.setForeground(Color.black);
                    }catch (NumberFormatException nfx) {
                        txtTestRandomN.setForeground(Color.red);
                        Project.getExecSettings().setNumberOfJobs(1); // one job by default
                    }
                }else if (src == DocRandomSeed) {
                    long seed;
                    try {
                        seed = Long.parseLong(txtRandomSeed.getText());
                        if (seed < 0) seed = new Date().getTime();
                        txtRandomSeed.setForeground(Color.black);
                    }catch (NumberFormatException nfx) {
                        seed = new Date().getTime();
                        txtRandomSeed.setForeground(Color.red);
                    }
                    Project.getExecSettings().setRandomSeed(seed);
                }else if(src == DocSaveList) {
                    Project.getExecSettings().getSteps().setJobListFile(txtSaveList.getText());
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // not applicable
            }

        };
        txtJobListFile.getDocument().addDocumentListener(DL);
        txtTestRandomN.getDocument().addDocumentListener(DL);
        txtRandomSeed.getDocument().addDocumentListener(DL);
        txtSaveList.getDocument().addDocumentListener(DL);
    }
    
    /**
     * Add ExecAgent to the mix
     * @param agent 
     */
    public void addExecAgent (EPlusAgent agent) {
        this.ExecAgents.add(agent);
        this.cboExecutionType.addItem(agent.getAgentID());
    }
    
    /**
     * Retrieve the current selected execution agent
     * @return ExecAgent object
     */
    public EPlusAgent getCurrentAgent () {
        return this.ExecAgents.get(cboExecutionType.getSelectedIndex());
    }

    /**
     * Set Execution type
     * @param type ...
     */
    public void setExecType (int type) {
        EPlusAgent agent = ExecAgents.get(type);
        if (agent != null) {
            Project.getExecSettings().setExecutionType(agent.getExecutionType());
            // agent.setSettings(Project.getExecSettings());
            this.updateAgentOptionPanel(agent);
        }else {
            logger.error("Requested execution type (" + type + ") is not supported!");
        }
    }

    protected void updateAgentOptionPanel (EPlusAgent agent) {
        this.jplOptions.removeAll();
        this.jplOptions.add(agent.getOptionsPanel());
        this.jplOptions.validate();
        this.jplOptions.repaint();
        this.cmdStart.setText(agent.getStartButtonText());
    }
    
    /**
     * Construct and validate the batch jobs according to the information
     * provided on the UI
     * @return successful or not
     */
    public boolean validateBatchJobs () {
        boolean success = true;

        this.displayInfo("Validating jobs. Hang on ...");
//        try {
//            Project.setProjectID(txtGroupID.getText().trim());
//        }catch (Exception ex) {};
        BatchManager = new EPlusBatch(this, Project);
        BatchManager.setAgent(this.ExecAgents.get(this.cboExecutionType.getSelectedIndex()));
        
        success = success && BatchManager.validateProject().isValid();
        //success = success && (BatchManager.testBuildJobs() > 0);
        success = success && BatchManager.Agent.checkAgentSettings();
        if (BatchManager.getBatchInfo().isValid()) {
            this.displayInfo("Validation successful!");
            this.displayInfo(BatchManager.getBatchInfo().getValidationErrorsText());
            this.displayInfo("Jobs are compiled from " + BatchManager.getNumberOfIDFs() + " models " +
                    (Project.getProjectType() == JEPlusProjectV2.ModelType.EPLUS ? 
                    "and " + BatchManager.getNumberOfWeathers() + " weather files" : "") + 
                    ", with the following");
            this.displayInfo(BatchManager.getBatchInfo().getParamChainsText(BatchManager.getProject()));
            //this.displayInfo("" + BatchManager.getNumberOfJobs() + " jobs have been identified.\n");
            long totalJobs = BatchManager.getBatchInfo().getTotalNumberOfJobs(BatchManager.getProject());
            String strTotalJobs = totalJobs >= 0 ? LargeIntFormatter.format(totalJobs) : "More than 9.2 x 10^18 ";
            this.displayInfo("" + strTotalJobs + " jobs have been identified in total.\n");
            this.displayInfo("Simulation work directories and results will be stored in: " + Project.resolveWorkDir());

            // Enable creating job index
            this.jMenuItemCreateIndex.setEnabled(true);
        }else {
            this.displayInfo("Validation finished with errors: ");
            this.displayInfo(BatchManager.getBatchInfo().getValidationErrorsText());
        }

        return success;
    }

    /**
     * Start full test run of this batch, including all combinations of IDF file
     * and parameter chain
     */
    public void startBatchRunTest () {
        // Update display
        if (OutputPanel == null) {
            OutputPanel = new EPlusTextPanelOld("Output", EPlusTextPanel.VIEWER_MODE);
            int tabn = TpnEditors.getTabCount();
            this.TpnEditors.insertTab("Executing batch ...", null, OutputPanel, "This is the execution log.", tabn);
            TpnEditors.setSelectedIndex(tabn);
        }else {
            TpnEditors.setSelectedComponent(OutputPanel);
        }
        // Create new batch with the first job of each idf/chain combination
        ActingManager = BatchManager;
        ActingManager.runTest();
        OutputPanel.appendContent("Batch test started. " + ActingManager.getNumberOfJobs() + " jobs to run ...\n");
    }

    /**
     * Start batch operation
     */
    public void startBatchRunAll () {
        // Update display
        if (OutputPanel == null) {
            OutputPanel = new EPlusTextPanelOld("Output", EPlusTextPanel.VIEWER_MODE);
            int tabn = TpnEditors.getTabCount();
            this.TpnEditors.insertTab("Executing batch ...", null, OutputPanel, "This is the execution log.", tabn);
            TpnEditors.setSelectedIndex(tabn);
        }else {
            TpnEditors.setSelectedComponent(OutputPanel);
        }
        // Check batch size and exec agent's capacity
        if (BatchManager.getBatchInfo().getTotalNumberOfJobs(BatchManager.getProject()) > BatchManager.getAgent().getQueueCapacity()) {
            // Project is too large
            StringBuilder buf = new StringBuilder ("<html><p>The estimated batch size (");
            buf.append(LargeIntFormatter.format(BatchManager.getBatchInfo().getTotalNumberOfJobs(BatchManager.getProject())));
            buf.append(") exceeds the capacity of ").append(BatchManager.getAgent().getAgentID()).append(" (");
            buf.append(LargeIntFormatter.format(BatchManager.getAgent().getQueueCapacity()));
            buf.append("). </p><p>A large batch may cause jEPlus to crash. Please choose a different agent, or use random sampling or optimisation.</p>");
            buf.append("<p>However, the estimation did not take into account of manually fixed parameter values.</p><p>Please choose Yes if you want to go ahead with the simulation. </p>");
            int res = JOptionPane.showConfirmDialog(this, buf.toString(), "Batch is too large", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                OutputPanel.appendContent("Batch cancelled.\n");
                return;
            }
        }
        // Build jobs
        OutputPanel.appendContent("Building jobs ... ");
        ActingManager = BatchManager;
        // Start job
        ActingManager.runAll();
        OutputPanel.appendContent("" + ActingManager.getJobQueue().size() + " jobs created.\n");
        OutputPanel.appendContent("Starting simulation ...\n");
    }

    /**
     * Start batch operation
     * @param file
     * @param dir
     * @return 
     */
    public boolean createJobList (String file, String dir) {
        if (this.validateBatchJobs()) {
            // Check batch size and exec agent's capacity
            if (BatchManager.getBatchInfo().getTotalNumberOfJobs(BatchManager.getProject()) > 100000 /* BatchManager.getAgent().getQueueCapacity() */) {
                // Project is too large
                StringBuilder buf = new StringBuilder ("<html><p>The estimated batch size (");
                buf.append(LargeIntFormatter.format(BatchManager.getBatchInfo().getTotalNumberOfJobs(BatchManager.getProject())));
                buf.append(") exceeds 10,000. </p><p>Are you sure to create a list of all jobs?</p>");
                buf.append("<p>Select Yes if you want to go ahead generate the job list file. </p>");
                int res = JOptionPane.showConfirmDialog(this, buf.toString(), "List is too long", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.NO_OPTION) {
                    OutputPanel.appendContent("Operation cancelled.\n");
                    return false;
                }
            }
            // Build jobs
            displayInfo("Building jobs ... ");
            BatchManager.buildJobs();
            displayInfo("" + BatchManager.getJobQueue().size() + " jobs created.\n");
            displayInfo("Saving job list in " + dir + file + "...\n");
            BatchManager.writeJobListFile(file, dir);
            displayInfo("Done.\n");
        }else {
            
        }
        return true;
    }

    /**
     * Start batch operation with a random sample of jobs
     * @param njobs The number of jobs to run
     * @param opt
     * @param randomsrc Random generator source. Null means no randomisation is required
     */
    public void startBatchRunSample (int njobs, EPlusBatch.SampleType opt, Random randomsrc) {
        // Update display
        if (OutputPanel == null) {
            OutputPanel = new EPlusTextPanelOld("Output", EPlusTextPanel.VIEWER_MODE);
            int tabn = TpnEditors.getTabCount();
            this.TpnEditors.insertTab("Executing batch ...", null, OutputPanel, "This is the execution log.", tabn);
            TpnEditors.setSelectedIndex(tabn);
        }else {
            TpnEditors.setSelectedComponent(OutputPanel);
        }
        if (opt == EPlusBatch.SampleType.SHUFFLE) {
            // Check batch size 
            if (BatchManager.getBatchInfo().getTotalNumberOfJobs(BatchManager.getProject()) > 10000000) { // larger than 10M cases
                // Project is too large
                StringBuilder buf = new StringBuilder ("<html><p>The estimated solution space size (");
                buf.append(LargeIntFormatter.format(BatchManager.getBatchInfo().getTotalNumberOfJobs(BatchManager.getProject())));
                buf.append(") is too big. </p><p>Creating a random sample may take a (very) long time. </p>");
                buf.append("<p>The Latin Hypercube Sampling method is more suitable.</p><p>Are you sure you want to continue? </p>");
                int res = JOptionPane.showConfirmDialog(this, buf.toString(), "Solution space is too large", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.NO_OPTION) {
                    OutputPanel.appendContent("Sample cancelled.\n");
                    return;
                }
            }
            OutputPanel.appendContent("Start sampling. Please waite ... \n");
        }
        ActingManager = BatchManager;
        ActingManager.runSample(opt, njobs, randomsrc);
        OutputPanel.appendContent("A " + opt + " sample of " + njobs + " jobs has started ...\n");
    }

    /**
     * Start batch operation with the list of jobs in the supplied file
     * @param file the file containing the job list
     */
    public void startBatchRunFile (String file) {
        // Update display
        if (OutputPanel == null) {
            OutputPanel = new EPlusTextPanelOld("Output", EPlusTextPanel.VIEWER_MODE);
            int tabn = TpnEditors.getTabCount();
            this.TpnEditors.insertTab("Executing batch ...", null, OutputPanel, "This is the execution log.", tabn);
            TpnEditors.setSelectedIndex(tabn);
        }else {
            TpnEditors.setSelectedComponent(OutputPanel);
        }
        ActingManager = BatchManager;
        ActingManager.runJobSet(EPlusBatch.JobStringType.FILE, file);
        OutputPanel.appendContent("" + ActingManager.getNumberOfJobs() + " jobs in " + file + " started ...\n");
    }

    /**
     * Convenient function for displaying a line of text in the Output panel
     * @param info Information to display
     */
    public void displayInfo (String info) {
        if (OutputPanel != null) OutputPanel.appendContent(info + "\n");
    }

    /**
     *
     * @param resultfn
     */
    public void displayResult (String resultfn) {
        if (ResultFilePanel == null)
            ResultFilePanel = new EPlusTextPanelOld("Simulation results", EPlusTextPanel.EDITOR_MODE);
        ResultFilePanel.viewFile(resultfn);
        int tabn = TpnEditors.getTabCount();
        this.TpnEditors.addTab("Results", ResultFilePanel);
        TpnEditors.setSelectedIndex(tabn);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btg = new javax.swing.ButtonGroup();
        rdoExportIndividual = new javax.swing.JRadioButton();
        rdoCombineResults = new javax.swing.JRadioButton();
        rdoTestFirstN = new javax.swing.JRadioButton();
        txtTestFirstN = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jMenuItemExportJson = new javax.swing.JMenuItem();
        jMenuItemCreateIndex = new javax.swing.JMenuItem();
        jMenuItemJESSClient = new javax.swing.JMenuItem();
        jMenuItemJEPlusEA = new javax.swing.JMenuItem();
        jMenuItemCreateJobList = new javax.swing.JMenuItem();
        jplParamTreeHolder = new javax.swing.JPanel();
        jplSettings = new javax.swing.JPanel();
        jplEPlusSettings = new jeplus.gui.JPanel_EPlusSettings();
        jSplitPane1 = new javax.swing.JSplitPane();
        tpnMain = new javax.swing.JTabbedPane();
        pnlProject = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jplProjectFilesPanelHolder = new javax.swing.JPanel();
        jPanel_EPlusProjectFiles2 = new jeplus.gui.JPanel_EPlusProjectFiles();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jplParamTableHolder = new javax.swing.JPanel();
        cboProjectType = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmdValidate = new javax.swing.JButton();
        pnlRvx = new javax.swing.JPanel();
        jplModelTest = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTestResultFolder = new javax.swing.JTextField();
        cmdSelectTestFolder = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jplRVX = new javax.swing.JPanel();
        pnlExecution = new javax.swing.JPanel();
        cboExecutionType = new javax.swing.JComboBox();
        jLabel27 = new javax.swing.JLabel();
        jplOptions = new javax.swing.JPanel();
        jplLocalControllerSettings = new jeplus.gui.JPanel_LocalControllerOptions();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        rdoTestChains = new javax.swing.JRadioButton();
        rdoTestRandomN = new javax.swing.JRadioButton();
        txtTestRandomN = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRandomSeed = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        rdoJobListFile = new javax.swing.JRadioButton();
        rdoAllJobs = new javax.swing.JRadioButton();
        txtJobListFile = new javax.swing.JTextField();
        cmdSelectJobListFile = new javax.swing.JButton();
        cmdEditJobListFile = new javax.swing.JButton();
        cboSampleOpt = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        cmdStart = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        chkPrepare = new javax.swing.JCheckBox();
        chkCollect = new javax.swing.JCheckBox();
        chkCreateList = new javax.swing.JCheckBox();
        chkRun = new javax.swing.JCheckBox();
        txtSaveList = new javax.swing.JTextField();
        cmdValidate1 = new javax.swing.JButton();
        pnlUtilities = new javax.swing.JPanel();
        TpnUtilities = new javax.swing.JTabbedPane();
        TpnEditors = new javax.swing.JTabbedPane();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuRecent = new javax.swing.JMenu();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemImportTable = new javax.swing.JMenuItem();
        jMenuItemExportTable = new javax.swing.JMenuItem();
        jMenuItemResetTree = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItemToRelative = new javax.swing.JMenuItem();
        jMenuItemToAbsolute = new javax.swing.JMenuItem();
        jMenuAction = new javax.swing.JMenu();
        jMenuItemValidate = new javax.swing.JMenuItem();
        jMenuItemSimulate = new javax.swing.JMenuItem();
        jMenuItemPostprocess = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItemMonitor = new javax.swing.JMenuItem();
        jMenuItemStop = new javax.swing.JMenuItem();
        jMenuViewResult = new javax.swing.JMenu();
        jMenuItemViewFolder = new javax.swing.JMenuItem();
        jMenuItemViewIndex = new javax.swing.JMenuItem();
        jMenuItemViewReports = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemConfig = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItemViewErr = new javax.swing.JMenuItem();
        jMenuItemViewLog = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItemVersionConverter = new javax.swing.JMenuItem();
        jMenuItemRunPython = new javax.swing.JMenuItem();
        jMenuItemRunReadVars = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemMemoryUsage = new javax.swing.JMenuItem();
        jMenuItemDefaultLaF = new javax.swing.JMenuItem();
        jMenuItemEditorTheme = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemUserGuide = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemAbout = new javax.swing.JMenuItem();

        rdoExportIndividual.setText("Export individual results as: [job-id]_");
        rdoExportIndividual.setEnabled(false);
        rdoExportIndividual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoExportIndividualActionPerformed(evt);
            }
        });

        rdoCombineResults.setSelected(true);
        rdoCombineResults.setText("Assemble results into CSV");
        rdoCombineResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoCombineResultsActionPerformed(evt);
            }
        });

        btg.add(rdoTestFirstN);
        rdoTestFirstN.setText("The first ");
        rdoTestFirstN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoTestFirstNActionPerformed(evt);
            }
        });

        txtTestFirstN.setText("10");
        txtTestFirstN.setEnabled(false);

        jLabel9.setText("jobs");

        jMenuItemExportJson.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/view_as_json.png"))); // NOI18N
        jMenuItemExportJson.setText("Export JSON project ...");
        jMenuItemExportJson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportJsonActionPerformed(evt);
            }
        });

        jMenuItemCreateIndex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_key.png"))); // NOI18N
        jMenuItemCreateIndex.setText("Create parameter indexes");
        jMenuItemCreateIndex.setToolTipText("Create index tables for the parameters in this project.");
        jMenuItemCreateIndex.setEnabled(false);
        jMenuItemCreateIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCreateIndexActionPerformed(evt);
            }
        });

        jMenuItemJESSClient.setText("Launch JESS Client");
        jMenuItemJESSClient.setEnabled(false);
        jMenuItemJESSClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemJESSClientActionPerformed(evt);
            }
        });

        jMenuItemJEPlusEA.setText("Launch jEPlus+EA");
        jMenuItemJEPlusEA.setEnabled(false);
        jMenuItemJEPlusEA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemJEPlusEAActionPerformed(evt);
            }
        });

        jMenuItemCreateJobList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_key.png"))); // NOI18N
        jMenuItemCreateJobList.setText("Create the full job list...");
        jMenuItemCreateJobList.setToolTipText("Create the list of jobs in the current project and save it in a CSV file. This list or part of it can be used as a job list file in the future.");
        jMenuItemCreateJobList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCreateJobListActionPerformed(evt);
            }
        });

        jplParamTreeHolder.setLayout(new java.awt.BorderLayout());

        jplSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Executables"));
        jplSettings.setLayout(new java.awt.BorderLayout());
        jplSettings.add(jplEPlusSettings, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1200, 740));
        setPreferredSize(new java.awt.Dimension(1366, 855));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(700);
        jSplitPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSplitPane1.setLastDividerLocation(700);
        jSplitPane1.setOpaque(false);

        tpnMain.setToolTipText("Project specifications / Execution settings / optional Post process");
        tpnMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tpnMainStateChanged(evt);
            }
        });

        pnlProject.setPreferredSize(new java.awt.Dimension(450, 688));

        javax.swing.GroupLayout jplProjectFilesPanelHolderLayout = new javax.swing.GroupLayout(jplProjectFilesPanelHolder);
        jplProjectFilesPanelHolder.setLayout(jplProjectFilesPanelHolderLayout);
        jplProjectFilesPanelHolderLayout.setHorizontalGroup(
            jplProjectFilesPanelHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_EPlusProjectFiles2, javax.swing.GroupLayout.DEFAULT_SIZE, 694, Short.MAX_VALUE)
        );
        jplProjectFilesPanelHolderLayout.setVerticalGroup(
            jplProjectFilesPanelHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_EPlusProjectFiles2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jplParamTableHolder.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("Parameter Table", jplParamTableHolder);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jplProjectFilesPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jplProjectFilesPanelHolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        cboProjectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EnergyPlus Project", "TRNSYS Project", "INSEL Project" }));
        cboProjectType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboProjectTypeActionPerformed(evt);
            }
        });

        jLabel2.setText("Select Project type:");

        cmdValidate.setText("Validate project");
        cmdValidate.setToolTipText("Validate the configuration");
        cmdValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdValidateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlProjectLayout = new javax.swing.GroupLayout(pnlProject);
        pnlProject.setLayout(pnlProjectLayout);
        pnlProjectLayout.setHorizontalGroup(
            pnlProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlProjectLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboProjectType, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmdValidate)
                .addContainerGap())
        );
        pnlProjectLayout.setVerticalGroup(
            pnlProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboProjectType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cmdValidate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tpnMain.addTab("Project Params", pnlProject);

        jplModelTest.setBorder(javax.swing.BorderFactory.createTitledBorder("Test Simulation Model"));
        jplModelTest.setEnabled(false);

        jLabel1.setText("<html>Please go to the next tab (<b>Execution</b>) to perform a test run of one or more jobs. Then select below the result folder of one of the jobs.These information may be useful for defining RVX items.(<b>to be implemented</b>)</html>");
        jLabel1.setEnabled(false);

        txtTestResultFolder.setText("N/A");
        txtTestResultFolder.setToolTipText("The output folder of test simulation");
        txtTestResultFolder.setEnabled(false);

        cmdSelectTestFolder.setText("...");
        cmdSelectTestFolder.setToolTipText("Select the output folder of a test simulation");
        cmdSelectTestFolder.setEnabled(false);
        cmdSelectTestFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectTestFolderActionPerformed(evt);
            }
        });

        jLabel3.setText("Select the result folder: ");
        jLabel3.setEnabled(false);

        javax.swing.GroupLayout jplModelTestLayout = new javax.swing.GroupLayout(jplModelTest);
        jplModelTest.setLayout(jplModelTestLayout);
        jplModelTestLayout.setHorizontalGroup(
            jplModelTestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplModelTestLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jplModelTestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jplModelTestLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTestResultFolder, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSelectTestFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jplModelTestLayout.setVerticalGroup(
            jplModelTestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplModelTestLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jplModelTestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdSelectTestFolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTestResultFolder)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jplRVX.setBorder(javax.swing.BorderFactory.createTitledBorder("Result Extraction (RVX)"));
        jplRVX.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pnlRvxLayout = new javax.swing.GroupLayout(pnlRvx);
        pnlRvx.setLayout(pnlRvxLayout);
        pnlRvxLayout.setHorizontalGroup(
            pnlRvxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRvxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRvxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jplModelTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jplRVX, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlRvxLayout.setVerticalGroup(
            pnlRvxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRvxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jplModelTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jplRVX, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addContainerGap())
        );

        tpnMain.addTab("Result Collection", pnlRvx);

        pnlExecution.setPreferredSize(new java.awt.Dimension(500, 688));

        cboExecutionType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Local batch controller", "Offline PBS script generator", "Local PBS controller", "JEPlusPlus Job Server (PBS only)", "JEPlusPlus Job Server (Windows only)", "JEPlusPlus Job Server" }));
        cboExecutionType.setToolTipText("Select execution type here");
        cboExecutionType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboExecutionTypeActionPerformed(evt);
            }
        });

        jLabel27.setText("Select execution controller: ");

        jplOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        jplOptions.setLayout(new java.awt.BorderLayout());
        jplOptions.add(jplLocalControllerSettings, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Run"));

        btg.add(rdoTestChains);
        rdoTestChains.setSelected(true);
        rdoTestChains.setText("The first job of each parameter chain (see validation report for parameter chains)");
        rdoTestChains.setToolTipText("This option is for testing jobs only. The job IDs it uses may be different to other modes.");
        rdoTestChains.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoTestChainsActionPerformed(evt);
            }
        });

        btg.add(rdoTestRandomN);
        rdoTestRandomN.setText("A random sample of ");
        rdoTestRandomN.setToolTipText("Run a random sample of the whole project.");
        rdoTestRandomN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoTestRandomNActionPerformed(evt);
            }
        });

        txtTestRandomN.setColumns(4);
        txtTestRandomN.setText("1000");
        txtTestRandomN.setToolTipText("Sample size");
        txtTestRandomN.setEnabled(false);

        jLabel5.setText("cases using:");

        txtRandomSeed.setColumns(4);
        txtRandomSeed.setText("12345");
        txtRandomSeed.setToolTipText("Set a random seed to fix the job sequence. If a negative value is specified, the current time is used as the seed.");
        txtRandomSeed.setEnabled(false);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText(" with Seed:");

        btg.add(rdoJobListFile);
        rdoJobListFile.setText("List of cases in: ");
        rdoJobListFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoJobListFileActionPerformed(evt);
            }
        });

        btg.add(rdoAllJobs);
        rdoAllJobs.setText("All cases of the project");
        rdoAllJobs.setToolTipText("This option will start ALL jobs in the project.");
        rdoAllJobs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoAllJobsActionPerformed(evt);
            }
        });

        txtJobListFile.setText("jobs.csv");
        txtJobListFile.setToolTipText("For the format of a job list file, please refer to the users manual.");
        txtJobListFile.setEnabled(false);

        cmdSelectJobListFile.setText("...");
        cmdSelectJobListFile.setToolTipText("Select a job list file");
        cmdSelectJobListFile.setEnabled(false);
        cmdSelectJobListFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectJobListFileActionPerformed(evt);
            }
        });

        cmdEditJobListFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_edit.png"))); // NOI18N
        cmdEditJobListFile.setToolTipText("Edit the contents of the file");
        cmdEditJobListFile.setEnabled(false);
        cmdEditJobListFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditJobListFileActionPerformed(evt);
            }
        });

        cboSampleOpt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboSampleOpt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSampleOptActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdoTestChains, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addComponent(rdoAllJobs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdoTestRandomN)
                            .addComponent(rdoJobListFile))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(txtJobListFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSelectJobListFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdEditJobListFile, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(txtTestRandomN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboSampleOpt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRandomSeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(6, 6, 6))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(rdoTestChains)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoTestRandomN)
                    .addComponent(txtTestRandomN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRandomSeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(cboSampleOpt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdoJobListFile)
                        .addComponent(txtJobListFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmdSelectJobListFile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdEditJobListFile))
                .addGap(7, 7, 7)
                .addComponent(rdoAllJobs)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Execution"));

        cmdStart.setText("Run Batch");
        cmdStart.setToolTipText("Start batch simulation");
        cmdStart.setActionCommand("start");
        cmdStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdStartActionPerformed(evt);
            }
        });

        chkPrepare.setSelected(true);
        chkPrepare.setText("Prepare case folders and write simulation input files");
        chkPrepare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrepareActionPerformed(evt);
            }
        });

        chkCollect.setSelected(true);
        chkCollect.setText("Collect simulation results and run post-processing scripts");
        chkCollect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCollectActionPerformed(evt);
            }
        });

        chkCreateList.setSelected(true);
        chkCreateList.setText("Create case indexes and save the list to: ");
        chkCreateList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCreateListActionPerformed(evt);
            }
        });

        chkRun.setSelected(true);
        chkRun.setText("Run simulations");
        chkRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRunActionPerformed(evt);
            }
        });

        txtSaveList.setText("joblist.csv");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(chkCreateList)
                        .addGap(18, 18, 18)
                        .addComponent(txtSaveList, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkPrepare)
                    .addComponent(chkRun)
                    .addComponent(chkCollect))
                .addContainerGap(168, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCreateList)
                    .addComponent(txtSaveList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkPrepare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkCollect)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cmdValidate1.setText("Validate project");
        cmdValidate1.setToolTipText("Validate the configuration");
        cmdValidate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdValidate1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cmdValidate1)
                        .addGap(18, 18, 18)
                        .addComponent(cmdStart)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdStart)
                    .addComponent(cmdValidate1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlExecutionLayout = new javax.swing.GroupLayout(pnlExecution);
        pnlExecution.setLayout(pnlExecutionLayout);
        pnlExecutionLayout.setHorizontalGroup(
            pnlExecutionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExecutionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlExecutionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlExecutionLayout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboExecutionType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jplOptions, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlExecutionLayout.setVerticalGroup(
            pnlExecutionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlExecutionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlExecutionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboExecutionType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jplOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tpnMain.addTab("Execution", pnlExecution);

        javax.swing.GroupLayout pnlUtilitiesLayout = new javax.swing.GroupLayout(pnlUtilities);
        pnlUtilities.setLayout(pnlUtilitiesLayout);
        pnlUtilitiesLayout.setHorizontalGroup(
            pnlUtilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUtilitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TpnUtilities)
                .addContainerGap())
        );
        pnlUtilitiesLayout.setVerticalGroup(
            pnlUtilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUtilitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TpnUtilities, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                .addContainerGap())
        );

        tpnMain.addTab("Utilities", pnlUtilities);

        jSplitPane1.setLeftComponent(tpnMain);
        tpnMain.getAccessibleContext().setAccessibleName("Project Params");

        TpnEditors.setMaximumSize(new java.awt.Dimension(900, 1200));
        TpnEditors.setPreferredSize(null);
        jSplitPane1.setRightComponent(TpnEditors);
        TpnEditors.getAccessibleContext().setAccessibleName("Information");

        jMenuBarMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 0));

        jMenuFile.setText("File");

        jMenuItemNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_add.png"))); // NOI18N
        jMenuItemNew.setText("New");
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);

        jMenuItemOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/folder_explore.png"))); // NOI18N
        jMenuItemOpen.setText("Open ...");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuRecent.setText("Open recent");
        jMenuFile.add(jMenuRecent);

        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/save1.png"))); // NOI18N
        jMenuItemSave.setText("Save ");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/disk.png"))); // NOI18N
        jMenuItemSaveAs.setText("Save as ... ");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator3);

        jMenuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/cross.png"))); // NOI18N
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBarMain.add(jMenuFile);

        jMenuEdit.setText("Edit");

        jMenuItemImportTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_get.png"))); // NOI18N
        jMenuItemImportTable.setText("Import parameters from CSV ...");
        jMenuItemImportTable.setToolTipText("Import parameter definitions from a CSV table.");
        jMenuItemImportTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportTableActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemImportTable);

        jMenuItemExportTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_go.png"))); // NOI18N
        jMenuItemExportTable.setText("Export parameters to CSV ...");
        jMenuItemExportTable.setToolTipText("Export parameters in this project to a CSV table");
        jMenuItemExportTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportTableActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemExportTable);

        jMenuItemResetTree.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/cross.png"))); // NOI18N
        jMenuItemResetTree.setText("Reset parameter tree");
        jMenuItemResetTree.setToolTipText("Clear the parameter tree");
        jMenuItemResetTree.setEnabled(false);
        jMenuItemResetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemResetTreeActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemResetTree);
        jMenuEdit.add(jSeparator7);

        jMenuItemToRelative.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/document-save.png"))); // NOI18N
        jMenuItemToRelative.setText("Change all paths to relative form");
        jMenuItemToRelative.setToolTipText("Change paths in the project to a relative form relative to the location of the project file.");
        jMenuItemToRelative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemToRelativeActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemToRelative);

        jMenuItemToAbsolute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/document-save-as.png"))); // NOI18N
        jMenuItemToAbsolute.setText("Change all paths to absolute form");
        jMenuItemToAbsolute.setToolTipText("Change the path to absolute form.");
        jMenuItemToAbsolute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemToAbsoluteActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemToAbsolute);

        jMenuBarMain.add(jMenuEdit);

        jMenuAction.setText("Action");

        jMenuItemValidate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/check.png"))); // NOI18N
        jMenuItemValidate.setText("Validate jobs");
        jMenuItemValidate.setToolTipText("Validate the current project and count jobs.");
        jMenuItemValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemValidateActionPerformed(evt);
            }
        });
        jMenuAction.add(jMenuItemValidate);

        jMenuItemSimulate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/lightening2.png"))); // NOI18N
        jMenuItemSimulate.setText("Simulate ");
        jMenuItemSimulate.setToolTipText("Start simulation.");
        jMenuItemSimulate.setEnabled(false);
        jMenuItemSimulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSimulateActionPerformed(evt);
            }
        });
        jMenuAction.add(jMenuItemSimulate);

        jMenuItemPostprocess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/chart.png"))); // NOI18N
        jMenuItemPostprocess.setText("Post-process");
        jMenuItemPostprocess.setToolTipText("Go to the post process / utilities tab");
        jMenuItemPostprocess.setEnabled(false);
        jMenuItemPostprocess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPostprocessActionPerformed1(evt);
            }
        });
        jMenuAction.add(jMenuItemPostprocess);
        jMenuAction.add(jSeparator6);

        jMenuItemMonitor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/tool.png"))); // NOI18N
        jMenuItemMonitor.setText("Show Monitor");
        jMenuItemMonitor.setToolTipText("Show simulation monitor.");
        jMenuItemMonitor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMonitorActionPerformed(evt);
            }
        });
        jMenuAction.add(jMenuItemMonitor);

        jMenuItemStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/cross.png"))); // NOI18N
        jMenuItemStop.setText("Stop Simulation");
        jMenuItemStop.setToolTipText("Cancel the current simulation jobs.");
        jMenuItemStop.setEnabled(false);
        jMenuItemStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStopActionPerformed(evt);
            }
        });
        jMenuAction.add(jMenuItemStop);

        jMenuViewResult.setText("View results");
        jMenuViewResult.setToolTipText("View result folder and files");
        jMenuViewResult.setEnabled(false);

        jMenuItemViewFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/folder_explore.png"))); // NOI18N
        jMenuItemViewFolder.setText("Go to output folder");
        jMenuItemViewFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemViewFolderActionPerformed(evt);
            }
        });
        jMenuViewResult.add(jMenuItemViewFolder);

        jMenuItemViewIndex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_magnify.png"))); // NOI18N
        jMenuItemViewIndex.setText("View Job Index (JobIndex.csv)");
        jMenuItemViewIndex.setActionCommand("View Job Index (SimJobIndex.csv)");
        jMenuItemViewIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemViewIndexActionPerformed(evt);
            }
        });
        jMenuViewResult.add(jMenuItemViewIndex);

        jMenuItemViewReports.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_magnify.png"))); // NOI18N
        jMenuItemViewReports.setText("View Simulation Reports (RunTimes.csv)");
        jMenuItemViewReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemViewReportsActionPerformed(evt);
            }
        });
        jMenuViewResult.add(jMenuItemViewReports);

        jMenuAction.add(jMenuViewResult);
        jMenuAction.add(jSeparator9);

        jMenuBarMain.add(jMenuAction);

        jMenuTools.setText("Tools ");

        jMenuItemConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/hammer_screwdriver.png"))); // NOI18N
        jMenuItemConfig.setText("Configure External Programs ...");
        jMenuItemConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConfigActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemConfig);
        jMenuTools.add(jSeparator4);

        jMenuItemViewErr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/bug.png"))); // NOI18N
        jMenuItemViewErr.setText("View jEPlus error log (jeplus.err)");
        jMenuItemViewErr.setToolTipText("Open jeplus.err file to check errors.");
        jMenuItemViewErr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemViewErrActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemViewErr);

        jMenuItemViewLog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/bug.png"))); // NOI18N
        jMenuItemViewLog.setText("View E+ console log (console.log)");
        jMenuItemViewLog.setEnabled(false);
        jMenuItemViewLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemViewLogActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemViewLog);
        jMenuTools.add(jSeparator8);

        jMenuItemVersionConverter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/go-up.png"))); // NOI18N
        jMenuItemVersionConverter.setText("IDF Version Converter ...");
        jMenuItemVersionConverter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemVersionConverterActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemVersionConverter);

        jMenuItemRunPython.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/16px-Icon-Python.png"))); // NOI18N
        jMenuItemRunPython.setText("Run Python script ...");
        jMenuItemRunPython.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRunPythonActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemRunPython);

        jMenuItemRunReadVars.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/edit-clear.png"))); // NOI18N
        jMenuItemRunReadVars.setText("Run ReadVars ...");
        jMenuItemRunReadVars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRunReadVarsActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemRunReadVars);
        jMenuTools.add(jSeparator5);

        jMenuItemMemoryUsage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/ddr_memory.png"))); // NOI18N
        jMenuItemMemoryUsage.setText("Memory Usage");
        jMenuItemMemoryUsage.setToolTipText("Show memory usage dialog to check available resources");
        jMenuItemMemoryUsage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMemoryUsageActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemMemoryUsage);

        jMenuItemDefaultLaF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/preferences-desktop-wallpaper.png"))); // NOI18N
        jMenuItemDefaultLaF.setText("Switch to defaul Look and Feel (Metal)");
        jMenuItemDefaultLaF.setToolTipText("Change GUI style");
        jMenuItemDefaultLaF.setActionCommand("default");
        jMenuItemDefaultLaF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDefaultLaFActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemDefaultLaF);

        jMenuItemEditorTheme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_gear.png"))); // NOI18N
        jMenuItemEditorTheme.setText("Edit syntax highlighting theme");
        jMenuItemEditorTheme.setToolTipText("The syntax highlighting style can be edited to your preference.");
        jMenuItemEditorTheme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditorThemeActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemEditorTheme);

        jMenuBarMain.add(jMenuTools);

        jMenuHelp.setText("Help");

        jMenuItemUserGuide.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemUserGuide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/bulb.png"))); // NOI18N
        jMenuItemUserGuide.setText("User's Guide Online");
        jMenuItemUserGuide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUserGuideActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemUserGuide);
        jMenuHelp.add(jSeparator1);

        jMenuItemAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/mail.png"))); // NOI18N
        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBarMain.add(jMenuHelp);

        setJMenuBar(jMenuBarMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jSeparator2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jSplitPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartActionPerformed
        if (cmdStart.getActionCommand().equals("start")) {
            // Disable button during the preparation period
            cmdStart.setEnabled(false);
            // Check if files need to be saved first
            for (int i=1; i<TpnEditors.getTabCount(); i++) {
                try {
                    EPlusEditorPanel etp = (EPlusEditorPanel)TpnEditors.getComponentAt(i);
                    if (etp.isContentChanged()) {
                        TpnEditors.setSelectedIndex(i);
                        int ans = JOptionPane.showConfirmDialog(this,
                            "The contents of " + etp.getTitle() + " has been modified. Would you like to save the changes first?",
                            "Confirm saving ...",
                            JOptionPane.YES_NO_OPTION);
                        if (ans == JOptionPane.YES_OPTION) {
                            etp.saveFileContent();
                            etp.setContentChanged(false);
                        }
                    }
                }catch (ClassCastException cce) {

                }
            }

            boolean OkToStart = true;
            // Reload RVX if depends on external file
            if (Project.getRVIFile() != null) {
                try {
                    Project.setRvx(RVX.getRVX(Project.getRVIFile(), Project.getBaseDir()));
                }catch (IOException ioe) {
                    logger.error("Error reloading RVX from " + Project.getRVIFile(), ioe);
                    Project.setRvx(new RVX());
                }
            }            
            // Validate jobs if the execution agent requires it
            if (ExecAgents.get(this.cboExecutionType.getSelectedIndex()).isValidationRequired()) {
                // Validate the jobs before start
                if (! validateBatchJobs()) {
                    if (BatchManager.getBatchInfo().ValidationSuccessful)
                        JOptionPane.showMessageDialog(this,
                                "General parameter tree composition, parameter names and search strings are OK.\n" +
                                "Compilation of jobs failed! Please check the alternative values of each parameter.",
                                "Validation failed", JOptionPane.INFORMATION_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(this, BatchManager.getBatchInfo().getValidationErrorsText(),
                                "Validation failed", JOptionPane.INFORMATION_MESSAGE);
                    OkToStart = false;
                }
            }else {
                BatchManager = new EPlusBatch(this, Project);
                BatchManager.setAgent(this.ExecAgents.get(this.cboExecutionType.getSelectedIndex()));
            }

            // Clear output folder
            // This is for diagnostic of file locking issues. It is not necessary and unsafe to delete the whole output folder
            /*
            if (OkToStart) {
                File workdir = new File (Project.resolveWorkDir());
                if (workdir.exists() && workdir.isDirectory() && workdir.listFiles().length > 0) {
                    int rep = JOptionPane.showConfirmDialog(this,
                            "The output folder " + workdir.getAbsolutePath() + " is not empty.\n" +
                            "Do you want to delete its contents before running new simulations?\n" +
                                    "Warning: All files and subfolders in the folder will be deleted if you choose YES",
                            "Output folder not empty", JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (rep) {
                        case JOptionPane.YES_OPTION:
                            try {
                                FileUtils.deleteDirectory(workdir);
                            }catch (IOException ioe) {
                                JOptionPane.showMessageDialog(this, 
                                        "Failed to delete some of the files. Please make sure these files are not open in \n" +
                                                "programs such as Excel or Windows Explorer.\n" + ioe.getMessage(), 
                                        "Cannot clear the output folder", 
                                        JOptionPane.OK_OPTION);
                                OkToStart = false;
                            }
                            break;
                        case JOptionPane.NO_OPTION: 
                            break;
                        case JOptionPane.CANCEL_OPTION:
                        default:
                            OkToStart = false;
                    }
                }
            }
            */
            // Run simulations
            if (OkToStart) {
                // Start jobs accordingly
                if (BatchManager.getAgent().isValidationRequired()) {
                    // do something?
                }
                if (Project.getExecSettings().getSubSet() == ExecutionOptions.ALL) {
                    startBatchRunAll ();
                }else {
                    switch (Project.getExecSettings().getSubSet()) {
                        case ExecutionOptions.CHAINS: 
                            startBatchRunTest ();
                            break;
                        case ExecutionOptions.RANDOM:
                            // Read again random seed
                            long seed;
                            try {
                                seed = Long.parseLong(txtRandomSeed.getText());
                                if (seed < 0) seed = new Date().getTime();
                                txtRandomSeed.setForeground(Color.black);
                            }catch (NumberFormatException nfx) {
                                seed = new Date().getTime();
                                txtRandomSeed.setForeground(Color.red);
                            }
                            Project.getExecSettings().setRandomSeed(seed);

                            startBatchRunSample (Project.getExecSettings().getNumberOfJobs(), 
                                    Project.getExecSettings().getSampleOpt(),
                                    RandomSource.getRandomGenerator(Project.getExecSettings().getRandomSeed()));
                            break;
                        case ExecutionOptions.FILE:
                            startBatchRunFile (Project.getExecSettings().getJobListFile());
                    }
                }
            }
            // Enable button
            cmdStart.setEnabled(true);
        }else {
            BatchManager.getAgent().setStopAgent(true);
        }
    }//GEN-LAST:event_cmdStartActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    jMenuItemExitActionPerformed(null);
}//GEN-LAST:event_formWindowClosing

private void cmdValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdValidateActionPerformed
    if (validateBatchJobs()) {
        JOptionPane.showMessageDialog(this, "Validation successful! " +
                LargeIntFormatter.format(BatchManager.getBatchInfo().getTotalNumberOfJobs(BatchManager.getProject())) + " jobs identified.\n\n" +
                BatchManager.getBatchInfo().getValidationErrorsText() + "\n" +
                "Please note that the search strings in the template file(s) have not been verified. You should also check any external\n" +
                "referencesin the template file(s), e.g. the absolute path names in '##fileprefix' or '##include'. It is also a good \n" +
                "idea to test a few jobs before running the whole batch.",
                "Validation result", JOptionPane.INFORMATION_MESSAGE);
    }else {
        if (BatchManager.getBatchInfo().ValidationSuccessful)
            JOptionPane.showMessageDialog(this,
                    "General parameter tree composition, parameter names and search strings are OK.\n" +
                    "Compiling JobGroup failed! Please check the alternative values of each parameter.",
                    "Validation result", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, BatchManager.getBatchInfo().getValidationErrorsText(),
                    "Validation result", JOptionPane.INFORMATION_MESSAGE);
    }
}//GEN-LAST:event_cmdValidateActionPerformed

private void cboExecutionTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboExecutionTypeActionPerformed
    int opt = this.cboExecutionType.getSelectedIndex();
    this.setExecType(opt);
}//GEN-LAST:event_cboExecutionTypeActionPerformed

private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
    showSplash(this);
}//GEN-LAST:event_jMenuItemAboutActionPerformed

private void jMenuItemValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemValidateActionPerformed
    this.cmdValidateActionPerformed(evt);
}//GEN-LAST:event_jMenuItemValidateActionPerformed

private void jMenuItemSimulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSimulateActionPerformed
    this.cmdStartActionPerformed(evt);
}//GEN-LAST:event_jMenuItemSimulateActionPerformed

private void jMenuItemPostprocessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPostprocessActionPerformed
    
}//GEN-LAST:event_jMenuItemPostprocessActionPerformed

private void rdoCombineResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoCombineResultsActionPerformed
}//GEN-LAST:event_rdoCombineResultsActionPerformed

private void rdoExportIndividualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoExportIndividualActionPerformed
    
}//GEN-LAST:event_rdoExportIndividualActionPerformed

private void jMenuItemUserGuideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUserGuideActionPerformed
    if (Desktop.isDesktopSupported()) {
        Desktop desktop = Desktop.getDesktop();
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(JEPlusVersion.UsersGuide));
            } catch (URISyntaxException | IOException ex1) {
                JOptionPane.showMessageDialog(this, JEPlusVersion.UsersGuide + " is not accessible. Please try locate the page manually on the jEPlus website.");
            }
        }else {
            JOptionPane.showMessageDialog(this, "Cannot open browser. Please locate the User Guide manually on the jEPlus website.");
        }
    }
}//GEN-LAST:event_jMenuItemUserGuideActionPerformed

private void jMenuItemPostprocessActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPostprocessActionPerformed1
    this.tpnMain.setSelectedIndex(POST_PROC_TAB);
}//GEN-LAST:event_jMenuItemPostprocessActionPerformed1

private void rdoTestChainsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoTestChainsActionPerformed
    if (rdoTestChains.isSelected()) {
        Project.getExecSettings().setSubSet(ExecutionOptions.CHAINS);
        this.txtTestFirstN.setEnabled(false);
        this.txtTestRandomN.setEnabled(false);
        this.txtRandomSeed.setEnabled(false);
        this.txtJobListFile.setEnabled(false);
        this.cmdSelectJobListFile.setEnabled(false);
        this.cmdEditJobListFile.setEnabled(false);
    }
}//GEN-LAST:event_rdoTestChainsActionPerformed

private void rdoTestRandomNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoTestRandomNActionPerformed
    if (rdoTestRandomN.isSelected()) {
        Project.getExecSettings().setSubSet(ExecutionOptions.RANDOM);
        this.txtTestFirstN.setEnabled(false);
        this.txtTestRandomN.setEnabled(true);
        this.txtRandomSeed.setEnabled(true);
        this.txtJobListFile.setEnabled(false);
        this.cmdSelectJobListFile.setEnabled(false);
        this.cmdEditJobListFile.setEnabled(false);
    }

}//GEN-LAST:event_rdoTestRandomNActionPerformed

private void rdoTestFirstNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoTestFirstNActionPerformed
    if (rdoTestFirstN.isSelected()) {
        Project.getExecSettings().setSubSet(ExecutionOptions.CHAINS);
        this.txtTestFirstN.setEnabled(true);
        this.txtTestRandomN.setEnabled(false);
        this.txtRandomSeed.setEnabled(false);
        this.txtJobListFile.setEnabled(false);
        this.cmdSelectJobListFile.setEnabled(false);
        this.cmdEditJobListFile.setEnabled(false);
    }
}//GEN-LAST:event_rdoTestFirstNActionPerformed

private void jMenuItemStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStopActionPerformed
    BatchManager.getAgent().setStopAgent(true);
}//GEN-LAST:event_jMenuItemStopActionPerformed

private void jMenuItemMemoryUsageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMemoryUsageActionPerformed
        JDialog dialog = new JDialog ((JFrame)null, "Memory Usage");
        JPanel_MemoryUsage panel = new JPanel_MemoryUsage ("./");
        dialog.getContentPane().add(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(550, 350);
        dialog.addWindowListener(panel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
}//GEN-LAST:event_jMenuItemMemoryUsageActionPerformed

private void jMenuItemViewErrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemViewErrActionPerformed
    this.openViewTabForFile("jeplus.err");
}//GEN-LAST:event_jMenuItemViewErrActionPerformed

private void jMenuItemViewLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemViewLogActionPerformed
    this.openViewTabForFile("console.log");
}//GEN-LAST:event_jMenuItemViewLogActionPerformed

private void jMenuItemViewReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemViewReportsActionPerformed
    this.openViewTabForFile(BatchManager.getResolvedEnv().getParentDir() + "RunTimes.csv");
}//GEN-LAST:event_jMenuItemViewReportsActionPerformed

    private void jMenuItemViewIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemViewIndexActionPerformed
        this.openViewTabForFile(BatchManager.getResolvedEnv().getParentDir() + "SimJobIndex.csv");
    }//GEN-LAST:event_jMenuItemViewIndexActionPerformed

    private void jMenuItemImportTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemImportTableActionPerformed
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.CSV));
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(DefaultDir);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            // import table
            if (! Project.importParameterTableFile(file)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Import parameter tree from CSV table in " + file.getAbsolutePath() + "failed. Please check the format of the table file.",
                    "Error",
                    JOptionPane.CLOSED_OPTION);
            }
            this.initProjectSection();
        } else {

        }
        fc.setFileFilter(null);
    }//GEN-LAST:event_jMenuItemImportTableActionPerformed

    private void cmdSelectJobListFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectJobListFileActionPerformed
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.LIST));
        // fc.setCurrentDirectory(new File(Project.getBaseDir()));
        fc.setSelectedFile(new File(""));
        fc.setMultiSelectionEnabled(false);
        String listfile = RelativeDirUtil.checkAbsolutePath(txtJobListFile.getText(), Project.getBaseDir());
        fc.setCurrentDirectory(new File (listfile).getParentFile());
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtJobListFile.setText(fc.getSelectedFile().getPath());
        }
        fc.resetChoosableFileFilters();
    }//GEN-LAST:event_cmdSelectJobListFileActionPerformed

    private void cmdEditJobListFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditJobListFileActionPerformed
        String fn = txtJobListFile.getText();
        String templfn = RelativeDirUtil.checkAbsolutePath(fn, Project.getBaseDir());
        File ftmpl = new File (templfn);
        if (! ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                this,
                "<html><p><center>The specified job list file " + templfn + " does not exist." +
                "Do you want to create this file? </p>",
                "File does not exist",
                JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                try {
                    ftmpl.createNewFile();
                } catch (IOException ex) {
                    logger.error("Error create file " + ftmpl.getAbsolutePath(), ex);
                }
            }else {
                return;
            }
        }
        int idx = TpnEditors.indexOfTab(fn);
        if (idx >= 0) {
            TpnEditors.setSelectedIndex(idx);
        }else {
//            EPlusTextPanel JobFilePanel = new EPlusTextPanel(
//                    TpnEditors,
//                    fn,
//                    EPlusTextPanel.EDITOR_MODE,
//                    EPlusConfig.getFileFilter(EPlusConfig.LIST),
//                    templfn,
//                    null);
            EPlusEditorPanel JobFilePanel = new EPlusEditorPanel(
                    TpnEditors,
                    fn,
                    templfn,
                    EPlusEditorPanel.FileType.PLAIN,
                    null);
            int ti = TpnEditors.getTabCount();
            this.TpnEditors.addTab(txtJobListFile.getText(), JobFilePanel);
            JobFilePanel.setTabId(ti);
            TpnEditors.setSelectedIndex(ti);
            TpnEditors.setTabComponentAt(ti, new ButtonTabComponent (TpnEditors, JobFilePanel));
            TpnEditors.setToolTipTextAt(ti, templfn);
        }

    }//GEN-LAST:event_cmdEditJobListFileActionPerformed

    private void rdoJobListFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoJobListFileActionPerformed
        if (rdoJobListFile.isSelected()) {
            Project.getExecSettings().setSubSet(ExecutionOptions.FILE);
            this.txtTestFirstN.setEnabled(false);
            this.txtTestRandomN.setEnabled(false);
            this.txtRandomSeed.setEnabled(false);
            this.txtJobListFile.setEnabled(true);
            this.cmdSelectJobListFile.setEnabled(true);
            this.cmdEditJobListFile.setEnabled(true);
        }
    }//GEN-LAST:event_rdoJobListFileActionPerformed

    private void rdoAllJobsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoAllJobsActionPerformed
        if (rdoAllJobs.isSelected()) {
            Project.getExecSettings().setSubSet(ExecutionOptions.ALL);
            this.txtTestFirstN.setEnabled(false);
            this.txtTestRandomN.setEnabled(false);
            this.txtRandomSeed.setEnabled(false);
            this.txtJobListFile.setEnabled(false);
            this.cmdSelectJobListFile.setEnabled(false);
            this.cmdEditJobListFile.setEnabled(false);
        }
    }//GEN-LAST:event_rdoAllJobsActionPerformed

    private void jMenuItemResetTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemResetTreeActionPerformed
        int resp = JOptionPane.showConfirmDialog(this, "Are you sure to deleted the whole parameter tree?", "Please confirm", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            Project.getParamTree().removeAllChildren();
            Project.getParamTree().setUserObject(new ParameterItemV2(0));
            this.initProjectSection();
        }
    }//GEN-LAST:event_jMenuItemResetTreeActionPerformed

    private void cboProjectTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboProjectTypeActionPerformed
        this.setProjectType ((JEPlusProjectV2.ModelType)cboProjectType.getSelectedItem());
    }//GEN-LAST:event_cboProjectTypeActionPerformed

    private void jMenuItemMonitorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMonitorActionPerformed
        if (BatchManager != null && BatchManager.getAgent() != null) {
            BatchManager.getAgent().showAgentMonitorGUI(true, false);
        }
    }//GEN-LAST:event_jMenuItemMonitorActionPerformed

    private void tpnMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tpnMainStateChanged
        // If it is post-process tab, update its content
        if (tpnMain.getSelectedComponent() == this.pnlUtilities) {
//            this.jplProgConfPanel.initSettings();
            this.jplReadVarsPanel.initContents();
            this.jplIDFConvPanel.setProject(Project);
            this.jplPythonPanel.setCurrentWorkDir(Project.resolveWorkDir());
        }else if (tpnMain.getSelectedComponent() == this.pnlExecution) {
            
        }
    }//GEN-LAST:event_tpnMainStateChanged

    private void jMenuItemDefaultLaFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDefaultLaFActionPerformed
        try {
            switch (jMenuItemDefaultLaF.getActionCommand()) {
                case "default":
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    SwingUtilities.updateComponentTreeUI(this);
                    jMenuItemDefaultLaF.setActionCommand("platform");
                    jMenuItemDefaultLaF.setText("Switch to system Look and Feel (OS)");
                    this.pack();
                    break;
                case "platform":
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    jMenuItemDefaultLaF.setActionCommand("default");
                    jMenuItemDefaultLaF.setText("Switch to defaul Look and Feel (Metal)");
                    this.pack();
                    break;
            }
        }catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            logger.error("Error setting Look-and-Feel.", ex);
        }
    }//GEN-LAST:event_jMenuItemDefaultLaFActionPerformed

    private void jMenuItemToRelativeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemToRelativeActionPerformed
        if (Project != null) {
            if (this.CurrentProjectFile == null) {
                int resp = JOptionPane.showConfirmDialog(this, "<html><p>Relative path will be calculated against the location where the "
                        + "project is saved. Do you want to save your project first?</p><p>Current location is: " 
                        + Project.getBaseDir() + " </p></html>", "Save project?", JOptionPane.YES_NO_CANCEL_OPTION);
                if (resp == JOptionPane.CANCEL_OPTION) {
                    return;
                }else if (resp == JOptionPane.YES_OPTION) {
                    this.jMenuItemSaveAsActionPerformed(null);
                }
            }
            Project.convertToRelativeDir();
            // update screen
            this.initProjectSection();
            this.setExecType(0);
        }
    }//GEN-LAST:event_jMenuItemToRelativeActionPerformed

    private void jMenuItemToAbsoluteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemToAbsoluteActionPerformed
        Project.convertToAbsoluteDir();
        // update screen
        this.initProjectSection();
        this.setExecType(0);

    }//GEN-LAST:event_jMenuItemToAbsoluteActionPerformed

    private void jMenuItemExportTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportTableActionPerformed
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.CSV));
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(DefaultDir);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            // import table
            if (! Project.exportParameterTableFile(file)) {
                JOptionPane.showMessageDialog(
                    this,
                    "<html>Export parameters to " + file.getAbsolutePath() + "failed. Please make sure the file is writable.</html>",
                    "Error",
                    JOptionPane.CLOSED_OPTION);
            }else {
                JOptionPane.showMessageDialog(
                    this,
                    "<html>Exported parameters to " + file.getAbsolutePath() + ".</html>",
                    "Information",
                    JOptionPane.CLOSED_OPTION);
            }
            //this.initProjectSection();
        } else {

        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFile(new File(""));
    }//GEN-LAST:event_jMenuItemExportTableActionPerformed

    private void jMenuItemCreateJobListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCreateJobListActionPerformed
        // Select a file to save
        // fc = new JFileChooser ();
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.CSV));
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(DefaultDir);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (! file.getName().endsWith(".csv"))
                file = new File (file.getPath().concat(".csv"));
            // convert to relative paths?
            // Project.convertToRelativeDir(file.getParentFile());
            // write object
            if (! this.createJobList(file.getName(), file.getParent().concat(File.separator))) {
                // warning message
                JOptionPane.showMessageDialog(
                    this,
                    "The JEPlus job list cannot be saved to " + file.getAbsolutePath(),
                    "Error",
                    JOptionPane.CLOSED_OPTION);
            }
        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFile(new File(""));
    }//GEN-LAST:event_jMenuItemCreateJobListActionPerformed

    private void jMenuItemViewFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemViewFolderActionPerformed
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            try {
                File output = new File (BatchManager.getResolvedEnv().getParentDir());
                if (output.exists()) {
                    Desktop.getDesktop().open(output);
                }else {
                    JOptionPane.showMessageDialog(this, "Output folder " + output.getAbsolutePath() + " does not exist.");
                }
            } catch (IOException ex) {
                logger.error ("Failed to open folder " + BatchManager.getResolvedEnv().getParentDir(), ex);
            }
        }else {
            JOptionPane.showMessageDialog(this, "Open folder is not supported, or the current job record is not valid.", "Operation failed", JOptionPane.CLOSED_OPTION);
        }

    }//GEN-LAST:event_jMenuItemViewFolderActionPerformed

    private void jMenuItemRunPythonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRunPythonActionPerformed
        // Switch to the right tab
        this.tpnMain.setSelectedIndex(3);
        this.TpnUtilities.setSelectedIndex(0);
    }//GEN-LAST:event_jMenuItemRunPythonActionPerformed

    private void jMenuItemVersionConverterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVersionConverterActionPerformed
        // Switch to the right tab
        this.tpnMain.setSelectedIndex(3);
        this.TpnUtilities.setSelectedIndex(1);
    }//GEN-LAST:event_jMenuItemVersionConverterActionPerformed

    private void jMenuItemEditorThemeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditorThemeActionPerformed
        // Test if the template file is present
        String templfn = "RSyntaxTheme.xml";
        File ftmpl = new File(templfn);
        if (!ftmpl.exists()) {
            int n = JOptionPane.showConfirmDialog(
                    this,
                    "<html><p><center>The syntax highlighting theme file " + templfn + " does not exist."
                    + " Do you want to select one and copy it to the present working directory?</center></p><p> Select 'NO' to use the default theme. </p>",
                    "Theme file not exist",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                // Select a file to open
                this.getFileChooser().setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.XML));
                this.getFileChooser().setMultiSelectionEnabled(false);
                this.getFileChooser().setSelectedFile(new File(""));
                this.getFileChooser().setCurrentDirectory(new File("./"));
                if (this.getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = this.getFileChooser().getSelectedFile();
                    try {
                        FileUtils.copyFile(file, new File(templfn));
                    } catch (IOException ex) {
                        logger.error("Failed to copy file " + file.getAbsolutePath() + " to ./" + templfn, ex);
                    }
                }
                this.getFileChooser().resetChoosableFileFilters();
                this.getFileChooser().setSelectedFile(new File(""));
            }else {
                return;
            }
        }
        int idx = this.getTpnEditors().indexOfTab(templfn);
        if (idx >= 0) {
            this.getTpnEditors().setSelectedIndex(idx);
        } else {
            EPlusEditorPanel XmlFilePanel = new EPlusEditorPanel(
                    this.getTpnEditors(),
                    templfn,
                    templfn,
                    EPlusEditorPanel.FileType.XML,
                    null);
            int ti = this.getTpnEditors().getTabCount();
            this.getTpnEditors().addTab(templfn, XmlFilePanel);
            XmlFilePanel.setTabId(ti);
            this.getTpnEditors().setSelectedIndex(ti);
            this.getTpnEditors().setTabComponentAt(ti, new ButtonTabComponent(this.getTpnEditors(), XmlFilePanel));
            this.getTpnEditors().setToolTipTextAt(ti, ftmpl.getAbsolutePath());
        }
        
    }//GEN-LAST:event_jMenuItemEditorThemeActionPerformed

    private void jMenuItemRunReadVarsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRunReadVarsActionPerformed
        // Switch to the right tab
        this.tpnMain.setSelectedIndex(3);
        this.TpnUtilities.setSelectedIndex(2);       
    }//GEN-LAST:event_jMenuItemRunReadVarsActionPerformed

    private void jMenuItemExportJsonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportJsonActionPerformed
        // Select a file to save
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.JSON));
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(DefaultDir);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (! file.getName().endsWith(".json"))
                file = new File (file.getPath().concat(".json"));
            // write object
            if (! Project.saveAsJSON(file)) {
                // warning message
                JOptionPane.showMessageDialog(
                    this,
                    "The JEPlus Project cannot be saved for some reasons. :-(",
                    "Error",
                    JOptionPane.CLOSED_OPTION);
            }
        } else {

        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFile(new File(""));
    }//GEN-LAST:event_jMenuItemExportJsonActionPerformed

    private void jMenuItemJESSClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemJESSClientActionPerformed
        // Check if JESS Client folder is available
        if (JEPlusConfig.getDefaultInstance().getJESSClientDir() == null) {
            String ori = fc.getDialogTitle();
            // Select a file to open
            fc.setDialogTitle("Choose where JESS Client is located");
            fc.resetChoosableFileFilters();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setCurrentDirectory(new File("./"));
            fc.setMultiSelectionEnabled(false);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getPath() + File.separator;
                JEPlusConfig.getDefaultInstance().setJESSClientDir(path);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle(ori);
            }else {
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle(ori);
                return;
            }
        }
        // Launch JESS Client
        new Thread(new Runnable() {
            @Override
            public void run () {
                List<String> command = new ArrayList<> ();
                command.add("java");
                command.add("-jar");
                command.add("jess_client_v3.jar");
                command.add(getProject().getBaseDir());
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.directory(new File (JEPlusConfig.getDefaultInstance().getJESSClientDir()));
                builder.redirectErrorStream(true);
                try {
                    Process proc = builder.start();
                    // int ExitValue = proc.waitFor();
                    try (BufferedReader ins = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                        int res = ins.read();
                        while (res != -1) {
                            res = ins.read();
                        }
                    }
                } catch (IOException ex) {
                    logger.error("Cannot run JESS_Client.", ex);
                }
            }
        }, "JESS_Client").start();
    }//GEN-LAST:event_jMenuItemJESSClientActionPerformed

    private void jMenuItemJEPlusEAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemJEPlusEAActionPerformed
        // Check if JEPlus+EA folder is available
        if (JEPlusConfig.getDefaultInstance().getJEPlusEADir() == null) {
            // Select a file to open
            fc.resetChoosableFileFilters();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setCurrentDirectory(new File("./"));
            fc.setMultiSelectionEnabled(false);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getPath() + File.separator;
                JEPlusConfig.getDefaultInstance().setJEPlusEADir(path);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }else {
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                return;
            }
        }
        // Launch JESS Client
        new Thread(new Runnable() {
            @Override
            public void run () {
                List<String> command = new ArrayList<> ();
                command.add("java");
                command.add("-jar");
                command.add("jEPlus+EA.jar");
                command.add(getCurrentProjectFile());
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.directory(new File (JEPlusConfig.getDefaultInstance().getJEPlusEADir()));
                builder.redirectErrorStream(true);
                try {
                    Process proc = builder.start();
                    // int ExitValue = proc.waitFor();
                    try (BufferedReader ins = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                        int res = ins.read();
                        while (res != -1) {
                            res = ins.read();
                        }
                    }
                } catch (IOException ex) {
                    logger.error("Cannot run jEPlus+EA.", ex);
                }
            }
        }, "jEPlus+EA").start();
    }//GEN-LAST:event_jMenuItemJEPlusEAActionPerformed

    private void jMenuItemConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConfigActionPerformed
        showConfigDialog();
    }//GEN-LAST:event_jMenuItemConfigActionPerformed

    private void cboSampleOptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSampleOptActionPerformed
        Project.getExecSettings().setSampleOpt((EPlusBatch.SampleType)cboSampleOpt.getSelectedItem());
    }//GEN-LAST:event_cboSampleOptActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
//        // Assign the first branch to the Parameters list
//        DefaultMutableTreeNode thisleaf = Project.getParamTree().getFirstLeaf();
//        Object [] path = thisleaf.getUserObjectPath();
//        Project.getParameters().clear();
//        for (Object item : path) {
//            Project.getParameters().add((ParameterItemV2)item);
//        }
        // Detect project changes
        if (Project.isContentChanged() /*|| ! Objects.equals(Project, SavedProject)*/) {
            // Save the project file before exit?
            String cfn = this.CurrentProjectFile;
            int n = JOptionPane.showConfirmDialog(
                this,
                "Do you want to save the current project to " + (cfn==null? "file" : cfn) + " before exit?",
                "Save project",
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (n == JOptionPane.CANCEL_OPTION) {
                return;
            }else if (n == JOptionPane.YES_OPTION) {
                this.jMenuItemSaveActionPerformed(null);
            }
        }
        // Check opened files
        for (int i=TpnEditors.getTabCount()-1; i>=0; i--) {
            try {
                boolean cancel = ((IF_JEPlusEditorPanel)TpnEditors.getComponentAt(i)).closeTextPanel();
                if (cancel) return;
            }catch (ClassCastException | NullPointerException cce) {
            }
        }

        // Save EnergyPlus settings
        String currentdate = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(new Date());
        JEPlusConfig.getDefaultInstance().saveAsJSON(new File(JEPlusConfig.getDefaultConfigFile()));
        // Exit
        if (this.getFrameCloseOperation() == JEPlusFrameMain.EXIT_ON_CLOSE) {
            System.exit(0);
        }else {
            this.dispose();
        }
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        // Select a file to save
        // fc = new JFileChooser ();
        fc.setFileFilter(EPlusConfig.getFileFilter(JEPlusConfig.JSON));
        fc.setSelectedFile(new File(CurrentProjectFile == null ? "" : CurrentProjectFile));
        fc.setCurrentDirectory(DefaultDir);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (! file.getName().endsWith(".json")) {
                file = new File (file.getPath().concat(".json"));
            }
            // convert to relative paths?
            Project.setBaseDir(file.getParent());
            // Not to convert automatically on saving the new project
            // boolean ok = Project.convertToRelativeDir(file.getParentFile());
            // write object
            if (! Project.saveAsJSON(file)) {
                // warning message
                JOptionPane.showMessageDialog(
                    this,
                    "The JEPlus Project cannot be saved for some reasons. Check logs for more information.",
                    "Error",
                    JOptionPane.CLOSED_OPTION
                );
            }else {
                // Update the original copy of project
                try {
                    SavedProject = JEPlusProjectV2.loadFromJSON(file);
                }catch (IOException ioe) {
                    logger.warn("Cannot reload project file after saving!", ioe);
                }
                // Update default dir and current project file reference
                DefaultDir = new File (Project.getBaseDir());
                this.setCurrentProjectFile(file.getPath());
                // update screen
                this.initProjectSection();
                this.setExecType(0);
            }
        } else {

        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFile(new File(""));
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        if (CurrentProjectFile != null) {
            File file = new File (CurrentProjectFile);
            // convert to relative paths?
            // Project.convertToRelativeDir(file.getParentFile());
            // Save as .jep
            if (! Project.saveAsJSON(file)) {
                // warning message
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to save the JEPlus project for some reasons! Please check the logs for more information.",
                    "Error",
                    JOptionPane.CLOSED_OPTION);
            }else {
                // Update the original copy of project
                try {
                    SavedProject = JEPlusProjectV2.loadFromJSON(file);
                }catch (IOException ioe) {
                    logger.error("Error reading saved project from " + file, ioe);
                    SavedProject = null;
                }
            }
        }else {
            jMenuItemSaveAsActionPerformed(null);
        }
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
        // Save current project
        if (Project.isContentChanged() /*|| ! Objects.equals(Project, SavedProject)*/) {
            // Save the project file before exit?
            String cfn = this.CurrentProjectFile;
            int n = JOptionPane.showConfirmDialog(
                this,
                "Do you want to save the current project to " + (cfn==null? "file" : cfn) + " before opening another project?",
                "Save project",
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (n == JOptionPane.CANCEL_OPTION) {
                return;
            }else if (n == JOptionPane.YES_OPTION) {
                this.jMenuItemSaveActionPerformed(null);
            }
        }
        // Check opened files
        for (int i=TpnEditors.getTabCount()-1; i>=1; i--) {
            try {
                boolean cancel = ((IF_JEPlusEditorPanel)TpnEditors.getComponentAt(i)).closeTextPanel();
                if (cancel) return;
            }catch (ClassCastException | NullPointerException cce) {
            }
        }
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.JEP_OR_JSON));
        // fc.addChoosableFileFilter(EPlusConfig.getFileFilter(EPlusConfig.JEP));
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(DefaultDir);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            // load object
            this.openProject (this, file);
        } else {

        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFile(new File(""));
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewActionPerformed

        // Check if changes have been saved; prompt if not
        if (Project.isContentChanged() /*|| ! Objects.equals(Project, SavedProject)*/) {
            // Save the project file before exit?
            String cfn = this.CurrentProjectFile;
            int n = JOptionPane.showConfirmDialog(
                this,
                "Do you want to save the current project to " + (cfn==null? "file" : cfn) + " before creating a new project?",
                "Save project",
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (n == JOptionPane.CANCEL_OPTION) {
                return;
            }else if (n == JOptionPane.YES_OPTION) {
                this.jMenuItemSaveActionPerformed(null);
            }
        }
        // Check opened files
        for (int i=TpnEditors.getTabCount()-1; i>=1; i--) {
            try {
                boolean cancel = ((IF_JEPlusEditorPanel)TpnEditors.getComponentAt(i)).closeTextPanel();
                if (cancel) return;
            }catch (ClassCastException | NullPointerException cce) {
            }
        }

        Project.removeAllListeners();
        // New project and update GUI
        Project = new JEPlusProjectV2 ();
        Project.setProjectType((JEPlusProjectV2.ModelType)cboProjectType.getSelectedItem());
        this.initProjectSection();
        this.setExecType(0);
        CurrentProjectFile = null;
        this.setTitle(JEPlusVersion.getVersion() + " - New Project");
        Project.addListener(this);
    }//GEN-LAST:event_jMenuItemNewActionPerformed

    private void cmdSelectTestFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectTestFolderActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File (Project.getBaseDir()));
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtTestResultFolder.setText(fc.getSelectedFile().getAbsolutePath());
        }       
    }//GEN-LAST:event_cmdSelectTestFolderActionPerformed

    private void jMenuItemCreateIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCreateIndexActionPerformed
        if (BatchManager.getBatchInfo().isValid()) {
//            displayInfo (BatchManager.writeProjectIndexCSV());
//            displayInfo (BatchManager.writeProjectIndexSQL("JEPLUSDB", BatchManager.BatchId));
        }else {
            displayInfo ("Project is invalid. Please use the \'Validate Jobs\' command in \'Actions\' menu first, and correct all errors, then try create indexes again.");
        }
    }//GEN-LAST:event_jMenuItemCreateIndexActionPerformed

    private void chkCreateListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCreateListActionPerformed
        Project.getExecSettings().getSteps().setWriteJobList(chkCreateList.isSelected());
    }//GEN-LAST:event_chkCreateListActionPerformed

    private void chkPrepareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrepareActionPerformed
        Project.getExecSettings().getSteps().setPrepareJobs(chkPrepare.isSelected());
    }//GEN-LAST:event_chkPrepareActionPerformed

    private void chkRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRunActionPerformed
        Project.getExecSettings().getSteps().setRunSimulations(chkRun.isSelected());
    }//GEN-LAST:event_chkRunActionPerformed

    private void chkCollectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCollectActionPerformed
        Project.getExecSettings().getSteps().setCollectResults(chkCollect.isSelected());
    }//GEN-LAST:event_chkCollectActionPerformed

    private void cmdValidate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdValidate1ActionPerformed
        cmdValidateActionPerformed(null);
    }//GEN-LAST:event_cmdValidate1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TpnEditors;
    private javax.swing.JTabbedPane TpnUtilities;
    private javax.swing.ButtonGroup btg;
    private javax.swing.JComboBox cboExecutionType;
    private javax.swing.JComboBox cboProjectType;
    private javax.swing.JComboBox cboSampleOpt;
    private javax.swing.JCheckBox chkCollect;
    private javax.swing.JCheckBox chkCreateList;
    private javax.swing.JCheckBox chkPrepare;
    private javax.swing.JCheckBox chkRun;
    private javax.swing.JButton cmdEditJobListFile;
    private javax.swing.JButton cmdSelectJobListFile;
    private javax.swing.JButton cmdSelectTestFolder;
    private javax.swing.JButton cmdStart;
    private javax.swing.JButton cmdValidate;
    private javax.swing.JButton cmdValidate1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenuAction;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemConfig;
    private javax.swing.JMenuItem jMenuItemCreateIndex;
    private javax.swing.JMenuItem jMenuItemCreateJobList;
    private javax.swing.JMenuItem jMenuItemDefaultLaF;
    private javax.swing.JMenuItem jMenuItemEditorTheme;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemExportJson;
    private javax.swing.JMenuItem jMenuItemExportTable;
    private javax.swing.JMenuItem jMenuItemImportTable;
    private javax.swing.JMenuItem jMenuItemJEPlusEA;
    private javax.swing.JMenuItem jMenuItemJESSClient;
    private javax.swing.JMenuItem jMenuItemMemoryUsage;
    private javax.swing.JMenuItem jMenuItemMonitor;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemPostprocess;
    private javax.swing.JMenuItem jMenuItemResetTree;
    private javax.swing.JMenuItem jMenuItemRunPython;
    private javax.swing.JMenuItem jMenuItemRunReadVars;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemSimulate;
    private javax.swing.JMenuItem jMenuItemStop;
    private javax.swing.JMenuItem jMenuItemToAbsolute;
    private javax.swing.JMenuItem jMenuItemToRelative;
    private javax.swing.JMenuItem jMenuItemUserGuide;
    private javax.swing.JMenuItem jMenuItemValidate;
    private javax.swing.JMenuItem jMenuItemVersionConverter;
    private javax.swing.JMenuItem jMenuItemViewErr;
    private javax.swing.JMenuItem jMenuItemViewFolder;
    private javax.swing.JMenuItem jMenuItemViewIndex;
    private javax.swing.JMenuItem jMenuItemViewLog;
    private javax.swing.JMenuItem jMenuItemViewReports;
    private javax.swing.JMenu jMenuRecent;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JMenu jMenuViewResult;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private jeplus.gui.JPanel_EPlusProjectFiles jPanel_EPlusProjectFiles2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private jeplus.gui.JPanel_EPlusSettings jplEPlusSettings;
    private jeplus.gui.JPanel_LocalControllerOptions jplLocalControllerSettings;
    private javax.swing.JPanel jplModelTest;
    private javax.swing.JPanel jplOptions;
    private javax.swing.JPanel jplParamTableHolder;
    private javax.swing.JPanel jplParamTreeHolder;
    private javax.swing.JPanel jplProjectFilesPanelHolder;
    private javax.swing.JPanel jplRVX;
    private javax.swing.JPanel jplSettings;
    private javax.swing.JPanel pnlExecution;
    private javax.swing.JPanel pnlProject;
    private javax.swing.JPanel pnlRvx;
    private javax.swing.JPanel pnlUtilities;
    private javax.swing.JRadioButton rdoAllJobs;
    private javax.swing.JRadioButton rdoCombineResults;
    private javax.swing.JRadioButton rdoExportIndividual;
    private javax.swing.JRadioButton rdoJobListFile;
    private javax.swing.JRadioButton rdoTestChains;
    private javax.swing.JRadioButton rdoTestFirstN;
    private javax.swing.JRadioButton rdoTestRandomN;
    private javax.swing.JTabbedPane tpnMain;
    private javax.swing.JTextField txtJobListFile;
    private javax.swing.JTextField txtRandomSeed;
    private javax.swing.JTextField txtSaveList;
    private javax.swing.JTextField txtTestFirstN;
    private javax.swing.JTextField txtTestRandomN;
    private javax.swing.JTextField txtTestResultFolder;
    // End of variables declaration//GEN-END:variables

    public void openProject (Component parent, File file) {
        if (Project != null) {Project.removeAllListeners();}
        JEPlusProjectV2 proj = null;
        String ext = FilenameUtils.getExtension(file.getName());
        if (ext.equalsIgnoreCase("json")) {
            try {
                SavedProject = JEPlusProjectV2.loadFromJSON(file);
                Project = JEPlusProjectV2.loadFromJSON(file);
                this.setCurrentProjectFile(file.getPath());
            }catch (IOException ioe) {
                logger.error("Error reading JSON project from " + file, ioe);
                // warning message
                JOptionPane.showMessageDialog(
                    parent,
                    "Failed to load project from " + file.getPath() + ". Please check the contents of the project.",
                    "Error",
                    JOptionPane.CLOSED_OPTION);
                SavedProject = null;
                Project = new JEPlusProjectV2();
            }
        }else if (ext.equalsIgnoreCase("jep")) {
            SavedProject = null;
            Project = new JEPlusProjectV2(JEPlusProject.loadAsXML(file));
            // this.setCurrentProjectFile(FilenameUtils.removeExtension(file.getPath()) + ".json");
            this.setCurrentProjectFile(file.getPath() + ".json");
            if (Project == null) {
                // warning message
                JOptionPane.showMessageDialog(
                    parent,
                    "Failed to load project from " + file.getPath() + ". Please check the contents of the project.",
                    "Error",
                    JOptionPane.CLOSED_OPTION);
                Project = new JEPlusProjectV2();
            }
        }

        // GUI update
        // this.initProjectSection();
        // Update project type (E+ or TRNSYS) and gui
        this.cboProjectType.setSelectedItem(Project.getProjectType());
        // update Exec Agent's reference to the Execution options
        for (EPlusAgent agent: ExecAgents) {
            agent.setSettings(Project.getExecSettings());
        }
        this.setProjectType(Project.getProjectType());
        // select again Exec agent and update gui
        // this.setExecType(Project.getExecSettings().getExecutionType());
        // this.cboExecutionTypeActionPerformed(null);
        // Base directory update
        DefaultDir = new File (Project.getBaseDir());
        // Batch options gui
        this.initBatchOptions();
        // Attach listener
        Project.addListener(this);
    }

    private void addMenuItemRecentFile (String fn) {
        final File file = new File (fn);
        JMenuItem item = new JMenuItem (file.getAbsolutePath());
        item.setToolTipText(fn);
        final JEPlusFrameMain gui = this;
        item.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if the current project should be saved
                if (Project.isContentChanged() /*|| ! Objects.equals(Project, SavedProject)*/) {
                    // Save the project file before exit?
                    String cfn = CurrentProjectFile;
                    int n = JOptionPane.showConfirmDialog(
                        gui,
                        "Do you want to save the current project to " + (cfn==null? "file" : cfn) + " before creating a new project?",
                        "Save project",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                    if (n == JOptionPane.CANCEL_OPTION) {
                        return;
                    }else if (n == JOptionPane.YES_OPTION) {
                        jMenuItemSaveActionPerformed(null);
                    }
                }
                // Check opened files
                for (int i=TpnEditors.getTabCount()-1; i>=1; i--) {
                    try {
                        boolean cancel = ((IF_JEPlusEditorPanel)TpnEditors.getComponentAt(i)).closeTextPanel();
                        if (cancel) return;
                    }catch (ClassCastException | NullPointerException cce) {
                    }
                }
                // Open the recent project
                openProject(null, file);
            }
        });
        this.jMenuRecent.add(item);
    }

    private void updateRecentFilesMenu () {
        this.jMenuRecent.removeAll();
        List<String> recent = JEPlusConfig.getDefaultInstance().getRecentProjects();
        if (recent != null) {
            int idx = 0;
            for (int i=0; i<recent.size(); i++) {
                String prj = recent.get(i);
                if (prj != null && prj.trim().length() > 0) {
                    this.addMenuItemRecentFile (prj);
                    idx ++;
                }
            }
        }
    }
    
    private void addMenuItemResultFile (String fn) {
        final File file = new File (BatchManager.getResolvedEnv().getParentDir() + fn);
        JMenuItem item = new JMenuItem (file.getName());
        item.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/page_white_magnify.png")));
        item.setToolTipText(file.getPath());
        item.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                //openViewTabForFile(file.getPath());

                // Open it in associated application
                
                try {
                    Desktop.getDesktop().open(file);
                }catch (Exception ex) {
                    logger.error("Error open result file " + file.getAbsolutePath(), ex);
                    JOptionPane.showMessageDialog(jMenuViewResult, "Cannot open " + file.getName() + " with the associated application.", "Operation failed", JOptionPane.INFORMATION_MESSAGE);
                }
                
            }
        });
        this.jMenuViewResult.add(item);
    }

    /**
     * Initialise and load GUI
     * @param frame The Frame object to load
     * @param prjfile File name of project to load
     * @param showSplash Flag to show splash window or not
     */
    public static void startGUI(final JEPlusFrameMain frame, final String prjfile, final boolean showSplash) {
        if (frame != null) {
            // Note down the current main window instance
            CurrentMainWindow = frame;
            // Configue and make it visible
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Clear console log file content
                    JEPlusConfig.getDefaultInstance().purgeScreenLogFile();
                    // Set recent projects in menu
                    frame.updateRecentFilesMenu();
                    // Update local e+ configurations
                    frame.setExecType(0);
                    if (prjfile != null) {
                        frame.openProject(frame, new File (prjfile));
                    }
                    frame.pack();
                    frame.setVisible(true);
//                    // Start a new thread for output panel
//                    new Thread (frame.OutputPanel).start();
                    // Load EnergyPlus settings
                    if (showSplash) {
                        showSplash(frame);
                        frame.jMenuItemConfigActionPerformed(null);
                        // this.cmdEditEPlusSettingsActionPerformed(null);
                    }
                }
            });
            // Initialize syntax markers for recognized file types
            AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
            atmf.putMapping("text/EPlusIDF", "jeplus.gui.tokenmaker.EPlusIDF_TokenMaker");
            atmf.putMapping("text/EPlusEPW", "jeplus.gui.tokenmaker.EPlusEPW_TokenMaker");
            atmf.putMapping("text/EPlusRVI", "jeplus.gui.tokenmaker.EPlusRVI_TokenMaker");
            
        }else {
            System.err.println("Build main GUI failed. Please check jEPlusFrameMain instance is valid.");
        }
    }

    private void openViewTabForFile(String fn) {
        // Test if the template file is present
        File ftmpl = new File(fn);
        if (!ftmpl.exists()) {
            JOptionPane.showMessageDialog(
                    this,
                    "<html><p><center>The file " + ftmpl.getPath() + " does not exist.</p>",
                    "File not found",
                    JOptionPane.OK_OPTION);
        } else {
            EPlusTextPanel TextFilePanel = new EPlusTextPanel(
                    TpnEditors,
                    fn,
                    EPlusTextPanel.VIEWER_MODE,
                    EPlusConfig.getFileFilter(EPlusConfig.ALL),
                    ftmpl.getPath(),
                    null);
            int ti = TpnEditors.getTabCount();
            TextFilePanel.setTabId(ti);
            this.TpnEditors.addTab(fn, TextFilePanel);
            TpnEditors.setSelectedIndex(ti);
            TpnEditors.setTabComponentAt(ti, new ButtonTabComponent(TpnEditors, TextFilePanel));
            TpnEditors.setToolTipTextAt(ti, ftmpl.getPath());
        }
    }

    @Override
    public void projectChanged(JEPlusProjectV2 new_prj) {
        // Update title to show project name
        this.setTitle(JEPlusVersion.getVersion() + " - " + (CurrentProjectFile==null?"New Project":CurrentProjectFile) + (Project.isContentChanged()?"*":""));
        // Update display
        //initProjectSection ();
        //setExecType (0); // Has only local agent
        // Update search tags if model editor is open
        for (int i=1; i<TpnEditors.getTabCount(); i++) {
            try {
                EPlusEditorPanel etp = (EPlusEditorPanel)TpnEditors.getComponentAt(i);
                switch (etp.getContentType()) {
                    case IDF:
                    case TRNSYS:
                        etp.updateSearchStrings((Project == null) ? null : Project.getSearchStrings());
                        break;
                    default:
                }
            }catch (ClassCastException cce) {

            }
        }
    }
    
    @Override
    public void projectSaved (String filename) {
        // Does nothing
    }

}

