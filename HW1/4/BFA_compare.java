import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class BFA_compare
{
    // Fields
    private File dir; // Name of the dirctory containing files to be analyzed
    private File bfaFingerprint;
    private double[] fingerprint = new double[256];
    private double[] fileSignature = new double[256];
        
    // Constructor
    public BFA_compare(String jsonPath,String dirPath) throws IllegalArgumentException
    {
        // Ensure paths are valid
        if (jsonPath == null || dirPath == null)
        {
            System.out.println("Path not specified!");
            throw new IllegalArgumentException();
        }
        
        // Ensure directory exists
        dir = new File(dirPath);
        if (!dir.isDirectory())
        {
            System.out.println("Directory "+dirPath+" not found!");
            throw new IllegalArgumentException();
        }
        
        // Ensure json file exists
        bfaFingerprint = new File(jsonPath);
        if (!bfaFingerprint.isFile())
        {
            System.out.println("Json file "+jsonPath+" not found!");
            throw new IllegalArgumentException();
        }
    }

    private void readJson()
    {
        // Read json file line by line and store finger print
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(bfaFingerprint));
            // Initialize the fingerprint by reading from json file
            // Regex for "Byte-0": 0.8689165960011597,
            String regex = "\"Byte-(\\d+)\":\\s(\\d[\\.]\\d+)";
            String line = "";
            
            while ((line = br.readLine()) != null)
            {
                //fingerprint[i] = 0;
                Pattern p = Pattern.compile(regex);
                //  get a matcher object
                Matcher m = p.matcher(line);
                if (m.find() && m.groupCount() == 2) 
                {
                    int index = Integer.parseInt(line.substring(m.start(1), m.end(1)));
                    if (index >= 0 && index <= 255)
                    {
                        fingerprint[index] = Double.parseDouble(line.substring(m.start(2), m.end(2)));
                    }
                    //System.out.println("From "+line+" matched: "+line.substring(m.start(1), m.end(1)) );
                    //System.out.println("From "+line+" matched: "+line.substring(m.start(2), m.end(2) );
                } 
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("File not found: "+bfaFingerprint.getName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException reading file: "+bfaFingerprint.getName());
        }
    }
    
    private void countByteFreq(File f)
    {
        // Reset data
        for(int i=1;i<256;i++)
            fileSignature[i] = 0;
            
        FileInputStream in = null;
        try 
        {
            in = new FileInputStream(f);
            int b;
            while ((b = in.read()) != -1)
                fileSignature[b] += 1;
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
    }
    
    private void normalizeAndCompand()
    {
        double max = 0;
        double A = 87.6;
        double A_inv = 0.0114155251141553;
        double A_denom = (double) 1.0 + Math.log(A);
    
        // Find max
        for (int i=0;i<256;i++)
        {
            if (max < fileSignature[i])
                max = fileSignature[i];
        }
        
        // Normalize
        for (int i=0;i<256;i++)
        {            
            fileSignature[i] = (double) fileSignature[i]/max;
        }
                
        // Companding
        for (int i=0;i<256;i++)
        {
            double temp = fileSignature[i];
            if (temp < A_inv)            
                temp = (A*temp)/A_denom;
            else            
                temp = (1+Math.log(A*temp))/A_denom;
            fileSignature[i] = temp;
        }
    }
    
    private void compareSignature()
    {
        double score = 0;
        //for (int i=0;i<256;i++)
            //System.out.println(fingerprint[i]+" , "+fileSignature[i]);
        
        for(int i=0;i<256;i++)
        {
            score += Math.abs(fingerprint[i] - fileSignature[i])/256;
        }
        score = 1 - score;
        System.out.println("Similarity score is : "+score);
    }
    
    private void computeBFAcorelation()
    {
        // Get finger print from json
        readJson();
        
        for (File f: dir.listFiles())
        {
            System.out.println("Processing file: "+f.getName());
            
            // Step1: Count byte frequency
            countByteFreq(f);
            
            // Step2: Normalize and apply companding
            normalizeAndCompand();
            
            // Step3: Compare signature with fingerprint passed from Json
            compareSignature();
        }
    }
    
    // Tester main method
    public static void main(String args[])
    {
        String basedir = "C:\\stuff\\Git\\csci599-12\\HW1\\4\\";
        
        // Try MIME Type 1 directory e.g. PDF
        BFA_compare mimeBfa = new BFA_compare(basedir+"pdf.json",basedir+"pdf2");       
        mimeBfa.computeBFAcorelation();
    }    
}