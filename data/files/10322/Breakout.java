/*
 * File: Breakout.java
 * -------------------
 * Name: Shahpar Mirza
 * Section Leader: Ruiqi Chen
 * 
 * This file will implement the game of Breakout.
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


	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int lives = 3;
	private int bricksLeft = NBRICK_COLUMNS*NBRICK_ROWS;
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");







	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		buildBricks();				
		paddle = makePaddle();
		addPaddleToCenter(); 	// starts paddle in center of console
		addMouseListeners();
		play();					// includes all that is needed for functionality and interaction within the game
	}

/* Method: buildBricks();
 * Precondition: Constants of NBRICK_ROWS and NBRICK_COLUMNS is set for a Breakout world. World is empty
 * Postcondition: Lays colorful bricks down (2 rows per color) in rainbow repeating fashion.
 */
	private void buildBricks() {
		for(int row = 0; row < NBRICK_ROWS; row++) {						
			for(int brickNum = 0; brickNum < NBRICK_COLUMNS; brickNum++) { 						
				double oneOver = brickNum * (BRICK_WIDTH + BRICK_SEP);
				double x = oneOver + (getWidth() - NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) + BRICK_SEP)/2;
				double y = row * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET;	
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				int rowColor = row/2 % 5;
				if (rowColor == 0) {
					brick.setColor(Color.RED);
				} 
				if (rowColor == 1) {
					brick.setColor(Color.ORANGE);
				} 
				if (rowColor == 2) {
					brick.setColor(Color.YELLOW);
				} 
				if (rowColor == 3) {
					brick.setColor(Color.GREEN);
				} 
				if (rowColor == 4) {
					brick.setColor(Color.CYAN);
				} 
				add(brick);
			}
		}
	}

// this method sets the paddles movement to the mouse movement
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();  	
		double paddleY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		if (mouseX > PADDLE_WIDTH/2 & mouseX < (getWidth() - PADDLE_WIDTH/2)) { 	// this makes sure that a part of the paddle doesn't go off screen
			paddle.setLocation(mouseX - paddle.getWidth()/2, paddleY);
		}
	}

	
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	
/* Method: addPaddleToCenter();
 * Precondition: Paddle was already made but has not yet been added to the screen. 
 * Postcondition: Paddle is added to the correct Y location and in the center of the console.
 */
	private void addPaddleToCenter() {
		double startX = (getWidth() - PADDLE_WIDTH) / 2;
		double startY = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		add(paddle, startX, startY);
	}

/* Method: play();
 * Precondition: The game of Breakout is set up with all of its parts except for the ball and its functionality.
 * Postcondition: Creates the ball and makes the game of Breakout playable.
 */
	private void play() {
		if (lives != 0) {		// this only adds a ball to the screen again if the player has lives left.
			ball = makeBall();
		}
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while(lives>0) {
			// the following two if statements make the ball bounce off of the walls
			if(hitLeftWall() || hitRightWall()) {
				vx = -vx;
			}
			if(hitTopWall()) {
				vy = -vy;
			}
			ball.move(vx, vy);
			pause(DELAY);
			GObject collider = getCollidingObject(); 	// records object that was collided with by the ball
			if (collider != paddle && collider != null) {	// makes the ball bounce off of the bricks, makes the brick disappear, and records the number of bricks left
				vy = -vy;
				bounceClip.play();	// adds a really ugly sound when a brick is hit
				remove(collider);
				bricksLeft--;
			}
			if (collider == paddle && vy > 0) { 	// these conditions fix the "ball stuck in paddle" bug
				vy = -vy;
			} 
			recordMiss();
			if (bricksLeft == 0) {	// when the last brick is hit, this statement congratulates the winner, and ends the game play
				congratsWinner();
				remove(ball);
				return;
			}
		}
	}
	
	
// this assigns an object to 'collider' for the loop in play(); to recognize
// it checks all for corners of the ball
	private GObject getCollidingObject() {
		GObject collider = getElementAt(ball.getX(), ball.getBottomY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getX(), ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getRightX(), ball.getY());
		if (collider != null) {
			return collider;
		}
		collider = getElementAt(ball.getRightX(), ball.getBottomY());
		if (collider != null) {
			return collider;
		}
		return collider;
	}

// this boolean establishes whether of not the ball is hitting a wall or not (only top, left, and right walls included)
	private boolean hitBottomWall() {
		return ball.getY() > getHeight() - ball.getHeight();
	}
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

// this establishes the ball for the game and starts it in the center of the game
	private GOval makeBall() {
		GOval ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		double ballCenterX = getWidth()/2 - ball.getWidth()/2;
		double ballCenterY = getHeight()/2 - ball.getHeight()/2;
		add(ball, ballCenterX, ballCenterY);
		return ball;
	}

// this acknowledges a miss in the game. Removing the ball, subtracting a life, and only letting the game continue if the player still has lives left
	private void recordMiss() {
		if(hitBottomWall()) {
			remove(ball);
			lives--;
			if (lives > 0) {
				play();
			}
		}
		if(lives<1) {	// this adds a cheeky acknowledgement the player has lost when they have run out of lives
			GLabel lostGame = new GLabel("GET GRekt");
			double lostGameCenterX = getWidth()/2 - lostGame.getWidth()/2;
			double lostGameCenterY = getHeight()/2 - lostGame.getAscent()/2;
			add(lostGame, lostGameCenterX, lostGameCenterY);
		}
	}

/* Method: congratsWinner();
 * Precondition: The player has hit all of the bricks in the game
 * Postcondition: The console tells the winner that they've won by putting a "YOU WIN!" label in the center of the console.
 */
	private void congratsWinner() {
		GLabel wonGame = new GLabel("YOU WIN!");
		double wonGameCenterX = getWidth()/2 - wonGame.getWidth()/2;
		double wonGameCenterY = getHeight()/2 - wonGame.getAscent()/2;
		add(wonGame, wonGameCenterX, wonGameCenterY);
	}
}

