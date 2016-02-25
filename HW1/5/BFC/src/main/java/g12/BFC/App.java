package g12.BFC;
import g12.BFC.Mimetype;

/**
 * 
 *
 */

import java.io.File;



public class App 
{
	protected Mimetype[] mimeCollection; 
	protected final String path = "mimetypes.txt";

	public App(){
		String[] types = {"application_rss+xml"};
		mimeCollection = new Mimetype[types.length];
		int i = 0; 
		for(String str : types){
			Mimetype mimetype = new Mimetype(str,true);
			mimetype.run_BFA();
			mimeCollection[i++]=mimetype;
		}		
	}
	
	protected void compare_diff(double[] signature){
		double[] difference = new double[256];
		for(Mimetype mimetype : mimeCollection){
			int len = 0;
			//System.out.println("kjhewfkwehifhifnksjfhiwho");
			for(double value : signature){
				difference[len] = mimetype.signature[len] - value;
				len++;
			}
			System.out.println("Difference values");
			for(double value : difference){
				System.out.print(value);
			}
			System.out.println("Mimetype");
			for(double value : mimetype.signature){
				System.out.print(value);
			}
		}
		
	}
	
	protected void correlation(String path){
		File dir = new File(path);
		for(File fileiter : dir.listFiles()){
			Mimetype file = new Mimetype(fileiter.getName(),false);
			file.run_BFA();
			compare_diff(file.signature);
		}
		
		
	}
	
	public static void main(String[] args){
		String str = "/Users/kshah/output/"+"Batch5/application_rss+xml";
		App app = new App();
		app.correlation(str);
		

	}
	
}
