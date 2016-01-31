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

//	"constraints" : [

//		{
//			"identifier" : "s1",
//			"formula" : "v1/1000",
//			"caption" : "Chiller Capacity [kW]",
//			"scaling" : true,
//			"lb" : 0,
//			"ub" : 200,
//			"min" : 0,
//			"max" : 300,
//			"weight" : 1.0,
//                      "enabled" : true
//		}
//	],
public class RVX_Constraint implements Serializable {
    private String Identifier = "s1";
    private String Formula = "c1";
    private String Caption = "Constraint 1 []";
    private boolean Scaling = false;
    private double LB = 0;
    private double UB = 1;
    private double Min = 0;
    private double Max = 1;
    private double Weight = 1;
    private boolean Enabled = true;

    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String Identifier) {
        this.Identifier = Identifier;
    }

    public String getFormula() {
        return Formula;
    }

    public void setFormula(String Formula) {
        this.Formula = Formula;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String Caption) {
        this.Caption = Caption;
    }

    public boolean isScaling() {
        return Scaling;
    }

    public void setScaling(boolean Scaling) {
        this.Scaling = Scaling;
    }

    public double getLB() {
        return LB;
    }

    public void setLB(double LB) {
        this.LB = LB;
    }

    public double getUB() {
        return UB;
    }

    public void setUB(double UB) {
        this.UB = UB;
    }

    public double getMin() {
        return Min;
    }

    public void setMin(double Min) {
        this.Min = Min;
    }

    public double getMax() {
        return Max;
    }

    public void setMax(double Max) {
        this.Max = Max;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double Weight) {
        this.Weight = Weight;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean Enabled) {
        this.Enabled = Enabled;
    }

    /**
     * Normalize and scale (weigh) the objective value. User is responsible for ensuring the correct values of mMax and mMin
     * @param initval
     * @return
     */
    public double scale(double initval) {
        double val = 0;
        if (initval <= Min || initval >= Max) {
            val = 1.;
        } else if (initval >= LB && initval <= UB) {
            val = 0.;
        } else if (initval > Min && initval < LB) {
            val = (LB - initval) / (LB - Min);
        } else if (initval > UB && initval < Max) {
            val = (initval - UB) / (Max - UB);
        }
        return Weight * val;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(Caption);
        buf.append(": ").append(Identifier).append(" = ").append(Formula);
        if (Scaling) {
            buf.append("; feasible range [").append(LB).append(", ").append(UB).append("], normalized between [");
            buf.append(Min).append(", ").append(Max).append("] ");
        }
        return buf.toString();
    }
    
}
