
/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
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

	// instance variables for velocity of the ball
	private double vx;
	private double vy;

	// instance variable for random-number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// instance variable for paddle
	private GRect paddle;

	// instance variable for ball
	private GOval ball;

	//instance variable for counter
	private int counter = NBRICK_ROWS * NBRICK_COLUMNS; 
	
	//instance variable for turns
	private int turns = 3;
	

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// This part of the run method adds mouse listeners, and creates a system that
		// will set up the game and allow
		// the user to play the game.
		addMouseListeners();
		gameSetup();
		playGame();
	}

	/*
	 * This method is the game setup. The method has three components, creating the
	 * bricks, creating the paddle, and creating the ball, which lays out the game.
	 */

	private void gameSetup() {
		makeBricks();
		makePaddle();
		makeBall();

	}

	/*
	 * This method creates and centers the ball.
	 */
	private void makeBall() {
		double size = BALL_RADIUS * 2;
		ball = new GOval(size, size);
		ball.setFilled(true);
		add(ball, CANVAS_WIDTH / 2 - size / 2, CANVAS_HEIGHT / 2 - size / 2);
	}

	/*
	 * This method creates the paddle.
	 */
	private void makePaddle() {
		double x = getWidth() / 2 - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * This method creates a MouseEvent which ensures that as the user moves the
	 * mouse, the paddle will follow.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX >= 0 && mouseX <= (getWidth() - PADDLE_WIDTH)) {
			paddle.setLocation(mouseX, paddle.getY());
		}

	}

	/*
	 * This method creates all of the bricks. In this scenario, there are an equal
	 * number of bricks and rows. This method centers all the bricks such that there
	 * is an equal amount of space on each side of the console. Every two rows, the
	 * color of the bricks changes, ultimately creating two red rows, two orange
	 * rows, two yellow rows, two green rows, and two cyan rows.
	 */
	private void makeBricks() {
		double xOffset = getWidth() / 2 - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP) / 2;
		for (int row = 0; row < NBRICK_ROWS; row++) {
			double yOffset = BRICK_Y_OFFSET + (row * (BRICK_HEIGHT + BRICK_SEP));

			Color color;

			if (row % 10 == 0 || row % 10 == 1) {
				color = Color.RED;
			} else if (row % 10 == 2 || row % 10 == 3) {
				color = Color.ORANGE;
			} else if (row % 10 == 4 || row % 10 == 5) {
				color = Color.YELLOW;
			} else if (row % 10 == 6 || row % 10 == 7) {
				color = Color.GREEN;
			} else {
				color = Color.CYAN;
			}
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double x = xOffset + col * (BRICK_WIDTH + BRICK_SEP);
				add(makeBrick(x, yOffset, color));
			}
		}

	}

	/*
	 * This method creates a single rectangle, which in this case, is a single
	 * brick. The parameters are set to have both location and color. This brick
	 * method is then used in the above method to make all of the bricks that exist
	 * in the console.
	 */
	private GRect makeBrick(double x, double y, Color color) {
		GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setFilled(true);
		rect.setColor(color);
		return rect;
	}

	/*
	 * This method allows the user to play the game. It has two parts: One for
	 * moving the ball, and another that allows the collisions to occur.
	 */
	private void playGame() {
		moveBall();
		getCollidingObject();

	}

	/*
	 * This method checks for collisions with the ball. If the ball hits the paddle,
	 * it will bounce back the other direction. If a ball collides with a brick, the
	 * brick will be removed and the ball will bounce back.
	 */
	private void checkCollisions() {
		GObject collisions = getCollidingObject();
		if (collisions == paddle) {
			// this ensures that the ball does not get stuck behind the paddle when they
			// collide.
			vy = -Math.abs(vy);
		} else if (!(collisions == paddle) && collisions != null) {
			remove(collisions);
			vy = -vy;
			counter --;
			
		}
	}

	

	/*
	 * This method checks whether or not the ball is colliding with another object, such as the paddle or a brick. 
	 * It uses the coordinates as if there is a box around the ball, and determines whether or not those 
	 * coordinates overlap with part of another object; if not, it is null, meaning another object is not there. 
	 */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			GObject object = getElementAt(ball.getX(), ball.getY());
			return object;
		} else if ((getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null)) {
			GObject object = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
			return object;
		} else if ((getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null)) {
			GObject object = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
			return object;
		} else if ((getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null)) {
			GObject object = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
			return object;
		} else {
			return null;
		}
	}

	/*
	 * This method makes the ball centered and allows the user clicks it to initiate the movement. It sets the 
	 * velocities so that the ball moves in all directions, not just one way.
	 */
	
	private void centerBall() {
		ball.setLocation(CANVAS_WIDTH / 2 - BALL_RADIUS, CANVAS_HEIGHT / 2 - BALL_RADIUS);
		waitForClick();
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		vy = 3.0;
	}
	
	/*
	 * This method moves the ball and ensures that it can bounce of the left, right, and top wall, but that the user
	 * loses a turn when the ball goes to the bottom wall.
	 */
	private void moveBall() {
		centerBall();
		while (counter > 0 && turns > 0) {
			checkCollisions();

			// update velocity
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = -vy;
			} else if (hitBottomWall(ball)) {
				turns --;
				centerBall();
			}
			
			

			// update visualization
			ball.move(vx, vy);

			// pause
			pause(DELAY);
		}

	}

	/*
	 * This method determines whether or not the given ball should bounce off of the
	 * bottom wall of the window.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/*
	 * This method determines whether or not the given ball should bounce off of the
	 * top wall of the window.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/*
	 * This method determines whether or not the given ball should bounce off of the
	 * right wall of the window.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*
	 * This method determines whether or not the given ball should bounce off of the
	 * left wall of the window.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

}
