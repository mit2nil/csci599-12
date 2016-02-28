import sys
import os
import re

def flattenifyJson(filename):
    fi = open(filename, 'r')
    fo_name = filename.replace("json","csv")
    fo = open(fo_name,'w')
    flag = 0
    
    for line in fi:
        matcher = re.match(r'\s+"(\w+)/(.+)":\s+"(\d+)"', line)

        if matcher != None:
            # print ("matcher.group() : ", matcher.group())
            # print ("matcher.group(1) : ", matcher.group(1))
            # print ("matcher.group(2) : ", matcher.group(2))
            # print ("matcher.group(3) : ", matcher.group(3))
            if flag == 0:
                fo.write(matcher.group(1)+":"+matcher.group(2)+","+matcher.group(3))
                flag = 1
            else:
                fo.write("\n"+matcher.group(1)+":"+matcher.group(2)+","+matcher.group(3))
        
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