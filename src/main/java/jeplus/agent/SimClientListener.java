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
package jeplus.agent;

/**
 *
 * @author zyyz
 */
public interface SimClientListener {
    /**
     * Simulation update event handler. This event is normally informative.
     * @param source JobDataRecord instance of the current job is expected
     */
    abstract public void simulationUpdate (Object source);

    /**
     * Simulation finished event handler. This event is fired when simulation completed successfully and result collected.
     * @param source JobDataRecord instance of the current job is expected
     */
    abstract public void simulationFinished (Object source);

    /**
     * Simulation failure event handler. This event is fired when simulation has failed to complete due to either error(s) in the job or JESS.
     * @param source JobDataRecord instance of the current job is expected
     */
    abstract public void simulationFailed (Object source);
    
    /**
     * Simulation cancelled event handler. This event is fired when simulation is cancelled by user/administrator, or rejected by JESS.
     * @param source JobDataRecord instance of the current job is expected
     */
    abstract public void simulationCanceled (Object source);
    
    /**
     * Client error event handler. This event is fired when the client fails to communicate with JESS, or other client errors.
     * @param source A error message (String) is expected
     */
    abstract public void clientError (Object source);
}
