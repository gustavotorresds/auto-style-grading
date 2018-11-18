/*
\ * File: Breakout.java
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
	public static final double PADDLE_WIDTH =  60;
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
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	GRect paddle = null;
	//in order to keep record of the winning and losing of the player
	int BricksLeft = NBRICK_ROWS * NBRICK_COLUMNS;
	GOval ball = null;
	GRect collider = null;
	double vx;
	double vy;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		int times = NTURNS;
		paddle = makePaddle();
		addMouseListeners();
		addPaddle();
		tileWorldSetUp();
		for (int i = 0; i < times; i++) {
			ball = makeBall();
			add(ball);
			waitForClick();
			vy = VELOCITY_Y;
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5))
				vx = -vx;
			while (BricksLeft != 0 && !hitsBottomWall()) {
				ball.move(vx, vy);
				pause(DELAY);
				hitWall();
				getCollidingObject();
				//in the case of the ball colliding with the paddle
				if (collider == paddle) {
					//resolving the sticky paddle problem
					vy = -Math.abs(vy);
				} else if (collider != null) {
					remove(collider);
					vy = -vy;
					BricksLeft -= 1;
				}
			}
			if (BricksLeft == 0) {
				times = 0;
			}
			remove(ball);
		}
		remove(ball);
		if (BricksLeft > 0) {
			displayMessage("You Lost!");
		} else {
			displayMessage("You Won!");
		}
	}

	//displays a label after the game has finished
	public void displayMessage(String x) {
		GLabel label = new GLabel("");
		label.setFont("Courier-50");
		label.setColor(Color.BLUE);
		label.setLabel(x);
		add(label, getWidth() / 2 - label.getWidth() / 2, getHeight() / 2);
	}
	
	//this method dictates how the direction of the ball should change in colliding
	//with different walls of the screen
	private void hitWall() {
		if (hitsRightWall() || hitsLeftWall()) {
			vx = -vx;
		} else if (hitsTopWall()) {
			vy = -vy;
		}
	}
	
	//this method checks for possible objects that collided with the ball
	//at the bottom right corner of the ball
	private GRect bottomRightCorner() {
		GRect object = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		return object;
	}
	
	//this method checks for possible objects that collided with the ball
	//at the bottom left corner of the ball
	private GRect bottomLeftCorner() {
		GRect object = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		return object;
	}
	
	//this method checks for possible objects that collided with the ball
	//at the top right corner of the ball
	private GRect topRightCorner() {
		GRect object = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		return object;
	}
	
	//this method checks for possible objects that collided with the ball
	//at the top left corner of the ball
	private GRect topLeftCorner() {
		GRect object = getElementAt(ball.getX(), ball.getY());
		return object;
	}
	
	//this method correctly identifies colliding object by 
	//identifying which corner of the ball collided with
	private void getCollidingObject() {
		if (bottomRightCorner() != null) {
			collider = bottomRightCorner();
		} else if (bottomLeftCorner() != null) {
			collider = bottomLeftCorner();
		} else if (topLeftCorner() != null) {
			collider = topLeftCorner();
		} else if (topRightCorner() != null) {
			collider = topRightCorner();
		} else {
			collider = null;
		}
	}
	
	//when the ball hits the right wall of the screen the method returns 'true'
	private boolean hitsRightWall() {
		return ball.getX() >= getWidth() - BALL_RADIUS * 2;
	}
	
	//when the ball hits the left wall of the screen the method returns 'true'
	private boolean hitsLeftWall() {
		return ball.getX() <= 0;
	}
	
	//when the ball hits the top wall the method returns 'true'
	private boolean hitsTopWall() {
		return ball.getY() <= 0;
	}
	
	//when the ball hits the bottom wall the method returns 'true'
	private boolean hitsBottomWall() {
		return ball.getY() >= getHeight()- BALL_RADIUS * 2;
	}
	
	//creates the ball 
	private GOval makeBall() {
		ball = new GOval(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLUE);
		return (ball);
	}
	
	//this method is responsible for displaying complete set of bricks
	private void tileWorldSetUp() {
		double initialX = (CANVAS_WIDTH - (BRICK_WIDTH * NBRICK_COLUMNS) - ((NBRICK_COLUMNS - 1) * BRICK_SEP)) / 2;
		double initialY = BRICK_Y_OFFSET;
		for (int i = 1; i <= NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect rect = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				if (i <= 2) {
					rect.setColor(Color.RED);
				} else if (i <= 4) {
					rect.setColor(Color.ORANGE);
				} else if (i <= 6) {
					rect.setColor(Color.YELLOW);
				} else if (i <= 8) {
					rect.setColor(Color.GREEN);
				} else if (i <= 10) {
					rect.setColor(Color.CYAN);
				}
				rect.setLocation(initialX, initialY);
				rect.setFilled(true);
				add(rect);
				initialX += BRICK_WIDTH + BRICK_SEP;
			}
			initialY += BRICK_HEIGHT + BRICK_SEP;
			initialX = (CANVAS_WIDTH - (BRICK_WIDTH * NBRICK_COLUMNS) - ((NBRICK_COLUMNS - 1) * BRICK_SEP)) / 2;
		}
	}
	
	//mouse event where the cursor is moved is activated
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		double y = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		if (x < getWidth() - PADDLE_WIDTH && x > 0) {
			paddle.setLocation(x, y);
		} else {
			stop();
		}
	}
	
	//places the paddle at the lower centre of the screen
	private void addPaddle() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		add(paddle, x, y);
	}
	
	//makes a rectangular paddle which its dimension is provided by constant
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
}
