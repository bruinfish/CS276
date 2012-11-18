import java.util.ArrayList;

public class FeatureGenerator {
	public int len;

	public FeatureGenerator(int len){
		this.len = len;
	}
	
	public ArrayList<Feature> gen2RecFeature(){
		ArrayList<Feature> features = new ArrayList<Feature>();
		
		int maxWidth = len/2;
		int maxLength = len;
		
		for(int w = 1; w <= maxWidth; w++){
			for(int l = 1; l <=maxLength; l++){
				features.addAll(getFeatures(FeaturePattern.genPattern(w, l, 2), true));
			}
		}
		
		return features;
	}
	
	public ArrayList<Feature> gen3RecFeature(){
		ArrayList<Feature> features = new ArrayList<Feature>();
		
		int maxWidth = len/3;
		int maxLength = len;
		
		for(int w = 1; w <= maxWidth; w++){
			for(int l = 1; l <=maxLength; l++){
				features.addAll(getFeatures(FeaturePattern.genPattern(w, l, 3), false));
			}
		}
		
		return features;
	}
	
	public ArrayList<Feature> gen4RecFeature(){
		ArrayList<Feature> features = new ArrayList<Feature>();
		
		int maxWidth = len/2;
		int maxLength = len/2;
		
		for(int w = 1; w <= maxWidth; w++){
			for(int l = 1; l <=maxLength; l++){
				features.addAll(getFeatures(FeaturePattern.genPattern(w, l, 4), false));
			}
		}
		
		return features;
	}
	
	private ArrayList<Feature> getFeatures(FeaturePattern pattern,  boolean flip){
		ArrayList<Feature> features = new ArrayList<Feature>();
		int maxw = len - pattern.width;
		int maxl = len - pattern.length;
		
		for(int w = 0; w <= maxw; w++){
			for(int l = 0; l <= maxl; l++){
				ArrayList<SquareCoordinate> normalCoordinates = new ArrayList<SquareCoordinate>();
				ArrayList<SquareCoordinate> revertCoordinates = new ArrayList<SquareCoordinate>();
				for(int i = 0; i < pattern.squares.size(); i++){
					SquareCoordinate sc = pattern.squares.get(i);					
					SquareCoordinate nc = new SquareCoordinate(w + sc.x1, l + sc.y1, w + sc.x2, l + sc.y2);
					normalCoordinates.add(nc);
					revertCoordinates.add(nc.revert());
				}
				features.add(new Feature(normalCoordinates, pattern.type));
				if(flip == true)
					features.add(new Feature(revertCoordinates, pattern.type));
			}
		}
		
		return features;
	}
	
	/**
	 * @param args
	 * test
	 */
	public static void main(String[] args) {
		FeatureGenerator fg = new FeatureGenerator(16);
		System.out.println(fg.gen4RecFeature().size());
//		Iterator<ArrayList<SquareCoordinate>> iterator = fg.gen2RecFeature()).iterator();
//		while(iterator.hasNext()){
//			System.out.println(iterator.next());
//		}
	}

}

