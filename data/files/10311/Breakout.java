/*
 * File: Breakout.java
 * -------------------
 * Name: A.J. "CS is cool" Aldana
 * Section Leader: Kaitlyn "CS is /incredible/" Lagattuta
 * 
 * This file plays the game breakout! The goal of this game is to clear
 * the world of bricks in less than or equal to three lives! Have fun!
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {
	/*
	 * THE FOLLOWING TEXT CONTAINS VARIABLES USED THROUGHOUT THE PROGRAM
	 */
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

	// Instance variable for paddle
	GRect paddle = null;

	// Instance variable for brick
	GRect brick = null;

	// Instance variable for ball
	GOval ball = null;

	public int count = 0;

	public int brickCounter = 0;

	// Private double for velocity
	private double vx, vy;

	// Private random number generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Build 10 rows of 10 bricks evenly spaced and centered including y-offset
		buildBricks();

		// Build paddle
		buildPaddle();

		for (int play = 0; play < 3; play++) {
			int lives = play;
			displayText(lives);
			buildBall();
		}

		endMessagePlays();

		// Adds mouse listeners
		addMouseListeners(); 

	}


	private void endMessagePlays() {
		GLabel turnThree = new GLabel ("YOU LOST!");
		GLabel over = new GLabel ("GAME OVER, PLEASE CLOSE WINDOW.");
		add (turnThree, getWidth() / 2 - (turnThree.getWidth() / 2), getHeight() / 2 - (turnThree.getAscent()));
		add (over, getWidth() / 2 - (over.getWidth() / 2), getHeight() / 2 + (over.getAscent() * 2));
		waitForClick();
		remove (turnThree);
		remove (over);

	}


	private void displayText(int lives) {
		if (lives == 0)  {
			GLabel start = new GLabel ("CLICK TO START!");
			add (start, getWidth() / 2 - (start.getWidth() / 2), getHeight() / 2 - (start.getAscent()));
			waitForClick();
			remove (start);
		}
		if (lives == 1)  {
			GLabel turnOne = new GLabel ("2 LIVES REMAINING");
			add (turnOne, getWidth() / 2 - (turnOne.getWidth() / 2), getHeight() / 2 - (turnOne.getAscent()));
			waitForClick();
			remove (turnOne);
		}
		if (lives == 2)  {
			GLabel turnTwo = new GLabel ("1 LIFE REMAINING!");
			add (turnTwo, getWidth() / 2 - (turnTwo.getWidth() / 2), getHeight() / 2 - (turnTwo.getAscent()));
			waitForClick();
			remove (turnTwo);
		}
	}


	private void collisionActions() {
		getCollidingObjects();

		collider = getCollidingObjects();

		if (collider != null && collider == paddle) {
			vy = -vy;
		} else if (collider != null) {
			remove(collider);
			brickCounter++;
			vy = -vy;
		}
		if (brickCounter == NBRICK_ROWS * NBRICK_COLUMNS) {
			remove(ball);
			endMessageBricks();
		}

	}

	private void endMessageBricks() {
		GLabel ballsGone = new GLabel ("YOU WON!");
		GLabel over = new GLabel ("GAME OVER, PLEASE CLOSE WINDOW.");
		add (ballsGone, getWidth() / 2 - (ballsGone.getWidth() / 2), getHeight() / 2 - (ballsGone.getAscent()));
		add (over, getWidth() / 2 - (over.getWidth() / 2), getHeight() / 2 + (over.getAscent() * 2));
		waitForClick();
		remove (ballsGone);
		remove (over);

	}


	private GObject getCollidingObjects() {
		double collObjX = ball.getX();
		double collObjY = ball.getY();

		GObject collObj1 = getElementAt(collObjX, collObjY);
		GObject collObj2= getElementAt(collObjX + (2 * BALL_RADIUS), collObjY);
		GObject collObj3 = getElementAt(collObjX, collObjY + (2 * BALL_RADIUS));
		GObject collObj4 = getElementAt(collObjX + (2 * BALL_RADIUS), collObjY + (2 * BALL_RADIUS));

		if (collObj1 != null) {
			return collider = collObj1;
		} else if (collObj2 != null) {
			return collider = collObj2; 
		} else if (collObj3 != null) {
			return collider = collObj3; 
		} else if (collObj4 != null) {
			return collider = collObj4; 
		} else {
			return null;
		}
	}

	public GObject collider;

	/* 
	 * This method creates the ball and helps it move
	 */
	private void buildBall() {
		double xBall = ((getWidth() / 2) - (BALL_RADIUS / 2));
		double yBall = ((getHeight() / 2) - (BALL_RADIUS / 2));
		// Build ball


		ball = new GOval (BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add (ball, xBall, yBall);

		ballMove();

	}

	private void ballMove() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		// Waits for user to click mouse to begin animation
		waitForClick();
		while(true) {
			if (ball != null) { 
				updateVelocity();
				collisionActions();
				if (ball.getY() > getHeight()) {
					break;
				}
			}
		}

	}



	private void updateVelocity() {
		// Updates velocity
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball)) {
			vy = -vy;
		}
		if(hitBottomWall(ball)) {
			remove(ball);
			ball = null;
		}
		// Updates visualization
		ball.move(vx, vy);

		// Pauses animation
		pause(DELAY);

	}


	// This method checks to see if the ball hits an object below it

	private boolean hitBottomWall(GOval ball2) {

		return ball.getY() > getHeight() + (2 * BALL_RADIUS);
	}

	/*
	 * This method checks to see if the ball hits an object above it
	 */
	private boolean hitTopWall(GOval ball2) {
		return ball.getY() <= 0;
	}

	/*
	 * This method checks to see if the ball hits an object to its right
	 */
	private boolean hitRightWall(GOval ball2) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*
	 * This method checks to see if the ball hits an object to its left
	 */
	private boolean hitLeftWall(GOval ball2) {
		return ball.getX() <= 0;
	}

	/* 
	 * This method creates the paddle
	 */
	private void buildPaddle() {
		double xPaddle = ((getWidth() / 2)) - (PADDLE_WIDTH / 2);
		double yPaddle = ((getHeight()) - (PADDLE_Y_OFFSET + PADDLE_HEIGHT));

		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add (paddle, xPaddle, yPaddle);
	}

	/*
	 * This method makes the paddle track mouse movement in the X-direction
	 */
	public void mouseMoved(MouseEvent e) {
		double paddleMoveX = (e.getX() - (PADDLE_WIDTH / 2));
		double paddleMoveY = ((getHeight()) - (PADDLE_Y_OFFSET + PADDLE_HEIGHT));

		if (paddleMoveX <= getWidth() - PADDLE_WIDTH && paddleMoveX >= 0) {
			paddle.setLocation(paddleMoveX, paddleMoveY);
		}
	}

	/*
	 * This method creates the rows of bricks
	 */
	private void buildBricks() {
		double xBrick = ((getWidth() / 2) - (NBRICK_COLUMNS / 2) * (BRICK_WIDTH + BRICK_SEP));
		double yBrick = ((NBRICK_ROWS * BRICK_SEP) + (BRICK_Y_OFFSET));
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				GRect brick = createBrick(xBrick, yBrick);
				xBrick += BRICK_WIDTH + BRICK_SEP;
				createRainbowBricks(row, brick); 
			}
			yBrick += BRICK_HEIGHT + BRICK_SEP;
			xBrick -= ((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS);
		}
	}

	/*
	 * This method creates a singular brick
	 */
	private GRect createBrick(double xBrick, double yBrick) {
		GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		add (brick, xBrick + (BRICK_SEP / 2), yBrick);
		return brick;
	}

	/*
	 * This method creates the rainbow bricks, letting every two
	 * rows change in color (R > O > Y > G > B)
	 */
	private void createRainbowBricks(int row, GRect brick) {
		if ((row) % 10 <= 1) {
			brick.setColor(Color.RED);	
		} else if((row) % 10 <= 3) {
			brick.setColor(Color.ORANGE);						
		} else if((row) % 10 <= 5) {
			brick.setColor(Color.YELLOW);						
		} else if((row) % 10 <= 7) {
			brick.setColor(Color.GREEN);						
		} else if((row) % 10 <= 9) {
			brick.setColor(Color.CYAN);						
		}
	}

}
