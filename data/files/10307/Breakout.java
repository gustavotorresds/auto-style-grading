/*
 * File: Breakout.java
 * -------------------
 * Name: Johannes Hui
 * Section Leader: Thariq Ridha
 * 
 * This file will eventually implement the game of Breakout.
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
	
	//Color pattern resets every 10 rows
	public static final int COLOR_CYCLE = 10;

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
	public static final double DELAY = 1000.0 / 150.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//Variable to create paddle
	private GRect paddle = null;
	
	//Variable to create ball
	private GOval ball = null;
	
	//Variable to track x and y components of ball velocity
	private double vx, vy;
	
	//Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Number of bricks in game
	private int brickCounter = NBRICK_ROWS * NBRICK_COLUMNS;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setup();
		play();
	}

	//Sets up game by adding bricks and paddle
	private void setup() {
		addBricks();
		addPaddle();
	}

	//Adds all bricks to the canvas
	private void addBricks() {
		Color color = null;
		
		//Sets color for each row of bricks
		//Color changes every two rows and cycles through 5 colors 
		for (int i = 0; i < NBRICK_ROWS; i++) {
			int rowColorCounter = i % COLOR_CYCLE;
			if (rowColorCounter < 2) {
				color = Color.RED;
			} else if (rowColorCounter < 4) {
				color = Color.ORANGE;
			} else if (rowColorCounter < 6) {
				color = Color.YELLOW;
			} else if (rowColorCounter < 8) {
				color = Color.GREEN;
			} else {
				color = Color.CYAN;
			}
	
		//Adds row of bricks of given color and at given row number
		addBrickRow(color, i);
		}
	}

	//Adds row of bricks given color and row number (relative to rest of bricks)
	private void addBrickRow(Color color, int rowNumber) {
		
		//Adds same number of bricks to the row as the number of columns of bricks
		for (int j = 0; j < NBRICK_COLUMNS; j++) {
			
			//Sets x location for each brick added
			double xAlign = BRICK_SEP + (BRICK_WIDTH + BRICK_SEP) * j;
			//Sets y location for each brick added
			double yAlign = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * rowNumber;
			
			//Defines brick, sets its, and adds it to the canvas 
			GRect brick = new GRect (xAlign, yAlign, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setFillColor(color);
			brick.setColor(color);
			add (brick);
		}
	}

	//Adds paddle to the canvas
	private void addPaddle() {
		addMouseListeners();
		
		//Defines paddle and sets initial location at horizontal center of canvas 
		paddle = new GRect (getWidth() / 2 + PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);
	}

	//Moves paddle to mouse x location
	public void mouseMoved(MouseEvent e) {
		
		//Places center of paddle at mouse x location
		double paddleX = e.getX() - PADDLE_WIDTH / 2;
	
		//Stops paddle from moving out of the canvas
		if (paddleX > 0 && paddleX < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(paddleX, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	/* 
	 * Plays game for number of turns specified by NTURNS.
	 * Displays message after player wins by removing all bricks or loses by failing to after NTURNS number of turns.
	 */ 
	private void play() {
		
		//Ends game after NTURNS number of turns
		for (int i = 0; i < NTURNS; i++) {		
			
			//Checks if player has knocked out all bricks and won the game
			if (brickCounter != 0) {
				ball = addBall();
				
				//Sets x and y velocity of the ball. X velocity is randomized
				vx = rgen.nextDouble(1.0, 3.0);
				//Creates equal probability of the ball going towards the left or right side
				if (rgen.nextBoolean(0.5)) vx = -vx;
				vy = -VELOCITY_Y;
				
				//Begins one round of the game after user clicks anywhere on the canvas
				waitForClick();
				playOneRound();
			}
		}
		//Displays win message if no bricks remaining
		if (brickCounter == 0) {
			displayWinMessage();
		//Displays lose message if bricks remaining and there are no turns remaining
		} else {
			displayLoseMessage();
		}
	}

	
	// Returns ball at the x-center of the canvas right above the paddle. No ball is added to the canvas yet.
	public GOval addBall() {
		
		//Diameter of the ball
		double d = BALL_RADIUS * 2;
		
		//Initial y location of ball
		double ballYStart = getHeight() - PADDLE_Y_OFFSET - d;
		
		GOval ball = new GOval (d, d);
		add (ball, getWidth()/2, ballYStart);
		return (ball);
	}
	
	/* 
	 * Plays one round of the Breakout game
	 * Ends round if ball hits the bottom wall or there are no breaks remaining
	 * 
	 * Pre: Ball is on canvas at the x-center above the paddle line
	 * Post: Ball is removed from the canvas
	 */
	private void playOneRound() {
		
		//Plays one round until ball hits the bottom wall or there are no bricks remaining
		while(!hitBottomWall() && brickCounter != 0) {
			
			//Bounces ball in the right direction upon hitting a wall, paddle, or brick
			if (hitLeftWall() || hitRightWall()) {
				vx = -vx;
			}
			if (hitTopWall()) {
				vy = -vy;
			}
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				bouncePaddle();
			} else if (collider != null) {
				bounceBrick();
				//Removes brick if ball hits a brick
				remove (collider);
				//Keeps track of the number of bricks remaining in the game. Subtracts one after one brick is removed
				brickCounter--;
			}
			
			//Animates the ball by moving the ball by x and y velocity before pausing
			ball.move(vx, vy);
			pause(DELAY);
		}
		remove (ball);
	}

	//Evaluates true if ball hits the left wall
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	//Evaluates true if ball hits the right wall
	private boolean hitRightWall() {
		return ball.getX() + BALL_RADIUS * 2 >= getWidth();
	}

	//Evaluates true if ball hits the top wall
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	//Evaluates true if ball hits the bottom wall
	private boolean hitBottomWall () {
		return ball.getY() + BALL_RADIUS * 2 >= getHeight();
	}

	//Returns object that the ball hits
	private GObject getCollidingObject() {
		
		//Horizontal (x) center of the ball
		double ballXCenter = ball.getX() + BALL_RADIUS;
		//Vertical (y) center of the ball
		double ballYCenter = ball.getY() + BALL_RADIUS;
		
		//Object that the ball hits
		GObject collider = null;
		
		/*
		 * Checks it the ball has hit an object
		 * Checks twelve positions on a circle with radius BALL_RADIUS + 1 and returns the object if an object is detected
		 * Uses BALL_RADIUS + 1 so it will not return the ball itself
		 * The twelve positions are spaced 30 degrees apart
		 */		
		for (int i = 0; i < 12; i++) {
			collider = getElementAt(ballXCenter + (BALL_RADIUS + 1) * Math.cos(i * 30), ballYCenter + (BALL_RADIUS + 1) * Math.sin(i * 30));
			//Returns object hit by ball if an object is detected
			if (collider != null) {
				return collider;
			}
		}
		//Returns null if the ball does not hit an object
		return collider;
	}

	//Bounces ball off the paddle when they collide
	private void bouncePaddle() {
		
		//Horizontal (x) center of the ball
		double ballXCenter = ball.getX() + BALL_RADIUS;
		//Vertical (y) center of the ball
		double ballYCenter = ball.getY() + BALL_RADIUS;
		
		//Sets y velocity of ball to move upwards if ball hits the exact bottom of the ball
		//Uses BALL_RADIUS + 1 as the getCollidingObject() checks for collisions 1 pixel outside of the ball's radius
		if (getElementAt(ballXCenter, ballYCenter + BALL_RADIUS + 1) == paddle) {
			vy = -VELOCITY_Y;
		
		//Sets y velocity of ball to move upwards and reverses x velocity if bottom portion of the ball (besides the exact bottom) hits the paddle
		//Allows ball to bounce of side corners of the paddle in a more natural way
		} else if (bounceSidePaddle()) {
			vx = -vx;
			vy = -VELOCITY_Y;
		}
	}

	//Evaluates to true if ball hits bottom quarter (90 degrees) of the ball
	private boolean bounceSidePaddle() {
		
		//Horizontal (x) center of the ball
		double ballXCenter = ball.getX() + BALL_RADIUS;
		//Vertical (y) center of the ball
		double ballYCenter = ball.getY() + BALL_RADIUS;
	
		/*
		 * Returns true if ball hits bottom quarter of the ball
		 * Checks four positions located on a quarter circle of radius BALL_RADIUS + 1
		 * Uses circle of radius BALL_RADIUS + 1 as getCollidingObject() method detects collisions by checking at this distance from the ball
		 * All four positions are within 45 degrees of the exact bottom of the center 
		 */
		for (int i = 0; i < 4; i++) {
			if (getElementAt(ballXCenter - (BALL_RADIUS + 1) * Math.cos(45 + i * 30), ballYCenter - (BALL_RADIUS + 1) * Math.sin(45 + i * 30)) == paddle) {
				return true;
			}
		}
		//Returns false if bottom quarter of ball does not hit the paddle
		return false;
	}

	//Bounces ball off brick when they collide
	private void bounceBrick() {
		//Reverses x velocity of ball if exact left or right side of ball hits a brick
		if (bounceXBrick()) {
			vx = -vx;
		//Reverse y velocity of ball if exact top or bottom side of ball hits a brick
		} else if (bounceYBrick()) {
			vy = -vy;
		//Reverses x and y velocity of ball if ball hits brick anywhere else on the ball
		} else {
			vx = -vx;
			vy = -vy;
		}
	}

	//Evaluates true if ball hits brick on exact left or right side of ball
	//Checks for brick 1 pixel out from edge of ball as getCollidingObject() method detects collisions by checking at this distance from the ball 
	private boolean bounceXBrick() {
	
		//x location of ball
		double ballX = ball.getX();
		//Vertical (y) center of ball
		double ballYCenter = ball.getY() + BALL_RADIUS;
		
		//Returns true if ball hits brick on exact left side of ball
		if (getElementAt (ballX - 1, ballYCenter) != null) {
			return true;
		//Returns true if ball hits brick on exact right side of ball 
		} else if (getElementAt (ballX + BALL_RADIUS * 2 + 1, ballYCenter) != null) {
			return true;
		//Returns false if brick does not hit ball on its exact left or right side 
		} else {
			return false;
		}
	}

	//Evaluates true if ball hits brick on exact top or bottom side of ball
	//Checks for brick 1 pixel out from edge of ball as getCollidingObject() method detects collisions by checking at this distance from the ball 
	private boolean bounceYBrick() {
		
		//Horizontal (x) center of the ball
		double ballXCenter = ball.getX() + BALL_RADIUS;
		//y location of the ball
		double ballY = ball.getY();
		
		//Returns true if ball hits brick on exact top side of ball
		if (getElementAt (ballXCenter, ballY - 1) != null) {
			return true;
		//Returns true if ball hits brick on exact bottom side of ball
		} else if (getElementAt (ballXCenter, ballY + BALL_RADIUS * 2 + 1) != null) {
			return true;
		//Returns false if brick does not hit ball on its exact top or bottom side
		} else {
			return false;
		}
	}

	//Displays message if player wins
	private void displayWinMessage() {
		GLabel winMessage = new GLabel ("Congratulations, you have won!");
		//x location to center the label on the canvas
		double labelX = getWidth() / 2 - winMessage.getWidth() / 2;
		add(winMessage, labelX, BRICK_Y_OFFSET / 2);
	}
	
	//Displays message if player loses
	private void displayLoseMessage() {
		GLabel loseMessage = new GLabel ("Game over. Thanks for playing!");
		//x location to center the label on the canvas
		double labelX = getWidth()/2 - loseMessage.getWidth() / 2;
		add(loseMessage, labelX, BRICK_Y_OFFSET / 2);
	}
}
