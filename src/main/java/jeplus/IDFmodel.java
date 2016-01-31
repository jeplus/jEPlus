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
package jeplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;

/**
 * This class is used to process IDF/IMF files, including get version info,
 * check include files, check search strings, and check output variables
 * @author zyyz
 */
public class IDFmodel {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(IDFmodel.class);

    /**
     * Read E+ version information from the IDF file
     * @param IDFTemplate The IDF file whose version is to be extracted
     * @return Version string, e.g. "4.0"
     */
    public static String getEPlusVersionInIDF(String IDFTemplate) {
        // Locate "Version,xx.xx;" in the idf/imf file
        try (BufferedReader ins = new BufferedReader(new FileReader(IDFTemplate))) {
            String line = ins.readLine();
            while (line != null) {
                line = (line.contains("!")) ? line.substring(0, line.indexOf("!")).trim() : line.trim();
                if (line.startsWith("Version,")) {
                    String verline = line;
                    while (! line.contains(";")) {
                        line = ins.readLine();
                        if (line != null) {
                            verline = verline.concat((line.contains("!")) ? line.substring(0, line.indexOf("!")).trim() : line.trim());
                        }else {
                            break;
                        }
                    }
                    ins.close();
                    if (verline.contains(";")) {
                        return verline.substring(8, verline.indexOf(";")).trim();
                    }
                }
                line = ins.readLine();
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return "Unknown";

    }

    /**
     * Test if the input template file is an EP-Macro input file (*.imf)
     * @return True if the file name ends with ".imf"
     */
    public static boolean isIMF (String idf) {
        return (idf.toLowerCase().endsWith(EPlusConfig.getEPlusIMFExt()));
    }

    /**
     * Get Base directory of the include files
     * @param idf Input IMF file
     * @return the base directory
     */
    public static String getIncludeFilePrefix(String idf) {
        // Locate '##fileprefix ' in the imf file
        if (isIMF(idf)) {
            try (BufferedReader ins = new BufferedReader(new FileReader(idf))) {
                String line = ins.readLine();
                while (line != null) {
                    line = line.trim();
                    if (line.startsWith("##fileprefix ")) {
                        ins.close();
                        return line.substring(13).trim();
                    }
                    line = ins.readLine();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return null;
    }

    /**
     * Write base directory to imf file
     * @param imfin the input imf file to copy from
     * @param imfout the output imf file to write to
     * @param dir the base directory to be put in ##fileprefix
     */
    public static void setIncludeFilePrefix (String imfin, String imfout, String dir) {
        // Locate '##fileprefix ' in the imf file
        if (isIMF(imfin)) {
            try (PrintWriter outs = new PrintWriter (new FileWriter(imfout));
                 BufferedReader ins = new BufferedReader(new FileReader(imfin))) {
                
                outs.println("##fileprefix " + dir + (dir.endsWith("/") ? "" : "/"));
                
                String line = ins.readLine();
                while (line != null) {
                    line = line.trim();
                    if (! line.startsWith("##fileprefix ")) {
                        outs.println(line);
                    }
                    line = ins.readLine();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    /**
     * Get a list of include files in this imf
     * @param imfin
     * @return A list of include files
     */
    public static ArrayList<String> getIncludeFiles(String imfin) {
        ArrayList<String> list = null;
        // Locate '##include ' in the imf file
        if (isIMF(imfin)) {
            list = new ArrayList<> ();
            try (BufferedReader ins = new BufferedReader(new FileReader(imfin))) {
                String line = ins.readLine();
                while (line != null) {
                    line = line.trim();
                    if (line.startsWith("##include ")) {
                        list.add(line.substring(10).trim());
                    }
                    line = ins.readLine();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return list;
    }

    public static boolean replaceScheduleFiles (String idfin, ArrayList<String> schdfiles) {
        boolean ok = true;
        File ori = new File (idfin);
        File temp = new File (idfin + ".temp");
        int counter = 0;
        try (PrintWriter fw = new PrintWriter (new FileWriter (temp));
            BufferedReader ins = new BufferedReader(new FileReader(ori))) {

            String line = ins.readLine();
            while (line != null) {
                if (counter < schdfiles.size()) {
                    String thisline = (line.contains("!")) ? line.substring(0, line.indexOf("!")).trim() : line.trim();
                    if (thisline.contains(schdfiles.get(counter))) {
                        String fn = new File (schdfiles.get(counter)).getName();
                        line = line.replace(schdfiles.get(counter), fn);
                        counter ++;
                    }
                }
                fw.println(line);
                line = ins.readLine();
            }
            ori.delete();
            temp.renameTo(ori);
        } catch (Exception e) {
            logger.error("", e);
            ok = false;
        }
        return ok && counter == schdfiles.size();
    }
    
    /**
     * Get a list of schedule files (as in Schedule:file objects) in this idf model
     * @param idfin
     * @return A list of schedule files
     */
    public static ArrayList<String> getScheduleFiles(String idfin) {
        ArrayList<String> list = new ArrayList<> ();
        try (BufferedReader ins = new BufferedReader(new FileReader(idfin))) {
            String line = ins.readLine();
            while (line != null) {
                line = (line.contains("!")) ? line.substring(0, line.indexOf("!")).trim() : line.trim();
                if (line.toLowerCase().startsWith("schedule:file,")) {
                    String verline = line;
                    while (! line.contains(";")) {
                        line = ins.readLine();
                        if (line != null) {
                            verline = verline.concat((line.contains("!")) ? line.substring(0, line.indexOf("!")).trim() : line.trim());
                        }else {
                            break;
                        }
                    }
                    if (verline.contains(";")) { // a complete Schedule:File object
                        String [] parts = verline.split("\\s*,\\s*");
                        //Definition in V7.2 IDD
                        //Schedule:File,
                        // \min-fields 5
                        //       \memo A Schedule:File points to a text computer file that has 8760-8784 hours of data.
                        //  A1 , \field Name
                        //       \required-field
                        //       \type alpha
                        //       \reference ScheduleNames
                        //  A2 , \field Schedule Type Limits Name
                        //       \type object-list
                        //       \object-list ScheduleTypeLimitsNames
                        //  A3 , \field File Name
                        //       \required-field
                        //       \retaincase
                        //  N1 , \field Column Number
                        //       \required-field
                        //       \type integer
                        //       \minimum 1
                        //  N2 , \field Rows to Skip at Top
                        //       \required-field
                        //       \type integer
                        //       \minimum 0
                        //  N3 , \field Number of Hours of Data
                        //       \note 8760 hours does not account for leap years, 8784 does.
                        //       \note should be either 8760 or 8784
                        //       \default 8760
                        //       \minimum 8760
                        //       \maximum 8784
                        //  A4 ; \field Column Separator
                        //       \type choice
                        //       \key Comma
                        //       \key Tab
                        //       \key Fixed
                        //       \key Semicolon
                        //       \default Comma
                        list.add(parts[3]);
                    }
                }
                line = ins.readLine();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return list;
    }

    /**
     * Collect search strings in the idf file
     * @param idf the idf file
     * @return A list of search strings
     */
    public static ArrayList <String> getSearchStrings(String idf) {
        ArrayList <String> list = new ArrayList <> ();
        try (BufferedReader ins = new BufferedReader(new FileReader(idf))) {
            String line = ins.readLine();
            while (line != null) {
                line = line.trim();
                while (line.contains("@@")) {
                    int a = line.indexOf("@@");
                    int b = line.indexOf("@@", a+2) + 2;
                    list.add(line.substring(a, b));
                    line = line.substring(b);
                }
                line = ins.readLine();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return list;
    }

    public static void main (String[] args) {
        String imf = "C:/JEPlusPlusDist/JEPPServer/test_building1.imf";
        File imffile = new File (imf);
        System.out.println(imffile.getName() + " in directory " + imffile.getParent());
        System.out.println("Search Strings:");
        ArrayList<String> list = IDFmodel.getSearchStrings (imf);
        for (String tag : list) {
            System.out.println("  " + tag);
        }
        System.out.println("Include files:");
        list = IDFmodel.getIncludeFiles (imf);
        for (String tag : list) {
            System.out.println("  " + tag);
        }
        System.out.println("Search Strings:");
        System.out.println("  " + IDFmodel.getEPlusVersionInIDF(imf));
        System.out.println("Base directory:");
        System.out.println("  " + IDFmodel.getIncludeFilePrefix(imf));
    }
}
