/*
 * File: Breakout.java

 * -------------------
 * Name: Kyler Presho
 * Section Leader: Drew Bassilakis
 * 
 * This file will eventually implement the game of Breakout.
 * 
 * Sets up bricks, a paddle that can track the mouse's x coordinates and match it, and a ball. The ball bounces off of walls and the paddle, but when it hits 
 * bricks it both bounces off of that brick and removes that brick. The player must remove all 100 bricks while not letting the ball fall underneath the paddle,
 * and has 3 tries to do so.
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
	//originally 420
	public static final double CANVAS_WIDTH = 420;
	//originally 600
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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//declared to establish velocity
	private double vx, vy;

	//declared so that the paddle can be referenced in multiple methods
	private GRect paddle;

	//declared so brick can be referenced in multiple methods
	private GRect brick;

	//declared as an instance variable so that it the ball can be set up in a private method and also used in the run method
	private GOval ball;

	//declared because honestly the instructions told me to
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//broke the problem down into 3 simple methods: createBricks, createPaddle, and playGame. The first two are pretty self explanatory--they simply
	//create a GObject each, but the last is much more complex. playGame sets up a for loop which gives the player a number of lives based on 
	//a constant (in this case 3) and then runs a while loop to animate the ball moving, bouncing, and removing where necessary. At the end
	//of the game, a label pops up to tell the player whether he or she has won or lost, depending if all the bricks are gone or not.
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//sets up the bricks in the correct location and spacing, then colors them
		createBricks();
		//creates the paddle which the player will be using
		createPaddle();
		//plays the game
		playGame();
	}

	//the actual "play" part of the program. Establishes some variables necessary for the animation loop to run properly, 
	//establishes a for loop which will give the player 3 "lives" and then runs through the animation loop until the player either
	//wins or loses the game (or simply doesn't click to start it)
	private void playGame() {
		//sets up the integer for the number of blocks to be manipulated later
		int numBricksLeft = 100;
		//repeats the loop 3 times, giving the player 3 chances to remove all the bricks before losing the game
		int numLivesLeft = 2;
		//creates the condition which immediately exits the program from the animation loop upon completing the game
		boolean isFinished = false;
		//runs through 3 times so that the player has 3 chances to complete the game
		for(int i = 0; i < NTURNS; i++) {
			addMouseListeners();
			createBall();
			vx = rgen.nextDouble(VELOCITY_X_MIN, 3.0);
			if(rgen.nextBoolean(0.5)) vx = -vx;
			vy = VELOCITY_Y;
			waitForClick();
			//continues to run the animation while the ball isn't below the bottom of the canvas
			while (isNotBelowFloor() && !isFinished) {
				ball.move(vx, vy);
				pause(DELAY);
				//sets the variable collider equal to the return from the function getCollidingObject
				GObject collider = getCollidingObject();
				//sets up an if statement for when collider registers as not null
				if(collider != null) {
					//when the collider value equals the paddle, forces the ball to move in the negatve y direction. Avoids "sticky paddle"
					//by using absolute value
					if(collider == paddle) {
						vy = -(Math.abs(vy));
						//if the collider registers as anything but the paddle, it should be a brick (walls aren't objects). As such, the ball should
						//invert its y velocity (bounce) and then remove what it hit
					} else {
						vy = -vy;
						remove(collider);
						numBricksLeft--;
					}
				}
				//sets the boundary for the right wall and makes ball "bounce" off of it by inverting x velocity
				if(isPastRight()) {
					vx = -vx;
				}
				//sets the boundary for the left wall and makes ball "bounce" off of it by inverting x velocity
				if(isPastLeft()) {
					vx = -vx;
				}
				//sets the boundary for the ceiling and makes ball "bounce" off of it by inverting y velocity
				if(isPastTop()) {
					vy = -vy;
				}
				if(numBricksLeft == 0) {
					isFinished = true;
				}
			}
			//removes the ball so that there are no extra balls laying around at the end
			remove(ball);
			//displays a label telling the player he/she has won if there are 100 collisions in the game, not
			//including walls (there are 100 blocks to start the game)
			if(numBricksLeft == 0) {
				createWinningLabel();
			//displays the losing label if the player runs out of "lives" before removing all 100 bricks
			} else if(numLivesLeft == 0) {
				createLosingLabel();
			}
			//removes any extra balls left over if the player has not used up all his/her "lives" upon completing the game
			while(isFinished && numLivesLeft > 0) {
				remove(ball);
			}
			//subtracts 1 from the number of lives left
			numLivesLeft--;
			//displays that the player has lost the game if he/she has used up all 3 attempts
		}
	}
	
	//creates the label for players who fail to remove all the bricks with their 3 lives
	private void createLosingLabel() {
		GLabel losingLabel = new GLabel("Loser!");
		losingLabel.setFont("Courier-24");
		losingLabel.setColor(Color.RED);
		double centerX = getWidth() / 2 - losingLabel.getWidth() / 2;
		double centerY = getHeight() / 2 - losingLabel.getHeight() / 2;
		add(losingLabel, centerX, centerY);
	}

	//creates the label for players who have won
	private void createWinningLabel() {
		GLabel winningLabel = new GLabel("Congratulations! You've won!");
		winningLabel.setFont("Courier-24");
		winningLabel.setColor(Color.CYAN);
		double centerX = getWidth() / 2 - winningLabel.getWidth() / 2;
		double centerY = getHeight() / 2 - winningLabel.getHeight() / 2;
		add(winningLabel, centerX, centerY);
	}

	//sets up the first parameter which the ball must pass to remain in the animation while loop
	private boolean isNotBelowFloor() {
		return ball.getY() < getHeight();
	}

	//sets the top boundary of the game
	private boolean isPastTop() {
		return ball.getY() < 0;
	}

	//sets the left boundary
	private boolean isPastLeft() {
		return ball.getX() < 0;
	}

	//sets the right boundary
	private boolean isPastRight() {
		return ball.getX() > getWidth() - (2 * BALL_RADIUS);
	}

	//sets up the bricks for the game in a similar way to how pyramid was constructed in the last assignment. 
	//At the end of each row created, the y coordinate is changed by the length of one brick plus the separation
	//between bricks. This process is repeated 10 times.
	private void createBricks() {

		double topRow = BRICK_Y_OFFSET;

		double centerX = getWidth() / 2 - (((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS) - BRICK_SEP) / 2;

		//a nested for loop which establishes the columns of bricks and then the rows
		for(int k = 0; k < NBRICK_COLUMNS; k++) {
			for(int i = 0; i < NBRICK_ROWS; i++) {
				brick = new GRect ((i * (BRICK_WIDTH + BRICK_SEP)) + centerX, topRow, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				//colors the bricks red, orange, yellow, green, or cyan depending on which row they are in. Colors in lines
				//further down simply paint over the previous ones
				if(k < 10) {
					brick.setColor(Color.RED);
					brick.setFilled(true);
				}
				if(k == 2 || k == 3) {
					brick.setColor(Color.ORANGE);
					brick.setFilled(true);
				}
				if(k == 4 || k == 5) {
					brick.setColor(Color.YELLOW);
					brick.setFilled(true);
				}
				if(k == 6 || k == 7) {
					brick.setColor(Color.GREEN);
					brick.setFilled(true);
				}
				if(k == 8 || k == 9) {
					brick.setColor(Color.CYAN);
					brick.setFilled(true);
				}
			}
			//moves the top row of bricks one higher before running through the for loop again--similar to how I set up pyramid
			topRow = topRow + (BRICK_HEIGHT + BRICK_SEP);
		}
	}
	
	//creates the paddle as a GRect
	private void createPaddle() {

		double centerX = getWidth() / 2 - PADDLE_WIDTH / 2;

		double paddleY = getHeight() - PADDLE_Y_OFFSET;

		paddle = new GRect (centerX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	//creates the ball as a GOval
	private void createBall() {

		double centerX = getWidth() / 2 - ((BALL_RADIUS * 2) / 2);

		double centerY = getHeight() / 2 - ((BALL_RADIUS * 2) / 2);

		ball = new GOval (centerX, centerY, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}
	
	//sets up a method which detects if the ball is colliding with any objects at any of its four corners. Returns a GObject
	//if the ball collides with any object (either the paddle or a brick, the walls don't count)
	private GObject getCollidingObject() {										
		//detects if the bottom left of the ball collides with an object
		GObject detectBottomLeft = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		GObject detectBottomRight = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		GObject detectTopRight = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		GObject detectTopLeft = getElementAt(ball.getX(), ball.getY());
		if(detectBottomRight != null) {
			return detectBottomRight;
		} else if(detectBottomLeft != null) {
			return detectBottomLeft;
		} else if(detectTopRight != null) {
			return detectTopRight;
		} else if(detectTopLeft != null) {
			return detectTopLeft;
		} else {
			return null;
		}
	}

	//sets up the paddle to track the mouse's x location and centers it
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		double y = paddle.getY();
		paddle.setLocation(x, y);
		if(x < 0) {
			paddle.setLocation(0, y);
		}
		double rightBound = getWidth() - PADDLE_WIDTH;
		if(x > rightBound) {
			paddle.setLocation(rightBound, y);
		}
	}

}