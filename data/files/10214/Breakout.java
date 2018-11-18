/*
 * File: Breakout.java
 * -------------------
 * Name: Steven Biringer
 * Section Leader: Nidhi Manoj
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
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS); //This dynamically sets the brick width to evenly fill canvas

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
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	//////////////////////////////////////////////////////////////////// End of Constants

	//Instance Variables
	GRect paddle = null;
	GOval ball = null;
	private double vx, vy; //ball velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int bricksRemaining = NBRICK_ROWS * NBRICK_COLUMNS; //This tracks whether the game is won

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//This begins the BreakOut code sequence
		setUp();
		addMouseListeners();

		int nLives = NTURNS;
		while (nLives > 0) {
			//clickToStart(nLives);
			playTheGame();
			if (bricksRemaining == 0) {
				break;
			}
			nLives -= 1;
		}
		if (bricksRemaining == 0) {
			win();
		} else {
			gameOver();
		}
	}

	/**
	 * Method: This simple method outlines the 
	 * setup of the canvas before the game is played
	 */
	private void setUp() {
		layBricks();
		drawPaddle(); //See mouseMoved() for paddle animation
	}

	/**
	 * Method: Play the Game
	 * Once the mouse is clicked, this method initiates ball movement,
	 * tests for collisions, and removes bricks when struck
	 * Precondition: Ball is static at center x and center y
	 * Postcondition: Ball has passed the bottom wall, is out of play, and has been removed
	 * or all bricks have been crushed and you in!
	 */
	private void playTheGame() {
		drawBall();
		waitForClick();

		//Initiate ball movement upon click and generate random x direction
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;

		//Ball movement loop, testing for collisions with walls, paddle, bricks, and bottom boundary
		while (ball.getY() <= CANVAS_HEIGHT) {
			if (ball.getY() + vy <= 0) {
				vy = -vy;
			}
			if (ball.getX() + vx + BALL_RADIUS * 2 >= CANVAS_WIDTH) {
				vx = -vx;
			} else {
				if (ball.getX() + vx <= 0) {
					vx = -vx;
				}
			}

			ball.move(vx, vy);
			pause(DELAY);
			GObject collider = getCollidingObject();
			if (collider != null) {
				if (collider == paddle) {
					if (vy >= 0) vy = -vy;
				} else {
					vy = -vy;
					remove(collider);
					bricksRemaining -= 1;
				}
			}
			if (bricksRemaining == 0) {
				remove(ball);
				break;
			}
		}
		remove(ball);
	}

	/**
	 * Method: If you were to draw a square around the ball, this method
	 * checks to see if there is another object intersecting with either
	 * of the four points of that square and returns either that object
	 * or null if no object is identified
	 * @return
	 */
	private GObject getCollidingObject() {
		GObject collider = null;
		//Search Top Left
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			collider = getElementAt(ball.getX(),ball.getY());
			//Search Bottom Left
		} else if (getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2) != null) {
			collider = getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2);
			//Search Top Right	
		} else if (getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY()) != null) {
			collider = getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY());
			//Search Bottom Right
		} else if (getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + BALL_RADIUS * 2) != null) {
			collider = getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + BALL_RADIUS * 2);
		}
		return collider;
	}

	/**
	 * Method: Lays all of the bricks
	 * 1. Finds the origin of the first brick
	 * 2. Lays each row by color
	 * 3. Repeats if additional rows are needed
	 */
	private void layBricks() {

		//Set origin coordinates
		double layX = findingXOrigin(); //Beginning X coordinate
		double layY = BRICK_Y_OFFSET;	//Beginning Y coordinate
		int numRowsLaid = 0;
		Color brickColor = null;


		//NOW FOR THE REAL MASONARY WORK

		while (numRowsLaid < NBRICK_ROWS) {
			
			//Lay RED brick rows and return new Y value for next color
			brickColor = Color.RED;
			layY = layBricksByColor(layX, layY, brickColor, numRowsLaid);
			numRowsLaid += 2;

			//Lay ORANGE brick rows and return new Y value for next color
			brickColor = Color.ORANGE;
			layY = layBricksByColor(layX, layY, brickColor, numRowsLaid);
			numRowsLaid += 2;
			
			//Lay YELLOW brick rows and return new Y value for next color
			brickColor = Color.YELLOW;
			layY = layBricksByColor(layX, layY, brickColor, numRowsLaid);
			numRowsLaid += 2;
			
			//Lay GREEN brick rows and return new Y value for next color
			brickColor = Color.GREEN;
			layY = layBricksByColor(layX, layY, brickColor, numRowsLaid);
			numRowsLaid += 2;
			
			//Lay CYAN brick rows and return new Y value for next color
			brickColor = Color.CYAN;
			layY = layBricksByColor(layX, layY, brickColor, numRowsLaid);
			numRowsLaid += 2;
		}
	}

	/**
	 * Method: This method finds the appropriate starting x coordinate to lay bricks
	 * determined by the number of columns in each row. Returns the X coordinate.
	 * @return
	 */
	private double findingXOrigin() {
		int nBricksPerRow = NBRICK_COLUMNS;
		double layX;
		if (nBricksPerRow % 2 > 0) {
			layX = (getWidth() / 2.0) - (BRICK_WIDTH / 2.0) - (((NBRICK_COLUMNS-1) / 2.0) * (BRICK_WIDTH + BRICK_SEP));
		} else {
			layX = (getWidth() / 2.0) - (BRICK_SEP / 2.0) - ((NBRICK_COLUMNS / 2.0) * (BRICK_WIDTH)) - (((NBRICK_COLUMNS) / 2.0 - 1 ) * BRICK_SEP);
		}
		return layX;
	}

	/**
	 * Method: Lays bricks in n rows for a given color
	 * Precondition: X coordinate is always reset to what's given at beginning of LayBricks method (e.g., BRICK_SEP),
	 * Y is fed from the return value of the last time layBricksbyColor was run
	 * Postcondition: X returns to BRICK_SEP and Y is changed to the next row down and returns that value
	 * @param layX
	 * @param layY
	 * @param brickColor
	 * @param numColorRows
	 * @return
	 */
	private double layBricksByColor(double layX, double layY, Color brickColor, int numRowsLaid) {
		int numColorRows = Math.min(2, NBRICK_ROWS - numRowsLaid);
		int numBricksLaidInRow = 1;
		double layXLegacy = layX;
		
		while (numColorRows > 0) {
			//Loop for laying bricks in a single row
			while (numBricksLaidInRow <= NBRICK_COLUMNS) {
				GRect rect = new GRect(layX, layY, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				rect.setColor(brickColor);
				add(rect);
				layX += (BRICK_WIDTH + BRICK_SEP);
				numBricksLaidInRow++;
			}

			//Set origin for next row
			layX = layXLegacy;
			layY += (BRICK_HEIGHT + BRICK_SEP);
			numBricksLaidInRow = 1;
			//Move to next row
			numColorRows--;
		}
		return layY;
	}

	/**
	 * Method: Draws static paddle on the canvas
	 */
	private void drawPaddle() {
		double x = getWidth() / 2 - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(x,y,PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/**
	 * Method: Paddle Movement
	 * This method re-assigns the location of the paddle
	 * each time the mouse is moved while on the canvas
	 */
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		if (mouseX - PADDLE_WIDTH / 2 <= 0 ) {
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
		} else if (mouseX + PADDLE_WIDTH / 2 >= CANVAS_WIDTH) {
			paddle.setLocation(CANVAS_WIDTH - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		} else {
			paddle.setLocation(mouseX - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	/**
	 * Method: Draws ball in the center of the screen
	 */
	private void drawBall() {
		ball = new GOval(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	/**
	 * Method: GameOver - This method alerts the player that the game is over
	 */
	private void gameOver() {
		GLabel endOfGame = new GLabel("GAME OVER");
		endOfGame.setFont("Courier-52");
		add(endOfGame, getWidth() / 2 - endOfGame.getWidth() / 2, getHeight() / 2 - endOfGame.getHeight() / 2);
	}
	/**
	 * Method: Win - This alerts the player of victory!!!
	 */
	private void win() {
		GLabel win = new GLabel("You Win!!!");
		win.setFont("Courier-52");
		add(win, getWidth() / 2 - win.getWidth() / 2, getHeight() / 2 - win.getHeight() / 2);

	}
}
