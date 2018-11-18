/*
 * File: Breakout.java
 * ------
 * Name: Allan Zhao
 * Section Leader: Marilyn Zhang
 * -------
 * This program makes the Breakout game, which
 * obliterates bricks when the ball touches them.
 * ------
 * Resources Used: Textbook, Lecture videos, YEAH slides
 * ------
 * Pre: NA
 * Post: Working Breakout game
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

	//Adds the paddle as an instance variable
	public static GRect paddle = null;

	//Adds the ball as an instance variable
	public static GOval ball = null; 

	//Number of bricks left on the screen.
	public static int NBRICKS = 100;

	public void run() {
		setTitle("CS 106A Breakout");

		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		makeBricks();

		addMouseListeners();
		createPaddle();

		for (int i = 0; i < 3; i++) {
			createBall();
			animateBall();
		}

		if (NBRICKS != 0) {
			GLabel message = new GLabel ("You lose", 0 , 0);
			add(message);
		} else {
			GLabel message = new GLabel ("You win", 0 , 0);
			add(message);
		}
	}

	/*
	 * Method: makeBricks()
	 * -------
	 * This method makes all the bricks using a for loop
	 * and by separating the rows by colors.
	 */
	private void makeBricks() {
		//Using a double in the for loop in case the amount for NBRICK_ROWS changes and
		//the decimal calculation is no longer an integer.
		for (double i = 0; i < NBRICK_ROWS; i++) {
			for (double j = 0; j < NBRICK_COLUMNS; j++) {
				GRect bricks = new GRect((getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) + ((NBRICK_COLUMNS - 1) * BRICK_SEP)))/2 + ((BRICK_WIDTH + BRICK_SEP) * j), BRICK_Y_OFFSET + (i * (BRICK_SEP +BRICK_HEIGHT)), BRICK_WIDTH, BRICK_HEIGHT);
				bricks.setFilled(true);
				//Red rows
				if (i < (0.2 * NBRICK_ROWS)) {
					bricks.setColor(Color.RED);
					add(bricks);
				}
				//Orange rows
				if (i < (0.4 * NBRICK_ROWS) && i > (0.1 * NBRICK_ROWS)) {
					bricks.setColor(Color.ORANGE);
					add(bricks);
				}
				//Yellow rows
				if (i < (0.6 * NBRICK_ROWS) && i > (0.3 * NBRICK_ROWS)) {
					bricks.setColor(Color.YELLOW);
					add(bricks);
				}
				//Green rows
				if (i < (0.8 * NBRICK_ROWS) && i > (0.5 * NBRICK_ROWS)) {
					bricks.setColor(Color.GREEN);
					add(bricks);
				}
				//Cyan rows
				if (i < NBRICK_ROWS && i > (0.7 * NBRICK_ROWS)) {
					bricks.setColor(Color.CYAN);
					add(bricks);
				}
			}
		}
	}

	/*
	 * method: createPaddle()
	 * -------
	 * This method adds the paddle the screen before it is moved by
	 * the mouse listeners.
	 */
	private void createPaddle() {
		double initialX = getWidth()/2;
		paddle = new GRect (initialX, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * Mouse Event: mouseMoved()
	 * -------
	 * This mouse event creates a paddle and makes it move across
	 * the bottom of the screen following where the mouse pointer is.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = (e.getX() - PADDLE_WIDTH/2);
		paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
	}

	/*
	 * method: createBall()
	 * ------
	 * This method creates the ball to be moved by a mouseListener
	 * in the future.
	 */
	private void createBall() {
		ball = new GOval ((getWidth() - BALL_RADIUS)/2, (getHeight() - BALL_RADIUS)/2, BALL_RADIUS * 2, BALL_RADIUS *2);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * method: private RandomGenerator
	 * ------
	 * This method which is not called generates a random number
	 * for the ball to move in an x direction.
	 */
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*
	 * method: animateBall()
	 * -------
	 * This method animates the ball.
	 */
	private void animateBall() {
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		double vy = VELOCITY_Y;
		waitForClick();
		while (true) {
			ball.move(vx, vy);


			//These lines define what the ball does in the extremities
			//of the screen.
			if (ball.getX() <= 0 || ball.getX() >= getWidth() - (BALL_RADIUS * 2)) {
				vx = -vx;
			}
			if (ball.getY() <= 0) {
				vy = -vy;
			}
			if (ball.getY() >= getHeight()) {
				break;
			}


			//These lines define what happens to the ball when it hits
			//the paddle
			if (getElementAt(ball.getX(), ball.getY() + (BALL_RADIUS * 2)) == paddle) {
				vy = -vy;
			}


			//These lines define what happens to the bricks: sorry for
			//bad style, had no time!!!! :(
			GObject collider = getElementAt (ball.getX(), ball.getY());
			GObject collider2 = getElementAt (ball.getX() + (BALL_RADIUS * 2), ball.getY());
			GObject collider3 = getElementAt (ball.getX() + (BALL_RADIUS * 2), ball.getY() + (BALL_RADIUS * 2));
			GObject collider4 = getElementAt (ball.getX(), ball.getY() + (BALL_RADIUS *2));
			if (collider != paddle && collider != null) {
				remove(collider);
				NBRICKS = NBRICKS - 1;
				vy = -vy;
			} else if (collider2 != paddle && collider2 != null) {
				remove(collider2);
				NBRICKS = NBRICKS - 1;
				vy = -vy;
			} else if (collider3 != paddle && collider3 != null) {
				remove(collider3);
				NBRICKS = NBRICKS - 1;
				vy = -vy; 
			} else if (collider4 != paddle && collider4 != null) {
				remove(collider4);
				NBRICKS = NBRICKS - 1;
				vy = -vy;
			}
			pause(DELAY);
		}
	}
}
