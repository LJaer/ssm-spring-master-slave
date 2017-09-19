import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test1 {

	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++)
			list.add(new Integer(i));
		System.out.println("打乱前:");
		System.out.println(list);

		for (int i = 0; i < 5; i++) {
			System.out.println("第" + i + "次打乱：");
			Collections.shuffle(list);
			System.out.println(list);
		}
	}
}
