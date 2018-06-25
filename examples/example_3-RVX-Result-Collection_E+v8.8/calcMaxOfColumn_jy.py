# Example python script to read a table of (hourly) data and calculate stats on each column
# The results are written to the table specified by user.
# This file should be run within each job folder
# Arguments:
#   sys.argv[1]  -  project's base folder where the project files are located
#   sys.argv[2]  -  output folder of the project where the RunTimes.csv is located
#   sys.argv[3]  -  user-defined output table name + .csv
#   sys.argv[4]  -  Other arguments specified in the RVX file, in this case, the input table file name without extension

import csv, math, sys, os   # Imports necessary modules

def getSimStats(ifile, ofile):
    minlist = ["Min"]   # Creates the list of minimum values
    maxlist = ["Max"]   # Creates the list of maximum values
    sumlist = ["Sum"]   # Creates the list of Sum values
    meanlist = ["Mean"]     # Creates the list of mean values
    stddevlist = ["Standard Deviation"]     # Creates the list of standard deviation values
    headlist = []   # Creates the header row

    with open(ifile, 'rb') as csvfile:   # Opens file as "read-able binary"
        reader = list(csv.reader(csvfile))  # Reads the file as a list
        for item in reader[0]:  # Loops through items in the first row (header) of the file
            headlist.append(item)   # Creates the Header row
        y = len(headlist)  # Calculates the number of columns
        for n in range(y):  # Loops through columns in the file
            value = []  # Resets "Value" list to empty
            for row in reader:  # Loops through rows in the file
                try:
                    data = float(row[n])    # Sets "data" to the value of column "n" in the row as a decimal
                    value.append(data)  # Adds "data" to the list of "Values"
                except ValueError:  # Checks if an error has been returned
                    pass
            try:
                minlist.append(min(value))  # Calculates the minimum value in the "value" list and adds it to "minlist"
                maxlist.append(max(value))  # Calculates the maximum value in the "value" list and adds it to "maxlist"
                sum = 0    # Sets mean as 0
                for item in value:  # Loops through items in "value
                    sum += item    # Adds "item" to "mean"
                sumlist.append(sum)     # Adds "sum" to the list of sums
                mean = sum / len(value)   # Adds each item in the "value" list to "mean" and divides "mean" by the number of values that were added
                meanlist.append(mean)   # Adds "mean" to the list of means
                sumsq = 0   # Sets "sumsq" to 0
                for i in range(len(value)):     # Loops through numbers until it gets to the length of "value"
                    sumsq += (value[i] - mean) **2     # Adds the square of the difference between the "i"th value of value and the mean of value to "sumsq"
                stddevlist.append(math.sqrt(sumsq/(len(value)-1)))  # Adds the square root of "sumsq" divided by the length of "value" minus 1 to "stddevlist"
            except ValueError:  # Checks for an error
                pass

    with open(ofile, "wb") as csvfile:     # Opens file as "write-able" binary
        writer = csv.writer(csvfile, delimiter=",")
        writer.writerow(headlist)   # }
        # writer.writerow(minlist)    # }
        writer.writerow(maxlist)    # } Writes the data to a different document
        # writer.writerow(sumlist)   # }
        # writer.writerow(meanlist)   # }
        # writer.writerow(stddevlist) # }

# Console output will be recorded in either console.log in each job folder, or PyConsole.log in the output folder
# These are for debug only. Disable before running the full project
# for arg in sys.argv:
#    print arg
# print os.getcwd()
os.chdir(sys.argv[2])

getSimStats(sys.argv[4]+'.csv', sys.argv[3])

# Console output will be recorded in either console.log or PyConsole.log. 
# This is for debug only. Disable before running the full project
print sys.argv[0] + ': Reading from ' + sys.argv[4]+'.csv' + ' and writing to ' + sys.argv[3] + ' in ' + os.path.dirname(os.path.realpath(sys.argv[3]))