# Example python script: This script reads from RunTimes.csv, calculates CPU time used in seconds,
# and then write to different table specified by the user.
# Arguments:
#   sys.argv[1]  -  project's base folder where the project files are located
#   sys.argv[2]  -  output folder of the project where the RunTimes.csv is located
#   sys.argv[3]  -  Other arguments specified in the parameter definition. They are passed in as a ',' delimitted string
#   sys.argv[4]  -  The location of the binary files of the simulation program, e.g. the location of Energy+.idd. This argument is only relevant with EnergyPlus simulations

import sys

print sys.argv[0] + ' is called with args: '
print '  argv[1] - ' + sys.argv[1]
print '  argv[2] - ' + sys.argv[2]
print '  argv[3] - ' + sys.argv[3]
print '  argv[4] - ' + sys.argv[4]

