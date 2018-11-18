/*
 * File: Breakout.java
 * -------------------
 * Name: Disney Vorng
 * Section Leader: Andrew Marshall
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
	
	//paddle
	GRect paddle = null;
	GOval ball = null;
	
	//velocity
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//win conditions
	private int brickCount;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBricks();
		paddle = getPaddle();
		for (int i = 0; i < NTURNS; i++) {
			addMouseListeners();
			ball = setBall();
			animateBall();
		}
	}
	
	
	private GObject getCollidingObject() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		GObject topLeft = getElementAt(ballX, ballY);
		GObject topRight = getElementAt(ballX + BALL_RADIUS, ballY);
		GObject bottomLeft = getElementAt(ballX, ballY + BALL_RADIUS);
		GObject bottomRight = getElementAt(ballX + BALL_RADIUS, ballY + BALL_RADIUS);
		if (topLeft != null) {	
			return(topLeft);
		} else if (topRight != null) {
			return(topRight);
		} else if (bottomLeft != null) {
			return(bottomLeft);
		} else if (bottomRight != null) {
			return(bottomRight);
		} 
		return (null);
	}
	//signals the end of the turn 
	private boolean endTurn() {
			remove(ball);
			remove(paddle);
			return(false);
	}
	//canvas wall constraints 
		private boolean reachBottom (GOval ball) {
			return ball.getY() > getHeight() - BALL_RADIUS;
		}
		private boolean reachTop (GOval ball) {
			return ball.getY() < 0;
		}
		private boolean reachLeft (GOval ball) {
			return ball.getX() < 0;
		}
		private boolean reachRight (GOval ball) {
			return ball.getX() > getWidth() - BALL_RADIUS;
		}
		
		//ball bounces back if it bounced off a wall 
		public void collidingWall() {
			if (reachTop(ball)) {
				vy = -vy;
			}
			if (reachRight(ball) || reachLeft(ball)) {
				vx = -vx;
			}
			if (reachBottom(ball)) {
				endTurn();
			}
		}
	//ball moves around the canvas
	public void animateBall() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		waitForClick();
		while(true) {
			GObject collider = getCollidingObject();
			if (collider != null) {
				if (collider == paddle) {
					vy = -vy;
				} else {
					remove(collider);
					vy = -vy;
					brickCount = brickCount - 1;
				}
			}
			collidingWall();
			ball.move(vx, vy);
			pause(DELAY);
			if (brickCount == 0) {
				endTurn();
			}
		}
	}
	//creates a ball in the center of the canvas
	private GOval setBall() {
		double ballX = ((getWidth() / 2.0 - BALL_RADIUS / 2.0));
		double ballY = ((getHeight()/ 2.0 - BALL_RADIUS));
		GOval ball = new GOval(ballX, ballY, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		return(ball);
	}
	//allows the paddle to track the mouse 
	public void mouseMoved(MouseEvent e) {
		double x = ((getWidth() - PADDLE_WIDTH) / 2.0);
		double y = (getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT) / 2.0);
		add(paddle);
		x = e.getX() - PADDLE_WIDTH/ 2.0;
		y = (getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT) / 2.0);
		paddle.setLocation(x, y);
	}
	//creates a paddle
		private GRect getPaddle() {
			GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
			paddle.setFilled(true);
			return paddle;
		}
	//puts the bricks in the world 
	public void setBricks() {
		getWidth();
		getHeight();
		//Setting up the first column of bricks 
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			int rowNum = i + 1;
			double yStart = BRICK_Y_OFFSET + (rowNum - 1.0) * (BRICK_HEIGHT + BRICK_SEP);
			double xStart = getWidth() / 2.0 +(BRICK_SEP/2.0) - ((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS / 2.0);
			GRect rect = new GRect (xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
			add(rect);
			rect.setFilled(true);
			brickCount++;
			int rowColor = rowNum % 10;
			//filling in rows of colored bricks 
			int colNum = 1;
			if (rowColor == 1 || rowColor == 2) {
				rect.setColor(Color.RED);
				while (colNum < NBRICK_COLUMNS) {
					xStart = xStart + (BRICK_WIDTH + BRICK_SEP);
					GRect Nextrect = new GRect (xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
					add(Nextrect); 
					Nextrect.setFilled(true);
					Nextrect.setColor(Color.RED);
					colNum++;
					brickCount++;
				}
			} else if (rowColor == 3 || rowColor == 4) {
				rect.setColor(Color.ORANGE);
				while (colNum < NBRICK_COLUMNS) {
					xStart = xStart + (BRICK_WIDTH + BRICK_SEP);
					GRect Nextrect = new GRect (xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
					add(Nextrect); 
					Nextrect.setFilled(true);
					Nextrect.setColor(Color.ORANGE);
					colNum++;
					brickCount++;
				}
			} else if (rowColor == 5 || rowColor == 6) {
				rect.setColor(Color.YELLOW);
				while (colNum < NBRICK_COLUMNS) {
					xStart = xStart + (BRICK_WIDTH + BRICK_SEP);
					GRect Nextrect = new GRect (xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
					add(Nextrect); 
					Nextrect.setFilled(true);
					Nextrect.setColor(Color.YELLOW);
					colNum++;
					brickCount++;
				}
			} else if (rowColor == 7 || rowColor == 8) {
				rect.setColor(Color.GREEN);
				while (colNum < NBRICK_COLUMNS) {
					xStart = xStart + (BRICK_WIDTH + BRICK_SEP);
					GRect Nextrect = new GRect (xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
					add(Nextrect); 
					Nextrect.setFilled(true);
					Nextrect.setColor(Color.GREEN);
					colNum++;
					brickCount++;
				}
			} else if (rowColor == 9 || rowColor == 0) {
				rect.setColor(Color.CYAN);
				while (colNum < NBRICK_COLUMNS) {
					xStart = xStart + (BRICK_WIDTH + BRICK_SEP);
					GRect Nextrect = new GRect (xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
					add(Nextrect); 
					Nextrect.setFilled(true);
					Nextrect.setColor(Color.CYAN);
					colNum++;
					brickCount++;
				}
			}
		}
	}
}
