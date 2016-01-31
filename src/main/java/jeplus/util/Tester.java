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
import java.util.logging.Level;
import java.util.logging.Logger;
import jeplus.EPlusWinTools;
import jeplus.JEPlusConfig;
import jeplus.JEPlusProject;

/**
 *
 * @author Yi
 */
public class Tester {
    public static void main (String [] args) throws IOException {
        
        final JEPlusConfig config = JEPlusConfig.getNewInstance("D:\\4\\jEPlus_v1.5.2\\jeplus_v72.cfg");
        final String workdir = "D:\\4\\jEPlus_v1.5.2\\std_job_v72";
        final ProcessWrapper wrapper = new ProcessWrapper ();
        new Thread (new Runnable () {
            @Override
            public void run () {
                EPlusWinTools.runEPlus(config, workdir, false, wrapper);
            }
        }).start();
        System.out.println("E+ started...");
        while (true) {
            if (wrapper.getWrappedProc() != null) {
                System.out.println("Got the process, waiting 10s before killing it...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
                }
                wrapper.getWrappedProc().destroy();
                System.out.println("Process killed.");
                break;
            }
            System.out.println("Waiting for process to start...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Done");
        System.exit(0);
        
        JEPlusConfig.getDefaultInstance().saveAsJSON (new File("D:\\4\\jEPlus_v1.5.2\\test_config.json"));
        System.exit(0);
        
        JEPlusProject proj = JEPlusProject.loadAsXML(new File("D:\\4\\jEPlus_v1.5.2\\example_2-rvx_E+v8.1\\project.jep"));
        proj.saveAsJSON(new File("D:\\4\\jEPlus_v1.5.2\\test_project.json"));
        
        proj = JEPlusProject.loadFromJSON(new File("D:\\4\\jEPlus_v1.5.2\\test_project.json"));
        proj.saveAsXML(new File("D:\\4\\jEPlus_v1.5.2\\test_project.jep"));
    }
}
