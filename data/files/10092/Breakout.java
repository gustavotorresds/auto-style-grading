/*
 * File: Breakout.java
 * -------------------
 * Name: Jocelyn Kang
 * Section Leader: Chase Davis
 * 
 * This file is the game Breakout that fulfills all the basic requirements of the assignment.
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

	//Random generator instance variable
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Audio clip for bounce
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();
		for (int i = 0; i < NTURNS; i++) playGame();
		remove(ball);
	}

	/** Method: Set Up Game
	 * --------------------
	 * Creates the game board with colored bricks, a paddle, and a ball that moves when the mouse button is clicked.
	 */

	private void setUpGame() {
		createBricks();
		createBall();
		createPaddle();
	}

	/** Method: Create Bricks
	 * ----------------------
	 * Creates the rainbow bricks 
	 */
	private void createBricks() {
		for (int i = 0; i < NBRICK_ROWS; i ++) { //makes new row
			for (int j = 0; j < NBRICK_COLUMNS; j ++) { //makes each brick in column

				//gets X coordinate of first column
				double x = (getWidth() - (NBRICK_ROWS * BRICK_WIDTH)) / 2 - BRICK_WIDTH / 2 + ((BRICK_WIDTH + BRICK_SEP) * i); 

				//gets Y coordinate of first column
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * j;

				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				add(brick, x, y);
				brick.setFilled(true);

				//makes bricks rainbow colors 
				//The remainder determines what color to paint the brick
				if (j % 10 == 0 || j % 10 == 1)  brick.setColor(Color.RED);	
				if (j % 10 == 2 || j % 10 == 3)  brick.setColor(Color.orange);
				if (j % 10 == 4 || j % 10 == 5)  brick.setColor(Color.YELLOW);
				if (j % 10 == 6 || j % 10 == 7) brick.setColor(Color.GREEN);
				if (j % 10 == 8 || j % 10 == 9) brick.setColor(Color.CYAN);
			}
		}
	}

	/** Method: Create Ball
	 * --------------------
	 * Creates a ball in center of screen and returns it so it can be used for animation
	 */

	private GOval createBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, getWidth() / 2, getHeight() / 2);
		return ball;
	}
	
	/** Method: Create Paddle
	 * ----------------------
	 * Creates a paddle
	 */

	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, paddleX, paddleY);
		addMouseListeners();

	}

	/** Method: Mouse Moved
	 * --------------------
	 * Makes the paddle track the mouse clicker
	 */

	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX() - (PADDLE_WIDTH / 2);
		double mouseY= getHeight() - PADDLE_Y_OFFSET;
		//need if statement that limits movement of paddle to being on screen
		//if x coordinate of mouse is greater than 0 and less than getWidth() - PADDLE_WIDTH the paddle is set to track mouse pointer
		if (mouseX >= 0 && mouseX <= getWidth() - PADDLE_WIDTH) paddle.setLocation(mouseX, mouseY);
	}

	/** Method: Play Game
	 * ------------------
	 *  The player can now begin hitting bricks with the ball by bouncing it off the paddle
	 */

	private void playGame() {
		waitForClick();
		makeBallMove();
	}

	/** Method: Make Ball Move
	 * -----------------------
	 * Will make the ball move and bounce off walls and bricks.
	 */

	private void makeBallMove() {
		//creates counter that keeps track of how many bricks there are on the screen
		counter = NBRICK_ROWS * NBRICK_COLUMNS;
		//creates initial x and y velocities for ball, and counts the turns
		vx = rgen.nextDouble  (VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(.5)) vx = -vx;		
		vy = VELOCITY_Y;

		while (true) {

			//checks for collisions with paddle or with brick
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				vy = -1 * Math.abs(vy); //this fixes the ball sticking to the paddle because it makes the velocity always
				//positive when the ball hits the paddle so the ball won't constantly be turning around.

			}
			else if (collider != null) { //if the collider isn't the paddle, or 
				//a null value, it's a brick. 
				remove(collider);
				counter --;
				vy = -vy;
				if (counter == 0) {
					remove(ball);
					break;
				}
			}
			if (hitsLeftWall(ball) || hitsRightWall(ball)) vx = -vx; //checks if ball hits walls
			if (hitsTopWall(ball)) vy = -vy;
			if (hitsBottomWall(ball)) {
				remove(ball);
				break;
			}
			ball.move(vx, vy);
			pause (DELAY);
		}
		if (counter != 0) add(ball, (getWidth() - ball.getWidth()) / 2, (getHeight()-ball.getHeight()) / 2);
	}


	/** Method: Get Colliding Object
	 * -----------------------------
	 * Returns whether or not a ball has hit an object. If the ball coordinates touch a non-null value, 
	 * that value is returned as the object. If neither one of the coordinates touches any non-null values, 
	 * the value of the object will be returned as null.
	 */

	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			return (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS));
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		} else {
			return null;
		}
	}


	/** Method: Hits Bottom Wall
	 * -------------------------
	 * Returns if ball has passed the bottom wall
	 */

	private boolean hitsBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/** Method: Hits Top Wall
	 * ---------------------
	 * Returns whether or not the ball should bounce off the top wall
	 */

	private boolean hitsTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/** Method: Hits Right Wall
	 * ---------------------
	 * Returns whether or not the ball should bounce off the right wall
	 */

	private boolean hitsRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/** Method: Hits Top Wall
	 * ---------------------
	 * Returns whether or not the ball should bounce off the top wall
	 */

	private boolean hitsLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/** Instance variable for ball */
	private GOval ball;

	/**instance variable for paddle */
	private GRect paddle;

	/** instance variable for velocity */
	private double vx, vy;

	/** instance variable for brick counter */
	private int counter;
}
