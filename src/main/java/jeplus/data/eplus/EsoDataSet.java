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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yi
 */
public class EsoDataSet {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EsoDataSet.class);
    
    public enum FileType {
        ESO, MTR
    }
    static final String StartDataLine = "End of Data Dictionary";
    static final String EndDataLine = "End of Data";
    static final String NRecWrittenLine = " Number of Records Written=";
    
    /** Eso Data Dictionary records */
    EsoDataDictionary DataDict = new EsoDataDictionary ();
    /** Run period entries records, indexed by serial id as they appeared in the eso file */
    HashMap<Integer, EsoDataRecordRunPeriod> RunPeriodRecords = new HashMap<> ();
    /** Indices of time step entries linked to the given run period id */
    HashMap<Integer, ArrayList<Integer>> TimeStepIndex = new HashMap<>  ();
    /** Simulation time step (hourly, daily ...) entries records, indexed by serial id as they appeared in the eso file */
    HashMap<Integer, EsoDataRecordTimeStep> TimeStepRecords = new HashMap<> ();
    /** Indices of data entries linked to the given time step id */
    HashMap<Integer, ArrayList<Integer>> EntryIndex = new HashMap<> ();
    /** Variable data record (hourly, daily ...) entries records, indexed by serial id as they appeared in the eso file */
    HashMap<Integer, EsoDataRecordEntry> EntryRecords = new HashMap<> ();
    
    /** Default constructor */
    public EsoDataSet () {
        
    }
    
    /** Read full content of the given eso or mtr file */
    public int readEsoDataSet (String filename) {
        DataDict.readEsoDataDictionary(filename);
        try (BufferedReader fr = new BufferedReader (new FileReader (filename))) {
            String line = fr.readLine();
            while (line != null && ! line.startsWith(StartDataLine)) {
                line = fr.readLine();
            }
            // Reset current RunPeriod id and current TimeStep id
            int CurrentRunPeriod = -1;
            ArrayList<Integer> TIndex = new ArrayList<> ();
            int CurrentTimeStep = -1;
            ArrayList<Integer> EIndex = new ArrayList<> ();
            // Parsing lines
            while (line != null) {
                // Split text by "," and adjacent white spaces
                String [] parts = line.split("\\s*,\\s*");
                if (parts.length > 1) {
                    // Check first field to determine what type of entry this is
                    int type = Integer.parseInt(parts[0]);
                    if (type == 1) { // RunPeriod record
                        EsoDataRecordRunPeriod item = 
                            EsoDataRecordRunPeriod.getEsoDataRecordRunPeriod(DataDict.DictItems.get(type), parts);
                        if (item != null) {
                            CurrentRunPeriod = RunPeriodRecords.size();
                            RunPeriodRecords.put(CurrentRunPeriod, item);
                            TIndex = new ArrayList<> ();
                            TimeStepIndex.put(CurrentRunPeriod, TIndex);
                        }
                    }else if (type <= 5) { // TimeStep record
                        EsoDataRecordTimeStep item = 
                            EsoDataRecordTimeStep.getEsoDataRecordTimeStep(CurrentRunPeriod, DataDict.DictItems.get(type), parts);
                        if (item != null) {
                            CurrentTimeStep = TimeStepRecords.size();
                            TimeStepRecords.put(CurrentTimeStep, item);
                            TIndex.add(CurrentTimeStep);
                            EIndex = new ArrayList<> ();
                            EntryIndex.put(CurrentTimeStep, EIndex);
                        }
                    }else {  // Variable record
                        EsoDataRecordEntry item = 
                            EsoDataRecordEntry.getEsoDataRecordEntry(CurrentTimeStep, DataDict.DictItems.get(type), parts);
                        if (item != null) {
                            int item_id = EntryRecords.size();
                            EntryRecords.put(item_id, item);
                            EIndex.add(item_id);
                        }
                    }
                }else {
                    // line is empty, ignore
                }
                line = fr.readLine();
            }
        }catch (Exception ex) {
            logger.error("Error reading ESO/MTR data from file " + filename, ex);
        }
        return /*RunPeriodRecords.size() + TimeStepRecords.size() + */EntryRecords.size();
    }
    
    /** Write content to a file */
    public void writeEsoFile (String filename) {
        // Write dictionary section
        DataDict.writeToFile (filename);
        // Now, data section
        try (PrintWriter fw = new PrintWriter (new FileWriter (filename, true))) {
            for (int pid = 0; pid < RunPeriodRecords.size(); pid ++) {
                EsoDataRecordRunPeriod period = RunPeriodRecords.get(pid);
                fw.println(period.toString());
                for (Integer sid : TimeStepIndex.get(pid)) {
                    EsoDataRecordTimeStep step = TimeStepRecords.get(sid);
                    fw.println(step.toString());
                    for (Integer eid : EntryIndex.get(sid)) {
                        EsoDataRecordEntry entry = EntryRecords.get(eid);
                        fw.println(entry.toString());
                    }
                }
            }
            fw.println(EndDataLine);
            fw.println(NRecWrittenLine + EntryRecords.size());
        }catch (Exception ex) {
            logger.error("Failed to write data to file " + filename, ex);
        }
    }
    
    protected List<Integer> findTimeStepsOfPeriod (int pid) {
        ArrayList <Integer> list = new ArrayList <> ();
        for (int i=0; i<TimeStepRecords.size(); i++) {
            if (TimeStepRecords.get(i).getRunPeridID() == pid) {
                list.add(i);
            }
        }
        return list;
    }

    protected List<Integer> findEntriesOfTimeStep (int sid) {
        ArrayList <Integer> list = new ArrayList <> ();
        for (int i=0; i<EntryRecords.size(); i++) {
            if (EntryRecords.get(i).getTimeStepID() == sid) {
                list.add(i);
            }
        }
        return list;
    }
}
