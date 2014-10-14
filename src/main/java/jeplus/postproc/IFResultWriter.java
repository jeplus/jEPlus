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
 * Result writer interface
 */
public interface IFResultWriter {

    /**
     * Write result table and header to file
     * @param manager Simulation manager contains jobs and paths information
     * @param header Table header
     * @param table Table content
     */
    public void writeResult(EPlusBatch manager, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table);

    /**
     * Get the last written result file in full path
     * @return The last written result file
     */
    public String getResultFile();

    /**
     * Get the file name only of the result file to be written
     * @return File name of the result file
     */
    public String getResultFileName();

    /**
     * Get option panel for configuring this post processor
     * @return an instance of option panel
     */
    public abstract JPanel getOptionPanel();
    
}
