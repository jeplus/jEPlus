/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yi@jeplus.org>                          *
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
 ***************************************************************************/
package jeplus.gui;

/**
 *
 * @author Yi
 */
public interface IFJEPlusEditorPanel {

    /**
     * Confirm closing the current file. User will be prompted to save if the
     * content of the file has been changed.
     * @return cancel flag
     */
    boolean closeTextPanel();

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
