/*
 * File: Breakout.java
 * -------------------
 * Name: Christian Kontaxis
 * Section Leader: James Zhuang 
 * 
 * This program implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 450;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 5;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 5;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS - 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 40;

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

	private GRect paddle; //paddle is instance variable
	private GOval ball; //ball is instance variable
	private double vx, vy; //instance variable for velocity
	private RandomGenerator rgen = RandomGenerator.getInstance(); //random generator
	private int brickCounter = NBRICK_ROWS * NBRICK_COLUMNS; //total number of bricks

	public void run() {
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		constructBricks();
		addMouseListeners();
		for (int i = 0; i < NTURNS; i++) {
			setUpGame();
			waitForClick();
			playGame();
			if (brickCounter == 0 && NTURNS > 0) {
				remove(ball);
				youWin();
				break;
			}
		}
		if (brickCounter != 0) {
			youLose();
		}
	}
	
	/*
	 * Sets up the paddle and ball initially and after every turn is lost.
	 */
	private void setUpGame() {
		constructPaddle();
		constructBall();
	}
	
	/*
	 * THis method sets sequential rows of bricks. X location is updated by
	 * adding the brick width and the brick separation between each subsequent
	 * brick. 
	 */
	private void constructBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) { //number of rows
			for (int bricksInRow = 0; bricksInRow < NBRICK_COLUMNS; bricksInRow++) { //bricks per row
				double x = BRICK_SEP/2 + ((BRICK_WIDTH + BRICK_SEP) * bricksInRow);
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row;
				GRect brick = new GRect(x, y + BRICK_Y_OFFSET, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick);
				colorBricks(row, brick);
			}
		}
	}
	
	/*
	 * colorBricks uses the modulus function to color rows of bricks
	 * in twos (according to the rainbow).
	 */
	private void colorBricks(int row, GRect brick) {
		if (row % 10 == 0 || row % 10 == 1) {
			brick.setColor(Color.RED);
		} else if (row % 10 == 2 || row % 10 == 3) {
			brick.setColor(Color.ORANGE);
		} else if (row % 10 == 4 || row % 10 == 5) {
			brick.setColor(Color.YELLOW); 
		} else if (row % 10 == 6 || row % 10 == 7) {
			brick.setColor(Color.GREEN);
		} else if (row % 10 == 8 || row % 10 == 9) {
			brick.setColor(Color.CYAN);
		}
	}
	
	/*
	 * constructPaddle constructs a filled GRect at
	 * the bottom of the screen.
	 */
	private void constructPaddle() {
		double x = getWidth()/2 - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/*
	 * This function allows the paddle to track the movement of
	 * the mouse. 
	 */
	public void mouseMoved (MouseEvent e) {
		double move = e.getX() - PADDLE_WIDTH/2; //x location of paddle following mouse
		if (move >= 0 && move <= getWidth() - PADDLE_WIDTH) { 
			paddle.setLocation(move, getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET); //sets new location of paddle based on mouse movement
		}
	}
	
	/*
	 * Adds a filled GOval to the center of the screen.
	 */
	private void constructBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}
	
	/*
	 * playGame is the bulk of the program. Here, the user is 
	 * able to run the game, moving the paddle and trying to bounce
	 * the ball into the bricks, until none remain.
	 */
	private void playGame() {
		vy = 5.0; //vertical velocity
		vx = rgen.nextDouble(1.0,3.0); //horizontal velocity (random bounce)
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while(true) {
			ball.move(vx, vy); //moves ball at set velocities
			pause(15); //slows ball down so that user can see it
			collideWithWall();
			collideWithOtherObjects();
			if (brickCounter == 0) {
				removeAll(); //removes everything when game is over
				break;
			}
			if (ball.getY() > getHeight()) {
				remove(paddle); //user has lost a turn
				break;
			}

		}
	}
	
	/*
	 * collideWithWall checks for if the ball bounces off the wall,
	 * and reverses vx and vy accordingly.
	 */
	private void collideWithWall() {
		double x = ball.getLocation().getX();
		if (x <= 0 || (x + BALL_RADIUS*2) >= getWidth()) {
			vx = -vx; //if ball is less than or greater than either side of the wall, reverse horizontal velocity
		}
		if (ball.getY() <= 0 && vy < 0) {
			vy = -vy; // reverse velocity if ball hits top wall
		}
	}
	
	/*
	 * collideWithOtherObjects creates an object called collider.
	 * This objects checks to see if any part of the ball hits another
	 * object. If this collider is not null, the function then checks to 
	 * see if it has collided with the paddle or the bricks. If it collides
	 * with the paddle, it reverses the y direction. If it collides with
	 * the bricks, it reverses the y direction and removes the present brick.
	 */
	private void collideWithOtherObjects() {
		GObject collider = getCollidingObject(); 
		double paddleY = getHeight() - PADDLE_Y_OFFSET - BALL_RADIUS*2 - PADDLE_HEIGHT;
		if (collider != null) {
			if (collider == paddle) {
				if (ball.getY() >= paddleY && vy > 0) {
					vy = -vy;
				}
			} else {
				remove(collider);
				brickCounter--;
				vy = -vy;
			}
		}
	}
	
	/*
	 * getCollidingObject checks for collisions on all sides of the ball.
	 * If it returns not null, then there is an object there. If there is no
	 * object present, method returns null. 
	 */
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		if (getElementAt(x,y) != null) {
			return(getElementAt(x,y));
		} else if (getElementAt(x + BALL_RADIUS*2, y) != null) {
			return(getElementAt(x + BALL_RADIUS*2, y));
		} else if (getElementAt(x, y + BALL_RADIUS*2) != null) {
			return(getElementAt(x, y + BALL_RADIUS*2));
		} else if (getElementAt(x + BALL_RADIUS*2, y + BALL_RADIUS*2) != null) {
			return(getElementAt(x + BALL_RADIUS*2, y + BALL_RADIUS*2));
		} else return null;
	}
	
	/*
	 * youWin displays a centered green label when game finishes and
	 * user has won.
	 */
	private void youWin() {
		GLabel winLabel = new GLabel("You win!");
		winLabel.setColor(Color.GREEN);
		winLabel.setLocation(getWidth()/2 - winLabel.getWidth()/2, getHeight()/2 - winLabel.getAscent()/2);
		add(winLabel);
	}
	
	/*
	 * youLose displays a centered red label when game finishes
	 * and user has lost. 
	 */
	private void youLose() {
		GLabel loseLabel = new GLabel("YOU LOSE. GAME OVER.");
		loseLabel.setColor(Color.RED);
		loseLabel.setLocation(getWidth()/2 - loseLabel.getWidth()/2, getHeight()/2 - loseLabel.getAscent()/2);
		add(loseLabel);
	}
}
