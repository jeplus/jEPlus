from collections import defaultdict
import re
import csv # to get data from the file
import matplotlib.pyplot as plt # to make the plot
 
def load_data(filename, *keys):
    result = defaultdict(list)
    with open(filename, 'r') as f: # initialise the file handle
        reader = csv.DictReader(f, skipinitialspace=True) # wrap it with a csv DictReader
        
        for row in reader: # loop over the data rows - each is passed into the row variable as a dictionary
            if row['Message'] ==  'EnergyPlus Completed Successfully': # ignore missing data
                for key in keys: # loop over the provided keys
                    value = float(row[key]) # get string from row and convert to float
                    result[key].append(value) # grow the data
    return result
 
def plot_data(data, outfilename, *keys):
    nkeys = len(keys)
    fig, axarr = plt.subplots(nkeys, nkeys) # create a figure with lots of axes
    for x, x_key in enumerate(keys):
        for y, y_key in enumerate(keys):
            ax = axarr[y][x]
            ax.scatter(data[x_key], data[y_key], s=10, alpha=0.25) # scatter of cooling against heating
            if x == 0:
                ax.set_ylabel(axis_label(y_key), fontsize=10)
            if y == nkeys - 1:
                ax.set_xlabel(axis_label(x_key), fontsize=10)
            ax.grid(True)
    fig.tight_layout()
    #plt.savefig(outfilename)
    plt.show()
 
def axis_label(key):
    pattern = re.compile("(\w+):(\w+)\s*\[(\w+)\]")
    match = pattern.match(key)
    name, unit = match.group(1, 3)
    return "%s (%s)" % (name, unit)
 
 
def load_and_plot(infile, outfile, *keys):
    data = load_data(infile, *keys)
    plot_data(data, outfile, *keys)
 
if __name__ == "__main__":
    import sys
    usage = "Usage: %s output_folder key1 [key2 [key3 ..]]" % sys.argv[0]
    args = sys.argv[1:]
 
    outfile = 'example.png'
 
    if len(args) <= 1:
        heat_key = 'Heating:DistrictHeating [J](RunPeriod)'
        cool_key = 'Cooling:DistrictCooling [J](RunPeriod)'
        equip_key = 'InteriorEquipment:Electricity [J](RunPeriod)'
        light_key = 'InteriorLights:Electricity [J](RunPeriod)'
        keys = [heat_key, cool_key, equip_key, light_key]
    else:
        keys = args[1:]
    # keys for getting data (column headings become dictionary keys)
    load_and_plot(sys.argv[1] + 'AllCombinedResults.csv', outfile, *keys)
