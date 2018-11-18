/*
 * File: Breakout.java
 * -------------------
 * Name: Joseph Matan
 * Section Leader: Shanon
 * 
 * This file plays breakout
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

	private double paddleX = new Double (40.0);
	private int brickcount = new Integer (NBRICK_ROWS * NBRICK_COLUMNS);
	GRect paddle;
	GOval ball;
	double vy = 5.0;
	double vx = 1.0;
	boolean gamecondition = true;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* 
		 * game adds listeners to track mouse, 
		 * sets up bricks and paddle and ball,
		 * and starts the balls motion
		 * 
		 */
		addMouseListeners();
		setUpGame();
		startAndMoveBall();
	}

	/**
	 * returns the object that the ball touches
	 */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			return getElementAt(ball.getX(),ball.getY());
		} else if (getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY()) != null) {
			return getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS)) != null) {
			return getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
		} else if (getElementAt(ball.getX() + (2* BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS)) != null) {
			return getElementAt(ball.getX() + (2* BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		} else if (getElementAt(ball.getX(), ball.getY() + (BALL_RADIUS)) != null) {
			return getElementAt(ball.getX(), ball.getY() + (BALL_RADIUS));
		} else {
			return null;
		}
	}
/**
 * moves the ball
 */
	private void startAndMoveBall() {
		double vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (gamecondition = true) {
			ball.move(vx, vy);
			if ((ball.getY() < 0)) {
				vy = -(Math.abs(vy));
				vx= -(Math.abs(vx));
			}
			if ((ball.getY() > getHeight() - ball.getHeight())) { // condition for losing game
				ball.setFillColor(Color.RED);
				GLabel loseText = new GLabel("YOU LOSE");
				loseText.setFont("Courier-34");
				loseText.setColor(Color.RED);
				add(loseText, (getWidth()/2) - (loseText.getWidth() / 2), (getHeight()/2) - (loseText.getHeight() / 2));
				remove(ball);
				break;
			}
			if ((ball.getX() > getWidth() - ball.getWidth()) || (ball.getX() < 0)) { // if ball touches x edges
				vx = -vx;
			}
			getCollidingObject(); // if ball touches object
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy = -(Math.sqrt(vy * vy));
			} else if (collider != null) {
				vy = -vy;
				remove(collider);
				brickcount--;
				if (brickcount == 0 ) { //winning condition
					remove(ball);
					winGameText();
					break;
				}
			}
			pause(DELAY);
		}
	}
/**
 * generates four labels to make a cute win game text
 */
	private void winGameText() { 
		GLabel winText1 = new GLabel("YOU WIN");
		winText1.setFont("Courier-24");
		winText1.setColor(Color.CYAN);
		add(winText1, (getWidth()/2) - (winText1.getWidth() / 2), (getHeight()/2) - (winText1.getHeight() / 2));

		GLabel winText11 = new GLabel("YOU WIN");
		winText11.setFont("Courier-24");
		winText11.setColor(Color.RED);
		add(winText11, (getWidth()/2) - (winText11.getWidth() / 2) + 10, (getHeight()/2) - (winText11.getHeight() / 2) + 10);

		GLabel winText111 = new GLabel("YOU WIN");
		winText111.setFont("Courier-24");
		winText111.setColor(Color.ORANGE);
		add(winText111, (getWidth()/2) - (winText111.getWidth() / 2) + 20, (getHeight()/2) - (winText111.getHeight() / 2) - 15);

		GLabel winText1111 = new GLabel("YOU WIN");
		winText1111.setFont("Courier-24");
		winText1111.setColor(Color.GREEN);
		add(winText1111, (getWidth()/2) - (winText1111.getWidth() / 2) - 15, (getHeight()/2) - (winText1111.getHeight() / 2) - 5);
	}
/**
 * the four parts of the game set up
 */
	private void setUpGame() {
		buildBlocks();
		buildPaddle();
		buildBall();
	}
/**
 * builds the ball
 */
	private void buildBall() {
		ball = new GOval (2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add (ball, (getWidth() / 2) - (BALL_RADIUS / 2), ((getHeight() / 2) - (BALL_RADIUS / 2)));
	}
/**
 * makes the rectangular paddle
 */
	private void buildPaddle() {
		paddle = makePaddle();
		addPaddle();
	}
/**
 * adds paddle to screen with arbitrary initial position
 */
	private void addPaddle() {
		double x = paddleX;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add (paddle, x, y);
	}
/**
 * actually makes the rectangular paddle object
 *
 */
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		return paddle;
	}
/**
 * builds and colors the rows of bricks
 */
	private void buildBlocks() {
		for (int j = 0; j < NBRICK_ROWS; j++) {
			for (int i = NBRICK_COLUMNS; i > 0; i--) { //how many bricks will print for a given layer
				double x = (i * (BRICK_WIDTH + BRICK_SEP) - BRICK_WIDTH + (BRICK_SEP / 2));
				double y = ((BRICK_HEIGHT + BRICK_SEP) * j) + BRICK_SEP + BRICK_Y_OFFSET;
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setFillColor(colorbricks(j));
				add(brick);
			}
		}
	}
/**
 * colors the bricks
 * for j rows it returns 10 with color width 2
 * then j - 10 of cyan
 */
	private Color colorbricks(int j) {
		if (j == 0 || j == 1) {
			return Color.RED;
		} else if (j == 2 || j== 3) {
			return Color.ORANGE;
		} else if (j == 4 || j == 5) {
			return Color.YELLOW;
		} else if (j == 6 || j == 7) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}
/**
 * how I track the mouse
 * paddle tracks mouse
 * unless mouse would go to a part of screen where 
 * edge of paddle would be out of view
 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if (x < getWidth() - (PADDLE_WIDTH / 2) && (x > PADDLE_WIDTH / 2)) {
			paddle.setLocation(x - (PADDLE_WIDTH / 2), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
}

