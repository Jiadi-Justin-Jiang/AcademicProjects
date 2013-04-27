/*
 * File: Hangman.java
 * ------------------
 * This program will eventually play the Hangman game from
 * Assignment #4.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;

//Name: Jiadi "Justin" Jiang
//Section Leader: Elmer

public class Hangman extends ConsoleProgram {

	private static final int NUM_GUESSES = 8;
	private static final int PAUSE_TIME_FACE_FALL = 50;
	private static final int PAUSE_TIME_FACE_CHANGE = 500;
	private static final int HEAD_RADIUS = 36;
	private static final int SCAFFOLD_HEIGHT = 360;
	private static final int ROPE_LENGTH = 18;
	private static final int VERTICAL_OFFSET = 48;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private HangmanCanvas canvas = new HangmanCanvas();
	private HangmanLexicon lexicon = new HangmanLexicon();
	
	HangmanDeadFace face = new HangmanDeadFace( 2*HEAD_RADIUS );
	private int vx = 5;
	private int vy = 0;
	
    public void run() {
    	canvas.reset();
    	setLayout(new GridLayout(1, 2));
    	add(canvas);
    	validate();
    	
    	while ( true ) {
    		canvas.reset();
    		try {
				hangManBegins();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    		if ( !isToContinueGame() ) {
    			break;
    		}
    	}
    }	

	private boolean isToContinueGame() {
		println( "Play Hangman Again? Y/N" );
		String gameContinueIndicator = readLine();
		while ( true ) {
			if( gameContinueIndicator.length() >= 2 || !isLetter( gameContinueIndicator.charAt(0) ) || (
				gameContinueIndicator.charAt(0) != 'Y' && gameContinueIndicator.charAt(0) != 'N' &&
				gameContinueIndicator.charAt(0) != 'y' && gameContinueIndicator.charAt(0) != 'n') ) {
				println( "Illegal Input: Play Hangman Again? Y/N" );
				gameContinueIndicator = readLine();
			} else {
				return ( gameContinueIndicator.charAt(0) == 'Y' || gameContinueIndicator.charAt(0) == 'y' );
			}	
		}
	}

	private void hangManBegins() throws FileNotFoundException {
		
		/* Prompt and get secret word */
		println( "Welcome to Hangman!" );
		
		//Call object of HangmanLexicon class once, otherwise getWordCount will return 0
		lexicon.getWord(0);
		
		int secretWordIndex = rgen.nextInt( 0, ( lexicon.getWordCount() - 1 ) );
		String secretWord = lexicon.getWord( secretWordIndex );
		int guessesLeft = NUM_GUESSES;
		String displayedWord = getInitialDisplayedWord( secretWord );
		
		/* Initialize index to check duplicate letter */
		int[] revealedLetterIndex = new int[secretWord.length()];
		for (int i = 0; i < secretWord.length(); i++) {
				revealedLetterIndex[i] = -1;
		}
		
		/* Begins iterations of Hangman */
		while ( true ) {
			
			/* Get newly guessed letter and set index */
			char newGuessLetter = getNewGuessLetter();
			newGuessLetter = Character.toUpperCase(newGuessLetter);
			int newGuessLetterIndex = -1;  //Initialize default new guess letter index
			
			/* Check if it is a correct guess */
			for (int i = 0; i < secretWord.length(); i++) {
				if ( i == revealedLetterIndex[i] ) {
					continue;
				}
				if ( newGuessLetter == secretWord.charAt(i) ) {
					newGuessLetterIndex = i;
					revealedLetterIndex[i] = i;
					break;
				}
			}
			
			/* Update displayed word */
			if ( newGuessLetterIndex != -1 ) { // Guess is correct
				println( "That guess is correct." );
				/* Correctly guessed letter could be at the beginning, in the middle or at the end */
				if ( newGuessLetterIndex != 0 && newGuessLetterIndex != ( secretWord.length() - 1 ) ) {
					displayedWord = displayedWord.substring( 0, newGuessLetterIndex ) +
									newGuessLetter + displayedWord.substring(newGuessLetterIndex + 1); 
				} else if ( newGuessLetterIndex == 0 ) {
					displayedWord = newGuessLetter + displayedWord.substring(1);
				} else if ( newGuessLetterIndex == ( secretWord.length() - 1 ) ) {
					displayedWord = displayedWord.substring( 0, secretWord.length() - 1 ) + newGuessLetter;
				}
				print( "The word now looks like this: " );
				println( displayedWord );
				canvas.displayWord( displayedWord );
			} else { // Guess is incorrect
				println( "There are no " + newGuessLetter + "'s in the word." );
				canvas.noteIncorrectGuess( newGuessLetter, guessesLeft);
				guessesLeft--;
				canvas.drawHangMan( guessesLeft );
			}
			
			/* Winning condition */
			if ( displayedWord.equals( secretWord ) ) {
				println( "You guessed the word: " + secretWord );
				println( "You Win!" );
				break;
			}
			
			/* Losing condition */
			if ( guessesLeft == 0 ) {
				println( "You are completely hung." );
				println( "The word was: " + secretWord );
				println( "You Lose!" );
				/* A question: why getWidth()/4, but not /2 ? */
				canvas.addToCanvas( face, getWidth()/4 - HEAD_RADIUS, 
										  getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH);
				pause(PAUSE_TIME_FACE_CHANGE);
				headFall();
				break;
			}
			
			/* Prompt for numbers of guess left */
			if ( guessesLeft != 1) {
				println( "You have " + guessesLeft + " guesses left.");
			} else {
				println( "You have only one guess left");
			}
		}
	}
	
	private void headFall() {
		while ( true ) {
			canvas.moveOnCanvas( face, vx, vy++);
			pause( PAUSE_TIME_FACE_FALL );
			if ( face.getY() > SCAFFOLD_HEIGHT ) {
				break;
			}
		}
	}
	
	private String getInitialDisplayedWord ( String secretWord ) {
		String displayedWord = "";
		print("The word looks like this: ");
		for (int i = 0; i < secretWord.length(); i++){
			displayedWord += "-";
		}
		println(displayedWord);
		canvas.displayWord( displayedWord );
		return displayedWord;
	}
	
	private char getNewGuessLetter() {
		print("Your guess: ");
		String newGuess = readLine();
		while ( true ) {
			if(newGuess.length() >= 2 || !isLetter(newGuess.charAt(0))){
				println("Guess is illegal, guess again!");
				print("Your guess: ");
				newGuess = readLine();
			}else{
				return newGuess.charAt(0);
			}
		}
	}	
		
	private boolean isLetter(char ch) {
		return ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'));
	}

}
