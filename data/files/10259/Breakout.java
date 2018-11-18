/*
 * File: Breakout.java
 * -------------------
 * Name: Alice Ballard-Rossiter
 * Section Leader: Ella Tessier-Lavigne
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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 3.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Random generator for ball movements.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Ball velocity in x and y direction.
	private double vx, vy;

	private GOval ball;

	private GRect paddle;

	// Total number of bricks present.
	int totalBricks = NBRICK_COLUMNS*NBRICK_ROWS;

	// Number of turns (or "lives") left.
	int nTurnsLeft = NTURNS;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp();
		addMouseListeners();

		// The game as a whole ends if either the user has no turns left, or wins.
		// Hence if these conditions hold, a round of the game is played, with the ball
		// resetting to its starting position each round. 
		while(nTurnsLeft > 0 && !gameWon()) {
			resetBall();
			waitForClick();
			playRound();
		}

		// Alerts the user that the game has ended. 
		GLabel announceEnd = new GLabel("End of game.");
		add(announceEnd,(getWidth()- announceEnd.getWidth())/2, (getHeight()- announceEnd.getHeight())/2);
	}

	/*
	 * Method: Play round.
	 * -------------------
	 * The user plays a round of breakout, aiming to delete all the bricks by bouncing the ball
	 * into them with the aid of the paddle. If the ball goes off the bottom of the screen, the
	 * round ends and either the game ends or the number of turns left goes down by one.
	 * Pre: User has turns left, has clicked the mouse, the ball from the previous round (if any)
	 * has been removed and the ball has reset to its initial position in the center of the screen.
	 * Post: see end of method description.
	 */

	private void playRound() {
		while(!endOfRound()) {
			moveBall();
			checkForCollisions();
			pause(DELAY);
		}

		if(!gameWon()) {
			nTurnsLeft--;
			remove(ball);
		}
	}

	// The round ends if the ball is missed by the paddle and goes off the bottom of the screen,
	// or the user has won.
	private boolean endOfRound() {
		return(ballMissed() || gameWon());
	}

	// The user wins by deleting all the bricks on the screen.
	private boolean gameWon() {
		return(totalBricks == 0);
	}

	/*
	 * Method: Reset Ball
	 * -------------------
	 * Sets the ball to its starting position in the centre of the screen at the beginning of each 
	 * round and gives it a random initial velocity within the predefined range for when the ball starts 
	 * to move. 
	 * Pre: Ball is not currently present on the screen and user is about to play a new round so the
	 * number of turns they have left is greater than zero.
	 * Post: Ball is not yet moving but has been added to screen and has a value for its velocity when
	 * it does start to move. 
	 */
	private void resetBall() {
		add(ball);
		ball.setLocation((getWidth() - BALL_RADIUS)/2, (getHeight() - BALL_RADIUS)/2);
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;

		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	/*
	 * Method: Check for collisions 
	 * ----------------------------
	 * Determines what happens if and when a collision occurs between the ball
	 * and another object. If the ball hits the paddle, it bounces
	 * off in the opposite Y direction. If the ball hits a brick, the 
	 * brick disappears and so the total number of bricks left decreases
	 * by one.
	 * Pre: The ball is moving and bricks are present.
	 * Post: see method description. 
	 */
	private void checkForCollisions() {
		GObject collider = getCollidingObject();

		if(collider == paddle) {
			vy = -Math.abs(vy);

		} else if (collider !=null){
			remove(collider);
			totalBricks--;
			vy = -vy;
		}		
	}

	/*
	 * Method: Get Colliding Object
	 * ----------------------------
	 * Checks whether the ball has touched an object, as measured by a touch on one
	 * of the four corners of its bounding box (moving in order clockwise from the 
	 * upper left corner). 
	 */
	private GObject getCollidingObject() {
		double diameter = BALL_RADIUS * 2;
		GObject firstCorner = getElementAt(ball.getX(), ball.getY());
		GObject secondCorner = getElementAt(ball.getX() + diameter, ball.getY());
		GObject thirdCorner = getElementAt(ball.getX(), ball.getY() + diameter);
		GObject fourthCorner = getElementAt(ball.getX() + diameter, ball.getY() + diameter);

		if(firstCorner != null) {
			return firstCorner;
		} else if(secondCorner != null) {
			return secondCorner;
		} else if(thirdCorner != null) {
			return thirdCorner;
		} else if(fourthCorner != null) {
			return fourthCorner;
		} else {
			return null;
		}
	}

	/*
	 * Method: Move Ball
	 * -------------------
	 * Continuously moves the ball around the screen with a random velocity, bouncing off
	 * in the opposite direction if it hits a side wall or the top wall.
	 * Pre: A round is being played and there are bricks left on the screen.
	 * Post: The round and or game has ended.
	 */
	private void moveBall() {
		// update visualization

		if(hitLeftWall() || hitRightWall()) {
			vx = -vx;
		} else if(hitTopWall()) {
			vy = -vy;
		}
		ball.move(vx, vy);
	}

	// Checks whether left wall has been hit by the ball.
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}

	// Checks whether right wall has been hit by the ball.
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - 2*BALL_RADIUS;
	}

	// Checks whether top wall has been hit by the ball.
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}

	// Checks whether the ball was missed by the paddle and subsequently
	// went off the bottom of the screen.
	private boolean ballMissed() {
		return ball.getY() > getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
	}

	/*
	 * Method: Set up.
	 * -------------------
	 * Sets up the screen with a full block of bricks, a ball and a paddle.
	 */
	private void setUp() {
		buildBricks();
		createBall();
		createPaddle();	
	}

	private void buildBricks() {
		double blockWidth = (NBRICK_COLUMNS*BRICK_WIDTH) + ((NBRICK_COLUMNS - 1)*BRICK_SEP);
		// Creates evenly spaced set number of rows of bricks.
		for (int r = 0; r < NBRICK_ROWS; r++) {
			double brickX = (getWidth() - blockWidth)/2;
			double brickY = BRICK_Y_OFFSET + (r*(BRICK_HEIGHT + BRICK_SEP));

			// Creates evenly spaced set number of columns of bricks.
			for(int c = 0; c < NBRICK_COLUMNS; c++) {
				GRect brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				chooseBrickColor(r, brick);
				add(brick);
				brickX += (BRICK_WIDTH + BRICK_SEP);
			}
		}
	}

	/*
	 * Method: Choose brick color
	 * --------------------------
	 * Chooses which color a brick should be based on its row number. There are two 
	 * consecutive  rows for each of the five colors, and then the pattern repeats
	 * for any additional number of rows. 
	 * Pre: A brick has been constructed but without color yet (i.e. is the default black)
	 * and the full block of bricks is not yet complete.
	 * Post: The brick has been assigned the correct color for its row number.
	 */
	private void chooseBrickColor(int r, GRect brick) {
		if ((r % 10) == 0 ||(r % 10) == 1) {
			brick.setColor(Color.RED);
		} else if ((r % 10) == 2 || (r % 10) == 3) {
			brick.setColor(Color.ORANGE);
		} else if ((r % 10) == 4 || (r % 10) == 5) {
			brick.setColor(Color.YELLOW);
		} else if ((r % 10) == 6 || (r % 10) == 7) {
			brick.setColor(Color.GREEN);
		} else if ((r % 10) == 8 || (r % 10) == 9) {
			brick.setColor(Color.CYAN);
		}		
	}

	// Creates a black paddle near the bottom of the screen in the center.
	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, (getWidth()- PADDLE_WIDTH)/2, (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET));
	}

	// Makes the paddle move horizontally according to the mouse's x location. 
	// Pre: A paddle has been created on the screen 
	// Post: Paddle moves horizontally with the mouse.
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		if (x >=0 && x<(getWidth()-PADDLE_WIDTH)) {
			paddle.setLocation(x, y);
		}
	}

	// Creates a solid black ball.
	public void createBall() {
		double diameter = BALL_RADIUS * 2;
		ball = new GOval(diameter, diameter);
		ball.setFilled(true);
	}
}

