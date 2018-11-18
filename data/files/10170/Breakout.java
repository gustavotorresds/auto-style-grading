/*
 * File: Breakout.java
 * -------------------
 * Name: Jonathan Khalfayan
 * Section Leader: Ruiqi Chen
 * 
 * This program implements the game "breakout" where the objective is to eliminate bricks by bouncing a ball off of a paddle
 * on the bottom of the screen
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

	// Creates instance variables for the velocity (random for x between certain boundaries)
	private RandomGenerator rGen = RandomGenerator.getInstance();
	private double vx = rGen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	private double vy = VELOCITY_Y;


	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 400.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Define public variables for entire row length and column height
	double rowLength = (BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS - BRICK_SEP;
	double columnHeight = (BRICK_HEIGHT + BRICK_SEP) * NBRICK_ROWS - BRICK_SEP;

	// Define public startX and startY variables
	double startX = getWidth()/2.0 - rowLength/2.0;
	double startY = (BRICK_Y_OFFSET) + BRICK_HEIGHT;

	// Initializes paddle
	private GRect paddle = null;

	// Initializes ball
	private GOval ball = null;

	// Initializes collider
	private GObject collider = null;

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Create rows with correct colors
		makeRows(Color.RED);
		makeRows(Color.ORANGE);
		makeRows(Color.YELLOW);
		makeRows(Color.GREEN);
		makeRows(Color.CYAN);

		// Adds mouse listeners and creates paddle
		addMouseListeners();
		paddle = makePaddle();

		// Creates initial starting coordinates for ball
		double ballX = getWidth()/2.0 - 2.0 * BALL_RADIUS;
		double ballY = getHeight()/2.0 - 2.0 * BALL_RADIUS;

		// Create ticker equal to the number of bricks, in this case 100
		int ticker = 100;

		// Create variable turns that equals the number of turns set by the instance variable
		int turns = NTURNS;

		// While loop that keeps game going until turns are out or all the bricks are gone
		while (turns > 0 && ticker > 0) {

			// Makes ball and sets location
			ball = makeBall();
			ball.setLocation(ballX, ballY);

			// Starts game on click of mouse
			waitForClick();

			// Beginning of animation loop
			while(true && ball!= null) {

				// update velocity
				// Switch velocity in the x direction if hits left or right wall
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				}
				// Switch velocity in the y direction if hits top or bottom wall
				if(hitTopWall(ball)) {
					vy = -vy;
				}

				// Removes ball if it hits the bottom wall and decreases the number of turns by 1
				// Breaks loop to start again on click
				if(hitBottomWall(ball)) {
					remove(ball);
					turns --;
					break;
				}

				// update visualization
				ball.move(vx, vy);

				// Create collider object
				collider = getCollidingObject();

				// Account for collider being paddle and then redirecting
				if(collider == paddle) {
					vy = -1 * Math.abs(vy);

					// Plays sound when hits paddle
					bounceClip.play();


					// Account for collider being brick, redirecting and removing brick
				} else if (collider != null) {
					vy = -vy;
					remove(collider);

					// Decreases brick counter and plays sounds
					ticker = ticker - 1;
					bounceClip.play();

					// Breaks loop if all bricks are gone
					if (ticker == 0) {
						break;
					}
				}

				// pause for visual purpose
				pause(DELAY);
			}
		}

		// Tells the user they win if all bricks are gone or game is over when all turns are gone
		if (ticker == 0) {
			makeWinLabel();
		} else {
			makeOverLabel();
		}
	}

	// Boolean to determine if ball hits the bottom wall
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	// Boolean to determine if ball hits top wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	// Boolean to determine if ball hits right wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	// Boolean to determine if ball hits left wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	// Method that creates two rows of the same color using a nested for loop
	private void makeRows(Color color) {
		for (int i = 0; i < 2.0; i++) {

			// Redefines X to make sure the first row begins in the correct spot
			startX = getWidth()/2.0 - rowLength/2.0;

			// For loop that creates single row of bricks of set color
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect brick = new GRect(startX, startY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick);
				startX = startX + BRICK_WIDTH + BRICK_SEP;
			}

			// Makes startX initial value, pushes startY down to next row
			startX = getWidth()/2.0 - rowLength/2.0;
			startY = startY + BRICK_HEIGHT + BRICK_SEP;
		}
	}

	// Creates new mouse event to establish mouse tracking of paddle
	public void mouseMoved(MouseEvent e) {

		// Creates paddle coordinate variables
		double mouseX = e.getX();
		double paddleY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;

		// Ensures that the paddle stays on the screen
		if (mouseX >= getWidth() - PADDLE_WIDTH) {
			mouseX = getWidth() - PADDLE_WIDTH;
		}

		// Sets location of paddle on the screen
		paddle.setLocation(mouseX,paddleY);
	}

	// Method that creates and returns paddle
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}

	// Method to make and return ball
	private GOval makeBall() {
		double ballSize = 2.0 * BALL_RADIUS;
		GOval ball = new GOval(ballSize, ballSize);
		ball.setFilled(true);
		add(ball);
		return ball;
	}

	// Method to create colliding object 
	private GObject getCollidingObject() {

		// Creates initial starting coordinates for ball
		double ballX = ball.getX();
		double ballY = ball.getY();

		// Creates initial bottom and right x/y coordinates
		double xRight = ballX + 2.0*BALL_RADIUS;
		double yBottom = ballY + 2.0*BALL_RADIUS;

		// Determines if top left corner is contacting object
		if (getElementAt(ballX,ballY) != null) {
			return getElementAt(ballX,ballY);
		}

		// Determines if top right corner is contacting object
		if (getElementAt(xRight,ballY) != null) {
			return getElementAt(xRight,ballY);
		}

		// Determines if bottom right corner is contacting object
		if (getElementAt(xRight,yBottom) != null) {
			return getElementAt(xRight,yBottom);
		}

		// Determines if bottom left corner is contacting object; if none of these have been returned, 
		// returns null to show no collision
		if (getElementAt(ballX,yBottom) != null) {
			return getElementAt(ballX,yBottom);
		} else {
			return null;
		}
	}

	// Method to create "Game Over" label centered in the screen
	private void makeOverLabel() {

		GLabel label = new GLabel("GAME OVER");
		label.setFont("Courier-24");
		double x = getWidth()/2.0 - label.getWidth()/2;
		double y = getHeight()/2.0 - label.getAscent()/2.0;
		add(label, x, y);
	}

	// Makes win label in event user clears all the bricks
	private void makeWinLabel() {

		GLabel label = new GLabel("YOU WIN!");
		label.setFont("Courier-24");
		double x = getWidth()/2.0 - label.getWidth()/2.0;
		double y = getHeight()/2.0 - label.getAscent()/2.0;
		add(label, x, y);
	}
}



