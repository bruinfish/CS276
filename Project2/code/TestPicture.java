import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;


public class TestPicture {
	
	public int[][] pixels;
	public ArrayList<Feature> features;
	public int size;
	private int len = 0;
	private int width = 0;
	private int step = 4;
	private int l = 0;
	private int w = 0;
	private boolean started;
	
	public TestPicture(String filename, int picID, String featureFile, int size, int step) throws IOException, ClassNotFoundException{

		switch(picID){
		case 1:
			len = 200;
			width = 300;
			break;
		case 2:
			len = 250;
			width = 375;
			break;
		case 3:
			len = 313;
			width = 469;
			break;
		case 4:
			len = 391;
			width = 586;
			break;
		case 5:
			len = 489;
			width = 733;
			break;
		case 6:
			len = 611;
			width = 916;
			break;
		default:
			len = 200;
			width = 300;
			break;
		}
		
		this.pixels = new int[len][width];
		
		Scanner s = null;
		try {
			s = new Scanner(new BufferedReader(new FileReader(filename)));
			int x = 0;
			int y = 0;
			while(s.hasNextInt()){
				this.pixels[x][y] = s.nextInt();
				y++;
				if(y == width){
					y = 0;
					x++;
				}
			}
		} finally {
            s.close();
        }
		
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(featureFile)));
		int count = 0;
		this.features = new ArrayList<Feature>();
		while (count < 200) {
			Feature f = (Feature) ois.readObject();
			count++;
			features.add(f);
		}
		ois.close();
		
//		System.out.println(features.size());
		
		this.size = size;
		this.step = step;
		this.started = false;
	}
	
	public void detectFace(){
		while(hasNext()){
			FDPicture pic = nextPiece();
			if(hasFace(pic)){
				System.out.println(l+","+w+";");
			}
		}
		
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
	
	private boolean hasFace(FDPicture pic){
		double value = 0;
		Iterator<Feature> iterator = features.iterator();
		while(iterator.hasNext()){
			Feature feature = iterator.next();
//			System.out.println(feature + "\t" + feature.threshold + "\t" + feature.isError(pic));
			value += (feature.featureWeight*featureValue(feature, pic));
		}

		if (value < 0)
			return false;
		else
			return true;
	}
	
	private boolean hasNext(){
		if(!started){
			started = true;
			return true;
		}
		
		w += step;
		
		if(w+size >= this.width){
			w = 0;
			l += step;
			if(l+size >= this.len){
				return false;
			}
		}
		
		return true;
	}
	
	private FDPicture nextPiece(){
		int [][] piece = new int[size][size];
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				piece[i][j] = this.pixels[i+l][j+w];
			}
		}
		
		return new FDPicture(piece, size);
	}
	
	static public void main(String args[]) throws IOException, ClassNotFoundException{
		int id = 5;
		TestPicture tp = new TestPicture("./data/test_"+id+".txt", id, "./adaBoostFeature16", 16, (new Double(4*Math.pow(1.25,id-1.0))).intValue());
		tp.detectFace();
//		System.out.println((new Double(4*Math.pow(1.25,id-1.0))).intValue());
	}
}
