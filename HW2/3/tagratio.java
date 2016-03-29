import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import com.google.gson.stream.JsonWriter;
import java.util.ArrayList;

public class tagratio
{
    private static File inputFolder;
    private static File outputFolder;
    
    public static void main( String[] args )
    {
        String inputFolderPath = "C:\\stuff\\Git\\csci599-12\\HW2\\3\\sample";
        String outputFolderPath = "C:\\stuff\\Git\\csci599-12\\HW2\\3\\sample\\out\\";
        inputFolder = new File(inputFolderPath);
        outputFolder = new File(outputFolderPath);
        
        // Run Tika's content extraction tool
        extractContent(inputFolder);
        
    }
    
    private static void extractContent(File dir)
    {
        for (File f: dir.listFiles())
        {
            //System.out.println("Traversing: "+f);
            if(f.isDirectory())
            {
                // Recursiely traverse
                //extractContent(f);
            }
            else
            {
                // Get Tika extracted content of a file in raw XML structured format as a string
                String s = extractXMLContent(f);
                
                // Apply tagratio algorithm (CETR) on each line, smooth the TR array and return it.
                tagratioClusters(s,f.getName());
            }
        }
    }
    
    private static String extractXMLContent(File f)
    {
        try
        {
            //File f = new File("abcd");
            StringWriter xmlBuffer = new StringWriter();
            SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
            TransformerHandler handler = factory.newTransformerHandler();
            handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
            handler.setResult(new StreamResult(xmlBuffer));
            
            AutoDetectParser parser = new AutoDetectParser();
            InputStream stream = new FileInputStream(f);
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();

            parser.parse(stream, handler, metadata, parseContext);

            String content = handler.toString();
            //System.out.println(xmlBuffer.toString());
            return xmlBuffer.toString();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            System.out.println("IOException occured");    
        }
        catch (TikaException e) 
        {
            e.printStackTrace();
            System.out.println("TikaException occured");
        }
        catch (SAXException e) 
        {
            e.printStackTrace();
            System.out.println("SAXException occured");
        }
        catch (TransformerConfigurationException e)
        {
            e.printStackTrace();
            System.out.println("TransformerConfigurationException occured");
        }
        
        return "";
    }
    
    private static void tagratioClusters(String s,String jsonName)
    {
        ArrayList<Double> tagratioValue = new ArrayList<Double>();
        ArrayList<String> tagratioLineContent = new ArrayList<String>();

        String[] lines = s.split("\\n");
        for (int i=0; i<lines.length; i++)
        {
            // Ignore empty lines. Comments and script tags are already removed by Tika
            if (lines[i].trim().isEmpty())
                continue;
            
            //System.out.print(lines[i]);
            char[] ca = lines[i].toCharArray();
            boolean tagFlag = false;
            String tempLine = "";
            int x = 0,y = 0;
            for (int j=0;j<ca.length-1;j++)
            {
                //System.out.print(ca[j]);
                if (ca[j] == '<')
                {
                    tagFlag = true;
                    //System.out.println(" -- Found tag beginning");
                }
                
                if (ca[j] == '>')
                {
                    tagFlag = false;
                    y++; // Increment tag count
                    //System.out.println(" -- Found tag ending");
                    continue;
                }
                
                // Ignore as long as tag flag is on
                if (tagFlag)
                    continue;
                
                // count non-tag ASCII characters
                if (ca[j] >= 0 && ca[j] <= 256)
                {
                    tempLine = tempLine+ca[j];
                    x++;
                }
                
                //System.out.println("x: "+x);
                //System.out.println("y: "+y);
            }
            if (y == 0)
            {
                tagratioValue.add(new Double(x));
                //System.out.println("value "+new Double(x));
            }
            else
            {
                tagratioValue.add((double) x/y);
                //System.out.println("value "+(double) x/y);
            }
            tagratioLineContent.add(tempLine);
        }
        //System.out.println();
        
        // Smooth the data and calculate SD to be used as a cutoff threshold
        tagratioValue = smootheTTR(tagratioValue);
        double sd = sdTTR(tagratioValue,meanTTR(tagratioValue));
        //System.out.println("SD is "+sd);
        
        String json = "";
        for (int i=0;i<tagratioValue.size();i++)
        { 
            //System.out.println("value "+i+" is "+tagratioValue.get(i));
            // If value above threshold, store line number in new array.
            if (tagratioValue.get(i) > sd)
            {
                //System.out.println(tagratioLineContent.get(i));
                json = json+tagratioLineContent.get(i).trim()+" ";
            }
        }
        
        // dump the concataned extracted text to json.
        dumpJson(outputFolder.getAbsolutePath()+"\\tagratio_"+jsonName+".json",json);
        //dumpJson(outputFolder.getAbsolutePath()+"\\tagratio.json",json);
    }
    
    private static double meanTTR(ArrayList<Double> values)
    {
        double sum = 0;
        for (int i=0;i<values.size();i++)
            sum += values.get(i);
        return sum/values.size();
    }
    
    private static double sdTTR(ArrayList<Double> values, double mean)
    {
        double sum = 0;
        for (int i=0;i<values.size();i++)
            sum += (values.get(i) - mean)*(values.get(i) - mean);
        return Math.sqrt(sum/values.size());
    }

    private static ArrayList<Double> smootheTTR(ArrayList<Double> values)
    {
        int size = values.size();
        
        // How to do the smoothing? Formula unclear
        double[] smoothedValues = new double[size];
        smoothedValues[0] = values.get(0);
        smoothedValues[1] = values.get(1);
        smoothedValues[size-2] = values.get(size-2);
        smoothedValues[size-1] = values.get(size-1);
        
        for(int i=2;i<size-2;i++)
        {
            smoothedValues[i] = values.get(i-2)+values.get(i-1)+values.get(i)+values.get(i+1)+values.get(i+2);
            smoothedValues[i] = smoothedValues[i]/5;
        }
        
        for(int i=0;i<size;i++)
            values.set(i,smoothedValues[i]);
        
        return values;
    }
    
    private static void dumpJson(String filename, String content)
    {
        try
        {
            JsonWriter jsonWriter = null;
            jsonWriter = new JsonWriter(new FileWriter(filename));
            jsonWriter.setIndent("    ");
            
            jsonWriter.beginObject();
            jsonWriter.name("TTR");
            jsonWriter.beginObject();
            jsonWriter.name("Extracted text");
            jsonWriter.value(content);
            jsonWriter.endObject();
            jsonWriter.endObject();   
            jsonWriter.close();
            
            // Create mime type directory 
            /*
            File output = new File(outputFolder.getAbsolutePath() +"\\" + contentType);
            if(!output.isDirectory())
            {
                output.mkdir();
            }
            
            // Move file to mime directory
            String fileName = f.getName();
            f.renameTo(new File(output.getAbsolutePath()+"\\" + fileName));
            //System.out.println("Moved "+f.getAbsolutePath()+" to "+output.getAbsolutePath()+"\\" + fileName);
            */
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException occured");    
        }
    }
}
