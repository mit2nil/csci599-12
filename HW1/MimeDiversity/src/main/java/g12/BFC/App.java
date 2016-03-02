package g12.BFC;
import g12.BFC.BFA;

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
	public static void main(String[] args){
		//Give input path of the mime type, 75 percent path. 
		BFA bfa = new BFA("C:\\stuff\\Git\\polar_data_all_mime15_splitted\\audio_x-wav\\audio_x-wav");
		//Check if BFA succeeds.
		boolean status = bfa.computeBFA(true);
		if(!status){
			System.out.println("Byte Frequency Analysis Failed");
			return;
		}
		//Give input of the format : path to dir of 25% files, path of the Main folder[to search for BFA_mimeType.json]. 
		//Below code will calculate both BFA for 100% files and BFC for 25% files. 
		BFA100 bfa100 = new BFA100("C:\\stuff\\Git\\polar_data_all_mime15_splitted\\audio_x-wav\\audio_x-wav_25","C:\\stuff\\Git\\polar_data_all_mime15_splitted");
		
		//Give input of the format : Main folder[to search for BFA100_mimeType.json],Test Directory path. 
		//Test directory could contain non-classified files or files you want to see correlation matrix against. 
		//Either way out, it will output a matrix of the correlation between the file and all mime types that exist. 
		BFCC bfcc = new BFCC("C:\\stuff\\Git\\polar_data_all_mime15_splitted","C:\\stuff\\Git\\polar_data_all_mime15_splitted\\audio_x-wav\\audio_x-wav_25");
		
		
		
	}
	
	
							
}
