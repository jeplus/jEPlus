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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;
import javax.swing.filechooser.FileFilter;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: jEPlus - INSEL configurations </p> 
 * <p>Description: Strings and constants for INSEL v8 configuration </p> 
 * <p>Copyright: Copyright (c) 2006-2013, Yi Zhang</p> 
 * <p>Company: IESD, De Montfort University</p>
 *
 * @author Yi Zhang
 * @version 1.4
 * @since 1.4
 */
public class INSELConfig extends ConfigFileNames {

    /** Logger */
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(INSELConfig.class);

    //public static final String FileSeparator = System.getProperty("file.separator");
    
    // File type contants, starting from 50
    public static final int INSEL = 50;

    /** Bin Directory */
    private static final String InselBinDir_WIN = "C:/Program Files/insel 8/";
    /** INSEL executable file */
    private static final String InselEXEC_WIN = "resources/insel.exe"; 
    
    /**
     * UserLib Directory
     */
    private static final String InselUserLibDir = "UserLib/";
    /**
     * Default file extensions
     */
    private static final String InselExt = ".insel";
    /**
     * Default files
     */
    private static final String InselDefOutTXT = "inselout.txt";
    private static final String InselDefModel = "in.insel";
    /**
     * INSEL settings
     */
    protected String InselBinDir = null;
    protected String InselEXEC = null;

    public INSELConfig() {
        super ();
        InselBinDir = getDefInselBinDir();
        InselEXEC = InselBinDir + getDefInselEXEC();
        ScreenFile = "console.log";
    }

    public boolean loadFromFile(String fn) {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(fn));
            InselBinDir = prop.getProperty("InselBinDir", getDefInselBinDir());
            InselEXEC = prop.getProperty("InselEXE", InselBinDir + getDefInselEXEC());
            ScreenFile = prop.getProperty("ScreenFile", "console.log");
        } catch (FileNotFoundException fnfe) {
            logger.error("Specified configue file " + fn + " is not found.");
            return false;
        }catch (Exception ex) {
            logger.error("Error loading configure file " + fn, ex);
            return false;
        }
        return true;
    }

    /**
     * Get Default Bin Directory
     */
    public static String getDefInselBinDir() {
        return InselBinDir_WIN;
    }

    /**
     * Set Bin Directory
     */
    public void setInselBinDir(String dir) {
        InselBinDir = dir;
        InselEXEC = new File (InselBinDir + INSELConfig.getDefInselEXEC()).getAbsolutePath();
        fireConfigChangedEvent ();
    }

    /**
     * Get Bin Directory
     */
    @JsonIgnore
    public String getResolvedInselBinDir() {
        String dir = RelativeDirUtil.checkAbsolutePath(InselBinDir, UserBaseDir);
        dir = dir.concat(dir.endsWith(File.separator) ? "" : File.separator);
        return dir;
    }

    /**
     * Get TRNSYS executable
     */
    public static String getDefInselEXEC() {
        return InselEXEC_WIN;
    }

    /**
     * Get TRNSYS executable
     */
    public String getInselEXEC() {
        return InselEXEC;
    }

    /**
     * Set TRNSYS executable
     */
    public void setInselEXEC(String name) {
        InselEXEC = name;
        fireConfigChangedEvent ();
    }

    /**
     * Get full TRNSYS exec command path
     */
    @JsonIgnore
    public String getResolvedInselEXEC() {
        String cmd = RelativeDirUtil.checkAbsolutePath(InselEXEC, UserBaseDir);
        return cmd;
    }

    public static String getInselExt() {
        return InselExt;
    }

    public static String getInselUserLibDir() {
        return InselUserLibDir;
    }

    public static String getInselDefOutTXT() {
        return InselDefOutTXT;
    }

    public static String getInselDefModel() {
        return InselDefModel;
    }

    public String getInselBinDir() {
        return InselBinDir;
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
                        case INSEL:
                            return extension.equals(getInselExt());
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
                    case INSEL:
                        return "Insel text model file (*.insel)";
                    default:
                        return ConfigFileNames.getFileFilter(type).getDescription();
                }

            }
        };
        return ff;
    }
}
