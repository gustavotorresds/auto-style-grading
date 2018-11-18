/*
 * File: Breakout_BASIC.java
 * -------------------
 * Name: Matthew Vollrath
 * Section Leader: Avery Wang
 * 
 * This file implements a BASIC game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_BASIC extends GraphicsProgram {

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
	public static final double VELOCITY_X_MAX = VELOCITY_Y;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 7;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Declares paddle as an instance variable.
	private GRect paddle = null;
	
	// Declares x and y velocity of the ball as instance variables
	private double vx, vy;
	
	//Creates random number generator variable.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Allows mouse event methods to be called
		addMouseListeners();

		setUpGame();
		playGame();
	}

	
	private void setUpGame() {
		addBricks();
		addPaddle();
	}
	
	/**
	 * Adds a grid of bricks to the screen based on the specified constants
	 * for row and column number.
	 */
	private void addBricks() {
		for (int rowNum = 0; rowNum < NBRICK_ROWS; rowNum++) {
			for(int brickNum = 0; brickNum < NBRICK_COLUMNS; brickNum++) {
				drawBrick(rowNum, brickNum);
			}
		}	
	}

	/** 
	 * Creates and adds each brick at the correct location.
	 * @param rowNum
	 * @param brickNum The number of the particular brick in the row (column number)
	 */
	private void drawBrick(int rowNum, int brickNum) {
		double firstInRow = (getWidth() - (NBRICK_COLUMNS*(BRICK_WIDTH + BRICK_SEP) - BRICK_SEP))/2; // Defines x-cord of first column such that rows are centered
		double brickX = firstInRow + brickNum*(BRICK_WIDTH + BRICK_SEP);
		double brickY = BRICK_Y_OFFSET + rowNum*(BRICK_HEIGHT + BRICK_SEP);
		
		GRect brick = new GRect (brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(getBrickColor(rowNum));
		brick.setFilled(true);
		add(brick);
	}

	/**
	 * Determines the color of each row of bricks based on the ones digit of the row number.
	 * @param rowNum
	 * @return brickColor
	 */
	private Color getBrickColor(int rowNum) {
		Color brickColor = null;
		int rowOnesDigit = rowNum % 10;
		switch(rowOnesDigit) {
			case 0: case 1:
				brickColor = Color.RED;
				break;
			case 2: case 3:
				brickColor = Color.ORANGE;
				break;
			case 4: case 5:
				brickColor = Color.YELLOW;
				break;
			case 6: case 7:
				brickColor = Color.GREEN;
				break;
			case 8: case 9:
				brickColor = Color.CYAN;
				break;
		}
		return brickColor;
	}
	
	/**
	 * Creates paddle and adds it to the screen
	 */
	private void addPaddle() {
		paddle = new GRect((getWidth() - PADDLE_WIDTH)/2, getHeight() - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/**
	 * Sets the paddle to follow the mouse, and return to the edge of the
	 * screen if it goes off.
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		paddle.setCenterX(mouseX);
		if(paddle.getX() < 0) {
			paddle.setX(0);
		} else if (paddle.getRightX() > getWidth()){
			paddle.setRightX(getWidth());
		}
	}
	
	/**
	 * The main method of the program: animates ball and handles turn/game
	 * end conditions.
	 */
	private void playGame() {
		int ballsRemaining = 3;
		int bricksRemaining = NBRICK_ROWS * NBRICK_COLUMNS;
		boolean ballLost = false;
		boolean gameWon = false;
		
		while(ballsRemaining > 0) { //Plays a new turn while there are balls remaining.
			GOval ball = makeBall();
			add(ball);
			
			while(true) { // Animation loop for ball.
				ball.move(vx, vy);
				ballLost = checkWallCollisions(ball); // Tests for wall collisions AND sets turn to end if ball goes out of bounds.
				bricksRemaining -= checkObjectCollisions(ball); // Tests for object collisions AND updates remaining brick count.
				
				if(ballLost) { // Ends turn if ball has gone out of bounds.
					ballsRemaining -= 1;
					remove(ball);
					break;
				} else if(bricksRemaining == 0) { // Triggers win screen if last brick has been removed.
					gameWon = true;
					break;
				}
				pause(DELAY); // Pauses before next loop
			}
			
			if(gameWon) { // Breaks outer loop (which controls the three turns)
				break;
			}
		}
		endGame(gameWon); // Triggers game end when outer (turn) loop ends.
	}
	
	/**
	 * Defines a ball in the center of the screen, sets x- and y- velocity
	 * @return ball
	 */
	private GOval makeBall() {
		GOval ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		ball.setFilled(true);
		return ball;
	}

	/**
	 * Tests for collisions with walls and changes ball's velocity accordingly.
	 * ALSO returns whether or not ball has gone out of bounds.
	 * @param ball
	 * @return ballLost
	 */
	private boolean checkWallCollisions(GOval ball) {
		boolean ballLost = false;
		if (ball.getX() <= 0) {
			vx = -vx;
		} else if(ball.getRightX() >= getWidth()) {
			vx = -vx;
		} else if(ball.getY() <= 0) {
			vy = -vy;
		} else if(ball.getBottomY() >= getHeight()) {
			ballLost = true;
		}
		return ballLost;
	}

	/**
	 * Tests for collisions with objects and removes bricks the ball collides with.
	 * ALSO subtracts one from count of remaining bricks if a brick has been removed.
	 * @param ball
	 * @return bricksToSubtract
	 */
	private int checkObjectCollisions(GOval ball) {
		GObject collider = getCollidingObject(ball);
		int bricksToSubtract = 0;
		if(collider != null) {
			vy = -vy;
			if(collider != paddle) {
				remove(collider);
				bricksToSubtract = 1;
			}
		}
		return bricksToSubtract;
	}

	/**
	 * Checks whether there is an object at one of the four corners of the ball.
	 * If there is, it returns that object. Otherwise it returns null.
	 * @param ball
	 * @return collider
	 */
	private GObject getCollidingObject(GOval ball) {
		for(double ballX = ball.getX(); ballX <= ball.getRightX(); ballX += 2*BALL_RADIUS) { //Checks both left and right of ball
			for(double ballY = ball.getY(); ballY <= ball.getBottomY(); ballY += 2*BALL_RADIUS) { //Checks both top and bottom of ball
				GObject collider = getElementAt(ballX, ballY);
				if(collider != null) {
					return collider;
				}
			}
		}
		return null;
	}

	/**
	 * Displays either win or loss message at game end.
	 * @param gameWon Defines whether game has ended in a win or a loss
	 */
	private void endGame(boolean gameWon) {
		GLabel endMessage = null;
		if(gameWon) {
			endMessage = new GLabel("Congratulations!");
		} else {
			endMessage = new GLabel("Game Over.");
		}
		endMessage.setCenterLocation(getWidth()/2, getHeight()/2);
		add(endMessage);
	}
}


