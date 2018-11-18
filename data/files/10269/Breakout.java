/*
 * File: Breakout.java
 * -------------------
 * Name: Meghana Reddy
 * Section Leader: Cat Xu
 * 
 * This file will eventually implement the game of Breakout.
 * 
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
	
	private double vx;
	private double vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GRect paddle;
	private GOval ball;
	
	public void mouseMoved(MouseEvent e) {
		double xCoord = e.getX();
		if (xCoord < CANVAS_WIDTH-PADDLE_WIDTH) {
			paddle.setLocation(xCoord, CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	private GObject getCollidingObject() {
		GObject obj = getElementAt(ball.getX(), ball.getY());
		if (obj != null) return obj;
		obj = getElementAt(ball.getRightX(), ball.getY());
		if (obj != null) return obj;
		obj = getElementAt(ball.getX(), ball.getBottomY());
		if (obj != null) return obj;
		obj = getElementAt(ball.getRightX(), ball.getBottomY());
		return obj;
	}
	private int brickCounter = NBRICK_COLUMNS*NBRICK_ROWS;
	
	//This creates the bricks
	private void createbricks() {
	double xStart = BRICK_SEP;
	double yStart = BRICK_Y_OFFSET;
	for (int j = 0; j < NBRICK_ROWS; j++) {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			GRect rect = new GRect (xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
			rect.setFilled(true);
			if (j < 2) {
				rect.setColor(Color.RED);
			} else if (j < 4) {
				rect.setColor(Color.ORANGE);
			} else if (j < 6) {
				rect.setColor(Color.YELLOW);
			} else if (j < 8) {
				rect.setColor(Color.GREEN);
			} else {
				rect.setColor(Color.CYAN);
			}
			add(rect);
			xStart = xStart + BRICK_WIDTH + BRICK_SEP;
		}
		xStart = BRICK_SEP;
		yStart = yStart + BRICK_HEIGHT + BRICK_SEP;
	}
	}
	// This sets the paddle position.
	private void createpaddle() {
	paddle = new GRect(CANVAS_WIDTH/2 - PADDLE_WIDTH/2, CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
	paddle.setFilled(true);
	paddle.setColor(Color.BLACK);
	add(paddle);
	}
	
	public void run() {
		
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();

		createbricks();
		createpaddle();
	
		
		// This sets the ball position.
		ball = new GOval(CANVAS_WIDTH/2-BALL_RADIUS, CANVAS_HEIGHT/2-BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);				
		ball.setColor(Color.red);
		ball.setFillColor(Color.RED);
		ball.setFilled(true);
		add(ball);

		// Starting velocity for ball.
		vx = rgen.nextDouble(1.0, 3.0);
		vy = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))  vx = -vx;
		pause(1000);
		
		// This checks all four corners of the ball to see if it is touching an object.//
		while (true) {
			
			// Wall collisions. This will cause the ball to change direction or break.
			if (ball.getRightX() > CANVAS_WIDTH || ball.getX() < 0) {
				vx = -vx;
			} 
			if (ball.getY() < 0) {
				vy = -vy;
			}
			if (ball.getY() > CANVAS_HEIGHT - PADDLE_Y_OFFSET) {
				remove(ball);
				break;
			}
			
			// Object collisions.
			GObject obj = getCollidingObject();
			if (obj == paddle) {
				vy = -vy;
			} else if (obj != null) {
				remove(obj);
				vy = -vy;
			}
			
			ball.move(vx, vy);
			pause(DELAY);
		}
	}
}



