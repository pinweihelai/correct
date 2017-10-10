

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * 非确定有限状态机
 * 
 * @author luogang
 * 
 */
public class NFA {
	/**
	 * 计算状态编号
	 * @param c 正确的字符数
	 * @param e 错误的字符数
	 * @return 状态编号
	 */
	public int getStateNo(int c, int e) {
		int hash = e * (this.n+1) + c;
		return hash;
	}

	public String toString(int stateNo) {
		int targetOffset = stateNo%(this.n+1);
		int e = (stateNo - targetOffset)/(this.n+1);
		return targetOffset+"("+e+")";
	}

	public String toString(StateSet set) {
		StringBuilder sb = new StringBuilder();
		for (int state = set.table.nextSetBit(0); state >= 0; state = set.table.nextSetBit(state + 1)) {
			sb.append(toString(state) + "\t");
		}
		return sb.toString();
	}
	
	public int n, k;

	private int _startState; // 开始状态
	//HashMap<Integer, HashMap<Character, Integer>> transitions = new HashMap<Integer, HashMap<Character, Integer>>();
	char[] transChar;
	int[] transTarget;
	HashMap<Integer, StateSet> anyTrans = new HashMap<Integer, StateSet>();
	HashMap<Integer, Integer>  epsilonTrans = new HashMap<Integer, Integer>();
	
	StateSet finalStates = new StateSet(n, k);

	public StateSet startState() {
		StateSet start = new StateSet(n, k);
		start.add(_startState);
		return expand(start);
	}

	public void addAnyTrans(int src, int dest) {
		StateSet transition = anyTrans.get(src);
		if (transition == null) {
			transition = new StateSet(n, k);
			anyTrans.put(src, transition);
		}
		
		transition.add(dest);
	}

	public void addTransition(int src, char c, int dest) {
		transChar[src] = c;
		transTarget[src] = dest;
		/*HashMap<Character, Integer> transition = transitions.get(src);
		if (transition == null) {
			transition = new HashMap<Character, Integer>();
			transitions.put(src, transition);
		}
		
		transition.put(c, dest);*/
	}
	
	public void addFinalState(int state) {
		finalStates.add(state);
	}

	/**
	 * 构造方法
	 * @param term       词
	 * @param k         距离
	 */
	public NFA(String term, int k) {
		this._startState = getStateNo(0, 0); //开始状态
		this.n = term.length(); //长度
		this.k = k; //允许的最大距离

		transChar = new char[(n+1)*(k+1)];
		transTarget = new int[(n+1)*(k+1)];
		
		for (int i = 0; i < n; ++i) { //i表示正确匹配上的字符数
			char c = term.charAt(i);
			for (int e = 0; e < (k + 1); ++e) { //e表示错误字符数
				// 正确字符
				addTransition(getStateNo(i, e), c, getStateNo(i + 1, e));
				if (e < k) {
					// 删除，也就是删除当前的输入字符，向上的箭头
					addAnyTrans(getStateNo(i, e),getStateNo(i,e + 1));
					
					// 插入，曲线斜箭头
					epsilonTrans.put(getStateNo(i, e), getStateNo(i + 1, e + 1));
					
					// 替换，直的斜箭头
					addAnyTrans(getStateNo(i, e),getStateNo(i+1,e + 1));
				}
			}
		}
		for (int e = 0; e < (k + 1); ++e) {
			if (e < k){   //最后一列往上的箭头
				addAnyTrans(getStateNo(term.length(), e),getStateNo(term.length(), e + 1));
			}
			addFinalState(getStateNo(term.length(), e)); //设置结束状态
		}
	}

	public HashSet<Character> getInputs(StateSet current) {
		HashSet<Character> inputs = new HashSet<Character>();
		// 遍历所有为1的位
		for (int state = current.table.nextSetBit(0); state >= 0; state = current.table
				.nextSetBit(state + 1)) {
			char c = transChar[state];
			if(c!=0){
				inputs.add(c);
			}
			/*HashMap<Character, Integer> transition = transitions.get(state);
			if (transition == null)
				continue;
			for (Entry<Character, Integer> e : transition.entrySet()) {
				inputs.add(e.getKey());
			}*/
		}
		return inputs;
	}

	public StateSet expand(StateSet states) { // 扩展空输入转换
		if(states == null)
			return null;
		//System.out.println("to expand");
		ArrayDeque<Integer> frontier = new ArrayDeque<Integer>();
		for (int state = states.table.nextSetBit(0); state >= 0; state = states.table
		.nextSetBit(state + 1)) {
			frontier.add(state);
		}
		
		while (!frontier.isEmpty()) {
			//System.out.println("in frontier");
			Integer state = frontier.pop();
			//System.out.println("expand:"+state);
			//HashMap<Character, Integer> transition = transitions
			//		.get(state);

			Integer newStates = epsilonTrans.get(state);
			
			if(newStates==null){
				continue;
			}
			
			if(states.table.get(newStates)){
				newStates = null;
			}
			else{
				frontier.add(newStates);
				states.add(newStates);
			}
		}
		return states;
	}

	public DFA toDFA() {
		StateSet start = startState(); //开始状态集合
		DFA dfa = new DFA(new DFA.State(start)); //构建DFA
		ArrayDeque<StateSet> frontier = new ArrayDeque<StateSet>(); //待扩展的状态集合
		frontier.add(start); //从开始状态开始扩展
		HashSet<DFA.State> seen = new HashSet<DFA.State>(); //已经访问过的状态
		while (!frontier.isEmpty()) { //当待扩展的状态集合不是空
			StateSet current = frontier.pop();  //弹出栈
			HashSet<Character> inputs = getInputs(current); // 取得状态集合发出的输入
			if (inputs == null)
				continue;
			for (char input : inputs) { //遍历每一个输入
				StateSet newState = nextState(current, input); //下一个状态集合
				if (!seen.contains(newState)) { //如果以前没见到过这个状态集合
					frontier.add(newState); //把这个状态集合放到待扩展的状态集合
					seen.add(new DFA.State(newState)); //这个状态放到已经访问过的状态集合中
					if (isFinal(newState)) { //判断这个状态是不是可结束节点
						dfa.addFinalState(new DFA.State(newState));
					}
				}
				dfa.addTransition(new DFA.State(current), input,
							new DFA.State(newState));
			}
			
			//处理任意转移
			StateSet anyStates = new StateSet(n, k);
			for (int state = current.table.nextSetBit(0); state >= 0; state = current.table.nextSetBit(state + 1)) {
				anyStates.add( expand( anyTrans.get(state) ) );
			}
			if(!anyStates.isEmpty())
				dfa.setDefaultTransition(new DFA.State(current),
					new DFA.State(anyStates));
		}
		return dfa;
	}

	private boolean isFinal(StateSet newState) {
		return finalStates.containsAny(newState);
	}

	private StateSet nextState(StateSet current, char input) {
		StateSet destStateSet = new StateSet(n, k);
		
		for (int state = current.table.nextSetBit(0); state >= 0; state = current.table.nextSetBit(state + 1)) {
			/*HashMap<Character, Integer> stateTransition = transitions
					.get(state);
			if (stateTransition == null) {
				continue;
			}
			Integer temp = stateTransition.get(input);
			if (temp != null) {
				destStateSet.add(temp);
			}*/
			if(transChar[state]==input){
				destStateSet.add(transTarget[state]);
			}
			StateSet t = anyTrans.get(state);
			if (t != null)
				destStateSet.add(t);
		}

		return expand(destStateSet);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("正常转换\n");
		for (int i=0;i<transChar.length;++i) {
			if(transChar[i]!=0)
				sb.append(toString(i) + " -"+transChar[i]+"-> " +  toString(transTarget[i]) + "\n");
		}
		sb.append("空转换\n");
		for(Entry<Integer, Integer> e :epsilonTrans.entrySet()){
			sb.append(toString(e.getKey()) + " -> " + toString(e.getValue()) + "\n");
		}
		sb.append("任意转换\n");
		for(Entry<Integer, StateSet> e :anyTrans.entrySet()){
			sb.append(toString(e.getKey()) + " -> " + toString(e.getValue()) + "\n");
		}
		sb.append("结束状态\n");
		
		sb.append(toString(finalStates));
		/*for (int state = finalStates.table.nextSetBit(0); state >= 0; state = finalStates.table.nextSetBit(state + 1)) {
			sb.append(toString(state) + "\n");
		}*/
		
		return sb.toString();
	}
}
