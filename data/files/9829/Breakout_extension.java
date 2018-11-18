
/*
 * File: Breakout.java
 * -------------------
 * Name: Marcela De los Rios
 * Section Leader: Avery Wang
 * 
 * This program is the game of Breakout. In this game, the user uses a 
 * paddle to a bounce a ball up the screen and hit colored bricks at the top of the screen.
 * Once a brick is hit, the brick disappears and the ball continues to bounce.
 * If the ball hits the bottom of the screen, the game is over.
 * The goal of the game is to get all of the bricks to disappear.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_extension extends GraphicsProgram{

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

	// Number of colors
	public static final int NCOLORS = 5;

	// Number of rows for each color
	public static final int NROWS_COLOR = NBRICK_ROWS/NCOLORS;

	// Ball diameter
	public static final double BALL_DIAMETER = BALL_RADIUS*2;

	// Total numbers of bricks at the start
	public static final int NUM_BRICKS = NBRICK_COLUMNS*NBRICK_ROWS;

	// Instance variable for the paddle that is tracked
	private GRect paddle = null;

	// Instance variable for the ball 
	private GOval ball = null;

	// Instance variable for a random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Instance variable for horizontal velocity
	private double vx;

	// Instance variable for vertical velocity
	private double vy;

	// Instance variable for the user's current score
	private int score;

	// Instance variable for score label
	private GLabel scoreLabel = new GLabel("");

	// loads the the sound clip for each bounce
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setupGame();
		runGame();
	}

	/**
	 * Sets up the game, adding the bricks, the ball, and the paddle.
	 */

	private void setupGame() {
		addBricks();
		createPaddle();
		addMouseListeners();
	}

	/**
	 * This method adds the bricks to the screen.
	 */

	private void addBricks() {
		double xFirstBrick = getWidth()/2-BRICK_SEP/2-BRICK_SEP*4-BRICK_WIDTH*5;
		double yFirstBrick = BRICK_Y_OFFSET;
		for (int row = 0; row < NBRICK_ROWS; row++)	{
			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				createBrick(xFirstBrick, yFirstBrick, column, row);
			}
		} 
	}

	/**
	 * This method creates a single brick, fills it in, colors it,
	 * and adds it to the screen at the proper x and y location.
	 * @param x1 - the x location of the first brick
	 * @param y1 - the y location of the first brick
	 * @param column - the column number that the brick belongs to
	 * @param row - the row number that the brick belongs to
	 */

	private void createBrick(double x1, double y1, int column, int row) {
		double x = x1 + (BRICK_WIDTH + BRICK_SEP)*(column);
		double y = y1 + (BRICK_HEIGHT + BRICK_SEP)*(row);
		GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		colorBricks(row, brick);
		add(brick);
	}

	/**
	 * This method colors the brick depending on which row number it is in.
	 * @param row - the row number that the brick belongs to
	 * @param brick - the brick itself. This includes information on where the brick
	 * should be located and that the brick filled.
	 */

	private void colorBricks(int row, GRect brick) {
		if(row % NBRICK_ROWS == 0 || row % NBRICK_ROWS == 1) { 
			brick.setColor(Color.RED);
		}
		if(row % NBRICK_ROWS == 2 || row % NBRICK_ROWS == 3) {
			brick.setColor(Color.ORANGE);
		}
		if(row % NBRICK_ROWS == 4 || row % NBRICK_ROWS == 5) {
			brick.setColor(Color.YELLOW);
		} 
		if(row % NBRICK_ROWS == 6 || row % NBRICK_ROWS == 7) {
			brick.setColor(Color.GREEN);
		}
		if(row % NBRICK_ROWS == 8 || row % NBRICK_ROWS == 9) {
			brick.setColor(Color.CYAN);
		}
	}

	/**
	 * This method creates the paddle
	 */

	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
	}

	/**
	 * This method adjusts the location of the paddle so that it tracks with the mouse.
	 */

	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = getHeight()-PADDLE_Y_OFFSET;
		add(paddle, x-PADDLE_WIDTH/2, y);
		if(x > getWidth()-PADDLE_WIDTH/2) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, y);
		}
		if(x < PADDLE_WIDTH) {
			paddle.setLocation(0.0, y);
		}
	}

	/**
	 * This method runs the game and gets the ball moving.
	 */

	private void runGame() {
		ball = createBall();
		startBall();
	}

	/**
	 * This method creates the ball, setting the starting x and y coordinates,
	 * filling it in, and placing it on the screen.
	 * @return ball - returns the ball to the runGame method
	 */

	private GOval createBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		GOval ball = new GOval(x, y, BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
		add(ball);
		return ball;
	}

	/**
	 * This method gets the ball moving initially in the downwards direction using
	 * a set y velocity and a random x velocity within a range. It continues to keep the ball
	 * moving as long as it has not collided with a wall. If it hits a wall, it bounces
	 * in the opposite direction.
	 */

	private void startBall() {

		// sets the initial x and y velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;

		clickToStart();
		int bricksLeft = NUM_BRICKS;

		while(true) {
			checkIfWon(bricksLeft);

			// changes the direction of the ball if the ball hits a wall
			if(ball.getX() <= 0 || ball.getX() >= getWidth() - BALL_DIAMETER) {
				rgen.nextBoolean(0.5);
				vx = -vx; 
			} 
			if(ball.getY() <= 0 ) {
				vy = -vy;
			}

			// This ends the game and removes the ball if it hits the bottom wall.
			// It also adds a label to the screen and tells the user that they have lost
			// the game if the ball hits the bottom wall.
			if(ball.getY() >= getHeight() - BALL_DIAMETER) {
				remove(ball);
				GLabel looseGame = new GLabel ("You lose!");
				looseGame.setFont("SansSerif-50");
				add(looseGame, getWidth()/2 - looseGame.getWidth()/2, getHeight()/2);

			}

			// moves the ball and checks for collisions with the paddle or a brick
			ball.move(vx, vy); 
			pause(DELAY);
			bricksLeft = checkForCollisions(bricksLeft);
		}
	}

	/**
	 * This method asks the user to click once to start the game and waits for a click.
	 * It then removes the label once the user has clicked once
	 */

	private void clickToStart() {
		GLabel startGame = new GLabel ("Click once to start the game!");
		startGame.setFont("SansSerif-18");
		add(startGame, getWidth()/2 - startGame.getWidth()/2, getHeight()/2 - ball.getWidth());
		waitForClick();
		remove(startGame); 
	}

	/**
	 * This method adds a label to the screen telling the user that they have won
	 * if all of the bricks have disappeared 
	 * @param bricksLeft - keeps track of the number of bricks remaining on the screen,
	 * if this number is zero, then the user has won the game
	 */

	private void checkIfWon(int bricksLeft) {
		if(bricksLeft == 0) {
			GLabel winGame = new GLabel ("You won!");
			winGame.setFont("SansSerif-50");
			add(winGame, getWidth()/2 - winGame.getWidth()/2, getHeight()/2);
		}
	}

	/**
	 * This method checks for collisions. If it collides with the paddle,
	 * it bounces straight back up. If it collides with a brick, it bounces
	 * the opposite direction.
	 * @param bricksLeft - this is the counter for how many total bricks
	 * are left on the screen before the game will be won.
	 * @return int - this returns the counter for the number of bricks left
	 */

	private int checkForCollisions(int bricksLeft) {
		GObject collider = getCollidingObject();
		if (collider == paddle) { // this means it collides with the paddle
			vy = -Math.abs(vy);	
			bounceClip.play();
		}
		if (collider != null && collider != paddle) { // this means it collides with a brick
			remove(collider)	;
			vy = -vy; 
			bounceClip.play();
			bricksLeft--;
		}
		score = NUM_BRICKS - bricksLeft;  
		keepScore();
		return bricksLeft;
	}

	/**
	 * This method finds the object that the ball collides with.
	 * @return Object - this returns the collided object back to the
	 * checkForCollisions method in order to determine what to do about the collision
	 * and what type of collision it is.
	 */

	private GObject getCollidingObject() {
		double xBall = ball.getX();
		double yBall = ball.getY();
		GObject collider = checkFourCorners(xBall, yBall);
		return collider;
	}

	/**
	 * This method checks all four corners of the ball to check if
	 * any corner of the ball is colliding with an object.
	 * @param xBall - this provides the x location of the ball
	 * @param yBall - this provides the y location of the ball
	 * @return Obj - this returns the object that the corner of the ball collides with
	 */

	private GObject checkFourCorners(double xBall, double yBall) {
		GObject objTopLeft = getElementAt(xBall, yBall);
		if (objTopLeft != null) {
			return objTopLeft;
		}
		GObject objTopRight = getElementAt(xBall + BALL_DIAMETER, yBall);
		if (objTopRight != null) {
			return objTopRight;
		}
		GObject objBottomLeft = getElementAt(xBall, yBall + BALL_DIAMETER);
		if(objBottomLeft != null) {
			return objBottomLeft;
		}
		GObject objBottomRight = getElementAt(xBall + BALL_DIAMETER, yBall + BALL_DIAMETER);
		if(objBottomRight != null) {
			return objBottomRight;
		}
		return null;
	}

	/**
	 * This method adds the score to the bottom of the screen, underneath the paddle.
	 * The score is the number of bricks the user has hit
	 */

	private void keepScore() {
		scoreLabel.setFont("SansSerif-10");
		scoreLabel.setLabel("Score: " + score);
		scoreLabel.setLocation(paddle.getX() + PADDLE_WIDTH/2 - scoreLabel.getWidth()/2, paddle.getY() + 2*PADDLE_HEIGHT);
		add(scoreLabel);
	}

}


