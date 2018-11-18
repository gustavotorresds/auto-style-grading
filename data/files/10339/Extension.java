/*
 * File: Breakout.java
 * -------------------
 * Name: David Ludeke
 * Section Leader: Andrew Davis
 * CS 106A
 * 
 * This program implements the game Breakout through the use of graphics 
 * and mouse events, which allow the user to interact wtih objects displayed
 * in the window in real time using the mouse. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Extension extends GraphicsProgram {

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
	
	// Delay time between when the ball hits the ground and when it is removed 
	// and redrawn in the center
	public static final int BALL_RESET_DELAY = 1500;
	
	// Delay time for adding bricks
	public static final int BRICK_DELAY = 30;
	
	// Add audio clips.
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	
	/* Method: Run
	 * ------------
	 * Sets up and then runs the game Breakout.
	 */
	public void run() {
		setUp();
		playGame(); 
	}
	
	
	/* Method: Set Up
	 * ------------
	 * Sets up the game by titling the window, setting the canvas size, 
	 * drawing the grid of bricks, and drawing the paddle in its starting
	 * position. 
	 */
	private void setUp() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		buildBricks();
		buildPaddle();
		
	}
	
	
	/* Method: Build Ball
	 * ---------------------
	 * Builds the ball of specified dimensions in the center of the window.
	 */
	private void buildBall() {
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval(x, y, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}


	/* Method: Build Bricks
	 * ---------------------
	 * Builds the grid of bricks with dimensions NBRICK_ROWS by NBRICK_COLUMNS.
	 */
	private void buildBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			buildRow(i);
			bounceClip.play();
		}
	}

	/* Method: Build Row
	 * -----------------
	 * Builds a single row of bricks, modifying the x position of the brick by adding
	 * the brick width and brick separation after every brick before is built.
	 */
	private void buildRow(int rowN) {
		double x = (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP)) / 2;
		for (int j = 0; j < NBRICK_COLUMNS; j++) {
			buildBrick(x, rowN);
			x += (BRICK_WIDTH + BRICK_SEP);
		}
	}
	

	/* Method: Build Brick
	 * -------------------
	 * Uses GRect to build a brick at an x position that is passed from the build row  
	 * method, a color that is determined by the row number, which was passed from the 
	 * original build bricks method.
	 */
	private void buildBrick(double x, int rowN) {
		double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * rowN;
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(rgen.nextColor());
		add(brick);
		pause(BRICK_DELAY);
	}

	
	/* Method: Build Paddle
	 * -------------------
	 * Uses GRect to draw a paddle of the specified dimensions centered in the x direction
	 * and offset from the bottom of the window by a constant in the y direction. 
	 */
	private void buildPaddle() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	
	/* Method: Mouse Moved  
	 * --------------------
	 * Uses the current coordinates of the mouse to constantly update the location 
	 * of the paddle. If statement ensures the paddle won't move beyond the range 
	 * allowed by its width (i.e. the entire paddle will always be visible in the 
	 * window). 
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		if (e.getX() > PADDLE_WIDTH/2 && e.getX() < getWidth() - PADDLE_WIDTH/2) {
			paddle.setLocation(x, y);
		}
	}

	
	/* Method: Play Game
	 * ------------------
	 * Keeps track of the number of lives lost, then builds the ball, waits for 
	 * click, and moves the ball. This process repeats until the number of lives 
	 * lost equals the number of turns allowed OR when the player wins by removing
	 * all the bricks. 
	 */
	private void playGame() {
		int livesLost = 0;
		while (bricksLeftInGame > 0 && livesLost < NTURNS) {
			setBackground(Color.WHITE);
			buildBall();
			waitForClick();
			moveBall();
			remove(ball);
			livesLost++;
		}
		// The game is over yay now the screen will flash bright colors and punish your eyes and ears!
		while (true) {
			bounceClip.play();
			setBackground(rgen.nextColor());
			pause(BRICK_DELAY);
		}
	}
	
	
	/* Method: Move Ball
	 * -----------------
	 * Randomly generates an x velocity, then moves the ball a certain amount in the
	 * x and y directions, pauses, checks for collisions with the wall or the objects 
	 * (i.e. paddle or bricks), then repeats this until the game ends when the ball 
	 * hits or until there are no more bricks left. 
	 */
	private void moveBall() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while(!gameOver() && bricksLeftInGame > 0) {
			ball.move(vx,  vy);
			pause(DELAY);
			checkWorldCollisions();
			checkObjectCollisions();
		}
	}


	/* Method: Check World Collisions
	 * ------------------------------
	 * Using the dimensions of the window, determines if the ball has hit a wall, the
	 * ceiling, or the floor, and changes the velocity accordingly (for ceiling reverses 
	 * y direction, for walls reverses x direction, for the ground sets both to 0, making
	 * the ball stop). 
	 */
	private void checkWorldCollisions() {
		if (ball.getY() > getHeight() - BALL_RADIUS * 2) {
			vy = 0; 
			vx = 0;
		}
		if (ball.getX() < 0 || ball.getX() > (getWidth() - BALL_RADIUS * 2)) {
			vx = -vx;
			bounceClip.play();
		}
		if (ball.getY() < 0) {
			vy = -vy;
			bounceClip.play();
		}
	}

	
	/* Method: Check Object Collisions
	 * -------------------------------
	 * Detects if any GObject exists in any of the four corners bordering the ball. 
	 * If an object is the paddle, the y direction is reversed, otherwise the object
	 * must be a brick so it is removed. 
	 */
	private void checkObjectCollisions() {
		double x1 = ball.getX();
		double x2 = ball.getX() + 2*BALL_RADIUS;
		double y1 = ball.getY();
		double y2 = ball.getY() + 2*BALL_RADIUS;
		
		GObject collider1 = getCollidingObject(x1, y2);
		GObject collider2 = getCollidingObject(x2, y2);
		GObject collider3 = getCollidingObject(x1, y1);
		GObject collider4 = getCollidingObject(x2, y1);

		if (collider1 == paddle || collider2 == paddle) {
			vy = -vy;
			bounceClip.play();
			return;
		} 
		if (collider1 != null && collider1 !=paddle) {
			remove(collider1);
			vy = -vy;
			bounceClip.play();
			bricksLeftInGame--;
			return;
		}
		if (collider2 != null && collider2 !=paddle) {
			remove(collider2);
			vy = -vy;
			bounceClip.play();
			bricksLeftInGame--;
			return;
		}
		if (collider3 != null && collider3 !=paddle) {
			remove(collider3);
			vy = -vy;
			bounceClip.play();
			bricksLeftInGame--;
			return;
		}
		if (collider4 != null && collider4 !=paddle) {
			remove(collider4);
			vy = -vy;
			bounceClip.play();
			bricksLeftInGame--;
			return;
		}
	} 

	
	/* Method: Get Colliding Object
	 * ----------------------------
	 * Takes in an x and y position, then checks if an object exists there, and returns 
	 * that object if it exists, or null if it doesn't. 
	 */
	private GObject getCollidingObject(double x, double y) {
		GObject collider = getElementAt(x, y);
		return collider;
	}
	
	/* Method: Game Over
	 * ------------------
	 * Determines if the game is over by returning true of the ball is on the ground, 
	 * otherwise returning false (i.e. the game is NOT over). 
	 */
	private boolean gameOver() {
		if (ball.getY() + 2*BALL_RADIUS > getHeight()) {
			setBackground(Color.RED);
			pause(BALL_RESET_DELAY);
			return true;
		}
		return false;
	}
	
	
	/** Instance variables for moving objects (ball and paddle), as well as 
	 ** changing characteristics of the game, such as velocities vx/vy of the 
	 ** ball, the number of bricks left in the game (initialized as the total
	 ** beginning number of bricks, calculated by rows times columns), and a 
	 ** random number generator for use in determining vx. */
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private int bricksLeftInGame = NBRICK_ROWS * NBRICK_COLUMNS;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
}
