/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@googlemail.com>               *
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

import java.util.*;
import org.slf4j.LoggerFactory;

/**
 * RadianceTask class is a wrapper for identifying job type as RTRACE or RPICT in JESS
 * @author Yi Zhang
 * @version 1.0
 * @since 1.6
 */
public class RadianceTask extends EPlusTask {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(RadianceTask.class);

    //static final long serialVersionUID = 1L;
    String Type = null;
    String Args = null;
    String Model = null;
    String Infile = null;
    String Outfile = null;
    
    /**
     * Create an instance of the INSEL task
     * @param env Data packet including template
     * @param label Label of the task
     * @param id ID of the task
     * @param prevkey Search string list
     * @param prevval Alt value list
     */
    public RadianceTask(EPlusWorkEnv env, String label, int id, ArrayList<String> prevkey, ArrayList<String> prevval) {
        super (env, label, id, prevkey, prevval);
    }

    /**
     * Create an instance of the INSEL task
     * @param env Data packet including template
     * @param job_id Externally defined job_ID string of this task
     * @param type
     * @param args
     * @param model
     * @param infile
     * @param outfile
     */
    public RadianceTask(EPlusWorkEnv env, String job_id, String type, String args, String model, String infile, String outfile) {
        super (env, job_id, new ArrayList<String>(), new ArrayList<String>());
        Type = type;
        Args = args;
        Model = model;
        Infile = infile;
        Outfile = outfile;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getArgs() {
        return Args;
    }

    public void setArgs(String Args) {
        this.Args = Args;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String Model) {
        this.Model = Model;
    }

    public String getInfile() {
        return Infile;
    }

    public void setInfile(String Infile) {
        this.Infile = Infile;
    }

    public String getOutfile() {
        return Outfile;
    }

    public void setOutfile(String Outfile) {
        this.Outfile = Outfile;
    }

    
}