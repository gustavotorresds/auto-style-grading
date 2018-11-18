/*
 * File: Breakout.java
 * -------------------
 * Name: D-Wade
 * Section Leader: Kaitlyn
 * THis is the code for Breakout. The objective is to have a game where the ball bounces off the paddle, walls and bricks in attempt to clear out all the bricks.
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
	
	private double vx, vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private int brickCounter = 100;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		
		for (int i=0; i < NTURNS; i++) {
			setUpGame();
			playGame();
		
			// Uses brickcounter to check for winner/end of game
			if (brickCounter == 0) {
				ball.setVisible(false);
				printWinner();
				break;
			}
			if(brickCounter > 0) {
				removeAll();
			}
		}
		if (brickCounter > 0) {
			printGameOver();
		}
	}
	
	//draws all components of the game
	private void setUpGame() {
		drawBricks (getWidth()/2, BRICK_Y_OFFSET);
		drawPaddle();
		drawBall();
	}
	
	// draws and colors the bricks 
	private GRect brick;
	private void drawBricks(double cx, double cy) {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				
				double x = cx - (NBRICK_COLUMNS * BRICK_WIDTH)/2 - ((NBRICK_COLUMNS - 1) * BRICK_SEP)/2 + column * BRICK_WIDTH + column * BRICK_SEP;
				double y = cy + row * BRICK_HEIGHT + row * BRICK_SEP;
				
				brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);
				if (row < 2) {
					brick.setColor(Color.RED);
				}
				if (row == 2 || row == 3) { 
					brick.setColor(Color.ORANGE);
				}
				if (row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				}
				if (row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				}
				if (row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}
	
	// draws and centers the paddle 
	private GRect paddle;
	private void drawPaddle() {
		
		double x = getWidth()/2 - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);
		addMouseListeners();
	}
	
	//Makes the paddle follow the mouse
	public void mouseMoved(MouseEvent e) {
		if ((e.getX() < getWidth() - PADDLE_WIDTH/2) && (e.getX() > PADDLE_WIDTH/2)) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
	
	//draws the ball
	private GOval ball;
	private void drawBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add (ball);
	}
	
	//begins game upon click
	private void playGame() {
		waitForClick();
		ballVelocity();
		
		//ball moves while game is being played unless it hits bottom wall or all the bricks are gone
		while (true) {
			moveBall();
			
			if (ball.getY() >= getHeight()) {
				break;
			}
			if(brickCounter == 0) {
				break;
			}
		}
	}
	
	// creates vertical and horizontal velocities
	private void ballVelocity() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = 3.0;
	}
	
	//moves ball and makes it bounce off walls
	private void moveBall() {
		ball.move(vx, vy);
		if (ball.getX() + BALL_RADIUS*2 >= getWidth() || ball.getX() <= 0) {
			vx = -vx;
		}
		if (ball.getY() <= 0) {
			vy = -vy;
		}
		
		//allows ball to bounce off paddle
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS * 2 && ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS * 2 + 4) {
				vy = -vy;
			}
		}
		else if (collider != null) {
			remove(collider);
			brickCounter--;
			vy = -vy;
		}
		pause (DELAY);
	}
	
	//prevents sticky paddle
	private GObject getCollidingObject() {
		if ((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if (getElementAt((ball.getX() + BALL_RADIUS *2)) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		}
		else if (getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS * 2)) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS *2);
		}
		else if (getElementAt((ball.getX() + BALL_RADIUS * 2), (ball.getY() + BALL_RADIUS * 2)) != null) {
			return getElementAt(ball.getX(), + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}
		else {
			return null;
		}
	}
	
	//end of game for loser
	private void printGameOver() {
		GLabel gameOver = new GLabel ("Game Over", getWidth()/2, getHeight()/2);
		gameOver.move(-gameOver.getWidth()/2, -gameOver.getHeight());
		gameOver.setColor(Color.RED);
		add (gameOver);
	}
	
	
	//end of game for winner
	private void printWinner() {
		GLabel Winner = new GLabel ("Winner", getWidth()/2, getHeight()/2);
		Winner.move(-Winner.getWidth()/2, -Winner.getHeight());
		Winner.setColor(Color.GREEN);
		add (Winner);
	}

}
