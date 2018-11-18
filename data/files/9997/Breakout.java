
/*
 * File: Breakout.java
 * -------------------
 * Name: Elena Mosse
 * Section Leader: Julia Daniel
 * 
 * This file creates the game Breakout.
 * It makes a section of bricks and then allows the user to move a paddle
 * to hit a ball into the bricks, deleting them, until there are no bricks left
 * or the user runs out of turns.
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
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	public static final double MESSAGE_SPACING = 40;

	private GRect paddle = null;

	private GOval ball = null;

	private double vx, vy;

	private double ballXCoord, ballYCoord;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private int numBricksLeft;

	/*
	 * This run method calls other methods to set up the game (the bricks, the ball and the
	 * paddle) and then puts the ball into motion upon a mouse click. The paddle then follows
	 * the movement of the mouse and the ball bounces off objects, removing bricks as it 
	 * collides with them until either there are no more bricks or the user has run out of turns.
	 * The turn ends when the ball hits the bottom wall. The user gets 3 turns. The method also 
	 * announces whether the user has won or lost.
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		brickSetUp();

		makePaddle();

		addMouseListeners();

		makeBall();
		// allows the user to have 3 turns 
		for (int i = 0; i < 3; i++) {
			if (numBricksLeft > 0) { // checks to see if there are bricks left before executing
				ballMovement();
			}
		}
		// win or lose messages displayed
		if (numBricksLeft == 0) {
			GLabel messageToWinner = new GLabel("You WIN!", getCenterX(), getCenterY() - MESSAGE_SPACING);
			add(messageToWinner);
		} else if (numBricksLeft > 0) {
			GLabel messageToLoser = new GLabel("You LOSE!", getCenterX(), getCenterY() - MESSAGE_SPACING);
			add(messageToLoser);
		}
	}

	/*
	 * This method sets up the colored bricks at the top of the screen. 
	 * Pre: blank canvas 
	 * Post: 10 rows with 10 colored bricks in each, changing color every 2 rows.
	 */
	private void brickSetUp() {
		double yCoord = BRICK_Y_OFFSET; // initial ycoord of bricks 
		int countRows = 0;	// counts the row number to determine the color
		for (int row = 0; row < NBRICK_ROWS; row++) {
			// nested for loops to fill each row across and then descend to the next row
			double xCoord = ((getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH + BRICK_SEP * 9)) / 2); // initial xcoord
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				GRect brick = new GRect(xCoord, yCoord, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(countRows >= 10) // in case there are more than 10 rows restarts color pattern
				{
					countRows = 0;
				}
				if (countRows == 0 || countRows == 1) {
					brick.setColor(Color.RED);
				} else if (countRows == 2 || countRows == 3) {
					brick.setColor(Color.ORANGE);
				} else if (countRows == 4 || countRows == 5) {
					brick.setColor(Color.YELLOW);
				} else if (countRows == 6 || countRows == 7) {
					brick.setColor(Color.GREEN);
				} else if (countRows == 8 || countRows == 9) {
					brick.setColor(Color.CYAN);
				}
				add(brick);

				xCoord += BRICK_WIDTH + BRICK_SEP; // adjust x coord for each new brick in the row
			}
			countRows++; // for the purpose of determining the color
			yCoord = yCoord + BRICK_HEIGHT + BRICK_SEP; //adjust y coord each time finish a row
		}
		numBricksLeft = NBRICK_COLUMNS * NBRICK_ROWS; // to keep track of how many bricks are left later in the program
	}

	/*
	 * Creates a rectangular paddle that is filled and in the center initially.
	 */
	private void makePaddle() {
		paddle = new GRect((getWidth() / 2) - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * If the mouse is moved, the paddle's location is set to be the location of the mouse,
	 * making the paddle follow the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX < getWidth() && mouseX > PADDLE_WIDTH) { //accounts for paddle's width causing it to go off screen
			paddle.setLocation(mouseX - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	/*
	 * Creates the ball and puts it in the center.
	 */
	private void makeBall() {
		ball = new GOval(getCenterX() - BALL_RADIUS, getCenterY() - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * Sets the ball in motion somewhat randomly after the user clicks and then adjusts the direction 
	 * of the ball if it hits something (wall, brick, or paddle).
	 */
	private void ballMovement() {
		vx = rgen.nextDouble(1.0, 3.0); // sets vx to be a random double between 1 and 3
		if (rgen.nextBoolean(0.5))
			vx = -vx; // makes vx negative half the time
		vy = VELOCITY_Y;

		waitForClick(); // waits for user's click to move ball

		// while loop to keep the ball in motion so long as it doesn't hit the bottom of the canvas
		while (!ballHitBottomWall()) {
			if (numBricksLeft > 0) {
				checkForCollision(); // calls checkCollision class to deal with collisions
				if (ball.getX() <= 0 || ball.getX() >= getWidth() - BALL_RADIUS * 2) {
					vx = -vx; // switches the direction of the ball if it hits either left or right wall
				}
				if (ball.getY() <= 0) {
					vy = -vy; // switches the direction of the ball if it hits the top wall
				}
				ball.move(vx, vy); 
				pause(DELAY);
			}
			if (numBricksLeft == 0) { // displays winning message if the user wins as soon as there are no bricks left
				GLabel messageToWinner2 = new GLabel("You WIN!", getCenterX(), getCenterY() - MESSAGE_SPACING);
				add(messageToWinner2);
			}
		}
	}

	/*
	 * Checks to see if the ball has hit the bottom wall and if it returns true the ballMovement() while loop stops
	 */
	private boolean ballHitBottomWall() {
		if (ball.getY() >= getHeight() - BALL_RADIUS * 2) {
			ball.setLocation(getCenterX() - BALL_RADIUS, getCenterY() - BALL_RADIUS); // puts the ball back in the center to restart
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Checks each of the corners of the rectangle surrounding the GOval ball for collisions.
	 * If there is an object at the coordinates checked it calls the reactToCollidingObject()
	 * method to deal with the collision.
	 */
	private void checkForCollision() {
		// 4 if loops to check if there is an element at any corner of the ball
		// if does collide then reverse direction & remove brick
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			reactToCollidingObject(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
			reactToCollidingObject(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		} else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
			reactToCollidingObject(ball.getX() + BALL_RADIUS * 2, ball.getY());
		} else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
			reactToCollidingObject(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}
	}

	/*
	 * If the ball collides with a brick it changes it's direction and removes the brick.
	 * If it collides with the paddle it just changes the ball's direction.
	 */
	private void reactToCollidingObject(double xLocation, double yLocation) {
		GObject objectInWay = getElementAt(xLocation, yLocation);
		if (objectInWay == paddle) { // check if object is paddle, if yes then change vy = -vy
			vy = Math.abs(vy) * (-1);
		} else { // else do vy = -vy and remove objectInWay
			vy = -vy;
			remove(objectInWay);
			numBricksLeft--;
		}
	}
}
