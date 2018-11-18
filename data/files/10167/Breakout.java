/*
 * File: Breakout.java
 * -------------------
 * Name: Lauren Rood
 * Section Leader: Rachel Gardner
 * 
 * This file implements the game of Breakout. The game will
 * show a ball that hits off of a paddle moved by the user
 * and rebounds off the paddle to hit the bricks located 
 * above. Once the ball hits the bricks the bricks will 
 * disappear. If the user misses the ball with the paddle
 * and the ball hits the bottom wall, the game is over. 
 * If the user manages to hit all of the bricks with the 
 * ball, then the user wins the breakout game! 
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

	// Paddle dimensions
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	// Ball dimensions
	private GOval ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);

	// Velocity of the ball
	private double vx, vy;

	// Random coordinate generator
	// for the ball so the ball 
	// bounces in random directions 
	// off of other objects with a 
	// negative velocity.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// determines if the ball hit an object.
	GObject collider = null;

	// The label that pops up to tell
	// the user if they won or lost the
	// game. 
	private GLabel label;

	// The amount of bricks left in the game.
	int bricksLeftover = (NBRICK_ROWS * NBRICK_COLUMNS);

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// Adds mouse listeners
		addMouseListeners();

		//Creates and colors the brick rows.
		for(int i = 0; i< NBRICK_ROWS; i++) {
			Color color = rowNumberColor(i);
			drawARow(NBRICK_ROWS, i, color);
		}
		double paddleX = ((getWidth() - PADDLE_WIDTH) / 2);
		double paddleY = (getHeight() - PADDLE_Y_OFFSET);
		paddle = new GRect (paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT); 
		paddle.setFilled(true);	
		add(paddle);

		// Makes the ball the correct size and
		// adds the ball to the screen. 
		makeBall();

		// This allows the user to have 
		// only 3 turns to try and remove
		// all the bricks. If the user
		// fails to remove all the bricks
		// and lets the ball hit the bottom
		// wall 3 times, which is a loss of 
		// one turn each time, then the 
		// user loses the game.
		for (int i= 0; i< NTURNS; i++) {
			if (bricksLeftover != 0) {
				remove(ball);
				makeBall();
				moveBall();
			} else {
				break;
			}
		}
		// If there are no more bricks left
		// and the user did not use all of 
		// their turns, the user wins!
		if (bricksLeftover == 0) {
			label = new GLabel ("You Won!");
			add(label, getWidth()/ 2 - label.getWidth()/ 2, getHeight()/ 2 - label.getAscent()/ 2);
			remove(ball);
		} 
		
		if (bricksLeftover != 0) {	
			label = new GLabel ("Game Over!");
			add(label, getWidth()/ 2 - label.getWidth()/ 2, getHeight()/ 2 - label.getAscent()/ 2);
			remove(ball);
			// If the user does not remove
			// all the bricks before their 
			// third turn, they lose. 
		}

		// Animation to make the ball move.
		moveBall();

	}
	// Applies color to specific rows to make a 
	// rainbow pattern of 5 colors. 
	private Color rowNumberColor(int rowNumber) {
		rowNumber = rowNumber % 10;
		if (rowNumber == 0 || rowNumber == 1) {
			return Color.RED;
		} else if (rowNumber == 2 || rowNumber == 3) {
			return Color.ORANGE;
		}
		if (rowNumber == 4 || rowNumber == 5) {
			return Color.YELLOW;
		} else if (rowNumber == 6 || rowNumber == 7) {
			return Color.GREEN;
		}
		if (rowNumber == 8 || rowNumber == 9) {
			return Color.CYAN;
		}
		return null;
	}

	// This makes the ball the correct size and places
	// it in the middle of the screen. 
	private void makeBall() {
		double ballX = ((getWidth()/ 2 - (BALL_RADIUS * 2)/ 2));
		double ballY = ((getHeight()/ 2 - (BALL_RADIUS * 2) / 2));
		ball = new GOval ( ballX, ballY, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}
	// Creates the individual bricks and 
	// all of the brick rows. 
	private void drawARow(double count, double height, Color color) {
		double xOffset = (getWidth() - ((BRICK_WIDTH * NBRICK_COLUMNS) + ((NBRICK_COLUMNS - 1) * BRICK_SEP))) / 2;
		for( int i = 0; i < (NBRICK_ROWS); i++ ) {
			double rowX = (i * (BRICK_WIDTH + BRICK_SEP)) + xOffset;
			double rowY = (height * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET);
			GRect rect = new GRect ( rowX, rowY, BRICK_WIDTH, BRICK_HEIGHT);
			rect.setFilled(true);
			rect.setColor(color);
			add(rect);
		}
	}
	// The paddle is centered on the mouse
	// and follows the mouse location from
	// left to right but not up and down.
	// The paddle will not go outside the
	// boundaries of the canvas.
	public void mouseMoved (MouseEvent e) {
		double mouseX = (e.getX()) - (PADDLE_WIDTH/2); 
		double mouseY = (getHeight() - PADDLE_Y_OFFSET);
		if(mouseX < 0) {
			paddle.setLocation(0, mouseY);
		} else if (mouseX + PADDLE_WIDTH > getWidth()){
			paddle.setLocation((getWidth() - PADDLE_WIDTH), mouseY);
		} else {
			paddle.setLocation(mouseX, mouseY);
		}
	}

	// Animation to make the ball move
	private void moveBall() {
		vx = rgen.nextDouble(1.0, 3.0);
		vy = (3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);

			// Preventing the ball from leaving
			// the canvas. Makes the ball bounce
			// off of any wall.
			ball.getX();
			ball.getY();
			if (ball.getX() < 0) {
				vx = -vx;
			} else if (ball.getX() + (BALL_RADIUS * 2) > getWidth()) {
				vx = -vx;
			}
			if (ball.getY() < 0) {
				vy = -vy;
				// Notifies player that they lost if the ball 
				// hits the bottom wall. 
			} else if (ball.getY() + (BALL_RADIUS * 2) > getHeight()) {
				remove(ball);
				break;
			}
			if (bricksLeftover == 0) {
				label = new GLabel ("You Won!");
				add(label, getWidth()/ 2 - label.getWidth()/ 2, getHeight()/ 2 - label.getAscent()/ 2);//
				remove(ball);
				break;
			}
			checkForCollisions();
		}

	}

	// Checks for collisions and removes
	// bricks when the ball hits them.
	private void checkForCollisions() {
		collider = getCollidingObject();
		if (collider == paddle) {
			vy = -Math.abs(vy);
		} else if (collider != null)	{
			remove(collider);
			vy = -vy;
			bricksLeftover = (bricksLeftover - 1);
		}
	}

	// Checks all four corners of the
	// ball to see if an object is hit
	// by any side on the ball.
	private GObject getCollidingObject() {
		if (getElementAt (ball.getX(), ball.getY()) != null) {
			return getElementAt (ball.getX(), ball.getY());
		} else if (getElementAt ((ball.getX() + (BALL_RADIUS * 2)), ball.getY()) != null) {
			return getElementAt (ball.getX() + (BALL_RADIUS * 2), ball.getY());
		} else if (getElementAt (ball.getX(), ball.getY() + (BALL_RADIUS * 2)) != null) {
			return(getElementAt (ball.getX(), ball.getY() + (BALL_RADIUS * 2)));
		} else if (getElementAt (ball.getX() + (BALL_RADIUS * 2), ball.getY() + (BALL_RADIUS * 2)) != null) {
			return(getElementAt (ball.getX() + (BALL_RADIUS * 2), ball.getY() + (BALL_RADIUS * 2)));
		}
		return(null); 
	}
}

