import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.PriorityQueue;

public class HuffmanCode {

	private char[] chars;

	private static DecimalFormat f = new DecimalFormat("#0.0");
	private static DecimalFormat g = new DecimalFormat("#0.00");
	private static int allHuffmanBits = 0;

	private static char[] readFile(String filename) {
		ArrayList<Character> chars = new ArrayList<Character>();
		try (FileInputStream fis = new FileInputStream(filename)) {
			int input;
			while ((input = fis.read()) != -1) {
				chars.add((char) input);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Die angegebene Datei konnte nicht gefunden werden.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		char[] result = new char[chars.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = chars.get(i);
		return result;
	}

	public static HuffmanTree buildTree(int[] charFreqs) {
		PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
		for (int i = 0; i < charFreqs.length; i++)
			if (charFreqs[i] > 0)
				trees.offer(new HuffmanLeaf(charFreqs[i], (char) i));

		assert trees.size() > 0;
		
		while (trees.size() > 1) {
			HuffmanTree a = trees.poll();
			HuffmanTree b = trees.poll();

			trees.offer(new HuffmanNode(a, b));
		}
		return trees.poll();
	}

	public static void calcAllBitsHuffman(HuffmanTree tree, StringBuffer prefix) {
		assert tree != null;
		if (tree instanceof HuffmanLeaf) {
			HuffmanLeaf leaf = (HuffmanLeaf) tree;
			allHuffmanBits += leaf.frequency * prefix.length();
             System.out.println(leaf.value + "\t"+"\t" + prefix + "\t"+"\t" + "\t" + "Codewortlaenge: " + prefix.length() );

        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;
 
            prefix.append('0');
            calcAllBitsHuffman(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);
 
            prefix.append('1');
            calcAllBitsHuffman(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
        
	}	
	
	private static char[] diffChars(char[] chars){
		int n = chars.length;
	        for (int i=0, m=0; i!=n; i++, n=m ) {
	        	for (int j=m=i+1; j!=n; j++) {
	        		if (chars[j] != chars[i]) {
	        			if (m !=j){
	        				chars[m] = chars[j];
	        			}
	        			m++;
	        		}
	        	}
	        }	

	        if (n != chars.length) {
		    char[] charsb = new char[n];
		    for ( int i = 0; i < n; i++ ){
			charsb[i] = chars[i];
		    }
		    chars = charsb;
			}
			
		return chars;

	    }
	
	private static void frequency(char[] chars) {
        char[] clone = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            clone[i] = chars[i];
        }
        char[] b = diffChars(chars);
        double[] count = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < clone.length; j++) {
                if (b[i] == clone[j]) {
                    count[i]++;
                }
            }
        }

        for(int i = 0; i < 10;i++) {
            if(b[i] ==  '\n') {
                System.out.println("0x0" + Integer.toHexString(b[i]) + "  , " + "Haeufigkeit: " + (int)count[i] + 
                " (" + f.format(count[i]/chars.length*100) + "%) ");
                continue;
            }
            if(b[i] ==  '\r'){
                System.out.println("0x0" + Integer.toHexString(b[i]) + "  , " + "Haeufigkeit: " + (int)count[i] + 
                " (" + f.format(count[i]/chars.length*100) + "%) ");
                continue;
            }

            if(count[i]!=0) {
                System.out.println("0x" + Integer.toHexString(b[i]) + " " + b[i] + "," + " Haeufigkeit: " + (int)count[i] + 
                " (" + f.format(count[i]/chars.length*100) + "%) ");
            }

        }

        double[] proz = new double[count.length];
        for (int i = 0; i < count.length; i++) {
            proz[i] = ((double) count[i] / chars.length);
        }

		System.out.println();
		
        double entropie = 0;
        for (int i = 0; i < diffChars(chars).length; i++) {
            entropie += proz[i] * lg(proz[i]);
        }

		System.out.println("Entropie\t\t\t  : " + g.format(-(entropie)));
		
    }

	public static double lg( double x ) {
	  return Math.log( x ) / Math.log( 2 );
    }
    
	public static void main(String[] args) {
		try {
			HuffmanCode huffman = new HuffmanCode();
			huffman.chars = readFile(args[0]);
				// "C:\\Users\\Pascal\\Dropbox\\eclipse-fss20\\pii ii blatt05\\src\\blatt05-datei0.txt")
				
			
			int[] charFreqs = new int[256];
        	for (char c : huffman.chars){
           		 charFreqs[c]++;
       		 }
			
			HuffmanTree tree = buildTree(charFreqs);
			System.out.println("CHAR\t\tHUFFMANCODE\t\tLENGTH");
			calcAllBitsHuffman(tree, new StringBuffer());
			
			System.out.println();
			System.out.println("Haeufigste Zeichen:");
			frequency(huffman.chars);
			
			System.out.println("Anzahl Zeichen\t\t\t  : " + huffman.chars.length);
			System.out.println("Anzahl verschiedener Zeichen\t  : " + diffChars(huffman.chars).length);
			System.out.println("Kodierung mit fester Bitlaenge\t  : " + (int)(huffman.chars.length * (Math.ceil(lg(diffChars(huffman.chars).length))))
			+ " Bits (" + (int) Math.ceil(lg(diffChars(huffman.chars).length)) + " Bits pro Zeichen)");
			System.out.println("Kodierung mit Huffman-Code\t  : " + allHuffmanBits + " Bits" + " (" + g.format((double)allHuffmanBits/(double)huffman.chars.length)
			+ " Bits pro Zeichen)");
			System.out.println("Ersparnis (optimale feste Laenge) : " 
					+  g.format((( ((double)8*(double)huffman.chars.length) - (double)huffman.chars.length*(double)((int)Math.ceil(lg(diffChars(huffman.chars).length)))) 
					/ ((double)8*(double)huffman.chars.length)) *100) + "%");
			System.out.println("Ersparnis (Huffman-Code)\t  : "
					 + g.format((( ((double)8*(double)huffman.chars.length) - (double)allHuffmanBits) / ((double)8*(double)huffman.chars.length)) *100) + "%");


		} catch (ArrayIndexOutOfBoundsException aiobe) {
			System.out.println("Gueltiger Aufruf: java HuffmanCode datei");
		}
	}
}


class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency;
    public HuffmanTree(int freq) { 
        frequency = freq;
     }
    public int compareTo(HuffmanTree tree) {
        return frequency - tree.frequency;
    }
}

class HuffmanNode extends HuffmanTree {
    public final HuffmanTree left, right; 
 
    public HuffmanNode(HuffmanTree l, HuffmanTree r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}

class HuffmanLeaf extends HuffmanTree {
    public final char value; 
 
    public HuffmanLeaf(int freq, char val) {
        super(freq);
        value = val;
    }
}

