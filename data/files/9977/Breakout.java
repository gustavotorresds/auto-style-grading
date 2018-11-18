/*
 * File: Breakout.java
 * -------------------
 * Name: Daniel Turley
 * Section Leader: Kathryn Rydberg
 * 
 * Implements the game of Breakout for a specified number of turns.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
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

	// The ball's minimum and maximum horizontal velocity; 
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	/* Declare instance variables */
	GRect paddle = null;   // Instance variable for the paddle
	GOval ball = null;   // Instance variable for the ball
	private double vx, vy; // Instance variable ball velocity in x and y directions
	private RandomGenerator rgen = RandomGenerator.getInstance(); // Random number generator
	private double bricksLeft = 0; // instance variable to count number of bricks removed

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Starts mouse tracking for the paddle. 
		addMouseListeners();
		
		// Initializes and plays the game, for the specified number of turns.		
		for (int i=0; i<NTURNS; i++) {
			setupGame();		
			playGame();
		}				
	}

	/**
	 * Method: Setup Game
	 * ------------------
	 * Initializes the breakout game, resetting the bricks, paddle, ball, and velocity. 
	 */		
	private void setupGame() {
		removeAll();  			// Clears screen from previous turn
		setupBricks();			// Resets the brick array
		paddle = makePaddle();	// Creates the paddle
		addPaddleToGame();		// Places the paddle at its starting point
		ball = makeBall();		// Creates the ball
		addBallToGame();		// Add the ball at its starting point
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);	// Generates the x-velocity for this turn
		if (rgen.nextBoolean(0.5)) vx = -vx;					
		vy = VELOCITY_Y;	
		bricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;	// Resets the number of remaining bricks
	}
	
	/**
	 * Method: Play Game
	 * ------------------
	 * Plays the breakout game once by starting and running the animation loop. 
	 */		
	private void playGame() {
		waitForClick(); // Click to start
		boolean continueGame = true;
		while(continueGame) {		// Start animation loop
			ball.move(vx, vy); 		// Moves ball 
			checkForWall();    		// Checks for a hit on a wall and changes velocity accordingly
			checkForCollision();	// Checks for a brick or paddle collision			
			continueGame = checkForGameEnd(); // Checks for game end, by ball hitting bottom wall or all bricks removed
			pause(DELAY);
		}
		remove(paddle);
		remove(ball);
	}
	
	/**
	 * Method: Mouse Moved
	 * -------------------
	 * Moves the paddle horizontally with the mouse. This method is called anytime the mouse moves in the
	 * program screen.
	 */
	 public void mouseMoved(MouseEvent e) {
	 	double x = e.getX() - PADDLE_WIDTH/2;
	 	if (x > getWidth()- PADDLE_WIDTH) { // Stops paddle from leaving the right of screen
	 		x = getWidth()- PADDLE_WIDTH;
	 	}
	 	if (x < 0) {						// Stops paddle leaving the left of screen
		 	x = 0;
	 	}
		paddle.setLocation(x, getHeight()- PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	 }
	
	/**
	 * Method: Add Paddle to Game
	 * -------------------
	 * Adds the instance variable paddle to the bottom center of the
	 * screen.
	 */
	private void addPaddleToGame() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = (getHeight()- PADDLE_Y_OFFSET - PADDLE_HEIGHT / 2);
		add(paddle, x, y);
	}	
		
	/**
	 * Method: Make Paddle
	 * -------------------
	 * Creates a paddle of specified color and size.
	 */
	private GRect makePaddle() {	
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}

	/**
	 * Method: Add Ball to Game
	 * -------------------
	 * Adds the instance variable ball to the center of the
	 * screen.
	 */
	private void addBallToGame() {
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		add(ball, x, y);
	}	
		
	/**
	 * Method: Make Ball
	 * -------------------
	 * Creates a circle of specified color and size.
	 */
	private GOval makeBall() {	
		GOval ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		return ball;
	}
	

	/**
	 * Method: Setup Bricks
	 * ------------------
	 * Resets the bricks for the start of a new game.  
	 */	
	private void setupBricks() {
		// Uses a color array to cycle through colors, can allow for changes in number of brick rows 
		Color [] brickColors = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN };
		for (int i=1; i<=NBRICK_ROWS; i++) {
			double yStart = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * (i - 1);
			int currentColor = ((i - 1) / 2) % 5; // If number of rows is more than 10, repeats the color sequence
			placeBrickRow(yStart, brickColors[currentColor]);
		}		
	}
	
	/**
	 * Method: Place Brick Row
	 * ------------------
	 * Places a row of bricks, centered on the screen, filled with the specified color.  
	 * Input: y coordinate for the top of the brick row, brick color. 
	 * Output: Row of centered, filed, colored bricks.   
	 */	
	private void placeBrickRow(double yStart, Color brickColor) {
		for (int i=0; i<NBRICK_COLUMNS; i++) {
			double xStart=(getWidth() - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) - BRICK_SEP) / 2 + i * (BRICK_WIDTH + BRICK_SEP);    
			GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(brickColor);
			add(brick, xStart, yStart);
		}
	}

	/**
	 * Method: Check For Wall
	 * -----------------------
	 * Checks whether the ball has hit the top, left or right wall, and changes velocity accordingly. 
	 */
	private void checkForWall() {
		if(ball.getX() >= getWidth() - ball.getWidth() || ball.getX() <= 0) { // Check left or right wall
			vx = -vx;
		}
		if(ball.getY() <= 0) { // Check top wall
			vy = -vy;
		}
	}

	/**
	 * Method: Check For Game End
	 * -----------------------
	 * Checks whether the game has ended, either by hitting bottom wall or removing all bricks. Returns a boolean 
	 * that is true if the game continues, false if it is over. 
	 */
	private boolean checkForGameEnd() {
		boolean continueGame = ball.getY() < (getHeight() - ball.getHeight()); // Checks for bottom wall
		if (bricksLeft == 0) { // Checks for no bricks left
			continueGame = false;
		}
		return continueGame;
	}
		
	/**
	 * Method: Check For Collision
	 * -----------------------
	 * Checks whether the ball has hit another object, i.e. paddle or brick, and acts accordingly.
	 * If a paddle is hit, the ball bounces back. If a brick is hit, it disappears, and the remaining brick count decreases. 
	 */
	private void checkForCollision() {
		GObject collider = getCollidingObject();
		if (collider != null) {
			// Changes direction only if ball at top of paddle when collision occurs, otherwise ball continues down and game ends
			if (ball.getY() <= (paddle.getY() - ball.getHeight() + vy)) { 
				vy = -vy;
			}
			if (collider != paddle) {
				remove(collider);
				bricksLeft--;
			}
		}
	
	}
	
	/**
	 * Method: Get Colliding Object
	 * -----------------------
	 * Checks whether the ball is in contact with another object, i.e. paddle or brick, and returns that object.
	 * Otherwise NULL is returned. 
	 */
	private GObject getCollidingObject() {
		GObject topLeftCorner = getElementAt(ball.getX(), ball.getY());
		if (topLeftCorner != null) {
			return topLeftCorner;
		} else {
			GObject topRightCorner = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
			if (topRightCorner != null) {
				return topRightCorner;
			} else {
				GObject bottomLeftCorner = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
				if (bottomLeftCorner != null) {
					return bottomLeftCorner;
				} else {
					GObject bottomRightCorner = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);			
					return bottomRightCorner;
				}
			}
		}
	}

}
