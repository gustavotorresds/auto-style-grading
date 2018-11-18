/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This program recreates the classic game of Breakout
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
	
	// value of brick for score
	public static final int BRICK_VALUE = 10;
	
	// instance variable for the paddle
	GRect paddle = null;
	
	// instance variable for the ball
	GOval ball = null;
	
	// instance variables for the speed of the ball
	private double vx, vy;
	
	// instance variable for random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// audio clip for bounce sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// instance variable for remaining bricks left
	private int bricks_left = NBRICK_ROWS * NBRICK_COLUMNS;
	
	// instance variable for running game score
	private int score = 0;
	
	// instance variable for score label
	GLabel scoreText = null;
	
	// instance variable for object the ball hits
	GObject collider = null;
	
	
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size using CANVAS_WIDTH and CANVAS_HEIGHT.  Use getWidth()
		// and getHeight() later to get actual screen dimensions.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* first add the bricks */
		makeBricks();
		
		// add the paddle, and let it move with the mouse //
		makePaddle();
		addMouseListeners();
		
		// show initial score
		scoreText = new GLabel("SCORE: " + score, 10, CANVAS_HEIGHT - 5);
		scoreText.setFont("Courier-18");
		add(scoreText);
		
		// now add the ball and start it moving
		addBall();
		
		for (int i = NTURNS; i > 0; i--) {
			
			
			while (ball.getY() < CANVAS_HEIGHT && bricks_left > 0) {
				
				moveBall();
				
				//check for collision and resolve it
				collider = checkCollision();
				resolveCollision(collider);
				
				pause(DELAY);
			}
			/* once a ball leaves the screen display the number
			 * of turns left and start another ball, or display
			 * the end of game message, or display the win message
			 */
			if (i>1 && bricks_left > 0) {
				displayTurns(i-1);
				addBall();
			}
			if (i<=1 && bricks_left > 0) {
				displayEnd();
			}
			if (bricks_left < 1) {
				displayWin();
			}
		}	
	}
	/* displayWin displays the win message when all bricks have been removed
	 */
	private void displayWin() {
		GImage winImage = new GImage("win.png");
		double x_location = (CANVAS_WIDTH - winImage.getWidth()) / 2;
		double y_location = (CANVAS_HEIGHT - winImage.getHeight()) / 2;
		add(winImage, x_location, y_location);
		
	}
	/* checkCollision checks to see if the ball collides with the paddle or a
	 * brick, changes the direction of the ball if yes and makes a sound, and
	 * returns the colliding object
	 */
	private GObject checkCollision() {
		// get position of ball
		double x_ball = ball.getX();
		double y_ball = ball.getY();
		
		// define object of collision
		GObject object = null;
		
		//define points to check on ball
		double x1 = x_ball + 0.95 * BALL_RADIUS;
		double y1 = y_ball;
		double x2 = x_ball + 2 * BALL_RADIUS;
		double y2 = y_ball + 0.95 * BALL_RADIUS;
		double x3 = x_ball + 1.05 * BALL_RADIUS;
		double y3 = y_ball + 2 * BALL_RADIUS;
		double x4 = x_ball;
		double y4 = y_ball + 1.05 * BALL_RADIUS;
		
		// check points on ball for contact
		if (getElementAt(x1, y1) != null && getElementAt(x1, y1) != scoreText) {
			object = getElementAt(x1, y1);
			bounceClip.play();
			vy = -vy;
		}
		if (getElementAt(x2, y2) != null && getElementAt(x2, y2) != scoreText) {
			object = getElementAt(x2, y2);
			bounceClip.play();
			vx = -vx;
		} 
		if (getElementAt(x3, y3) != null && getElementAt(x3, y3) != scoreText) {
			object = getElementAt(x3, y3);
			bounceClip.play();
			vy = -vy;
		}	
		if (getElementAt(x4, y4) != null && getElementAt(x4, y4) != scoreText) {
			object = getElementAt(x4, y4);
			bounceClip.play();
			vx = -vx;
		}
		return(object);
	}
	/* resolveCollision updates the score, and removes
	 *  the colliding object if it is a brick
	 */
	private void resolveCollision(GObject object) {
		
		if (object != paddle && object != null) {
			remove(object);
			score += BRICK_VALUE;
			bricks_left -= 1;
			remove(scoreText);
			scoreText = new GLabel("SCORE: " + score);
			scoreText.setFont("Courier-18");
			add(scoreText, 10, CANVAS_HEIGHT - 5);
		}
	}
	// displayEnd displays the end of game message
	private void displayEnd() {
		GLabel endMessage = new GLabel("Game over - good job!");
		endMessage.setFont("Courier-20");
		double x_location = (CANVAS_WIDTH - endMessage.getWidth()) / 2;
		double y_location = (CANVAS_HEIGHT + endMessage.getAscent()) / 2;
		add(endMessage, x_location, y_location);
		
	}
	/* displayTurns displays the number of balls remaining in the game
	 * for 1 second before starting the next ball
	 */
	private void displayTurns(int i) {
		GLabel turns = new GLabel("You have " + i + " turn(s) left - are you ready?");
		turns.setFont("Courier-20");
		double x_location = (CANVAS_WIDTH - turns.getWidth()) / 2;
		double y_location = (CANVAS_HEIGHT + turns.getAscent()) / 2;
		add(turns, x_location, y_location);
		pause(1000);
		remove(turns);
	}
	/* makeBricks sets up NBRICK_ROWS rows and NBRICK_COLUMNS columns of bricks for
	 * a game of breakout
	 */
	private void makeBricks() {
		//find starting x of the top row of bricks, which is row 10
		double x_start = (CANVAS_WIDTH - (10 * BRICK_WIDTH) - (9 * BRICK_SEP)) / 2;
		
		// now add bricks //
		for (int i = NBRICK_ROWS; i > 0; i--) {
			for (int j = 1; j < NBRICK_COLUMNS + 1; j ++) {
				double x_coord = x_start + (i - 1) * (BRICK_SEP + BRICK_WIDTH);
				double y_coord = BRICK_Y_OFFSET + (j - 1) * (BRICK_SEP + BRICK_HEIGHT);
				GRect rect = new GRect(x_coord, y_coord, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				
				// add fill color to bricks //
				switch (j) {
					case 1: rect.setColor(Color.RED); break;
					case 2: rect.setColor(Color.RED); break;
					case 3: rect.setColor(Color.ORANGE); break;
					case 4: rect.setColor(Color.ORANGE); break;
					case 5: rect.setColor(Color.YELLOW); break;
					case 6: rect.setColor(Color.YELLOW); break;
					case 7: rect.setColor(Color.GREEN); break;
					case 8: rect.setColor(Color.GREEN); break;
					case 9: rect.setColor(Color.CYAN); break;
					case 10: rect.setColor(Color.CYAN); break;
				}
				add (rect);
			}
		}			
	}
	/* makePaddle makes a paddle at the bottom of the screen for a game of breakout.
	 * the paddle is controlled by the mouse
	 */
	private void makePaddle() {
		//start the paddle centered on the screen, PADDLE_Y_OFFSET from the bottom
				double x_start = CANVAS_WIDTH / 2 - PADDLE_WIDTH / 2;
				double y_start = CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
				
				// add the filled paddle to the screen //
				paddle = new GRect(x_start, y_start, PADDLE_WIDTH, PADDLE_HEIGHT);
				paddle.setFilled(true);
				add (paddle);
	}
	
	/* mouseMoved moves the paddle only in the x direction based on the mouse movements
	 */
	public void mouseMoved(MouseEvent event) {
		
		// define the mouse x position and limit paddle movement to stay on the screen//
		int x = event.getX();
		if (x > 0 && x < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(x, CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
	/* addBall adds a ball for a game of breakout with an initial velocity
	 */
	private void addBall() {
		/* add the ball at the center of the screen
		*/ 
		double x_start = CANVAS_WIDTH / 2 - BALL_RADIUS;
		double y_start = CANVAS_HEIGHT / 2 - BALL_RADIUS;
		
		ball = new GOval(x_start, y_start, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add (ball);
		
		// set the ball's initial velocity
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = 3.0;
	}
	/* moveBall moves the ball, and rebounds off walls with a sound
	 */
	private void moveBall() {
		
		ball.move(vx, vy);
		
		//get position of ball
		double x_ball = ball.getX();
		double y_ball = ball.getY();
		
		/* if ball contacts wall, reverse x velocity (side walls) or
		 * y velocity (top wall)
		*/
		if (x_ball < 0 || x_ball + 2 * BALL_RADIUS > CANVAS_WIDTH) {
			bounceClip.play();
			vx = -vx;
		}
		if (y_ball < 0) {
			bounceClip.play();
			vy = -vy;
		}
	}
}
