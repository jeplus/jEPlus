/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jeplus.postproc;

import jeplus.EPlusBatch;

/** Index writer interface */
public interface IFIndexWriter {

    public boolean writeIndex(EPlusBatch manager);

    public String getIndexFile();
    
}
