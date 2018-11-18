/*
 * File: Breakout.java

 * -------------------
 * Name: Walter "Teke" Dado
 * Section Leader:Ruiqi Chen
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

	//add instance variable for paddle
	private GRect paddle = null;
	
	//add instance variable for the ball
	private GOval ball = null;
	
	//add instance variable random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//make vx and vy public so changes can be seen by other methods
	private double vx;
	private double vy;
	
	//open instance variable for gameIsOver
	private boolean gameIsOver;
	
	//instance variable for counting bricks destroyed
	private int brickCounter;
	private GLabel scoreCounter;
	
	//instance variable for life counter
	private int lifeCounter = NTURNS;
	
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		
		//Takes care of setting up the game, including 
		setUp();
		playGame();
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		double leftPaddleLimit = 0;
		double rightPaddleLimit = getWidth() - PADDLE_WIDTH;
		if(x > leftPaddleLimit && x < rightPaddleLimit) {
			paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);
		}
	}
	private void setUp() {
		//should bricks actually be an instance variable? won't we
		//use getElementAt later on?
		addBricks();
		paddle = makePaddle();
		addPaddle();
		ball = makeBall();
		addBall();
	}
	
	private void playGame() {
		setUpBallMovement();
		brickCounter = 0;
		while(gameIsOver != true) {
			ball.move(vx, vy);
			checkForWalls();
			checkForCollisions();
			checkIfGameIsOver();
			addScoreCounter();
			pause(DELAY);
		}
	}

	private void addScoreCounter() {
		if (scoreCounter != null) {
			remove(scoreCounter);
		}
		defineScoreCounter();
		add(scoreCounter);
	}

	private void defineScoreCounter() {
		scoreCounter = new GLabel("SCORE:" + brickCounter);
		scoreCounter.setFont("Courier-24");
		scoreCounter.setColor(Color.BLUE);
		scoreCounter.setLocation((getWidth() - scoreCounter.getWidth())/2.0, getHeight());
	}

	private void checkIfGameIsOver() {
		//if ball hits bottom wall 3 times, gameIsOver becomes true
		if (ball.getY() >= getHeight() - 2*BALL_RADIUS) {
			lifeCounter = lifeCounter - 1;
			if(lifeCounter == 0) {
				gameIsOver = true;
				displayGameOver();
			}
			ball.setLocation(getWidth()/2.0 - ball.getWidth()/2.0, getHeight()/2 - ball.getHeight()/2.0);
			setUpBallMovement();
			
		}
		
		//if all the bricks are gone, the game is over also
		int totalNbricks = NBRICK_ROWS*NBRICK_COLUMNS;
		if(brickCounter == totalNbricks) {
			gameIsOver = true;
			remove(ball);
			displayGameWon();
		}
	}
	private void displayGameWon() {
		GLabel gameWon = new GLabel("GAME WON");
		gameWon.setFont("Courier-64");
		gameWon.setColor(Color.BLUE);
		gameWon.setLocation(getWidth()/2.0 - gameWon.getWidth()/2.0, getHeight()/2.0 - gameWon.getHeight()/2.0);
		add(gameWon);
	}

	private void displayGameOver() {
		GLabel gameOver = new GLabel("GAME OVER");
		gameOver.setFont("Courier-64");
		gameOver.setColor(Color.RED);
		gameOver.setLocation(getWidth()/2.0 - gameOver.getWidth()/2.0, getHeight()/2.0 - gameOver.getHeight()/2.0);
		add(gameOver);
	}

	private void setUpBallMovement() {
		vy= VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		waitForClick();
	}

	private void checkForWalls() {
		if (ball.getX() <= 0 || ball.getX() >= getWidth()- 2.0*BALL_RADIUS) {
			vx = -vx;
		}
		if (ball.getY() <= 0 || ball.getY() >= getHeight() - 2.0*BALL_RADIUS) {
			vy = -vy;
		}
	}

	private void checkForCollisions() {
		//check lower left 
		checkCorner(ball.getX(), ball.getY() + 2.0*BALL_RADIUS);
		//check lower right
		checkCorner(ball.getX() + 2.0*BALL_RADIUS, ball.getY() + 2.0*BALL_RADIUS);
		//check upper right
		checkCorner(ball.getX() + 2.0*BALL_RADIUS, ball.getY());
		//check upper left
		checkCorner(ball.getX(), ball.getY());
		//check top middle
		checkCorner(ball.getX() + BALL_RADIUS, ball.getY());
		//check lower middle
		checkCorner(ball.getX() + BALL_RADIUS, ball.getY() + 2.0*BALL_RADIUS);
	}

	

	private void checkCorner(double xPoint, double yPoint) {
		GObject collider = getElementAt(xPoint,yPoint);
		if(collider != null) {
			vy = -vy;
			if (collider == paddle) {
				//This checks if the ball is hitting the side of the paddle
				//given the precondition that the paddle is already been
				//hit, then it sees if the ball is below the top of the paddle
				//and in between its x boundaries. if so, we want to negate
				//the vx
				if (ball.getY() + BALL_RADIUS > paddle.getY() && (ball.getX() + 2.0*BALL_RADIUS >= paddle.getX() || ball.getX() <= paddle.getX() + PADDLE_WIDTH)) {
					vx = -vx;
					//this while loop makes sure the ball doesn't get stuck
					//inside the paddle if you hit the paddle into the ball
					//at the last second from the side, by moving ball to
					//the edge of the paddle before releasing it to check
					//if it has hit anything again
					while(ball.getBottomY() > paddle.getY() && (xPoint >= paddle.getX() || xPoint <= paddle.getX() + PADDLE_WIDTH)) {
						ball.move(vx, vy);
						pause(DELAY);
					}
					ball.move(vx, vy);
				}
				
			} else {
				remove(collider);
				brickCounter = brickCounter + 1;
			}
		}
	}

	private GOval makeBall() {
		GOval ball = new GOval(2.0*BALL_RADIUS, 2.0*BALL_RADIUS);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		return ball;
	}
	
	private void addBall() {
		ball.setLocation(getWidth()/2.0, getHeight()/2.0);
		add(ball);
	}

	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		return paddle;
	}

	private void addPaddle() {
		paddle.setLocation(getWidth()/2.0 - PADDLE_WIDTH/2.0, getHeight() - PADDLE_Y_OFFSET);
		add(paddle);
	}
	
	/* So at each tick of i, j will run from zero to ten, adding bricks
	 * in a row and coloring them based on i's value. 
	 */
	private void addBricks() {
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				//This first x coordinate needs to be set back from the midpoint
				//by the number of bricks in a row divided in half, along with
				//half the brick separations, which is one less than half the
				//number of bricks in a row
				double firstBrickX = getWidth()/2.0 - (NBRICK_COLUMNS/2.0)*(BRICK_WIDTH + BRICK_SEP) + BRICK_SEP/2.0;
				brick.setLocation(firstBrickX + (j)*(BRICK_WIDTH +BRICK_SEP), BRICK_Y_OFFSET + (i+1.0)*BRICK_HEIGHT + i*BRICK_SEP);
				if(i < 2) {
					brick.setColor(Color.RED);
					brick.setFilled(true);
				}
				else if (i < 4) {
					brick.setColor(Color.ORANGE);
					brick.setFilled(true);
				}
				else if (i < 6) {
					brick.setColor(Color.YELLOW);
					brick.setFilled(true);
				}
				else if (i < 8) {
					brick.setColor(Color.GREEN);
					brick.setFilled(true);
				}
				else if (i < 10) {
					brick.setColor(Color.CYAN);
					brick.setFilled(true);
				}
				add(brick);
			}
			
		}
	}

}
