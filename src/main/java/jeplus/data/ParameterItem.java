/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@googlemail.com>               *
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
 * 22/03/2013                                                              *
 *  - Added support for search string and value sets                       *
 *  - Added text qualifier (" ") support                                   *
 *                                                                         *
 ***************************************************************************/
package jeplus.data;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import jeplus.JEPlusProject;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * Encapsulate a parameter for parametric analysis. This includes a variable name,
 * description, short name, lookup string, and alternative values string.
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.1
 */
public class ParameterItem implements Serializable, Cloneable {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(ParameterItem.class);

    // Serialization version code; to maintain backwards compatibility, do not change.
    static final long serialVersionUID = 7439317083898309376L;

    private final static int COMBINE = 1;
    private final static int EXCLUDE = 2;

    // Parameter value types
    public final static int INTEGER = 0;
    public final static int DOUBLE = 1;
    public final static int DISCRETE = 2;
    public final static int VALUE_SETS = 3;
    // Parameter types
    public final static int PARAMETRICS = 0;
    public final static int OPTIMISATION = 1;
    public final static int UNCERTAINTY = 2;
    // Simulation platform types
    public final static int ANY = 0;
    public final static int WINDOWS = 1;
    public final static int LINUX = 2;

    /** Parameter id as a unique identifier */
    public String ID = null;
    /** Parameter name - for information only */
    public String Name = null;
    /** Parameter type: PARAMETRICS, OPTIMISATION or UNCERTAINTY */
    public int ParamType = PARAMETRICS;
    /** Parameter value type: INTEGER, DOUBLE, or DISCRETE */
    public int Type = 0;
    /** Description of the parameter - for information only */
    public String Description = null;
    public String SearchString = null;
    protected String ValuesString = null;
    private boolean ValueStringChanged = true;

    protected String [] AltValues = null;
    protected int NAltValues = 0;
    protected int Platform = 0;
    protected int SelectedAltValue = 0; // 0-use all; 1-the 1st value; 2-the 2nd value ...
    
    /** Reference to project in order to get access to its base dir - for loading parameter from file */
    protected JEPlusProject Project;

    /**
     * Construct an empty entry
     */
    public ParameterItem () {
        Name = "Parameter1";
        Type = INTEGER;
        Description = "new parameter item";
        ID = "P1";
        SearchString = "@@parameter1@@";
        ValuesString = "[ : : ] & { , , , } ^ { , , }";
        Project = null;
    }

    /**
     * Construct an empty entry
     * @param project Reference to the parent project
     */
    public ParameterItem (JEPlusProject project) {
        int pnumber = project.getNumberOfParams() + 1;
        Name = "Parameter " + pnumber;
        Type = INTEGER;
        Description = "new parameter item";
        ID = "P" + pnumber;
        SearchString = "@@tag" + pnumber + "@@";
        ValuesString = "[ : : ] & { , , , } ^ { , , }";
        Project = project;
    }

    /**
     * Construct a new parameter item with supplied information
     * @param name Name of the item
     * @param type Type of the parameter
     * @param desc Description of the item
     * @param id ID of the item to be used for naming the work directory
     * @param sstr SearchString encoded in the E+ IDF template file
     * @param vstr ValuesString encodes the alternative values
     */
    public ParameterItem (String name, int type, String desc, String id, String sstr, String vstr) {
        Name = name;
        Type = type;
        Description = desc;
        ID = id;
        SearchString = sstr;
        ValuesString = vstr;
        ValueStringChanged = true;
    }

    /**
     * Construct a new parameter item with supplied information in a string array.
     * This constructor is used for importing a parameter from a parameter table,
     * see <code>JEPlusProject.importParameterTableFile()</code> for details.
     * @param project Reference to the parent project
     * @param vals values in a string array
     */
    public ParameterItem (JEPlusProject project, String [] vals) {
        // Input sequence: ID, Name, Parameter Type, Description, Search String, Value Type, Value String, Selected Value Index
        try {
            ID = vals[0];
            Name = vals[1];
            ParamType = Integer.parseInt(vals[2]);
            Description = vals[3];
            SearchString = vals[4];
            Type = Integer.parseInt(vals[5]);
            ValuesString = vals[6];
            SelectedAltValue = Integer.parseInt(vals[7]);
            ValueStringChanged = true;
            Project = project;
        }catch (NumberFormatException ex) {
            logger.error("", ex);
        }
    }

    /**
     * Construct a new instance by copying from an existing one
     * @param item The item from which to copy
     */
    public ParameterItem (ParameterItem item) {
        Name = item.Name;
        Type = item.Type;
        Description = item.Description;
        ID = item.ID;
        SearchString = item.SearchString;
        ValuesString = item.ValuesString;
        ValueStringChanged = true;
        Platform = item.Platform;
        SelectedAltValue = item.SelectedAltValue;
        Project = item.Project;
    }


    public boolean isAltValueFixed () {
        return this.SelectedAltValue > 0;
    }

    public int getPlatform() {
        return Platform;
    }

    public void setPlatform(int Platform) {
        this.Platform = Platform;
    }

    public int getSelectedAltValue() {
        return SelectedAltValue;
    }

    public void setSelectedAltValue(int SelectedAltValue) {
        this.SelectedAltValue = SelectedAltValue;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getSearchString() {
        return SearchString;
    }

    public void setSearchString(String SearchString) {
        this.SearchString = SearchString;
    }

    public int getParamType() {
        return ParamType;
    }

    public void setParamType(int ParamType) {
        this.ParamType = ParamType;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getValuesString() {
        return ValuesString;
    }

    public void setValuesString(String ValuesString) {
        this.ValuesString = ValuesString;
        ValueStringChanged = true;
    }

    /**
     * clone the object
     * @return the clone
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Object clone () throws CloneNotSupportedException {
        return new ParameterItem (this);
    }

    /**
     * Return the number of alternative values identified by the 
     * <code>getAlternativeValues ()</code> function.
     * @return Number of alternative values of this parameter item
     */
    public int getNAltValues() {
        if (NAltValues <= 0) getAlternativeValues ();
        return NAltValues;
    }

    /**
     * Retrieve alternative values list. Par
     * @return
     */
    public String [] getAlternativeValues () {
        if (ValueStringChanged || AltValues == null) {
            parseAlternativeValues ();
            ValueStringChanged = false;
        }
        return AltValues;
    }

    public JEPlusProject getProject() {
        return Project;
    }

    public void setProject(JEPlusProject Project) {
        this.Project = Project;
    }
    
    /**
     * Get the minimum of the alternative values. This function is used for 
     * optimisation or sampling purposes. In the current implementation, it
     * takes the left most alt value. This is subject to future changes.
     * @return 
     */
    public double getMinAltValue () {
        double min = Double.NaN;
        String [] vals = getAlternativeValues ();
        if (this.Type == DOUBLE) {
            min = Double.parseDouble(vals[0]);
        }
        return min;
    }

    /**
     * Get the maximum of the alternative values. This function is used for 
     * optimisation or sampling purposes. In the current implementation, it
     * takes the right most alt value. This is subject to future changes.
     * @return 
     */
    public double getMaxAltValue () {
        double max = Double.NaN;
        String [] vals = getAlternativeValues ();
        if (this.Type == DOUBLE) {
            max = Double.parseDouble(vals[vals.length - 1]);
        }
        return max;
    }

    /**
     * This function extracts alternative values from a string. An example
     * of the string is shown here: [ : : ]&[ : : ]&{ , , , }^{ , , }
     * The square brackets define a range, for which lower boundary, interval,
     * and upper boundary are given, e.g. [LB:INT:UB]. The curvy brackets define
     * a list of options, e.g. {v1, v2, v3 ...}. Different ranges and lists can
     * be combined by using a "&" sign. Lists can be excluded, too, by using a
     * "^" prefixes. "^" cannot be used with ranges.
     */
    protected void parseAlternativeValues () {
        //ValuesString = ValuesString.trim();
        if (ValuesString.length() > 0) {
            try {
                //String [] sections = ValuesString.split("\\s*[\\&|\\^]\\s*");
                String [] sections = ValuesString.split("\\s*[\\&\\^]\\s*");
                int [] signs = new int [sections.length];
                // handle first character
                char [] chars = ValuesString.toCharArray();
                char c0 = chars[0];
                if (c0 == '^') {
                    signs[0] = EXCLUDE;
                }else if (c0 == '&' | c0== '[' | c0== '{' | c0 == '@') {
                    signs[0] = COMBINE;
                }else {
                    // something's wrong
                    throw new Exception ("Illegal character at the beginning of the line.");
                }
                // scan for "+" and "-"
                int idx = 1;
                for (int i=1; i<chars.length; i++) {
                    if (chars[i] == '&') {
                        signs[idx] = COMBINE;
                        idx ++;
                    }else if (chars[i] == '^') {
                        signs[idx] = EXCLUDE;
                        idx ++;
                    }
                }
                // parse those sections to be combined
                String [] vals = new String [0];
                for (int i=0; i<signs.length; i++) {
                    if (signs[i] == COMBINE) {
                        String tmp = sections[i].substring(1, sections[i].length()-1).trim();
                        if (sections[i].startsWith("[")) {
                            vals = mergeLists(vals, parseRange(tmp), COMBINE);
                        }else if (sections[i].startsWith("{")) {
                            vals = mergeLists(vals, parseList(tmp), COMBINE);
                        }else if (sections[i].toLowerCase().startsWith("@file")) {
                            int start = sections[i].indexOf("(") + 1;
                            int end = sections[i].indexOf(")");
                            int end_selected = Math.max(0,sections[i].indexOf(",")) + Math.max(0,sections[i].indexOf(";"));
                            if (end_selected == 0) {
                                vals = mergeLists(vals, loadValuesFromFile (sections[i].substring(start, end).trim()), COMBINE, true);
                            }else {
                                int pos = Integer.parseInt(sections[i].substring(end_selected + 1, end).trim());
                                String [] selected = new String [1];                               
                                String [] FileVals = loadValuesFromFile (sections[i].substring(start, end_selected).trim());
                                selected [0] = FileVals [Math.min(Math.max(0, pos - 1), FileVals.length)];
                                vals = mergeLists(vals, selected, COMBINE, true);
                            }
                        }else if (sections[i].toLowerCase().startsWith("@sample")) {
                            int start = sections[i].indexOf("(") + 1;
                            int end = sections[i].indexOf(")");
                            vals = mergeLists(vals, sampleDistribution (sections[i].substring(start, end).trim()), COMBINE, true);
                        }else if (sections[i].toLowerCase().startsWith("@calc")) {
                            int start = sections[i].indexOf("(") + 1;
                            int end = sections[i].lastIndexOf(")");
                            vals = mergeLists(vals, createFormula (sections[i].substring(start, end).trim()), COMBINE, true);
                        }
                    }
                }

                // parse those sections to be excluded
                for (int i=0; i<signs.length; i++) {
                    if (signs[i] == EXCLUDE) {
                        String tmp = sections[i].substring(1, sections[i].length()-1).trim();
                        if (sections[i].startsWith("[")) {
                            vals = mergeLists(vals, parseRange(tmp), EXCLUDE);
                        }else if (sections[i].startsWith("{")) {
                            vals = mergeLists(vals, parseList(tmp), EXCLUDE);
                        }else if (sections[i].toLowerCase().startsWith("@file")) {
                            int start = sections[i].indexOf("(") + 1;
                            int end = sections[i].indexOf(")");
                            int end_selected = Math.max(0,sections[i].indexOf(",")) + Math.max(0,sections[i].indexOf(";"));
                            if (end_selected == 0) {
                                vals = mergeLists(vals, loadValuesFromFile (sections[i].substring(start, end).trim()), EXCLUDE);
                            }else {
                                int pos = Integer.parseInt(sections[i].substring(end_selected + 1, end).trim());
                                String [] selected = new String [1];                               
                                String [] FileVals = loadValuesFromFile (sections[i].substring(start, end_selected).trim());
                                selected [0] = FileVals [Math.min(Math.max(0, pos - 1), FileVals.length)];
                                vals = mergeLists(vals, selected, EXCLUDE);
                            }                            
                        }else if (sections[i].toLowerCase().startsWith("@sample")) {
                            int start = sections[i].indexOf("(") + 1;
                            int end = sections[i].indexOf(")");
                            vals = mergeLists(vals, sampleDistribution (sections[i].substring(start, end).trim()), EXCLUDE);
                        }else if (sections[i].toLowerCase().startsWith("@calc")) {
                            // @calc is not supported in the exclusion mode
                        }
                    }
                }

                // return
                this.NAltValues = vals.length;
                this.AltValues = vals;
                return;

            } catch (MalformedValuesException mve) {
                // does nothing
            } catch (Exception ex) {
                // logger.error("Something's wrong with parameter " + this.getID(), ex);
            }
        }else {
            //System.err.println("Empty string.");
        }
        this.NAltValues = 0;
        this.AltValues = null;
    }

    /**
     * To parse the given string as a range delimited with colon ':', e.g. -20:1:20
     * @param rstr The string to be parse
     * @return A parsed list of strings
     * @throws java.lang.Exception
     */
    private String [] parseRange (String rstr) throws Exception {
        String [] s = rstr.split("\\s*:\\s*");
        if (s.length != 3) throw new MalformedValuesException("Range format: [LB:Int:UB]");
        if (Type == INTEGER) {
            int lb = Integer.parseInt(s[0]);
            int intv = Integer.parseInt(s[1]);
            if (intv <= 0) throw new MalformedValuesException("Range format: [LB:Int:UB]; incremental value must be greater than 0.");
            int ub = Integer.parseInt(s[2]);
            ArrayList<String> list = new ArrayList();
            while (lb <= ub) {
                list.add(Integer.toString(lb));
                lb += intv;
            }
            return list.toArray(new String[0]);
        }else if (Type == DOUBLE) {
            double lb = Double.parseDouble(s[0]);
            double intv = Double.parseDouble(s[1]);
            if (intv <= 0) throw new MalformedValuesException("Range format: [LB:Int:UB]; incremental value must be greater than 0.");
            double ub = Double.parseDouble(s[2]);
            ArrayList<String> list = new ArrayList();
            while (lb <= ub) {
                // list.add(Double.toString(lb));
                String val = new Formatter().format("%g", lb).toString();
                if (val.contains(".")) {    // only when a decimal point is present
                    // split at 'e'
                    String [] parts = val.split("e");
                    val = parts[0];
                    while (val.length() > 3 && val.endsWith("0")) val = val.substring(0, val.length()-1);
                    if (parts.length > 1 && parts[1] != null) val = val.concat("e").concat(parts[1]);
                }
                list.add(val);
                lb += intv;
            }
            return list.toArray(new String[0]);
        }
        throw new MalformedValuesException("parseRange failed: unsupported parameter type.");
    }

    /**
     * Parse the given string as a list delimited by comma ','
     * @param lstr The string to be parsed
     * @return A parsed list of strings
     */
    private String [] parseList (String lstr) {
        ArrayList <String> list = new ArrayList <>();
        Matcher m = Pattern.compile("([^\"][\\S&&[^,;]]*|\".+?\")(\\s|,|;)*").matcher(lstr);
        while (m.find()) {
            if (m.group(1) != null) {
                String part = m.group(1).replace("\"", "");
                list.add(part); 
            }
        }
        return list.toArray(new String [0]);
        //return lstr.split("\\s*[,;]\\s*");
    }

    /**
     * Combining the two lists or excluding the second list from the first.
     * @param vals1 The first list
     * @param vals2 The second list
     * @param op Operation can be either COMBINE or EXCLUDE
     * @return a new list as the combination of the two
     */
    private String [] mergeLists (String [] vals1, String [] vals2, int op) {
        return mergeLists(vals1, vals2, op, false);
    }

    /**
     * Combining the two lists or excluding the second list from the first.
     * @param vals1 The first list
     * @param vals2 The second list
     * @param op Operation can be either COMBINE or EXCLUDE
     * @return a new list as the combination of the two
     */
    private String [] mergeLists (String [] vals1, String [] vals2, int op, boolean allow_duplicates) {
        ArrayList<String>  set = new ArrayList(Arrays.asList(vals1));
        if (op == COMBINE) {
            for (int i=0; i<vals2.length; i++) {
                if (allow_duplicates || ! set.contains(vals2[i])) set.add(vals2[i]);
            }
        }else {
            for (int i=0; i<vals2.length; i++) {
                set.remove(vals2[i]);
            }
        }
        return set.toArray(new String[0]);
    }

    /**
     * Load parameter item's alternative values from a text file
     * @param file Name of file containing alt. values
     * @return String array contains all parsed alternative values
     */
    protected String[] loadValuesFromFile(String file) {
        String path = (Project == null) ? file : RelativeDirUtil.checkAbsolutePath(file, Project.getBaseDir());
        ArrayList <String> list;
        try (BufferedReader fr = new BufferedReader (new FileReader (path))) {
            list = new ArrayList <>();
            String line = fr.readLine();
            while (line != null) {
                // Filter comments
                if (line.contains("!"))
                    line = line.substring(0, line.indexOf("!")).trim();
                // split values in the same line
                if (line.length() > 0) {
                    Matcher m = Pattern.compile("([^\"][\\S&&[^,;]]*|\".+?\")(\\s|,|;)*").matcher(line);
                    while (m.find()) {
                        if (m.group(1) != null) {
                            String part = m.group(1).replace("\"", ""); 
                            list.add(part); 
                        }
                    }
                    //String []  vals = line.trim().split("(\\s|,|;)+");
                    //for (int i=0; i<vals.length; i++) list.add (vals[i]);
                }
                line = fr.readLine();
            }
            return list.toArray(new String [0]);
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    /**
     * Sampling function for probabilistic distributions
     * @param funcstr Function string
     * @return List of sampled values in a string array
     */
    private String[] sampleDistribution(String funcstr) {
        ArrayList <String> list = new ArrayList <>();
        String [] params = funcstr.split("\\s*[,;]\\s*");
        // For integer/double types, returns randomized N samples conforming
        // a specified distribution, currently 'gaussian'/'normal'/'n', 
        // 'uniform'/'u', 'triangular'/'tr', or 'discrete'/'d'
        // for examples: @sample(gaussian, 0, 1.5, 20), with mean, sd and N
        //           or  @sample(uniform, -10, 10, 20), with lb, ub and N
        //           of  @sample(triangular, -1.0, 0.3, 1.0, 20), with lb, mode, ub and N
        //           of  @sample(discrete, option_A, 0.3, option_B, 0.5, option_C, 0.2, 20), with op1, weight1, op2, weight2..., and N
        String distribution = params[0].toLowerCase();
        switch (distribution) {
            case "uniform":
            case "u":
                {
                    // requires lb, ub, n
                    double lb = Double.parseDouble(params[1]);
                    double ub = Double.parseDouble(params[2]);
                    int n = Integer.parseInt(params[3]);
                    if (this.Type == DISCRETE) {
                        // list.add(params[1]);
                        // list.add(params[2]);
                        list = null;
                    }else {
                        for (int i=0; i<n; i++) {
                            if (this.Type == DOUBLE) {
                                list.add(Double.toString(RandomSource.getRandomGenerator().nextDouble() * (ub - lb) + lb));
                            }else if (this.Type == INTEGER) {
                                list.add(Long.toString(Math.round(RandomSource.getRandomGenerator().nextDouble() * (ub - lb) + lb)));
                            }
                        }
                    }
                    break;
                }
            case "gaussian":
            case "normal":
            case "n":
                {
                    // requires mean, sd, n
                    double mean = Double.parseDouble(params[1]);
                    double sd = Double.parseDouble(params[2]);
                    int n = Integer.parseInt(params[3]);
                    if (this.Type == DISCRETE) {
                        // list.add(params[1]);
                        list = null;
                    }else {
                        for (int i=0; i<n; i++) {
                            if (this.Type == DOUBLE) {
                                list.add(Double.toString(RandomSource.getRandomGenerator().nextGaussian() * sd + mean));
                            }else if (this.Type == INTEGER) {
                                list.add(Long.toString(Math.round(RandomSource.getRandomGenerator().nextGaussian() * sd + mean)));
                            }
                        }
                    }
                    break;
                }
            case "triangular":
            case "tr":
                {
                    // requires a(lb), c(tip), b(ub), n
                    double a = Double.parseDouble(params[1]);
                    double c = Double.parseDouble(params[2]);
                    double b = Double.parseDouble(params[3]);
                    // sort a, b, c, so a < c < b
                    // ...
                    int n = Integer.parseInt(params[4]);
                    if (this.Type == DISCRETE || ! (a < c && c < b)) {
                        // list.add(params[1]);
                        // list.add(params[2]);
                        // list.add(params[3]);
                        list = null;
                    }else {
                        for (int i=0; i<n; i++) {
                            double x = RandomSource.getRandomGenerator().nextDouble();
                            double y = 0;
                            if (x <= (c-a)/(b-a)) {
                                y = Math.sqrt((c-a)*(b-a)*x) + a;
                            }else {
                                y = b - Math.sqrt((1 - x) * (b - a) * (b - c));
                            }
                            if (this.Type == DOUBLE) {
                                list.add(Double.toString(y));
                            }else if (this.Type == INTEGER) {
                                list.add(Long.toString(Math.round(y)));
                            }
                        }
                    }
                    break;
                }
            case "discrete":
            case "d":
                {
                    // requires op1, prob1, op2, prob2, ..., n
                    int nOptions = params.length / 2 - 1;
                    String [] options = new String [nOptions];
                    double [] probabilities = new double [nOptions];
                    double [] accProb = new double [nOptions];
                    for (int i=0; i<nOptions; i++) {
                        options[i] = params[2*i+1];
                        try {
                            probabilities[i] = Double.parseDouble(params[2*i+2]);
                        }catch (NumberFormatException nfe) {
                            probabilities[i] = 0.1;
                        }
                        if (i == 0) accProb[i] = probabilities[i];
                        else accProb[i] = accProb[i-1] + probabilities[i];
                    }
                    int n = Integer.parseInt(params[params.length - 1]);
                    for (int i=0; i<n; i++) {
                        double x = RandomSource.getRandomGenerator().nextDouble() * accProb[nOptions - 1];
                        int sel = 0;
                        for (int j=0; j<nOptions; j++) {
                            if (x < accProb[j]) {
                                sel = j;
                                break;
                            }
                        }
                        list.add(options[sel]);
                    }
                    break;
                }
            case "custom":
                // to be implemented
                list = null;
                break;
        }
        return (list == null) ? null : list.toArray(new String [0]);
    }
    
    private String [] createFormula (String funcstr) {
        // scan for parameter ids in the given string, and replace them with the corresponding search tag
        // 1. locate this parameter in the tree
        Enumeration nodes = Project.getParamTree().depthFirstEnumeration();
        DefaultMutableTreeNode thisnode = Project.getParamTree();
        while (nodes.hasMoreElements()) {
            thisnode = (DefaultMutableTreeNode)nodes.nextElement();
            if (thisnode.getUserObject()== this) {
                break;
            }
        }
        Object [] items = thisnode.getUserObjectPath();
        String newstr = "";
        String bufstr = funcstr;
        for (int i=0; i<items.length-1; i++) {
            ParameterItem item = (ParameterItem)items[i];
            newstr = bufstr.replace(item.getID(), item.getSearchString());
            bufstr = newstr;
        }
        return new String [] {"?=" + newstr};
    }

    /**
     * ToString function
     * @return String presentation of the contents of this item
     */
    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder("[");
        buf.append(ID).append("]").append(Name);
        buf.append("::").append(SearchString).append("=");
        buf.append(ValuesString);
        return buf.toString();
    }

    /**
     * Write parameter item definition to a text line following CSV format
     * @return CSV row text
     */
    public String toCSVrow () {
        StringBuilder buf = new StringBuilder (ID);
        buf.append(",").append(Name);
        buf.append(",").append(ParamType);
        buf.append(",").append(Description);
        buf.append(",").append(SearchString);
        buf.append(",").append(Type);
        buf.append(",").append(ValuesString);
        buf.append(",").append(SelectedAltValue);
        return buf.toString();
    }
    
    public String toText () {
        Properties prop = new Properties ();
        prop.setProperty("ID", ID);
        prop.setProperty("Name", Name);
        prop.setProperty("Type", Integer.toString(Type));
        prop.setProperty("Description", Description);
        prop.setProperty("SearchString", SearchString);
        prop.setProperty("ValuesString", ValuesString);
        StringWriter sw = new StringWriter();
        try {
            prop.store(sw, "Parameter item details");
        }catch (Exception ex) {
            logger.error("", ex);
            return null;
        }
        return sw.getBuffer().toString();
    }

    public ParameterItem fromText (String text) {
        StringReader sr = new StringReader (text);
        Properties prop = new Properties ();
        try {
            prop.load(sr);
            ID = prop.getProperty("ID");
            Name = prop.getProperty("Name");
            Type = Integer.parseInt(prop.getProperty("Type"));
            Description = prop.getProperty("Description", "");
            SearchString = prop.getProperty("SearchString");
            ValuesString = prop.getProperty("ValuesString");
            ValueStringChanged = true;
        }catch (IOException | NumberFormatException ex) {
            logger.error("", ex);
            return null;
        }
        return this;
    }

    public boolean exportCSV (String fn) {
        try (PrintWriter fw = new PrintWriter (new FileWriter (fn))) {
            fw.println("Index, ID, NAME, Type, DISCRIPTION, SEARCHSTRING, VALUE");
            String [] vals = getAlternativeValues();
            for (int i=0; i<vals.length; i++) {
                fw.println("" + i + ", " +
                        ID + ", " +
                        Name + ", " +
                        Type + ", " +
                        Description + ", " +
                        SearchString + ", " +
                        vals[i]);
            }
            return true;
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return false;
    }

    public boolean exportSQL (String fn, String tablename) {
        try (PrintWriter fw = new PrintWriter (new FileWriter (fn, true))) {
            fw.println("CREATE TABLE `" + tablename + "` (");
            fw.println("`ID` smallint NOT NULL,");
            fw.println("`Name` varchar(25) NOT NULL,");
            fw.println("`LongName` varchar(255) NOT NULL,");
            fw.println("`Type` smallint NOT NULL,");
            fw.println("`Description` varchar(255) NOT NULL,");
            fw.println("`SearchString` varchar(50) NOT NULL,");
            fw.println("`Value` varchar(255) NOT NULL,");
            fw.println("PRIMARY KEY  (`ID`),");
            fw.println("KEY `Name` (`Name`)");
            fw.println(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            fw.println();

            // Insert command
            fw.println("INSERT INTO `" + tablename + "` (`ID`, `Name`, `LongName`, `Type`, `Description`, `SearchString`, `Value`) VALUES");
            String [] vals = getAlternativeValues();
            for (int i=0; i<vals.length; i++) {
                if (i > 0) fw.println (",");
                fw.print("(" + i + "," +
                        "'" + ID + "', " +
                        "'" + Name + "', " +
                        Type + ", " +
                        "'" + Description + "', " +
                        "'" + SearchString + "', " +
                        "'" + vals[i] + "')");
            }
            fw.println(";");
            fw.println();
            return true;
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return false;
    }

}
