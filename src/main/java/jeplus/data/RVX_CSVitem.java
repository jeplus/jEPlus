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

import java.io.Serializable;

//	"csvs" : [
//		{
//                      "sourceCsv"  : "eplustbl.csv",
//                      "fromReport"  : "Annual Building Utility Performance Summary",
//                      "fromTable"  : "Heating Coils",
//                      "fromColumn" : "Nominal Total Capacity [W]",
//                      "fromRow"    : "TEST AIR-TO-AIR HEAT PUMP HP HEATING COIL",
//			"tableName"  : "CoilCapacity",
//			"columnHeaders" : "Nominal Total Capacity - HP Heating Coil [W]",
//                      "usedInCalc" : true
//		}
//	],
public class RVX_CSVitem implements Serializable {
    private String SourceCsv = "eplustbl.csv"; 
    private String FromReport = ""; 
    private String FromTable = ""; 
    private String FromColumn = ""; 
    private String FromRow = ""; 
    private String TableName = "CsvTable"; // E.g.
    private String ColumnHeaders = null; // E.g. "Temperature [K], Heating [kWh]"
    private boolean UsedInCalc = true;

    public String getSourceCsv() {
        return SourceCsv;
    }

    public void setSourceCsv(String SourceCsv) {
        this.SourceCsv = SourceCsv;
    }

    public String getFromReport() {
        return FromReport;
    }

    public void setFromReport(String FromReport) {
        this.FromReport = FromReport;
    }

    public String getFromTable() {
        return FromTable;
    }

    public void setFromTable(String FromTable) {
        this.FromTable = FromTable;
    }

    public String getFromColumn() {
        return FromColumn;
    }

    public void setFromColumn(String FromColumn) {
        this.FromColumn = FromColumn;
    }

    public String getFromRow() {
        return FromRow;
    }

    public void setFromRow(String FromRow) {
        this.FromRow = FromRow;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String TableName) {
        this.TableName = TableName;
    }

    public String getColumnHeaders() {
        return ColumnHeaders;
    }

    public void setColumnHeaders(String ColumnHeaders) {
        this.ColumnHeaders = ColumnHeaders;
    }

    public boolean isUsedInCalc() {
        return UsedInCalc;
    }

    public void setUsedInCalc(boolean UsedInCalc) {
        this.UsedInCalc = UsedInCalc;
    }
    
    @Override
    public String toString () {
        return TableName + ":" + ColumnHeaders + "(" + SourceCsv + ")" + (UsedInCalc ? "" : "*");
    }
}
