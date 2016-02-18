package g12.PreProcessPolar;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        System.out.println( "Hello World!" );
//		String inputFolderPath = "/Users/manali/599/ass1/polar-fulldump/";
//		String outputFolderPath = "/Users/manali/599/ass1/mime-types/";
		String inputFolderPath = "/Users/manali/599/ass1/team1-data/input/";
		String outputFolderPath = "/Users/manali/599/ass1/team1-data/output/";
		long st1 = System.currentTimeMillis();
		File inputFolder = new File(inputFolderPath);
		File outputFolder = new File(outputFolderPath);
		
		File[] listOfFiles = inputFolder.listFiles();
		System.out.println("total files:" + listOfFiles.length);
		System.out.println("Time taken:" + (System.currentTimeMillis()-st1));
		Tika tika = new Tika();
		File output;
		for(File file: listOfFiles){
			if(file.isFile() && !file.getName().startsWith(".DS_")){
				String contentType = tika.detect(file);
				contentType = contentType.replace("/", "_");
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
    }
}
