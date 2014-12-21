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
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus.postproc;

import java.util.ArrayList;
import java.util.HashMap;
import jeplus.EPlusBatch;
import jeplus.data.RVX;
import org.slf4j.LoggerFactory;

/**
 * The base class for result collectors. Although not abstract, it is recommended to create a sub-class for each collector type.
 * @author zyyz
 */
public class ResultCollector {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(ResultCollector.class);
    
    /** Result collector description */
    String Description = "";
    
    ArrayList<String> ResultFiles = new ArrayList<> ();
    
    /** Report header list */
    protected ArrayList<String> ReportHeader = null;
    /** Table for report data */
    protected ArrayList <ArrayList <String>> ReportTable = null;
    /** Header map - column header is mapped to column number */
    protected HashMap <String, Integer> ResultHeader = null;
    /** Table for result data */
    protected ArrayList <ArrayList <String>> ResultTable = null;
    
    /** Process data on-the-fly instead of storing it memory */
    protected boolean OnTheFly = false;
    /** Report reader instance */
    protected IFReportReader RepReader;
    /** Result reader instance */
    protected IFResultReader ResReader;
    /** Result writer instance */
    protected IFResultWriter ResWriter;
    /** Report writer instance */
    protected IFReportWriter RepWriter;
    /** Index writer instance */
    protected IFIndexWriter IdxWriter;
    /** Post processor instance */
    protected IFPostProcessor PostProc;
    
    /**
     * Create collector with assigned job owner
     * @param desc Description of this collector
     */
    public ResultCollector (String desc) {
        Description = desc;
    }

    // ================== Getters and Setters ===================
    
    public ArrayList<String> getResultFiles() {
        return ResultFiles;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public boolean isOnTheFly() {
        return OnTheFly;
    }

    public void setOnTheFly(boolean OnTheFly) {
        this.OnTheFly = OnTheFly;
    }

    public ArrayList<String> getReportHeader() {
        return ReportHeader;
    }

    public void setReportHeader(ArrayList<String> ReportHeader) {
        this.ReportHeader = ReportHeader;
    }

    public ArrayList<ArrayList<String>> getReportTable() {
        return ReportTable;
    }

    public void setReportTable(ArrayList<ArrayList<String>> ReportTable) {
        this.ReportTable = ReportTable;
    }

    public HashMap<String, Integer> getResultHeader() {
        return ResultHeader;
    }

    public void setResultHeader(HashMap<String, Integer> ResultHeader) {
        this.ResultHeader = ResultHeader;
    }

    public ArrayList<ArrayList<String>> getResultTable() {
        return ResultTable;
    }

    public void setResultTable(ArrayList<ArrayList<String>> ResultTable) {
        this.ResultTable = ResultTable;
    }

    public IFReportReader getRepReader() {
        return RepReader;
    }

    public void setRepReader(IFReportReader RepReader) {
        this.RepReader = RepReader;
    }

    public IFResultReader getResReader() {
        return ResReader;
    }

    public void setResReader(IFResultReader ResReader) {
        this.ResReader = ResReader;
    }

    public IFResultWriter getResWriter() {
        return ResWriter;
    }

    public void setResWriter(IFResultWriter ResWriter) {
        this.ResWriter = ResWriter;
    }

    public IFReportWriter getRepWriter() {
        return RepWriter;
    }

    public void setRepWriter(IFReportWriter RepWriter) {
        this.RepWriter = RepWriter;
    }

    public IFIndexWriter getIdxWriter() {
        return IdxWriter;
    }

    public void setIdxWriter(IFIndexWriter IdxWriter) {
        this.IdxWriter = IdxWriter;
    }

    public IFPostProcessor getPostProc() {
        return PostProc;
    }

    public void setPostProc(IFPostProcessor PostProc) {
        this.PostProc = PostProc;
    }
    
    // ================== End Getters and Setters ===================

    public int collectReports (EPlusBatch JobOwner, boolean onthefly) {
        if (onthefly) {
            // Method not implemented
            throw new UnsupportedOperationException("Not supported yet.");
        }else {
            if (RepReader != null && RepWriter != null) {
                ReportHeader = new ArrayList<>();
                ReportTable = new ArrayList<>();
                RepReader.readReport(JobOwner, JobOwner.getResolvedEnv().getParentDir(), ReportHeader, ReportTable);
                RepWriter.writeReport(JobOwner, ReportHeader, ReportTable);
                return ReportTable.size();
            }
        }
        return -1;
    }
    
    /**
     * Get a list of expected result table file names from the given rvx configuration. 
     * This function should be overridden by child classes.
     * @param rvx
     * @return 
     */
    public ArrayList<String> getExpectedResultFiles (RVX rvx) {
        return new ArrayList<String>();
    }
    
    public ArrayList<String[]>  listResultFilesFromRVI (String rvifile) {
        ResultFiles.clear();
        if (ResWriter != null) {
            ResultFiles.add(ResWriter.getResultFileName());
        }
        return null;
    }
    
    public int collectResutls (EPlusBatch JobOwner, boolean onthefly) {
        if (onthefly) {
            // Method not implemented
            throw new UnsupportedOperationException("Not supported yet.");
        }else {
            if (ResReader != null && ResWriter != null) {
                ResultHeader = new HashMap<>();
                ResultTable = new ArrayList<>();
                ResReader.readResult(JobOwner, JobOwner.getResolvedEnv().getParentDir(), ResultHeader, ResultTable);
                if (PostProc != null) {PostProc.postProcess(ResultHeader, ResultTable);}
                ResultFiles.clear();
                ResWriter.writeResult(JobOwner, ResultHeader, ResultTable);
                ResultFiles.add(ResWriter.getResultFileName());
                return ResultTable.size();
            }
        }
        return -1;
    }
    
    public boolean collectIndexes (EPlusBatch JobOwner) {
        return IdxWriter == null ? false : IdxWriter.writeIndex(JobOwner);
    }
}
