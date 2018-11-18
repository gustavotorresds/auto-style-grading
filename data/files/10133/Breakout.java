/*
 * File: Breakout.java
 * Name: Christopher Bechler
 * Due Date: February 7, 2018
 * Section Leader: Garrick Fernandez
 * Sources Used: Lecture & Handout Materials
 * -------------------
 * In short, this file will implements the game of Breakout. In slightly more detail, this file consists
 * of programming three main parts: setting up the game (e.g., bricks and paddles), setting
 * up Mouse Events, and playing the game.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// BELOW IS CODE PROVIDED IN THE STARTER CODE 

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

	// BELOW IS CODE I HAVE ADDED TO IMPLEMENT BREAKOUT

	// Create instance variable for paddle
	GRect paddle = null;

	// Create instance variables for velocity
	private double vx;
	private double vy;

	// Create instance variable for random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Counter to give feedback if game is won!
	private int bricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;

	public void run() {

		// BELOW IS CODE PROVIDED IN THE STARTER CODE 

		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// BELOW IS CODE I HAVE ADDED TO IMPLEMENT BREAKOUT

		// Set up rectangles and move paddle
		setUpGame();
		addMouseListeners();

		// Playing game!
		playGame();		
	}

	/*
	 * Method Name: Play Game
	 * --------------------------------
	 * This method performs the necessary functions to allow one to play the Breakout game. On
	 * a broad level, it creates the ball for the game, implements all of the required animation,
	 * and prints the winning or losing message (depending on how good the player is!).
	 */	
	private void playGame() {		

		for (int turns = 0; turns < NTURNS; turns++) {
			// Create ball
			GOval ball = makeBall();

			// Collect relevant ball coordinates. Note that I checked with Garrick and 
			// "getBottomY" and similar commands are okay to use!
			double ballBottom = ball.getBottomY(); 
			double ballTop = ball.getY();
			double ballRight = ball.getRightX();
			double ballLeft = ball.getX();

			// Start ball animation (random x velocity)
			vy = VELOCITY_Y;
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx = -vx;

			// Animation while ball is on screen and bricks are still on screen!
			while (ballBottom <= getHeight() && bricksLeft > 0) {
				// Move ball
				ball.move(vx, vy);

				// Collect relevant ball coordinates
				ballBottom = ball.getBottomY();
				ballTop = ball.getY();
				ballRight = ball.getRightX();
				ballLeft = ball.getX();
				
				// Check for possible changes in ball movement
				checkCollisions(ballBottom, ballTop, ballRight, ballLeft);
				
				// Pause (necessary for human eyes!)
				pause(DELAY);
			}
			// Remove ball if passes paddle or game is won!
			remove(ball);
			
			// Print winning message if game won!
			if (bricksLeft == 0) {
				printWinMessage();
			}
		}
		// Print losing message if ran out of balls and at least one brick left!
		if (bricksLeft > 0) {
			printLoseMessage();
		}
	}

	/*
	 * Method Name: Losing message
	 * --------------------------------
	 * This method prints a losing message on the horizontal center of the screen.
	 */	
	private void printLoseMessage() {

		GLabel label = new GLabel("YOU LOSE!!!");
		label.setFont("Courier-48");

		// Horizontally center label on screen (a bit higher than vertical center).
		double centerX = (getWidth() - label.getWidth())/2;
		double centerY = (getHeight() - label.getHeight())/2;

		add(label, centerX, centerY);
	}
	
	/*
	 * Method Name: Make Ball
	 * --------------------------------
	 * This method makes the ball and places it on center of the screen.
	 */	
	private GOval makeBall() {
		// Center placement for ball
		double centerX = getWidth()/2 - BALL_RADIUS;
		double centerY = getHeight()/2 - BALL_RADIUS;
		
		// Make ball
		GOval makeBall = new GOval(centerX, centerY, BALL_RADIUS*2, BALL_RADIUS*2);
		makeBall.setFilled(true);
		add(makeBall);
		return makeBall;
	}

	/*
	 * Method Name: Check for Collisions
	 * --------------------------------
	 * This method checks for three types of collisions: collisions with walls, collisions
	 * with bricks, and collisions with the paddle.
	 */	
	private void checkCollisions(double ballBottom, double ballTop, 
			double ballRight, double ballLeft) {

		// Check for colliding with walls
		checkWalls(ballBottom, ballTop, ballRight, ballLeft);

		// Check for other collisions through getCollidingObject method!
		GObject collider = getCollidingObject(ballBottom, ballTop, ballRight, ballLeft);
		if (collider != null && collider != paddle) {
			remove(collider);
			vy = -vy;
			bricksLeft = bricksLeft - 1;
		}	
		
		// Checks for collisions with the paddle. Importantly, when the ball hits the paddle, 
		// it should always go up! This corrects the "glued to the paddle" issue!
		if (collider == paddle) {
			vy = -VELOCITY_Y; 
		}
	}
	
	/*
	 * Method Name: Winning message
	 * --------------------------------
	 * This method prints a winning message on the horizontal center of the screen.
	 */	
	private void printWinMessage() {
		GLabel label = new GLabel("YOU WIN!!!");
		label.setFont("Courier-48");

		// Horizontally center label on screen (a bit higher than vertical center).
		double centerX = (getWidth() - label.getWidth())/2;
		double centerY = (getHeight() - label.getHeight())/2;

		add(label, centerX, centerY);
	}

	/*
	 * Method Name: Get Colliding Object!
	 * --------------------------------
	 * This method checks for collisions with an object I have created and placed
	 * on the screen. It returns null if there is no object touching the four 
	 * corners of the ball (GOval is defined in terms of bounding rectangle!). If
	 * there is an object touching one of the four corners of the ball, it returns 
	 * that object.
	 */
	private GObject getCollidingObject(double ballBottom, double ballTop, 
			double ballRight, double ballLeft) {
		
		GObject maybeObject = null;
		
		// Check top left
		maybeObject = getElementAt(ballLeft, ballTop);
		if (maybeObject != null) {
			return maybeObject;
		} 
		// Check top right
		maybeObject = getElementAt(ballRight, ballTop);
		if (maybeObject != null) {
			return maybeObject;
		}
		// Check bottom right
		maybeObject = getElementAt(ballRight, ballBottom);
		if (maybeObject != null) {
			return maybeObject;
		}
		// Check bottom left
		maybeObject = getElementAt(ballLeft, ballBottom);
		if (maybeObject != null) {
			return maybeObject;
		} else {
			return null;
		}
	}

	/*
	 * Method Name: Check Walls
	 * --------------------------------
	 * This method checks collisions with walls. The ball bounces off the top, left, and right 
	 * walls, but keeps going if it hits the bottom wall (the player loses, or at least loses
	 * a life!).
	 */
	private void checkWalls(double ballBottom, double ballTop, double ballRight, double ballLeft) {
		// Check top
		if (ballTop <= 0) {
			vy = -vy;
		}
		// Check left and right
		if (ballLeft <= 0 || ballRight >= getWidth()) {
			vx = -vx;
		}
		// But if hits bottom ball keeps going!
	}

	/*
	 * Mouse Event Name: Move Paddle
	 * --------------------------------
	 * This mouse event name allows the mouse to move the paddle horizontally but not 
	 * vertically. The center of the paddle is aligned with the x coordinate of the 
	 * users mouse. This mouse event name also requires the paddle to stay completely 
	 * on the screen regardless of where the mouse moves.
	 */	
	public void mouseMoved(MouseEvent e) {

		// Mouse follows paddle horizontally but not vertically
		double x = e.getX() - PADDLE_WIDTH/2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;

		// Create left and right boundaries for paddle
		double leftBoundary = 0;	
		double rightBoundary = getWidth() - PADDLE_WIDTH;

		if (x < leftBoundary) {
			paddle.setLocation(leftBoundary, paddleY);
		}
		else if (x > rightBoundary) {
			paddle.setLocation(rightBoundary, paddleY);
		}
		else {
			paddle.setLocation(x, paddleY);
		}
	}

	/*
	 * Method Name: Set Up Game
	 * --------------------------------
	 * This method performs the necessary functions to set up the Breakout game. It creates
	 * the appropriate number of columns and rows of bricks (with appropriate colors), and 
	 * creates a paddle.
	 */	
	private void setUpGame() {
		// Create bricks
		createBricks();

		// Create paddle
		createPaddle();
	}

	/*
	 * Method Name: Create Paddle
	 * --------------------------------
	 * This method creates a black paddle and adds it to the horizontal center of the screen
	 * at the previously specified vertical height.
	 */	
	private void createPaddle() {
		double paddleStartX = (getWidth() - PADDLE_WIDTH)/2;
		double paddleStartY = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(paddleStartX, paddleStartY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * Method Name: Create Bricks
	 * --------------------------------
	 * This method creates rows of bricks horizontally centered on the screen. The number of 
	 * rows, separation between bricks, and vertical starting point for the bricks are previously 
	 * specified. It uses the function makeRowBricks() to make each row of bricks.
	 */	
	private void createBricks() {
		// Get starting x,y position of bricks 
		double leftMostBrick = (getWidth() 
				- (NBRICK_COLUMNS*BRICK_WIDTH) - ((NBRICK_COLUMNS-1)*BRICK_SEP))/2;
		double startBrickY = BRICK_Y_OFFSET;
		
		// Create rows of bricks for specified number of rows
		for (int row = 0; row < NBRICK_ROWS; row++) {
			makeRowBricks(leftMostBrick, startBrickY, row);
			startBrickY = startBrickY + BRICK_HEIGHT + BRICK_SEP;
		}
	}

	/*
	 * Method Name: Make Row of Bricks
	 * --------------------------------
	 * This method takes in parameters for the x,y position where a row of bricks should start
	 * and outputs a row of bricks. It also uses static final values and implements the 
	 * setBrickColor() method.
	 * Precondition: Have available x,y position to start row.
	 * Postcondition: A row of bricks!
	 */
	private void makeRowBricks(double leftMostBrick, double startBrickY, int row) {		
		double startBrickX = leftMostBrick;
		for (int col = 0; col < NBRICK_COLUMNS; col++) { 
			GRect rect = new GRect(startBrickX, startBrickY, BRICK_WIDTH, BRICK_HEIGHT);
			setBrickColor(rect, row);
			add(rect);
			startBrickX = startBrickX + BRICK_WIDTH + BRICK_SEP;
		}
	}
	
	/*
	 * Method Name: Color Brick
	 * --------------------------------
	 * This method takes in the address for an object (rectangle) and changes the 
	 * color of the object to the appropriate color.
	 * Precondition: Have available an address of a rectangle.
	 * Postcondition: An appropriately colored rectangle.
	 */
	private void setBrickColor(GRect rect, int row) {
		int sequence = row % 10;
		if (sequence < 2) {
			rect.setColor(Color.RED);
		} 
		if (sequence >= 2 && sequence < 4) {
			rect.setColor(Color.ORANGE);
		}
		if (sequence >= 4 && sequence < 6) {
			rect.setColor(Color.YELLOW);
		}
		if (sequence >= 6 && sequence < 8) {
			rect.setColor(Color.GREEN);
		}
		if (sequence >= 8 && sequence < 10) {
			rect.setColor(Color.CYAN);
		}
		rect.setFilled(true);
	}
}
