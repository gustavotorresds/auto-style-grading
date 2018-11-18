
/*
 * File: Breakout.java
 * -------------------
 * Name: Helen Lin
 * Section Leader: Semir Shafi
 * 
 * This is the EXTENDED VERSION of the brick breaker game program.
 * This program will, by default, setup a 10x10 brick breaker game
 * The user will have, by default, 3 rounds to try to "break" all the bricks by hitting the colored tiles with the ball
 * The user's round ends when the ball hits the bottom of the screen
 * The user wins when all bricks have been cleared
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout2_Extension extends GraphicsProgram {

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
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	// Diamter of the ball in pixels
	public static final double BALL_DIAMETER = 2 * BALL_RADIUS;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 5.0;
	public double vy = -VELOCITY_Y;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;
	public double vx = 0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// Font to use for on-screen text
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	// Prep variables to create and track paddle
	private GRect paddle = null;
	double paddleX = 0;
	double paddleY = 0;

	// Prep variables to create and track ball
	private GOval ball = null;
	double ballX = 0;
	double ballY = 0;

	// Track mouse position
	double mouseX = 0;
	double mouseY = 0;

	// Random number generator to help determine ball launch x velocity
	private RandomGenerator rgen = new RandomGenerator();

	// Track number of bricks to determine win/loss and to display
	int countBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	GLabel brickCountLabel = null;

	// Tracks rounds of gameplay
	int i = 0;

	// Insert bounce audio
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	GLabel roundCountLabel = null;

	// Boolean to track if ball is in play (manages mouse clicking)
	boolean ballInPlay = false;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Setup the game
		buildBricks();
		createPaddle();
		addMouseListeners();

		// Play the game
		for (i = 0; i < NTURNS; i++) {
			addRoundLabel(i);

			if (ball == null) {
				createBall();
				ballInPlay = false;
			}

			// Launches ball
			waitForClick();

			// Allows ball to bounce and includes killing off the ball when it hits the
			// bottom of the screen
			bounceBall();

			remove(roundCountLabel);
		}
		if (countBricks == 0) {
			GLabel winningMessage = new GLabel("YOU WIN!!");
			winningMessage.setFont(SCREEN_FONT);
			add(winningMessage, getWidth() / 2 - winningMessage.getWidth() / 2, winningMessage.getAscent());
		} else {
			GLabel losingMessage = new GLabel("Game Over - maybe don't quit your day job");
			losingMessage.setFont(SCREEN_FONT);
			add(losingMessage, getWidth() / 2 - losingMessage.getWidth() / 2, losingMessage.getAscent());
		}
	}

	/**
	 * Method: Add Round Label
	 * 
	 * Adds a label to the screen that tells you which round you are on
	 * 
	 */
	private void addRoundLabel(int i) {
		int labelCount = i + 1;
		roundCountLabel = new GLabel("ROUND " + labelCount + " OF " + NTURNS);
		roundCountLabel.setFont(SCREEN_FONT);
		roundCountLabel.setLocation(getWidth() - roundCountLabel.getWidth(), getHeight());
		add(roundCountLabel);
	}

	/**
	 * Method: Mouse Clicked
	 * 
	 * This is a mouse event that launches the ball in a random direction on the
	 * screen
	 * 
	 */
	public void mouseClicked(MouseEvent e) {
		// First if checks to see if ball is in play, if ball is in play, mouse clicking
		// shouldn't do anything
		if (ballInPlay == false) {
			// Set random direction
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5))
				vx = -vx;
		}
	}

	/**
	 * Method: Mouse Moved
	 * 
	 * Allows paddle to follow where the mouse is
	 * 
	 */
	public void mouseMoved(MouseEvent e) {
		paddleX = e.getX() - PADDLE_WIDTH / 2;
		paddle.setLocation(paddleX, paddleY);
	}

	/**
	 * Method: Build Bricks
	 * 
	 * Places 10 rows and 10 columns of bricks on the screen: 2 rows are red, 2 are
	 * orange, 2 are yellow, 2 are green, 2 are cyan
	 * 
	 */
	private void buildBricks() {
		// Set first brick location (top left)
		double brickX = getWidth() / 2 - (NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)) / 2 + BRICK_SEP / 2;
		double brickY = BRICK_Y_OFFSET;

		for (int rowCount = 0; rowCount < NBRICK_ROWS; rowCount++) {
			placeRowOfBricks(rowCount, brickX, brickY);
			brickY = brickY + BRICK_HEIGHT + BRICK_SEP;
		}
	}

	/**
	 * Method: Place Row of Bricks
	 * 
	 * Called by buildBricks() method Places bricks side by side across the screen
	 * 
	 */
	private void placeRowOfBricks(int rowCount, double brickX, double brickY) {
		for (int columnCount = 0; columnCount < NBRICK_COLUMNS; columnCount++) {
			GRect brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);

			// select color of the brick based on the row
			if (rowCount == 0 || rowCount == 1) {
				brick.setColor(Color.RED);
			} else if (rowCount == 2 || rowCount == 3) {
				brick.setColor(Color.ORANGE);
			} else if (rowCount == 4 || rowCount == 5) {
				brick.setColor(Color.YELLOW);
			} else if (rowCount == 6 || rowCount == 7) {
				brick.setColor(Color.GREEN);
			} else if (rowCount == 8 || rowCount == 9) {
				brick.setColor(Color.CYAN);
			}

			add(brick);
			brickX = brickX + BRICK_WIDTH + BRICK_SEP;
		}

	}

	/**
	 * Method: Create Paddle
	 * 
	 * Builds the black paddle at the bottom of the screen
	 * 
	 */
	private void createPaddle() {
		paddleX = getWidth() / 2 - PADDLE_WIDTH / 2;
		paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	/**
	 * Method: Create Ball
	 * 
	 * Builds the black paddle at the bottom of the screen on to of the paddle
	 * 
	 */
	private void createBall() {
		ballX = getWidth() / 2 - BALL_RADIUS;

		// additional "-1" is so ball isn't touching the paddle at launch (avoids bottom
		// collision boolean issue in ball bounce)
		ballY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_DIAMETER - 1;
		ball = new GOval(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	/**
	 * Method: Bounce Ball
	 * 
	 * Animates the ball Allows ball to react to walls and bricks by bouncing off
	 * 
	 */
	private void bounceBall() {
		// Disable mouse clicking
		ballInPlay = true;

		// Ensure ball is being launched upwards
		vy = -VELOCITY_Y;

		// Animate ball
		while (ball != null) {
			// bounce off walls
			if (hitWindowLeft(ball) || hitWindowRight(ball)) {
				vx = -vx;
				bounceClip.play();
			}
			if (hitWindowTop(ball)) {
				vy = -vy;
				bounceClip.play();
			}

			// update visualization
			ball.move(vx, vy);

			// pause
			pause(DELAY);

			// Bounces off & removes bricks from screen and includes countBricks var, counts
			// down to 0
			clearBricks();

			// kill ball if ball hits bottom of window
			ballY = ball.getY();
			if (ballY + BALL_DIAMETER > getHeight()) {
				remove(ball);
				ball = null;
			}
		}

	}

	/**
	 * Method: Hit Window Top
	 * 
	 * Allows us to check if ball has hit the top of the screen
	 * 
	 */
	private boolean hitWindowTop(GOval ball) {
		return ball.getY() <= 0;
	}

	/**
	 * Method: Hit Window Right
	 * 
	 * Allows us to check if ball has hit the right side of the screen
	 * 
	 */
	private boolean hitWindowRight(GOval ball) {
		return ball.getX() >= getWidth() - BALL_DIAMETER;
	}

	/**
	 * Method: Hit Window Left
	 * 
	 * Allows us to check if ball has hit left side of the screen
	 * 
	 */
	private boolean hitWindowLeft(GOval ball) {
		return ball.getX() <= 0;
	}

	/**
	 * Method: Clear Bricks
	 * 
	 * This method will remove bricks from the screen as the ball hits them; the
	 * output will either continue or end the game loop by adjusting the game loop
	 * counter, "i"
	 * 
	 */
	private void clearBricks() {
		if (brickCountLabel != null) {
			remove(brickCountLabel);
		}

		GObject collider = getCollidingObject();
		if (collider != null) {
			if (collider == paddle) {
				bounceClip.play();
			} else {
				bounceClip.play();
				remove(collider);
				countBricks = countBricks - 1;
			}
		}

		// If all bricks are cleared, set i = 3 so that game loop will end
		if (countBricks == 0) {
			i = 3;
		}
		brickCountLabel = new GLabel("Bricks Remaining: " + countBricks);
		brickCountLabel.setFont(SCREEN_FONT);
		add(brickCountLabel, 0, getHeight());
	}

	private GObject getCollidingObject() {
		if (ball != null) {
			// Check upper left
			GObject collObj1 = getElementAt(ball.getX(), ball.getY());
			if (collObj1 != null) {
				if (collObj1 != paddle || collObj1 != roundCountLabel) {
					vy = -vy;
				}
				return collObj1;
			}

			// Check upper right
			GObject collObj2 = getElementAt(ball.getX() + BALL_DIAMETER, ball.getY());
			if (collObj2 != null) {
				if (collObj1 != paddle || collObj1 != roundCountLabel) {
					vy = -vy;
				}
				return collObj2;
			}

			// Check lower left
			GObject collObj3 = getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER);
			if (collObj3 != null) {
				if (collObj1 != paddle || collObj1 != roundCountLabel) {
					vx = -vx;
					vy = -vy;
				}
				return collObj3;
			}

			// Check lower right
			GObject collObj4 = getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER);
			if (collObj4 != null) {
				if (collObj1 != paddle || collObj1 != roundCountLabel) {
					vx = -vx;
					vy = -vy;
				}
				return collObj4;
			}
		}
		return null;
	}

}
