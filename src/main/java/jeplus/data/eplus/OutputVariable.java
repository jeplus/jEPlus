/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2013  Yi Zhang <yizhanguk@gmail.com>                    *
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
package jeplus.data.eplus;

/**
 * Data structure for representing EnergyPlus output variable
 * @author Yi
 */
public class OutputVariable {
    public enum VarType {
        Zone,
        HVAC
    }
    
    public enum VarReportType {
        Meter,
        Average,
        Sum
    }
    
    protected String Name = null;
    protected String Units = null;
    protected VarType Type = VarType.Zone;
    protected VarReportType ReportType = VarReportType.Average;
    
    /**
     * Default constructor
     */
    public OutputVariable () {
        
    }
    
    /**
     * Construct a variable record from the given string
     * @param line A line of variable definition from either a .rdd or a .mdd
     */
    public static OutputVariable getOutputVariable (String line) {
        if (line != null && line.length() > 7) {    // "a,b,c[]"
            String [] parts = line.split(",");
            if (parts.length < 3 || ! parts [2].contains("[")) {
                System.err.println ("Input line \"" + line + "\" is not a valid variable definition.");
                return null;
            }
            OutputVariable var = new OutputVariable ();
            var.Type = VarType.valueOf(parts[0]);
            var.ReportType = VarReportType.valueOf(parts[1]);
            var.Name = parts[2].substring(0, parts[2].indexOf("[")).trim();
            var.Units = parts[2].substring(parts[2].indexOf("[")+1, parts[2].indexOf("]")).trim();
            return var;
        }
        return null;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getUnits() {
        return Units;
    }

    public void setUnits(String Units) {
        this.Units = Units;
    }

    public VarType getType() {
        return Type;
    }

    public void setType(VarType Type) {
        this.Type = Type;
    }

    public VarReportType getReportType() {
        return ReportType;
    }

    public void setReportType(VarReportType ReportType) {
        this.ReportType = ReportType;
    }
    
    
}
