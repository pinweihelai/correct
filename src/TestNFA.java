

public class TestNFA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NFA lev = new NFA("food",2);
		//System.out.println(lev.getStateNo(0, 0));
		System.out.println(lev.toString());
	}

}
