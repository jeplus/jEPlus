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

import java.io.*;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;

/**
 * FileList object encapsulates a special parameter type, the input files.
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.1
 */
public class FileList extends ArrayList<String> {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(FileList.class);
    
    protected String GroupReference = null;
    protected String IDprefix = null;
    protected String Directory = null;

    public FileList (String grpref, String prefix, String dir) {
        this.GroupReference = grpref;
        this.IDprefix = prefix;
        this.Directory = dir;
    }
    
    public String getIDprefix () {
        return this.IDprefix;
    }
    
    public void setIDprefix (String prefix) {
        this.IDprefix = prefix;
    }
    
    public void setGroupReference (String grp) {
        this.GroupReference = grp;
    }
    
    /**
     * Validate the file list by checking the presence of each file
     * @param errors Collection of error details
     * @return Validation successful (all files are accessible) or not
     */
    public boolean validate (ArrayList<String> errors) {
        boolean ok = true;
        try {
            for (int i=0; i<this.size(); i++) {
                File fn = new File (Directory + (String)get(i));
                if (! (fn.canRead() && fn.isFile())) {
                    if (errors != null) errors.add("  " + fn.getAbsolutePath() + " does not exist or cannot be read.");
                    ok = false;
                }
            }
        }catch (Exception ex) {
            logger.error("", ex);
            ok = false;
        }
        return ok;
    }

    public boolean exportCSV (String fn) {
        try (PrintWriter fw = new PrintWriter (new FileWriter (fn))) {
            fw.println("ID, GROUP, NAME, DIRECTORY, FILE");
            for (int i=0; i<this.size(); i++) {
                fw.println("" + i + ", " + 
                        GroupReference + ", " + 
                        (IDprefix+i) + ", " + 
                        Directory + ", " +
                        this.get(i));
            }
            return true;
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return false;
    }

    public boolean exportSQL (String fn, String tablename) {
            try (PrintWriter fw = new PrintWriter (new FileWriter (fn, true))) {
                fw.println("CREATE TABLE `" + tablename + "` (");
                fw.println("`ID` smallint NOT NULL,");
                fw.println("`Group` varchar(25) NOT NULL,");
                fw.println("`Name` varchar(25) NOT NULL,");
                fw.println("`Directory` varchar(255) NOT NULL,");
                fw.println("`FileName` varchar(255) NOT NULL,");
                fw.println("PRIMARY KEY  (`ID`),");
                fw.println("KEY `Group_Ref` (`Group`)");
                fw.println(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
                fw.println();

                // Insert command
                fw.println("INSERT INTO `" + tablename + "` (`ID`, `Group`, `Name`, `Directory`, `FileName`) VALUES");
                for (int i=0; i<this.size(); i++) {
                    if (i > 0) fw.println(",");
                    fw.print("(" + i + "," +
                            "'" + GroupReference + "', '" +
                            (IDprefix+i) + "', '" +
                            Directory + "', '" +
                            this.get(i) + "')");
                }
                fw.println(";");
                fw.println();
            return true;
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return false;
    }
}
