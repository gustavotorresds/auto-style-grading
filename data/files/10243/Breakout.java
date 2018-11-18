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


	GRect paddle;

	GRect bricks;

	GOval ball;

	private double vx, vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);


		layBricks();
		makePaddle();
		makeBall();
		moveBall();


	}

	public void init() {
		addMouseListeners();
	}

	// makes a paddle that is placed at the y offset that is given
	private void makePaddle() {
		double X = CANVAS_WIDTH/2;
		double Y = CANVAS_HEIGHT - PADDLE_Y_OFFSET;
		paddle = new GRect(X, Y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	//allows for the paddle to move
	public void mouseMoved (MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		//if the paddle is on the far right it will stop moving
		if (x >= CANVAS_WIDTH - PADDLE_WIDTH) {
			x = CANVAS_WIDTH - PADDLE_WIDTH;
		}
		//if the paddle is on the far left it will stop moving
		else if (x <= 0) {
			x = 0;
		}
		paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);

	}

	//creates the colorful bricks
	private void layBricks() {
		double Y = BRICK_Y_OFFSET; 
		for(int j = 1; j < NBRICK_ROWS +1; j++) {
			double X = (CANVAS_WIDTH - NBRICK_COLUMNS * BRICK_WIDTH - (BRICK_SEP * (NBRICK_COLUMNS - 1)))/2;
			Color brickColor = Color.BLACK;
			//first rows of bricks are red
			if (j <= 2) {
				brickColor = Color.RED;
			}
			//third and fourth row of bricks are orange
			if (j <= 4 && j > 2) {
				brickColor = Color.ORANGE;
			}
			//fifth and sixth row of bricks are yellow
			if (j <= 6 && j > 4) {
				brickColor = Color.YELLOW;
			}
			//seventh and eighth row of bricks are green 
			if (j <= 8 && j > 6) {
				brickColor = Color.GREEN;
			}
			//ninth and tenth row of bricks are cyan
			if (j <= 10 && j > 8) {
				brickColor = Color.CYAN;
			}
			for(int i = 1; i < NBRICK_COLUMNS +1; i++) {
				GRect bricks = new GRect(X, Y, BRICK_WIDTH, BRICK_HEIGHT);
				X = X + BRICK_SEP + BRICK_WIDTH;
				bricks.setFilled(true);
				bricks.setColor(brickColor);
				add(bricks);
			}
			Y = Y + BRICK_SEP + BRICK_HEIGHT;

		}
	}

	//makes a ball and adds it to screen
	private void makeBall() {
		double X = getWidth()/2 - BALL_RADIUS;
		double Y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(X, Y, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);		
	}

	private void moveBall() {
		//start count for how many bricks there are 
		double count = NBRICK_COLUMNS * NBRICK_ROWS;
		//gives you three lives
		for (int i = 0; i < 3; i++) {
			//will exit the four loop if there are no bricks left
			if (count == 0) {
				break;
			}
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			vy = VELOCITY_Y;
			//waits until the user clicks for the ball to drop
			waitForClick();
			while (ball.getY() < getHeight() - BALL_RADIUS*2) {
				GObject collider = getCollidingObject();
				ball.move(vx, vy);
				pause(10);
				//if the ball hits the left side of the screen, its direction changes 
				if (ball.getX() < 0 ) {
					vx = -vx;
				}
				//if the ball hits the right side of the screen, its direction changes
				else if (ball.getX() > getWidth()) {
					vx = -vx;
				}
				//if the ball is below the top of the screen, it will move down
				else if (ball.getY() < 0) {
					vy = -vy;
				}
				//if the ball hits the paddle, it will bounce off of it and go back up
				if (collider != null) {
					if (collider == paddle) {
						vy = -Math.abs(vy);
					}
					//if the ball hits a brick
					else if (collider != paddle) {
						vy = -vy;
						//the brick will be removed if hit
						remove(collider);
						//subtracts a brick from the count if one is hit
						count--;
						if (count == 0) {
							break;
						}

					}
				}
			}
			//tells you that you won if there are no bricks left
			if (count == 0) {
				GLabel win = new GLabel("You Won!", getWidth()/2, getHeight()/2);
				add(win);
			}
			//resets the ball if it drops
			else {
				ball.setLocation(getWidth()/2, getHeight()/2);
			}	
		}
		//tells you that you lost if there are still bricks and you are out of lives
		if (count > 0) {
			GLabel lose = new GLabel("You Lost:(", getWidth()/2, getHeight()/2);
			add(lose);
		}

	}

	private GObject getCollidingObject() {
		GObject collider = null;
		double X = ball.getX();
		double Y = ball.getY();
		//sees if anything hits the top left of the ball
		if (getElementAt(X, Y) != null) {
			collider = getElementAt(X, Y);
		}
		//sees if anything hits the top right of the ball
		else if (getElementAt(X + BALL_RADIUS*2, Y) != null) {
			collider = getElementAt(X + BALL_RADIUS*2, Y);
		}
		//sees if anything hits the bottom left of the ball
		else if (getElementAt(X, Y + BALL_RADIUS*2) != null) {
			collider = getElementAt(X, Y + BALL_RADIUS*2);
		}
		//sees if anything hits the bottom right of the ball
		else if (getElementAt(X + BALL_RADIUS*2, Y) != null) {
			collider = getElementAt(X + BALL_RADIUS*2, Y);
		}
		return collider;
	}
}
