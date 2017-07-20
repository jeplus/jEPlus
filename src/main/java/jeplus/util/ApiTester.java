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
package jeplus.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeplus.EPlusBatch;
import jeplus.EPlusWinTools;
import jeplus.JEPlusConfig;
import jeplus.JEPlusProject;
import jeplus.Main;
import jeplus.agent.EPlusAgentLocal;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class ApiTester {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(ApiTester.class);

    public static void main (String [] args) throws IOException {
        
        // configure logger
        PropertyConfigurator.configure("log4j.cfg");

        // load jEPlus configuration file
        JEPlusConfig.setDefaultInstance(new JEPlusConfig("jeplus.cfg"));

        // load project file
        JEPlusProject Project = JEPlusProject.loadAsXML(new File("example_3-RVX_v1.6_E+v8.3/project.jep")); // Or your own project file

        // create simulation manager
        EPlusBatch SimManager = new EPlusBatch (null, Project);

        // Set simulation agent
        SimManager.setAgent(new EPlusAgentLocal ( Project.getExecSettings()));

        // Validate project
        SimManager.validateProject();

        // If project is valid
        if (SimManager.getBatchInfo().isValidationSuccessful()) {

            // specify jobs
            String [][] jobs = new String [][] {{"case-1", "0", "0", "0", "7500", "350", "1.05", "-1"}, 
                                                {"case-2", "0", "0", "90", "3000", "800", "1.05", "-1"},
                                                {"case-3", "0", "0", "180", "3500", "200", "1.45", "-1"},
                                                {"case-4", "0", "0", "270", "175", "650", "1.05", "-1"}};
                                                // Or specify your own value set
            // execute jobs
            SimManager.runJobSet(jobs);
            
            // Alternatively, run jobs in the job list file 
            // SimManager.runJobSet(EPlusBatch.JobStringType.FILE, "example_3-RVX_v1.6_E+v8.3/jobs2.txt");

            // wait for jobs to finish
            try {
            do {
                Thread.sleep(2000);
            }while (SimManager.isSimulationRunning());
            }catch (InterruptedException iex) {
            SimManager.getAgent().setStopAgent(true);
            }

            // collect simulation results
            HashMap<String, ArrayList<ArrayList<double []>>> Results = SimManager.getSimulationResults(
              SimManager.getAgent().getResultCollectors(),
              SimManager.getResolvedEnv().getParentDir(),
              Project.getRvx(),
              null
            );

            int n = Results.size();
            // ... ...

        }else {
          logger.info(SimManager.getBatchInfo().getValidationErrorsText());
        }
    }
}
