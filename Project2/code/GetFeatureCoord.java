import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;


public class GetFeatureCoord {

	public ArrayList<Feature> features;
	
	public GetFeatureCoord(String featureFile) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(featureFile)));
		int count = 0;
		this.features = new ArrayList<Feature>();
		while (count < 200) {
			Feature f = (Feature) ois.readObject();
			count++;
			features.add(f);
		}
		ois.close();
	}
	
	public String squareArray(SquareCoordinate sc){
		return "("+(sc.x1+1)+":"+(sc.x2)+","+(sc.y1+1)+":"+sc.y2+")";				
	}
	public void printFeature(Feature f, int id){
		int m=128;
		System.out.println("a=ones(16,16)*128;");
		for(int i = 0; i < f.type; i++){
			m*=-1;
			if(i==2 && f.type==4){
				m*=-1;
			}
			System.out.println("a"+squareArray(f.recs.get(i))+"="+"a"+squareArray(f.recs.get(i))+"+("+m+");");
		}
		System.out.println("subplot(1,20,"+id+");");
		System.out.println("imshow(a, [min(a(:)) max(a(:))]);");
	}
	
	public void printTop10Feature(){
		System.out.println("figure(1);");
		for(int i = 0; i < 20; i++){
			this.printFeature(features.get(i), i+1);
		}
	}
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		GetFeatureCoord gfc = new GetFeatureCoord("./adaBoostFeature16");
		gfc.printTop10Feature();
	}

}
