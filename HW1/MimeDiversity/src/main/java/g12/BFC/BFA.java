package g12.BFC;

import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
  
public class BFA
{
    // Fields
    protected File dir; // Name of the directory containing files to be analyzed
    protected long[] signatures = new long[256];
    protected double[] normalizedSignatures = new double[256];
    protected double[] tempNormalizedSignatures = new double[256];
    
    // Constructor
    public BFA(String path) throws IllegalArgumentException 
    {
        // Ensure path is valid
        if (path == null)
        {
            System.out.println("Path not specified!");
            throw new IllegalArgumentException();
        }
        
        // Initialize the signatures to zero
        for(int i=0;i<256;i++)
        {   signatures[i] = 0;
            normalizedSignatures[i] = 0;
            tempNormalizedSignatures[i] = 0;
        }
        dir = new File(path);
    }
    
    // Computation flow for Byte Frequency analysis for set of files
    protected boolean computeBFA(Boolean flag)
    {
        boolean status = true;
        int count = 0; // Tracking number of files
        if(!dir.isDirectory()){
        	return BFA_file(dir);
        }
        System.out.println(dir);
        
        // Repeat the process for every file in a directory
        for (File f: dir.listFiles())
        {
            System.out.println("Processing file: "+f.getName());
            
            // Step-1
            status = computeByteFrequency(f);
            if (!status)
            {
                System.out.println("Byte Frequency Computation failed!");
            }
            
            // Step-2
            status = normalize();
            if (!status)
            {
                System.out.println("Normalization failed!");
            }
            
            // Step-3
            // Average the normalized frequency
            if (count != 0)
            {
                for(int i=0;i<256;i++)
                {
                    normalizedSignatures[i] += tempNormalizedSignatures[i];
                    normalizedSignatures[i] = normalizedSignatures[i]/2;
                }
            }
            else
            {
               for(int i=0;i<256;i++)
                {
                    normalizedSignatures[i] = tempNormalizedSignatures[i];
                }
            }
            //printFqs();
            count++;
        }
        
        // Output final signatures to Json
        if(flag)
        dumpJson();
        return status;
    }
    
    // Count byte frequency for each files in the directory
    // Assumption: all files belong to same file type
    protected boolean computeByteFrequency(File f)
    {
        boolean status = true;
        FileInputStream in = null;
        try 
        {
            in = new FileInputStream(f);
            int b;
            while ((b = in.read()) != -1){
            	signatures[b] += 1;
             //	System.out.println(signatures[b]);
            }
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
        
        // Right now always returning true. 
        // Failing to read one file does not mean entire function failed.
        // Modify later if required
        return status;
    }
    
    // Normalize the frequency distribution
    // Divide every value by maximum frequency to normalize in range 0-1
    protected boolean normalize()
    {
        boolean status = true;
        long max = 0;
        
        // Find max
        for (int i=0;i<256;i++)
        {
            if (max < signatures[i])
                max = signatures[i];
        }
        
        // Normalize
        for (int i=0;i<256;i++)
        {            
            tempNormalizedSignatures[i] = (float) signatures[i]/max;
        }
                
        // Apply a-law
        double A = 87.6;
        double A_inv = 0.0114155251141553;
        double A_denom = (double) 1.0 + Math.log(A);
        for (int i=0;i<256;i++)
        {
            double temp = tempNormalizedSignatures[i];
            if (temp < A_inv)            
                temp = (A*temp)/A_denom;            
            else            
                temp = (1+Math.log(A*temp))/A_denom;
            tempNormalizedSignatures[i] = temp;
        }
        
        // Right now always returning true. 
        // Modify later if required
        return status;
    }
    
    protected void printFqs()
    {
        for (int i=0;i<256;i++)
        {
            
             System.out.print(normalizedSignatures[i]+" ");
             System.out.println("");
                
    }
    }
    
    protected void dumpJson()
    {
    	//printFqs();
        JsonWriter jsonWriter = null;
        try 
        {
        	String file = dir.getAbsolutePath().substring(0,dir.getAbsolutePath().lastIndexOf("/")) + "/BFA_" +  dir.getAbsolutePath().substring(dir.getAbsolutePath().lastIndexOf("/")+1) + ".json";
        	
            jsonWriter = new JsonWriter(new FileWriter(file));
            jsonWriter.setIndent("    ");
            jsonWriter.beginObject();
            jsonWriter.name("property");
            jsonWriter.beginArray();
            jsonWriter.beginObject();
            jsonWriter.name("companding");
            jsonWriter.value("True");
            jsonWriter.name("compandingAlgorithm");
            jsonWriter.value("A-law");
            jsonWriter.endObject();
            jsonWriter.endArray();
            jsonWriter.name("BFA Signatures");
            jsonWriter.beginArray();
            jsonWriter.beginObject();
            for (int i=0;i<256;i++)
            {
                jsonWriter.name("Byte-"+i);
                jsonWriter.value(String.format("%.10f", normalizedSignatures[i]));               
            }
            jsonWriter.endObject();
            jsonWriter.endArray();            
            jsonWriter.endObject();            
        } 
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException writing Json file: ");
        }
        finally
        {
            try 
            {
                jsonWriter.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
                System.out.println("IOException closing Json file: ");
            }
        }
    }
    
    protected boolean BFA_file(File f){
    	System.out.println("TTTTT Processing file: "+f.getName());
        // Step-1
        boolean status = computeByteFrequency(f);
        if (!status)
        {
            System.out.println("Byte Frequency Computation failed!");
        }
        
        // Step-2
        status = normalize();
        if (!status)
        {
            System.out.println("Normalization failed!");
        }
        
        // Step-3
        // Average the normalized frequency

           for(int i=0;i<256;i++)
            {
                normalizedSignatures[i] = tempNormalizedSignatures[i];
            }
        return status; 

    }
    
    
	protected static void dumpJson(double[] arr,String mimename, File dir)
    {
		
        JsonWriter jsonWriter = null;
        try 
        {
        	String file = dir.getAbsolutePath().substring(0,dir.getAbsolutePath().lastIndexOf("/")) + "/BFA100_" + mimename + ".json";
        	System.out.println("XXXXXX" + file);
        	jsonWriter = new JsonWriter(new FileWriter(file));
            jsonWriter.setIndent("    ");
            jsonWriter.beginObject();
            jsonWriter.name("property");
            jsonWriter.beginArray();
            jsonWriter.beginObject();
            jsonWriter.name("companding");
            jsonWriter.value("True");
            jsonWriter.name("compandingAlgorithm");
            jsonWriter.value("A-law");
            jsonWriter.endObject();
            jsonWriter.endArray();
            jsonWriter.name("BFA Signatures");
            jsonWriter.beginArray();
            jsonWriter.beginObject();
            for (int i=0;i<256;i++)
            {
                jsonWriter.name("Byte-"+i);
                jsonWriter.value(arr[i]);               
            }
            jsonWriter.endObject();
            jsonWriter.endArray();            
            jsonWriter.endObject();            
        } 
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException writing Json file: ");
        }
        finally
        {
            try 
            {
                jsonWriter.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
                System.out.println("IOException closing Json file: ");
            }
        }
    }
    
}
 

