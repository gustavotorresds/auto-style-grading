/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
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



	/*---------------------------------------------------------------------*/
	/*PROGRAM STARTS HERE */
	/*---------------------------------------------------------------------*/


	/* Declare all instance variables*/

	GRect paddle;
	GOval ball;
	GObject collider; 
	double yPaddle;
	double xPaddle;
	RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx = rgen.nextDouble (1.0 , 3.0);
	private double vy = -3.0;
	int lifeCount = NTURNS;		// counts the number of turns
	int nBrick = NBRICK_COLUMNS*NBRICK_ROWS;		// the total number of bricks


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		double vx = rgen.nextDouble (1.0 , 3.0);
		double vy= 3.0;
		setUp();
		play();
	}

	// set up all elements of the game
	private void setUp() {
		createBricks();			
		createPaddle();
		createBall();
		addMouseListeners();
		waitForClick();		
	}

	// create a series of bricks and add them on the screen
	private void createBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {		
			buildRow(row);
		}
	}

	private void buildRow (int row) {
		for (int column = 0; column < NBRICK_COLUMNS; column++) {			
			GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
			double midX = (getWidth()/2 - (NBRICK_COLUMNS*BRICK_WIDTH/2)) - BRICK_WIDTH/2;	// Here:
			double x = (column*BRICK_WIDTH) + (BRICK_SEP*column);								// Center the bricks
			double y = (row*BRICK_HEIGHT) + (BRICK_SEP*row) + BRICK_Y_OFFSET;					//
			brick.setFilled(true);
			paintBrick(brick, row);
			add (brick, midX + x, y);
		}
	}

	// paint the bricks
	private void paintBrick(GRect brick, int row) {
		if (row % 10 == 0 || row % 10 == 1) {
			brick.setColor(Color.RED);
		}
		if (row % 10 == 2 || row % 10 == 3) {
			brick.setColor(Color.ORANGE);
		}
		if (row % 10 == 4 || row % 10 == 5) {
			brick.setColor(Color.YELLOW);
		}
		if (row % 10 == 6 || row % 10 == 7) {
			brick.setColor(Color.GREEN);
		}
		if (row % 10 == 8 || row % 10 == 9) {
			brick.setColor(Color.CYAN);
		}
	}

	// create a paddle and add it on the screen
	private void createPaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double x = xPaddle;
		yPaddle = getHeight() - PADDLE_Y_OFFSET;
		add (paddle, x, yPaddle);
	}

	// set the paddle's location
	public void mouseMoved(MouseEvent e) {
		xPaddle = e.getX();
		if (e.getX() + PADDLE_WIDTH > getWidth()) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, yPaddle);	
		} else {
			paddle.setLocation(xPaddle,yPaddle);	
		}
	}

	// create a ball and add it on the screen
	private void createBall() {
		ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2); 
		double xBall = getWidth()/2 - BALL_RADIUS;
		double yBall = getHeight()/2 - BALL_RADIUS;
		ball.setFilled(true);
		add (ball,xBall,yBall);	
	}

	// play the game
	private void play() {

		boolean gameIsRunning = true;
		while (gameIsRunning) {
			ball.move(vx , vy);
			pause (DELAY);
			checkForCollisions();

			// end game
			if (lifeCount == 0 || nBrick == 0) {
				gameIsRunning = false;
			}
		}
	}


	private void checkForCollisions() {

		// hit left or right wall
		if ((ball.getX() > getWidth() - BALL_RADIUS*2) || (ball.getX() <=  0)) {
			vx = - vx;
		}

		// hit top wall
		if ((ball.getY() <= 0)) {
			vy = - vy;
		}

		// hit bottom wall
		if (ball.getY() >= getHeight() - BALL_RADIUS*2) {
			remove(ball);
			double xBall = getWidth()/2 - BALL_RADIUS;
			double yBall = getHeight()/2 - BALL_RADIUS;
			add (ball, xBall, yBall);
			// minus one life
			lifeCount --; 	
		}

		// check for the colliding objects
		collider = getCollidingObject();
		if (collider != null) {

			// hit paddle
			if (collider == paddle) {	//since there are only two non-null objects on the screen: paddle and brick, 
				vy = - Math.abs(vy);		// if collider is not paddle then it must be brick
			} else {
				// hit brick
				remove(collider);	
				vy = - vy;
				// minus one brick
				nBrick--;	
				// add sound
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();
			}
		}
	}


	// check four corners of the ball for collisions
	private GObject getCollidingObject() {
		double xBall = ball.getX();
		double yBall = ball.getY();
		if (getElementAt(xBall, yBall) != null) {
			return (getElementAt(xBall, yBall));
		}
		if (getElementAt(xBall + 2*BALL_RADIUS, yBall) != null) {
			return (getElementAt(xBall + 2*BALL_RADIUS, yBall));
		}
		if (getElementAt (xBall, yBall + 2*BALL_RADIUS) != null) {
			return (getElementAt(xBall, yBall + 2*BALL_RADIUS));
		}
		if (getElementAt (xBall + 2*BALL_RADIUS, yBall + 2*BALL_RADIUS) != null) {
			return (getElementAt(xBall + 2*BALL_RADIUS, yBall + 2*BALL_RADIUS));	
		}
		return null;
	}
}




