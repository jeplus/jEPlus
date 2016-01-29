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

//	"userSupplied" : [
//		{
//			"fileName" : "ExternResultTable.csv",
//			"headerRow" : 0,
//			"jobIdColumn" : 1,
//			"dataColumns" : "3 4",
//                      "missingValue" : 0,
//			"tableName" : "UserResults"
//		}
//	],
public class RVX_UserSuppliedItem implements Serializable {
    private String FileName = "ExternResultTable.csv";
    private int HeaderRow = 0;
    private int JobIdColumn = 1;
    private String DataColumns = "3";
    private double MissingValue = 0;
    private String TableName = "UserResults";

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public int getHeaderRow() {
        return HeaderRow;
    }

    public void setHeaderRow(int HeaderRow) {
        this.HeaderRow = HeaderRow;
    }

    public int getJobIdColumn() {
        return JobIdColumn;
    }

    public void setJobIdColumn(int JobIdColumn) {
        this.JobIdColumn = JobIdColumn;
    }

    public String getDataColumns() {
        return DataColumns;
    }

    public void setDataColumns(String DataColumns) {
        this.DataColumns = DataColumns;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String TableName) {
        this.TableName = TableName;
    }

    public double getMissingValue() {
        return MissingValue;
    }

    public void setMissingValue(double MissingValue) {
        this.MissingValue = MissingValue;
    }
    
}
