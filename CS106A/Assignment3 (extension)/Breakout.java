/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Breakout extends GraphicsProgram {
	
	/* Initializes the the mouse listeners */
	public void init() {
		addMouseListeners();
	}
	
	/* Runs the Breakout program */
	public void run() {
		setUpBricks();
		createPaddle();
		createBall();
		breakOutBegins();
	}
	

	/* Set up the bricks */
	private void setUpBricks(){
		for (int i=0;i<NBRICK_ROWS;i++){
			for (int j=0;j<NBRICKS_PER_ROW;j++){
				GRect rec = new GRect(j*(BRICK_WIDTH+BRICK_SEP),
						BRICK_Y_OFFSET+i*(BRICK_HEIGHT+BRICK_SEP),
						BRICK_WIDTH,BRICK_HEIGHT);
				rec.setFilled(true);
				fillColor(i,rec);
				add(rec);
			}
		}
	}
	
	/* Fill color for the bricks */
	private void fillColor(int i, GRect rec){
		if (i%10==0 || i%10==1)
			rec.setFillColor(Color.RED);
		else if (i%10==2 || i%10==3)
			rec.setFillColor(Color.ORANGE);
		else if (i%10==4 || i%10==5)
			rec.setFillColor(Color.YELLOW);
		else if (i%10==6 || i%10==7)
			rec.setFillColor(Color.GREEN);
		else if (i%10==8 || i%10==9)
			rec.setFillColor(Color.CYAN);
	}
	
	/* Create paddle on screen */	
	private void createPaddle() {
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
	}
	
	/* Create a MouseEvent to control paddle */
	public void mouseMoved (MouseEvent e){
		if ((e.getX()-PADDLE_WIDTH/2)>=0 && (e.getX()+PADDLE_WIDTH/2)<getWidth()){
			paddle.setLocation(e.getX()-PADDLE_WIDTH/2,
					getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	/* Create a ball on screen */
	private void createBall(){
		Ball.setColor(Color.WHITE);
		Ball.setFilled(true);
		Ball.setFillColor(Color.BLACK);
		add(Ball, getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS);
	}
	
	private void setBallToCenter(){
		Ball.setLocation(getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS);
	}
	
	/* Game really starts here */
	private void breakOutBegins() {
		turnStartPrompt();
		setBallInitialVelocity();
		while(true){
			checkWindowBounces();
			checkForCollision();
			/* Game Lose Condition: ball hits bottom */
			if((Ball.getY()+2*BALL_RADIUS)>HEIGHT){
				timesOfFallingToBottom++;
				if(timesOfFallingToBottom >= NTURNS){
					remove(Ball);
					gameLosePrompt();
					break;
				}else{
					setBallToCenter();
					turnStartPrompt();
					setBallInitialVelocity();
				}
			}
			/* Game Win Condition: no brick left */
			if(bricksCount==0){
				remove(Ball);
				gameWinPrompt();
				break;
			}
			nextMove();
		}
	}

	private void nextMove() {
		Ball.move(vx, vy);
		pause(PAUSE_TIME);
	}

	private void gameWinPrompt() {
		GLabel label = new GLabel("You Win: Score is " +
				(score + (NTURNS - timesOfFallingToBottom)*scoreMultiplier) + "!");
		label.setFont("Serif-24");
		label.setColor(Color.RED);
		add(label, getWidth()/2 - label.getWidth()/2,
				getHeight()/2 - label.getAscent()/2);
	}

	private void gameLosePrompt() {
		GLabel label = new GLabel("You Lose: Score is " + score + "!");
		label.setFont("Serif-24");
		label.setColor(Color.RED);
		add(label, getWidth()/2 - label.getWidth()/2,
				getHeight()/2 - label.getAscent()/2);
	}
	
	private void turnStartPrompt() {
		GLabel label = new GLabel("Click to Start: You Have " 
				+ (NTURNS - timesOfFallingToBottom) + " Turns!");
		label.setFont("Serif-24");
		label.setColor(Color.RED);
		add(label, getWidth()/2 - label.getWidth()/2,
				getHeight()/2 - label.getAscent()/2);
		waitForClick();
		remove(label);
	}

	private void checkWindowBounces() {
		if(Ball.getX()<0 || (Ball.getX()+2*BALL_RADIUS)>WIDTH)
			vx = -vx;
		if(Ball.getY()<0)
			vy = -vy;
	}
	
	private void checkForCollision() {
		GObject collider = getCollidingObject(Ball.getX(),Ball.getY(),BALL_RADIUS);
		if(collider!=null){
			if(collider==paddle){
				vy = -Math.abs(vy);
			}else{
			vy = -vy;
			remove(collider);
			bounceClip.play();
			bricksCount--;
			score += 100;
			}
		}
	}
	
	private GObject getCollidingObject(double x, double y, int r){
		for(int i=0;i<2;i++){
			for(int j=0;j<2;j++){
				GObject obj = getElementAt(x+i*r,y+j*r);
				if(obj!=null)
					return obj;
			}
		}
		return null;
	}
	
	private void setBallInitialVelocity() {
		vx = rgen.nextDouble(MIN_X_VELOCITY, MAX_X_VELOCITY);
		if (rgen.nextBoolean()) vx = -vx; //Make random x velocity
		vy = MAX_Y_VELOCITY;
	}
	
	/* Add bounce clip */	
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.wav");
	/* Create private instance variable: paddle */
	private GRect paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
	/* Create private instance variable: Ball */
	GOval Ball = new GOval(BALL_RADIUS,BALL_RADIUS);
	/* Total number of bricks*/
	private int bricksCount = NBRICKS_PER_ROW*NBRICK_ROWS;
	/* Random Generator */
	private RandomGenerator rgen = RandomGenerator.getInstance();
	/* Variables of x & y velocities */
	private double vx,vy;
	/* Count for lose condition */
	private int timesOfFallingToBottom = 0;
	/* Count for score */
	private int score = 0;
	
	/* Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;
	/* Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;
	/* Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;
	/* Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;
	/* Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;
	/* Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;
	/* Separation between bricks */
	private static final int BRICK_SEP = 4;
	/* Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;
	/* Height of a brick */
	private static final int BRICK_HEIGHT = 8;
	/* Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;
	/* Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;
	/* Number of turns */
	private static final int NTURNS = 3;
	/* Maximum and Minimum velocities in x direction */	
	private static final double MIN_X_VELOCITY = 1.5;
	private static final double MAX_X_VELOCITY = 3;
	private static final double MAX_Y_VELOCITY = 3;
	/* Pause time for ball movement*/
	private static final int PAUSE_TIME = 10;
	/* Score multiplier */
	private static final int scoreMultiplier = 2000;
}


