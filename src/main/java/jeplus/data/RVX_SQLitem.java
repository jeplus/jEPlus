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
public class RVX_SQLitem implements Serializable {
    private String TableName = "SqlTable.csv"; // E.g.
    private String ColumnHeaders = ""; // E.g. "Temperature [K], Heating [kWh]"
    private String SQLcommand = ""; // E.g.
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

    public String getSQLcommand() {
        return SQLcommand;
    }

    public void setSQLcommand(String SQLcommand) {
        this.SQLcommand = SQLcommand;
    }

    public boolean isUsedInCalc() {
        return UsedInCalc;
    }

    public void setUsedInCalc(boolean UsedInCalc) {
        this.UsedInCalc = UsedInCalc;
    }
    
    @Override
    public String toString () {
        return TableName + ":" + ColumnHeaders + (UsedInCalc ? "" : "*");
    }
}
