/*
 * File: Breakout.java
 * -------------------
 * Name: Eliot Chang
 * Section Leader: Brahm Capoor
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

	// KNOWN BUGS
	// 1 - ball at corner will remove all three bricks
	// 2 - ball can enter side of paddle and ricochet until it gets to the other side
	//



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
	public static final double DELAY = 1000.0 / 200.0;

	// Number of turns 
	public static final int NTURNS = 3;

	/**instance variables**/
	GRect paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
	GOval ball = new GOval(BALL_RADIUS,BALL_RADIUS);
	private double vx;
	private double vy;
	private int turnsRemaining = NTURNS;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	GLabel turnsText;
	GLabel bricksRemainingText;
	GLabel finalResult;
	private int bricksRemaining;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();
		setupBricks();
		createPaddle();
		while(turnsRemaining>0) {
			reportTurnsRemaining();
			bricksRemainingText = new GLabel(bricksRemaining+" bricks remaining");
			add(bricksRemainingText,100,10);
			startBall();
			playGame();	
		}
		if(turnsRemaining==0 & bricksRemaining > 0) {
			losingSad();
		}

	}

	/**
	 * Method: Losing Sad!
	 * ------------------
	 * This method triggers when no balls are remaining
	 * and the user has lost the game.
	 */

	private void losingSad() {
		GLabel finalResult = new GLabel("Sorry, you have lost! There were "+bricksRemaining+" bricks remaining");
		add(finalResult, (getWidth() - finalResult.getWidth())/2, (getHeight() - finalResult.getAscent())/2);

	}

	private void reportTurnsRemaining() {
		turnsText = new GLabel(turnsRemaining+" balls left");
		add(turnsText,10,10);
	}

	/**
	 * Method: Play Game
	 * -----------------
	 * This method will start when the user clicks, and will start to play the game.
	 * This method will last until the user wins or the ball dies.
	 */

	private void playGame() {
		waitForClick();
		remove(turnsText);
		remove(bricksRemainingText);
		while(ball.getY() < getHeight() & bricksRemaining > 0) {
			ball.move(vx,vy);
			pause(DELAY);
			double y = ball.getY();
			double x = ball.getX();
			bounceOffWalls(x, y);
			GObject collider = getCollidingObject(x,y); 
			if(collider != null) {
				if (collider == paddle) {
					vy = -Math.abs(vy);
				} else {
					vy = -vy;
					remove(collider);
					bricksRemaining--;
					if(bricksRemaining == 0) {
						winning();
					}
				}

			}

		}
		remove(ball);
		turnsRemaining--;


	}

	/**
	 * Method: Winning
	 * ------------------
	 * This method triggers when no bricks are remaining 
	 * and the user has won the game. It should notify the user
	 * and end the game.
	 */

	private void winning() {
		GLabel finalResult = new GLabel("Congratulations, you have won!");
		add(finalResult, (getWidth() - finalResult.getWidth())/2, (getHeight() - finalResult.getAscent())/2);
	}

	/**
	 * Method: Get Colliding Object
	 * ------------------
	 * This method tests to see if an object exists at any of the four
	 * "corners" of the ball. If so, this method returns the element at
	 * that location. If not, it returns NULL.
	 */

	private GObject getCollidingObject(double x, double y) {
		GObject collider = getElementAt(x,y);
		if (collider == null) {
			collider = getElementAt(x + BALL_RADIUS,y);
		}
		if (collider == null) {
			collider = getElementAt(x,y + BALL_RADIUS);

		}
		if (collider == null) {
			collider = getElementAt(x + BALL_RADIUS,y + BALL_RADIUS);
		}	
		return collider;

	}

	private void bounceOffWalls(double x, double y) {
		if (isLowerLimitWall(y)) {
			vy = -vy;
		}

		if (isLowerLimitWall(x)) {
			vx = -vx;
		}

		if(isRightWall(x)) {
			vx = -vx;
		}
	}

	private boolean isRightWall(double x) {
		return x >= getWidth() - BALL_RADIUS;
	}

	private boolean isLowerLimitWall(double y) {
		return y <= 0;
	}

	private boolean isBottomWall(double y) {
		return y >= getHeight() - BALL_RADIUS;
	}


	/**
	 * Method: Start Ball
	 * ---------------
	 * This method will create a ball in the middle of the screen and set it to start
	 * moving in the positive y direction, with a random x velocity  
	 */

	private void startBall() {
		ball.setFilled(true);
		add(ball,(getWidth()-BALL_RADIUS)/2,(getHeight()-BALL_RADIUS)/2);
		vx = rgen.nextDouble(1.0,3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy=VELOCITY_Y;

	}

	public void mouseMoved (MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		if(x > 0 & x < (getWidth() - PADDLE_WIDTH) ) {
			paddle.setLocation(x,getHeight() - PADDLE_Y_OFFSET);
		}
		
	}

	/** 
	 * Method: Create Paddle
	 * -------------
	 * This method will create the paddle and make it responsive to the mouse
	 */

	private void createPaddle() {
		paddle.setFilled(true);
		add(paddle,(getWidth() - PADDLE_WIDTH)/2,getHeight() - PADDLE_Y_OFFSET);

	}

	/**
	 * Method: Setup Bricks
	 * -----------------
	 * This method will setup the initial set of bricks before the game begins
	 */
	private void setupBricks() {
		for(int row = 0; row < NBRICK_ROWS; row++) { 

			for(int column = 0; column < NBRICK_COLUMNS; column++) {
				GRect brick = new GRect(BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				switch(row) {
				case 0: 
				case 1:	
					brick.setColor(Color.RED);
					break;
				case 2: 
				case 3:	
					brick.setColor(Color.ORANGE);
					break;
				case 4: 
				case 5:	
					brick.setColor(Color.YELLOW);
					break;
				case 6: 
				case 7:	
					brick.setColor(Color.GREEN);
					break;
				case 8: 
				case 9:	
					brick.setColor(Color.CYAN);
					break;
				}

				add(brick,column*BRICK_WIDTH + (column+1)*BRICK_SEP,row*(BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET);
				bricksRemaining++;	
			}

		}



	}

}
