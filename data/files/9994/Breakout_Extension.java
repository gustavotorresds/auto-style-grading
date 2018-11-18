/*
 * File: Breakout.java
 * -------------------
 * Name: Alex Nam
 * Section Leader: Ella Tessier_Lavigne
 * This file implements the game of Breakout.
 * Extension features: 
 * Y speed increases as the player hits more bricks.
 * Produces sound every time a brick is hit.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_Extension extends GraphicsProgram {

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

	// instance variables
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy;
	private GRect brick;
	private int numberOfRemainingBricks;
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		addMouseListeners();

		// Set up a brick wall
		setBrickWall();

		// Set up a paddle
		paddle = createPaddle();
		// Before the mouse moves, the paddle is located in the center of the screen
		addPaddleToCenter(paddle);

		// Add a ball to the center of the screen
		ball = createBall();

		// Once the mouse starts moving, the paddle moves in the direction of the mouse's motion
		addMouseListeners();

		numberOfRemainingBricks = NBRICK_COLUMNS * NBRICK_ROWS;

		// repeat playGame method for the number of turns
		for (int i = 0; i < NTURNS; i++ ) {
			
			// for every turn end, display the number of lives left and ask the player to click to continue the game
			GLabel reminder = restart(i);
			waitForClick();

			// once the game is restarted, remove the label and set the ball back to the center
			remove(reminder);
						
			// set ball to the center
			addBallToCenter(ball);
			
			// start the game
			playGame();	
			
			// whenever the player removes all bricks, the loop stops
			if(win(numberOfRemainingBricks)) {
				break;
			}
		}

		// determine whether the player has won or lost, depending on the number of remaining bricks
		if (win(numberOfRemainingBricks)) {
			GLabel label = new GLabel ("CONGRATULATIONS! MISSION COMPLETE");
			label.setFont("Courier-13");
			label.setColor(Color.BLACK);
			add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getAscent()/2);
		} else {
			GLabel label = new GLabel ("GAME OVER");
			label.setFont("Courier-24");
			label.setColor(Color.BLACK);
			add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getAscent()/2);
		}
	}

	// click to start the game
	private void playGame() {

		// set up
		vy = VELOCITY_Y;
		// randomly generate x velocity
		randomX_Velocity();
		
		// this loop continues until the number of remaining bricks is equal to 0
		while (numberOfRemainingBricks > 0) {
			// ball bounces off the left and right wall
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}

			// if the ball hits the bottom wall, the turn ends
			if (hitBottomWall(ball)) {
				break;
			}

			// if the ball hits the top wall, y velocity of the ball changes
			if (hitTopWall(ball)) {
				vy = -vy;
			}

			GObject collider = getCollidingObject(ball);

			// if the ball hits any object, either a paddle or a brick, it bounces back
			if (collider == paddle) {
				// every time the ball hits the paddle, it should bounce upward (y velocity should always be negative)
				vy = -Math.abs(vy);
			} else if (collider != null) {
				vy = -vy;
				remove(collider);
				// each time a brick is removed, subtract one from the total number of bricks
				numberOfRemainingBricks = numberOfRemainingBricks - 1;
				// each time a brick is removed, the ball moves slightly faster
				vy = vy * 1.009;
				// make sound
				bounceClip.play();
			}

			// ball starts moving
			ball.move(vx, vy);

			// pause
			pause(DELAY);
		}
	}

	// determine whether the player has won or lost, depending on the number of bricks remaining
	private boolean win(int numberOfRemainingBricks) {
		return numberOfRemainingBricks == 0;
	}

	// randomly generates a new x velocity each time this method is called
	private void randomX_Velocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	// draws a brick wall, which consists of rows of colored bricks 
	private void setBrickWall() {
		// each brick row begins at a different y location, which is determined by the number of rows previously created, and each row takes a different color scheme
		// the color scheme changes every two rows from red (rows 0, 1), orange (rows 2, 3), yellow (rows 4, 5), green (rows 6, 7), to cyan (rows 8, 9)
		// once the cycle of color schemes is completed, the color goes back to red 

		for (int numberOfRows = 0; numberOfRows < NBRICK_ROWS; numberOfRows++ ) {
			if (numberOfRows % 10 == 0 || numberOfRows % 10 == 1) {
				setBrickRow(Color.RED, numberOfRows);
			}
			if (numberOfRows % 10 == 2 || numberOfRows % 10 == 3) {
				setBrickRow(Color.ORANGE, numberOfRows);
			}
			if (numberOfRows % 10 == 4 || numberOfRows % 10 == 5) {
				setBrickRow(Color.YELLOW, numberOfRows);
			}
			if (numberOfRows % 10 == 6 || numberOfRows % 10 == 7) {
				setBrickRow(Color.GREEN, numberOfRows);
			}
			if (numberOfRows % 10 == 8 || numberOfRows % 10 == 9) {
				setBrickRow(Color.CYAN, numberOfRows);
			}
		}
	}

	/**
	 * Method: setBrickRow
	 * Parameters: color of a new brick row, variable y indicates the y location at which a new row begins
	 * Draws a row of bricks and each brick is a given distance away from each other
	 */
	private void setBrickRow(Color color, double y) {
		for (int numberOfBricks = 0; numberOfBricks < NBRICK_COLUMNS; numberOfBricks++) {
			// each time creates a brick of the same width and height
			brick = makeBrick();
			brick.setColor(color);
			// each time a new brick is added at a different x location
			add(brick, getWidth()/2 - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP)/2 + (BRICK_WIDTH + BRICK_SEP) * numberOfBricks, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * y);
		}
	}

	// make a brick using GRect method and return the shape
	private GRect makeBrick() {
		brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		return brick;
	}

	// creates a paddle; location of the paddle can change
	private GRect createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	// displays a paddle in the center of the screen
	private void addPaddleToCenter(GRect paddle) {
		double x = getWidth()/2 - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, x, y);
	}

	// when the mouse starts moving, the paddle follows the horizontal motion of the mouse
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		// y position of the paddle remains the same
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

		// ensures that the entire paddle is always displayed on the screen even when the mouse passes the horizontal end of the screen
		double nearEnd = getWidth() - PADDLE_WIDTH;
		if (mouseX < nearEnd) {
			add(paddle, mouseX, y);
		} else {
			add(paddle, nearEnd, y);
		}
	}

	// create a ball of a given size
	private GOval createBall() {
		ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		return ball;
	}

	// locate the ball in the center of the screen before it is released
	private void addBallToCenter(GOval ball) {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		add(ball, x, y);
	}

	// check if the ball hits the left wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}

	// check if the ball hits the right wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	// check if the ball hits the bottom wall, in which case, the turn ends
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	// check if the ball hits the top wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/**
	 * Method: getCollidingObject
	 * checks if any of the four corners of the ball is touching an object
	 * if the ball is colliding an object, return the colliding object, either a paddle or a brick
	 */
	private GObject getCollidingObject(GOval ball) {
		GObject topLeftCorner = getElementAt(ball.getX(), ball.getY());
		if(topLeftCorner != null) {
			return topLeftCorner;
		}

		GObject topRightCorner = getElementAt(ball.getX() + ball.getWidth(), ball.getY());
		if(topRightCorner != null) {
			return topRightCorner;
		}

		GObject bottomLeftCorner = getElementAt(ball.getX(), ball.getY() + ball.getHeight());
		if(bottomLeftCorner != null) {
			return bottomLeftCorner;
		}

		GObject bottomRightCorner = getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getHeight());
		if(bottomRightCorner != null) {
			return bottomRightCorner;
		}
		// if all else fails, there's no object to return 
		return null;
	}

	// display the number of remaining turns and an indicator to restart the game
	private GLabel restart(int i) {
		GLabel restart = new GLabel ("You have " + (NTURNS - i) + " lives. Click anywhere on the screen to start.");
		restart.setFont("Courier-11");
		restart.setColor(Color.BLACK);
		add(restart, getWidth()/2 - restart.getWidth()/2, getHeight()/2 - restart.getAscent()/2);
		return restart;
	}
}