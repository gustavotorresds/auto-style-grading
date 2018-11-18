/*
 * File: Breakout.java
 * -------------------
 * Name: Jonathan Morales
 * Section Leader: Brahm Capoor
 * 
 * This subclass implements the game Breakout! The player has
 * three balls to remove all of the bricks. If successful, 
 * the player wins. 
 * 
 * Extensions: 
 * - Sound
 * - Lives label
 * - Game restart
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

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
	public static int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	//private instance variables
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GRect paddle = null;
	private GOval ball = null;
	private GLabel liveslabel;
	private double vx, vy;
	private double brickCount = 0;

	/**
	 * Method: Run
	 * --------------------
	 * Execution starts here 
	 */
	public void run() {
		setupGame();
		playBreakout();
	}

	private void setupGame() {

		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Sets up bricks and paddle
		setupBricks();
		setupPaddle();

		// displays balls left
		updateLives();
	}

	private void setupBricks() {
		// nested for loop to set up the brick structure 
		for (int r = 0; r < NBRICK_ROWS; r++) {
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = (BRICK_WIDTH + BRICK_SEP) * c;
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT+BRICK_SEP) * r;
				GRect brick = new GRect(x+5, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				setColors(r, brick);
				add(brick);
			}
		}
	}

	// method to fill in the color of the rows using parameters
	private void setColors(int row, GRect brick) {
		row %= 20;
		if(row <= 1) { brick.setColor(Color.RED);
		} else if(row <= 3) { brick.setColor(Color.ORANGE);
		} else if(row <= 5) { brick.setColor(Color.YELLOW);
		} else if(row <= 7) { brick.setColor(Color.GREEN);
		} else if(row <= 9) { brick.setColor(Color.CYAN);	
		}
	}

	private void setupPaddle() { 
		double x = (getWidth() - PADDLE_WIDTH)/2;
		double y = getHeight() - PADDLE_Y_OFFSET; 
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, x, y);
		addMouseListeners();
	}

	// mouse listeners to track cursor movement on paddle
	public void mouseMoved(MouseEvent e) {

		// tracking x movement cursor
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET;

		// set location to cursor x plus standard y
		paddle.setLocation(x, y);

		// if cursor is beyond 0, repositions to 0
		if(x < 0) {
			paddle.setLocation(0, y);

			// if cursor is beyond (or equal) to max
			// canvas width, repositions to max width
		} else if(x >= getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, y);
		}
	}

	private void playBreakout() {

		// adds ball with given velocity values
		ball = addBall(); 
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}

		// waits for user to click before starting
		waitForClick();

		while(true) {

			// checks for collisions
			checkCollisions();

			// initial ball movement
			ball.move(vx, vy);

			// checks if the player is out of turns
			if(NTURNS == 0) {
				gameOver();
				break;
			}

			// checks if the player has removed all 
			// of the bricks
			if(brickCount == 100) {
				playerWin();
			}

			// velocity change based on wall hit
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			if(hitBottomWall(ball)) {
				remove(ball);
				waitForClick();
				addBall();
				NTURNS--;
				remove(liveslabel);
				updateLives();
			}
			// delay for animation to be seen
			pause(DELAY);
		}
	}

	private void checkCollisions() {

		// adds audio!
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

		// initializes collider object if one exists
		GObject collider = getCollidingObject();

		if(collider == paddle) {
			// accounts for sticky paddle
			vy = -Math.abs(vy);
		} else if(collider != null) {
			remove(collider);
			bounceClip.play();
			vy = -vy;
			// counts bricks removed
			brickCount++;
		} 
	}


	private GObject getCollidingObject() {

		double x = ball.getX();
		double y = ball.getY();

		// gets the coordinates of the four points on the ball
		// and stores them to a GObject
		GObject northWest= getElementAt(x,y);
		GObject northEast = getElementAt(x+(BALL_RADIUS*2), y);
		GObject southWest = getElementAt(x,y+(BALL_RADIUS*2));
		GObject southEast = getElementAt(x+(BALL_RADIUS*2), y+(BALL_RADIUS*2));

		// if there is something at one of the four points, 
		// an object is returned 
		if(northWest != null) { return northWest;
		} else if(northEast != null) { return northEast;
		} else if(southWest != null) { return southWest;
		} else if(southEast != null) { return southEast;
		} return null;
	}

	// creates the ball and returns it
	private GOval addBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		return ball;
	}

	// these four booleans check to see if the 
	// ball hits a wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - BALL_RADIUS*2;
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	// announces when the player loses
	private void gameOver() {
		GLabel label = new GLabel("Game Over!");
		// not sure why this label wasn't centering
		double x = ((getWidth()-40) - label.getWidth())/2;
		double y = (getHeight() - label.getAscent())/2;
		label.setFont(SCREEN_FONT);
		add(label, x, y);
		waitForClick();
		restartGame(label, x, y);
	}

	// announces victory if all bricks removed
	private void playerWin() {
		remove(ball);
		GLabel label = new GLabel("You Win!");
		double x = ((getWidth()-40) - label.getWidth())/2;
		double y = (getHeight() - label.getAscent())/2;
		label.setFont(SCREEN_FONT);
		add(label, x, y);
	}

	// displays balls left
	private GLabel updateLives() { 
		liveslabel = new GLabel("Balls Left: " + NTURNS);
		double x = getWidth()/30;
		double y = getHeight()/8 - liveslabel.getAscent();
		liveslabel.setFont("Helvetica-9");
		add(liveslabel, x, y);
		return liveslabel;
	}

	// restarts game
	private void restartGame(GLabel label, double x, double y) {
		remove(label);
		GLabel retry = new GLabel ("Click to Try Again!");
		retry.setFont(SCREEN_FONT);
		add(retry, x-20, y);
		waitForClick();
		remove(retry);
		remove(ball);
		remove(liveslabel);
		NTURNS = 3;
		updateLives();
		playBreakout();
	}
}