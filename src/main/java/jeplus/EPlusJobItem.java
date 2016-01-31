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
package jeplus;

/**
 * EPlusJobItem interface enables assignment of an id to each job (EPlusTask or
 * EPlusTaskGroup)
 * @author Yi Zhang
 * @version 0.1a
 * @since 0.1
 */
public interface EPlusJobItem {
    /**
     * Get the ID of this job
     * @return Job ID string
     */
    public String getJobID ();
    
    /**
     * Set the ID string of this job
     * @param id ID string of the job
     */
    public void setJobID (String id);
}
