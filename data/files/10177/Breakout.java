/*
 * File: Breakout.java
 * -------------------
 * Name: Devin Hagan
 * Section Leader: Adam Mosharrafa
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
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 5.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private GRect paddle;
	private double vx, vy;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int brickCounter = 100;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		setUpBricks();
		for(int i = 0; i < NTURNS; i++) {
			if(brickCounter > 0) {
				setUpGame();
				playGame();
			}
		}
	}
	//sets up the game by adding the paddle and ball, as well as mouse listeners to allow the paddle to move
	private void setUpGame() {
		makePaddle();
		addMouseListeners();
		addBall();
	}
	//method to see if the ball is making a collision with an object
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		}
		else if(getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		}
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}
		else {
			return null;
		}
	}
	//adds a ball to the screen
	private void addBall() {
		double x = (getWidth() / 2) - (BALL_RADIUS);
		double y = (getHeight() / 2) - (BALL_RADIUS);
		ball = new GOval (x,y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);

	}
	//plays the game after user clicks
	private void playGame() {
		waitForClick();
		getVelocity();
		while(true) {
			moveBall();
			if(ball.getY() >= getHeight()) {
				remove(ball); //removes the ball so when the turn starts over the new ball won't hit the old ball
				break;
			}
			if(brickCounter == 0) {
				break;
			}
			pause(DELAY);

		}
	}
	//method that finds the velocity of the ball in x direction and y direction
	private void getVelocity() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(.5)) {
			vx = -vx;
		}
	}
	//method that moves the ball and checks for walls as well as collisions with the paddle or bricks
	private void moveBall() {
		ball.move(vx, vy);
		if(ball.getX() <= 0 || ball.getX() >= getWidth() - BALL_RADIUS * 2) {
			vx = -vx;
		}
		if(ball.getY() <= 0) {		
			vy = -vy;
		}
		GObject collider = getCollidingObject();
		if(collider == paddle) {
			if(ball.getY() > getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - (BALL_RADIUS * 2)){
				vy = -Math.abs(vy); //allows the ball to not become stuck to the paddle after making collision
			}
		}
		else if(collider != null) {
			remove(collider);
			vy = -vy;
			brickCounter--;
		}
	}
	//makes the paddle and adds to screen
	private GRect makePaddle() {
		if(paddle == null) {
			double x = (getWidth() / 2) - (PADDLE_WIDTH / 2);
			double y =  getHeight() - PADDLE_Y_OFFSET;
			paddle = new GRect(x,y, PADDLE_WIDTH, PADDLE_HEIGHT);
			paddle.setFilled(true);
			paddle.setColor(Color.BLACK);
			add(paddle);
		}
		return paddle;
	}
	//allows the paddle to move without passing the two side walls
	public void mouseMoved(MouseEvent e) {

		double xLocation = e.getX();
		double yLocation = getHeight() - PADDLE_Y_OFFSET;		
		if(xLocation > 0 && xLocation < (getWidth() - PADDLE_WIDTH)) {
			paddle.setLocation(xLocation, yLocation);
		}
	}
	//sets up the bricks with certain rows having certain colored bricks
	private void setUpBricks() {
		for(int row = 0; row < NBRICK_ROWS; row++) {
			for(int col = 0; col < NBRICK_COLUMNS; col++) {
				double lengthFromSide = getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * (BRICK_SEP));
				double x = (lengthFromSide / 2) + (BRICK_WIDTH * col) + (BRICK_SEP * col);
				double y = (BRICK_Y_OFFSET) + ((row + 1) * (BRICK_HEIGHT)) + (BRICK_SEP * row);
				GRect brick = new GRect(x,y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
					add(brick);
				}
				if(row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
					add(brick);
				}
				if(row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
					add(brick);
				}
				if(row == 3 || row == 2) {
					brick.setColor(Color.ORANGE);
					add(brick);
				}
				if(row == 0 || row == 1) {
					brick.setColor(Color.RED);
					add(brick);
				}
			}
		}
	}
}
