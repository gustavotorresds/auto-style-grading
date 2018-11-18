
/*
 * File: Breakout.java



 * -------------------
 * Name: Julia Rathmann-Bloch jrbloch
 * Section Leader: Ben Barnett
 * 1/31/18
 * 
 * This program implements breakout!
 * 
 * Notes: Please grade this version. I found some bugs when doing the extension
 * so I edited the original. This is the updated original.
 * 
 * Citations: I copied the bouncing ball code from the animation lecture
 * to move the ball. 
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

	// Delay after the ball hits the bottom (ms)
	public static final double  WAIT_TIME = DELAY * 10;

	// Number of turns 
	public static final int NTURNS = 3;

	// How much the score increases when you hit a brick
	private static final int SCORE_INCREASE = 100;

	//Instance Variables
	private int brickCounter = 0;
	private GLabel bricksLeft;
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int lifeCounter = NTURNS;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();

		//setup
		setUpLevel1();
		placeBall();
		waitForClick();
		double vx = vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		double vy = VELOCITY_Y;

		//animation loop
		while(true) {
			// update velocity
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			if (hitBottomWall(ball)) {
				lifeCounter--;
				remove(ball);
				if (lifeCounter == 0) {
					removeAll();
					GLabel youLose = new GLabel ("You Lose :(", getWidth()/2, getHeight()/2);
					double x = youLose.getWidth();
					double y = youLose.getAscent();
					youLose.setLocation((getWidth()-x)/2, (getHeight()-y)/2);
					add(youLose);
					pause(WAIT_TIME);
					break;
				}
				placeBall();
				waitForClick();
			}

			// update visualization
			ball.move(vx, vy);

			// pause
			pause(DELAY);

			// check for collisions
			vy =checkForCollisions(vy);

			//update counters
			updateBricksLeft();

			//check to see if the game is over
			if (brickCounter == 0) {
				removeAll();
				GLabel youWin = new GLabel ("You Win!!", getWidth()/2, getHeight()/2);
				add(youWin);
				pause(WAIT_TIME);
				break;
			}
		}
	}

	private double checkForCollisions(double vy) {
		GObject maybeBrick = collisionChecker();
		if (maybeBrick != paddle && maybeBrick != null ) {
			remove(maybeBrick);
			brickCounter--;
			return -vy;
		} else if (maybeBrick == paddle) {
			return -1 * absValue(vy);
		} return vy;
	}

	/*
	 * Method: Absolute Value
	 * ------------------------
	 * Given a double, this method returns the absolute value of
	 * that number. I'm using it to get rid of the bug where the ball
	 * gets impaled on the paddle. 
	 */
	private double absValue(double x) {
		if(x>0) {
			return x;
		}else {
			return -x;
		}
	}


	/*
	 * Method: Update Bricks Left
	 * ------------------------------------
	 * This method updates the number of bricks displayed on the screen.
	 */
	private void updateBricksLeft() {
		bricksLeft.setLabel("Bricks left: " + brickCounter);
	}

	/*
	 * Method: Collision Checker
	 * ----------------------------------
	 * This method checks if any of the ball's corners have collided with 
	 * an object and returns that object.
	 */
	private GObject collisionChecker() {
		double centerX =ball.getCenterX();
		double centerY = ball.getCenterY();
		GObject topLeftCorner = getElementAt(centerX -BALL_RADIUS, centerY-BALL_RADIUS);
		GObject topRightCorner = getElementAt(centerX +BALL_RADIUS, centerY-BALL_RADIUS);
		GObject bottomLeftCorner = getElementAt(centerX -BALL_RADIUS, centerY+BALL_RADIUS);
		GObject bottomRightCorner = getElementAt(centerX +BALL_RADIUS, centerY+BALL_RADIUS);

		if (bottomLeftCorner != null) {
			return bottomLeftCorner;
		} else if ( topLeftCorner!= null) {
			return topLeftCorner;
		} else if (bottomRightCorner != null) {
			return bottomRightCorner;
		} else if (topRightCorner != null) {
			return topRightCorner;
		} else {
			return null;
		}
	}

	/*
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * This method checks to see if the ball has hit or is below the bottom wall.
	 */
	private Boolean hitBottomWall(GOval ball) {
		return(ball.getY() > getHeight() - ball.getHeight());
	}

	/*
	 * Method: Hit Top Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * Method: Hit Right Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the right wall of the window.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*
	 * Method: Hit Left Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the left wall of the window.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/*
	 * Method: Place Ball
	 * -------------------------
	 * This method draws the ball in the center of the screen.
	 */
	private void placeBall() {
		ball = new GOval (getWidth()/2-BALL_RADIUS, getHeight()/2+ BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}


	/*
	 * Method: Display Brick Counter
	 * ------------------------------------
	 * This method adds a label to the upper left corner
	 * of the screen with the words "Bricks left:"  and the
	 * number of bricks. 
	 */
	private void displayBrickCounter() {
		bricksLeft = new GLabel ("Bricks left: " + brickCounter, 10, 40);
		add(bricksLeft);
	}

	/*
	 * Method: Place Paddle
	 * ---------------------------------
	 * This method puts the paddle in the bottom center of the screen.
	 */
	private void placePaddle() {
		paddle = new GRect (getWidth()/2 - PADDLE_WIDTH/2, getHeight()- PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);

	}

	/*
	 * Method: Mouse Moved
	 * ----------------------------
	 * This method uses the built in Mouse Moved method to
	 * set the paddle location to that of the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double paddleX= e.getX();
		double paddleLength = paddle.getWidth();
		if (paddleX<getWidth()-paddleLength) {
			paddle.setLocation(paddleX, (getHeight()-PADDLE_Y_OFFSET));
		}
	}

	/*
	 * Method: Set Up Level 1
	 * --------------------------------
	 * This method sets up the second level with cool random bricks
	 * and powerups.
	 */
	private void setUpLevel1() {
		placeBricks1();
		placePaddle();
		displayBrickCounter();
	}

	/*
	 * Method: Place Bricks 1
	 * ------------------------------------
	 * This method places bricks for level 1. 
	 * 
	 */
	private void placeBricks1() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			double y = BRICK_Y_OFFSET + (row)* (BRICK_HEIGHT + BRICK_SEP);
			double x = (getWidth()- (NBRICK_COLUMNS * ((BRICK_WIDTH)) + 
					(NBRICK_COLUMNS-1) * ((BRICK_SEP)))) /2.0;
			for (int col = 0; col < NBRICK_COLUMNS; col ++) {
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				paintBricks1(brick,row);
				x=x+BRICK_WIDTH + BRICK_SEP;
				brickCounter ++;
			}
		}
	}

	/*
	 * Method: Paint Bricks 1
	 * ----------------------------------
	 * This method paints the bricks according to the specifications.
	 */
	private void paintBricks1(GRect brick, int row) {
		if(row == 0 || row == 1) {
			brick.setColor(Color.RED);
		} else if (row == 2 || row == 3) {
			brick.setColor(Color.ORANGE);
		} else if (row == 4 || row == 5) {
			brick.setColor(Color.YELLOW);
		} else if (row == 6 || row == 7) {
			brick.setColor(Color.GREEN);
		} else if (row == 8 || row == 9) {
			brick.setColor(Color.CYAN);
		} else {
			brick.setColor(Color.BLACK);
		}
		add(brick);
	}
	
}