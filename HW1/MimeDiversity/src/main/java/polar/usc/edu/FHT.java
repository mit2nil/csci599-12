package polar.usc.edu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
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
		 // Initialize the signatures to default zero       
    	fingerprint_4 = new double[4][256];
    	fingerprint_8 = new double[8][256];
    	fingerprint_16 = new double[16][256];
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
	
	public boolean computeFHT(){
		boolean status = true;
		int count = 0; //Tracking Number of Files
		if(type.equals(DIRECTORY)){
	
			for (File f: path.listFiles())
	        {
				if(f.getName().equals(".DS_Store")){
					continue;
				}
//	            System.out.println("Processing file: "+f.getName());
	            
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
            dumpJSON("DIRECTORY");
            
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
	
	
	
	private void dumpJSON(String type) {
		// TODO Auto-generated method stub
		ObjectMapper o = new ObjectMapper();
		JsonNode n = o.valueToTree(fingerprint_16);
		FileWriter f=null;
		try {
			int i;
			String mimeType;
			if (Detect.OS.contains("windows"))
			{
				i  = path.getAbsolutePath().lastIndexOf("\\");
				mimeType = path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf("\\")+1);
			}
			else
			{
				i  = path.getAbsolutePath().lastIndexOf("/");
				mimeType = path.getAbsolutePath().substring(path.getAbsolutePath().lastIndexOf("/")+1);
			}

			String test = path.getAbsolutePath().substring(0,path.getAbsolutePath().indexOf(mimeType))+"FHT_fingerprint_"+mimeType+".json";
			f = new FileWriter(test);
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
	
	//input mainFolder: Path to the main folder that contains all fingerprints, 
	//input path: path to the folder/files that need to be compared with existing fingerprint
	//mimeType: if known compare to only that mime fingerprint else if unknown compare to all obtained from mainFolder
	public void detectFileScore(String mainFolder, String path, String mimeType){
		File unknown = new File(path);
		File mainDirectory = new File(mainFolder);
		FHT mimeType_fingerprint=null;
		StringBuilder x = new StringBuilder("[");
		if(!mimeType.equals("unknown")){
			
			try {
				mimeType_fingerprint = new FHT(mainDirectory.getAbsolutePath() + "/FHT_fingerprint_" + mimeType +".json");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(unknown.isDirectory()){
				FileWriter score=null;
				for(File f : unknown.listFiles()){
					
					if(f.getName().contains(".DS_Store") || f.getName().endsWith(".json")){
						continue;
					}
					x.append("{\"id\":\"" + f.getName() +"\",");
					FHT file_fingerprint = new FHT(f.getAbsolutePath(), "FILE");
					file_fingerprint.computeFHT();
					ObjectMapper o = new ObjectMapper();
					JsonNode n = o.valueToTree(file_fingerprint.fingerprint_16);
					
					try {
						x.append("\"fingerprint\":" + o.writeValueAsString(n) +"," );
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					x.append(mimeType_fingerprint.computeWeightedCorrelation(file_fingerprint) + "},");
				}
				try {
					score = new FileWriter(mainDirectory.getAbsolutePath() + "/FHT_solution_" + mimeType + ".json");

					x.deleteCharAt(x.length()-1);
					score.write(x.toString()+"]");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					if(score!=null){
						try {
							score.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
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
					try {
						mimeType_fingerprint = new FHT(f1.getAbsolutePath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
	
	public String computeWeightedCorrelation(FHT file_fingerprint) {
		// TODO Auto-generated method stub
		double cg,g,score;
		cg=0;
		g=0;
		String x = "";
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
		x += "\"4\":" + score + ",";
		//System.out.println("4ByteFHT Score: "+score);
		
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
		x += "\"8\":" + score + ",";
//		System.out.println("8ByteFHT Score: "+score);
		
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
		x += "\"16\":" + score;
//		System.out.println("16ByteFHT Score: "+score);
		
		return x;
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