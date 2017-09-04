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
    
    @Override
    public String toString () {
        return TableName + ":" + FileName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.FileName);
        hash = 17 * hash + this.HeaderRow;
        hash = 17 * hash + this.JobIdColumn;
        hash = 17 * hash + Objects.hashCode(this.DataColumns);
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.MissingValue) ^ (Double.doubleToLongBits(this.MissingValue) >>> 32));
        hash = 17 * hash + Objects.hashCode(this.TableName);
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
        final RVX_UserSuppliedItem other = (RVX_UserSuppliedItem) obj;
        if (this.HeaderRow != other.HeaderRow) {
            return false;
        }
        if (this.JobIdColumn != other.JobIdColumn) {
            return false;
        }
        if (Double.doubleToLongBits(this.MissingValue) != Double.doubleToLongBits(other.MissingValue)) {
            return false;
        }
        if (!Objects.equals(this.FileName, other.FileName)) {
            return false;
        }
        if (!Objects.equals(this.DataColumns, other.DataColumns)) {
            return false;
        }
        if (!Objects.equals(this.TableName, other.TableName)) {
            return false;
        }
        return true;
    }
}
