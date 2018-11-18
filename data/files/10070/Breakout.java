/*
 * File: Breakout.java
 * -------------------
 * Name: Courtney Urbancsik
 * Section Leader: Vineet Kosaraju
 * 
 * This file implements the game of Breakout. It sets up a Brick Breaker game with
 * colored bricks and then allows NTURNS for the users to try to remove all the bricks, 
 * while the program resets every turn. This program for loops, if loops, and graphics to create the
 * set up, mouse listeners to allow the user to "move" the paddle, a RandomGenerator to 
 * start the game with a random x-velocity each round, and the null concept to test if the 
 * ball is colliding with any object.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Constants
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	public static final int NBRICK_COLUMNS = 10;
	public static final int NBRICK_ROWS = 10;

	public static final double BRICK_SEP = 4;
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);
	public static final double BRICK_HEIGHT = 8;
	public static final double BRICK_Y_OFFSET = 70;

	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;
	public static final double PADDLE_Y_OFFSET = 30;

	public static final double BALL_RADIUS = 10;

	public static final double VELOCITY_Y = 3.0;
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	public static final double DELAY = 1000.0 / 100.0;
	public static final int NTURNS = 3;
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	// Instance Variables
	private GRect brick = null;
	private GRect paddle = null;
	private GOval ball = null;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int numOfBricks = NBRICK_ROWS * NBRICK_COLUMNS;

	public void run() {
		// Allows user to play games for NTURNS
		for(int i=0; i < NTURNS; i++) {
			setUpGame();
			playGame();
			// User wins game is numOfBricks is 0
			if(numOfBricks == 0) {
				ball.setVisible(false);
				printYouWin();
				break;
			} 
			// Ends Game if NTURNS are all used
			else if(numOfBricks > 0) {
				removeAll();
			}
		} 
		// User loses game if numOfBricks > 0 when all NTURNS are used
		if(numOfBricks > 0) {
			printGameOver();
		}
	}

	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		createPaddle();
		createBall();
	}

	// Create colored brick layout that is centered on screen
	private void setUpBricks() {
		for (int row = 0 ; row < NBRICK_ROWS; row++) { 
			for  (int col=0; col < NBRICK_ROWS; col++) {
				// Parameters for dimensions of  rows 
				double rowXLocation = getWidth()/2 - (NBRICK_ROWS * BRICK_WIDTH)/2 - ((NBRICK_ROWS-1) *BRICK_SEP)/2  + col * ( BRICK_WIDTH + BRICK_SEP );
				double rowYLocation = row * ( BRICK_HEIGHT + BRICK_SEP );
				// Creates brick 
				GRect brick = new GRect (rowXLocation,rowYLocation, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick); 
				// Defines two rows of each colored brick
				if (row < 2) {
					brick.setColor(Color.RED);
				}
				if (row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
				}
				if (row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				}
				if (row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				}
				if (row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}

	// Create paddle that follows with mouse movement in x-direction and remains on screen
	private void createPaddle() {
		double startX = getWidth()/2 - PADDLE_WIDTH/2;
		double startY = getHeight()-PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect (startX,startY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners(); 
	}
	// Define mouseMove event to have the paddle track the x-movement through e.getX
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double lockedY = getHeight()-PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		// Keep paddle fully on screen
		if ((mouseX < getWidth() - PADDLE_WIDTH/2) && (mouseX > PADDLE_WIDTH/2)) {
			paddle.setLocation(mouseX - PADDLE_WIDTH/2, lockedY);
		}
	}

	// Create ball and center it on the screen
	private void createBall() {
		double startX = getWidth()/2 - BALL_RADIUS;
		double startY = getHeight()/2 - BALL_RADIUS;
		ball = new GOval (startX,startY, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	// Allows user to play NTURNS rounds
	private void playGame() {
		// Game starts once the user clicks the screen
		waitForClick(); 
		getBallVelocity();
		while (true) {
			moveBall();
			// Stop round when ball misses paddle and falls through floor
			if (ball.getY() >= getHeight()) {
				break;
			}
			// When number of remaining bricks is 0, terminate game
			if (numOfBricks == 0)  {
				break;
			}
		}
	}

	// Define velocity
	private void getBallVelocity() {
		vy = VELOCITY_Y;
		// Uses the RandomGenerator to define a x-velocity within the defined range
		vx = rgen.nextDouble(1.0, 3.0);
		// Make the x-velocity negative half the time
		if (rgen.nextBoolean(0.5)) vx = -vx;	
	}

	private void moveBall() {
		ball.move(vx,vy);
		// Slow ball down so user can see the ball's movement
		pause(DELAY);
		ifCollideWalls();
		getCollidingObject();
		GObject collider = getCollidingObject();
		if (collider==paddle) {
			bounceOffPaddle();
			// If ball collides with brick (only objects that are not the wall and not null), remove brick
		} else if (collider != null) {
			bounceSound();
			remove(collider);
			// To count the remaining number of bricks, subtract one from the current number of bricks
			numOfBricks--;
			// Bounce off brick
			vy = -vy;
		}
	}

	// Bounce of walls
	private void ifCollideWalls() {
		// If ball hits top wall (Y=0), bounce in opposite direction; Remember -vy is upwards motions
		if (ball.getY() - vy <= 0 && vy < 0) {
			vy = -vy;
			// If ball hits left wall bounce in opposite direction; Remember -vx is leftwards motions
		} else if (ball.getX() - vx <= 0 && vx < 0) {
			vx = -vx; 
			// If ball hits right wall bounce in opposite direction; Remember vx is rightwards motions
		} else if (ball.getX() + vx >= (getWidth() - 2*BALL_RADIUS) && vx > 0) {
			vx = -vx;
		}
	}

	// Determine if ball collides with any object (paddle or brick)
	private GObject getCollidingObject() {
		// Check ball's top left corner
		if( (getElementAt (ball.getX(), ball.getY() ) ) != null) {
			return getElementAt (ball.getX(), ball.getY());
		} 	
		// Check ball's top right corner
		else if ( getElementAt( (ball.getX() + 2*BALL_RADIUS), ball.getY() ) != null ) {
			return getElementAt (ball.getX() + 2*BALL_RADIUS, ball.getY() );
		}
		// Check ball's bottom left corner
		else if( getElementAt (ball.getX(), (ball.getY() + 2*BALL_RADIUS) ) != null ) {
			return getElementAt (ball.getX(), ball.getY() + 2*BALL_RADIUS);
		}
		// Check ball's bottom right corner
		else if( getElementAt ((ball.getX() + 2*BALL_RADIUS), (ball.getY() + 2*BALL_RADIUS) ) != null ) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS*2, ball.getY() + 2*BALL_RADIUS);
		}
		// Return null if there are no objects present
		else {
			return null;
		}
	}

	private void bounceOffPaddle() {
		double paddleY = getHeight()-PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		// Reverse y-velocity if ball lands moves within the height of the paddle
		if (ball.getY() >= paddleY - 2*BALL_RADIUS && ball.getY() < paddleY - 2*BALL_RADIUS + vy) {
			bounceSound();
			vy = -vy;
		}
	}

	private void bounceSound() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		bounceClip.play();
	}

	private void printYouWin() {
		GLabel youWin = new GLabel ("You Win!");
		double labelXCenter = getWidth()/2 - youWin.getWidth()/2;
		double labelYCenter = getHeight()/2 + youWin.getAscent()/2;
		add (youWin, labelXCenter, labelYCenter);
	}

	private void printGameOver() {
		GLabel gameOver = new GLabel ("Game Over");
		double labelXCenter = getWidth()/2 - gameOver.getWidth()/2;
		double labelYCenter = getHeight()/2 + gameOver.getAscent()/2;
		add (gameOver, labelXCenter, labelYCenter);
	}

}