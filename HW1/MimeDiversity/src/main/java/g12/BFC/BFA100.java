package g12.BFC;
import g12.BFC.Mimetype;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.io.IOException;
import java.io.FileNotFoundException;

public class BFA100 {
	protected ArrayList<Mimetype> mimeCollection = new ArrayList<Mimetype>(); 
	protected final String path = "mimetypes.txt";
	protected int count = 0;
	
	public BFA100(String pathTo25, String dirPath){
		test25Data(pathTo25,dirPath);
		compute25Data(pathTo25,dirPath);	
	}
	
	protected static double[] readJson(File bfaFingerprint)
    {
		System.out.println("READING" + bfaFingerprint.getName());
		double[] fingerprint = new double[256];
        try
        {
        	if (!bfaFingerprint.isFile())
            {
                System.out.println("Json file not found!");
                throw new IllegalArgumentException();
            }
            BufferedReader br = new BufferedReader(new FileReader(bfaFingerprint));
            // Initialize the fingerprint by reading from json file
            // Regex for "Byte-0": 0.8689165960011597,
            String regex = "\"Byte-(\\d+)\":\\s\"(\\d[\\.]\\d+)\"";
            String line = "";
            
            while ((line = br.readLine()) != null)
            {
                //fingerprint[i] = 0;
                Pattern p = Pattern.compile(regex);
                //  get a matcher object
                Matcher m = p.matcher(line);
                if (m.find() && m.groupCount() == 2) 
                {
                    int index = Integer.parseInt(line.substring(m.start(1), m.end(1)));
                    if (index >= 0 && index <= 255)
                    {
                        fingerprint[index] = Double.parseDouble(line.substring(m.start(2), m.end(2)));
                    }
                    //System.out.println("From "+line+" matched: "+line.substring(m.start(1), m.end(1)) );
                    //System.out.println("From "+line+" matched: "+line.substring(m.start(2), m.end(2) );
                } 
            }
            for(double val : fingerprint){
            	System.out.print(val);
            }
            br.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("File not found: "+bfaFingerprint.getName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("IOException reading file: "+bfaFingerprint.getName());
        }
        return fingerprint;
    }
	
	protected void readData(String dirPath,String name){
		File dir; 
		dir = new File(dirPath);
        if (!dir.isDirectory())
        {
            System.out.println("Directory "+dirPath+" not found!");
            throw new IllegalArgumentException();
        }
        double[] temp = new double[256];
        for(File f : dir.listFiles()){
        	String fname = f.getName();
        	if(fname.equals("BFA_"+name+".json")){
        		String mimename = fname.substring(4,fname.length()-5);
        		temp = readJson(f);
        		for(double val : temp){
        			System.out.print(val);
        		}
        		Mimetype mime = new Mimetype(mimename, temp);
        		mimeCollection.add(mime);        		
        	}
        }  
	}
	
	protected double[] read75fingerprint(String dirPath, String mimename){
		File dir;
		double[] signature = new double[256];
		dir = new File(dirPath);
        if (!dir.isDirectory())
        {
            System.out.println("Directory "+dirPath+" not found!");
            throw new IllegalArgumentException();
        }
        for(File f : dir.listFiles()){
        	String fname = f.getName();
        	if(fname.equals("BFA_"+mimename+".json")){
        		signature = readJson(f);
        	}
        }  
        for(double val : signature){
        	System.out.print(val);
        }
        return signature;
	}
		
	protected void compute25Data(String path,String dirPath){
		File dir = new File(path);
		String mimename = readtype(dir);
		double[] sign75 = new double[256];
		double[] temp = new double[256];
		BFA bfa25 = new BFA(path);
		boolean status = bfa25.computeBFA(false);
		if(!status){
			System.out.println("Byte Frequency Analysis Failed");
			System.out.println("Error in folder : "+path);
			return;
		}
		sign75 = read75fingerprint(dirPath,mimename);
		for(int i = 0; i<256; i++){
			temp[i] = (0.75*sign75[i]) + (0.25*bfa25.normalizedSignatures[i]);
		}
		BFA.dumpJson(temp,mimename,dir);		
	}
	
	protected void test25Data(String path, String dirPath){
		File dir = new File(path);
		String mimename = readtype(dir);
		readData(dirPath,mimename);
		boolean status ;
		BFA bfa25;
		for(File file : dir.listFiles()){
			if(file.getName().startsWith(".DS_Store")){
				continue;
			}
			bfa25 = new BFA(file.getAbsolutePath());
			status = bfa25.computeBFA(false);
			if(!status){
				System.out.println("Byte Frequency Analysis Failed");
				System.out.println("Error in folder : "+path);
				continue;
			}
			computeDifference(bfa25.normalizedSignatures);	
		}	
	}
	
	protected void computeDifference(double[] signature){
		double[][] matrix = new double[mimeCollection.size()][256];
		int count = 0; 
		for(Mimetype mime : mimeCollection){
			for(int i =0; i<256; i++){
				matrix[count][i] = Math.abs(signature[i]-mime.signature[i]);
				System.out.print(matrix[count][i]);
			}
			count++;
			System.out.println("");
		}
	}
	
	protected String readtype(File dir){
		if (!dir.isDirectory())
        {
            System.out.println("Directory "+path+" not found!");
            throw new IllegalArgumentException();
        }
		String mimename = dir.getName();
		System.out.println(mimename);
		mimename = mimename.substring(0,mimename.length()-3); 
		System.out.println(mimename);
		return mimename;
	}

}
