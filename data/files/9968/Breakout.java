/*
 * File: Breakout.java
 * -------------------
 * Name: Anderson Sumarli
 * Section Leader: Julia Rachel Truitt
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

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		for (int lives = 0; lives < NTURNS; lives++) {
			setUpGame();
			playGame();
			clear();
		}
		GLabel loser = new GLabel ("Sorry, you lost!");
		loser.setFont("Courier-24");
		loser.setColor(Color.RED);
		add (loser, getWidth() / 2 - loser.getWidth() / 2, getHeight() / 2 - loser.getHeight() / 2);
	}
	
	// set up game;
	private void setUpGame() {
		setUpTheBricks();
		createThePaddle();
	}
	
	// set up the bricks
	private void setUpTheBricks() {
		// loop row of bricks
		for (int rowOfBricks = 0; rowOfBricks < NBRICK_ROWS; rowOfBricks++) {		
			// loop column of bricks
			for (int bricksBuilt = 0; bricksBuilt < NBRICK_COLUMNS; bricksBuilt++) {
				double y = BRICK_Y_OFFSET + rowOfBricks * (BRICK_HEIGHT + BRICK_SEP);
				double x = getWidth()/2 - NBRICK_COLUMNS * BRICK_WIDTH / 2 - (NBRICK_COLUMNS - 1) * BRICK_SEP / 2 + bricksBuilt * (BRICK_WIDTH + BRICK_SEP);
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				// set colors
				if (rowOfBricks <= 2) {
				rect.setColor(Color.RED);
				}
				if (rowOfBricks > 2 && rowOfBricks <=4) {
				rect.setColor(Color.ORANGE);
				}
				if (rowOfBricks > 4 && rowOfBricks <=6) {
				rect.setColor(Color.YELLOW);
				}
				if (rowOfBricks > 6 && rowOfBricks <=8) {
				rect.setColor(Color.GREEN);
				}
				if (rowOfBricks > 8 && rowOfBricks <=10) {
				rect.setColor(Color.CYAN);
				}
				add(rect);
			}
		}
	}
	
	// create paddle object
	private GRect paddle;
	
	// create the paddle
	private void createThePaddle() {
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double x = getWidth() - PADDLE_WIDTH / 2;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	
	// make paddle move with mouse
	public void mouseMoved(MouseEvent e) {
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double x = e.getX() - PADDLE_WIDTH / 2;
		if (x > 0 && x < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(x, y);
		}
	}
	
	// create ball object
	private GOval ball;
	
	// play game;
	private void playGame() {
		createBall();
		ballBounce();
	}
	
	// create a ball
	private void createBall() {
		double y = getHeight() / 2 - BALL_RADIUS;
		double x = getWidth() / 2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	// create random-number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// declare velocity of ball
	private double vx, vy;
	
	// colliding object check
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		} else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		} else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		} else {
			return null;
		}
	}
	
	// count bricks
	private int countBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	
	// get ball to bounce off walls
	private void ballBounce() {
		// determine velocity of ball
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		// move ball
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);
			// reverse sign if hit wall
			if (ball.getX() <= 0 || ball.getX() >= getWidth() - BALL_RADIUS * 2) {
				vx = -vx;
			}
			if (ball.getY() <= 0) {
				vy = -vy;
			}
			// check if collide with paddle or brick
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				if (ball.getY() + BALL_RADIUS * 2 >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT) {
					vy = -vy;
				}
			} else if (collider != null) {
				remove(collider);
				vy = -vy;
				countBricks--;
			}
			if (ball.getY() + BALL_RADIUS * 2 >= getHeight()) {
				break;
			}
			if (countBricks == 0) {
				GLabel winner = new GLabel ("Congratulations, you won!");
				winner.setFont("Courier-24");
				winner.setColor(Color.BLUE);
				add (winner, getWidth() / 2 - winner.getWidth() / 2, getHeight() / 2 - winner.getHeight() / 2);
			}
		}
	}
}
