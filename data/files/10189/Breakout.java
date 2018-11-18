
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

	public static final double APPLICATION_WIDTH = 420;
	public static final double APPLICATION_HEIGHT = 600;

	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	// * Instance variable for the paddle to be tracked
	private GRect paddle = null;

	// * Instance variable for the ball
	private GOval ball;

	// *Instance variable for the ball's velocity
	private double vx = 3.0;
	private double vy = 5.0;

	// *Instance variable for the ball's random velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Number of bricks
	public static final int bricks = 100;

	public void run() {
		setup();
		for (int i = 0; i < NTURNS; i++) {
			waitForClick();
			// launch ball from center of screen
			launchBall();
			// continue activities below and break if needed
			rungame();
		}
	}

	private void rungame() {
		// while following is true, run the game
		while (true) {
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			if (hitBottomWall(ball)) {
				break;
			}
			ball.move(vx, vy);
			// pause
			pause(DELAY);
			// checks to see if there is a paddle or brick at the ball's coordinates
			GObject collider = getCollidingObject();
			if (collider != null) {
				if (collider != paddle) {
					remove(collider);
				} else {
					Math.abs(vy);
				}
				vy = -vy;
			}
		}
	}

	private GObject getCollidingObject() {
		GObject collider;
		collider = getElementAt(ball.getX(), ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		if (collider != null) {
			return collider;
		}
		return null;
	}

	private void launchBall() {
		// launch ball from center
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		ball.move(vx, vy);
	}

	/**
	 * Method: Hit Bottom Wall ----------------------- Returns whether or not the
	 * given ball should bounce off of the bottom wall of the window.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/**
	 * Method: Hit Top Wall ----------------------- Returns whether or not the given
	 * ball should bounce off of the top wall of the window.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/**
	 * Method: Hit Right Wall ----------------------- Returns whether or not the
	 * given ball should bounce off of the right wall of the window.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/**
	 * Method: Hit Left Wall ----------------------- Returns whether or not the
	 * given ball should bounce off of the left wall of the window.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private void makeBall() {
		// if(ball == null) {
		ball = new GOval(BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, (getWidth() - BALL_RADIUS) / 2, (getHeight() - BALL_RADIUS) / 2);
	}

	private void setup() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// draw colored rows of bricks
		drawBricks();
		paddle = makePaddle();
		addMouseListeners();
		addPaddle();
		makeBall();
	}

	private GRect makePaddle() {
		// makes paddle
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}

	private void addPaddle() {
		// adds paddle to screen
		double x = 0;
		double y = PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}

	public void mouseMoved(MouseEvent e) {
		// tracks paddle to mouse movements
		double mouseX = e.getX();
		if (mouseX + PADDLE_WIDTH >= getWidth()) {
			mouseX = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(mouseX, CANVAS_HEIGHT - PADDLE_Y_OFFSET);

	}

	private void drawBricks() {
		// define coordinates of first brick
		double x = getWidth() / 2 - (BRICK_WIDTH * NBRICK_COLUMNS) / 2 - BRICK_SEP - (BRICK_SEP) / 2;
		double y = BRICK_Y_OFFSET;

		for (int j = 0; j < NBRICK_ROWS; j++) {
			// builds bricks by rows going vertically up from base

			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				// builds bricks by going horizontally
				GRect brick2 = new GRect(x + (i * BRICK_WIDTH) + BRICK_SEP, y + j * BRICK_HEIGHT, BRICK_WIDTH,
						BRICK_HEIGHT);
				// builds bricks
				brick2.setFilled(true);
				brick2.setColor(Color.BLACK);
				if (j <= 1) {
					brick2.setFillColor(Color.RED);
				}
				if (1 < j && j <= 3) {
					brick2.setFillColor(Color.ORANGE);
				}
				if (3 < j && j <= 5) {
					brick2.setFillColor(Color.YELLOW);
				}
				if (5 < j && j <= 7) {
					brick2.setFillColor(Color.GREEN);
				}
				if (7 < j && j <= 10) {
					brick2.setFillColor(Color.CYAN);
				}
				add(brick2);
			}
		}

	}
}
