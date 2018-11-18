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

import com.sun.javafx.font.directwrite.RECT;

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

	private GRect paddle;

	private GOval ball;

	private double vx;

	private double vy = 3.0;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private GLabel gameOver;

	private GLabel congratulations;

	private int nTurns = NTURNS; // Keeps track of turns

	private int nRemaining = NBRICK_ROWS * NBRICK_COLUMNS; // Keeps track of bricks



	public void run() {
		// Set the window's title bar text
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//Sets up rectangles and calibrates paddle
		setup();
		// Plays the game
		play();
	}	

	//Setup first creates the bricks and gives them color. Then it creates the paddle.
	private void setup() {	
		//Nested for loops to create 10 rows of ten bricks
		addMouseListeners(); //Prepares to implement MouseEvent
		for (int col = 0; col < NBRICK_COLUMNS; col++) {
			for (int row = 0; row < NBRICK_ROWS; row++) {
				double x = (getWidth()- NBRICK_ROWS * (BRICK_WIDTH+ BRICK_SEP)) / 2 + (BRICK_WIDTH + BRICK_SEP) * row; // x is calculated using getWidth() row width and current brick as factor of row
				double y = BRICK_Y_OFFSET + col * BRICK_HEIGHT+ BRICK_SEP * col; // Y is calculated using offset, brick height * col and brick sep * col
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);

				//Set of if statements that fill in the proper color
				if (col == 0 || col ==1) {
					brick.setColor(Color.RED);
				}
				else if (col == 2 || col == 3) {
					brick.setColor(Color.ORANGE);
				}
				else if (col == 4 || col == 5) {
					brick.setColor(Color.YELLOW);
				}
				else if (col == 6 || col == 7) {
					brick.setColor(Color.GREEN);
				}
				else if (col == 8 || col == 9) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
		// Creates the paddle
		paddle = new GRect((getWidth() - PADDLE_WIDTH)/ 2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);

	}
	// Moves paddle with mouse; Keeps mouse centered in paddle.
	// Ensures that paddle does not go off the canvas.
	public void mouseMoved(MouseEvent e) {
		double newX = e.getX();
		if (newX > 0.5*PADDLE_WIDTH && newX < getWidth()- 0.5*PADDLE_WIDTH ) {
			paddle.setLocation(e.getX()- 0.5*PADDLE_WIDTH, getHeight()-PADDLE_Y_OFFSET);
		}
	}
	// Plays the game. First creates the ball and then sets up an animation loop
	private void play() {
		//Creates ball and centers it in the screen
		double x = (getWidth() - BALL_RADIUS) / 2;
		double y = (getHeight() - BALL_RADIUS) / 2;
		getBall(x, y);

		// Animation loop to move ball around the screen. Includes 3 methods
		while(true) {
			//1. Ball bounces off sides or top
			offTheWalls();
			double dy = offTheWalls();
			//2. Ball Passes bottom:
			pastBottom(x, y, dy);
			if (nTurns == 0) {
				break; // Ends losing game
			}
			// 3. Collisions and end of winning game
			collisions();
			if (nRemaining == 0){
				break; // ends winning game
			}
		}
	}
	// GOval created, set in motion
	private void getBall(double x, double y) {

		ball = new GOval(x, y, BALL_RADIUS * 2, BALL_RADIUS *2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);

		vx = rgen.nextDouble(1.0, 3.0); //Initializes using random number generator
		if(rgen.nextBoolean(0.5)) vx = -vx;
	}

	// Conditions for ball movement off of main walls
	private double offTheWalls(){
		ball.move(vx, vy); 
		pause(DELAY); // The main animation function is here.

		double dx = ball.getX();
		double dy = ball.getY();
		if(dx > getWidth() - BALL_RADIUS * 2) { // Ball bounces off right side, reverses x direction
			vx = -vx;	
		}
		if (dx < 0) { // Ball bounces off left side, reverses x direction
			vx = -vx;
		}
		if (dy < 0) { // Ball bounces off top, revereses y direction
			vy = -vy;
		}
		return dy;
	}	
	// Conditions for when ball passes the bottom of screen
	private void pastBottom(double x, double y, double dy) {
		if (dy > getHeight()) {
			nTurns -= 1;
			if (nTurns == 0) {
				gameOver = new GLabel ("Game Over :(", getWidth()/2, getHeight()/2); // Height and width not chosen in order to center label
				add(gameOver);
			}
			else { 
				add(ball, x, y);
				ball.move(vx, vy);
			}
		}
	}
	// Deletes bricks and reverses direction or moves up off of paddle
	private void collisions () {
		GObject collider = getCollidingObject();
		if (collider == paddle) { // Condition for hitting paddle 
			vy = - VELOCITY_Y; // Moves up rather than reversing direction. Prevents "sticky" paddle
		} else if(collider != null) { // Condition for hitting brick
			vy = - vy;
			remove(collider);
			nRemaining -= 1;	
		}
		if (nRemaining == 0) {
			congratulations = new GLabel ("Congratulations!", getWidth()/ 2, getHeight()/2); // Height and width not chosen in order to center label
			add(congratulations);
		}
	}
	// Searches the four corners of the GOval to return presence of brick
	private GObject getCollidingObject() {
		double x1 = ball.getX();
		double y1 = ball.getY();
		double x2 = x1 + BALL_RADIUS * 2;
		double y2 = y1 + BALL_RADIUS * 2;
		if (getElementAt(x1,y1) != null) { // Tests upper left corner
			return getElementAt(x1,y1);
		}
		else if (getElementAt(x2, y1) != null) { // Tests upper right corner
			return getElementAt(x2, y1);
		}
		else if (getElementAt(x1, y2) != null) { // Tests lower left corner
			return getElementAt(x1, y2);
		}
		else if (getElementAt(x2, y2) != null){ // Tests lower right corner
			return getElementAt(x2, y2);
		} else {
			return null;
		}
	}
}



