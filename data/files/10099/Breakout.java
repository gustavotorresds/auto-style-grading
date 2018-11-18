/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
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

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// After the essentials of the game are set up,
		// the player will be able to play the game up to 3 turns
		setUpGame();
		for (int i = 0; i<NTURNS; i++) {
			playGame();
			if (brickCounter==0) {
				youWin();
			}
		}
		youLose(); // Once the player takes all 3 turns, the player will lose
	}

	private void setUpGame() {
		buildBricks();
		buildPaddle();
		buildBall();
	}
	
	private GRect brick;
	private void buildBricks() {
		for (int i=0; i<NBRICK_COLUMNS; i++) {
			for (int j=0; j<NBRICK_ROWS; j++) {
				
				// This will start building bricks from the bottom left brick
				double x = getWidth()/2 - (BRICK_WIDTH + BRICK_SEP)*(NBRICK_ROWS/2.0) + BRICK_SEP/2.0 + (BRICK_WIDTH + BRICK_SEP)*j;
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP)*i;
				brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick);
				
				/*
				 * I will set the first two columns (from the top) to be red bricks
				 * Considering that there will be more than 10 columns (e.g. 11 columns),
				 * the same pattern should repeat again, starting with the red columns.
				 * Therefore, the color of bricks will change depending on the remainder of i/10
				 */
				if (i%10 < 2) {
					brick.setColor(Color.RED);
				}
				if (2 <= i%10 && i%10 <= 3) {
					brick.setColor(Color.ORANGE);
				}
				if (4 <= i%10 && i%10 <=5) {
					brick.setColor(Color.YELLOW);
				}
				if (6 <= i%10 && i%10 <=7) {
					brick.setColor(Color.GREEN);
				}
				if (8 <= i%10 && i%10 <=9) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}

	private GRect paddle;
	private void buildPaddle() {
		// Initially build paddle in the center of the canvas
		// The height is set according to "Y_OFFSET"
		double x = getWidth()/2.0 - PADDLE_WIDTH/2.0;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		
		// This allows the paddle to respond to mouse movement later on
		addMouseListeners();
	}
	
	/*
	 * The mouse detects the midpoint of the paddle
	 * In the given range below, the paddle will move horizontally
	 * according to the movement of the mouse
	 */
	public void mouseMoved(MouseEvent e) {
		if((e.getX() < getWidth() - PADDLE_WIDTH/2.0) && (e.getX() > PADDLE_WIDTH/2.0)) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
	
	// This will initially build a ball in the center of the canvas
	private GOval ball;
	private void buildBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS*2.0, BALL_RADIUS*2.0);
		ball.setFilled(true);
		add(ball);
	}
	
	// This instance variable "rgen" allows the ball to move in random directions 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	private void playGame() {
		getBallSpeed();
		waitForClick(); // Game starts once the player clicks on the screen
		
		/*
		 * The ball will move while it doesn't touch the ground
		 * If the player breaks all the bricks and there are no more bricks left,
		 * the loop will stop
		 * After each failure (when ball touches the ground),
		 * the ball will return to its original position in the center of the screen
		 */
		while (ball.getY() <= getHeight() - BALL_RADIUS*2.0) {
			moveBall();
			if (brickCounter == 0) {
				break;
			}
		}
		ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
	}
	
	// The ball will bounce off the walls and the ceiling of the canvas
	// And check for collisions while moving
	private double vx, vy;
	private void moveBall() {
		if ((ball.getX() <= 0) || (ball.getX() >= getWidth() - BALL_RADIUS*2.0)) {
			vx = -vx;
		}
		if (ball.getY() <= 0) {
			vy = -vy;
		}
		checkForCollision();
		ball.move(vx, vy);
		pause(DELAY);
	}
	
	// Set the velocity of the ball in x and y directions
	// The ball can go in two different directions 
	private void getBallSpeed() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	
	/*
	 * Consider the four different points of the square encompassing the ball
	 * that can collide with any object within the canvas
	 * Collider is defined according to the four points 
	 */
	private GObject getCollidingObject() {
		GObject collider = null;
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			collider = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()) != null) {
			collider = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		}
		return collider;
	}

	private void checkForCollision() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			/*
			 * Perhaps the ball collides to the side of the paddle
			 * The ball will attempt to move up, but it will detect part of the paddle
			 * and go back down because of the reversal of the vy sign
			 * And this procedure will repeat until the the ball doesn't encounter a collider
			 * However, we want the ball to move up even if it touches the side of the paddle
			 * By finding the absolute value of negative vy, vy will always be negative,
			 * which means the ball will move up even if it collides to the side
			 */
			vy = -Math.abs(vy);
			
		// A brick will be removed each time a ball collides to it
		} else if (collider != null) {
			remove(collider);
			brickCounter --;
			vy = -vy;
		}
	}
	
	// Counting the total number of bricks
	private int brickCounter = NBRICK_COLUMNS*NBRICK_ROWS;
	
	// Insert GLabel message "YOU WIN!" in the middle of the canvas
	private void youWin() {
		GLabel glabel = new GLabel("YOU WIN!");
		double x = getWidth()/2 - glabel.getWidth()/2.0;
		double y = getHeight()/2 - glabel.getWidth()/2.0;
		add (glabel, x, y);
	}
	
	// Insert GLabel message "YOU LOSE :(" in the middle of the canvas
	private void youLose() {
		GLabel glabel = new GLabel("YOU LOSE :(");
		double x = getWidth()/2 - glabel.getWidth()/2.0;
		double y = getHeight()/2 - glabel.getWidth()/2.0;
		add (glabel, x, y);
	}
}
