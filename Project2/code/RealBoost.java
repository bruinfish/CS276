import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RealBoost {
	private ArrayList<FDPicture> samples;
	private ArrayList<Feature> realFeature;
	private ArrayList<Feature> features;
	private int round;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private double tpos;
	private double tneg;
	private SynchronizedCounter counter;
	

	public RealBoost(int faceRange, int nonfaceRange, int len, int round) throws IOException, ClassNotFoundException{
		samples = new ArrayList<FDPicture>();
		this.realFeature = new ArrayList<Feature>();
		this.features = new ArrayList<Feature>();
		
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
		
		ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("./featureStep")));
		int count = 0;
		while (count < round) {
			Feature f = (Feature) ois.readObject();
			Feature newF = new Feature(f.recs, f.type);
			newF.direct = f.direct;
			newF.threshold = f.threshold;
			newF.error = 0.0;
			count++;
			features.add(newF);
		}
		this.round = features.size();
		ois.close();
		
		oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("./realFeatureStep")));   
	}
	
	public void realBoost() throws IOException{
		for (int i = 0; i < round; i++){
			realBoostStep(i);
			realVerify(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		for (int i = 0; i < round; i++){
			oos.writeObject(this.realFeature.get(i));
		}
	}
	
	private void realBoostStep(int round){
		getError(round);
		
//		this.printResult();
		
		Collections.sort(features);
		
		int bestID = -1;
//		double error = 0;
		for(int i = 0; i < features.size(); i++){
			if(!features.get(i).isSelected()){
				bestID = i;
//				error = features.get(i).error;
				break;
			}
		}
		features.get(bestID).setSelected();
		Feature bestFeature = features.get(bestID);
		this.realFeature.add(bestFeature);
		double fweight = bestFeature.featureWeight;
		
		double wSum = 0.0;
		for(int i = 0; i < samples.size(); i++){
			double sign = bestFeature.isError(samples.get(i));
			double tmpweight = samples.get(i).weights[round]*Math.exp(sign*fweight);
			wSum += tmpweight;
			samples.get(i).weights[round+1] = tmpweight;
		}
//		System.out.println("Best " + bestFeature.error + " wSum" + wSum );
		for(int i = 0; i < samples.size(); i++){
			samples.get(i).weights[round+1] /= wSum;
		}
	}
	
	public void getError(int round){
		this.counter = new SynchronizedCounter();
		ExecutorService threadpool = Executors.newFixedThreadPool(16);
		int featureSize = features.size();
		for(int i = 0; i < featureSize; i++){
			threadpool.submit(new RealThresholdCal(samples, tpos, tneg, features.get(i), i, counter, round));
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
	
	public void realVerify(int round){
		double errors = 0.0;
		Iterator<FDPicture> iterator = samples.iterator();
		while(iterator.hasNext()){
			FDPicture pic = iterator.next();
			errors += pic.weights[0]*this.realValue(pic, round);
		}
		System.out.println("Round " + (round+1) + " ERROR :" + errors);
	}
	
	private int realValue(FDPicture pic, int round){
		double value = 0;
		Feature feature = realFeature.get(round);
		value = feature.featureWeight*feature.isError(pic)*(-1) + pic.featureValue;
		pic.featureValue = value;
		if (value < 0)
			return 1;
		else
			return 0;
	}
	
	public void end() throws IOException{
		oos.close();
	}
	
	public void printResult(){
		Collections.sort(features);
		for(int i = 0; i < 10; i++){
			System.out.println(features.get(i));
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		RealBoost classifier = new RealBoost(500, 500, 16, 20);
		classifier.realBoost();
		classifier.end();
	}
}
