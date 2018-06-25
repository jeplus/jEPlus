# python script for pre-processing: This script takes WWR value from the argument list and modifies all the windows in the IDF model
# This script is designed to work with jEPlus v1.6.4 and later, with the @python? syntax
# @author: Dr Ivan Korolija [ivan.korolija@gmail.com]
# Arguments:
#   sys.argv[1]  -  project's base folder where the project files are located
#   sys.argv[2]  -  folder of the current case where in.idf is located
#   sys.argv[3]  -  Other arguments specified in the parameter definition. They are passed in as a ',' delimited string
#   sys.argv[4]  -  folder of the binary files of the simulation program, e.g. the location of Energy+.idd

import os
from eppy.modeleditor import IDF
import sys
import math

# function for calculating wall width and height
def wall_width_height(coordinates):
    ulc = coordinates[0]  # upper left corner coordinates
    blc = coordinates[1]  # bottom left corner coordinates
    brc = coordinates[2]  # bottom right corner coordinates

    # calculate wall width and height by using the Euclidean distance
    w = math.sqrt(math.pow(
        (brc[0] - blc[0]), 2) + math.pow((brc[1] - blc[1]), 2) +
        math.pow((brc[2] - blc[2]), 2))
    h = math.sqrt(math.pow(
        (ulc[0] - blc[0]), 2) + math.pow((ulc[1] - blc[1]), 2) +
        math.pow((ulc[2] - blc[2]), 2))
    return w, h  # return wall width and height
# End 


# path to E+ idd file (required by eppy)
iddfile = os.path.join(sys.argv[4], 'Energy+.idd')
IDF.setiddname(iddfile)

# path to energyplus input file within each simulated folder
idf = os.path.join(sys.argv[2], 'in.idf')

# glazing ratio convert to integer
gr = int(sys.argv[3])

idf = IDF(idf)  # read idf file to eppy
# extract window and wall objects
window_objects = idf.idfobjects['Window'.upper()]
wall_object = idf.idfobjects['BuildingSurface:Detailed'.upper()]

# loop through window objects
for window in window_objects:
    # find the base surface for the window
    win_base_surface = window.Building_Surface_Name

    # loop through the wall objects
    for wall in wall_object:
        # when wall name equals to the window base surface name extract coords
        if wall.Name == win_base_surface:
            coord = wall.coords
            # calculate wall width and height
            w, h = wall_width_height(coord)
            # calculate wall length and height as a function of glazing ratio
            wl = w * math.sqrt(gr / 100)  # window length
            wh = h * math.sqrt(gr / 100)  # window height
            # starting X/Z coordinates relative to the wall bottom left corner
            x = (w - wl) / 2
            z = (h - wh) / 2

    # coords and window H/L converted into strings and applied to window object
    window.Starting_X_Coordinate = '%.2f' % x
    window.Starting_Z_Coordinate = '%.2f' % z
    window.Length = '%.2f' % wl
    window.Height = '%.2f' % wh

# save the updated idf file
idf.saveas('in.idf')

# Done
