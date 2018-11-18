/*
 * File: Breakout.java
 * -------------------
 * Name: Vineet Gupta
 * Section Leader: Chase Davis
 * 
 * This file implements the game of Breakout. Generally, we assume that
 * the player does not alter the size of the game window.
 * 
 * Note: We removed the paddle glitch by insisting that the ball can only collide
 * with the paddle from above. This does still result in a minor visual glitch that the ball
 * can be overlapping the side of the paddle slightly, but otherwise the game fully functions.
 * To compensate, I reduced the paddle height from 10 to 6, so this error is barely visible.
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
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 6;

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
	
	// Creates a paddle and a ball.
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	private GOval ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
	
	// Generates a random number. 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Ball velocities.
	private double vx, vy;
	
	// Initiate brick counter.
	private int brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;
	
	// Win Checker.
	private boolean win = false;
	
	public void run() {
		
		// Build world components.
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		makeAllBricks();
		makePaddle();
		
		// Game play.
		for (int i=0; i < NTURNS; i++) {
			makeBall();
			initializeBallVelocity();
			while(turnInPlay()) { 
				ballMovement();
			}	
			if (win) { 
				break;
			}
			remove(ball);
		}
		
		// Post game play. 
		displayGameOver();
	}
	
	private void initializeBallVelocity() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}
	
	private void makeBall() {
		ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
		ball.setFillColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
	}
	
	
	/* This method introduces all the bricks on the screen, 
	 * specified by the number of rows and columns above. It 
	 * relies on the two methods directly following it. */	
	private void makeAllBricks() { 
		Color color = null;
		double y = BRICK_Y_OFFSET;
		for (int i=0; i < NBRICK_ROWS; i++) {
			// sets color so that each fifth rows of bricks are different color
			double colorcounter = Math.floor(i * 5/NBRICK_ROWS);
			if (colorcounter==0) {
				color = Color.RED;
			} else if (colorcounter==1) {
				color = Color.ORANGE;
			} else if (colorcounter==2) {
				color = Color.YELLOW; 
			} else if (colorcounter==3) {
				color = Color.GREEN; 
			} else if (colorcounter==4) {
				color = Color.CYAN;
			}
			makeRowOfBricks(color, y);
			y = y + BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	/* This method makes a row of bricks (centered on the screen) 
	 * of a specified color at a specified height.  */
	private void makeRowOfBricks(Color color, double y) {
		
		// offset x coordinate of initial brick to center row
		double initial_x = 0.5 * (getWidth() - (NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) - BRICK_SEP));
		
		double x = initial_x;
		for(int i=0; i < NBRICK_COLUMNS; i++) {
			makeBrick(color, x, y);
			x = x + (BRICK_WIDTH + BRICK_SEP);
		}
	}
	
	/* This method makes a single brick of a specified color
	 * at a specified location.	 */
	private void makeBrick(Color color, double x, double y) {
		GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFillColor(color);
			brick.setFilled(true);
			brick.setColor(color);
			brick.setLocation(x,y);;
		add(brick);
	}

	/* This method makes the paddle. */
	private void makePaddle() {
		paddle.setFillColor(Color.BLACK);
		paddle.setFilled(true);
		// sets paddle location in the center at the specified height
		paddle.setLocation(getWidth()/2 - paddle.getWidth()/2, getHeight() - PADDLE_Y_OFFSET);
		add(paddle);
		addMouseListeners();
	}
	
	/* This method moves the paddle in sync with the mouse. */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		paddle.setLocation(mouseX - paddle.getWidth()/2, getHeight() - PADDLE_Y_OFFSET);
		add(paddle);
	}
	
	/* This method keeps the ball moving and reverses the ball 
	 * during collisions. */
	private void ballMovement() {
		ball.setLocation(ball.getX() + vx, ball.getY() + vy);
		bounceOffWalls();
		collisions();
		pause(DELAY);
	}
	
	/* This method alters the ball's velocity when it hits a wall. */
	private void bounceOffWalls() {
		if (ball.getX() < 0 || ball.getX() + 2 * BALL_RADIUS > getWidth()) {
			vx = -vx;
		}
		if (ball.getY() < 0) {
			vy = -vy;
		}
	}
	
	/* This method alters the ball's velocity in case of collision,
	 * and it removes and tracks any bricks that collide with the ball. */
	private void collisions() {
		GObject collider = getCollidingObject();
		if (collider==paddle) {
			vy=-vy;
		} else if (collider!=null) {
			remove(collider);
			brickCounter = brickCounter - 1;
			vy=-vy;
		}
	}
	
	/* This method returns the object colliding with the ball,
	 * or null. The only exception is that the paddle can only
	 * collide with the bottom of the ball. */
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		double r = BALL_RADIUS;
		
		GObject topLeftCollider = getElementAt(x,y);
		GObject topRightCollider = getElementAt(x+2*r,y);
		GObject bottomLeftCollider = getElementAt(x,y+2*r);
		GObject bottomRightCollider = getElementAt(x+2*r,y+2*r);
		
		if (topLeftCollider!=null && topLeftCollider!=paddle) {
			return topLeftCollider;
		} else if (topRightCollider!=null && topRightCollider!=paddle) {
			return topRightCollider;
		} else if (bottomLeftCollider!=null) {
			return bottomLeftCollider;
		} else if (bottomRightCollider!=null) {
			return bottomRightCollider;
		} else {
			return null;
		}
	}
	
	/* This method ensures the game is still running. */ 
	private boolean turnInPlay() {
		if (brickCounter==0) {
			win = true;
		}
		// terminating conditions are the ball hitting the bottom wall, or all bricks have been removed.
		return ball.getY() + 2 * BALL_RADIUS < getHeight() 
				&& brickCounter > 0	; 
	}
	
	// Display game over message.
	private void displayGameOver() {
		GLabel gameOver = new GLabel("");
		gameOver.setFont("Courier-48");
		if (win) {
			gameOver.setLabel("You Won!");
		} else {
			gameOver.setLabel("You lost.");
		}
		gameOver.setLocation(getWidth()/2 - gameOver.getWidth()/2, getHeight()/2 + gameOver.getHeight()/2);
		add(gameOver);
	}
}
