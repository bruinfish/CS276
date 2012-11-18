import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;


public class Test {

	
	static public void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream objectIn = null;
		objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream("./featureStep")));
		int count = 0;
		while (count < 10) {
			Feature features = (Feature) objectIn.readObject();
			count++;
			System.out.println(features);
		}

		objectIn.close();

	}
}