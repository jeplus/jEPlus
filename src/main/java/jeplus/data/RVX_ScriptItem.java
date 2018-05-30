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

//	"scripts" : [
//		{
//			"fileName" : "readRunTimes_jy.py",
//			"pythonVersion" : "jython",
//			"onEachJob" : false,
//			"arguments" : "",
//			"tableName" : "CpuTime"
//		}
//	],
public class RVX_ScriptItem implements Serializable {
    
    public enum Language {
        python2,
        python3
    };
    
    private String FileName = "script.py";
    private String PythonVersion = "python3";
    private boolean OnEachJob = false;
    private String Arguments = "";
    private String TableName = "script_table";

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public boolean isOnEachJob() {
        return OnEachJob;
    }

    public void setOnEachJob(boolean OnEachJob) {
        this.OnEachJob = OnEachJob;
    }

    public String getPythonVersion() {
        return PythonVersion;
    }

    public void setPythonVersion(String PythonVersion) {
        this.PythonVersion = PythonVersion.equalsIgnoreCase("jython") ? "python2" : PythonVersion;
    }

    public String getArguments() {
        return Arguments;
    }

    public void setArguments(String Arguments) {
        this.Arguments = Arguments;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String TableName) {
        this.TableName = TableName;
    }
    
    @Override
    public String toString () {
        return TableName + ":" + FileName + "(" + PythonVersion + ")" + (OnEachJob ? "+" : "");
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.FileName);
        hash = 43 * hash + Objects.hashCode(this.PythonVersion);
        hash = 43 * hash + (this.OnEachJob ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.Arguments);
        hash = 43 * hash + Objects.hashCode(this.TableName);
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
        final RVX_ScriptItem other = (RVX_ScriptItem) obj;
        if (this.OnEachJob != other.OnEachJob) {
            return false;
        }
        if (!Objects.equals(this.FileName, other.FileName)) {
            return false;
        }
        if (!Objects.equals(this.PythonVersion, other.PythonVersion)) {
            return false;
        }
        if (!Objects.equals(this.Arguments, other.Arguments)) {
            return false;
        }
        if (!Objects.equals(this.TableName, other.TableName)) {
            return false;
        }
        return true;
    }
}
