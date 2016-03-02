// Usage javac splitter.java and "java splitter <relative dir name e.g. test>

import java.io.*;

public class splitter
{
    public static void main(String[] args)
    {
        // Ensure directory exists
        File dir = new File(args[0]);
        System.out.println(dir.getAbsolutePath());

        for (File mimedir: dir.listFiles())
        {
            if (!(mimedir != null && mimedir.isDirectory()))
            {
                continue;
            }
            
            // Store number of files for split
            int count = mimedir.listFiles().length; // Tracking number of files
            int initialCount = count;
            System.out.println("Total files, 75%, 25% "+initialCount+"-"+initialCount*0.75+"-"+initialCount*0.25);
            
            // Create two sub dir
            File dir75 = new File(mimedir.getAbsolutePath()+"\\"+mimedir.getName());
            File dir25 = new File(mimedir.getAbsolutePath()+"\\"+mimedir.getName()+"_25");
            
            dir75.mkdir();
            dir25.mkdir();
            
            System.out.println(dir75.getAbsolutePath());
            System.out.println(dir25.getAbsolutePath());
            
            // Move files
            for (File f:mimedir.listFiles())
            {
                if (f.isDirectory())
                    continue;
                
                if (count > initialCount*0.25)
                {
                    //System.out.println("Moving file "+f.getAbsolutePath()+" to "+dir75.getAbsolutePath()+"\\"+f.getName());
                    f.renameTo(new File(dir75.getAbsolutePath()+"\\"+f.getName()));
                }
                else
                {
                    //System.out.println("Moving file "+f.getAbsolutePath()+" to "+dir25.getAbsolutePath()+"\\"+f.getName());
                    f.renameTo(new File(dir25.getAbsolutePath()+"\\"+f.getName()));
                }
                count--;
            }
        }
    }
}