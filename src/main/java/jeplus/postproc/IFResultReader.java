/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jeplus.postproc;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;
import jeplus.EPlusBatch;

/**
 * Report reader interface
 */
public interface IFResultReader {

    /**
     * Read result from the named files within the given dir. New data will
     * be added to Header and Data table. This function returns the number
     * of files read.
     * @param manager The simulation manager holds information on the jobs
     * @param dir Folder's path in which reports are stored. This is normally the working directory of the batch.
     * @param file Name of the result file(s), e.g. eplusout.csv in the case of E+ simulations. This field may not contain the full file name.
     * @param header Table header of the report data
     * @param table Table content of the report data
     * @return Number of files read
     */
    public int readResult(EPlusBatch manager, String dir, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table);

    /**
     * This method read result from the named file in the given dir. New data will
     * be added to Header and Data table with the assigned job_id. The header map is
     * maintained to preserve consistency of the columns of the table. This function returns the number
     * of lines read.
     * @param dir Folder path in which reports are stored. This is normally the working directory of the batch.
     * @param file Name of the result file(s), e.g. eplusout.csv in the case of E+ simulations. This field may not contain the full file name.
     * @param job_id ID string of the job
     * @param header Table header of the report data
     * @param table Table content of the report data
     * @return Number of files read
     */
    public int readResult(String dir, String job_id, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table);

    /**
     * Get option panel for configuring this post processor
     * @return an instance of option panel
     */
    public JPanel getOptionPanel();
    
}
