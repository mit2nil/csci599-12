// Usage javac splitter.java and "java splitter <relative dir name e.g. test>

import java.io.*;

public class generateData
{
    public static void main(String[] args)
    {
        // Ensure directory exists
        File dir1 = new File("application_dif+xml");
        //System.out.println(dir.getAbsolutePath());
        File dir0 = new File("application_xhtml+xml");

        int len1 = dir1.listFiles().length;
        int len0 = dir0.listFiles().length;
        int initialCount1 = len1;
        int initialCount0 = len0;
        
                  
        // train.csv
        try 
        {
            // Prepare first row
            String header = "";
            for (int i=0;i<256;i++)
                header = header+"\"V"+(i+1)+"\",";
            header=header+"\"numeric(400) + 1\"\n";

            File file = new File("train.csv");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            
            // write first row
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(header);
            
            System.out.println("len1-initialcount1 values : "+len1+" and "+initialCount1);            
            System.out.println("len0-initialcount0 values : "+len0+" and "+initialCount0); 
            System.out.println("file count in 1 and 0 are : "+dir1.listFiles().length+" and "+dir0.listFiles().length);
            // Write byte frequency of each file (300 from diff+xml)
            for (File f: dir1.listFiles())
            {
                if (len1 > initialCount1-300)
                {
                    String bfa = "";
                    bfa = bfa+getByteFreq(f);
                    bfa=bfa+",1\n";
                    bw.write(bfa);
                }
                len1--;
                
                if (len1 == initialCount1 - 300)
                    break;
            }
            
            // Write byte frequency of each file (100 from xhtml+xml)
            for (File f: dir0.listFiles())
            {
                if (len0 > initialCount0-100)
                {
                    String bfa = "";
                    bfa = bfa+getByteFreq(f);
                    bfa=bfa+",0\n";
                    bw.write(bfa);
                }
                len0--;
                
                if (len0 == initialCount0 - 100)
                    break;
            }
            
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        
        // test.csv
        try 
        {
            // Prepare first row
            String header = "";
            for (int i=0;i<256;i++)
                header = header+"\"V"+(i+1)+"\",";
            header=header+"\"numeric(400) + 1\"\n";

            File file = new File("test.csv");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            
            // write first row
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(header);
            
            System.out.println("len1-initialcount1 values : "+len1+" and "+initialCount1);            
            System.out.println("len0-initialcount0 values : "+len0+" and "+initialCount0); 
            System.out.println("file count in 1 and 0 are : "+dir1.listFiles().length+" and "+dir0.listFiles().length);            
            // Write byte frequency of each file (300 from diff+xml)
            int tempCount = 300;
            for (File f: dir1.listFiles())
            {
                if (tempCount > 0)
                {
                    tempCount--;
                    continue;
                }
                                
                if (len1 > initialCount1-600)
                {
                    String bfa = "";
                    bfa = bfa+getByteFreq(f);
                    bfa=bfa+",1\n";
                    bw.write(bfa);
                }
                len1--;
                
                if (len1 == initialCount1 - 600)
                    break;
            }
            
            // Write byte frequency of each file (100 from xhtml+xml)
            tempCount = 100;
            for (File f: dir0.listFiles())
            {
                
                if (tempCount > 0)
                {
                    tempCount--;
                    continue;
                }
                
                if (len0 > initialCount0-200)
                {
                    String bfa = "";
                    bfa = bfa+getByteFreq(f);
                    bfa=bfa+",0\n";
                    bw.write(bfa);
                }
                len0--;
                
                if (len0 == initialCount0 - 200)
                    break;
            }
            
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        // val.csv
        try 
        {
            // Prepare first row
            String header = "";
            for (int i=0;i<256;i++)
                header = header+"\"V"+(i+1)+"\",";
            header=header+"\"numeric(400) + 1\"\n";

            File file = new File("val.csv");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            
            // write first row
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(header);
            
            System.out.println("len1-initialcount1 values : "+len1+" and "+initialCount1);            
            System.out.println("len0-initialcount0 values : "+len0+" and "+initialCount0); 
            System.out.println("file count in 1 and 0 are : "+dir1.listFiles().length+" and "+dir0.listFiles().length);             
            // Write byte frequency of each file (300 from diff+xml)
            int tempCount = 600;
            for (File f: dir1.listFiles())
            {
                if (tempCount > 0)
                {
                    tempCount--;
                    continue;
                }
                
                if (len1 > initialCount1-900)
                {
                    String bfa = "";
                    bfa = bfa+getByteFreq(f);
                    bfa=bfa+",1\n";
                    bw.write(bfa);
                }
                len1--;
                
                if (len1 == initialCount1 - 900)
                    break;
            }
            
            // Write byte frequency of each file (100 from xhtml+xml)
            tempCount = 200;
            for (File f: dir0.listFiles())
            {
                if (tempCount > 0)
                {
                    tempCount--;
                    continue;
                }
                
                if (len0 > initialCount0-300)
                {
                    String bfa = "";
                    bfa = bfa+getByteFreq(f);
                    bfa=bfa+",0\n";
                    bw.write(bfa);
                }
                len0--;
                
                if (len0 == initialCount0 - 300)
                    break;
            }
            
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
    }
    
    private static String getByteFreq(File f)
    {
        double[] signatures = new double[256];
        String bfa = "";
        FileInputStream in = null;
        
        // Count freq.
        try 
        {
            in = new FileInputStream(f);
            int b;
            while ((b = in.read()) != -1)
                signatures[b] += 1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException reading file: "+f.getName());
        }
        finally 
        {
            try
            {
                if (in != null)
                    in.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
                System.out.println("IOException closing file: "+f.getName());
            }
        }
        
        // normalize it
        double max = 0;
        
        // Find max
        for (int i=0;i<256;i++)
        {
            if (max < signatures[i])
                max = signatures[i];
        }
        
        // Normalize and write as a string
        for (int i=0;i<256;i++)
        {            
            signatures[i] = (double) signatures[i]/max;
            if (i == 0)
                bfa = bfa+String.format("%.10f", signatures[i]);
            else
                bfa = bfa+","+String.format("%.10f", signatures[i]);
        }
        
        return bfa;
    }
}