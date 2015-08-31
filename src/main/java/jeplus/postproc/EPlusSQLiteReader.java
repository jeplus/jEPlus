/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yizhanguk@gmail.com>                    *
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
package jeplus.postproc;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import javax.swing.JPanel;
import jeplus.EPlusBatch;
import jeplus.EPlusConfig;
import jeplus.EPlusTask;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

/**
 *
 * @author zyyz
 */
public class EPlusSQLiteReader implements IFResultReader {

    /** Logger */
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(EPlusSQLiteReader.class);

    protected static String EPlusResultFile = EPlusConfig.getEPDefOutSQL();
    
    /** Transient header string (unparsed) */
    transient String [] HeaderStrings = null;
    /** Transient sql command */
    transient String SQLcommand = null;
    
    public static DataSource getDataSource (String sqlitedb) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + sqlitedb);
        return dataSource;
    }

    public static Connection getDBConnection (String sqlitedb) {
        Connection conn = null;
        try {
            conn = getDataSource(sqlitedb).getConnection();
        } catch (SQLException ex) {
            logger.error("Cannot get DB connection using DataSource.", ex);
        }
        if (conn == null) {
            // Connect using client mode failed
            try {
                Class.forName("jdbc:sqlite:JDBC").newInstance();
                conn = DriverManager.getConnection("jdbc:sqlite:" + sqlitedb);
                //logger.info("Connected to jdbc:sqlite:" + sqlitedb + " in embedded mode.");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
                logger.error("Cannot create connection using embedded driver. ", e);
            }
        }
        return conn;
    }
    
    public static ArrayList<Object []> runSQL(String dbfile, String sql) {
        ArrayList<Object []> result = new ArrayList<> ();
        Connection connection = null;
        try {
            connection = getDBConnection(dbfile);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();  
            int ncol = rsmd.getColumnCount();            
            while (rs.next()) {
                Object [] objs = new Object [ncol];
                for (int i=1; i<=ncol; i++) {
                    objs[i-1] = rs.getObject(i);
                }
                result.add(objs);
            }
        } catch (SQLException e) {
            // if the error message is "out of memory", 
            // it probably means no database file is found
            logger.error("SQL error: ", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                logger.error("SQL failed to close connection: ", e);
            }
        }
        return result;
    }
    
    /**
     * Construct reader by filling the three fields from the passed-in string
     * @param sqlspecs ';' delimited string containing three parts: [table name]; [',' delimited header string], and [sql command]
     */
    public EPlusSQLiteReader (String headerstr, String sqlstr) {
            // Add user specified headers
            HeaderStrings = headerstr.trim().split("\\s*,\\s*");
            SQLcommand = sqlstr;
    }

    @Override
    public int readResult(EPlusBatch manager, String dir, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        // Initiate header, adding staple items
        header.clear();
        header.put("#", Integer.valueOf(0));
        header.put("Job_ID", Integer.valueOf(1));
        header.put("Reserved", Integer.valueOf(2));
        // Add user specified headers
        for (int i=0; i<HeaderStrings.length; i++) {
            if (! header.containsKey(HeaderStrings[i])) {
                header.put(HeaderStrings[i], i+3);
            }
        }
        // Get finished jobs
        List <EPlusTask> JobQueue = manager.getAgent().getFinishedJobs();
        // Collect Job results
        int counter = 0;
        for (int i = 0; i < JobQueue.size(); i++) {
            // For each job, do:
            EPlusTask job = JobQueue.get(i);
            String job_id = job.getJobID();
            if (readResult(dir, job_id, header, table) > 0) counter ++;
        } // done with loading
        return counter;
    }

    @Override
    public int readResult(String dir, String job_id, HashMap<String, Integer> header, ArrayList<ArrayList<String>> table) {
        // Example [job_id].csv:
        // row 1 - Collumn heading: comma delimitted text
        // row 2 and on - data: comma delimitted numerial text

        // Number of jobs whose results have been collected.
        int nResCollected = 0;
        // Read job result file
        try {
            File sql = new File(dir + (dir.endsWith(File.separator)?"":"/") + job_id + "/" + EPlusResultFile);
            if (sql.exists()) {
                ArrayList<Object []> data = runSQL(sql.getPath(), this.SQLcommand);
                // get header index
                int [] index = new int [HeaderStrings.length];
                for (int j=0; j<index.length; j++) {
                    if (! header.containsKey(HeaderStrings[j])) {
                        index[j] = header.size();
                        header.put(HeaderStrings[j], index[j]);
                        for (int k=0; k<table.size(); k++) {
                            table.get(k).add("-");
                        }
                    }else {
                        index[j] = header.get(HeaderStrings[j]).intValue();
                    }
                }
                // add new data
                if (data.isEmpty()) {
                    ArrayList<String> row = new ArrayList<> ();
                    row.add(Integer.toString(table.size()));
                    row.add(job_id);
                    row.add(" ");
                    // add a new row in the data table
                    for (int j=3; j<header.size(); j++) row.add("-");
                    table.add(row);
                }else {
                    for (int i=0; i<data.size(); i++) {
                        ArrayList<String> row = new ArrayList<> ();
                        row.add(Integer.toString(table.size()));
                        row.add(job_id);
                        row.add(" ");
                        // add a new row in the data table
                        for (int j=3; j<header.size(); j++) row.add("-");
                        // fill in data from the result file
                        for (int j=0; j<data.get(i).length; j++) {
                            row.set(index[j], data.get(i)[j].toString());
                        }
                        nResCollected ++;
                        table.add(row);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error in reading SQL table or extracting data.", ex);
        }
        return nResCollected;
    }

    @Override
    public JPanel getOptionPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
