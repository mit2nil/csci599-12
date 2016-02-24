package g12.assignment1;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;

public class PreProcess_TRECDD {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String inputFolderPath = "/Users/manali/599/ass1/polar-fulldump/";
//		String outputFolderPath = "/Users/manali/599/ass1/mime-types/";
		String inputFolderPath = "/Volumes/Kshah/Pending";
		String outputFolderPath = "/Users/manali/599/ass1/team1-data/output";
		long st1 = System.currentTimeMillis();
		File inputFolder = new File(inputFolderPath);
		File outputFolder = new File(outputFolderPath);
		
		File[] listOfFiles = inputFolder.listFiles();
		System.out.println("total files:" + listOfFiles.length);
		System.out.println("Time taken:" + (System.currentTimeMillis()-st1));
		Tika tika = new Tika();
		
		for(File file: listOfFiles){
			if(file.isFile() && !file.getName().startsWith(".DS_")){
				String contentType = tika.detect(file);
				contentType = contentType.replace("/", "_");
				File output = new File(outputFolder.getAbsolutePath() + contentType);
				if(!output.isDirectory()){
					output.mkdir();
				}
				String fileName= file.getName();
				file = file.getAbsoluteFile();
				if(!file.renameTo(new File(output.getAbsoluteFile() + fileName))){
					System.out.println("File not moved to folder" + output.getAbsolutePath());
				}
			}
		}
	}

}
