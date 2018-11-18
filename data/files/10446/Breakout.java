/*
 * File: Breakout.java
 * -------------------
 * Name: Dominick Mandarino
 * Section Leader:Tessera
 * 
 * This program will implement the game breakout. The user will have 
 * three turns to break all of the bricks using a paddle and a ball.
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

	public static final double NUMBER_OF_BRICKS_IN_ROW = 10;

	private RandomGenerator rg = new RandomGenerator();
	// Starting X value equals zero
	double startingX = 0;
	// Starting Y value equals zero
	double startingY = 0;
	// 	Declaring the paddle outside of the public void run() method
	//  so the entire program can access it
	private GRect paddle;
	// 	Declaring the paddle outside of the public void run() method
	//  so the entire program can access it
	private GOval ball;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// Draws bricks and paddle
		drawGame();
		// Controls the number of turns the user has,
		// which is three
		for(int i = 0; i < NTURNS; i++) {
			// Makes ball
			ball = makeBall();
			// Allows the paddle to move
			addMouseListeners();
			// Causes the ball to bounce off of walls and 
			// break bricks
			moveBall();
			// If player wins, prints "You Win!" and ends game immediately
			if(brickCounter == 0) {
				printYouWin();
				break;
			}
		}
		// If player loses, prints "You Lose!" and ends game immediately
		if(brickCounter != 0) {
			printYouLose();
		}
	}
/*
 * This method causes the ball to bounce of walls while changing direction
 * It also allows the ball to bounce of the paddle and to remove bricks
 */
	private void moveBall() {
		double vx = rg.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rg.nextBoolean(0.5)) vx = -vx;
		double vy = VELOCITY_Y;
		waitForClick();
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);

			if(ball.getX() > getWidth() - ball.getWidth()) {
				vx = -vx;
			}
			if(ball.getY() <= 0) {
				vy = -vy;
			}
			if(ball.getX() <= 0) {
				vx = -vx;
			}
			GObject collider = getCollidingObject();
			if(collider == paddle) {
				vy = -vy;
			} else if (collider !=null && collider !=paddle) {
				remove(collider);
				brickCounter--;
				vy = -vy;
			}
			if(brickCounter == 0) {
				remove(ball);
				break;
			}
			if(ball.getY() > getHeight()) {
				remove(ball);
				break;

			}
		}
	}
/*
 * This detects collisions with the ball
 */
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) !=null ) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null ) {
			return getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return null;
/*
 * This method creates the bricks and the paddle
 */
	}
	private void drawGame() {
		makeBricks();
		paddle = makePaddle();
		positionPaddle(paddle);
	}
/*
 * This method makes the bricks and positions them appropriately.
 * This method also colors the bricks
 */
	private void makeBricks() {
		for(int row = 0; row < NBRICK_ROWS; row++) {
			for(int column = 0; column < NBRICK_COLUMNS; column++) {
				startingX = getWidth()/2 - (NUMBER_OF_BRICKS_IN_ROW*BRICK_WIDTH)/2 - ((NUMBER_OF_BRICKS_IN_ROW-1)*BRICK_SEP)/2 +column*BRICK_WIDTH + column*BRICK_SEP;
				startingY = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row;
				GRect brick = new GRect(startingX, startingY, BRICK_WIDTH, BRICK_HEIGHT);

				if (row == 0 || row == 1) {
					brick.setColor(Color.red);
				}
				if (row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
				}
				if (row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				}
				if (row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				}
				if (row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
				}
				brick.setFilled(true);
				add(brick);

			}
		}
	}
/*
 * This method allows the user to move the paddle
 * side to side with the  use of a mouse
 */
	public void mouseMoved(MouseEvent e) {
		if ((e.getX() < getWidth() - PADDLE_WIDTH/2) && (e.getX() > PADDLE_WIDTH/2)) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}

	}
/*
 * This method creates the ball to its appropriate size
 *  while also coloring it blue
 */
	public GOval makeBall() {
		double size = BALL_RADIUS*2;
		GOval ball = new GOval (size,size);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS*3);
		return ball;

	}
/*
 * This method declares the x and y coordinates for 
 * the paddle and adds it.
 */
	private void positionPaddle(GRect paddle) {
		double x = (getWidth() - PADDLE_WIDTH) /2;
		double y = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		add (paddle, x, y);
	}
/*
 * This method creates the outline for the paddle to open
 * up the next step of adding the paddle
 */
	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;		
	}
	
/*
 * This method keeps count of the amount of bricks left in the game	
 */
	private int brickCounter = NBRICK_COLUMNS*NBRICK_ROWS;
/*
 * This method prints "You Win!"
 */
	private void printYouWin() {
		GLabel YouWin = new GLabel ("YOU WIN!", getHeight()/2, getWidth()/2);
		YouWin.setColor(Color.GREEN);
		add (YouWin);
	}
/*
 * This method prints "You Lose!"
 */
	private void printYouLose() {
		GLabel YouLose = new GLabel ("YOU LOSE!", getHeight()/2, getWidth()/2);
		YouLose.setColor(Color.RED);
		add(YouLose);
	}
}






