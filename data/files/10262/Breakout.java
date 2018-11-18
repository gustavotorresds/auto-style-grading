/*
 * File: Breakout.java
 * -------------------
 * Name: Nic Becker
 * Section Leader: Ella TL
 * 
 * Implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels; used when setting up the initial size of the game,
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
	public static final double PADDLE_Y_OFFSET = 50;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 8.0; // default: 3.0

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Brick counter
	private int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;

	// Instance variable for the paddle
	private GRect paddle = null;
	
	// Instance variables for the ball and velocity
	private GOval ball = null;
	private double vx, vy;
	
	// Make a random generator instance variable
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setupGame();
		playGame();
	}
	
	/* 
	 * Sets up bricks and paddle on the canvas as determined by above constants
	 */

	private void setupGame() {
		createBricks();
		createPaddle();
	}
	
	/*
	 * Creates ball, generates initial velocity, and begins play loop
	 */
	
	private void playGame() {
		int lives = NTURNS;
		createBall();
		waitForClick();
		while(lives > 0 && bricksRemaining != 0) {
			// bounce with wall and remove bricks with collisions
			bounceIfWall();
			bounceIfObject();
			// animate movement
			ball.move(vx, vy);
			pause(DELAY);
			if (bottomWallHit()) {
				lives--;
				resetGame(lives);
			}
		}
		winOrLose(lives);
	}
	
	/*
	 * Moves paddle with mouse as long as mouse is within window
	 */

	public void mouseMoved(MouseEvent e) {
		updatePaddle(e.getX());
	}
	
	/*
	 * Updates the paddle's position with mouse location
	 */
	
	private void updatePaddle(int mouseX) {
		double x;
		if (mouseX - PADDLE_WIDTH/2 <= 0) {
			x = 0;
		} else if (mouseX + PADDLE_WIDTH/2 >= getWidth()) {
			x = getWidth() - PADDLE_WIDTH;
		} else {
			x = mouseX - PADDLE_WIDTH/2;
		}
		paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);
	}

	/* 
	 * Create bricks determined by above constants and colors them
	 */
	
	private void createBricks() {
		for (int j = 0; j < NBRICK_ROWS; j++) {
			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				GRect brick = new GRect((CANVAS_WIDTH - NBRICK_COLUMNS*(BRICK_WIDTH) - (NBRICK_COLUMNS-1)*BRICK_SEP)/2+(BRICK_WIDTH+BRICK_SEP)*i, BRICK_Y_OFFSET+(BRICK_HEIGHT+BRICK_SEP)*j, BRICK_WIDTH, BRICK_HEIGHT);
				Color color = getRowColor(j);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick);
			}
		}	
	}
	
	/*
	 *  Colors rows in red, orange, yellow, green, cyan pattern. All rows over 8 default to cyan.	
	 */

	private Color getRowColor(int row) {
		if (row == 0 || row == 1) {
			return Color.RED;
		} else if (row == 2 || row == 3) {
			return Color.ORANGE;
		} else if (row == 4 || row == 5) {
			return Color.YELLOW;
		} else if (row == 6 || row == 7) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}
	
	/*
	 * Returns true of ball has passed the bottom of the window
	 */

	private boolean bottomWallHit() {
		if (ball.getY() >= getHeight()) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Bounces ball if in contact with object, and initiates bounce
	 * Paddle only returns ball in negative y direction
	 */

	private void bounceIfObject() {
		GObject collider = getCollidingObject();
		if (collider != null && !(collider instanceof GImage)) {
			if (collider == paddle) {
				vy = -Math.abs(vy);
			} else {
				vy = -vy;
				remove(collider);
				bricksRemaining--;
			}
		}
	}
	
	/*
	 * Creates ball based on radius defined above, initializes velocity
	 */

	private void createBall() {
		ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		// choose random horizontal velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		// choose random left/right direction
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	
	/*
	 * Creates paddle as determined by above constants
	 */
	
	private void createPaddle() {
		paddle = new GRect(getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/*
	 * Returns object if ball collides with paddle or bricks, returns null otherwise
	 */
	
	private GObject getCollidingObject() {
		// check for object at each of the ball's corners and return that object or null
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		} else {
			return null;
		}
	}
	
	/*
	 * Bounces ball off walls
	 */

	private void bounceIfWall() {
		// bounces ball if right, left, or top walls are hit
		if (ball.getX() <= 0) {
			vx = -vx;
		} else if (ball.getY() <= 0) {
			vy = -vy;
		} else if (ball.getX() + 2*BALL_RADIUS >= getWidth()) {
			vx = -vx;
		}	
	}

	/*
	 * Resets game for remaining lives
	 */
	
	private void resetGame(int lives) {
		if (lives > 0) {
			remove(ball);
			createBall();
			waitForClick();
		} 
	}

	/*
	 * Prompts user on end of game status
	 */

	private void winOrLose(int lives) {
		if (bricksRemaining == 0) {
			GLabel label = new GLabel("You win!");
			GLabel youWin = new GLabel("You win!", getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getHeight()/2);
			remove(ball);
			add(youWin);
		} else if (lives == 0){
			GLabel label = new GLabel("You lose!");
			GLabel youLose = new GLabel("You lose!", getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getHeight()/2);
			add(youLose);
		}
	}
}
