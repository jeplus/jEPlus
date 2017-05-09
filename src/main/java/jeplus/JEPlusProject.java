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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.tree.DefaultMutableTreeNode;
import jeplus.data.ExecutionOptions;
import jeplus.data.ParameterItem;
import jeplus.data.RVX;
import jeplus.data.RandomSource;
import jeplus.data.RouletteWheel;
import jeplus.util.CsvUtil;
import jeplus.util.RelativeDirUtil;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.random.SobolSequenceGenerator;
import org.slf4j.LoggerFactory;

/**
 * JEPlus Project class encapsulates definition of a project
 * @author Yi Zhang
 * @version 1.0
 * @since 1.0
 */
public class JEPlusProject implements Serializable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JEPlusProject.class);
    
    /** ScriptEngine used by all evaluators */
    protected static final ScriptEngine Script_Engine = new ScriptEngineManager().getEngineByName("javascript");
    static {
        // Set up script engine
//        try {
//            Script_Engine.eval("import math");
//        }catch (ScriptException sce) {
//            logger.error("Script engine error when importing math module.", sce);
//        }
    }
    
    public static ScriptEngine getScript_Engine() {
        return Script_Engine;
    }

    private static final long serialVersionUID = -3920321004466467177L;
    public static final int EPLUS = 0;
    public static final int TRNSYS = 1;
    public static final int INSEL = 2;
    public static final int EP2TRN = 99;
    
    /** This is the working directory of the program */
    protected static String UserBaseDir = System.getProperty("user.dir") + File.separator;
    
    /** Flag marking whether this project has been changed since last save/load */
    transient private boolean ContentChanged = false;
    
    /** Base directory of the project, i.e. the location where the project file is saved */
    protected String BaseDir = null;
    
    /** Project Type: E+ or TRNSYS */
    protected int ProjectType = -1; // set to illegal type
    
    /** Project ID string */
    protected String ProjectID = null;
    
    /** Project notes string */
    protected String ProjectNotes = null;

    /** Local directory for IDF template files */
    protected String IDFDir = null;
    /** Template file to be used in this job; or a (';' delimited) list of files for the batch project */
    protected String IDFTemplate = null;

    /** Local directory for weather files */
    protected String WeatherDir = null;
    /** Weather file to be used in this job; or a (';' delimited) list of files for the batch project */
    protected String WeatherFile = null;

    /** Flag for calling ReadVarsESO or not */
    protected boolean UseReadVars = false;

    /** ReadVarsESO configure file to be used to extract results */
    protected String RVIDir = null;
    /** ReadVarsESO configure file to be used to extract results */
    protected String RVIFile = null;

    /** Local directory for DCK/TRD (for TRNSYS) template files */
    protected String DCKDir = null;
    /** Template file to be used in this job; or a (';' delimited) list of files for the batch project */
    protected String DCKTemplate = null;
    /** Output file names that contain results for each simulation; used for TRNSYS */
    protected String OutputFileNames = null;

    /** Local directory for INSEL (for INSEL) template files */
    protected String INSELDir = null;
    /** Template file to be used in this job; or a (';' delimited) list of files for the batch project */
    protected String INSELTemplate = null;

    /** Execution settings */
    protected ExecutionOptions ExecSettings = null;

    /** List of parameters */
    protected ArrayList<ParameterItem> Parameters = null;
    
    /** Parameter tree */
    protected DefaultMutableTreeNode ParamTree = null;
       
    /** Parameter definition file */
    protected String ParamFile = null;

    /** RVX object for result collection */
    protected RVX Rvx = null;
    
    /** External file defining the RVX object, in JSON format */
    protected String RvxFile = null;
    
    /**
     * Class containing post-process function options
     */
    public class PostProcOptions {
        protected String ExtraRVIFile = "./my.rvi";
        protected String ExtraRVIFrequency = "RunPeriod";
        protected String SelectedTRNOutput = "TRNSYSout.csv";
        protected boolean DeleteOutput = false;
        
        protected String PostProcFunctor = "DefaultPostProcFunc";
        protected String ReferenceCase = null;
        protected String ReferenceTable = "reference.csv";
        
        protected String ExportDir = "./";
        protected boolean ExportJobTables = false;
        protected String JobTablePrefix = "my";
        protected boolean ExportOneTable = true;
        protected String OneTableName = "processed_result";
        protected boolean ExportStatsTables = true;
        protected String StatsTablePrefix = "my";
        
        public PostProcOptions () {}
    }

    /**
     * Default constructor
     */
    public JEPlusProject () {
        ProjectType = EPLUS;
        ProjectID = "G";
        ProjectNotes = "New project";
        IDFDir = "./";
        // IDFTemplate = "select files ...";
        WeatherDir = "./";
        // WeatherFile = "select files ...";
        UseReadVars = true;
        RVIDir = "./";
        // RVIFile = "select a file ...";
        DCKDir = "./";
        // DCKTemplate = "select a file ...";
        INSELDir = "./";
        // INSELTemplate = "select a file ...";
        OutputFileNames = "trnsysout.csv";  // fixed on one file name for the time being
        ExecSettings = new ExecutionOptions ();
        ParameterItem root = new ParameterItem(this);
        Parameters = new ArrayList<> ();
        Parameters.add(root);
        ParamTree = new DefaultMutableTreeNode (root);
        BaseDir = new File ("./").getAbsolutePath() + File.separator;
    }

    /**
     * Cloning constructor. New project state is set to 'changed' after cloning
     * @param proj Project object to be cloned
     */
    public JEPlusProject (JEPlusProject proj) {
        this();
        if (proj != null) {
            ContentChanged = true;  // set content changed for the new project obj
            BaseDir = proj.BaseDir;
            ProjectType = proj.ProjectType;
            ProjectID = proj.ProjectID;
            ProjectNotes = proj.ProjectNotes;
            IDFDir = proj.IDFDir;
            IDFTemplate = proj.IDFTemplate;
            WeatherDir = proj.WeatherDir;
            WeatherFile = proj.WeatherFile;
            UseReadVars = proj.UseReadVars;
            RVIDir = proj.RVIDir;
            RVIFile = proj.RVIFile;
            DCKDir = proj.DCKDir;
            DCKTemplate = proj.DCKTemplate;
            INSELDir = proj.INSELDir;
            INSELTemplate = proj.INSELTemplate;
            OutputFileNames = proj.OutputFileNames;
            ExecSettings = new ExecutionOptions (proj.ExecSettings);
            Parameters = proj.Parameters;
            ParamTree = proj.ParamTree;
            Rvx = proj.Rvx;
        }
    }

    // ================= File operations ==============================
    /**
     * Save the project to an object file
     * @param fn The File object associated with the file to which the contents will be saved
     * @param proj The project object to be serialised
     * @return Successful or not
     */
    public static boolean serialize (File fn, JEPlusProject proj) {
        boolean success = true;
        try (ObjectOutputStream ow = new ObjectOutputStream(new FileOutputStream(fn))) {
            ow.writeObject(proj);
        }catch (IOException ioe) {
            logger.error("Error writing project object to " + fn, ioe);
            success = false;
        }
        return success;
    }

    /**
     * Read parameter tree from an object file
     * @param fn The File object associated with the file
     * @return de-serialised object
     */
    public static JEPlusProject deserialize (File fn) {
        JEPlusProject proj = null;
        try (ObjectInputStream or = new ObjectInputStream(new FileInputStream(fn))) {
            proj = (JEPlusProject)or.readObject();
        }catch (IOException ioe) {
            logger.error("Error reading project object from " + fn, ioe);
        }catch (Exception e) {
            logger.error("Error parsing project object from " + fn, e);
        }
        return proj;
    }

    /**
     * Save this project to an XML file
     * @param fn The File object associated with the file to which the contents will be saved
     * @return Successful or not
     */
    public boolean saveAsXML (File fn) {
        boolean success = true;
        
        // Write project file
        XMLEncoder encoder;
        try {
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fn)));
            // Clear external parameters and rvx file reference fields before saving the project
            // These files are for importing only
            this.ParamFile = null;
            this.RvxFile = null;
            encoder.writeObject(this);
            encoder.close();
            // get new location of project file
            String dir = fn.getAbsoluteFile().getParent();
            dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
            this.updateBaseDir(dir);
            this.ContentChanged = false;
        } catch (FileNotFoundException ex) {
            logger.error("Failed to create " + fn + " for writing project.", ex);
            success = false;
        }
        return success;
    }

    /**
     * Read a project from an XML file. The members of this project are not updated.
     * @param fn The File object associated with the file
     * @return a new project instance from the file
     */
    public static JEPlusProject loadAsXML (File fn) {
        JEPlusProject proj;
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(fn)))) {
            proj = ((JEPlusProject)decoder.readObject());
        }catch (Exception ex) {
            logger.error("Error loading project from file " + fn, ex);
            return null;
        }
        String dir = fn.getAbsoluteFile().getParent();
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        // proj.updateBaseDir(dir);
        proj.setBaseDir(dir);
        if (proj.ParamFile != null) {
            // Load parameters from text file
            proj.importParameterTableFile(new File (RelativeDirUtil.checkAbsolutePath(proj.ParamFile, dir)));
        }else {
            // Reassign reference to project in all parameters
            if (proj.getParamTree() != null) {
                Enumeration params = proj.getParamTree().breadthFirstEnumeration();
                while (params.hasMoreElements()) {
                     ((ParameterItem)((DefaultMutableTreeNode)params.nextElement()).getUserObject()).setProject(proj);
                }
            }
        }
        // Assign the first branch to the Parameters list
        DefaultMutableTreeNode thisleaf = proj.getParamTree().getFirstLeaf();
        Object [] path = thisleaf.getUserObjectPath();
        proj.setParameters(new ArrayList<ParameterItem> ());
        for (Object item : path) {
            proj.getParameters().add((ParameterItem)item);
        }
        // Load Rvx if a RVX file is available
        try {
            proj.Rvx = RVX.getRVX(proj.resolveRVIDir() + proj.getRVIFile());
        }catch (IOException ioe) {
            logger.error("Cannot read the project's RVX file", ioe);
        }

        // done            
        return proj;
    }
    
    /**
     * Save this project to an XML file
     * @param file The File object associated with the file to which the contents will be saved
     * @return Successful or not
     */
    public boolean saveAsJSON (File file) {
        boolean success = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(format);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try (FileOutputStream fw = new FileOutputStream(file); ) {
            mapper.writeValue(fw, this);
            logger.info("Project saved to " + file.getAbsolutePath());
        }catch (Exception ex) {
            logger.error("Error saving project to JSON.", ex);
            success = false;
        }
        return success;
    }

    /**
     * Read the project from the given JSON file. 
     * @param file The File object associated with the file
     * @return a new project instance from the file
     * @throws java.io.IOException
     */
    public static JEPlusProject loadFromJSON (File file) throws IOException {
        // Read JSON
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        JEPlusProject project = mapper.readValue(file, JEPlusProject.class);
        // Set base dir
        String dir = file.getAbsoluteFile().getParent();
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        // project.updateBaseDir(dir);
        project.setBaseDir(dir);
        // If parameter file is given, use the contents to override the parameters in the project
        if (project.ParamFile != null) {
            // Load parameters from text file, to replace the existing Parameters list and tree
            project.importParameterTableFile(new File (RelativeDirUtil.checkAbsolutePath(project.ParamFile, dir)));
        }else {
            // Reassign reference to project in all parameters, and build param tree
            if (project.getParameters() != null) {
                for (ParameterItem item: project.getParameters()) {
                    item.setProject(project);
                }
                project.addParameterListAsBranch(null, project.getParameters());
            }
        }
        // If external RVX file is specified, use its contents for Rvx object
        if (project.RvxFile != null) {
            try {
                project.Rvx = RVX.getRVX(RelativeDirUtil.checkAbsolutePath(project.RvxFile, dir));
            }catch (IOException ioe) {
                logger.error("Cannot read the given RVX file", ioe);
            }
        }
        project.ContentChanged = false;
        // Return
        return project;
    }
    
    // ================== Getters and Setters ==========================
    
    /**
     * Get the base directory of the current project
     * @return Base directory
     */
    @JsonIgnore
    public String getBaseDir() {
        return BaseDir;
    }

    /**
     * Set the base directory of the current project to the given paths. This 
     * function is for serialisation only. For updating the base dir, use updateBaseDir()
     * @param BaseDir The new base directory for this project
     */
    @JsonIgnore
    public void setBaseDir(String BaseDir) {
        this.BaseDir = BaseDir;
    }

    public int getProjectType() {
        return ProjectType;
    }

    public void setProjectType(int ProjectType) {
        this.ProjectType = ProjectType;
    }

    public String getProjectID() {
        return ProjectID;
    }

    public void setProjectID(String ProjectID) {
        this.ProjectID = ProjectID;
    }

    public String getProjectNotes() {
        return ProjectNotes;
    }

    public void setProjectNotes(String ProjectNotes) {
        this.ProjectNotes = ProjectNotes;
    }

    public ExecutionOptions getExecSettings() {
        return ExecSettings;
    }

    public void setExecSettings(ExecutionOptions ExecSettings) {
        this.ExecSettings = ExecSettings;
    }

    public String getIDFDir() {
        return IDFDir;
    }

    public void setIDFDir(String IDFDir) {
        this.IDFDir = IDFDir;
    }

    public String getIDFTemplate() {
        return IDFTemplate;
    }

    public void setIDFTemplate(String IDFTemplate) {
        this.IDFTemplate = IDFTemplate;
    }

    @JsonIgnore
    public boolean isContentChanged() {
        return ContentChanged;
    }

    @JsonIgnore
    public void setContentChanged(boolean ContentChanged) {
        this.ContentChanged = ContentChanged;
    }

    public ArrayList<ParameterItem> getParameters() {
        return Parameters;
    }

    public void setParameters(ArrayList<ParameterItem> Parameters) {
        this.Parameters = Parameters;
    }

    @JsonIgnore
    public DefaultMutableTreeNode getParamTree() {
        return ParamTree;
    }

    @JsonIgnore
    public void setParamTree(DefaultMutableTreeNode ParamTree) {
        this.ParamTree = ParamTree;
    }

    @JsonIgnore
    public String getRVIDir() {
        return RVIDir;
    }

    @JsonIgnore
    public void setRVIDir(String RVIDir) {
        this.RVIDir = RVIDir;
    }

    @JsonIgnore
    public String getRVIFile() {
        return RVIFile;
    }

    @JsonIgnore
    public void setRVIFile(String RVIFile) {
        this.RVIFile = RVIFile;
    }

    @JsonIgnore
    public boolean isUseReadVars() {
        return UseReadVars;
    }

    @JsonIgnore
    public void setUseReadVars(boolean UseReadVars) {
        this.UseReadVars = UseReadVars;
    }

    @JsonIgnore
    public String getDCKDir() {
        return DCKDir;
    }

    @JsonIgnore
    public void setDCKDir(String DCKDir) {
        this.DCKDir = DCKDir;
    }

    @JsonIgnore
    public String getDCKTemplate() {
        return DCKTemplate;
    }

    @JsonIgnore
    public void setDCKTemplate(String DCKTemplate) {
        this.DCKTemplate = DCKTemplate;
    }

    @JsonIgnore
    public String getINSELDir() {
        return INSELDir;
    }

    @JsonIgnore
    public void setINSELDir(String INSELDir) {
        this.INSELDir = INSELDir;
    }

    @JsonIgnore
    public String getINSELTemplate() {
        return INSELTemplate;
    }

    @JsonIgnore
    public void setINSELTemplate(String INSELTemplate) {
        this.INSELTemplate = INSELTemplate;
    }

    @JsonIgnore
    public String getOutputFileNames() {
        return OutputFileNames;
    }

    @JsonIgnore
    public void setOutputFileNames(String OutputFileNames) {
        this.OutputFileNames = OutputFileNames;
    }

    public String getWeatherDir() {
        return WeatherDir;
    }

    public void setWeatherDir(String WeatherDir) {
        this.WeatherDir = WeatherDir;
    }

    public String getWeatherFile() {
        return WeatherFile;
    }

    public void setWeatherFile(String WeatherFile) {
        this.WeatherFile = WeatherFile;
    }

    public String getParamFile() {
        return ParamFile;
    }

    public void setParamFile(String ParamFile) {
        this.ParamFile = ParamFile;
    }

    public RVX getRvx() {
        return Rvx;
    }

    public void setRvx(RVX Rvx) {
        this.Rvx = Rvx;
    }

    public String getRvxFile() {
        return RvxFile;
    }

    public void setRvxFile(String RvxFile) {
        this.RvxFile = RvxFile;
        // If external RVX file is specified, use its contents for Rvx object
        if (RvxFile != null) {
            try {
                this.Rvx = RVX.getRVX(RelativeDirUtil.checkAbsolutePath(RvxFile, this.getBaseDir()));
            }catch (IOException ioe) {
                logger.error("Cannot read the given RVX file", ioe);
            }
        }
    }
    
    

    // ====================== End Getters and Setters ======================
    
    
    // A new set of resolveXYZFile functions
    
    /**
     * Set the base directory of the current project to the given paths. Once the
     * new paths are set, the relative paths of all project files are recalculated,
     * and the absolute paths converted to relative form.
     * @param BaseDir The new base directory for this project
     */
    public void updateBaseDir(String BaseDir) {
        // First to convert all paths to absolute using the existing Base
        this.setWeatherDir(this.resolveWeatherDir());   // Weather file path
        this.setIDFDir(this.resolveIDFDir());        // idf file path
        this.setDCKDir(this.resolveDCKDir());        // dck file path
        this.setRVIDir(this.resolveRVIDir());        // rvi file path
        this.getExecSettings().setParentDir(this.resolveWorkDir());        // output dir
        // Update BaseDir
        this.BaseDir = BaseDir;
        // Calculate relative dir from the new base
        this.setWeatherDir(RelativeDirUtil.getRelativePath(this.getWeatherDir(), this.BaseDir, "/"));   // Weather file path
        this.setIDFDir(RelativeDirUtil.getRelativePath(this.getIDFDir(), this.BaseDir, "/"));        // idf file path
        this.setDCKDir(RelativeDirUtil.getRelativePath(this.getDCKDir(), this.BaseDir, "/"));        // dck file path
        this.setRVIDir(RelativeDirUtil.getRelativePath(this.getRVIDir(), this.BaseDir, "/"));        // rvi file path
        this.getExecSettings().setParentDir(RelativeDirUtil.getRelativePath(this.getExecSettings().getParentDir(), this.BaseDir, "/"));        // output dir
    }

    /** 
     * Resolve the path to the project's work (a.k.a. parent) directory. If
     * relative path is used, it is relative to the project folder
     * @return Resolved absolute paths
     */
    public String resolveWorkDir () {
        String dir = RelativeDirUtil.checkAbsolutePath(ExecSettings.getWorkDir(), BaseDir);
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        return dir;
    }
    
    /** 
     * Resolve the path to the PBS script to use for running this project. If
     * relative path is used, it is relative to the UserBaseDir rather than
     * the project folder
     * @return Resolved absolute paths
     */
    public String resolvePBSscriptFile () {
        return RelativeDirUtil.checkAbsolutePath(ExecSettings.getPBSscriptFile(), UserBaseDir);
    }
    
    /** 
     * Resolve the path to the server config file for running this project. If
     * relative path is used, it is relative to the UserBaseDir rather than
     * the project folder
     * @return Resolved absolute paths
     */
    public String resolveServerConfigFile () {
        return RelativeDirUtil.checkAbsolutePath(ExecSettings.getServerConfigFile(), UserBaseDir);
    }
    
    /** 
     * Resolve the path to the IDF models of this project. If
     * relative path is used, it is relative to the project folder
     * @return Resolved absolute paths
     */
    public String resolveIDFDir () {
        String dir = RelativeDirUtil.checkAbsolutePath(this.getIDFDir(), BaseDir);
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        return dir;
    }
    
    /** 
     * Resolve the path to the RVI file of this project. If
     * relative path is used, it is relative to the project folder
     * @return Resolved absolute paths
     */
    public String resolveRVIDir () {
        String dir = RelativeDirUtil.checkAbsolutePath(this.getRVIDir(), BaseDir);
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        return dir;
    }
    
    /** 
     * Resolve the path to the weather files of this project. If
     * relative path is used, it is relative to the project folder
     * @return Resolved absolute paths
     */
    public String resolveWeatherDir () {
        String dir = RelativeDirUtil.checkAbsolutePath(this.getWeatherDir(), BaseDir);
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        return dir;
    }
    
    /** 
     * Resolve the path to the RVI file of this project. If
     * relative path is used, it is relative to the project folder
     * @return Resolved absolute paths
     */
    public String resolveDCKDir () {
        String dir = RelativeDirUtil.checkAbsolutePath(this.getDCKDir(), BaseDir);
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        return dir;
    }
    
    /** 
     * Resolve the path to the RVI file of this project. If
     * relative path is used, it is relative to the project folder
     * @return Resolved absolute paths
     */
    public String resolveINSELDir () {
        String dir = RelativeDirUtil.checkAbsolutePath(this.getINSELDir(), BaseDir);
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        return dir;
    }
    
    /**
     * This function reads the E+ version from the first model file, and return it in a string, such as 7.0
     * @return Version info in a string
     */
    @JsonIgnore
    public String getEPlusModelVersion () {
        return IDFmodel.getEPlusVersionInIDF (resolveIDFDir() + parseFileListString(resolveIDFDir(), getIDFTemplate()).get(0));
    }
    
    /**
     * This function copies information from an EPlusWorkEnv object to provide
     * some backwards compatibility
     * @param env the EPlusWorkEnv object
     */
    public void copyFromEnv (EPlusWorkEnv env) {
        IDFDir = env.IDFDir;
        IDFTemplate = env.IDFTemplate;
        WeatherDir = env.WeatherDir;
        WeatherFile = env.WeatherFile;
        UseReadVars = env.UseReadVars;
        RVIDir = env.RVIDir;
        RVIFile = env.RVIFile;
        ProjectType = env.ProjectType;
        DCKDir = env.DCKDir;
        DCKTemplate = env.DCKTemplate;
        INSELDir = env.INSELDir;
        INSELTemplate = env.INSELTemplate;
        OutputFileNames = env.OutputFileNames;
        ExecSettings.setParentDir(env.ParentDir);
        ExecSettings.setKeepEPlusFiles(env.KeepEPlusFiles);
        ExecSettings.setKeepJEPlusFiles(env.KeepJEPlusFiles);
        ExecSettings.setKeepJobDir(env.KeepJobDir);
        ExecSettings.setDeleteSelectedFiles(env.SelectedFiles != null);
        ExecSettings.setSelectedFiles(env.SelectedFiles);
        ExecSettings.setRerunAll(env.ForceRerun);
    }

    /**
     * This function copies information to an EPlusWorkEnv object to provide
     * some backwards compatibility
     * @param env the EPlusWorkEnv object
     */
    public void resolveToEnv (EPlusWorkEnv env) {
        env.IDFDir = this.resolveIDFDir();
        env.IDFTemplate = IDFTemplate;
        env.WeatherDir = this.resolveWeatherDir();
        env.WeatherFile = WeatherFile;
        env.UseReadVars = UseReadVars;
        env.RVIDir = this.resolveRVIDir();
        env.RVIFile = RVIFile;
        env.ProjectType = ProjectType;
        env.DCKDir = this.resolveDCKDir();
        env.DCKTemplate = DCKTemplate;
        env.INSELDir = this.resolveINSELDir();
        env.INSELTemplate = INSELTemplate;
        env.OutputFileNames = OutputFileNames;
        env.ProjectBaseDir = this.BaseDir;
        env.ParentDir = this.resolveWorkDir();
        env.KeepEPlusFiles = ExecSettings.isKeepEPlusFiles();
        env.KeepJEPlusFiles = ExecSettings.isKeepJEPlusFiles();
        env.KeepJobDir = ExecSettings.isKeepJobDir();
        env.SelectedFiles = ExecSettings.isDeleteSelectedFiles() ? ExecSettings.getSelectedFiles() : null;
        env.ForceRerun = ExecSettings.isRerunAll();
    }

    /**
     * Decode IDF or Weather files string and store them, with directory, in an array
     * @param dir Default directory for IDF/IMF/EPW/LST files. Entries in the LST files should contain only relative paths to this directory
     * @param files Input files string. ';' delimited list of IDF/IMF/EPW/LST files
     * @return Validation result: true if all files are available
     */
    public ArrayList<String> parseFileListString(String dir, String files) {
        ArrayList<String> Files = new ArrayList<>();
        if (files != null) {
            String[] file = files.split("\\s*;\\s*");
            for (int i = 0; i < file.length; i++) {
                if (file[i].length() > 0) {
                    // If a list file, parse it
                    if (file[i].toLowerCase().endsWith(".lst")) {
                        Files.addAll(parseListFile(dir, file[i]));
                    // otherwise, just add
                    }else {
                        Files.add(file[i]);
                    }
                }
            }
        }
        return Files;
    }

    /**
     * Get all input files in the project. This function is for E+ version conversion and possibly auto project compilation 
     * for remote execution. In a jEPlus project, the following files will be listed:
     * - Weather files (not for version conversion)
     * - IDF/IMF models 
     * - Include files in IMF models. Actual file name will be identified if include files are used as parameters
     * - RVI/MVI file
     * @return A list of file full paths
     */
    @JsonIgnore
    public ArrayList<String> getAllInputFiles () {
        ArrayList<String> filelist = new ArrayList<> ();
        if (ProjectType == EPLUS) {
            
        }else if (ProjectType == TRNSYS) {
            
        }else if (ProjectType == INSEL) {
            
        }
        return filelist;
    }
    
    /**
     * Convert all directories to relative paths to where the project base (the
     * location of the project file, for example) is.
     * @param Base The base directory of the project
     * @return conversion successful or not
     */
    protected boolean convertToRelativeDir (File Base) {
        if (Base != null && Base.exists()) {
            File idf = new File (IDFDir);
            File wthr = new File (WeatherDir);
            File rvi = new File (RVIDir);
            File out = new File (ExecSettings.getWorkDir());
            if (idf.exists() && wthr.exists() && rvi.exists() && out.exists()) {
                IDFDir = RelativeDirUtil.getRelativePath(Base, idf);
                WeatherDir = RelativeDirUtil.getRelativePath(Base, wthr);
                RVIDir = RelativeDirUtil.getRelativePath(Base, rvi);
                ExecSettings.setParentDir(RelativeDirUtil.getRelativePath(Base, out));
                return true;
            }
        }
        return false;
    }

    /**
     * Convert all directories to absolute paths.
     * @param base The base directory of the project
     */
    protected void convertToAbsoluteDir (File base) {
        IDFDir = new File (base, IDFDir).getAbsolutePath();
        WeatherDir = new File (base, WeatherDir).getAbsolutePath();
        RVIDir = new File (base, RVIDir).getAbsolutePath();
        ExecSettings.setParentDir(new File (base, ExecSettings.getWorkDir()).getAbsolutePath());
        //ExecSettings.setPBSscriptFile(new File (base, ExecSettings.getPBSscriptFile()).getAbsolutePath());
        //ExecSettings.setServerConfigFile(new File (base, ExecSettings.getServerConfigFile()).getAbsolutePath());
    }

    /**
     * Get a list of search strings from the parameter tree of this project.
     *
     * @return
     */
    @JsonIgnore
    public String [] getSearchStrings () {
        DefaultMutableTreeNode ParaTree = this.getParamTree();
        if (ParaTree == null) return null;

        ArrayList<String> SearchStrings = new ArrayList<> ();
        Enumeration nodes = ParaTree.preorderEnumeration();
        while (nodes.hasMoreElements()) {
            Object node = nodes.nextElement(); 
            String ss = ((ParameterItem)((DefaultMutableTreeNode)node).getUserObject()).getSearchString();
            if (ss != null && ss.trim().length() > 0 && !SearchStrings.contains(ss)) {
                SearchStrings.add(ss);
            }
        }
        return SearchStrings.toArray(new String [0]);
    }

    /**
     * Get the total number of parameters in the parameter tree
     *
     * @return parameter count
     */
    @JsonIgnore
    public int getNumberOfParams () {
        DefaultMutableTreeNode ParaTree = this.getParamTree();
        if (ParaTree == null) {
            return 0;
        }
        Enumeration nodes = ParaTree.preorderEnumeration();
        int count = 0;
        while (nodes.hasMoreElements()) {
            nodes.nextElement(); 
            count ++;
        }
        return count;
    }

    /**
     * Parse the list file (for models or weathers) and return result in an
     * ArrayList. The format of a list file must be one input file in each line.
     * "#" and "!" can be used for comment lines.
     * @param dir Directory of the list file
     * @param fn File name
     * @return File list in an List
     */
    protected ArrayList<String> parseListFile (String dir, String fn) {
        ArrayList<String> list = new ArrayList<>();
        try (BufferedReader fr = new BufferedReader(new FileReader(dir + fn))) {
            String line = fr.readLine();
            while (line != null) {
                if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
                if (line.contains("!")) line = line.substring(0, line.indexOf("!")).trim();
                if (line.length() > 0) list.add(line);
                line = fr.readLine();
            }
            fr.close();
        } catch (Exception ex) {
            logger.error("Error reading from list file " + dir + fn, ex);
        }
        return list;
    }

    /**
     * Import parameters in a CSV table (#-commented) and create a new single-branch tree
     * @param file File name of the table
     * @return import successful or not
     */
    public boolean importParameterTableFile (File file) {
        String [][] table = CsvUtil.parseCSVwithComments(file);
        if (table != null) {
            Parameters = new ArrayList<> ();
            for (String[] row : table) {
                if (row.length >= 8) {
                    Parameters.add(new ParameterItem (this, row));
                }
            }
            addParameterListAsBranch (null, Parameters);
            return true;
        }
        return false;
    }
    
    /**
     * Import parameters in a CSV table (#-commented) and create a new single-branch tree
     * @param file File name of the table
     * @return import successful or not
     */
    public boolean exportParameterTableFile (File file) {
        // Get all parameters in the first branch
        if (ParamTree != null) {
            try (PrintWriter fw = new PrintWriter (new FileWriter (file))) {
                fw.println("# Parameter list for project: " + this.getProjectID() + " (exported at " + new SimpleDateFormat().format(new Date()) + ")");
                fw.println("# Note: this list contains only the first branch of the parameter tree.");
                fw.println("# Parameter definitions in a csv file. Column headings are as below");
                fw.println("# ID, Name, Parameter Type, Description, Search String, Value Type, Value String, Selected Value Index");
                fw.println("#           {0}                                         {0, 1, 2}                 {0, .... depending on number of values}");
                fw.println("# ");
                DefaultMutableTreeNode thisleaf = ParamTree.getFirstLeaf();
                Object[] path = thisleaf.getUserObjectPath();
                for (Object obj : path) {
                    ParameterItem item = (ParameterItem) obj;
                    fw.println(item.toCSVrow());
                }
                return true;
            }catch (Exception ex) {
                logger.error ("Error writing parameter table to file " + file.getAbsolutePath(), ex);
            }
        }
        return false;
    }
    
    /**
     * Add a list of parameter items as a branch at the given root node
     * @param root Root where the new branch is attached
     * @param list The list of parameter items
     */
    public void addParameterListAsBranch (DefaultMutableTreeNode root, List<ParameterItem> list) {
        if (list != null && list.size() > 0) {
            if (root == null) { // replace current tree
                ParamTree = new DefaultMutableTreeNode (list.get(0));
                DefaultMutableTreeNode current = ParamTree;
                for (int i=1; i<list.size(); i++) {
                    DefaultMutableTreeNode newnode = new DefaultMutableTreeNode (list.get(i));
                    current.add(newnode);
                    current = newnode;
                }
            }else {
                DefaultMutableTreeNode current = root;
                for (ParameterItem item : list) {
                    DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(item);
                    current.add(newnode);
                    current = newnode;
                }
            }
        }
    }
    
    public String [][] getLHSJobList (int LHSsize, Random randomsrc) {
        
        if (randomsrc == null) randomsrc = RandomSource.getRandomGenerator();
        
        String [][] JobList = new String [LHSsize][];
        
        // Get all parameters (inc. idf and weather) and their distributions
        if (ParamTree != null) {
            // Create sample for each parameter
            String [][] SampledValues = getSampleInEqualProbSegments(LHSsize, randomsrc);
            // debug
            logger.debug(Arrays.deepToString(SampledValues));
            //
            int length = SampledValues.length;
            // Shuffle the sample value vector of each parameter
            for (int i=1; i<length; i++) {
                Collections.shuffle(Arrays.asList(SampledValues[i]), randomsrc);
            }
            // n jobs are created by taking a value from each parameter's vector 
            // sequentially
            for (int i=0; i<LHSsize; i++) {
                JobList[i] = new String [length];
                JobList[i][0] = new Formatter().format("LHS-%06d", i).toString();  // Job id
                for (int j=1; j<length; j++) {
                    JobList[i][j] = SampledValues[j][i];
                }
            }
            return JobList;
        }
        return null;
    }
    
    public String [][] getSobolJobList (int LHSsize, Random randomsrc) {
        
        if (randomsrc == null) randomsrc = RandomSource.getRandomGenerator();
        
        String [][] JobList = new String [LHSsize][];
        
        // Get all parameters (inc. idf and weather) and their distributions
        if (ParamTree != null) {
            // Create sample for each parameter
            String [][] SampledValues = getSampleInEqualProbSegments(LHSsize, randomsrc);
            int length = SampledValues.length;
            // Generate Sobol sequence
            SobolSequenceGenerator SSG = new SobolSequenceGenerator(length - 1);
            // SSG.skipTo(1000);
            // Shuffle the sample value vector of each parameter
//            for (int i=1; i<length; i++) {
//                Collections.shuffle(Arrays.asList(SampledValues[i]), randomsrc);
//            }
            // n jobs are created by taking a value from each parameter's vector 
            // sequentially
            for (int i=0; i<LHSsize; i++) {
                double [] vector = SSG.nextVector();
                JobList[i] = new String [length];
                JobList[i][0] = new Formatter().format("SOBOL-%06d", i).toString();  // Job id
                for (int j=1; j<length; j++) {
                    JobList[i][j] = SampledValues[j][Math.round((float)vector[j-1] * LHSsize)];
                }
            }
            return JobList;
        }
        return null;
    }
    
    /**
     * 
     * @param sampleSize
     * @param randomsrc
     * @return 
     */
    private String [][] getSampleInEqualProbSegments (int sampleSize, Random randomsrc) {
        DefaultMutableTreeNode thisleaf = ParamTree.getFirstLeaf();
        Object[] path = thisleaf.getUserObjectPath();
        int length = path.length + 3; // tree depth plus JobID (reserved space), IDF and Weather
        String [][] SampledValues = new String [length][];
        int n_alt;
        // First element is reserved for job id
        // Weather
        n_alt = this.parseFileListString(this.resolveWeatherDir(), this.getWeatherFile()).size();
        int [] SampledIndex = this.defaultLHSdiscreteSample(sampleSize, n_alt, randomsrc);
        SampledValues [1] = new String [sampleSize];
        for (int j=0; j<sampleSize; j++) {
            SampledValues[1][j] = Integer.toString(SampledIndex[j]);
        }
        // IDF
        n_alt = this.parseFileListString(this.resolveIDFDir(), this.getIDFTemplate()).size();
        SampledIndex = this.defaultLHSdiscreteSample(sampleSize, n_alt, randomsrc);
        SampledValues [2] = new String [sampleSize];
        for (int j=0; j<sampleSize; j++) {
            SampledValues[2][j] = Integer.toString(SampledIndex[j]);
        }

        // Parameters
        for (int i=3; i<length; i++) {
            ParameterItem Param = ((ParameterItem) path[i-3]);
            if (Param.getValuesString().startsWith("@sample")) {
                // A distribution definition
                SampledValues [i] = this.defaultLHSdistributionSample(sampleSize, Param.getValuesString(), Param.getType(), randomsrc);
            }else {
                // distribution undefined; normal parameter
                n_alt = Param.getNAltValues();
                SampledIndex = this.defaultLHSdiscreteSample(sampleSize, n_alt, randomsrc);
                SampledValues [i] = new String [sampleSize];
                for (int j=0; j<sampleSize; j++) {
                    SampledValues[i][j] = Param.getAlternativeValues()[SampledIndex[j]];
                }
            }
        }
        return SampledValues;
    }

    private int [] defaultLHSdiscreteSample (int n, int n_alt, Random randomsrc) {
        int [] index = new int [n];
        if (n_alt > 1) {
            RouletteWheel Wheel = new RouletteWheel (n_alt, randomsrc);
            for (int j=0; j<n; j++) {
                index[j] = Wheel.spin(j*Wheel.getTotalWidth()/n, (j+1)*Wheel.getTotalWidth()/n);
            }
        }else {
            for (int j=0; j<n; j++) index[j] = 0;
        }
        return index;
    }
    
    private String [] defaultLHSdistributionSample(int n, String funcstr, int type, Random randomsrc) {
        // Trim off brackets
        int start = funcstr.indexOf("(") + 1;
        int end = funcstr.indexOf(")");
        funcstr = funcstr.substring(start, end).trim();
        
        ArrayList <String> list = new ArrayList <>();
        String [] params = funcstr.split("\\s*,\\s*");
        // For integer/double types, returns randomized N samples conforming
        // a specified distribution, currently 'gaussian'/'normal'/'n', 
        // 'uniform'/'u', 'triangular'/'tr', or 'discrete'/'d'
        // for examples: @sample(gaussian, 0, 1.5, 20), with mean, sd and N
        //           or  @sample(uniform, -10, 10, 20), with lb, ub and N
        //           of  @sample(triangular, -1.0, 0.3, 1.0, 20), with lb, mode, ub and N
        //           of  @sample(discrete, option_A, 0.3, option_B, 0.5, option_C, 0.2, 20), with lb, mode, ub and N
        String distribution = params[0].toLowerCase();
        switch (distribution) {
            case "uniform":
            case "u":
                // requires lb, ub, n
                double lb = Double.parseDouble(params[1]);
                double ub = Double.parseDouble(params[2]);
                for (int i=0; i<n; i++) {
                    if (type == ParameterItem.DOUBLE) {
                        double bin = (ub - lb) / n;
                        double v = randomsrc.nextDouble() * bin + lb + i * bin;
                        list.add(Double.toString(v));
                    }else if (type == ParameterItem.INTEGER) {
                        double bin = (ub + 1. - lb) / n;
                        double v = randomsrc.nextDouble() * bin + lb + i * bin;
                        list.add(Integer.toString((int)Math.floor(v)));
                    }
                }
                break;
            case "gaussian":
            case "normal":
            case "n":
                {
                    // requires mean, sd, n
                    double mean = Double.parseDouble(params[1]);
                    double sd = Double.parseDouble(params[2]);
                    NormalDistribution Dist = new NormalDistribution (mean, sd);
                    double bin = 1.0 / n;
                    for (int i=0; i<n; i++) {
                        double a = Dist.inverseCumulativeProbability((i == 0) ? bin/10 : i*bin);            // lb of each bin
                        double b = Dist.inverseCumulativeProbability((i == n-1) ? 1.-bin/n : (i+1)*bin);    // ub of each bin
                        double v = randomsrc.nextDouble() * (b - a) + a;
                        if (type == ParameterItem.DOUBLE) {
                            list.add(Double.toString(v));
                        }else if (type == ParameterItem.INTEGER) {
                            // Warning: for integer, binomial distribution should be used.
                            // the following function is provided just for convenience
                            list.add(Long.toString(Math.round(v)));
                        }
                    }
                    break;
                }
            case "lognormal":
            case "ln":
                {
                    // requires mean, sd, n
                    double mean = Double.parseDouble(params[1]);
                    double sd = Double.parseDouble(params[2]);
                    LogNormalDistribution Dist = new LogNormalDistribution (mean, sd);
                    double bin = 1.0 / n;
                    for (int i=0; i<n; i++) {
                        double a = Dist.inverseCumulativeProbability((i == 0) ? bin/10 : i*bin);            // lb of each bin
                        double b = Dist.inverseCumulativeProbability((i == n-1) ? 1.-bin/n : (i+1)*bin);    // ub of each bin
                        double v = randomsrc.nextDouble() * (b - a) + a;
                        if (type == ParameterItem.DOUBLE) {
                            list.add(Double.toString(v));
                        }else if (type == ParameterItem.INTEGER) {
                            // Warning: for integer, binomial distribution should be used.
                            // the following function is provided just for convenience
                            list.add(Long.toString(Math.round(v)));
                        }
                    }
                    break;
                }
            case "exponential":
            case "e":
                {
                    // requires mean, sd, n
                    double mean = Double.parseDouble(params[1]);
                    ExponentialDistribution Dist = new ExponentialDistribution (mean);
                    double bin = 1.0 / n;
                    for (int i=0; i<n; i++) {
                        double a = Dist.inverseCumulativeProbability((i == 0) ? bin/10 : i*bin);            // lb of each bin
                        double b = Dist.inverseCumulativeProbability((i == n-1) ? 1.-bin/n : (i+1)*bin);    // ub of each bin
                        double v = randomsrc.nextDouble() * (b - a) + a;
                        if (type == ParameterItem.DOUBLE) {
                            list.add(Double.toString(v));
                        }else if (type == ParameterItem.INTEGER) {
                            // Warning: for integer, binomial distribution should be used.
                            // the following function is provided just for convenience
                            list.add(Long.toString(Math.round(v)));
                        }
                    }
                    break;
                }
            case "triangular":
            case "tr":
                {
                    // requires a(lb), c(mode), b(ub), n
                    double a = Double.parseDouble(params[1]);
                    double c = Double.parseDouble(params[2]);
                    double b = Double.parseDouble(params[3]);
                    TriangularDistribution Dist = new TriangularDistribution (a, c, b);
                    double bin = 1.0 / n;
                    for (int i=0; i<n; i++) {
                        a = Dist.inverseCumulativeProbability(i * bin);         // lb of each bin
                        b = Dist.inverseCumulativeProbability((i + 1) * bin);   // ub of each bin
                        double v = randomsrc.nextDouble() * (b - a) + a;
                        if (type == ParameterItem.DOUBLE) {
                            list.add(Double.toString(v));
                        }else if (type == ParameterItem.INTEGER) {
                            // Warning: for integer, user defined discrete distribution should be used.
                            // the following function is provided just for convenience
                            list.add(Long.toString(Math.round(v)));
                        }
                    }
                    break;
                }
            case "discrete":
            case "d":
                {
                    // requires op1, prob1, op2, prob2, ..., n
                    int nOptions = params.length / 2 - 1;
                    String [] options = new String [nOptions];
                    double [] probabilities = new double [nOptions];
                    double sum = 0;
                    for (int i=0; i<nOptions; i++) {
                        options[i] = params[2*i+1];
                        try {
                            probabilities[i] = Double.parseDouble(params[2*i+2]);
                        }catch (NumberFormatException nfe) {
                            probabilities[i] = 0.1;
                        }
                        sum += probabilities[i];
                    }
                    RouletteWheel Wheel = new RouletteWheel (probabilities, randomsrc);
                    double bin = sum / n;
                    for (int i=0; i<n; i++) {
                        double a = i * bin;   // lb of each bin
                        double b = (i + 1) * bin;         // ub of each bin
                        int sel = Wheel.spin(a, b);
                        list.add(options[sel]);
                    }
                    break;
                }
            case "custom":
                break;
        }
        return list.toArray(new String [0]);
    }
}
