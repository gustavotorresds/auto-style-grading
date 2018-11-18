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

	// INSTANCE VARIABLES - defining paddle and rgen - 'paddle' because we want the same variable to be recognized throughout the program and 'rgen' because the RandomGenerator function requires the variable to be defined as an Instant 
	//define paddle
	GRect paddle = null;

	//define rgen for random movement of the ball
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//laying down the Breakout program!
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//Set up the bricks - very similar to the pyramid method - define a private method for the rowcolor

		for (int row = 0; row < NBRICK_ROWS; row ++) {
			for (int col = 0; col < NBRICK_COLUMNS; col ++) {
				double x = (getWidth()/2 - (NBRICK_COLUMNS * (BRICK_WIDTH))/2 - ((NBRICK_COLUMNS - 1) * BRICK_SEP)/2); // x is calculated from left to right
				double y = BRICK_Y_OFFSET;
				GRect brick = new GRect (x + col * (BRICK_WIDTH + BRICK_SEP), y + row * (BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				Color rowColor = getColor(row);
				brick.setColor(rowColor);				
				add (brick);
			}
		}
		//first establish the set up and then work on the 'play' phase of the game
		// set up paddle
		double xPaddle = (getWidth()/2 - PADDLE_WIDTH/2);
		double yPaddle = (getHeight() - PADDLE_Y_OFFSET);

		paddle = new GRect (xPaddle, yPaddle, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor((Color.BLACK));
		paddle.setFilled(true);
		add (paddle);

		//make the paddle respond to the mouse movement on the screen
		addMouseListeners();

		//create the ball - at the center
		double xBall = getWidth()/2 - BALL_RADIUS;
		double yBall = getHeight()/2 - BALL_RADIUS;

		GOval ball = new GOval ((xBall), (yBall), (BALL_RADIUS*2), (BALL_RADIUS*2));
		ball.setFilled (true); 
		ball.setColor (Color.BLACK);
		add(ball);

		//make the ball bounce and make the x velocity of the ball to be random - use the rgen instant variable defined above
		double vxBall = rgen.nextDouble (VELOCITY_X_MIN,VELOCITY_X_MAX); 
		if (rgen.nextBoolean(0.5)) {
			vxBall = - vxBall; //makes the velocity negative half the time
		}

		double vyBall = VELOCITY_Y; //fixed velocity

		//define a function for figuring out the remaining bricks - this is important because if the total bricks are removed before the Nturns end, the player would have won the game and the game would stop - a for loop for the same has been created below
		int remainingbricks = NBRICK_ROWS * NBRICK_COLUMNS;

		for (int i = 0 ; i < NTURNS; i++) {
			if(remainingbricks == 0) {
				break;
			}

			//make sure that the ball remains within the four corners of the game screen - define private methods for all four corners such that if the ball stikes either left, right or the top wall, its velocity is reversed in the opposite direction 
			waitForClick();		
			ball.setLocation(xBall - BALL_RADIUS, yBall - BALL_RADIUS);

			//while loop because the ball would have to continue to move until it hits the bottom wall or all the bricks are eliminated 
			while(true) {
				if(hitLeftWall (ball) || hitRightWall(ball)) {
					vxBall = -vxBall;
				}	else if (hitTopWall(ball)) {
					vyBall = -vyBall;
				} else if(hitBottomWall(ball)) {
					break;
				}	else if (remainingbricks == 0) {
					break;
				}

				ball.move(vxBall, vyBall);
				pause(DELAY);

				// define a function for when the ball strikes either the paddle or the bricks - velocity to be reversed in the opposite direction if the ball hits the paddle or a brick, but in case of brick collision, the brick would also be removed 
				GObject object = getCollidingObject(ball); //defined below for the system to ascertain which object the ball is colliding with

				if (object == paddle) {
					vyBall = -vyBall; 
				} else if (object != null) {
					vyBall = -vyBall;
					remove (object);
					remainingbricks = remainingbricks - 1; //with each removal - there would be one less brick from the total number of bricks - and the remainingbricks function defined above attains the new reduced value of remainingbricks
				}
			}
		}
	}

	//HitLeftWall
	private boolean hitLeftWall (GOval ball) {
		return ball.getX() <=0;
	}

	//HitRightWall - account for the fact that the ball's coordinates are left most corner - and that even if the ball hits the wall from the opposite end, the velocity of the ball would be reversed - this is to ensure that the ball doesn't leave the game fence
	private boolean hitRightWall (GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	//hitTopWall
	private boolean hitTopWall (GOval ball) {
		return ball.getY() <= 0;
	}

	//hitBottomWall
	private boolean hitBottomWall (GOval ball) {
		return ball.getY() >= getHeight()- ball.getHeight();
	}

	//check collision - create a private function for ascertaining whether the ball collides with an object
	private GObject getCollidingObject(GOval ball) {
		double x = ball.getX(); //get the x coordinate of the ball
		double y = ball.getY();	//get the y coordinate of the ball	

		GObject object = getElementAt (x,y); //system to figure out if the ball's coordinates strike any object - all the four corners of the ball to be checked as required in the Assignment
		if (object != null) {
			return object; 
		}
		object = getElementAt (x + BALL_RADIUS * 2, y);
		if (object != null) {
			return object; 
		}
		object = getElementAt (x, y + BALL_RADIUS *2); 
		if (object != null) {
			return object; 
		}
		object = getElementAt (x + BALL_RADIUS * 2, y + BALL_RADIUS *2);
		if (object != null) {
			return object;
		} 
		return null;

	}

	// make paddle move according to the mouse
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		paddle.setLocation(x, (getHeight() - PADDLE_Y_OFFSET));
	}

	//establish a private function for determining the color of the rows
	private Color getColor(int row) {
		int rownumber = row % NBRICK_ROWS; //remainder function to get the last digit of the row number

		if(rownumber == 0 || row % NBRICK_ROWS == 1) {
			return Color.RED; 
		} else if (row % NBRICK_ROWS == 2 || row % NBRICK_ROWS == 3) {
			return Color.ORANGE;
		} else if (row % NBRICK_ROWS == 4 || row % NBRICK_ROWS == 5) {
			return Color.YELLOW; 
		} else if (row % NBRICK_ROWS == 6 || row % NBRICK_ROWS == 7) { 
			return Color.GREEN; 
		} else if (row % NBRICK_ROWS == 8 || row % NBRICK_ROWS == 9) {
			return Color.CYAN;
		} else {
			return Color.BLACK;
		}

	}

}