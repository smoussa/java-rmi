import java.util.ArrayList;
import java.util.List;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		List<String> alist = new ArrayList<String>();
		alist.add("apple");
		alist.add("banana");
		alist.add("orange");
		alist.remove(1);

		System.out.println(alist.get(1));
	}

}
