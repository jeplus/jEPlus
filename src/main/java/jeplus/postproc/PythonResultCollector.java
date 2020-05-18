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
package jeplus.postproc;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jeplus.EPlusBatch;
import jeplus.EPlusTask;
import jeplus.JEPlusConfig;
import jeplus.data.RVX_ScriptItem;
import jeplus.data.RVX;
import jeplus.util.PythonTools;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * @todo not implemented yet
 * @author Yi
 */
public class PythonResultCollector extends ResultCollector {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(PythonResultCollector.class);
    
    public PythonResultCollector (String Desc) {
        super (Desc);
        this.RepReader = null;
        this.RepWriter = null;
        this.ResReader = null;
        this.ResWriter = null;
        this.IdxWriter = null;
        this.PostProc = null;
    }

    @Override
    public int collectResutls (EPlusBatch JobOwner, boolean onthefly) {
        if (onthefly) {
            // Method not implemented
            throw new UnsupportedOperationException("Not supported yet.");
        }
        int ResCollected = 0;
        ResultFiles.clear();
        RVX rvx = JobOwner.getProject().getRvx();
        if (rvx != null && rvx.getScripts() != null) {
            for (RVX_ScriptItem item : rvx.getScripts()) {
                String fn = item.getTableName() + ".csv";
                ResultFiles.add(fn);
                if (item.isOnEachJob()) {
                    ResWriter = new DefaultCSVWriter(null, fn);
                    ResReader = new EPlusScriptReader(
                            RelativeDirUtil.checkAbsolutePath(item.getFileName(), JobOwner.getProject().getBaseDir()), 
                            item.getPythonVersion(), 
                            JobOwner.getProject().getBaseDir(), 
                            item.getArguments(), 
                            fn);
                    ResultHeader = new HashMap <>();
                    ResultTable = new ArrayList <> ();
                    try {
                        ResReader.readResult(JobOwner, JobOwner.getResolvedEnv().getParentDir(), ResultHeader, ResultTable);
                        ResWriter.writeResult(JobOwner, ResultHeader, ResultTable);
                        ResCollected += ResultTable.size();
                    }catch (Exception ex) {
                        logger.error("Error running Python script " + item.getFileName() + " to collect results in each job folder.", ex);
                    }
                }else {
                    ResWriter = new DefaultCSVWriter(null, null);
                    ResReader = new EPlusScriptReader(null, null, null, null, null);
                    StringBuilder buf = new StringBuilder ();
                    try {
                        // Get finished jobs
                        List <EPlusTask> JobQueue = JobOwner.getAgent().getFinishedJobs();
                        // Collect Job List
                        for (EPlusTask job : JobQueue) {
                            buf.append(job.getJobID()).append(";");
                        } // done with loading
                    }catch (NullPointerException npe) {
                    }
                    String workdir = JobOwner.getResolvedEnv().getParentDir();
                    String id_list = "job_ids.lst";
                    if (buf.length() > 8000) {
                        try (PrintWriter fw = new PrintWriter (new FileWriter (workdir + id_list))) {
                            fw.print(buf);
                            fw.close();
                        }catch (IOException ioe) {
                            logger.error("Error writing the list of job_ids to " + workdir + id_list + "!");
                            id_list = "NA";
                        }
                    }else {
                        id_list = buf.toString();
                    }
                    JEPlusConfig config = JEPlusConfig.getDefaultInstance();
                    try (PrintStream out = (config.getScreenFile() == null) ? System.err : new PrintStream (new FileOutputStream (workdir + "PyConsole.log", true))) {
                        PythonTools.runPython(
                                config, 
                                RelativeDirUtil.checkAbsolutePath(item.getFileName(), JobOwner.getProject().getBaseDir()), 
                                item.getPythonVersion(), 
                                JobOwner.getProject().getBaseDir(),
                                workdir, 
                                id_list, 
                                fn, 
                                item.getArguments(), 
                                out
                        );
                        ResCollected ++;
                    }catch (Exception ex) {
                        logger.error("Error when calling Python script " + item.getFileName(), ex);
                    }
                }
            }
        }
        return ResCollected;
    }
    
    @Override
    public ArrayList<String> getExpectedResultFiles(RVX rvx) {
        ArrayList<String> list = new ArrayList<> ();
        if (rvx != null && rvx.getScripts() != null) {
            for (RVX_ScriptItem item : rvx.getScripts()) {
                list.add(item.getTableName() + ".csv");
            }
        }
        return list;
    }
        
}
