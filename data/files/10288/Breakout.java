/*
 * File: Breakout.java
 * -------------------
 * Name: Isabella Duan
 * Section Leader: Julia Daniel
 * 
 * This is the game of Breakout! 
 * The initial configuration is a world with colored bricks and a paddle.
 * A ball is released from the middle of the screen.
 * The user must try to use the paddle to bounce the ball back to the bricks.
 * When all the bricks are destroyed by the ball, the user wins.
 * If after three turns, this has not been done, the user loses.
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

	// Number of total colors for bricks 
	public static final int NCOLORS = 5;

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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Private instance variables
	private GRect paddle = null;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int count;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUp();
		playGame();
	}

	/*
	 * Initializes all settings necessary for playing the game
	 * There are two main tasks, creating the wall and creating the paddle
	 */
	private void setUp() {
		constructWall();
		constructPaddle();
	}

	/*
	 * Calls for creation of entire wall
	 * This line is especially necessary for finding the x-coordinate of the first brick so that the entire row will be centered
	 */
	private void constructWall() {
		double x = (getWidth() / 2) - ((NBRICK_COLUMNS * BRICK_WIDTH + BRICK_SEP * (NBRICK_COLUMNS - 1)) / 2);  
		double y = BRICK_Y_OFFSET;
		makeBrickWall(x, y); 	
	}

	/*
	 * Makes several rows (according to number specified in constant) of a single line of bricks 
	 * Rows are colored accordingly
	 */
	private void makeBrickWall(double x, double y) {
		for (int i = 0; i < NBRICK_ROWS; i++) {	
			Color color = null;
			// These formulas ensure that no matter the number of rows, the colors will be evenly spaced 
			if (((i * NCOLORS) / NBRICK_ROWS) == 0) { 
				color = Color.RED;
			}
			if (((i * NCOLORS) / NBRICK_ROWS) == 1) {
				color = Color.ORANGE;
			} 
			if (((i * NCOLORS) / NBRICK_ROWS) == 2) {
				color = Color.YELLOW;
			}
			if (((i * NCOLORS) / NBRICK_ROWS) == 3) {
				color = Color.GREEN;
			}
			if (((i * NCOLORS) / NBRICK_ROWS) == 4) {
				color = Color.CYAN;
			}
			makeLine(x, y, color);
			y += BRICK_HEIGHT + BRICK_SEP;
		}
	}

	/*
	 * Makes a single line of bricks
	 * Color is passed down from previous method
	 */
	private void makeLine(double x, double y, Color color) {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			makeBrick(x, y, color);
			x += (BRICK_WIDTH + BRICK_SEP);
		}
	}

	/*
	 * Makes a single brick
	 * Color is again passed down from previous method
	 */
	private void makeBrick(double x, double y, Color color) {
		GRect rect = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setFilled(true);
		rect.setColor(color);
		add(rect);
	}

	/*
	 * Encompasses three commands for generating a paddle that can be controlled by the user's mouse
	 * Paddle begins in middle of bottom of screen
	 */
	private void constructPaddle() {
		paddle = makePaddle();
		addPaddleToCenter();
		addMouseListeners();	
	}

	/*
	 * Outlines exactly what the paddle will look like 
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;	
	}

	/*
	 * Adds paddle to middle of screen, near the bottom
	 * It is offset from the bottom by a constant
	 */
	private void addPaddleToCenter() {
		double x = ((getWidth() / 2) - (PADDLE_WIDTH / 2));
		double y = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		add (paddle, x, y);
	}

	/*
	 * Allows cursor to control paddle
	 * Cursor is 'connected' to the middle of the top of the paddle
	 */
	public void mouseMoved (MouseEvent e) {
		double x = (e.getX() - PADDLE_WIDTH / 2);
		// Ensures that paddle will not exit screen
		if (x >= getWidth() - PADDLE_WIDTH) {
			x = getWidth() - PADDLE_WIDTH;
		}
		if (x <= 0) {
			x = 0;
		}
		double y = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle.setLocation(x, y);
	}

	/*
	 * Here lies the program for the user to actually interact with the game
	 * Incorporates both the ball's movement and losing/winning the game
	 */
	private void playGame() {
		for (int i = 0; i < NTURNS; i++) {
			programBall();
			// If all bricks are removed, and game is won, this happens
			if (count == (NBRICK_COLUMNS * NBRICK_ROWS)) {
				remove(ball);
				addWinningLabel();
				break;
			}
		}
		// If all three turns are used up and all the bricks are not removed, the game is last, and this happens
		if (count != (NBRICK_COLUMNS * NBRICK_ROWS)) {
			addLosingLabel();
		}
	}

	/*
	 * Encompasses all commands for the ball
	 */
	private void programBall() {
		ball = makeBall();
		addBallToCenter();
		moveBall();
	}

	/*
	 * Outlines exactly what the ball will look like 
	 */
	private GOval makeBall() {
		GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);;
		ball.setColor(Color.BLACK);
		return ball;
	}

	/*
	 * Adds ball to center of screen, per handout's instructions
	 */
	private void addBallToCenter() {
		double x = (getWidth() / 2) - BALL_RADIUS;
		double y = (getHeight() / 2) - BALL_RADIUS;
		ball.setLocation(x, y);
		add (ball);
	}

	/*
	 * Determines movement of ball off of walls, bricks, and paddle
	 * Initial movement is random, but downwards
	 * Program waits for user to click before ball begins movement
	 * While loop is exited if user wins or loses a turn
	 */
	private void moveBall() {
		// To make the game less predictable, this makes the initial velocity in the x direction random
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		waitForUser();
		while (true) {
			// Checks for hitting the left wall or right wall
			if (ball.getX() <= 0 || ball.getX() >= getWidth() - (2 * BALL_RADIUS)) {
				vx = -vx;
			}
			// Checks for hitting the top wall
			if (ball.getY() <= 0) {
				vy = -vy;
			}
			// Checks for hitting the bottom wall - if this happens, this turn is over so loop is exited
			if (ball.getY() >= getHeight() - (2 * BALL_RADIUS)) {
				remove (ball); 
				break;
			}
			ball.move(vx, vy);
			pause(DELAY);
			bounce();
			// If all bricks are removed, game is won, so loop is exited
			if (count == (NBRICK_COLUMNS * NBRICK_ROWS)) {
				break;
			}
		}
	}

	/* 
	 * Ensures that user is ready to play the game
	 * Prompts user to click the screen to begin
	 * Once screen is clicked, label disappears and ball begins to move
	 */
	private void waitForUser() {
		GLabel label = new GLabel ("CLICK TO BEGIN");
		label.setLocation((getWidth() / 2) - (label.getWidth() / 2), ball.getY() + (4 * BALL_RADIUS));
		add (label); 
		waitForClick();
		remove (label);
	}

	/* 
	 * Provides for the ball to bounce off of the paddle and bricks
	 */
	private void bounce() {
		GObject collider = getCollidingObject();
		// Checks if the collision is with either a brick or the paddle - if so, the velocity in the y direction is reversed
		if(collider != null) {
			vy *= -1;
			// Gets rid of the 'sticky paddle' problem by immediately shifting ball away from paddle enough that it will not accidentally turn around again
			if (collider == paddle) {
				ball.move(0, -(BALL_RADIUS / 2));
			}
			// Checks if collision is with a brick - if so, brick is removed
			if (collider != paddle) {
				remove(collider);
				count += 1;
			}
		}
	}

	/*
	 * Checks for corners of the ball for a collision with an object 
	 */
	private 	GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		GObject element = getElementAt(x, y);
		if(element instanceof GRect) {
			return element;
		}
		element = getElementAt(x + (2 * BALL_RADIUS), y);
		if(element instanceof GRect) {
			return element;
		}
		element = getElementAt(x, y + (2 * BALL_RADIUS));
		if(element instanceof GRect) {
			return element;
		}
		element = getElementAt(x + (2 * BALL_RADIUS), y + (2 * BALL_RADIUS));
		if(element instanceof GRect) {
			return element;
		} else 
			return null;	
	}

	/*
	 * If all bricks have been removed, the game has been won, and this helper method informs the user of that
	 */
	private void addWinningLabel() {
		GLabel label = new GLabel ("YOU WON");
		label.setLocation((getWidth() / 2) - (label.getWidth() / 2), (getHeight() / 2) - (label.getHeight() / 2));
		add (label);	
		GImage img = new GImage("background.jpg");
		label.setLocation((getWidth() / 2) - (img.getWidth() / 2), (getHeight() / 2) - (img.getHeight() / 2));
		add (img);
	}

	/*
	 * If all three turns have been used and not all the bricks have been removed, the game has been lost, and this helper method informs the user of that
	 */
	private void addLosingLabel() {
		GLabel label = new GLabel ("GAME OVER");
		label.setLocation((getWidth() / 2) - (label.getWidth() / 2), (getHeight() / 2) - (label.getAscent()) / 2);
		add (label);		
	}
}
