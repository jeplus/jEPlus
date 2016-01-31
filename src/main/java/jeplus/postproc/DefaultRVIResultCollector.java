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
package jeplus.postproc;

import java.util.ArrayList;
import jeplus.data.RVX;
import org.slf4j.LoggerFactory;

/**
 * This default RVI result collector reads the indexes and simulation reports.
 * @author Yi
 */
public class DefaultRVIResultCollector extends ResultCollector {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(DefaultRVIResultCollector.class);
    
    /**
     * Empty constructor. Actual assignment of readers and writers are done in the <code>collectResutls()</code> function
     * @param Desc Description of this collector
     */
    public DefaultRVIResultCollector (String Desc) {
        super (Desc);
        DefaultCSVWriter csvwriter = new DefaultCSVWriter("RunTimes.csv", "SimResults.csv");
        EPlusOutputReader defreader = new EPlusOutputReader();
        this.RepReader = defreader;
        this.RepWriter = csvwriter;
        // this.ResReader = defreader;
        this.ResWriter = csvwriter;
        this.IdxWriter = new DefaultIndexWriter ("SimJobIndex.csv");
    }

    @Override
    public ArrayList<String> getExpectedResultFiles(RVX rvx) {
        ArrayList<String> list = new ArrayList<> ();
        // This collector deals with index and reports only
        return list;
    }
}
