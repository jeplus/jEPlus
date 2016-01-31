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
package jeplus.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 * Data object representing an DaySim model
 * @author Yi
 */
public class DaySimModel {

    /** Logger */
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DaySimModel.class);
    
    String CurrentFile = null;
    Properties Prop = new Properties ();

    //#############################
    //# Example DAYSIM header file
    //#############################
    String project_name       = "DaysimExample";
    String project_directory  = ".\\";
    String bin_directory      = "Daysim\\bin\\";
    String tmp_directory      = "tmp\\";
    String Template_File      = "Daysim\\template\\DefaultTemplate.htm";

    //###################
    //# site information
    //###################
    String place              = "40 Edward Street";
    String latitude           = "51.15";
    String longitude          = "-0.18";
    String time_zone          = "0";
    String site_elevation     = "117";
    String time_step          = "60";
    String wea_data_short_file = "in.wea";
    String wea_data_short_file_units = "1";
    String lower_direct_threshold    = "2";
    String lower_diffuse_threshold   = "2";
    String output_units             = "2";

    //#######################
    //# building information
    //#######################
    String material_file         = "dbmodel_material.rad";
    String geometry_file         = "dbmodel_geometry.rad";
    String radiance_source_files = "1, dbmodel.rad";
    String sensor_file           = "trace.in";
    String AdaptiveZoneApplies   = "0";
    String dgp_image_x_size      = "500";
    String dgp_image_y_size      = "500";

    //#################################
    //# Radiance simulation parameters
    //#################################
    String ab = "5";
    String ad = "2048";
    String as = "1024";
    String ar = "512";
    String aa = ".2";
    String lr = "6";
    String st = "0.1500";
    String sj = "1.0000";
    String lw = "0.0040000";
    String dj = "0.0000";
    String ds = "0.200";
    String dr = "2";
    String dp = "512";

    //#############################
    //# dynamic simulation options
    //##############################

    //#===================
    //#= user description
    //#===================
    String occupancy = "5 dbmodel_occ.csv";
    String minimum_illuminance_level = "300";
    String daylight_savings_time = "1";


    //#==========================
    //#= shading control system
    //#==========================
    String shading = "1 dbmodel dbmodel.dc dbmodel.ill";


    //#==========================
    //#= electric lighting system
    //#==========================
    //#sensor_file_info 0 0 0 0

    //######################
    //# daylighting results
    //######################
    String daylight_autonomy_active_RGB = "dbmodel_autonomy.DA";
    String electric_lighting = "dbmodel_electriclighting.htm";
    String direct_sunlight_file = "dbmodel.dir";
    String thermal_simulation = "dbmodel_intgain.csv";
    
    
    /**
     * Load DaySim model header from text file (java property format)
     * @param fn Configure file name
     * @return Load successful or not
     */
    public final boolean loadFromFile (String fn) {
        try {
            Prop.load(new FileReader (fn));
            this.CurrentFile = fn;
        }catch (FileNotFoundException fnfe) {
            logger.error("Specified configue file " + fn + " is not found.");
            return false;
        }catch (Exception ex) {
            logger.error("Error loading configure file " + fn, ex);
            return false;
        }
        return true;
    }

    /**
     * Save DaySim model to properties file that DaySim header uses
     * @param comment Comment line to be added to the file
     * @return Save successful or not
     */
    public boolean saveToFile (String comment) {
        try {
            Prop.store(new FileWriter (this.CurrentFile), comment);
        }catch (Exception ex) {
            logger.error("Error saving properties to " + CurrentFile, ex);
            return false;
        }
        return true;
    }

    /**
     * Update properties in the model
     * @param props The list of properties and their new values to be set
     */
    public void updateProperties (Map<String, String> props) {
        for (String key : props.keySet()) {
            Prop.setProperty(key, props.get(key));
        }
    }

    /**
     * Update the DaySim header file with the given properties
     * @param headerfile
     * @param newfile
     * @param props The list of properties and their new values to be set
     */
    public static void updateHeaderFile (String headerfile, String newfile, Map<String, String> props) {
        try (BufferedReader fr = new BufferedReader (new FileReader (headerfile));
                PrintWriter fw = new PrintWriter (new FileWriter (newfile))) {
            String line = fr.readLine();
            while (line != null) {
                String content = line;
                // Filter comments
                if (content.contains("#")) { content = content.substring(0, content.indexOf("#")).trim(); }
                if (content.contains("=")) { content = content.substring(0, content.indexOf("=")).trim(); }
                // Split key and value
                if (content.length() > 0) {
                    String [] kvpair = content.split("\\s+", 2);
                    // Try update value if key is found in props
                    if (kvpair.length >= 1 && props.containsKey(kvpair[0])) {
                        line = kvpair[0] + " " + props.get(kvpair[0]);
                    }
                }
                fw.println(line);
                line = fr.readLine();
            }
            fr.close();
            fw.close();
        }catch (Exception ex) {
            logger.error ("Error processing header file " + headerfile, ex);
        }
    }
}
