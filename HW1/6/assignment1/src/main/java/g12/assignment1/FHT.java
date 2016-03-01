package g12.assignment1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

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
			f = new FileWriter("/Users/manali/599/ass1/manali/"+"/image_gif_FHT_fingerprint.json");
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
	
//	private void calculateCorrelation() {
//		// TODO Auto-generated method stub
//		double max;
//        int byteValue;
//        max = Double.MIN_VALUE;
//        byteValue=0;
//        for(int i=0; i<fingerprint_4.length; i++){
//        	for(int j=0; j<fingerprint_4[i].length; j++){
//        		if(fingerprint_4[i][j]>max){
//        			max = fingerprint_4[i][j];
//        			byteValue = j;
//        		}
//        	}
//        	fingerprint_4[i] = new double[256];
//        	fingerprint_4[i][byteValue] = 1;
//        }
//		
//        
//        max = Double.MIN_VALUE;
//        byteValue=0;
//        for(int i=0; i<fingerprint_8.length; i++){
//        	for(int j=0; j<fingerprint_8[i].length; j++){
//        		if(fingerprint_8[i][j]>max){
//        			max = fingerprint_8[i][j];
//        			byteValue = j;
//        		}
//        	}
//        	fingerprint_8[i] = new double[256];
//        	fingerprint_8[i][byteValue] = 1;
//        	
//        }
//        
//        
//        max = Double.MIN_VALUE;
//        byteValue=0;
//        for(int i=0; i<fingerprint_16.length; i++){
//        	for(int j=0; j<fingerprint_16[i].length; j++){
//        		if(fingerprint_16[i][j]>max){
//        			max = fingerprint_16[i][j];
//        			byteValue = j;
//        		}
//        	}
//        	fingerprint_16[i] = new double[256];
//        	fingerprint_16[i][byteValue] = 1;
//        	
//        }
//	}

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

	public void computeCorelation(FHT y){
		int tempsum = 0;
		int sum = 0;
		int i,j;
		//4 Byte FHT
		outer:
		for(i= 0; i<fingerprint_4.length; i++){
			tempsum=0;
			for(j=0; j< fingerprint_4[i].length; j++){
				if(y.fingerprint_4[i][j] == -1){
					break outer;
				}
				tempsum += Math.abs(y.fingerprint_4[i][j]-fingerprint_4[i][j]);
			}
			tempsum = tempsum/256;
			sum += tempsum;
		}
	    sum = sum/(4+(i-4));
	    sum = (1-sum) - (i-4)/4;
	    System.out.println("correlation in 4 Byte FHT: " + sum);
	    sum=0;
	    
	    //8 Byte FHT
	    outer2:
  		for(i= 0; i<fingerprint_8.length; i++){
  			tempsum=0;
  			for(j=0; j< fingerprint_8[i].length; j++){
  				if(y.fingerprint_8[i][j] == -1){
  					break outer2;
  				}
  				tempsum += Math.abs(y.fingerprint_8[i][j]-fingerprint_8[i][j]);
  			}
  			tempsum = tempsum/256;
  			sum += tempsum;
  		}
  	    sum = sum/(8+(i-8));
  	    sum = (1-sum) - (i-8)/8;
  	    System.out.println("correlation in 8 Byte FHT: " + sum);
	    sum=0;
	    //16 Byte FHT
  		for(i= 0; i<fingerprint_16.length; i++){
  			tempsum=0;
  			for(j=0; j< fingerprint_16[i].length; j++){
  				tempsum += Math.abs(y.fingerprint_16[i][j]-fingerprint_16[i][j]);
  			}
  			tempsum = tempsum/256;
  			sum += tempsum;
  		}
  	    sum = sum/(16+(i-16));
  	    sum = (1-sum) - (i-16)/16;
  	    System.out.println("correlation in 16 Byte FHT: " + sum);
	
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