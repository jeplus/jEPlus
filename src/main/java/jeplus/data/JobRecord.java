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
import java.util.Date;

/**
 * Record of the execution of a job, including time stamps
 * @author yzhang
 */
public class JobRecord implements Serializable {

    static final long serialVersionUID = -9061342493303642021L;
    
    protected String Originator = null;
    protected Date OriginatorTime = null;
    protected String Coordinator = null;
    protected Date CoordinatorTime = null;
    protected String Executor = null;
    protected Date ExecutorTimeStart = null;
    protected Date ExecutorTimeEnd = null;
    protected String JobID = null;
    protected String TransactionID = null;
    protected String JobOutcome = null;

    public JobRecord (String jobid, String ori) {
        JobID = jobid;
        Originator = ori;
        OriginatorTime = new Date();
    }

    public void setCoordinatorStamp (String co) {
        Coordinator = co;
        CoordinatorTime = new Date();
    }

    public void setExecutorStartStamp (String exec) {
        Executor = exec;
        ExecutorTimeStart = new Date();
    }

    public void setExecutorEndStamp () {
        ExecutorTimeEnd = new Date();
    }

    public void setJobOutcome(String JobOutcome) {
        this.JobOutcome = JobOutcome;
    }

    public void setTransactionID(String TransactionID) {
        this.TransactionID = TransactionID;
    }

    public String getCoordinator() {
        return Coordinator;
    }

    public Date getCoordinatorTime() {
        return CoordinatorTime;
    }

    public String getExecutor() {
        return Executor;
    }

    public Date getExecutorTimeEnd() {
        return ExecutorTimeEnd;
    }

    public Date getExecutorTimeStart() {
        return ExecutorTimeStart;
    }

    public String getJobID() {
        return JobID;
    }

    public String getJobOutcome() {
        return JobOutcome;
    }

    public String getOriginator() {
        return Originator;
    }

    public Date getOriginatorTime() {
        return OriginatorTime;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    @Override
    public String toString ()  {
        StringBuilder buf = new StringBuilder ("Job record for ");
        if (this.JobID != null) buf.append(this.JobID);
        else buf.append("Unknown job");
        buf.append("\n");
        if (this.Originator!=null && this.OriginatorTime!= null)
            buf.append("Originator ").append(this.Originator).append(": ").append(this.OriginatorTime.toString()).append("\n");
        if (this.Coordinator!=null && this.CoordinatorTime!= null)
            buf.append("Coordinator ").append(this.Coordinator).append(": ").append(this.CoordinatorTime.toString()).append("\n");
        if (this.Executor!=null && this.ExecutorTimeStart!= null)
            buf.append("Executor ").append(this.Executor).append(": ").append(this.ExecutorTimeStart.toString());
        if (this.ExecutorTimeEnd != null)
            buf.append(" --> ").append(this.ExecutorTimeEnd.toString());
        buf.append("\n");
        if (this.TransactionID != null)
            buf.append("Transaction ID:").append(this.TransactionID).append("\n");
        if (this.JobOutcome != null)
            buf.append("Job outcome: ").append(this.JobOutcome).append("\n");
        buf.append("End record.\n");
        return buf.toString();
    }

    public String toTableRow() {
        StringBuilder buf = new StringBuilder ();
        buf.append(TransactionID).append(",");
        buf.append(JobID!=null ? this.JobID : "Unknown").append(",");
        buf.append(Originator).append(",");
        buf.append(OriginatorTime).append(",");
        buf.append(Coordinator).append(",");
        buf.append(CoordinatorTime).append(",");
        buf.append(Executor).append(",");
        buf.append(ExecutorTimeStart).append(",");
        buf.append(ExecutorTimeEnd).append(",");
        buf.append(JobOutcome);
        return buf.toString();
    }
}
