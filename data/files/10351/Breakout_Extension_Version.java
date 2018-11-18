/*
 * File: Breakout.java
 * -------------------
 * Name: Sandro Boaro
 * Section Leader: Ben Barnett
 * 
 * This file will implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_Extension_Version extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// This sets a label to tell the user to click to begin.
	public GLabel gameStartLabel = null;

	// Number of bricks in each row.
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks.
	public static final int NBRICK_ROWS = 10;
	
	// The total amount of bricks on the screen.
	public static final int totalBricksOnScreen = NBRICK_ROWS*NBRICK_COLUMNS;
	
	// This variable shows keeps track of how many bricks have been destroyed by the ball.
	public int bricksDestroyed = 0;
	
	// Separation between neighboring bricks, in pixels.
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels.
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels.
	public static final double BRICK_Y_OFFSET = 70;

	// Creates an "empty" uninitialized paddle
	public GRect paddle = null;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Creates the ball that will later be used in several methods.
	public GOval ball = null;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// Diameter of the ball in pixels
	public static final double BALL_DIAM = 2*BALL_RADIUS;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Instance variable of the velocity in the x and y direction.
	private double vx;
	private double vy;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static int NTURNS = 3;
	
	// Number of turns used. This increases after a ball has fallen off the screen.
	public int NTURNS_USED = 0;
	
	GLabel lives = null;
	public int LIVES = 3;

	GLabel score = null;
	public int GAME_SCORE = 0;
	
	// Allows for a value to be randomly generated later in the program.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// This method sets up all of the bricks on screen to be later destroyed.
		setUpColoredBricks();
		
		// Sets up on screen text.
		setUpScreenText();
		
		// This initializes the paddle once the bricks have been set up.
		setUpPaddle();

		// This calls the Click To Begin Label to let the user know how to start the game.
		promptClickToBegin();

		// Makes the program listen to the mouse.
		addMouseListeners();

		// Sets the starting speed and direction of the ball.
		setGameBallSpeed();
		
		// Animates the motion of the ball, and begins game.
		playBreakout();
	}

	// This is just an animation loop for the ball once the game has been setup and the player is ready to play.
	private void playBreakout() {
		while (true) {
			moveBall();		
		}
	}

	private void promptClickToBegin() {
		gameStartLabel = new GLabel ("CLICK TO BEGIN GAME");
		gameStartLabel.setLocation((getWidth()/2)-(gameStartLabel.getWidth()/1.5), (getHeight() - gameStartLabel.getAscent())/2);
		gameStartLabel.setFont("Helvetica-16");
		gameStartLabel.setColor(Color.RED);
		add(gameStartLabel);
		gameStartLabel.setVisible(true);
	}
 
	// This helper method loops to check if any of the "corners"
	private  GObject getCollidingObject(){
		GObject objectCollided = null;
		
		GObject topLeftCollider = getElementAt(ball.getX(), ball.getY()); // Gets element for top left corner of ball.
		GObject topRightCollider = getElementAt(ball.getRightX(), ball.getY()); // Gets element for the top right corner of the ball.
		GObject bottomLeftCollider = getElementAt(ball.getX(), ball.getBottomY()); // Gets element for the bottom left corner of the ball.
		GObject bottomRightCollider = getElementAt(ball.getRightX(), ball.getBottomY()); // Gets element for the bottom right corner of the ball.

		// When checking for each getElementAt above, the value for each GObject is given to the local variable objectCollided;
		if (topLeftCollider != null) {
			objectCollided = topLeftCollider;
			}
		if (topRightCollider != null) {
			objectCollided = topRightCollider;
			}
		if (bottomLeftCollider != null) {
			objectCollided = bottomLeftCollider;
			}
		if (bottomRightCollider != null) {
			objectCollided = bottomRightCollider;
			}
		return objectCollided;
	}
	
	private void setGameBallSpeed() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
	}
	
	private void moveBall() {
		double bottomOfPaddle = paddle.getBottomY() + PADDLE_HEIGHT/2;
		if (ball != null){
			int lives = LIVES;
			int score = GAME_SCORE;
			ball.move(vx,vy);
			if ( ball.getBottomY() <= bottomOfPaddle  && bricksDestroyed != totalBricksOnScreen && NTURNS > NTURNS_USED) {
				// These if statements allow the ball to bounce of the walls of the canvas.
				if ( ball.getY() > CANVAS_HEIGHT - BALL_DIAM || ball.getY() < 0) { 
					vy = -vy;
					}
				if ( ball.getX() > CANVAS_WIDTH - BALL_DIAM || ball.getX() < 0 ) {
					vx = -vx;
					}

				// These if statements allow the ball to remove the bricks while also bouncing off the paddle.
				if ( getCollidingObject() == paddle && ball.getBottomY() >= paddle.getY() ) {
					vy = - Math.abs(vy);
					}  
				else if ( getCollidingObject() != null && getCollidingObject() != paddle) {
					vy = -vy;
					remove(getCollidingObject());
					bricksDestroyed++;
					score += 100;
					}
				} else {
					ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
					waitForClick();
					NTURNS_USED++ ; // Every time the ball has hit the bottom of the screen it will increase the amount of turns used
					lives-- ;
				}
			}
		pause(DELAY);
	}

	private void setUpPaddle() {
		double startPaddleX = (getWidth() - PADDLE_WIDTH)/2;
		double startPaddleY = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect( startPaddleX, startPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		paddle.setColor(Color.RED);
		add(paddle);
	}
	
	// This mouse event moves the center of the paddle where the mouse's x coordinate is on the screen.
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX() - (PADDLE_WIDTH/2); // makes paddle move about its center
		if (paddle != null){
			if (mouseX < CANVAS_WIDTH - PADDLE_WIDTH && mouseX > 0) { //  The && statement gives boundaries to the paddle
			paddle.setX(mouseX);
			}
		}
	}

	//  This will generate the ball once the user presses down the mouse.
	public void mousePressed (MouseEvent e) {
//		 Condition to remove the start label if it is visible.
//		 At the end of the statement, it sets visibility to false so it does not
//		 run into a null pointer extension error.
		if (gameStartLabel != null) {
			if (gameStartLabel.isVisible()) {
				remove(gameStartLabel);
				gameStartLabel.setVisible(false);
				pause(250);
			}

			double ballX = getWidth()/2 - BALL_RADIUS;
			double ballY = getHeight()/2 - BALL_RADIUS;
			// Condition only generates a ball if no ball has been generated already.
			if (ball == null) {
				ball = new GOval(ballX,ballY,BALL_DIAM, BALL_DIAM);
				ball.setFilled(true);
				ball.setFillColor(Color.BLACK);
				ball.setColor(Color.RED);
				add(ball);
			}
		}
	}
	
	// This method sets up the on-screen text and information.
	private void setUpScreenText() {
		score = new GLabel ("SCORE: " + GAME_SCORE);
		score.setLocation(10.0, 20.0);
		
		lives = new GLabel ("LIVES: " + LIVES);
		lives.setLocation(getWidth()-10 - lives.getWidth(), 20.0);
		
		add(score);
		add(lives);
		
	}

	private void setUpColoredBricks() {
		double startBrickRowX = getWidth() - (NBRICK_COLUMNS*BRICK_WIDTH) - (NBRICK_COLUMNS*(BRICK_SEP) + BRICK_SEP);
		// Makes red rows of bricks
		createBrickRow(startBrickRowX, BRICK_Y_OFFSET, Color.RED);
		// Makes orange rows of bricks
		createBrickRow(startBrickRowX, BRICK_Y_OFFSET + (2*(BRICK_HEIGHT + BRICK_SEP)), Color.ORANGE);
		// Makes yellow rows of bricks
		createBrickRow(startBrickRowX, BRICK_Y_OFFSET + (4*(BRICK_HEIGHT + BRICK_SEP)), Color.YELLOW);
		// Makes green rows of bricks
		createBrickRow(startBrickRowX, BRICK_Y_OFFSET + (6*(BRICK_HEIGHT + BRICK_SEP)), Color.GREEN);
		// Makes cyan rows of bricks
		createBrickRow(startBrickRowX, BRICK_Y_OFFSET + (8*(BRICK_HEIGHT + BRICK_SEP)), Color.CYAN);

	}

	private void createBrickRow(double x, double y, Color color) {	
		// Parameters passed are the x coordinate, the y coordinate, and the color of the rows. 
		// First for statement loops to make 2 rows of bricks
		for (int rowsBuilt = 0; rowsBuilt < 2 ; rowsBuilt++) { 
			y = y + (rowsBuilt*(BRICK_HEIGHT + BRICK_SEP)); // sets a new y location for the second row of brick.

			// Builds out bricks dependent on instance variable NBRICK_COLUMNS
			for (int bricksBuilt  = 0; bricksBuilt < NBRICK_COLUMNS; bricksBuilt++ ){
				GRect nextBrickRow = new GRect (x + (bricksBuilt*(BRICK_WIDTH + BRICK_SEP)), y, BRICK_WIDTH, BRICK_HEIGHT);
				nextBrickRow.setFilled(true);
				nextBrickRow.setFillColor(color);
				nextBrickRow.setColor(color);
				add(nextBrickRow);
				pause(100);
			}
		}
	}
}
