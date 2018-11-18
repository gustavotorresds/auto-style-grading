/*
 * File: Breakout.java
 * -------------------
 * Name: Angie Lee
 * Section Leader: Julia Daniel
 * 
 * This file implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private double vx;

	private double vy;

	private GLabel label = new GLabel("");

	// Initializes brick counter
	private int numberOfBricks = 100; 

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		playGame();
	}

	/* This method allows the user to play the game, giving the user 
	 * 3 lives to break all the bricks, otherwise printing the losing message. 
	 */
	private void playGame() {
		for(int turn = 0; turn < NTURNS; turn++) {
			if(numberOfBricks == 0) {
				printWinningMessage();
				break; // exits the loop if no bricks are left
			}
			getBallBouncing();
		}
		if(numberOfBricks != 0) {
			remove(ball); 
			printLosingMessage(); 	
		} else {
			printWinningMessage();
		}
	}

	/* This method checks if there is an object where a collision occurred at the
	 * four "corners" surrounding the ball, and returns that object if so. 
	 */
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if(getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY()) != null) {
			return getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		} else if(getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS)) != null) {
			return getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
		} else if(getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS))
				!= null) {
			return getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		} else {
			return null;
		} 
	}

	/* This method makes the ball bounce around the world, makes the ball
	 * bounce off the paddle, and removes bricks if the ball collides with them.
	 */
	private void getBallBouncing() {
		makeBallMove(); 
		
		// makes ball bounce around the wall and ends turn if it hits the bottom.
		while(true) {
			ball.move(vx, vy);
			if(hitsTopWall(ball)) {
				vy = - vy;
			} 
			else if(hitsRightWall(ball) || hitsLeftWall(ball)) {
				vx = - vx;
			}
			else if(hitsBottomWall(ball)) {
				break;
			}
		
		// checks for ball's collisions with objects
			GObject collider = getCollidingObject();
			if(collider != null && collider == paddle) {
				vy = -1 * Math.abs(vy); 
			// Using the absolute value allows for the ball to always move upwards when it hits the paddle.
			} else if(collider != null) {
				remove(collider);
				numberOfBricks = numberOfBricks - 1; // counts bricks left
				vy = -vy;
				if(numberOfBricks == 0) {
					remove(ball);
					printWinningMessage();
					break;
				}
			}
			pause(DELAY);
		}
	}

	/* This method adds a label to the screen indicating when the player loses. */ 
	private void printLosingMessage() {
		label.setFont("Courier-30");
		label.setColor(Color.RED);
		label.setLabel("GAME OVER. YOU LOSE.");
		add(label, (getWidth() - label.getWidth()) / 2, 
				(getHeight() - label.getHeight()) / 2);
	}

	/* This method adds a label to the screen indicating when the player wins. */ 
	private void printWinningMessage() {
		label.setFont("Courier-20");
		label.setColor(Color.GREEN);
		label.setLabel("CONGRATULATIONS! YOU WIN!");
		add(label, (getWidth() - label.getWidth()) / 2, 
				(getHeight() - label.getHeight()) / 2);
	}

	/* This method finds out if the ball hit the left wall. */
	private boolean hitsLeftWall(GOval ball) {
		return ball.getX() < 0;
	}

	/* This method finds out if the ball hit the right wall. */
	private boolean hitsRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	/* This method finds out if the ball hit the top wall */ 
	private boolean hitsTopWall(GOval ball) {
		return ball.getY() < 0;
	}

	/* This method finds out if the ball goes past the bottom wall. */
	private boolean hitsBottomWall(GOval ball) {
		return ball.getY() > getHeight();
	}


	/* This method makes the ball move down and in a random x-direction when the user clicks. */ 
	private void makeBallMove() {
		ball = makeBall();
		add(ball);
		waitForClick();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
	}
	
	/* This method creates the game ball and sets it initially in the center
	 * of the screen.
	 */
	public GOval makeBall() {
		double diameter = BALL_RADIUS * 2;
		GOval r = new GOval(diameter, diameter);
		r.setFilled(true);
		double x = getWidth() / 2;
		double y = getHeight() / 2;
		add(r, x, y);
		return r;
	}

	/* This method sets up the game so that it can be played, consisting of 
	 * creating rows of bricks at the top of the screen and creating a paddle that 
	 * tracks the mouse.
	 */
	private void setUpGame() {
		setUpBricks();
		paddle = createPaddle();
		addPaddleToScreen(paddle);
		addMouseListeners();
	} 

	/* This method sets up the rows of bricks at the top of the game, using
	 * the dimensions, numbers, and spacing named in the constants. It also
	 * colors accordingly.
	 */
	private void setUpBricks() {
		for(int col = 0; col < NBRICK_COLUMNS; col++) {
			for(int row = 0; row < NBRICK_ROWS; row++) {
				double xcoord = (getWidth() - ((BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS) + BRICK_SEP) / 2;
				double x = xcoord + row * (BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + (col * (BRICK_HEIGHT + BRICK_SEP));
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(rect); 
				rect.setFilled(true);
				if(col < 2) {
					rect.setColor(Color.RED);
				} else if(col > 1 && col < 4) {
					rect.setColor(Color.ORANGE);
				} else if (col > 3 && col < 6) {
					rect.setColor(Color.YELLOW);
				} else if (col > 5 && col < 8) {
					rect.setColor(Color.GREEN);
				} else {
					rect.setColor(Color.CYAN);
				}
			}
		}
	} 

	/* This method tracks the mouse and makes the paddle follow its movements 
	 * in the x-direction. */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = getHeight() - PADDLE_Y_OFFSET;
		paddle.setLocation(mouseX, mouseY);
		if(mouseX > getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, mouseY);
		}
	}

	/* This method adds the instance variable paddle to the screen. */
	private void addPaddleToScreen(GRect paddle) {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}

	/* This method creates the paddle. */ 
	private GRect createPaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

}
