/*
 * File: Breakout.java

 * -------------------
 * Name: Avani Singh
 * Section Leader: Chase Davis 
 * 
 * This file implements the game of Breakout.
 * 
 * Extensions added: Set up effect, sounds, click before starting, messages.
 * I discussed some ideas with Edouard Asmar before coding the game.
 * All of the code is mine. He helped with a few bugs and one idea as mentioned below.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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

	// Instance variable for paddle
	private GRect paddle; 
	// Instance variable for ball
	private GOval ball;
	// Instance variable for ball velocity 
	private double vx, vy = VELOCITY_Y;

	// Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Total bricks in the game (+1 for keeping accurate count)
	private int brickCount = (NBRICK_COLUMNS * NBRICK_ROWS) + 1;

	// Extension: Variable to add sound 
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// The game of BreakOut.
		setUpGame();
		playGame();
	}

	private void setUpGame () { 
		setUpBricks();
		setUpPaddle();
	}

	private void setUpBricks() { 
		// ensures that the bricks will be set for the desired number of rows
		for (int row = 0; row < NBRICK_ROWS; row ++) {
			// defines the color of the row
			Color c = getBrickColor(row);
			// ensures that columns of bricks are set up 
			for (int col = 0; col < NBRICK_COLUMNS; col ++) {
				// coordinates of the brick
				double x = getWidth()/2 - ((NBRICK_COLUMNS - 1)*BRICK_SEP)/2 - (BRICK_WIDTH * NBRICK_COLUMNS)/2 + col*BRICK_WIDTH + col*BRICK_SEP; 
				double y = BRICK_Y_OFFSET + row*BRICK_HEIGHT + row*BRICK_SEP;
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setColor(c);
				brick.setFilled(true);
				add(brick);
				pause(10); // based on Edouard Asmar's suggestion. Gives a cool set-up effect
			}
		}	
	}

	/*
	 * this method gets the color of the bricks in each row 
	 * the color of the bricks change according to the rows 
	 * the first fifth (that is the first 2 rows are set red)
	 * the second fifths, orange, and so on..
	 */
	private Color getBrickColor(int row) {
		if (row < NBRICK_ROWS/5) {
			return Color.RED;
		} else if (row < (2*NBRICK_ROWS)/5) {
			return Color.ORANGE;
		} else if (row < (3*NBRICK_ROWS)/5) {
			return Color.YELLOW;
		} else if (row < (4*NBRICK_ROWS)/5) {
			return Color.GREEN;
		} else if (row < NBRICK_ROWS) {
			return Color.CYAN;
		} else {
			return null;
		}
	}

	private void setUpPaddle() {
		// initial location of the paddle in the bottom center of the screen
		paddle = new GRect (getWidth()/2 - PADDLE_WIDTH/2, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}

	private void setUpBall() {
		ball = new GOval (getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
	}

	private void playGame() { 
		beginningText();
		for (int i = 0; i<NTURNS; i++) {
			if (brickCount != 0) {
				setUpBall();
				waitForClick(); // extension: ball only appears when user clicks
				moveBall();
			}
		}
		if (brickCount == 0) {
			winningText();
		} else {
			losingText();
		}
	}

	// extension: label at the beginning of the game probing the user to begin playing
	private void beginningText() {
		GLabel label = new GLabel ("Click To Begin!", 0, 0);
		label.setColor(Color.BLUE);
		add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 + label.getHeight()*2);
	}

	// extension: display text if player wins
	private void winningText() {
		GLabel label = new GLabel ("Congrats, you won!", 0, 0);
		label.setColor(Color.BLUE);
		add (label, getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getHeight()/2);
	}

	// extension: display text if player loses 
	private void losingText() {
		GLabel label = new GLabel ("Sorry, game over :(", 0, 0);
		label.setColor(Color.BLUE);
		add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getHeight()/2);
	}

	private void moveBall() {
		vx = randomX();
		while (true) { 
			ball.move(vx, vy);
			pause(DELAY);
			if (checkEdges()) { // if the ball has disappeared
				return; // end the method of 'move ball'
			}
			// checks if the ball hit the bricks or paddle
			checkForCollisions();
			if (brickCount == 0) {
				return; // ends the program if there are no more bricks left
			}
		}
	}

	// returns a random x so that the direction of the ball changes each time
	private double randomX() {
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;	
		return vx;
	}

	// to make the ball bounce off the walls and fall through the bottom
	private boolean checkEdges() {
		double x = ball.getX();
		double y = ball.getY();
		/* 
		 * this if statement checks for the left and right edges of the canvas
		 * if the ball hits either side, it changes the x direction of the ball
		 */
		if (x < 0 || (x + 2*BALL_RADIUS) > getWidth()) {
			vx = -vx;
		}
		/*
		 * checks for the top and bottom edges of the canvas
		 * deals with the exception case of the bottom
		 * the ball falls through and is removed when it hits the bottom
		 */
		if (y < 0) {
			vy = -vy;
		} else if ((y + 2*BALL_RADIUS) > getHeight()) {
			remove(ball);
			return true; 
		} 

		return false;
	}

	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		/* 
		 * changes direction of ball if the object is paddle
		 * checks only for the bottom of the ball to avoid the ball getting "glued" to the paddle
		 */
		if (collider == paddle && vy > 0) {
			vy = -vy;
			bounceClip.play(); // sound when ball hits paddle
		} else if (collider != null) {
			remove (collider);
			vy = -vy; // to make sure the direction of the ball changes after it hits bricks
			brickCount -- ; // keeps track of the number of bricks removed 
			bounceClip.play(); // sound when ball hits bricks
		}
	}

	private GObject getCollidingObject() {
		// the four corner coordinates of the ball
		double ballLeftX = ball.getX();
		double ballTopY = ball.getY();
		double ballRightX = ballLeftX+ 2*BALL_RADIUS;
		double ballBottomY = ballTopY + 2*BALL_RADIUS;
		// checking each corner for object
		GObject topLeft = getElementAt (ballLeftX, ballTopY);
		GObject bottomLeft = getElementAt (ballLeftX, ballBottomY);
		GObject topRight = getElementAt (ballRightX, ballTopY);
		GObject bottomRight = getElementAt (ballRightX, ballBottomY);
		// returns the object that the ball hits 
		if (topLeft != null) {
			return topLeft;
		} else if (bottomLeft != null) {
			return bottomLeft;
		} else if (topRight != null) {
			return topRight;
		} else if (bottomRight != null) {
			return bottomRight;
		} else {
			return null;
		}
	}

	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		// the if statement ensures that the paddle stays at the edges when the mouse is moved to the far left and right corners
		if (mouseX <= getWidth() - PADDLE_WIDTH / 2 && paddle != null && mouseX >= PADDLE_WIDTH/2) {
			paddle.move (mouseX - paddle.getX() - PADDLE_WIDTH/2, 0); //moves the paddle according to the mouse's x coordinates
		} 
	}
}
