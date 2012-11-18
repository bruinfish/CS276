import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Classifier {

	private ArrayList<FDPicture> samples;
	private double tpos;
	private double tneg;
	private FeatureGenerator fg;
	private ArrayList<Feature> features;
	private SynchronizedCounter counter;
	private ArrayList<Feature> adaFeature;
	private int round;
	
	private ObjectOutputStream oos;
	
	public Classifier(int faceRange, int nonfaceRange, int len, int round) throws IOException{
		samples = new ArrayList<FDPicture>();
		this.adaFeature = new ArrayList<Feature>();
		this.round = round;
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
		fg = new FeatureGenerator(len);
		features = new ArrayList<Feature>();
		features.addAll(fg.gen2RecFeature());
		features.addAll(fg.gen3RecFeature());
		features.addAll(fg.gen4RecFeature());
		
		oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("./featureStep")));   
	}
	
	public void getError(int round){
		this.counter = new SynchronizedCounter();
//		System.out.println("Count "+counter.value());
//		System.out.println("tpos "+this.tpos);
//		System.out.println("tneg "+this.tneg);
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
//				System.out.println(counter.value() + "/" + featureSize);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void adaBoost() throws IOException{
		for (int i = 0; i < round; i++){
			adaBoostStep(i);
//			printResult();
//			printWeight(i);
//			System.out.println(this.tneg + "/" + this.tpos);
//			System.out.println("==================");
			adaVerify(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		for (int i = 0; i < round; i++){
			oos.writeObject(this.adaFeature.get(i));
		}

	}
	
	public void adaBoostStep(int round){
		// Get Error Rate
		getError(round);
		// Sort features according to error rate, pick the one with the lest error rate.
		Collections.sort(features);
		int bestID = -1;
		double error = 0;
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
		this.adaFeature.add(bestFeature);
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
	
	public void adaVerify(int round){
		double errors = 0.0;
		Iterator<FDPicture> iterator = samples.iterator();
		while(iterator.hasNext()){
			FDPicture pic = iterator.next();
			errors += pic.weights[0]*this.adaValue(pic, round);
		}
		System.out.println("Round " + (round+1) + " ERROR :" + errors);
	}
	
	private int adaValue(FDPicture pic, int round){
		double value = 0;
		Feature feature = adaFeature.get(round);
		value = feature.featureWeight*feature.isError(pic)*(-1) + pic.featureValue;
		pic.featureValue = value;
		if (value < 0)
			return 1;
		else
			return 0;
	}
	
	public void printResult(){
		Collections.sort(features);
		for(int i = 0; i < 10; i++){
			System.out.println(features.get(i));
		}
	}
	
	public void printWeight(int round){
		for(int i = 0; i < samples.size(); i++){
			System.out.println(samples.get(i).type + " " + samples.get(i).weights[round]);
		}
	}
	
	public void end() throws IOException{
		oos.close();
	}

	/**
	 * @param args
	 * test
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Classifier classifier = new Classifier(1000, 1000, 16, 20);
		classifier.adaBoost();
		classifier.end();
	}
}

