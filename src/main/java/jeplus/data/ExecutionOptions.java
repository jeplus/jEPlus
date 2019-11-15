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
package jeplus.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Objects;
import jeplus.EPlusBatch;

/**
 * Data object representing execution options model
 * @author yzhang
 */
@JsonPropertyOrder({ 
    "numThreads", 
    "workDir", 
    "subSet", 
    "sampleOpt", 
    "randomSeed", 
    "numberOfJobs", 
    "jobListFile", 
    "rerunAll",
    "keepJEPlusFiles", 
    "keepEPlusFiles", 
    "deleteSelectedFiles", 
    "selectedFiles"
})
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

    // PBS specific options
    /** PBS job script template file name */
    protected String PBSscriptFile = null;

    // Server specific options
    /** JobServer configuration file name */
    protected String ServerConfigFile = null;

    // Remote services specific options
    /** JESS Client configuration file name */
    protected String ClientConfigFile = null;
    
    // Last batch options
    /** job set within the project */
    protected int SubSet = ALL;
    /** Number of random jobs to run */
    protected int NumberOfJobs = 0;
    /** Use Latin Hypercube sampling or not */
    protected boolean UseLHS = false;
    /** Sampling option */
    protected EPlusBatch.SampleType SampleOpt = EPlusBatch.SampleType.SHUFFLE;
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

//        RemoteServerAddr = "set server addr...";
//        RemoteServerPort = 0;
//        PackedFile = "project/";

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

//        RemoteServerAddr = "set server addr...";
//        RemoteServerPort = 0;
//        PackedFile = "project/";

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
//        RemoteServerAddr = obj.RemoteServerAddr;
//        RemoteServerPort = obj.RemoteServerPort;
        DeleteSelectedFiles = obj.DeleteSelectedFiles;
        SelectedFiles = obj.SelectedFiles;
//        UseSQLite = obj.UseSQLite;
//        UseUserSpreadsheet = obj.UseUserSpreadsheet;
//        PackedFile = obj.PackedFile;
        SubSet = obj.SubSet;
        NumberOfJobs = obj.NumberOfJobs;
        UseLHS = obj.UseLHS;
        SampleOpt = obj.SampleOpt;
        RandomSeed = obj.RandomSeed;
        JobListFile = obj.JobListFile;
    }

    public String getWorkDir() {
        return WorkDir;
    }

    public void setWorkDir(String WorkDir) {
        this.WorkDir = WorkDir;
    }

    @JsonIgnore
    public String getPBSscriptFile() {
        return PBSscriptFile;
    }

    @JsonIgnore
    public String getServerConfigFile() {
        return ServerConfigFile;
    }

    @JsonIgnore
    public void setServerConfigFile(String ServerConfigFile) {
        this.ServerConfigFile = ServerConfigFile;
    }

    @JsonIgnore
    public void setPBSscriptFile(String PBSscriptFile) {
        this.PBSscriptFile = PBSscriptFile;
    }

    @JsonIgnore
    public int getDelay() {
        return Delay;
    }

    @JsonIgnore
    public void setDelay(int Delay) {
        this.Delay = Delay;
    }

    @JsonIgnore
    public int getExecutionType() {
        return ExecutionType;
    }

    @JsonIgnore
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

    @JsonIgnore
    public boolean isKeepJobDir() {
        return KeepJobDir;
    }

    @JsonIgnore
    public void setKeepJobDir(boolean KeepJobDir) {
        this.KeepJobDir = KeepJobDir;
    }

    public boolean isDeleteSelectedFiles() {
        return DeleteSelectedFiles;
    }

    public void setDeleteSelectedFiles(boolean KeepSelectedFiles) {
        this.DeleteSelectedFiles = KeepSelectedFiles;
    }

    @JsonIgnore
    public int getOMPThreads() {
        return OMPThreads;
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getParentDir() {
        return WorkDir;
    }

    @JsonIgnore
    public void setParentDir(String ParentDir) {
        this.WorkDir = ParentDir;
    }

    @JsonIgnore
    public int getJobSubmissionCap() {
        return JobSubmissionCap;
    }

    @JsonIgnore
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

    @JsonIgnore
    public boolean isUseLHS() {
        return UseLHS;
    }

    @JsonIgnore
    public void setUseLHS(boolean UseLHS) {
        this.UseLHS = UseLHS;
    }

    public EPlusBatch.SampleType getSampleOpt() {
        return SampleOpt;
    }

    public void setSampleOpt(EPlusBatch.SampleType SampleOpt) {
        this.SampleOpt = SampleOpt;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.ExecutionType;
        hash = 67 * hash + this.NumThreads;
        hash = 67 * hash + this.OMPThreads;
        hash = 67 * hash + this.JobSubmissionCap;
        hash = 67 * hash + (this.RerunAll ? 1 : 0);
        hash = 67 * hash + this.Delay;
        hash = 67 * hash + Objects.hashCode(this.WorkDir);
        hash = 67 * hash + (this.KeepJobDir ? 1 : 0);
        hash = 67 * hash + (this.KeepJEPlusFiles ? 1 : 0);
        hash = 67 * hash + (this.KeepEPlusFiles ? 1 : 0);
        hash = 67 * hash + (this.DeleteSelectedFiles ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(this.SelectedFiles);
        hash = 67 * hash + Objects.hashCode(this.PBSscriptFile);
        hash = 67 * hash + Objects.hashCode(this.ServerConfigFile);
        hash = 67 * hash + Objects.hashCode(this.ClientConfigFile);
        hash = 67 * hash + this.SubSet;
        hash = 67 * hash + this.NumberOfJobs;
        hash = 67 * hash + (this.UseLHS ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(this.SampleOpt);
        hash = 67 * hash + (int) (this.RandomSeed ^ (this.RandomSeed >>> 32));
        hash = 67 * hash + Objects.hashCode(this.JobListFile);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExecutionOptions other = (ExecutionOptions) obj;
        if (this.ExecutionType != other.ExecutionType) {
            return false;
        }
        if (this.NumThreads != other.NumThreads) {
            return false;
        }
        if (this.OMPThreads != other.OMPThreads) {
            return false;
        }
        if (this.JobSubmissionCap != other.JobSubmissionCap) {
            return false;
        }
        if (this.RerunAll != other.RerunAll) {
            return false;
        }
        if (this.Delay != other.Delay) {
            return false;
        }
        if (this.KeepJobDir != other.KeepJobDir) {
            return false;
        }
        if (this.KeepJEPlusFiles != other.KeepJEPlusFiles) {
            return false;
        }
        if (this.KeepEPlusFiles != other.KeepEPlusFiles) {
            return false;
        }
        if (this.DeleteSelectedFiles != other.DeleteSelectedFiles) {
            return false;
        }
        if (this.SubSet != other.SubSet) {
            return false;
        }
        if (this.NumberOfJobs != other.NumberOfJobs) {
            return false;
        }
        if (this.UseLHS != other.UseLHS) {
            return false;
        }
        if (this.RandomSeed != other.RandomSeed) {
            return false;
        }
        if (!Objects.equals(this.WorkDir, other.WorkDir)) {
            return false;
        }
        if (!Objects.equals(this.SelectedFiles, other.SelectedFiles)) {
            return false;
        }
        if (!Objects.equals(this.PBSscriptFile, other.PBSscriptFile)) {
            return false;
        }
        if (!Objects.equals(this.ServerConfigFile, other.ServerConfigFile)) {
            return false;
        }
        if (!Objects.equals(this.ClientConfigFile, other.ClientConfigFile)) {
            return false;
        }
        if (!Objects.equals(this.JobListFile, other.JobListFile)) {
            return false;
        }
        if (this.SampleOpt != other.SampleOpt) {
            return false;
        }
        return true;
    }

    
    
}
