/*
 * File: Breakout.java
 * -------------------
 * Name: Susy Manrique
 * Section Leader: Julia Daniel
 * 
 * This file implements the game of Breakout.
 * The objective is to eliminate all the bricks
 * before you lose all your lives.
 *  
 * Extensions: click-to-play again feature,
 * welcome screen, winning and losing messages
 * displayed, and audio clip when ball hits
 * something other than a wall.
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

	//number of bricks on the screen
	private int brickCount = 0;

	//number of balls left
	private int ballCount;

	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GObject collider;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();
		
		//loop forever
		while(true) {
			setUpGame(); //lay bricks and place paddle
			playGame(); //add ball and play game until win or lose
			waitForClick(); //"click to play again"
			removeAll();	//clear canvas
		}

	}

	/*
	 * Display Play Game
	 * -----------------------
	 * Reinitializes ballCount, starts the ball
	 * moving and crashing into things, checks
	 * for collisions and appropriate responses,
	 * and displays and end-of-game exit message.
	 */
	private void playGame() {
		ballCount = NTURNS;
		initializeBall(); //ball starts moving
		checkForCollisions(); //check ball for collisions until game ends
		clearAndDisplayExitMessage(); //clear canvas and display end-of-game message
	}

	/*
	 * Check for Collisions
	 * -----------------------
	 * Does the ball hit anything other than a wall?
	 * This method finds out, and acts according to
	 * what the ball collides with. If the ball collides
	 * with a brick, it breaks it and subtracts from
	 * the brick count. If it collides with a paddle, 
	 * it just bounces off. (Move around while checking
	 * for collisions)
	 */
	private void checkForCollisions() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		while (ballCount > 0 && brickCount!=0) {
			//collisions
			collider = getCollidingObject();
			if (collider != null ) {
				if (collider != paddle) {
					brickCount= brickCount - 1;	
					remove(collider);
				} 
				vx=-vx;
				vy=-vy;
				bounceClip.play();
			}

			checkIfBallHitsWalls();

			// update movement
			ball.move(vx, vy);
			pause(DELAY);

		}		
	}
	
	/*
	 * Check If Ball Hits Walls
	 * -----------------------
	 * Checks to see if ball is hitting a wall, and
	 * changes velocity/balls left accordingly
	 */

	private void checkIfBallHitsWalls() {
		// update velocity if ball hits wall
		if(ballHitsLeftWall() || ballHitsRightWall()) {
			vx = -vx;
		}
		if(ballHitsTopWall()) {
			vy = -vy;
		}
		//if ball hits bottom, remove a life and reinitialize ball
		if ( ballHitsBottomWall()) {
			remove(ball);
			ballCount = ballCount - 1;
			if (ballCount > 0) {
				initializeBall();
			}
		}		
	}

	
	/*
	 * Method: clear And Display Message
	 * -----------------------
	 * Game has ended, so screen is cleared.
	 * A different label is displayed, depending
	 * on whether the player won or lost the game.
	 */
	private void clearAndDisplayExitMessage() {
		removeAll();	//clear canvas
		GLabel label = new GLabel("");
		if (ballCount == 0) {
			//display "you lose"
			label.setLabel("You lose! Click anywhere to play again. Close the screen to exit");
		}
		if (brickCount == 0) {
			//display "you win!"
			label.setLabel("Congratulations, you win! Click anywhere to play again. Close the screen to exit");
		}
		label.setLocation(getWidth()/2 - label.getWidth()/2,BRICK_Y_OFFSET);
		add(label);		
	}


	/*
	 * Method: Initialize Ball
	 * -----------------------
	 * Create ball at center of the screen. When
	 * the screen is clicked, ball downward, and
	 * in a random X location at a random X velocity.
	 */
	private void initializeBall() {
		//place ball on center of paddle
		createBall();
		//mouse clicked +> if clicked, release ball
		waitForClick();
		//initialize velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;		
	}

	/*
	 * Method: Get Colliding Object
	 * -----------------------
	 * Returns the object with with the ball collided with.
	 * If the ball did not/has not collided with anything,
	 * the method returns null 
	 */
	private GObject getCollidingObject() {
		//if ball hits something with top left corner
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			return (getElementAt(ball.getX(),ball.getY()));
		}
		//if ball hits something with bottom left corner
		if (getElementAt(ball.getX(), ball.getBottomY()) != null) {
			return (getElementAt(ball.getX(), ball.getBottomY()));
		}
		//if ball hits something with top right corner
		if (getElementAt(ball.getRightX(),ball.getY()) != null) {
			return (getElementAt(ball.getRightX(),ball.getY()));
		}
		//if ball hits something with bottom right corner
		if (getElementAt(ball.getRightX(), ball.getBottomY()) != null) {
			return (getElementAt(ball.getRightX(), ball.getBottomY()));
		} else {
			return null;
		}
	}

	/*
	 * Method: Hit Left Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the left wall of the window.
	 */
	private boolean ballHitsLeftWall() {
		return ball.getX() <= 0;
	}

	/*
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the bottom wall of the window.
	 */
	private boolean ballHitsBottomWall() {
		return ball.getY() > getHeight() - BALL_RADIUS*2;
	}

	/*
	 * Method: Hit Top Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean ballHitsTopWall() {
		return ball.getY() <= 0;
	}

	/*
	 * Method: Hit Right Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the right wall of the window.
	 */
	private boolean ballHitsRightWall() {
		return ball.getX() >= getWidth() - BALL_RADIUS*2;
	}

	/*
	 * Method: Create Ball
	 * -----------------------
	 * sets ball in middle of screen
	 */
	private void createBall() {
		double initialBallX = (getWidth()/2) - (BALL_RADIUS);
		double initialBallY = (getHeight()/2 - BALL_RADIUS*2);
		ball = new GOval (initialBallX, initialBallY, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);			
	}

	/*
	 * Method: Set Up Game
	 * -----------------------
	 * Displays welcome screen, lays bricks,
	 * and creates paddle
	 */
	private void setUpGame() {
		displayWelcomeScreen();
		setUpBricks();
		createPaddle();
	}

	/*
	 * Display Welcome Screen
	 * -----------------------
	 * Welcomes user/player to Breakout game
	 */
	private void displayWelcomeScreen() {
		GLabel label = new GLabel("Welcome to Breakout! Click anywhere on the screen to start.");
		label.setLocation(getWidth()/2 - label.getWidth()/2,BRICK_Y_OFFSET);
		add(label);
		waitForClick();
		removeAll();	//clear canvas
	}

	/*
	 * Display Create Paddle
	 * -----------------------
	 * Adds paddle to screen at initial location (center
	 * in x direction, with a given y offset in the y direction)
	 */
	private void createPaddle() {
		double initialPaddleX = ((getWidth()/2) -(PADDLE_WIDTH/2));
		double PaddleY = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle = new GRect (initialPaddleX, PaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);;//will be global variable so it can be changed in a different method
		paddle.setFilled(true);
		add(paddle);		
	}

	/*
	 * Method: Set up Bricks
	 * -----------------------
	 * lays bricks by row from top to bottom
	 */
	private void setUpBricks() {
		for	(int i=0; i< NBRICK_ROWS; i++) {
			Color rowColor = whichColor(i);
			//account for space between bricks and space from the top
			double rowYCoord = (i*BRICK_HEIGHT) + BRICK_Y_OFFSET + (i*BRICK_SEP);
			drawRow(rowYCoord, rowColor);
		}
	}

	/*
	 * Method: Which Color
	 * -----------------------
	 * decides which row gets which color
	 * according to this color pattern:
	 *  RED, ORANGE, YELLOW, GREEN, CYAN.
	 *  Each color gets two rows before
	 *  going on to the next color.
	 */
	private Color whichColor(int rowNumi) {
		Color c = Color.BLACK;
		if(rowNumi%10 < 10){
			c = Color.CYAN;
		}
		if(rowNumi%10 < 8){
			c = Color.GREEN;
		}
		if(rowNumi%10 < 6){
			c = Color.YELLOW;
		}
		if(rowNumi%10 < 4){
			c = Color.ORANGE;
		}
		if(rowNumi%10 < 2){
			c = Color.RED;
		} 
		return c;
	}

	/*
	 * Method: Draw Row
	 * -----------------------
	 * Draws row of bricks, one by one,
	 * from left to right. For each brick
	 * it adds, the method also adds one to
	 * the brick Count of the number of bricks on the screen
	 */
	private void drawRow(double rowYCoord, Color rowColor) {
		double brickXCoord;
		//center in x direction
		double brickXOffset = (getWidth() - (((NBRICK_COLUMNS)*BRICK_WIDTH) + ((NBRICK_COLUMNS-1)*BRICK_SEP)))/2.0;
		//for each brick, set x coord, add brick, and count how many bricks on the screen
		for (int i = 0; i < NBRICK_COLUMNS; i++) { 
			brickXCoord = brickXOffset + (i*BRICK_WIDTH) + ((i) *BRICK_SEP); //set the x coordinate of the location of each brick
			GRect brick = new GRect(brickXCoord,rowYCoord, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFillColor(rowColor);
			brick.setColor(rowColor);
			brick.setFilled(true);
			add(brick);
			brickCount = brickCount + 1;
		}
	}

	/*
	 * Method: Mouse Moved
	 * -----------------------
	 * The paddle follows the mouse (in the
	 * x direction only), but the paddle does
	 * not go off the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		//move paddle according to x-coordinate movements of mouse events
		double mouseX = e.getX();
		double paddleX = mouseX - PADDLE_WIDTH/2;
		//don't let the paddle move off the screen
		if (mouseX < PADDLE_WIDTH/2) {
			paddleX = 0;
		}
		if (mouseX > getWidth() - (PADDLE_WIDTH/2)) {
			paddleX = getWidth() - (PADDLE_WIDTH);
		}
		paddle.setX(paddleX);
	}

}
