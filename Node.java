//https://en.wikipedia.org/wiki/Huffman_coding#/media/File:Huffman_coding_visualisation.svg

/**
 * This class creates a node structure that can be used for encoding
 */
public class Node implements Comparable<Node> {
    private String letters; //all letters found in node and its children
    private int freq; //combined frequency of all letters found in node and children
    private Node left;
    private Node right;

    /**
     * This instantiates a new object of the Node class
     * Each node has letters, the combined frequency of those letters, and 
     * its left and right nodes
     * Note: the text in the node can be length of 1 or more
     * 
     * @param in The letter(s) that the node will have
     * @param frequency The combined frequency that the letters in the node and 
     *                  its children will have
     * @param leftN The left child node
     * @param rightN The right child node
     */
    public Node(String in, int frequency, Node leftN, Node rightN){
        this.letters = in;
        this.freq = frequency;
        this.left = leftN;
        this.right = rightN;
    }
    
    /**
     * This displays the letter(s) the node contains and the combined frequencies
     * of the letters
     * 
     * @return The letter(s) and the sum of its frequencies
     */
    public String toString(){
        return this.freq + ":" + this.letters;
    }
    
    /**
     * This gets all the letters that are in the node and its children
     * 
     * @return All the letters in the node and children
     */
    public String getLetters(){
        return this.letters;
    }

    /**
     * This gets the combined frequency of the letters in the node
     * 
     * @return The combined frequency
     */
    public int getFreq(){
        return this.freq;
    }
    
    /**
     * This compares the frequency of one node to another node
     * 
     * @param other The other node that needs to be compared
     * 
     * @return the numerical difference in frequency between two nodes
     */
    public int compareTo(Node other){
        return this.freq - other.freq;
    }
    
    /**
     * This finds the path in the node to a certain letter and returns a binary 
     * sequence of the path; if you go left, a '1' is added to the path
     * If you go right, a '0' is added to the path
     * 
     * @param specifiedLetter The letter that the path needs to be found for 
     * 
     * @return The path to the specified letter
     */
    public String findPath(String specifiedLetter){
        if (this.letters.equals(specifiedLetter)){
            return "";
        }
        if (this.left != null && this.left.letters.contains(specifiedLetter)){
            return "1" + this.left.findPath(specifiedLetter);
        }
        if (this.right != null && this.right.letters.contains(specifiedLetter)){
            return "0" + this.right.findPath(specifiedLetter);
        }
        return "";
    }
    
    /**
     * This gets the right child node
     * 
     * @return The right child node
     */
    public Node getRight(){
        return this.right;
    }
    
    /**
     * This gets the left child node
     * 
     * @return The left child node
     */
    public Node getLeft(){
        return this.left;
    }
}
