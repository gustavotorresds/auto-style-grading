
/*
 * File: Breakout.java
 * -------------------
 * Name: Jonathan Jean-Pierre
 * Section Leader: Ella Tessier-Lavigne
 * 
 * The Breakout class extends the GraphicsProgram class to play the classic arcade game of Breakout. It
 * gives the player three chances to eliminate all of the bricks by controlling a paddle at the bottom of the
 * screen with their mouse. If the player can eliminate all bricks before the three turns are up, they win;
 * otherwise, the player loses.
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
	public static final double BRICK_WIDTH = Math.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	// The ball's minimum and maximum horizontal velocity; the bounds of the initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	// Instance variable for the paddle to be tracked
	GRect paddle = null;

	// Instance variable serving as a random-number generator for the ball's X velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Assesses the total number of bricks at the start of the game
		int totalBricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;

		setUp();
		for (int i = 0; i < NTURNS; i++) {
			totalBricksRemaining = play(totalBricksRemaining);
		}
		endGame(totalBricksRemaining);
	}
	
	/**
	 * Method: Set Up 
	 * ----------------------- 
	 * This method sets up the initial conditions for the start of the game. 
	 * The bricks and paddle are created and added to the screen.
	 */
	
	private void setUp() {
		double cx = getWidth() / 2;

		// creates the bricks for the game
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				Color c = setBrickColor(i);
				drawBrick(cx - (NBRICK_COLUMNS / 2.0 * BRICK_WIDTH) - ((NBRICK_COLUMNS - 1.0) / 2 * BRICK_SEP) + j * (BRICK_WIDTH + BRICK_SEP), BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT, c);
			}
		}
		
		// creates paddle, adds it to center of the screen and enables user to control it with the mouse
		paddle = makePaddle();
		addPaddleToCenter();
		addMouseListeners();
	}

	/**
	 * Method: Play 
	 * ----------------------- 
	 * This method launches and plays the game. It counts how many bricks are remaining and ends the game 
	 * if there are no bricks left or if the ball reaches the bottom wall of the screen.
	 */
	
	private int play(int bricksRemaining) {

		// Creates the ball to be used in the game
		GOval ball = makeBall();

		// Starts the ball at a random x velocity and a constant y velocity.
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		double vy = VELOCITY_Y;

		while (bricksRemaining > 0) {
			GObject collider = getCollidingObject(ball);

			// Updates velocity if the ball collides with a wall
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = -vy;
			}
			
			/*
			 * If ball collides with an object that is not the paddle, the object is removed
			 * and y direction is reversed.
			 */
			if (collider != null && collider != paddle) {
				remove(collider);
				bricksRemaining -= 1;
				vy = -vy;
			}

			// If ball collides with the paddle, its y direction is reversed
			if (collider == paddle) {
				vy = -Math.abs(vy);
			}

			// updates visualization
			ball.move(vx, vy);

			// pause
			pause(DELAY);

			// If the ball reaches the bottom wall, the game ends
			if (ball.getY() > getHeight()) {
				break;
			}
		}
		remove(ball);
		return bricksRemaining;
	}

	/**
	 * Method: End Game 
	 * ------------------ 
	 * When the game ends, this method uses the number of bricks remaining to determine if a player 
	 * has won or lost. It then calls drawLabel to add the appropriate message to the screen.
	 */

	private void endGame(int bricksRemaining) {
		if (bricksRemaining > 0) {
			drawLabel("Game Over: You lose");
		} else {
			drawLabel("Congratulations! You win");
		}
	}

	/**
	 * Method: Draw Label 
	 * ------------------ 
	 * Creates and adds a label to the screen.
	 */

	private void drawLabel(String str) {
		GLabel label = new GLabel(str);
		label.setFont("Courier-24");
		label.setLocation((getWidth() - label.getWidth()) / 2, (getHeight() + label.getAscent()) / 2);
		add(label);
	}

	/**
	 * Method: Make Ball 
	 * ------------------ 
	 * Creates a ball, adds it to the screen and returns it so the ball can be used for animation.
	 */

	private GOval makeBall() {
		double size = BALL_RADIUS * 2;
		double cx = getWidth() / 2;
		double cy = getHeight() / 2;
		GOval r = new GOval(size, size);
		r.setFilled(true);
		add(r, cx - BALL_RADIUS / 2, cy - BALL_RADIUS / 2);
		return r;
	}

	/**
	 * Method: Get Colliding Object 
	 * ------------------ 
	 * Checks if any of the four corners of the ball has collided with a brick. If so, the object is returned.
	 */

	private GObject getCollidingObject(GOval ball) {
		GObject brick = null;
		double bx = ball.getX();
		double by = ball.getY();
		double size = 2 * BALL_RADIUS;

		if (getElementAt(bx, by) != null) {
			brick = getElementAt(bx, by);
			return brick;
		} else if (getElementAt(bx + size, by) != null) {
			brick = getElementAt(bx + size, by);
			return brick;
		} else if (getElementAt(bx, by + size) != null) {
			brick = getElementAt(bx, by + size);
			return brick;
		} else if (getElementAt(bx + size, by + size) != null) {
			brick = getElementAt(bx + size, by + size);
			return brick;
		} else {
			return null;
		}
	}

	/**
	 * Method: Mouse Moved 
	 * ------------------ 
	 * Moves the location of the paddle based on the location of the mouse.
	 */

	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		double y = (getHeight() - PADDLE_Y_OFFSET);
		double canvasWidth = getWidth();

		// prevents the paddle from moving beyond the edge of the window
		if (e.getX() < PADDLE_WIDTH / 2) {
			paddle.setLocation(0, y);
		} else if (e.getX() > canvasWidth - PADDLE_WIDTH / 2) {
			paddle.setLocation(canvasWidth - PADDLE_WIDTH, y);
		} else {
			paddle.setLocation(x, y);
		}
	}

	/**
	 * Method: Draw Brick 
	 * ------------------ 
	 * Draws bricks and adds them to the screen
	 */

	private void drawBrick(double x, double y, double width, double height, Color c) {
		GRect rect = new GRect(x, y, width, height);
		rect.setColor(c);
		rect.setFilled(true);
		add(rect);
	}

	/**
	 * Method: Set Brick Color 
	 * ------------------ 
	 * Controls the color of the bricks depending on which row is being created. Decimals are used rather than
	 * absolute numbers in the event that the number of brick rows is changed in the constants section.
	 */

	private Color setBrickColor(double i) {
		double brickFraction = (i + 1) / NBRICK_ROWS;
		Color brickColor = null;

		if (brickFraction <= 0.2) {
			brickColor = Color.RED;
		} else if (brickFraction > 0.2 && brickFraction <= 0.4) {
			brickColor = Color.ORANGE;
		} else if (brickFraction > 0.4 && brickFraction <= 0.6) {
			brickColor = Color.YELLOW;
		} else if (brickFraction > 0.6 && brickFraction <= 0.8) {
			brickColor = Color.GREEN;
		} else {
			brickColor = Color.CYAN;
		}
		return brickColor;
	}

	/**
	 * Method: Make Paddle 
	 * ------------------ 
	 * Creates the paddle and returns it so it can be used for animation.
	 */

	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	/**
	 * Method: Add Paddle to Center 
	 * ------------------ 
	 * Adds the paddle to the center of the screen
	 */

	private void addPaddleToCenter() {
		double x = (getWidth() / 2 - PADDLE_WIDTH / 2);
		double y = (getHeight() - PADDLE_Y_OFFSET);
		add(paddle, x, y);
	}

	/**
	 * Method: Hit Left Wall 
	 * ----------------------- 
	 * Returns whether or not the given ball should bounce off of the left wall of the window.
	 */

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	/**
	 * Method: Hit Right Wall 
	 * ----------------------- 
	 * Returns whether or not the given ball should bounce off of the right wall of the window.
	 */

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - BALL_RADIUS * 2;
	}

	/**
	 * Method: Hit Top Wall 
	 * ----------------------- 
	 * Returns whether or not the given ball should bounce off of the top wall of the window.
	 */

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
}

