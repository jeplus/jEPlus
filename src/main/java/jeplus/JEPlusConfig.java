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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import static jeplus.EPlusConfig.getDefEPlusBinDir;
import jeplus.event.IF_ConfigChangedEventHandler;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yzhang
 */
public class JEPlusConfig extends RadianceConfig {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JEPlusConfig.class);
    
    protected ArrayList<IF_ConfigChangedEventHandler> Listeners = new ArrayList<> ();
    public void addListener (IF_ConfigChangedEventHandler listener) { Listeners.add(listener); }
    public void removeListener (IF_ConfigChangedEventHandler listener) { Listeners.remove(listener); }
    public void removeAllListeners () { Listeners.clear(); }
    public void fireConfigChangedEvent () {
        for (IF_ConfigChangedEventHandler item : Listeners) {
            if (item != null) { item.configChanged(Config); }
        }
    }

    protected final static int NRecentProjs = 5;
    protected static ArrayList<String> RecentProjects = new ArrayList<>();

    public static ArrayList<String> getRecentProjects() {
        return RecentProjects;
    }
    
    /** Static instance of configuration */
    public static JEPlusConfig Config = new JEPlusConfig ();
    public static JEPlusConfig getDefaultInstance () {return Config;}
    public static void setDefaultInstance (JEPlusConfig config) {Config = config;}
    
    public static JEPlusConfig getNewInstance (String fn) {
        return new JEPlusConfig(fn);
    }

    
    /** Reference to configure file */
    protected String CurrentConfigFile = "jeplus.cfg";
    @JsonIgnore
    public String getCurrentConfigFile () { return CurrentConfigFile; }
    
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

    /**
     * Construct from file
     * @param fn Configure file name
     */
    public JEPlusConfig (String fn) {
        super ();
        loadFromFile (fn);
    }

    
    // ========= Getters and Setters =========
    
    /** Set Bin Directory
     * @param dir */
    @Override
    public void setEPlusBinDir(String dir) {
        EPlusBinDir = dir;
        EPlusEPMacroEXE = EPlusBinDir + EPlusConfig.getDefEPlusEPMacro();
        EPlusExpandObjectsEXE = EPlusBinDir + EPlusConfig.getDefEPlusExpandObjects();
        EPlusEXE = EPlusBinDir + EPlusConfig.getDefEPlusEXEC();
        EPlusReadVarsEXE = EPlusBinDir + EPlusConfig.getDefEPlusReadVars();
        fireConfigChangedEvent ();
    }

    /** 
     * Set EnergyPlus executable
     * @param name 
     */
    @Override
    public void setEPlusEXEC(String name) {
        EPlusEXE = name;
        fireConfigChangedEvent ();
    }

    /** 
     * Set EnergyPlus ReadVarsESO executable
     * @param name 
     */
    @Override
    public void setEPlusReadVars(String name) {
        EPlusReadVarsEXE = name;
        fireConfigChangedEvent ();
    }

    /** 
     * Set EnergyPlus EPMacro executable
     * @param name 
     */
    @Override
    public void setEPlusEPMacro(String name) {
        EPlusEPMacroEXE = name;
        fireConfigChangedEvent ();
    }

    /** 
     * Set EnergyPlus ExpandObjects executable
     * @param name 
     */
    @Override
    public void setEPlusExpandObjects(String name) {
        EPlusExpandObjectsEXE = name;
        fireConfigChangedEvent ();
    }

    /** 
     * set Screen capture file name
     * @param ScreenFile 
     */
    @Override
    public void setScreenFile(String ScreenFile) {
        this.ScreenFile = ScreenFile;
        fireConfigChangedEvent ();
    }

    /**
     * Set Bin Directory
     * @param dir
     */
    @Override
    public void setTRNYSBinDir(String dir) {
        TRNSYSBinDir = dir;
        TRNSYSEXE = new File (TRNSYSBinDir + TRNSYSConfig.getDefTRNSYSEXEC()).getAbsolutePath();
        fireConfigChangedEvent ();
    }

    /**
     * Set TRNSYS executable
     * @param name
     */
    @Override
    public void setTRNSYSEXEC(String name) {
        TRNSYSEXE = name;
        fireConfigChangedEvent ();
    }

    /**
     * Set Bin Directory
     * @param dir
     */
    @Override
    public void setInselBinDir(String dir) {
        InselBinDir = dir;
        InselEXEC = new File (InselBinDir + INSELConfig.getDefInselEXEC()).getAbsolutePath();
        fireConfigChangedEvent ();
    }

    /**
     * Set TRNSYS executable
     * @param name
     */
    @Override
    public void setInselEXEC(String name) {
        InselEXEC = name;
        fireConfigChangedEvent ();
    }

    
    // ========= End =========
    /**
     * Load configuration from text file (java property format)
     * @param fn Configure file name
     * @return Load successful or not
     */
    @Override
    public final boolean loadFromFile (String fn) {
        Properties prop = new Properties ();
        try {
            prop.load(new FileReader (fn));
            this.CurrentConfigFile = fn;
        }catch (FileNotFoundException fnfe) {
            logger.error("Specified configue file " + fn + " is not found.");
            return false;
        }catch (Exception ex) {
            logger.error("Error loading configure file " + fn, ex);
            return false;
        }
        EPlusBinDir = prop.getProperty("EPlusBinDir", getDefEPlusBinDir());
        EPlusEPMacroEXE = prop.getProperty("EPlusEPMacroEXE", EPlusBinDir + getDefEPlusEPMacro());
        EPlusExpandObjectsEXE = prop.getProperty("EPlusExpandObjectsEXE", EPlusBinDir + getDefEPlusExpandObjects());
        EPlusEXE = prop.getProperty("EPlusEXE", EPlusBinDir + getDefEPlusEXEC());
        EPlusReadVarsEXE = prop.getProperty("EPlusReadVarsEXE", EPlusBinDir + getDefEPlusReadVars());
        EPlusVerConvDir = prop.getProperty("EPlusVerConvDir", null);
        Python2EXE = prop.getProperty("Python2EXE", null);
        Python3EXE = prop.getProperty("Python3EXE", null);
        PythonArgv = prop.getProperty("PythonArgv", null);
        PythonScript = prop.getProperty("PythonScript", null);
        JESSClientDir = prop.getProperty("JESSClientDir", null);
        JEPlusEADir = prop.getProperty("JEPlusEADir", null);
        TRNSYSBinDir = prop.getProperty("TRNSYSBinDir", getDefTRNSYSBinDir());
        TRNSYSEXE = prop.getProperty("TRNSYSEXE", TRNSYSBinDir + getDefTRNSYSEXEC());
        InselBinDir = prop.getProperty("InselBinDir", getDefInselBinDir());
        InselEXEC = prop.getProperty("InselEXE", InselBinDir + getDefInselEXEC());
        RadianceBinDir = prop.getProperty("RadianceBinDir", null);
        RadianceLibDir = prop.getProperty("RadianceLibDir", null);
        DaySimBinDir = prop.getProperty("DaySimBinDir", null);
        DaySimLibDir = prop.getProperty("DaySimLibDir", null);
        //NThreads = Integer.parseInt(prop.getProperty("NThreads", "0"));
        ScreenFile = prop.getProperty("ScreenFile", "console.log");
        if (ScreenFile.trim().length() == 0) {
            ScreenFile = null;
        }
        for (int i=0; i<NRecentProjs; i++) {
            RecentProjects.add(prop.getProperty("RecentProject" + i, null));
        }
        fireConfigChangedEvent ();
        return true;
    }

    /**
     * Save configuration to file in java property format
     * @param comment Comment line to be added to the file
     * @return Save successful or not
     */
    public boolean saveToFile (String comment) {
        Properties prop = new Properties ();
        try {
            prop.setProperty("EPlusBinDir", EPlusBinDir);
            prop.setProperty("EPlusEPMacroEXE", EPlusEPMacroEXE);
            prop.setProperty("EPlusExpandObjectsEXE", EPlusExpandObjectsEXE);
            prop.setProperty("EPlusEXE", EPlusEXE);
            prop.setProperty("EPlusReadVarsEXE", EPlusReadVarsEXE);
            prop.setProperty("TRNSYSBinDir", TRNSYSBinDir);
            prop.setProperty("TRNSYSEXE", TRNSYSEXE);
            prop.setProperty("InselBinDir", InselBinDir);
            prop.setProperty("InselEXE", InselEXEC);
            if (ScreenFile != null) {
                prop.setProperty("ScreenFile", ScreenFile);
            }
            if (EPlusVerConvDir != null) {
                prop.setProperty("EPlusVerConvDir", EPlusVerConvDir);
            }
            if (Python2EXE != null) {
                prop.setProperty("Python2EXE", Python2EXE);
            }
            if (Python3EXE != null) {
                prop.setProperty("Python3EXE", Python3EXE);
            }
            if (PythonArgv != null) {
                prop.setProperty("PythonArgv", PythonArgv);
            }
            if (PythonScript != null) {
                prop.setProperty("PythonScript", PythonScript);
            }
            if (JESSClientDir != null) {
                prop.setProperty("JESSClientDir", JESSClientDir);
            }
            if (JEPlusEADir != null) {
                prop.setProperty("JEPlusEADir", JEPlusEADir);
            }
            if (RadianceBinDir != null) {
                prop.setProperty("RadianceBinDir", RadianceBinDir);
            }
            if (RadianceLibDir != null) {
                prop.setProperty("RadianceLibDir", RadianceLibDir);
            }
            if (DaySimBinDir != null) {
                prop.setProperty("DaySimBinDir", DaySimBinDir);
            }
            if (DaySimLibDir != null) {
                prop.setProperty("DaySimLibDir", DaySimLibDir);
            }
            for (int i=0; i<Math.min(NRecentProjs, RecentProjects.size()); i++) {
                if (RecentProjects.get(i) != null) prop.setProperty("RecentProject" + i, RecentProjects.get(i));
            }
            prop.store(new FileWriter (this.CurrentConfigFile), comment);
        }catch (Exception ex) {
            logger.error("Error saving configuration to " + CurrentConfigFile, ex);
            return false;
        }
        return true;
    }

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
        // Set current file
        config.CurrentConfigFile = file.getAbsolutePath();
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
