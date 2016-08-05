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

import java.io.Serializable;
import org.slf4j.LoggerFactory;

/**
 * Class encapsulates the variables for E+ working environment
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.1
 */
public class EPlusWorkEnv implements Serializable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusWorkEnv.class);

    // Serialization version code; to maintain backwards compatibility, do not change.
    static final long serialVersionUID = -6548458146610263263L;

    /** Local directory for IDF template files */
    public String IDFDir = "./";
    /** Template file to be used in this job; or a (';' delimited) list of files for the batch project */
    public String IDFTemplate = "tmpl.idf";
    /** Local directory for weather files */
    public String WeatherDir = "./";
    /** Weather file to be used in this job; or a (';' delimited) list of files for the batch project */
    public String WeatherFile = "USA_IL_Chicago-OHare_TMY2.epw";
    /** Flag for calling ReadVarsESO or not */
    public boolean UseReadVars = true;
    /** ReadVarsESO configure file to be used to extract results */
    public String RVIDir = "./";
    /** ReadVarsESO configure file to be used to extract results */
    public String RVIFile = "my.rvi";

    /** Project Type: E+ or TRNSYS */
    protected int ProjectType = JEPlusProject.EPLUS; // set to illegal type
    /** Local directory for DCK/TRD (for TRNSYS) template files */
    protected String DCKDir = "./";
    /** Template file to be used in this job; or a (';' delimited) list of files for the batch project */
    protected String DCKTemplate = "template.dck";
    /** Local directory for INSEL (for INSEL) template files */
    protected String INSELDir = "./";
    /** Template file to be used in this job; or a (';' delimited) list of files for the batch project */
    protected String INSELTemplate = "template.insel";
    /** Output file names that contain results for each simulation; used for TRNSYS */
    protected String OutputFileNames = "trnsysout.csv";

    /** Base directory of the project */
    public String ProjectBaseDir = "./";
    /** The local parent directory to the jobs */
    public String ParentDir = "./";
    /** Whether or not to keep job directory */
    public boolean KeepJobDir = true;
    /** Whether or not to keep jEPlus intermediate files */
    public boolean KeepJEPlusFiles = true;
    /** Whether or not to keep EnergyPlus output files */
    public boolean KeepEPlusFiles = true;
    /** Selected files to delete */
    public String SelectedFiles = "";
    /** Force re-run this job even if results are available */
    public boolean ForceRerun = true;

    /** Required E+ version */
    public String EPlusVersion = "4.0";
    /** Required platform on which E+ should run */
    public int Platform = 0;

    public EPlusWorkEnv() {
    }

    public EPlusWorkEnv(EPlusWorkEnv env) {
        IDFDir = env.IDFDir;
        IDFTemplate = env.IDFTemplate;
        WeatherDir = env.WeatherDir;
        WeatherFile = env.WeatherFile;
        ProjectBaseDir = env.ProjectBaseDir;
        ParentDir = env.ParentDir;
        UseReadVars = env.UseReadVars;
        RVIDir = env.RVIDir;
        RVIFile = env.RVIFile;
        ProjectType = env.ProjectType;
        DCKDir = env.DCKDir;
        DCKTemplate = env.DCKTemplate;
        OutputFileNames = env.OutputFileNames;
        KeepJobDir = env.KeepJobDir;
        KeepJEPlusFiles = env.KeepJEPlusFiles;
        KeepEPlusFiles = env.KeepEPlusFiles;
        SelectedFiles = env.SelectedFiles;
        ForceRerun = env.ForceRerun;
        EPlusVersion = env.EPlusVersion;
        Platform = env.Platform;
    }

    public String getIDFDir() {
        return IDFDir;
    }

    public void setIDFDir(String IDFDir) {
        this.IDFDir = IDFDir;
    }

    public String getIDFTemplate() {
        return IDFTemplate;
    }

    public void setIDFTemplate(String IDFTemplate) {
        this.IDFTemplate = IDFTemplate;
    }

    public boolean isKeepEPlusFiles() {
        return KeepEPlusFiles;
    }

    public void setKeepEPlusFiles(boolean KeepEPlusFiles) {
        this.KeepEPlusFiles = KeepEPlusFiles;
    }

    public boolean isKeepJEPlusFiles() {
        return KeepJEPlusFiles;
    }

    public void setKeepJEPlusFiles(boolean KeepJEPlusFiles) {
        this.KeepJEPlusFiles = KeepJEPlusFiles;
    }

    public boolean isKeepJobDir() {
        return KeepJobDir;
    }

    public String getSelectedFiles() {
        return SelectedFiles;
    }

    public void setSelectedFiles(String SelectedFiles) {
        this.SelectedFiles = SelectedFiles;
    }

    public void setKeepJobDir(boolean KeepJobDir) {
        this.KeepJobDir = KeepJobDir;
    }

    public boolean isForceRerun() {
        return ForceRerun;
    }

    public void setForceRerun(boolean ForceRerun) {
        this.ForceRerun = ForceRerun;
    }

    public String getProjectBaseDir() {
        return ProjectBaseDir;
    }

    public void setProjectBaseDir(String ProjectBaseDir) {
        this.ProjectBaseDir = ProjectBaseDir;
    }

    public String getParentDir() {
        return ParentDir;
    }

    public void setParentDir(String ParentDir) {
        this.ParentDir = ParentDir;
    }

    public String getRVIDir() {
        return RVIDir;
    }

    public void setRVIDir(String RVIDir) {
        this.RVIDir = RVIDir;
    }

    public String getRVIFile() {
        return RVIFile;
    }

    public void setRVIFile(String RVIFile) {
        this.RVIFile = RVIFile;
    }

    public int getProjectType() {
        return ProjectType;
    }

    public void setProjectType(int ProjectType) {
        this.ProjectType = ProjectType;
    }

    public String getDCKDir() {
        return DCKDir;
    }

    public void setDCKDir(String DCKDir) {
        this.DCKDir = DCKDir;
    }

    public String getDCKTemplate() {
        return DCKTemplate;
    }

    public void setDCKTemplate(String DCKTemplate) {
        this.DCKTemplate = DCKTemplate;
    }

    public String getINSELDir() {
        return INSELDir;
    }

    public void setINSELDir(String INSELDir) {
        this.INSELDir = INSELDir;
    }

    public String getINSELTemplate() {
        return INSELTemplate;
    }

    public void setINSELTemplate(String INSELTemplate) {
        this.INSELTemplate = INSELTemplate;
    }

    public String getOutputFileNames() {
        return OutputFileNames;
    }

    public void setOutputFileNames(String OutputFileNames) {
        this.OutputFileNames = OutputFileNames;
    }

    public boolean isUseReadVars() {
        return UseReadVars;
    }

    public void setUseReadVars(boolean UseReadVars) {
        this.UseReadVars = UseReadVars;
    }

    public String getWeatherDir() {
        return WeatherDir;
    }

    public void setWeatherDir(String WeatherDir) {
        this.WeatherDir = WeatherDir;
    }

    public String getWeatherFile() {
        return WeatherFile;
    }

    public void setWeatherFile(String WeatherFile) {
        this.WeatherFile = WeatherFile;
    }

    public String getEPlusVersion() {
        return EPlusVersion;
    }

    public void setEPlusVersion(String EPlusVersion) {
        this.EPlusVersion = EPlusVersion;
    }

    /**
     * Test if the input template file is an EP-Macro input file (*.imf)
     * @return True if the file name ends with ".imf"
     */
    public boolean isIMF () {
        return (IDFTemplate.toLowerCase().endsWith(EPlusConfig.getEPlusIMFExt()));
    }
    
    public boolean isRVX () {
        return (RVIFile == null || RVIFile.toLowerCase().endsWith(EPlusConfig.getJEPlusRvxExt()));
    }
    
    @Override
    public String toString () {
        return "<" + IDFTemplate + ", " + WeatherFile + ">";
    }

}
