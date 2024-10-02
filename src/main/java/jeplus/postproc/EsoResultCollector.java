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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import jeplus.EPlusBatch;
import jeplus.EPlusConfig;
import jeplus.JEPlusConfig;
import jeplus.data.RVX_RVIitem;
import jeplus.data.RVX;
import jeplus.data.RVX_ESOitem;
import jeplus.data.RVX_MTRitem;
import jeplus.util.RelativeDirUtil;
import org.slf4j.LoggerFactory;

/**
 * This collector calls ReadVars to extract results from ESO output. It works only with the new RVX file.
 * @author Yi
 */
public class EsoResultCollector extends ResultCollector {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EsoResultCollector.class);
    
    protected EPlusConfig Config = null;
    
    /**
     * Empty constructor. Actual assignment of readers and writers are done in the <code>collectResutls()</code> function
     * @param Desc Description of this collector
     * @param execsvc
     */
    public EsoResultCollector (String Desc, ExecutorService execsvc) {
        super (Desc);
        ExecService = execsvc;

        this.RepReader = null;
        this.RepWriter = null;
        this.ResReader = null;
        this.ResWriter = null;
        this.IdxWriter = null;
    }

    /**
     * Empty constructor with assigned EPlusConfig object. Actual assignment of readers and writers are done in the <code>collectResutls()</code> function
     * @param Desc Description of this collector
     * @param config Assigned EPlusConfig object to be used by this collector
     * @param execsvc ExecutorService instance to run parallel processing
     */
    public EsoResultCollector (String Desc, EPlusConfig config, ExecutorService execsvc) {
        super (Desc);
        Config = config;
        ExecService = execsvc;
        this.RepReader = null;
        this.RepWriter = null;
        this.ResReader = null;
        this.ResWriter = null;
        this.IdxWriter = null;
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
        if (rvx != null) {
            List<RVX_RVIitem> rvis = new ArrayList<>();
            if (rvx.getRVIs() != null) {
                rvis.addAll(rvx.getRVIs());
            }
            if (rvx.getESOs() != null) {
                for (RVX_ESOitem item : rvx.getESOs()) {
                    rvis.add(
                            new RVX_RVIitem(item.getTableName() + ".rvi", 
                                    item.getFrequency(), 
                                    item.getTableName(), 
                                    item.isUsedInCalc())
                    );
                }
            }
            if (rvx.getMTRs() != null) {
                for (RVX_MTRitem item : rvx.getMTRs()) {
                    rvis.add(
                            new RVX_RVIitem(item.getTableName() + ".mvi", 
                                    item.getFrequency(), 
                                    item.getTableName(), 
                                    item.isUsedInCalc())
                    );
                }
            }
            
            for (RVX_RVIitem item : rvis) {
                String fn = item.getTableName() + ".csv";
                ResultFiles.add(fn);
                ResWriter = new DefaultCSVWriter(null, fn);
                if (ExecService != null) {
                    ResReader = new EPlusRVIReader2(
                            Config == null ? JEPlusConfig.getDefaultInstance().findMatchingEPlusConfig(JobOwner.getProject().getEPlusModelVersion()) : Config,
                            RelativeDirUtil.checkAbsolutePath(item.getFileName(), JobOwner.getProject().getBaseDir()), 
                            item.getFrequency(), 
                            fn, 
                            item.isUsedInCalc(),
                            ExecService
                    );
                    
                }else {
                    ResReader = new EPlusRVIReader(
                            Config == null ? JEPlusConfig.getDefaultInstance().findMatchingEPlusConfig(JobOwner.getProject().getEPlusModelVersion()) : Config,
                            RelativeDirUtil.checkAbsolutePath(item.getFileName(), JobOwner.getProject().getBaseDir()), 
                            item.getFrequency(), 
                            fn, 
                            item.isUsedInCalc()
                    );
                }
                ResultHeader = new HashMap <>();
                ResultTable = new ArrayList <> ();
                try {
                    ResReader.readResult(JobOwner, JobOwner.getResolvedEnv().getParentDir(), ResultHeader, ResultTable);
                    if (PostProc != null) {PostProc.postProcess(ResultHeader, ResultTable);}
                    ResWriter.writeResult(JobOwner, ResultHeader, ResultTable);
                }catch (Exception ex) {
                    logger.error("Error reading RVI/MVI file " + item.getFileName(), ex);
                }
                ResCollected += ResultTable.size();
            }
        }
        return ResCollected;
    }

    @Override
    public ArrayList<String> getExpectedResultFiles(RVX rvx) {
        ArrayList<String> list = new ArrayList<> ();
        if (rvx != null) {
            if (rvx.getRVIs() != null) {
                for (RVX_RVIitem item : rvx.getRVIs()) {
                    if (item.isUsedInCalc()) list.add(item.getTableName() + ".csv");
                }
            }
            if (rvx.getESOs() != null) {
                for (RVX_ESOitem item : rvx.getESOs()) {
                    if (item.isUsedInCalc()) list.add(item.getTableName() + ".csv");
                }
            }
            if (rvx.getMTRs() != null) {
                for (RVX_MTRitem item : rvx.getMTRs()) {
                    if (item.isUsedInCalc()) list.add(item.getTableName() + ".csv");
                }
            }
        }
        return list;
    }
    
    
}
