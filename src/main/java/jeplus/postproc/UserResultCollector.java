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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import jeplus.EPlusBatch;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class UserResultCollector extends ResultCollector {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(UserResultCollector.class);
    
    protected static final String SectionStart = "!-user spreadsheet";
    protected static final String SectionEnd = "!-end user spreadsheet";
    
    public UserResultCollector (String Desc) {
        super (Desc);
    }

    /**
     * Parse result table list from RVI file and store the list in <code> ResultFiles </code>
     * @param JobOwner 
     * @return List of result specifications
     */
    @Override
    public ArrayList<String[]> listResultFilesFromRVI (String rvifile) {
        // get extra output specifications from rvi in the project
        // The part starts with "!-User Spreadsheet" and ends with "!-End User Spreadsheet" (case insensitive)
        // Contents of the section contain ';' delimited triple-segment rows, e.g.
        // Table name; Spreadsheet file name; Column numbers to extract (0-based, e.g. 3,5,6,7,9)
        ArrayList<String[]> sections;
        try (BufferedReader fr = new BufferedReader (new FileReader (rvifile))) {
            String line = fr.readLine();
            boolean extra_on = false;
            sections = new ArrayList<> ();
            while (line != null) {
                if (extra_on) {
                    if (line.toLowerCase().startsWith(SectionEnd)) {
                        extra_on = false;
                    }else {
                        line = line.substring(0, line.contains("!") ? line.indexOf("!") : line.length());
                        if (line.trim().length() > 0) {
                            String [] section = line.split(";");
                            sections.add(section);
                        }
                    }
                }
                if (line.trim().toLowerCase().startsWith(SectionStart)) {
                    extra_on = true;
                }
                line = fr.readLine();
            }
            ResultFiles.clear();
            for (int i=0; i<sections.size(); i++) {
                String fn = sections.get(i)[0] + ".csv";
                ResultFiles.add(fn);
            }
            return sections;
        }catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    @Override
    public int collectResutls (EPlusBatch JobOwner, boolean onthefly) {
        if (onthefly) {
            // Method not implemented
            throw new UnsupportedOperationException("Not supported yet.");
        }else {
            ArrayList<String[]> sections = listResultFilesFromRVI (JobOwner.getResolvedEnv().getRVIDir() + JobOwner.getResolvedEnv().getRVIFile());
            if (sections != null) {
                int ResCollected = 0;
                for (int i=0; i<ResultFiles.size(); i++) {
                    ResWriter = new DefaultCSVWriter(null, ResultFiles.get(i));
                    ResReader = new EPlusUserSpreadsheetReader(sections.get(i)[1], sections.get(i)[2]);
                    ResultHeader = new HashMap <>();
                    ResultTable = new ArrayList <> ();
                    ResReader.readResult(JobOwner, JobOwner.getResolvedEnv().getParentDir(), ResultHeader, ResultTable);
                    if (PostProc != null) {PostProc.postProcess(ResultHeader, ResultTable);}
                    ResWriter.writeResult(JobOwner, ResultHeader, ResultTable);
                    ResCollected += ResultTable.size();
                }
                return ResCollected;
            }
        }
        return -1;
    }
    
    
}
