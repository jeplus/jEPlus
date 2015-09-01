/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@googlemail.com>               *
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
 *  - Created  2011-10-01                                                  *
 *                                                                         *
 ***************************************************************************/
package jeplus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;

/**
 * Utilities for simple descriptive stats from csv files
 * @author yzhang
 */
public class CsvUtil {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(CsvUtil.class);

    static int TableWidth = 0;
    static int TableHeight = 0;
    static int TextColumns = 2; // job_id, date/time, data ....
    static int N = 0;
    static String[][] StatMean = null;
    static double[][] Mean = null;
    static double[][] M2 = null;
    static String[][] StatVariance = null;
    static double[][] Variance = null; // this is population variance (sum of error squared / N)
    static String[][] StatMin = null;
    static double[][] Min = null;
    static String[][] StatMax = null;
    static double[][] Max = null;

    /**
     * Reset all stats
     */
    public static void reset() {
        TableWidth = 0;
        TableHeight = 0;
        TextColumns = 2; // job_id, date/time, data ....
        N = 0;
        StatMean = null;
        Mean = null;
        M2 = null;
        StatVariance = null;
        Variance = null; // this is population variance (sum of error squared / N)
        StatMin = null;
        Min = null;
        StatMax = null;
        Max = null;
    }

    /**
     * Parse a csv file 
     * @param file Input file
     * @return String array contains the table
     */
    public static String[][] parseCSV(File file) {
        ArrayList<String[]> buf = new ArrayList<>();
        try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
            String line = fr.readLine();
            while (line != null) {
                buf.add(line.split(","));
                line = fr.readLine();
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return buf.toArray(new String[0][]);
    }

    /**
     * Parse a special csv file with '#'-marked comments
     * @param file Input file
     * @return String array contains the table
     */
    public static String[][] parseCSVwithComments(File file) {
        ArrayList<String[]> buf = new ArrayList<>();
        try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
            String line = readNextLineOfContents(fr);
            while (line != null) {
                // buf.add(line.split("\\s*,\\s*"));
                buf.add(line.split("\\s*,\\s*(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\(\\[\\{]*[\\}\\]\\)]))"));
                line = readNextLineOfContents(fr);
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return buf.toArray(new String[0][]);
    }

    public static String stripEnclosingQuotes (String text) {
        return text.replaceAll("(^[\"\'])|([\"\']$)", "");
    }
    
    /**
     * Parse input text as CSV
     * @param text input text
     * @return String array containing table
     */
    public static String[][] parseCSV(String text) {
        ArrayList<String[]> buf = new ArrayList<>();
        try (BufferedReader fr = new BufferedReader(new StringReader(text))) {
            String line = fr.readLine();
            while (line != null) {
                buf.add(line.split(","));
                line = fr.readLine();
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return buf.toArray(new String[0][]);
    }

    /**
     * Write the given table into a CSV string
     * @param table 
     * @return Text in CSV format
     */
    public static String writeCSV(String[][] table) {
        if (table == null || table.length == 0 || table[0] == null || table[0].length == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                buf.append(table[i][j]).append(",");
            }
            buf.append("\n");
        }
        return buf.toString();
    }
    
    /**
     * Write the given table into a CSV string
     * @param table 
     * @return Text in CSV format
     */
    public static String writeTRNSYSCSV(String[][] table) {
        if (table == null || table.length == 0 || table[0] == null || table[0].length == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < table.length; i++) {
            for (int j = 2; j < table[0].length; j++) {
                buf.append(table[i][j]).append(",");
            }
            buf.append("\n");
        }
        return buf.toString();
    }

    public static void addData(String[][] table) {
        if (table == null || table.length == 0 || table[0] == null || table[0].length == 0) {
            return;
        }
        // Table's width and height are only set in the first call to this method
        if (TableWidth == 0) {
            TableWidth = table[0].length;
        }
        if (TableHeight == 0) {
            TableHeight = table.length;
        }
        if (Mean == null) {
            Mean = new double[TableHeight][TableWidth];
        }
        if (Variance == null) {
            Variance = new double[TableHeight][TableWidth];
        }
        if (Min == null) {
            Min = new double[TableHeight][TableWidth];
        }
        if (Max == null) {
            Max = new double[TableHeight][TableWidth];
        }
        if (M2 == null) {
            M2 = new double[TableHeight][TableWidth];
        }

        if (StatMean == null) {
            StatMean = new String[TableHeight][TableWidth];
        }
        if (StatVariance == null) {
            StatVariance = new String[TableHeight][TableWidth];
        }
        if (StatMin == null) {
            StatMin = new String[TableHeight][TableWidth];
        }
        if (StatMax == null) {
            StatMax = new String[TableHeight][TableWidth];
        }
        if (N == 0) {
            for (int i = 0; i < TableHeight; i++) {
                StatMean[i][0] = "mean";
                StatMin[i][0] = "minimum";
                StatMax[i][0] = "maximum";
                StatVariance[i][0] = "variance (N)";
                for (int j = 1; j < TextColumns; j++) {
                    StatMean[i][j] = table[i][j];
                    StatMin[i][j] = table[i][j];
                    StatMax[i][j] = table[i][j];
                    StatVariance[i][j] = table[i][j];
                }
            }
        }

        N++;
        double x;
        for (int i = 0; i < TableHeight; i++) {
            for (int j = TextColumns; j < TableWidth; j++) {
                try {
                    x = Double.parseDouble(table[i][j]);
                } catch (Exception ex) {
                    // Assign large value if data is missing
                    x = Double.MAX_VALUE;
                }
                if (N == 1) {
                    Mean[i][j] = x;
                    Min[i][j] = x;
                    Max[i][j] = x;
                    Variance[i][j] = 0;
                    M2[i][j] = 0;
                } else {
                    double delta = x - Mean[i][j];
                    Mean[i][j] += delta / N;
                    M2[i][j] += delta * (x - Mean[i][j]);
                    Variance[i][j] = M2[i][j] / N;
                    Min[i][j] = Math.min(x, Min[i][j]);
                    Max[i][j] = Math.max(x, Max[i][j]);
                }
            }
        }
    }

    public static String[][] getStatMax() {
        for (int i = 0; i < TableHeight; i++) {
            for (int j = TextColumns; j < TableWidth; j++) {
                StatMax[i][j] = Double.toString(Max[i][j]);
            }
        }
        return StatMax;
    }

    public static String[][] getStatMean() {
        for (int i = 0; i < TableHeight; i++) {
            for (int j = TextColumns; j < TableWidth; j++) {
                StatMean[i][j] = Double.toString(Mean[i][j]);
            }
        }
        return StatMean;
    }

    public static String[][] getStatMin() {
        for (int i = 0; i < TableHeight; i++) {
            for (int j = TextColumns; j < TableWidth; j++) {
                StatMin[i][j] = Double.toString(Min[i][j]);
            }
        }
        return StatMin;
    }

    public static String[][] getStatVariance() {
        for (int i = 0; i < TableHeight; i++) {
            for (int j = TextColumns; j < TableWidth; j++) {
                StatVariance[i][j] = Double.toString(Variance[i][j]);
            }
        }
        return StatVariance;
    }

    public static int getN() {
        return N;
    }

    /**
     * Read next data line. All preceeding comment lines are filtered out.
     * @param rf BufferedReader the reader
     * @return String Return the next trimmed data line, or "null" if EOF reached
     */
    public static String readNextLineOfContents(BufferedReader rf) throws IOException {
        String ln = null;
        do {
            ln = rf.readLine();
            if (ln != null) {
                int sharp = ln.indexOf("#");
                if (sharp < 0) {
                    sharp = ln.length();
                }
                ln = ln.substring(0, sharp).trim();
            } else {
                break;
            }
        } while (ln.length() == 0);
        return ln;
    }

    /**
     * Read next data line. All preceeding comment lines (marked by the given comment character) are filtered out.
     * @param rf BufferedReader the reader
     * @return String Return the next trimmed data line, or "null" if EOF reached
     */
    public static String readNextLineOfContents(BufferedReader rf, String commentchar) throws IOException {
        String ln = null;
        do {
            ln = rf.readLine();
            if (ln != null) {
                int sharp = ln.indexOf(commentchar);
                if (sharp < 0) {
                    sharp = ln.length();
                }
                ln = ln.substring(0, sharp).trim();
            } else {
                break;
            }
        } while (ln.length() == 0);
        return ln;
    }
    
    /**
     * 
     * @param input
     * @return 
     */
    public static boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }    

    /**
     * 
     * @param input
     * @return 
     */
    public static boolean isFloat( String input ) {
        try {
            Double.parseDouble(input );
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }    
}
