/*
 * File: Breakout.java
 * -------------------
 * Name: Serena Jing
 * Section Leader: Andrew Marshall
 * 
 * This file implements the game "Breakout". Breakout is a game in which the user has control of a paddle which can be used
 * to direct a ball towards a brick wall which removes the bricks it comes in contact with. 
 * The player gets three chances to remove the bricks in the brick wall and each chance is lost once the user loses 
 * the ball (by letting it slip past the paddle). 
 *
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

	// Distance between bottom of the paddle and bottom of screen
	public static final double Y_DIST = PADDLE_Y_OFFSET + PADDLE_HEIGHT;

	// Creates a paddle
	GRect paddle = null;

	// Creates a ball
	GOval ball = null;

	// Creates a brick
	GRect brick = null;

	// Brick counter
	int counter;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity
	public static final double VELOCITY_Y = 3.0;

	// Random number generator (will be used to generate random horizontal velocities for the ball)
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Horizontal and vertical velocities of the ball
	private double vx,vy;

	// Object that ball collides with (either paddle or brick)
	private GObject collider;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-)
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public void run() {

		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Initializes the counter to 100 bricks
		counter = 100;	

		addMouseListeners();
		createWall();
		createPaddle();
		for (int i = 0; i < NTURNS; i++) {
			createBall();
			moveBall(); 
		}

		// Depending on how many bricks are left after all three turns, an end banner will be displayed indicating
		// whether or not the user has won or lost the game 
		if (counter == 0) {
			remove(ball);
			GLabel label = new GLabel("Congratulations! You win.");

			// Adds the label to the center of the screen
			label.setLocation(getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getHeight()/2);
			add(label);
		} else if (counter > 0) {
			remove(ball);
			GLabel label = new GLabel("Game Over! You lose.");

			// Adds the label to the center of the screen
			label.setLocation(getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getHeight()/2);
			add(label);
		}
	}

	// Creates the wall of bricks that the user must break through with a ball
	private void createWall() {
		for(int r = 0; r < NBRICK_ROWS; r++) {
			for(int c = 0; c < NBRICK_COLUMNS; c++) {

				// Sets the location for each brick that is added 
				double x = r * BRICK_WIDTH; 
				double y = c * BRICK_HEIGHT; 
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);

				// Colors a row of bricks depending on the row number
				if (c == 0 || c == 1) {
					brick.setFilled(true);
					brick.setColor(Color.RED);
				} else if (c == 2 || c == 3) {
					brick.setFilled(true);
					brick.setColor(Color.ORANGE); 
				} else if (c == 4 || c == 5) {
					brick.setFilled(true);
					brick.setColor(Color.YELLOW);
				} else if (c == 6 || c == 7) {
					brick.setFilled(true);
					brick.setColor(Color.GREEN);
				} else if (c == 8 || c == 9) {
					brick.setFilled(true);
					brick.setColor(Color.CYAN);
				}

				// Adds the bricks so that the center of the wall is aligned with the horizontal center of the screen
				add(brick, x + getWidth()/2 - (BRICK_WIDTH + BRICK_SEP) * NBRICK_ROWS/2 + r * BRICK_SEP, BRICK_Y_OFFSET + y + c * BRICK_SEP);
			}

		}

	}

	// Creates the paddle used to bounce the ball and hit the bricks
	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);

		// Adds the paddle to the horizontal center of the screen
		add(paddle, getWidth()/2 - PADDLE_WIDTH/2, getHeight() - Y_DIST);
	}

	// Creates the ball used to hit the bricks and remove them
	private void createBall() {
		ball = new GOval(BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);

		// Adds the ball to the center of the screen 
		add(ball, getWidth()/2 - BALL_RADIUS/2, getHeight()/2 - BALL_RADIUS/2);
	}

	// Controls the movement of the ball, including how the ball bounces when it encounters different objects  
	private void moveBall() {

		// Generates a random horizontal velocity for the ball between 1 and 3
		vx = rgen.nextDouble(1.0, 3.0);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}

		// Ball sets in motion after user clicks the screen
		waitForClick();

		// Ball is moved as long as it remains within the dimensions of the screen
		while ((ball.getX() > 0 && ball.getX() < getWidth()) || (ball.getY() > 0 && ball.getY() < getHeight())) {
			ball.move(vx, vy);

			// Passes whatever object collided with the ball to the variable collider and distinguishes this collider
			// as either a paddle or brick
			collider = getCollidingObject();

			// Reverses the direction of the ball in the vertical direction if the collider was the paddle 
			if (collider == paddle) {
				vy = -vy;

				// Reverses the direction of the ball in the vertical direction if the collider was a brick and removes the brick once 
				// the ball has collided with it
			} else if (collider != null) {
				remove(collider);
				counter--;
				vy = -vy;	
			}

			// If the ball hits any of the sides of the screen (except the bottom side), the ball will bounce in the opposite
			// direction that it approached the side initially
			if ((ball.getX() <= 0 || ball.getX() >= getWidth() - 2*BALL_RADIUS)) {
				vx = -vx;
			} 
			if (ball.getY() <= 0) {
				vy = -vy;
			}

			if (ball.getY() >= getHeight() - 2*BALL_RADIUS) {
				remove(ball);
				break;	
			}
			pause(DELAY);
		}
	} 

	// Checks whether or not the ball has collided with any object
	private GObject getCollidingObject() {
		GObject collObj = getElementAt(ball.getX(), ball.getY());
		if (collObj != null) {
			return collObj;	
		}
		collObj = getElementAt(ball.getX()+2 * BALL_RADIUS, ball.getY());

		if (collObj != null) {
			return collObj;	
		}

		collObj = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		if (collObj != null) {
			return collObj;	
		}

		collObj = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);

		if (collObj != null) {
			return collObj;	
		}

		return null;
	}

	// Mouse controlled paddle movement based on movement of x coordinate of mouse
	public void mouseMoved(MouseEvent event) {
		int x = event.getX();

		// Ensures that the paddle remains on the screen 
		if(x <= PADDLE_WIDTH/2) {
			paddle.setLocation(0, getHeight() - Y_DIST);
		} else if (x >= getWidth() - PADDLE_WIDTH/2) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - Y_DIST);
		} else {
			paddle.setLocation(x - PADDLE_WIDTH/2, getHeight() - Y_DIST);
		}

	}
}




