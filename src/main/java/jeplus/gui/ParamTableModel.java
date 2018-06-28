/*
 * Copyright (C) 2018 yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jeplus.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import jeplus.JEPlusProjectV2;
import jeplus.data.ParameterItemV2;

/**
 *
 * @author yi
 */
public class ParamTableModel extends AbstractTableModel {
    
    String [] ColumnNames = {
        "#", "Type", "PID", "Search Tag", "Value Type", "Values", "N"
    };
            
    ArrayList<ParameterItemV2> Parameters = null;
    
    JEPlusProjectV2 Project = null;
    
    public ParamTableModel (ArrayList<ParameterItemV2> Parameters, JEPlusProjectV2 Project) {
        this.Parameters = Parameters;
        this.Project = Project;
    }
    
    @Override
    public String getColumnName(int col) {
        return ColumnNames[col];
    }
    
    @Override
    public int getRowCount() { 
        return Parameters.size(); 
    }
    
    @Override
    public int getColumnCount() { 
        return ColumnNames.length; 
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        ParameterItemV2 item = Parameters.get(row);
        switch (col) {
            case 0:
                return row;
            case 1:
                return item.getParamType();
            case 2:
                return item.getID();
            case 3:
                return item.getSearchString();
            case 4:
                return item.getType();
            case 5:
                return item.getValuesString();
            case 6:
                return item.getNAltValues(Project);
        }
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int col){ 
        return col != 0 && col != 6; 
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        ParameterItemV2 item = Parameters.get(row);
        boolean changed = false;
        switch (col) {
            case 0:
                break;
            case 1:
                item.setParamType((ParameterItemV2.PType)value);
                changed = true;
                break;
            case 2:
                item.setID(value.toString());
                changed = true;
                break;
            case 3:
                item.setSearchString(value.toString());
                changed = true;
                break;
            case 4:
                item.setType((ParameterItemV2.VType)value);
                changed = true;
                break;
            case 5:
                item.setValuesString(value.toString());
                changed = true;
                break;
            case 6:
                break;
        }
        if (changed) fireTableCellUpdated(row, col);
    }    
}
