/*
 * File: Breakout.java
 * -------------------
 * Name: Elyssa Hofgard
 * Section Leader: Shanon Reckinger
 * 
 * This program will implement the basic version of the game Breakout.
 * The game will consist of a ball, a certain number of colored bricks,
 * and a paddle. The user will bounce the ball off of the paddle to attempt 
 * to break the maximum number of bricks. The user will have a certain number
 * of turns to attempt to break all of the bricks. If the ball falls off of the 
 * bottom screen, the user's turn is finished.
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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 3.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Instance variable for the paddle
	private GRect paddle = null;

	// Instance variable for the ball.
	private GOval ball = null;

	// Instance variable for the random-number generator.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Instance variables for the x and y velocities.
	private double vx, vy;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Sets up the world for the game.
		setUpGame();

		// Allows the user to play the game.
		playGame();

		// Adds mouse listeners to respond to paddle movement.
		addMouseListeners();
	}

	/*
	 * Plays the game for three turns. 
	 * The number of turns is given by an 
	 * instance variable that can be changed.
	 */
	private void playGame() {
		for (int i = 0; i < NTURNS; i++) {
			ball = makeBall();
			animateBall(ball);
		}
	}

	/*
	 * Sets up the game with the rows of bricks and the paddle.
	 */
	private void setUpGame() {
		setUpRectangles();
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		addPaddletoScreen();
	}


	/*
	 * Sets up and colors the rows of bricks at the top of the screen.
	 * Loops over the rows and the columns.
	 */
	private void setUpRectangles() {
		// Creates each row of bricks.
		for (int r = 0; r < NBRICK_ROWS; r++) {
			double y = BRICK_Y_OFFSET + BRICK_HEIGHT * r + BRICK_SEP * r;

			// Creates each column of bricks.
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = getWidth() / 2 - (NBRICK_COLUMNS / 2.0) * (BRICK_WIDTH + BRICK_SEP) + (BRICK_WIDTH + BRICK_SEP) * c + BRICK_SEP / 2;

				// Creates a rectangle with given coordinates, fills the rectangle, and adds it to the screen.
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				add(rect);

				// Fills the rows of bricks. This color pattern will repeat for a given number of bricks.
				if (r % 10 == 0 || r % 10 == 1) {
					rect.setColor(Color.RED);
				}
				else if (r % 10 == 2 || r % 10 == 3) {
					rect.setColor(Color.ORANGE);
				}
				else if (r % 10 == 4 || r % 10 == 5) {
					rect.setColor(Color.YELLOW);
				}
				else if (r % 10 == 6 || r % 10 == 7) {
					rect.setColor(Color.GREEN);
				}
				else {
					rect.setColor(Color.CYAN);
				}
			}
		}
	}

	/*
	 * The paddle will follow the movement of the mouse.
	 * The mouse must be moved for the program to enter this method.
	 */
	public void mouseMoved(MouseEvent e) {
		if (e.getX() < getWidth() && e.getX() > PADDLE_WIDTH) {
			//Gets the coordinates of the mouse.
			double x = e.getX() - PADDLE_WIDTH;
			double y = getHeight() - PADDLE_Y_OFFSET;
			//Sets the paddle location to the location of the mouse.
			paddle.setLocation(x, y);
		}
	}

	/*
	 * Adds the paddle to the screen with defined x and y coordinates.
	 */
	private void addPaddletoScreen() {
		double x = getWidth() / 2-PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}

	/*
	 * Makes the ball for the game.
	 * Ball radius is given by an instance variable.
	 */
	private GOval makeBall() {
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval(x, y, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		return ball;
	}

	/*
	 * Returns a boolean for if the ball hits the bottom wall.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/*
	 * Returns a boolean for if the ball hits the top wall.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * Returns a boolean for if the ball hits the right wall.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth()-ball.getWidth();
	}

	/* 
	 * Returns a boolean for if the ball hits the left wall.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/*
	 * Updates the velocity of the ball. The ball must be moving.
	 * If the ball hits the left wall or the right wall, the direction of its x velocity will reverse.
	 * If the ball hits the top wall, the direction of its y velocity will reverse.
	 */
	private void updateVelocity(GOval ball) {
		if (hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if (hitTopWall(ball)) {
			vy = -vy;
		}
	}

	/*
	 * Animates the ball for given conditions.
	 */
	private void animateBall(GOval ball) {
		// The number of bricks that the game begins with.
		int count = NBRICK_COLUMNS * NBRICK_ROWS;

		// Random x velocity for given bounds.
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		// The x velocity will be negative half of the time.
		if (rgen.nextBoolean(0.5)) {
			vx = - vx;
		}
		// The original y velocity.
		vy = VELOCITY_Y;

		/*
		 * If the ball is on the screen, will update the y velocity components.
		 * Depends if the ball hits a paddle or a brick.
		 */
		while(ball.getY()<=getHeight()) {
			// Updates the velocity.
			updateVelocity(ball);
			// Gets the object that the ball collides with.
			GObject collider = getCollidingObject();

			/*
			 * If the colliding object is the paddle, makes the ball bounce off of it.
			 * Accomplishes this by reversing direction of y velocity.
			 */
			if (collider == paddle) {
				//Fixes the "sticky paddle" issue by defining a range that the ball will not bounce in.
				if (ball.getY() >= getHeight() - PADDLE_Y_OFFSET - BALL_RADIUS * 2 && ball.getY() < getHeight() - PADDLE_Y_OFFSET - BALL_RADIUS * 2 + 3) {
					vy = -vy;
				}
			}

			/*
			 * If the colliding object is anything else, the object will be removed.
			 * In this case, the only other objects are the bricks.
			 */
			else if (collider!=null) {
				// Removes the brick if the ball hits it.
				remove(collider);
				// Reverses the direction of the y velocity.
				vy = -vy;
				// Updates the count for the number of bricks that have been hit by the ball.
				count --;
				// Removes the ball if all of the bricks have been removed.
				if (count == 0) {
					remove(ball);
				}
			}

			// If the ball hits the bottom wall, it will appear to fall off of the screen.
			if (hitBottomWall(ball)) {
				remove(ball);
			}

			// Animates the ball from given x and y velocities.
			ball.move(vx, vy);	
			pause(DELAY);
		}
	}

	/*
	 * Returns the object that the ball has collided with.
	 * Uses the bounding rectangle of the ball to check different points of collision.
	 */
	private GObject getCollidingObject() {
		// Checks if there is an element at the upper left corner of the ball.
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return (getElementAt(ball.getX(), ball.getY()));
		}

		// Checks if there is an element at the upper right corner.
		else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			return (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()));
		}

		// Checks if there is an element at the lower left corner.
		else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			return (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS));
		}

		// Checks if there is an element at the lower right corner.
		else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			return (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS));

		} else {
			return null;
		}

	}
}


