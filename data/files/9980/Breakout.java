/*
 * File: Breakout.java
 * -------------------
 * Name: Vincent Nicandro
 * Section Leader: Rachel Gardner
 * 
 * Implements the game of Breakout without extensions.
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

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setup();
		playBreakout();	
	}

	// Sets up screen with bricks and paddle
	private void setup() {
		addMouseListeners();
		createBricks();
		createPaddle();
	}
	
	// Runs full game of Breakout and launches end titles depending on if
	// game ended as win or loss.
	private void playBreakout() {
		count = NBRICK_COLUMNS * NBRICK_ROWS;	// Sets count as number of bricks at start of game
		for (int i = 0; i < NTURNS; i++) {	// Limits regeneration of ball to three lives
			createBall();
			waitForClick();		// Waits for player to click to launch ball motion
			moveBall();
			if (count == 0) {		// If player has cleared all bricks, break from loop to launch end
				break;
			}
		}
		endGame();
	}
	
	// Creates brick pattern in rainbow pattern
	private void createBricks() {
		double y = BRICK_Y_OFFSET;
		
		// Creates a row of NBRICK_COLUMNS bricks for every NBRICK_ROWS rows
		for (int row = 0; row < NBRICK_ROWS; row++) {
			double x = (getWidth() - (10 * BRICK_WIDTH) - (9 * BRICK_SEP))/2;
			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(colorBrick(row));
				add(brick);
				x += BRICK_WIDTH + BRICK_SEP;
			}
			y += BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	// Returns color based on which row brick is in
	private Color colorBrick(int row) {
		if (row % 10 < 2) {
			return Color.RED;
		} else if (row % 10 < 4) {
			return Color.ORANGE;
		} else if (row % 10 < 6) {
			return Color.YELLOW;
		} else if (row % 10 < 8) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}
	
	// Creates paddle at bottom of screen
	private void createPaddle() {
		paddle = new GRect((getWidth() - PADDLE_WIDTH)/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	// When mouse is moved, paddle moves accordingly left or right to match mouse movement
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		// If mouse causes paddle to be cropped out of screen, keeps paddle within bounds of screen
		if (x < PADDLE_WIDTH/2) {
			x = PADDLE_WIDTH/2;
		} else if (x > (getWidth() - PADDLE_WIDTH/2)) {
			x = getWidth() - PADDLE_WIDTH/2;
		}
		// Mouse is kept at same y-component offset
		paddle.setBounds(x - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, 
				PADDLE_WIDTH, PADDLE_HEIGHT);
	}
	
	// Generates ball at center of screen
	private void createBall() {
		ball = new GOval((getWidth() - 2 * BALL_RADIUS)/2, (getHeight() - 2 * BALL_RADIUS)/2, 
				2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	// Animates movement of ball for ball's lifetime
	private void moveBall() {
		// Generates random x-component for speed of ball
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;	// Randomly determines if x-component is negative or positive
		}
		vy = VELOCITY_Y;
		
		while (true) {
			if (count == 0) {	// Breaks ball movement if player has won the game
				break;
			}
		
			double x = ball.getCenterX() + vx;
			double y = ball.getCenterY() + vy;
			
			// If ball reaches bottom edge of screen, ball is void
			if (y > getHeight() - BALL_RADIUS) {
				remove(ball);
				return;
			}
			
			// Bounces ball from sides and top of screen
			if (x < BALL_RADIUS || x > getWidth() - BALL_RADIUS) {
				vx = -vx;
			}
			if (y < BALL_RADIUS) {
				vy = -vy;
			}
			
			checkForCollisions();	// Checks for collisions (from brick or paddle)
						
			ball.setCenterLocation(ball.getCenterX() + vx, ball.getCenterY() + vy);
			pause(DELAY);
		}
	}
	
	// Checks for collisions: if collider is brick, removes brick and decreases count
	// by one, then ball bounces off object. If collider is paddle, ball bounces off paddle.
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		if (collider != null) {		// If collider isn't null, means it's either paddle or brick
			if (collider == paddle) {	// If paddle, ensure ball moves in upwards direction
				vy = -Math.abs(vy);
			} else {	// Otherwise, object is brick, so remove brick, decrease count, and reverse y-component of velocity
				remove(collider); 
				count--;
				vy = -vy;
			}
		}
	}
	
	// Returns object ball collides into, if it exists, by checking corners of GOval
		private GObject getCollidingObject() {
			if (getElementAt(ball.getX(), ball.getY()) != null) {	// Checks top-left corner of GOval
				return getElementAt(ball.getX(), ball.getY());
			} else if (getElementAt(ball.getRightX(), ball.getY()) != null) {	// Checks top-right corner of GOval
				return getElementAt(ball.getRightX(), ball.getY());
			} else if (getElementAt(ball.getX(), ball.getBottomY()) != null) {	// Checks bottom-left corner of GOval
				return getElementAt(ball.getX(), ball.getBottomY());
			} else if (getElementAt(ball.getRightX(), ball.getBottomY()) != null) {		// Checks bottom-right corner of GOval
				return getElementAt(ball.getRightX(), ball.getBottomY());
			} else {
				return null;
			}
		}
	
	// Prints end credits and notifies players whether they won or lost the game
	private void endGame() {
		if (count == 0) {	//  If player has cleared bricks, clears screen and announces win; else, announces loss
			clear();
			GLabel win = new GLabel("You won!");
			win.setFont("Courier-24");
			add(win, (getWidth() - win.getWidth())/2, (getHeight() + win.getAscent())/2);
		} else {
			GLabel lose = new GLabel("You lost!");
			lose.setFont("Courier-24");
			add(lose, (getWidth() - lose.getWidth())/2, (getHeight() + lose.getAscent())/2);
		}
	}
	
	// Private instance variables
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private int count;
	private RandomGenerator rgen = RandomGenerator.getInstance();
}
