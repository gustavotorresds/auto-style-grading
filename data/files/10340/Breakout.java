/*
 * File: Breakout.java
 * -------------------
 * Name: Jensen Neff
 * Section Leader: Avery Wang
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

/*
 * This file creates a game called "Breakout"
 * The user's goal is to get rid of all the colored bricks by hitting them with the ball
 * If they get rid of all the bricks, they win the game
 * The user hits the ball off the paddle to keep the game alive
 * If the ball hits the bottom of the screen, the user loses a life
 * The user has three lives before they lose the game
 */

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

	//Instance variable for paddle
	GRect paddle = null;
	
	//Instance variable for ball
	GOval ball = null;
	
	// Instance variables for velocities in the X and Y directions
	private double vx, vy;
	
	// Instance variable for the random generator for velocities
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Instance variable for the number of lives
	private int lives = 3;
	
	// Instance variable for the number of bricks that exist
	private int existingBricks = NBRICK_COLUMNS * NBRICK_ROWS;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Calls on the methods that will be used in this program
		placeBricks();
		addMouseListeners();
		makePaddle();
		makeBall();
		moveBall();

	}

/*
 * This method places the bricks at the top of the screen
 * The bricks are colored by rows of two in the colors red, orange, yellow, green, and blue
 * The number of bricks is based on constants, and the space between the bricks is also based on a constant
 */
	private void placeBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				double x = (CANVAS_WIDTH - (NBRICK_COLUMNS * BRICK_WIDTH) - ((NBRICK_COLUMNS - 1) * BRICK_SEP)) 
						/ 2 + ((BRICK_WIDTH + BRICK_SEP) * j);
				double y = BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP)* i);

				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(i <= 1) {
					brick.setColor(Color.RED);
				}else if(i <= 3 && i > 1) {
					brick.setColor(Color.ORANGE);
				}else if(i <= 5 && i > 3) {
					brick.setColor(Color.YELLOW);
				}else if(i <= 7 && i > 5) {
					brick.setColor(Color.GREEN);
				}else if(i <= 9 && i > 7) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}

	/*
	 * This method creates a paddle thats size is determined by constants
	 * The paddle is placed in the middle of the screen
	 */
	private void makePaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, getWidth() / 2 - PADDLE_WIDTH / 2, CANVAS_HEIGHT - PADDLE_Y_OFFSET);
	}

	/*
	 * This method uses mouse listeners to check the location of the mouse whenever the user moves the mouse
	 * The movement of the mouse in the X direction determines the X location of the paddle on the screen
	 * The method makes sure that the paddle does not move off the screen
	 * -------------------------------------------------------------------------------------------------
	 * Pre: Paddle is in the middle of the screen
	 * Post: Paddle moves in the X direction based on the X location of the mouse
	 */
	public void mouseMoved(MouseEvent e) {
		double x;
		double y = CANVAS_HEIGHT - PADDLE_Y_OFFSET;
		if(e.getX() <= PADDLE_WIDTH/2.0) {
			x = 0;
		}else if (e.getX() >= CANVAS_WIDTH - (PADDLE_WIDTH / 2.0)) {
			x = CANVAS_WIDTH - PADDLE_WIDTH;
		}else {
			x = e.getX() - PADDLE_WIDTH/2.0;
		}
		paddle.setLocation(x, y);
	}

	/* 
	 * This method creates the ball based on constants
	 * The ball is placed in the middle of the screen
	 */
	private void makeBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
	}

	/* 
	 * This method moves the ball based on a constant Y velocity,
	 * and an X velocity determined by a random generator.
	 * Checks and calls upon a new method if a wall or object is hit
	 */
	private void moveBall() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;;
		vy = VELOCITY_Y;

		/* 
		 * Checks if the ball collides with something on the screen
		 * If it is the wall or object, it calls upon new methods
		 */
		while(getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS) != null) {
			checkWallCollide();
			checkObjCollide();
			ball.move(vx, vy);
			pause(DELAY);
		}
	}

	/* 
	 * This method checks if the ball hits the bottom.
	 * If it does hit the bottom, the user loses a life, up until three lives,
	 * at which point the user loses the game.
	 * If the ball hits any other wall, it bounces off of the wall.
	 */
	private void checkWallCollide() {
		if(ball.getBottomY() > getHeight()) {
			remove(ball);
			lives--;
			if(lives > 0) {
				GLabel label = new GLabel("YOU LOST A LIFE! KEEP TRYING :)");
				add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 + label.getWidth()/2);
				pause(1500);
				remove(label);
				makeBall();
			}else {
				removeAll();
				GLabel label = new GLabel("YOU LOSE :(");
				add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 + label.getWidth()/2);
				pause(4000);
				remove(label);
			}
		}
		if(ball.getX() < 0 || ball.getRightX() > getWidth()) {
			vx = -vx;
		}
		if(ball.getY() < 0) {
			vy = -vy;
		}

	}

	/* 
	 * This method checks if the ball collides with an object.
	 * If the object is the paddle, it bounces off of it as if it is a wall.
	 * If it is a brick, it removes the brick.
	 * If the ball removes the last brick, a label is printed 
	 * that tells the user they won the game. 
	 * Because there are four corners of the ball, the program must check if the 
	 * ball is colliding at any of these four corners.
	 */
	private void checkObjCollide() {

		// Checks at top left corner of the ball
		GObject checkForObject = getElementAt(ball.getX(), ball.getY());
		if(checkForObject != null) {	
			if(checkForObject == paddle) {
				if(vy > 0) {
					vy = -vy;
				}
			}else{
				remove(checkForObject);
				existingBricks--;
				if(existingBricks == 0) {
					removeAll();
					GLabel label = new GLabel("YOU WON!!!!");
					add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 + label.getWidth()/2);
					//GImage image = new GImage()
					pause(4000);
					remove(label);
				} else {
					vy = -vy;
				}
			}
		}else{ 	   // Checks at top right corner of the ball
			checkForObject = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
			if(checkForObject != null) {	
				if(checkForObject == paddle) {
					if(vy > 0) {
						vy = -vy;
					}
				}else{
					remove(checkForObject);
					existingBricks--;
					if(existingBricks == 0) {
						removeAll();
					} else {
						vy = -vy;
					}
				}
			}else{  		// Checks at bottom left corner of the ball
				checkForObject = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
				if(checkForObject != null) {	
					if(checkForObject == paddle) {
						if(vy > 0) {
							vy = -vy;
						}
					}else{
						remove(checkForObject);
						existingBricks--;
						if(existingBricks == 0) {
							removeAll();
						}else{
							vy = -vy;
						}
					}
				}else {  	// Checks at bottom right corner of the ball
					checkForObject = getElementAt(ball.getX() + BALL_RADIUS * 2 , ball.getY() + BALL_RADIUS * 2);
					if(checkForObject != null) {	
						if(checkForObject == paddle) {
							if(vy > 0) {
								vy = -vy;
							}
						}else{
							remove(checkForObject);
							existingBricks--;
							if(existingBricks == 0) {
								removeAll();
							}else{
								vy = -vy;
							}
						}
					}
				}
			}
		}
	}
}

