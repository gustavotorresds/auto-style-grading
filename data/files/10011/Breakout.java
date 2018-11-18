/*
 * File: Breakout.java
 * -------------------
 * Name: Ahmad Ibrahim
 * Section Leader: James Mayclin
 * 
 * This file will implement the game of Breakout.
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

	// The ball's minimum and maximum horizontal velocity; 
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// define paddle and its coordinates
	GRect Paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);

	// define random generator (to be used in ball movement)
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// define an object that will be used to remove bricks 
	GObject Collider = null;

	// ball to be used across the whole program
	GOval ball = null;

	// velocity of ball in x-direction
	double vx;

	// velocity of ball in y-direction
	double vy;

	// number of Bricks in all rows (or all columns)
	int countBricks = NBRICK_ROWS*NBRICK_COLUMNS; 

	// success message when user wins!
	private GLabel Success = new GLabel("");

	// failure message when user loses
	private GLabel Failure = new GLabel("");

	// display number of remaining trials (before the user loses)
	private GLabel trialsRemaining = new GLabel("");

	// This run method will draw the group of bricks,
	// create both the paddle and the ball, and set a
	// framework for the ball to move within the canvas
	// in a way that removes the bricks and completes the
	// game.
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//count number of bricks
		for (int turnsLeft = NTURNS; turnsLeft > 0; turnsLeft--) {
			//print out remaining rounds
			printTurnsLeft(turnsLeft);
			// draw group of bricks at the top of the screen
			drawBricks();
			// identify location of mouse
			addMouseListeners();
			// draws the ball and waits for a click to start moving it
			ball = drawBall();
			waitForClick();
			playGame();
			if (countBricks == 0) {
				removeAll();
				// display success message
				successMessage();
				break;
			}
		}
		if (countBricks != 0) {
			removeAll();
			// display failure message
			failureMessage();
		}
	}

	private void playGame() {
		// randomize the velocity of x between a minimum (1) and a maximum (2)
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		// make the velocity of x negative half the time
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		// remove the text displaying the remaining trails at beginning of the game
		remove (trialsRemaining);
		// move ball while a) it doesn't hit the bottom  
		// and b) the number of bricks is not zero 
		while(hitBottomWall(ball) != true & countBricks != 0) {

			ball.move(vx, vy);
			// bounce ball within canvas

			bounceBall();
			// check whether a colliding object is present

			GObject collider = getCollidingObject();
			// if  object collided is the paddle, make
			// sure the y velocity of the ball is up

			if(collider == Paddle) {
				vy = - Math.abs(vy);
			} else if(collider != null) {
				// add audio sound every time the ball hits a brick
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();
				// remove collided object if it is 
				// not not the paddle not the null
				remove (collider);
				// once the ball hits a brick and causes it to disappear,
				// decrease the total number of bricks by 1
				countBricks--;
				vy = -vy;
			}			
		}
		// removes old ball to avoid collision with new ball
		// when the playGame method enters in the for loop
		remove(ball);
	}
	// lets the user know how many trails 
	// are left before losing the game
	private void printTurnsLeft(int turnsLeft) {
		int Trials = turnsLeft;
		trialsRemaining.setLabel("Remaining Trials: " + Trials);
		trialsRemaining.setFont("Courier-28");
		trialsRemaining.setColor(Color.RED);
		add (trialsRemaining, getWidth()/2 - trialsRemaining.getWidth()/2, BRICK_Y_OFFSET/2);
	}
	// bounces the ball from the top, left and right walls 
	private void bounceBall() {
		if (hitRightWall(ball) || hitLeftWall(ball) ) {
			vx = -vx;
		}
		if (hitTopWall(ball)) {
			vy = -vy;
		}	
		pause(DELAY);
	}
	// identifies whether a colliding object exists
	// at each of the four corners of the ball
	private GObject getCollidingObject() {

		for (int row = 0; row < 2; row++) {

			for (int col = 0; col < 2; col++) {

				double xCorner = ball.getX() + col*ball.getWidth();
				double yCorner = ball.getY() + row*ball.getWidth();

				GObject collider = getElementAt(xCorner,yCorner);
				if (collider != null) {
					return collider;
				}
			}
		}
		return null;
	}
	// lets the user know that the game is 
	// over and that he/she has failed
	private void failureMessage() {
		Failure.setLabel("Game Over! Sorry, you lost..");
		Failure.setFont("Courier-24");
		Failure.setColor(Color.RED);
		add (Failure, getWidth()/2 - Failure.getWidth()/2, getHeight()/2 - Failure.getHeight()/2);
	}
	// lets the user know that he/she has won! 
	private void successMessage() {
		Success.setLabel("Congrats, you have won! :)");
		Success.setFont("Courier-24");
		Success.setColor(Color.BLUE);
		add (Success, getWidth()/2 - Success.getWidth()/2, getHeight()/2 - Success.getHeight()/2);
	}
	// defines the x coordinate the ball can  
	// reach before bouncing off the right wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}
	// defines the x coordinate the ball can  
	// reach before bouncing off the left wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}
	// defines the y coordinate the ball can  
	// reach before bouncing off the bottom wall
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight();
	}
	// defines the y coordinate the ball can  
	// reach before bouncing off the top wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() < 0;
	}
	// draws the bricks in the top part of the screen
	private void drawBricks() {

		for (int row = 0; row < NBRICK_ROWS; row++) {

			for (int col = 0; col < NBRICK_COLUMNS; col++) {

				// define x and y coordinates for each brick
				double x = BRICK_SEP + col*(BRICK_SEP+BRICK_WIDTH);
				double y = BRICK_Y_OFFSET + row*(BRICK_SEP+BRICK_HEIGHT);

				// define characteristics of brick shape (width, height, x and y coordinates) 
				GRect Brick = new GRect (x,y, BRICK_WIDTH, BRICK_HEIGHT);

				//color brick according to its row number
				//every two rows have a different color
				if (row<2) {
					Brick.setColor(Color.RED);
				} else if (row < 4) {
					Brick.setColor(Color.ORANGE);
				} else if (row < 6) {
					Brick.setColor(Color.YELLOW);
				} else if (row < 8) {
					Brick.setColor(Color.GREEN);
				} else {
					Brick.setColor(Color.CYAN);
				}

				// fill each brick
				Brick.setFilled(true);

				// draw brick
				add(Brick);	
			}
		}
	}
	// add the paddle and move it horizontally using the mouse
	public void mouseMoved (MouseEvent e) {		

		// fill and color the paddle black
		Paddle.setFilled(true);
		Paddle.setColor(Color.BLACK);

		// define x and y coordinates of paddle
		double xPaddle = e.getX();
		double yPaddle = getHeight() - PADDLE_Y_OFFSET;

		if (xPaddle > getWidth() - PADDLE_WIDTH) {
			xPaddle = getWidth() - PADDLE_WIDTH;
		}

		// set location of paddle (fix y-axis and move in x-axis with mouse)
		Paddle.setLocation(xPaddle, yPaddle);
		add (Paddle);
	}
	// draw the ball in the center of the screen
	private GOval drawBall() {

		// set x and y coordinates of ball in the center of the screen
		double xBall = getWidth()/2 - BALL_RADIUS;
		double yBall = getHeight()/2 - BALL_RADIUS;
		GOval ball = new GOval (xBall, yBall, 2*BALL_RADIUS, 2*BALL_RADIUS);

		// color, fill and add the ball to the screen
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, xBall, yBall);
		return ball;
	}
}
