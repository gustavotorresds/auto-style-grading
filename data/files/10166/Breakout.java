/*
 * File: Breakout.java
 * -------------------
 * Name: Natalie Hojel
 * Section Leader: Julia Daniel 
 * 
 * This file implements the game of Breakout. The user can 
 * now play the game Breakout.
 * 
 * Precondition: The screen is empty.
 * Postcondition: The user can play Breakout. 
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
	
	// Instance variable for the paddle to be tracked. 
	GRect paddle = null;  
	
	// Instance variable for the ball to be animated.
	GOval ball = null; 
	
	// Instance variables for velocity.
	private double vx, vy; 
	
	// Random generator to use for vx.
	private RandomGenerator rgen = RandomGenerator.getInstance(); 
	
	// Instance variable for counting bricks.
	private int countBricks = 0;

	public void run() {
		setUpGame(); 
		playGame(); 
	}

	/* This method includes all the setup for the game. It adds the bricks, paddle 
	 * and mouse listeners for the game. 
	 * 
	 * Precondition: The screen is empty. 
	 * Postcondition: The breakout game elements are on the screen. 
	 */
	private void setUpGame() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Setup the elements. 
		setBricks();
		paddle = createPaddle();  
		addMouseListeners(); 
	}

	/* This method animates the ball and allows to player to play the game.
	 * 
	 * Precondition: The game is set up but the ball doesn't move or acknowledge the
	 * presence of other objects.
	 * Postcondition: The ball moves around the screen, acknowledges other objects
	 * in the game, and the player can now win or lose. 
	 */
	private void playGame() {
		// Animate the game.
		ball = createBall();  
		boolean gameWon = false; 
		for(int i = 0; i < NTURNS; i++) {
			gameWon = animateBall(); 	
			if(gameWon) {
				break; 
			}
			resetBall(); 
		}
		remove(ball); 
		printResult(gameWon); 
	}

	/* This method creates the bricks at the top of the game. It creates
	 * the rainbow-like color pattern of bricks.
	 * 
	 * Precondition: There are no bricks on the screen.
	 * Postcondition: There is a rainbow-like sequence of bricks at the 
	 * top of the screen. 
	 */
	private void setBricks() {
		int row = NBRICK_ROWS;
		for(int i = NBRICK_ROWS; i > 0; i--) { //number of rows 
			double xcoord = (getWidth() - ((BRICK_WIDTH + BRICK_SEP)*(NBRICK_COLUMNS)))/2; 
			double ycoord = BRICK_Y_OFFSET + ((BRICK_HEIGHT + BRICK_SEP)*(NBRICK_ROWS - i)); 
			row--; 
			for(int j = 0; j < NBRICK_COLUMNS; j++) { //bricks per row
				GRect rect = new GRect (xcoord + (BRICK_WIDTH + BRICK_SEP)*j, ycoord, BRICK_WIDTH, BRICK_HEIGHT); 
				add(rect); 
				rect.setFilled(true); 
				if(row < NBRICK_ROWS-10) { //restarts the color pattern after cyan color bricks
					row = NBRICK_ROWS-1;
				}
				if(row < NBRICK_ROWS) {
					rect.setColor(Color.RED);
				}
				if(row < NBRICK_ROWS-2) {
					rect.setColor(Color.ORANGE);
				}
				if(row < NBRICK_ROWS-4) {
					rect.setColor(Color.YELLOW);
				}
				if(row < NBRICK_ROWS-6) {
					rect.setColor(Color.GREEN);
				}
				if(row < NBRICK_ROWS-8) {
					rect.setColor(Color.CYAN);
				}
			}
		}
	}
	/* This creates the paddle and allows it to move about the screen with the mouse.
	 * 
	 * Precondition: No paddle exists.
	 * Postcondition: The paddle is on the screen. 
	 */
	private GRect createPaddle() {
		double ycoord = getHeight() - PADDLE_Y_OFFSET; 
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, getWidth()/2 - PADDLE_WIDTH/2, ycoord); 
		return paddle;
	}

	/* Allows the paddle to move with the mouse. 
	 * 
	 * Precondition: The paddle does not move.
	 * Postcondition: The paddle moves horizontally when the mouse moves.
	 */
	public void mouseMoved(MouseEvent e) {
		double ycoord = getHeight() - PADDLE_Y_OFFSET; 
		int mouseX = e.getX();
		if(mouseX < (getWidth() - PADDLE_WIDTH)) {
			paddle.setLocation(mouseX, ycoord); 
		}
	}

	/* This method creates the ball and places it in the center of the screen. 
	 * 
	 * Precondition: There is no ball.
	 * Postcondition: There is a ball in the center of the screen.
	 */
	private GOval createBall() {
		double size = 2*BALL_RADIUS; 
		GOval ball = new GOval (size, size);
		ball.setFilled(true);
		add(ball, (getWidth()-size)/2, (getHeight()-size)/2); 
		return ball; 
	}
	/* This method animates the ball so that it bounces around the screen.
	 * 
	 * Precondition: The position of the ball is fixed.
	 * Postcondition: The ball bounces around the screen.
	 */
	private boolean animateBall() {
		vy = VELOCITY_Y; 
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
		if(rgen.nextBoolean(0.5)) vx = -vx; 
		waitForClick(); 
		while(true) {
			//update world
			ball.move(vx, vy);
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy; 
			}
			if(hitBottomWall(ball)) {
				return(false); 
			}
			// Ball notices the bricks and paddle when it hits them. 
			// Removes the bricks and bounces off the paddle.
			GObject collider = getCollidingObject();
			if(collider == paddle) {
				if(getElementAt(ball.getX(), ball.getY()) != paddle && getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != paddle) {
					vy = -Math.abs(vy);
				}
			} else if (collider != null) {
				vy = -vy; 
				remove(collider); 
				countBricks++;
				if(countBricks == NBRICK_COLUMNS*NBRICK_ROWS) {
					return(true); 
				}
			}
			//pause
			pause(DELAY); 
		}
	}

	/* Hit the Top Wall
	 * This method returns if the ball hits the top of the screen. 
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0; 
	}

	/* Hit the Bottom Wall
	 * This method returns if the ball hits the bottom of the screen.
	 */
	private boolean hitBottomWall (GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight(); 
	}

	/* Hit the Right Wall
	 * This method returns if the ball hits the right side of the screen.
	 */
	private boolean hitRightWall (GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth(); 
	}

	/* Hit the Left Wall
	 * This method returns if the ball hits the left side of the screen.
	 */
	private boolean hitLeftWall (GOval ball) {
		return ball.getX() <= 0; 
	}

	/* This method checks if there is an object at any of the four "corners" of the ball. 
	 * 
	 * Precondition: The ball moves through the GObjects.
	 * Postcondition: The ball removes the bricks and bounces off the paddle. 
	 */
	private GObject getCollidingObject() {
		double ballX = ball.getX(); 
		double ballY = ball.getY(); 
		//check each corner 
		GObject collider = getElementAt(ballX,ballY); 
		if(collider != null) {
			return(collider); 
		}
		collider = getElementAt(ballX + 2*BALL_RADIUS, ballY); 
		if( collider != null) {
			return(collider); 
		}
		collider = getElementAt(ballX, ballY + 2*BALL_RADIUS); 
		if(collider != null) {
			return(collider); 
		}
		collider = getElementAt(ballX + 2*BALL_RADIUS, ballY + 2*BALL_RADIUS); 
		if(collider != null) {
			return(collider); 
		} else {
			return(null); 
		}
	}

	/* This method puts the ball back in the middle of the screen after losing a life.
	 * 
	 * Precondition: Ball hits the bottom wall and stays there.
	 * Postcondition: Ball returns from the bottom wall to the middle of the screen. 
	 */
	private void resetBall() {
		ball.setLocation((getWidth()-2*BALL_RADIUS)/2, (getHeight()-2*BALL_RADIUS)/2);
	}

	/* This method prints the result: win or loss. 
	 * 
	 * Precondition: The game ends with no labels of winning or losing.
	 * Postcondition: The label tells the player whether they won or lost.
	 */
	private void printResult(boolean gameWon) {
		if(gameWon) {
			GLabel winLabel = new GLabel (" YOU WIN :) "); 
			add(winLabel, getWidth()/2 - winLabel.getWidth()/2, getHeight()/2 - winLabel.getAscent()/2);
		} else {
			GLabel loseLabel = new GLabel (" Game over. You lose :( "); 
			add(loseLabel, getWidth()/2 - loseLabel.getWidth()/2, getHeight()/2 - loseLabel.getAscent()/2); 
		}
	}
}

