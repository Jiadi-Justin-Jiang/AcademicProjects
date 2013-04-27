/*
 * File: HangmanCanvas.java
 * ------------------------
 * This file keeps track of the Hangman display.
 */

import java.awt.Color;

import acm.graphics.*;

//Name: 
//Section Leader: 

public class HangmanCanvas extends GCanvas {

	/* Constants for the simple version of the picture (in pixels) */
		private static final int SCAFFOLD_HEIGHT = 360;
		private static final int BEAM_LENGTH = 144;
		private static final int ROPE_LENGTH = 18;
		private static final int VERTICAL_OFFSET = 48;
		private static final int HEAD_RADIUS = 36;
		private static final int BODY_LENGTH = 144;
		private static final int ARM_OFFSET_FROM_HEAD = 28;
		private static final int UPPER_ARM_LENGTH = 72;
		private static final int LOWER_ARM_LENGTH = 44;
		private static final int HIP_WIDTH = 36;
		private static final int LEG_LENGTH = 108;
		private static final int FOOT_LENGTH = 28;
		private static final int INITIAL_GUESSES = 8;
		private static final int PAUSE_TIME = 20;
	
		GLabel displayedWord = new GLabel( "" );
		GLabel displayedIncorrectGuesses = new GLabel( "" );
		String incorrectGuesses = "";
		HangmanFace face = new HangmanFace( 2*HEAD_RADIUS );
		private int vx = 5;
		private int vy = 0;
	
	/** HangmanCanvas constructor. You can do initialization (if needed) here. */
	public HangmanCanvas() {

	}
	
	public void drawHangMan( int index ) {
		switch(index) {
			case 7: drawHead( HEAD_RADIUS ); break;
			case 6: drawBody(); break;
			case 5: drawLeftArm(); break;
			case 4: drawRightArm(); break;
			case 3: drawLeftLeg(); break;
			case 2: drawRightLeg(); break;
			case 1: drawLeftFoot(); break;
			case 0: drawRightFoot(); remove( face ); break;
			default: break;
		}
	}
	
	public void addToCanvas( GCompound face, int x, int y ) {
		add( face, x, y );
	}
	
	public void moveOnCanvas( GCompound face, int dx, int dy ) {
		face.move(dx, dy);
	}
	
	private void drawHead( int radius ) {
		add( face, getWidth()/2 - HEAD_RADIUS , 
				   getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH );
	}

	private void drawBody() {
		add( new GLine( getWidth()/2, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS, 
						getWidth()/2, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH) );
	}

	private void drawLeftArm() {
		add( new GLine( getWidth()/2, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + ARM_OFFSET_FROM_HEAD, 
						getWidth()/2 - UPPER_ARM_LENGTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + ARM_OFFSET_FROM_HEAD ) );
		add( new GLine( getWidth()/2 - UPPER_ARM_LENGTH, 
				getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
					2*HEAD_RADIUS + ARM_OFFSET_FROM_HEAD, 
				getWidth()/2 - UPPER_ARM_LENGTH, 
				getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
					2*HEAD_RADIUS + ARM_OFFSET_FROM_HEAD + LOWER_ARM_LENGTH ) );
	}

	private void drawRightArm() {
		add( new GLine( getWidth()/2, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + ARM_OFFSET_FROM_HEAD, 
						getWidth()/2 + UPPER_ARM_LENGTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + ARM_OFFSET_FROM_HEAD ) );
		add( new GLine( getWidth()/2 + UPPER_ARM_LENGTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + ARM_OFFSET_FROM_HEAD, 
						getWidth()/2 + UPPER_ARM_LENGTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + ARM_OFFSET_FROM_HEAD + LOWER_ARM_LENGTH ) );
	}

	private void drawLeftLeg() {
		add( new GLine( getWidth()/2, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH, 
						getWidth()/2 - HIP_WIDTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH ) );
		add( new GLine( getWidth()/2 - HIP_WIDTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH, 
						getWidth()/2 - HIP_WIDTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH + LEG_LENGTH) );
	}

	private void drawRightLeg() {
		add( new GLine( getWidth()/2, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH, 
						getWidth()/2 + HIP_WIDTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH ) );
		add( new GLine( getWidth()/2 + HIP_WIDTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH, 
						getWidth()/2 + HIP_WIDTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH + LEG_LENGTH) );
	}

	private void drawLeftFoot() {
		add( new GLine( getWidth()/2 - HIP_WIDTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH + LEG_LENGTH, 
						getWidth()/2 - HIP_WIDTH - FOOT_LENGTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH + LEG_LENGTH) );
	}

	private void drawRightFoot() {
		add( new GLine( getWidth()/2 + HIP_WIDTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH + LEG_LENGTH, 
						getWidth()/2 + HIP_WIDTH + FOOT_LENGTH, 
						getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH + 
							2*HEAD_RADIUS + BODY_LENGTH + LEG_LENGTH) );
	}

/** Resets the display so that only the scaffold appears */
	public void reset() {
		removeAll();
		drawScaffold();
	}

	private void drawScaffold() {
		drawStick();
		drawBeam();
		drawRope();
	}

	private void drawStick() {
		add( new GLine( getWidth()/2 - BEAM_LENGTH, getHeight()/2 + SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET, 
					    getWidth()/2 - BEAM_LENGTH, getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET ) );
	}

	private void drawBeam() {
		add( new GLine( getWidth()/2 - BEAM_LENGTH, getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET, 
						getWidth()/2, getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET) );
	}

	private void drawRope() {
		add( new GLine( getWidth()/2, getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET, 
						getWidth()/2, getHeight()/2 - SCAFFOLD_HEIGHT/2 - VERTICAL_OFFSET + ROPE_LENGTH) );
	}

/**
 * Updates the word on the screen to correspond to the current
 * state of the game.  The argument string shows what letters have
 * been guessed so far; unguessed letters are indicated by hyphens.
 */
	public void displayWord(String word) {
		remove(displayedWord);
		displayedWord = new GLabel ( word, getWidth()/2 - BEAM_LENGTH, getHeight()/2 + SCAFFOLD_HEIGHT/2);
		displayedWord.setFont("Serif-36");
		add(displayedWord);
	}

	
/**
 * Updates the display to correspond to an incorrect guess by the
 * user.  Calling this method causes the next body part to appear
 * on the scaffold and adds the letter to the list of incorrect
 * guesses that appears at the bottom of the window.
 */
	public void noteIncorrectGuess( char letter, int guessesLeft) {
		if ( guessesLeft == INITIAL_GUESSES ) {
			incorrectGuesses = "";
		}
		incorrectGuesses = incorrectGuesses + letter;
		displayedIncorrectGuesses = new GLabel ( incorrectGuesses, getWidth()/2 - BEAM_LENGTH, 
											getHeight()/2 + SCAFFOLD_HEIGHT/2 + VERTICAL_OFFSET/2);
		displayedIncorrectGuesses.setFont("Serif-24");
		displayedIncorrectGuesses.setColor(Color.RED);
		add(displayedIncorrectGuesses);
	}

}
