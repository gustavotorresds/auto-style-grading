/*
 * File: Breakout.java
 * Name: Maria Cecilia Marques
 * Section Leader: Kaitlyn Lagattuta
 * ---------------------------------
 * This file implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels.
	// These should be used when setting up the initial size of the game,
	// but in later calculations getWidth() and getHeight() are used
	// rather than constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row.
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks.
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels.
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels.
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0)*BRICK_SEP)/NBRICK_COLUMNS);

	// Height of each brick, in pixels.
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels.
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle.
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom. 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels.
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity is chosen randomly (+/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms).
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns.
	public static final int NTURNS = 3;

	//Instance variable for paddle to track mouse.
	GRect paddle;

	//Instance variable for ball.
	GOval ball;

	//Instance variable for the random number generator.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Instance variables for ball velocity.
	private double vx, vy;

	//Instance variable for counter.
	private int counter = NBRICK_ROWS*NBRICK_COLUMNS;

	/*
	 * Method: Run
	 * -----------
	 * This program sets up and implements the Breakout game. 
	 */
	public void run() {
		// Sets the window's title bar text
		setTitle("CS 106A Breakout");
		// Sets the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		playGame();
	}

	/*
	 * Method: Set Up Game
	 * -------------------
	 * Sets up the game. It creates the rows of bricks at 
	 * the top of the game and then the paddle, which is in a fixed position
	 * in the vertical dimension but moves back and forth across the screen
	 * along with the mouse until it reaches the edge of its space.
	 */
	private void setUpGame() {
		setUpBricks();
		makePaddle();
		centerPaddle();
		addMouseListeners();
	}

	/*
	 * Method: Set Up Bricks
	 * ---------------------
	 * Sets up the bricks by calling "drawRowPair" five times;
	 * different colors and heights are used to build each pair of rows.
	 * The heights depend on the brick_y_offset constant. 
	 */
	private void setUpBricks() { 
		//This defines the height at which each pair of rows begins. 
		double rowPairHeight = 2*(BRICK_HEIGHT + BRICK_SEP);
		drawRowPair(BRICK_Y_OFFSET, Color.RED);
		drawRowPair(BRICK_Y_OFFSET + rowPairHeight, Color.ORANGE);
		drawRowPair(BRICK_Y_OFFSET + 2*rowPairHeight, Color.YELLOW);
		drawRowPair(BRICK_Y_OFFSET + 3*rowPairHeight, Color.GREEN);
		drawRowPair(BRICK_Y_OFFSET + 4*rowPairHeight, Color.CYAN);	
	}

	/*
	 * Method: Draw Row Pair
	 * ---------------------
	 * Draws each pair of rows; it takes in a different y 
	 * coordinate and color each time. The bricks are centered in the window. 
	 */
	private void drawRowPair(double yOfRowPair, Color color) {
		//This determines the y coordinate of each row. 
		for (int c = 0; c < 2; c++) {
			double yOfRow = yOfRowPair + (BRICK_HEIGHT + BRICK_SEP)*c;
			//This draws each row of bricks.
			for (int i = 0; i < NBRICK_COLUMNS; i++) { 
				double rowWidth = NBRICK_COLUMNS*BRICK_WIDTH + BRICK_SEP*(NBRICK_COLUMNS - 1); 
				double xOfBrick = (getWidth() - rowWidth)/2 + (BRICK_WIDTH+BRICK_SEP)*i;
				double yOfBrick = BRICK_Y_OFFSET + yOfRow;
				GRect brick = new GRect (xOfBrick, yOfBrick, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setColor(color);
				brick.setFilled(true);
				brick.setFillColor(color);
				add (brick);
			}
		}
	}

	/*
	 * Method: Make Paddle
	 * -------------------
	 * Creates the black paddle. 
	 */
	private void makePaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
	}

	/*
	 * Method: Center Paddle
	 * ---------------------
	 * Adds the paddle to the center of the screen.
	 */
	private void centerPaddle() {
		double xOfPaddle = (getWidth() - PADDLE_WIDTH)/2;
		double yOfPaddle = getHeight() - (PADDLE_Y_OFFSET+PADDLE_HEIGHT);
		add(paddle, xOfPaddle, yOfPaddle);
	}

	/*
	 * Method: Mouse Moved
	 * -------------------
	 * This method responds to mouse motion. It gets the current x mouse
	 * location and makes the paddle track the mouse. The y position of the paddle 
	 * remains fixed and the paddle is centered around the mouse.
	 */
	public void mouseMoved(MouseEvent e)  {
		double newXOfPaddle = e.getX() - PADDLE_WIDTH/2;
		//This ensures that the entire paddle is visible in the window at all times. 
		boolean xWithinWindow = PADDLE_WIDTH/2 < e.getX() && e.getX() < getWidth() - PADDLE_WIDTH/2;
		if (xWithinWindow) {	
			paddle.setLocation(newXOfPaddle, paddle.getY());		
		}
	}

	/*
	 * Method: Play Game
	 * -----------------
	 * Executes the game. On each of the three turns, the user clicks the screen so that 
	 * a ball is launched from center of the window toward bottom of the screen at a random angle;
	 * ball bounces off the paddle and the walls of the world. If the ball collides with a brick,
	 * brick is removed. After game is over player's result is displayed on screen. 
	 */
	private void playGame() {
		for (int i = 0; i < NTURNS; i++) { 
			//Checks if there still are bricks in the screen. If not, breaks out of loop.
			if (!noMoreBricks()) {
				if (i!=0) pause (DELAY*20);
				drawBall();
				//The vx component is chosen randomly so that ball is launched at random angle.
				vx = rgen.nextDouble(1.0, 3.0);
				if (rgen.nextBoolean(0.5)) vx = -vx;
				//Initially ball is heading downwards so vy is positive. 
				//Its magnitude remains constant throughout the program.
				vy = VELOCITY_Y;
				waitForClick();
				//Checks for terminating conditions. 
				while (!trialOver()) {
					ball.move(vx, vy);
					checkForWalls();
					checkForCollisions();
					pause (DELAY/2);	
				}
			}	
		}
		showResult();
	}

	/*
	 * Method: No More Bricks
	 * ----------------------
	 * Returns true if there are no more bricks in the screen. 
	 */
	private boolean noMoreBricks() {
		return (counter == 0); 
	}

	/*
	 * Method: Draw Ball
	 * -----------------
	 * Makes and adds the black ball to the screen.
	 */
	private void drawBall() {
		double xOfBall = getWidth()/2 - BALL_RADIUS;
		double yOfBall = getHeight()/2 - BALL_RADIUS;
		ball = new GOval (xOfBall, yOfBall, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * Method: Trial Over
	 * ------------------
	 * Checks for terminating conditions; if ball hits bottom wall or last brick
	 * has been removed (or both), ball is removed and method returns true. Condition 
	 * used to control while loop in "playGame" becomes false; loop exits; that turn is over. 
	 */
	private boolean trialOver() {
		if ((hitBottomWall())  || (noMoreBricks())) {
			remove(ball);
			return true;
		} else { 
			return false;
		}
	}

	/*
	 * Method: Check For Walls
	 * -----------------------
	 * Makes ball bounce around the world; if ball hits top or bottom wall,
	 * vy sign is reversed, if it hits side walls, vx sign is reversed.
	 */
	private void checkForWalls() {
		if (hitLeftWall() || hitRightWall()) vx=-vx;
		if (hitTopWall()) vy=-vy;
	}

	/*
	 * Method: Hit Left Wall
	 * ---------------------
	 * Returns true if ball hits left wall.  
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	/*
	 * Method: Hit Right Wall
	 * ----------------------
	 * Returns true if ball hits right wall.
	 */
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*
	 * Method: Hit Top Wall
	 * --------------------
	 * Returns true if ball hits top wall.
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	/*
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * Returns true if ball hits bottom wall.
	 */
	private boolean hitBottomWall() {
		return ball.getY() >= getHeight() - ball.getHeight();
	}

	/*
	 * Method: Check For Collisions
	 * ----------------------------
	 * Checks for collisions of the ball with the paddle and bricks. If the ball
	 * collides with the paddle, it bounces off. If it collides with a brick it 
	 * also bounces off and brick is removed. 
	 */
	private void checkForCollisions() {
		GObject collider = getCollidingObject();	
		if (collider == paddle) {
			vy=-Math.abs(vy);
			//If collider object is neither the paddle nor null than it can only be a brick.
		} else if (collider != null) {
			vy=-vy;
			remove(collider);
			//Keeps count of number of bricks on screen. 
			counter--; 
		}
	}

	/*
	 * Method: Get Colliding Object
	 * ----------------------------
	 * Returns GObject or null value depending on whether collision there was 
	 * a collision with the ball. To check for collisions with ball, method tests 
	 * for presence of elements at the four corner points of the square in which ball is inscribed. 
	 */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if(getElementAt(ball.getX() + ball.getWidth(), ball.getY()) != null) {
			return getElementAt(ball.getX() + ball.getWidth(), ball.getY());
		} else if(getElementAt(ball.getX(), ball.getY() + ball.getWidth()) != null) {
			return getElementAt(ball.getX(), ball.getY() + ball.getWidth());
		} else if(getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getWidth()) != null) {
			return getElementAt(ball.getX() + ball.getWidth(), ball.getY() + ball.getWidth());
		} else { return null;
		}
	}

	/*
	 * Method: Show Result
	 * -------------------
	 * Displays game result. If player manages to clear all bricks in 3 turns or less, 
	 * a label indicating his win is displayed. If not, the label indicates a loss.
	 */
	private void showResult() {
		pause (DELAY*20);
		if (noMoreBricks()) { 
			showWinLabel();
		}	else  {
			showLoseLabel();
		}
	}

	/*
	 * Method: Show Win Label
	 * ----------------------
	 * Makes and adds to the screen a "You Win :)" label.
	 */
	private void showWinLabel() {
		GLabel winLabel = new GLabel ("You Win :)");
		winLabel.setFont("Monospaced-28");
		double xWinLabel = ((getWidth() - winLabel.getWidth())/2);
		double yWinLabel = ((getHeight() + winLabel.getAscent())/2);
		winLabel.setColor(Color.GREEN);
		add(winLabel, xWinLabel, yWinLabel);
	}

	/*
	 * Method: Show Lose Label
	 * -----------------------
	 * Makes and adds to the screen a "You Lose :(" label.
	 */
	private void showLoseLabel() {
		GLabel loseLabel = new GLabel ("You Lose :(");
		loseLabel.setFont("Monospaced-28");
		double xLoseLabel = ((getWidth() - loseLabel.getWidth())/2);
		double yLoseLabel = ((getHeight() + loseLabel.getAscent())/2);
		loseLabel.setColor(Color.RED);
		add(loseLabel, xLoseLabel, yLoseLabel);
	}
}