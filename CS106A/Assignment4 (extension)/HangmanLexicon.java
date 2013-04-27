/*
 * File: HangmanLexicon.java
 * -------------------------
 * This file contains a stub implementation of the HangmanLexicon
 * class that you will reimplement for Part III of the assignment.
 */

import java.io.*;
import java.util.*;
import acm.util.*;

//Name: 
//Section Leader: 

public class HangmanLexicon {

	/** Declare Instance Variables you need here */
	
	
	/** HangmanLexicon constructor. Do any initialization of your lexicon here. */
	public HangmanLexicon() {
		
	}
	
	private int wordCount = 0;
	
/** Returns the number of words in the lexicon. */
	public int getWordCount() {
		return wordCount;
	}

/** Returns the word at the specified index. 
 * @throws FileNotFoundException */
	public String getWord(int index) throws FileNotFoundException {
		BufferedReader rd = new BufferedReader( new FileReader( "ShorterLexicon.txt" ) );
		String[] lexicon = readLineArray(rd);
		return lexicon[index];
	}

	private String[] readLineArray(BufferedReader rd) {
	    ArrayList<String> lineList = new ArrayList<String>();
	    try {
	    	while (true) {
	        String line = rd.readLine();
	        if (line == null) break;
	        lineList.add(line);
	    	}
	    	rd.close();
	    } catch (IOException ex) {
	    	throw new ErrorException(ex);
	    }
	    String[] result = new String[lineList.size()];
	    for (int i = 0; i < result.length; i++) {
	        result[i] = lineList.get(i);
	    }
	    wordCount = result.length;
	    return result;
	}
}
