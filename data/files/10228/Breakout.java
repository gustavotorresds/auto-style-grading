/*
 * File: Breakout.java
 * -------------------
 * Name: Victoria Valverde
 * Section Leader: Kaitlyn Lagattuta
 * This implements Breakout!
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
	
	// Number of bricks total
	public static final int NBRICKS = NBRICK_COLUMNS * NBRICK_ROWS;

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

	/**
	 * Instance variables that I want to be visible by all program.
	 * The one that is not obvious/totally necessary is "lives". The reason why I chose it is because I
	 * want to return it to the main run () method to determine if you win or lose. However, I don't 
	 * want to set my playGame() equal to lives.
	 */
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GRect paddle = null;
	private GOval ball = null;
	private int lives = NTURNS;
	private double vx = 0, vy = 0;
	private GLabel speedMsg = null;
	
	/**
	 * This method is at a very high level. It sets the window, the setup (bricks, paddle, and mouseListeners),
	 * plays the game and then return the outcome of the game (win or lose).
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// set up
		setUpBricks ();
		setPaddle ();
		addMouseListeners ();
		// play
		playGame ();
		// end game
		endGame ();
	}

	/** Creates all rows of bricks and colors them*/
	private void setUpBricks() {
		Color color = Color.RED;
		for (int row = 1; row <= NBRICK_ROWS; row++) {
			double x0 = xLocationFirstBrick ();							// stays constant in all rows.
			double y0 = yLocationFirstBrick (row);						//changes between rows, must take "row" parameter.
			if (row > 2 && row % 2 == 1) {								//determines color at every uneven row.
				color = colorRow (color);
			}
			drawRow (x0, y0, row, color);
		}	
	}

	
	/** Finds x-location of the first brick in all rows (distance from left wall) */
	private double xLocationFirstBrick() {
		double midpoint = getWidth() / 2;
		double distanceFromMidpoint = ( NBRICK_COLUMNS*BRICK_WIDTH + (NBRICK_COLUMNS - 1)*BRICK_SEP ) / 2;   //accounts for space between bricks and width of bricks.
		double x = midpoint - distanceFromMidpoint;
		return x;
	}
	
	
	/** Finds y-location depending on row by finding the constant change in y between rows */
	private double yLocationFirstBrick(int row) {
		double dy = BRICK_HEIGHT + BRICK_SEP;
		double y = BRICK_Y_OFFSET + (row - 1)*dy;
		return y;
	}
	
	/**
	 * Method to determine color of the line based on the previous color. That way, if there are more than 10 rows,
	 * they are still colored in in the same sequence. The only tricky part was to start the loop again changing to RED.
	 */
	private Color colorRow(Color color) {
		if (color == Color.RED) {
			color = Color.ORANGE;
		} else if ( color == Color.ORANGE) {
			color = Color.YELLOW;
		} else if ( color == Color.YELLOW) {
			color = Color.GREEN;
		} else if ( color == Color.GREEN) {
			color = Color.CYAN;
		} else if ( color == Color.CYAN) {
			color = color.RED;
		}
		return color;
	}

	
	/** Draws and colors a row by taking an the previously calculated inputs of x0, y0, row and color */
	private void drawRow(double x0, double y0, int row, Color color) {
		for (int column = 1; column <= NBRICK_COLUMNS; column++) {
			double x = x0 + (BRICK_WIDTH + BRICK_SEP) * (column -1);
			double y = y0; 																	// not needed, but just to make x and y stylistically parallel
			GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled (true);
			brick.setColor (color);
			add (brick);
		}
	}
	
	
	/** Created the paddle and adds it centered at the bottom of the window */
	private void setPaddle() {
		double xpaddle = (getWidth() - PADDLE_WIDTH) / 2 ;
		double ypaddle = getHeight () - (PADDLE_Y_OFFSET + PADDLE_HEIGHT) ;
		paddle = new GRect (xpaddle, ypaddle, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled (true);
		add (paddle);
	}
	
	
	/** As soon as mouse moves, this program starts running and tracks the mouse only on the x direction to move the paddle */
	public void mouseMoved (MouseEvent e) {
		double xCornerPaddle = e.getX() - PADDLE_WIDTH / 2;
		double yCornerPaddle = paddle.getY();
		if (paddleFitsInWindow (xCornerPaddle) ){
			paddle.setLocation (xCornerPaddle, yCornerPaddle);
		}
	}

	
	/** Checks that paddle length fits fully in window at all times and doesn't let it move further. This is mainly on the right wall */
	private boolean paddleFitsInWindow (double xCornerPaddle) {
		double maxMouseX = getWidth() - PADDLE_WIDTH;							// since I keep track of the top left corner oft the paddle.
		if (xCornerPaddle >= 0 && xCornerPaddle <= maxMouseX) {
			return true;
		} else {	
			return false;
		}
	}
	
	
	/** This method lets you play the game. It keeps track of the number of bricks you have left to determine when to leave the game if you win.
	 * It also keeps track of lives to determine at the end if you have won (lives left) or you have lost (ran out of lives).
	 */
	private void playGame() {
		lives = NTURNS;
		int bricksLeft = NBRICKS;
		while (lives != 0) {
			showLivesLeftAndWaitToStart (lives);
			makeBall();
			bricksLeft = makeBallMove (bricksLeft);
			if (bricksLeft == 0) break;
			lives--;
		}
	}
	
	
	/** Shows the user how many lives they have left before they start the round. Also waits for them to click to start playing and removes the message */
	private void showLivesLeftAndWaitToStart(int lives) {
		GLabel livesLeft = new GLabel (" You have " + lives + " lives left. Click to START! ");
		double x = (getWidth() - livesLeft.getWidth() ) / 2;
		double y = (getHeight () - livesLeft.getHeight()) / 2;
		livesLeft.setLocation (x, y);
		add (livesLeft);
		waitForClick ();
		remove (livesLeft);
	}

	
	/** Makes ball and adds it to the center of the screen */
	private void makeBall() {
		double xCornerBall = getWidth() / 2 - BALL_RADIUS;
		double yCornerBall = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval (xCornerBall, yCornerBall, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled (true);
		add (ball);	
	}
	
	/**
	 * Animation loops that keeps ball moving. It sets vx (random at start) and vy (given) initial values, it waits for click from user,
	 * changes velocities depending on the different obstacles the ball encounters, and it updates the location of the ball.
	 * IMPORTANT = it will return bricks left to the playGame() ( see reasons in playGame () description).
	 */
	private int makeBallMove (int bricksLeft) {
		vx = initialvx();
		vy = VELOCITY_Y;
		waitForClick ();
		while (true) {
			// UPDATE velocities and bricks left in these cases
			bricksLeft = checkForBallColisions ( bricksLeft);
			// TERMINATE in these cases	
			if ( hitBottomWall (ball) || bricksLeft == 0) {
				remove (ball);
				break;
			}
			// update graphics
			ball.move(vx, vy);
			// pause at different stages in game
			pause (DELAY);
		}
		return bricksLeft;
	}

	
	/** Randomly assigns initial vx */
	private double initialvx () {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		return vx;
	}
	
	
	/**
	 * Checks for all the different situations in which the program must change direction. It does so by checking the ball's surroundings and  "colliders".
	 * It also deals with the special case of the "sticky paddle" : when the ball is stuck inside and constantly redirecting its vertical velocity.
	 */
	private int checkForBallColisions ( int bricksLeft) {
		GObject collider = getColliderObject ();
		// different changes in direction
		if (hitLeftWall (ball) || hitRightWall (ball)) vx = -vx;
		if (hitTopWall (ball)) vy = -vy;
		if (collider != null) {
			if (collider == paddle) {
				vy = Math.abs (vy) * -1;  // always want it to go up even inside paddle!
			} else {
				vy = -vy;
				remove (collider);
				bricksLeft --;
				if (speedMsg != null) remove (speedMsg);
				updateSpeed (bricksLeft);
			}
		}
		return bricksLeft;
	}
	

	/** Checks for collider objects with ball. It checks all 4 corner surrounding ball until it finds a collider. If after checking the 4 corners
	 *  it doesn't find a collider, it return null.
	 */
	private GObject getColliderObject() {
		GObject collider =null;
		for (double i = 0; i < 2; i ++) {
			for (double j = 0; j < 2; j ++) {
				double x = ball.getX() + (BALL_RADIUS*2)*i;
				double y = ball.getY() + (BALL_RADIUS*2)*j;
				collider = getElementAt (x, y);
				if (collider != null) return collider;
			}
		}
		return collider;
	}
	
	
	/** sets conditions for walls */
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
		return ball.getY() >= getHeight() - BALL_RADIUS*2;
	}

	
	/** Increases speed every time you eliminate 2 times the number of bricks you ave already eliminated */
	private void updateSpeed (int bricksLeft) {
		if (bricksLeft == NBRICKS - 5) {
			notifyPlayer();
			increaseSpeed ();
		}
		if (bricksLeft == NBRICKS - 10) {
			notifyPlayer();
			increaseSpeed();
		}
		if (bricksLeft == NBRICKS - 20) {
			notifyPlayer();
			increaseSpeed();
		}
	}

	
	/** notifies player that they are speeding up. The message will be removed when the next brick is hit */
	private void notifyPlayer() {
		speedMsg = new GLabel (" Great job! Let's go faster!");
		double x = (getWidth() - speedMsg.getWidth() ) / 2;
		double y = BRICK_Y_OFFSET / 2;
		speedMsg.setLocation (x, y);
		add (speedMsg);
	}

	
	/** increases speed by a factor of 1.2*/
	private void increaseSpeed() {
		vx = 1.2*vx;
		vy = 1.2*vy;
	}

	
	/** end game based on whether the player had lives when the game stopped running */
	private void endGame() {
		if (lives == 0) lostGame();
		if (lives != 0) wonGame ();
	}
	
	
	/** Displays message to user*/
	private void wonGame() {
		remove (ball);
		GLabel endMessage = new GLabel (" YOU WON! ");
		double x = (getWidth() - endMessage.getWidth() ) / 2;
		double y = (getHeight () - endMessage.getHeight()) / 2;
		endMessage.setLocation (x, y);
		add (endMessage);
	}

	
	/** Displays message to user*/
	private void lostGame() {
		GLabel endMessage = new GLabel (" GAME OVER :(");
		double x = (getWidth() - endMessage.getWidth() ) / 2;
		double y = (getHeight () - endMessage.getHeight()) / 2;
		endMessage.setLocation (x, y);
		add (endMessage);
	}
}