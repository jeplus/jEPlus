import sys
import _csv
import math

ifile  = open(sys.argv[1] + "RunTimes.csv", "rt")
reader = _csv.reader(ifile)

rownum = 0
timelist = []
for row in reader:
    # Save header row.
    if rownum == 0:
        header = row
    else:
        time = [float(t) for t in row[5:]]
        timelist.append(time[0]*3600+time[1]*60+time[2])
    rownum += 1
ifile.close()

n = len(timelist)
mean = sum(timelist) / n
sd = math.sqrt(sum((x-mean)**2 for x in timelist) / n)
print '%(n)d jobs done, mean simulation time = %(mean).2fs, stdev = %(sd).2fs' % {'n':n, 'mean':mean, 'sd':sd}