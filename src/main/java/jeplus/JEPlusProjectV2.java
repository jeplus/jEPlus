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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.tree.DefaultMutableTreeNode;
import jeplus.data.ExecutionOptions;
import jeplus.data.ParameterItem;
import jeplus.data.ParameterItemV2;
import jeplus.data.RVX;
import jeplus.data.RVX_ScriptItem;
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
public class JEPlusProjectV2 implements Serializable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JEPlusProjectV2.class);
    
    /** ScriptEngine used by all evaluators */
    protected static final ScriptEngine Script_Engine = new ScriptEngineManager().getEngineByName("javascript");
    static {
        // Set up script engine
    }
    
    public static ScriptEngine getScript_Engine() {
        return Script_Engine;
    }

    public static enum ModelType {
        EPLUS, TRNSYS, INSEL
    }
    
    /** This is the working directory of the program */
    protected static String UserBaseDir = System.getProperty("user.dir") + File.separator;
    
    /** Flag marking whether this project has been changed since last save/load */
    transient private boolean ContentChanged = true;
    
    /** Base directory of the project, i.e. the location where the project file is saved */
    protected String BaseDir = null;
    
    /** Project Type: E+ or TRNSYS */
    protected ModelType ProjectType = ModelType.EPLUS; // set to illegal type
    
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
    protected ArrayList<ParameterItemV2> Parameters = null;
    
    /** Parameter definition file */
    protected String ParamFile = null;

    /** RVX object for result collection */
    protected RVX Rvx = null;
    
    /** ReadVarsESO configure file to be used to extract results */
    protected String RVIFile = null;

    /**
     * Default constructor
     */
    public JEPlusProjectV2 () {
        ProjectType = ModelType.EPLUS;
        ProjectID = "G";
        ProjectNotes = "New project";
        IDFDir = "./";
        // IDFTemplate = "select files ...";
        WeatherDir = "./";
        // WeatherFile = "select files ...";
        // RVIFile = "select a file ...";
        DCKDir = "./";
        // DCKTemplate = "select a file ...";
        INSELDir = "./";
        // INSELTemplate = "select a file ...";
        OutputFileNames = "trnsysout.csv";  // fixed on one file name for the time being
        ExecSettings = new ExecutionOptions ();
        Parameters = new ArrayList<> ();
        Parameters.add(new ParameterItemV2());
        BaseDir = new File ("./").getAbsolutePath() + File.separator;
        Rvx = new RVX();
    }

    /**
     * Cloning constructor. New project state is set to 'changed' after cloning
     * @param proj Project object to be cloned
     */
    public JEPlusProjectV2 (JEPlusProjectV2 proj) {
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
            DCKDir = proj.DCKDir;
            DCKTemplate = proj.DCKTemplate;
            INSELDir = proj.INSELDir;
            INSELTemplate = proj.INSELTemplate;
            OutputFileNames = proj.OutputFileNames;
            ExecSettings = new ExecutionOptions (proj.ExecSettings);
            Parameters = proj.Parameters;
            ParamFile = proj.ParamFile;
            Rvx = proj.Rvx;
            RVIFile = proj.RVIFile;
        }
    }

    /**
     * Copy from Project V1. New project state is set to 'changed' after cloning
     * @param proj Project V1 object to be copied
     */
    public JEPlusProjectV2 (JEPlusProject proj) {
        this();
        copyFromProjectV1(proj);
    }

    // ================= File operations ==============================

    /**
     * Save this project to JSON Project v2.0 file
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
        // Project changed state unaffected by the exporting to JSON
        return success;
    }

    /**
     * Read the project from the given JSON file. 
     * @param file The File object associated with the file
     * @return a new project instance from the file
     * @throws java.io.IOException
     */
    public static JEPlusProjectV2 loadFromJSON (File file) throws IOException {
        // Read JSON
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        JEPlusProjectV2 project = mapper.readValue(file, JEPlusProjectV2.class);
        // Set base dir
        String dir = file.getAbsoluteFile().getParent();
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        // project.updateBaseDir(dir);
        project.setBaseDir(dir);
        // If parameter file is given, use the contents to override the parameters in the project
        if (project.ParamFile != null) {
            // Load parameters from text file, to replace the existing Parameters list and tree
            project.importParameterTableFile(new File (RelativeDirUtil.checkAbsolutePath(project.ParamFile, dir)));
            // Reset ParamFile so it is read-once
            project.ParamFile = null;
        }
        // If external RVX file is specified, use its contents for Rvx object
        if (project.RVIFile != null) {
            try {
                project.Rvx = RVX.getRVX(project.getRVIFile(), project.getBaseDir());
                // Remove RVI file reference. With the RVX/editor being built-in now, external RVI/RVX file is only for importing (one time read)
                project.RVIFile = null;
            }catch (IOException ioe) {
                logger.error("Cannot read the project's RVX file", ioe);
            }
        }
        // Default project format remains .jep, so newly imported project is unsaved.
        project.ContentChanged = true;
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

    public ModelType getProjectType() {
        return ProjectType;
    }

    public void setProjectType(ModelType ProjectType) {
        if (this.ProjectType != ProjectType) {
            ContentChanged = true;
        }
        this.ProjectType = ProjectType;
        
    }

    public String getProjectID() {
        return ProjectID;
    }

    public void setProjectID(String ProjectID) {
        if (! Objects.equals(this.ProjectID, ProjectID)) {
            ContentChanged = true;
        }
        this.ProjectID = ProjectID;
    }

    public String getProjectNotes() {
        return ProjectNotes;
    }

    public void setProjectNotes(String ProjectNotes) {
        if (! Objects.equals(this.ProjectNotes, ProjectNotes)) {
            ContentChanged = true;
        }
        this.ProjectNotes = ProjectNotes;
    }

    public ExecutionOptions getExecSettings() {
        return ExecSettings;
    }

    public void setExecSettings(ExecutionOptions ExecSettings) {
        ContentChanged = true;
        this.ExecSettings = ExecSettings;
    }

    public String getIDFDir() {
        return IDFDir;
    }

    public void setIDFDir(String IDFDir) {
        if (! Objects.equals(this.IDFDir, IDFDir)) {
            ContentChanged = true;
        }
        this.IDFDir = IDFDir;
    }

    public String getIDFTemplate() {
        return IDFTemplate;
    }

    public void setIDFTemplate(String IDFTemplate) {
        if (! Objects.equals(this.IDFTemplate, IDFTemplate)) {
            ContentChanged = true;
        }
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

    public ArrayList<ParameterItemV2> getParameters() {
        return Parameters;
    }

    public void setParameters(ArrayList<ParameterItemV2> Parameters) {
        this.Parameters = Parameters;
    }

    public String getRVIFile() {
        return RVIFile;
    }

    public void setRVIFile(String RVIFile) {
        if (! Objects.equals(this.RVIFile, RVIFile)) {
            ContentChanged = true;
        }
        this.RVIFile = RVIFile;
    }

    public String getDCKDir() {
        return DCKDir;
    }

    public void setDCKDir(String DCKDir) {
        ContentChanged = true;
        this.DCKDir = DCKDir;
    }

    public String getDCKTemplate() {
        return DCKTemplate;
    }

    public void setDCKTemplate(String DCKTemplate) {
        ContentChanged = true;
        this.DCKTemplate = DCKTemplate;
    }

    @JsonIgnore
    public String getINSELDir() {
        return INSELDir;
    }

    @JsonIgnore
    public void setINSELDir(String INSELDir) {
        ContentChanged = true;
        this.INSELDir = INSELDir;
    }

    @JsonIgnore
    public String getINSELTemplate() {
        return INSELTemplate;
    }

    @JsonIgnore
    public void setINSELTemplate(String INSELTemplate) {
        ContentChanged = true;
        this.INSELTemplate = INSELTemplate;
    }

    public String getOutputFileNames() {
        return OutputFileNames;
    }

    public void setOutputFileNames(String OutputFileNames) {
        ContentChanged = true;
        this.OutputFileNames = OutputFileNames;
    }

    public String getWeatherDir() {
        return WeatherDir;
    }

    public void setWeatherDir(String WeatherDir) {
        if (! Objects.equals(this.ProjectNotes, ProjectNotes)) {
            ContentChanged = true;
        }
        this.WeatherDir = WeatherDir;
    }

    public String getWeatherFile() {
        return WeatherFile;
    }

    public void setWeatherFile(String WeatherFile) {
        if (! Objects.equals(this.WeatherFile, WeatherFile)) {
            ContentChanged = true;
        }
        this.WeatherFile = WeatherFile;
    }

    public String getParamFile() {
        return ParamFile;
    }

    public void setParamFile(String ParamFile) {
        ContentChanged = true;
        this.ParamFile = ParamFile;
    }

    public RVX getRvx() {
        return Rvx;
    }

    public void setRvx(RVX Rvx) {
        ContentChanged = true;
        this.Rvx = Rvx;
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
        this.getExecSettings().setParentDir(this.resolveWorkDir());        // output dir
        // Update BaseDir
        this.BaseDir = BaseDir;
        // Calculate relative dir from the new base
        this.setWeatherDir(RelativeDirUtil.getRelativePath(this.getWeatherDir(), this.BaseDir, "/"));   // Weather file path
        this.setIDFDir(RelativeDirUtil.getRelativePath(this.getIDFDir(), this.BaseDir, "/"));        // idf file path
        this.setDCKDir(RelativeDirUtil.getRelativePath(this.getDCKDir(), this.BaseDir, "/"));        // dck file path
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
     * This function checks Python dependency of the project
     * @return Python versions required in a set
     */
    @JsonIgnore
    public HashSet<String> getPythonDependency () {
        HashSet<String> versions = new HashSet<>();
        // Input scripts
        for (ParameterItemV2 item: Parameters) {
            if (item.getValuesString().toLowerCase().startsWith("@jython")) {
                versions.add("python2");
            }else if (item.getValuesString().toLowerCase().startsWith("@python2")) {
                versions.add("python2");
            }else if (item.getValuesString().toLowerCase().startsWith("@python3")) {
                versions.add("python3");
            }
        }
        // Output scripts
        if (this.Rvx.getScripts() != null) {
            for (RVX_ScriptItem item : Rvx.getScripts()) {
                versions.add(item.getPythonVersion().toLowerCase());
            }
        }
        // Return
        return versions;
    }
    
    /**
     * Copy from Project V1. New project state is set to 'changed' after cloning
     * @param proj Project V1 object to be copied
     */
    protected final void copyFromProjectV1 (JEPlusProject proj) {
        if (proj != null) {
            ContentChanged = true;  // set content changed for the new project obj
            BaseDir = proj.BaseDir;
            ProjectType = proj.ProjectType == 0 ? ModelType.EPLUS : (proj.ProjectType == 1 ? ModelType.TRNSYS : ModelType.INSEL);
            ProjectID = proj.ProjectID;
            ProjectNotes = proj.ProjectNotes;
            IDFDir = proj.IDFDir;
            IDFTemplate = proj.IDFTemplate;
            WeatherDir = proj.WeatherDir;
            WeatherFile = proj.WeatherFile;
            RVIFile = proj.RVIFile;
            DCKDir = proj.DCKDir;
            DCKTemplate = proj.DCKTemplate;
            INSELDir = proj.INSELDir;
            INSELTemplate = proj.INSELTemplate;
            OutputFileNames = proj.OutputFileNames;
            ExecSettings = new ExecutionOptions (proj.ExecSettings);
            Parameters = new ArrayList<> ();
            DefaultMutableTreeNode thisleaf = proj.getParamTree().getFirstLeaf();
            Object[] path = thisleaf.getUserObjectPath();
            for (Object obj : path) {
                Parameters.add(new ParameterItemV2((ParameterItem) obj));
            }
            Rvx = proj.Rvx;
            ParamFile = null;
            RVIFile = null;
        }
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
        // Mark content changed
        ContentChanged = true;
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
        env.RVIDir = this.BaseDir;
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
            for (String file1 : file) {
                if (file1.length() > 0) {
                    // If a list file, parse it
                    if (file1.toLowerCase().endsWith(".lst")) {
                        Files.addAll(parseListFile(dir, file1));
                        // otherwise, just add
                    } else {
                        Files.add(file1);
                    }
                }
            }
        }
        return Files;
    }

    @JsonIgnore
    public DefaultMutableTreeNode getParamTree() {
        DefaultMutableTreeNode ParamTree = null;
        if (Parameters != null && Parameters.size() > 0) {
            ParamTree = new DefaultMutableTreeNode (Parameters.get(0));
            DefaultMutableTreeNode current = ParamTree;
            for (int i=1; i<Parameters.size(); i++) {
                DefaultMutableTreeNode newnode = new DefaultMutableTreeNode (Parameters.get(i));
                current.add(newnode);
                current = newnode;
            }
        }
        return ParamTree;
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
        switch (ProjectType) {
            case TRNSYS:
                
                break;
            case INSEL:
                
                break;
            case EPLUS:
            default:
                
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
            File out = new File (ExecSettings.getWorkDir());
            if (idf.exists() && wthr.exists() && out.exists()) {
                IDFDir = RelativeDirUtil.getRelativePath(Base, idf);
                WeatherDir = RelativeDirUtil.getRelativePath(Base, wthr);
                ExecSettings.setParentDir(RelativeDirUtil.getRelativePath(Base, out));
                // Mark content changed
                ContentChanged = true;
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
        ExecSettings.setParentDir(new File (base, ExecSettings.getWorkDir()).getAbsolutePath());
        //ExecSettings.setPBSscriptFile(new File (base, ExecSettings.getPBSscriptFile()).getAbsolutePath());
        //ExecSettings.setServerConfigFile(new File (base, ExecSettings.getServerConfigFile()).getAbsolutePath());
        // Mark content changed
        ContentChanged = true;
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
            String ss = ((ParameterItemV2)((DefaultMutableTreeNode)node).getUserObject()).getSearchString();
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
                    Parameters.add(new ParameterItemV2 (row));
                }
            }
            // Mark content changed
            ContentChanged = true;
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
        try (PrintWriter fw = new PrintWriter (new FileWriter (file))) {
            fw.println("# Parameter list for project: " + this.getProjectID() + " (exported at " + new SimpleDateFormat().format(new Date()) + ")");
            fw.println("# Note: this list contains only the first branch of the parameter tree.");
            fw.println("# Parameter definitions in a csv file. Column headings are as below");
            fw.println("# ID, Name, Parameter Type, Description, Search String, Value Type, Value String, Selected Value Index");
            fw.println("#           {0}                                         {0, 1, 2}                 {0, .... depending on number of values}");
            fw.println("# ");
            for (ParameterItemV2 item : Parameters) {
                fw.println(item.toCSVrow());
            }
            return true;
        }catch (Exception ex) {
            logger.error ("Error writing parameter table to file " + file.getAbsolutePath(), ex);
        }
        return false;
    }
    
    public String [][] getLHSJobList (int LHSsize, Random randomsrc) {
        
        if (randomsrc == null) randomsrc = RandomSource.getRandomGenerator();
        
        String [][] JobList = new String [LHSsize][];
        
        // Get all parameters (inc. idf and weather) and their distributions
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
    
    public String [][] getSobolJobList (int LHSsize, Random randomsrc) {
        
        if (randomsrc == null) randomsrc = RandomSource.getRandomGenerator();
        
        String [][] JobList = new String [LHSsize][];
        
        // Get all parameters (inc. idf and weather) and their distributions
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
    
    /**
     * 
     * @param sampleSize
     * @param randomsrc
     * @return 
     */
    private String [][] getSampleInEqualProbSegments (int sampleSize, Random randomsrc) {
        Object[] path = Parameters.toArray();
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
            ParameterItemV2 Param = ((ParameterItemV2) path[i-3]);
            if (Param.getValuesString().startsWith("@sample")) {
                // A distribution definition
                SampledValues [i] = this.defaultLHSdistributionSample(sampleSize, Param.getValuesString(), Param.getType(), randomsrc);
            }else {
                // distribution undefined; normal parameter
                n_alt = Param.getNAltValues(this);
                SampledIndex = this.defaultLHSdiscreteSample(sampleSize, n_alt, randomsrc);
                SampledValues [i] = new String [sampleSize];
                for (int j=0; j<sampleSize; j++) {
                    SampledValues[i][j] = Param.getAlternativeValues(this)[SampledIndex[j]];
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
    
    private String [] defaultLHSdistributionSample(int n, String funcstr, ParameterItemV2.VType type, Random randomsrc) {
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
                    if (type == ParameterItemV2.VType.DOUBLE) {
                        double bin = (ub - lb) / n;
                        double v = randomsrc.nextDouble() * bin + lb + i * bin;
                        list.add(Double.toString(v));
                    }else if (type == ParameterItemV2.VType.INTEGER) {
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
                        if (type == ParameterItemV2.VType.DOUBLE) {
                            list.add(Double.toString(v));
                        }else if (type == ParameterItemV2.VType.INTEGER) {
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
                        if (type == ParameterItemV2.VType.DOUBLE) {
                            list.add(Double.toString(v));
                        }else if (type == ParameterItemV2.VType.INTEGER) {
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
                        if (type == ParameterItemV2.VType.DOUBLE) {
                            list.add(Double.toString(v));
                        }else if (type == ParameterItemV2.VType.INTEGER) {
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
                        if (type == ParameterItemV2.VType.DOUBLE) {
                            list.add(Double.toString(v));
                        }else if (type == ParameterItemV2.VType.INTEGER) {
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.ProjectType);
        hash = 79 * hash + Objects.hashCode(this.ProjectID);
        hash = 79 * hash + Objects.hashCode(this.ProjectNotes);
        hash = 79 * hash + Objects.hashCode(this.IDFDir);
        hash = 79 * hash + Objects.hashCode(this.IDFTemplate);
        hash = 79 * hash + Objects.hashCode(this.WeatherDir);
        hash = 79 * hash + Objects.hashCode(this.WeatherFile);
        hash = 79 * hash + Objects.hashCode(this.DCKDir);
        hash = 79 * hash + Objects.hashCode(this.DCKTemplate);
        hash = 79 * hash + Objects.hashCode(this.OutputFileNames);
        hash = 79 * hash + Objects.hashCode(this.INSELDir);
        hash = 79 * hash + Objects.hashCode(this.INSELTemplate);
        hash = 79 * hash + Objects.hashCode(this.ExecSettings);
        hash = 79 * hash + Objects.hashCode(this.Parameters);
        hash = 79 * hash + Objects.hashCode(this.ParamFile);
        hash = 79 * hash + Objects.hashCode(this.Rvx);
        hash = 79 * hash + Objects.hashCode(this.RVIFile);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JEPlusProjectV2 other = (JEPlusProjectV2) obj;
        if (!Objects.equals(this.ProjectID, other.ProjectID)) {
            return false;
        }
        if (!Objects.equals(this.ProjectNotes, other.ProjectNotes)) {
            return false;
        }
        if (!Objects.equals(this.IDFDir, other.IDFDir)) {
            return false;
        }
        if (!Objects.equals(this.IDFTemplate, other.IDFTemplate)) {
            return false;
        }
        if (!Objects.equals(this.WeatherDir, other.WeatherDir)) {
            return false;
        }
        if (!Objects.equals(this.WeatherFile, other.WeatherFile)) {
            return false;
        }
        if (!Objects.equals(this.DCKDir, other.DCKDir)) {
            return false;
        }
        if (!Objects.equals(this.DCKTemplate, other.DCKTemplate)) {
            return false;
        }
        if (!Objects.equals(this.OutputFileNames, other.OutputFileNames)) {
            return false;
        }
        if (!Objects.equals(this.INSELDir, other.INSELDir)) {
            return false;
        }
        if (!Objects.equals(this.INSELTemplate, other.INSELTemplate)) {
            return false;
        }
        if (!Objects.equals(this.ParamFile, other.ParamFile)) {
            return false;
        }
        if (!Objects.equals(this.RVIFile, other.RVIFile)) {
            return false;
        }
        if (this.ProjectType != other.ProjectType) {
            return false;
        }
        if (!Objects.equals(this.ExecSettings, other.ExecSettings)) {
            return false;
        }
        if (!Objects.equals(this.Parameters, other.Parameters)) {
            return false;
        }
        if (!Objects.equals(this.Rvx, other.Rvx)) {
            return false;
        }
        return true;
    }

  
}
