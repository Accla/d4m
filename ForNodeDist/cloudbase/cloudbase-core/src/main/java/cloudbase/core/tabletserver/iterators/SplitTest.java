package cloudbase.core.tabletserver.iterators;

public class SplitTest {

	public static void main(String[] args) {
		String s = "aa.bb.cx";
		String[] sa = s.split("\\.");
		for (String string : sa) {
			System.out.println(string);
		}
	}

}
