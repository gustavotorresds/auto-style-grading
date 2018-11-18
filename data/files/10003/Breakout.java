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


	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GOval ball =  new GOval(CANVAS_WIDTH/2, CANVAS_HEIGHT/2, BALL_RADIUS*2, BALL_RADIUS*2);
	private GRect paddle = new GRect(0, CANVAS_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
	private int numberOfBricks = NBRICK_ROWS * NBRICK_COLUMNS;
	private double vx;
	private double vy = VELOCITY_Y;
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		paintBricks();
		ball.setFilled(true);
		add(ball);
		paddle.setFilled(true);
		add(paddle);

		// this makes the program act according to mouse actions
		addMouseListeners();

		// this sets the velocity of the ball randomly
		randomXVelocity();

		// game doesn't start until the user clicks
		waitForClick();

		for (int i = 0; i < NTURNS; i++) {
			playGame();
			// this shows the player how many lives they have remaining until the game is over
			if (i != NTURNS - 1) {
			GLabel labelReminder = new GLabel ("You have " + (NTURNS - 1 - i) + " lives remaining. Click to continue.");
			labelReminder.setFont("Courier-10");
			// this places the reminder in the middle of the screen
			add(labelReminder, getWidth()/2 - labelReminder.getWidth()/2, getHeight()/2 - labelReminder.getHeight()/2);
			waitForClick();
			remove(labelReminder);
			vy = -vy;
			
			randomXVelocity();
			
			ball.setLocation(getWidth()/2 - ball.getWidth()/2, getHeight()/2 - ball.getHeight()/2 );
			}
		}
		
		remove(ball);
		
		// tells whether the player has won or lost
		if(numberOfBricks == 0) {
			GLabel labelWin = new GLabel ("You Win!");
			labelWin.setFont("Courier-20");
			add(labelWin, getWidth()/2 - labelWin.getWidth()/2, getHeight()/2 - labelWin.getAscent()/2 );
		} else {
			GLabel labelLost = new GLabel ("Game Over");
			labelLost.setFont("Courier-20");
			add(labelLost, getWidth()/2 - labelLost.getWidth()/2, getHeight()/2 - labelLost.getAscent()/2);
		}
	}
	
	private void randomXVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}

	private void playGame() {
		// ball will constantly bounce off the objects - such as paddle, 
		// wall, and bricks - until it hits the ground, in which case
		// the player has lost.
		while(true) {
			ball.move(vx,vy);
			double x = ball.getX();
			double y = ball.getY();
			if(x < 0) {
				vx = -vx;
			}
			if(x + BALL_RADIUS*2 > getWidth()) {
				vx = -vx;
			}
			if(y < 0) {
				vy = -vy;
			}
			if(y + BALL_RADIUS*2 > getHeight()) {
				vy = -vy;
				break;
				// we lost
			}

			// this eliminates the bricks that collided with the ball
			GObject collider = getCollidingObject(x,y);
			if (collider != null) {
				if (collider == paddle) {
					vy = -vy;
					ball.setY(paddle.getY() - BALL_RADIUS*2);
				} else {
					vy = -vy;
					remove(collider);
					numberOfBricks = numberOfBricks - 1;
					
				}
			}
			pause(DELAY);
		}
	}


	private GObject getCollidingObject(double x, double y) {
		// checks what object collided with upper left corner of the ball
		GObject element = getElementAt(x,y);
		if (element != null) {
			return element;
		}
		// checks what object collided with upper right corner of the ball
		element = getElementAt(x + 2*BALL_RADIUS, y);
		if (element != null ) {
			return element;
		}
		// checks what object collided with lower right corner of the ball
		element = getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS);
		if (element != null ) {
			return element;
		}
		// checks what object collided with lower left corner of the ball
		element = getElementAt(x , y + 2*BALL_RADIUS);
		if (element != null ) {
			return element;
		}
		return null;
	}

	// makes all the bricks
	public void paintBricks() {
		double x = BRICK_SEP;
		double y = BRICK_Y_OFFSET;

		// red bricks for two lines
		for (int i=0; i < 2; i++) {
			double xCopy = getWidth()/2 - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)/2;
			for (int j=0; j < NBRICK_COLUMNS; j++) {
				GRect rect = new GRect (xCopy, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setColor(Color.RED);
				rect.setFilled (true);
				add(rect);

				xCopy += BRICK_SEP + BRICK_WIDTH; 
			}
			y += BRICK_SEP + BRICK_HEIGHT;
		}

		// orange bricks for two lines
		for (int i=0; i < 2; i++) {
			double xCopy = getWidth()/2 - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)/2;
			for (int j=0; j < NBRICK_COLUMNS; j++) {
				GRect rect = new GRect (xCopy, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setColor(Color.ORANGE);
				rect.setFilled (true);
				add(rect);

				xCopy += BRICK_SEP + BRICK_WIDTH; 
			}
			y += BRICK_SEP + BRICK_HEIGHT;
		}

		// yellow bricks for two lines
		for (int i=0; i < 2; i++) {
			double xCopy = getWidth()/2 - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)/2;
			for (int j=0; j < NBRICK_COLUMNS; j++) {
				GRect rect = new GRect (xCopy, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setColor(Color.YELLOW);
				rect.setFilled (true);
				add(rect);

				xCopy += BRICK_SEP + BRICK_WIDTH; 
			}
			y += BRICK_SEP + BRICK_HEIGHT;
		}

		// green bricks for two lines
		for (int i=0; i < 2; i++) {
			double xCopy = getWidth()/2 - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)/2;
			for (int j=0; j < NBRICK_COLUMNS; j++) {
				GRect rect = new GRect (xCopy, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setColor(Color.GREEN);
				rect.setFilled (true);
				add(rect);

				xCopy += BRICK_SEP + BRICK_WIDTH; 
			}
			y += BRICK_SEP + BRICK_HEIGHT;
		}

		// cyan bricks for two lines
		for (int i=0; i < 2; i++) {
			double xCopy = getWidth()/2 - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)/2;
			for (int j=0; j < NBRICK_COLUMNS; j++) {
				GRect rect = new GRect (xCopy, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setColor(Color.CYAN);
				rect.setFilled (true);
				add(rect);

				xCopy += BRICK_SEP + BRICK_WIDTH; 
			}
			y += BRICK_SEP + BRICK_HEIGHT;
		}
	}

	// makes paddle follow the mouse, exactly at its half-point so that the
	// mouse would be placed in the middle of the paddle whenever it moves.
	public void mouseMoved (MouseEvent e) {
		paddle.setX(e.getX() - PADDLE_WIDTH / 2);
	}
}

