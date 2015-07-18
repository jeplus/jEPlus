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
 ***************************************************************************
 *                                                                         *
 * Change log:                                                             *
 *                                                                         *
 *  - Created                                                              *
 *                                                                         *
 ***************************************************************************/
package jeplus;

import jeplus.data.ParameterItem;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import jeplus.data.Counter;
import org.slf4j.LoggerFactory;

/**
 * <p>TaskGroup is a list of tasks to be performed. It serves as a
 * shell that organises the individual tasks and determines the order and
 * dependence of execution. It also facilitate input(initial states) and output
 * (result states) to the tasks. There are two types of Task Groups: for the 
 * serial group, the next job will not be executed before the present job has 
 * finished. This may be useful for the input/output dependent job series. The 
 * other type is the independent parallel series which can be executed out of 
 * order. Task Groups can be nested, which means a group can hold subsidiary
 * groups as its jobs. The execution of a group is implemented in a separate
 * thread. </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: IESD</p>
 * @author Yi Zhang
 * @version 0.1a
 * @since 0.1
 */
public class EPlusTaskGroup implements EPlusJobItem {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusTaskGroup.class);

    public static final int SERIAL = 1;
    public static final int PARALLEL = 0;
    /** Type of the batch runs */
    public int BatchType = PARALLEL;
    
    //protected static long GlobalJobCount = 0;
    protected static final String JobNameFormat = "%09d"; // nine characters long padded with zero.
    //public static void resetJobCounter () { GlobalJobCount = 0; }
    
    /** Input state file as the initial states of the model - not in use */
    // public String InitStates = null;
    /** Output state file as the result states of the model - not in use */
    // public String ResultStates = null;
    
    /** Label of this task group */
    String GroupLabel = null;
    /** Work environment of EnergyPlus. This is to be shared among tasks in the same group */
    EPlusWorkEnv WorkEnv = null;
    /** Search strings and their corresponding values are stored in a HashMap - to be replaced by ArrayList pair to save memory */
    // protected HashMap<String, String> ParameterMap;
    /** Search strings ArrayList */
    protected ArrayList<String> SearchStringList;
    /** Alt values ArrayList */
    protected ArrayList<String> AltValueList;
    /** The current variable to be evaluated in the sub-jobs */
    protected ParameterItem CurrentVariable = null;
//    /** The search string of the current variable to be used in the sub-jobs */
//    protected String CurSearchString;
//    /** The alternative values of the current variable to be evaluated in the sub-jobs */
//    protected String [] CurAltValues;
    
    /** The job queue */
    //ArrayList<EPlusJobItem> Jobs = new ArrayList<EPlusJobItem>();

//    /**
//     * Construct an EnergyPlus task group
//     * @param label The label of this task group, normally the short form of the variable name
//     */
//    public EPlusTaskGroup(EPlusWorkEnv env, String label, HashMap<String, String> prevmap) {
//        WorkEnv = env;
//        GroupLabel = label;
//        ParameterMap = (prevmap == null) ? new HashMap<String, String>() : prevmap;
//    }


    /**
     * Construct an EnergyPlus task group
     * @param env Job settings
     * @param label Task label
     * @param prevkey Search string list
     * @param prevval Alt value list
     */
    public EPlusTaskGroup(EPlusWorkEnv env, String label, ArrayList<String> prevkey, ArrayList<String> prevval) {
        WorkEnv = env;
        GroupLabel = label;
        SearchStringList = (prevkey == null) ? new ArrayList<String>() : prevkey;
        AltValueList = (prevval == null) ? new ArrayList<String>() : prevval;
    }

    /**
     * Get the identity label of this job item
     * @return The identity label
     */
    @Override
    public String getJobID() {
        return GroupLabel;
    }

    /**
     * Set the identity label to this job item
     * @param id The assigned label
     */
    @Override
    public void setJobID(String id) {
        GroupLabel = id;
    }

    /**
     * Compile a task group to create jobs from the given parameter tree
     * @param jobs Job list to which new jobs are added
     * @param jobcount A global counter for jobs
     * @param tree The parameter tree from which the task group is to be compiled.
     * @param autolabel Use automatically generated labels for each job
     * @param indexlist Build jobs, but only keep those in the index list (job id corresponding to GlobalJobCount). This list must have been sorted!!
     * @param ptr Current pointer to the item in index list
     * @return Successful or not
     */
    public boolean compile(List <EPlusTask> jobs, Counter jobcount, DefaultMutableTreeNode tree, boolean autolabel, long [] indexlist, Counter ptr) {
        // If all jobs in the index list have been found, return directly
        if (indexlist != null && ptr.getValue() >= indexlist.length) return true;
        // Otherwise, compile...
        boolean success = true;
        // If this tree node is a leaf node, compile all the tasks and return
        if (tree.isLeaf()) {
            // Create sub-tasks
            try {
                ParameterItem item = (ParameterItem)tree.getUserObject();
                this.CurrentVariable = item;
                String [] vals = item.getAlternativeValues();
                if (indexlist == null) {
                    if (item.getSelectedAltValue() > 0) {
                        // Fixing on one value
                        int i = item.getSelectedAltValue() - 1;
                        ArrayList<String> keys = (ArrayList<String>)SearchStringList.clone();
                        ArrayList<String> altvals = (ArrayList<String>)AltValueList.clone();
                        keys.add(item.getSearchString());
                        altvals.add(vals[i]);
                        String lbl = GroupLabel + "-" + item.getID();
                        EPlusTask task;
                        switch (WorkEnv.getProjectType()) {
                            case JEPlusProject.TRNSYS: 
                                task = new TRNSYSTask (WorkEnv, lbl, i, keys, altvals);
                                break;
                            case JEPlusProject.INSEL: 
                                task = new INSELTask (WorkEnv, lbl, i, keys, altvals);
                                break;
                            case JEPlusProject.EPLUS: 
                            default:
                                task = new EPlusTask (WorkEnv, lbl, i, keys, altvals);
                        }
                        if (autolabel) {
                            task.setJobID(new Formatter().format(GroupLabel + JobNameFormat, jobcount.getValue()).toString());
                        }
                        jobs.add(task);
                        jobcount.inc(); // !! This is not correct, as the generated job-ids for selected values do not conform 
                                        // !! the same id as in the whole project.
                    } else {
                        // Iterating through all values
                        for (int i=0; i<vals.length; i++) {
                            ArrayList<String> keys = (ArrayList<String>)SearchStringList.clone();
                            ArrayList<String> altvals = (ArrayList<String>)AltValueList.clone();
                            keys.add(item.getSearchString());
                            altvals.add(vals[i]);
                            String lbl = GroupLabel + "-" + item.getID();
                            EPlusTask task;
                            switch (WorkEnv.getProjectType()) {
                                case JEPlusProject.TRNSYS: 
                                    task = new TRNSYSTask (WorkEnv, lbl, i, keys, altvals);
                                    break;
                                case JEPlusProject.INSEL: 
                                    task = new INSELTask (WorkEnv, lbl, i, keys, altvals);
                                    break;
                                case JEPlusProject.EPLUS: 
                                default:
                                    task = new EPlusTask (WorkEnv, lbl, i, keys, altvals);
                            }
                            if (autolabel) {
                                task.setJobID(new Formatter().format(GroupLabel + JobNameFormat, jobcount.getValue()).toString());
                            }
                            jobs.add(task);
                            jobcount.inc();
                        }
                    }
                }else { 
                    if (item.getSelectedAltValue() > 0) {
                        // Iterating through all values
                        int i = item.getSelectedAltValue() - 1;
                        if (indexlist[(int)ptr.getValue()] == jobcount.getValue()) {
                            ArrayList<String> keys = (ArrayList<String>)SearchStringList.clone();
                            ArrayList<String> altvals = (ArrayList<String>)AltValueList.clone();
                            keys.add(item.getSearchString());
                            altvals.add(vals[i]);
                            String lbl = GroupLabel + "-" + item.getID();
                            EPlusTask task;
                            switch (WorkEnv.getProjectType()) {
                                case JEPlusProject.TRNSYS: 
                                    task = new TRNSYSTask (WorkEnv, lbl, i, keys, altvals);
                                    break;
                                case JEPlusProject.INSEL: 
                                    task = new INSELTask (WorkEnv, lbl, i, keys, altvals);
                                    break;
                                case JEPlusProject.EPLUS: 
                                default:
                                    task = new EPlusTask (WorkEnv, lbl, i, keys, altvals);
                            }
                            if (autolabel) {
                                task.setJobID(new Formatter().format(GroupLabel + JobNameFormat, jobcount.getValue()).toString());
                            }
                            jobs.add(task);
                            ptr.inc();
                            if (ptr.getValue() >= indexlist.length) {
                                // this means all jobs have been found
                                return (true);
                            }
                        }
                        jobcount.inc();
                    }else {
                        // Iterating through all values
                        for (int i=0; i<vals.length; i++) {
                            if (indexlist[(int)ptr.getValue()] == jobcount.getValue()) {
                                ArrayList<String> keys = (ArrayList<String>)SearchStringList.clone();
                                ArrayList<String> altvals = (ArrayList<String>)AltValueList.clone();
                                keys.add(item.getSearchString());
                                altvals.add(vals[i]);
                                String lbl = GroupLabel + "-" + item.getID();
                                EPlusTask task;
                                switch (WorkEnv.getProjectType()) {
                                    case JEPlusProject.TRNSYS: 
                                        task = new TRNSYSTask (WorkEnv, lbl, i, keys, altvals);
                                        break;
                                    case JEPlusProject.INSEL: 
                                        task = new INSELTask (WorkEnv, lbl, i, keys, altvals);
                                        break;
                                    case JEPlusProject.EPLUS: 
                                    default:
                                        task = new EPlusTask (WorkEnv, lbl, i, keys, altvals);
                                }
                                if (autolabel) {
                                    task.setJobID(new Formatter().format(GroupLabel + JobNameFormat, jobcount.getValue()).toString());
                                }
                                jobs.add(task);
                                ptr.inc();
                                if (ptr.getValue() >= indexlist.length) {
                                    // this means all jobs have been found
                                    return (true);
                                }
                            }
                            jobcount.inc();
                        }
                    }
                }
            }catch (ClassCastException cce) {
                logger.error("", cce);
                success = false;
            }catch (Exception ex) {
                logger.error("", ex);
                success = false;
            }
        // Else if this node has more branches, create sub-groups and pass the
        // branches to the sub-groups to compile.
        }else {
            // Create sub-groups
            try {
                ParameterItem item = (ParameterItem)tree.getUserObject();
                String [] vals = item.getAlternativeValues();
                if (item.getSelectedAltValue() > 0) {
                    int i = item.getSelectedAltValue() - 1;
                    ArrayList<String> keys = (ArrayList<String>)SearchStringList.clone();
                    ArrayList<String> altvals = (ArrayList<String>)AltValueList.clone();
                    keys.add(item.getSearchString());
                    altvals.add(vals[i]);
                    String lbl = GroupLabel + "-" + item.getID() + "_" + i;
                    EPlusTaskGroup group = new EPlusTaskGroup (WorkEnv, autolabel ? GroupLabel : lbl, keys, altvals);
                    // Pass control to the sub-groups
                    for (Enumeration e = tree.children(); e.hasMoreElements();) {
                        try {
                            DefaultMutableTreeNode c = (DefaultMutableTreeNode)e.nextElement();
                            group.compile(jobs, jobcount, c, autolabel, indexlist, ptr);
                        }catch (ClassCastException cce) {
                            logger.error("", cce);
                            success = false;
                        }catch (Exception ex) {
                            logger.error("", ex);
                            success = false;
                        }
                    }
                } else {
                    for (int i=0; i<vals.length; i++) {
                        ArrayList<String> keys = (ArrayList<String>)SearchStringList.clone();
                        ArrayList<String> altvals = (ArrayList<String>)AltValueList.clone();
                        keys.add(item.getSearchString());
                        altvals.add(vals[i]);
                        String lbl = GroupLabel + "-" + item.getID() + "_" + i;
                        EPlusTaskGroup group = new EPlusTaskGroup (WorkEnv, autolabel ? GroupLabel : lbl, keys, altvals);
                        // Pass control to the sub-groups
                        for (Enumeration e = tree.children(); e.hasMoreElements();) {
                            try {
                                DefaultMutableTreeNode c = (DefaultMutableTreeNode)e.nextElement();
                                group.compile(jobs, jobcount, c, autolabel, indexlist, ptr);
                            }catch (ClassCastException cce) {
                                logger.error("", cce);
                                success = false;
                            }catch (Exception ex) {
                                logger.error("", ex);
                                success = false;
                            }
                        }
                    }
                }
            }catch (ClassCastException cce) {
                logger.error("", cce);
                success = false;
            }catch (Exception ex) {
                logger.error("", ex);
                success = false;
            }
        }
        
        return success;
    }
    
}
