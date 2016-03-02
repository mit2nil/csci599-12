package g12.BFC;
import g12.BFC.BFA;
import g12.BFC.FHT;

/**
 * This project runs BFA, BFC, BFCC and FHT and generates all the necessary output files for D3 visualization. 
 * Structure of Main Folder : 
 * INPUT : All folders[named by MIME types and split test percent] - e.g. image_gif and image_gif_25.  
 * OUTPUT : 
 * 1) BFA : BFA_mimeType.json  
 * 2) BFC : BFA100_mimeType.json, (//one more file, will name later)
 * 3) BFCC : mimeType_Filename.csv 
 */
import g12.BFC.BFA100;

public class App 
{
    protected static String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args){

    	String mainDirectory = args[0];
    	String mimeType = args[1];
        BFA bfa;
        if (OS.contains("windows"))
        {
            //Give input path of the mime type, 75 percent path.
            bfa = new BFA(mainDirectory + "\\" + mimeType + "\\" + mimeType);
        }
        else
        {
            //Give input path of the mime type, 75 percent path.
            bfa = new BFA(mainDirectory + "/" + mimeType + "/" + mimeType);
        }

		//Check if BFA succeeds.
		boolean status = bfa.computeBFA(true);
		if(!status){
			System.out.println("Byte Frequency Analysis Failed");
			return;
		}

        //Give input of the format : path to dir of 25% files, path of the Main folder[to search for BFA_mimeType.json].
		//Below code will calculate both BFA for 100% files and BFC for 25% files.
        if (OS.contains("windows"))
        {
            BFA100 bfa100 = new BFA100(mainDirectory + "\\" + mimeType + "\\" + mimeType +"_25",mainDirectory);

        }
        else
        {
            BFA100 bfa100 = new BFA100(mainDirectory + "/" + mimeType + "/" + mimeType+"_25",mainDirectory);
        }

		//Give input of the format : Main folder[to search for BFA100_mimeType.json],Test Directory path. 
		//Test directory could contain non-classified files or files you want to see correlation matrix against. 
		//Either way out, it will output a matrix of the correlation between the file and all mime types that exist. 

        //Give input of the format : path to dir of 25% files, path of the Main folder[to search for BFA_mimeType.json].
        //Below code will calculate both BFA for 100% files and BFC for 25% files.
        if (OS.contains("windows"))
        {
            BFCC bfcc = new BFCC(mainDirectory,mainDirectory + "\\" + mimeType + "\\" + mimeType +"_25");

            FHT f = new FHT(mainDirectory + "\\" + mimeType + "\\" + mimeType, "DIRECTORY");
            f.computeFHT();
            f.detectFileScore(mainDirectory, mainDirectory + "\\" + mimeType + "\\" + mimeType + "_25", mimeType);
        }
        else
        {
            BFCC bfcc = new BFCC(mainDirectory,mainDirectory + "/" + mimeType + "/" + mimeType + "_25");

            
            FHT f = new FHT(mainDirectory + "/" + mimeType + "/" + mimeType, "DIRECTORY");
            f.computeFHT();
            f.detectFileScore(mainDirectory, mainDirectory + "/" + mimeType + "/" + mimeType + "_25", mimeType);
        }
		//System.out.println("Generated FHT scores in: " + (System.currentTimeMillis()-st1));
	}
}
