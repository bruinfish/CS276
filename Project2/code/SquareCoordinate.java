import java.io.Serializable;


public class SquareCoordinate implements Serializable{
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	
	public SquareCoordinate(int x1, int y1, int x2, int y2){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public SquareCoordinate revert(){
		return (new SquareCoordinate(y1, x1, y2, x2));
	}
	
	public String toString(){
		return "<" + x1 + "," + y1 + "," + x2 + "," + y2 + ">";
	}
}
