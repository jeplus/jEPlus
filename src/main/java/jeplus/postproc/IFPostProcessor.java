/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jeplus.postproc;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * Post processor interface
 */
public interface IFPostProcessor {

    /**
     * Process the result table from the batch
     * @param header Result table header in a HashMap
     * @param table Table content
     */
    public void postProcess(HashMap<String, Integer> header, ArrayList<ArrayList<String>> table);

    /**
     * Process each job's csv file and return results in a string. The results can be
     * identified by the job_id if included.
     * @param jobid The job_id can be used to identify the current csv file
     * @param csvfile The csv file to processJobResult
     * @return Results in a string.
     */
    public abstract String processJobResult(String jobid, String csvfile);

    /**
     * Get option panel for configuring this post processor
     * @return an instance of option panel
     */
    public abstract JPanel getOptionPanel();
    
}
