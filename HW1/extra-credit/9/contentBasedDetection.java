import org.apache.tika.Tika;
import org.apache.tika.detect.NNExampleModelDetector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import java.io.*;

public class contentBasedDetection 
{
    public static void main( String[] args )
    {
        Tika tk1 = new Tika();
        NNExampleModelDetector tk2 = new NNExampleModelDetector();    // Content based detector
        try
        {
            String dirpath = "C:\\stuff\\Git\\csci599-12\\HW1\\extra-credit\\9\\";
            String mime = "data\\sample";
            String modelName = "tika.model";
          
            File dir = new File(dirpath+mime);
            for (File f: dir.listFiles())
            {
                //System.out.println("Traversing: "+f);
                if(!f.isDirectory())
                {
                    String contentType = tk1.detect(f);
                    System.out.println("Tika detected with default detector: "+contentType);
                    
                    tk2.loadDefaultModels(new File(dirpath+modelName));
                    Metadata m = new Metadata();
                    contentType = tk2.detect(TikaInputStream.get(f, m),m).toString();
                    System.out.println("Tika detected with content based detector: "+contentType);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException occured");
        }
    }
    
    private static float[] getBFA(File f)
    {
        float[] signatures = new float[256];
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
        float max = 0;
        
        // Find max
        for (int i=0;i<256;i++)
        {
            if (max < signatures[i])
                max = signatures[i];
        }
        
        // Normalize and write as a string
        for (int i=0;i<256;i++)
            signatures[i] = (float) signatures[i]/max;
        
        return signatures;
    }
}
