import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import acm.program.ConsoleProgram;
import acm.util.ErrorException;

/*
 * File: NameSurferDataBase.java
 * -----------------------------
 * This class keeps track of the complete database of names.
 * The constructor reads in the database from a file, and
 * the only public method makes it possible to look up a
 * name and get back the corresponding NameSurferEntry.
 * Names are matched independent of case, so that "Eric"
 * and "ERIC" are the same names.
 */

public class NameSurferDataBase implements NameSurferConstants {
	
/* Constructor: NameSurferDataBase(filename) */
/**
 * Creates a new NameSurferDataBase and initializes it using the
 * data in the specified file.  The constructor throws an error
 * exception if the requested file does not exist or if an error
 * occurs as the file is being read.
 * @throws FileNotFoundException 
 */
	public NameSurferDataBase(String filename) throws FileNotFoundException {
		BufferedReader rd = new BufferedReader(new FileReader(filename));
		lineList = new ArrayList<String>();
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
	}
	
/* Method: findEntry(name) */
/**
 * Returns the NameSurferEntry associated with this name, if one
 * exists.  If the name does not appear in the database, this
 * method returns null.
 */
	public NameSurferEntry findEntry(String name) {
		
		int lh = 0;
		int rh = lineList.size() - 1;
		while (lh <= rh) {
			int mid = (lh + rh) / 2;
			String line = lineList.get(mid);
			StringTokenizer tokenizer = new StringTokenizer(line);
			int cmp = name.compareToIgnoreCase(tokenizer.nextToken());
			if (cmp == 0) {
				return new NameSurferEntry(line);
			}
			if (cmp < 0) {
				rh = mid - 1;
			} else {
				lh = mid + 1;
			}
		}
		return null;
	}
	
/* Private instance variable */
	private ArrayList<String> lineList;
	
}

