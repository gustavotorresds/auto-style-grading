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

	public static final int BALL_RADII = 10;

/*
 * This program creates a game where the player has three lives and in order to win they must get rid of all the blocks, or in order to lose they must run out of all their lives
 * create several instant variables so they can be used in any of the methods 
 */

	public int r = 1;  
	private GOval ball = null;
	private GRect paddle = null;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double x;
	private double y;
	private static int NUMBER_BLOCKS = NBRICK_ROWS * NBRICK_COLUMNS;
	private static int lives = 3;


	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp();
		waitForClick();
		playGame();
	}

	public void mouseMoved(MouseEvent e) {
		/*
		 * checks to see if mouse is moved, gets coordinates of the mouse and sets the paddle to that
		 * the inequality makes sure that if the mouse goes off the screen the paddle does not
		 */
		x = e.getX();
		y = e.getY();
		if (x < getWidth()- PADDLE_WIDTH) {
		paddle.setLocation(x, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
	}
	}


	private void setUp() {
		/*
		 * uses nested for loop in same manner as in pyramid
		 */
		for (int r = 0; r < NBRICK_ROWS; r ++) {
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = ((getWidth()/2)-(NBRICK_COLUMNS/2)*(BRICK_WIDTH+BRICK_SEP));
				double y =0 + BRICK_Y_OFFSET;
				GRect block = new GRect (x+c*(BRICK_SEP + BRICK_WIDTH),y+r*(BRICK_SEP + BRICK_HEIGHT), BRICK_WIDTH, BRICK_HEIGHT);
				block.setFilled(true);
				if (r % 10 == 0 || r % 10 == 1) {
					block.setColor(Color.RED);
				}
				if (r % 10 == 2 || r % 10 == 3) {
					block.setColor(Color.ORANGE);
				}
				if (r % 10 == 4 || r % 10 == 5) {
					block.setColor(Color.YELLOW);
				}
				if (r % 10 == 6 || r % 10 == 7) {
					block.setColor(Color.GREEN);
				}
				if (r % 10 == 8 || r % 10 == 9) {
					block.setColor(Color.CYAN);
				}
				add (block);
				/*
				 * add Mouse Listeners so that the paddle can be controlled by mouse
				 */
				addMouseListeners();
			}
		}
		paddle = new GRect((getWidth()-PADDLE_WIDTH)/2,getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		ball = new GOval((getWidth()-2*BALL_RADII)/2, (getHeight() - 2*BALL_RADII)/2, 2*BALL_RADII, 2* BALL_RADII);
		ball.setFilled(true);
		add(ball);
	}

	private void playGame() {
		/*
		 * set vy to be y velocity and create a random generator to generate a different x velocity each time
		 */
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		/*
		 * boolean makes it so that half the time the ball moves ot the right and the other half it moves to the left
		 */
		if (rgen.nextBoolean(0.5)) vx = -vx; 
		/*
		 * the conditions for the game to keep on playing is the lives have to be greater than 0 or there has to still be blocks remaining
		 */
		while ((NUMBER_BLOCKS > 0) && (lives >0)) {
			ball.move(vx, vy);
			/*
			 * use pause and delay so that we can see the animation
			 */
			pause(DELAY);
			makeBallBounce();
			/*
			 *  this accounts for if the paddle misses the ball, and deducts 1 from the number of lives and then resets the ball to original position 
			 */
			if ((ball.getY() >  getHeight()- PADDLE_Y_OFFSET)) {
				ball.setLocation((getWidth()-2*BALL_RADII)/2, (getHeight() - 2*BALL_RADII)/2);
				lives -= 1;
			}
			findCollisions();
		}
		if (NUMBER_BLOCKS == 0) {
			remove(ball);
			println("Congrats! You Won!");
		}
	}



	private void makeBallBounce() {
		/*
		 * allows ball to bounce off the walls, whenever the x or y value is so that the ball is touching a wall the velocity is negated so it bounes off and goes the other way
		 */
		if (( ball.getX() < 0) || (ball.getX() >  getWidth()- BALL_RADIUS*2)) {
			vx = - vx;
		}
		if (ball.getY() < 0) {
			vy = - vy;
		}
	}

	private void findCollisions() {
		/* 
		 * uses GObject collider to check if the ball comes into contact with anything
		 * if the ball collides with paddle, switch the vy to - vy to make it bounce. Taking absolute value makes sure it doesn't stick to paddle
		 * else statement accounts for if it hits a block and it then removes the block and deducts the number of blocks by 1;
		 */
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			vy = -Math.abs(vy);
		} else if (collider != null) {
			vy = -vy;
			remove(collider);
			collider = null;
			NUMBER_BLOCKS -= 1 ;
		}
	}

	private GObject getCollidingObject() {
		/*
		 * checks all four corners of the ball using getElementAt to see if the ball has collided
		 * if there isn't a collision on one corner it returns collider and then checks the next one.
		 */
		GObject collider = getElementAt(ball.getX(),ball.getY());
		if (collider != null) {
			return collider;
		} else if ((collider = getElementAt(ball.getX()+2*(BALL_RADII),ball.getY())) != null) {
			return collider;
		} else if ((collider = getElementAt(ball.getX(),ball.getY()+2*BALL_RADII)) != null) {
			return collider;
		} else if ((collider = getElementAt(ball.getX(),ball.getY()+2*BALL_RADII)) != null) {
			return collider;
		}
		return null;
	}
}
