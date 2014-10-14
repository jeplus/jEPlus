/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@googlemail.com>               *
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

import java.util.*;
import org.slf4j.LoggerFactory;

/**
 * INSELTask class encapsulates the details of a INSEL job to be run.
 * @author Yi Zhang
 * @version 1.4
 * @since 1.4
 */
public class INSELTask extends EPlusTask {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(INSELTask.class);

    //static final long serialVersionUID = 1587629823039332802L;
    
    protected static ArrayList<String> jeplusfiles = new ArrayList<>();

    /**
     * Create an instance of the INSEL task
     * @param env Data packet including template
     * @param label Label of the task
     * @param id ID of the task
     * @param prevkey Search string list
     * @param prevval Alt value list
     */
    public INSELTask(EPlusWorkEnv env, String label, int id, ArrayList<String> prevkey, ArrayList<String> prevval) {
        super (env, label, id, prevkey, prevval);
    }

    /**
     * Create an instance of the INSEL task
     * @param env Data packet including template
     * @param job_id Externally defined job_ID string of this task
     * @param prevkey Search string list
     * @param prevval Alt value list
     */
    public INSELTask(EPlusWorkEnv env, String job_id, ArrayList<String> prevkey, ArrayList<String> prevval) {
        super (env, job_id, prevkey, prevval);
    }

    public String getOutputPrinter() {
        return WorkEnv.getOutputFileNames();
    }
    
    public static ArrayList<String> getjeplusfiles() {
        return jeplusfiles;
    }
    
    public static void setjeplusfile(String name) {
        jeplusfiles.add(name);
    }
    
    /**
     * Preprocess the input file
     * @return Operation successful or not
     */
    @Override
    public boolean preprocessInputFile (JEPlusConfig config) {
        boolean ok;
        String[] SearchStrings = SearchStringList.toArray(new String[0]);
        String[] Newvals = AltValueList.toArray(new String[0]);
        
        ok = INSELWinTools.updateModelFile(WorkEnv.getINSELTemplate(), WorkEnv.getINSELDir(), INSELConfig.getInselDefModel(), getWorkingDir(), SearchStrings, Newvals, WorkEnv.getOutputFileNames());
        
        return ok;
    }

    /**
     * Execute this task in local machine
     */
    @Override
    public void run() {
        Executed = true;
        // Prepare work directory
        boolean ok = INSELWinTools.prepareWorkDir(getWorkingDir());
        // Write DCK file
        ok = ok && this.preprocessInputFile(JEPlusConfig.getDefaultInstance());
        // Ready to run TRNSYS
        if (ok) {
            int code = INSELWinTools.runINSEL(JEPlusConfig.getDefaultInstance(), getWorkingDir(), INSELConfig.getInselDefModel());
            ok = (code >= 0) && INSELWinTools.isAnyFileAvailable(getOutputPrinter(), getWorkingDir());
        }      
        // Remove temperory files/dir if required
        if (ok) {
            ok = INSELWinTools.cleanupWorkDir(getWorkingDir(), 
                    WorkEnv.KeepEPlusFiles, WorkEnv.KeepJEPlusFiles, WorkEnv.KeepJobDir, WorkEnv.SelectedFiles, WorkEnv.OutputFileNames);
        }
        ResultAvailable = ok;
    }
    
}