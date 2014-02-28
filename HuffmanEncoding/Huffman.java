import java.util.*;
import java.io.*;

public class Huffman {

	public static void main(String[] args) {
		
		// Error checking for command line file input
		if (args.length < 1) {
			System.out.println("Missing command-line argument: filename");
			return;
		}
		
		String filename = args[0]; // argument for file name
		HashMap<Character,Integer> freq = frequencies(filename); // sets up frequency table for given file
		System.out.println("Frequencies: " + freq);
		
		System.out.println("Simple encoding: " + simpleEncoding(freq));
		System.out.println("Optimal encoding: " + huffmanEncoding(freq));
	}
	
	// Creates a HashMap table to store each character with its frequency
	public static HashMap<Character,Integer> frequencies(String filename) {
		
		HashMap<Character,Integer> f = new HashMap<Character,Integer>();
		
		try {
		
			FileReader stream = new FileReader(filename);
		
			int r = stream.read();
			while (r != -1) {
			
				char letter = (char)r;
				r = stream.read();
			
				if (!f.containsKey(letter)) {
					f.put(letter,1);
				} else {
					f.put(letter, f.get(letter)+1);
				}
			}	
		
			stream.close();
		
		} catch (IOException e) {
			System.out.println("Could not read file.");
			System.exit(0);
		}
		
		// Checks for and removes newline character at EOF
		if (f.containsKey('\n')) {
			f.remove('\n');
		}
		
		return f;
	}
	
	// Function to generate a simple text encoding based on the number of unique characters in the frequency table
	public static HashMap<Character,String> simpleEncoding (HashMap<Character,Integer> freq) {
		
		HashMap<Character,String> encoding = new HashMap<Character,String>();
		
		int codeLength = (int)Math.ceil(Math.log(freq.size()/Math.log(2)));
		
		int codeNumber = 0;
		for (char letter : freq.keySet()) {
			
			String code = Integer.toBinaryString(codeNumber);
			while (code.length() < codeLength) {
				code = "0" + code;
			}
			
			encoding.put(letter, code);
			codeNumber++;
		}
		
		return encoding;
	}
	
	// Inner class to represent nodes of a Huffman tree
	private static class Node {
		
		// Instance data
		private Character ch;
		private int freq;
		private Node left, right; // children
		
		// Constructor for a new Node with the specified character, frequency, and children
		public Node(Character ch, int freq, Node left, Node right) {
			this.ch = ch;
			this.freq = freq;
			this.left = left;
			this.right = right;
		}
		
		// Constructor for a new leaf node
		public Node(Character ch, int freq) {
			this(ch,freq,null,null);
		}
		
		// Constructor for a new root node
		public Node() {
			this(null,0);
		}
		
		public int getChar() {
			return this.ch;
		}
		
		public int getFreq() {
			return this.freq;
		}
		
		public Node getLeft() {
			return this.left;
		}
		
		public Node getRight() {
			return this.right;
		}
		
		// Function to get Huffman codes from a pre-built Huffman tree
		public HashMap<Character,String> encode() {
			
			// If the tree is empty, return null
			if (this.left == null && this.right == null) {
				return null;
			}
			
			HashMap<Character,String> map = new HashMap<Character,String>();
			
			preorder(this, "", map);
			
			return map;
		}
		
		// Recursive function to build Huffman codes when leaves are traversed
		private void preorder(Node node, String code, HashMap<Character,String> currMapping) {
			
			// Associate each leaf's character with its Huffman code
			if (this.left == null && this.right == null) {
				currMapping.put(this.ch, code);
			}
		
			if (this.left != null) {
				this.left.preorder(this, code + "1", currMapping);
			}
		
			if (this.right != null) {
				this.right.preorder(this, code + "0", currMapping);
			}
		}
	}
	
	// Function to generate a Huffman encoding from a given frequency table
	public static HashMap<Character,String> huffmanEncoding (HashMap<Character,Integer> freq) {
	
		HashMap<Character,String> encoding = new HashMap<Character,String>();
		
		// Creates a new priority queue with initial capacity equal to the number of input HashMap elements
		// and orders its elements according to the comparator specified
		PriorityQueue<Node> q = new PriorityQueue<Node>(freq.size(), new Comparator<Node>() {
			
			// Orders priority queue elements in ascending order
			public int compare(Node node1, Node node2) {
				return node1.getFreq() - node2.getFreq();
			}
		});
		// Fill the priority queue
		for (Character ch : freq.keySet()) {
			int f = freq.get(ch);
			Node node = new Node(ch,f); // Make a tree root for (ch,f)
			q.offer(node);				// and add it to q
		}
		
		// While q contains more than one tree
		while (q.size() > 1) {
			Node newNode = new Node(); // new root
			newNode.left = q.poll(); // left child
			newNode.right = q.poll(); //right child
			newNode.freq = newNode.left.freq + newNode.right.freq; // newNode frequency is the sum of its children's frequencies
			
			// Re-add the merged node, decreasing q.size()
			q.add(newNode);
		}
		
		// The only node remaining in q becomes the root of the Huffman tree
		Node last = q.poll();
		
		// Generate the Huffman codes and store them in a HashMap
		encoding = last.encode();
		
		return encoding;
	}
}































