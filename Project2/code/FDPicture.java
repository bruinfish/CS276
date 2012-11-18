import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class FDPicture {
	public int id;		//ID of the picture
	public String type;	//Type of the picture

	public int len;		//Length of a picture
	public int [][] pixels;
	private int [][] integral;
	public double[] weights;
	public double featureValue;
	
	
	public FDPicture(String filename, String type, int id, int len, int round, double iWeight) throws IOException{
		this.id = id;
		this.type = type;
		this.len = len;
		this.pixels = new int [len][len];
		this.integral = new int [len+1][len+1];
		this.weights = new double[round+1];
		this.weights[0] = iWeight;
		this.featureValue = 0;
		
		Scanner s = null;
		try {
			s = new Scanner(new BufferedReader(new FileReader(filename)));
			int x = 0;
			int y = 0;
			while(s.hasNextInt()){
				this.pixels[x][y] = s.nextInt();
				y++;
				if(y == this.len){
					y = 0;
					x++;
				}
			}
		} finally {
            s.close();
        }
		this.computeIntegral();
	}
	
	public void computeIntegral(){
		int[][] s = new int [len+1][len+1];
		
		for(int i = 1; i <= len; i++){
			for(int j = 1; j <= len; j++){
				s[i][j] = s[i][j-1] + pixels[i-1][j-1];
				integral[i][j] = integral[i-1][j] + s[i][j];
			}
		}
	}
	
	public int getSum(SquareCoordinate c){
		return integral[c.x2][c.y2] + integral[c.x1][c.y1] - integral[c.x1][c.y2] - integral[c.x2][c.y1];
	}
	
	public int getdiff(ArrayList<SquareCoordinate> feature){			
		switch(feature.size()){
		case 2:
			return getSum(feature.get(0)) - getSum(feature.get(1));
		case 3:
			return getSum(feature.get(0)) + getSum(feature.get(2)) - getSum(feature.get(1));
		case 4:
			return getSum(feature.get(0)) + getSum(feature.get(3)) - getSum(feature.get(1)) - getSum(feature.get(2));
		default:
			return Integer.MAX_VALUE;
		}
	}
	
	public void printData(){
		for (int i = 0; i < len; i++){
			for (int j = 0; j < len; j++){
				System.out.print(this.pixels[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public void printIntegral(){
		for (int i = 0; i <= len; i++){
			for (int j = 0; j <= len; j++){
				System.out.print(this.integral[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	public void setWeight(int index, double weight){
		this.weights[index] = weight;
	}
	
	public double getWeight(int index){
		return this.weights[index];
	}

	/**
	 * @param args
	 * For test
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FDPicture pic = new FDPicture("./Data/face16_" + String.format("%06d", 1) +".txt", "F", 1, 16, 200, 0);
		pic.printIntegral();
		System.out.println(pic.getSum(new SquareCoordinate(5, 3, 7, 8)));
	}

}
