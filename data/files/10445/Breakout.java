/*
 * File: Breakout.java
 * -------------------
 * Name: Shuyan Zhou
 * Section Leader: Remi Oso
 * 
 * This file will eventually implement the game of Breakout.
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
	private int bricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;

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
	
	//keep track of the ball's horizontal velocity
	private double vx, vy = VELOCITY_Y;
	//random-number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// main function to play the game
		int numTurns = NTURNS;
		addMouseListeners();
		while(numTurns > 0) {
			setupGame();
			if (beginGame() > 0) { //if you win the game, game ends immediately
				removeAll();
				break;
			}
			removeAll();
			numTurns -= 1;
		}
		println("Game is over!");
	}
	
	//set up game by build bricks, paddle and ball
	private void setupGame() {
		setupBricks();
		createPaddle();
		createBall();	
	}
	// Start to play the game
	// If return value is less than 0, you lost the game
	// If return value is greater than 0, you win the game
	private int beginGame() {
		println("wait for click begin");
		waitForClick();
		getVelocity();
		while (true) {
			bounceBall();
			if(ball.getBottomY() > getHeight()) {
				println("Sorry, you lost!");
				return -1;
			}
			if(bricksLeft == 0) {
				println("Congratulations， you win!");
				return 1;
			}
			pause(DELAY);
		}
	}
	
	/* set up by drawing rainbow colored bricks by row and column*/
	private void setupBricks() {
		for(int r = 0; r < NBRICK_ROWS; r++) { // for each row of bricks
			for(int c = 0; c < NBRICK_COLUMNS; c++) { //for each brick in a row
				//calculate the x & y -coordinate starting point of first row
				double xStart = getWidth() / 2 - NBRICK_COLUMNS * BRICK_WIDTH/2 - (NBRICK_COLUMNS - 1) * BRICK_SEP /2+ c * BRICK_WIDTH + c * BRICK_SEP;
				double yStart = BRICK_Y_OFFSET + r * BRICK_HEIGHT + r * BRICK_SEP;
				
				GRect brick = new GRect(xStart, yStart, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				//color bricks
				Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};
				brick.setFilled(true);
				brick.setColor(colors[r/2]);
			}			
		}
	}
	
	/* create a paddle object*/
	private GRect paddle;
	private void createPaddle() {
		this.paddle = new GRect(getWidth()/2 - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFillColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
	}
	
	//listen to mouse movement
	public void mouseMoved(MouseEvent e) {
		if (paddle == null) return;
		//check if mouse goes across the boundary
		if(e.getX() + PADDLE_WIDTH >= getWidth()) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH , getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		} else if (e.getX() < 0) {
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		} else {
			paddle.setLocation(e.getX(), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
	
	/* create a ball object */
	private GOval ball;
	private void createBall() {
		this.ball = new GOval(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		this.ball.setFillColor(Color.BLACK);
		this.ball.setFilled(true);
		add(this.ball);
	}
	// get components of ball's velocity, the initial velocity is drop down ball with v = 3.0
	private void getVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}
	//start to bounce ball and check if it bounce into walls
	private void bounceBall() {
		ball.move(vx, vy);
		if(ball.getRightX() > getWidth() || ball.getX() < 0) { vx = -vx; }
		if(ball.getBottomY() > getHeight() || ball.getY() < 0) { vy = -vy; }
		moveAfterCollisions();
	}
	
	//check for collision
	private GObject checkCollisions() {
		if(getElementAt(ball.getX(), ball.getY()) != null){
			return getElementAt(ball.getX(), ball.getY());
		} else if(getElementAt(ball.getRightX(), ball.getY()) != null) {
			return getElementAt(ball.getRightX(), ball.getY());
		}else if(getElementAt(ball.getX(), ball.getBottomY()) != null) {
			return getElementAt(ball.getX(), ball.getBottomY());
		}else if(getElementAt(ball.getRightX(), ball.getBottomY()) != null) {
			return getElementAt(ball.getRightX(), ball.getBottomY());
		}
		return null;
	}
	
	/* change movement after collision */
	private void moveAfterCollisions() {
		GObject collidingObject = checkCollisions();
		if(collidingObject != null) {
			if(collidingObject == paddle) { //if the collision object is paddle
			    // we have to assume that
				// 1. the y velocity should always be positive
				// 2. the bottom y of ball should be at least above the lower end of the paddle
				if(vy > 0 && ball.getBottomY() < getHeight() - PADDLE_Y_OFFSET) {
					vy = -vy;
				}
			} else {  // when ball it's the brick
				vy = -vy;
				remove(collidingObject);
				bricksLeft -= 1;
				vy += 0.2; //increase the velocity on y-axis as # of bricks left decreases
			}
		}
	}
}
