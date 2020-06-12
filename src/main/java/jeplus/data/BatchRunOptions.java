/*
 * Copyright (C) 2020 yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jeplus.data;

import java.io.Serializable;

/**
 *
 * @author yi
 */
public class BatchRunOptions implements Serializable {
    boolean WriteJobList = true;
    String JobListFile = "joblist_out.csv";
    boolean PrepareJobs = true;
    boolean RunSimulations = true;
    boolean CollectResults = true;
    
    public BatchRunOptions () {}

    public BatchRunOptions (BatchRunOptions other) {
        WriteJobList = other.WriteJobList;
        JobListFile = other.JobListFile;
        PrepareJobs = other.PrepareJobs;
        RunSimulations = other.RunSimulations;
        CollectResults = other.CollectResults;
    }

    public boolean isWriteJobList() {
        return WriteJobList;
    }

    public void setWriteJobList(boolean WriteJobList) {
        this.WriteJobList = WriteJobList;
    }

    public String getJobListFile() {
        return JobListFile;
    }

    public void setJobListFile(String JobListFile) {
        this.JobListFile = JobListFile;
    }

    public boolean isPrepareJobs() {
        return PrepareJobs;
    }

    public void setPrepareJobs(boolean PrepareJobs) {
        this.PrepareJobs = PrepareJobs;
    }

    public boolean isRunSimulations() {
        return RunSimulations;
    }

    public void setRunSimulations(boolean RunSimulations) {
        this.RunSimulations = RunSimulations;
    }

    public boolean isCollectResults() {
        return CollectResults;
    }

    public void setCollectResults(boolean CollectResults) {
        this.CollectResults = CollectResults;
    }
    
    
}
