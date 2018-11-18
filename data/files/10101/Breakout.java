/*
 * File: Breakout.java
 * -------------------
 * Name: Michal Skreta
 * Section Leader: Avery Wang
 * 
 * This program implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These are used when setting up the initial size of the game,
	// in later calculations getWidth() and getHeight() are used
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
	
	// Number of bricks left on the screen
	int bricksOnScreen = NBRICK_COLUMNS * NBRICK_ROWS;
	
	// Number of the current round
	int roundNumber = 1;
	
	private GRect paddle = null;
	
	private GOval ball = null;
	
	private double vx, vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		setTitle("CS 106A Breakout");
		// Sets the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setup();
		playGame();
		addMouseListeners();
	}
	
	/*
	 * Method: Set Up
	 * --------------
	 * Makes all "physical" elements needed for
	 * the game to work visible on the canvas.
	 * 
	 * Precondition: Canvas set.
	 * 
	 * Postcondition: All "physical" elements needed for
	 * the game to work visible on the canvas.
	 */
	private void setup() {
		setUpBricks();
		createPaddle();
		createBall();
	}

	/*
	 * Method: Set Up Bricks
	 * --------------
	 * Creates a set of bricks in the upper
	 * portion of the canvas.
	 * 
	 * Precondition: Canvas set.
	 * 
	 * Postcondition: A set of bricks in the upper
	 * portion of the canvas is created.
	 */
	private void setUpBricks() {
		for (double row = 0; row < NBRICK_ROWS; row++) {
			for (double rowBricks = 0; rowBricks < NBRICK_COLUMNS; rowBricks++) {
				double x = (getWidth() / 2) - (NBRICK_COLUMNS * BRICK_WIDTH + BRICK_SEP * (NBRICK_COLUMNS - 1)) / 2 + rowBricks * (BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				if (row % 10 == 0 || row % 10 == 1) {
					brick.setColor(Color.RED);
				}
				if (row % 10 == 2 || row % 10 == 3) {
					brick.setColor(Color.ORANGE);
				}
				if (row % 10 == 4 || row % 10 == 5) {
					brick.setColor(Color.YELLOW);
				}
				if (row % 10 == 6 || row % 10 == 7) {
					brick.setColor(Color.GREEN);
				}
				if (row % 10 == 8 || row % 10 == 9) {
					brick.setColor(Color.CYAN);
				}
				brick.setFilled(true);
				add(brick);
			}
		}
	}
	
	/*
	 * Method: Create Paddle
	 * ---------------------
	 * Creates a paddle in the lower
	 * portion of the screen.
	 * 
	 * Precondition: Canvas set.
	 * 
	 * Postcondition: A paddle in the lower
	 * portion of the screen is created.
	 */
	private void createPaddle() {
		paddle = new GRect (getWidth() - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/*
	 * Method: Mouse Moved
	 * -------------------
	 * Moves the paddle.
	 * 
	 * Precondition: Canvas set.
	 * 
	 * Postcondition: None.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle.setLocation(x, y);
		if (e.getX() - PADDLE_WIDTH / 2 <= 0) {
			paddle.setX(0);
		}
		if (e.getX() >= (getWidth() - PADDLE_WIDTH)) {
			paddle.setX(getWidth() - PADDLE_WIDTH);
		}
	}
	
	/*
	 * Method: Create Ball
	 * ---------------------
	 * Creates a ball in the central
	 * portion of the screen.
	 * 
	 * Precondition: Canvas set.
	 * 
	 * Postcondition: A ball in the central
	 * portion of the screen is created.
	 */
	private void createBall() {
		ball = new GOval (BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
	}
	
	/*
	 * Method: Play Game
	 * -----------------
	 * Makes the whole game animate.
	 * 
	 * Precondition: Canvas set and
	 * "physical" elements added to it.
	 * 
	 * Postcondition: None.
	 */
	private void playGame() {
		startTurn();
		while(bricksOnScreen > 0) {
			ball.move(vx, vy);
			pause(DELAY);
			if (hitRightWall(ball) || hitLeftWall(ball)) {
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			if (hitBottomWall(ball)) {
				roundNumber ++;
				if (roundNumber <= NTURNS) {
				startTurn();
				} else {
					remove(ball);
					remove(paddle);
					GLabel lose = new GLabel("YOU LOST");
					add(lose, getWidth() / 2 - lose.getWidth() / 2, getHeight() / 2 + lose.getAscent() / 2);
					break;
				}
			}	
			collisions();
		}
		gameWon();
	}
	
	/*
	 * Method: Collisions
	 * ------------------
	 * Determines action that needs to be
	 * taken in case of collisions.
	 * 
	 * Precondition: Canvas set and
	 * "physical" elements added to it, game in motion.
	 * 
	 * Postcondition: None.
	 */
	private void collisions() {
		GObject collider = getCollidingObject();
		if (collider != null) {
			if (collider == paddle) {
				vy = -vy;
				ball.move(0, -BALL_RADIUS);
			} else { // when the ball collides with a brick
				vy = -vy;
				remove(collider);
				bricksOnScreen --;
			}
		}
	}
	
	/*
	 * Method: Game Won
	 * ----------------
	 * Displays a gratifying message to the player.
	 * 
	 * Precondition: Canvas set and
	 * "physical" elements added to it, game in motion.
	 * 
	 * Postcondition: None.
	 */
	private void gameWon() {
		if (bricksOnScreen == 0) {
			remove(ball);
			remove(paddle);
			GLabel win = new GLabel("YOU WON");
			add(win, getWidth() / 2 - win.getWidth() / 2, getHeight() / 2 + win.getAscent() / 2);
			}
	}
	
	/*
	 * Method: Start Turn
	 * ------------------
	 * Sets the initial conditions of a given round.
	 * 
	 * Precondition: Canvas set and
	 * "physical" elements added to it.
	 * 
	 * Postcondition: Ball moving.
	 */
	private void startTurn() {
		ball.setLocation(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	
	/*
	 * Method: Hit Right Wall
	 * ----------------------
	 * Checks if the ball hits the right wall.
	 * 
	 * Precondition: Ball moving.
	 * 
	 * Postcondition: None.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - BALL_RADIUS * 2;
	}
	
	/*
	 * Method: Hit Left Wall
	 * ---------------------
	 * Checks if the ball hits the left wall.
	 * 
	 * Precondition: Ball moving.
	 * 
	 * Postcondition: None.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	/*
	 * Method: Hit Top Wall
	 * ----------------------
	 * Checks if the ball hits the top wall.
	 * 
	 * Precondition: Ball moving.
	 * 
	 * Postcondition: None.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/*
	 * Method: Hit Bottom Wall
	 * ----------------------
	 * Checks if the ball hits the bottom wall.
	 * 
	 * Precondition: Ball moving.
	 * 
	 * Postcondition: None.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - BALL_RADIUS * 2;
	}
	
	/*
	 * Method: Get Colliding Object
	 * ----------------------------
	 * Gets the object that collided with
	 * the corners of the ball.
	 * 
	 * Precondition: Ball moving.
	 * 
	 * Postcondition: None.
	 */
	private GObject getCollidingObject() {
		GObject upperLeftCorner = getElementAt(ball.getX(), ball.getY());
		if (upperLeftCorner == null) {
			GObject lowerLeftCorner = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
			if (lowerLeftCorner == null) {
				GObject upperRightCorner = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
				if (upperRightCorner == null) {
					GObject lowerRightCorner = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
					if (lowerRightCorner == null) {
						return null;
					} else {
						return lowerRightCorner;
					}
				} else {
					return upperRightCorner;
				}
			} else {
				return lowerLeftCorner;
			}
		} else {
			return upperLeftCorner;
		}
	}

}