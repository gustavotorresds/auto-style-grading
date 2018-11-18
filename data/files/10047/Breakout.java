/*
 * File: Breakout.java
 * -------------------
 * Name: Sam Jones
 * Section Leader: Garrick
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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// constants on number of colored rows
	public static final int RED_ROWS = NBRICK_ROWS/5 * 1;
	public static final int ORANGE_ROWS = NBRICK_ROWS/5 * 2;
	public static final int YELLOW_ROWS = NBRICK_ROWS/5 * 3;
	public static final int GREEN_ROWS = NBRICK_ROWS/5 * 4;
	public static final int CYAN_ROWS = NBRICK_ROWS/5 * 5;
	
	// Instance variables: blocks, paddle, rgen, ball velocity, etc.
	private GRect paddle = null;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	
	// Run method to setup the game - see game play beginning with the next run method
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		addBlocks();
		addPaddle();
		// initialize vx
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		// initialize vy
		vy = VELOCITY_Y;
		// set number of bricks
		int numBricks = NBRICK_ROWS * NBRICK_COLUMNS;

		// while loop animation for game, end if turns run out
		int turns = NTURNS;
		while (turns > 0 && numBricks > 0) { 
			// add ball to begin the game
			addBall();
			// return instructions for how to start and turns remaining
			GLabel turnsRemaining = new GLabel(turns + " chance(s) remaining");
			add(turnsRemaining, (getWidth()/2 - turnsRemaining.getWidth()/2), (getHeight()/2 - turnsRemaining.getHeight()*2));
			GLabel instructionsLab = new GLabel("Click anywhere to begin the game");
			// instructions label changed manually to look better aesthetically 
			add(instructionsLab, (getWidth()/2 - instructionsLab.getWidth()/2), (getHeight()/2 - instructionsLab.getHeight()));
			waitForClick();
			// while loop for single turn, end if numBricks = 0 or loses game
			while(numBricks > 0 && hitBottomWall(ball) == false) {
				// remove label
				remove(instructionsLab);
				remove(turnsRemaining);
				// bounce off 3 walls only
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				}
				if(hitTopWall(ball)) {
					vy = -vy;
				}
				// remove blocks and reverse directions if hit block, reverse if hitting paddle
				GObject collider = getCollidingObject(ball);
				if(collider != null && collider != paddle) {
					remove(collider);
					vy = -vy;
					numBricks = numBricks - 1;				
				} else if (collider != null) {
					// only change direction if ball is at top of paddle (avoid sticking)
					if (ball.getY() < paddle.getY()) {
						vy = -vy;
					}
				}
				// move in any case
				ball.move(vx, vy);
				// pause
				pause(DELAY);
			}
			// remove 1 turn and take old ball off screen
			turns --;
			remove(ball);
		}
		// new label with outcome
		if(numBricks == 0) {
			addWinLab();
		} else {
			addLoseLab();
		}

	}

	private void addLoseLab() {
		// generates losing label
				GLabel lossLab = new GLabel("Too bad! You have lost. Restart the program to try again");
				add(lossLab, (getWidth()/2 - lossLab.getWidth()/2), (getHeight()/2 - lossLab.getHeight()/2));	
	}

	private void addWinLab() {
		// generates winning label
		GLabel winLab = new GLabel("Congratulations! You have won the game");
		add(winLab, (getWidth()/2 - winLab.getWidth()/2), (getHeight()/2 - winLab.getHeight()/2));		
	}

	private GObject getCollidingObject(GOval ball) {
		// test all corners off ball, return object at any corner of the ball
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2* BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2* BALL_RADIUS);
		} else if (getElementAt(ball.getX(), ball.getY() + 2* BALL_RADIUS) != null) {
			return getElementAt(ball.getX(), ball.getY() + 2* BALL_RADIUS);
		} else {
			return null;
		}
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private void addBall() {
		// new method: create ball
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
	}

	/**
	 * Method: Mouse Moved
	 * ------------------
	 * Tracks the paddle to the mouse x location
	 */
	public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			if(x > getWidth() - PADDLE_WIDTH) {
				paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
			} else {
			paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);
			}
	}
	
	private void addPaddle() {
		/* New Method
		 * ==========
		 * creates paddle and sets in center of the screen */
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, (getWidth() - PADDLE_WIDTH)/2, getHeight() - PADDLE_Y_OFFSET);
		
	}

	private void addBlocks() {
		/* New method
		 *==========
		 * adds blocks according to specified dimensions and colors */
				for(int r = 0; r < NBRICK_ROWS; r++) {
					for(int c = 0; c < NBRICK_COLUMNS; c++) {
						GRect block = null;
						block = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
						block.setFilled(true);
						int cols = c;
						int rows = r;
						// set row color based on parameters
						if(rows < RED_ROWS) {
							block.setColor(Color.RED);
						} else if(rows < ORANGE_ROWS) {
							block.setColor(Color.ORANGE);
						} else if(rows < YELLOW_ROWS) {
							block.setColor(Color.YELLOW);
						} else if(rows < GREEN_ROWS) {
							block.setColor(Color.GREEN);
						} else if(rows < CYAN_ROWS) {
							block.setColor(Color.CYAN);
						}
						add(block, (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1)*BRICK_SEP))/2 + cols * (BRICK_WIDTH + BRICK_SEP), BRICK_Y_OFFSET + (rows * (BRICK_HEIGHT + BRICK_SEP)));
					}
				}
	}
	
}
