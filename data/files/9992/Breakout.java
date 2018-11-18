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

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//The paddle
	GRect paddle = null;
	
	//This is the integer counter of how many bricks are left.
	private int i = NBRICK_ROWS * NBRICK_COLUMNS;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//velocity constants for the bullet
	private double vx, vy = VELOCITY_Y;
	
	//For the life counter extension I did.
	GLabel lives = null;
	
	//This adds a bounce effect every time a bounce is made. This is a minor extension
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	//This is the game breakout, click run and find out what it does.
	public void run() {
		for (int n = 0; n < NTURNS; n++) {
			// Set the window's title bar text
			setTitle("CS 106A Breakout");

			// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
			// and getHeight() to get the screen dimensions, not these constants!
			setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

			/* You fill this in, along with any subsidiary methods */
			setup();
			//Sets up the famous bullet 
			GOval bullet = new GOval(getWidth()/2, getHeight()/2, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
			bullet.setFilled(true);
			add(bullet);
			
			 //This is an extension I decided on, it's a life counter
			double lifecount = NTURNS - n;
			lives = new GLabel("Lives: " + lifecount);
			lives.setLocation(getWidth()-lives.getWidth() - 5, lives.getAscent());
			add(lives);
			
			//randomizes the initial x-speed and direction.
			vx = rgen.nextDouble(1.0, 3.0);
		
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			//initiates game
			waitForClick();
			
			//The animation loop 
			while (true) {
				bullet.move(vx, vy);
				pause(DELAY);
				//see further down to find the definition of this method
				bulletBoundry(bullet);
				//Ditto
				bulletBrickBouncing(bullet);
				
				//Below takes care of the case when the ball goes to the bottom screen
				if (bullet.getY() >= getHeight()) {
					break;
				}
			
				//This takes care of the case if the game is won
				if (i == 0) {
					break;
					
				}
		    
			}
			remove(bullet);
			if (i ==0 ) {
				//This is all done when the game is won
				GLabel winner = new GLabel("YOU WIN!!!", getWidth()/2, getHeight()/2);
				winner.setLocation(getWidth()/2-winner.getWidth()/2, getHeight()/2);
				add(winner);
				//starts the game again if you have more lives
				waitForClick();
				remove(winner); 
			} else {
				//The case for if you lose the game before running out of lives
				GLabel tryAgain = new GLabel("TRY AGAIN?");
				tryAgain.setLocation(getWidth()/2-tryAgain.getWidth()/2, getHeight()/2);
				add(tryAgain);
				waitForClick();
				remove(tryAgain);
			}
			//Cleans up the screen for when you restart the game
			remove(paddle);
			remove(lives);
		}
		
		//This is for when you've lost the game and run out of lives
		if (i > 0) {
			GLabel loser = new GLabel("JUST KIDDING. YOU LOSE :'(", getWidth()/2, getHeight()/2);
			loser.setLocation(getWidth()/2-loser.getWidth()/2, getHeight()/2);
			add(loser);
		}
	}
	
	//This method provides the conditions so that the bullet hits and bounces off of bricks
	private void bulletBrickBouncing(GOval oval) {
		GObject collObjtOne = getElementAt(oval.getX(), oval.getY());
		GObject collObjtTwo = getElementAt(oval.getX() + 2 * BALL_RADIUS, oval.getY() + 2 * BALL_RADIUS);
		GObject collObjtThree = getElementAt(oval.getX(), oval.getY() + 2 * BALL_RADIUS);  
		GObject collObjtFour = getElementAt(oval.getX() + 2 * BALL_RADIUS, oval.getY());
		
		if (collObjtTwo == paddle || collObjtThree == paddle) {
		    vy = -vy;
		    bounceClip.play();
		}
		
		if (collObjtThree == collObjtOne) {
			if (collObjtTwo != paddle && collObjtTwo != null && collObjtTwo != lives) {
				remove(collObjtTwo);
				vy = -vy;
				//each of these i--'s removes one from the counter so that when all the bricks are gone it activates 
				//the "win" screen
				i--;
				bounceClip.play();
			}
		}
		
		if (collObjtThree != paddle && collObjtThree != null && collObjtThree != lives) {
			remove(collObjtThree);
			vy = -vy;
			i--;
			bounceClip.play();
		}
		
		if (collObjtFour != paddle && collObjtFour != null && collObjtFour != lives) {
			remove(collObjtFour);
			vy = -vy;
			i--;
			bounceClip.play();
		}
	}
	
	//This creates a method that takes in an oval as input and makes sure that
	// it bounces off of all but the bottom wall when animated.
	private void bulletBoundry(GOval oval) {
		
		//Left and Right walls
		if (oval.getX() + 2 * BALL_RADIUS >= getWidth() || oval.getX() <= 0) {
			vx = -vx;
			bounceClip.play();
		}
		
		//Top wall
		if (oval.getY() <= 0) {
			vy = -vy;
			bounceClip.play();
		}	
		
		
	}
	
	//This method just sets up the screen to have a paddle and colored rectangles, but doesn't actually
	// animate anything
	private void setup() {
		double x = BRICK_SEP;
		double y = BRICK_Y_OFFSET;
		createRectangleRow(x, y);		
		paddle = new GRect(getWidth()/2-PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	//This method just makes the paddle track the mouse movements in the x direction
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		add(paddle, mouseX - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET);
		
		if (paddle.getX() >= getWidth() - PADDLE_WIDTH) {
			add(paddle, getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		}
		
		if (paddle.getX() <= 0) {
			add(paddle, 0, getHeight() - PADDLE_Y_OFFSET);
		}
		
	}
	
	//This method just uses a double for loop to create the columns and rows of bricks	
	private void createRectangleRow(double p, double f) {
		for (int a = 0; a < NBRICK_COLUMNS; a++) {
				for (int i = 0; i < NBRICK_ROWS; i++) {
					
					//accounts for the increase in x location each iteration of the for loops
					double v = (i * BRICK_WIDTH) + (i * BRICK_SEP);
					
					//accounts for each increase in the y location each iteration of the for loops
					double w = (a * BRICK_HEIGHT) + (a * BRICK_SEP);
					GRect rect = new GRect(p + v, f + w, BRICK_WIDTH, BRICK_HEIGHT);
					fillRectangleRow(rect, a);
					add(rect);
				}
			}
		}

	//This method fills a rectangle given a rectangle and a number, used in cases where you want to
	//fill rectangle rows using for loops
	private void fillRectangleRow(GRect rect, int x) {
		rect.setFilled(true);
	
		//The following if statements control the color of the rectangle rows
		if (x < 2) {
			rect.setColor(Color.RED);
		}
	
		if (1 < x && x < 4) {
			rect.setColor(Color.ORANGE);
		}
	
		if (3 < x && x < 6) {
			rect.setColor(Color.YELLOW);
		}
	
		if (5 < x && x < 8) {
			rect.setColor(Color.GREEN);
		}
	
		if (7 < x && x < 10) {
			rect.setColor(Color.CYAN);
		}
	}

}
