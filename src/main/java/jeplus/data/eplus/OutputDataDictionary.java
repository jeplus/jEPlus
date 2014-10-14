/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2013  Yi Zhang <yizhanguk@gmail.com>                    *
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
package jeplus.data.eplus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * EnergyPlus output data dictionary for representing information in .mdd and .rdd files
 * @author Yi
 */
public class OutputDataDictionary {
    public enum Source {
        RDD,
        MDD
    }
    
    protected Source DictSource = Source.RDD;
    protected String OutputDataFile = "eplusout.eso";// or "eplusout.mtr"
    protected String EPlusVersionInfo = null;
    protected ArrayList <OutputVariable> Variables = null;
    
    public OutputDataDictionary (String fn) {
        if (fn.toLowerCase().endsWith(".rdd")) {
            DictSource = Source.RDD;
            OutputDataFile = "eplusout.eso";
        }else if (fn.toLowerCase().endsWith(".mdd")) {
            DictSource = Source.MDD;
            OutputDataFile = "eplusout.mtr";
        }
        readDictFile (fn);
    }
    
    protected final void readDictFile (String fn) {
        BufferedReader fr = null;
        try {
            fr = new BufferedReader (new FileReader (fn));
            String line = fr.readLine();
            String [] parts;
            if (line != null && line.startsWith("Program Version")) {
                EPlusVersionInfo = line.trim();
            }else {
                System.err.println("Error in " + fn + ": first line of text should contain E+ version information.");
                fr.close();
                return;
            }
            fr.readLine(); // headers
            Variables = new ArrayList <> ();
            line = fr.readLine();
            while (line != null) {
                OutputVariable var = OutputVariable.getOutputVariable(line);
                if (var != null) {
                    Variables.add(var);
                }
                line = fr.readLine();
            }
            fr.close();
        }catch (Exception ex) {
            // ex...
        }finally {
            if (fr != null) {
                
            }
        }
    }

    public Source getDictSource() {
        return DictSource;
    }

    public void setDictSource(Source DictSource) {
        this.DictSource = DictSource;
    }

    public String getOutputDataFile() {
        return OutputDataFile;
    }

    public void setOutputDataFile(String OutputDataFile) {
        this.OutputDataFile = OutputDataFile;
    }

    public String getEPlusVersionInfo() {
        return EPlusVersionInfo;
    }

    public void setEPlusVersionInfo(String EPlusVersionInfo) {
        this.EPlusVersionInfo = EPlusVersionInfo;
    }

    public ArrayList<OutputVariable> getVariables() {
        return Variables;
    }

    public void setVariables(ArrayList<OutputVariable> Variables) {
        this.Variables = Variables;
    }
    
    public static void main (String [] args) {
        OutputDataDictionary dict = new OutputDataDictionary ("D:\\4\\jEPlus v1.3 build 02\\DBcompetition\\output\\EP_G-T_0-W_0-P1_0-P2_0-P3_0\\eplusout.rdd");
        dict = new OutputDataDictionary ("D:\\\\4\\\\jEPlus v1.3 build 02\\\\DBcompetition\\\\output\\\\EP_G-T_0-W_0-P1_0-P2_0-P3_0\\\\eplusout.mdd");
        
        EsoDataSet eso = new EsoDataSet();
        int nrec = eso.readEsoDataSet("D:\\4\\jEPlus v1.3 build 05\\example_E+v7.2\\output\\EP_0-T_0-W_0-P1_0-P2_0\\eplusout.eso");
        System.out.println("Eso read " + nrec + " lines of data.");
        System.out.println();
        
        eso.writeEsoFile("test.eso");
    }
}
