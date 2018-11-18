/*
 * File: Breakout.java
 * -------------------
 * Name: Anna Ekholm
 * Section Leader: Marilyn Zhang
 * 
 * Plays the game breakout
 */

/**
 * THINGS I WANT MY PROGRAM TO DO
 * ------------------------------
 * - Make the ball someones face
 * - Play background music
 * - fix the draw rectangles function so that 
 * it works for odd numbers FIXED IN THIS PROGRAM
 * I JUST NEED TO ADD IT TO TH EXTENSION
 *  - change starting coordinates of the ball
 *  - fix sticky paddle FIXED (i think gotta do some more tests)
 *  also need to add the sticky paddle fix to the extension
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
	public static final double PADDLE_HEIGHT = 50;

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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-30";
	
	// create the paddle so that it can be accessed outside of any method
	public GRect paddle = null;
	
	// create the ball so that it can be accessed in any method
	public GOval ball = null;
	
	// add the random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();


	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setUpScreen();
		play();
	}
	
	/*
	 * Method: Play
	 * --------------------------
	 * Plays the game
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * none
	 */
	private void play() {
		int numberOfBricks = NBRICK_ROWS * NBRICK_COLUMNS;
		if (numberOfBricks != 0) {
		for (int round = 0; round < NTURNS; round ++) {
			createBall();
			addBallToScreen();
			// initialize ball motion
			double vy = VELOCITY_Y * (round/2 + 1);
			double vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			//make the ball bounce off the sides of the screen
			while (numberOfBricks != 0) {
				if(hitLeftWall() || hitRightWall()) {
					vx = -vx;
				}
				if(hitTopWall()) {
					vy = -vy;
				}
				if(hitBottomWall()) {
					break;
				}
				if (hit()) {
					GObject collider = getCollidingObject();
					vy = -vy;
					if (collider != paddle) {
						remove(collider);
					} else {
						double yLocOfHit = ball.getY();
						if (yLocOfHit < getHeight() + PADDLE_HEIGHT) {
							for (int i = 0; i < - PADDLE_HEIGHT/vy; i++) {
								ball.move(vx, vy);
								pause(DELAY);
							}
						}
					}
					
				}
				ball.move(vx, vy);
				pause(DELAY);
			}
			remove(ball);
		}
		}
	}



	/*
	 * Method: Get Colliding Object 
	 * --------------------------------
	 * Gets whatever object the ball has collided 
	 * with
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * obj (GObject) - whatever the ball has collided 
	 * with, or null if the ball did not collide with
	 * anything
	 */
	private GObject getCollidingObject() {
		double xLoc = ball.getX();
		double yLoc = ball.getY();
		GObject obj = getElementAt(xLoc, yLoc);
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(xLoc + 2*BALL_RADIUS, yLoc);
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(xLoc, yLoc + 2*BALL_RADIUS);
		if (obj != null) {
			return obj;
		}
		obj = getElementAt(xLoc + 2*BALL_RADIUS, yLoc + 2*BALL_RADIUS);
		if (obj != null) {
			return obj;
		}
		return obj;
		
	}
	
	/*
	 * Method: Hit
	 * --------------------------------
	 * Returns true if one of the corners of the ball
	 * hit something
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean hit() {
		return topLeftHit() || bottomLeftHit() || topRightHit() || bottomRightHit();
	}
	
	
	/*
	 * Method: Bottom Right Hit
	 * ----------------------
	 * Checks to see if the ball has hit in its bottom right corner
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean bottomRightHit() {
		double xLoc = ball.getX() + 2*BALL_RADIUS;
		double yLoc = ball.getY() + 2*BALL_RADIUS;
		if (getElementAt(xLoc, yLoc) != null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Method: Top Right Hit
	 * ----------------------
	 * Checks to see if the ball has hit in its top right corner
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	
	private boolean topRightHit() {
		double xLoc = ball.getX() + 2*BALL_RADIUS;
		double yLoc = ball.getY();
		if (getElementAt(xLoc, yLoc) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Method: Bottom Left Hit
	 * ----------------------
	 * Checks to see if the ball has hit in its bottom left corner
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean bottomLeftHit() {
		double xLoc = ball.getX();
		double yLoc = ball.getY() + 2*BALL_RADIUS;
		if (getElementAt(xLoc, yLoc) != null) {
			return true;
		} else {
			return false;
		}
	}


	/*
	 * Method: Top Left Hit
	 * ----------------------
	 * Checks to see if the ball has hit in its top left corner
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean topLeftHit() {
		double xLoc = ball.getX();
		double yLoc = ball.getY();
		if (getElementAt(xLoc, yLoc) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	/*
	 * Method: Hit Bottom Wall
	 * ----------------------
	 * checks if the ball has hit the bottom wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean hitBottomWall() {
		return ball.getY() >= getHeight() - 2*BALL_RADIUS;
	}


	/*
	 * Method: Hit Top Wall
	 * ----------------------
	 * checks if the ball has hit the top wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}


	/*
	 * Method: Hit Right Wall
	 * ----------------------
	 * checks if the ball has hit the right wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - 2*BALL_RADIUS;
	}


	/*
	 * Method: Hit Left Wall
	 * ----------------------
	 * checks if the ball has hit the right wall
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * a boolean whether the ball has collided
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0 ;
	}


	/*
	 * Method: Mouse Moved
	 * --------------------------
	 * Tells the program what to do if the
	 * mouse is moved
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if (e.getX() >= getWidth() - PADDLE_WIDTH/2) {
			x = getWidth() - PADDLE_WIDTH;
		} else if (e.getX()<=PADDLE_WIDTH/2) {
			x = 0;
		}
		paddle.setLocation(x, y);
		
	}
	
	/*
	 * Method: Set Up Screen 
	 * -----------------------------
	 * Adds all of the bricks to the top of the 
	 * screen and the initial paddle
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * bricks and paddles on the screen
	 */
	private void setUpScreen() {
		createTopBricks();
		paddle = createPaddle();
		addPaddleToScreen();
		
	}
	
	/*
	 * Method: Add Ball to Screen
	 * -----------------------------
	 * Adds a ball to the center of the screen
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * ball in the center of the screen
	 */
	private void addBallToScreen() {
		double initXCord = getWidth()/2 - BALL_RADIUS;
		double initYCord = getHeight()/2 - BALL_RADIUS;
		add(ball, initXCord, initYCord);
	}


	/*
	 * Method: Create Ball
	 * ---------------------------
	 * Makes the ball
	 * ---------------------------
	 * Inputs: none
	 * Outputs: none
	 */
	private void createBall() {
		ball = new GOval (2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
	}


	/*
	 * Method: Add Paddle to screen
	 * ---------------------------
	 * Adds the paddle to the center of the screen
	 * in terms of its x coordinate 
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * paddle in the center of the screen
	 */
	private void addPaddleToScreen() {
		double initXCord = getWidth()/2 - PADDLE_WIDTH/2;
		double initYCord = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, initXCord, initYCord);
	}


	/*
	 * Method: Create paddle
	 * ----------------------------
	 * Makes a black paddle but dos not add it to the screen
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * paddle (GRect) - the paddle
	 */
	private GRect createPaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}


	/*
	 * Method: Create Top Bricks
	 * ------------------------------
	 * Adds all of the bricks to the top of the screen
	 * ---------------------------
	 * Inputs: 
	 * none
	 * Outputs: 
	 * Colored bricks at the top of the screen
	 */
	private void createTopBricks() {
		double widthOfARow = NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS-1)*BRICK_SEP;
		double xOffset = (getWidth() - widthOfARow)/2;
		
		double initialxCord = xOffset;
		
		double initialyCord = BRICK_Y_OFFSET;
		
		for (int col = 0; col < NBRICK_COLUMNS; col ++) {
			for (int row = 0; row < NBRICK_ROWS; row ++) {
				double xCord = initialxCord + col*(BRICK_WIDTH + BRICK_SEP);
				double yCord = initialyCord + row*(BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				addColor(brick, row);
				add(brick, xCord, yCord);
			}
		}
	}

	/*
	 * Method: Add Color
	 * --------------------------
	 * Adds the correct color for the row of bricks 
	 * that you're trying to add to the screen
	 * ---------------------------
	 * Inputs: 
	 * brick (GRect) - the brick that you want to color
	 * row (integer) - the row that the brick is in
	 * Outputs: 
	 * the brick has the correct color
	 */
	private void addColor(GRect brick, int row) {
		row %= 10;
		if (row == 0 || row == 1) {
			brick.setColor(Color.RED);
		} else if (row == 2 || row == 3) {
			brick.setColor(Color.ORANGE);
		} else if (row == 4 || row == 5) {
			brick.setColor(Color.YELLOW);
		} else if (row == 6 || row == 7) {
			brick.setColor(Color.GREEN);
		} else {
			brick.setColor(Color.CYAN);
		}
	}

}
