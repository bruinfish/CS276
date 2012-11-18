import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;



public class ThresholdCal implements Runnable{
	private ArrayList<FDPicture> samples;
	private double tpos;
	private double tneg;
	private int round;
	private Feature feature;
//	private int featureID;
	private SynchronizedCounter counter;
		
	public ThresholdCal(ArrayList<FDPicture> samples, 
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
//		this.featureID = featureID;
		this.counter = counter;
		this.round = round;
	}

	public void run(){
		if(feature.isSelected()){
			counter.increment();
			return;
		}
		
		ArrayList<Record> list = new ArrayList<Record>();
		Iterator<FDPicture> iterator = samples.iterator();
		
		int count = 0;
		while(iterator.hasNext()){
			FDPicture pic = iterator.next();
			list.add(new Record(count, pic.type, pic.getdiff(feature.recs)));
			count++;
		}
		
		Collections.sort(list);
		
		passList(list);

		counter.increment();
	}
	
	private void passList(ArrayList<Record> records){
		int threshold = records.get(0).value;
		double error = Double.MAX_VALUE;
		boolean direct = true;
		double sneg = 0;
		double spos = 0;
//		int fcount = 0;
//		int ncount = 0;
		for(int i = 0; i < records.size(); i++){
			if(records.get(i).type == "F"){
				spos += samples.get(records.get(i).picid).getWeight(round);
			}
			else if(records.get(i).type == "N"){
				sneg += samples.get(records.get(i).picid).getWeight(round);
			}
			else
				System.exit(-101);
/*
			if(round == 1 && featureID == 26565){
				System.out.println(featureID + " f:"+fcount+" n:"+ncount);
				System.out.println("picid: " + records.get(i).picid);
//				System.out.println("tpos: " + tpos);
				System.out.println("spos: " + spos);
				System.out.println(samples.get(records.get(i).picid).weights[round]);
//				System.out.println("tneg: " + tneg);
				System.out.println("sneg: " + sneg);
				System.out.println("###########");
			}
*/
			double tmpError = (spos + (tneg - sneg)) > (sneg + (tpos - spos)) ? (sneg + (tpos - spos)) : (spos + (tneg - sneg));
			boolean tmpDirect = (spos + (tneg - sneg)) > (sneg + (tpos - spos)) ? false : true;
			if(tmpError < error){
				error = tmpError;
				direct = tmpDirect;
				threshold = records.get(i).value;
			}
		}
		
		feature.error = error;
//		if(this.round == 0){
			feature.direct = direct;
			feature.threshold = threshold;
//		}
	}
}
