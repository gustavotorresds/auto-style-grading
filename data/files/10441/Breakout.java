/*
 * File: Breakout.java
 * -------------------
 * Name: Ryan Kang
 * Section Leader: Semir Shafi
 * Breakout: This program creates and allows a user to play the game Breakout. Upon clicking 
 * the screen, the ball in the center of the canvas will be released and the user will 
 * have the opportunity to move the paddle and bounce the ball upwards to break the bricks
 * placed above. Upon breaking all bricks, the user will win. Upon letting the ball
 * fall below the paddle, the user will have the opportunity to try again and again
 * without resetting the bricks.
 * Sources: Stanford Java Lib; The Art & Science of Java, CS 106A study group.
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

	//Gives the total number of bricks
	public static final int TOTAL_NUM_BRICKS = NBRICK_COLUMNS * NBRICK_ROWS;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 5.5;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 3.0;
	public static final double VELOCITY_X_MAX = 6.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	private int winCount = TOTAL_NUM_BRICKS;

	private GObject brick = null;
	private GRect paddle = null;
	private GOval ball = null;
	private double vx;
	private double vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBreakOut();
		playGame();
		addMouseListeners();
	}

	/*
	 * This method executes the game Breakout. It waits for the user to click on the screen
	 * before releasing the ball, and upon breaking all bricks, displays a winning message,
	 * and a losing message if the ball hits the bottom wall.
	 * Precondition: There must be a ball (GOval) placed at the center of the screen.
	 */
	private void playGame() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		waitForClick();
		startBall();
		displayWinnerMessage();
	}

	/*
	 * This method displays a message to the winner of the game after having broken every brick.
	 */
	private void displayWinnerMessage() {
		GLabel winner = new GLabel("Congrats, you won! Thanks for playing.");
		winner.setFont("Helvetica-20");
		winner.setColor(Color.BLUE);
		add(winner, getWidth() / 10, getHeight() / 1.5);
	}

	/*
	 * This method is an animation loop that bounces the ball from wall to paddle to bricks.
	 * It plays a "bounce" sound after the ball touches any one of these objects or walls.
	 * It also displays a losing message if the player allows the ball to touch the bottom wall.
	 */
	private void startBall() {
		while(winCount != 0) {
			if(hitTopWall()) {
				vy = -vy;
			}
			if(hitLeftWall() || hitRightWall()) {
				vx = -vx;
			}
			getCollidingObject();
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				//could not for the life of me figure out how to un-stick the paddle.
				//I know that vy must only bounce off the top part of the paddle, but the code eluded me, sorry!
				vy = -vy;
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();
			} else if (collider != null){
				remove(collider);
				//counter for how many bricks are left and how close the game is to ending.
				winCount = winCount - 1;
				vy = -vy;
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();
			}
			ball.move(vx, vy);
			pause(DELAY);
			taketheL();

		}
	}

	/*
	 * This method resets the ball in the center of the screen and creates a temporary
	 * losing message for the user.
	 */
	private void taketheL() {
		if(hitBottomWall()) {
			endRound();
			GLabel lose = new GLabel("ARGH! You lost the ball! Keep trying.");
			lose.setColor(Color.RED);
			add(lose, getWidth() / 10, getHeight() / 1.5);
			pause(10000000.0 / 6000.0);
			remove(lose);
			waitForClick();
		}
	}

	/*
	 * This method removes the ball at the bottom of the screen and positions a new ball
	 * in the center.
	 * Precondition: ball must be at or below (positive y value) the bottom of the screen.
	 */
	private void endRound() {
		remove(ball);
		createBall();
	}

	/*
	 * This method is the catalyst for the losing sequence of Breakout.
	 */
	private boolean hitBottomWall() {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/*
	 * These next three boolean methods are called in the "startBall" method. They ensure that
	 * when the ball hits any of the four walls, the ball reverses the necessary velocity to
	 * display a "bounce effect." 
	 */
	private boolean hitTopWall() {
		return ball.getY() < 0;
	}

	private boolean hitLeftWall() {
		return ball.getX() < 0;
	}

	private boolean hitRightWall() {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	/*
	 * This method establishes collision check-points at each of the four corners of the ball,
	 * provided that one imagines that a ball has square corners. It returns the object, if any,
	 * that it finds to the "startBall" method.
	 */
	private GObject getCollidingObject() {
		//collision check at upper leftmost corner
		double x = ball.getX();
		double y = ball.getY();
		//collision check-point for upper left corner of ball
		if(getElementAt(x, y) != null) {
			return getElementAt(x, y);
		}
		//collision check-point for upper right corner of ball
		else if(getElementAt(x + (2 * BALL_RADIUS), y) != null) {
			return getElementAt(x + (2 * BALL_RADIUS), y);
		}
		//collision check-point for lower left corner of ball
		else if(getElementAt(x, y + (2 * BALL_RADIUS)) != null) {
			return getElementAt(x, y + (2 * BALL_RADIUS));
		}
		//collision check-point for lower right corner of ball
		else if(getElementAt(x + (2 * BALL_RADIUS), y + (2 * BALL_RADIUS)) != null) {
			return getElementAt(x + (2 * BALL_RADIUS), y + (2 * BALL_RADIUS));
		}
		return null;
	}

	/*
	 * This method sets up Breakout by creating the layers of bricks, the paddle,
	 * and positions the ball.
	 * Postcondition: The bricks must be centered with a gap between the top of the bricks
	 * and the top of the canvas. The paddle must be a reasonable distance away from the bottom
	 * layer of bricks. The ball must be in the center of the screen.
	 */
	private void setUpBreakOut() {
		createBricks();
		createPaddle();
		createBall();
	}

	/*
	 * This method creates a ball and positions it in the center of the screen.
	 */
	private void createBall() {
		ball = new GOval((getWidth() / 2) - BALL_RADIUS, 
				(getHeight() / 2) - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * This method creates a paddle and positions it in the center of the screen.
	 */
	private void createPaddle() {
		paddle = new GRect(getWidth() / 2 - PADDLE_WIDTH / 2, 
				getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * This method allows the user to move the paddle in the x direction of the canvas.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if(mouseX + PADDLE_WIDTH / 2 < getWidth() && mouseX - PADDLE_WIDTH / 2 > 0) {
			paddle.setLocation(mouseX - PADDLE_WIDTH / 2,
					getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	/*
	 * This method creates the layers of bricks for Breakout.
	 */
	private void createBricks() {
		double brickStartPoint = (0.5 * getWidth()) - (0.5 *(BRICK_WIDTH * NBRICK_COLUMNS) + BRICK_WIDTH / 2);
		//nested for-loop builds the pyramid
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			//Y coordinate for each successive layer of bricks.
			double y = (i) * BRICK_HEIGHT + (i * BRICK_SEP);
			//X coordinate for each successive layer of bricks.
			double x = brickStartPoint;
			//builds single row of bricks.
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				buildBrick(x + (j * BRICK_WIDTH) + (BRICK_SEP * j), y + BRICK_Y_OFFSET, i);
			}
		}
	}

	/*
	 * This method creates one brick, adds it to the screen, and colors the rows accordingly.
	 */
	private void buildBrick(double x, double y, int i) {
		brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		((GRect) brick).setFilled(true);
		add(brick);
		if(i < 2) {
			brick.setColor(Color.RED);
		} else if (i < 4) {
			brick.setColor(Color.ORANGE);
		} else if (i < 6) {
			brick.setColor(Color.YELLOW);
		} else if (i < 8) {
			brick.setColor(Color.GREEN);
		} else if (i < 10) {
			brick.setColor(Color.CYAN);
		}
	}
}

