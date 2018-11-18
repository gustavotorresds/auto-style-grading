/*
 * File: Breakout.java
 * -------------------
 * Name: Rodica Timotin	
 * Section Leader: Meng Zhang
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
	
	// Pause after losing a ball and starting a new turn (ms)
	public static final double DELAY_NEW_TURN = 1000.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Paddle object (visible throughout the program)
	private GRect paddle = null;
	
	// Ball object (visible throughout the program)
	private GOval ball =null;

	// Random number generator (visible throughout the program)
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Velocities of the ball (visible throughout the program)
	private double vx,vy;
	
	// Count of remaining bricks (visible throughout the program)
	private int countRemainingBricks;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Set up the rows of bricks
		setUpBrickRows();
		
		// Add the paddle to the canvas
		paddle = addPadle();
		
		// Add mouse listeners
		addMouseListeners();
		
		// Start the count of remaining bricks, which initially equals the total number of bricks
		countRemainingBricks = NBRICK_COLUMNS* NBRICK_ROWS;
		
		/* Starts a new game for NTURNS times,
		 * unless there are no more bricks left
		 */
		for (int i=0; i<NTURNS; i++) {
			if (countRemainingBricks!=0) { // check if there are any bricks left
				ball = addBall(); // adds a ball in the middle of the canvas
			}
			// Initializes the velocities at which the ball will bounce
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); // horizontal speed in pixels
			vy = 2* VELOCITY_Y; // vertical speed in pixels
			if (rgen.nextBoolean(0.5)) {
				vx = -vx; // the ball is released towards the left or right with equal probabilities
			}
			// Bounces the ball off the walls, the paddle and the bricks
			bounceBall();
		}
		
		/* After the game is over, i.e all NTURNS have passed or there are no more bricks left,
		 * the ball is removed from the screen.
		 */
		remove(ball);
		
		// Prints a message at the end of the game
		printEndMessage();
	}
	

	/* At the end of the game, the player gets a message:
	 * "Congratulations! You won!" if there are no more bricks left
	 * "Game over! You lost!" otherwise
	 */
	private void printEndMessage() {
		GLabel endMessage = new GLabel("",0,0); // creates an empty label
		endMessage.setFont("Courier-24");
		// update the message of the label depending on the outcome of the game
		if (countRemainingBricks ==0) {
			endMessage.setLabel("Congratulations! You won!");
		} else {
			endMessage.setLabel("Game over! You lost!");
		}
		double labelX = (getWidth()-endMessage.getWidth())/2; // x-position for the label to be centered
		double labelY = (getHeight()+endMessage.getAscent())/2; // y-position for the label to be centered
		endMessage.setLocation(labelX,labelY); // update the location of the label to the center of the canvas
		add (endMessage);
	}
	
	
	// Returns the object upon the ball's collision
	private GObject getCollidingObject() {
		GObject collider = null; // initialize the object collider
		
		// Determine the x-position of the top-left corner of the square surrounding the ball
		double ballCornerX = ball.getX(); 
		
		/* Checks whether there is an object in each of the four corners of  
		 * the square surrounding the ball
		 */
		for (int i = 0; i<2;i++) {
			double ballCornerY = ball.getY(); // y-position of the top-left corner of the square
			for (int j=0;j<2; j++) {
				if (collider == null) {
					collider = getElementAt(ballCornerX,ballCornerY);
				}
				ballCornerY += 2*BALL_RADIUS; // alternate between top and bottom corners				
			}
			ballCornerX += 2*BALL_RADIUS; // alternate between left and right corners
		}
		return collider;
	}
	
	
	/* Bounces the ball off the left, right and top walls, 
	 * off the paddle and off the bricks
	 * 
	 * If the ball collides with a brick, the brick is removed, and
	 * the count of remaining bricks drops by one
	 */
	private void bounceBall() {
		while (gameOn()) {
			// If the ball hits the left or right wall, it changes horizontal direction
			if (hitLeftWall() || hitRightWall()) {
				vx = -vx;
			}
			
			// If the ball hits the top wall, it changes vertical direction 
			if (hitTopWall()) {
				vy = -vy;
			}
			// Finds the objects the ball touches at any point in time
			GObject collider = getCollidingObject();
			
			/* Upon collision, the ball changes vertical direction
			 * If the object is a brick, i.e. not the paddle,
			 * the brick is removed and the number of remaining bricks
			 * goes down by 1.
			 */
			if ((collider != null)) {
				vy = -vy;
				if (collider != paddle ) {
					remove(collider);
					countRemainingBricks += -1;
				}
			}
			
			// Continuously move the ball with vx, vy velocities
			ball.move(vx,vy);			
			pause(DELAY);
		}	
	}

	
	/* Checks whether the game is still on
	 * If the ball did not fall through the bottom and there are still some
	 * remaining bricks, the current turn of the game continues (returns true);
	 * otherwise, the current turn is over (returns false).
	 */
	private boolean gameOn() {
		return (!fallAtBottom() && countRemainingBricks != 0);
	}

	// Returns true if the ball hit the left wall, false otherwise.
	private boolean hitLeftWall() {
		return (ball.getX() < 0);
	}

	// Returns true if the ball hit the right wall, false otherwise.
	private boolean hitRightWall() {
		return (ball.getX()+2*BALL_RADIUS > getWidth());
	}
	
	// Returns true if the ball hit the top wall, false otherwise.
		private boolean hitTopWall() {
			return (ball.getY()< 0);
		}

		// Returns true if the ball falls below the paddle
		private boolean fallAtBottom() {
			return(ball.getY() > getHeight());
		}
		
	
	// Adds a ball in the middle of the console
	private GOval addBall() {
		pause(DELAY_NEW_TURN);
		// x-origin of the ball
		double ballX = getWidth()/2 - BALL_RADIUS;
		// y-origin of the ball
		double ballY = getHeight()/2-BALL_RADIUS;
		GOval ball = new GOval (ballX,ballY,2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setFilled(true);
		add (ball);
		return ball;
	}

	// Moves the paddle horizontally, along with the mouse
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX(); // sets the x-position of the mouse
		double paddleY = getHeight()-PADDLE_HEIGHT - PADDLE_Y_OFFSET; // sets the y-position of the paddle
		if (mouseX>0 && mouseX<getWidth()-PADDLE_WIDTH) { // checks the mouse is within the edges of the console
			paddle.setLocation(mouseX,paddleY); // sets location of the paddle in line with the mouse
		}
	}
	
	
	// Builds a paddle in the middle of the console
	private GRect addPadle() {
		// compute x location of the paddle
		double paddleX = (getWidth()-PADDLE_WIDTH)/2;
		// compute y location of the paddle
		double paddleY = getHeight()-PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		GRect paddle = new GRect(paddleX,paddleY,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
		return paddle;
	}

	// Builds the entire brick wall
	private void setUpBrickRows() {
		// Calculate the x and y position of the top left brick
		double rowX = (getWidth() - (NBRICK_COLUMNS + 1.0) * BRICK_SEP - NBRICK_COLUMNS*BRICK_WIDTH);
		double rowY = BRICK_Y_OFFSET;
		
		// Builds all one row at a time for NBRICK_ROWS times
		for (int i = 0 ; i < NBRICK_ROWS; i++) {
			buildBrickRow(rowX,rowY,i);
			// move on to next row
			rowY += BRICK_HEIGHT + BRICK_SEP;
		}
	}

	
	// Builds a row of colorful bricks, one by one
	private void buildBrickRow(double x, double y, int row) {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			buildBrick(x,y,row); // the row determines the color of the brik row
			// move on to next brick
			x += BRICK_WIDTH + BRICK_SEP;
		}		
	}
	
	// Builds a brick given the coordinates (x,y) and the row, which determines the color
	private void buildBrick(double x, double y, int row) {
		GRect brick = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
		brick.setFilled(true);
		if (row % 10 < 2) {
			brick.setColor(Color.RED);
		} else if (row % 10 < 4) {
			brick.setColor(Color.ORANGE);
		} else if (row % 10 < 6) {
			brick.setColor(Color.YELLOW);
		} else if (row % 10 < 7) {
			brick.setColor(Color.GREEN);
		} else {
			brick.setColor(Color.CYAN);
		}
		add (brick);
	}



}
