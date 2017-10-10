

import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * 
 * @author luogang
 *
 */
public class TestDFA extends TestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//testNFA();
		//testDFA();
		//testMatch();
		testCom();
	}
	
	public static void testDFAIntersect() {
		//DFA dfa1 = new DFA();
		
	}
	
	public static void testNFA() {
		NFA lev = new NFA("food",2);
		
		//System.out.println(lev.getStateNo(0, 0));
		System.out.println(lev.toString());
	}
	
	//测试编辑距离有限状态机的匹配情况
	public static void testMatch(){
		//根据错误词构建NFA
		NFA lev = new NFA("weight",3);
		
		//把NFA转换成DFA
		DFA dfa = lev.toDFA();
		
		//正确词表
		Trie<String> stringTrie = new Trie<String>();
		stringTrie.add("food", "food");
		stringTrie.add("hammer", "hammer");
		stringTrie.add("hammock", "hammock");
		stringTrie.add("ipod", "ipod");
		stringTrie.add("iphone", "iphone");
		stringTrie.add("wait", "wait");
		
		//返回相似的正确的词
		ArrayList<String> match = dfa.transduce(stringTrie);
		for(String s:match){
			System.out.println(s);  //输出 food
		}
	}
	

	//测试编辑距离有限状态机的匹配情况
	public static void testCom(){
		//根据错误词构建NFA
		NFA lev = new NFA("DAVID OPPENHEIMER CO..",2);
		//把NFA转换成DFA
		DFA dfa = lev.toDFA();
		
		//词表
		Trie<String> stringTrie = new Trie<String>();
		stringTrie.add("DAVID OPPENHEIMER CO.", "DAVID OPPENHEIMER CO.");
		stringTrie.add("COLUMBIA MARKETING INTL LLC", "COLUMBIA MARKETING INTL LLC");
		
		//返回相似的正确的词
		ArrayList<String> match = dfa.transduce(stringTrie);
		for(String s:match){
			System.out.println(s);  //输出 food
		}
	}
	
	
	public static void testDFA() {
		//构建编辑距离自动机
		NFA lev = new NFA("food",1);
		//System.out.println(lev.toString());
		//根据幂集构造转换成确定有限状态机
		DFA dfa = lev.toDFA();
		//System.out.println("dfa");
		System.out.println(dfa.toString(lev));
		//看单词food是否能够被接收
		System.out.println(dfa.accept("food"));
		System.out.println(dfa.accept("fooxd"));
		System.out.println(dfa.accept("fooxdjj"));
	}

}
