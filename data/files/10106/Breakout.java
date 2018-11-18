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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	// Paddle
	private GRect paddle = null;
	
	// Ball
	private GOval ball = null;
	
	// Initial ball velocity 
	private double vx;
	private double vy;
	
	// Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Counter for number of bricks hit
	private int bricksRemaining = NBRICK_ROWS * NBRICK_COLUMNS;
	
	// Counter for number of turns remaining
	private int turnsRemaining = NTURNS;
	
	/*
	 * In basic terms, the run method will:
	 * 1. Add the mouse listeners (to control the paddle)
	 * 2. Set up the bricks and paddle
	 * 3. Play the game
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setupGame();
		playGame();
	}
	
	/*
	 * mouseMoved method: the paddle moves in the x direction with the mouse.
	 * This takes in a mouse event as an argument.
	 * This method entails:
	 * 1. Getting the x coordinate of the mouse and storing it in an integer (xMouse)
	 * 2. Set another local variable, xPaddleMax, for the maximum x coordinate of the paddle.
	 * The value of this is getWidth() - PADDLE_WIDTH
	 * 2. When xMouse is between 0 and xPaddleMax (inclusive), set the paddle's x coordinate accordingly
	 * 3. When xMouse is larger than xPaddleMax (the minimum value of xMouse is 0),
	 * set the paddle's x coordinate to xPaddleMax
	 */
	public void mouseMoved (MouseEvent e) {
		int xMouse = e.getX();
		int xPaddleMax = getWidth() - (int) PADDLE_WIDTH;
		if (xMouse <= xPaddleMax) {
			paddle.setLocation(xMouse,getHeight()-PADDLE_Y_OFFSET);
		}
		else {
			paddle.setLocation(xPaddleMax,getHeight()-PADDLE_Y_OFFSET);
		}
	}

	/*
	 * Private method #1: Set up the game.
	 * This entails:
	 * 1. Laying the bricks
	 * 2. Creating the paddle that can move with the mouse
	 */
	private void setupGame() {
		layBricks();
		makePaddle();
	}
	
	/*
	 * Private method #1a: Lay the bricks.
	 * The y coordinate of the top left corner of the wall of bricks has been set.
	 * The x coordinate is calculated by the following:
	 * 1. Half the width of the canvas (use getWidth())
	 * 2. Multiply half the number of columns by the width of the brick and subtract
	 * 3. Multiply (half the number of columns subtract 1) by the brick separation and subtract
	 * The creation of the bricks will now be similar to the CS106A tiles from Assignment #2.
	 * 
	 * Graphic (i,j) is:
	 * 1. i lots of (BRICK_SEP + BRICK_WIDTH) away from brick (0,0) in the x direction
	 * 2. j lots of (BRICK_SEP + BRICK_HEIGHT) away from brick (0,0) in the y direction
	 * Each brick is added in the innermost loop. The bricks are coloured separately.
	 */
	private void layBricks() {
		double xTopLeft = (getWidth() * 0.5) - (NBRICK_COLUMNS * BRICK_WIDTH * 0.5) - ((0.5 * NBRICK_COLUMNS - 0.5) * BRICK_SEP);
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			double xBrick = xTopLeft + (i * (BRICK_SEP + BRICK_WIDTH));
			for (int j = 0; j < NBRICK_ROWS; j++) {
				double yBrick = BRICK_Y_OFFSET + (j * (BRICK_SEP + BRICK_HEIGHT));
				addColoredBrick(xBrick, yBrick, j);
			}
		}
	}

	/*
	 * Private method #1a-i: Add a coloured brick to the display.
	 * This takes in doubles x and y, and integer rowNumber as arguments.
	 * This can be achieved by:
	 * 1. Making a new GRect (brick) with origin (x,y) and size BRICK_WIDTH by BRICK_HEIGHT
	 * 2. Ensuring the brick is filled in
	 * 3. Creating a switch statement that colours the brick based on the integer part of rowNumber/2.
	 * 4. Adding the brick to the display
	 */
	private void addColoredBrick(double x, double y, int rowNumber) {
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		switch (rowNumber/2) {
			case 0:
				brick.setColor(Color.RED);
				break;
			case 1:
				brick.setColor(Color.ORANGE);
				break;
			case 2:
				brick.setColor(Color.YELLOW);
				break;
			case 3:
				brick.setColor(Color.GREEN);
				break;
			case 4:
				brick.setColor(Color.CYAN);
				break;
		}
		add(brick);
	}
	
	/*
	 * Private method #1b: Create the paddle.
	 * The paddle's ability to respond to mouse movements will be set in the mouseMoved method.
	 * To make the paddle, we define its starting position and size, then add it to the canvas.
	 * For now, I shall initialise its x coordinate to 0. This will change when the mouse moves.
	 */
	private void makePaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, 0, getHeight()-PADDLE_Y_OFFSET);
	}
	
	/*
	 * Private method #2: Play the game.
	 * This entails:
	 * 1. Making the ball
	 * 2. Setting it in motion
	 * 3. Checking for collisions
	 */
	private void playGame() {
		makeBall();
		setBallInMotion();
	}

	/*
	 * Private method #2a: Make the ball.
	 * This ball will modify the instance variable ball,  which is currently a null.
	 * This method initialises the ball's starting position (middle of the canvas) and size.
	 * It then adds it to the canvas.
	 */
	private void makeBall() {
		ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		addBallAtCenter();
	}
	
	/*
	 * Private method #2b: Set the ball in motion.
	 * This will:
	 * 1. Initialise the values of vx and vy appropriately
	 * 2. Update the position of the ball after a delay
	 * And continuously:
	 * 3. Make sure the ball bounces off the walls correctly
	 * 		a) If the ball hits the left or right wall, change the sign of vx
	 * 		b) If the ball hits the top wall, change the sign of vy
	 * 		c) If the ball hits the bottom wall, put the ball in the centre and reduce the number of turns
	 * 4. Check if the ball has collided with an object and deal with the object appropriately
	 * 5. End the game once the player is out of turns or out of bricks
	 */
	private void setBallInMotion() {
		vy = 3.0;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while(turnsRemaining > 0 && bricksRemaining > 0) {
			if(hitLeftWall(ball) || hitRightWall(ball)) vx = -vx;
			if(hitTopWall(ball)) vy = -vy;
			if(hitBottomWall(ball)) {
				turnsRemaining--;
				remove(ball);
				addBallAtCenter();
			}
			checkForCollisions(ball);
			pause(DELAY);
			ball.move(vx, vy);
		}
		remove(ball);
	}
	
	/*
	 * Private method #2b-i: Check to see if the ball is at the left wall.
	 * This returns a boolean value, which determines the conditions in method #2b.
	 */
	private boolean hitLeftWall (GOval oval) {
		return oval.getX() <= 0;
	}
	
	
	// Private method #2b-ii: Check to see if the ball is at the right wall. Defined similarly.
	private boolean hitRightWall (GOval oval) {
		return oval.getX() >= getWidth() - oval.getWidth();
	}
	
	
	// Private method #2b-iii: Check to see if the ball is at the top wall. 
	private boolean hitTopWall (GOval oval) {
		return oval.getY() <= 0;
	}
	
	// Private method #2b-iv: Check to see if the ball is at the bottom wall.
	private boolean hitBottomWall (GOval oval) {
		return oval.getY() >= getHeight() - oval.getHeight();
	}
	
	/*
	 * Private method #2c: Check whether the ball has collided with an object.
	 * There are two types of object a ball can collide with: a brick and the paddle.
	 * If the ball collides with the paddle, we want it to bounce upwards.
	 * If the ball collides with a brick, we want to:
	 * 1. Change the ball's y direction
	 * 2. Remove the brick from the display
	 * The collision checking only happens while there are bricks still in the game.
	 * We need to check the ball at the four points specified in the handout.
	 * This is done in a separate method.
	 * 
	 * If the ball collides with the paddle below its top edge, the game does the following:
	 * 1. Removes the ball from the canvas
	 * 2. Adds it back just above the top edge of the paddle at the same x coordinate
	 * The ball will continue to move in the opposite y direction from before it hit the paddle.
	 * This is a simplifying assumption facilitating game play. 
	 */
	private void checkForCollisions(GOval oval) {
		while (bricksRemaining > 0) {
			GObject collider = getCollidingObject(oval);
			if (collider == null) {
				break;
			} else {
				vy = -vy;
				if (collider == paddle) {
					if(belowPaddleTop(oval)) {
						double ovalX = oval.getX();
						remove(oval);
						add(oval, ovalX, getHeight() - (PADDLE_Y_OFFSET + oval.getHeight() + 1));
					}
					break;
				} else {
					remove(collider);
					bricksRemaining--;
				}
			}
		}
	}
	
	/*
	 * Private method #2c-i: Get the object with which the ball has collided.
	 * This checks whether there is an object at each 'corner' of the ball.
	 * It first sets the coordinates of each corner as variables.
	 * Then it checks for objects and returns an object if there is one.
	 */
	private GObject getCollidingObject(GOval oval) {
		double ovalLeft = oval.getX();
		double ovalRight = oval.getRightX();
		double ovalTop = oval.getY();
		double ovalBottom = oval.getBottomY();
		GObject collider = getElementAt(ovalLeft, ovalTop);
		if (collider == null) collider = getElementAt(ovalLeft, ovalBottom);
		if (collider == null) collider = getElementAt(ovalRight, ovalTop);
		if (collider == null) collider = getElementAt(ovalRight, ovalBottom);
		return collider;
	}
	
	// Private method #2c-ii: Check whether an oval is below the top of the paddle.
	private boolean belowPaddleTop(GOval oval) {
		boolean belowTop = oval.getBottomY() > paddle.getY();
		return belowTop;
	}
	
	// Private method #2A: Add the ball at the centre of the canvas.
	private void addBallAtCenter() {
		add(ball, getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS);
	}
	
}
