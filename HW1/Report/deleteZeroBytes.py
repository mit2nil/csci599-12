# Usage: python deleteZeroBytes.py > output.txt

# File size to be deleted
target_size = 0
import os

# os.walk(give folder name which contains one or more mime folders to be cleaned)
for dirpath, dirs, files in os.walk('polar_data_all_mime15'):
    count = 0
    for file in files: 
        path = os.path.join(dirpath, file)
        if os.stat(path).st_size == target_size:
            os.remove(path)
            count = count + 1
    if count != 0:
        print ("Deleted "+str(count)+" zero byte files from "+dirpath)
            