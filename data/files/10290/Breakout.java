/*
 * File: Breakout.java
 * -------------------
 * Name: Jared Hysinger
 * Section Leader: Tessera Chin
 * 
 * This file plays the game of Breakout.
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
	public static double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Mouse Coordinates, used for paddle
	private int MouseX = 0;
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	//Random Generator for Ball
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Creates an instance variable for the ball
	private GOval ball = null;

	//Creates an instance variable which generates the ball's diameter
	private double BALL_DIAMETER = BALL_RADIUS*2;

	//Creates instance variables for the ball's current X,Y position
	private double ballCurrentX;
	private double ballCurrentY;

	//Creates an instance variable to track # of bricks which have been removed
	private int bricksRemoved = 0;

	//Creates an instance variable which determines the total number of bricks at the beginning
	private int TOTAL_BRICKS = NBRICK_COLUMNS * NBRICK_ROWS;


	//Creates an instance variable which keeps track of the number of lives the user has remaining.
	private int livesRemaining = NTURNS;

	//Creates an instance variable which determines the ease at which a corner shot off the paddle can be made.
	private int CORNER_SHOT_LIMIT = 5;

	//Creates instance variables for the 2 mid-game messages
	private GLabel welcomeMessage;
	private GLabel nextLife;



	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setupGame();
		addMouseListeners();
		while(livesRemaining != 0) {
			waitForClick();
			playGame();

		}
	}

	private void playGame() {
		bouncingBallSetup();
		getCollidingObject();
		bouncingBallMove();
		terminateGame();
	}

	//This method runs every time the ball either hits the bottom wall, or the player wins.
	private void terminateGame() {


		//This runs a congratulations message and terminates the game when the player eliminates the last brick.
		if(bricksRemoved == TOTAL_BRICKS) {
			GLabel winMessage = new GLabel ("Congratulations! YOU WIN");
			winMessage.setFont("Courier-24");
			winMessage.setColor(Color.GREEN);
			winMessage.setLocation(0, getHeight()/2);
			add(winMessage);
		}

		//This subtracts a life from the "remaining" amount of lives before running the other if-statements.
		livesRemaining = livesRemaining - 1;

		//If the player has lives remaining: displays a message w/ amount of lives left
		if(livesRemaining != 0) {
			if(bricksRemoved != TOTAL_BRICKS) {
				nextLife = new GLabel ("Click to start new ball. You have " + livesRemaining + " lives remaining.");
				nextLife.setColor(Color.RED);
				nextLife.setLocation(0,getHeight()/2);
				add(nextLife);
			}
		}

		//If the player has no lives remaining: prints a loosing message.
		if(bricksRemoved != TOTAL_BRICKS ) {
			if(livesRemaining == 0) {
				GLabel loseMessage = new GLabel ("You have lost");
				loseMessage.setFont("Courier-24");
				loseMessage.setLocation(0, getHeight()/2);
				loseMessage.setColor(Color.RED);
				add(loseMessage);
			}
		}
	}

	//This method returns the object that the ball collided with, if one exists.
	private GObject getCollidingObject() {

		//These variables check the right and bottom of the ball.
		double ballRightX = ballCurrentX + BALL_DIAMETER;
		double ballBottomY = ballCurrentY + BALL_DIAMETER;
		GObject collisionDetector = getElementAt(ballCurrentX, ballCurrentY);
		//Prevents ball from getting stuck on paddle, by returning a null value
		//if the paddle is detected on the top of the ball.
		if(collisionDetector == paddle) {
			collisionDetector = null;
		}

		if(collisionDetector == null) {
			collisionDetector = getElementAt(ballRightX, ballCurrentY);
			//Performs same function as the paddle-null if statement above.
			if(collisionDetector == paddle) {
				collisionDetector = null;
			}
		}
		if(collisionDetector == null) {
			collisionDetector = getElementAt (ballCurrentX, ballBottomY);
		}
		if(collisionDetector == null) {
			collisionDetector = getElementAt (ballRightX, ballBottomY);
		}
		return collisionDetector;
	}

	//Performs most of the function throughout the game by animating the ball, and 
	//controlling the actions of the ball.
	private void bouncingBallMove() {
		double vy = VELOCITY_Y;

		//This gets a new random number to determine initial x velocity, and sets the
		//variables for ball movement.
		double vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;

		while(true) {
			ball.move(vx, vy);
			pause(DELAY);

			//Gets the balls position for  instance variables.
			ballCurrentX = ball.getX();
			ballCurrentY = ball.getY();

			//Switches the ball's horizontal direction if it hits a wall.
			if(ballCurrentX <= 0 || ballCurrentX >= (getWidth()-BALL_DIAMETER)) {
				vx = -vx;
			}

			//Switches the ball's vertical direction if it hits the top wall.
			if(ballCurrentY <= 0) {
				vy = -vy;
			}

			//Breaks out of the loop entirely, proceeding onto the terminateGame() method
			//ending the player's turn if the ball hits the bottom wall.
			if(ballCurrentY >= (getHeight() - (BALL_DIAMETER+1))) {
				remove(ball);
				break;
			}

			//Returns the object the ball collided with if one exists, for the below flow statements.
			GObject collidedObject = getCollidingObject();

			//This variable keeps track of the number of collisions
			double numberofCollisions = 0;

			//The following flow statements determine actions the ball should take
			//if it collides with an object.
			if(collidedObject != null) {

				//Increases the collision count, assuming that the ball did collide with something.
				numberofCollisions = numberofCollisions + 1;

				//Creates an audio sound effect every time the ball hits either a brick or the paddle.
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				bounceClip.play();

				//This if statement increases the Y speed of the ball the more collisions there are,
				//thereby making the game harder as it goes.
				if(numberofCollisions == 20) {
					vy = vy + 0.3;
					numberofCollisions = 0;
				}



				//Bounces the ball upwards if it hits the paddle, also prevents the ball from 
				//getting stuck to the paddle by ensuring the ball's direction is already downward.
				if(collidedObject == paddle && vy > 0) {
					vy = -vy;

					//Defines variables and allows the ball's x direction to reverse if
					//the player hits the ball with the corner of the paddle.
					double cornerShotLeft = MouseX + CORNER_SHOT_LIMIT;
					double cornerShotRight = MouseX + PADDLE_WIDTH - CORNER_SHOT_LIMIT;
					if(ballCurrentX <= cornerShotLeft && vx > 0) {
						vx = -vx;
					}
					if(ballCurrentX >= cornerShotRight && vx < 0) {
						vx = -vx;
					}
				}

				//Assumes the collided object is not the paddle and instead a brick and 
				//therefore removes the brick.  Then it also checks to see if the brick
				//removed was the last brick and if so, breaks out of the loop, calling
				//terminateGame() to congratulate the user.
				if(collidedObject != paddle) {
					remove(collidedObject);
					bricksRemoved = bricksRemoved + 1;
					if (bricksRemoved == TOTAL_BRICKS) {
						break;
					}
					vy = -vy;
				}
			}
		}
	}

	//This method sets up the parameters for the ball.
	private void bouncingBallSetup() {

		//This initial statement is only run on the first turn and removes the initial,
		//welcome message before starting the game.
		if(livesRemaining == NTURNS) {
			remove(welcomeMessage);
		}

		//Defines the position at which the ball should start at the beginning of 
		//every turn.
		double ballStartX = (getWidth()/2.0) - BALL_RADIUS;
		double ballStartY = (getHeight()/2.0) - BALL_RADIUS;
		ball = new GOval (ballStartX,ballStartY,BALL_DIAMETER,BALL_DIAMETER);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);

		//Subtracts a life before beginning the turn. 
		if(livesRemaining != NTURNS) {
			remove(nextLife);
		}
	}


	//This method establishes the process for setting up the various components
	//of the game.
	private void setupGame() {
		setupBrickRows();
		setupPaddle();
		setWelcomeMessages();

	}

	//Creates a welcome message for the player before beginning the game. 
	private void setWelcomeMessages() {
		welcomeMessage = new GLabel ("Welcome to Breakout.  Click to start.");
		welcomeMessage.setLocation(0, getHeight()/2);
		welcomeMessage.setFont("COURIER-18");
		add(welcomeMessage);
	}

	//Keeps track of the paddle's position.
	public void mouseMoved (MouseEvent e) {
		MouseX = e.getX();
		double paddleY = CANVAS_HEIGHT- PADDLE_Y_OFFSET;
		double paddleBoundary = (getWidth() - PADDLE_WIDTH);
		if (MouseX < paddleBoundary) {
			if(MouseX > 0) {
				paddle.setLocation(MouseX, paddleY);
				add(paddle);
			}
		}
	}

	private void setupPaddle() {
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
	}

	//This method sets up the rows of bricks according to the constants defined above.
	private void setupBrickRows() {
		//These 2 variables calculate the starting position of the top-left brick.
		double bricksStartY = BRICK_Y_OFFSET;
		double bricksStartX = (CANVAS_WIDTH- (BRICK_WIDTH * NBRICK_COLUMNS))/2.0;

		//This loop calculates the Y-coordinate of each row
		for (int i = 0 ; i < NBRICK_ROWS ; i++) {
			double rowStartY = bricksStartY + ((i+1) * BRICK_HEIGHT ) + (BRICK_SEP/2.0);

			//This loop creates the rows of bricks.
			for(int j = 0 ; j < NBRICK_COLUMNS ; j++) {
				double brickX = bricksStartX + (BRICK_WIDTH * j) + (BRICK_SEP/2.0);
				GRect brick = new GRect(brickX, rowStartY, BRICK_WIDTH,BRICK_HEIGHT );
				brick.setColor(Color.WHITE);
				add(brick);

				//The following if-statements determine the color of the row of bricks,
				//based upon the predetermined pattern.  
				if(i == 0 || i == 1) {
					brick.setFillColor(Color.RED);
				}
				if(i == 2 || i == 3) {
					brick.setFillColor(Color.ORANGE);
				}
				if(i == 4 || i == 5) {
					brick.setFillColor(Color.YELLOW);
				}
				if(i == 6 || i == 7) {
					brick.setFillColor(Color.GREEN);
				}
				if(i == 8 || i == 9) {
					brick.setFillColor(Color.CYAN);
				}
				brick.setFilled(true);
			}
		}
	}
}
