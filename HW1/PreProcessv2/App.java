import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.apache.tika.Tika;
import com.google.gson.stream.JsonWriter;

public class App 
{
    private static HashMap<String,Integer> mimetypes = new HashMap<String,Integer>();
    private static File inputFolder;
    private static File outputFolder;
    private static Tika tk = new Tika();
    
    public static void main( String[] args )
    {
        try
        {
            String inputFolderPath = "C:\\stuff\\Git\\csci599-12\\HW1\\PreProcessv2\\in\\";
            String outputFolderPath = "C:\\stuff\\Git\\csci599-12\\HW1\\PreProcessv2\\out\\";
            inputFolder = new File(inputFolderPath);
            outputFolder = new File(outputFolderPath);
            
            // Run Tika
            detectTika(inputFolder);
            
            // Dump to Json
            JsonWriter jsonWriter = null;
            jsonWriter = new JsonWriter(new FileWriter(outputFolder.getAbsolutePath()+"\\mime_diversity.json"));
            jsonWriter.setIndent("    ");
            jsonWriter.beginObject();
            jsonWriter.name("Mime_diversity");
            jsonWriter.beginArray();
            jsonWriter.beginObject();
            Set<String> itr = mimetypes.keySet();
            for(String s: itr)
            {
                jsonWriter.name(s);
                jsonWriter.value(mimetypes.get(s).toString());
            }
            jsonWriter.endObject();
            jsonWriter.endArray();            
            jsonWriter.endObject();   
            jsonWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException occured");
        }
        finally 
        {
            System.out.println("IOException occured");
        }
    }
    
    private static void detectTika(File dir)
    {
        try
        {
            for (File f: dir.listFiles())
            {
                //System.out.println("Traversing: "+f);
                if(f.isDirectory())
                {
                    // Recursiely traverse
                    detectTika(f);
                }
                else
                {
                    String contentType = tk.detect(f);
                    //System.out.println("Tika detected: "+contentType);
                    contentType = contentType.replace("/", "_");
                    
                    if(mimetypes.containsKey(contentType))
                    {
                        mimetypes.put(contentType, (mimetypes.get(contentType)+1));
                    }
                    else
                    {
                        mimetypes.put(contentType,1);
                    }
                    
                    // Create mime type directory 
                    File output = new File(outputFolder.getAbsolutePath() +"\\" + contentType);
                    if(!output.isDirectory())
                    {
                        output.mkdir();
                    }
                    
                    // Move file to mime directory
                    String fileName = f.getName();
                    f.renameTo(new File(output.getAbsolutePath()+"\\" + fileName));
                    //System.out.println("Moved "+f.getAbsolutePath()+" to "+output.getAbsolutePath()+"\\" + fileName);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException occured");
        }
        finally 
        {
            System.out.println("IOException occured");
        }
    }
}
