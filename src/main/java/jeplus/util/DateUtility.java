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
package jeplus.util;

/**
 *
 * @author yzhang
 */
public class DateUtility {
    static public final long secondInMillis = 1000;
    static public final long minuteInMillis = secondInMillis * 60;
    static public final long hourInMillis = minuteInMillis * 60;
    static public final long dayInMillis = hourInMillis * 24;
    static public final long yearInMillis = dayInMillis * 365;

    public static String showElapsedTime (long since, boolean compact) {

        long ms = System.currentTimeMillis() - since;

        boolean show = false;
        StringBuilder buf = new StringBuilder ();
        long elapsedYears = ms / yearInMillis;
        if (show || elapsedYears > 0) {
            buf.append(elapsedYears);
            if (compact) buf.append( "y ");
            else buf.append( " years ");
            show = true;
        }
        ms = ms % yearInMillis;
        long elapsedDays = ms / dayInMillis;
        if (show || elapsedDays > 0) {
            buf.append(elapsedDays);
            if (compact) buf.append( "d ");
            else buf.append( " days ");
            show = true;
        }
        ms = ms % dayInMillis;
        long elapsedHours = ms / hourInMillis;
        if (show || elapsedHours > 0) {
            buf.append(elapsedHours);
            if (compact) buf.append( "h ");
            else buf.append( " hours ");
            show = true;
        }
        ms = ms % hourInMillis;
        long elapsedMinutes = ms / minuteInMillis;
        if (show || elapsedMinutes > 0) {
            buf.append(elapsedMinutes);
            if (compact) buf.append( "m ");
            else buf.append( " minutes ");
            show = true;
        }
        ms = ms % minuteInMillis;
        long elapsedSeconds = ms / secondInMillis;
        if (show || elapsedSeconds > 0) {
            buf.append(elapsedSeconds);
            if (compact) buf.append( "s ");
            else buf.append( " seconds ");
            show = true;
        }
        return buf.toString();
    }

}
