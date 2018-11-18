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
	public static final int NBRICK_ROWS = 20;

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

	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy; 
	private int numberOfBricks;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp();
		play();

	}

	/** 
	 * The method setUp essentially compiles all of the methods below that
	 * make up the paddle, ball, bricks etc (everything that is needed before
	 * the game is able to be played).
	 */

	public void setUp(){
		addingBricks();
		addingPaddle();
		addMouseListeners();

	}

	/**
	 * addingBricks uses the instance variables mentioned before the public run method
	 * to add bricks to the screen as well as color/fill them.  
	 */

	private void addingBricks(){

		for (int row = 0; row < NBRICK_ROWS; row++) {

			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				add (brick, (getWidth()/2.0 - ((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS)/2.0 + (BRICK_WIDTH + BRICK_SEP) * column) + BRICK_SEP/2.0, PADDLE_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row);

				if (row % 10 == 0 || row % 10 == 1) {
					brick.setColor(Color.RED);
				} else if (row % 10 == 2 || row % 10 == 3) {
					brick.setColor(Color.ORANGE);
				} else if (row % 10 == 4 || row % 10 == 5) {
					brick.setColor(Color.YELLOW);
				} else if (row % 10 == 6 || row % 10 == 7) {
					brick.setColor(Color.GREEN);
				} else if (row % 10 == 8 || row % 10 == 9) {
					brick.setColor(Color.CYAN);
				}
				brick.setFilled(true);
			}
		}
		numberOfBricks = NBRICK_ROWS * NBRICK_COLUMNS;
	}		


	/**
	 * mouseMoved method allows the paddle to follow where the mouse goes. 
	 */

	public void mouseMoved (MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET;
		if ( x >= 0 && x <= getWidth() - PADDLE_WIDTH ) {			
			paddle.setLocation(x, y);
		}

	}

	/**
	 * addingPaddle adds the paddle to the screen per our instance variables defined
	 * above the run method 
	 */

	private void addingPaddle() {
		double x = getWidth()/2.0 - PADDLE_WIDTH/2.0;
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/**
	 * this method creates the ball and allows for the ball to begin with 
	 * random direction each time the player loses a life or restarts the game 
	 */

	private void createBall() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		ball = new GOval (getWidth()/2.0 - BALL_RADIUS/2.0, getHeight()/2.0 - BALL_RADIUS/2.0, 2.0 * BALL_RADIUS, 2.0 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	/**
	 * play is a method that incorporates all the other methods needed to allow the player
	 * to play the game (i.e. check for collisions, moving the ball, and accounting for lives)
	 */

	private void play() {
		int counter = NTURNS;
		while(counter > 0 && numberOfBricks > 0) {
			createBall();
			waitForClick();
			while(ball.getY() <= getHeight() && numberOfBricks > 0) {
				moveBall();
				checkCollisions();
			}
			counter--;
		}
		if (counter == 0) {
			GLabel winningLabel = new GLabel ("Game Over :(");
			add(winningLabel, getWidth()/2.0 - winningLabel.getWidth()/2.0, getHeight()/2.0 + winningLabel.getAscent()/2.0);
		} else {
			removeAll();
			GLabel losingLabel = new GLabel ("Congratulations, you won!");
			add(losingLabel, getWidth()/2.0 - losingLabel.getWidth()/2.0, getHeight()/2.0 + losingLabel.getAscent()/2.0);
		}
	}

	/**
	 * this method allows for the ball to move, giving it direction
	 */

	private void moveBall() {
		ball.move(vx, vy);
		pause(DELAY);
	}

	/*
	 * checkCollisions determines if the ball hits a wall or bricks or the paddle and 
	 * takes the necessary action needed depending on the object the ball hits.
	 * CHANGES DIRECTION!
	 */

	private void checkCollisions () {
		// checks for walls 
		GObject collider = getCollidingObject();
		if (ball.getX() >= getWidth() - BALL_RADIUS * 2.0 || ball.getX() <= 0) {
			vx = -vx;
		}
		else if (ball.getY() >= getHeight() - BALL_RADIUS * 2.0) {
			remove(ball);
		}
		else if (ball.getY() <= 0) {
			vy = -vy;
		}
		else if (collider == paddle) {
			vy = -Math.abs(vy);
		} else if (collider != null && collider != paddle) {
			vy = -vy;
			remove(collider);
			numberOfBricks--;
		}
	}

	/**
	 * getColliding objects allows for the ball to return what object was involved in the 
	 * collision or null
	 */

	private GObject getCollidingObject() {
		GObject object = null;
		if (getElementAt(ball.getX(), ball.getY()) != null && getElementAt(ball.getX(), ball.getY()) != paddle) {
			object = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			object = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null && getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != paddle) {
			object = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			object = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return object;
	}
}












