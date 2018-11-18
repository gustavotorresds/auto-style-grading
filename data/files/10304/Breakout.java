/*
 * File: Breakout.java
 * -------------------
 * Name: Kristel Bugayong
 * Section Leader: Chase Davis
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

	private GRect brick = null;

	private GRect paddle = null;

	private GOval ball = null;

	private double vx, vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		buildBricks();
		addPaddle();
		addMouseListeners();
		addBall();
		vy = 3.0;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		int turnsLeft = NTURNS;
		int brickCount = 0;
		int nPaddleCollisions = 0; //the number of times the ball collided with paddle
		while (true) {	
			double ballX = ball.getX();
			double ballY = ball.getY();
			if(ballY > getHeight() - (BALL_RADIUS * 2)) { //if the ball moves past the bottom of the screen
				turnsLeft--;
				remove(ball);
				if (turnsLeft > 0) {
					println("Turns Left: " + turnsLeft);
					add(ball, getWidth()/2, getHeight()/2);
				} else { //if there are no turns left
					remove(ball);
					println("Game Over");
					break;
				}
			}
			ballX = ball.getX();
			ballY = ball.getY();
			if(ballY <= 0) vy = -vy;
			if(ballX >= getWidth() - (BALL_RADIUS * 2) || ballX <= 0) vx = -vx;
			ball.move(vx, vy); 
			pause(DELAY);

			GObject collider = getCollidingObject();
			
			if (collider == paddle) {
				if (nPaddleCollisions < 1) { //if the ball hasn't collided with the paddle before, it will bounce.
					vy = -vy;
				}
				nPaddleCollisions++; //increase number of times the ball has collided with the paddle
			} 
			/*after the ball has bounced off the paddle once and is away from the paddle,
			* the number of paddle collisions will reset to 0, and it will be able to bounce off the paddle again.
			* this is to prevent the ball from sticking to the paddle after one bounce.
			*/
			if (collider == null) nPaddleCollisions = 0; 
			if (collider != null && collider != paddle){
				brickCount++;
				remove(collider);
				if (brickCount == NBRICK_COLUMNS * NBRICK_ROWS) {//if all of the bricks are removed
					remove(ball);
					println("you win!");
					break;
				}
				vy = -vy;
			}
		}

	}
	//detects objects at the four corners that border the ball.
	//if there is an object in any of the corners, getCollidingObject() returns that object.
	private GObject getCollidingObject() {
		GObject obj = getElementAt(ball.getX(), ball.getY()); //check upper left corner
		if (obj != null) return obj;
		obj = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY()); //check upper right corner
		if (obj != null) return obj;
		obj = getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS)); //check lower left corner
		if (obj != null) return obj;
		obj = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS)); //check lower right corner
		if(obj != null) return obj;
		return obj; //if null at all corners, return null

	}
	//adds the ball to the center of the screen
	private void addBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, getWidth()/2 , getHeight()/2);
	}
	//the paddle moves horizontally based on mouse movement
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		if(x  >= 0 && x < (getWidth() - PADDLE_WIDTH)) {
			paddle.setLocation(x, y);
		}
	}
	//adds the paddle to the screen
	private void addPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, 0, getHeight() - PADDLE_Y_OFFSET);
	}

	/*first, center the blocks by putting them in a frame.
	 *1. build the frame
	 *2. add the frame so that it is centered on the x-axis and is offset by BRICK_Y_OFFSET
	 *3. add bricks into frame
	 *4. remove the frame
	 */
	private void buildBricks() {
		int halfCanvasWidth = getWidth()/2;
		double totalSeparations = (BRICK_SEP * 9);
		GRect frame = new GRect(BRICK_WIDTH * NBRICK_COLUMNS + totalSeparations, BRICK_HEIGHT * NBRICK_ROWS + totalSeparations);
		double halfFrameWidth = (frame.getWidth()/2);
		double frameXPosition = halfCanvasWidth - halfFrameWidth;
		double frameYPosition = BRICK_Y_OFFSET;
		frame.setFilled(false);
		frame.setColor(Color.WHITE);
		add(frame, frameXPosition, frameYPosition);		

		for(int r = 0; r < NBRICK_ROWS; r++) {
			for(int c = 0; c < NBRICK_COLUMNS; c++) {
				brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (r < 2) {
					brick.setColor(Color.RED);
				} else if (r >= 2 && r < 4) {
					brick.setColor(Color.ORANGE);
				} else if (r >= 4 && r < 6) {
					brick.setColor(Color.YELLOW);
				} else if (r >= 6 && r < 8){
					brick.setColor(Color.GREEN);
				} else {
					brick.setColor(Color.CYAN);
				}
				add(brick, frameXPosition + (BRICK_WIDTH * c) + (BRICK_SEP * c), frameYPosition + (BRICK_HEIGHT * r)+ (BRICK_SEP * r));
			}
		}
		remove(frame);
	}
}
