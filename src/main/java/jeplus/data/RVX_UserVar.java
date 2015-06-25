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

//	"userVars" : [

//		{
//			"identifier" : "v2",
//			"formula" : "c2",
//			"caption" : "Variable 2 []",
//			"report" : false
//		}
//	],
public class RVX_UserVar implements Serializable {
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
    
}
