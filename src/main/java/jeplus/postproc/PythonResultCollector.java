/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@gmail.com>                    *
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
 ***************************************************************************
 *                                                                         *
 * Change log:                                                             *
 *                                                                         *
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus.postproc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
        try {
            RVX rvx = RVX.getRVX(JobOwner.getResolvedEnv().getRVIDir() + JobOwner.getResolvedEnv().getRVIFile());
            if (rvx.getScripts() != null) {
                for (RVX_ScriptItem item : rvx.getScripts()) {
                    String fn = item.getTableName() + ".csv";
                    ResultFiles.add(fn);
                    if (item.isOnEachJob()) {
                        ResWriter = new DefaultCSVWriter(null, fn);
                        ResReader = new EPlusScriptReader(RelativeDirUtil.checkAbsolutePath(item.getFileName(), JobOwner.getResolvedEnv().getRVIDir()), item.getPythonVersion(), item.getArguments(), fn);
                        ResultHeader = new HashMap <>();
                        ResultTable = new ArrayList <> ();
                        ResReader.readResult(JobOwner, JobOwner.getResolvedEnv().getParentDir(), ResultHeader, ResultTable);
                        ResWriter.writeResult(JobOwner, ResultHeader, ResultTable);
                        ResCollected += ResultTable.size();
                    }else {
                        ResWriter = new DefaultCSVWriter(null, null);
                        ResReader = new EPlusScriptReader(null, null, null, null);
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
                        JEPlusConfig config = JEPlusConfig.getDefaultInstance();
                        String workdir = JobOwner.getResolvedEnv().getParentDir();
                        try (PrintStream out = (config.getScreenFile() == null) ? System.err : new PrintStream (new FileOutputStream (workdir + "PyConsole.log", true))) {
                            PythonTools.runPython(
                                    config, 
                                    RelativeDirUtil.checkAbsolutePath(item.getFileName(), JobOwner.getResolvedEnv().getRVIDir()), 
                                    item.getPythonVersion(), 
                                    workdir, 
                                    buf.toString(), 
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
        }catch (IOException ex) {
            logger.error("Error reading RVX file " + JobOwner.getResolvedEnv().getRVIDir() + JobOwner.getResolvedEnv().getRVIFile(), ex);
        }
        return ResCollected;
    }
    
    @Override
    public ArrayList<String> getExpectedResultFiles(RVX rvx) {
        ArrayList<String> list = new ArrayList<> ();
        if (rvx.getScripts() != null) {
            for (RVX_ScriptItem item : rvx.getScripts()) {
                list.add(item.getTableName() + ".csv");
            }
        }
        return list;
    }
        
}
