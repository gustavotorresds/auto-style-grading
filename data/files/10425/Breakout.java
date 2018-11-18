/*
 * File: Breakout.java
 * -------------------
 * Name: Gregory Block
 * Section Leader: Vineet Kosaraju
 * 
 * This program will play the arcade game of Breakout, involving a paddle, a moving ball and rows of colored bricks. 
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

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setBricks();
		setPaddle();

		//this limits player to three tries before losing game
		for(int tries = 0; tries < NTURNS && counter > 0; tries ++) {
			setBall();
			playBreakout();
		}

		//these if statements clear screen when all three tries have been used and print losing message
		if(counter > 0) {
			removeAll();
			remove(ball);
		}
		if(counter > 0) {
			printLoserLabel();
		}
	}

	//this method creates a label for lost game
	private void printLoserLabel() {
		GLabel loser = new GLabel("GAME OVER");
		loser.setColor(Color.RED);
		loser.setFont("Dialog-36");
		//centers label in screen
		add(loser, getWidth()/2 - loser.getWidth()/2, getHeight()/2 - loser.getHeight()/2);
	}

	//this method creates a label if game is won
	private void printWinnerLabel() {
		GLabel winner = new GLabel("Congratulations. You win!");
		winner.setColor(Color.BLUE);
		//centers label in screen
		add(winner, getWidth()/2 - winner.getWidth()/2, getHeight()/2 - winner.getWidth()/2);
	}

	//creates the brick counter
	private int counter = 100; 

	//instance variable for the ball
	private GOval ball;

	private void playBreakout() {
		//doesn't start until user clicks screen
		waitForClick();
		findBallVelocity();
		moveBall();
	}

	//this method checks to see if there is an element at any of the four corners of the ball using four different "if" statements
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		if(getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		}
		if(getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null) {
			return getElementAt((ball.getX() + 2*BALL_RADIUS), ball.getY());
		}
		if(getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) ;
		}
		//this else statement checks to see if there is no object at any of the corners of the ball
		else return null;
	}

	private void moveBall() {
		//while loop tells ball to continue moving instead of moving just once
		while(true) {
			//slows ball down so we can see it moving
			pause(DELAY);
			ball.move(vx, vy);
			//check to see if right x coordinate is greater than screen width to find if it hit the left edge of the screen
			if(ball.getX() <= 0) {
				//tells ball to switch direction when it hits edge
				vx = - vx;
			}

			//same as above, but checks for right edge of screen
			if(ball.getX() >= getWidth() - 2 * BALL_RADIUS) {
				vx =  - vx;
			}
			//this is for top of the screen
			if(ball.getY() <= 0) {
				vy = - vy;
			}
			//this is for bottom of the screen
			if(ball.getY() >= getHeight() - 2 * BALL_RADIUS) {
				remove(ball);
				//ends while loop if ball gets past paddle
				break;
			}
			//this creates a collider equal to the element returned from the above method
			GObject collider = getCollidingObject();

			//this is if the collider is a brick
			if (collider != paddle && collider != null) {
				//removes brick when it is struck by the ball
				remove(collider);
				//tells ball to change directions
				vy = -vy;
				//creates a counter for the bricks 
				counter = counter - 1;
				//this plays sound effect each time ball hits brick
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();
				//speeds up ball at certain points throughout game
				if (counter == 90) {
					vy = 1.5* vy;
				}
				if (counter == 60) {
					vy = 1.5 * vy;
				}
				if (counter == 30) {
					vy = 1.5 * vy;
				}
				//if all bricks are gone, game has been won, so it clears screen and prints winner message
				//also breaks out of the while loop
				if(counter == 0) {
					removeAll();
					remove(ball);
					printWinnerLabel();
					break;
				}
			}
			
			//tells ball to change directions if it hits paddle
			else if(collider == paddle) {
				vy = -vy;
			} 
		}
	}

	//establishes instance variables for randomly generated horizontal velocity value and for the horizontal/vertical velocities
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;

	private void findBallVelocity() {
		//this uses the constant for vertical velocity
		vy = VELOCITY_Y;
		//this establishes a random horizontal velocity between 1 and 3
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		//makes horizontal velocity negative half the time
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}

	private void setBall() {
		//this method draws the ball in the center of the screen
		ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);	
	}

	//creates the object outside of the method  
	private GRect paddle;

	//this method draws the paddle in the correct location on the screen
	private void setPaddle() {
		//this will allow the paddle to be tracked by the mouse
		addMouseListeners();
		//x location of paddle on the screen
		double w = getWidth()/2 - PADDLE_WIDTH/2;
		//y location of paddle on the screen
		double h = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect(w, h, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}
	//this method sets up the paddle to be moved by the user's mouse
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}

	//this method will set up the colored bricks and will need two for loops (for rows and for columns)
	private void setBricks() {
		//this creates the coordinate value for the starting brick so that it is centered in the screen
		double startX = getWidth()/2 - NBRICK_COLUMNS/2 * BRICK_WIDTH - ((NBRICK_COLUMNS/2) - 1) * BRICK_SEP - BRICK_SEP * 0.5;
		//this loop establishes the number of rows of bricks
		for (int row = 0; row < NBRICK_ROWS; row ++) {
			//this loop establishes the number of bricks in each row
			for(int column = 0; column < NBRICK_COLUMNS; column ++) {
				//these values will be used 
				double x = startX + column * (BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + row * BRICK_HEIGHT + row * BRICK_SEP;
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);
				//these if statements establish two rows of each color, starting at top with red and moving down to cyan
				if(row < 2) {
					brick.setColor(Color.RED);
				}
				if(row >= 2 && row < 4) {
					brick.setColor(Color.ORANGE);
				}
				if(row >= 4 && row < 6) {
					brick.setColor(Color.YELLOW);
				}
				if(row >= 6 && row < 8) {
					brick.setColor(Color.GREEN);
				}
				if (row >= 8 && row < 10) {
					brick.setColor(Color.CYAN);
				}
			}
		}

	}

}
