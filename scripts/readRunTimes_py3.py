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
        # colnum = 0
        # for col in row:
            # print ('%s: %s' % (header[colnum], col))
            # colnum += 1
        time = [float(t) for t in row[5:]]
        timelist.append(time[0]*3600+time[1]*60+time[2])
        # print ('{0}: Total simulation time = {1:.2f}s'.format(row[1], time[0]*3600+time[1]*60+time[2]))
    rownum += 1
ifile.close()
n = len(timelist)
mean = sum(timelist) / n
sd = math.sqrt(sum((x-mean)**2 for x in timelist) / n)
print ('{0:10d} jobs done, mean simulation time = {1:.2f}s, stdev = {2:.2f}s'.format(n, mean, sd))