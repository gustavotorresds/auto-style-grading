/*
 * File: BreakoutExtended.java
 * -------------------
 * Name: James Peng
 * Section Leader: Semir Shafi
 * 
 * This file implements the game of Breakout with extensions (adds
 * sounds and displays number of remaining lives and bricks).
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtended extends GraphicsProgram {
	
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
	public static final int NRED_ROWS = NBRICK_ROWS / 5; // number of rows of red bricks
	public static final int NORANGE_ROWS = NBRICK_ROWS / 5; // number of rows of orange bricks
	public static final int NYELLOW_ROWS = NBRICK_ROWS / 5; // number of rows of yellow bricks
	public static final int NGREEN_ROWS = NBRICK_ROWS / 5; // number of rows of green bricks
	public static final int NCYAN_ROWS = NBRICK_ROWS - NRED_ROWS - NORANGE_ROWS - NYELLOW_ROWS - NGREEN_ROWS; // number of rows of cyan bricks
	
	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;
	
	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);
	
	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;
	
	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;
	
	// Dimensions of the paddle, in pixels
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;
	
	// Offset of the paddle up from the bottom, in pixels
	public static final double PADDLE_Y_OFFSET = 30;
	
	// Radius of the ball, in pixels
	public static final double BALL_RADIUS = 10;
	
	// The ball's vertical velocity
	public static final double VELOCITY_Y = 3.0;
	
	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-)
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;
	
	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;
	
	// Indent of lives label (from left and bottom) and total bricks label (from right and bottom), in pixels
	public static final double LABEL_INDENT = 10;
	
	// Number of turns
	public static final int NTURNS = 3;
	private int lives = NTURNS; // starting with NTURNS, tracks number of "lives" or attempts remaining
	private GLabel livesLabel = new GLabel(""); // placeholder for label displaying number of remaining lives/attempts
	
	// Tracks number of bricks
	private int totalBricks;
	private GLabel totalBricksLabel = new GLabel(""); // placeholder for label displaying number of remaining bricks
	
	// Placeholder for paddle
	private GRect paddle;
	
	// Placeholders for ball
	private GOval ball;
	private double vx, vy; // x- and y-velocity of ball
	
	// Random-number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Audio for ball colliding with brick or paddle
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	/*
	 * Sets title and canvas size, and calls methods to set up the game (create bricks,
	 * paddle, lives label, and total bricks label) and to play the game; also adds
	 * mouse listeners used to move paddle.
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Calls methods to set up game
		setUpBricks();
		setUpPaddle();
		showLives();
		showTotalBricks();
		
		// Calls methods to play game
		playGame();
		displayResult();
		
		// add mouse listeners
		addMouseListeners();
	}
	
	/*
	 * Sets up NBRICK_ROWS by NBRICK_COLUMNS of bricks, offset from the top of the display window
	 * by BRICK_Y_OFFSET and centered horizontally; the bricks have width of BRICK_WIDTH and height
	 * of BRICK_HEIGHT and are each separated by BRICK_SEP pixels. The top NRED_ROWS are red, the
	 * next NORANGE_ROWS are orange, the next NYELLOW_ROWS are yellow, the next NGREEN_ROWS are
	 * green, and the remaining bottom NCYAN_ROWS are cyan.
	 */
	private void setUpBricks() {
		double leftBound = (getWidth() - BRICK_WIDTH * NBRICK_COLUMNS - BRICK_SEP * (NBRICK_COLUMNS - 1.0)) / 2;
		double topBound = BRICK_Y_OFFSET;
		for (int i = 0; i < NRED_ROWS; i++) {
			buildRowOfBricks(leftBound, topBound, NBRICK_COLUMNS, Color.RED);
			topBound += BRICK_HEIGHT + BRICK_SEP;
		}
		for (int i = 0; i < NORANGE_ROWS; i++) {
			buildRowOfBricks(leftBound, topBound, NBRICK_COLUMNS, Color.ORANGE);
			topBound += BRICK_HEIGHT + BRICK_SEP;
		}
		for (int i = 0; i < NYELLOW_ROWS; i++) {
			buildRowOfBricks(leftBound, topBound, NBRICK_COLUMNS, Color.YELLOW);
			topBound += BRICK_HEIGHT + BRICK_SEP;
		}
		for (int i = 0; i < NGREEN_ROWS; i++) {
			buildRowOfBricks(leftBound, topBound, NBRICK_COLUMNS, Color.GREEN);
			topBound += BRICK_HEIGHT + BRICK_SEP;
		}
		for (int i = 0; i < NCYAN_ROWS; i++) {
			buildRowOfBricks(leftBound, topBound, NBRICK_COLUMNS, Color.CYAN);
			topBound += BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	/*
	 * Creates a rectangular paddle of PADDLE_WIDTH width and PADDLE_HEIGHT height, offset from the
	 * bottom of the display window by PADDLE_Y_OFFSET and centered horizontally.
	 */
	private void setUpPaddle () {
		double leftBound = (getWidth() - PADDLE_WIDTH) / 2;
		double topBound = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(leftBound, topBound, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/*
	 * Displays remaining lives/attempts on lower left-hand corner of display window.
	 */
	private void showLives() {
		livesLabel.setLabel("Lives: " + lives);
		livesLabel.setLocation(LABEL_INDENT, getHeight() - LABEL_INDENT);
		add(livesLabel);
	}
	
	/*
	 * Displays remaining bricks on lower right-hand corner of display window.
	 */
	private void showTotalBricks() {
		totalBricksLabel.setLabel("Bricks: " + totalBricks);
		totalBricksLabel.setLocation(getWidth() - LABEL_INDENT - totalBricksLabel.getWidth(), getHeight() - LABEL_INDENT);
		add(totalBricksLabel);
	}
	
	/*
	 * For each life (and while there are bricks remaining), calls methods to create
	 * ball and play that life/attempt. When life is lost, the ball is removed and
	 * the lives counter and label are updated.
	 */
	private void playGame() {
		while (lives > 0 && totalBricks > 0) {
			createBall();
			playLife();
			remove(ball);
			if (totalBricks > 0) {
				lives--;
				livesLabel.setLabel("Lives: " + lives);
			}
		}
	}
	
	/*
	 * When game is over, displays message depending on whether player wins (breaks all bricks)
	 * or loses (uses all NTURNS of lives/attempts before breaking all bricks) in middle of
	 * display window.
	 */
	private void displayResult() {
		if (totalBricks == 0) {
			remove(ball);
			GLabel finalMessage = new GLabel("YOU WIN!");
			finalMessage.setLocation((getWidth() - finalMessage.getWidth()) / 2, (getHeight() + finalMessage.getHeight()) / 2);
			add(finalMessage);
		} else {
			GLabel finalMessage = new GLabel("GAME OVER");
			finalMessage.setLocation((getWidth() - finalMessage.getWidth()) / 2, (getHeight() + finalMessage.getHeight()) / 2);
			add(finalMessage);
		}
	}
	
	/*
	 * When the mouse moves in the display window, the x-position of the paddle moves with the
	 * mouse (center of the paddle aligns with the mouse), while the y-position does not change.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if (x - PADDLE_WIDTH / 2 < 0) {
			x = PADDLE_WIDTH / 2;
		} else if (x + PADDLE_WIDTH / 2 > getWidth()) {
			x = getWidth() - PADDLE_WIDTH / 2;
		}
		paddle.setLocation(x - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}
	
	/*
	 * Builds a single row of bricks starting at (left, top), with numBricks bricks of brickColor
	 * color, BRICK_WIDTH width, and BRICK_HEIGHT height, separated by BRICK_SEP pixels.
	 */
	private void buildRowOfBricks(double left, double top, int numBricks, Color brickColor) {
		for (int i = 0; i < numBricks; i++) {
			GRect brick = new GRect(left, top, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setColor(brickColor);
			brick.setFilled(true);
			add(brick);
			totalBricks++;
			left += BRICK_WIDTH + BRICK_SEP;
		}
	}
	
	/*
	 * Creates a ball with radius of BALL_RADIUS and centers the ball in the display window
	 */
	private void createBall() {
		double leftBound = getWidth() / 2 - BALL_RADIUS;
		double topBound = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval(leftBound, topBound, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}
	
	/*
	 * For each life/attempt, sets x- and y-velocity for ball and moves ball accordingly;
	 * calls method to determine effect of ball at each movement.
	 */
	private void playLife() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		while (ball.getY() < getHeight() - BALL_RADIUS * 2 && totalBricks > 0) {
			ball.move(vx,  vy);
			ballEffect();
			pause(DELAY);
		}
	}
	
	/*
	 * Determines effect of ball based on its location (i.e., bounce off wall or paddle,
	 * break and bounce off brick, or do nothing/continue trajectory), prioritizing
	 * vertical bouncing over horizontal bouncing; bottom of ball must hit top of
	 * paddle to bounce vertically.
	 */
	private void ballEffect() {
		double x = ball.getX();
		double y = ball.getY();
		GObject yCollider = getYCollidingObject(x, y);
		GObject xCollider = getXCollidingObject(x, y);
		if  (y <= 0 || (yCollider != null && y + BALL_RADIUS * 2 < paddle.getY() + Math.abs(vy))) {
			if (yCollider != null && yCollider != livesLabel && yCollider != totalBricksLabel) {
				bounceClip.play();
				if (yCollider != paddle) {
					brickRemove(yCollider);
				}
			}
			vy = -vy;
		} else if (x <= 0 || x >= getWidth() - BALL_RADIUS * 2 || (xCollider != null && xCollider != livesLabel && xCollider != totalBricksLabel)) {
			if (x < 0) {
				ball.setX(0); // prevents ball from getting "stuck" on left wall (if hit paddle/brick and wall at same time)
			} else if (x > getWidth() - BALL_RADIUS * 2)
				ball.setX(getWidth()- BALL_RADIUS * 2); // prevents ball from getting "stuck" on right wall (if hit paddle/brick and wall at same time)
			if (xCollider != null && xCollider != livesLabel && xCollider != totalBricksLabel) {
				bounceClip.play();
				if (xCollider != paddle) {
					brickRemove(xCollider);
				}
			}
			vx = -vx;
		}
	}
	
	/*
	 * Returns GObject if touching top or bottom of ball located at (x, y), null otherwise.
	 * To ensure no object "slips" through top or bottom of ball, checks for object every
	 * min(BRICK_WIDTH, PADDLE_WIDTH) pixels along top and bottom of ball. Note that this
	 * is not checking ball's corners (to prevent "bounce back").
	 */
	private GObject getYCollidingObject(double x, double y) {
		int layer = 1; // tracks segments along horizontal plane being tested for whether top/bottom of ball is colliding with an object
		double leftBound = x; // stores ball's original x
		double minWidth = Math.min(BRICK_WIDTH,  PADDLE_WIDTH);
		x += Math.abs(vx); // do not want to check ball's corners
		while (getElementAt(x, y) == null && getElementAt(x, y + BALL_RADIUS * 2) == null && layer * minWidth < BALL_RADIUS * 2) {
			x += minWidth;
			layer++;
		}
		if(getElementAt(x, y) != null) {
			return getElementAt(x, y);
		} else if (getElementAt(x, y + BALL_RADIUS * 2) != null) {
			return getElementAt(x, y + BALL_RADIUS * 2);
		} else if (getElementAt(leftBound + BALL_RADIUS * 2 - Math.abs(vx), y) != null) {
			return getElementAt(leftBound + BALL_RADIUS * 2 - Math.abs(vx), y);
		} else {
			return getElementAt(leftBound + BALL_RADIUS * 2 - Math.abs(vx), y + BALL_RADIUS * 2);
		}
	}
	
	/*
	 * Returns GObject if touching either side of ball located at (x, y), null otherwise.
	 * To ensure no object "slips" through left or right side of ball, check for object
	 * every min(BRICK_HEIGHT, PADDLE_HEIGHT) pixels along sides of ball. Note that this
	 * is not checking ball's corners (to prevent "bounce back").
	 */
	private GObject getXCollidingObject(double x, double y) {
		int layer = 1; // tracks segments along vertical plane being tested for whether sides of ball is colliding with an object
		double topBound = y; // stores ball's original y
		double minHeight = Math.min(BRICK_HEIGHT,  PADDLE_HEIGHT);
		y += Math.abs(vy); // do not want to check ball's corners
		while (getElementAt(x, y) == null && getElementAt(x + BALL_RADIUS * 2, y) == null && layer * minHeight < BALL_RADIUS * 2) {
			y += minHeight;
			layer++;
		}
		if(getElementAt(x, y) != null) {
			return getElementAt(x, y);
		} else if (getElementAt(x + BALL_RADIUS * 2, y) != null) {
			return getElementAt(x + BALL_RADIUS * 2, y);
		} else if (getElementAt(x, topBound + BALL_RADIUS * 2 - Math.abs(vy)) != null) {
			return getElementAt(x, topBound + BALL_RADIUS * 2 - Math.abs(vy));
		} else {
			return getElementAt(x + BALL_RADIUS * 2, topBound + BALL_RADIUS * 2 - Math.abs(vy));
		}
	}
	
	/*
	 * Removes brick obj and updates totalBricks and totalBricksLabel.
	 */
	private void brickRemove(GObject obj) {
		remove(obj);
		totalBricks--;
		totalBricksLabel.setLabel("Bricks: " + totalBricks);
	}

}
