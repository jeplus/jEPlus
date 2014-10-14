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

/**
 * Super class of customised post processing functions. Use sub-class'es processJobResult()
 * function.
 * @author zyyz
 */
public abstract class PostProcessFunc {

    public static final String [] PosProcFunc = { "DefaultPostProcFunction" ,
                                                  "LinkingTRNSYSFunction"
                                                };
    public static final String [] PosProcFuncDescription = { "Default post-process function does nothing exception copies outputs from reader to writer." ,
                                                             "LinkingTRNSYS post-process function copies outputs in a folder to be easily read as inputs in TRNSYS projects."
                                                           };
    
    /** Get Name of Post-Proccess Function */
    public static String [] getPosProcFunc() {
        return PosProcFunc;   
    }

    /** Get Description of Post-Proccess Function */
    public static String [] getPosProcFuncDescription() {
        return PosProcFuncDescription;   
    }
    
    /**
     * Process the csv file and return results in a string. The results can be
     * identified by the job_id if included.
     * @param jobid The job_id can be used to identify the current csv file
     * @param csvfile The csv file to processJobResult
     * @return Results in a string.
     */
    public abstract String processJobResult(String jobid, String csvfile);
    
    

}
