/*
 * File: Breakout.java
 * -------------------
 * Name: Elena Felix
 * Section Leader: Maggie
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
	public static final double DELAY = 700.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;






	//Creates instance variables for brick placement
	private double x = 0;
	private double y = 0;

	//Keeps track of paddle location (instance variable)
	private double paddleX = 0;
	private double paddleY = 0;

	//Keeps track of ball velocity in the x and y directions (instance variable)
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Keeps track of ball location (instance variable)
	private double ballX = 0;
	private double ballY = 0;

	//Creates the paddle so it is visible to mouseClicked
	private GRect paddle = null;
	private GOval ball = null;

	//Keeps track of how many bricks are left onscreen
	private int brickCounter = NBRICK_ROWS*NBRICK_COLUMNS;






	public void run() {
		/* Set the window's title bar text and canvas size */
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* Set up the game's environment */
		setUp();
		addMouseListeners();

		/* Generates three subsequent turns of play */
		for (int i=0; i<NTURNS; i++) {
			oneTurn();
			if (brickCounter == 0) {
				GLabel win = new GLabel("YOU WON!");
				add(win, getWidth()/2.0-win.getWidth()/2.0, getHeight()/2.0-win.getHeight()/2.0);
				remove(ball);
				return;
			}
			remove(ball);
		}
		GLabel lose = new GLabel("You lost...");
		add(lose, getWidth()/2.0-lose.getWidth()/2.0, getHeight()/2.0-lose.getHeight()/2.0);
	}

	private void setUp() {
		makeBricks();
		makePaddle();

	}

	private void makeBricks() {
		x = 2*BRICK_SEP;
		y = BRICK_Y_OFFSET;
		makeTwoRows(Color.RED);
		makeTwoRows(Color.ORANGE);
		makeTwoRows(Color.YELLOW);
		makeTwoRows(Color.GREEN);
		makeTwoRows(Color.CYAN);

	}

	private void makeTwoRows(Color brickColor) {
		for (int i=0; i<NBRICK_ROWS/5; i++) {
			for (int j=0; j<NBRICK_COLUMNS; j++) {
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setColor(brickColor);
				brick.setFilled(true);
				add(brick);
				x = x + BRICK_WIDTH + BRICK_SEP;
			}
			x = 2*BRICK_SEP;
			y = y + BRICK_HEIGHT + BRICK_SEP;
		}

	}

	private void makePaddle() {
		paddleX = getWidth()/2-PADDLE_WIDTH/2;
		paddleY = getHeight()-PADDLE_Y_OFFSET;
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX <= getWidth()-PADDLE_WIDTH) {
			paddleX = mouseX;
			add(paddle, paddleX, paddleY);
		}
	}

	private void oneTurn() {
		makeBall();
		waitForClick();
		launchBall(ball);
	}

	private GOval makeBall() {
		ball = new GOval(BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ballX = getWidth()/2.0-BALL_RADIUS/2.0;
		ballY = getHeight()/2.0-BALL_RADIUS;
		add(ball, ballX, ballY);
		return ball;
	}

	private void launchBall(GObject ball) {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (true) {
			ball.move(vx, vy);
			ballX = ballX + vx;
			ballY = ballY + vy;
			pause(DELAY);
			if (ballX >= getWidth()-BALL_RADIUS || ballX <= 0) {
				vx = -vx;
			}
			if (ballY <= 0) {
				vy = -vy;
			}
			if (ballY >= getHeight()-BALL_RADIUS) {
				return;
			}
			GObject collider = getCollidingObject();
			if (collider != null) {
				if (collider == paddle) {
					if (ballY+BALL_RADIUS <= paddleY ) {
						vy = -vy;
					}
				} else {
					vy = -vy;
					remove(collider);
					brickCounter = brickCounter - 1;
				}
			}
			if(brickCounter == 0) {
				return;
			}
		}
	}

	private GObject getCollidingObject() {
		GObject obj = getElementAt(ballX, ballY);
		if (obj == null) {
			obj = getElementAt(ballX+BALL_RADIUS, ballY);
		} else {
			return obj;
		}
		if (obj == null) {
			obj = getElementAt(ballX, ballY+BALL_RADIUS);
		} else {
			return obj;
		}
		if (obj == null) {
			obj = getElementAt(ballX+BALL_RADIUS, ballY+BALL_RADIUS);
		} else {
			return obj;
		}
		return obj;
	}



}
