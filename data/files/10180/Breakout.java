/*
 * File: Breakout.java
 * -------------------
 * Name: Kai Ping Tien (Hiro)
 * Section Leader: Andrew
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

	// instance variable for random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// instance variable of x and y for velocity
	private double vx, vy;

	// Indicate total number of bricks available
	private int bricksLeft = 100;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		buildBricks();
		for(int i=0; i<NTURNS;i++) {
			buildPaddle();
			buildBall();
			startGame();
			if(bricksLeft == 0) {
				postWinGame();
			} 
			remove(paddle);
			remove(ball);
		}
		if(bricksLeft > 0) { 
			postLostGame();
		}
	}


	// builds bricks by using the given numober of rows and columns
	// and given constants for brick sizes. also fill in colors
	// for different rows
	private GRect bricks;
	private void buildBricks() {
		for (int row=0; row < NBRICK_ROWS; row++) {

			for(int col=0; col < NBRICK_COLUMNS; col++) {
				double x = getWidth()/2 - ((NBRICK_COLUMNS*BRICK_WIDTH)/2.0) - ((NBRICK_COLUMNS*BRICK_SEP)/2.0) + (col*BRICK_WIDTH) + col*BRICK_SEP;
				double y = BRICK_Y_OFFSET + (row*BRICK_HEIGHT) + row*BRICK_SEP;
				bricks = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				add(bricks);
				bricks.setFilled(true);
				if (row == 0) {
					bricks.setColor(Color.RED);
				}
				if (row == 1) {
					bricks.setColor(Color.RED);
				}
				if (row == 2) {
					bricks.setColor(Color.ORANGE);
				}
				if (row == 3) {
					bricks.setColor(Color.ORANGE);
				}
				if (row == 4) {
					bricks.setColor(Color.YELLOW);
				}
				if (row == 5) {
					bricks.setColor(Color.YELLOW);
				}
				if (row == 6) {
					bricks.setColor(Color.GREEN);
				}
				if (row == 7) {
					bricks.setColor(Color.GREEN);
				}
				if (row == 8) {
					bricks.setColor(Color.CYAN);
				}
				if (row == 9) {
					bricks.setColor(Color.CYAN);
				}

			}
		}
	}	

	// create a paddle, with mouselistener included
	private GRect paddle;
	private void buildPaddle() {
		double x = getWidth()/2 - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		add(paddle);
		paddle.setFilled(true);
		addMouseListeners();
	}
	// tracks x and y of mouse and makes sure paddle don't go out of window
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = getHeight();
		if((e.getX() < getWidth() - PADDLE_WIDTH/2))
			if((e.getX() > PADDLE_WIDTH/2))
				paddle.setLocation(x-PADDLE_WIDTH/2,y-PADDLE_Y_OFFSET-PADDLE_HEIGHT);	
	}
	// create a ball
	private GOval ball;
	private void buildBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x,y,BALL_RADIUS,BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	private void startGame() {
		waitForClick();
		moveBall();
	}
	// determine ball speed and random left/right movement
	private void moveBall() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		while(true) {
			// update velocity when hitting top, right and left walls
			if(isTopWall(ball)) {
				vy = -vy;
			}
			if(isRightWall(ball) || isLeftWall(ball)) {
				vx = -vx;
			}
			ball.move(vx, vy);
			pause(DELAY);

			// if colliding with paddle, reverse motion or else remove
			// object (brick) and reverse motion
			// if hit bottom wall, break loop
			// if bricks run out, break loop too
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2) { 
					vy = -vy;
				} 
			} else if (collider != null)  {
				remove (collider);
				bricksLeft --;
				vy = -vy;
			}

			if(isBottomWall(ball)) {
				break;
			}
			if(bricksLeft == 0) {
				break;
			}
		}
	}
	// create method for colliding object
	// gotta make sure all four corners are accounted for
	private GObject getCollidingObject() {
		if((getElementAt(ball.getX(),ball.getY())) !=null) {
			return getElementAt(ball.getX(),ball.getY());
		}
		if((getElementAt(ball.getX() + 2*BALL_RADIUS,ball.getY())) !=null) {
			return getElementAt(ball.getX(),ball.getY());
		}
		if((getElementAt(ball.getX(),ball.getY() + 2*BALL_RADIUS)) !=null) {
			return getElementAt(ball.getX(),ball.getY());
		}
		if((getElementAt(ball.getX() + 2*BALL_RADIUS,ball.getY() + 2*BALL_RADIUS)) !=null) {
			return getElementAt(ball.getX(),ball.getY());
		}

		return null;
	}


	// method for publishing Game Over!! message when lost
	private void postLostGame() {
		GLabel postLostGame = new GLabel ("Game Over!!");
		postLostGame.setFont("SansSerif-28");
		double xl = (getWidth()/2 - postLostGame.getWidth()/2);
		double yl = (getHeight()/2 - postLostGame.getAscent()/2);
		postLostGame.setColor(Color.RED);
		postLostGame.setLocation(xl,yl);
		add(postLostGame);
	}
	// method for publishing Congratulations!! message when won
	private void postWinGame() {
		GLabel postWinGame = new GLabel ("CONGRATULATIONS!!!");
		postWinGame.setFont("SansSerif-28");
		double xl = (getWidth()/2 - postWinGame.getWidth()/2);
		double yl = (getHeight()/2 - postWinGame.getAscent()/2);
		postWinGame.setColor(Color.GREEN);
		postWinGame.setLocation(xl,yl);
		add(postWinGame);
	}

	// boolean for hitting walls
	private boolean isTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	private boolean isRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}
	private boolean isLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	private boolean isBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

}



