/*
 * File: Breakout.java
 * -------------------
 * S106ATiles.java
 * Name: Seth Liyanage
 * Date: 02/06/18
 * Section Leader: Marilyn Zhang 
 * Sources: Style Guide, Assignment #3 Hand out, Stanford java lib
 * -------------------------------------------------
 * Creates the program breakout
 * Pre: Empty screen
 * Post: 100 bricks, 20 or each of the 5 colors, Red, Orange, Yellow, Green, and Cyan.
 * Paddle at the bottom of the screen at the defined distance. ball created around the
 * center of the screen. The ball moves at a set y velocity with a changing x velocity 
 * depending on where it hits on the paddle. Ball bounces off of the walls, paddle and 
 * brick, excluding the bottom wall which if hit will respawn the ball at the center of 
 * the screen and remove a life. Bricks will be removed if hit. Once either all of the 
 * bricks are removed or the lives are lost a message will appear on the screen
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
	
	// The object of the paddle
	private GRect paddle = null;
	
	//The object of the ball
	private GOval ball = null;
	
	// The x and y velocities
	private double vx, vy;
	
	//The random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		int counter = 100;
		int lives = NTURNS;
		
		makeBricks();
		
		makePaddle();
		
		addMouseListeners();
		
		makeBall();
		
		defineStartVelocity();
		
		
		while(counter > 0 && lives > 0 ) {
			ball.move(vx, vy);
			double topSide = ball.getY();
			double bottomSide = ball.getY() + (2 * BALL_RADIUS);
			double leftSide = ball.getX();
			double rightSide = ball.getX() + (2 * BALL_RADIUS);
			keepBallInBounds(topSide, bottomSide, rightSide, leftSide);	
			GObject collider = getCollidingObject(topSide, bottomSide, rightSide, leftSide);
			counter = removingObjectsAndHittingPaddle(counter, collider); // removes objects when hit by ball and bounces ball off paddle if hit
			lives = ballHitsBottom(bottomSide, lives);
			pause(DELAY);
		}
		removeAll();
		endMessage(counter, lives);
	}
	
	/*
	 * Creates an end message depending on whether the player wins or loses
	 */
	private void endMessage(int counter, int lives) {
		if(lives == 0) {
			GLabel label = new GLabel("Better Luck Next Time");
			label.setFont("Sans Serif-30");
			double xPosition = (getWidth() - label.getWidth()) / 2;
			double yPosition = (getHeight() - label.getHeight()) / 2;
			label.move(xPosition, yPosition);
			add(label);
		}else if(counter == 0) {
			GLabel winLabel = new GLabel("Congratulations");
			winLabel.setFont("Sans Serif-30");
			double xPosition = (getWidth() - winLabel.getWidth()) / 2;
			double yPosition = (getHeight() - winLabel.getHeight()) / 2;
			winLabel.move(xPosition, yPosition);
			add(winLabel);
		}
	}

	/*
	 * Roves objects when ball hits them and calculates the direction the ball goes 
	 * when it hits the paddle
	 */
	private int removingObjectsAndHittingPaddle(int counter, GObject collider) {
		if (collider != null) {
			vy = -vy;
			if (collider != paddle) {
				remove(collider);
				counter--;
			} else if(collider == paddle) {
				vx = ((ball.getCenterX() - paddle.getCenterX()) / PADDLE_WIDTH)  * 3;	// Makes the vx change proportionally depending in where the center of the ball hits the paddle
				double ballBottom = ball.getBottomY();
				double paddleTop = paddle.getY();
				if(ballBottom > paddleTop + 3) {			// Stops the ball from getting stuck inside the paddle, as well as increasing the vx if the ball hits the side
					ball.setLocation(ball.getX(), paddle.getY() - 2 * BALL_RADIUS);
					vx = (absoluteValue(vx) / vx) * 5;
				}
			} 
		} 
		return counter;
	}

	/*
	 * Calculates the absolute value
	 */
	private double absoluteValue(double value) {
		if(value < 0) {
			value = value * -1;
		}
		return value;
	}

	/*
	 * Resets the ball if it hits the bottom and removes a life
	 */
	private int ballHitsBottom(double bottomSide, int lives) {
		if(bottomSide > getHeight()) {
			defineStartVelocity();
			ball.setLocation((getWidth()-(2 * BALL_RADIUS))/2, getHeight()/2);
			lives--;
		}
		return lives;
	}


	/*
	 * Changes the balls velocity when it hits a wall and also keeps the ball 
	 * in bounds to fix bug which would sometimes cause ball to get stuck on a side
	 */
	private void keepBallInBounds(double topSide, double bottomSide, double rightSide, double leftSide) {
		if(topSide < 0) {
			vy = -vy;
			ball.setLocation(ball.getX(), 0);
		}  
		if(leftSide < 0) {
			vx = -vx;
			ball.setLocation(0, ball.getY());
		} else if(rightSide > getWidth()) {
			vx = -vx;
			ball.setLocation(getWidth() - 2 * BALL_RADIUS, ball.getY());
		}
	}

	/*
	 * Find if ball hits any object on the screen
	 */
	private GObject getCollidingObject(double topSide, double bottomSide, double rightSide, double leftSide) {
		
		if(getElementAt(rightSide, topSide) != null) {
			return getElementAt(rightSide, topSide);
		} else if(getElementAt(rightSide, bottomSide) != null) {
			return getElementAt(rightSide, bottomSide);
		} else if(getElementAt(leftSide, topSide) != null) {
			return getElementAt(leftSide, topSide);
		} else if(getElementAt(leftSide, bottomSide) != null) {
			return getElementAt(leftSide, bottomSide);
		} else {
			return null;
		}
	}

	/*
	 * defines the starting velocity of the ball
	 */
	private void defineStartVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
	}

	/*
	 * Create ball
	 */
	private void makeBall() {
		ball = new GOval((getWidth()-(2 * BALL_RADIUS))/2, getHeight()/2, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * Create the games paddle
	 */
	private void makePaddle() {
		double startWidthLocation = (getWidth() - PADDLE_WIDTH) / 2;
		double startHeightLocation = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(startWidthLocation, startHeightLocation, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);;
		add(paddle);
	}

	/*
	 * Makes the 10 by 10 grid of bricks
	 */
	private void makeBricks() {
		for(int rowNumber = 0; rowNumber < NBRICK_ROWS; rowNumber++) {
			for(int columnNumber = 0; columnNumber < NBRICK_COLUMNS; columnNumber++) {
				double brickStartWidthLocation = (getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) + (NBRICK_COLUMNS-1) * BRICK_SEP)) / 2;
				double brickStartHeightLocation = BRICK_Y_OFFSET;
				double brickWidthLocation = brickStartWidthLocation + (columnNumber * (BRICK_WIDTH + BRICK_SEP));
				double brickHeightLocation = brickStartHeightLocation + (rowNumber * (BRICK_HEIGHT + BRICK_SEP));
				makeBrick(brickWidthLocation, brickHeightLocation, rowNumber);
			}
		}
	}
	
	/*
	 * Creates each brick and gives it color
	 */
	private void makeBrick(double brickWidthLocation, double brickHeightLocation, int rowNumber) {
		GRect brick = new GRect(brickWidthLocation, brickHeightLocation, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		if(rowNumber == 0 || rowNumber == 1) {
			brick.setColor(Color.RED);
		}else if(rowNumber == 2 || rowNumber == 3) {
			brick.setColor(Color.ORANGE);
		}else if(rowNumber == 4 || rowNumber == 5) {
			brick.setColor(Color.YELLOW);
		}else if(rowNumber == 6 || rowNumber == 7) {
			brick.setColor(Color.GREEN);
		} else if(rowNumber == 8 || rowNumber == 9) {
			brick.setColor(Color.CYAN);
		}
		add(brick);	
	}

	/*
	 * (non-Javadoc)
	 * @see acm.program.Program#mouseMoved(java.awt.event.MouseEvent)
	 * Move paddle based on mouse movement
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleWidthLocation = mouseX - (PADDLE_WIDTH / 2);
		double maxWidthValue = getWidth() - PADDLE_WIDTH;
		double paddleHeightLocation = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if(paddleWidthLocation > 0 && paddleWidthLocation < maxWidthValue) {
			paddle.setLocation(paddleWidthLocation,paddleHeightLocation);
		}
	}
}
