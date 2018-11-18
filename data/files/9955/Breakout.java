/*
 * File: Breakout.java
 * -------------------
 * Name: Ayush Jain
 * Section Leader: Meng Zhang
 * 
 * Runs the game of Breakout.
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
	public static final double DELAY = 450.0 / 60.0; // Optimized for better speed and more fun!

	// Number of turns 
	public static final int NTURNS = 3;

	// instance variables for paddle
	private GRect paddle;
	
	// instance variable for velocity of ball
	private double vx, vy;
	
	// instance variable for random generation of x velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// instance variable for turns played
	private int i;
	
	// instance variable for turns played
	private int brickCount;
		
	public void run() {
		setTitle("CS 106A Breakout_Ayush Jain");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		i = 1; // First turn
		brickCount = NBRICK_ROWS * NBRICK_COLUMNS;
		while (i <= NTURNS && brickCount > 0) {
			playGame();
		}
		if (i > NTURNS) {
			gameOver();
		}
		else {
			youWon();
		}
		addMouseListeners();
	}

	private void setUpGame() {
		setUpBricks();
		setUpPaddle();
	}
	
	private void setUpBricks() {
		double cx = getWidth()/2; // Measures x-center of window
		double NBRICK_COLUMNS_DOUBLE = NBRICK_COLUMNS;
		for(int j=1;j<=NBRICK_ROWS;j++) { // Builds multiple rows
			for(int i=1;i<=NBRICK_COLUMNS;i++) { // Builds one row
				double startX = cx-NBRICK_COLUMNS_DOUBLE/2*BRICK_WIDTH - (NBRICK_COLUMNS_DOUBLE/2-0.5)*BRICK_SEP;	
				if (i > 1) {
					startX = cx-(NBRICK_COLUMNS_DOUBLE/2-i+1)*BRICK_WIDTH - (NBRICK_COLUMNS_DOUBLE/2-0.5-i+1)*BRICK_SEP; 
				}
				double startY = BRICK_Y_OFFSET+(j-1)*BRICK_HEIGHT+(j-1)*BRICK_SEP; // Calculates starting height
				GRect rect = new GRect(startX,startY,BRICK_WIDTH,BRICK_HEIGHT);
				rect.setFilled(true);
				
				// Following lines determine which color each row should get (follows sequential color pattern)
				if (j % 10 >= 1 && j % 10 <= 2) { 
					rect.setColor(Color.RED);
				}
				if (j % 10 >= 3 && j % 10 <= 4) {
					rect.setColor(Color.ORANGE);
				}
				if (j % 10 >= 5 && j % 10 <= 6) {
					rect.setColor(Color.YELLOW);
				}
				if (j % 10 >= 7 && j % 10 <= 8) {
					rect.setColor(Color.GREEN);
				}
				if (j % 10 == 9 || j % 10 == 0) {
					rect.setColor(Color.BLUE);
				}
				add(rect);
			}
		}	
	}

	// sets up starting position of paddle
	private void setUpPaddle() {
		double cx = getWidth()/2;
		double paddleX = cx - PADDLE_WIDTH/2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT/2;
		paddle = new GRect(paddleX,paddleY,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	// mouse event that tracks mouse to determine paddle position 
	public void mouseMoved(MouseEvent paddleTrack) {
		double x = paddleTrack.getX() - PADDLE_WIDTH/2;
		if (x < 0) {
			x = 0;
		}
		if (x > getWidth() - PADDLE_WIDTH) {
			x = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT/2);
		add(paddle);
	}
	
	// start of the game (after a click)
	private void playGame() {
		GOval ball = setUpBall();
		waitForClick();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		while(!isBottomWall(ball) && brickCount > 0) {
			if (isRightWall(ball) || isLeftWall(ball)) { // update x velocity
				vx = -vx;
			}
			if (isTopWall(ball)) { // update y velocity
				vy = -vy;
			}
			GObject collider = getCollidingObject(ball); // Checks if ball collided with paddle
			if (collider == paddle) {
				if (ball.getY() > getHeight() - PADDLE_Y_OFFSET - ball.getHeight()) {
					vx = -vx;
				}
				else {
					vy = -vy;
				}
			}
			if (collider != paddle && collider != null) { // Checks if ball collided with brick
				remove(collider);
				vy = -vy;
				brickCount -= 1;
			}
			ball.move(vx, vy); // update
			pause(DELAY); // pause

		}
		remove(ball);
		if (isBottomWall(ball)) {
			i += 1;
		}
	}

	// following lines test if the ball has reached a wall
	private boolean isBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	private boolean isTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean isLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private boolean isRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}
	
	// returns which object the ball collided with
	private GObject getCollidingObject(GObject r) {
		GObject collider = getElementAt(r.getX(),r.getY());
		if (collider == null) {
			collider = getElementAt(r.getX()+2*BALL_RADIUS,r.getY());
		}
		if (collider == null) {
			collider = getElementAt(r.getX(),r.getY()+2*BALL_RADIUS);
		}
		if (collider == null) {
			collider = getElementAt(r.getX()+2*BALL_RADIUS,r.getY()+2*BALL_RADIUS);
		}
		return collider;
	}
	
	// sets up the ball for the game
	public GOval setUpBall() {
		double ballX = getWidth()/2 - BALL_RADIUS;
		double ballY = getHeight()/2 - BALL_RADIUS;
		GOval r = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
		r.setFilled(true);
		add(r, ballX, ballY);
		return(r);
	}
	
	// message that is returned if the player has used all three turns and has not managed to finish the bricks
	private void gameOver() {
		GLabel gameOver = new GLabel("Game Over!");
		gameOver.setFont("Courier-20");
		add(gameOver, getWidth()/2-gameOver.getWidth()/2, getHeight()/2-gameOver.getAscent()/2);
	}
	
	// message that is returned if the player has managed to finish the bricks
	private void youWon() {
		GLabel youWon = new GLabel("You won! Well done!");
		youWon.setFont("Courier-20");
		youWon.setColor(Color.GREEN);
		add(youWon, getWidth()/2-youWon.getWidth()/2, getHeight()/2-youWon.getAscent()/2);
	}
}
