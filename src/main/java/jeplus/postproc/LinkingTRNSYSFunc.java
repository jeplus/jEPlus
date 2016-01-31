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

import java.io.*;
import org.slf4j.LoggerFactory;

/**
 * PostProcessing function for linking E+ output to TRNSYS
 * @author yzhang
 */
public class LinkingTRNSYSFunc extends PostProcessFunc {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(LinkingTRNSYSFunc.class);
    
    public static final String LinkFileName = "LinkingTRNSYS.lst";
    
    /**
     * This function simply remove the first row (the header) of the input csv,
     * and pass the rest on in a string
     * @param jobid
     * @param csvfile
     * @return File contents excluding the header in a string
     */
    public void processJobResult(String Basedir, String Exportdir, String JobID) {
        
        try (FileWriter out = new FileWriter(Basedir + LinkFileName, true)) {
            out.write("\n" + Exportdir + File.separator + JobID + ".csv");
            out.flush();
        } catch (Exception ex) {
            logger.error("", ex);
            new File(Basedir + LinkFileName).delete();
            new File(Exportdir).delete();  
        }            
    }   

    @Override
    public String processJobResult(String jobid, String csvfile) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
