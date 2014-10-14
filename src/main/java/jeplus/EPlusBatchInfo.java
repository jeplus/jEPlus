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
import java.util.ArrayList;
import jeplus.data.FileList;
import org.slf4j.LoggerFactory;

/**
 * Object to capture batch simulation info and results - Experimental
 * @author Yi Zhang
 * @version 0.1a
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

    public EPlusBatchInfo () {
        ParamChains = new ArrayList<> ();
        ParamList = new ArrayList<> ();
        SearchStrings = new ArrayList<> ();
        ShortNames = new ArrayList<> ();
        ValidationErrors = new ArrayList<>();
    }

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
    
    public boolean isValid() { return ValidationSuccessful; }
    
    public void addValidationError (String desc) { ValidationErrors.add(desc); }
    
    public String getValidationErrorsText() {
        StringBuilder buf = new StringBuilder ("Validation " + (ValidationSuccessful? "successful: " : "failed") + "\n");
        for (int i=0; i<ValidationErrors.size(); i++) {
            buf.append(ValidationErrors.get(i)).append("\n");
        }
        return buf.toString(); 
    }
    
    public String [] getSearchStringsArray () {
        String [] strs = null;
        if (SearchStrings != null) {
            strs = SearchStrings.toArray(new String[0]);
        }
        return strs;
    }

    public String getParamChainsText () {
        StringBuilder buf = new StringBuilder ("Parameter Chains: ");
        if (ParamChains.size() > 0) {
            buf.append("\n");
            for (ArrayList chain: ParamChains) {
                long n = 1;
                for (ParameterItem param: (ArrayList<ParameterItem>)chain) {
                    buf.append(param.ID).append("(\"").append(param.SearchString).append("\") --> ");
                    n *= param.getNAltValues();
                }
                buf.append(n).append( " jobs\n");
            }
        }else {
            buf.append("empty");
        }
        return buf.toString();
    }
    
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
