# Example python script: This script reads from RunTimes.csv, calculates CPU time used in seconds,
# and then write to different table specified by the user.
# Arguments:
#   sys.argv[1]  -  project's base folder where the project files are located
#   sys.argv[2]  -  output folder of the project where the RunTimes.csv is located
#   sys.argv[3]  -  the list of jobs have been executed in the project
#   sys.argv[4]  -  user-defined output table name + .csv
#   sys.argv[5..] - Other arguments specified in the RVX file

import sys
import _csv
import math

ifile  = open(sys.argv[2] + "RunTimes.csv", "rt")
reader = _csv.reader(ifile)
ofile = open(sys.argv[2] + sys.argv[4], "wb")
writer = _csv.writer(ofile)

rownum = 0
timelist = []
for row in reader:
    # Save header row.
    if rownum == 0:
        header = row[0:3]
        header.append("CpuTime")
        writer.writerow(header)
    else:
        time = [float(t) for t in row[5:]]
        seconds = time[0]*3600+time[1]*60+time[2]
        timelist.append(seconds)
        temprow = row[0:3]
        temprow.append(seconds)
        writer.writerow(temprow)
    rownum += 1
ifile.close()
ofile.close()

n = len(timelist)
mean = sum(timelist) / n
sd = math.sqrt(sum((x-mean)**2 for x in timelist) / n)

# Console output will be recorded in PyConsole.log
print '%(n)d jobs done, mean simulation time = %(mean).2fs, stdev = %(sd).2fs' % {'n':n, 'mean':mean, 'sd':sd}