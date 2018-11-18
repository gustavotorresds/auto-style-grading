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
	
	// Make paddle an instance variable because has to be used by many methods
	private static GRect paddle;
	
	// Make ball an instance variable because it has to be used in many methods
	private static GOval ball;
	
	// Instance variables for velocity of the ball
	private double vx, vy;
	
	// Instance variable for random generator of velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		int blockCount = makeBlocks();
		
		/*
		 * This is how the paddle is made and is responsive to the position of the mouse. There are specific dimensions
		 * put into the "makePaddle" method so that it starts in the x-oriented middle of the screen and a constant
		 * distance from the bottom of the screen.
		 */
		makePaddle(getWidth()/2 - .5*PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		addMouseListeners();
		
		// This varible is necessary to define in order to keep track of what turn the player is on.
		int turns = NTURNS;
		
		/*
		 * This first while loop is run for each turn. This is why the conditions tested are that there are still one or more turns left
		 * and that the player has not already won the game.
		 */
		while (turns != 0 && blockCount != 0) {
			// A new ball is made and placed in the center for each turn
			makeBall(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
			
			// This new ball has the same x velocity and a random y velocity between 1 and 3
			setUpBall();
			
			/*
			 * This while loop is meant to run during each turn while the player is not violating the two kill options.
			 * The conditions of this while loop test for whether or not the player has already won and if the ball has
			 * touched the bottom of the screen. If neither of these conditions are true, the player continues to play on
			 * that turn. 
			 */
			while (blockCount != 0 && ball.getY() + 2*BALL_RADIUS <= getHeight()) {
				// Ball changes x velocity by -1 if hits a right or left wall
				if (ball.getX() <= 0 || ball.getX() >= getWidth()-2*BALL_RADIUS) {
					vx = -vx;
				}
				
				// Ball changes y velocity by -1 if hits a top wall
				if (ball.getY() <= 0 || ball.getY() >= getHeight()-2*BALL_RADIUS) {
					vy = -vy;
				}
				
				// Sets ball into motion with given vx and vy
				ball.move(vx, vy);
				
				// The position of the ball is updated and there is a certain delay in this updating that affects the speed
				pause(DELAY);
				
				// This object is made from calling the "getCollidingObject" method described later. It tells us if an object is present
				// where the ball is present.
				GObject collider = getCollidingObject();
				
				// The "determineColliderType" method is called using the object "collider" retrieved above.
				determineColliderType(collider);
				
				// This if statement lowers the block count each time the object present is a block. This makes sense when referencing
				// the "determineColliderType" method, which deletes the block that the ball collides with
				// Overall, this is necessary in keeping track of whether or not the user has won the game yet
				if (collider != null && collider != paddle) {
					blockCount --;
				}
			}
			// after each turn the ball is removed to not confuse the user
			remove(ball);
			turns --;
		}
		
		// One game has finished, returns a non-centered remark on whether or not the game was won.
		if (turns == 0) {
			GLabel label = new GLabel("The Game is over. You have lost.");
			add(label, getWidth()/4, getHeight()/2);	
		}
		else if (turns != 0) {
			GLabel label = new GLabel("The Game is over. You have WON!");
			label.setFont("Courier-24");
			label.setColor(Color.ORANGE);
			add(label, getWidth()/4, getHeight()/2);
		}
	}
	
	/*
	 * The drawRow method creates an entire row of blocks for the pyramid. The method takes in three parameters (number of blocks in row,
	 * the x coordinate value for the middle of the screen -- where each first block of row starts, and the y value for the entire row).
	 * A for loop is used to reuse the addRect method until the proper amount of bricks have been made.
	 * Pre: No row of blocks is present for the desired level of the stack of blocks
	 * Post: There is a row of same-colored blocks. The y-location of the row is uniform for the row. The new row is centered on the
	 * screen and on the rectangular block structure.
	 */
	private void drawRow(double yValue, double blockXStart) {
		for (double q = NBRICK_ROWS/2; q >= 1-NBRICK_ROWS/2; q--) {
			/*
			 * Think of the center of the screen as x=0, then you would need half blocks on the positive side and half blocks on
			 * the negative side. This is why "q" is set to run from 1/2 row amount to -1/2 row amount.
			 */
			addRect((blockXStart - (-.5*BRICK_SEP + q*(BRICK_WIDTH + BRICK_SEP))), yValue, BRICK_WIDTH, BRICK_HEIGHT);
			/*
			 * This "q" value is then used to place a brick that many times of BRICK_WIDTH away from the x-center of the screen.
			 * "BRICK_SEP" is included as well because there is a half the space outward from the center for each block being laid.
			 */
		}
	}
	
	/*
	 * The addRect method creates one brick. The parameters are for the x-point, y-point, width, and height. This method
	 * creates a single rectangle brick at the x and y location from the drawRow method.
	 * Pre: No brick has been made in the desired position.
	 * Post: A single brick has been made in the desired position. The brick is of its desired color, BRICK_WIDTH, and BRICK_HEIGHT.
	 */
	private void addRect(double x, double y, double w, double h) {
		GRect rect = new GRect (x, y, w, h);
		
		// First and Second rows made RED
		if (y <= (2*BRICK_HEIGHT + 2*BRICK_SEP + BRICK_Y_OFFSET)) {
			rect.setFilled(true);
			rect.setColor(Color.RED);
		}
		
		// Third and Fourth rows made ORANGE
		if (y > (2*BRICK_HEIGHT + 2*BRICK_SEP + BRICK_Y_OFFSET) && y <= (4*BRICK_HEIGHT + 4*BRICK_SEP + BRICK_Y_OFFSET)) {
			rect.setFilled(true);
			rect.setColor(Color.ORANGE);
		}
		
		// Fifth and Sixth rows made YELLOW
		if (y > (4*BRICK_HEIGHT + 4*BRICK_SEP + BRICK_Y_OFFSET) && y <= (6*BRICK_HEIGHT + 6*BRICK_SEP + BRICK_Y_OFFSET)) {
			rect.setFilled(true);
			rect.setColor(Color.YELLOW);
		}
		
		// Seventh and Eighth rows made GREEN
		if (y > (6*BRICK_HEIGHT + 6*BRICK_SEP + BRICK_Y_OFFSET) && y <= (8*BRICK_HEIGHT + 8*BRICK_SEP + BRICK_Y_OFFSET)) {
			rect.setFilled(true);
			rect.setColor(Color.GREEN);
		}
		
		// Ninth and Tenth rows made CYAN
		if (y > (8*BRICK_HEIGHT + 8*BRICK_SEP + BRICK_Y_OFFSET) && y <= (10*BRICK_HEIGHT + 10*BRICK_SEP + BRICK_Y_OFFSET)) {
			rect.setFilled(true);
			rect.setColor(Color.CYAN);
		}
		
		add(rect);
	}

	/*
	 * This method takes in the four constructors for the paddle's x-location, y-location, width, and height. This method makes a new
	 * rectangle with these four parameters from the run method. The rectangle is set to black and filled.
	 * Pre: No paddle for the game.
	 * Post: A black rectangle of PADDLE_WIDTH and PADDLE_HEIGHT is made.
	 */
	private void makePaddle(double x, double y, double w, double h) {
		paddle = new GRect (x, y, w, h);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}
	
	/*
	 * This method allows for the paddle to be directed by the mouse. The mouse guides the top left corner of the paddle. However, the
	 * method does not allow for the paddle to slightly leave the screen.
	 * Pre: Stationary paddle
	 * Post: The paddle moves with the x-position of the mouse, stopping at either edge 
	 * of the screen and moving no further in that direction.
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX(); //gets X position of mouse
		
		// for when the paddle should be slightly off the screen
		if (mouseX <= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
		}
		// all other times
		else {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		}
	}
	
	/*
	 * This method is what actually makes a new ball to use. There are four parameters (x-location, y-location, width, and height)
	 * that are entered in the method called in the run method. The "makeBall" method calls upon the GOval class and creates an oval
	 * object.
	 * Pre: No ball or oval present on the screen
	 * Post: A black oval is centered on the screen and has the dimensions of 2*BALL_RADIUS in both width and height
	 */
	private void makeBall(double x, double y, double w, double h) {
		ball = new GOval (x, y, w, h);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}
	
	/*
	 * This method returns an object. The method tests for whether or not an object is present at any of the four corners of the ball.
	 * If there is an object present at this point, the method assigns a new name to this object; it is now called "collider."
	 * This object, whether a block or a paddle, is returned.
	 * Pre: No awareness of what the ball is coming into contact with
	 * Post: Know exactly what the ball is coming into contact with at all times. The position and name of this object is known.
	 */
	private GObject getCollidingObject() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		double d = 2*BALL_RADIUS;
		GObject collider = (getElementAt(ballX,ballY));
		if (getElementAt(ballX, ballY) != null) {
			collider = getElementAt(ballX, ballY);
		}
		else if (getElementAt(ballX + d, ballY) != null) {
			collider = getElementAt(ballX + d, ballY);
		}
		else if (getElementAt(ballX, ballY + d) != null) {
			collider = getElementAt(ballX, ballY + d);
		}
		else if (getElementAt(ballX + d, ballY + d) != null) {
			collider = getElementAt(ballX + d, ballY + d);
		}
		return collider;
	}
	
	/*
	 * This method differentiates between the actions to be taken for each object the ball could be hitting: the paddle or a block.
	 * The one parameter is an object. The object is the collider that the previous "getCollidingObject" method returned.
	 * If the object is the paddle, the y-velocity is taken to be its negative absolute value (flip the y-velocity). If the collider is
	 * a block, the y-velocity is also flipped (multiplied by -1) and the block is removed.
	 * Pre: Ball hits object but there is no reaction
	 * Post: Ball hits object and changes y-velocity and destroys block if under right circumstances.
	 */
	private void determineColliderType(GObject collider1) {
		if (collider1 == paddle) {
			vy = -Math.abs(vy);
		}
		else if (collider1 != null) {
			vy = -vy;
			remove(collider1);
		}
	}
	
	/*
	 * This method is responsible for building all the blocks of the game interface.
	 * This method retrieves the distance of 1/2 of the screen's width and 1/2 of the screen's height.
	 * A for loop is used to run the called drawRow method until ten rows of ten blocks have been made.
	 * The drawRow method that is called ten times uses two parameters (1/2 of screen width and offset height).
	 * Pre: The screen is blank and white with no objects.
	 * Post: The screen has a ten rows of ten blocks each. The first two rows are red, second two rows are orange, then yellow,
	 * then green, then cyan. The blocks are spaced out a uniform distance from one another. They are located in a rectangular
	 * arrangement in the top of the screen.
	 */
	private int makeBlocks() {
		double halfWidth = (getWidth()/2);
		double offsetHeight = BRICK_Y_OFFSET;
		int numberOfBlocks = 0;
		for (int b = NBRICK_ROWS; b >= 1; b--) {
			// This height position value increases by the height of a block to set "y" value for each new row, also 
			// starts a constant amount below the screen top
			offsetHeight += (BRICK_HEIGHT + BRICK_SEP);
			
			// This is another method that is called on that makes one row at a time
			drawRow(offsetHeight, halfWidth);
			
			// The variable allows the method to keep track of the number of blocks created so far
			numberOfBlocks += NBRICK_COLUMNS;
		}
		// The total number of blocks the user has to destroy to notify the program that the user has won
		return numberOfBlocks;
	}
	
	/*
	 * This method is responsible for setting up the initial velocities of the ball when it appears in the game. Every time a new ball
	 * is made the x-velocity is 3, but the y-velocity is randomly generated between 1 and 3.
	 * Pre: There is a stationary ball in the center of the screen.
	 * Post: The ball is moving downwards with velocity components of vx and vy
	 */
	private void setUpBall() {
		vy = 3.0;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}
}
