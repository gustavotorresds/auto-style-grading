/**
 * File: Breakout.java
 * -------------------
 * Name: Joshua Orrick
 * Section Leader: Jordan
 * 
 * this file contains the game "breakout"
 * 
 * the game consists of a ball, bricks, and a paddle
 * 
 * the objective is to clear all the bricks from the screen
 * 
 * the user has three lives
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

	//initializes paddle
	private GRect paddle = new GRect(CANVAS_WIDTH/2 - PADDLE_WIDTH/2, CANVAS_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	//initializes the ball
	private GOval ball;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	//initializes the random generator for random velocity values and also the variables vx/vy which represent the velocity
	private double vx;
	private double vy = VELOCITY_Y;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// Number of turns 
	public static final int NTURNS = 3;

	//counts bricks destroyed
	private int count = 0;

	//checks if win or loss
	private boolean win = false;

	//counts number of lives
	private int lives = 3;

	//initializes object to catch collisions
	private GObject collisionCatch;

	/**
	 * the first method sets up the game by setting the title, setting the console size, adding a movable paddle connected to the mouse, creating a moving ball that bounces, and creating the bricks
	 * 
	 * the last method allows the user to play the game, setting win/loss conditions and statements and creating a life counter while also utilizing the "ballMechanics()" method which makes the ball move and destroy bricks
	 */
	public void run() {
		while(true) {
			setGame();
			playGame();
		}
	}

	/**
	 * method to set up the game
	 * 
	 * sets the title and canvas size by use of given variables and strings
	 * 
	 * adds mouse listeners in order for the paddle to be controlled by the mouse
	 * 
	 * adds the paddle linked to the mouse
	 * 
	 * adds the 10 x 10 group of colored bricks on the top of the console
	 * 
	 * adds the ball to the center of the screen
	 */
	private void setGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		paddle.setFillColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
		createBricks();
		createBall();
	}

	/**
	 * utilizes a "for" loop that runs a maximum of three times as the player has three lives
	 * 
	 * adds a counter that starts at 3 and loses 1 every time the user lets the ball touch the bottom of the console
	 * 
	 * utilizes the "ballMechanics()" method which contains the ball's physics
	 * 
	 * sets the balls location in the center of the screen as it would have gone off the screen at this point in the code
	 * 
	 * adds in two conditional statements, one changes the boolean "win" to true if the user breaks all 100 bricks and then breaks the loop, and the other changes "win" to false if the user does not
	 * 
	 * after the loop is broken, either through the user winning the game or losing, utilizes the "printMessage()" method which prints a different message depending on if the user wins or loses
	 */
	private void playGame() {
		for (int i = 0; i < NTURNS; i++) {
			GLabel lifeCount = new GLabel(lives + " lives remaining", getWidth() / 8, getHeight() - CANVAS_HEIGHT / 3);
			add(lifeCount);
			waitForClick();
			remove(lifeCount);
			ballMechanics();
			ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
			if (count ==  NBRICK_COLUMNS * NBRICK_ROWS) {
				win = true;
				break;
			}
			else {
				win = false;
			}
			lives = lives - 1;
		}
		printMessage(win);
	}

	/**
	 * this method is utilized after the user loses or wins the game
	 * 
	 * if the user loses, prints a statement using a label placed above the ball saying the user lost
	 * 
	 * if the user wins, prints a statement using a label placed above the ball saying the user won
	 */
	private void printMessage(boolean win) {
		if (win == false) {
			GLabel loseStatement = new GLabel("You Lose!", getWidth() / 2, getHeight() / 2);
			loseStatement.move(-loseStatement.getWidth() / 2, -20);
			add(loseStatement);
		}
		else {
			GLabel winStatement = new GLabel("You Win!", getWidth() / 2, getHeight() / 2);
			winStatement.move(-winStatement.getWidth() / 2, -20);
			add(winStatement);
		}
	}

	/**
	 * method to set paddle's location and link it to the mouse's movement
	 * 
	 * accounts for the paddle going off the screen, stops it before it does by using the width of the paddle divided by 2
	 * 
	 * sets the location with the values given
	 */
	public void mouseMoved(MouseEvent event) {
		double x = event.getX();
		if (x >= getWidth() - PADDLE_WIDTH/2) {
			return;
		}
		if (x <= 0 + PADDLE_WIDTH/2) {
			return;
		}
		double y = CANVAS_HEIGHT - PADDLE_Y_OFFSET;
		paddle.setLocation(x - PADDLE_WIDTH/2, y);
	}

	/**
	 * method to lay all bricks in order with respective colors
	 * 
	 * two "for" loops, one for creating rows and the one inside for creating columns
	 * 
	 * for each time the first loop runs, 10 bricks are placed in a row filled with their respective colors
	 * 
	 * the colors are controlled by the if statements in the second loop with conditions relating to what row is being placed, for instance if the row is the fourth, then yellow bricks will be placed
	 */
	private void createBricks() {
		for (int j = 0; j < NBRICK_ROWS; j++) {
			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				GRect brick = new GRect( BRICK_SEP + (BRICK_WIDTH + BRICK_SEP) * i , BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * j ,  BRICK_WIDTH, BRICK_HEIGHT);
				add(brick); 
				if (j == 0 || j == 1) {
					brick.setColor(Color.RED);
					brick.setFillColor(Color.RED);
					brick.setFilled(true);
				}
				if (j == 2 || j == 3) {
					brick.setColor(Color.ORANGE);
					brick.setFillColor(Color.ORANGE);
					brick.setFilled(true);
				}
				if (j == 4 || j == 5) {
					brick.setColor(Color.YELLOW);
					brick.setFillColor(Color.YELLOW);
					brick.setFilled(true);
				}
				if (j == 6 || j == 7) {
					brick.setColor(Color.GREEN);
					brick.setFillColor(Color.GREEN);
					brick.setFilled(true);
				}
				if (j == 8 || j == 9) {
					brick.setColor(Color.CYAN);
					brick.setFillColor(Color.CYAN);
					brick.setFilled(true);
				}
			}
		}
	}

	/**
	 * creates the ball centered in the middle of the screen
	 */
	private void createBall() {
		ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}

	/**
	 * this method allows the ball to start with a random velocity by use of a random generator "rgen" and then sets the velocity of the ball while making sure it bounces off the walls
	 */
	private void ballMechanics() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);		//given code for randomizing initial velocity
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		while (true) {
			ball.move(vx, vy);		
			pause(DELAY);
			double x = ball.getX();	
			double y = ball.getY();
			getElementAt(x , y);		//uses the tracked x and y values of the ball to check if the ball is hitting the sides or the top of the walls and then changes the velocity to negative so it bounces away
			if (y <= 0) { 
				vy = -vy;
			}
			if (x <= 0 || x + 2*BALL_RADIUS >= getWidth()) {		//accounts for the dimensions of the ball
				vx = -vx;
			}
			collisionCatch = checkCollisions(); 	//creates an object to check for collisions with respect to the paddle and bricks and if it touches neither
			if (collisionCatch == paddle) {
				vy = -Math.abs(vy);		//accounts for the ball sticking to the paddle
			}
			else if (collisionCatch != null) {		
				vy = -vy;
				remove(collisionCatch);
				count = count + 1;
			}
			if (y >= getHeight()) {		//if the ball touches the bottom of the console, breaks the loop
				break;
			}
			if (count == NBRICK_COLUMNS * NBRICK_ROWS) {		//if the user breaks all the bricks, breaks the loop and sets the integer that counts bricks equal to 100
				break;
			}
		}
	}

	/**
	 * creates an object to track what collides with the ball 
	 * 
	 * tracks the ball's position and sets four points on the outside of the ball to check for collisions
	 * 
	 * if the ball collides with something, sets the object "collisionObject" to not null
	 * 
	 * if the ball collides with something, returns the collisionObject 
	 */
	private GObject checkCollisions() {
		GObject collisionObject = null;
		double x = ball.getX();
		double y = ball.getY();
		collisionObject = getElementAt(x,y);
		if (collisionObject != null) {
			return collisionObject;
		}
		collisionObject = getElementAt(x + 2*BALL_RADIUS, y);
		if (collisionObject != null) {
			return collisionObject;
		}
		collisionObject = getElementAt(x, y + 2*BALL_RADIUS);
		if (collisionObject != null) {
			return collisionObject;
		}
		collisionObject = getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS);
		if (collisionObject != null) {
			return collisionObject;
		}
		return collisionObject;
	}

}
