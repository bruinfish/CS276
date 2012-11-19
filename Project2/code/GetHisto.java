import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class GetHisto {

	private ArrayList<FDPicture> samples = new ArrayList<FDPicture>();;
	private ArrayList<Feature> adaFeatures = new ArrayList<Feature>();
	
	private double tpos;
	private double tneg;
	private int round = 100;
	private int max = 0;
	
	public GetHisto(int faceRange, int nonfaceRange, String featureFile, int max) throws IOException, ClassNotFoundException{
		
		this.tpos = 0.5;
		this.tneg = 0.5;
		this.max = max;
		
		double wpos = 0.5/faceRange;
		double wneg = 0.5/nonfaceRange;
		
		for(int i = 1; i <= faceRange; i++){
			samples.add(new FDPicture("./data/dataface16/face16_" + String.format("%06d", i) +".txt", "F", i, 16, round, wpos));
		}		
		for(int i = 1; i <= nonfaceRange; i++){
			samples.add(new FDPicture("./data/datanonface16/nonface16_" + String.format("%06d", i) +".txt", "N", i, 16, round, wneg));
		}
		
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(featureFile)));
		int count = 0;
		while (count < 200) {
			Feature f = (Feature) ois.readObject();
			count++;
			adaFeatures.add(f);
		}
		ois.close();
	}
	
	private int featureValue(Feature f, FDPicture pic){
		boolean isUp = false;
		boolean decision = false;
			
		int diff = pic.getdiff(f.recs);
		if(diff < f.threshold )
			isUp = false;
		else
			isUp = true;
			
		if(f.direct)
			decision = isUp;
		else
			decision = !isUp;
			
		if(decision)
				return 1;
		else
				return -1;
	}
	
	private void hasFace(FDPicture pic){
		double value = 0;
		for(int i = 0; i < max; i++){
			Feature feature = adaFeatures.get(i);
//			System.out.println(feature + "\t" + feature.threshold + "\t" + feature.isError(pic));
			value += (feature.featureWeight*featureValue(feature, pic));
		}

		System.out.println(pic.type + "\t" + value);
	}
	
	public void run(){
		for(int i = 0; i < samples.size(); i++)
			hasFace(samples.get(i));
	}
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		GetHisto gh = new GetHisto(11838, 45356, "./adaBoostFeature16", 10);
		gh.run();
	}

}
