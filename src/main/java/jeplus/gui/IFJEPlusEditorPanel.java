/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jeplus.gui;

/**
 *
 * @author Yi
 */
public interface IFJEPlusEditorPanel {

    /**
     * Confirm closing the current file. User will be prompted to save if the
     * content of the file has been changed.
     */
    void closeTextPanel();

    /**
     * Get tab id of this panel in the host tabbed pane
     * @return Tab id
     */
    int getTabId();

    /** 
     * Get the title of this panel. The title will appear on the tabbed pane
     * @return  
     */
    String getTitle();

    /**
     * Set the tab id according to the host tabbed pane
     * @param TabId 
     */
    void setTabId(int TabId);

    /** 
     * Set title of this panel to the given string
     * @param title 
     */
    void setTitle(String title);
    
}
