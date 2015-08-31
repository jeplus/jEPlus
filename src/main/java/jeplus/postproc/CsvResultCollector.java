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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import jeplus.EPlusBatch;
import jeplus.data.RVX;
import jeplus.data.RVX_CSVitem;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class CsvResultCollector extends ResultCollector {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(CsvResultCollector.class);
    
    public CsvResultCollector (String Desc) {
        super (Desc);
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
            // RVX rvx = RVX.getRVX(JobOwner.getResolvedEnv().getRVIDir() + JobOwner.getResolvedEnv().getRVIFile());
            RVX rvx = JobOwner.getProject().getRvx();
            if (rvx != null && rvx.getCSVs() != null) {
                for (RVX_CSVitem item : rvx.getCSVs()) {
                    String fn = item.getTableName() + ".csv";
                    ResultFiles.add(fn);
                    ResWriter = new DefaultCSVWriter(null, fn);
                    ResReader = new EPlusCsvReader (item);
                    ResultHeader = new HashMap <>();
                    ResultTable = new ArrayList <> ();
                    ResReader.readResult(JobOwner, JobOwner.getResolvedEnv().getParentDir(), ResultHeader, ResultTable);
                    if (PostProc != null) {PostProc.postProcess(ResultHeader, ResultTable);}
                    ResWriter.writeResult(JobOwner, ResultHeader, ResultTable);
                    ResCollected += ResultTable.size();
                }
            }
        }catch (Exception ex) {
            logger.error("Error reading RVX file " + JobOwner.getResolvedEnv().getRVIDir() + JobOwner.getResolvedEnv().getRVIFile(), ex);
        }
        return ResCollected;
    }
    
    @Override
    public ArrayList<String> getExpectedResultFiles(RVX rvx) {
        ArrayList<String> list = new ArrayList<> ();
        if (rvx != null && rvx.getCSVs() != null) {
            for (RVX_CSVitem item : rvx.getCSVs()) {
                list.add(item.getTableName() + ".csv");
            }
        }
        return list;
    }
    
    
}
