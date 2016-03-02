package g12.BFC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FHT {
	
	 // Fields
    private File path; // Name of the directory containing files to be analyzed
    private double[][] fingerprint_4;
    private double[][] fingerprint_8;
    private double[][] fingerprint_16;
	private final String DIRECTORY = "DIRECTORY";
	private final String FILE = "FILE";
	private String type;
	private int totalFiles=0;

	public FHT(String jsonDump) throws IOException{
		String json = new String(Files.readAllBytes(Paths.get(jsonDump)));
		// Create a Jackson mapper
		ObjectMapper mapper = new ObjectMapper();

		// Map the JSON to a 2D array
		fingerprint_16 = mapper.readValue(json, double[][].class);
		for(int i=0; i<8; i++){
			if(i<4){
				fingerprint_4[i] = fingerprint_16[i];
				fingerprint_8[i] = fingerprint_16[i];
			}
			else{
				fingerprint_8[i] = fingerprint_16[i];
			}
		}
		
	}
    public FHT(String filepath, String type) throws IllegalArgumentException 
    {
        // Ensure path is valid
        if (filepath == null)
        {
            System.out.println("Path not specified!");
            throw new IllegalArgumentException();
        }
        this.type = type;
        if(type.equals(DIRECTORY)){
        	// Ensure directory exists
	        path = new File(filepath);
	        if (!path.isDirectory())
	        {
	            System.out.println("Directory "+filepath+" not found!");
	            throw new IllegalArgumentException();
	        }
        }
        else if(type.equals(FILE)){
        	// Ensure File exists
	        path = new File(filepath);
	        if (!path.isFile())
	        {
	            System.out.println("File "+filepath+" not found!");
	            throw new IllegalArgumentException();
	        }
        }
        // Initialize the signatures to default zero       
    	fingerprint_4 = new double[4][256];
    	fingerprint_8 = new double[8][256];
    	fingerprint_16 = new double[16][256];
        
    }
	
	private boolean computeFHT(){
		boolean status = true;
		int count = 0; //Tracking Number of Files
		if(type.equals(DIRECTORY)){
	
			for (File f: path.listFiles())
	        {
				if(f.getName().equals(".DS_Store")){
					continue;
				}
	            System.out.println("Processing file: "+f.getName());
	            
	            // Step-1
	            status = computeFrequencyHeader(f,count);
	            if (!status)
	            {
	                System.out.println("Frequency Header Trailer Computation failed! " + f.getName());
	            }
	            count++;
	        }
		    totalFiles = count;
			//Step-2
            //Co-relation strength is the maximum byte valye for each byte vector
//            calculateCorrelation();
            
            //Step-3
            dumpJSON();
            
		}
		else{
			File f = path;
			System.out.println("Processing file: "+f.getName());
	            	
            // Step-1
            status = computeFrequencyHeader(f,count);
            if (!status)
            {
                System.out.println("Frequency Header Trailer Computation failed! " + f.getName());
            }
            
            
		}
		return status;
		
	}
	
	
	
	private void dumpJSON() {
		// TODO Auto-generated method stub
		ObjectMapper o = new ObjectMapper();
		JsonNode n = o.valueToTree(fingerprint_16);
		FileWriter f=null;
		try {
			int i  = path.getAbsolutePath().lastIndexOf("/");
			String mimeType = path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf("/")+1);
			String test = path.getAbsolutePath().substring(0,i)+"FHT_fingerprint_+"+mimeType+".json";
			System.out.println(test);
			f = new FileWriter(path.getAbsolutePath().substring(0,i)+"FHT_fingerprint_+"+mimeType+".json");
			f.write(o.writeValueAsString(n));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			 try
	            {
	                if (f != null)
	                    f.close();
	                System.out.println("Dumped results in json file");
	            }
	            catch(IOException e)
	            {
	                 e.printStackTrace();
	                 System.out.println("IOException closing jsonfile");
	            }
		}
		
	}

	private double getMaxCorrelation(double [][]x,int i){
		double max;
        max = Double.MIN_VALUE;
    
    	for(int j=0; j<x[i].length; j++){
    		if(x[i][j]>max){
    			max = x[i][j];
    		}
    	}
  
		return max;
	}

	private boolean computeFrequencyHeader(File f, int count) {
		// TODO Auto-generated method stub
		boolean status = true;
		 FileInputStream input = null;
        try 
        {
            input = new FileInputStream(f);
            int byteValue;
            for(int i = 0; i<fingerprint_16.length; i++){
				if((byteValue=input.read())!=-1){
					if(i<4){
						fingerprint_4[i][byteValue] = (count*fingerprint_4[i][byteValue] + 1)/(count+1);
						fingerprint_8[i][byteValue] = (count*fingerprint_8[i][byteValue] + 1)/(count+1);
						fingerprint_16[i][byteValue] = (count*fingerprint_16[i][byteValue] + 1)/(count+1);
					}
					else if(i>3 && i<8){
						fingerprint_8[i][byteValue] = (count*fingerprint_8[i][byteValue] + 1)/(count+1);
						fingerprint_16[i][byteValue] = (count*fingerprint_16[i][byteValue] + 1)/(count+1);
					}
					else{
						fingerprint_16[i][byteValue] = (count*fingerprint_16[i][byteValue] + 1)/(count+1);
					}
					
				}
				else{
					if(i<4){
						for(int j=0; j<fingerprint_4[i].length; j++){
							fingerprint_4[i][j] = -1;
						}
						
						for(int j=0; j<fingerprint_8[i].length; j++){
							fingerprint_8[i][j] = -1;
						}
						
						for(int j=0; j<fingerprint_16[i].length; j++){
							fingerprint_16[i][j] = -1;
						}
					}
					else if(i>3 && i<8){
						
						for(int j=0; j<fingerprint_8[i].length; j++){
							fingerprint_8[i][j] = -1;
						}
						
						for(int j=0; j<fingerprint_16[i].length; j++){
							fingerprint_16[i][j] = -1;
						}
					}
					else{
						for(int j=0; j<fingerprint_16[i].length; j++){
							fingerprint_16[i][j] = -1;
						}
					}
					break;
					
					
					
					
				}
			}
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException reading file: "+f.getName());
            status = false;
        }
        finally 
        {
            try
            {
                if (input != null)
                    input.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
                System.out.println("IOException closing file: "+f.getName());
                status = false;
            }
        }
        
        // Right now always returning true. 
        // Failing to read one file does not mean entire function failed.
        // Modify later if required
		return status;
	}
	

	public static void main(String m[]) throws IOException{
		long st1 = System.currentTimeMillis();
		//test json constructor
		FHT mm = new FHT("/Users/manali/599/ass1/polar-fulldump/demoFHT/image_gif/FHT_fingerprint_image_gif.json");
		
		
		
		
		
		
		//Calculate frequency header for a particular mime type
		FHT gif_fingerprint = new FHT("/Users/manali/599/ass1/manali/image_gif", "DIRECTORY");
		gif_fingerprint.computeFHT();
		System.out.println("Generated 16-Byte FHT Fingerprint in: " + (System.currentTimeMillis()-st1));
		st1 = System.currentTimeMillis();
		
		
		//Compare with all unknown files to determine
		File unknown = new File("/Users/manali/599/ass1/polar-fulldump/demoFHT/unknown_type/");
		
		for(File f : unknown.listFiles()){
			if(f.getName().contains(".DS_Store")){
				continue;
			}
			FHT file_fingerprint = new FHT(f.getAbsolutePath(), "FILE");
			file_fingerprint.computeFHT();
			gif_fingerprint.computeWeightedCorrelation(file_fingerprint);
		}
	
		System.out.println("Generated FHT scores in: " + (System.currentTimeMillis()-st1));
	}
	
	//input mainFolder: Path to the main folder that contains all fingerprints, 
	//input path: path to the folder/files that need to be compared with existing fingerprint
	//mimeType: if known compare to only that mime fingerprint else if unknown compare to all obtained from mainFolder
	public static void detectFileScore(String mainFolder, String path, String mimeType) throws IOException{
		File unknown = new File(path);
		File mainDirectory = new File(mainFolder);
		FHT mimeType_fingerprint;
		if(!mimeType.equals("unkown")){
			
			mimeType_fingerprint = new FHT(mainDirectory.getAbsolutePath() + "/FHT_fingerprint_" + mimeType);
			if(unknown.isDirectory()){
				for(File f : unknown.listFiles()){
					if(f.getName().contains(".DS_Store")){
						continue;
					}
					FHT file_fingerprint = new FHT(f.getAbsolutePath(), "FILE");
					file_fingerprint.computeFHT();
					mimeType_fingerprint.computeWeightedCorrelation(file_fingerprint);
				}
			}
			else if(unknown.isFile()){
				FHT file_fingerprint = new FHT(unknown.getAbsolutePath(), "FILE");
				file_fingerprint.computeFHT();
				mimeType_fingerprint.computeWeightedCorrelation(file_fingerprint);
			}
		}
		else{
			
			for(File f1: mainDirectory.listFiles()){
				
				if(f1.isFile() && !f1.getName().contains(".DS_Store") && f1.getName().startsWith("FHT_fingerprint")){
					mimeType_fingerprint = new FHT(f1.getAbsolutePath());
					if(unknown.isDirectory()){
						for(File f : unknown.listFiles()){
							if(f.getName().contains(".DS_Store")){
								continue;
							}
							FHT file_fingerprint = new FHT(f.getAbsolutePath(), "FILE");
							file_fingerprint.computeFHT();
							mimeType_fingerprint.computeWeightedCorrelation(file_fingerprint);
						}
					}
					else if(unknown.isFile()){
						FHT file_fingerprint = new FHT(unknown.getAbsolutePath(), "FILE");
						file_fingerprint.computeFHT();
						mimeType_fingerprint.computeWeightedCorrelation(file_fingerprint);
					}
				}
			}
		}
		
	}
	
	private void computeWeightedCorrelation(FHT file_fingerprint) {
		// TODO Auto-generated method stub
		double cg,g,score;
		cg=0;
		g=0;
				
		for(int i=0; i<file_fingerprint.fingerprint_4.length; i++){
			int j;
			for(j= 0; j<file_fingerprint.fingerprint_4[i].length; j++){
				if(file_fingerprint.fingerprint_4[i][j] == 1.0){
					break;
				}
			}
			cg+=fingerprint_4[i][j];
			g+=getMaxCorrelation(fingerprint_4, i);
		}
		score=cg/g;
		System.out.println("4ByteFHT Score: "+score);
		
		cg=0;
		g=0;
				
		for(int i=0; i<file_fingerprint.fingerprint_8.length; i++){
			int j;
			for(j= 0; j<file_fingerprint.fingerprint_8[i].length; j++){
				if(file_fingerprint.fingerprint_8[i][j] == 1.0){
					break;
				}
			}
			cg+=fingerprint_8[i][j];
			g+=getMaxCorrelation(fingerprint_8, i);
		}
		score=cg/g;
		System.out.println("8ByteFHT Score: "+score);
		
		cg=0;
		g=0;
				
		for(int i=0; i<file_fingerprint.fingerprint_16.length; i++){
			int j;
			for(j= 0; j<file_fingerprint.fingerprint_16[i].length; j++){
				if(file_fingerprint.fingerprint_16[i][j] == 1.0){
					break;
				}
			}
			cg+=fingerprint_16[i][j];
			g+=getMaxCorrelation(fingerprint_16, i);
		}
		score=cg/g;
		System.out.println("16ByteFHT Score: "+score);
		
		
	}

	public void display(double a[][]){
		for(int i=0; i<a.length; i++){
			for(int j=0; j<a[i].length; j++){
				System.out.print(a[i][j] + "\t");
			}
			System.out.println();
		}
	}
}