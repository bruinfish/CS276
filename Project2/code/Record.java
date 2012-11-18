import java.util.ArrayList;
import java.util.Collections;


public class Record implements Comparable<Record>{
	public int picid;
	public String type;
	public int value;
	
	public Record(int id, String type, int value){
		this.picid = id;
		this.type = type;
		this.value = value;
	}

	@Override
	public int compareTo(Record rec) {
		if(this.value < rec.value)
			return -1;
		else if(this.value == rec.value)
			return 0;
		else
			return 1;
	}
	
	public String toString(){
		return "<" + picid + "," + type + "," + value + ">";
	}
	
	public static void main(String[] args){
		ArrayList<Record> array = new ArrayList<Record>();
		array.add(new Record(1, "1", 100));
		array.add(new Record(2, "2", 20));
		array.add(new Record(3, "3", 150));
		array.add(new Record(4, "4", 70));
		
		Collections.sort(array);
		if("F" == "F"){
			System.out.println(array);
		}
	}
}
