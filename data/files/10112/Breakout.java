/*
 * File: Breakout.java
 * -------------------
 * Name: Amelia O'Donohue
 * Section Leader: Marilyn Zhang
 * 
 * This file runs the game Breakout. In this game, the ball moves around the world, bouncing off of the
 * walls. When the ball hits a brick, it removes the brick. When it hits the paddle at the bottom of the
 * screen, the ball changes direction. The turn ends when either all of the bricks have been removed or
 * when the ball hits the bottom wall. The player has 3 turns.
 * 
 * Sources: Java Library, Assignment Handout, lecture Examples
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
	
	private GRect paddle = null;
	private GOval ball = null;	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy;

	public void run() {
		// Sets the window's title bar text
		setTitle("CS 106A Breakout");

		// Sets the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
			addMouseListeners();
			setUpBricks();
			paddle = makePaddle();
			// gives the player three turns
			for (int i = 0; i < NTURNS; i++) {
				ball = makeBall();
				moveBall();
				remove(ball);
				pause(DELAY);
			}
	}
	
	/*
	 * This method moves the ball around the window. The ball's x and y velocities are first set. 
	 * Then, if the ball runs into something (either a wall or the paddle or a brick), the x or y
	 * velocity becomes negative so that the ball changes directions. The ball stops moving when
	 * either the ball hits the bottom wall or the ball hits all of the bricks.
	 * 
	 */
	private void moveBall() {
		// finds random velocity in x direction between given velocity minimum and maximum
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		int NumBricks = NBRICK_ROWS * NBRICK_COLUMNS;
		while(hitBottomWall() == false && NumBricks != 0) {
			ball.move(vx, vy);
			GObject collider = getCollidingObject();
			if (collider != null) {
				// if the collider is a brick, brick is removed. 1 is subtracted from the number of bricks 
				if (collider != paddle) {
					vy = -vy;
					remove(collider);
					NumBricks = NumBricks -1;
				} else {
					vy = -Math.abs(vy);
				}
			}
			// if hits left or right wall, bounces in opposite direction
			if (hitLeftWall() == true || hitRightWall() == true) {
				vx = -vx;
			}
			// if hits top wall, bounces in opposite direction
			if (hitTopWall() == true) {
				vy = -vy;
			}
			pause (3*DELAY);
		}
	}
	
	
	/* 
	 * This method checks each corner of the ball to see if there is an object there. It returns the
	 * collider with the value null or, depending on the Y coordinate, paddle or not paddle.
	 * 
	 */
	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		double ballRightX = ball.getX() + 2*BALL_RADIUS;
		double ballBottomY = ball.getY() + 2*BALL_RADIUS;
		if (collider == null) {
			// check at top right corner of ball
			collider = getElementAt(ballRightX, ball.getY());
		}
		if (collider == null) {
			// check at bottom left corner of ball
			collider = getElementAt(ball.getX(), ballBottomY);
		}
		if (collider == null) {
			// check at bottom right corner of ball
			collider = getElementAt(ballRightX, ballBottomY);
		}
		return collider;
	}
	

	/*
	 * This method tells you whether or not the given ball has hit the right wall of the window.
	 */
	private boolean hitRightWall() {
		if (ball.getX() < getWidth() - 2*BALL_RADIUS) {
			return false;
		} else {
			return true;
		}	
	}
	
	
	/*
	 * This method tells you whether or not the given ball has hit the left wall of the window.
	 */
	private boolean hitLeftWall() {
		if (ball.getX() > 0) {
			return false;
		} else {
			return true;
		}
	}
		
	/*
	 * This method tells you whether or not the given ball has hit the top wall of the window.
	 */
	private boolean hitTopWall() {
		if (ball.getY() > BALL_RADIUS) {
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * This method tells you whether or not the given ball has hit the bottom wall of the window.
	 */
	private boolean hitBottomWall() {
		if (ball.getY() < getHeight() - BALL_RADIUS) {
			return false;
		} else {
			return true;
		}
	}

	
	/*
	 * This method makes a ball filled black with the given ball radius.
	 */
	private GOval makeBall() {
		GOval ball = new GOval (2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		double ballX = (getWidth() / 2) - BALL_RADIUS;
		double ballY = (getHeight()/2) - BALL_RADIUS;
		add (ball, ballX, ballY);
		return ball;		
	}
	
	/* This method, called any time the mouse moves on the program screen, makes a paddle that
	 * has the same x coordinate as the mouse's location. The y coordinate is fixed.
	 * If statement makes sure that the entire paddle is on the screen at all times by limiting
	 * x coordinate size.
	 */
	public void mouseMoved(MouseEvent e) {
		double paddleX = e.getX();
		if (e.getX() > getWidth() - PADDLE_WIDTH) {
			paddleX = getWidth() - PADDLE_WIDTH;
		}
		double paddleY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add (paddle, paddleX, paddleY);
	}
		
		
	/* This method makes the paddle. */
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		return paddle;
	}
		
	/* This method makes NBRICK_ROWS rows with equal number of blocks. */
	private void setUpBricks() {
		// makes each row
		int rowNum = 0;
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			int bricksMade = 0;
			// makes each brick
			for (int l = 0; l < NBRICK_ROWS; l++) {
				buildBrick(bricksMade, rowNum);	
				bricksMade = bricksMade + 1;
			}
			rowNum = rowNum + 1;
		}			
	}
		
	/* This method finds the X and Y values for each brick, then makes the brick */
	private void buildBrick(int bricksMade, int rowNum) {
		double X = findX(bricksMade);
		double Y = findY(rowNum);
		makeBrick(X, Y, rowNum);
	}
		
	/* This method makes one brick given X and Y values and fills it a specific color */
	private void makeBrick(double X, double Y, int rowNum) {
		GRect brick = new GRect(X, Y, BRICK_WIDTH, BRICK_HEIGHT);
		Color color = findColor(rowNum);
		brick.setColor(color);
		brick.setFilled(true);
		add (brick);
	}
		
	/* This method finds the X value of each brick */
	private double findX(int bricksMade) {
		double centerX = getWidth()/2;
		double startingX = centerX - ((BRICK_WIDTH + (BRICK_SEP/2)) * (NBRICK_COLUMNS/2));
		double X = startingX + (bricksMade * (BRICK_WIDTH + (BRICK_SEP/2)));
		return X;	
	}
		
	/* This method finds Y value of each brick */
	private double findY(int rowNum) {
		double startingY = BRICK_Y_OFFSET;
		double Y = startingY + (rowNum * (BRICK_HEIGHT + (BRICK_SEP/2)));
		return Y;
	}
		
	/* This method finds the color for each brick based on its row number (rowNum) */
	private Color findColor(int rowNum) {
		if (rowNum == 0 || rowNum ==1) {
			Color color = Color.RED;
			return color;
		}
		if (rowNum == 2 || rowNum ==3) {
			Color color = Color.ORANGE;
			return color;
		}	
		if (rowNum == 4 || rowNum == 5) {
			Color color = Color.YELLOW;
			return color;
		}
		if (rowNum == 6 || rowNum == 7) {
			Color color = Color.GREEN;
			return color;		
		} else {
			Color color = Color.CYAN;
			return color;
		}
	}

}
