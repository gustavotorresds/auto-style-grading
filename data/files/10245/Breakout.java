/*
 * File: Breakout.java
 * -------------------
 * Name:Leonardo Orsini
 * Section Leader:Niki Agrawal
 * 
 * Breakout Game! Objective: get rid of all the bricks. You have 3 chances. 
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
	public static final double DELAY = 1000.0 / 100.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Instance variables
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private int count = NBRICK_ROWS * NBRICK_COLUMNS; // total # of bricks
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setup();
		for (int rounds = 0; rounds < 3 && count > 0; rounds++) {
			movingBall();
		}
		if (count == 0) {
			GLabel won = new GLabel ("You Won!");	// won
			won.setFont("Courier-24");
			add(won, getWidth() / 2 - (won.getWidth() / 2), getHeight() / 2 - (won.getWidth() / 2));
		} else {
			GLabel lose = new GLabel ("You Lose!");	// lose
			lose.setFont("Courier-24");
			add(lose, getWidth() / 2 - (lose.getWidth() / 2), getHeight() / 2 - (lose.getHeight() / 2));
		}
	}


	/***
	 * Method: setup()
	 * ---------------
	 * builds bricks and paddle
	 */
	private void setup() {
		buildBricks();
		buildPaddle();
	}

	/***
	 * Method: getCollidingObject()
	 * @return an obj that is not null
	 * Returns an object at each of the 4 corners of the ball
	 */
	private GObject getCollidingObject() {
		// top left
		getElementAt(ball.getX(), ball.getY());
		GObject obj = getElementAt(ball.getX(), ball.getY());
		if (obj != null) {
			return obj;
		}
		//bottom left
		getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));	
		GObject obj1 = getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
		if (obj1 != null) {
			return obj1;
		}
		//top right
		getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		GObject obj2 = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		if (obj2 != null) {
			return obj2;
		}
		//bottom right
		getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		GObject obj3 = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		if (obj3 != null) {
			return obj3;
		} else {
			return null;
		}
	}

	/***
	 * Method: movingBall()
	 * --------------------
	 * Controls animation: bounces ball off walls, checks for collisions, and 
	 * recreates and deletes ball position each round + changes velocity
	 */
	private void movingBall() {
		buildBall();		// recreates ball
		waitForClick();
		vy = VELOCITY_X_MAX;	
		// so that the x-coordinate changes
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); // for speed
		if (rgen.nextBoolean(0.5)) vx = -vx;		// 50% chance left or right
		while(count > 0) {		//switch out true

			ball.move(vx, vy);
			pause(DELAY);

			// left and right walls
			if (ball.getX() < 0 || ball.getX() > getWidth() - BALL_RADIUS * 2) {
				vx = -vx;
			}

			// top wall
			if (ball.getY() < 0) {
				vy = -vy;
			}

			// bottom wall
			if (ball.getY() > getHeight() - BALL_RADIUS * 2) {
				break;
			}

			// checking for collisions 

			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy = -vy;		// changes velocity
			} else if (collider != null) {
				vy = -vy;		// changes velocity
				remove(collider);		// removes brick
				count --;		// subtracts from total bricks
			}
		}
		remove(ball);	// removes ball
	}


	/***
	 * Method: buildBall()
	 * ---------------------
	 * Builds ball and set it at the initial position center of screen
	 */
	private void buildBall() {
		ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		double initialx = (getWidth() / 2) - (ball.getWidth() / 2);
		double initialy = (getHeight() / 2);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball, initialx, initialy);
	}

	/***
	 * Method: buildPaddle()
	 * ---------------------
	 * Builds paddle and sets it at the initial position at the bottom of screen
	 */
	private void buildPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);		// reassigns instance variable
		double initialx = (getWidth()  / 2) - (0.5 * PADDLE_WIDTH);
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle, initialx, y);
	}

	/*** 
	 * Executed when mouse is moved. Mouse tracks the paddle and prevents it
	 * from going out of the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		double dx = e.getX() - (paddle.getX() + PADDLE_WIDTH / 2);	//centers mouse track to center of paddle
		double dy = 0;
		if(e.getX() < getWidth() - (PADDLE_WIDTH / 2) && e.getX() > PADDLE_WIDTH / 2) {	//blocks the paddle from going off the screen
			paddle.move(dx, dy);	
		}
	}

	/***
	 * Method: buildBricks()
	 * ---------------------
	 * PreCondition: no bricks
	 * PostCondition: builds bricks and sorts rows for color
	 */
	private void buildBricks() {
		for(int i = 0; i < NBRICK_ROWS; i++) {
			if (i < 2) {
				buildRow(i, Color.RED);
			}
			else if(i < 4) {
				buildRow(i, Color.ORANGE); 
			}
			else if(i < 6) {
				buildRow(i, Color.YELLOW); 
			}
			else if(i < 8) {
				buildRow(i, Color.GREEN);
			} else {
				buildRow(i, Color.CYAN);
			}
		}
	}

	/***
	 * Method: buildRow()
	 * @param i is the row number
	 * @param color is the appropriate color to be filled
	 * PreCondition: no bricks
	 * PostConidtion: builds bricks with location, dimensions, and color
	 */
	private void buildRow(int i, Color color) {
		for(int j = 0; j < NBRICK_ROWS; j++) {
			GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
			double x = (getWidth() - (BRICK_SEP*(NBRICK_ROWS+1) + BRICK_WIDTH*(NBRICK_ROWS))) / 2 + BRICK_SEP*(j+1) + BRICK_WIDTH*j;
			double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * i;
			brick.setColor(color);
			brick.setFilled(true);
			add(brick, x, y);
		}
	}
}
