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
    protected String ScriptHome = "";
    protected String ScriptExec = "";

    public ScriptConfig() {
        super ();
        ScriptExt = "";
        ScriptHome = "";
        ScriptExec = "";
        ScreenFile = "console.log";
    }

    public String getScriptExt() {
        return ScriptExt;
    }

    public void setScriptExt(String ScriptExt) {
        this.ScriptExt = ScriptExt;
    }

    public String getScriptHome() {
        return ScriptHome;
    }

    public void setScriptHome(String ScriptHome) {
        this.ScriptHome = ScriptHome;
    }

    public String getScriptExec() {
        return ScriptExec;
    }

    public void setScriptExec(String ScriptExec) {
        this.ScriptExec = ScriptExec;
    }

    
    /**
     * Get a
     * <code>javax.swing.filechooser.FileFilter</code>
     *
     * @return Swing FileFilter of the specific type
     */
    public FileFilter getFileFilter() {
        FileFilter ff = new FileNameExtensionFilter("Script file (." + getScriptExt() + ")", getScriptExt());
        return ff;
    }
}
