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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import jeplus.data.VersionInfo;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yzhang
 */
public class JEPlusConfig extends ConfigFileNames {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JEPlusConfig.class);
    
    /** Static instance of configuration */
    public static JEPlusConfig Config = new JEPlusConfig ();
    public static JEPlusConfig getDefaultInstance () {return Config;}
    public static void setDefaultInstance (JEPlusConfig config) {Config = config;}
    public static JEPlusConfig getNewInstance (String fn) {
        JEPlusConfig cfg = null;
        try {
            cfg = loadFromJSON(new File (fn));
        }catch (IOException ioe) {
            logger.error("Error loading tools config from " + fn, ioe);
            cfg = new JEPlusConfig();
        }
        return cfg;
    }

    /** Reference to configure file */
    public static String DefaultConfigFile = "tools.json";
    public static String getDefaultConfigFile () { return DefaultConfigFile; }
    public static void setDefaultConfigFile (String fn) { DefaultConfigFile = fn; }

    /** EPlus configurations */
    protected List<EPlusConfig> EPlusList = new ArrayList<>();
    /** TRNSYS configurations */
    protected List<TRNSYSConfig> TRNSYSList = new ArrayList<>();
    /** INSEL configurations */
    protected List<INSELConfig> INSELList = new ArrayList<>();
    /** Radiance configurations */
    protected List<RadianceConfig> RadianceList = new ArrayList<>();
    /** Radiance configurations */
    protected List<ScriptConfig> ScriptList = new ArrayList<>();
    /** EPlus configurations */
    protected transient TreeMap<VersionInfo, EPlusConfig> EPlusConfigs = new TreeMap<>();
    /** TRNSYS configurations */
    protected transient TreeMap<String, TRNSYSConfig> TRNSYSConfigs = new TreeMap<>();
    /** INSEL configurations */
    protected transient TreeMap<String, INSELConfig> INSELConfigs = new TreeMap<>();
    /** Radiance configurations */
    protected transient TreeMap<String, RadianceConfig> RadianceConfigs = new TreeMap<>();
    /** Radiance configurations */
    protected transient TreeMap<String, ScriptConfig> ScripConfigs = new TreeMap<>();
    /** Current selected EPlus Config */
    protected transient EPlusConfig CurrentEPlus = null;
    /** Current selected TRNSYS Config */
    protected transient TRNSYSConfig CurrentTRNSYS = null;

    /** Recent projects */
    protected List<String> RecentProjects = new ArrayList<>();
    
    protected String EPlusVerConvDir = null;
    public String getEPlusVerConvDir() { return EPlusVerConvDir; }
    public void setEPlusVerConvDir(String EPlusVerConvDir) { this.EPlusVerConvDir = EPlusVerConvDir; fireConfigChangedEvent ();}

    protected String Python2EXE = null;
    public String getPython2EXE() { return Python2EXE; }
    public void setPython2EXE(String Python2EXE) { this.Python2EXE = Python2EXE; fireConfigChangedEvent ();}

    protected String Python3EXE = null;
    public String getPython3EXE() { return Python3EXE; }
    public void setPython3EXE(String Python3EXE) { this.Python3EXE = Python3EXE; fireConfigChangedEvent ();}

    
    
    protected String PythonArgv = null;
    public String getPythonArgv() { return PythonArgv; }
    public void setPythonArgv(String PythonArgv) { this.PythonArgv = PythonArgv; }

    protected String PythonScript = null;
    public String getPythonScript() { return PythonScript; }
    public void setPythonScript(String PythonScript) { this.PythonScript = PythonScript; }

    protected String JESSClientDir = null;
    public String getJESSClientDir() {return JESSClientDir;}
    public void setJESSClientDir(String JESSClientDir) { this.JESSClientDir = JESSClientDir; fireConfigChangedEvent ();}
    
    protected String JEPlusEADir = null;
    public String getJEPlusEADir() {return JEPlusEADir;}
    public void setJEPlusEADir(String JEPlusEADir) { this.JEPlusEADir = JEPlusEADir; fireConfigChangedEvent ();}
    

    /**
     * Default constructor
     */
    public JEPlusConfig () {
        super ();
//        EPlusConfig epcfg = new EPlusConfig();
//        EPlusList.add(epcfg);
//        EPlusConfigs.put(epcfg.getVersion(), epcfg);
//        
        TRNSYSConfig trcfg = new TRNSYSConfig();
        TRNSYSList.add(trcfg);
        TRNSYSConfigs.put("TRNSYS", trcfg);
//        
//        INSELConfig incfg = new INSELConfig();
//        INSELList.add(incfg);
//        INSELConfigs.put("INSEL", incfg);
//        
//        RadianceConfig raycfg = new RadianceConfig();
//        RadianceList.add(raycfg);
//        RadianceConfigs.put("Radiance", raycfg);
    }

    // ========= Getters and Setters =========

    public List<EPlusConfig> getEPlusList() {    
        return EPlusList;
    }

    public void setEPlusList(List<EPlusConfig> EPlusList) {
        this.EPlusList = EPlusList;
    }

    public List<TRNSYSConfig> getTRNSYSList() {
        return TRNSYSList;
    }

    public void setTRNSYSList(List<TRNSYSConfig> TRNSYSList) {
        this.TRNSYSList = TRNSYSList;
    }

    public List<INSELConfig> getINSELList() {
        return INSELList;
    }

    public void setINSELList(List<INSELConfig> INSELList) {
        this.INSELList = INSELList;
    }

    public List<RadianceConfig> getRadianceList() {
        return RadianceList;
    }

    public void setRadianceList(List<RadianceConfig> RadianceList) {
        this.RadianceList = RadianceList;
    }

    public List<ScriptConfig> getScriptList() {
        return ScriptList;
    }

    public void setScriptList(List<ScriptConfig> ScriptList) {
        this.ScriptList = ScriptList;
    }

    @JsonIgnore
    public TreeMap<VersionInfo, EPlusConfig> getEPlusConfigs() {    
        return EPlusConfigs;
    }

    @JsonIgnore
    public void setEPlusConfigs(TreeMap<VersionInfo, EPlusConfig> EPlusConfigs) {
        this.EPlusConfigs = EPlusConfigs;
    }

    @JsonIgnore
    public TreeMap<String, TRNSYSConfig> getTRNSYSConfigs() {
        return TRNSYSConfigs;
    }

    @JsonIgnore
    public void setTRNSYSConfigs(TreeMap<String, TRNSYSConfig> TRNSYSConfigs) {
        this.TRNSYSConfigs = TRNSYSConfigs;
    }

    @JsonIgnore
    public TreeMap<String, INSELConfig> getINSELConfigs() {
        return INSELConfigs;
    }

    @JsonIgnore
    public void setINSELConfigs(TreeMap<String, INSELConfig> INSELConfigs) {
        this.INSELConfigs = INSELConfigs;
    }

    @JsonIgnore
    public TreeMap<String, RadianceConfig> getRadianceConfigs() {
        return RadianceConfigs;
    }

    @JsonIgnore
    public void setRadianceConfigs(TreeMap<String, RadianceConfig> RadianceConfigs) {
        this.RadianceConfigs = RadianceConfigs;
    }

    @JsonIgnore
    public TreeMap<String, ScriptConfig> getScripConfigs() {
        return ScripConfigs;
    }

    @JsonIgnore
    public void setScripConfigs(TreeMap<String, ScriptConfig> ScripConfigs) {
        this.ScripConfigs = ScripConfigs;
    }

    @JsonIgnore
    public EPlusConfig getCurrentEPlus() {
        return CurrentEPlus;
    }

    @JsonIgnore
    public void setCurrentEPlus(EPlusConfig CurrentEPlus) {
        this.CurrentEPlus = CurrentEPlus;
    }

    @JsonIgnore
    public TRNSYSConfig getCurrentTRNSYS() {
        return CurrentTRNSYS;
    }

    @JsonIgnore
    public void setCurrentTRNSYS(TRNSYSConfig CurrentTRNSYS) {
        this.CurrentTRNSYS = CurrentTRNSYS;
    }

    public List<String> getRecentProjects() {
        return RecentProjects;
    }

    public void setRecentProjects(List<String> RecentProjects) {    
        this.RecentProjects = RecentProjects;
    }

    // ========= End getters and setters =========

    /**
     * Save this configuration to a JSON file
     * @param file The File object associated with the file to which the contents will be saved
     * @return Successful or not
     */
    public boolean saveAsJSON (File file) {
        boolean success = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(format);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Update config lists
        EPlusList.clear();
        EPlusList.addAll(EPlusConfigs.values());
        TRNSYSList.clear();
        TRNSYSList.addAll(TRNSYSConfigs.values());
        INSELList.clear();
        INSELList.addAll(INSELConfigs.values());
        RadianceList.clear();
        RadianceList.addAll(RadianceConfigs.values());
        // Write to file
        try (FileOutputStream fw = new FileOutputStream(file); ) {
            mapper.writeValue(fw, this);
            logger.info("Configuration saved to " + file.getAbsolutePath());
        }catch (Exception ex) {
            logger.error("Error saving configuration to JSON.", ex);
            success = false;
        }
        return success;
    }
    
    /**
     * Read the configuration from the given JSON file. 
     * @param file The File object associated with the file
     * @return a new configuration instance from the file
     * @throws java.io.IOException
     */
    public static JEPlusConfig loadFromJSON (File file) throws IOException {
        // Read JSON
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        JEPlusConfig config = mapper.readValue(file, JEPlusConfig.class);
        // Construct maps
        config.EPlusConfigs.clear();
        for (EPlusConfig cfg: config.EPlusList) {
            config.EPlusConfigs.put(cfg.getVersion(), cfg);
        }
        config.TRNSYSConfigs.clear();
        for (TRNSYSConfig cfg: config.TRNSYSList) {
            config.TRNSYSConfigs.put("TRNSYS", cfg);
        }
        if (config.TRNSYSConfigs.isEmpty()) {
            config.TRNSYSConfigs.put("TRNSYS", new TRNSYSConfig());
        }
        config.INSELConfigs.clear();
        for (INSELConfig cfg: config.INSELList) {
            config.INSELConfigs.put("INSEL", cfg);
        }
        if (config.INSELConfigs.isEmpty()) {
            config.INSELConfigs.put("INSEL", new INSELConfig());
        }
        config.RadianceConfigs.clear();
        for (RadianceConfig cfg: config.RadianceList) {
            config.RadianceConfigs.put("Radiance", cfg);
        }
        if (config.RadianceConfigs.isEmpty()) {
            config.RadianceConfigs.put("Radiance", new RadianceConfig());
        }
        // Return
        return config;
    }
    
    /** 
     * Clear the screen log file
     */
    public void purgeScreenLogFile () {
        if (ScreenFile != null) {
            File scrfile = new File (ScreenFile);
            if (scrfile.exists()) {
                scrfile.delete();
            }
        }
    }
    
    public EPlusConfig findMatchingEPlusConfig (String idf_ver) {
        return this.EPlusConfigs.get(new VersionInfo (idf_ver));
    }
}
