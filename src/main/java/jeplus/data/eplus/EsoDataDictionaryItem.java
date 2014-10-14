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

/**
 *
 * @author Yi
 */
public class EsoDataDictionaryItem {
    int ID;
    int NValues;
    String Domain;
    String [] Names;
    String [] Units;
    String Comment;
    
    public EsoDataDictionaryItem () {
        
    }
    
    public static EsoDataDictionaryItem getEsoDataDictionaryItem (String text) {
        EsoDataDictionaryItem item = new EsoDataDictionaryItem();
        if (item.parseText(text)) {
            return item;
        }else {
            return null;
        }
    }
    
    public boolean parseText (String line) {
        if (line != null && line.length() > 7) {    // "1,1,c[]"
            // Comment
            int mark = line.indexOf("!");
            if (mark >= 0) {
                Comment = line.substring(mark + 1).trim();
                line = line.substring(0, mark);
            }else {
                Comment = "";
            }
            // Fields
            String [] parts = line.split(",");
            if (parts.length < 3) {
                System.err.println ("Input line \"" + line + "\" is not a valid variable definition.");
                return false;
            }
            
            try {
                ID = Integer.parseInt(parts[0]);
                NValues = Integer.parseInt(parts[1]);
                // v7.2 eso header contains errors in hourly and daily entries
                if (ID == 2) {
                    NValues = 8;
                }else if (ID == 3) {
                    NValues = 5;
                }
            }catch (NumberFormatException nfe) {
                System.err.println ("First two fields of the input line \"" + line + "\" are not numbers.");
                return false;
            }
            Names = new String [NValues];
            Units = new String [NValues];
            if (ID <= 5) {
                Domain = "";
                for (int i=0; i<NValues; i++) {
                    if (2 + i < parts.length) {
                        // 2 and 3 has day type field which deos not contain []
                        if (parts[2+i].contains("[")) {
                            Names[i] = parts[2+i].substring(0, parts[2+i].indexOf("[")).trim();
                            Units[i] = parts[2+i].substring(parts[2+i].indexOf("[")+1, parts[2+i].indexOf("]")).trim();
                        }else {
                            Names[i] = parts[2+i].trim();
                            Units[i] = "";
                        }
                    }else {
                        Names[i] = "";
                        Units[i] = "";
                    }
                }
            }else {
                Domain = parts[2];
                for (int i=0; i<NValues; i++) {
                    if (3 + i < parts.length) {
                        Names[i] = parts[3+i].substring(0, parts[3+i].indexOf("[")).trim();
                        Units[i] = parts[3+i].substring(parts[3+i].indexOf("[")+1, parts[3+i].indexOf("]")).trim();
                    }else {
                        Names[i] = "";
                        Units[i] = "";
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder ();
        buf.append(ID);
        buf.append(",").append(NValues);
        if (ID > 5) { 
            buf.append(",").append(Domain); 
        }
        for (int i=0; i<NValues; i++) {
            if (Names[i].length() > 0) {
                buf.append(",").append(Names[i]).append(" [").append(Units[i]).append("]");
            }
        }
        buf.append(" ! ").append(Comment);
        return buf.toString();
    }
}
