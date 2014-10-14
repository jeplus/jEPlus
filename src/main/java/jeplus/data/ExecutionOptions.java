/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@gmail.com>                    *
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
 ***************************************************************************
 *                                                                         *
 * Change log:                                                             *
 *                                                                         *
 *  - Created  2010-11-01                                                  *
 *                                                                         *
 ***************************************************************************/
package jeplus.data;

import java.io.Serializable;

/**
 *
 * @author yzhang
 */
public class ExecutionOptions implements Serializable {
    
    private static final long serialVersionUID = -1400077773086505500L;

    public static final int INTERNAL_CONTROLLER = 0;
    public static final int LINUX_PBS_SCRIPTS = 1;
    public static final int LOCAL_PBS_CONTROLLER = 2;
    public static final int VENUS_C_CONTROLLER = 3;
    public static final int NETWORK_JOB_SERVER = 4;
    public static final int JOB_SERVER_FULL = 5;
    public static final int REMOTE_SERVER = 6;
    
    public static final int NONE = -99; // an illegal option
    public static final int ALL = -1;
    public static final int FILE = 0;
    public static final int CHAINS = 1;
    public static final int RANDOM = 2; // or above

    /** Execution type offers a choice among internal controller and external controller (use scripts) */
    protected int ExecutionType = -1; // deliberate illegal option
    /** Number of threads to be used */
    protected int NumThreads = 0;
    /** Number of E+ v7.1 OMP threads */
    protected int OMPThreads = 1;
    /** Maximum number of jobs can be submitted to an external job queue */
    protected int JobSubmissionCap = 0;
    /** Option to force rerun all simulations even if previous results are present */
    protected boolean RerunAll = false;
    /** Delay between the launch of each thread */
    protected int Delay = 0;
    /** The local parent directory to the jobs */
    protected String WorkDir = null;
    /** Whether or not to keep job directory */
    protected boolean KeepJobDir = false;
    /** Whether or not to keep jEPlus intermediate files */
    protected boolean KeepJEPlusFiles = false;
    /** Whether or not to keep EnergyPlus output files */
    protected boolean KeepEPlusFiles = false;
    /** Whether or not to keep EnergyPlus output files */
    protected boolean DeleteSelectedFiles = false;
    /** Selected file name patterns to keep */
    protected String SelectedFiles = "*.dxf *.htm *.mtd *.mdd *.rdd *.shd *.out *.audit *.eio *.idd *.bnd *.ini";
    /** Whether or not to try collect results from E+ SQLite output file */
    protected boolean UseSQLite = true;
    /** Whether or not to try look up further information from a user-specified spreadsheet */
    protected boolean UseUserSpreadsheet = true;

    // PBS specific options
    /** PBS job script template file name */
    protected String PBSscriptFile = null;

    // Server specific options
    /** JobServer configuration file name */
    protected String ServerConfigFile = null;

    // Remote services specific options
    /** Remote server address */
    protected String RemoteServerAddr = null;
    /** Remote server port number */
    protected int RemoteServerPort = -1;
    /** Packed file name */
    protected String PackedFile = null;

    // Last batch options
    /** job set within the project */
    protected int SubSet = NONE;
    /** Number of random jobs to run */
    protected int NumberOfJobs = 0;
    /** Use Latin Hypercube sampling or not */
    protected boolean UseLHS = false;
    /** Random seed */
    protected long RandomSeed = 0;
    /** job list file */
    protected String JobListFile = null;
    
    /** Default constructor */
    public ExecutionOptions () {
        ExecutionType = INTERNAL_CONTROLLER;
        NumThreads = Runtime.getRuntime().availableProcessors();
        JobSubmissionCap = 100;
        RerunAll = true;
        Delay = 100;
        WorkDir = "output/";
        KeepJobDir = true;
        KeepJEPlusFiles = true;
        KeepEPlusFiles = true;

        PBSscriptFile = "select script file...";

        ServerConfigFile = "server.cfg";

        RemoteServerAddr = "set server addr...";
        RemoteServerPort = 0;
        PackedFile = "project/";

        SubSet = ALL;
        NumberOfJobs = 1;
        UseLHS = false;
        RandomSeed = 12345;
        JobListFile = "select job list file ...";
    };

    /** Construct with specified type */
    public ExecutionOptions (int type) {
        ExecutionType = type;
        NumThreads = Runtime.getRuntime().availableProcessors();
        JobSubmissionCap = 100;
        RerunAll = true;
        Delay = 2000;
        WorkDir = "output/";
        KeepJobDir = true;
        KeepJEPlusFiles = true;
        KeepEPlusFiles = true;

        PBSscriptFile = "select script file...";

        ServerConfigFile = "server.cfg";

        RemoteServerAddr = "set server addr...";
        RemoteServerPort = 0;
        PackedFile = "project/";

        SubSet = ALL;
        NumberOfJobs = 1;
        UseLHS = false;
        RandomSeed = 12345;
        JobListFile = "select job list file ...";
    }

    /** Clone constructor */
    public ExecutionOptions (ExecutionOptions obj) {
        ExecutionType = obj.ExecutionType;
        NumThreads = obj.NumThreads;
        OMPThreads = obj.OMPThreads;
        JobSubmissionCap = obj.JobSubmissionCap;
        RerunAll = obj.RerunAll;
        Delay = obj.Delay;
        WorkDir = obj.WorkDir;
        KeepJobDir = obj.KeepJobDir;
        KeepJEPlusFiles = obj.KeepJEPlusFiles;
        KeepEPlusFiles = obj.KeepEPlusFiles;
        PBSscriptFile = obj.PBSscriptFile;
        ServerConfigFile = obj.ServerConfigFile;
        RemoteServerAddr = obj.RemoteServerAddr;
        RemoteServerPort = obj.RemoteServerPort;
        DeleteSelectedFiles = obj.DeleteSelectedFiles;
        SelectedFiles = obj.SelectedFiles;
        UseSQLite = obj.UseSQLite;
        UseUserSpreadsheet = obj.UseUserSpreadsheet;
        PackedFile = obj.PackedFile;
        SubSet = obj.SubSet;
        NumberOfJobs = obj.NumberOfJobs;
        UseLHS = obj.UseLHS;
        RandomSeed = obj.RandomSeed;
        JobListFile = obj.JobListFile;
    }

    public String getWorkDir() {
        return WorkDir;
    }

    public void setWorkDir(String WorkDir) {
        this.WorkDir = WorkDir;
    }

    public String getPBSscriptFile() {
        return PBSscriptFile;
    }

    public String getRemoteServerAddr() {
        return RemoteServerAddr;
    }

    public void setRemoteServerAddr(String RemoteServerAddr) {
        this.RemoteServerAddr = RemoteServerAddr;
    }

    public int getRemoteServerPort() {
        return RemoteServerPort;
    }

    public void setRemoteServerPort(int RemoteServerPort) {
        this.RemoteServerPort = RemoteServerPort;
    }

    public String getServerConfigFile() {
        return ServerConfigFile;
    }

    public void setServerConfigFile(String ServerConfigFile) {
        this.ServerConfigFile = ServerConfigFile;
    }

    public void setPBSscriptFile(String PBSscriptFile) {
        this.PBSscriptFile = PBSscriptFile;
    }

    public int getDelay() {
        return Delay;
    }

    public void setDelay(int Delay) {
        this.Delay = Delay;
    }

    public int getExecutionType() {
        return ExecutionType;
    }

    public void setExecutionType(int ExecutionType) {
        this.ExecutionType = ExecutionType;
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

    public void setKeepJobDir(boolean KeepJobDir) {
        this.KeepJobDir = KeepJobDir;
    }

    public boolean isDeleteSelectedFiles() {
        return DeleteSelectedFiles;
    }

    public void setDeleteSelectedFiles(boolean KeepSelectedFiles) {
        this.DeleteSelectedFiles = KeepSelectedFiles;
    }

    public int getOMPThreads() {
        return OMPThreads;
    }

    public void setOMPThreads(int OMPThreads) {
        this.OMPThreads = OMPThreads;
    }

    public String getSelectedFiles() {
        return SelectedFiles;
    }

    public void setSelectedFiles(String SelectedFiles) {
        this.SelectedFiles = SelectedFiles;
    }

    public int getNumThreads() {
        return NumThreads;
    }

    public void setNumThreads(int NumThreads) {
        this.NumThreads = NumThreads;
    }

    public String getParentDir() {
        return WorkDir;
    }

    public void setParentDir(String ParentDir) {
        this.WorkDir = ParentDir;
    }

    public String getPackedFile() {
        return PackedFile;
    }

    public void setPackedFile(String PackedFile) {
        this.PackedFile = PackedFile;
    }

    public int getJobSubmissionCap() {
        return JobSubmissionCap;
    }

    public void setJobSubmissionCap(int JobSubmissionCap) {
        this.JobSubmissionCap = JobSubmissionCap;
    }

    public boolean isRerunAll() {
        return RerunAll;
    }

    public void setRerunAll(boolean RerunAll) {
        this.RerunAll = RerunAll;
    }

    public String getJobListFile() {
        return JobListFile;
    }

    public void setJobListFile(String JobListFile) {
        this.JobListFile = JobListFile;
    }

    public long getRandomSeed() {
        return RandomSeed;
    }

    public void setRandomSeed(long RandomSeed) {
        this.RandomSeed = RandomSeed;
    }

    public int getSubSet() {
        return SubSet;
    }

    public void setSubSet(int SubSet) {
        this.SubSet = SubSet;
    }

    public int getNumberOfJobs() {
        return NumberOfJobs;
    }

    public void setNumberOfJobs(int NumberOfJobs) {
        this.NumberOfJobs = NumberOfJobs;
    }

    public boolean isUseLHS() {
        return UseLHS;
    }

    public void setUseLHS(boolean UseLHS) {
        this.UseLHS = UseLHS;
    }

    public boolean isUseSQLite() {
        return UseSQLite;
    }

    public void setUseSQLite(boolean UseSQLite) {
        this.UseSQLite = UseSQLite;
    }

    public boolean isUseUserSpreadsheet() {
        return UseUserSpreadsheet;
    }

    public void setUseUserSpreadsheet(boolean UseUserSpreadsheet) {
        this.UseUserSpreadsheet = UseUserSpreadsheet;
    }

}
