/*
 * File: Breakout.java
 * -------------------
 * Name: Yu Jin Lee
 * Section Leader: Maggie Davis
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

	// Counter of the remaining bricks
	public int NBRICK = NBRICK_COLUMNS * NBRICK_ROWS;
	
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

	//Random number generator for the ball's velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	//Define the ball
	double size = BALL_RADIUS * 2.0;
	GOval ball = new GOval (size, size);

	//coordinates of the ball
	double top;
	double left;

	// The ball's initial vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity .
	public static final double VELOCITY_X_MIN = 2.0;
	public static final double VELOCITY_X_MAX = 4.0;

	//The ball's velocity.
	public double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	public double vy = VELOCITY_Y;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Define paddle and its location
	public double paddleX;
	GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		addMouseListeners();
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		drawBricks();
		makeBall();
		waitForClick();
		//the ball's initial direction is chosen randomly
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		
		//loops while there are remaining bricks and the ball doesn't hit the bottom wall.
		while ( NBRICK != 0 && top <= getHeight() - size) {
			//coordinates of the ball
			left = ball.getX();
			top = ball.getY();

			GObject object = getCollidingObjects(left, top);
			if (object != null) {
				//the object is a BRICK
				if (object != paddle) {
					remove (object);
					//counter
					 NBRICK = NBRICK -1;
					vx = -vx;
					vy = -vy;
				}
				//the ball hit the paddle
				else {
					vy = -vy;
				}
			}
			dontHitWalls();
			ball.move(vx, vy);
			pause(DELAY);
			
		}
		if (NBRICK == 0) {
			println("BREAK OUT WAS SUCCESSFUL!!!! CONGRATULATIONS!!!");
		}
		else if (top >= getHeight() - size) {
			println("Sorry. Try again");
		}
		
	}

	//The velocities of the ball will change if it hits the walls
	private void dontHitWalls() {
		//if the ball hits the walls on the top 
		if ( top <= 0 ) {
			vy = -vy;
		}
		//if the ball hits the side walls
		if (left >= getWidth() - size || left <= 0 ) {
			vx = -vx;
		}

	}

	//If there is collision in any of the four corners of the ball, the object at the corner is returned 
	private GObject getCollidingObjects( double left, double top) {
		double right = left + size;
		double bottom = top + size;
		GObject corner_1 = getElementAt(left, top);
		GObject corner_2 = getElementAt(left, bottom);
		GObject corner_3 = getElementAt(right, top);
		GObject corner_4 = getElementAt(right, bottom);

		if( corner_1 != null) {
			return corner_1;
		}
		if( corner_2 != null) {
			return corner_2;
		}
		if( corner_3 != null) {
			return corner_3;
		}
		if( corner_4 != null) {
			return corner_4;
		}
		return null;

	}

	private void makeBall() {
		//initial coordinates of the ball; 
		double leftBall = getWidth()/2.0 - BALL_RADIUS;
		double topBall = getHeight()/2.0 - BALL_RADIUS;
		ball.setFilled(true);
		add(ball, leftBall, topBall);
	}

	private void drawBricks() {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			//y coordinates move away one row at a time from the top 
			double brickY = BRICK_Y_OFFSET + i * (BRICK_HEIGHT+BRICK_SEP);

			//draws a row of bricks
			for (int j = 0; j < NBRICK_ROWS; j++) {
				double brickX = (getWidth()- (BRICK_WIDTH * NBRICK_ROWS + BRICK_SEP * (NBRICK_ROWS - 1)))/2.0 + ((BRICK_WIDTH + BRICK_SEP) * j);
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				//changes colors of the bricks of each row in a rainbow scheme.
				if (i%10 <= 1) {
					brick.setColor(Color.RED);
				}
				else if (i%10 <= 3) {
					brick.setColor(Color.ORANGE);
				}
				else if (i%10 <= 5) {
					brick.setColor(Color.YELLOW);
				}
				else if (i%10 <= 7) {
					brick.setColor(Color.GREEN);
				}
				else if (i%10 <= 9) {
					brick.setColor(Color.CYAN);
				}
				add (brick, brickX, brickY);

			}

		}

	}


	public void mouseMoved(MouseEvent e) {
		paddleX = e.getX() - PADDLE_WIDTH/2.0; 
		//if the mouse moves outside the screen on the right side
		if (e.getX() > getWidth() - PADDLE_WIDTH/2.0) {
			paddleX = getWidth()-PADDLE_WIDTH;
		}
		//if the mouse moves outside to the left
		if (e.getX() < PADDLE_WIDTH/2.0) {
			paddleX = 0;
		}
		paddle.setFilled(true);
		add (paddle, paddleX, getHeight()-PADDLE_Y_OFFSET); 	

	}

}
