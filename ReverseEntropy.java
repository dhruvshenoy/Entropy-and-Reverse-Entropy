//https://www.hackerrank.com/challenges/tree-huffman-decoding/problem

import java.util.HashMap;
import java.util.PriorityQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class can decode a zipper file as it can find 
 * the character frequency, the encoding for each charcter, and the decoded text
 */
public class ReverseEntropy {
    private BufferedReader br;    
    private HashMap<String, Integer> freq;
    private String bitPattern;
    private int added;
    private Node tree;
    
    /**
     * This instantiates an object of the ReverseEntropy class using a specified zipper file
     * 
     * @param fileName The name of the zipper file that needs to be decoded
     */
    public ReverseEntropy(String fileName){
        try {
            this.br = new BufferedReader(new FileReader(fileName));;
            this.freq = new HashMap<>();
            this.bitPattern = "";
            this.tree = null;
        } catch (FileNotFoundException f){
            System.out.println("file not found");
        }
    }
    
    /**
     * This decompresses the character count map
     * The fully populated map has all the unique characters and their frequencies
     * Pre: the ReverseEntropy object has been made and BufferedReader has been initialized
     * Post: the frequency map is made from the characters
     */
    private void decompressMap(){
        try {
            this.added = this.br.read();
            
            String mapSizeString = "";
            for (int i = 0; i < 4; i++){
                mapSizeString += String.format("%8s", Integer.toBinaryString(this.br.read())).replace(' ', '0');
            }
            int mapSize = Integer.parseInt(mapSizeString, 2);
            
            for (int i = 0; i < mapSize; i += 5){
                char key = (char) this.br.read();
                String freqInBinary = "";
                for (int j = 0; j < 4; j++){
                    freqInBinary += String.format("%8s", Integer.toBinaryString(this.br.read())).replace(' ', '0');
                }
                this.freq.put(String.valueOf(key), Integer.parseInt(freqInBinary, 2));
            }
        } catch (IOException io){
            System.out.println("Something went wrong");
        }
    }
    
    /**
     * This makes a tree with all of the nodes and organizes based on each node's
     * frequency; When making a new node that combines the two smallest nodes, 
     * the lower frequency node goes to the left and other goes to the right
     * Pre-Condition: The size of nodes is greater than 0
     * Pre: The frequency hash map must be made with decompressMap
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
     * This decompresses the bit pattern from characters to a binary pattern
     * Pre: The frequency map must be made with decompressMap and the 
     * BufferedReader has been initialized
     * Post: the bit pattern is updated
     */
    private void decompressBitPattern(){
        try {
            while (this.br.ready()){
                this.bitPattern += String.format("%8s", Integer.toBinaryString(this.br.read())).replace(' ', '0');
            }
            this.bitPattern = this.bitPattern.substring(0, this.bitPattern.length() - this.added);
        } catch (IOException io){
            System.out.println("Something went wrong");
        }
    }
    
    /**
     * This decompresses the zipped text by getting the character counts
     * Then it makes the Huffman tree and gets the bit pattern of '1's and '0's
     * Pre: the methods must be called in this order
     * Post: decompresses the counts map and bit pattern and updates variables
     */
    public void decompress(){
        this.decompressMap();
        this.tree = this.makeTree();
        this.decompressBitPattern();
    }
    
    /**
     * This decodes the bit pattern (made of 0's and 1's) using the Huffman tree
     * Pre: the huffman tree must be made and the bit pattern must be in ones and zeroes using decompress()
     * Post: the plain text is made
     * 
     * @return The plain text from the bit pattern
     */
    public String decodeBitPattern(){
        String result = "";
        Node duplicateTree = this.tree;
        if (duplicateTree.getRight() == null && duplicateTree.getLeft() == null){
            for (int i = 0; i < this.bitPattern.length(); i++){
                result += duplicateTree.getLetters();
            }
        } else {
            for (int i = 0; i < this.bitPattern.length(); i++){
                if (this.bitPattern.charAt(i) == '0'){
                    duplicateTree = duplicateTree.getRight();
                } else {
                    duplicateTree = duplicateTree.getLeft();
                }
                if (duplicateTree.getLeft() == null && duplicateTree.getRight() == null){
                    result += duplicateTree.getLetters();
                    duplicateTree = this.tree;
                }
            }
        }
        return result;
    }
}
