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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Objects;

// "rvis" : [
//		{
//			"fileName" : "5ZoneCostEst2.rvi",
//                      "frequency" : "Annual",
//			"tableName" : "SimResults2",
//                      "usedInCalc" : true
//		}
//	],
@JsonPropertyOrder({ 
    "fileName", 
    "frequency", 
    "tableName", 
    "usedInCalc"
})
public class RVX_RVIitem implements Serializable, IF_RVXItem {
    
    public static enum Frequencies {
        Annual,
        RunPeriod,
        Monthly,
        Daily,
        Hourly,
        Timestep,
        Detailed
    };
    private String FileName = "my.rvi";
    private String Frequency = "RunPeriod";
    private String TableName = "SimResults";
    private boolean UsedInCalc = true;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public String getFrequency() {
        return Frequency;
    }

    public void setFrequency(String Frequency) {
        this.Frequency = Frequency;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String TableName) {
        this.TableName = TableName;
    }

    public boolean isUsedInCalc() {
        return UsedInCalc;
    }

    public void setUsedInCalc(boolean UsedInCalc) {
        this.UsedInCalc = UsedInCalc;
    }
    
    @Override
    public String toString () {
        return TableName + ":" + FileName + "(" + Frequency + ")" + (UsedInCalc ? "" : "*");
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.FileName);
        hash = 53 * hash + Objects.hashCode(this.Frequency);
        hash = 53 * hash + Objects.hashCode(this.TableName);
        hash = 53 * hash + (this.UsedInCalc ? 1 : 0);
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
        final RVX_RVIitem other = (RVX_RVIitem) obj;
        if (this.UsedInCalc != other.UsedInCalc) {
            return false;
        }
        if (!Objects.equals(this.FileName, other.FileName)) {
            return false;
        }
        if (!Objects.equals(this.Frequency, other.Frequency)) {
            return false;
        }
        if (!Objects.equals(this.TableName, other.TableName)) {
            return false;
        }
        return true;
    }

    @Override
    public void copyFrom(IF_RVXItem item) {
        try {
            RVX_RVIitem src = (RVX_RVIitem)item;
            FileName   = src.FileName  ;
            Frequency  = src.Frequency ;
            TableName  = src.TableName ;
            UsedInCalc = src.UsedInCalc;
        }catch (ClassCastException cce) {
        }
    }
}
