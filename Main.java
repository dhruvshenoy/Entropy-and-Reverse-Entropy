import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;


public class Main
{
    /**
     * This allows the user to compress or decompress a file of their choosing
     * The output will be a separate file (zipper or output depending on selection)
     * Error messages are displayed when a wrong choice is given, when the file empty
     * or when the file doesn't exist
     */
	public static void main(String[] args) {
	    Scanner sc = new Scanner(System.in);
	    String choice = "";
	    while (!choice.equals("e")){ //loop idea is from Avi
    	    System.out.print("Would you like to compress or decompress or exit, type 'c' or 'd' or 'e': ");
    	    choice = sc.nextLine();
    	    if (choice.equals("c") || choice.equals("d")) {
                System.out.print("What is the name of the file: ");
                String fileName = sc.nextLine();
                String contents = readFile(fileName);
            
                if (contents == null) {
                    System.out.println("Can't find file");
                } else if (contents.equals("")) {
                    System.out.println(choice.equals("c") ? "Nothing to compress since file is empty; no zipper file was made" 
                                                           : "Nothing to decompress since file is empty; no output file was made");
                } else {
                    if (choice.equals("c")) {
                        if (!checkExt(fileName, choice)){
                            System.out.println("Invalid file ext, should be '.txt' or '.out'");
                        } else {
                            Entropy test = new Entropy(contents);
                            test.compress();
                            createCompressedFile(fileName, test);
                        }
                    } else { // choice.equals("d")
                        if (!checkExt(fileName, choice)){
                            System.out.println("Invalid file ext, shold be '.zpr'");
                        } else {
                            ReverseEntropy test = new ReverseEntropy(fileName);
                            test.decompress();
                            createDecompressedFile(fileName, test);    
                        }
                    }
                }
            } else if (choice.equals("e")) {
                System.out.println("Successfully exited program");
            } else {
                System.out.println("Invalid choice. Please enter 'c' or 'd' or 'e'.");
            }
            System.out.println();
	    }
	}
	
	/**
	 * This retrieves the contents of a user specified file (text or zipper)
	 * 
	 * @param fileName This is the user specified file name
	 * 
	 * @return The contents of the file in string format
	 *         It returns null if file isn't found
	 */
	public static String readFile(String fileName){
	   try {
	       Scanner sc = new Scanner(new FileReader(fileName));
	       String contents = "";
	       while (sc.hasNextLine()){
	           contents += sc.nextLine();
	           if (sc.hasNextLine()){
	               contents += "\n";
	           }
	       }
	       return contents;
	   } catch (FileNotFoundException f){
	       return null;
	   }
	}
	
	/**
	 * This checks if the file name has a valid extension
	 * If the choice is for compress, it must end with '.txt' or '.out'
	 * If the choice is for decompress, it must end with '.zpr'
	 * 
	 * @param fileName The file name that needs to checked
	 * @param choice The choice for compress or decompress
	 * Pre-Condition: choice is "c" or "d"
	 * 
	 * @return If the file has a valid extension, return true
	 *          Otherwise return false
	 */
	public static boolean checkExt(String fileName, String choice){
	    if (fileName.length() < 4){
            return false;
        }
        String ext = fileName.substring(fileName.length() - 4);
	    if (choice.equals("c")){
	        return ext.equals(".txt") || ext.equals(".out");
	    }
	    if (choice.equals("d")){
	        return ext.equals(".zpr");
	    }
	    return false;
	}
	
	/**
	 * This creates the compressed file of the user specified text file
	 * The output is a zipper file with the same name but with a different extension (.zpr)
	 * 
	 * The format of the zipper file is 1 byte for many '1's were added to the bit pattern, 
	 * 4 bytes for size of character count map, 1 byte for each unique character and 4 bytes for their 
	 * respective count, and the compressed bit pattern converted into characters
	 * 
	 * @param fileName The user specified file name
	 * @param entro An Entropy object that has details about the frequency count and bit pattern
	 * of the text in the user specified text file
	 */
	public static void createCompressedFile(String fileName, Entropy entro){ //with help from avi and ramaakshay for overall structure
	    try {
	        fileName = fileName.substring(0, fileName.length() - 4) + ".zpr";
    	    BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
    	    String mapSize = String.format("%32s", Integer.toBinaryString(5 * entro.getFreq().size())).replace(' ', '0');
    	    String mapPattern = entro.compressMap();
    	    String pattern = entro.getBitPattern();
    	    int added = ((8 - (pattern.length() % 8)) == 8) ? 0 : 8 - (pattern.length() % 8); 
    	    pattern += "1".repeat(added); //from kanishk
    	    bw.write(added); //from kanishk
    	    
    	    for (int i = 0; i < mapSize.length(); i += 8){
    	        bw.write(Integer.parseInt(mapSize.substring(i, i + 8), 2));
    	    }
    	    
    	    for (int i = 0; i < mapPattern.length(); i += 33){
    	        bw.write(mapPattern.substring(i, i + 1));
    	        for (int j = i + 1; j < i + 33; j += 8){
    	            bw.write(Integer.parseInt(mapPattern.substring(j, j + 8), 2));
    	        }
    	    }
    	    
    	    for (int i = 0; i < pattern.length(); i += 8){
    	        bw.write(Integer.parseInt(pattern.substring(i, i + 8), 2));
    	    }
    	    bw.close();
    	    System.out.println(fileName + " has been made");
	    } catch (IOException io){
	        System.out.println("something went wrong when outputting to file");
	    }
	}
	
	/**
	 * This creates the decompressed file of the user specified zipper file
	 * by getting the bit pattern from the zipper file and decoding it
	 * The output is an output file with the same name as the zipper file
	 * but with a different extension (.out)
	 * 
	 * The zipper file follows this format: 1 byte for many '1's were added to the bit pattern, 
	 * 4 bytes for size of character count map, 1 byte for each unique character and 4 bytes for their 
	 * respective count, and the compressed bit pattern converted into characters
	 * 
	 * @param fileName The user specified file name
	 * @param entro A ReverseEntropy object that has details about the compressed character count map
	 * and bit pattern of the compressed text in the user specified zipper file
	 */
	public static void createDecompressedFile(String fileName, ReverseEntropy revEntro){
	    try {
	        fileName = fileName.substring(0, fileName.length() - 4) + ".out";
    	    BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
    	    bw.write(revEntro.decodeBitPattern());
    	    bw.close();
    	    System.out.println(fileName + " has been made");
	    } catch (IOException io){
	        System.out.println("something went wrong when outputting to file");
	    }
	}
}
