/*
 * File: Breakout.java
 * -------------------
 * Name: Thea
 * Section Leader: Rhea
 * 
 * The GraphicsProgram Breakout is a classic arcade game of Breakout. 
 * A complete game consists of three turns. On each turn, a ball is launched
 * from the center of the window toward the bottom of the screen at a random
 * angle. The ball bounces off the paddle, the walls of the world, and the 
 * bricks. When a ball bounces off a brick, the brick disappears. The player
 * moves the paddle to put it in line with the returning ball. If the player 
 * misses the ball with the paddle and the player has no turns left, the game 
 * ends. If the last brick is eliminated, the player wins and the game ends. 
 * 
 * Sources:
 * Lecture slides and the Java textbook were used to understand and implement
 * variables, methods, parameters, statements, events, and control flow, as well
 * as correct syntax.
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
	public static final int NBRICK_COLUMNS = 1;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 1;

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

	// Constant y coordinate of the paddle
	public static final double Y_COORDINATE_PADDLE = CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

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

	/* Added instance variables */

	// X coordinate of the first column
	private static final double STARTING_X_LOCATION = 
			(CANVAS_WIDTH - (NBRICK_COLUMNS - 1.0) * BRICK_SEP - BRICK_WIDTH * NBRICK_COLUMNS) / 2;

	// Instance variable for the paddle
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	// Instance variable for the ball
	private GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);

	// Velocity components represent the change in position that occurs on each time step
	private double vx;
	private double vy;

	// To make the ball choose the vx component randomly
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// A counter for the number of bricks left
	private int counter =  NBRICK_COLUMNS * NBRICK_ROWS;

	/* Runs the program */
	public void run() {
		setup();
		addMouseListeners();
		playGame();
	}

	/*
	 * Method: Setup
	 * ------------------------
	 * Sets the window title bar text, the canvas size, the bricks, and the paddle.
	 */
	private void setup() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBricks();
		setPaddle();
	}

	/*
	 * Method: Set Bricks
	 * ------------------------
	 * Set Bricks uses a nested for loop to create ten rows of bricks of ten bricks each.
	 * The bricks are centered in the window, leftover space divided equally on the left and
	 * right sides. A switch statement is used to make the color of the bricks remain constant for 
	 * two rows and run in the following rainbow-like sequence: RED, ORANGE, YELLOW, GREEN, CYAN.
	 */
	private void setBricks() {
		for(int i = 0; i < NBRICK_COLUMNS; i++) {
			for(int j = 0; j < NBRICK_ROWS; j++) {
				double x = STARTING_X_LOCATION + i * BRICK_WIDTH + i * BRICK_SEP;
				double y = BRICK_Y_OFFSET + j * BRICK_HEIGHT + j * BRICK_SEP;
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				switch (j) {
				case 0: case 1:
					brick.setColor(Color.RED);
					break;
				case 2: case 3:
					brick.setColor(Color.ORANGE);
					break;
				case 4: case 5:
					brick.setColor(Color.YELLOW);
					break;
				case 6: case 7:
					brick.setColor(Color.GREEN);
					break;
				case 8: case 9:
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}

	/*
	 * Method: Set Paddle
	 * ------------------------
	 * Assigns the starting location of the paddle. The y coordinate of the paddle is fixed.
	 */
	private void setPaddle() {
		double startingX = getWidth() / 2 - PADDLE_WIDTH / 2;
		paddle.setFilled(true);
		add(paddle, startingX, Y_COORDINATE_PADDLE);
	}

	/*
	 * Method: Mouse Moved
	 * ------------------------
	 * The mouseMoved method is called any time the mouse moves in the program screen. The x
	 * coordinate of the mouse at its current location is passed into the setLocation method 
	 * as a parameter so the paddle tracks the mouse. The paddle never extends beyond the two
	 * boundaries; the entire paddle is visible in the window.
	 */
	public void mouseMoved(MouseEvent e) {
		if (e.getX() >= getWidth() - PADDLE_WIDTH) {
			double x = getWidth() - PADDLE_WIDTH;
			paddle.setLocation(x, Y_COORDINATE_PADDLE);
		} else {
			double x = e.getX();
			paddle.setLocation(x, Y_COORDINATE_PADDLE);
		}
	}

	/* Method: Play Game
	 * ------------------------
	 * Play Game allows the user to play three times with a for loop that launches a ball
	 * three times. During each turn the method checks if the ball hits a wall or if it
	 * collides with the paddle or a brick, in all of which cases the ball bounces off. 
	 * If the ball goes beyond the lower boundary of the screen, the next ball is launched.
	 * If there has been three turns or if the counter is equal to 0, the game ends and an
	 * end of game statement is displayed.  
	 */
	public void playGame() {
		for(int i = 0; i < 3; i++) {
			ball.setFilled(true);
			double x = getWidth() / 2 - BALL_RADIUS / 2;
			double y = getHeight() / 2 - BALL_RADIUS / 2;
			add(ball, x, y);
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			vy = VELOCITY_Y;
			while (true) {
				ball.move(vx, vy);
				double bx = ball.getX();
				double by = ball.getY();
				// ball bounces if it hits a wall
				if(bx < 0 || bx > getWidth() - 2 * BALL_RADIUS) {
					vx = -vx;
				}
				if(by < 0) {
					vy = -vy;
				}
				// removes the ball if it misses the paddle and ends the turn
				if(by > getHeight()) {
					remove(ball);
					break;
				}
				// returns the object a ball collides with at its current position, if any, and null otherwise
				GObject collider = getCollidingObject(bx, by);
				// if the collider is the paddle, the ball bounces back so that it starts traveling up
				if(collider == paddle && vy > 0) {
					vy = -vy;
				// if the collider is not the paddle, is not the countLabel and does not equal to null, it must be a brick
				} else if(collider != paddle && collider != null) {
					// removes the brick
					remove(collider);
					vy = -vy;
					updateCounter();
				}
				pause(DELAY);
			}
			if(counter == 0) {
				break;
			}
		}
		endOfGameStatement();
	}

	/* Method: Get Colliding Object
	 * ------------------------
	 * This method checks to see if the ball has collided with with an object at the upper left,
	 * upper right, lower left, and lower right corner. The method takes in two doubles that 
	 * represent the current location of the ball, bx and by. The location of the corners are
	 * found by adding two times the ball radius to either the x or y coordinate. If an object is
	 * present at a corner, getCollidingObject returns the object, otherwise it returns null.
	 */
	private GObject getCollidingObject(double bx, double by) {
		if(getElementAt(bx, by) != null) {
			return getElementAt(bx, by);
		} else if(getElementAt(bx + 2 * BALL_RADIUS, by) != null) {
			return getElementAt(bx + 2 * BALL_RADIUS, by);
		} else if(getElementAt(bx, by + 2 * BALL_RADIUS) != null) {
			return getElementAt(bx, by + 2 * BALL_RADIUS);
		} else if(getElementAt(bx + 2 * BALL_RADIUS, by + 2 * BALL_RADIUS) != null) {
			return getElementAt(bx + 2 * BALL_RADIUS, by + 2 * BALL_RADIUS);
		} else {
			return null;
		}
	}

	/* Method: Update Counter
	 * ------------------------
	 * Update Counter is called every time a brick collides with a brick. It subtracts 1 from the 
	 * counter for every brick. 
	 */
	private void updateCounter() {
		counter--;
		if(counter == 0) {
			remove(ball);
		}
	}

	/* Method: End Of Game Statement
	 * ------------------------
	 * This method displays a label at the end of the game and is called when the for loop is
	 * exited. If the counter is zero, the user has won the game and a hooray statement is
	 * displayed on the screen. If the counter is not equal to zero, it means that the player
	 * has some bricks left and a game over statement is displayed. 
	 */
	private void endOfGameStatement() {
		if(counter != 0) {
			GLabel gameOver = new GLabel("Game Over :-(", getWidth() / 2, getHeight() / 2);
			gameOver.setFont("Serif-24");
			add(gameOver, getWidth() / 2 - gameOver.getWidth() / 2, getHeight() / 2);
		} else {
			GLabel youWin = new GLabel("Hooray!!! You Won :-)", getWidth() / 2, getHeight() / 2);
			youWin.setFont("Serif-24");
			add(youWin, getWidth() / 2 - youWin.getWidth() / 2, getHeight() / 2);
		}
	}
}