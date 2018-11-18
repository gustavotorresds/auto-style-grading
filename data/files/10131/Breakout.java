/*
 * File: Breakout.java
 * -------------------
 * Name:Yahui Zhu
 * Section Leader: Ben Allen
 * 
 * This file implement the game of Breakout.
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

	// Dimensions of the paddle, in pixels
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom, in pixels 
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

	// Number of turns in one game
	public static final int NTURNS = 3;
	
	//Number of bricks
	public static final int BRICK_NUM = NBRICK_COLUMNS * NBRICK_ROWS;
	
	//Instance variables
	private double mouseX;
	private GRect bricks;
	private GRect paddle;
	private double paddleX, paddleY;
	private double vx, vy;
	private double ballX, ballY;
	private GObject collider;
	private int remainingBrick;
	
	//Set up Random Generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpBricks();
		createPaddle();
		
		// Add mouse listeners
		addMouseListeners();
		
		playGame();
	}
			


/*
 * Method: Set Up Bricks
 * Set up the bricks in the game in different colors with given height and width
 */
	private void setUpBricks() {
		double startX = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH -(NBRICK_COLUMNS - 1) * BRICK_SEP) / 2;
		double startY = BRICK_Y_OFFSET;
		for (int row = 1; row <= NBRICK_ROWS; row++) {
			for (int column = 1; column <= NBRICK_COLUMNS; column++) {
				double brickX = startX + (column - 1) * (BRICK_WIDTH + BRICK_SEP);
				double brickY = startY + (row -1) * (BRICK_HEIGHT + BRICK_SEP);
				bricks = new GRect (brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				bricks.setFilled(true);
				if (row == 1 || row == 2) {
					bricks.setColor(Color.RED);
				}
				if (row == 3 || row ==4) {
					bricks.setColor(Color.ORANGE);
				}
				if (row == 5 || row ==6) {
					bricks.setColor(Color.YELLOW);
				}
				if (row == 7 || row == 8) {
					bricks.setColor(Color.GREEN);
				}
				if (row == 9 || row == 10) {
					bricks.setColor(Color.CYAN);	
				}
				add (bricks);
			}
		}		
	}
	
/*
 *  Method: Create Paddle
 *  Create the paddle in the center of the screen with
 *  given width and height
 */
	private void createPaddle() {
		double centerX = (getWidth() - PADDLE_WIDTH) / 2;
		paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect (centerX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add (paddle);		
	}

/*
 * Method: mouseMoved
 * Create MouseEvent, make the paddle move in the X axis following the mouse
 * Special condition: make sure the paddle stays within the boundary of the screen
 */
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		paddleX = mouseX;
		if (mouseX <= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(mouseX, paddleY);
		} else {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, paddleY);
		}
	}

	
/*
 * Method: playGame
 * This method instructs the user to play the game. Each game has three turns.
 * If there are remaining bricks after the third turn, the screen will displays
 * "Game Over". If the user clears all the bricks, the game ends and shows "You win".
 */
	private void playGame() {
		
	// Count the number of remaining bricks
		remainingBrick = BRICK_NUM;
		
	// Determine the turns of the game
		for (int turn = 0; turn < NTURNS; turn++) {
			GOval ball = makeBall();
			
	// Make the velocity of X random numbers in a given range
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			
			vy = VELOCITY_Y;
			waitForClick();
			
	// Two conditions to keep the game running: there are remaining bricks and the ball doesn't hit the bottom wall
			while (remainingBrick > 0  && notHitBottomWall(ball)) {
			
	// Bounce the ball when it hits surrounding walls and the paddle 
				bounceBall(ball);
				
	// If the ball collides with the bricks, then remove them
				removeBrick(ball);
	
				ball.move(vx, vy);				
				pause(DELAY);	
			}
	
	// When a turn ends, remove the ball to prepare for next turn
			remove(ball);
			
	// If there are no remaining brick in the turn, the game ends and displays "You win"
			if (remainingBrick == 0) {
				GImage img = new GImage("you-win.PNG");
				add(img, getWidth()/2 - img.getWidth()/2, getHeight()/2-img.getHeight()/2);
				return;
			}
		}
	// If there are still remaining bricks after three turn, the game ends and displays "Game over"
		if(remainingBrick != 0) {
			GImage img = new GImage("game-over.png");
			add(img,getWidth()/2 - img.getWidth()/2, getHeight()/2-img.getHeight()/2);
			return;
		}						
	}
	
/*
 *  Method: Make Ball
 *  Set up the ball in the game starting in the center of the 
 *  screen. Return the ball for animation.	
 */
	public GOval makeBall() {
		double ballCenterX = getWidth() / 2 - BALL_RADIUS;
		double ballCenterY = getHeight() / 2 - BALL_RADIUS;
		double size = BALL_RADIUS * 2;
		GOval r = new GOval (size, size);
		r.setFilled(true);
		r.setColor(Color.BLACK);
		add (r, ballCenterX, ballCenterY);
		return r;
	}

/*
 * Method: Bounce Ball
 * Make the ball bounce if it hits the surrounding walls (except bottom wall) and the paddle
 */
	private void bounceBall(GOval ball) {
		if (hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if (hitTopWall(ball) ||(hitPaddle(ball))) {
			vy = -vy;
		}		
	}
	
/*
 * Method: Remove Brick
 * Remove a brick if the ball hits it, check the four corner of the ball.
 */
	private void removeBrick(GOval ball) {
		ballX = ball.getX();
		ballY = ball.getY();
		if (checkUpperLeftCorner(collider) || checkUpperRightCorner(collider) || checkLowerLeftCorner(collider) || checkLowerRightCorner(collider)) {
			vy = -vy;
			remainingBrick = remainingBrick - 1;
		}		
	}
	
/*
 *  Method: not Hit Bottom Wall
 *  Returns whether or not the given ball should bounce off
 *  of the bottom wall of the window. If the ball doesn't
 *  hit the bottom wall, returns true.
 */
	private boolean notHitBottomWall(GOval ball) {
		return ball.getY() < getHeight() - BALL_RADIUS * 2;
	}

/*
 *  Method: Hit Left Wall
 *  Returns whether or not the given ball should bounce off
 *  of the left wall of the window. If the ball hits the 
 *  left wall, returns true.
 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

/*
*  Method: Hit Right Wall
*  Returns whether or not the given ball should bounce off
*  of the right wall of the window. If the ball hits the 
*  right wall, returns true.
*/
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

/*
 * Method: Hit Top Wall
 * Returns whether or not the given ball should bounce off
 * of the top wall of the window. If the ball hits the 
 * top wall, returns true.
 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

/*
 * Method: Hit Paddle
 * Returns whether or not the given ball should bounce off
 * of the paddle.
 * Special condition: make sure the ball won't get "glued"
 * to the paddle by checking its Y velocity and its
 * x location compared to the paddle
 */
	private boolean hitPaddle(GOval ball) {
		return vy > 0 && ball.getY() > paddleY - BALL_RADIUS * 2 && ball.getY() < paddleY && ball.getX() >= paddle.getX() - BALL_RADIUS * 2 && ball.getX() <= paddle.getX() + PADDLE_WIDTH;
	}
	

/*
 *  Method: Check Upper Left Corner
 *  Returns whether or not the upper left corner of the ball should
 *  collide with the object and remove it.
 */
	private boolean checkUpperLeftCorner(GObject collider) {
		collider = getElementAt(ballX, ballY);
		return removeCollider(collider);
	}

/*
 * Method: Check Upper Right Corner
 * Returns whether or not the upper right corner of the ball should
 * collide with the object and remove it. 
 */
	private boolean checkUpperRightCorner(GObject collider) {
		collider = getElementAt(ballX + BALL_RADIUS * 2, ballY);
		return removeCollider(collider);
	}

/*
 * Method: Check Lower Left Corner
 * Returns whether or not the lower left corner of the ball should
 * collide with the object and remove it.
 */
	private boolean checkLowerLeftCorner(GObject collider) {
		collider = getElementAt(ballX, ballY + BALL_RADIUS * 2);
		return removeCollider(collider);
	}

/*
 * Method: Check Lower Right Corner
 * Returns whether or not the lower right corner of the ball should
 * collide with the object and remove it.
 */
	private boolean checkLowerRightCorner(GObject collider) {
		collider = getElementAt(ballX + BALL_RADIUS * 2, ballY + BALL_RADIUS * 2);
		return removeCollider(collider);
	}

/*
 * Method: Remove Collider
 * Returns whether or not the given collider should be removed. If the collider isn't
 * null nor the paddle, it would be removed.
 */
	private boolean removeCollider(GObject collider) {
		boolean hitBrick = collider != null && collider != paddle;
		if (hitBrick) {
			remove(collider);
		}
		return hitBrick;
	}
}