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

import java.util.Random;

/**
 * Roulette wheel functions
 * @author zyyz
 */
public class RouletteWheel {
    
    Random RandomGenerator;
    boolean EqualWidth = true;
    double TotalWidth = 0;
    int NCells = 0;
    double CellWidth = 0.1;
    double [] Bins = null;
    
    public RouletteWheel (int ncells, Random randomsrc) {
        if (randomsrc != null) {RandomGenerator = randomsrc;}
        else {RandomGenerator = RandomSource.getRandomGenerator();}
        NCells = ncells;
        EqualWidth = true;
        TotalWidth = CellWidth * NCells;
    }

    public RouletteWheel (double [] cellwidths, Random randomsrc) {
        if (randomsrc != null) {RandomGenerator = randomsrc;}
        else {RandomGenerator = RandomSource.getRandomGenerator();}
        EqualWidth = false;
        NCells = cellwidths.length;
        Bins = new double [NCells];
        Bins[0] = cellwidths[0];
        for (int i=1; i<cellwidths.length; i++) {
            Bins[i] = Bins[i-1] + cellwidths[i];
        }
    }
    
    public double getDefaultCellWidth () {
        return CellWidth;
    }

    public double[] getBins() {
        return Bins;
    }

    public boolean isEqualWidth() {
        return EqualWidth;
    }

    public int getNCells() {
        return NCells;
    }

    public double getTotalWidth() {
        return TotalWidth;
    }
    
    public int spin (double ball) {
        if (EqualWidth) {
            return (int)Math.floor(ball / CellWidth);
        }else {
            for (int i=0; i<Bins.length; i++) {
                if (ball < Bins[i]) return i;
            }
        }
        return -1; // failed to find the right bin
    }
    
    public int spin () {
        return (spin(RandomGenerator.nextDouble() * TotalWidth));
    }
    
    public int spin (double lb, double ub) {
        return (spin(RandomGenerator.nextDouble() * (ub - lb) + lb));
    }
    
}
