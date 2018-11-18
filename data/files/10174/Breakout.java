/*
 * File: Breakout.java
 * -------------------
 * Name: Kyra Whitelaw
 * Section Leader: Thariq Ridha
 * 
 * This file will implements the game of Breakout. A complete game consists of three turns. On each turn, a ball is launched from the center of the
 * window toward the bottom of the screen at a random angle. That ball bounces off the paddle and the walls of the world.  When the ball collides with
 * a brick, the ball bounces just as it does on any other collision, but the brick disappears. The play continues until either the ball hits the lower
 * wall, in which case the turn ends and the next ball is served if the player has any turns left or the player loses, or the last brick is eliminated
 * in which case the player wins, and the game ends immediately.
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
	
	//Offset of the leftmost brick column from the left side of the screen in pixels.
	public static final double TOTAL_BRICK_WIDTH = 10 * BRICK_WIDTH + 9 * BRICK_SEP;
	
	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;
	
	//Diameter of the ball in pixels
	public static final double BALL_DIAMETER = 2 * BALL_RADIUS;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 600.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	
	//Private instance variables//
	private GRect brick;
	private GRect paddle;
	private GOval ball;
	private double vy;
	private double vx;
	private int counter = 0;
	private RandomGenerator rg = new RandomGenerator ();
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//Sets up the game
		makeBricks();
		createPaddle();
		addMouseListeners();
		
		//Plays Breakout!  Gives the player three lives (the ball will reappear twice after hitting the bottom wall until the game ends).
		for (int i = 0; i < 3; i++) {
			createBall();
			moveBall();
			if (counter == 100) {
				i = 3;
			}
		}
		
		//Displays a winning or losing message at the end of the game
		endMessage();
	}

	/*
	 * Creates ten rows of ten bricks using constants for the brick size and relative location
	 */
	private void makeBricks () {
		for (int n=0; n<NBRICK_COLUMNS; n++) {
			for (int i=0; i<NBRICK_ROWS; i++) {
				double brickxoffset = (getWidth() - TOTAL_BRICK_WIDTH)/2;
				double bx = brickxoffset + ((BRICK_WIDTH + BRICK_SEP) * n);
				double by = (BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP));
				brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setLocation (bx, by);
				brick.setFilled(true);
				colorBricks(i);
				add (brick);
			}	
		}
	}
	/*
	 * Colors the bricks in sets of two rows so that they form a rainbow pattern from red to cyan blue.
	 */
	private void colorBricks (int i) {
		if (i<2) {
			brick.setColor(Color.RED);
		}
		if (i==2 || i==3) {
			brick.setColor(Color.ORANGE);
		}
		if (i==4 || i==5) {
			brick.setColor(Color.YELLOW);
		}
		if (i==6 || i==7) {
			brick.setColor(Color.GREEN);
		}
		if (7 < i && i < NBRICK_COLUMNS) {
			brick.setColor(Color.CYAN);
		}
	}
	
	/*
	 * Creates a paddle in the center of the screen using constants for size.
	 */
	private void createPaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double px = (getWidth() - PADDLE_WIDTH)/2;
		double py = getHeight() - PADDLE_Y_OFFSET;
		paddle.setLocation(px, py);
		add (paddle);
	}
	
	/*
	 * Synchronizes the mouse with the paddle so that the paddle moves horizontally in accordance with the movement of the mouse.
	 */
	public void mouseMoved (MouseEvent e) {
		double paddlex = e.getX();
		double paddlexmax = getWidth() - PADDLE_WIDTH;
		if (paddlex > paddlexmax) {
			paddle.setLocation(paddlexmax, getHeight() - PADDLE_Y_OFFSET);
		} else {
			paddle.setLocation(paddlex, getHeight() - PADDLE_Y_OFFSET);
		}
	}
	
	/*
	 * Creates a black ball in the center of the screen with ball size determined by constants.
	 */
	private void createBall() {
		ball = new GOval (BALL_DIAMETER, BALL_DIAMETER);
		double bally = getHeight()/2 - BALL_RADIUS;
		double ballx = getWidth()/2 - BALL_RADIUS;
		ball.setFilled(true);
		ball.setLocation(ballx, bally);
		add (ball);
	}
	
	/*
	 * The ball begins moving at a constant downward velocity and a random horizontal velocity.
	 * The absolute velocities are constant throughout the game.
	 * The ball bounces off of walls, bricks, and the paddle.
	 * If the ball hits the bottom of the screen, it is removed.
	 * If all of the bricks are removed, the game ends immediately.
	 */
	private void moveBall() {
		vx = rg.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rg.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		while (true) {
			checkWalls();
			checkBricks();
			if (hitBottom()) {
				remove (ball);
				break;
			}
			if (counter == 100) {
				break;
			}
			ball.move(vx, vy);
			pause (DELAY);
		}
	}
	
	/*
	 * Causes the ball to bounces off of the top, left, and right walls.
	 */
	private void checkWalls() {
		if (hitTop()) {
			vy = -vy;
		}
		if (hitLeft() || hitRight()) {
			vx = -vx;
		}
	}
	
	/*
	 * Returns true if the ball has hit the bottom wall, and false if not.
	 */
	private boolean hitBottom() {
		return ball.getY() >= getHeight() - BALL_DIAMETER;
	}

	/*
	 * Returns true if the ball has hit the top wall, and false if not.
	 */
	private boolean hitTop() {
		return ball.getY() <= 0;
	}

	/*
	 * Returns true if the ball has hit the right wall, and false if not.
	 */
	private boolean hitRight() {
		return ball.getX() >= getWidth() - BALL_DIAMETER;
	}

	/*
	 * Returns true if the ball has hit the left wall, and false if not.
	 */
	private boolean hitLeft() {
		return ball.getX() <= 0;
	}
	
	/*
	 * Checks if the ball has an object.
	 * If the object is a brick, the brick is removed and the ball bounces.
	 * The number of bricks that has been removed is counted in the counter variable.
	 * If the ball object is the paddle, the ball bounces.
	 */
	private void checkBricks() {
		GRect collider = getCollider();
		if (collider == paddle) {
			vy = - Math.abs (vy);
		} else if (collider != null) {
			vy = - vy;
			remove (collider);
			counter += 1;
		}
	}
	
	/*
	 * Checks if the ball has hit an object at any of the four corners of the square in which it is inscribed.
	 */
	private GRect getCollider() {
		double ballxleft = ball.getX();
		double ballxright = ball.getX() + BALL_DIAMETER;
		double ballytop = ball.getY();
		double ballybottom = ball.getY() + BALL_DIAMETER;
		GRect collider = getElementAt (ballxleft, ballytop);
		if (collider != null) {
			return collider;
		} else {
			collider = getElementAt (ballxleft, ballybottom);
		}
		if (collider != null) {
			return collider;
		} else {
			collider = getElementAt (ballxright, ballytop);
		}
		if (collider != null) {
			return collider;
		} else {
			collider = getElementAt (ballxright, ballybottom);
		}
		if (collider != null) {
			return collider;
		} else {
			return null;
		}
	}

	/*
	 * Prints a message at the end of the game.
	 * If the player has removed all the bricks, a winning message is printed.
	 * If the ball has hit the bottom wall three times before all bricks were removed, a losing message is printed.
	 */
	private void endMessage() {
		if (counter != 100) {
			GLabel loseMessage = new GLabel ("Sorry! You lose!");
			loseMessage.setLocation(loseMessage.getWidth(), getHeight()/2);
			loseMessage.setFont("Courier-24");
			loseMessage.setColor(Color.BLACK);
			add(loseMessage);
		} else {
			GLabel winMessage = new GLabel ("Congratulations! You win!");
			winMessage.setLocation(BRICK_WIDTH, getHeight()/2);
			winMessage.setFont("Courier-24");
			winMessage.setColor(Color.RED);
			add(winMessage);
		}
	}
}


