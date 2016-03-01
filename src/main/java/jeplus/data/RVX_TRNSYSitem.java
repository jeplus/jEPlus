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

//	"trnsys" : [
//		{
//			"plotterName" : "plotter1",
//                      "aggregation" : "None",
//			"tableName" : "SimResults_plotter1",
//                      "usedInCalc" : true
//		}
//	],
public class RVX_TRNSYSitem implements Serializable {
    private String PlotterName = "plotter1";
    private String Aggregation = "None"; // Or 'LastRow', 'Sum', 'Average' ... to be implemented
    private String TableName = "SimResults_plotter1";
    private boolean UsedInCalc = true;

    public String getPlotterName() {
        return PlotterName;
    }

    public void setPlotterName(String PlotterName) {
        this.PlotterName = PlotterName;
    }

    public String getAggregation() {
        return Aggregation;
    }

    public void setAggregation(String Aggregation) {
        this.Aggregation = Aggregation;
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
    
}
