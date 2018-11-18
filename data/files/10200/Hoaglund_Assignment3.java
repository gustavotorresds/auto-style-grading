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

	private GRect paddle  = new GRect((getWidth() - PADDLE_WIDTH)/ 2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);

	private GOval ball;

	private double vx;

	private double vy = 3.0;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private GLabel turnsLeft;

	private GLabel gameOver;

	private GLabel congratulations;



	public void run() {
		// Set the window's title bar text
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//Sets up rectangles and calibrates paddle
		setup();
		play();
	}	
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

	private void play() {
		//Creates ball and centers it in the screen
		double nTurns = NTURNS;
		double x = (getWidth() - BALL_RADIUS) / 2;
		double y = (getHeight() - BALL_RADIUS) / 2;
		ball = new GOval(x, y, BALL_RADIUS * 2, BALL_RADIUS *2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);


		vx = rgen.nextDouble(1.0, 3.0); //Initializes vx using random number generator
		if(rgen.nextBoolean(0.5)) vx = -vx;

		


		// Animation loop to move ball around the screen
		while(true) {
			ball.move(vx, vy);
			pause(DELAY);
			double dx = ball.getX();
			double dy = ball.getY();
			if(dx > getWidth() - BALL_RADIUS* 2) { // Ball bounces off right side
				vx = -vx;	
			}
			if (dx < BALL_RADIUS * 2) { // Ball bounces off left side
				vx = -vx;
			}
			if (dy < BALL_RADIUS * 2) { // Ball bounces off top
				vy = -vy;
			}

			//Ball Passes bottom:
			if (dy > getHeight()) {
				nTurns -= 1;
				if (nTurns == 0) {
					remove(ball);
					gameOver = new GLabel ("Game Over :(", getWidth()/2, getHeight()/2);
					add(gameOver);
					break;}
				else { 
					add(ball, x, y);
					ball.move(vx, vy);
				}
			}
			
			
			// Collisions and end of game
			int nRemaining = collisions();
			if (nRemaining == 0) {
				congratulations = new GLabel ("Congratulations!", getWidth()/ 2, getHeight()/2);
				add(congratulations);
				break;
			}
		}
	}
		private int collisions () {
			int nRemaining = NBRICK_ROWS * NBRICK_COLUMNS; // Tracks number of bricks remaining
			GObject collider = getCollidingObject();
			if (collider == paddle) { // Condition for hitting paddle 
				vy = - VELOCITY_Y;
			} else if(collider != null) { // Condition for hitting brick
				vy = - vy;
				remove(collider);
				nRemaining -= 1;				
			}
			return(nRemaining);
		}	
	
	
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		if (getElementAt(x,y) != null) {
			return getElementAt(x,y);
		}
		else if (getElementAt(x + BALL_RADIUS * 2, y) != null) {
			return getElementAt(x + BALL_RADIUS * 2, y);
		}
		else if (getElementAt(x, y + BALL_RADIUS *2) != null) {
			return getElementAt(x, y + BALL_RADIUS * 2);
		}
		else if (getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2) != null){
			return getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2);
		} else {
			return null;
		}
	}
}



