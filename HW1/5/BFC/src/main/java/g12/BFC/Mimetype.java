package g12.BFC;


import g12.BFC.BFA;

public class Mimetype {
	protected double[] signature;
	protected String name; 
	
	public Mimetype(String mimename,Boolean flag){
		if(flag){
			name = mimename;
		}
		else{
			name = "application_rss+xml/"+mimename;
		}
		signature = new double[256];
	}
	
	protected void run_BFA(){
		BFA mimeBfa = new BFA("/Users/kshah/output/Batch5/"+name);
		boolean status = mimeBfa.computeBFA();
		if(!status){
			System.out.println("Byte Frequency Analysis Failed");
			System.out.println("Error in mimetype :"+name);
			return;
		}
		int len = 0;
		for(double value : mimeBfa.normalizedSignatures){
			signature[len++]=value;
		}
	}

}
