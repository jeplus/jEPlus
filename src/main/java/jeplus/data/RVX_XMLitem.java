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
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.TableName);
        hash = 59 * hash + Objects.hashCode(this.ColumnHeaders);
        hash = 59 * hash + Objects.hashCode(this.XPath);
        hash = 59 * hash + (this.UsedInCalc ? 1 : 0);
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
        final RVX_XMLitem other = (RVX_XMLitem) obj;
        if (this.UsedInCalc != other.UsedInCalc) {
            return false;
        }
        if (!Objects.equals(this.TableName, other.TableName)) {
            return false;
        }
        if (!Objects.equals(this.ColumnHeaders, other.ColumnHeaders)) {
            return false;
        }
        if (!Objects.equals(this.XPath, other.XPath)) {
            return false;
        }
        return true;
    }
    
}
