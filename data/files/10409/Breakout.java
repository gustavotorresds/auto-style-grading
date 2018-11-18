/*
 * File: Breakout.java
 * -------------------
 * Name: Brandon Kier
 * Section Leader: Jordan Rosen-Kaplan
 * This program runs the game Breakout.
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


	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int brickNum = NBRICK_COLUMNS * NBRICK_ROWS;
	private boolean win = false;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();

		setupGame();
		
		for(int i=0; i < NTURNS; i++) {
			playGame(i);
			if (win) {
				break;
			}
		}
		if (!win) youLose();
	}
		
			
		

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if(e.getX() > PADDLE_WIDTH/2 && e.getX() < getWidth() - PADDLE_WIDTH/2)
			paddle.setLocation(mouseX, y);
	}

	/**
	 * Method: Setup Game
	 * -----------------------
	 * Adds all the elements to the canvas required to start the game.
	 */
	private void setupGame() {
		placeBricks();
		addPaddle();
		addBall();
	}
	
	/**
	 * Method: You Win
	 * -----------------------
	 * Adds the label "You Win!" to the center of the screen.
	 */
	private void youWin() {
		GLabel label = new GLabel("You Win!");
		label.setLocation(getWidth()/2 - label.getWidth()/2, getHeight()/2);
		label.setColor(Color.BLUE);
		add(label);
	}
	
	/**
	 * Method: You Lose
	 * -----------------------
	 * Adds the label "You Lose!" to the center of the screen.
	 */
	private void youLose() {
		GLabel label = new GLabel("You Lose!");
		label.setLocation(getWidth()/2 - label.getWidth()/2, getHeight()/2 - 10);
		label.setColor(Color.RED);
		add(label);
	}
	
	/**
	 * Method: Place Bricks
	 * -----------------------
	 * Places all of the colored bricks at the top of the canvas needed for the game.
	 */
	private void placeBricks() {
		for(int c = 0; c < NBRICK_COLUMNS; c++) {
			for(int r = 0; r < NBRICK_ROWS; r++) {
				double x = getWidth()/2 + (0.5 * BRICK_SEP) - (BRICK_WIDTH + BRICK_SEP) * (0.5 * NBRICK_COLUMNS);
				double y = BRICK_Y_OFFSET;
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(r == 0 || r == 1)
					brick.setColor(Color.RED);
				if(r == 2 || r == 3)
					brick.setColor(Color.ORANGE);
				if(r == 4 || r == 5)
					brick.setColor(Color.YELLOW);
				if(r == 6 || r == 7)
					brick.setColor(Color.GREEN);
				if(r == 8 || r == 9)
					brick.setColor(Color.CYAN);
				add(brick, x + c * (BRICK_WIDTH + BRICK_SEP), y + r * (BRICK_HEIGHT + BRICK_SEP));
			}
		}
	}

	/**
	 * Method: Add Paddle
	 * -----------------------
	 * Adds the paddle to the bottom center of the screen.
	 */
	private void addPaddle() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = (getHeight() - PADDLE_Y_OFFSET- PADDLE_HEIGHT);
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);

	}

	/**
	 * Method: Add Ball
	 * -----------------------
	 * Adds the ball to the center of the screen.
	 */
	private void addBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval (x, y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);

	}

	/**
	 * Method: Play Game
	 * -----------------------
	 * This method allows the ball to move/bounce around the screen and destroy bricks.
	 */
	private void playGame(int currentLife) {
		waitForClick();
		initializeVelocity();
		while(true) {
			moveBall();
			if(hitBottomWall(ball)) {
				remove(ball);
				currentLife = currentLife + 1;
				if (currentLife < NTURNS && !win) {
					addBall();
				}
				break;
				}
		}
	}
	


	/**
	 * Method: Initialize Velocity
	 * -----------------------
	 * Initializes the variables for the ball velocity
	 */
	private void initializeVelocity() {
		vx = rgen.nextDouble(1.0, 3.0);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}


	/**
	 * Method: Move Ball
	 * -----------------------
	 * This moves the ball around the screen. If the the ball hits any of the walls except the bottom wall, 
	 * it reverses direction.
	 */
	private void moveBall() {
		ball.move(vx, vy);	

		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball)) {
			vy = -vy;
		}

		pause(DELAY);
		destroyBricks();
	}
	


	/**
	 * Method: Destroy Bricks
	 * -----------------------
	 * This method allows for a brick to disappear once the ball hits its. 
	 */
	private void destroyBricks() {
		GObject collider = getCollidingObject();

		//Allows the ball to bounce off the paddle
		if(collider==paddle) {
			//sticky paddle
			if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 && ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 + 4) {
				vy = -vy;	
			}
		}

		// When the ball collides with a brick, it removes that brick, and the ball reverses direction.
		else if (collider != null) {
			remove(collider); 
			vy = -vy;
			brickNum = brickNum - 1;
			if(brickNum == 0) {
				youWin();
				remove(ball);
				win = true;
			}
		}
	}


	/**
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the bottom wall of the window.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/**
	 * Method: Hit Top Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/**
	 * Method: Hit Right Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the right wall of the window.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/**
	 * Method: Hit Left Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the left wall of the window.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}	

	
	private GObject getCollidingObject() {
		if((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		if (getElementAt( (ball.getX() + BALL_RADIUS*2), ball.getY()) != null ){
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
		}
		if(getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2)) != null ){
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
		}
		if(getElementAt((ball.getX() + BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2)) != null ){
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
		}
		//returns null if there are no objects present
		else{
			return null;
		}
	}
}






