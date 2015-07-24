/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import org.slf4j.LoggerFactory;

/**
 *
 * @author yzhang
 */
public class JEPlusConfig extends INSELConfig {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(JEPlusConfig.class);

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
    
    public static JEPlusConfig buildNewInstance (String pathtobin) {
        JEPlusConfig config = new JEPlusConfig();
        config.setEPlusBinDir(pathtobin + "/");
        config.setEPlusEPMacro(pathtobin + "/" + getDefEPlusEPMacro());
        config.setEPlusEXEC(pathtobin + "/" + getDefEPlusEXEC());
        config.setEPlusExpandObjects(pathtobin + "/" + getDefEPlusExpandObjects());
        config.setEPlusReadVars(pathtobin + "/" + getDefEPlusReadVars());
        config.setScreenFile("console.log"); // no screen log by default
        return config;
    }
    
    /** Reference to configure file */
    public String CurrentConfigFile = "jeplus.cfg";
    @JsonIgnore
    public String getCurrentConfigFile () { return CurrentConfigFile; }
    
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
        TRNSYSBinDir = prop.getProperty("TRNSYSBinDir", getDefTRNSYSBinDir());
        TRNSYSEXE = prop.getProperty("TRNSYSEXE", TRNSYSBinDir + getDefTRNSYSEXEC());
        InselBinDir = prop.getProperty("InselBinDir", getDefInselBinDir());
        InselEXEC = prop.getProperty("InselEXE", InselBinDir + getDefInselEXEC());
        //NThreads = Integer.parseInt(prop.getProperty("NThreads", "0"));
        ScreenFile = prop.getProperty("ScreenFile", "console.log");
        if (ScreenFile.trim().length() == 0) {
            ScreenFile = null;
        }
        for (int i=0; i<NRecentProjs; i++) {
            RecentProjects.add(prop.getProperty("RecentProject" + i, null));
        }
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
            prop.setProperty("ScreenFile", ScreenFile);
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
