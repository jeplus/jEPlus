/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jeplus.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import jeplus.EPlusConfig;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class RVX implements Serializable {
    
    

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(RVX.class);
    
    // Serialization version code; to maintain backwards compatibility, do not change.
    static final long serialVersionUID = 5600302679570045420L;
    
//	"rvis" : [ 
//		{ 
//			"fileName" : "5ZoneCostEst2.rvi",
//                      "frequency" : "Annual",
//			"tableName" : "SimResults2",
//                      "usedInCalc" : true
//		}
//	],
    public static class RVIitem implements Serializable {
        private String FileName = "my.rvi";
        private String Frequency = "Annual";
        private String TableName = "SimResults";
        private boolean UsedInCalc = true;

        public String getFileName() { return FileName; }
        public void setFileName(String FileName) { this.FileName = FileName; }
        public String getFrequency() { return Frequency; }
        public void setFrequency(String Frequency) { this.Frequency = Frequency; }
        public String getTableName() { return TableName; }
        public void setTableName(String TableName) { this.TableName = TableName; }
        public boolean isUsedInCalc() { return UsedInCalc; }
        public void setUsedInCalc(boolean UsedInCalc) { this.UsedInCalc = UsedInCalc; }
    }
    private RVIitem [] RVIs = null;
    public RVIitem[] getRVIs() { return RVIs; }
    public void setRVIs(RVIitem[] RVIs) { this.RVIs = RVIs; }

    
//	"sqls" : [
//		{ 
//			"tableName" : "ChillerCap",
//			"columnHeaders" : "Chiller Nominal Capacity [W]",
//			"sqlcommand" : "select Value from ComponentSizes WHERE (CompType='Chiller:Electric' AND CompName='CHILLER PLANT CHILLER' AND Description='Nominal Capacity')"
//                      "useInCalc" : true
//		},
//		{ 
//			"tableName" : "ConsCost",
//			"columnHeaders" : "Construction Cost [$/m2]",
//			"sqlcommand" : "select Value from TabularDataWithStrings WHERE (ReportName='Construction Cost Estimate Summary' AND ReportForString='Entire Facility' AND TableName='Construction Cost Estimate Summary' AND RowName='Cost Per Conditioned Building Area (~~$~~/m2)' AND ColumnName='Current Bldg. Model' AND Units='' AND RowId=10)"
//                      "usedInCalc" : true
//		}
//	],
    public static class SQLitem implements Serializable {
        private String TableName = "SqlTable.csv";  // E.g.
        private String ColumnHeaders = "";          // E.g. "Temperature [K], Heating [kWh]"
        private String SQLcommand = "";             // E.g.
        private boolean UsedInCalc = true;

        public String getTableName() { return TableName; }
        public void setTableName(String TableName) { this.TableName = TableName; }
        public String getColumnHeaders() { return ColumnHeaders; }
        public void setColumnHeaders(String ColumnHeaders) { this.ColumnHeaders = ColumnHeaders; }
        public String getSQLcommand() { return SQLcommand; }
        public void setSQLcommand(String SQLcommand) { this.SQLcommand = SQLcommand; }
        public boolean isUsedInCalc() { return UsedInCalc; }
        public void setUsedInCalc(boolean UsedInCalc) { this.UsedInCalc = UsedInCalc; }
    }
    private SQLitem [] SQLs = null;
    public SQLitem[] getSQLs() { return SQLs; }
    public void setSQLs(SQLitem[] SQLs) { this.SQLs = SQLs; }

    
//	"scripts" : [
//		{
//			"fileName" : "readRunTimes_jy.py",
//			"pythonVersion" : "jython",
//			"onEachJob" : false,
//			"arguments" : "",
//			"tableName" : "CpuTime"
//		}
//	],
    public static class PYTHONitem implements Serializable {
        private String FileName = "readRunTimes_jy.py";
        private String PythonVersion = "jython";
        private boolean OnEachJob = false;
        private String Arguments = "";
        private String TableName = "CpuTime";

        public String getFileName() { return FileName; }
        public void setFileName(String FileName) { this.FileName = FileName; }
        public boolean isOnEachJob() { return OnEachJob; }
        public void setOnEachJob(boolean OnEachJob) { this.OnEachJob = OnEachJob; }
        public String getPythonVersion() { return PythonVersion; }
        public void setPythonVersion(String PythonVersion) { this.PythonVersion = PythonVersion; }
        public String getArguments() { return Arguments; }
        public void setArguments(String Arguments) { this.Arguments = Arguments; }
        public String getTableName() { return TableName; }
        public void setTableName(String TableName) { this.TableName = TableName; }
    }
    private PYTHONitem [] Scripts = null;
    public PYTHONitem[] getScripts() { return Scripts; }
    public void setScripts(PYTHONitem[] Scripts) { this.Scripts = Scripts; }

//	"userSupplied" : [
//		{
//			"fileName" : "ExternResultTable.csv",
//			"headerRow" : 0,
//			"jobIdColumn" : 1,
//			"dataColumns" : "3 4",
//			"tableName" : "UserResults"
//		}
//	],
    public static class UserSuppliedItem implements Serializable {
        private String FileName = "ExternResultTable.csv";
        private int HeaderRow = 0;
        private int JobIdColumn = 1;
        private String DataColumns = "3";
        private String TableName = "UserResults";

        public String getFileName() { return FileName; }
        public void setFileName(String FileName) { this.FileName = FileName; }
        public int getHeaderRow() { return HeaderRow; }
        public void setHeaderRow(int HeaderRow) { this.HeaderRow = HeaderRow; }
        public int getJobIdColumn() { return JobIdColumn; }
        public void setJobIdColumn(int JobIdColumn) { this.JobIdColumn = JobIdColumn; }
        public String getDataColumns() { return DataColumns; }
        public void setDataColumns(String DataColumns) { this.DataColumns = DataColumns; }
        public String getTableName() { return TableName; }
        public void setTableName(String TableName) { this.TableName = TableName; }
    }
    private UserSuppliedItem [] UserSuppliedResults = null;
    public UserSuppliedItem[] getUserSuppliedResults() { return UserSuppliedResults; }
    public void setUserSuppliedResults(UserSuppliedItem[] usersupplied) { this.UserSuppliedResults = usersupplied; }

    
    
//	"userVars" : [
//		{
//			"identifier" : "v2",
//			"formula" : "c2",
//			"caption" : "Variable 2 []",
//			"report" : false
//		}
//	],
    public static class UserVar implements Serializable {
        private String Identifier = "v1";
        private String Formula = "0";
        private String Caption = "Variable 1 []";
        private boolean Report = false;

        public String getIdentifier() { return Identifier; }
        public void setIdentifier(String Identifier) { this.Identifier = Identifier; }
        public String getFormula() { return Formula; }
        public void setFormula(String Formula) { this.Formula = Formula; }
        public String getCaption() { return Caption; }
        public void setCaption(String Caption) { this.Caption = Caption; }
        public boolean isReport() { return Report; }
        public void setReport(boolean Report) { this.Report = Report; }
        @Override
        public String toString () {
            StringBuilder buf = new StringBuilder (Caption);
            buf.append(": ").append(Identifier).append(" = ").append(Formula);
            return buf.toString();
        }
    }
    private UserVar [] UserVars = null;
    public UserVar[] getUserVars() { return UserVars; }
    public void setUserVars(UserVar[] UserVars) { this.UserVars = UserVars; }
    public ArrayList<UserVar> getReportedUserVars () {
        ArrayList<UserVar> list = new ArrayList<> ();
        if (UserVars != null) {
            for (UserVar var : UserVars) {
                if (var.isReport()) list.add(var);
            }
        }
        return list;
    }

	
//	"constraints" : [
//		{
//			"identifier" : "s1",
//			"formula" : "v1/1000",
//			"caption" : "Chiller Capacity [kW]",
//			"scaling" : true,
//			"lb" : 0,
//			"ub" : 200,
//			"min" : 0,
//			"max" : 300,
//			"weight" : 1.0,
//                      "enabled" : true
//		}
//	],
    public static class Constraint implements Serializable {
        private String Identifier = "s1";
        private String Formula = "c1";
        private String Caption = "Constraint 1 []";
        private boolean Scaling = false;
        private double LB = 0;
        private double UB = 1;
        private double Min = 0;
        private double Max = 1;
        private double Weight = 1;
        private boolean Enabled = true;

        public String getIdentifier() { return Identifier; }
        public void setIdentifier(String Identifier) { this.Identifier = Identifier; }
        public String getFormula() { return Formula; }
        public void setFormula(String Formula) { this.Formula = Formula; }
        public String getCaption() { return Caption; }
        public void setCaption(String Caption) { this.Caption = Caption; }
        public boolean isScaling() { return Scaling; }
        public void setScaling(boolean Scaling) { this.Scaling = Scaling; }
        public double getLB() { return LB; }
        public void setLB(double LB) { this.LB = LB; }
        public double getUB() { return UB; }
        public void setUB(double UB) { this.UB = UB; }
        public double getMin() { return Min; }
        public void setMin(double Min) { this.Min = Min; }
        public double getMax() {  return Max; }
        public void setMax(double Max) { this.Max = Max; }
        public double getWeight() { return Weight; }
        public void setWeight(double Weight) { this.Weight = Weight; }
        public boolean isEnabled() { return Enabled; }
        public void setEnabled(boolean Enabled) { this.Enabled = Enabled; }
        
        /**
         * Normalize and scale (weigh) the objective value. User is responsible for ensuring the correct values of mMax and mMin
         * @param initval
         * @return 
         */
        public double scale(double initval) {
            double val = 0;
            if (initval <= Min || initval >= Max) {
                val = 1.;
            } else if (initval >= LB && initval <= UB) {
                val = 0.;
            } else if (initval > Min && initval < LB) {
                val = (initval - Min) / (LB - Min);
            } else if (initval > UB && initval < Max) {
                val = (initval - UB) / (Max - UB);
            }
            return Weight * val;
        }
        
        @Override
        public String toString () {
            StringBuilder buf = new StringBuilder (Caption);
            buf.append(": ").append(Identifier).append(" = ").append(Formula);
            if (Scaling) {
                buf.append("; feasible range [").append(LB).append(", ").append(UB).append("], normalized between [");
                buf.append(Min).append(", ").append(Max).append("] ");
            }
            return buf.toString();
        }
    }
    private Constraint [] Constraints = null;
    public Constraint[] getConstraints() { return Constraints; }
    public void setConstraints(Constraint[] Constraints) { this.Constraints = Constraints; }
    public ArrayList<Constraint> getEnabledConstraints () {
        ArrayList<Constraint> list = new ArrayList<> ();
        if (Constraints != null) {
            for (Constraint cons : Constraints) {
                if (cons.isEnabled()) list.add(cons);
            }
        }
        return list;
    }

	
//	"objectives" : [
//		{
//			"identifier" : "t2",
//			"formula" : "v2",
//			"caption" : "Construction Cost [$/m2]",
//			"scaling" : false,
//			"min" : 0,
//			"max" : 1000,
//			"weight" : 1.0,
//                      "enabled" : true
//		}
//	]
    public static class Objective implements Serializable {
        private String Identifier = "t1";
        private String Formula = "c1";
        private String Caption = "Objective 1 []";
        private boolean Scaling = false;
        private double Min = 0;
        private double Max = 1;
        private double Weight = 1;
        private boolean Enabled = true;

        public String getIdentifier() { return Identifier; }
        public void setIdentifier(String Identifier) { this.Identifier = Identifier; }
        public String getFormula() { return Formula; }
        public void setFormula(String Formula) { this.Formula = Formula; }
        public String getCaption() { return Caption; }
        public void setCaption(String Caption) { this.Caption = Caption; }
        public boolean isScaling() { return Scaling; }
        public void setScaling(boolean Scaling) { this.Scaling = Scaling; }
        public double getMin() { return Min; }
        public void setMin(double Min) { this.Min = Min; }
        public double getMax() { return Max; }
        public void setMax(double Max) { this.Max = Max; }
        public double getWeight() { return Weight; }
        public void setWeight(double Weight) { this.Weight = Weight; }
        public boolean isEnabled() { return Enabled; }
        public void setEnabled(boolean Enabled) { this.Enabled = Enabled; }
        /**
         * Normalize and scale (weigh) the objective value. User is responsible for ensuring the correct values of mMax and mMin
         * @param initval
         * @return 
         */
        public double scale(double initval) {
            double val = 0;
            if (Max > Min + 0.000001) {
                if (initval > Max) {
                    val = 1.0;
                } else if (initval > Min) {
                    val = (initval - Min) / (Max - Min);
                }
            }
            return Weight * val;
        }
        @Override
        public String toString () {
            StringBuilder buf = new StringBuilder (Caption);
            buf.append(": ").append(Identifier).append(" = ").append(Formula);
            if (Scaling) {
                buf.append("; normalized between [").append(Min).append(", ").append(Max).append("] ");
            }
            return buf.toString();
        }
    }
    private Objective [] Objectives = null;
    public Objective[] getObjectives() { return Objectives; }
    public void setObjectives(Objective[] Objectives) { this.Objectives = Objectives; }
    public ArrayList<Objective> getEnabledObjectives () {
        ArrayList<Objective> list = new ArrayList<> ();
        if (Objectives != null) {
            for (Objective obj : Objectives) {
                if (obj.isEnabled()) list.add(obj);
            }
        }
        return list;
    }

    
//	"NOTE" : "Some notes about this RVX"
    private String Notes = "";
    public String getNotes() { return Notes; }
    public void setNotes(String Notes) { this.Notes = Notes; }
    
    /**
     * Read RVX from a json file or a traditional RVI file with extensions
     * @param rvxfile
     * @return RVX object
     * @throws IOException 
     */
    public static RVX getRVX (String rvxfile) throws IOException {
        if (rvxfile.toLowerCase().endsWith(EPlusConfig.getJEPlusRvxExt())) {    //RVX
            ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
            return mapper.readValue(new File (rvxfile), RVX.class);
        }else {
            RVX rvx = new RVX ();
            // Convert RVI into RVX object. User spreadsheet function is no longer supported
            ArrayList<String[]> sqlite = new ArrayList<> ();
            ArrayList<String[]> objectives = new ArrayList<> ();
            try (BufferedReader fr = new BufferedReader (new FileReader (rvxfile))) {
                ArrayList<String[]> sections = sqlite;
                
                String line = fr.readLine();
                boolean extra_on = false;
                // Locating the Objectives section
                while (line != null) {
                    if (extra_on) {
                        if (line.toLowerCase().startsWith("!-end ")) {
                            extra_on = false;
                        }else {
                            line = line.substring(0, line.contains("!") ? line.indexOf("!") : line.length());
                            if (line.trim().length() > 0) {
                                String [] section = line.split("\\s*;\\s*");
                                sections.add(section);
                            }
                        }
                    }else {
                        if (line.trim().toLowerCase().startsWith("!-objectives")) {
                            sections = objectives;
                            extra_on = true;
                        }else if (line.trim().toLowerCase().startsWith("!-sqlite")) {
                            sections = sqlite;
                            extra_on = true;
                        }
                    }
                    line = fr.readLine();
                }
            }catch (Exception ex) {
                logger.error("Error reading extended rvi file for objective definitions: ", ex);
            }
            // RVI section points to this RVI file
            RVX.RVIitem [] rvis = new RVX.RVIitem [] {new RVX.RVIitem()};
            rvis[0].setFileName(rvxfile);
            rvis[0].setTableName("SimResults");
            rvx.setRVIs(rvis);
            // SQLite section
            if (sqlite.size() > 0) {
                RVX.SQLitem [] sqls = new RVX.SQLitem [sqlite.size()];
                for (int i=0; i<sqlite.size(); i++) {
                    String [] row = sqlite.get(i);
                    sqls[i] = new RVX.SQLitem();
                    if (row.length >= 3) {
                        sqls[i].setTableName(row[0]);
                        sqls[i].setColumnHeaders(row[1]);
                        sqls[i].setSQLcommand(row[2]);
                    }
                }
                rvx.setSQLs(sqls);
            }
            // Objective section
            if (objectives.size() > 0) {
                RVX.Objective [] objs = new RVX.Objective [objectives.size()];
                for (int i=0; i<objectives.size(); i++) {
                    String [] row = objectives.get(i);
                    objs[i] = new RVX.Objective();
                    if (row.length >= 3) {
                        objs[i].setIdentifier("t" + i);
                        objs[i].setCaption(row[0] + " [" + row[1] + "]");
                        objs[i].setFormula(row[2]);
                        objs[i].setScaling(false);
                    }
                }
                rvx.setObjectives(objs);
            }
            // Constraint section is empty
            rvx.setConstraints(new RVX.Constraint [0]);
            // Retrun rvx
            return rvx;
        }
    }
    
    
    /**
     * Tester
     * @param args 
     * @throws java.io.IOException 
     */
    public static void main (String [] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        RVX rvx = mapper.readValue(new File ("my.rvx"), RVX.class);
        mapper.writeValue(new File("user-modified.json"), rvx);
        System.exit(0);
    }
}
