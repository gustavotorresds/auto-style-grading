/*
 * File: Breakout.java
 * -------------------
 * Name: Shayla Harris
 * Section Leader: Cat
 * 
 * This file will implement the game of Breakout.
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
	
	// Adding paddle as an instance variable, because the whole program needs to see it
	private GRect paddle = null;
	
	// Adding the ball as an instance variable.
	private GOval ball = null;
	
	// Adding the ball's velocity as an instance variable.
	private double vx, vy;
	
	//Adding an instance variable to create random numbers
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// make one row of bricks
		for (int i = 0; i< NBRICK_COLUMNS; i++) {
			for (int j = 0; j<NBRICK_ROWS; j++) {
				// if NCOLUMNS is even, then we can position the "middle" brick on getWidth()/2 plus half a brick separation space.
				// But 
				// if NCOLUMNS is odd, then the middle brick needs to be positioned at getWidth()/2 - half a brick width.
				// I don't know why it won't let me make xVar conditional on number of columns. I'll just pretend we know
				// that xVar has an even number of columns.
				// From there, we can derive the positions of the other bricks. 
				
				double xVar = getWidth()/2 - NBRICK_COLUMNS/2*BRICK_WIDTH - (NBRICK_COLUMNS/2 - 1)*BRICK_SEP - BRICK_SEP/2 + (BRICK_WIDTH + BRICK_SEP)*i;
				if (NBRICK_COLUMNS%2 == 1) {
					xVar = getWidth()/2 - NBRICK_COLUMNS/2.0*BRICK_WIDTH - NBRICK_COLUMNS/2*BRICK_SEP  + (BRICK_WIDTH + BRICK_SEP)*i;
				}

				double yVar = BRICK_Y_OFFSET + j*(BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect (xVar, yVar, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (j<2) {
					brick.setColor(Color.RED);
				}
				if (j==2||j==3) {
					brick.setColor(Color.ORANGE);
				}
				if (j==4||j==5) {
					brick.setColor(Color.YELLOW);
				}
				if (j==6||j==7) {
					brick.setColor(Color.GREEN);
				}
				if (j>7) {
					brick.setColor(Color.CYAN);
				}
				add (brick);
			}
		}	
		// Time to make the paddle.
		makePaddle();
		
		// Time to make it track the mouse.
		addMouseListeners();
		
		// Time to make the ball.
		makeBall();

		// Time to make the ball move.
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		int lives = NTURNS;
		int bricksHit = 0;
		while (true) {
			ball.move(vx,  vy);
			if (isAtTop(ball)) {
				vy = -vy;
			}
			if (isAtLeftWall(ball)||isAtRightWall(ball)) {
				vx = -vx;
			}
			GObject collider = getCollidingObject();
			if (collider == paddle && ball.getY() <= getHeight()-PADDLE_Y_OFFSET-BALL_RADIUS*2) {
				// This tells us that if it hits the paddle, we should bounce off it, but only if the bottom 
				// of the ball is not already below the paddle. If the bottom is already below the paddle,
				// the ball should not bounce off the paddle. This way, I avoid the bug where the ball
				// gets stuck inside the paddle.
				vy = -vy;
			}
			if (collider != null && collider != paddle) {
				// If collider is not null and not the paddle, it must be a brick, so we should
				// bounce, remove the brick, and add to our total count of bricks hit. 
				vy = -vy;
				remove (collider);
				bricksHit++;
			}
			if (bricksHit == NBRICK_ROWS*NBRICK_COLUMNS) {
				//If the number of bricks hit by the ball equals the number of total bricks,
				// we can exit this loop, and the game is won.
				break;
			}
			String str = "You lost a life. Lives left: " + (lives-1);
			GLabel livesLeft = new GLabel (str);
			if (isAtBottom(ball)) {
				lives = lives - 1;
				remove (ball);
					if (lives < 1) {
						// If we have used up all our lives, we can exit the loop, and the game is lost.
						break;
					}
				// This adds a temporary label displaying the number of lives left. 
				add (livesLeft, getWidth()/2-livesLeft.getWidth()/2, getHeight()/2);
				pause (100000.0/60.0);
				remove (livesLeft);
				pause (50000.0/60.0);
				makeBall();	
				pause(50000.0/60.0);
			}
			pause(DELAY);
		}
		if (bricksHit == NBRICK_ROWS*NBRICK_COLUMNS) {
			// If the winning condition is met, this displays a label saying "You Win!"
			String youWin = "You Win!";
			GLabel win = new GLabel (youWin);
			add (win, getWidth()/2-win.getWidth()/2, getHeight()/2-win.getHeight()/2);
		} else {
			// If the losing condition is met, this displays a label saying "Game Over"
			String gameOver = "Game Over";
			GLabel end = new GLabel (gameOver);
			add (end, getWidth()/2-end.getWidth()/2, getHeight()/2-end.getHeight()/2);
		}
	}
	
	
	private void makePaddle() {
		paddle = new GRect ((getWidth()-PADDLE_WIDTH)/2, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);
	}
	
	public void mouseMoved(MouseEvent e) {
		double paddleX = e.getX();
		// to cover the case where the mouse might cause part of the paddle to go off screen
		if (paddleX > getWidth()-PADDLE_WIDTH) {
			paddleX = getWidth()-PADDLE_WIDTH;
		}
		double paddleY = getHeight()-PADDLE_Y_OFFSET;
		paddle.setLocation(paddleX, paddleY);
	}
	
	private void makeBall() {
		ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		// To center the ball, divide width in two and subtract radius. Divide height in two and subtract diameter.
		add (ball, getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS*2);
		
	}
	
	private boolean isAtBottom(GOval ball) {
		if (ball.getY() >= getHeight()) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isAtTop(GOval ball) {
		if (ball.getY() <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isAtLeftWall (GOval ball) {
		if (ball.getX()<=0 ) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isAtRightWall (GOval ball) {
		if (ball.getX()>=getWidth()-BALL_RADIUS*2) {
			return true;
		} else {
			return false;
		}
	}
	
	private GObject getCollidingObject() {
		// This checks the four points at the "corners" of the ball, and creates an object, collider,
		// which is the object found at the point. As soon as collider is not null, it returns collider.
		// If collider is null for all four points, it returns null.
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider != null) {
			return collider;
		} else {
			collider = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY());
		}
		if (collider != null) {
			return collider;
		} else {
			collider = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2);
		}
		if (collider != null) {
			return collider;
		} else {
			collider = getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2);
		}
		return collider;
	}

}
