/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeplus;

/**
 *
 * @author Yi
 */
public class JEPlusVersion {
    public final static String Version = "2.2.0";
    public final static String Release = "";
    public final static String Version_PS = "_2_2";
    public final static String OsName = System.getProperty( "os.name" );
    public final static String Year = "2024";
    public final static String License = "https://www.gnu.org/licenses/gpl-3.0.en.html";
    public final static String UsersGuide = "https://www.jeplus.org/wiki/doku.php?id=docs:manual_2_2";
    
    public static String getVersion() {
        return "jEPlus (version " + Version + " " + Release + ") ";
    }
}
