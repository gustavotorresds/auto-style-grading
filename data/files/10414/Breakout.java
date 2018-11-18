
/*
 * File: Breakout.java
 * -------------------
 * Name: Vedang Vadalkar
 * Section Leader: Andrew Marshall
 * 
 * This is the sourcecode of the game of Breakout. It gives the player 3 tries before the game is lost.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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
	// initial random velocity.
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 100.0;

	// Number of turns
	public static final int NTURNS = 3;

	// Creates an instance variable for the paddle and the ball so that they are
	// accessible to all the methods in this class.
	private GRect paddle;
	private GOval ball;
	// Variables to assign the velocity of the ball in x and y direction
	// respectively.
	private double vx;
	private double vy;
	// A random number generator variable that will be used to randomly assign vx of
	// the ball.
	private RandomGenerator rgen = RandomGenerator.getInstance();
	// Counter variable used to keep track of the number of times the ball was lost
	// from the bottom of the screen.
	int i = 0;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size to getWidth and getHeight.
		setCanvasSize(getWidth(), getHeight());
		// Calls the method that sets up the game to start with.
		setUpTheGame();
		// Calls the method after setup that plays the game.
		playGame();

	}

	private void playGame() {

		waitForClick();
		// Counter variable that keeps track of the number of bricks on the screen.
		double end = NBRICK_COLUMNS * NBRICK_ROWS;
		// Randomly selects a velocity x for the ball.
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		vy = VELOCITY_Y;
		// While the number of turns are not exhausted by the player, the while loop
		// executes and lets the game go on.
		while (i < NTURNS) {

			if (hitLeftWall() || hitRightWall()) {
				vx = -vx;
			}
			if (hitTopWall()) {
				vy = -vy;
			}
			if (hitBottomWall()) {
				// After the ball is lost from the bottom of the screen, this code removes the
				// ball from the screen and again sets up a ball at the center of the screen
				// while the player has not exhausted their playing turns.
				i++;
				remove(ball);
				ball = setUpBall();
				add(ball);
				if (i < NTURNS) {
					playGame();
				} else {
					// When the player has exhausted all their "lives" this code removes all the
					// remaining GObjects from the screen and displays a text that lets the player
					// know that the game is over!
					removeAll();
					GLabel gover = new GLabel("GAME OVER!!");
					gover.setFont("Hellvetica-42");
					double x = (getWidth() - gover.getWidth()) / 2.0;
					double y = (getHeight() - gover.getHeight()) / 2.0;
					add(gover, x, y);
					break;
				}

			}

			ball.move(vx, vy);
			pause(DELAY);
			GObject collider = getCollidingObject();
			if (collider == paddle) {

				vy = -Math.abs(vy);
				// The math.abs is my best attempt at avoiding the sticky paddle.
			}
			if (collider != null && collider != paddle) {
				remove(collider);
				end--;

				vy = -vy;
				if (end == 0) {
					// Tests the counter variable has reached 0 and then displays a text on screen
					// that lets player know that the game has been won.
					GLabel win = new GLabel("WINNER!!!");
					win.setLocation((getWidth() - win.getWidth()) / 2.0, (getHeight() - win.getHeight()) / 2.0);
					win.setFont("Hellvetica-42");
					add(win);
					break;
				}

			}

		}

	}

	private GObject getCollidingObject() {
		// A method that returns the GObject that the ball has collided with.

		double dia = BALL_RADIUS;

		double x1 = ball.getX();
		double x2 = ball.getX() + dia;
		double y1 = ball.getY();
		double y2 = ball.getY() + dia;

		if (getElementAt(x1, y1) != null) {
			return getElementAt(x1, y1);
		} else if (getElementAt(x2, y1) != null) {
			return getElementAt(x2, y1);
		} else if (getElementAt(x1, y2) != null) {
			return getElementAt(x1, y2);
		} else if (getElementAt(x2, y2) != null) {
			return getElementAt(x2, y2);
		} else {
			return null;
		}
	}

	// 4 methods that take care of the ball colliding with the 4 sides of the box.
	private boolean hitBottomWall() {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/**
	 * Method: Hit Top Wall. Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	/**
	 * Method: Hit Right Wall. Returns whether or not the given ball should bounce
	 * off of the right wall of the window.
	 */
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/**
	 * Method: Hit Left Wall. Returns whether or not the given ball should bounce
	 * off of the left wall of the window.
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	private void setUpTheGame() {

		setUpBricks();
		paddle = setUpPaddle();
		add(paddle);
		ball = setUpBall();
		add(ball);
		addMouseListeners();

	}

	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double bound = getWidth() - PADDLE_WIDTH;
		if (x < 0)
			x = 0;
		if (x > bound)
			x = bound;
		paddle.setLocation(x, y);
	}

	private GOval setUpBall() {

		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		GOval ball = new GOval(x, y, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		return (ball);

	}

	private GRect setUpPaddle() {

		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		GRect paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return (paddle);

	}

	private void setUpBricks() {
		double x = (getWidth() - (NBRICK_COLUMNS + 1) * BRICK_SEP) / NBRICK_COLUMNS;
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect rect = new GRect(j * (x + BRICK_SEP) + BRICK_SEP, i * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET,
						x, BRICK_HEIGHT);
				rect.setFilled(true);

				// double row = NBRICK_ROWS/5;

				if (i == 0 || i == 1)
					rect.setColor(Color.RED);
				if (i == 2 || i == 3)
					rect.setColor(Color.ORANGE);
				if (i == 4 || i == 5)
					rect.setColor(Color.YELLOW);
				if (i == 6 || i == 7)
					rect.setColor(Color.GREEN);
				if (i == 8 || i == 9)
					rect.setColor(Color.CYAN);

				add(rect);

			}
		}
	}

}
