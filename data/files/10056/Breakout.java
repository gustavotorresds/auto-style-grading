
/*
 * File: Breakout.java
 * -------------------
 * Name: Anika Gupta
 * Section Leader:Jonathan Kula
 * 
 * This file eventually implements the game of Breakout.
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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// Instance variables
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private int counter = NBRICK_COLUMNS * NBRICK_ROWS;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		setup();
		addMouseListeners();
		while (!gameOver()) {
			moveBall();
			pause(DELAY);
		}
	}

	/** setup game board with paddle, colored bricks, and ball*/
	private void setup() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addBricks();
		addPaddle();
		addBall();
		startBall();
	}

	private void addPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, (getWidth() - PADDLE_WIDTH) / 2, getHeight() - PADDLE_Y_OFFSET);
	}

	private void addBricks() {
		for (int r = 1; r <= NBRICK_ROWS; r++) {
			for (int c = 1; c < NBRICK_COLUMNS + 1; c++) {
				/*Horizontal position: First brick is at center offset by 1/2 number of bricks in
				total row. Subsequent bricks add c*brick width to the first brick position*/
				double brickX = (getWidth() - BRICK_WIDTH) / 2 - (NBRICK_COLUMNS - 1) * (BRICK_WIDTH + BRICK_SEP) / 2
						+ (BRICK_WIDTH + BRICK_SEP) * (c - 1);
				double brickY = BRICK_Y_OFFSET + r * (BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				setBrickColor(r, brick);
				add(brick, brickX, brickY);
			}
		}
	}

	private void addBall() {
		ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
	}

	private void setBrickColor(int r, GRect brick) {
		if (r <= 2) {
			brick.setColor(Color.RED);
		} else if (r <= 4) {
			brick.setColor(Color.ORANGE);
		} else if (r <= 6) {
			brick.setColor(Color.YELLOW);
		} else if (r <= 8) {
			brick.setColor(Color.GREEN);
		} else {
			brick.setColor(Color.CYAN);
		}
	}

	/**Moves paddle horizontally depending on mouse position*/
	public void mouseMoved(MouseEvent e) {
		int MouseX = e.getX();
		if (MouseX > getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		} else {
			paddle.setLocation(MouseX, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	/**Initiates ball direction and movement*/
	private void startBall() {
		vy = 3.0;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
	}

	private void moveBall() {
		updateDirection();
		ball.move(vx, vy);
	}

	/**Updates ball direction based on collisions*/
	private void updateDirection() {
		if (collideWithTopWall()) {
			vy = -vy;
		} else if (collideWithSideWall()) {
			vx = -vx;
		} else if (collideWithPaddle()) {
			vy = -vy;
		} else if (collideWithBrick()) {
			vy = -vy;
		}
	}

	private boolean collideWithTopWall() {
		return (ball.getY() <= 0);
	}

	private boolean collideWithSideWall() {
		return (ball.getX() <= 0 || ball.getX() >= getWidth() - BALL_RADIUS * 2);
	}

	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			return getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		} else {
			return null;
		}
	}

	private boolean collideWithBrick() {
		GObject collider = getCollidingObject();
		if (collider != paddle && collider != null) {
			remove(collider);
			counter--;
			return true;
		} else {
			return false;
		}
	}

	private boolean collideWithPaddle() {
		GObject collider = getCollidingObject();
		return (collider == paddle);
	}

	/**Terminates game if ball hits the bottom or all bricks are cleared and
	 * returns a message accordingly*/
	private boolean gameOver() {
		if (ball.getY() > getHeight() - BALL_RADIUS * 2) {
			GLabel label = new GLabel("You lost!");
			add(label, (getWidth()-label.getWidth())/2, (getHeight()-label.getAscent())/2);
			return true;
		} else if (counter == 0) {
			GLabel label = new GLabel("You won!");
			add(label, (getWidth()-label.getWidth())/2, (getHeight()-label.getAscent())/2);
			return true;
		} else {
			return false;
		}
	}
}