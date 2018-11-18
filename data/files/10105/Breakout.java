
/*
 * File: Breakout.java
 * -------------------
 * Name: David Miró Llopis
 * Section Leader: Meng Zhang
 * 
 * This file implements the game of Breakout. 
 * Given the parameters introduced at the beginning,
 * the game will set a certain number of bricks and
 * the ball with start moving from the center downstairs the paddle
 * The game will end either when the ball touches the bottom of the screen
 * (in which case the game is lost)
 * or when all the bricks have been touched by the ball
 * (in which case the game is won)
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
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

	// Define instance variables
	// Define paddle
	private GRect paddle;
	// Define ball
	private GOval ball;
	// Define velocities
	private double vx, vy;
	// Define random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Run method
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// set up initial configuration of the game
		setInitialConfiguration();

		// play game
		playGame();
	}

	private void setInitialConfiguration() {
		// Create the rows and columns of bricks
		setBricks();

		// Create the paddle
		setPaddle();
	}

	private void setBricks() {
		// Calculate the position of the first line
		double y = BRICK_Y_OFFSET;

		// for loop to create the desired number of rows
		for (int i = 0; i < NBRICK_ROWS; i++) {
			// Call method that creates a line at a certain position with a color
			// The color is obtained through another method
			createLine(y, getcolor(i));
			// Calculate position of the next line
			y += BRICK_HEIGHT + BRICK_SEP;
		}

	}

	// Method to create a line bricks at a certain given y position and with a given
	// color
	private void createLine(double y, Color c) {
		double x = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH - (NBRICK_COLUMNS - 1) * BRICK_SEP) / 2;
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			createBrick(x, y, c);
			x += BRICK_WIDTH + BRICK_SEP;
		}

	}

	// Create a brick at a certain x,y position with a given color
	private void createBrick(double x, double y, Color c) {
		// Method that creates a brick given position and color
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(c);
		add(brick);
	}

	// Method that returns the color given the row number
	// The color sequence is (in the following order) : RED, ORANGE, YELLOW, GREEN,
	// CYAN
	// The same color is applied to 2 lines after moving to next color
	// Once CYAN is reached, RED color is applied again, and then ORANGE, and so
	// on...
	private Color getcolor(int i) {
		int j = i % 10;
		if (j <= 1) {
			return Color.RED;
		} else if (j <= 3) {
			return Color.ORANGE;
		} else if (j <= 5) {
			return Color.YELLOW;
		} else if (j <= 7) {
			return Color.GREEN;
		} else if (j <= 9) {
			return Color.CYAN;
		}
		return Color.GRAY;
	}

	// Method that creates the paddle, fixing a y position
	// and makes it move horizontally following the mouse moves
	private void setPaddle() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT);

		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);

		addMouseListeners();
	}

	// Method to get the position of the moving mouse
	// and set the x position of the paddle according to that
	public void mouseMoved(MouseEvent e) {
		// Get x position to the mouse
		double x = e.getX();
		// Set up the max x to make sure paddle is always entirely visible
		if (x >= getWidth() - PADDLE_WIDTH) {
			x = getWidth() - PADDLE_WIDTH;
		}
		paddle.setX(x);
	}

	private void playGame() {

		createBall();

		// initialize velocities
		setInitialVelocities();

		// initialize counter of number of bricks
		int numBricks = NBRICK_COLUMNS * NBRICK_ROWS;

		// check if walls hit
		numBricks = checkCollisionsAndReact(numBricks);

		// print final message
		printFinalMessage(numBricks);

	}

	private boolean hitBottom(GOval b) {
		return b.getY() >= getHeight() - b.getHeight();
	}

	private boolean hitTop(GOval b) {
		return b.getY() <= 0;
	}

	private boolean hitRight(GOval b) {
		return b.getX() >= getWidth() - b.getWidth();
	}

	private boolean hitLeft(GOval b) {
		return b.getX() <= 0;
	}

	private void createBall() {
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	// Set initial velocities according to define parameters
	private void setInitialVelocities() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		vy = VELOCITY_Y;
	}

	// check if the ball hits with any object and react accordingly
	// if it's the bottom wall -> Game Over
	// if it's the top wall, the paddle or a brick -> Invert direction of y velocity
	// if it's the Right or Left wall -> Invert direction of x velocity
	private int checkCollisionsAndReact(int numBricks) {
		while (hitBottom(ball) != true & numBricks != 0) {

			// Update vx, vy
			if (hitTop(ball)) {
				vy = -vy;
			}

			if (hitRight(ball) || hitLeft(ball)) {
				vx = -vx;
			}

			// make ball move
			ball.move(vx, vy);

			// pause
			pause(DELAY);

			// check if collision with an object:
			// either remove if it is a brick
			// either bounce the ball if it is the paddle
			GObject collider = getCollidingObject(ball.getX(), ball.getY());
			if (collider == paddle) {
				vy = -VELOCITY_Y;
			} else if (collider != null) {
				remove(collider);
				numBricks += -1;
				vy = -vy;
			}
		}
		return numBricks;
	}

	// Method that return the first object found
	// in any one the 4 corners of the ball,
	// starting in top left and going clokwise
	// or returns null if no objects are present
	private GObject getCollidingObject(double x, double y) {
		if (getElementAt(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS) != null) {
			return getElementAt(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS);
		} else if (getElementAt(x, y + 2 * BALL_RADIUS) != null) {
			return getElementAt(x, y + 2 * BALL_RADIUS);
		} else if (getElementAt(x, y) != null) {
			return getElementAt(x, y);
		} else if (getElementAt(x + 2 * BALL_RADIUS, y) != null) {
			return getElementAt(x, y);
		}
		return getElementAt(x, y);
	}

	// Method that prints a box with either "You won" if all bricks are gone
	// or either "You lost" if the ball touched the bottom wall
	private void printFinalMessage(int numBricks) {
		if (numBricks == 0) {
			printBox("You won");
		} else {
			printBox("You lost");
		}

	}

	// method to create a central box in the screen with a message s
	private void printBox(String s) {
		// Create the box
		double boxWidht = getWidth();
		;
		double boxHeight = PADDLE_WIDTH;
		GRect box = new GRect(0, getHeight() / 2 - boxHeight / 2, boxWidht, boxHeight);
		box.setFilled(true);
		box.setColor(Color.lightGray);
		add(box);

		// Create the message
		GLabel message = new GLabel(s);
		message.setFont("Courier-30");
		add(message, (getWidth() - message.getWidth()) / 2, (getHeight() + message.getAscent()) / 2);
	}
}
