/*
 * File: Breakout.java
 * -------------------
 * Name: Joshua Kim
 * Section Leader: Robbie Jones
 * 
 * The Breakout program plays the arcade game breakout. 
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
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity.
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Ball diameter
	public static final double BALL_DIAM = BALL_RADIUS * 2;

	// Private Instance Variables
	private GRect paddle = null;

	// Y-location of the paddle.
	private double y;

	private GOval ball = null;

	// The ball's x and y-velocity.
	private double vx, vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	// The current round of the game.
	private int round = 0;

	// The ball's initial x and y-position.
	private double BALL_X;
	private double BALL_Y;
	
	// Label which prompts the user to click their mouse.
	private GLabel clickToPlay = null;

	private int brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setupGame();
		playGame();
	}

	/**
	 * Method: Set Up Game
	 * -------------------
	 * Sets up the game by calling methods to draw the colored 
	 * array of bricks and paddle on the graphics screen.
	 */
	private void setupGame() {
		setupBricks();
		setupPaddle();
		addMouseListeners();
	}

	/**
	 * Method: Set Up Bricks
	 * ---------------------
	 * Creates a 10 x 10 row of bricks by using a nested for-loop.
	 * The outer for-loop creates the rows, and the inner for-loop
	 * creates the columns of bricks.
	 */
	private void setupBricks() {

		for (int row = 0; row < 10; row++) {

			for (int col = 0; col < 10; col++) {
				drawBricks(row, col);
			}
		}
	}

	/**
	 * Method: Draw Bricks
	 * ---------------------
	 * Draws one hundred bricks, which are centered on the 
	 * screen and calls a method to color them. 
	 */
	private void drawBricks(int row, int col) {
		double x = (getWidth() - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP))/2 + col * (BRICK_WIDTH + BRICK_SEP);
		double y = BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP);

		GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		setBrickColors(brick, row);
		add(brick, x, y);
	}

	/**
	 * Method: Set Brick Colors
	 * ------------------------
	 * Sets the brick colors based on the row number. 
	 * Receives this number (which is the index variable
	 * of the inner for-loop) as a parameter.
	 */
	private void setBrickColors(GRect brick, int row) {
		brick.setFilled(true);
		
		if (row < 2) {
			brick.setColor(Color.RED);
		} else if (row < 4) {
			brick.setColor(Color.ORANGE);
		} else if (row < 6) {
			brick.setColor(Color.YELLOW);
		} else if (row < 8) {
			brick.setColor(Color.GREEN);
		} else if (row < 10) {
			brick.setColor(Color.CYAN);
		}
	}

	/**
	 * Method: Set Up Paddle
	 * ---------------------
	 * Draws the paddle centered on the screen.
	 */
	private void setupPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2;
		y = getHeight() - PADDLE_Y_OFFSET;

		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, x, y);
	}

	/**
	 * Method: Mouse Moved
	 * -------------------
	 * Moves the x-coordinate of the paddle according to the 
	 * player's mouse position. Adds the paddle if the x-location
	 * of the ball is within the graphics window. 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		
		if (!isOutsideWindow(mouseX)) {
			add(paddle, mouseX, y);	
		}
	}

	/**
	 * Method: Is Outside Window
	 * -------------------------
	 * Returns true if the player's mouse is outside the window.
	 */
	private boolean isOutsideWindow(double x) {
		return x < 0 || x > getWidth() - PADDLE_WIDTH;  
	}

	/**
	 * Method: Play Game
	 * -----------------
	 * Plays the Breakout game.
	 */
	private void playGame() {
		createBall();
		vx = rgen.nextDouble(1.0, 3.0);
		vy = 3.0;
		
		if (rgen.nextBoolean(0.5)) vx = -vx;
		
		createClickLabel();
		waitForClick();
		remove(clickToPlay);
		
		while (round < NTURNS && brickCounter > 0) {
			checkForBoundaries();
			ball.move(vx, vy);
			checkForCollisions();
			pause(DELAY);
		}
	}

	/**
	 * Method: Create Ball
	 * -------------------
	 * Draws a GOval centered on the screen.
	 */
	private void createBall() {
		BALL_X = getWidth()/2 - BALL_RADIUS;
		BALL_Y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval (BALL_DIAM, BALL_DIAM);
		ball.setFilled(true);
		add(ball, BALL_X, BALL_Y);
	}

	private void createClickLabel() {
		clickToPlay = new GLabel("Click To Play.");
		clickToPlay.setFont("Serif-36");
		double x = (getWidth() - clickToPlay.getWidth())/2;
		double y = (getHeight() - clickToPlay.getHeight())/1.5;
		add(clickToPlay, x, y);
	}
	
	/**
	 * Method: Check For Collision
	 * ---------------------------
	 * Checks if the ball has touched the window's boundaries.
	 * Changes the x or y velocity of the ball after collision.
	 * Removes the ball if it touches the bottom wall.   
	 */
	private void checkForBoundaries() {

		if (isRightOrLeftWall()) {
			vx = -vx;
		} else if (ball.getY() <= 0) {
			vy = -vy;
		} else if (isBottomWall()) {
			remove(ball);
			round++;

			if (round < NTURNS) {
				pause(DELAY * 20);
				add(ball, BALL_X, BALL_Y);
			} else {
				displayGameOver();
			}
		}
	}

	/**
	 * Method: Is Right Or Left Wall
	 * ------------------------------
	 * Returns true if ball has reached or exceeded the left 
	 * or right boundaries of the graphics window.
	 */
	private boolean isRightOrLeftWall() {
		return ball.getX() >= getWidth() - BALL_DIAM || ball.getX() <= 0;
	}

	/**
	 * Method: Is Bottom Wall
	 * ----------------------
	 * Checks if the ball has touched the bottom of the window.
	 */
	private boolean isBottomWall() {
		return ball.getY() > getHeight() - BALL_DIAM;
	}

	/**
	 * Method: Display Game Over
	 * -------------------------
	 * Displays a GLabel to indicate the user has lost the game.
	 */
	private void displayGameOver() {
		GLabel gameOver = new GLabel ("Game Over.");
		gameOver.setFont("Serif-36");
		double x = (getWidth() - gameOver.getWidth())/2;  
		double y = (getHeight() - gameOver.getHeight())/2;
		add(gameOver, x, y);
	}

	/**
	 * Method: Check For Collissions
	 * -----------------------------
	 * Checks if the ball has collided with a brick or paddle.
	 */
	private void checkForCollisions() {

		GObject collider = getCollidingObject();

		if (collider != null) {

			if (collider == paddle) {
				vy = -1 * Math.abs(vy);
			} else {
				remove(collider);
				vy = -vy;
				brickCounter--;

				if (brickCounter == 0) {
					remove(ball);
					displayWinningResult();
				}
			}
		}
	}

	/**
	 * Method: Get Colliding Object
	 * ----------------------------
	 * Returns the object that collided with the ball. 
	 */
	private GObject getCollidingObject() {

		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + BALL_DIAM, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_DIAM, ball.getY());
		} else if (getElementAt(ball.getX() + BALL_DIAM, ball.getY() + BALL_DIAM) != null) {
			return getElementAt(ball.getX() + BALL_DIAM, ball.getY() + BALL_DIAM);
		} else if (getElementAt(ball.getX(), ball.getY() + BALL_DIAM) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_DIAM);
		} else { 
			return null;
		}
	}

	/**
	 * Method: Display Winning Result
	 * ------------------------------
	 * Displays a GLabel to indicate the user has won the game.
	 */
	private void displayWinningResult() {
		GLabel winner = new GLabel ("You Win!");
		winner.setFont("Serif-36");
		double x = (getWidth() - winner.getWidth())/2;  
		double y = (getHeight() - winner.getHeight())/2;
		add(winner, x, y);
	}
}
