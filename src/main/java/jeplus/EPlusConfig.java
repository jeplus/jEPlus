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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Properties;
import javax.swing.filechooser.FileFilter;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: jEPlus - EnergyPlus configurations </p>
 * <p>Description: Strings and constants for EnergyPlus configuration </p>
 * <p>Copyright: Copyright (c) 2006-2010</p>
 * <p>Company: IESD, De Montfort University</p>
 * @author Yi Zhang
 * @version 0.5c
 * @since 0.1
 */
public class EPlusConfig implements Serializable {

    /** Logger */
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusConfig.class);

    /** This is the working directory of the program */
    protected static String UserBaseDir = System.getProperty("user.dir") + File.separator;
    
    //public static final String FileSeparator = System.getProperty("file.separator");
    
    // File type constants
    public static final int IDF = 0;
    public static final int EPW = 1;
    public static final int STAT = 2;
    public static final int MTR = 3;
    public static final int CSV = 4;
    public static final int ESO = 5;
    public static final int IDD = 6;
    public static final int INI = 7;
    public static final int RVI = 8;
    public static final int MVI = 9;
    public static final int EPINPUT = 10;
    public static final int EPOUTPUT = 11;
    public static final int EPEXE = 12;
    public static final int ALL = 13;
    public static final int EPUSEROBJ = 14;
    public static final int EPUSERTXT = 15;
    public static final int IMF = 16;
    public static final int JEPLUS_INTERM = 17;
    public static final int LIST = 18;
    public static final int PBS = 19;
    public static final int CFG = 20;
    public static final int JEP = 21;
    public static final int ZIP = 22;
    public static final int JSON = 23;
    public static final int PYTHON = 24;
    public static final int XML = 25;
    public static final int RVX = 26;


    /** Bin Directory */
    private static final String EPlusBinDir_WIN = "C:/EnergyPlusV8-1-0/";
    private static final String EPlusBinDir_LIN = "/usr/local/EnergyPlus-8-1-0/";

    /** Interface for EnergyPlus executable */
    private static final String EPlusEPMacro_WIN = "EPMacro.exe"; // Eplus windows EPMacro
    private static final String EPlusEPMacro_LIN = "EPMacro"; // Eplus Linux EPMacro
    private static final String EPlusExpandObjects_WIN = "ExpandObjects.exe"; // Eplus windows ExpandObjects
    private static final String EPlusExpandObjects_LIN = "ExpandObjects"; // Eplus Linux ExpandObjects
    private static final String EPlusEXEC_WIN = "EnergyPlus.exe"; // Eplus windows exec
    private static final String EPlusEXEC_LIN = "EnergyPlus"; // Eplus Linux kernel
    private static final String EPlusReadVars_WIN = "PostProcess/ReadVarsEso.exe"; // Eplus windows ReadVarEso.exe
    private static final String EPlusReadVars_LIN = "PostProcess/ReadVarsEso"; // Eplus Linux rvEsoKernel
    private static final String EPDefINI = "Energy+.ini";
    private static final String EPDefIDD = "Energy+.idd";
    private static final String EPDefIDF = "in.idf";
    private static final String EPDefIMF = "in.imf";
    private static final String EPDefIDFOUT = "out.idf";
    private static final String EPDefExpandedIDF = "expanded.idf";
    private static final String EPDefEPW = "in.epw";
    private static final String EPDefSTAT = "in.stat";
    private static final String EPDefOutESO = "eplusout.eso";
    private static final String EPDefOutCSV = "eplusout.csv";
    private static final String EPDefOutEND = "eplusout.end";
    private static final String EPDefOutSql = "eplusout.sql";
    private static final String EPDefRvi = "my.rvi";
    private static final String EPDefMvi = "my.mvi";

    /** Default file extensions */
    private static final String EPlusWeatherExt = ".epw";
    private static final String EPlusWeatherStatExt = ".stat";
    private static final String EPlusIDFExt = ".idf";
    private static final String EPlusIMFExt = ".imf";
    private static final String EPlusMtrExt = ".mtr";
    private static final String EPlusCsvExt = ".csv";
    private static final String EPlusEsoExt = ".eso";
    private static final String EPlusRviExt = ".rvi";
    private static final String EPlusMviExt = ".mvi";
    private static final String EPlusUserObjExt = ".obj";
    private static final String EPlusUserTxtExt = ".txt";
    private static final String JEPlusRvxExt = ".rvx";
    private static final String JEPlusJepExt = ".jep";

    /** EPlus batch file settings, not useful if running the executable */
    protected static final String EPlusBAT = "call_eplus.bat";
    protected static final String EPParaEPIN = "in";
    protected static final String EPParaEPOUT = "eplusout";
    protected static final String EPParaEPWTHR = "USA_FL_Tampa_TMY2.epw";
    protected static final String EPParaEPTYPE = "EP";
    protected static final String EPParaPAUSING = "N";
    protected static final String EPParaEPINEXT = "idf";
    protected static final String EPParaMAXCOL = "250";
    protected static final String EPParaCONVESO = "N";
    protected static final String EPParaPROCESV = "N";

    /** EnergyPlus settings */
    protected String EPlusBinDir = null;
    protected String EPlusEPMacroEXE = null;
    protected String EPlusEXE = null;
    protected String EPlusReadVarsEXE = null;
    protected String EPlusExpandObjectsEXE = null;
    protected String ScreenFile = "console.log";
    protected int    NThreads = 0;

    public EPlusConfig () {
        EPlusBinDir = getDefEPlusBinDir();
        EPlusEPMacroEXE = EPlusBinDir + getDefEPlusEPMacro();
        EPlusEXE = EPlusBinDir + getDefEPlusEXEC();
        EPlusReadVarsEXE = EPlusBinDir + getDefEPlusReadVars();
        EPlusExpandObjectsEXE = EPlusBinDir + getDefEPlusExpandObjects();
        ScreenFile = "console.log"; // no screen log file if set to null
    }
    
    public boolean loadFromFile (String fn) {
        Properties prop = new Properties ();
        try {
            prop.load(new FileReader (fn));
            EPlusBinDir = prop.getProperty("EPlusBinDir", getDefEPlusBinDir());
            EPlusEPMacroEXE = prop.getProperty("EPlusEPMacroEXE", EPlusBinDir + getDefEPlusEPMacro());
            EPlusExpandObjectsEXE = prop.getProperty("EPlusExpandObjectsEXE", EPlusBinDir + getDefEPlusExpandObjects());
            EPlusEXE = prop.getProperty("EPlusEXE", EPlusBinDir + getDefEPlusEXEC());
            EPlusReadVarsEXE = prop.getProperty("EPlusReadVarsEXE", EPlusBinDir + getDefEPlusReadVars());
            ScreenFile = prop.getProperty("ScreenFile", "console.log");
        }catch (FileNotFoundException fnfe) {
            logger.error("Specified configue file " + fn + " is not found.");
            return false;
        }catch (Exception ex) {
            logger.error("Error loading configure file " + fn, ex);
            return false;
        }
        return true;
    }

    /** Get Default Bin Directory */
    public static String getDefEPlusBinDir() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusBinDir_WIN;
        } else {
            return EPlusBinDir_LIN;
        }
    }

    /** Get Bin Directory */
    public String getEPlusBinDir() {
            return EPlusBinDir;
    }

    /** Get Bin Directory */
    @JsonIgnore
    public String getResolvedEPlusBinDir() {
        String dir = RelativeDirUtil.checkAbsolutePath(EPlusBinDir, UserBaseDir);
        dir = dir.concat(dir.endsWith(File.separator)?"":File.separator);
        return dir;
    }

    /** Set Bin Directory */
    public void setEPlusBinDir(String dir) {
        EPlusBinDir = dir;
    }

    /** Get Default EnergyPlus executable */
    public static String getDefEPlusEXEC() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusEXEC_WIN;
        } else {
            return EPlusEXEC_LIN;
        }
    }

    /** Get EnergyPlus executable */
    public String getEPlusEXEC() {
        return EPlusEXE;
    }

    /** Get full EPlus exec command path */
    @JsonIgnore
    public String getResolvedEPlusEXEC() {
        String cmd = RelativeDirUtil.checkAbsolutePath(EPlusEXE, UserBaseDir);
        return cmd;
    }

    /** Set EnergyPlus executable */
    public void setEPlusEXEC(String name) {
        EPlusEXE = name;
    }

    /** Get Default EnergyPlus ReadVarsESO executable */
    public static String getDefEPlusReadVars() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusReadVars_WIN;
        } else {
            return EPlusReadVars_LIN;
        }
    }

    /** Get EnergyPlus ReadVarsESO executable */
    public String getEPlusReadVars() {
        return EPlusReadVarsEXE;
    }

    /** Get full EPlus readvarseso command path */
    @JsonIgnore
    public String getResolvedReadVars() {
        String cmd = RelativeDirUtil.checkAbsolutePath(EPlusReadVarsEXE, UserBaseDir);
        return cmd;
    }

    /** Set EnergyPlus ReadVarsESO executable */
    public void setEPlusReadVars(String name) {
        EPlusReadVarsEXE = name;
    }

    /** Get Default EnergyPlus EPMacro executable */
    public static String getDefEPlusEPMacro() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusEPMacro_WIN;
        } else {
            return EPlusEPMacro_LIN;
        }
    }

    /** Get EnergyPlus EPMacro executable */
    public String getEPlusEPMacro() {
        return EPlusEPMacroEXE;
    }

    /** Get full EPlus epmacro command path */
    @JsonIgnore
    public String getResolvedEPMacro() {
        String cmd = RelativeDirUtil.checkAbsolutePath(EPlusEPMacroEXE, UserBaseDir);
        return cmd;
    }

    /** Set EnergyPlus EPMacro executable */
    public void setEPlusEPMacro(String name) {
        EPlusEPMacroEXE = name;
    }

    /** Get Default EnergyPlus ExpandObjects executable */
    public static String getDefEPlusExpandObjects() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusExpandObjects_WIN;
        } else {
            return EPlusExpandObjects_LIN;
        }
    }

    /** Get EnergyPlus ExpandObjects executable */
    public String getEPlusExpandObjects() {
        return EPlusExpandObjectsEXE;
    }

    /** Get full EPlus ExpandObjects command path */
    @JsonIgnore
    public String getResolvedExpandObjects() {
        String cmd = RelativeDirUtil.checkAbsolutePath(EPlusExpandObjectsEXE, UserBaseDir);
        return cmd;
    }

    /** Set EnergyPlus ExpandObjects executable */
    public void setEPlusExpandObjects(String name) {
        EPlusExpandObjectsEXE = name;
    }

    /** get Screen capture file name */
    public String getScreenFile () {
        return ScreenFile;
    }

    /** set Screen capture file name */
    public void setScreenFile(String ScreenFile) {
        this.ScreenFile = ScreenFile;
    }

    /** */
    public static String getEPDefINI() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefINI;
        } else {
            return EPDefINI;
        }
    }

    /** */
    public static String getEPDefIDD() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefIDD;
        } else {
            return EPDefIDD;
        }
    }

    /** */
    public static String getEPDefIDF() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefIDF;
        } else {
            return EPDefIDF;
        }
    }

    /** */
    public static String getEPDefIMF() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefIMF;
        } else {
            return EPDefIMF;
        }
    }

    /** */
    public static String getEPDefIDFOUT() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefIDFOUT;
        } else {
            return EPDefIDFOUT;
        }
    }

    /** */
    public static String getEPDefExpandedIDF() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefExpandedIDF;
        } else {
            return EPDefExpandedIDF;
        }
    }

    /** */
    public static String getEPDefEPW() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefEPW;
        } else {
            return EPDefEPW;
        }
    }

    /** */
    public static String getEPDefSTAT() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefSTAT;
        } else {
            return EPDefSTAT;
        }
    }

    /** */
    public static String getEPDefOutESO() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefOutESO;
        } else {
            return EPDefOutESO;
        }
    }

    /** */
    public static String getEPDefOutCSV() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefOutCSV;
        } else {
            return EPDefOutCSV;
        }
    }

    /** */
    public static String getEPDefOutEND() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefOutEND;
        } else {
            return EPDefOutEND;
        }
    }

    /** */
    public static String getEPDefOutSQL() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefOutSql;
        } else {
            return EPDefOutSql;
        }
    }

    /** */
    public static String getEPDefRvi() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefRvi;
        } else {
            return EPDefRvi;
        }
    }

    /** */
    public static String getEPDefMvi() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPDefMvi;
        } else {
            return EPDefMvi;
        }
    }

    public static String getEPDefDELightIn() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return "eplusout.delightin";
        } else {
            return "eplusout.delightin";
        }
    }

    public static String getEPDefDELightOut() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return "eplusout.delightout";
        } else {
            return "eplusout.delightout";
        }
    }

    /** Get default file extensions */
    public static String getEPlusWeatherExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusWeatherExt;
        } else {
            return EPlusWeatherExt;
        }
    }

    /** */
    public static String getEPlusWeatherStatExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusWeatherStatExt;
        } else {
            return EPlusWeatherStatExt;
        }
    }

    /** */
    public static String getEPlusIDFExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusIDFExt;
        } else {
            return EPlusIDFExt;
        }
    }

    /** */
    public static String getEPlusIMFExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusIMFExt;
        } else {
            return EPlusIMFExt;
        }
    }

    /** */
    public static String getEPlusMtrExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusMtrExt;
        } else {
            return EPlusMtrExt;
        }
    }

    /** */
    public static String getEPlusCsvExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusCsvExt;
        } else {
            return EPlusCsvExt;
        }
    }

    /** */
    public static String getEPlusEsoExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusEsoExt;
        } else {
            return EPlusEsoExt;
        }
    }

    /** */
    public static String getEPlusRviExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusRviExt;
        } else {
            return EPlusRviExt;
        }
    }

    /** */
    public static String getEPlusMviExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusMviExt;
        } else {
            return EPlusMviExt;
        }
    }

    /** */
    public static String getEPlusUserObjExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusUserObjExt;
        } else {
            return EPlusUserObjExt;
        }
    }

    /** */
    public static String getEPlusUserTxtExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return EPlusUserTxtExt;
        } else {
            return EPlusUserTxtExt;
        }
    }

    /** */
    public static String getJEPlusRvxExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return JEPlusRvxExt;
        } else {
            return JEPlusRvxExt;
        }
    }

    /** */
    public static String getJEPlusJepExt() {
        if (JEPlusFrameMain.osName.toLowerCase().startsWith("windows")) {
            return JEPlusJepExt;
        } else {
            return JEPlusJepExt;
        }
    }

    /**
     * Concat parameters into a proper commandline to call the EnergyPlus batch file.
     * @return The string of the commandline
     */
    public static String getCommandLine() {
        StringBuilder buf = new StringBuilder(EPlusBinDir_WIN);
        buf.append(" ").append(EPlusBAT).append(" ");
        buf.append(EPParaEPIN).append(" ");
        buf.append(EPParaEPOUT).append(" ");
        buf.append(EPParaEPWTHR).append(" ");
        buf.append(EPParaEPTYPE).append(" ");
        buf.append(EPParaPAUSING).append(" ");
        buf.append(EPParaEPINEXT).append(" ");
        buf.append(EPParaMAXCOL).append(" ");
        buf.append(EPParaCONVESO).append(" ");
        buf.append(EPParaPROCESV);

        return buf.toString();
    }

    /**
     * Concatenate parameters into a proper commandline to call the EnergyPlus batch file.
     * @param fin Input file dir and name
     * @param fout Output file directory and name
     * @param fwthr Weather file name
     * @return The string of the commandline
     */
    public static String getCommandLine(String fin, String fout, String fwthr) {
        StringBuilder buf = new StringBuilder(EPlusBinDir_WIN);
        buf.append(EPlusBAT).append(" ");
        buf.append(fin).append(" ");
        buf.append(fout).append(" ");
        buf.append(fwthr).append(" ");
        buf.append(EPParaEPTYPE).append(" ");
        buf.append(EPParaPAUSING).append(" ");
        buf.append(EPParaEPINEXT).append(" ");
        buf.append(EPParaMAXCOL).append(" ");
        buf.append(EPParaCONVESO).append(" ");
        buf.append(EPParaPROCESV);

        return buf.toString();
    }

    /**
     * Get a string contains the contents of the Energy+.INI in the default installation
     * @return A string
     */
    public static String getDefaultEPlusINI() {
        return "[program]\ndir=C:\\ENERGY~1\\\n[spark]\ndir=C:\\ENERGY~1\\sparklink\\packages\n[BasementGHT]\ndir=PreProcess\\GrndTempCalc\n[SlabGHT]\ndir=PreProcess\\GrndTempCalc";
    }

    /*
     * Get the extension (including the '.') of a file.
     */
    public static String getFileExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i).toLowerCase();
        }
        return ext;
    }

    /**
     * Get java.io.FileFilter instance
     * @param type JEPlus predefined file types
     * @return IO FileFilter of the specific type
     */
    public static java.io.FileFilter getIOFileFilter (final int type) {
        return new java.io.FileFilter() {
            @Override
            public boolean accept(File f) {
                return getFileFilter(type).accept(f);
            }
        };
    }

    /**
     * Get a <code>javax.swing.filechooser.FileFilter</code>
     * @param type Predefined JEPlus file types
     * @return Swing FileFilter of the specific type
     */
    public static FileFilter getFileFilter (final int type) {
        FileFilter ff = new FileFilter () {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String extension = getFileExtension(f);
                String filename = f.getName();
                if (extension != null) {
                    extension = extension.toLowerCase();
                    switch (type) {
                        case EPINPUT:
                            return (extension.equals(getEPlusIDFExt()) || 
                                    extension.equals(getEPlusIMFExt()) ||
                                    extension.equals(".lst"));
                        case IDF:
                            return (extension.equals(getEPlusIDFExt()));
                        case IMF:
                            return (extension.equals(getEPlusIMFExt()));
                        case EPW:
                            return (extension.equals(getEPlusWeatherExt()) ||
                                    extension.equals(".lst"));
                        case STAT:
                            return (extension.equals(getEPlusWeatherStatExt()));
                        case MTR:
                            return (extension.equals(getEPlusMtrExt()));
                        case CSV:
                            return (extension.equals(getEPlusCsvExt()));
                        case ESO:
                            return (extension.equals(getEPlusEsoExt()));
                        case RVI:
                            return (extension.equals(getEPlusRviExt()) || extension.equals(getEPlusMviExt()));
                        case MVI:
                            return (extension.equals(getEPlusMviExt()));
                        case EPUSEROBJ:
                            return (extension.equals(getEPlusUserObjExt()));
                        case EPUSERTXT:
                            return (extension.equals(getEPlusUserTxtExt()));
                        case EPOUTPUT: // EnergyPlus output files. Used for deleting E+ files
                            return ((filename.startsWith("eplusout.") &&
                                    (! filename.endsWith(".err")) &&
                                    (! filename.endsWith(".end")) &&
                                    (! filename.endsWith(".csv"))) ||
                                    filename.endsWith(".audit") ||
                                    filename.endsWith(".htm") ||
                                    (filename.endsWith(".csv") &&
                                    (! filename.startsWith("eplusout."))) ||
                                    filename.equals("audit.out"));
                        case JEPLUS_INTERM: // JEPlus intermediate files. Used for deleting
                            return (filename.equals(getEPDefIDD()) ||
                                    filename.equals(getEPDefINI()) ||
                                    filename.equals(getEPDefIDF()) ||
                                    filename.equals(getEPDefIMF()) ||
                                    filename.equals(getEPDefEPW()) ||
                                    filename.equals(getEPDefRvi()) ||
                                    filename.equals(getEPDefMvi()) ||
                                    filename.equals(getEPDefIDFOUT()) ||
                                    filename.equals(getEPDefDELightIn()) ||
                                    filename.equals(getEPDefDELightOut())
                                    );
                        case IDD:
                            return (filename.equals(getEPDefIDD()));
                        case INI:
                            return (filename.equals(getEPDefINI()));
                        case EPEXE:
                            return (filename.equals(getDefEPlusEXEC()));
                        case PBS:
                            return (extension.equals(".pbs"));
                        case CFG:
                            return (extension.equals(".cfg"));
                        case JEP:
                            return (extension.equals(".jep"));
                        case ZIP:
                            return (extension.equals(".zip"));
                        case JSON:
                            return (extension.equals(".json"));
                        case PYTHON:
                            return (extension.equals(".py"));
                        case XML:
                            return (extension.equals(".xml"));
                        case RVX:
                            return (extension.equals(".rvx"));
                        case ALL:

                        default:
                            return true;
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
                    case EPINPUT:
                        return "E+ input file (.IDF) or EPmacro input file (.IMF)";
                    case IDF:
                        return "E+ input file (.IDF)";
                    case IMF:
                        return "EPmacro input file (.IMF)";
                    case EPW:
                        return "E+ weather file (.EPW)";
                    case STAT:
                        return "E+ weather stat file (.STAT)";
                    case MTR:
                        return "E+ output meter file (.MTR)";
                    case CSV:
                        return "CSV files as result or parameter tables (.CSV)";
                    case ESO:
                        return "E+ output eso file (.ESO)";
                    case RVI:
                        return "E+ readVarsESO rvi file (.RVI) or mvi file (.MVI)";
                    case MVI:
                        return "E+ readVarsESO mvi file (.MVI)";
                    case EPUSEROBJ:
                        return "Java Object file (.OBJ)";
                    case EPUSERTXT:
                        return "jE+ user project export file (.TXT)";
                    case EPOUTPUT: // EnergyPlus output files. Used for deleting E+ files
                        return "E+ output files";
                    case JEPLUS_INTERM: // JEPlus intermediate files. Used for deleting
                        return "jEPlus intermediate files";
                    case IDD:
                        return "E+ IDD file";
                    case INI:
                        return "E+ INI file";
                    case EPEXE:
                        return "E+ main executable";
                    case ALL:
                        return "All files";
                    case PBS:
                        return "PBS script for individual E+ jobs (.pbs)";
                    case CFG:
                        return "JobServer configuration file (.cfg)";
                    case JEP:
                        return "JEPlus Project file (.jep)";
                    case ZIP:
                        return "Zipped jEPlus Project input files (.zip)";
                    case JSON:
                        return "JSON format data file (.json)";
                    case PYTHON:
                        return "Python script file (.py)";
                    case XML:
                        return "XML document (.xml)";
                    case RVX:
                        return "JSON RVX (.rvx)";
                    default:
                        return "Filter not implemented";
                }

            }
        };
        return ff;
    }

    /**
     * Get the version of E+ from the Energy+.IDD file
     * @return the version string
     */
    public String getEPlusVersion () {
        if (new File(getEPlusBinDir() + getEPDefIDD()).exists()) {
            try (BufferedReader fi = new BufferedReader (new FileReader (getEPlusBinDir() + getEPDefIDD()))) {
                String line = fi.readLine();
                while (line != null) {
                    if (line.trim().startsWith("!IDD_Version ")) {
                        fi.close();
                        return line.trim().substring(13);
                    }
                    line = fi.readLine();
                }
            }catch (Exception ex) {
                logger.error("Error parsing IDD file for E+ version info.", ex);
            }
        }
        return "Unavailable";
    }
}
