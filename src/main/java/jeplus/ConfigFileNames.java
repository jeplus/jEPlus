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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.filechooser.FileFilter;
import jeplus.event.IF_ConfigChangedEventHandler;
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
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
//              include = JsonTypeInfo.As.PROPERTY,
//              property = "type")
//@JsonSubTypes({
//    @Type(value = EPlusConfig.class, name = "E+"),
//    @Type(value = TRNSYSConfig.class, name = "TRNSYS"),
//    @Type(value = INSELConfig.class, name = "INSEL"),
//    @Type(value = RadianceConfig.class, name = "Radiance")
//    })

public abstract class ConfigFileNames implements Serializable {

    /** Logger */
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigFileNames.class);

    /** This is the working directory of the program */
    protected static String UserBaseDir = System.getProperty("user.dir") + File.separator;
    public static String getUserBaseDir() { return UserBaseDir; }
    
    // Generic File type constants
    public static final int LIST = 18;
    public static final int PBS = 19;
    public static final int CFG = 20;
    public static final int JEP = 21;
    public static final int ZIP = 22;
    public static final int JSON = 23;
    public static final int JEP_OR_JSON = 29;
    public static final int XML = 25;
    public static final int RVX = 26;
    public static final int CSV = 27;
    public static final int OBJ = 14;
    public static final int EPUSERTXT = 15;
    public static final int ALL = 28;

    public static final int PYTHON = 101;
    public static final int RUBY = 102;
    public static final int PHP = 103;
    public static final int R = 104;
    public static final int JS = 105;
    public static final int PERL = 106;
    public static final int SH = 107;

    /** Configuration changed event listener */
    protected ArrayList<IF_ConfigChangedEventHandler> Listeners = new ArrayList<> ();
    public void addListener (IF_ConfigChangedEventHandler listener) { if (! Listeners.contains(listener)) Listeners.add(listener); }
    public void removeListener (IF_ConfigChangedEventHandler listener) { Listeners.remove(listener); }
    public void removeAllListeners () { Listeners.clear(); }
    public void fireConfigChangedEvent () {
        for (IF_ConfigChangedEventHandler item : Listeners) {
            if (item != null) { item.configChanged(this); }
        }
    }

    /** File name for capturing console output */
    protected String ScreenFile = "console.log";

    public ConfigFileNames () {
    }
    
    /** 
     * get Screen capture file name
     * @return  
     */
    public String getScreenFile () {
        return ScreenFile;
    }

    /** 
     * set Screen capture file name
     * @param ScreenFile 
     */
    public void setScreenFile(String ScreenFile) {
        this.ScreenFile = ScreenFile;
        fireConfigChangedEvent ();
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
                if (extension != null) {
                    extension = extension.toLowerCase();
                    switch (type) {
                        case PBS:
                            return (extension.equalsIgnoreCase(".pbs"));
                        case CFG:
                            return (extension.equalsIgnoreCase(".cfg"));
                        case JEP:
                            return (extension.equalsIgnoreCase(".jep"));
                        case ZIP:
                            return (extension.equalsIgnoreCase(".zip"));
                        case JSON:
                            return (extension.equalsIgnoreCase(".json"));
                        case JEP_OR_JSON:
                            return (extension.equalsIgnoreCase(".json") || extension.equalsIgnoreCase(".jep"));
                        case XML:
                            return (extension.equalsIgnoreCase(".xml"));
                        case RVX:
                            return (extension.equalsIgnoreCase(".rvx"));
                        case CSV:
                            return (extension.equalsIgnoreCase(".csv"));
                        case OBJ:
                            return (extension.equalsIgnoreCase(".obj"));
                        case EPUSERTXT:
                            return (extension.equalsIgnoreCase(".txt"));
                        case PYTHON:
                            return (extension.equalsIgnoreCase(".py"));
                        case RUBY:
                            return (extension.equalsIgnoreCase(".rb"));
                        case PHP:
                            return (extension.equalsIgnoreCase(".php"));
                        case R:
                            return (extension.equalsIgnoreCase(".r"));
                        case JS:
                            return (extension.equalsIgnoreCase(".js"));
                        case PERL:
                            return (extension.equalsIgnoreCase(".pl"));
                        case SH:
                            return (extension.equalsIgnoreCase(".sh"));
                        case LIST:
                            return (
                                    extension.equalsIgnoreCase(".lst")
                                    || extension.equalsIgnoreCase(".csv")
                                    || extension.equalsIgnoreCase(".txt")
                            );
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
                    case PBS:
                        return "PBS script for individual E+ jobs (.pbs)";
                    case CFG:
                        return "JobServer configuration file (.cfg)";
                    case JEP:
                        return "JEPlus v1.x Project file (.jep)";
                    case ZIP:
                        return "Zipped jEPlus Project input files (.zip)";
                    case JSON:
                        return "JEPlus JSON project file (.json)";
                    case JEP_OR_JSON:
                        return "JEPlus project file v2.x JSON (.json) or v1.x JEP (.jep)";
                    case XML:
                        return "XML document (.xml)";
                    case RVX:
                        return "JSON RVX (.rvx)";
                    case CSV:
                        return "CSV files as result or parameter tables (.csv)";
                    case OBJ:
                        return "JEPlus v0.5 Project object file (.obj)";
                    case EPUSERTXT:
                        return "jE+ user project export file (.txt)";
                    case PYTHON:
                        return "Python script file (.py)";
                    case RUBY:
                        return "Ruby script file (.rb)";
                    case PHP:
                        return "PHP script file (.php)";
                    case R:
                        return "R script file (.r)";
                    case JS:
                        return "JavaScript file (.js)";
                    case PERL:
                        return "Perl script file (.pl)";
                    case SH:
                        return "Shell script file (.sh)";
                    case LIST:
                        return "Text file containing a list (*.lst *.csv *.txt)";
                    case ALL:
                        return "All files (*.*)";
                    default:
                        logger.warn("Filter not specified for type " + type);
                        return "Filter not implemented";
                }

            }
        };
        return ff;
    }

}
