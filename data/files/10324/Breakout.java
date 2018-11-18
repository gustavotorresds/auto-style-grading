/*
 * File: Breakout.java
 * -------------------
 * Name: JiaJia Jin
 * Section Leader: N/A
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

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Add instance variables 
	GRect paddle;
	GOval ball;
	GObject collider;
	double vx, vy;
	
	// Initialize brick count, score, number of turns and scenario 
	int brickCount = NBRICK_ROWS * NBRICK_COLUMNS; 
	int numTurns = NTURNS; 

	/** Main function - executes program
	 */
	public void run() {
		
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Create a paddle object
		paddle = drawPaddle();
				
		// Add a mouse listener for paddle movements
		addMouseListeners();

		while(numTurns != 0) {
			
			// Initialize / reset the ball
			// After a failed turn, ball is removed so
			// we'll need to create a new one here
			ball = drawBall();
			
			// Initialize / reset the bricks
			// After a failed turn, the removed bricks
			// will have to be put back to restart the game
			drawAllRows();
			
			// Makes the ball move & bounce off wall
			waitForClick();
			moveBall();

			// Check whether all bricks are gone; player wins
			// If brick still exists, do the following
			while (brickCount != 0) {

				// Check if ball touches walls or paddle
				
				// Check if the ball has hit left or right wall;
				// Reverse x direction and add bounce sound
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				}
				
				// Check if the ball has hit top wall;
				// Reverse y direction and add bounce sound
				if(hitTopWall(ball)) {
					vy = -vy;
				}
				
				// Check if the ball has hit bottom wall or paddle
				// Reverse y direction and add bounce sound if it hits paddle
				// Wrap up the turn, take away a life and reset brick count to total
				
				if(hitBottomWallOrPaddle(ball)) {
					collider = getCollidingObject();
					// Ball falls thru the bottom wall; player loses a turn
					if(collider == null) {
						numTurns --; 
						brickCount = NBRICK_COLUMNS * NBRICK_ROWS;
						break;
					}
				}
				
				// Ball continues to move in x and y direction
				ball.move(vx, vy);
				
				// Check collisions with objects
				// If it's paddle, ball keeps moving as intended; 
				// if it's a brick, remove the brick and update brick count; 
				// if it's the score board, ignore and let ball pass thru
				collider = getCollidingObject();
								
				if (collider != null) {
					if (collider == paddle) {
						vy = -vy; 
					} else {
						remove(collider);
						brickCount--;
						vy = -vy;
					}
				}
				
				// Add a pause after each ball movement
				pause(DELAY); 
				
			}
			
			// Need to check why we are here - win game, or lost life?
			if (brickCount == 0) {
				// Set the scene for victory
				remove(ball);
				remove(paddle);
			}
			else {
				// Lost one life; restart game
				remove(ball);
			}
		}
		//set the scene for defeat
		remove(ball);
		remove(paddle);
	}
	

	/** Draw rows of colored bricks at top of screen
	 *  Refills the bricks if they are removed and 
	 *  we need to restart the game 
	 */
	private void drawAllRows() {
		drawOneRow(0, Color.RED);
		drawOneRow(1, Color.RED);
		drawOneRow(2, Color.ORANGE);
		drawOneRow(3, Color.ORANGE);
		drawOneRow(4, Color.YELLOW);
		drawOneRow(5, Color.YELLOW);
		drawOneRow(6, Color.GREEN);
		drawOneRow(7, Color.GREEN);
		drawOneRow(8, Color.BLUE);
		drawOneRow(9, Color.BLUE);
	}
	
	private void drawOneRow(int nrow, Color color) {
		// Initialize starting x, y positions
		double leftX = (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP)) / 2; 
		double topY = BRICK_Y_OFFSET; 
		
		for(int col = 0; col < NBRICK_COLUMNS; col++) {
			GRect brick; 
			
			// Check if there is already a brick underneath the position; if there isn't one (removed due to collision), draw one to refill
			brick = getElementAt(leftX + col * (BRICK_WIDTH + BRICK_SEP) + BRICK_SEP / 2, topY + nrow * (BRICK_HEIGHT + BRICK_SEP) + BRICK_SEP / 2);
			
			if (brick == null) {
				brick = new GRect(leftX + col * (BRICK_WIDTH + BRICK_SEP), topY + nrow * (BRICK_HEIGHT + BRICK_SEP), 
					BRICK_WIDTH, BRICK_HEIGHT);
				brick.setColor(color);
				brick.setFilled(true);
				add(brick);
			}
		}
	}
	
	/** When a mouse is moved, paddle moves along its x-axis  
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleY = getHeight() - PADDLE_WIDTH - PADDLE_Y_OFFSET;
		// Ensures that the paddle doesn't disappear beyond the right wall
		if (mouseX > getWidth() - PADDLE_WIDTH) {
			mouseX = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(mouseX, paddleY);
	}
	
	/** Add a paddle to bottom center of screen
	 */
	private GRect drawPaddle() {
		// Centers the paddle at the bottom of screen with an offset
		double paddleX = (getWidth() + PADDLE_WIDTH) / 2;
		
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle, paddleX, getHeight() - PADDLE_WIDTH - PADDLE_Y_OFFSET);
		return paddle;
	}
	
	/** Add a ball to the center of screen
	 */
	private GOval drawBall() {
		//Centers the ball at center of screen
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		
		GOval ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball, x, y);
		return ball;
	}
	
	/** Make the ball move in x and y directions
	 */
	private void moveBall() {
		// Randomize initial vx to make the game more interesting
		// ball does not bounce the same exact direction at each turn
		RandomGenerator rgen = RandomGenerator.getInstance();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		
		// initial vy is constant for each turn
		vy = VELOCITY_Y;
	}

	/** Check if it's true that the ball has hit four walls or paddle
	 */
	// When checking if ball has touched bottom wall or paddle, 
	// we need to remove paddle height so that it doesn't look like 
	// the ball has sunk into the paddle before bouncing back
	private boolean hitBottomWallOrPaddle(GOval ball) {
		return ball.getY() >= (getHeight() - ball.getHeight()) - PADDLE_HEIGHT;
	} 

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	// When checking if the ball has hit right wall,
	// we need to remove the ball's width so that it doesn't look like
	// the ball has disappeared behind the wall before bouncing back
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= (getWidth() - ball.getWidth());
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	/** Check if the four corners of ball has collided with an object 
	 */
	private GObject getCollidingObject() {
		GObject tempObject;
		
		tempObject = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS); // BottomLeft
		// Make sure that there is only one collided object detected at a given time
		if (tempObject != null) return tempObject; 
		
		tempObject = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS); //BottomRight
		if (tempObject != null) return tempObject;
		
		tempObject = getElementAt(ball.getX(), ball.getY()); // UpperLeft
		if (tempObject != null) return tempObject;
		
		tempObject = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()); //UpperRight
		if (tempObject != null) return tempObject;
				
		return tempObject;
	}
	
}


	
		


