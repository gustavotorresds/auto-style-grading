/*
 * File: Breakout.java
 * -------------------
 * Name: Antoni Brasó
 * Section Leader: Akua MkLeod
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
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Offset of the first column from the left, in pixels 
	public static final double BRICK_X_OFFSET = (CANVAS_WIDTH-NBRICK_COLUMNS*BRICK_WIDTH-(NBRICK_COLUMNS-1)*BRICK_SEP)/2;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 8.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Mouse X coordinates
	int mouseX;

	// Defines the paddle
	GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	// Defines the X and Y coordinates of the paddle
	double paddleX; 
	double paddleY; 

	private GOval ball;

	// Generates a random number for x
	private RandomGenerator rgen = RandomGenerator.getInstance();	

	int numberBricks = NBRICK_ROWS*NBRICK_COLUMNS;

	public void run() {
		createBreakoutGame();
		// Creates a ball and moves it through the canvas
		// The ball bounces when it hits the left, upper and right canvas wall
		// The ball bounces when it hits the paddle
		// The ball bounces when it hits a brick and eliminates it
		// The ball is disappears when hitting the lower canvas wall 
		for (int i = 0; i < NTURNS; i++) { 
			moveBallAndPlay ();
			if (numberBricks == 0) {
				remove(ball);
				remove(paddle);
				youWon();
				break;
			}
		}	
		if (numberBricks >0) {
			removeAll();
			youLost();
		}
		addMouseListeners();
	}


	private void createBreakoutGame() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// Sets up 10 rows and 10 columns of colored bricks, on the top of the Canvas	
		setUpBricks();
		// Adds the paddle and moves it in the X axes according to the mouse movement
		drawPaddle ();
	}

	private void setUpBricks () { 
		for (int row = 0; row<NBRICK_ROWS; row++) {
			// defines width (x) at which each bricks row starts, such that the bricks are centered 
			double y = BRICK_Y_OFFSET + (BRICK_SEP+BRICK_HEIGHT)*row; 
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				// defines x point at which each row starts. Base row starts at the center of the first axis minus the width of 7 bricks. Each brick is put next to 
				// the previous one. Each superior row starts half brick more to the right
				double x = BRICK_X_OFFSET + (BRICK_SEP + BRICK_WIDTH) * col;  
				GRect baseBricks = new GRect (x, y , BRICK_WIDTH, BRICK_HEIGHT);
				baseBricks.setFilled(true);

				// defines the bricks colors
				if (row <=1) {
					baseBricks.setColor(Color.RED);
				}
				else if (row <= 3) {
					baseBricks.setColor(Color.ORANGE);	
				}
				else if (row <= 5) {
					baseBricks.setColor(Color.YELLOW);	
				}
				else if (row <= 7) {
					baseBricks.setColor(Color.GREEN);	
				}
				else baseBricks.setColor(Color.CYAN);
				add(baseBricks);
			}
		}
	}

	private void drawPaddle () {	
		paddleY = getHeight()-(PADDLE_Y_OFFSET + PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setLocation(mouseX, paddleY);
		add(paddle);
	}	

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX(); // get the x-coordinate of where the mouse moves to
		if (mouseX > getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, paddleY); // does not allow the paddle to go beyond the right wall
		} else {
			paddle.setLocation(mouseX, paddleY);
		}
	}


	private double vx, vy;
	private void ballSpeed() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx=-vx;
		}
	}

	private void moveBallAndPlay() {
		ball = makeBall();	
		waitForClick();
		ballSpeed();
		while(true) {
			ball.move(vx, vy);
			pause(DELAY);
			// Checks for left, right, and top walls and updates the direction/speed if the ball hits one
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}	
			// Checks for the bottom wall. If the ball hits it, that turn is lost and a new turn starts.
			// After 3 lost turns the game is over
			if (hitBottomWall(ball)) {
				remove(ball);
				break;
			}

			// Checks for bricks. Eliminates a brick when one is hit and changes the direction
			double ballX = ball.getX();
			double ballY = ball.getY();
			GObject collider = getCollidingObject(ballX, ballY);
			if (collider == paddle) {
				vy = -VELOCITY_Y;
			}			
			else if (collider != null) { 
				remove(collider);
				numberBricks = numberBricks-1;
				vy = - vy;
			}
		}
	}

	/**
	 * Method: Make Ball
	 * -----------------------
	 * Creates a ball, adds it to the screen, and returns it so
	 * that the ball can be used for animation.
	 */
	public GOval makeBall() {
		double size = BALL_RADIUS * 2;
		GOval ball = new GOval(size, size);
		ball.setFilled(true);
		add(ball, 1, getHeight()/2);
		return ball;
	}	

	/**
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the bottom wall of the window.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight();
	}

	/**
	 * Method: Hit Top Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/**
	 * Method: Hit Right Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the right wall of the window.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/**
	 * Method: Hit Left Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the left wall of the window.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	// returns the object the ball collides with (bricks, paddle or nothing)
	private GObject getCollidingObject(double ballX, double ballY) {
		if (getElementAt(ballX, ballY) != null) {
			return getElementAt(ballX, ballY);
		}
		else if (getElementAt(ballX, ballY+2*BALL_RADIUS) != null) {
			return getElementAt(ballX, ballY+2*BALL_RADIUS);
		}
		else if (getElementAt(ballX+2*BALL_RADIUS, ballY+2*BALL_RADIUS) != null) {
			return getElementAt(ballX+2*BALL_RADIUS, ballY+2*BALL_RADIUS);
		}
		else if (getElementAt(ballX+2*BALL_RADIUS, ballY) != null) {
			return getElementAt(ballX+2*BALL_RADIUS, ballY);
		}
		else {
			return null;
		}
	}


	/**
	 * Method: You Lost
	 * -----------------------
	 * Removes all the objects if the ball hits the bottom wall 3 times or more and
	 * creates a label saying "You Lost :("
	 * 
	 */
	private void youLost() {
		GLabel youLost = new GLabel ("You Lost :(");
		youLost.setFont("Courier-48");
		youLost.setColor(Color.RED);
		add(youLost, getWidth()/2-youLost.getWidth()/2, getHeight()/2-youLost.getHeight()/2);
	}

	/**
	 * Method: You Won
	 * -----------------------
	 * Removes all the objects if the all the bricks are eliminated
	 * creates a label saying "You Won!!! =)"
	 */

	private void youWon() {
		GLabel youWon = new GLabel ("You Won!!! =)");
		youWon.setFont("Courier-48");
		youWon.setColor(Color.GREEN);
		add (youWon, getWidth()/2-youWon.getWidth()/2, getHeight()/2-youWon.getHeight()/2);
	}

}


