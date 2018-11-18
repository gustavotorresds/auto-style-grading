/*
 * File: Breakout.java
 * -------------------
 * Name: Ana Martins
 * Section Leader:Luciano Gonzales
 * 
 * This program runs the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class Breakout extends GraphicsProgram {

	/************************************************
	 *                  Constants                   *
	 ************************************************/

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

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	/************************************************
	 *             Instance Variables               *
	 ************************************************/

	// Instance variable: paddle
	private GRect paddle = null;

	//Instance variable: ball 
	private GOval ball = null;

	//Instance variable: random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Instance variable: number of bricks removed before the game starts
	private int bricksRemoved = 0;

	// Instance variable: number of turns the user has
	private int turns = NTURNS;

	/**
	 * Method: Run
	 * -----------
	 * Execution starts here
	 *
	 */
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setUpWorld();
		// Play breakout until game is over or user wins
		while(!gameOver() && !userWins()) {
			playBreakout();
		}
	}

	/**
	 * Method: Set Up World
	 * -----------
	 * Sets up the world with bricks and paddle
	 */

	private void setUpWorld() {
		setUpBricks();
		addPaddle();
	}

	/**
	 * Method: Play Breakout
	 * -----------
	 * Runs one turn in the game of Breakout
	 */

	private void playBreakout() {
		addBall();
		// sets velocity variables
		double vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		double vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		// game starts here and stops when ball hits bottom wall or user wins
		while(!hitBottomWall(ball) && !userWins()) {
			// updates velocity 
			if(hitLeftWall(ball) || hitRightWall(ball)) vx = -vx;
			if (hitTopWall(ball)) vy = -vy;
			// updates ball
			ball.move(vx, vy);
			// pauses
			pause(DELAY);
			// checks for colliding object
			getCollidingObject ();
			// manages collisions (changes velocity)
			vy = manageCollisions(vy);
		}
		// When ball hits bottom it is removed and user loses one turn
		remove(ball);
		turns -= 1;
	}

	/**
	 * Method: Game Over
	 * -----------
	 * Determines if game is over 
	 * True if user was left with 0 turns
	 * Number of turns - Number of times ball hit bottom = 0
	 *
	 */

	private boolean gameOver() {
		return turns == 0;
	}

	/**
	 * Method: User Wins
	 * -----------
	 * Determines if user wins
	 * True if user gets rid of all the bricks
	 */

	private boolean userWins() {
		int totalBricks = NBRICK_ROWS*NBRICK_COLUMNS;
		return bricksRemoved == totalBricks;
	}

	/**
	 * Method: Build Brick
	 * -----------
	 * Builds ine brick and sets color depending on row
	 */

	private void buildBrick(int row, int col) {
		// position variables for each brick
		double x = col*BRICK_WIDTH + col*BRICK_SEP;
		double y = BRICK_Y_OFFSET + row*BRICK_HEIGHT + row*BRICK_SEP; 
		double center = getCenterX() - BRICK_WIDTH*(NBRICK_COLUMNS/2) - NBRICK_COLUMNS/2*BRICK_SEP;
		// Create new brick
		GRect brick = new GRect (x+center, y, BRICK_WIDTH, BRICK_HEIGHT); 
		brick.setFilled(true);
		// set color depending on which row is being built
		if (row < 2) {
			brick.setColor(Color.RED);
		} else if (row < 4) {
			brick.setColor(Color.ORANGE);
		} else if (row < 6) {
			brick.setColor(Color.YELLOW);
		} else if (row < 8) {
			brick.setColor(Color.GREEN);
		} else if (row < 10) {
			brick.setColor(Color.CYAN);
		}
		add(brick);
	}

	/**
	 * Method: Set Up Bricks
	 * -----------
	 * Builds 10 rows of 10 bricks in center of canvas
	 */


	private void setUpBricks() { 
		// build 10 rows of bricks
		for (int row = 0; row < NBRICK_ROWS; row++) {
			// build one row of 10 bricks
			for (int col = 0; col < NBRICK_COLUMNS; col++ ) {
				buildBrick(row, col);
			}
		}

	}

	/**
	 * Method:Add Paddle
	 * -----------
	 * Draws black rectangle (paddle) in bottom-center of canvas
	 */

	private void addPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		double x = getCenterX() - PADDLE_WIDTH/2;
		double y = getHeight() - BRICK_Y_OFFSET;
		add(paddle, x, y);
	}

	/**
	 * Method: Mouse Moved
	 * -----------
	 * Called whenever the user moves the mouse
	 * Moves the paddle in accordance with the mouse
	 */

	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y =  getHeight() - BRICK_Y_OFFSET;
		if (x <= getWidth() - PADDLE_WIDTH && x >= 0) {
			paddle.setLocation(x, y);	
		}
	}


	/**
	 * Method:Add Ball 
	 * -----------
	 * Draws black rectangle (paddle) in bottom-center of canvas
	 */

	private void addBall() {
		ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, (getCenterX() - BALL_RADIUS), getCenterY() - BALL_RADIUS);

	}

	/**
	 * Method: Get Colliding Object
	 * -----------
	 * Checks for collision and returns colliding object 
	 */

	private GObject getCollidingObject () {
		// x and y as the position of the ball
		double x = ball.getX();
		double y = ball.getY();
		// diameter of the ball
		double diameter = BALL_RADIUS*2;
		// checks whether there is an object in each corner of the ball
		// x,y >>> x, y+2r >>> x+2r, y >>> x+2r, y+2r
		for (double i = x; i <= x + diameter; i+= diameter) {
			for (double j = y; j <= y + diameter; j+= diameter) {
				GObject obj = getElementAt(i, j);
				// if an object is found at any point, collision occured 
				if (obj != null) {
					return obj;
				}
			}
		}
		// if no object is found, there is no collision
		return null;
	}

	/**
	 * Method: Manage Collisions
	 * -----------
	 * Manages any collisions ball might encounter 
	 * Changes direction if collision is with paddle 
	 * Removes brick anc changes direction if collision is with brick
	 * Keeps track of how many bricks have been removed
	 */

	private double manageCollisions(double vy) {
		GObject collider = getCollidingObject();	
		// if ball collides with paddle, changes vert direction
		if (collider != null) {
			if (collider == paddle) vy = -Math.abs(vy);
			// if ball collides with brick, removes brick and changes vertical direction
			// records each time ball removes a brick
			if (collider != paddle) {
				remove(collider);
				vy = -vy;
				bricksRemoved += 1;
			}
		}
		return vy;
	}

	/**
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * Returns whether ball bounced off bottom wall
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/**
	 * Method: Hit Top Wall
	 * -----------------------
	 * Returns whether ball bounced off top wall
	 */

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/**
	 * Method: Hit Right Wall
	 * -----------------------
	 * Returns whether ball bounced off right wall
	 */

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/**
	 * Method: Hit Left Wall
	 * -----------------------
	 * Returns whether ball bounced off left wall
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
}

