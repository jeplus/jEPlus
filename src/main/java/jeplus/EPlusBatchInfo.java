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

import jeplus.data.ParameterItem;
import java.util.ArrayList;
import jeplus.data.FileList;
import org.slf4j.LoggerFactory;

/**
 * Object to capture batch simulation info and results
 * @author Yi Zhang
 * @version 0.1
 * @since 0.1
 */
public class EPlusBatchInfo {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusBatchInfo.class);
    
    protected boolean ValidationSuccessful = true;
    protected FileList Models = null;
    protected FileList WeatherFiles = null;
    protected ArrayList<ArrayList> ParamChains = null;
    protected ArrayList<ParameterItem> ParamList = null;
    protected ArrayList<String> SearchStrings = null;
    protected ArrayList<String> ShortNames = null;
    protected ArrayList<String> ValidationErrors = null;

    /** 
     * Default constructor
     */
    public EPlusBatchInfo () {
        ParamChains = new ArrayList<> ();
        ParamList = new ArrayList<> ();
        SearchStrings = new ArrayList<> ();
        ShortNames = new ArrayList<> ();
        ValidationErrors = new ArrayList<>();
    }

    // =========== Getters and Setters ============
    
    public FileList getModels() {
        return Models;
    }

    public void setModels(FileList Models) {
        this.Models = Models;
    }

    public FileList getWeatherFiles() {
        return WeatherFiles;
    }

    public void setWeatherFiles(FileList WeatherFiles) {
        this.WeatherFiles = WeatherFiles;
    }

    public ArrayList<ArrayList> getParamChains() {
        return ParamChains;
    }
    
    public int getParamTreeDepth () {
        return SearchStrings == null ? -1 : SearchStrings.size();
    }

    public void setParamChains(ArrayList<ArrayList> ParamChains) {
        this.ParamChains = ParamChains;
    }

    public ArrayList<ParameterItem> getParamList() {
        return ParamList;
    }

    public void setParamList(ArrayList<ParameterItem> ParamList) {
        this.ParamList = ParamList;
    }

    public ArrayList<String> getSearchStrings() {
        return SearchStrings;
    }

    public void setSearchStrings(ArrayList<String> SearchStrings) {
        this.SearchStrings = SearchStrings;
    }

    public ArrayList<String> getShortNames() {
        return ShortNames;
    }

    public void setShortNames(ArrayList<String> ShortNames) {
        this.ShortNames = ShortNames;
    }

    public ArrayList<String> getValidationErrors() {
        return ValidationErrors;
    }

    public void setValidationErrors(ArrayList<String> ValidationErrors) {
        this.ValidationErrors = ValidationErrors;
    }

    public boolean isValidationSuccessful() {
        return ValidationSuccessful;
    }

    public void setValidationSuccessful(boolean ValidationSuccessful) {
        this.ValidationSuccessful = ValidationSuccessful;
    }
    
    // =========== End getters and setters ============
    
    /**
     * Is the project job batch valid or not, according to the validation result
     * @return True if the batch is valid
     */
    public boolean isValid() { return ValidationSuccessful; }
    
    /**
     * Append validation error message to the errors list
     * @param desc The description of "error" to be added to the list
     */
    public void addValidationError (String desc) { ValidationErrors.add(desc); }
    
    /**
     * Get the error message list as a text string
     * @return Text of all error messages recorded
     */
    public String getValidationErrorsText() {
        StringBuilder buf = new StringBuilder ("Validation " + (ValidationSuccessful? "successful: " : "failed") + "\n");
        for (String ValidationError : ValidationErrors) {
            buf.append(ValidationError).append("\n");
        }
        return buf.toString(); 
    }
    
    /**
     * Get a list of search strings in an array form
     * @return The array of search strings
     */
    public String [] getSearchStringsArray () {
        String [] strs = null;
        if (SearchStrings != null) {
            strs = SearchStrings.toArray(new String[0]);
        }
        return strs;
    }

    /**
     * List parameter chains in text form
     * @return Text string of the parameter chains
     */
    public String getParamChainsText () {
        StringBuilder buf = new StringBuilder ("Parameter Chains: ");
        if (ParamChains.size() > 0) {
            buf.append("\n");
            for (ArrayList chain: ParamChains) {
                long n = 1;
                for (ParameterItem param: (ArrayList<ParameterItem>)chain) {
                    buf.append(param.getID()).append("(\"").append(param.getSearchString()).append("\") --> ");
                    n *= param.getNAltValues();
                }
                buf.append(n).append( " jobs\n");
            }
        }else {
            buf.append("empty");
        }
        return buf.toString();
    }
    
    /**
     * Calculated the total number of jobs from parameter definitions. This function 
     * takes into account if any parameter's value is fixed
     * @return Total number of jobs in the current project
     */
    public long getTotalNumberOfJobs () {
        long total = 0;
        if (Models != null && ParamChains.size() > 0) {
            for (ArrayList chain: ParamChains) {
                long n = 1;
                for (ParameterItem param: (ArrayList<ParameterItem>)chain) {
                    n *= (param.getSelectedAltValue() > 0) ? 1 : param.getNAltValues();
                }
                total += n;
            }
            total *= Models.size();
            if (WeatherFiles != null && WeatherFiles.size() > 0) total *= WeatherFiles.size();
        }
        return total;
    }

    /**
     * Calculated the total solution space from the parameter definitions. All
     * parameter values are counted.
     * @return The total search space size of the project
     */
    public long getTotalSolutionSpace () {
        long total = 0;
        if (Models != null && ParamChains.size() > 0) {
            for (ArrayList chain: ParamChains) {
                long n = 1;
                for (ParameterItem param: (ArrayList<ParameterItem>)chain) {
                    n *= param.getNAltValues();
                }
                total += n;
            }
            total *= Models.size();
            if (WeatherFiles != null && WeatherFiles.size() > 0) total *= WeatherFiles.size();
        }
        return total;
    }
}
