/*
 * File: Breakout.java
 * -------------------
 * Name: Timothy Sah
 * Section Leader: Jordan Rosen-Kaplan
 * 
 * This file creates the game "Breakout," where the user will use a paddle (tracked by the mouse) to rebound a ball, to make
 * the ball hit a series of blocks above. Each time the ball hits a block, that block is removed. The user has a set number of 
 * lives. A life is lost when the ball drops below the paddle and falls below the bottom boundary of the window. The user loses
 * the game when he/she runs out of lives. The user wins the game if he/she clears all of the bricks in the window before losing
 * all of his/her lives.
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
	public static final double BALL_DIAMETER = 2 * BALL_RADIUS;

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
	
	// Total number of bricks initially
	public static final int TOTALBRICKS = NBRICK_COLUMNS * NBRICK_ROWS;

	// Private instance variables
	private GRect brick;
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int lives;
	private int bricksRemaining;

	public void run() {
		setUpGame();
		playGame();
		endGame();
	}

	// Creates the blueprints of the game (including the bricks and paddle, with paddle mouse-tracking)
	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createBricks();
		createPaddle();
	}
	
	// Creates the main gameplay functionality of the game
	private void playGame() {
		bricksRemaining = TOTALBRICKS;
		lives = NTURNS;
		
		// As long as there are bricks remaining
		while (bricksRemaining > 0) {
			// If user has remaining lives
			if (lives > 0) {
				createBall();
				waitForClick();
				setBallMotion();
				lives--;
			} else {
			// If no more lives remaining
				break;
			}
		}
	}
	
	// Ends the game by clearing the window and displaying a "win" message or "lose" message.
	private void endGame() {
		removeAll();
		if (lostGame()) {
			displayLostMessage();
		}
		if (wonGame()) {
			displayWinMessage();
		}
	}

	// Creates Bricks
	private void createBricks() {
		for (int rows = 0; rows < NBRICK_ROWS; rows++) {		
			for (int columns  = 0; columns < NBRICK_COLUMNS; columns++) {
				double xBrick = ( getWidth() - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) + BRICK_SEP ) / 2 + columns * (BRICK_WIDTH + BRICK_SEP);
				double yBrick = BRICK_Y_OFFSET + (rows * (BRICK_HEIGHT + BRICK_SEP));

				brick = new GRect(xBrick, yBrick, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);

				if ((rows) % 10 == 0 || (rows) % 10 == 1) {
					brick.setColor(Color.RED);
				} else if ((rows) % 10 == 2 || (rows) % 10 == 3) {
					brick.setColor(Color.ORANGE);
				} else if ((rows) % 10 == 4 || (rows) % 10 == 5) {
					brick.setColor(Color.YELLOW);
				} else if ((rows) % 10 == 6 || (rows) % 10 == 7) {
					brick.setColor(Color.GREEN);
				} else if ((rows) % 10 == 8 || (rows) % 10 ==9) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}

	// Creates the Paddle
	private void createPaddle() {
		double xPaddle = ( getWidth() + PADDLE_WIDTH ) / 2;
		double yPaddle = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

		paddle = new GRect(xPaddle, yPaddle, PADDLE_WIDTH, PADDLE_HEIGHT);
		add(paddle);
		paddle.setFilled(true);

		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		// Makes it so that the mouse tracks the middle of the paddle.
		double mousePaddleX = e.getX() - PADDLE_WIDTH / 2;
		double mousePaddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

		double leftPaddleBoundary = PADDLE_WIDTH / 2;
		double rightPaddleBoundary = getWidth() - PADDLE_WIDTH / 2;

		if ( ( e.getX() >= leftPaddleBoundary ) && ( e.getX() <= rightPaddleBoundary) ) {
			paddle.setLocation(mousePaddleX, mousePaddleY);
		} else if (e.getX() > 0 && e.getX() < leftPaddleBoundary) {
			paddle.setLocation(0, mousePaddleY);
		} else if (e.getX() > rightPaddleBoundary && e.getX() < getWidth()) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, mousePaddleY);
		}
	}

	// Creates the Ball
	private void createBall() {
		double xBall = getWidth() / 2 - BALL_RADIUS;
		double yBall = getHeight() / 2 - BALL_RADIUS;

		ball = new GOval(xBall, yBall, BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
		add(ball);
	}

	// Sets the ball's motion
	private void setBallMotion() {
		// Initial ball motion
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;

		while (ballNotTouchingBottom()) {
			ball.move(vx, vy);
			pause(DELAY);

			wallCollisions();
			objectCollisions();
			if (bricksRemaining == 0) {
				break;
			}
		}
	}

	// Tests if the ball is touching the bottom of the window
	private boolean ballNotTouchingBottom() {
		if (ball.getY() < getHeight()) {
			return true;
		} else {
			remove(ball);
			return false;
		}
	}

	// Tests for a wall collision, and reverses the x velocity if wall collision exists
	private void wallCollisions() {
		if (ball.getX() <= 0 || ball.getX() >= getWidth() - BALL_DIAMETER) {
			vx = -vx;
		}
		if (ball.getY() <= 0) {
			vy = -vy;
		}
	}

	// Tests for collisions (excluding wall collisions)
	private void objectCollisions() {
		GObject collider = getCollidingObject();
		
		paddleCollision(collider);
		brickCollision(collider);
	}

	// Returns the colliding object 
	private GObject getCollidingObject() {
		double xLeft = ball.getX();
		double xRight = ball.getX() + BALL_DIAMETER;
		double yTop = ball.getY();
		double yBottom = ball.getY() + BALL_DIAMETER;

		if (getElementAt(xLeft, yTop) != null) {
			return (getElementAt(xLeft, yTop));
		} else if (getElementAt(xRight, yTop) != null) {
			return (getElementAt(xRight, yTop));
		} else if (getElementAt(xLeft, yBottom) != null) {
			return (getElementAt(xLeft, yBottom));
		} else if (getElementAt(xRight, yBottom) != null) {
			return (getElementAt(xRight, yBottom));
		} else {
			return null;
		}
	}
	
	// Reverses the y velocity if the collider is the paddle
	private void paddleCollision(GObject collider) {
		if (collider == paddle) {
			if (vy > 0) {
				vy = -vy;
			}
		}
	}
	
	// Updates the bricksRemaining counter, reverses the y velocity, and removes the brick
	// if a collision with a brick is detected
	private void brickCollision(GObject collider) {
		if (collider != null && collider != paddle) {
			bricksRemaining--;
			vy = -vy;
			remove(collider);
		}
	}

	// Tests if the user won the game
	private boolean wonGame() {
		if (bricksRemaining == 0) {
			return true;
		} else {
			return false;
		}
	}

	// Tests if the user lost the game
	private boolean lostGame() {
		if (lives == 0) {
			return true;
		} else {
			return false;
		}
	}

	// Displays the win message
	private void displayWinMessage() {
		GLabel winMessage = new GLabel ("You Won!");
		winMessage.setFont("Courier-24");
		winMessage.setColor(Color.RED);
		add(winMessage, ( getWidth() - winMessage.getWidth() ) / 2, ( getHeight() - winMessage.getAscent() ) / 2);
	}

	// Displays the lost message
	private void displayLostMessage() {
		GLabel gameOver = new GLabel ("You Lost!");
		gameOver.setFont("Courier-24");
		gameOver.setColor(Color.RED);
		add(gameOver, ( getWidth() - gameOver.getWidth() ) / 2, ( getHeight() - gameOver.getAscent() ) / 2);
	}

}