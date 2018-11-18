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
	
	//PADDLE
	public GRect PADDLE = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx = rgen.nextDouble(1.0, 3.0);
	private double vy = rgen.nextDouble(1.0, 3.0);
	
	private GOval BALL = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
	
	//Introduce BRICK
	GRect BRICK = null;
	
	//Brick Counter
	private int brickCounter = NBRICK_COLUMNS*NBRICK_ROWS;
	
	
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		setUpBricks();
		addMouseListeners();
		for (int lives = 0; lives < NTURNS; lives++) {
			BALL.setFilled(true);
			BALL.setLocation(CANVAS_WIDTH/2-BALL_RADIUS,CANVAS_HEIGHT/2-BALL_RADIUS);
			add(BALL);
			bouncingBall();
		}
	}
	
	//Setting up the lattice of blocks
	private void setUpBricks() {
		for (int nrows = 0; nrows < NBRICK_ROWS; nrows++) {
			for (int ncolumns = 0; ncolumns < NBRICK_COLUMNS; ncolumns++) {
				double xAdjust = (CANVAS_WIDTH-(BRICK_WIDTH + BRICK_SEP)*(NBRICK_COLUMNS))/2+ncolumns*(BRICK_WIDTH+BRICK_SEP);
				double yAdjust = (BRICK_Y_OFFSET+(nrows*(BRICK_HEIGHT + BRICK_SEP)));
				
				BRICK = new GRect(xAdjust, yAdjust, BRICK_WIDTH,BRICK_HEIGHT);
				BRICK.setFilled(true);
				if (0 == nrows || 1 == nrows) {
					BRICK.setColor(Color.RED);
				} else if (2 == nrows || 3 == nrows) {
					BRICK.setColor(Color.ORANGE);
				} else if (4 == nrows || 5 == nrows) {
					BRICK.setColor(Color.YELLOW);
				} else if (6 == nrows || 7 == nrows) {
					BRICK.setColor(Color.GREEN);
				} else if (8 == nrows || 9 == nrows) {
					BRICK.setColor(Color.CYAN);
				}
				add(BRICK);
			}
		}
	}
	
	//Introduce Ball	
	private void bouncingBall() {
		while(true) {
			BALL.move(vx , vy);
			pause(DELAY);
			if (BALL.getX()+2*BALL_RADIUS >= getCanvasWidth() || BALL.getX() <= 0) {
				vx = -vx;
			} else if (BALL.getY() <= 0) {
				vy = -vy;
			} else if (BALL.getY() >= CANVAS_HEIGHT) {
				break;
			}
			
			//collider
			GObject collider = getCollidingObject();
			if (collider == PADDLE) {
				if (BALL.getY() >= CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT - 2*BALL_RADIUS) {
					vy = -1*vy;
				}
			} else if (collider != null) {
				remove(collider);
				brickCounter= brickCounter--;
				vy = -1*vy;
			}
		}
	}
	
	//Introduce Collisions
	private GObject getCollidingObject() {
			if(getElementAt(BALL.getX(), BALL.getY()) != null) {
				return getElementAt(BALL.getX(), BALL.getY());
			} else if (getElementAt(BALL.getX() + 2*BALL_RADIUS, BALL.getY()) != null) {
				return getElementAt(BALL.getX() + 2*BALL_RADIUS, BALL.getY());
			} else if (getElementAt(BALL.getX() + 2*BALL_RADIUS, BALL.getY() + 2*BALL_RADIUS) != null) {
				return getElementAt(BALL.getX() + 2*BALL_RADIUS, BALL.getY() + 2*BALL_RADIUS);
			} else if (getElementAt(BALL.getX(), BALL.getY() + 2*BALL_RADIUS) != null) {
				return getElementAt(BALL.getX(), BALL.getY() + 2*BALL_RADIUS);
			} else {
				return null;
			}
		}
		
	//Constructing the PADDLE
	public void mouseMoved(MouseEvent e) {
		double xCoor = e.getX() - (PADDLE_WIDTH/2);
		if (xCoor > getWidth()-PADDLE_WIDTH) {
			xCoor = getWidth()-PADDLE_WIDTH;
		} else if (xCoor < PADDLE_WIDTH/2) {
			xCoor = 0;
		}
		
		PADDLE.setLocation(xCoor, (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT));
		PADDLE.setFilled(true);
		add(PADDLE);
	}		
	}