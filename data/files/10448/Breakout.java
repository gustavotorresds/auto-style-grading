/*
 * File: Breakout.java
 * -------------------
 * Name: Martin Altenburg
 * Section Leader: Andrew Davis
 * 
 * This program creates an environment for Breakout to be played and uses mouse inputs
 * from the user to play the game. The goal of this game is to be able to clear all the
 * bricks before having the game end from losing turns (through letting the ball fall 
 * below the screen.  
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle;

	private GOval ball;
	
	private int turnsRemaining = NTURNS;

	private int NBRICKS = NBRICK_ROWS * NBRICK_COLUMNS;

	private boolean isMouseClicked = false;


	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*
	 * This is the run method that sets up and plays the whole game. It first sets up the
	 * components for the game and then add mouse input.
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		setUpPaddle();
		setUpBall();
		addMouseListeners();
		playGame();
	}

	/*
	 * This method is used to create the array of bricks used within the game. Nested for loops
	 * are used in this method in order to create a grid of a certain dimension composed of 
	 * bricks whose colors change every two rows.
	 */
	private void setUpBricks() {
		for (int j = 0; j < NBRICK_ROWS; j++) {
			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				double x = getWidth() / 2 - (BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS / 2  + i * (BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * j ;
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				if (j >= 0) {
					rect.setColor(Color.RED);
				}
				if (j >= NBRICK_ROWS / 5) {
					rect.setColor(Color.ORANGE);
				}
				if (j >= NBRICK_ROWS * 2 / 5) {
					rect.setColor(Color.YELLOW);
				}
				if (j >= NBRICK_ROWS * 3 / 5) {
					rect.setColor(Color.GREEN);
				}
				if (j >= NBRICK_ROWS * 4 / 5) {
					rect.setColor(Color.CYAN);
				}
				add(rect);
			}	
		}
	}

	/*This method creates the paddle that is in the opening screen of the
	 * program. It does so by changing the empty instance variable to a 
	 * solid rectangle. 
	 */
	private void setUpPaddle() {
		paddle = new GRect(getWidth() / 2 - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	private void setUpBall() {
		ball = new GOval(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * This is the method that plays the actual game through user input. Points within this method are combinations
	 * of the x and y coordinate variables called near the beginning of this program. This method also tells the ball
	 * how to respond to a collision and what direction for it to go to. The pause command is also used since the ball is 
	 * an animated figure. Loops used to count the number of turns left and bricks broken are also used to make sure to
	 * end the game when necessary. Overall, this is the largest and most important method since it contains variables
	 * that are difficult to keep track of within private voids. 
	 */
	private void playGame() {
		//In the case that all bricks have been removed or all lives have been lost, the game ends.
		while (turnsRemaining != 0 && NBRICKS != 0) {
			ball.setLocation(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
			//Sets up ball direction the level will start with.
			double vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			double vy = VELOCITY_Y;
			//This command starts the level once the mouse is clicked.
			waitForClick();
			while(ball.getBottomY() < getHeight() ) {
				double a = ball.getX();
				double b = ball.getY();
				double c = ball.getRightX();
				double d = ball.getBottomY();
				if (a <= 0 || c >= getWidth()) {
					vx = -vx;
				}
				if (b <= 0) {
					vy = -vy;
				}
				GObject collider = getCollidingObject(a, b);
				if (collider != null) {
					if (collider == paddle) {
						vy = -vy;
					}
					//Specifies the conditions to for what direction for the ball
					//will bounce.
					else {
						if (collider.getX() + BRICK_WIDTH >= a && collider.getX() + BRICK_WIDTH - 3 <= a) {
							vx = -vx;
						}
						if (collider.getX() <= c && collider.getX() + 3 >= c) {
							vx = -vx;
						}
						if (collider.getY() + BRICK_HEIGHT >= b && collider.getY() + BRICK_HEIGHT - 3 <= b) {
							vy = -vy;
						}
						if (collider.getY() + 3 >= d && collider.getY() <= d) {
							vy = -vy;
						}
						remove(collider);
						NBRICKS --;
					}
				}
				pause(DELAY);
			}
			vx = 0;
			vy = 0;
			turnsRemaining --;
		}
	}



	/*
	 * This method responds directly to actions completed by the mouse input. In this case, the center of 
	 * the paddle follows the mouse within the x direction but maintains its y coordinate. While the 
	 * paddle doesn't directly move, it has its location continually reset to follow the mouse's x 
	 * coordinate.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if (x > 0 && x < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(x, y);
		}
	}

	/*This method is used in order to check every corner or the GOval in order to see whether
	 * or not the ball is colliding with any object. Using inputs that come from the x and y 
	 * coordinates of the upper left hand corner of the GOval, each corner is tested to see 
	 * to see whether or not an object exists.
	 */
	private GObject getCollidingObject(double a, double b) {
		GObject object1 = getElementAt(a, b);
		if (object1 != null) {
			return object1;
		}
		GObject object2 = getElementAt(a + 2 * BALL_RADIUS, b);
		if (object2 != null) {
			return object2;
		}
		GObject object3 = getElementAt(a, b + 2 * BALL_RADIUS);
		if (object3 != null) {
			return object3;
		}
		GObject object4 = getElementAt(a + 2 * BALL_RADIUS, b + 2 * BALL_RADIUS);
		if (object4 != null) {
			return object4;
		}
		return null;
	}

	/*
	 *Simply, this method recognizes when a mouse has been clicked and sets an instandce boolean to true
	 *which is useful within other programs. 
	 */
	public void mouseClicked(MouseEvent e) {
		isMouseClicked = true;
	}
}