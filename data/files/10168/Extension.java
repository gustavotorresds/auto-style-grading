/*
 * File: Breakout-Extension.java
 * -------------------
 * Name: Carly Malatskey		
 * Section Leader: Rachel Gardner
 * 
 * This file implements the Extension of the game of Breakout. 
 * As an extension, I added sounds and a GAMEOVER image. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Extension extends GraphicsProgram {
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
	public static final double BRICK_WIDTH = Math.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	// Ball Size 
	public static final double BALL_SIZE = BALL_RADIUS * 2;


	//Instance variables:
	private GRect paddle;
	private GRect brick;
	private GOval ball;
	private double vx; // velocity of X
	private double vy; // velocity of Y
	private RandomGenerator rgen = RandomGenerator.getInstance(); // To randomly select velocity and direction of ball. 
	private GObject collider = null;
	private int numberOfBricks = NBRICK_ROWS * NBRICK_COLUMNS;
	private GLabel label;


	public void run() {
		setTitle("CS 106A Breakout");

		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUp();

		// This ensures a complete game, allowing the
		// user to have a certain number of turns, 
		// depending on the value for NTURNS. 
		for (int i = 0; i < NTURNS; i++) {
			// This allows each turn to only execute as long as there are no bricks left. 
			if (numberOfBricks != 0) {
				remove(ball);
				drawBall();
				waitForClick();
				playGame();
			// The program will break because the user removed all of the bricks. 
			} else {
				break;
			}

		}
		// This adds a label to indicate the user won the game. 
		if (numberOfBricks == 0) {
			label = new GLabel ("You Win! Congrats :)");
			label.setFont ("Courier-18");
			// This adds and centers the label in the middle of the screen. 
			add(label, getWidth()/2 - (label.getWidth()/2), getHeight()/2 - (label.getAscent()/2));
			remove(ball);
		}
		// This adds an image to indicate the user lost the game and that it is the end of the game.  
		if (numberOfBricks != 0) {
			GImage image = new GImage ("Gameover.jpg");
			image.setLocation (0,0);
			image.setSize(CANVAS_WIDTH,CANVAS_HEIGHT);
			add(image);
			remove(ball);
		}

		addMouseListeners();
	}

	/*
	 * Method: setUp
	 * This sets up the game by drawing the bricks, the paddle
	 * and the ball. 
	 */

	private void setUp() {
		drawBricks();
		drawPaddle();
		drawBall();
	}

	/*
	 * Method: playGame
	 * This plays the game by the animation method for moving the ball.
	 */

	private void playGame() {
		moveBall();
	}

	/*
	 * Method: drawBricks
	 * The bricks are drawn and colored. 
	 */

	private void drawBricks() {
		// this "for" loop allows the bricks to be colored once per row rather than per brick. 
		for (int i=0; i < NBRICK_ROWS; i ++) { 	

			// This color variable allows the program to color the bricks while the bricks are being made.
			// The purpose is to take in the row number, using the variable i, and pick the color. 
			Color color = rowNumberColor(i);
			drawRow(i, color);
		}
	}

	/*
	 * Method: drawRow
	 * The rows are drawn by creating bricks (GRect), 
	 * centering all of the bricks in the window and separating the bricks appropriately.
	 */

	private void drawRow(double rowNumber, Color color) {	
		// In order to center the bricks in the window, the variable to determine 
		// the amount of space on each side of the bricks as a whole was created.
		double brickXOffSet = (getWidth() - ((NBRICK_COLUMNS *BRICK_WIDTH) + ((NBRICK_COLUMNS-1) * BRICK_SEP)))/2;

		//This for loop adds the rows in a rectangle as a whole with no space in between. 
		for (int i=0; i < NBRICK_COLUMNS; i ++) {
			// These are the x and y coordinates for the bricks. 
			double x = (i * (BRICK_SEP + BRICK_WIDTH)) + brickXOffSet;
			double y = (rowNumber * (BRICK_SEP + BRICK_HEIGHT)) + BRICK_Y_OFFSET;

			// Creates brick.
			brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
		} 
	}

	/*
	 * Method: rowNumberColor
	 * This method colors the rows and completes the brick setup of the game. 
	 */

	private Color rowNumberColor(double rowNumber) {
		// Allows each row to continue the rainbow colored pattern
		// for each additional row that may be added. 
		rowNumber = rowNumber % 10;

		// Colors the row according to the row number, which would be 
		// equivalent to the remainder of the row number divided by 10. 
		if (rowNumber == 0 || rowNumber == 1 ) {
			return Color.RED;
		} else if (rowNumber == 2 || rowNumber == 3 ) {
			return Color.ORANGE;
		} else if (rowNumber == 4 || rowNumber == 5) {
			return Color.YELLOW;
		} else if (rowNumber == 6 || rowNumber == 7) {
			return Color.GREEN;
		} else if (rowNumber == 8 || rowNumber == 9) {
			return Color.CYAN;
		}
		return null;
	}

	/*
	 * Method: drawPaddle
	 * This method draws the paddle and completes the paddle setup of the game. 
	 */

	private void drawPaddle() {
		// These are the X and Y coordinates of the paddle. 
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2; 
		double paddleY = getHeight() - PADDLE_Y_OFFSET;

		// Creates paddle. 
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * Method: drawBall
	 * This method draws the ball and completes the entire setup of the game. 
	 */

	private void drawBall() {
		// These are the X and Y coordinates of the ball. 
		double ballX = getWidth()/2 - BALL_SIZE/2;
		double ballY = getHeight()/2 - BALL_SIZE/2;

		// Creates ball.
		ball = new GOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * Method: moveBall
	 * This method initiates the play game part by moving the ball 
	 * with animation.
	 */

	private void moveBall() {
		// This randomizes the velocity of X to be between the two constants (VELOCITY_X_MIN, VELOCITY_X_MAX).
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 

		// This is the starting velocity for Y. 
		vy = (+VELOCITY_Y);

		// This makes the velocity of X negative for half of the time. 
		if (rgen.nextBoolean(0.5)) { 
			vx = -vx;
		}

		// This is the animation loop to continue the ball movement.
		while (true) {
			ball.move(vx, vy); // moves the ball.
			pause(DELAY); 

			// This indicates that if the ball hits the left or right wall,
			// the ball will move according to the velocity of x. 
			if (hitLeftWall() || hitRightWall()) {
				vx = -vx; 
			}

			// If the ball hits the top wall,
			// the ball will move according to the velocity of y. 
			if (hitTopWall()) {
				vy = -vy; 
			}

			checkForCollisions();

			// This adds a label to indicate the end of the game since there are no more bricks left.
			// This variable is established in the removeBricks method.
			if (numberOfBricks == 0) {
				label = new GLabel ("You Win! Congrats :)");
				label.setFont ("Courier-18");

				// This adds and centers the label. 
				add(label, getWidth()/2 - (label.getWidth()/2), getHeight()/2 - (label.getAscent()/2));
				remove(ball);

				// This indicates an end to the loop so the game can end. 
				break;
			}

			// This indicates the end of the game since the user hit the bottom wall. 
			if (hitBottomWall()) {
				remove(ball);
				break;
			}
		}	
	}


	// These boolean methods determine the location of which wall
	// the ball hits by using the X and Y coordinates of the ball. 

	private boolean hitLeftWall() {
		return ball.getX() < 0;
	}
	private boolean hitRightWall() {
		return ball.getX() + (BALL_SIZE) > getWidth();
	}
	private boolean hitTopWall() {
		return ball.getY() < 0;
	}
	private boolean hitBottomWall() {
		return ball.getY() + (BALL_SIZE) > getHeight();
	}

	/*
	 * Method: checkForCollisions
	 * This method checks for collisions in order to remove the bricks and bounce off the paddle.
	 */

	private void checkForCollisions() {
		// This variable retrieves the position of an object at the specified locations in that method.
		// The object is determined in getCollidingObject and the object becomes this collider variable. 
		collider = getCollidingObject();
		removeBricks();
	}

	/*
	 * Method: removeBricks
	 * This method removes the bricks or hits off the paddle. 
	 */

	private void removeBricks() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");	
		// If the ball hits the paddle, using the absolute value function ensures that 
		// the ball will always go up rather than get stuck moving up and down within 
		// the paddle. 
		if (collider == paddle) {
			bounceClip.play(); //adds sound.
			vy = - Math.abs(vy); 

			// This indicates that there is a brick present, and thus it is removed 
			// and the ball can move appropriately.
		} else if (collider != null) {
			remove(collider);	
			bounceClip.play();
			vy = -vy; 
			// This variable counts the number of bricks left. 
			numberOfBricks = numberOfBricks - 1;
		}	
	}

	/*
	 * Method: getCollidingObject
	 * This method returns the 4 possible locations, determined by 
	 * each "corner" of the ball, 
	 * of the object where the ball may hit an object. 
	 */

	private GObject getCollidingObject() {
		if (	getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());

		} else if (getElementAt (ball.getX() + BALL_SIZE, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_SIZE, ball.getY());

		} else if (getElementAt(ball.getX() + BALL_SIZE, ball.getY() + BALL_SIZE) != null) {
			return getElementAt(ball.getX() + BALL_SIZE, ball.getY() + BALL_SIZE); 

		} else if (getElementAt(ball.getX(), ball.getY() + BALL_SIZE) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_SIZE);

		} else {		
			return null; // There is no object (collider) there. 
		}
	}

	/*
	 * Method: mouseMoved
	 * This method allows the paddle to move with the user's mouse. 
	 */

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX() - PADDLE_WIDTH/2; // This centers the X coordinate of the mouse onto the paddle. 
		double mouseY = getHeight() - PADDLE_Y_OFFSET; 

		// This ensures the paddle does not go off the window screen. 
		if (mouseX < 0) { 
			paddle.setLocation(0, mouseY);
		} else if (mouseX + PADDLE_WIDTH > getWidth()) { 
			paddle.setLocation(getWidth() - PADDLE_WIDTH, mouseY);
		} else {
			paddle.setLocation(mouseX, mouseY);
		}
	}
}



