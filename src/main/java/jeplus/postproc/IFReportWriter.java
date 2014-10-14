/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jeplus.postproc;

import java.util.ArrayList;
import javax.swing.JPanel;
import jeplus.EPlusBatch;

/** Report writer interface */
public interface IFReportWriter {

    /**
     * Write report table to file
     * @param manager Simulation manager contains jobs and paths information
     * @param table Table content
     */
    public void writeReport(EPlusBatch manager, ArrayList<String> header, ArrayList<ArrayList<String>> table);

    /**
     * Get the last written report file in full path
     * @return The last written report file
     */
    public String getReportFile();

    /**
     * Get the file name only of the report file to be written
     * @return File name of the report file
     */
    public String getReportFileName();

    /**
     * Get option panel for configuring this post processor
     * @return an instance of option panel
     */
    public abstract JPanel getOptionPanel();
    
}
