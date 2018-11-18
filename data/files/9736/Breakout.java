/*
 * File: Breakout.java
 * -------------------
 * Name:Hannah Llorin
 * Section Leader: James Zhuang
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
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	//instance variable
	private RandomGenerator rgen = RandomGenerator.getInstance();
	//instance variable for the paddle to be tracked
	private GRect paddle;
	//instance variable for the velocity of the ball to be tracked
	private GOval ball;
	private double vx, vy;
	private double BrickCounter = (NBRICK_ROWS * NBRICK_COLUMNS);


	public void run() {
		addMouseListeners();
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		makeBricks();
		makePaddle();
		makeBall();
		setInitialVxYy();
		while(BrickCounter != 0) {
			moveBall();
		}
		endGame();
	}

	private void makeBricks () {
		makeRow(0, Color.RED);
		makeRow(1, Color.RED);
		makeRow(2, Color.ORANGE);
		makeRow(3, Color.ORANGE);
		makeRow(4, Color.YELLOW);
		makeRow(5, Color.YELLOW);
		makeRow(6, Color.GREEN);
		makeRow(7, Color.GREEN);
		makeRow(8, Color.CYAN);
		makeRow(9, Color.CYAN);
	}

	private void makeRow(int columnNumber, Color color) {
		int brickNumber = NBRICK_COLUMNS;
		while (brickNumber != 0) {
			double BrickX = CANVAS_WIDTH - (BRICK_WIDTH + BRICK_SEP) * brickNumber;
			double BrickY = BRICK_Y_OFFSET + columnNumber * (BRICK_HEIGHT + BRICK_SEP);
			GRect brick = new GRect (BrickX, BrickY, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
			brickNumber = brickNumber - 1;
		}
	}

	private void makePaddle() {
		paddle = new GRect (getWidth() /2, (getHeight()-PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	public void mouseMoved(MouseEvent e) {
		int MouseX = e.getX(); 
		if (MouseX < getWidth()-PADDLE_WIDTH && MouseX > -PADDLE_WIDTH) {
			paddle.setLocation(MouseX, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	private void makeBall() {
		double ballX = getWidth() / 2;
		double ballY = getHeight()/ 2;
		ball = new GOval (ballX, ballY, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	private void setInitialVxYy() {
		vy = 3.0;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}

	private void moveBall() {
		ball.move(vx,vy);
		//check for collision with walls
		if (ball.getX() > (getWidth()-(ball.getWidth()))) {
			vx = -vx;
		}
		if (ball.getX() < 0) {
			vx = -vx;
		}
		if (ball.getY() > (getHeight()-(ball.getHeight()))) {
			remove(ball);
			makeBall();
		}
		if (ball.getY() < 0 ) {
			vy = -vy;
		}
		//check for collision with paddle or brick
		GObject collider = getCollidingObject();
		if (collider != null) {
			if (collider != paddle) {
				remove(collider);
				vy = -vy;
				BrickCounter = BrickCounter - 1;
			} 
			if (collider == paddle) {
				double a = ball.getY();
				if (a == getHeight() - PADDLE_Y_OFFSET) {
					vy = -vy;
				}	
			}
		}
		pause(DELAY);
	}

		private GObject getCollidingObject() {
			if (getElementAt(ball.getX(), ball.getY()) != null) {
				return (getElementAt(ball.getX(), ball.getY()));
			}
			else if (getElementAt(ball.getX(), ball.getY() + getHeight()) != null) {
				return (getElementAt(ball.getX(), ball.getY() + ball.getHeight()));
			}
			else if (getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight()) !=null) {
				return (getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight()));
			}
			else if(getElementAt(ball.getX() + ball.getWidth(), ball.getY()) !=null) {
				return (getElementAt(ball.getX() + ball.getWidth(), ball.getY()));
			} else { 
				return null;
			}
		}
		
		private void endGame() {
			GLabel label = new GLabel("");
			label.setLabel("You Won!");
			add(label, getWidth()/2, getHeight()/2);
		}	
	}