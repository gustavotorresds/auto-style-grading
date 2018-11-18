/*
 * File: Breakout.java
 * -------------------
 * Name: Juan David Vargas Lopez (06232065)
 * Section Leader: Avery Hsiang-Wen Wang
 * 
 * This subclass allows the user to play a game of breakout using his mouse.
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

	// X coordinate of mouse at any point
	private int mouseX = 0;

	// Paddle object declaration
	GRect paddle;

	// Ball object declaration
	GOval ball;

	// Ball speed x and y planes
	private double vx = 0;
	private double vy = 0;

	// Random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth() and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpBricks();
		setUpPaddel();
		setUpBall();
		play();
		addMouseListeners();
	}

	/**
	 * This method sets up the brick wall on the top of the screen using constants as parameters.
	 *
	 * @param 
	 * @return 
	 */
	private void setUpBricks() {
		double leftIndent = (getWidth() - NBRICK_COLUMNS*BRICK_WIDTH - (NBRICK_COLUMNS-1)*BRICK_SEP) /2;
		for (int currentRow = 0; currentRow < NBRICK_ROWS; currentRow++) {
			double brickYPosition = BRICK_Y_OFFSET + currentRow*(BRICK_HEIGHT + BRICK_SEP);
			for (int currentColumn = 0; currentColumn < NBRICK_COLUMNS; currentColumn++) {
				double brickXPosition = leftIndent + currentColumn*(BRICK_WIDTH + BRICK_SEP);
				GRect brick = new GRect(brickXPosition, brickYPosition, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(brickLineColor(currentRow));
				add(brick);
			}
		}

	}

	/**
	 * This method returns the colour matching the row that it receives as input
	 *
	 * @param rowNumber integer that denotes the number of the row to produce the color for
	 * @return Color object corresponding to the row 
	 */
	private Color brickLineColor (int rowNumber){
		if(rowNumber%10 == 0 || rowNumber%10 == 1) {
			return Color.RED;
		} else if (rowNumber%10 == 2 || rowNumber%10 == 3) {
			return Color.ORANGE;
		} else if (rowNumber%10 == 4 || rowNumber%10 == 5) {
			return Color.YELLOW;
		} else if (rowNumber%10 == 6 || rowNumber%10 == 7) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}

	/**
	 * This method sets up the paddle at the bottom of the screen. Precondition: no paddle has been painted already. Postcondition: the canvas has a paddle added to it. 
	 *
	 * @param 
	 * @return  
	 */
	private void setUpPaddel() {
		paddle = new GRect((getWidth() - PADDLE_WIDTH)/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/**
	 * This method sets up the ball at the middle of the screen. Precondition: ball is a declared, null, instance. Postcondition: ball is declared as a new GOval. 
	 *
	 * @param 
	 * @return  
	 */
	private void setUpBall() {
		ball = new GOval(getWidth()/2-BALL_RADIUS/2, getHeight()/2-BALL_RADIUS/2, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		waitForClick();
		add(ball);
	}

	/**
	 * This method updates the instance 'mouseX' with me current x coordinate of the mouse. Precondition: 'mouseX' is a declared instance. Postcondition: 'mouseX' is the x position of the mouse. 
	 *
	 * @param MouseEvent default parameter for this mouse listener method
	 * @return  
	 */
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		paddle.setLocation(locationOfPaddle(), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}

	/**
	 * This method runs the game until winning or losing criteria have been met. Losing criteria: finishing out the number of turns. Winning criteria: destroying all blocks.
	 * Postcondition is a canvas with and updated playing situation as time passes and user moves mouse. 
	 * Note: This method incorporates and animation loop and event listeners.  
	 *
	 * @param 
	 * @return  
	 */
	private void play() {
		int bricksLeft = NBRICK_ROWS * NBRICK_COLUMNS;
		for (int turns = 0; turns < NTURNS; turns++) {
			ball.setLocation(getWidth()/2-BALL_RADIUS/2, getHeight()/2-BALL_RADIUS/2);
			newSpeeds();	
			boolean notLost = true;
			while (bricksLeft > 0 && notLost) {
				ball.move(vx, vy);
				notLost = reactIfWall();
				bricksLeft = bricksLeft - reactIfObject();
				pause(DELAY);
			}
		}
	}

	/**
	 * This method checks if the ball hits an object in its current position and reacts accordingly depending whether the hit object is a brick or the paddle. 
	 * Precondition: the speed in X and Y (measured in pixels per step) as initiated in the constants above cannot be greater the with (in pixels) of a bricks
	 * Postcondition: if the hit object is a brick, it will be removed from the canvas and the ball's vertical speed will be inverted. If the hit object is the paddle, the ball will set it's Y speed to go upwards.  
	 *
	 * @param 
	 * @return int 0 if the object hit was the paddle, 1 if the object touched and removed was a brick
	 */
	private int reactIfObject() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			vy = -Math.abs(vy);
		} else if (collider != null) {
			remove(collider);
			collider = null;
			vy = -vy;
			return 1;
		}
		return 0;
	}

	/**
	 * This method returns the object located on the canvas at the current location of the instance GOval ball  
	 * Precondition: GOval ball is declared and created
	 * Postcondition: object at position is returned. If no object at position, null will be returned.
	 * 
	 * @param 
	 * @return GObject object at the x and y coordinates of the ball 
	 */		
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS) != null){
			return getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		} else {
			return getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		} 
	}

	/**
	 * This method checks if the ball is hitting a wall in its current position and reacts accordingly depending on which wall was hit  
	 *
	 * @param 
	 * @return  boolean true if the wall hit was the left, right or top wall, false if it was the bottom wall
	 */
	private boolean reactIfWall() {
		if ( ball.getX() <= 0 || ball.getX() >= getWidth()-2*BALL_RADIUS ) {
			vx = -vx;
		} else if (ball.getY() <= 0) {
			vy = -vy;
		} else if (ball.getY() > getHeight()-2*BALL_RADIUS){
			return false; // The wall hit was the bottom wall
		}
		return true; // The wall hit was the left, right or top wall
	}

	/**
	 * This method updates the horizontal speed of the ball for the beginning of each round according to a random distribution
	 * Note: It's a separate method and it's called speed because it can be leveraged for extensions in which the vertical speed is also subject to change
	 * Precondition: The instance variables rgen, vx and vy must have been declared and created.
	 * Postcondition: The instance variables vx and vy will be updated  
	 *
	 * @param 
	 * @return 
	 */
	private void newSpeeds() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = - vx;
		}
		vy = VELOCITY_Y;		
	}

	/**
	 * This method calculates the appropriate X coordinate for the upper left corner of the paddle depending on the location of the mouse relative to the walls of the screen.  
	 * Precondition:Instance variable mouseX must have been declared
	 * Postcondition: No effects on any other instance take place
	 *
	 * @param 
	 * @return  double x coordinate for the upper left corner of the paddle to be used for animation purposes
	 */
	private double locationOfPaddle() {
		double leftBoundary = PADDLE_WIDTH / 2;
		double rightBoundary = getWidth() - PADDLE_WIDTH/2;
		if (mouseX < leftBoundary) {
			return 0;
		} else if (mouseX > rightBoundary) {
			return rightBoundary - PADDLE_WIDTH/2;
		} else {
			return mouseX - PADDLE_WIDTH / 2;
		}

	}
}
