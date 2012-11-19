import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class RealThresholdCal implements Runnable{

	private ArrayList<FDPicture> samples;
	private double tpos;
	private double tneg;
	private int round;
	private Feature feature;
	private SynchronizedCounter counter;
	private int featureID;
		
	public RealThresholdCal(ArrayList<FDPicture> samples, 
						double tpos,
						double tneg,
						Feature feature, 
						int featureID,
						SynchronizedCounter counter,
						int round){
		this.samples = samples;
		this.tpos = tpos;
		this.tneg = tneg;
		this.feature = feature;
		this.counter = counter;
		this.round = round;
		this.featureID = featureID;
	}

	public void run(){
		if(feature.isSelected()){
			counter.increment();
			return;
		}
		
		double tport = 0.0;
		double fport = 0.0;
		
		if(this.featureID == 0){
//			System.out.println(feature.threshold);
//			System.out.println(fport + " " + tport);
		}
		
		for(int i = 0; i < samples.size(); i++){
//			System.out.println("S"+i+ " " + samples.git
			FDPicture pic = samples.get(i);
			if(feature.isError(pic) > 0)
				fport += pic.weights[round];
			else
				tport += pic.weights[round];
		}
		
		
		
		feature.featureWeight =  Math.log(tport/fport)/2;
		feature.error = Math.sqrt(tport*fport)*2;
		
		counter.increment();
	}	
}
