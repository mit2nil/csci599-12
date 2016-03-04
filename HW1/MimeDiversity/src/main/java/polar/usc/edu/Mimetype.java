package polar.usc.edu;

public class Mimetype {
	protected double[] signature;
	protected String name; 
	
	public Mimetype(String mimename, double[] fingerprint){
		name = mimename;
		signature = new double[256];
		int count = 0;
		for(double val : fingerprint){
			signature[count++] = val;
		}
	}	

}
