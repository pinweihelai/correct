

/**
 * Trie tree
 * @author luogang
 *
 */
public class Trie<T> {
	public TrieNode<T> rootNode = new TrieNode<T>();

	public void add(String key, T value) {
		addNode(rootNode, key, 0, value);
	}

	public T find(String key) {
		return findKey(rootNode, key);
	}

	public T search(String prefix) {
		char[] ch = prefix.toCharArray();
		TrieNode<T> node = rootNode;
		for (int i = 0; i < ch.length; i++) {
			node = node.getChildren().get(ch[i]);
			if (node == null) {
				break;
			}
		}

		if (node != null) {
			return getValuesFromNode(node);
		}

		return null;
	}

	private T getValuesFromNode(TrieNode<T> currNode) {
		if (currNode.isTerminal()) {
			return (currNode.getNodeValue());
		}

		return null;
	}

	private T findKey(TrieNode<T> currNode, String key) {
		Character c = key.charAt(0);
		if (currNode.getChildren().containsKey(c)) {
			TrieNode<T> nextNode = currNode.getChildren().get(c);
			if (key.length() == 1) {
				if (nextNode.isTerminal()) {
					return nextNode.getNodeValue();
				}
			} else {
				return findKey(nextNode, key.substring(1));
			}
		}

		return null;
	}

	private void addNode(TrieNode<T> currNode, String key, int pos, T value) {
		Character c = key.charAt(pos);
		TrieNode<T> nextNode = currNode.getChildren().get(c);

		if (nextNode == null) {
			nextNode = new TrieNode<T>();
			nextNode.setNodeKey(c);
			if (pos < key.length() - 1) {
				addNode(nextNode, key, pos + 1, value);
			} else {
				nextNode.setNodeValue(value);
				nextNode.setTerminal(true);
			}
			currNode.getChildren().put(c, nextNode);
		} else {
			if (pos < key.length() - 1) {
				addNode(nextNode, key, pos + 1, value);
			} else {
				nextNode.setNodeValue(value);
				nextNode.setTerminal(true);
			}
		}
	}
	
	/*static class Product {
		private int productId;
		private String productDesc;

		public Product(int productId, String productDesc) {
			this.productId = productId;
			this.productDesc = productDesc;
		}

		public int getProductId() {
			return productId;
		}
		public void setProductId(int productId) {
			this.productId = productId;
		}
		public String getProductDesc() {
			return productDesc;
		}
		public void setProductDesc(String productDesc) {
			this.productDesc = productDesc;
		}
	}*/

	public static void main(String[] args) {
		/*Trie<Product> productTrie = new Trie<Product>();
		productTrie.add("ham", new Product(1, "ham"));
		productTrie.add("hammer", new Product(2, "hammer"));
		productTrie.add("hammock", new Product(3, "hammock"));
		productTrie.add("ipod", new Product(4, "ipod"));
		productTrie.add("iphone", new Product(5, "iphone"));
		System.out.println(productTrie.find("ham"));*/
		

		Trie<String> stringTrie = new Trie<String>();
		stringTrie.add("ham", new String("ham"));
		stringTrie.add("hammer", new String("hammer"));
		stringTrie.add("hammock", new String("hammock"));
		stringTrie.add("ipod", new String("ipod"));
		stringTrie.add("iphone", new String("iphone"));
		System.out.println(stringTrie.find("ham"));
	}
}
