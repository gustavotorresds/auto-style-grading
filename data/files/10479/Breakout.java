/*
 * File: Breakout.java


 * -------------------
 * Name: AnQi Yu
 * Section Leader: Semir Shafi
 * 
 * This program creates the game Breakout. In the game, a ball starts at the 
 * center of the screen and starts moving in a random direction with a random 
 * speed. There are colorful bricks at the top of the screen; the ball removes
 * bricks that it comes into contact with, and the objective of the game 
 * is to remove all of the bricks. The user can use a 'paddle' at the bottom of
 * the screen to bounce the ball back up to the top of the screen and prevent the 
 * ball from falling through the bottom of the screen. The game ends if the 
 * ball falls through the bottom of the screen.
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
	public static final double BALL_RADIUS = 20;

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
	
/*
 * Instance variables created during programming.
 */
		private GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		private GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		private double vx, vy;
		private RandomGenerator rgen = RandomGenerator.getInstance();
		private int brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();
		
		playGame();
	}
	
	private void setUpGame() {
		buildBlockOfBricks();
		buildPaddle();
	}

	private void buildBlockOfBricks() {
		
		//Builds block of bricks. 
		for (int numberOfRows = 0; numberOfRows < NBRICK_ROWS; numberOfRows ++) {
			for (int numberOfBricks = 0; numberOfBricks < NBRICK_COLUMNS; numberOfBricks ++) {
			
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				
				double widthOfBrickRow = (BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS - BRICK_SEP;
				
				double x = (BRICK_WIDTH + BRICK_SEP) * numberOfBricks + getWidth()/2 - widthOfBrickRow/2;
			
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * numberOfRows;
				
				//Adds red color. 
				if ((numberOfRows >= 0) && (numberOfRows < NBRICK_ROWS/5)) {
					brick.setFilled(true);
					brick.setColor(Color.RED);
				}
					
				//Adds orange color.
				if ((numberOfRows >= NBRICK_ROWS/5) && (numberOfRows < NBRICK_ROWS/5*2)) {
					brick.setFilled(true);
					brick.setColor(Color.ORANGE);
				}
				
				//Adds yellow color.
				if ((numberOfRows >= NBRICK_ROWS/5*2) && (numberOfRows < NBRICK_ROWS/5*3)) {
					brick.setFilled(true);
					brick.setColor(Color.YELLOW);
				}
				
				//Adds green color.
				if ((numberOfRows >= NBRICK_ROWS/5*3) && (numberOfRows < NBRICK_ROWS/5*4)) {
					brick.setFilled(true);
					brick.setColor(Color.GREEN);
				}
				
				//Adds cyan color. 
				if ((numberOfRows >= NBRICK_ROWS/5*4) && (numberOfRows < NBRICK_ROWS/5*5)) {
					brick.setFilled(true);
					brick.setColor(Color.CYAN);
				}
				
				add (brick, x, y);
			}
		}
	}
	
	//Builds the paddle and puts it at the specified location.
	private void buildPaddle() {
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle.setFilled(true);
		add (paddle, 0, y);
		addMouseListeners();
	}
	
	//Allows the paddle to be moved by the user.
	public void mouseMoved (MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET;
		if (e.getX() > 0 && e.getX() < getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(x, y);
		}
	}
	
	private void playGame() {
		makeBall();
		ballAnimation();
	}
	
	//Makes a ball with the specified dimensions; also declares the velocity of the ball.
	private GOval makeBall() {
		ball.setFilled(true);
		add (ball, (getWidth()-BALL_RADIUS)/2, (getHeight()-BALL_RADIUS)/2);
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		return ball;
	}
	
	//Animates the ball.
	private void ballAnimation() {
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);
			
			//Conditions for the situations when the ball hits the wall.
			if(hitRightWall(ball) || hitLeftWall(ball)) {
				vx = -vx;
			} else if(hitTopWall(ball)) {
				vy = -vy;
			} else if(hitBottomWall(ball)) {
				break;
			}
			collisions();
		}
		gameOver();
	}

	//Indicates when the ball has hit the right wall.
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}
	
	//Indicates when the ball has hit the left wall.
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0; 
	}
	
	//Indicates when the ball has hit the top wall.
	private boolean hitTopWall(GOval ball) {
		return ball.getY() < 0;
	}
	
	//Indicates when the ball has hit the bottom wall.
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight();
	}
	
	//Conditions when the ball collides with an object.
	private void collisions() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			vy = -vy;
		} else if (collider != null) {
			remove(collider);
			brickCounter--;
			
			//Allows the the user to "win" when the last brick is hit.
			if (brickCounter == 0) {
				remove(ball);
				youWin();
			}
		}
	}
	
	//Finds the object that the ball makes contact with.
	private GObject getCollidingObject() {
		GObject upperLeftCorner = getElementAt(ball.getX(), ball.getY());
		GObject upperRightCorner = getElementAt((ball.getX() + 2 * BALL_RADIUS), ball.getY());
		GObject lowerLeftCorner = getElementAt(ball.getX(), (ball.getY() + 2 * BALL_RADIUS));
		GObject lowerRightCorner = getElementAt((ball.getX() + 2 * BALL_RADIUS), (ball.getY() + 2* BALL_RADIUS));
		if (upperLeftCorner != null) {
			return upperLeftCorner;
		} else if (upperRightCorner != null) {
			return upperRightCorner;
		} else if (lowerLeftCorner != null) {
			return lowerLeftCorner;
		} else if (lowerRightCorner != null) {
			return lowerRightCorner;
		} else {
			return null;
		}
	}
	
	//Generates a label that says "Game Over."
	private void gameOver() {
		GLabel gameOver = new GLabel("Game Over");
		add(gameOver, getWidth()/2 -gameOver.getWidth()/2, getHeight()/2 - gameOver.getHeight()/2);
	}
	
	//Generates a label that says "You Win."
	private void youWin() {
		GLabel youWin = new GLabel("You Win");
		add(youWin, getWidth()/2 - youWin.getWidth()/2, getHeight()/2 - youWin.getHeight()/2);
	}

}
