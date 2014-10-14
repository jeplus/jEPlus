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

import java.io.*;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yzhang
 */
public class DefaultPostProcFunc extends PostProcessFunc {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(DefaultPostProcFunc.class);
    /**
     * This function simply remove the first row (the header) of the input csv,
     * and pass the rest on in a string
     * @param jobid
     * @param csvfile
     * @return File contents excluding the header in a string
     */
    @Override
    public String processJobResult(String jobid, String csvfile) {
        StringBuilder buf = new StringBuilder();
        try (BufferedReader fr = new BufferedReader(new FileReader(csvfile))) {
            fr.readLine();
            // First line of data
            String lr = fr.readLine();
            while (lr != null) {
                buf.append(jobid).append(",").append(lr).append("\n");
                lr = fr.readLine();
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return buf.toString();
    }

    /**
     * This function simply remove or not the first row (the header) of the 
     * input csv, and pass the rest on in a string
     * @param jobid
     * @param csvfile
     * @param keepheader
     * @return File contents excluding the header in a string
     */
    public String processJobResult(String jobid, String csvfile, int keepheader) {
        StringBuilder buf = new StringBuilder();
        try (BufferedReader fr = new BufferedReader(new FileReader(csvfile))) {
            if (keepheader != 1){
                fr.readLine();
            }
            // First line of data
            String lr = fr.readLine();
            while (lr != null) {
                buf.append(jobid).append(",").append(lr).append("\n");
                lr = fr.readLine();
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return buf.toString();
    }
}
