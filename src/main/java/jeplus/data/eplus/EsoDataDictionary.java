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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class EsoDataDictionary {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EsoDataDictionary.class);
    
    String Info;
    static final String EndLine = "End of Data Dictionary";
    HashMap<Integer, EsoDataDictionaryItem> DictItems = new HashMap<> ();
    
    public EsoDataDictionary () {}
    
    public int readEsoDataDictionary (String file) {
        BufferedReader fr;
        try {
            fr = new BufferedReader (new FileReader (file));
            String line = fr.readLine();
            Info = line;
            line = fr.readLine();
            while (line != null && ! line.startsWith(EndLine)) {
                EsoDataDictionaryItem item = EsoDataDictionaryItem.getEsoDataDictionaryItem(line);
                if (item != null) {
                    DictItems.put(item.ID, item);
                }
                line = fr.readLine();
            }
            fr.close();
        } catch (FileNotFoundException ex) {
            logger.error("Failed to open dictionary file " + file, ex);
        } catch (Exception ex) {
            logger.error("Error opening dictionary file " + file, ex);
        }
        return DictItems.size();
    }
    
    public void writeToFile (String filename) {
        try (PrintWriter fw = new PrintWriter (new FileWriter (filename))) {
            fw.println(Info);
            for (EsoDataDictionaryItem item : DictItems.values()) {
                fw.println(item.toString());
            }
            fw.println(EndLine);
        }catch (Exception ex) {
            logger.error("Failed to write to file " + filename, ex);
        }
        
    }
}
