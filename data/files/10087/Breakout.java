
/*
 * File: Breakout.java
 * -------------------
 * Name: Cody Evans
 * Section Leader: Jonathan Kula
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
	public static final double VELOCITY_Y = 8;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// Instance variable for paddle
	GRect paddle = null;

	// Instance variable for ball
	GOval ball = null;

	// Instance variables for the velocity of the ball in x and y
	private double vx, vy;
	
	// Instance variable for bricks remaining
	private int nBricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;

	// Instance variable rgen: random number generator for starting velocity of the
	// ball
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// RUN method for the program
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout - Cody Evans");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Sets up the starting layout
		setUpWorld();

		// Initiates game play with NTURNS
		for (int i = 1; i <= NTURNS; i++) {

			// Print ending dialogue if game is lost or won.
			if (i >= NTURNS && nBricksRemaining >= 1) {
				GLabel losingGameLine = new GLabel("You have lost the game! Try again, n00b.", 0, getHeight() / 2);
				add(losingGameLine);
			} else if (nBricksRemaining == 0) {
				GLabel winningGameLine = new GLabel("You won! Go crush a pizza and come back soon.", 0,
						getHeight() / 3);
				add(winningGameLine);
			} else {
				// Make and add ball to the screen
				ball = makeBall();
				addBall();

				// Starts game with mouse click
				waitForClick();

				// Sets starting velocity for ball
				initializeBallVelocity();

				// Begins game play by launching ball. Runs until bottom wall is hit
				while (!hitsBottomWall(ball) || nBricksRemaining != 0) {

					// Reverses velocity when ball hits a wall
					if (hitsTopWall(ball)) {
						vy = -vy;
					} else if (hitsLeftWall(ball) || hitsRightWall(ball)) {
						vx = -vx;
					}

					// Moves ball with given velocity
					ball.move(vx, vy);

					// Finds object that ball has collided with, if any exists. And removes object
					// when collision occurs.
					GObject collider = getCollidingObject();

					if (collider == paddle) {
						vy = -vy;
					} else if (collider != paddle && collider != null) {
						vy = -vy;
						remove(collider);
						nBricksRemaining = nBricksRemaining - 1;
					}
					collider = null;
					
					// Pause
					pause(DELAY);
				}

				// Remove ball from screen if it's gone off the bottom
				remove(ball);
			}
		}
	}

	// Methods that set up the starting brick, ball, and paddle layout for the game
	private void setUpWorld() {
		setUpBricks();
		paddle = makePaddle();
		addPaddleToCenter();
		addMouseListeners();
	}

	// METHODS FOR setUpWorld PORTION OF PROGRAM
	private void setUpBricks() {
		// finds starting x and y coordinates
		double startingXCoord = getWidth() / 2 - (NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) / 2);
		double startingYCoord = BRICK_Y_OFFSET;

		// loops for the number of rows as defined in NBRICK_ROWS
		for (int brickRow = 1; brickRow <= NBRICK_ROWS; brickRow++) {

			// loops for the number of bricks in each row as defined in NBRICK_Columns
			for (int currentBrick = 1; currentBrick <= NBRICK_COLUMNS; currentBrick++) {
				double currentXCoord = startingXCoord + (currentBrick - 1) * (BRICK_WIDTH + BRICK_SEP);
				double currentYCoord = startingYCoord + (brickRow - 1) * (BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect(currentXCoord, currentYCoord, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);

				/*
				 * change the colors to be per the layout instructions. NOTE: very manual //
				 * needs to be updated if no. of rows changes.
				 */

				if (brickRow == 1 || brickRow == 2) {
					brick.setColor(Color.RED);
				} else if (brickRow == 3 || brickRow == 4) {
					brick.setColor(Color.ORANGE);
				} else if (brickRow == 5 || brickRow == 6) {
					brick.setColor(Color.YELLOW);
				} else if (brickRow == 7 || brickRow == 8) {
					brick.setColor(Color.GREEN);
				} else if (brickRow == 9 || brickRow == 10) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}

	// Makes filled black paddle of defined width and height
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	// Centers paddle in the middle of the board
	private void addPaddleToCenter() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}

	// Add a mouse tracker to the paddle so the paddle tracks the X axis of the
	// mouse. Note, the paddle is fixed along the Y plane.
	public void mouseMoved(MouseEvent movePaddle) {
		double x = movePaddle.getX() - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;

		// ensures paddles doesn't move past left border of screen
		if (x <= 0) {
			x = 0;
		}

		// ensures paddle doesn't move past right border of screen
		if ((x + PADDLE_WIDTH) >= getWidth()) {
			x = (getWidth() - PADDLE_WIDTH);
		}

		paddle.setLocation(x, y);
	}

	// Makes the ball of BALL_RADIUS centered at the middle of the screen
	private GOval makeBall() {
		GOval ball = new GOval((getWidth() - BALL_RADIUS) / 2, (getHeight() - BALL_RADIUS) / 2, BALL_RADIUS,
				BALL_RADIUS);
		ball.setFilled(true);
		return ball;
	}

	// Adds the ball to the screen
	private void addBall() {
		add(ball);
	}

	private void initializeBallVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		vy = VELOCITY_Y;
	}

	// METHODS FOR GAME PLAY PORTION OF PROGRAM

	// Reverses X & Y velocities if ball hits walls
	private boolean hitsTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitsBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	private boolean hitsLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private boolean hitsRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	// Checks for Element (i.e. brick) at each of the four corner points on the
	// square in which the ball is inscribed
	private GObject getCollidingObject() {
		GObject collider = null;
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
