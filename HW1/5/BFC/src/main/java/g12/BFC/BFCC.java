package g12.BFC;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class BFCC {
		protected ArrayList<Mimetype> mimeCollection = new ArrayList<Mimetype>(); 
		
		public BFCC(String pathToDir, String testDir){
			File dir = new File(pathToDir);
			File tdir = new File(testDir);
			if(!dir.isDirectory() || !tdir.isDirectory()){
				System.out.println("Directory not found!");
	            throw new IllegalArgumentException();
			}
			double[] tempsignature = new double[256];
			for(File file : dir.listFiles()){
				if(file.getName().startsWith("BFA100_")){
					String name = file.getName().substring(7, file.getName().length()-5);
					tempsignature = BFA100.readJson(file);
					Mimetype mime = new Mimetype(name,tempsignature);
					mimeCollection.add(mime);
				}
			}
			double[][] tempmatrix = new double[256][256];
			for(File file : tdir.listFiles()){
				tempsignature = calFingerprint(file);
				
				for(Mimetype mime : mimeCollection){
					tempmatrix = correlation(tempsignature,mime.signature);
					System.out.println(file.getAbsolutePath()+" "+mime.name);
					writematrix(tempmatrix,tdir,file,mime);
				}				
			}
		}
		
		protected void writematrix(double[][] matrix, File tdir, File file, Mimetype mime){
			 if(file.getName().startsWith(".DS_Store")){
				 return;
			 }
			 String name = tdir.getAbsolutePath().substring(0,tdir.getAbsolutePath().lastIndexOf("/"))+ "/"+mime.name+"_"+file.getName()+".csv";
			 try{
				 FileWriter writer = new FileWriter(name); 
				 for(int i=0; i< 256; i++){
			    	  writer.append(",");
			    	  writer.append(String.valueOf("B"+String.valueOf(i)));	    	  
			      }
			      writer.append("\r\n");
			      for(int j=0; j<256 ; j++){
			    	  writer.append("B"+String.valueOf(j));
			    	  for(int i=0; i<256; i++){
			    		  writer.append(",");
			    		  writer.append(String.valueOf(matrix[j][i]));
			    	  }
			    	  writer.append("\r\n");
			      }
			      writer.close();
			 }
			 catch(Exception e)
			 {
			      e.printStackTrace();
			 } 
		}
		
		protected double[][] correlation(double[] arr1, double[] arr2){
			double[][] matrix = new double[256][256];
			for(int i=0; i<256;i++){
				for(int j=0;j<256;j++){
					matrix[i][j] = Math.abs(arr1[i] - arr2[j]);
				}
			}
			return matrix;
		}
		
		protected double[] calFingerprint(File file){
			BFA bfa = new BFA(file.getAbsolutePath());
			boolean status = bfa.computeBFA(false);
			if(!status){
				System.out.println("Byte Frequency Analysis Failed");
			}
			return bfa.normalizedSignatures;
		}
}
