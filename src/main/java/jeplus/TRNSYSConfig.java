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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.filechooser.FileFilter;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: jEPlus - TRNSYS configurations </p> <p>Description: Strings and constants for TRNSYS configuration </p> <p>Copyright:
 * Copyright (c) 2006-2012</p> <p>Company: IESD, De Montfort University</p>
 *
 * @author José Santiago Villar
 * @version 1.3
 * @since 0.1
 */
public class TRNSYSConfig extends ConfigFileNames {

    /** Logger */
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TRNSYSConfig.class);

    //public static final String FileSeparator = System.getProperty("file.separator");
    /**
     * Bin Directory
     */
    private static final String TRNSYSBinDir_WIN = "C:/Program Files/Trnsys16_1/";
    /**
     * TRNSYS executable file
     */
    private static final String TRNSYSEXEC_WIN = "Exe/TRNexe.exe"; // TRNSYS windows exec
    
    // File types - contants start from 30
    public static final int DCK = 30;
    public static final int TRD = 31;
    public static final int TREXE = 32;
    public static final int TRDLL = 33;
    public static final int TRID = 34;
    public static final int TRTXT = 35;
    public static final int TRDAT = 36;
    public static final int TRLOG = 37;
    public static final int TRLST = 38;
    public static final int TRNINPUT = 45;
    public static final int TRNSYSOUTPUT = 47;

    /**
     * UserLib Directory
     */
    private static final String TRNSYSUserLibDir = "UserLib/";
    /**
     * Directory of Folders located in UserLib
     */
    private static final String TRNSYSDebugDir = "UserLib/DebugDLLs/";
    private static final String TRNSYSReleaseDir = "UserLib/ReleaseDLLs/";
    /**
     * Default file extensions
     */
    private static final String TRNSYSDCKExt = ".dck";
    private static final String TRNSYSTRDExt = ".trd";
    private static final String TRNSYSLSTExt = ".lst";
    private static final String TRNSYSLOGExt = ".log";
    /**
     * Default files
     */
    private static final String TRNSYSDefOutCSV = "trnsysout.csv";
    private static final String TRNSYSDefDCK = "in.dck";
    private static final String TRNSYSDefLST = "in.lst";
    private static final String TRNSYSDefLOG = "in.log";
    private static final String TRNSYSFort = "fort.";
    /**
     * TRNSYS settings
     */
    protected String TRNSYSBinDir = null;
    @JsonProperty("trnsysEXE")	
    @JsonAlias({"trnsysEXEC", "trnsysexec"})
    protected String TRNSYSEXE = null;

    /** This config is valid or not */
    protected boolean Valid = false;
    
    public TRNSYSConfig() {
        super ();
        TRNSYSBinDir = getDefTRNSYSBinDir();
        TRNSYSEXE = TRNSYSBinDir + getDefTRNSYSEXEC();
    }

    public boolean loadFromFile(String fn) {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(fn));
            TRNSYSBinDir = prop.getProperty("TRNSYSBinDir", getDefTRNSYSBinDir());
            TRNSYSEXE = prop.getProperty("TRNSYSEXE", TRNSYSBinDir + getDefTRNSYSEXEC());
            ScreenFile = prop.getProperty("ScreenFile", "console.log");
        } catch (FileNotFoundException fnfe) {
            // do nothing and reture false;
            logger.warn("TRNSYS config file " + fn + " does not exist.");
            return false;
        } catch (IOException ex) {
            logger.error("Error loading TRNSYS config from " + fn, ex);
            return false;
        }
        return true;
    }

    /**
     * Get User ID file
     * @return 
     */
    @JsonIgnore
    public String getUserID() {
        File dir = new File(TRNSYSBinDir);
        try {
            String[] children = dir.list();
            if (children == null) {
                return null;
            } else {
                String filename = null;
                for (String children1 : children) {
                    if ((children1.endsWith(".id")) && (children1.startsWith("user"))) {
                        filename = children1;
                    }
                }
                return filename;
            }
        } catch (Exception ex) {
            logger.error("Error locating TRNSYS user id file. No file named user*.id is found in " + TRNSYSBinDir, ex);
            return null;
        }
    }

    /**
     * Get the version of TRNSYS from user.id file
     *
     * @return the version string
     */
    @JsonIgnore
    public String getTRNSYSVersion() {
        String vers = null;
        try (BufferedReader fi = new BufferedReader(new FileReader(TRNSYSBinDir + getUserID()));){
            String line = fi.readLine();
            while (line != null) {
                if (line.trim().startsWith("MakeId")) {
                    vers = "Version " + line.charAt(6) + line.charAt(7) + "." + (line.trim().substring(14));
                }
                line = fi.readLine();
            }
            fi.close();
            return vers;
        } catch (IOException ex) {
            logger.error("Error extracting TRNSYS version number from the user id file " + TRNSYSBinDir + getUserID(), ex);
            return null;
        }
    }

    public List<String> validate () {
        String bindir = getResolvedTRNSYSBinDir();
        String exe = getResolvedTRNSYSEXEC();
        String userid = getUserID();

        List<String> invalid = new ArrayList<> ();
        boolean ok = new File(bindir + userid).exists();
        if (! ok) {
            invalid.add(this.getTRNSYSBinDir());
        }
        Valid = ok;
        ok = new File(exe).exists();
        if (! ok) {
            invalid.add(getTRNSYSEXE());
        }
        Valid &= ok;
        return invalid;
    }
    
    /**
     * Get Default Bin Directory
     * @return 
     */
    public static String getDefTRNSYSBinDir() {
        return TRNSYSBinDir_WIN;
    }

    /**
     * Get Bin Directory
     * @return 
     */
    public String getTRNSYSBinDir() {
        return TRNSYSBinDir;
    }

    public void setTRNSYSBinDir(String TRNSYSBinDir) {
        this.TRNSYSBinDir = TRNSYSBinDir;
    }

    @JsonIgnore
    public boolean isValid() {
        return Valid;
    }

    @JsonIgnore
    public void setValid(boolean Valid) {
        this.Valid = Valid;
    }

    /**
     * Set Bin Directory
     * @param dir
     */
    @JsonIgnore
    public void setNewTRNSYSBinDir(String dir) {
        TRNSYSBinDir = dir;
        TRNSYSEXE = new File (TRNSYSBinDir + TRNSYSConfig.getDefTRNSYSEXEC()).getAbsolutePath();
        this.validate();
        fireConfigChangedEvent ();
    }

    /**
     * Get Bin Directory
     * @return 
     */
    @JsonIgnore
    public String getResolvedTRNSYSBinDir() {
        String dir = RelativeDirUtil.checkAbsolutePath(TRNSYSBinDir, UserBaseDir);
        dir = dir.concat(dir.endsWith(File.separator) ? "" : File.separator);
        return dir;
    }

    /**
     * Get TRNSYS executable
     * @return 
     */
    public static String getDefTRNSYSEXEC() {
        return TRNSYSEXEC_WIN;
    }

    /**
     * Get TRNSYS executable
     * @return 
     */
    public String getTRNSYSEXE() {
        return TRNSYSEXE;
    }

    /**
     * Set TRNSYS executable
     * @param name
     */
    public void setTRNSYSEXE(String name) {
        TRNSYSEXE = name;
        fireConfigChangedEvent ();
    }

    /**
     * Get full TRNSYS exec command path
     * @return 
     */
    @JsonIgnore
    public String getResolvedTRNSYSEXEC() {
        String cmd = RelativeDirUtil.checkAbsolutePath(TRNSYSEXE, UserBaseDir);
        return cmd;
    }

    /**
     *
     * @return  
     */
    public static String getTRNSYSDefOutCSV() {
        return TRNSYSDefOutCSV;
    }

    /**
     *
     * @return  
     */
    public static String getTRNSYSDefDCK() {
        return TRNSYSDefDCK;
    }

    /**
     *
     * @return  
     */
    public static String getTRNSYSDefLST() {
        return TRNSYSDefLST;
    }

    /**
     *
     * @return  
     */
    public static String getTRNSYSDefLOG() {
        return TRNSYSDefLOG;
    }

    /**
     *
     * @return  
     */
    public static String getTRNSYSFort() {
        return TRNSYSFort;
    }

    /**
     *
     * @return  
     */
    public static String getTRNSYSDCKExt() {
        return TRNSYSDCKExt;
    }

    /**
     *
     * @return  
     */
    public static String getTRNSYSTRDExt() {
        return TRNSYSTRDExt;
    }

    /**
     * Get a
     * <code>javax.swing.filechooser.FileFilter</code>
     *
     * @param type Predefined JEPlus file types
     * @return Swing FileFilter of the specific type
     */
    public static FileFilter getFileFilter(final int type) {
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String filename = f.getName();
                String name = filename.split("\\s*[.]\\s*")[0] + ".";
                String extension = getFileExtension(f);
                if (extension != null) {
                    extension = extension.toLowerCase();
                    switch (type) {
                        case TRNINPUT:
                            return (extension.equals(getTRNSYSDCKExt())
                                    || extension.equals(getTRNSYSTRDExt())
                                    || extension.equals(".lst"));
                        case DCK:
                            return (extension.equals(".dck"));
                        case TRD:
                            return (extension.equals(".trd"));
                        case TREXE:
                            return (filename.equals("TRNexe.exe"));
                        case TRDLL:
                            return (extension.equals(".dll"));
                        case TRID:
                            return (extension.equals(".id"));
                        case TRTXT:
                            return (extension.equals(".txt"));
                        case TRDAT:
                            return (extension.equals(".dat"));
                        case TRLOG:
                            return (extension.equals(".log"));
                        case TRNSYSOUTPUT: // TRNSYS output files. Used for deleting
                            return (filename.equals(getTRNSYSDefLOG())
                                    || name.equals(getTRNSYSFort()));
                        case TRLST:
                            return (extension.equals(".lst"));
                        default:
                            return ConfigFileNames.getFileFilter(type).accept(f);
                    }
                }
                return false;
            }

            /**
             * Get description of a specific type
             */
            @Override
            public String getDescription() {
                switch (type) {
                    case TRNINPUT:
                        return "TRNSYS input file (.DCK) or TRNEDIT input file (.TRD)";
                    case DCK:
                        return "TRNSYS input file (.DCK)";
                    case TRD:
                        return "TRNEDIT input file (.TRD)";
                    case TREXE:
                        return "TRNSYS main executable";
                    case TRDLL:
                        return "Library file (.DLL)";
                    case TRID:
                        return "Registration Data file (.ID)";
                    case TRTXT:
                        return "Text file (.TXT)";
                    case TRDAT:
                        return "Data file (.DAT)";
                    case TRLOG:
                        return "TRNSYS output file to check problems in the running (.LOG)";
                    case TRNSYSOUTPUT:
                        return "TRNSYS output files";
                    case TRLST:
                        return "Extended TRNSYS output file to check problems in the running (.LST)";
                    default:
                        return ConfigFileNames.getFileFilter(type).getDescription();
                }

            }
        };
        return ff;
    }
}
