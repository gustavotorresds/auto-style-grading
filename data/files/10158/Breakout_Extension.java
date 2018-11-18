/*
 * File: Breakout.java
 * -------------------
 * Name: Kaila Kim
 * Section Leader: Chase Davis
 * 
 * This program builds bricks, paddle, and a ball. Whenever the ball
 * makes contact with any object, it bounces away, and if it's a brick,
 * it removes the brick. The paddle follows the ball. If the ball falls to the bottom,
 * the ball is reset to the middle of the window, and the user can fail this 3 times before
 * the game ends. This version colors the bricks randomly with Stanford colors, displays a beginning, life lost
 * message, and a game over/victory message. Also, the game over message floats, so please look at it.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_Extension extends GraphicsProgram {

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
	
	// Width of each row, in pixels
	public static final double ROW_WIDTH = NBRICK_COLUMNS * (BRICK_WIDTH) + (NBRICK_COLUMNS - 1) * BRICK_SEP;

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

	// Number of tries the user gets
	public static final int HEARTS = 3;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();
		setBallSpeed();
		moveBall();
	}
	
	/*
	 * Draws the blocks, paddle, and ball, with the ball centered in the middle of the screen.
	 */
	private void setUpGame() {
		welcomeMessage();
		createBlocks();
		createPaddle();
		createBall();
	}
	
	/*
	 * Writes a start menu that tells the user the win/lose conditions as well as
	 * waiting for the user to click before proceeding with the game.
	 */
	private void welcomeMessage() {
		GLabel welcome = new GLabel("Welcome to Breakout!");
		welcome.setCenterLocation(getWidth()/2, getHeight()/5);
		add(welcome);
		GLabel rules = new GLabel ("To win, get rid of all of the bricks, but");
		rules.setCenterLocation(getWidth()/2, 2 * getHeight()/5);
		add(rules);
		GLabel rules2 = new GLabel(" don't let the ball hit the bottom of the window.");
		rules2.setCenterLocation(getWidth()/2, 3 * getHeight()/5);
		add(rules2);
		GLabel numHearts = new GLabel("You have " + HEARTS + " hearts.");
		numHearts.setCenterLocation(getWidth()/2, 4 * getHeight()/5);
		add(numHearts);
		waitForClick();
		remove(welcome);
		remove(rules);
		remove(rules2);
		remove(numHearts);
	}
	
	
	/*
	 * Builds layers of blocks from the top of the screen down.
	 */
	private void createBlocks() {
		int rowNo; //indicates the row number that the program is building
		for (rowNo = 1; rowNo <= NBRICK_ROWS; rowNo ++) { //Will build rowNo layers
			double y = BRICK_Y_OFFSET + (rowNo - 1)*(BRICK_HEIGHT + BRICK_SEP);	//y-coordinate of the bricks in the layer
			buildLayer(y, rowNo);
		}
	}

	/*
	 * Builds the bricks in a row from the left side of a screen
	 */
	private void buildLayer(double y, int rowNo) {
		double brickXOffset = (getWidth() - ROW_WIDTH)/2;
		int brickNo; //indicates the brick number that the program is building.
		for (brickNo = 1; brickNo <= NBRICK_COLUMNS; brickNo ++) { 
			double x = (brickNo - 1) * (BRICK_WIDTH + BRICK_SEP) + brickXOffset; // -1 compensates for brickNo starting at 1
			GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
			brick.setLocation(x, y);
			brick.setFilled(true);
			add(brick);
			int colorNo = rgen.nextInt(1, 3); // Randomizes the color of each brick
			if (colorNo == 1) {
				brick.setColor(Color.red);
				brick.setFillColor(Color.red);
			} else if (colorNo == 2) {
				brick.setColor(Color.lightGray);
				brick.setFillColor(Color.white);
			} else if (colorNo == 3) {
				brick.setColor(Color.darkGray);
				brick.setFillColor(Color.darkGray);
			}
		}
	}

	/*
	 * Creates a rectangle that serves as the paddle.
	 */
	private void createPaddle() {
		paddle.setFillColor(Color.PINK);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	
	/*
	 * Makes it such that the paddle will follow the x-coordinate of the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2; // the x-coordinate of the paddle such that the middle of the paddle is lined up with the mouse
		if (x > 0 && x < getWidth() - PADDLE_WIDTH) { //restricts the paddle from following the mouse off the screen
			paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET);
		}
	}

	/*
	 * Uses the instance variable ball to add a ball in the middle of the screen 
	 */
	private void createBall() {
		ballX = (getWidth() - BALL_RADIUS)/2;
		ballY = (getHeight() - BALL_RADIUS)/2;
		ball.setLocation(ballX, ballY);
		ball.setFilled(true);
		ball.setFillColor(Color.darkGray);
		add(ball);
	}
	
	/*
	 * Sets the vx and vy for the ball to move.
	 */
	private void setBallSpeed() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); // sets vx to a random value between 1.0 and 3.0
		if (rgen.nextBoolean(0.5)) vx = -vx; // sets vx negative half the time
		vy = VELOCITY_Y;
	}
	
	/*
	 * This function moves the ball so long as the ball doesn't hit a wall, brick, or paddle. If it does, it'll
	 * bounce off. It also tracks how many bricks are left on the screen.  
	 */
	private void moveBall() {
		double numBricks = NBRICK_COLUMNS * NBRICK_ROWS; //sets the number of bricks
		while (numBricks > 0) {
			ball.move(vx, vy);
			ballX = ball.getX(); //updates the x-coordinate of the ball
			ballY = ball.getY(); //updates the y-coordinate of the ball
			pause(DELAY);
			if (!checkXClear()) { //changes the x-direction of the ball if the front is blocked
				vx = -vx;
			}
			
			if (!checkYClear()) { //changes the y-direction of the ball if the front is blocked
				vy = -vy;
			}
			
			if (ballY > getHeight() - BALL_RADIUS) { //if the ball goes beneath the bottom of the window
				ballLost();
			}
			
			GObject collider = checkCollision();

			if (collider == paddle) { //just changes the direction of the ball if it hits the paddle
				vy = -vy;
				ball.move(0, (-ball.getY() - 2 * BALL_RADIUS - PADDLE_HEIGHT - PADDLE_Y_OFFSET + getHeight())/2);
				//moves the ball the amount between it and the paddleY at impact if it would otherwise stick
			
			} else if (collider != null) { //changes the direction and removes whatever object the ball hit
				vy=-vy;
				remove(collider);
				numBricks = numBricks - 1; //keeps track of how many times the ball has been hit.
			}
		}
		ball.setVisible(false); //turns the ball invisible so it's not distracting in the background
		displayVictoryMsg();
	}
	
	/*
	 * Runs the protocol for when the ball has hit the bottom of the window. If errorNum is < HEARTS, then it will
	 * reset ball location. If errorNum = HEARTS, it will end the game and display a message.
	 */
	private void ballLost() {
		errorNum = errorNum + 1;
		if (errorNum < HEARTS) {
			displayHeartLostMsg();
			ball.setLocation((getWidth() - BALL_RADIUS)/2, (getHeight() - BALL_RADIUS)/2);
			pause(500);
		} else {
			endGameLose();
		}
	}
	
	/*
	 * Displays a message and waits for the user to click before trying the game again.
	 */
	private void displayHeartLostMsg() {
		GRect window = new GRect(3*getWidth()/4, getHeight()/3);
		window.setLocation(getWidth()/2 - window.getWidth()/2, getHeight()/2 - window.getWidth()/2);
		window.setColor(Color.BLACK);
		window.setFilled(true);
		window.setFillColor(Color.WHITE);
		add(window);
		GLabel loss = new GLabel("Uh-oh! You have " + (HEARTS - errorNum) + " heart(s) left. Click to restart.");
		loss.setCenterLocation(getWidth()/2, window.getCenterY());
		add(loss);
		waitForClick();
		remove(window);
		remove(loss);
		
	}

	/*
	 * Displays a message bemoaning the inadequate skill of the user. The message bobs up and down slightly.
	 */
	private void endGameLose() {
		ball.setVisible(false);
		GLabel loser = new GLabel("You've lost all your hearts. Better luck next time!");
		loser.setCenterLocation(getWidth()/2, getHeight()/2);
		add(loser);
		while (true) {
			loser.move(0, 1); // bobs the message up and down slightly.
			pause(100);
			loser.move(0, 1);
			pause(100);
			loser.move(0, -2);
			pause(200);
			loser.move(0, -1);
			pause(100);
			loser.move(0, -1);
			pause(100);
			loser.move(0, 2);
			pause(200);
		}
	}	
		
	/*
	 * Checks the x-coordinate of the ball and makes sure it's within the width.
	 */
	private boolean checkXClear() {
		while (ballX < 0 | ballX > getWidth() - BALL_RADIUS) {
			return (false);
		}
		return (true);
	}
	
	/*
	 * Checks the y-coordinate of the ball and makes sure it's within the height.
	 */
	private boolean checkYClear() {
		while (ballY < 0) {
			return (false);
		}
		return (true);
	}

	/*
	 * Returns an object if the ball has collided with a brick or a paddle,
	 * returns null if nothing is at the corners.
	 */
	private GObject checkCollision() {
		if ((getElementAt(ballX, ballY)) != null) {
			return getElementAt(ballX, ballY);
		} else if ((getElementAt(ballX + 2*BALL_RADIUS, ballY)) != null) {
			return getElementAt(ballX + 2*BALL_RADIUS, ballY);
		} else if ((getElementAt(ballX, ballY +2*BALL_RADIUS)) != null) {
			return getElementAt(ballX, ballY +2*BALL_RADIUS);
		} else if ((getElementAt(ballX + 2*BALL_RADIUS, ballY + 2*BALL_RADIUS) != null)) {
			return getElementAt(ballX + 2*BALL_RADIUS, ballY + 2*BALL_RADIUS);
		}
		return null;
	}
	
	/*
	 * Creates a victory message for the end of the game when all bricks have been eliminated. The text flashes every half
	 * second for a fanfare.
	 */
	private void displayVictoryMsg() {
		GLabel victory = new GLabel("YOU WON!!");
		victory.setFont("Courier-50");
		victory.setLocation(getWidth()/2 - victory.getWidth()/2, getHeight()/2 - victory.getAscent()/2);
		add(victory);
		pause(1000);
		while (true) {
			victory.setVisible(false);
			pause(500);
			victory.setVisible(true);
			pause(500);
		}
	}
	
	// Number of times the user has let the ball touch the floor
	private int errorNum = 0; //number of times user has let ball touch bottom

	// Creates the initial random x-velocity of the ball
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// The velocity of the ball
	private double vx, vy;
	
	// The paddle
	private GRect paddle = new GRect((getWidth() - PADDLE_WIDTH)/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
	
	// The ball
	private GOval ball = new GOval (BALL_RADIUS, BALL_RADIUS);
	
	// The coordinates of the ball
	private double ballX, ballY;
}