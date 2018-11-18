/*
 * File: Breakout.java
 * -------------------
 * Name: Nick Jankovsky
 * Section Leader: Ella Tessier-Lavigne
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_Extend extends GraphicsProgram {
	
	/* Goal: Create the game breakout whereby a user uses a paddle at the bottom of a screen to deflect a moving ball from hitting the bottom of the canvas until all blocks at the top of the screen have been destroyed */

	/* Section: Named Constants
	 * ____________
	 * All named constant variables for the program
	 */
	
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
	
	// Set of instance variables to be used throughout the game
	public GRect paddle = null;
	public GOval ball = null;
	public double startY = BRICK_Y_OFFSET;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private double bricks = NBRICK_ROWS * NBRICK_COLUMNS;
	
	// add sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	/* Method: Run
	 * ___________
	 * Used to play the game breakout - decomposed into setup, play and result display
	 */
	public void run() {
		setupGame();
		for(int lives = NTURNS; lives > 0; lives--) {
			playGame();	
		}
		displayResult();
	}
	
	/* Method: Display Result
	 * ___________
	 * Depending on if user won or lost, message will be displayed with result
	 * Precondition: all user lives have been lost or all bricks have been destroyed
	 */
	private void displayResult() {
		if (bricks == 0) {
			GLabel result = new GLabel("CONGRATULATIONS - YOU WON!!", getWidth() / 2, getHeight() / 2 - BALL_RADIUS * 2);
			result.setFont("Serif-20");
			result.move(result.getWidth() / -2, result.getAscent() / 2);
			add(result);
		}else {
			GLabel result = new GLabel("YOU ARE SUCH A LOSER!", getWidth() / 2, getHeight() / 2 - BALL_RADIUS * 2);
			result.setFont("Serif-20");
			result.move(result.getWidth() / -2, result.getAscent() / 2);
			add(result);
		}
	}
	
	/* Method: Play Game
	 * ___________
	 * Method to animate and play breakout game
	 * Precondition: game setup complete
	 */
	private void playGame() {
		addMouseListeners();
		createBall();
		
		// user click to launch ball
		waitForClick();
		
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		
		// animation loop
		while(true) {
			
			// animate ball horizontally
			ball.move(vx,  0);		
			
			double ballX = ball.getX();
			double ballY = ball.getY();
			
			// check for side collisions			
			if(hitLeftWall() || hitRightWall()) {
				bounceClip.play();
				vx = -vx;
			}

			// check for object collisions on left and right
			if(getElementAt(ballX, ballY) != null) {
				handleXCollision(ballX, ballY);
			}else if(getElementAt(ballX + 2 * BALL_RADIUS, ballY) != null) {
				handleXCollision(ballX + 2 * BALL_RADIUS, ballY);
			}else if(getElementAt(ballX, ballY + 2 * BALL_RADIUS) != null) {
				handleXCollision(ballX, ballY + 2 * BALL_RADIUS);
			}else if(getElementAt(ballX + 2 * BALL_RADIUS, ballY + 2 * BALL_RADIUS) != null) {
				handleXCollision(ballX + 2 * BALL_RADIUS, ballY + 2 * BALL_RADIUS);
			}
						
			// move ball vertically
			ball.move(0,  vy);
			
			ballX = ball.getX();
			ballY = ball.getY();
			
			// check for top or bottom wall collisions
			if(hitBottomWall()) {
				remove(ball);
				return;
			}			
			if(hitTopWall()) {
				bounceClip.play();
				vy = -vy;
			}

			// check for object collisions on top and bottom
			if(getElementAt(ballX, ballY) != null) {
				handleYCollision(ballX, ballY);
			}else if(getElementAt(ballX + 2 * BALL_RADIUS, ballY) != null) {
				handleYCollision(ballX + 2 * BALL_RADIUS, ballY);
			}else if(getElementAt(ballX, ballY + 2 * BALL_RADIUS) != null) {
				handleYCollision(ballX, ballY + 2 * BALL_RADIUS);
			}else if(getElementAt(ballX + 2 * BALL_RADIUS, ballY + 2 * BALL_RADIUS) != null) {
				handleYCollision(ballX + 2 * BALL_RADIUS, ballY + 2 * BALL_RADIUS);
			}
			
			// check if user won
			if(bricks == 0) {
				return;
			}
			
			pause(DELAY);
		}		
	}
	
	/* Method: Handle Y Collision
	 * ___________
	 * Changes made to game based on ball contact with an object when moving vertically
	 * Precondition: ball has moved vertically and collided with an object
	 * Postcondition: ball reverses vertical direction and brick is destroyed (if collision is with a brick)
	 */
	private void handleYCollision(double x, double y) {
		GObject collider = getCollidingObject(x, y);
		if (collider == paddle) {
			bounceClip.play();
			vy = -vy;
			ball.move(0, vy);
		}else {
			bounceClip.play();
			remove(collider);
			bricks -= 1;
			vy = -vy;
			ball.move(0, vy);
		}
	}
	
	/* Method: Handle X Collision
	 * ___________
	 * Changes made to game based on ball contact with an object when moving horizontally
	 * Precondition: all has moved horizontally and collided with an object
	 * Postcondition: ball reverses horizontal direction and brick is destroyed (if collision is with a brick). If collision is with the paddle, player loses a life.
	 */
	private void handleXCollision(double x, double y) {
		GObject collider = getCollidingObject(x, y);
		if (collider == paddle) {
			bounceClip.play();
			vx = -vx;
			// means the paddle was not under the ball and the user should lose a live; thus the ball jumps to bottom wall
			ball.move(vx, 2 * BALL_RADIUS);
		}else {
			bounceClip.play();
			remove(collider);
			bricks -= 1;
			vx = -vx;
			ball.move(vx, 0);
		}
	}
	
	/* Method: Get Colliding Object
	 * ___________
	 * Get the object at the collision point with the ball
	 */
	private GObject getCollidingObject(double x, double y) {
		GObject collider = getElementAt(x,y);
		return collider;
	}
		
	/* Method: Wall Bouncing
	 * ___________
	 * Four methods to reverse the course of the ball when it hits any of the walls (top, bottom, left, right
	 */
	private boolean hitBottomWall() {
		return ball.getY() > getHeight() - ball.getHeight();
	}
	
	private boolean hitTopWall() {
		return ball.getY() < 0;
	}
	
	private boolean hitLeftWall() {
		return ball.getX() < 0;
	}
	
	private boolean hitRightWall() {
		return ball.getX() > getWidth() - ball.getWidth();
	}
	
	/* Method: Create Ball
	 * ___________
	 * Create game ball centered in the middle of the screen
	 */
	private void createBall() {
		ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setLocation((getWidth() / 2) - BALL_RADIUS, (getHeight() / 2) - BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}
	
	/* Method: Mouse Moved
	 * ___________
	 * Method to set paddle x location to x location of mouse bounded by the edges of the canvas
	 */
	public void mouseMoved(MouseEvent e) {
		if(e.getX() > 0 + PADDLE_WIDTH / 2 & e.getX() < getWidth() - PADDLE_WIDTH / 2) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET);
		}
	}
	
	/* Method: Setup Game
	 * ___________
	 * Perform activities necessary to prepare Breakout game including: setting title, setting up bricks and setting up paddle
	 */
	private void setupGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setupBricks();
		setupPaddle();
	}
		
	/* Method: Setup Paddle
	 * ___________
	 * Create and add paddle object
	 */
	private void setupPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
		add(paddle);
	}
	
	/* Method: Setup Bricks
	 * ___________
	 * Create grid of colored bricks
	 */
	private void setupBricks() {
		createBrickRows(2, Color.RED);
		createBrickRows(2, Color.ORANGE);
		createBrickRows(2, Color.YELLOW);
		createBrickRows(2, Color.GREEN);
		createBrickRows(2, Color.BLUE);
	}
	
	/* Method: Create Brick Rows
	 * ___________
	 * Create rows of colored bricks - number and color of bricks come as inputs
	 */
	private void createBrickRows(double numRows, Color color) {
		for(int n = 0; n < numRows; n++) {
			double startX = (getWidth() / 2) - ((NBRICK_COLUMNS / 2)*BRICK_WIDTH) - (((NBRICK_COLUMNS - 1) / 2) * BRICK_SEP);
			for(int i = 0; i < NBRICK_COLUMNS; i++) {
				createBrick(startX + i * (BRICK_WIDTH + BRICK_SEP), startY, color);
			}
			startY = startY + BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	
	/* Method: Create One Brick
	 * ___________
	 * Create one colored brick to add
	 */
	private void createBrick(double x, double y, Color color) {
		GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		brick.setLocation(x,y);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);
	}

}
