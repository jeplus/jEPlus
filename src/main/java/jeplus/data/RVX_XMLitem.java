/*
 * Copyright (C) 2015 Yi
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

//	"xmls" : [
//		{
//			"tableName" : "ChillerCap",
//			"columnHeaders" : "Chiller Nominal Capacity [W]",
//			"xpath" : "xpath command..."
//                      "useInCalc" : true
//		}
//	],
public class RVX_XMLitem implements Serializable {
    private String TableName = "SqlTable.csv"; // E.g.
    private String ColumnHeaders = ""; // E.g. "Temperature [K], Heating [kWh]"
    private String XPath = ""; // E.g.
    private boolean UsedInCalc = true;

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

    public String getXPath() {
        return XPath;
    }

    public void setXPath(String XPath) {
        this.XPath = XPath;
    }


    public boolean isUsedInCalc() {
        return UsedInCalc;
    }

    public void setUsedInCalc(boolean UsedInCalc) {
        this.UsedInCalc = UsedInCalc;
    }
    
}
