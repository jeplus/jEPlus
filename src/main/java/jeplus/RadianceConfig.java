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
 * <p>Title: jEPlus - Radiance configurations </p> 
 * <p>Description: Strings and constants for Radiance configuration </p> 
 * <p>Copyright: Copyright (c) 2006-2015, Yi Zhang</p> 
 * @author Yi Zhang
 * @version 1.6
 * @since 1.6
 */
public class RadianceConfig extends ConfigFileNames {

    /** Logger */
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RadianceConfig.class);

    // File type contants, starting from 60
    public static final int RAY = 60;
    public static final int RAD = 61;
    public static final int OCT = 62;
    
    /**
     * Default file extensions
     */
    private static final String RadExt = ".rad";
    private static final String OctreeExt = ".oct";
    /**
     * Radiance and DaySim settings
     */
    protected String RadianceBinDir = null;
    protected String RadianceLibDir = null;
    protected String DaySimBinDir = null;
    protected String DaySimLibDir = null;

    public RadianceConfig() {
        super ();
        ScreenFile = null;
    }

    public String getRadianceBinDir() {
        return RadianceBinDir;
    }

    public void setRadianceBinDir(String RadianceBinDir) {
        this.RadianceBinDir = RadianceBinDir;
    }

    public String getRadianceLibDir() {
        return RadianceLibDir;
    }

    public void setRadianceLibDir(String RadianceLibDir) {
        this.RadianceLibDir = RadianceLibDir;
    }

    public String getDaySimBinDir() {
        return DaySimBinDir;
    }

    public void setDaySimBinDir(String DaySimBinDir) {
        this.DaySimBinDir = DaySimBinDir;
    }

    public String getDaySimLibDir() {
        return DaySimLibDir;
    }

    public void setDaySimLibDir(String DaySimLibDir) {
        this.DaySimLibDir = DaySimLibDir;
    }

    /**
     * Get Radiance Bin Directory in absolute form
     * @return 
     */
    @JsonIgnore
    public String getResolvedRadianceBinDir() {
        String dir = RelativeDirUtil.checkAbsolutePath(RadianceBinDir, UserBaseDir);
        dir = dir.concat(dir.endsWith(File.separator) ? "" : File.separator);
        return dir;
    }
    
    /**
     * Get Radiance Lib Directory in absolute form
     * @return 
     */
    @JsonIgnore
    public String getResolvedRadianceLibDir() {
        String dir = RelativeDirUtil.checkAbsolutePath(RadianceLibDir, UserBaseDir);
        dir = dir.concat(dir.endsWith(File.separator) ? "" : File.separator);
        return dir;
    }
    
    /**
     * Get DaySim Bin Directory in absolute form
     * @return 
     */
    @JsonIgnore
    public String getResolvedDaySimBinDir() {
        String dir = RelativeDirUtil.checkAbsolutePath(DaySimBinDir, UserBaseDir);
        dir = dir.concat(dir.endsWith(File.separator) ? "" : File.separator);
        return dir;
    }
    
    /**
     * Get DaySim Lib Directory in absolute form
     * @return 
     */
    @JsonIgnore
    public String getResolvedDaySimLibDir() {
        String dir = RelativeDirUtil.checkAbsolutePath(DaySimLibDir, UserBaseDir);
        dir = dir.concat(dir.endsWith(File.separator) ? "" : File.separator);
        return dir;
    }
    
    public boolean loadFromFile(String fn) {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(fn));
            RadianceBinDir = prop.getProperty("RadianceBinDir", null);
            RadianceLibDir = prop.getProperty("RadianceLibDir", null);
            DaySimBinDir = prop.getProperty("DaySimBinDir", null);
            DaySimLibDir = prop.getProperty("DaySimLibDir", null);
        } catch (FileNotFoundException fnfe) {
            logger.error("Specified configue file " + fn + " is not found.", fnfe);
            return false;
        }catch (Exception ex) {
            logger.error("Error loading configure file " + fn, ex);
            return false;
        }
        return true;
    }

    /**
     * Get a <code>javax.swing.filechooser.FileFilter</code> for predefined file 
     * types.
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
                String extension = getFileExtension(f);
                if (extension != null) {
                    extension = extension.toLowerCase();
                    switch (type) {
                        case RAD:
                            return extension.equals(RadExt);
                        case OCT:
                            return extension.equals(OctreeExt);
                        case RAY:
                            return extension.equals(OctreeExt) || extension.equals(RadExt);
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
                    case RAD:
                        return "Radiance scene file (*.rad)";
                    case OCT:
                        return "Radiance Octree file (*.oct)";
                    case RAY:
                        return "Radiance files (*.rad *.oct)";
                    default:
                        return ConfigFileNames.getFileFilter(type).getDescription();
                }

            }
        };
        return ff;
    }
}
