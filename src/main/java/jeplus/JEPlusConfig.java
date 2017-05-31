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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;
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
            loadFromJSON(new File (fn));
        }catch (IOException ioe) {
            logger.error("Error loading tools config from " + fn, ioe);
            cfg = new JEPlusConfig();
        }
        return cfg;
    }

    /** Reference to configure file */
    public static String DefaultConfigFile = "tools.json";

    /** EPlus configurations */
    protected TreeMap<String, EPlusConfig> EPlusConfigs = new TreeMap<>();
    /** TRNSYS configurations */
    protected TreeMap<String, TRNSYSConfig> TRNSYSConfigs = new TreeMap<>();
    /** INSEL configurations */
    protected TreeMap<String, INSELConfig> INSELConfigs = new TreeMap<>();
    /** Radiance configurations */
    protected TreeMap<String, RadianceConfig> RadianceConfigs = new TreeMap<>();

    /** Recent projects */
    protected ArrayList<String> RecentProjects = new ArrayList<>();
    
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
    }

    // ========= Getters and Setters =========

    public TreeMap<String, EPlusConfig> getEPlusConfigs() {    
        return EPlusConfigs;
    }

    public void setEPlusConfigs(TreeMap<String, EPlusConfig> EPlusConfigs) {
        this.EPlusConfigs = EPlusConfigs;
    }

    public TreeMap<String, TRNSYSConfig> getTRNSYSConfigs() {
        return TRNSYSConfigs;
    }

    public void setTRNSYSConfigs(TreeMap<String, TRNSYSConfig> TRNSYSConfigs) {
        this.TRNSYSConfigs = TRNSYSConfigs;
    }

    public TreeMap<String, INSELConfig> getINSELConfigs() {
        return INSELConfigs;
    }

    public void setINSELConfigs(TreeMap<String, INSELConfig> INSELConfigs) {
        this.INSELConfigs = INSELConfigs;
    }

    public TreeMap<String, RadianceConfig> getRadianceConfigs() {
        return RadianceConfigs;
    }

    public void setRadianceConfigs(TreeMap<String, RadianceConfig> RadianceConfigs) {
        this.RadianceConfigs = RadianceConfigs;
    }

    public ArrayList<String> getRecentProjects() {
        return RecentProjects;
    }

    public void setRecentProjects(ArrayList<String> RecentProjects) {    
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
}
