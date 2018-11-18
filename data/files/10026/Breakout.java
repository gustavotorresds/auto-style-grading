/*
 * File: Breakout.java
 * -------------------
 * Jiya Janowitz
 * Section Leader: Ben Allen
 * Assignment 3, Breakout
 * This graphics program runs the classic arcade game of Breakout. The run method sets up
 * the game, then waits for the user to click to start the game. The movements of the user's
 * mouse are used to control the paddle. The game ends when the player either uses all of 
 * their lives and loses the game or breaks every brick and wins. 
 * Cite Sources: None
 * ----------------------
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
	public static final double DELAY = 800.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	private GRect paddle = null;
	private GOval ball = null;
	private double xV = 0;
	private double yV = 0;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double xPaddle = 0.0;
	private double yPaddle = 0.0;
	private int bricksRemaining = NBRICK_COLUMNS *NBRICK_ROWS;
	
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		xPaddle = (getWidth()-PADDLE_WIDTH)/2;
		yPaddle = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		
		createGame();
		waitForClick();
		addMouseListeners();
		playGame();
	}
	
	/*
	 *  This method runs the actual gameplay. While the game is still in progress, ie
	 * the player has lives and bricks remaining, it runs through a movement loop.
	 * There are special instructions for the ball bouncing off walls, bricks, and 
	 * the paddle. Once the game ends, the screen displays a message appropriate to
	 * games outcome.
	 */
	private void playGame() {
		int numLives = NTURNS;
		makeBall();
		findBallStartVelocity();
		while(bricksRemaining > 0 && numLives > 0) {
			shootBall();
			if (ball.getY() > getHeight()) {
				remove(ball);
				numLives--;
				if(numLives > 0) {
					makeBall();
				}
				waitForClick();
				findBallStartVelocity();
				shootBall();
			}
		}
		if (bricksRemaining == 0) {
			GLabel winLabel = new GLabel("Congrats, you win!");
			double labelPosXw = (getWidth() - winLabel.getWidth())/2;
			double labelPosYw = 3*getHeight()/4;
			add(winLabel, labelPosXw, labelPosYw);
		}
		if (numLives ==0) {
			GLabel lossLabel = new GLabel("Booo, you lose.");
			double labelPosXl = (getWidth() - lossLabel.getWidth())/2;
			double labelPosYl= 3*getHeight()/4;
			add(lossLabel, labelPosXl, labelPosYl);
		}
	}
	
	//Creates the initial game setup with the brick array and paddle's starting position.
	private void createGame() {
		buildBrickGrid();
		buildPaddle(xPaddle);
	}
	
	/* 
	 * Method chooses a random firing direction for the ball at the start of a turn. The ball
	 * is fired towards the bottom of the screen with a randomly generated x velocity between
	 * the predetermined bounds.
	 */
	private void findBallStartVelocity() {
		xV = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) xV = -xV;
		yV = VELOCITY_Y;
	}
	
	/*
	 * This is the bread and butter of the entire active game. This method gives the ball
	 * directions for when it hits walls, bricks, or the paddle. This method also keeps 
	 * track of the bricks remaining.
	 */
	private void shootBall() {
		ball.move(xV, yV);
		
		if (hitRightWall() || hitLeftWall()) {
			xV = -xV;
		}
		if (hitTopWall()) {
			yV = -yV;
		}
		
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			yV = Math.abs(yV);
		} else if (collider !=null) {
			yV=-yV;
			remove(collider);
			bricksRemaining--;
		}
		pause(DELAY);
	}

	//Determines if the ball hit the top wall
	private boolean hitTopWall() {
		return ball.getY() <=0;
	}

	//Determines if the ball hit the right wall
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - 2*BALL_RADIUS;
	}
	
	//Determines if the ball hit the left wall
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	/*
	 * Method tracks the position of the mouse, and has the paddle follow it. The 
	 * paddle is not allowed to venture off of the screen.
	 */
	public void mouseMoved(MouseEvent e) {
		double xPad = e.getX();
		if (xPad  >= getWidth()-PADDLE_WIDTH) {
			xPad = getWidth()-PADDLE_WIDTH;
		} else if (xPad == -getWidth()) {
			xPad = -getWidth();
		}
		paddle.setLocation(xPad, yPaddle);
	}
	
	/*
	 * This method detects collisions that the ball has with its surroundings. This method
	 * is used to determining when the ball makes contact with either the paddle or one of 
	 * the bricks. This process works by constantly determining whether or not any of the
	 * four corners of the ball are touching an object, and then returning that object.
	 */
	private GObject getCollidingObject() {
		GObject strickenObject = null;
		double ballX = ball.getX();
		double ballY = ball.getY();
		
		GObject upLeft = getElementAt (ballX, ballY);
		GObject upRight = getElementAt (ballX + 2*BALL_RADIUS, ballY);
		GObject downLeft = getElementAt (ballX + 2*BALL_RADIUS, ballY);
		GObject downRight = getElementAt (ballX + 2*BALL_RADIUS, ballY + 2*BALL_RADIUS);
		
		if (upLeft !=null) {
			strickenObject = upLeft;
		} else if (upRight !=null) {
			strickenObject = upRight;
		} else if (downLeft !=null) {
			strickenObject = downLeft;
		} else if (downRight !=null) {
			strickenObject = downRight;
		}
			
		return strickenObject;
	}
	
	//The rest of the program builds the initial setup of the program.
	
	/*
	 * Creates the grid from rows of bricks. The color scheme was made as described, the 
	 * colors of the rows based on their height on the screen.
	 */
	private void buildBrickGrid() {
		int numRowsCreated = 0;
		double yy = BRICK_Y_OFFSET;
		while (numRowsCreated < NBRICK_ROWS) {
			if (numRowsCreated < 2) {
				buildRow(Color.RED, yy);
			} else if (numRowsCreated < 4) {
				buildRow(Color.ORANGE, yy);
			} else if (numRowsCreated < 6) {
				buildRow(Color.YELLOW, yy);
			} else if (numRowsCreated < 8) {
				buildRow(Color.GREEN, yy);
			} else {
				buildRow(Color.CYAN, yy);
			}
		yy = yy + BRICK_SEP + BRICK_HEIGHT;
		numRowsCreated++;
		}
	}
	
	/*
	 * Method lays out a row of bricks given the number of bricks in a row and the
	 * specified brick separation.
	 */
	private void buildRow(Color color, double yPosition) {
		double xx = (getWidth() - NBRICK_COLUMNS*BRICK_WIDTH - (NBRICK_COLUMNS-1)*BRICK_SEP)/2;
		int bricksLaid = 0;
		while(bricksLaid < NBRICK_COLUMNS) {
			buildBrick(color , xx , yPosition);
			xx = xx + BRICK_SEP + BRICK_WIDTH;
			bricksLaid++;
		}
	}
	
	//Creates a brick that requires a user specified color, x position, and y position.
	private void buildBrick(Color color, double xPosition, double yPosition) {
		GRect brick = new GRect(xPosition, yPosition, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(color);
		brick.setFilled(true);
		add(brick);
	}
	
	//Creates the ball for the program.
	private void makeBall() {
		ball = new GOval(getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	//Creates the paddle for the program
	private void buildPaddle(double xP) {
		paddle = new GRect(xP, yPaddle, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
	}
}
