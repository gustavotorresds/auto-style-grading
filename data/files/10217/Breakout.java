/*
 * File: Breakout.java
 * -------------------
 * Name: Hannah Scott
 * Section Leader: Vineet Kosaraju
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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//INSTANCE VARIABLES
	private GRect paddle;
	private GOval ball;
	private double vx;
	private double vy;
	private int lives;
	private int brickCounter;
	private RandomGenerator rgen = new RandomGenerator();
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		addMouseListeners();

		//SET UP METHODS
		addBricks();
		makePaddle();
		makeBall();

		//PLAY METHODS
		checkWorld();
	}

	/*
	 * This method sets up the grid of bricks.
	 * Precondition: there are no bricks on the screen.
	 * Postcondition: there are NBRICK_ROWS/NBRICK_COLUMNS number of rows/columns of bricks with an alternating rainbow pattern
	 */
	private void addBricks() {
		for (int i = 1; i < NBRICK_COLUMNS + 1; i++) { //Columns
			for (int j = 1; j < NBRICK_ROWS + 1; j++) { //Rows
				double xCoord = ((BRICK_WIDTH + BRICK_SEP) * j) - (BRICK_WIDTH - (BRICK_SEP / 2.0));
				double yCoord = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * i;
				GRect bricks = new GRect(xCoord, yCoord, BRICK_WIDTH, BRICK_HEIGHT);
				if (i == 1 || i == 2) {
					bricks.setColor(Color.RED);
				} else if (i == 3 || i == 4) {
					bricks.setColor(Color.ORANGE);
				} else if (i == 5 || i == 6) {
					bricks.setColor(Color.YELLOW);
				} else if (i == 7 || i == 8) {
					bricks.setColor(Color.GREEN);
				} else if (i == 9 || i == 10) {
					bricks.setColor(Color.CYAN);
				}
				bricks.setFilled(true);
				add(bricks);
			}
		}
	}

	/*
	 * This method creates a paddle.
	 */
	private void makePaddle() {
		paddle = new GRect((getWidth() - PADDLE_WIDTH) / 2.0, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * This method is a Mouse Listener that determines the x-coordinates of the paddle according to where the mouse is.
	 */
	public void mouseMoved(MouseEvent e) {
		int xCoord = e.getX();
		double yCoord = getHeight() - PADDLE_Y_OFFSET;
		paddle.setFilled(true);
		paddle.setLocation(xCoord, yCoord);
	}

	/*
	 * This method checks the ball's world for objects that it should collide with, and allows it to bounce off in response.
	 */
	public void checkWorld() {
		vx = rgen.nextDouble(1.0, 3.0);
		vy = VELOCITY_Y;
		lives = NTURNS;
		brickCounter = NBRICK_ROWS * NBRICK_COLUMNS; //total number of bricks at the start.
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		waitForClick(); //starts when user clicks mouse.
		while (lives > 0) { //ends when user runs out of lives.
			GObject collider = getCollidingObject(); //creates a variable for the object with which the ball collides.
			if (collider == paddle && ((getElementAt(ball.getX() + 2.0 * BALL_RADIUS, ball.getY() + 2.0 * BALL_RADIUS) == paddle) 
					|| (getElementAt(ball.getX(), ball.getY() + 2.0 * BALL_RADIUS) == paddle))) { //Accounts for paddle
				hitPaddle();
				ball.move(vx, vy);
			} else if (getElementAt(ball.getX(), ball.getY()) == paddle) { //accounts for case when ball goes below paddle
				lives--;
				if (lives == 0) {
					removeAll();
					break;
				}
				remove(ball);
				makeBall();
				GLabel livesLabel = new GLabel("LIVES:  ");
				GLabel displayLives = new GLabel(("LIVES: " + lives), ((getWidth() / 2.0) - (livesLabel.getWidth() / 2.0)), (getHeight() / 2.0) - livesLabel.getAscent());
				add(displayLives);
				waitForClick();
				remove(displayLives);
			} else if (getElementAt(ball.getX() + BALL_RADIUS * 2.0, ball.getY()) == paddle) { //accounts for case when ball goes below paddle
				lives--;
				if (lives == 0) {
					removeAll();
					break;
				}
				remove(ball);
				makeBall();
				GLabel livesLabel = new GLabel("LIVES:  ");
				GLabel displayLives = new GLabel(("LIVES: " + lives), ((getWidth() / 2.0) - (livesLabel.getWidth() / 2.0)), (getHeight() / 2.0) - livesLabel.getAscent());
				add(displayLives);
				waitForClick();
				remove(displayLives);
			} else if (collider != null) { //Accounts for when ball hits a brick
				hitBricks();
			} else if (isOutsideWorldBottom()) { //If ball encounters bottom wall
				lives--;
				if (lives == 0) {
					removeAll();
					break;
				}
				remove(ball);
				makeBall(); //puts ball back in center
				GLabel livesLabel = new GLabel("LIVES:  "); //displays current lives
				GLabel displayLives = new GLabel(("LIVES: " + lives), ((getWidth() / 2.0) - (livesLabel.getWidth() / 2.0)), (getHeight() / 2.0) - livesLabel.getAscent());
				add(displayLives); 
				waitForClick();
				remove(displayLives);
			} else if (isOutsideWorldSides()) { //Switch x direction if hits side wall
				vx = -vx;
			} else if (isOutsideWorldTop()) { //Bounce off top wall
				vy = -vy;
			}
			ball.move(vx, vy);
			pause(DELAY);
		}
		GLabel gameOver = new GLabel("YOU LOSE!");
		GLabel endGame = new GLabel("YOU LOSE!", (getWidth() / 2.0) - (gameOver.getWidth() / 2.0), getHeight() / 2.0 - (gameOver.getAscent() / 2.0));
		add(endGame);
	}

	/*
	 * This helper method returns true if the ball is past the bottom wall
	 */
	private boolean isOutsideWorldBottom() {
		double currentY = ball.getY();
		double maxY = getHeight() - (BALL_RADIUS * 2.0);
		return (currentY > maxY);
	}

	/*
	 * This helper method returns true if the ball hits the top wall
	 */
	private boolean isOutsideWorldTop() {
		double currentY = ball.getY();
		double minY = 0;
		return (currentY < minY);
	}

	/*
	 * This helper method returns true if the ball hits the side wall
	 */
	private boolean isOutsideWorldSides() {
		double currentX = ball.getX();
		double minX = 0;
		double maxX = getWidth() - (BALL_RADIUS * 2.0);
		return (currentX > maxX || currentX < minX);
	}

	/*
	 * This method returns object found at each corner of the ball, if such an object exists.
	 */
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		GObject object = null;
		if (getElementAt(x, y) != null) {
			object = getElementAt(x,y);
		} else if (getElementAt(x + (2.0 * BALL_RADIUS), y) != null) {
			object = getElementAt(x + (2.0 * BALL_RADIUS), y);
		} else if (getElementAt(x, y + (2.0 * BALL_RADIUS)) != null) {
			object = getElementAt(x, y + (2.0 * BALL_RADIUS));
		} else if (getElementAt(x + (2.0 * BALL_RADIUS), y + (2.0 * BALL_RADIUS)) != null) {
			object = getElementAt(x + (2.0 * BALL_RADIUS), y + (2.0 * BALL_RADIUS));
		}
		return object;
	}

	/*
	 * This method creates the ball.
	 */
	private void makeBall() {
		double xCoord = (getWidth() - BALL_RADIUS * 2.0) / 2.0;
		double yCoord = (getHeight() - BALL_RADIUS * 2.0) / 2.0;
		ball = new GOval(xCoord, yCoord, BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * This method changes the vertical direction of the ball in response to hitting the paddle.
	 */
	private void hitPaddle() {
		bounceClip.play();
		vy = -vy;
	}

	/*
	 * This method removes the bricks with which the ball collides.
	 */
	private void hitBricks() {
		GObject collider = getCollidingObject();
		if (collider != paddle) { //ensures that the collided object is not the paddle.
			remove(collider);	
		}
		bounceClip.play();
		brickCounter--;
		if (brickCounter == 0) {
			GLabel gameWon = new GLabel("YOU WON!");
			GLabel endGame = new GLabel("YOU WON!", (getWidth() / 2.0) - (gameWon.getWidth() / 2.0), getHeight() / 2.0 - (gameWon.getAscent() / 2.0));
			removeAll();
			add(endGame);
		}
		vy = -vy;
	}

}
