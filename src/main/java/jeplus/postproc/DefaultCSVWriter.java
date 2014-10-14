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

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zyyz
 */
public class DefaultCSVWriter implements IFReportWriter, IFResultWriter {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(DefaultCSVWriter.class);
    
    protected String ReportCSV = null;
    protected String ResultCSV = null;
    transient String LastRepFile = "";
    transient String LastResFile = "";
    
    public DefaultCSVWriter (String reportfile, String resultfile) {
        ReportCSV = reportfile;
        ResultCSV = resultfile;
    }

    @Override
    public String getReportFileName() {
        return ReportCSV;
    }

    public void setReportFileName(String ReportCSV) {
        this.ReportCSV = ReportCSV;
    }

    @Override
    public String getResultFileName() {
        return ResultCSV;
    }

    public void setResultFileName(String ResultCSV) {
        this.ResultCSV = ResultCSV;
    }

    
    @Override
    public void writeResult(EPlusBatch manager, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        try (PrintWriter fw = new PrintWriter (new FileWriter (manager.getResolvedEnv().getParentDir() + ResultCSV))) {
            TreeMap <Integer, String> InvMap = new TreeMap <> ();
            for (String key: header.keySet()) {
                InvMap.put(header.get(key), key);
            }
            String [] Headers = InvMap.values().toArray(new String [0]);
            for (int i=0; i<Headers.length; i++) {
                if (i>0) fw.print(", ");
                fw.print(Headers[i]);
            }
            fw.println();
            // write data
            for (int i=0; i<table.size(); i++) {
                ArrayList<String> row = table.get(i);
                for (int j=0; j<row.size(); j++) {
                    if (j > 0) fw.print(", ");
                    fw.print(row.get(j));
                }
                fw.println();
            }
            fw.flush();
            LastResFile = manager.getResolvedEnv().getParentDir() + ResultCSV;
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    @Override
    public void writeReport(EPlusBatch manager, ArrayList<String> header, ArrayList<ArrayList<String>> table) {
        try (PrintWriter fw = new PrintWriter (new FileWriter (manager.getResolvedEnv().getParentDir() + ReportCSV))) {
            for (int i=0; i<header.size(); i++) {
                if (i>0) fw.print(", ");
                fw.print(header.get(i));
            }
            fw.println();
            // write data
            for (int i=0; i<table.size(); i++) {
                ArrayList<String> row = table.get(i);
                for (int j=0; j<row.size(); j++) {
                    if (j > 0) fw.print(", ");
                    fw.print(row.get(j));
                }
                fw.println();
            }
            fw.flush();
            LastRepFile = manager.getResolvedEnv().getParentDir() + ReportCSV;
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    @Override
    public String getReportFile() {
        return this.LastRepFile;
    }

    @Override
    public String getResultFile() {
        return this.LastResFile;
    }

    @Override
    public JPanel getOptionPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
