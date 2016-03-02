import sys
import os
import re

def flattenifyJson(filename):
    fi = open(filename, 'r')
    fo_name = filename.replace("json","csv")
    fo = open(fo_name,'w')
    flag = 0
    
    for line in fi:
        matcher = re.match('.*"Byte-\\d+":\\s"(\\d[\\.]\\d+)"', line)
        if matcher != None:
            if flag == 0:
                fo.write(matcher.group(1))
                flag = 1
            else:
                fo.write(","+matcher.group(1))
        
    # Close input/output files
    fo.close()
    fi.close()
    
def main():

    if len(sys.argv) != 2:
        print ('usage: ./wordcount.py {file}')
        sys.exit(1)

    filename = sys.argv[1]
    flattenifyJson(filename)
    
if __name__ == '__main__':
  main()