
/*
 * File: Breakout.java 

 * -------------------
 * Name: Phoebe Quinton
 * Section Leader: Niki Agrawal
 * 
 * This program will play the game Breakout
 * by allowing the user to have three trials 
 * to knock down all of the bricks in the game 
 * with a ball and paddle. 
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

	// Generates random numbers.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Horizontal and vertical velocities of the ball.
	private double vx, vy;

	GRect paddle = null;

	GOval ball = null;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();
		playGame();
	}

	// This method builds the bricks at the top of the
	// screen and programs the paddle.

	private void setUpGame() {
		setUpBricks();
		paddle = makePaddle();
		addMouseListeners();
	}

	// This method creates the block of multi-colored bricks
	// at the top of the screen.

	private void setUpBricks() {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			for (int j = 0; j < NBRICK_ROWS; j++) {
				double startX = ((getWidth() - ((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS)) / 2);
				double startY = BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP) * NBRICK_COLUMNS);
				double brickX = startX + ((BRICK_SEP + BRICK_WIDTH) * i);
				double brickY = startY - ((BRICK_SEP + BRICK_HEIGHT) * j);
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick, brickX, brickY);
				if (j < NBRICK_ROWS / 5) {
					brick.setColor(Color.CYAN);
				}
				if ((j < NBRICK_ROWS * 2 / 5) && j >= NBRICK_ROWS / 5) {
					brick.setColor(Color.GREEN);
				}
				if (j < NBRICK_ROWS * 3 / 5 && j >= NBRICK_ROWS * 2 / 5) {
					brick.setColor(Color.YELLOW);
				}
				if (j < NBRICK_ROWS * 3 / 5 && j >= NBRICK_ROWS * 2 / 5) {
					brick.setColor(Color.YELLOW);
				}
				if (j < NBRICK_ROWS * 4 / 5 && j >= NBRICK_ROWS * 3 / 5) {
					brick.setColor(Color.ORANGE);
				}
				if (j < NBRICK_ROWS && j >= NBRICK_ROWS * 4 / 5) {
					brick.setColor(Color.RED);
				}
			}
		}
	}

	// This method adds the paddle to the bottom
	// of the screen.

	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double paddleX = (getWidth() / 2) - (PADDLE_WIDTH / 2);
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, paddleX, paddleY);
		return paddle;
	}

	// This method makes the paddle move
	// horizontally with the mouse.

	public void mouseMoved(MouseEvent e) {
		if (e.getX() < getWidth() - PADDLE_WIDTH / 2 && e.getX() > PADDLE_WIDTH / 2) {
			double x = e.getX() - (PADDLE_WIDTH / 2);
			double y = getHeight() - PADDLE_Y_OFFSET;
			paddle.setLocation(x, y);
		}
	}

	// This method builds and animates the ball
	// to make it bounce off of the paddle and bricks
	// giving the user three times to win the game.

	private void playGame() {
		ball = makeBall();
		waitForClick();
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		int numLoss = 0;
		int numBricks = NBRICK_ROWS * NBRICK_COLUMNS;
		while (true) {
			boolean result = animateBall();
			if (result == false) {
				numLoss = numLoss + 1;
				ball = makeBall();
				if (numLoss == NTURNS) {
					vx = 0;
					vy = 0;
					break;
				}
				waitForClick();
				vy = +6.0;
				vx = rgen.nextDouble(1.0, 3.0);
				if (rgen.nextBoolean(0.5))
					vx = -vx;
			}
			GObject collider = getCollidingObject();
			if (collider != null) {
				if (collider == paddle) {
					vy = -Math.abs(vy);
				} else {
					remove(collider);
					numBricks = numBricks - 1;
					vy = -vy;
					if (numBricks == 0) {
						vx = 0;
						vy = 0;
						break;
					}
				}
			}
		}
	}

	// This method creates the ball.

	private GOval makeBall() {
		double ballX = getWidth() / 2 - BALL_RADIUS;
		double ballY = getHeight() / 2 - BALL_RADIUS;
		GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball, ballX, ballY);
		return (ball);
	}

	// This method makes the ball bounce off of the sides
	// of the screen and lose a life when
	// the ball hits the bottom of the screen.

	private boolean animateBall() {
		if (hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if (hitTopWall(ball)) {
			vy = -vy;
		}
		if (hitBottomWall(ball)) {
			vx = 0;
			vy = 0;
			remove(ball);
			return false;
		}
		ball.move(vx, vy);
		pause(DELAY);
		return true;
	}

	// These methods define when the ball has hit a wall.

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	// This method determines if the ball has
	// collided with an object and returns
	// that object or null.

	private GObject getCollidingObject() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		GObject collider = getElementAt(ballX, ballY);
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ballX + 2 * BALL_RADIUS, ballY);
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ballX, ballY + 2 * BALL_RADIUS);
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ballX + 2 * BALL_RADIUS, ballY + 2 * BALL_RADIUS);
		if (collider != null) {
			return collider;
		}
		return null;
	}
}
