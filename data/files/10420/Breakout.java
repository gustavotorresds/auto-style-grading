
/*
 * Name: Jeffrey Propp
 * Section Leader: Peter Maldonado
 * File: Breakout is a game wherein, by controlling a paddle, you attempt to hit all the bricks
 * at the top by directing a bouncing ball. You get three lives. Each run will setup and start the game, waiting
 * for your click to start. It will show if you won or lost.
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

	// Initialize the paddle
	public GRect paddle = null;
	//Initialize velocity components
	private double vx;
	private double vy;
	// Initialize the ball
	private GOval ball = null;
	// Initialize a sentinel that counts how many bricks have been hit to determine the end of the game.
	int sentinel;
	// Initialize the amount of lives used
	private int lives = 3;
	// Initialize message for screen
	private GLabel message = null;
	// Loads audio
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		while (lives > 0) {
			sentinel = NBRICK_ROWS * NBRICK_COLUMNS;
			setupGame();
			waitForClick();
			startGame();
			clear();
		}
		endSequence();
	}

	/*
	 * Calls methods to build all the blocks, create a paddle, move the paddle with the mouse,
	 * make the ball, and display a message across the screen.
	 */
	private void setupGame() {
		buildBlocks();
		createPaddle();
		addMouseListeners();
		makeBall();
		displayMessage();
	}

	/*
	 * Builds n1 number of columns and n2 number of rows. Centers them in the x coordinate system and
	 * offsets them from the top in the y system. Includes a separation between each brick. Employs a
	 * counter to run for each row, calling another method to set the color. Will reset color cycle
	 * every ten rows.
	 */
	private void buildBlocks() {
		int count = 0;
		for (int row = 0; row < NBRICK_COLUMNS; row++) {
			double startX = getCenterX() - ((NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) - BRICK_SEP) * .5);
			double startY = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row;
			for (int col = 0; col < NBRICK_ROWS; col++) {
				GRect rect = new GRect(startX, startY, BRICK_WIDTH, BRICK_HEIGHT);
				add(rect);
				rect.setFilled(true);
				rect.setColor(whichColor(count));
				startX += BRICK_WIDTH + BRICK_SEP;
			}
			count++;
			if (count >= 10) {
				count = 0;
			}
		}
	}

	/*
	 * Cycles through the colors, changing every two
	 */
	private Color whichColor(int count) {
		if (count < 2) {
			return Color.RED;
		} else if (count < 4) {
			return Color.ORANGE;
		} else if (count < 6) {
			return Color.YELLOW;
		} else if (count < 8) {
			return Color.GREEN;
		} else if (count < 10) {
			return Color.CYAN;
		} else {
			return null;
		}
	}

	/*
	 * Makes a paddle (rectangle) with specified height and width at the specified y
	 * offset, centered in the screen.
	 */
	private void createPaddle() {
		paddle = new GRect(getCenterX() - PADDLE_WIDTH * .5, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);

	}

	/*
	 * Makes the paddle move so its center is located at the x location of the
	 * mouse. Only works while the paddle is within the boundaries of the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX() - (PADDLE_WIDTH * .5);
		if (mouseX >= 0 && mouseX <= getWidth() - (PADDLE_WIDTH)) {
			if (paddle != null)
				paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
	
	/*
	 * Creates a ball with set radius. Puts it in the center of the screen.
	 */
	private void makeBall() {
		ball = new GOval(getCenterX() - BALL_RADIUS, getCenterY() - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLUE);
		add(ball);
	}
	
	/* 
	 * Creates a message to be displayed in between turns with directions and the number of 
	 * lives left.
	 */
	private void displayMessage() {
		message = new GLabel("Click to Start! You have " + lives + " lives left");
		message.setLocation(getCenterX() - message.getWidth() / 2, getCenterY() + message.getAscent() / 2 + 40);
		add(message);
	}

	// Creates a random generator in order to set the x velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*
	 * Takes the message off the screen. Sets the velocity (x component is random). Moves the ball with set
	 * velocity unless it hits the bottom of the screen, in which case it stops. Ricochets of walls, changing
	 * one component of the velocity to negative. Checks for collisions with other objects. If all bricks
	 * are hit (sentinel == 0), it ends the game. If the ball hits the bottom, you lose a life.
	 */
	private void startGame() {
		remove(message);
		vy = -2.0;
		vx = rgen.nextDouble(0.5, 1.0);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		while (ball.getY() <= (getHeight() - BALL_RADIUS * 2)) {
			ball.move(vx, vy);
			pause(4);
			if (ball.getY() <= 0) {
				vy = -vy;
			}
			if (ball.getX() <= 0 || ball.getX() >= (getWidth() - BALL_RADIUS * 2)) {
				vx = -vx;
			}
			checkForCollisions();
			if (sentinel == 0) {
				lives = 1;
				break;
			}
		}
		lives--;
	}
	
	/*
	 * Checks each of the four corners surrounding the ball to see if they hit an object. If they do,
	 * it calls another method.
	 * Also adds audio if the ball collides with something.
	 */
	private void checkForCollisions() {
		double left = ball.getX();
		double right = ball.getX() + BALL_RADIUS * 2;
		double up = ball.getY();
		double down = ball.getY() + BALL_RADIUS * 2;

		GObject collider = null;

		if (getElementAt(left, up) != null) {
			collider = getElementAt(left, up);
		} else if (getElementAt(right, up) != null) {
			collider = getElementAt(right, up);
		} else if (getElementAt(right, down) != null) {
			collider = getElementAt(right, down);
		} else if (getElementAt(left, down) != null) {
			collider = getElementAt(left, down);
		}
		if (collider != null) {
			bounceClip.play();
			getCollidingObject(collider);
		}
	}

	/*
	 * If the ball hits the paddle, the ball changes y direction. If it hits a brick, it removes the brick and
	 * subtracts one from the sentinel, counting the nuber of bricks left to know when the game is won.
	 */
	private void getCollidingObject(GObject collider) {
		if (collider == paddle) {
			vy = -vy;
		} else {
			vy = -vy;
			remove(collider);
			sentinel--;
		}

	}

	/*
	 * Displays if you won or lost based on if you hit all the bricks or lost all three lives.
	 */
	private void endSequence() {
		GLabel label = new GLabel("");
		if (sentinel == 0) {
			label.setText("You won!");
		} else {
			label.setText("You Lost! :(");
		}
		label.setLocation(getCenterX() - label.getWidth() / 2, getCenterY() + label.getAscent() / 2);
		add(label);
	}
}
