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
public class EsoDataRecordEntry {
    int TimeStepID;
    int DictItemID;
    double [] Values;
    
    public EsoDataRecordEntry () {}

    public int getTimeStepID() {
        return TimeStepID;
    }

    public int getDictItemID() {
        return DictItemID;
    }

    public double[] getValues() {
        return Values;
    }
    
    public static EsoDataRecordEntry getEsoDataRecordEntry (int step_id, EsoDataDictionaryItem dict, String [] textparts) {
        EsoDataRecordEntry item = new EsoDataRecordEntry();
        item.TimeStepID = step_id;
        item.DictItemID = dict.ID;
        if (item.parseText(dict, textparts)) {
            return item;
        }else {
            return null;
        }
    }
    
    public boolean parseText (EsoDataDictionaryItem dict, String [] parts) {
        if (parts.length < dict.NValues + 1) {
            System.err.println ("Not enough parts. " + dict.NValues + " strings are required, but only " + (parts.length - 1) + " are found.");
            return false;
        }
        Values = new double [dict.NValues];
        for (int i=0; i<dict.NValues; i++) {
            try {
                Values[i] = Double.parseDouble(parts[i+1]);
            }catch (NumberFormatException nfe) {
                System.err.println("Non-numeric field in data line: " + parts[i+1]);
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString () {
        StringBuilder buf = new StringBuilder ();
        buf.append(DictItemID);
        for (int i=0; i<Values.length; i++) {
            buf.append(",").append(Values[i]);
        }
        return buf.toString();
    }
}
