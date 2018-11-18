/*
 * File: Breakout.java
 * -------------------
 * Name: Travis Ramirez
 * Section Leader: Rhea
 * 
 * This file will eventually implement the game of Breakout.
 */


//This program still has a few bugs. I tried going back to fix my errors, but would get lost in loops and methods. 
//However, I believe a lot of the code is functional otherwise.


import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

public class Breakout extends GraphicsProgram {

	// DIMENSIONS of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	//Total number of Bricks
	public static final int BRICK_TOTAL = NBRICK_COLUMNS * NBRICK_ROWS; 

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

	//Add paddle rectangular object that ball can bounce off of
	private GRect paddle;

	//Add ball object that will bounce of paddle
	private GOval ball;  

	//Add brick object to the screen
	private GRect brick;

	//velocities (x and y directions) for the start of the ball 
	private double vX, vY;

	//Use random generator for x-direction
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//The number of tries a player has left before game over
	private int turn = 0;

	//A count for the number of bricks that have been removed
	private int removedBricks = 0;
	
	//A label for the end game results
	private GLabel result;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */

		//Add mouse listeners so the paddle can be traced
		addMouseListeners();

		//Method that will set up the game on the canvas
		setUpGame();

		//Method that runs game and makes necessary adjustments between turns
		playGame();
	}

	private void setUpGame(){
		//The method will need to build the bricks on the canvas so call build brick method
		buildBricks();
		//The method will also need to create a paddle that stays within the barrier walls of the canvas
		createPaddle();
	}

	private void playGame() {
		//loop checking if you still have turns left
		while (turn < NTURNS) {
			createBall();
			//method that will wait for user to click before the game begins and the ball moves
			waitForClick();
			moveBall();
			turn++;
		}
		printResult();
	}

	private void buildBricks() {
		//10 rows and 10 columns
		//1st start by setting up rows
		for (int row = 0; row < NBRICK_ROWS; row++) {

			//2nd set up the columns in the rows
			for (int column = 0; column < NBRICK_COLUMNS; column++) {

				//3rd create the rectangular brick GRect (x, y (with offset), width, height)
				brick = new GRect((BRICK_WIDTH + BRICK_SEP) * column, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row, BRICK_WIDTH, BRICK_HEIGHT);

				//fill in the bricks with color
				brick.setFilled(true);

				//fill each brick with the appropriate color
				//QUESTION FOR RHEA: Could I have decomp this to another method? How do I pass brick/brick color?
				if (row < 2) {
					brick.setColor(Color.RED);
				}
				else if (row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
				}
				else if (row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				}
				else if (row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				}
				//QUESTION: why does this not work with "else" to end if-else list?
				else if (row == 8 || row == 9 ) {
					brick.setColor(Color.CYAN);
				}

				//lay brick down on canvas
				add(brick);		
			}
		}
	}


	//This method creates a paddle for breakout
	private void createPaddle() {

		//need to build paddle rectangular shape (x-location, y-location, width, height)
		paddle = new GRect (getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		//fill paddle with color
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		//adds paddle to canvas
		add(paddle);
	}

	//Mouse tracker of the paddle
	//ConditionChecks: paddle cannot sink into side of walls, so paddle must remain in same position
	public void mouseMoved(MouseEvent e) {
		if (e.getX() >= 0 && e.getX() < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(e.getX(), getHeight() - PADDLE_Y_OFFSET);
		}

		//condition: make sure paddle is not past the wall
		else if (e.getX() >= getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	//Method that creates the ball for Breakout and places it at the center of the screen
	private void createBall() {
		//create ball that shows up in middle of the screen (x-location, y-location, width of ball, height ball)
		//ball is built from left corner
		ball = new GOval (getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	//method is for the movement function of the ball
	//Given velocities in X and Y direction
	private void moveBall() {
		vX = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		//50 percent of the time we change the initial velocity in x-direction
		if (rgen.nextBoolean(0.5)) vX = -vX;
		vY = VELOCITY_Y; 

		//Loop will check when ball hits walls and eventually end the loop when there is no balls remaining
		while (removedBricks < BRICK_TOTAL) {
			ball.move (vX, vY);
			pause (DELAY);
			checkWalls();
			GObject collider = getCollidingObject();

			//ball bounces back up when it hits the paddle
			if (collider == paddle) {
				vY = -vY;
			}

			//ball hits something that is not a paddle and bounces back in opposite y-direction
			else if (collider != null && collider != paddle) {
				vY = -vY;
				remove (collider);
				removedBricks++;
			}
		}
	}

	//Method to check when the ball hits the walls
	//if ball touches left or right wall, the x-direction should switch
	//if ball touches top or bottom wall, the y-direction should switch
	//LATER: bottom wall is GAME OVER
	private void checkWalls() {
		//checks when ball touches left wall
		if(ball.getX() <= 0) {
			vX = -vX;
		}

		//checks when ball touches right wall
		else if(ball.getX() + 2 * BALL_RADIUS >= getWidth()) {
			vX = -vX;
		}

		//check when ball touches top wall
		else if(ball.getY() <= 0) {
			vY = -vY; 
		}

		//check when ball touches bottom wall
		else if (ball.getY() + 2 * BALL_RADIUS >= getHeight()) {
			remove(ball);
		}
	}

	//Check element at the 4 corners of the ball's perimeter square
	//This method is suitable for when the ball collides with any object
	private GObject getCollidingObject() {
		//if not empty 
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		}
		else if (getElementAt(ball.getX(), ball.getY()+ 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		}
		else if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()+ 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		}

		return null; 
	}
	
	//method will print the result of the game
	private void printResult () {
		result = new GLabel ("Congratualtions You Won!!!" + ".....But Are You Ready For Level Two");
		//coordinates for GLabel
		double x = getWidth()/2 - result.getWidth()/2; 
		double y = getHeight()/2;
		
		//Conditions to be a winner
		if (removedBricks == BRICK_TOTAL) {
			add (result, x, y);
		}
		
		//Conditions if you are not a winner yet
		else if (turn == NTURNS) {
			result = new GLabel ("You Lose!!! Better look next time. May I recommend some youtube video tutorials?");
			add (result, x, y);
		}
	}
}
