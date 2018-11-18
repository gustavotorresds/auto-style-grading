/*
 * File: Breakout.java
 * -------------------
 * Name: Lizbeth Gomez
 * Section Leader: Garrick Fernandez
 * Date: February 2, 2018
 * 
 * This file will implement the game of Breakout.
 * The user will have three tries to win the game. This is done by 
 * getting rid of all the bricks in the game by using the paddle and 
 * ball to destroy the bricks. If the user loses or wins it will be
 * outputed to the screen.
 * Sources: Looked at Mouse Tracker Soloution from lecture for help on moving paddle. 
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

	//The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	//The ball's minimum and maximum horizontal velocity; the bounds of the
	//initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;                        
	public static final double VELOCITY_X_MAX = 3.0;                         

	//Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;                       


	// Instance variables for the brick 
	private GRect brick;
	double brickSpaceInPixels = (NBRICK_COLUMNS/2 - 0.5) * BRICK_SEP;
	double xBrick = (CANVAS_WIDTH/2 - (BRICK_WIDTH*(NBRICK_COLUMNS/2))- brickSpaceInPixels);
	double yBrick = BRICK_Y_OFFSET;
	int newRow;

	// keeps track of bricks present during game
	int bricksPresent = NBRICK_COLUMNS*NBRICK_ROWS;

	//Dimensions of the paddle
	public static final double PADDLE_WIDTH = 80; //60
	public static final double PADDLE_HEIGHT = 30; // 10

	//Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Instance variables for the paddle
	private GRect paddle;
	double yPaddle = (CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);


	// Instance Variables for the ball
	//Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;
	private GOval ball = null;

	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// keeps track of when the game should end
	boolean gameOver; 

	// Instance variable for a "Game Over!" label
	GLabel label = null;

	//Number of turns 
	public static final int NTURNS = 3;



	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//set up a loop for three tries
		for ( int i = 0; i < NTURNS; i++) {
			// if there is a label remove it before start of game 
			if ( label != null) {
				remove(label);
			}

			setUpGame();
			playGame(); 
			gameOver = !gameOver; // resets the game
		}
	}


	/**
	 * Method: Set Up Of Game
	 * ----------------------
	 * This method sets up the objects that are needed for the game.
	 * For example, creating all the bricks, the paddle, and the ball.
	 */
	private void setUpGame() {
		xBrick = (getWidth()/2 - (BRICK_WIDTH*(NBRICK_COLUMNS/2))- brickSpaceInPixels); // resets the x coord
		yBrick = BRICK_Y_OFFSET; // resets the y coor for bricks 
		drawAllBricks();
		paddle = makePaddle();
		initialLocationPaddle();
		ball = drawBall();
		addMouseListeners();
	}	

	/**
	 * Method: Draw Ball
	 * -----------------
	 * This method will create a blue ball and center it in the middle of the 
	 * screen and return the ball for later use.
	 */
	private GOval drawBall() {
		double xBall = getWidth()/2- BALL_RADIUS; //x coordinate
		double yBall = getHeight()/2 - BALL_RADIUS; // y coordinate
		ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2 );
		ball.setFilled(true);
		ball.setColor(Color.BLUE);
		add (ball, xBall, yBall);
		return ball;
	}

	/**
	 * Method: Play Game
	 * -----------------
	 * This method makes an animation loop that runs the whole motions of the 
	 * game by calling other methods. It also lets user know when its game over.
	 */ 
	private void playGame() {
		waitForClick();
		while ( !gameOver) {
			moveBall();
			if (gameOver) { // game over if brick hits bottom 
				removeAll();
				// tell user game is over
				label = new GLabel("Game Over!");
				double x = getWidth()/2 - label.getWidth()/2;
				double y = getHeight()/2 + label.getAscent()/2;
				label.setColor(Color.RED);
				add (label,x, y );
			}

		}
	}


	/**
	 * Method: Check For Collisions
	 * ----------------------------
	 * This method calls on the getColliding object to store the
	 * object as "collider". It will then pass that abject to method 
	 * resultOfCollisions.
	 * 
	 */
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		resultOfCollisions(collider);
	}


	/**
	 * Method: Result Of Collisions
	 * ----------------------------
	 * This method takes in the address of the object that collided with the ball.
	 * Then it tests whether its the paddle or a brick. If its the paddle then it will 
	 * bounce off it. If its a brick it will remove it and bounce off it.  
	 */
	private void resultOfCollisions(GObject collider) {
		// checks whether collider is a brick or paddle
		if (collider == paddle) {
			//if it hits top 
			if (vy > 0) {
				vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
				if (rgen.nextBoolean (0.5)) vx = -vx; // change to random
				vy = -vy;
			}
		} else if (collider != null) { 
			bricksPresent--; // keeps track of number of bricks in game
			remove(collider);
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean (0.5)) vx = -vx; // change to random 
			vy = -vy;
		}

	}


	/**
	 * Method: Get Colliding Object
	 * ----------------------------
	 * This method checks all four corners of the ball and finds if there are 
	 * any collisions.If there are collisions it returns the object's address 
	 * for later use.
	 */
	private GObject getCollidingObject() {
		double xCoor = ball.getX();
		double yCoor = ball.getY();
		GObject object = null;

		if (getElementAt(xCoor, yCoor) != null) { // checks top left corner
			object = getElementAt(xCoor, yCoor);
		} 
		else if (getElementAt(xCoor+2*BALL_RADIUS, yCoor) != null){ // checks top right corner
			object = getElementAt(xCoor+2*BALL_RADIUS, yCoor);
		} 
		else if (getElementAt(xCoor, yCoor+2*BALL_RADIUS) != null){ // checks bottom left corner
			object = getElementAt(xCoor, yCoor+2*BALL_RADIUS);
		}
		else if (getElementAt(xCoor+2*BALL_RADIUS, yCoor+2*BALL_RADIUS) != null){ // checks bottom left corner
			object = getElementAt(xCoor+2*BALL_RADIUS, yCoor+2*BALL_RADIUS);
		}
		return object;
	}


	/**
	 * Method: Move Ball
	 * -----------------
	 * This method moves the ball by updating the velocity direction when 
	 * the ball has a collision. It also outputs whether a player has won.
	 */
	private void moveBall() {
		// y velocity does not change
		vy = VELOCITY_Y;

		// create animation loop
		while (!gameOver) {
			checkForCollisions();

			//If ball hits left or right wall update the 
			//velocity coordinates of the ball
			if ( ballHitsLeftWall() || ballHitsRightWall()) {
				vx = -vx;
			}
			// If Ball hits top wall
			if (ballHitsTopWall()) {
				vy = -vy;
				vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
				if (rgen.nextBoolean (0.5)) vx = -vx;
			}
			// make game over If ball hits bottom
			if (ballHitsBottomWall()) { 
				gameOver = true; 
				remove(ball);
			}

			// update the actual ball 
			ball.move(vx, vy);

			// pause so it can be seen
			pause(DELAY);

			// outputs whether player has won
			youWon(); 
		}


	}


	/**
	 * Method: You Won
	 * ---------------
	 * This method checks when there are no bricks on screen. If there 
	 * aren't any then it removes all objects,and outputs a "You won!" 
	 * message to user.
	 */
	private void youWon() {
		if (bricksPresent == 0) {
			removeAll();
			// Tell user they won
			GLabel labelWin = new GLabel("You Won!");
			double x = getWidth()/2 - labelWin.getWidth()/2;
			double y = getHeight()/2 + labelWin.getAscent()/2;
			labelWin.setColor(Color.GREEN);
			add (labelWin,x, y );
		}
	}


	/**
	 * Method: Ball hits Bottom Wall
	 * -----------------------------
	 * This method tests if the ball hits the bottom wall
	 * and returns true.
	 */
	private boolean ballHitsBottomWall() {
		return (ball.getY() > getHeight()- ball.getWidth());
	}


	/**
	 * Method: Ball Hits Top Wall
	 * --------------------------
	 * This method tests if the ball hits the top wall
	 * and returns true.
	 */
	private boolean ballHitsTopWall() {
		return (ball.getY() <= 0);
	}


	/**
	 * Method: Ball Hits Right Wall
	 * --------------------------
	 * This method tests if the ball hits the right wall
	 * and returns true.
	 */
	private boolean ballHitsRightWall() {
		return (ball.getX() >= getWidth() - ball.getWidth());
	}


	/**
	 * Method: Ball Hits Left Wall
	 * --------------------------
	 * This method tests if the ball hits the left wall
	 * and returns true.
	 */
	private boolean ballHitsLeftWall() {
		return (ball.getX() <= 0);
	}


	/**
	 * Method: Mouse Moved
	 * -------------------
	 * This method is called by addMouseListeners and updates the paddle's
	 * location every time the user moves the mouse. 
	 */
	public void mouseMoved (MouseEvent e) {
		double xPaddle = e.getX() - PADDLE_WIDTH/2; // centers mouse on paddle
		if ( xPaddle >= 0 && xPaddle < getWidth() - PADDLE_WIDTH) { // adds bounds to the paddle
			paddle.setLocation(xPaddle, yPaddle);
		}
	}


	/**
	 * Method: Make Paddle
	 * -------------------
	 * This method creates the paddle with a specified width and height
	 * and returns it
	 */
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}


	/**
	 * Method: Initial Location Paddle
	 * -------------------------------
	 * This method places the paddle at the bottom center for 
	 * the start of game. 
	 */
	// places paddle at bottom center
	private void initialLocationPaddle() {
		double xPaddle = (getWidth()/2 - PADDLE_WIDTH/2);
		double yPaddle = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		add (paddle,xPaddle, yPaddle);
	}


	/**
	 * Method: Draw All Bricks
	 * -----------------------
	 * This method draws and centers all the bricks needed for the game.
	 * It creates a specified number of bricks in each row. The number 
	 * of bricks in each row and the number of rows are given by constants
	 * defined at the top of program.
	 */
	private void drawAllBricks() {
		// Print all rows of bricks
		for (newRow = 0; newRow < NBRICK_ROWS; newRow++) {
			xBrick = (getWidth()/2 - (BRICK_WIDTH*(NBRICK_COLUMNS/2))- brickSpaceInPixels); // reset xCoor to the beginning
			createRowOfBricks(); // print out row
			yBrick = yBrick + BRICK_HEIGHT + BRICK_SEP; // adjust y coordinate
		}
	}


	/**
	 *  Method: Create Row Of Bricks
	 *  ----------------------------
	 *  This method will create a row of bricks
	 *  that is centered in the screen.
	 */
	private void createRowOfBricks(){
		for (int row = 0; row < NBRICK_COLUMNS; row++) {
			outputBrick(xBrick, yBrick, BRICK_WIDTH,BRICK_HEIGHT);
			xBrick = xBrick + BRICK_WIDTH + BRICK_SEP;
		}
	}


	/**
	 * Method: outputBrick
	 * -------------------
	 * This method creates a brick from parameters given from 
	 * createRowOfBricks method.
	 */
	private GRect outputBrick(double xCoor, double yCoor, double brickWidth, double brickHeight) {
		brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT); 
		brick.setFilled(true);
		findColorOfNewRow();
		add (brick, xCoor, yCoor);
		return brick;
	}


	/**
	 * Method: findColorOfNewRow()
	 * ---------------------------
	 * This method will find the color of each row and then 
	 * set it up for each row of bricks. It is returned to the
	 * outputBrick method
	 */
	private void findColorOfNewRow() {
		//change color of rows: RED, ORANGE, YELLOW, GREEN, CYAN
		if ((newRow % 10) == 0 || (newRow % 10) == 1 ) { 
			brick.setColor(Color.RED); 
		} else if ((newRow % 10) == 2 || (newRow % 10) == 3) {
			brick.setColor(Color.ORANGE);
		} else if ((newRow % 10) == 4 || (newRow % 10) == 5) {
			brick.setColor(Color.YELLOW);
		} else if ((newRow % 10) == 6 || (newRow % 10) == 7) {
			brick.setColor(Color.GREEN);
		} else if ((newRow % 10) == 8 || (newRow % 10) == 9) {
			brick.setColor(Color.CYAN);
		}	
	} 
}

