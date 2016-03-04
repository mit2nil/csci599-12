fi = open('edit.csv','r')
fo = open('edit1.csv','w') 
for line in fi:
    line = line.replace(" ","_")
    #line = ","+line+","
    fo.write(line)