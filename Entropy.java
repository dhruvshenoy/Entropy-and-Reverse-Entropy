//My friend helped me decide which structures should and shouldn't be instance variables
//https://en.wikipedia.org/wiki/Huffman_coding

import java.util.LinkedList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * This class can encode a text file as it can find 
 * the character frequency, the encoding for each charcter, and the encoded text
 */
public class Entropy {
    private String text;
    private HashMap<String, Integer> freq;
    private HashMap<String, String> encryptions;
    private String bitPattern;

    /**
     * This instantiates an object of the Entropy class using a specified input
     * 
     * @param input The text that needs to be encoded
     */
    public Entropy(String input){
        this.text = input;
        this.freq = new HashMap<>();
        this.encryptions = new HashMap<>();
        this.bitPattern = "";
    }
    
    /**
     * This calculates the frequency of each unique character in the text
     * Pre: The entropy object is made
     * Post: the frequency map is populated
     */
    private void calcFrequencies(){
	    for (int i = 0; i < this.text.length(); i++){
	        String currLetter = this.text.substring(i, i + 1);
	        if (!this.freq.containsKey(currLetter)){
	            this.freq.put(currLetter, 1);
	        } else {
	            this.freq.put(currLetter, this.freq.get(currLetter) + 1);
	        }
	    }
    }
    
    /**
     * This makes a tree with all of the nodes and organizes based on each node's
     * frequency; When making a new node that combines the two smallest nodes, 
     * the lower frequency node goes to the left and other goes to the right
     * Pre-Condition: The size of nodes is greater than 0
     * Pre: the frequency hash map must be populated
     * Post: the huffman tree is made
     * 
     * @return The tree of all the nodes
     */
    private Node makeTree(){
        PriorityQueue<Node> nodes = new PriorityQueue<>();
        for (String key : this.freq.keySet()){
	        nodes.add(new Node(key, this.freq.get(key), null, null));
	    }
	    
        while (nodes.size() > 1){
            Node smallest1 = nodes.poll();
            Node smallest2 = nodes.poll();
            int freq1 = smallest1.getFreq();
            int freq2 = smallest2.getFreq();
            String combinedName = smallest1.getLetters() + smallest2.getLetters();
            nodes.add(new Node(combinedName, freq1 + freq2, smallest1, smallest2));
        }
        return nodes.poll();
    }
    
    /**
     * This finds the path of each unique character in the text based on a tree
     * If there is only 1 unique character, it will be encoded with a '1'
     * Pre-Condtion: The text length is greater than 0
     * Pre: the huffman tree must be made and the frequency map has been populated
     * Post: the encryption map is populated
     * 
     * @param tree This is the tree that has all the nodes organized in a specified way
     */
    private void makeEncryptions(Node tree){
        if (this.freq.keySet().size() == 1){
            this.encryptions.put(tree.getLetters(), "1");
        } else {
            for (String key : this.freq.keySet()){
                this.encryptions.put(key, tree.findPath(key));
            }
        }
    }

    /**
     * This retrieves the text as a bit pattern by converting original characters
     * into encoded characters
     * Pre: the encodings hash map must be populated
     * Post: the bit pattern is made
     * 
     * @return The String representation of the bit pattern
     */
    private void makeBitPattern(){
        for (int i = 0; i < this.text.length(); i++){
            String letter = this.text.substring(i, i + 1);
            this.bitPattern += encryptions.get(letter);
        }
    }
    
    /**
     * This populates the character count map and the encryption map
     * Then it generates the bit pattern
     * Pre: the methods must be called in this order
     * Post: frequency map, encoding map, and bit pattern are made
     */
    public void compress(){
        this.calcFrequencies();
        this.makeEncryptions(this.makeTree());
        this.makeBitPattern();
    }
    
    /**
     * This retrieves the frequency map 
     * Pre: The frequency map must be made with compress()
     * 
     * @return The frequency map
     */
    public HashMap<String, Integer> getFreq(){
        return this.freq;
    }
    
    /**
     * This retrieves the bit pattern
     * Pre: The bit pattern must be made with compress()
     * 
     * @return The bit pattern
     */
    public String getBitPattern(){
        return this.bitPattern;
    }
    
    /**
     * This compresses the map so it can be put into the zipper file
     * It uses 1 byte for each letter and 4 bytes for its respective frequency
     * Pre-Condtion: the frequency map must be populated
     * Post: the frequency map converted to characters
     * 
     * @return The compressed map in string format
     */
    public String compressMap(){ //string format help from kanishk
        //https://www.geeksforgeeks.org/java-lang-integer-tobinarystring-method/
	    String result = "";
	    for (String key : this.freq.keySet()){
	        int value = this.freq.get(key);
	        String valueInBinary = String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0');
	        result += key + valueInBinary;
	    }
	    return result;
    }
}
