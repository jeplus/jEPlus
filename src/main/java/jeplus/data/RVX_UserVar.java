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

// "userVars" : [
//		{
//			"identifier" : "v2",
//			"formula" : "c2",
//			"caption" : "Variable 2 []",
//			"report" : false
//		}
//	],
@JsonPropertyOrder({ 
    "identifier", 
    "caption", 
    "formula", 
    "report"
})
public class RVX_UserVar implements Serializable, IF_RVXItem {
    private String Identifier = "v1";
    private String Formula = "0";
    private String Caption = "Variable 1 []";
    private boolean Report = false;

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

    public boolean isReport() {
        return Report;
    }

    public void setReport(boolean Report) {
        this.Report = Report;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(Caption);
        buf.append(": ").append(Identifier).append(" = ").append(Formula);
        return buf.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.Identifier);
        hash = 97 * hash + Objects.hashCode(this.Formula);
        hash = 97 * hash + Objects.hashCode(this.Caption);
        hash = 97 * hash + (this.Report ? 1 : 0);
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
        final RVX_UserVar other = (RVX_UserVar) obj;
        if (this.Report != other.Report) {
            return false;
        }
        if (!Objects.equals(this.Identifier, other.Identifier)) {
            return false;
        }
        if (!Objects.equals(this.Formula, other.Formula)) {
            return false;
        }
        if (!Objects.equals(this.Caption, other.Caption)) {
            return false;
        }
        return true;
    }
    
    @Override
    public void copyFrom(IF_RVXItem item) {
        try {
            RVX_UserVar src = (RVX_UserVar)item;
            Identifier = src.Identifier;
            Formula    = src.Formula   ;
            Caption    = src.Caption   ;
            Report     = src.Report    ;
        }catch (ClassCastException cce) {
        }
    }
    
}
