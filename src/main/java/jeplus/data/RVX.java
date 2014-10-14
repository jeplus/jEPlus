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
    
//	[ 
//		"RVI" : { 
//			"filename" : "my.rvi",
//			"tablename" : "rvitable1.csv"
//		}
//	],
    public static class RVIitem implements Serializable {
        private String FileName = "my.rvi";
        private String TableName = "SimResults.csv";

        public String getFileName() { return FileName; }
        public void setFileName(String FileName) { this.FileName = FileName; }
        public String getTableName() { return TableName; }
        public void setTableName(String TableName) { this.TableName = TableName; }
    }
    private RVIitem [] RVIs = null;
    public RVIitem[] getRVIs() { return RVIs; }
    public void setRVIs(RVIitem[] RVIs) { this.RVIs = RVIs; }

    
//	[
//		"SQL" : { 
//			"tablename" : "sqltable1.csv"
//			"columnheaders" : "column1 [a], column2 [b]",
//			"command" : "SELECT * FROM abc"
//		}
//	],
    public static class SQLitem implements Serializable {
        private String TableName = "SqlTable.csv";  // E.g.
        private String ColumnHeaders = "";          // E.g. "Temperature [K], Heating [kWh]"
        private String SQLcommand = "";             // E.g.

        public String getTableName() { return TableName; }
        public void setTableName(String TableName) { this.TableName = TableName; }
        public String getColumnHeaders() { return ColumnHeaders; }
        public void setColumnHeaders(String ColumnHeaders) { this.ColumnHeaders = ColumnHeaders; }
        public String getSQLcommand() { return SQLcommand; }
        public void setSQLcommand(String SQLcommand) { this.SQLcommand = SQLcommand; }
    }
    private SQLitem [] SQLs = null;
    public SQLitem[] getSQLs() { return SQLs; }
    public void setSQLs(SQLitem[] SQLs) { this.SQLs = SQLs; }

    
//	[
//		"PYTHON" : {
//			"filename" : "myscript.py",
//			"pythonversion" : "python3",
//                      "onEachJob" : true,
//			"arguments" : "arg1 arg2 arg3", 
//			"tablename" : "pytable1.csv"
//		}
//	],
    public static class PYTHONitem implements Serializable {
        private String FileName = "postproc.py";
        private String PythonVersion = "2";
        private boolean OnEachJob = true;
        private String Arguments = "";
        private String TableName = "PyTable.csv";

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

    
//	"UserVar" : {
//		"identifier" : "v1",
//		"formula" : "c1 * 12"
//		"caption" : "Variable 1 []",
//              "report" : false
//	},
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

	
//	"CONSTRAINTs" : {
//		"identifier" : "s1",
//		"formula" : "v1 + 100",
//		"caption" : "Constraint 1 []",
//		"scaling" : false,
//		"lb" : "100.0",
//		"ub" : "200.0",
//		"min" : "0.0",
//		"max" : "1000.0",
//		"weight" : "1.0"
//	},
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
                buf.append("; feasible range [").append(LB).append(", ").append("UB").append("], normalized between [");
                buf.append(Min).append(", ").append(Max).append("] ");
            }
            return buf.toString();
        }
    }
    private Constraint [] Constraints = null;
    public Constraint[] getConstraints() { return Constraints; }
    public void setConstraints(Constraint[] Constraints) { this.Constraints = Constraints; }

	
//	"OBJECTIVEs" : {
//		"identifier" : "t1",
//		"formula" : "v1 + 100",
//		"caption" : "Objecive 1 []",
//		"scaling" : false,
//		"min" : "0",
//		"max" : "1000",
//		"weight" : "1.0"
//	}
    public static class Objective implements Serializable {
        private String Identifier = "t1";
        private String Formula = "c1";
        private String Caption = "Objective 1 []";
        private boolean Scaling = false;
        private double Min = 0;
        private double Max = 1;
        private double Weight = 1;

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
