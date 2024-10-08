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
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jeplus.EPlusConfig;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * Main RVX class
 * @author Yi
 */
@JsonPropertyOrder({ 
    "notes", 
    "rvis", 
    "csvs", 
    "sqls", 
    "scripts", 
    "userSupplied", 
    "trns", 
    "userVars",
    "objectives", 
    "constraints"
})
public class RVX implements Serializable {
    
    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(RVX.class);
    
    // Serialization version code; to maintain backwards compatibility, do not change.
    static final long serialVersionUID = 5600302679570045420L;
    
    private ArrayList<RVX_RVIitem> RVIs = new ArrayList<>();
    public ArrayList<RVX_RVIitem> getRVIs() { return RVIs; }
    @JsonSetter("rvis") public void setRVIs(ArrayList<RVX_RVIitem> RVIs) { this.RVIs = RVIs; }
    public void setRVIs(RVX_RVIitem[] RVIs) { this.RVIs.addAll(Arrays.asList(RVIs)); }

    private ArrayList<RVX_ESOitem> ESOs = new ArrayList<>();
    public ArrayList<RVX_ESOitem> getESOs() { return ESOs; }
    @JsonSetter("esos") public void setESOs(ArrayList<RVX_ESOitem> ESOs) { this.ESOs = ESOs; }
    public void setESOs(RVX_ESOitem[] ESOs) { this.ESOs.addAll(Arrays.asList(ESOs)); }

    private ArrayList<RVX_MTRitem> MTRs = new ArrayList<>();
    public ArrayList<RVX_MTRitem> getMTRs() { return MTRs; }
    @JsonSetter("mtrs") public void setMTRs(ArrayList<RVX_MTRitem> MTRs) { this.MTRs = MTRs; }
    public void setMTRs(RVX_MTRitem[] MTRs) { this.MTRs.addAll(Arrays.asList(MTRs)); }
    
    private ArrayList<RVX_SQLitem> SQLs = new ArrayList<>();
    public ArrayList<RVX_SQLitem> getSQLs() { return SQLs; }
    @JsonSetter("sqls") public void setSQLs(ArrayList<RVX_SQLitem> SQLs) { this.SQLs = SQLs; }
    public void setSQLs(RVX_SQLitem[] SQLs) { this.SQLs.addAll(Arrays.asList(SQLs)); }
    
    private ArrayList<RVX_ScriptItem> Scripts = new ArrayList<>();
    public ArrayList<RVX_ScriptItem> getScripts() { return Scripts; }
    @JsonSetter("scripts") public void setScripts(ArrayList<RVX_ScriptItem> Scripts) { this.Scripts = Scripts; }
    public void setScripts(RVX_ScriptItem[] Scripts) { this.Scripts.addAll(Arrays.asList(Scripts)); }
    
    private ArrayList<RVX_CSVitem> CSVs = new ArrayList<>();
    public ArrayList<RVX_CSVitem> getCSVs() { return CSVs; }
    @JsonSetter("csvs") public void setCSVs(ArrayList<RVX_CSVitem> csvs) { this.CSVs = csvs; }
    public void setCSVs(RVX_CSVitem[] csvs) { this.CSVs.addAll(Arrays.asList(csvs)); }
    
    private ArrayList<RVX_UserSuppliedItem> UserSupplied = new ArrayList<>();
    public ArrayList<RVX_UserSuppliedItem> getUserSupplied() { return UserSupplied; }
    @JsonSetter("userSupplied") public void setUserSupplied(ArrayList<RVX_UserSuppliedItem> usersupplied) { this.UserSupplied = usersupplied; }
    public void setUserSupplied(RVX_UserSuppliedItem[] usersupplied) { this.UserSupplied.addAll(Arrays.asList(usersupplied)); }
    
    private ArrayList<RVX_TRNSYSitem> TRNs = new ArrayList<>();
    public ArrayList<RVX_TRNSYSitem> getTRNs() { return TRNs; }
    @JsonSetter("trns") public void setTRNs(ArrayList<RVX_TRNSYSitem> TRNs) { this.TRNs = TRNs; }
    public void setTRNs(RVX_TRNSYSitem[] TRNs) { this.TRNs.addAll(Arrays.asList(TRNs)); }
    
    
    private ArrayList<RVX_UserVar> UserVars = new ArrayList<>();
    public ArrayList<RVX_UserVar> getUserVars() { return UserVars; }
    @JsonSetter("userVars") public void setUserVars(ArrayList<RVX_UserVar> UserVars) { this.UserVars = UserVars; }
    public void setUserVars(RVX_UserVar[] UserVars) { this.UserVars.addAll(Arrays.asList(UserVars)); }
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
    @JsonSetter("constraints") public void setConstraints(ArrayList<RVX_Constraint> Constraints) { this.Constraints = Constraints; }
    public void setConstraints(RVX_Constraint[] Constraints) { this.Constraints.addAll(Arrays.asList(Constraints)); }
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
    @JsonSetter("objectives") public void setObjectives(ArrayList<RVX_Objective> Objectives) { this.Objectives = Objectives; }
    public void setObjectives(RVX_Objective[] Objectives) { this.Objectives.addAll(Arrays.asList(Objectives)); }
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.RVIs);
        hash = 13 * hash + Objects.hashCode(this.SQLs);
        hash = 13 * hash + Objects.hashCode(this.Scripts);
        hash = 13 * hash + Objects.hashCode(this.CSVs);
        hash = 13 * hash + Objects.hashCode(this.UserSupplied);
        hash = 13 * hash + Objects.hashCode(this.TRNs);
        hash = 13 * hash + Objects.hashCode(this.UserVars);
        hash = 13 * hash + Objects.hashCode(this.Constraints);
        hash = 13 * hash + Objects.hashCode(this.Objectives);
        hash = 13 * hash + Objects.hashCode(this.Notes);
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
        final RVX other = (RVX) obj;
        if (!Objects.equals(this.Notes, other.Notes)) {
            return false;
        }
        if (!Objects.equals(this.RVIs, other.RVIs)) {
            return false;
        }
        if (!Objects.equals(this.SQLs, other.SQLs)) {
            return false;
        }
        if (!Objects.equals(this.Scripts, other.Scripts)) {
            return false;
        }
        if (!Objects.equals(this.CSVs, other.CSVs)) {
            return false;
        }
        if (!Objects.equals(this.UserSupplied, other.UserSupplied)) {
            return false;
        }
        if (!Objects.equals(this.TRNs, other.TRNs)) {
            return false;
        }
        if (!Objects.equals(this.UserVars, other.UserVars)) {
            return false;
        }
        if (!Objects.equals(this.Constraints, other.Constraints)) {
            return false;
        }
        if (!Objects.equals(this.Objectives, other.Objectives)) {
            return false;
        }
        return true;
    }
    
    public boolean hasUserVars () {
        return this.UserVars.size() > 0;
    }
    
    public boolean hasDerivedVars () {
        return ! (UserVars.isEmpty() && Constraints.isEmpty() && Objectives.isEmpty());
    }
    
    public void autoGenerateUserVars () {
        // Get existing user vars in a map
        Map<String, RVX_UserVar> UVarsMap = new HashMap<> ();
        for (RVX_UserVar var : UserVars) {
            UVarsMap.put(var.getIdentifier(), var);
        }
        // Go throgh the formulae of objectives and constraints to find refs to c?? variables
        HashSet<String> cx = new HashSet<>();
        for (RVX_Objective obj: Objectives) {
            // Match all c?? patterns
            Matcher m = Pattern.compile("c[0-9]+").matcher(obj.getFormula());
            while (m.find()) {
              cx.add(m.group());
            }
        }
        for (RVX_Constraint cons: Constraints) {
            // Match all c?? patterns
            Matcher m = Pattern.compile("c[0-9]+").matcher(cons.getFormula());
            while (m.find()) {
              cx.add(m.group());
            }
        }
        // Add User variables for the found c??
        Map<String, String> NewVarsMap = new HashMap<> ();
        for (String c: cx) {
            RVX_UserVar v = new RVX_UserVar();
            String id = c.replace("c", "v");
            if (UVarsMap.containsKey(id)) {
                id = id + "_";
            }
            v.setIdentifier(id);
            v.setFormula(c);
            v.setCaption("Output " + c);
            v.setReport(false);
            NewVarsMap.put(c, id);
            UserVars.add(v);
        }
        // Replace references to c?? with new user vars
        for (String c: NewVarsMap.keySet()) {
            for (RVX_Objective obj: Objectives) {
                obj.setFormula(obj.getFormula().replaceAll(c, NewVarsMap.get(c)));
            }
            for (RVX_Constraint cons: Constraints) {
                cons.setFormula(cons.getFormula().replaceAll(c, NewVarsMap.get(c)));
            }
        }
    }
    
    
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
            rvi.setFileName(RelativeDirUtil.getRelativePath(new File(rvxfile).getCanonicalPath(), basedir, "/", true));
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
