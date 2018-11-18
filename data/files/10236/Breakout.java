/*
 * File: Breakout.java
 * -------------------
 * Name: Smith Graham
 * Section Leader: Niki Agrawal
 * 
 * This program allows the user to engage in the most incredible gaming experience of their life. Upon launching the program, 
 * the user has the opportunity to act as the player in the magical game of Breakout.
 * 
 * I looked at the Stanford graphics library page for help.
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
	
	// Message to be displayed after a win
	public static final String CONGRATS = "CONGRATULATIONS! YOU'RE A WINNER!";
	
	// Message to be displayed after a loss
	public static final String SUCKS_TO_SUCK_1 = "THAT WAS AN EMBARASSINGLY POOR PERFORMANCE.";
	public static final String SUCKS_TO_SUCK_2 = "YOU'VE BROUGHT IMMENSE DISHONOR TO YOUR FAMILY.";
	
	// Instance variable for the paddle
	GRect paddle = null;
	
	// Instance variable for the ball
	GOval ball = null;
	
	// Instance variable for the life counter
	GLabel lifeCounter = null;
	
	
	// Instance variables for the ball's velocity in the x and y directions
	private double vx;
	private double vy = VELOCITY_Y;
	
	// Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		int livesRemaining = 3;
		setup(livesRemaining);
		addMouseListeners();
		
		setXVelocity();
		
		int numberOfBricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;
		int numberOfPaddleBounces = 0;
		playGame(numberOfBricksRemaining, livesRemaining, numberOfPaddleBounces);
		
	}
	
	/*
	 * Precondition: nothing exists on the screen
	 * Postcondition: the bricks, paddle, and ball have been added to the screen. The paddle is at the bottom, centered horizontally.
	 * The ball is centered on top of the paddle. The bricks are in rows at the top of the screen.
	 */
	private void setup(int livesRemaining) {
		
		makeBricks();
		makePaddle();
		makeBall();						
		waitForClick();
	}
	
	/*
	 * Precondition: no bricks
	 * Postconditions: the bricks are on the screen
	 */
	private void makeBricks() {
		
		for(int i = 0; i < NBRICK_ROWS; i++) {
			makeRow(i);
		}
	}
	
	/*
	 * Precondition: the row contains no bricks
	 * Postcondition: the row of bricks has been created
	 */
	private void makeRow(int rowNumber) {
		
		double rowY = (rowNumber * (BRICK_HEIGHT + BRICK_SEP)) + BRICK_Y_OFFSET;
		Color rowColor = rowColor(rowNumber);
		
		for(int i = 0; i <  NBRICK_COLUMNS; i++) {
			makeBrick(i, rowY, rowColor);
		}
	}
	
	/*
	 * Determines the color of the row. Because there are 10 rows in a full color cycle, the color is based on the row number mod 10.
	 * Since each pair of rows gets the same color, rowNumber mod 10 is floored. So  the floor value of rows 0 and 1 mod 10 is 0, for 
	 * 2 and 3, the value is 1, etc.
	 */
	private Color rowColor(int rowNumber) {
		Color rowColor = null;
		if(Math.floor((rowNumber % 10) / 2) == 0) {
			rowColor = Color.RED;
		} else if(Math.floor((rowNumber % 10) / 2) == 1) {
			rowColor = Color.ORANGE;
		} else if(Math.floor((rowNumber % 10) / 2) == 2) {
			rowColor = Color.YELLOW;
		} else if(Math.floor((rowNumber % 10) / 2) == 3) {
			rowColor = Color.GREEN;
		}else if(Math.floor((rowNumber % 10) / 2) == 4) {
			rowColor = Color.CYAN;
		}
		return rowColor;
	}
	
	/*
	 * Precondition: no brick
	 * Postcondition: a brick exists
	 */
	private void makeBrick(int columnNumber, double brickY, Color brickColor) {
		
		double brickX = columnNumber * (BRICK_WIDTH + BRICK_SEP);
		GRect brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(brickColor);
		brick.setFilled(true);
		add(brick);
		
	}
	
	/*
	 * Precondition: no paddle
	 * Postcondition: paddle exists centered horizontally, just above the very bottom of the screen
	 */
	private void makePaddle() {
		
		double paddleY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2;
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/*
	 * This method makes the paddle follow the mouse horizontally while maintaining a constant y.
	 */
	public void mouseMoved(MouseEvent e) {
		
		double paddleY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		double paddleX = e.getX();
		if(paddleX > (getWidth() - PADDLE_WIDTH)) {
			paddleX = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(paddleX, paddleY);
	}
	
	private void makeBall() {
		
		double ballX = getWidth()/2 - BALL_RADIUS;
		double ballY = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(ballX, ballY, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}
	
	/*
	 * Boolean whose value is true if the ball has had a collision on the side
	 */
	private boolean sideWallCollision() {
		return ball.getX() <= 0 || ball.getX() >= (getWidth() - BALL_RADIUS * 2);
	}
	
	/* 
	 * Boolean whose value is true if the ball has had a collision on the top or bottom
	 */
	private boolean verticalWallCollision() {
		return ball.getY() <= 0;
				//getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS * 2) == paddle;
	}
	
	private GObject getCollidingObject() {
		double ballLeftX = ball.getX();
		double ballTopY = ball.getY();
		double ballRightX = ball.getX() + BALL_RADIUS * 2;
		double ballBottomY = ball.getY() + BALL_RADIUS * 2;
		
		GObject collidingObject = null;
		
		if(getElementAt(ballLeftX, ballTopY) != null) {
			collidingObject = getElementAt(ballLeftX, ballTopY);
		} else if(getElementAt(ballRightX, ballTopY) != null) {
			collidingObject = getElementAt(ballRightX, ballTopY);
		} else if(getElementAt(ballLeftX, ballBottomY) != null) {
			collidingObject = getElementAt(ballLeftX, ballBottomY);
		} else if(getElementAt(ballRightX, ballBottomY) != null) {
			collidingObject = getElementAt(ballRightX, ballBottomY);
		}
		return collidingObject;
		
	}
	
	/*
	 * Reverses y direction
	 */
	private void verticalBounce() {
		vy = -vy;
	}
	
	/*
	 * Reverses x direction
	 */
	private void horizontalBounce() {
		vx = -vx;
	}
	
	/*
	 * Returns true when the player has won (no bricks are left)
	 */
	private boolean playerWins(int numberOfBricksRemaining) {
		return numberOfBricksRemaining == 0;
	}
	
	/*
	 * Returns true when the player has lost a life (the ball hit the bottom)
	 */
	private boolean ballHitBottom() {
		return ball.getY() >= (getHeight() - BALL_RADIUS * 2);
	}
	
	/*
	 * Sets a random x velocity between 1.0 and 3.0 and randomly chooses the direction left or right
	 */
	private void setXVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	
	/*
	 * Controls gameplay. Creates a new ball at the beginning of each life, checks for collisions with walls, checks for collisions
	 * with bricks and the paddle. Resets ball and removes a life when the ball hits the bottom. etc
	 */
	private void playGame(int numberOfBricksRemaining, int livesRemaining, int numberOfPaddleBounces) {
		
		while(livesRemaining > 0 && !playerWins(numberOfBricksRemaining)) {

			if(sideWallCollision()) {
				horizontalBounce();
			} else if(verticalWallCollision()) {
				verticalBounce();
			}
			GObject collidingObject = null;
			if(getCollidingObject() != null) {
				collidingObject = getCollidingObject();
			
				if(collidingObject == paddle) {
					verticalBounce();
					numberOfPaddleBounces++;
				} else {
					verticalBounce();
					remove(collidingObject);
					numberOfBricksRemaining--;
				}
			}
			
			ball.move(vx, vy);
			pause(DELAY);
			if(ballHitBottom()) {					//Controls what happens when the player loses a life
				remove(ball);						//Dead ball disappears
				livesRemaining--;					//Lives decreases by 1
				makeBall();							//New ball is added in the center
				waitForClick();
			}
		}
	}

}
