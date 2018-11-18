/*
 * File: Breakout.java
 * -------------------
 * Name: Romain Screve
 * Section Leader: Luciano Gonzalez
 * 
 * This file implements the game of Breakout.
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
	public static double VELOCITY_Y = 3.00;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Rectangle object for Paddle
	private static GRect PADDLE = null;

	// Oval object for Ball
	private static GOval BALL = null;

	// Rectangle object for bricks
	private static GRect BRICK = null;

	// Instantiates rgen, a RandomGenerator object
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// The ball's horizontal velocity.
	private static double VELOCITY_X = 0;

	// Number of user attempts in trying to remove all bricks
	private static int ATTEMPTS = 1;
	
	// Number of bricks removed
	private static int BRICKS_REMOVED = 0;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpBreakout();
		playBreakout();
		addMouseListeners();
	}
	
	/*
	 * mouseMoved listens for when the mouse moves, and moves the paddle horizontally based on the mouses x coordinate
	 */
	public void mouseMoved(MouseEvent e) {
		double xCoord = e.getX();
		if(xCoord < getWidth() - PADDLE_WIDTH/2 && xCoord > PADDLE_WIDTH/2) {
			PADDLE.setX(xCoord - PADDLE_WIDTH/2);
		}
	}

	/*
	 * setUpBreakout calls two methods, buildBricks and buildPaddles, which set up the game with 100 bricks and 1 paddle
	 */
	private void setUpBreakout() {
		buildBricks();
		buildPaddle();
	}

	/*
	 * buildBricks finds the proper top left brick's coordinates and builds 100 bricks based on that location, it also assigns every two rows with a specific color 
	 */
	private void buildBricks() {

		double xCoord = (getWidth() - (NBRICK_ROWS*BRICK_WIDTH + BRICK_SEP*(NBRICK_ROWS - 1)))/2;
		double yCoord = BRICK_Y_OFFSET;

		for(int col = 0; col < NBRICK_ROWS; col ++) {

			for(int row = 0; row < NBRICK_COLUMNS; row++) {
				BRICK = new GRect(xCoord + (row * (BRICK_WIDTH + BRICK_SEP)), yCoord + (col * (BRICK_HEIGHT + BRICK_SEP)), BRICK_WIDTH, BRICK_HEIGHT);
				BRICK.setFilled(true);
				add(BRICK);

				if(col % 10 == 0 || col % 10 == 1) {
					BRICK.setColor(Color.RED);
				} else if(col % 10 == 2 || col % 10 == 3) {
					BRICK.setColor(Color.ORANGE);
				} else if(col % 10 == 4 || col % 10 == 5) {
					BRICK.setColor(Color.YELLOW);
				} else if(col % 10 == 6 || col % 10 == 7) {
					BRICK.setColor(Color.GREEN);
				} else if(col % 10 == 8 || col % 10 == 9) {
					BRICK.setColor(Color.CYAN);
				} 
			}	
		}
	}
	
	/*
	 * buildPaddle creates a GRect object that will act as the paddle
	 */
	private void buildPaddle() {
		PADDLE = new GRect(getCenterX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		PADDLE.setFilled(true);
		add(PADDLE);
	}

	/*
	 * playBreakout calls three methods, which make a GOval object and drops it down towards the paddle at a random direction
	 */
	private void playBreakout() {
		createBall();
		setBallDrop();
		playBall();
		displayResult();
	}

	/*
	 * createBall creates a GOval object that will act as the ball
	 */
	private void createBall() {
		BALL = new GOval(getCenterX() - BALL_RADIUS, getCenterY() - BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2);
		BALL.setFilled(true);
		add(BALL);
	}
	
	/*
	 * dropBall sets the balls horizontal velocity to a random value between 1 and 3
	 */
	private void setBallDrop() {	
		VELOCITY_X = rgen.nextDouble(1.0,3.0);
		if(rgen.nextBoolean(0.5)) {
			VELOCITY_X = -1 * VELOCITY_X;
		}	
	}
	
	/*
	 * playBall changes the ball's direction if it hits a wall, brick, or the paddle
	 */
	private void playBall() {
		while(BALL.getBottomY() < getHeight()) {			
			BALL.move(VELOCITY_X, VELOCITY_Y);

			GObject collider = getCollidingObject();

			if(BALL.getRightX() > getWidth()) {
				VELOCITY_X = -VELOCITY_X;
			}

			if(BALL.getX() < 0) {
				VELOCITY_X = -VELOCITY_X;
			}

			if(BALL.getY() < 0) {
				VELOCITY_Y = -VELOCITY_Y;
			}

			if(BALL.getBottomY() > getHeight()) {
				remove(BALL);
			}
			
			if(collider == PADDLE) {
				VELOCITY_Y = -VELOCITY_Y;
			} else if(collider != null){
				VELOCITY_Y = -VELOCITY_Y;
				remove(collider);
				BRICKS_REMOVED++;
			}		
			pause(DELAY);
			
			if(BRICKS_REMOVED == NBRICK_COLUMNS*NBRICK_ROWS) {
				break;
			}
		}	
	}
	
	/*
	 * displayResult calculates whether the user successfully removed all the bricks within the allowed number of turn, and informs the user if they won or lost or if they still have more attempts  
	 */
	private void displayResult() {
		if(BRICKS_REMOVED == NBRICK_COLUMNS*NBRICK_ROWS) {
			GLabel winner = new GLabel("Congratulations! You've won Breakout!");
			winner.setLocation(getCenterX() - winner.getWidth()/2,  BRICK_Y_OFFSET/2 + winner.getAscent()/2);
			add(winner);
		} else if(ATTEMPTS == NTURNS) {
			GLabel loser = new GLabel("Bummer...you don't have any attempts left. Try again another day");
			loser.setLocation(getCenterX() - loser.getWidth()/2,  BRICK_Y_OFFSET/2 + loser.getAscent()/2);
			add(loser);
		} else {
			GLabel tryAgain = new GLabel("Try Again. Number of attempts left: " + (NTURNS - ATTEMPTS) + ". Click to start again");
			tryAgain.setLocation(getCenterX() - tryAgain.getWidth()/2,  BRICK_Y_OFFSET/2 + tryAgain.getAscent()/2);
			add(tryAgain);
			ATTEMPTS++;
			waitForClick();
			remove(tryAgain);
			playBreakout();
		}	
	}

	/*
	 * getCollidingObject checks if the ball is in contact with a brick or paddle, and returns the GObject that the ball just came in contact with
	 */
	private GObject getCollidingObject() {
		GObject collider = getElementAt(BALL.getX(), BALL.getY());
		if(collider != null) {
			return collider;
		}
		collider = getElementAt(BALL.getRightX(), BALL.getY());
		if(collider != null) {
			return collider;
		}
		collider = getElementAt(BALL.getRightX(), BALL.getBottomY());
		if(collider != null) {
			return collider;
		}
		collider = getElementAt(BALL.getX(), BALL.getBottomY());
		if(collider != null) {
			return collider;
		} else {
			return null;
		}
	}

}
