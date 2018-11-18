/*
 * File: Breakout.java
 * -------------------
 * Name: Felicia Hou
 * Section Leader: Julia Truitt
 * 
 * This program runs the game of Breakout, where the user has 3 lives
 * to bounce the ball off of the paddle to remove all the bricks from
 * the screen.
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
	public static final double VELOCITY_Y = 7.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	GRect paddle = null; 

	GOval ball;

	private double vx, vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private int numOfBricks = NBRICK_ROWS*NBRICK_COLUMNS;

	private int lives = 3;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setUpGame();	
		for (int i = 0; i < lives; i++) {
			addBall();
			moveBall();
			if (numOfBricks == 0) {
				GLabel win = new GLabel ("Game Won.");
				remove (ball);
				add (win, getWidth()/2 - win.getWidth()/2, getHeight()/2 + win.getAscent()/2);
			}
		}	
		if (numOfBricks!=0) {
			GLabel lose = new GLabel ("Game Lost.");
			add (lose, getWidth()/2 - lose.getWidth()/2, getHeight()/2 + lose.getAscent()/2 + ball.getWidth());
		}
	}	

	/* If the ball collides into the top or bottom of the paddle, its vertical velocity reverses.
	 * If the ball collides into a brick, its vertical velocity reverses and 
	 * the brick is removed. 
	 */
	private void collision() {
		GObject collider = getCollidingObject();
		if (collider == paddle && vy>0) {
			vy = -vy;
		}
		if (collider != null && collider !=paddle) {
			remove (collider);
			numOfBricks = numOfBricks-1;
			vy = -vy;
		}
	}

	/* Checks four corners of the ball to see if there is an object there.
	 * Moves to each corner if there is no object.
	 */
	private GObject getCollidingObject() {
		GObject collider = null;
		GObject upperLeft = getElementAt(ball.getX(), ball.getY());
		GObject upperRight = getElementAt (ball.getX() + 2*BALL_RADIUS, ball.getY());
		GObject lowerLeft = getElementAt (ball.getX(), ball.getY() + 2*BALL_RADIUS);
		GObject lowerRight = getElementAt (ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		if (upperLeft !=null) {
			collider = upperLeft;
		} else if (upperRight != null) {
			collider = upperRight;
		} else if (lowerLeft != null) {
			collider = lowerLeft;
		} else if (lowerRight != null) {
			collider = lowerRight;
		}
		return collider;
	}	

	/* When the user clicks, the ball first moves downward at a random speed.
	 * It then moves at random speeds in different directions after bouncing
	 * off of the left, right, and top walls.
	 */
	private void moveBall() {
		waitForClick();
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean (0.5)) {
			vx = - vx;
		}
		vy = VELOCITY_Y;
		while(true) {
			if (hitLeftWall() || hitRightWall ()) {
				vx = - vx;
			}
			if (hitTopWall ()) {
				vy = - vy;
			}
			ball.move(vx, vy);
			pause(DELAY);
			collision();
			if (ball.getY() >= getHeight()) {
				break;
			}
		}
	}

	/* Condition when ball hits left wall.
	 */
	private boolean hitLeftWall() {
		return (ball.getX() <=0);
	}

	/* Condition when ball hits right wall.
	 */
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/* Condition when ball hits top wall.
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	/* Adds colored bricks and paddle to the screen.
	 */
	private void setUpGame() {
		addBricks();
		paddle = makePaddle();
		addPaddleToCenter();
	} 
	/* Creates ball and adds it to the center of the screen.
	 */
	private void addBall() {
		double size = BALL_RADIUS*2;
		double cx = getWidth()/2 - BALL_RADIUS/2.0;
		double cy = getHeight()/2 - BALL_RADIUS/2.0;
		ball = new GOval (cx, cy, size, size);
		ball.setFilled(true);
		add (ball, cx, cy);
	}

	/* Creates a brick for a set number of columns.
	 */
	private void addBricks () {
		double cx = getWidth()/2 - (NBRICK_COLUMNS*(BRICK_WIDTH+BRICK_SEP)-BRICK_SEP)/2.0;
		double cy = BRICK_Y_OFFSET;
		for (int c = 0; c < NBRICK_COLUMNS; c++){	
			for (int r = 0; r < NBRICK_ROWS; r++) {
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add (brick, cx + (BRICK_SEP*c) + (BRICK_WIDTH*c), cy + (BRICK_SEP*r) + (BRICK_HEIGHT*r));
				if (r>=0) {
					brick.setColor(Color.RED);
				}
				if (r>=2) {
					brick.setColor(Color.ORANGE);
				}
				if (r>=4) {
					brick.setColor(Color.YELLOW);
				}
				if (r>=6) {
					brick.setColor(Color.GREEN);
				}
				if (r>=8) {
					brick.setColor(Color.CYAN);
				}
			}	
		}	
	}

	/* Creates a paddle for the game.
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	/* Adds paddle to center.
	 */
	private void addPaddleToCenter() {
		double cx = getWidth()/2;
		double cy = getHeight() - PADDLE_Y_OFFSET;
		add (paddle, cx, cy);
	}

	/* The paddle moves where the mouse takes it, except when reaching the left and
	 * right walls.
	 */
	public void mouseMoved (MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH;
		if (x > 0 && x < getWidth()) {
			paddle.setLocation (x, getHeight() - PADDLE_Y_OFFSET);	
		}
	}
}