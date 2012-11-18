import java.util.ArrayList;


public class FeaturePattern {
	public int type;
	public int width;
	public int length;
	public ArrayList<SquareCoordinate> squares;
	
	public FeaturePattern(int type, int width, int length, ArrayList<SquareCoordinate> squares){
		this.type = type;
		this.width = width;
		this.length = length;
		this.squares = squares;
	}
	
	static public FeaturePattern genPattern(int width, int length, int type){
		ArrayList<SquareCoordinate> squares;
		switch(type){
		case 2:
			squares = new ArrayList<SquareCoordinate>();
			squares.add(new SquareCoordinate(0, 0, width, length));
			squares.add(new SquareCoordinate(width, 0, width*2, length));
			return (new FeaturePattern(type, width*2, length, squares));
		case 3:
			squares = new ArrayList<SquareCoordinate>();
			squares.add(new SquareCoordinate(0, 0, width, length));
			squares.add(new SquareCoordinate(width, 0, width*2, length));
			squares.add(new SquareCoordinate(width*2, 0, width*3, length));
			return (new FeaturePattern(type, width*3, length, squares));
		case 4:
			squares = new ArrayList<SquareCoordinate>();
			squares.add(new SquareCoordinate(0, 0, width, length));
			squares.add(new SquareCoordinate(width, 0, width*2, length));
			squares.add(new SquareCoordinate(0, length, width, length*2));
			squares.add(new SquareCoordinate(width, length, width*2, length*2));
			return (new FeaturePattern(type, width*2, length*2, squares));
		default:
			return null;
		}
	}
	
	public static void main(String[] args) {
		FeaturePattern pattern = FeaturePattern.genPattern(1, 1, 2);
		System.out.println(pattern.squares);
	}
}
