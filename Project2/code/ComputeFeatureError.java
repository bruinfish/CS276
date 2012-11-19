import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ComputeFeatureError {
	private ArrayList<FDPicture> samples = new ArrayList<FDPicture>();;
	private ArrayList<Feature> features = new ArrayList<Feature>();
	private ArrayList<Feature> adaFeatures = new ArrayList<Feature>();
	
	private double tpos;
	private double tneg;
	private int round = 200;
	
	private SynchronizedCounter counter = new SynchronizedCounter();
	
	public ComputeFeatureError(int faceRange, int nonfaceRange, int len, String featureFile) throws IOException, ClassNotFoundException{
//		FeatureGenerator fg = new FeatureGenerator(len);
		features = new ArrayList<Feature>();
//		features.addAll(fg.gen2RecFeature());
//		features.addAll(fg.gen3RecFeature());
//		features.addAll(fg.gen4RecFeature());
		
		
		this.tpos = 0.5;
		this.tneg = 0.5;
		
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
			
			Feature newF = new Feature(f.recs, f.type);
			newF.direct = f.direct;
			newF.threshold = f.threshold;
			newF.error = 0.0;
			
			count++;
			features.add(newF);
			adaFeatures.add(f);
		}
		ois.close();
	}
	
	public void getError(int round){
		this.counter = new SynchronizedCounter();
		ExecutorService threadpool = Executors.newFixedThreadPool(16);
		int featureSize = features.size();
		for(int i = 0; i < featureSize; i++){
			threadpool.submit(new ThresholdCal(samples, tpos, tneg, features.get(i), i, counter, round));
		}
		threadpool.shutdown();
		
		
		while(true){
			if(counter.value() == features.size()){
				break;
			}
			try {
				Thread.sleep(1000);
				System.out.println(counter.value() + "/" + featureSize);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isSame(Feature f1, Feature f2){
		if(f1.type != f2.type){
			return false;
		}
		for(int i = 0; i < f1.type; i++){
			if(!(f1.recs.get(i).toString().equals(f2.recs.get(i).toString())))
				return false;
		}
		return true;
	}
	
	public void adaBoost() throws IOException{
		for (int i = 0; i < round; i++){
			adaBoostStep(i);
			if(i == 0 || i == 10 || i == 50 || i == 100)
				this.printResult();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
	}
	
	public void adaBoostStep(int round){
		// Get Error Rate
		getError(round);
		// Sort features according to error rate, pick the one with the lest error rate.
		Collections.sort(features);
		int bestID = 0;
		double error = 0.0;
		for(int i = 0; i < features.size(); i++){
			if(!features.get(i).isSelected()){
				bestID = i;
				error = features.get(i).error;
				break;
			}
		}
		features.get(bestID).setSelected();
		// Get weight of feature
		double fweight = Math.log((1-error)/error)/2;
		features.get(bestID).featureWeight = fweight;
		Feature bestFeature = features.get(bestID);
		// Update weight of samples;
		double wSum = 0;
		double tmpTpos = 0;
		double tmpTneg = 0;
		for(int i = 0; i < samples.size(); i++){
			double sign = bestFeature.isError(samples.get(i));
			double tmpweight = samples.get(i).weights[round]*Math.exp(sign*fweight);
			wSum += tmpweight;
			if(samples.get(i).type == "F")
				tmpTpos += tmpweight;
			else
				tmpTneg += tmpweight;
			samples.get(i).weights[round+1] = tmpweight;
		}
		this.tpos = tmpTpos/wSum;
		this.tneg = tmpTneg/wSum;
		for(int i = 0; i < samples.size(); i++){
			samples.get(i).weights[round+1] /= wSum;
		}
	}
	
	public void printResult(){
//		Collections.sort(features);
		for(int i = 1; i < 200; i++){
			System.out.println(i+"\t"+features.get(i).error);
		}
		System.out.println("====");
	}
	
	private void compare(){
		int count = 0;
		for( int i = 0; i < 100; i++){
			for ( int j = 0; j < features.size(); j++){
				if(isSame(this.features.get(j), this.adaFeatures.get(i))){
					count ++;
					break;
				}
			}
		}
		System.out.println(count);
	}
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ComputeFeatureError cfe = new ComputeFeatureError(11838, 45356, 16, "./result/adaBoostFeature16");
		cfe.adaBoost();
	}

}
