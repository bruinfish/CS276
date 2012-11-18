import java.io.Serializable;
import java.util.ArrayList;


public class Feature implements Comparable<Feature>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<SquareCoordinate> recs;
	public int type;
	public double error;
	public boolean direct;
	public int threshold;
	private boolean selected;
	public double featureWeight;
	
	public Feature(ArrayList<SquareCoordinate> recs, int type){
		this.recs = recs;
		this.type = type;
		this.error = 1;
		this.direct = true;
		this.threshold = 0;
		this.selected = false;
		this.featureWeight = 0;
	}
	
	public double isError(FDPicture pic){
		boolean isUp = false;
		boolean decision = false;
		boolean truth = false;
		
		int diff = pic.getdiff(this.recs);
		if(diff < this.threshold )
			isUp = false;
		else
			isUp = true;
		
		if(direct)
			decision = isUp;
		else
			decision = !isUp;
		
		if(pic.type == "F")
			truth = true;
		else
			truth = false;
		
		if(truth == decision)
			return -1;
		else
			return 1;
	}

	public void setSelected(){
		this.selected = true;
	}
	
	public boolean isSelected(){
		return selected;
	}

	@Override
	public int compareTo(Feature feature) {
		if(this.error < feature.error)
			return -1;
		else 
			return 1;
	}

	public String toString(){
		return "[" + type + "\t" + selected + "\t" + featureWeight + "\t" + error + "\t" + recs.toString() + "]";
	}
}
