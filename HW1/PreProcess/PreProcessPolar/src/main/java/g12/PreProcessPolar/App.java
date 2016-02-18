package g12.PreProcessPolar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.apache.tika.Tika;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        System.out.println( "Hello World!" );
		String inputFolderPath = "/Users/manali/599/ass1/polar-fulldump/input";
		String outputFolderPath = "/Users/manali/599/ass1/polar-fulldump/output";
//		String inputFolderPath = "/Users/manali/599/ass1/team1-data/input/";
//		String outputFolderPath = "/Users/manali/599/ass1/team1-data/output/";
		long st1 = System.currentTimeMillis();
		File inputFolder = new File(inputFolderPath);
		File outputFolder = new File(outputFolderPath);
		
		File[] listOfFiles = inputFolder.listFiles();
		System.out.println("total files:" + listOfFiles.length);
		System.out.println("Time taken to read filelist:" + (System.currentTimeMillis()-st1));
		st1 = System.currentTimeMillis();
		Tika tika = new Tika();
		System.out.println("Time taken to load Tika:" + (System.currentTimeMillis()-st1));
		st1 = System.currentTimeMillis();
		HashMap<String,Integer> mimetypes = new HashMap<String,Integer>();
		
		File output;
		int i = 0;
		for(File file: listOfFiles){
			if(file.isFile() && !file.getName().startsWith(".DS_")){
				System.out.println(i++);
				String contentType = tika.detect(file);
				contentType = contentType.replace("/", "_");
				if(mimetypes.containsKey(contentType)){
					mimetypes.put(contentType, (mimetypes.get(contentType)+1));
				}
				else{
					mimetypes.put(contentType,1);
				}
				output = new File(outputFolder.getAbsolutePath() +"/" + contentType);
				if(!output.isDirectory()){
					output.mkdir();
				}
				String fileName= file.getName();
				file = file.getAbsoluteFile();
				if(!file.renameTo(new File(output.getAbsoluteFile() +"/" + fileName))){
					System.out.println(fileName + " File not moved to folder: " + output.getAbsolutePath());
				}
			}
		}
		
		System.out.println("Time taken to detect:" + (System.currentTimeMillis()-st1));
		st1 = System.currentTimeMillis();
		FileWriter f = new FileWriter(new File(outputFolder.getAbsolutePath() + "/" + "mimetypesStatistics.json"));
		
		String mapAsJson = new ObjectMapper().writeValueAsString(mimetypes);
        System.out.println(mapAsJson);
		f.write(mapAsJson);
		f.close();
		System.out.println("Pushed statistics to json file in:" + (System.currentTimeMillis()-st1));
    }
}
