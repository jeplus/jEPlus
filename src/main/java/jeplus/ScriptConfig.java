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
package jeplus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.slf4j.LoggerFactory;

/**
 * Configuration on how to run certain script (e.g. Python)
 * @author Yi Zhang
 */
public class ScriptConfig extends ConfigFileNames {

    /** Logger */
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ScriptConfig.class);

    /**
     * Script settings
     */
    protected String ScriptExt = "";
    protected String Args = "";
    protected String Exec = "Select the executable...";
    protected String VerCmd = "-v";

    public ScriptConfig() {
        super ();
        ScreenFile = "console.log";
    }

    public String getScriptExt() {
        return ScriptExt;
    }

    public void setScriptExt(String ScriptExt) {
        this.ScriptExt = ScriptExt;
    }

    public String getArgs() {
        return Args;
    }

    public void setArgs(String Args) {
        this.Args = Args;
    }

    public String getExec() {
        return Exec;
    }

    public void setExec(String Exec) {
        this.Exec = Exec;
    }

    public String getVerCmd() {
        return VerCmd;
    }

    public void setVerCmd(String VerCmd) {
        this.VerCmd = VerCmd;
    }

    @JsonIgnore
    public boolean isValid () {
        File exec = new File (Exec);
        return exec.exists() && exec.canExecute();
    }
    
    /**
     * Get a
     * <code>javax.swing.filechooser.FileFilter</code>
     *
     * @return Swing FileFilter of the specific type
     */
    @JsonIgnore
    public FileFilter getFileFilter() {
        FileFilter ff = new FileNameExtensionFilter("Script file (." + getScriptExt() + ")", getScriptExt());
        return ff;
    }
    
    @Override
    public String toString () {
        return "" + Exec + " " + Args + " script." + ScriptExt;
    }
}
