
/*
 * File: Breakout.java
 * -------------------
 * Name: Josh Kornberg
 * Section Leader: Luciano Gonzalez
 * 
 * This file implements the game of Breakout.
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
	public static final int NBRICK_COLUMNS = 1;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 1;

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
	public static final double VELOCITY_Y_INIT = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of times the ball can hit the floor before the game ends
	public static final int NTURNS = 3;

	// private instance variables
	GRect paddle = null;
	GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance(); // for ball movement
	private double vx; // ball x velocity
	private double vy; // ball y velocity
	GObject collider = null; // receives value of object that ball has collided into. used for determining ball movement
	private int turnsUsed = 0; // for tallying the number of times the ball has hit the floor

	/*
	 * Method: Run Sets up the game and plays it. Setting up game entails laying the
	 * bricks, making the paddle, and adding the ball to the center of the game.
	 * Playing the game entails dropping the ball when mouse is clicked and bouncing
	 * the ball either of the following occurs first: the ball has hit the floor 3
	 * times, or no more bricks remain.
	 */

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setup();
		addMouseListeners();
		waitForClick();
		play();
	}

	/*
	 * Method: mouseMoved Takes the location of the mouse in the x-plane and moves
	 * the paddle so that it's centered around the mouse. Ensures that the paddle
	 * never leaves the screen. The paddle is fixed in the y-plane.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		if (x <= 0) {
			x = 0;
		} else if (x >= getWidth() - PADDLE_WIDTH) {
			x = (getWidth() - PADDLE_WIDTH);
		}

		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle.setLocation(x, y);

	}

	/*
	 * Method: setup Sets up the game so that it can be played. Adds bricks, makes
	 * the paddle, adds the paddle to the screen, adds the ball, and sets the
	 * initial direction of the ball in the x-plane.
	 */

	private void setup() {
		layBricks();
		paddle = makePaddle();
		addPaddleToCenter();
		ball = makeBall();
		addBallToCenter();
		setBallMotion();
	}

	/*
	 * Method: play Runs the game. Game continues until all turns have been played
	 * or no bricks remain, whatever happens first. Note: there is a bug here. The
	 * game does not immediately end when the final brick is hit.
	 */
	private void play() {
		int bricksRemoved = 0;
		int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS - bricksRemoved;
		// why isn't this ending the game when no bricks remain?
		while (turnsUsed < NTURNS && bricksRemaining > 0) {
			if (hitBottomWall(ball) != true) {
				getCollidingObject();
				if (collider == paddle) {
					vy = -vy;
				} else if (collider != paddle && collider != null) {
					remove(collider);
					bricksRemoved++;
					if (bricksRemaining == 0) {
						remove(ball);
						break;
					}
					vy = -vy;
				} else if (hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				} else if (hitTopWall(ball)) {
					vy = -vy;
				}
				ball.move(vx, vy);
				pause(DELAY);
				collider = null;
			} else {
				remove(ball);
				turnsUsed++;
				if (turnsUsed < NTURNS) {
					addBallToCenter();
					setBallMotion();
					waitForClick();
				}
			}
		}
		GLabel gameOver = new GLabel("The game is over. Thanks for playing!", getWidth(), getHeight());
		add(gameOver, getWidth() / 4, getHeight() / 2);
	}

	/*
	 * Method: layBricks Creates bricks and lays them in formation. Bricks are of
	 * pre-specified width and height. Bricks are placed in a pre-specified number
	 * of rows and columns. They are also spaced a pre-specifed number of pixels
	 * apart. They are also colored.
	 */
	
	private void layBricks() {
		for (int r = 0; r < NBRICK_ROWS; r++) {
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = (getWidth() - NBRICK_ROWS * BRICK_WIDTH - (NBRICK_ROWS - 1) * BRICK_SEP) / 2
						+ r * (BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + c * (BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick, x, y);
				if (c < 2) {
					brick.setColor(Color.RED);
				} else if (c >= 2 && c < 4) {
					brick.setColor(Color.ORANGE);
				} else if (c >= 4 && c < 6) {
					brick.setColor(Color.YELLOW);
				} else if (c >= 6 && c < 8) {
					brick.setColor(Color.GREEN);
				} else if (c >= 8 && c < 10) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}

	/*
	 * Method: makePaddle Creates a paddle of pre-specified width and height.
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	/*
	 * Method: addPaddleToCenter Adds paddle to the center of the game in the
	 * x-plane.
	 */
	private void addPaddleToCenter() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}

	/*
	 * Method: makeBall Creates a ball with pre-specified radius.
	 */
	private GOval makeBall() {
		GOval ball = new GOval(BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		return ball;
	}

	/*
	 * Method: addBallToCenter Adds ball to the center of the screen in the x-plane
	 * and y-plane.
	 */
	private void addBallToCenter() {
		add(ball, (getWidth() - BALL_RADIUS) / 2, (getHeight() - BALL_RADIUS) / 2);
	}

	/*
	 * Method: setBallMotion Sets the ball's velocity in the x-direction to a double
	 * randomly selected from a pre-specified range. There is an equal probability
	 * that the ball will travel left or right upon first moving.
	 */
	private void setBallMotion() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		vy = 3;
	}

	/*
	 * Method: Hit Bottom Wall Returns whether or not the given ball has hit the
	 * floor.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/*
	 * Method: hitTopWall Returns whether or not the ball is at the top of the
	 * window.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * Method: hitRightWall Returns whether or not the ball is at the right wall.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*
	 * Method: hitLeftWall Returns whether or not the ball is at the left wall.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	/*
	 * Method: getCollidingObject
	 * Imagines that the ball is inscribed in a square. Detects
	 * if any of the four corners of the square have collided
	 * another object: the floor, a wall, the paddle, 
	 * or a brick. Assigns the value of the collided object
	 * to the instance variable collider. 
	 */

	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			collider = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return collider;
	}
}
