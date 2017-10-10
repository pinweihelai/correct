

import java.util.BitSet;

public class StateSet {
	public BitSet table; // 位数组

	/**
	 * 构造一个状态集合
	 * 
	 * @param n
	 *            正确字符数
	 * @param e
	 *            错误字符数
	 */
	public StateSet(int n, int e) {
		table = new BitSet((n + 1) * (e + 1));
	}

	/**
	 * 状态集合是否包含某个状态
	 * @param o 状态编号
	 * @return
	 */
	public boolean contains(int o) {
		return table.get(o);
	}

	/**
	 * 两个状态集合是否存在交集
	 * @param s 要判断的另外一个状态集合
	 * @return
	 */
	public boolean containsAny(StateSet s) {
		for (int state = s.table.nextSetBit(0); state >= 0; state = s.table
				.nextSetBit(state + 1)) {
			if (table.get(state))
				return true;

		}
		return false;
	}
	
	public boolean isEmpty(){
		//System.out.println();
		return (table.cardinality()==0);
	}

	/**
	 * 增加一个状态到状态集合
	 * @param s
	 */
	public void add(int s) {
		table.set(s);
	}

	/**
	 * 增加一个状态集合中的所有状态到当前状态集合
	 * @param s
	 */
	public void add(StateSet s) {
		if(s==null)
			return;
		for (int state = s.table.nextSetBit(0); state >= 0; state = s.table
				.nextSetBit(state + 1)) {
			table.set(state);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof StateSet)) {
			return false;
		}
		StateSet other = (StateSet) obj;
		return table.equals(other.table);
	}
	
	@Override
	public int hashCode(){
		return table.hashCode();
	}
	
	public String toString(int n,int k) {
		StringBuilder sb = new StringBuilder();
		
		//输出当前状态集合中包含的状态编号
		for (int state = table.nextSetBit(0); state >= 0; state = table
				.nextSetBit(state + 1)) {
			//sb.append(state + "\t");
			int targetOffset = state%(n+1);
			int e = (state - targetOffset)/(n+1);
			sb.append(targetOffset+"("+e+")\t");
		}
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		//输出当前状态集合中包含的状态编号
		for (int state = table.nextSetBit(0); state >= 0; state = table
				.nextSetBit(state + 1)) {
			sb.append(state + "\t");
		}
		return sb.toString();
	}
}
