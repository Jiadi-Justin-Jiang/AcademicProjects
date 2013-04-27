/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

// Name: 
// Section Leader: 

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

	/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int[] dice = new int[N_DICE];
	private int category;
	private int[][] score;
	private boolean[][] categorySelected;
	
	public void run() {
		/* You shouldn't need to change this */
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	
	private void playGame() {
		score = new int[nPlayers][N_CATEGORIES];
		categorySelected = new boolean[nPlayers][N_CATEGORIES];
		for ( int i = 0; i < N_TURNS; i++ ) {
			for ( int j = 0; j < nPlayers; j++ ) {
				rollDice( j + 1 );
				updatePoints( j + 1 );
			}
		}	
		updateFinalResult();
	}
	
	
	private void rollDice( int player ) {
		display.printMessage(playerNames[player -1] + "'s turn! Click the \"Roll Dice\" Button to roll the dice");
		display.waitForPlayerToClickRoll( player );
		for ( int i = 0; i < N_ROLL_CHANCES; i++ ) {
			if ( i == 0 ) {
				for ( int j = 0; j < N_DICE; j++ ) {
					dice[j] = rgen.nextInt( MIN_DICE, MAX_DICE );
				}
			} else {
				display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\"");
				display.waitForPlayerToSelectDice();
				for ( int j = 0; j < N_DICE; j++ ) {
					if ( display.isDieSelected( j ) ) {
						dice[j] = rgen.nextInt( MIN_DICE, MAX_DICE );
					}
				}
			}
			display.displayDice( dice );
		}
	}
	

	private void updatePoints( int player ) {
		
		/* Select and prevent from reselect the same category */
		display.printMessage("Select a category for this roll");
		category = display.waitForPlayerToSelectCategory();
		while ( categorySelected[ player - 1 ][ category - 1 ] ) {
			category = display.waitForPlayerToSelectCategory();
		}
		categorySelected[ player - 1 ][ category - 1 ] = true;
		
		/* Calculate and update points */
		if ( categoriesMatch() ) {
			calculateScore( player );
			display.updateScorecard( category, player, score[ player - 1 ][ category - 1 ] );
			display.updateScorecard( TOTAL, player, score[ player - 1 ][ TOTAL - 1 ] );
		} else {
			display.updateScorecard( category, player, 0 );
		}
	}
	
	
	private void updateFinalResult() {
		int player;
		int highestScore = score[0][TOTAL - 1];
		String winnerNames = playerNames[0];
		for ( int i = 0; i < nPlayers; i++ ) {
			if ( i != 0  ) {
				if ( score[i][TOTAL-1] > highestScore ) {
					highestScore = score[i][TOTAL-1];
					winnerNames = playerNames[i];
				} else if ( score[i][TOTAL-1] == highestScore ){
					winnerNames += " & " + playerNames[i]; 
				}
			}
			player = i + 1;
			display.updateScorecard( UPPER_SCORE, player, score[ player - 1 ][ UPPER_SCORE - 1 ] );
			display.updateScorecard( LOWER_SCORE, player, score[ player - 1 ][ LOWER_SCORE - 1 ] );
			display.updateScorecard( UPPER_BONUS, player, score[ player - 1 ][ UPPER_BONUS - 1 ] );
			display.updateScorecard( TOTAL, player, score[ player - 1 ][ TOTAL - 1 ] );
		}
		display.printMessage("Congratulations, " + winnerNames + "! You are the winner(s) with a total score of " + highestScore + "!");
	}
	
	
	private void calculateScore( int player ) {
		
		/* calculate upper scores */
		if ( category == ONES || category == TWOS || category == THREES || 
		     category == FOURS || category == FIVES || category == SIXES ) {
			for ( int i = 0; i < N_DICE; i++ ) {
				if ( dice[i] == category ) {
					score[ player - 1 ][ category - 1 ] += dice[i];
				}
			}
			score[ player -1 ][ UPPER_SCORE -1 ] += score[ player - 1 ][ category - 1 ];
			if ( score[ player - 1 ][ UPPER_SCORE - 1 ] >= 63) {
				score[ player - 1 ][ UPPER_BONUS - 1 ] = UPPER_BONUS_SCORE;
			}
			
		/* calculate lower scores */	
		} else {
			if ( category == THREE_OF_A_KIND || category == FOUR_OF_A_KIND || category == CHANCE ) {
				for ( int i = 0; i < N_DICE; i++ ) {
					score[ player - 1 ][ category - 1 ] += dice[i];
				}
			} else if ( category == FULL_HOUSE ) {
				score[ player - 1 ][ category - 1 ] = FULL_HOUSE_SCORE;
			} else if ( category == SMALL_STRAIGHT ) {
				score[ player - 1 ][ category - 1 ] = SMALL_STRAIGHT_SCORE;
			} else if ( category == LARGE_STRAIGHT ) {
				score[ player - 1 ][ category - 1 ] = LARGE_STRAIGHT_SCORE;
			} else { // YAHTZEE 
				score[ player - 1 ][ category - 1 ] = YAHTZEE_SCORE;
			}
			score[ player - 1 ][ LOWER_SCORE - 1 ] += score[ player - 1 ][ category - 1 ];
		}
		
		/* calculate total scores */	
		score[ player -1 ][ TOTAL -1 ] = score[ player - 1 ][ LOWER_SCORE - 1 ] + 
										 score[ player - 1 ][ UPPER_SCORE - 1 ] + 
										 score[ player - 1 ][ UPPER_BONUS - 1 ];
	}


	/* Equivalent to YahtzeeMagicStub.checkCategory method */
	private boolean categoriesMatch() {
		/* 
		 * diceValue relates to number of dice of each value
		 * example: // Full House
		 * dice = {3,3,5,5,5}
		 * diceValue = {0,0,2,0,3,0}
		 */
		int[] diceValue = new int[MAX_DICE];
		for ( int i = 0; i < N_DICE; i++ ) {
			diceValue[ dice[i] - 1 ]++;
		}
		for ( int i = 0; i < MAX_DICE; i++ ) {
			print(diceValue[i]);
		}
		println("");
		
		if ( category == ONES || category == TWOS || category == THREES || 
			 category == FOURS || category == FIVES || category == SIXES || category == CHANCE ) {
			return true;
		} else if ( category == LARGE_STRAIGHT ) {
			if ( isLargeStraight( diceValue ) ) {
				return true;
			}
		} else if ( category == SMALL_STRAIGHT ) {
			if ( isLargeStraight( diceValue ) ) {
				return true;
			} else if ( isSmallStraight( diceValue ) ) {
				return true;
			}
		} else if ( category == FULL_HOUSE ){
			for ( int i = 0; i < diceValue.length; i++ ) {
				if ( i != ( diceValue.length - 1 ) && diceValue[i] == FULL_HOUSE_TWOS ) {
					println(i+10);
					for ( int j = i + 1; j < diceValue.length; j++ ) {
						if ( diceValue[j] == FULL_HOUSE_THREES ) {
							return true;
						}
					}
				}
				if ( i != ( diceValue.length - 1 ) && diceValue[i] == FULL_HOUSE_THREES ) {
					for ( int j = i + 1; j < diceValue.length; j++ ) {
						if ( diceValue[j] == FULL_HOUSE_TWOS ) {
							return true;
						}
					}
				}
			}
		} else if ( category == THREE_OF_A_KIND ) {
			for ( int i = 0; i < diceValue.length; i++ ) {
				if ( diceValue[i] >= THREES_NUM ) {
					return true;
				}
			}
		} else if ( category == FOUR_OF_A_KIND ) {
			for ( int i = 0; i < diceValue.length; i++ ) {
				if ( diceValue[i] >= FOURS_NUM ) {
					return true;
				}
			}
		} else { // YAHTZEE
			for ( int i = 0; i < diceValue.length; i++ ) {
				if ( diceValue[i] >= YAHTZEE_NUM ) {
					return true;
				}
			}
		} 
		return false;
	}
		
	private boolean isSmallStraight(int[] diceValue) {
		return ( allDiceValuesAreNoLargerThanTwo ( diceValue ) &&
				 ( diceValue[0] == 0 && diceValue[1] == 0 ) ||
				 ( diceValue[0] == 0 && diceValue[ diceValue.length - 1 ] == 0 ) ||
				 ( diceValue[ diceValue.length - 1 ] == 0 && diceValue[ diceValue.length - 2 ] == 0 ) );
	}

	private boolean isLargeStraight ( int[] diceValue ) {
		return ( allDiceValuesAreNoLargerThanOne( diceValue ) && ( diceValue[0] == 0 || diceValue[ MAX_DICE - 1 ] == 0 ) );
	}
	
	private boolean allDiceValuesAreNoLargerThanTwo(int[] diceValue) {
		for ( int i = 0; i < diceValue.length; i++ ) {
			if ( diceValue[i] > 2 ) {
				return false;
			}
		}
		return true;
	}

	private boolean allDiceValuesAreNoLargerThanOne( int[] diceValue ) {
		for ( int i = 0; i < diceValue.length; i++ ) {
			if ( diceValue[i] > 1 ) {
				return false;
			}
		}
		return true;
	}


	
}