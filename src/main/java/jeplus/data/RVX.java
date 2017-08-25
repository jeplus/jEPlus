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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import jeplus.EPlusConfig;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * Main RVX class
 * @author Yi
 */
public class RVX implements Serializable {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(RVX.class);
    
    // Serialization version code; to maintain backwards compatibility, do not change.
    static final long serialVersionUID = 5600302679570045420L;
    
    private ArrayList<RVX_RVIitem> RVIs = new ArrayList<>();
    public ArrayList<RVX_RVIitem> getRVIs() { return RVIs; }
    public void setRVIs(ArrayList<RVX_RVIitem> RVIs) { this.RVIs = RVIs; }

    
    private ArrayList<RVX_SQLitem> SQLs = new ArrayList<>();
    public ArrayList<RVX_SQLitem> getSQLs() { return SQLs; }
    public void setSQLs(ArrayList<RVX_SQLitem> SQLs) { this.SQLs = SQLs; }

    
    private ArrayList<RVX_ScriptItem> Scripts = new ArrayList<>();
    public ArrayList<RVX_ScriptItem> getScripts() { return Scripts; }
    public void setScripts(ArrayList<RVX_ScriptItem> Scripts) { this.Scripts = Scripts; }

    
    private ArrayList<RVX_CSVitem> CSVs = new ArrayList<>();
    public ArrayList<RVX_CSVitem> getCSVs() { return CSVs; }
    public void setCSVs(ArrayList<RVX_CSVitem> csvs) { this.CSVs = csvs; }

    
    private ArrayList<RVX_UserSuppliedItem> UserSupplied = new ArrayList<>();
    public ArrayList<RVX_UserSuppliedItem> getUserSupplied() { return UserSupplied; }
    public void setUserSupplied(ArrayList<RVX_UserSuppliedItem> usersupplied) { this.UserSupplied = usersupplied; }

    
    private ArrayList<RVX_TRNSYSitem> TRNs = new ArrayList<>();
    public ArrayList<RVX_TRNSYSitem> getTRNs() { return TRNs; }
    public void setTRNs(ArrayList<RVX_TRNSYSitem> TRNs) { this.TRNs = TRNs; }

    
    
    private ArrayList<RVX_UserVar> UserVars = new ArrayList<>();
    public ArrayList<RVX_UserVar> getUserVars() { return UserVars; }
    public void setUserVars(ArrayList<RVX_UserVar> UserVars) { this.UserVars = UserVars; }
    @JsonIgnore 
    public ArrayList<RVX_UserVar> getReportedUserVars () {
        ArrayList<RVX_UserVar> list = new ArrayList<> ();
        if (UserVars != null) {
            for (RVX_UserVar var : UserVars) {
                if (var.isReport()) list.add(var);
            }
        }
        return list;
    }

	
    private ArrayList<RVX_Constraint> Constraints = new ArrayList<>();
    public ArrayList<RVX_Constraint> getConstraints() { return Constraints; }
    public void setConstraints(ArrayList<RVX_Constraint> Constraints) { this.Constraints = Constraints; }
    @JsonIgnore 
    public ArrayList<RVX_Constraint> getEnabledConstraints () {
        ArrayList<RVX_Constraint> list = new ArrayList<> ();
        if (Constraints != null) {
            for (RVX_Constraint cons : Constraints) {
                if (cons.isEnabled()) list.add(cons);
            }
        }
        return list;
    }

	
    private ArrayList<RVX_Objective> Objectives = new ArrayList<>();
    public ArrayList<RVX_Objective> getObjectives() { return Objectives; }
    public void setObjectives(ArrayList<RVX_Objective> Objectives) { this.Objectives = Objectives; }
    @JsonIgnore 
    public ArrayList<RVX_Objective> getEnabledObjectives () {
        ArrayList<RVX_Objective> list = new ArrayList<> ();
        if (Objectives != null) {
            for (RVX_Objective obj : Objectives) {
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
    public static RVX getRVX (String rvxfile, String basedir) throws IOException {
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
            RVX_RVIitem rvi = new RVX_RVIitem();
            rvi.setFileName(RelativeDirUtil.getRelativePath(new File(basedir), new File(rvxfile).getParentFile()) + new File(rvxfile).getName());
            rvi.setTableName("SimResults");
            rvx.getRVIs().add(rvi);

            // SQLite section
            if (sqlite.size() > 0) {
                for (String[] row : sqlite) {
                    RVX_SQLitem sql = new RVX_SQLitem();
                    if (row.length >= 3) {
                        sql.setTableName(row[0]);
                        sql.setColumnHeaders(row[1]);
                        sql.setSQLcommand(row[2]);
                        rvx.getSQLs().add(sql);
                    }
                }
            }
            // Objective section
            if (objectives.size() > 0) {
                for (int i=0; i<objectives.size(); i++) {
                    String [] row = objectives.get(i);
                    RVX_Objective obj = new RVX_Objective();
                    if (row.length >= 3) {
                        obj.setIdentifier("t" + i);
                        obj.setCaption(row[0] + " [" + row[1] + "]");
                        obj.setFormula(row[2]);
                        obj.setScaling(false);
                        rvx.getObjectives().add(obj);
                    }
                }
            }
            // Constraint section is empty
            
            // Retrun rvx
            return rvx;
        }
    }
    
    /**
     * Create a quick index of contents of this RVX. This is used by the RVX editor
     * @return Array contains a list of keywords
     */
    public static String[] quickIndex() {
        String [] list = {"rvis", "sqls", "csvs", "scripts", "userSupplied", "userVars", "constraints", "objectives"};
        return list;
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
