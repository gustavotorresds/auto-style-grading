/*
 * File: Breakout.java
 * -------------------
 * Name: Gita Multani
 * Section Leader: Ben Barnett
 * 
 * This file implements the game of Breakout. The player gets three turns to remove
 * all of the colored bricks by using the paddle and the walls to collide into 
 * the bricks. If the ball falls through the bottom wall, the round terminates and 
 * resets given that the player has one or more turns left.
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

	// Create GObjects
	private GRect paddle;
	private GOval ball;
	private GLabel brickCount;
	
	// Create velocity variables
	private double vx, vy;
	
	// Total number of bricks 
	private int BRICK_COUNT = 100;
	
	// Implement random number generator 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Implement audio clip
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		breakoutSetUp();
		breakoutPlay();
	}
	
	// Implements methods to set up the game
	private void breakoutSetUp(){
		combineColoredBricks();
		addMouseListeners();
		paddle = drawPaddle();
		addPaddleToScreen(paddle);
	}

	// Implements methods to play the game
	private void breakoutPlay(){
		for(int play = 0; play < 3; play++) { // user gets three turns
			ball = drawBall();
			addBallToScreen(ball);
			waitForClick();
			moveBall();
			remove(ball);
		}
		if(BRICK_COUNT > 0) { // terminating condition
			makeLabel("YOU LOSE", Color.RED);
		}
	}
	
	// Make the colored bricks in order given
	private void combineColoredBricks() {
		drawColoredBricks(Color.RED, 0, 2);
		drawColoredBricks(Color.ORANGE, 2, 4);
		drawColoredBricks(Color.YELLOW, 4, 6);
		drawColoredBricks(Color.GREEN, 6, 8);
		drawColoredBricks(Color.CYAN, 8, 10);
	}
	
	/* Method takes in color, starting row number, and ending row number
	 * 		Red is rows 0 and 1
	 * 		Orange is rows 2 and 3
	 * 		Yellow is rows 4 and 5
	 */
	private void drawColoredBricks(Color color, int rowStart, int rowEnd) {
		for(int col = 0; col < NBRICK_COLUMNS ; col++) {
			for(int row = rowStart; row < rowEnd; row++) {
				double brickWidth = BRICK_WIDTH + BRICK_SEP; // each brick technically is its own width plus the distance between the bricks
				double x = getWidth()/2 - (NBRICK_COLUMNS * brickWidth/2) + (col* brickWidth) + BRICK_SEP/2; // last brick has additional distance to its right
				double y = BRICK_Y_OFFSET + BRICK_HEIGHT * row + BRICK_SEP * row;

				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick);
			}
		}		
	}

	// Takes in the paddle and adds it to the bottom center of the screen
	private void addPaddleToScreen(GRect square) {
		double xPos = (getWidth() - PADDLE_WIDTH) / 2;
		double yPos = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		add(square, xPos, yPos);
	}

	// Draws the paddle using GRect and returns that object
	private GRect drawPaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		return paddle;
	}
	
	// Takes in the ball and adds it to the center of the screen
	private void addBallToScreen(GOval oval) {
		double xPos = (getWidth()/2 - BALL_RADIUS);
		double yPos = (getHeight()/2 - BALL_RADIUS);
		add(oval, xPos, yPos);
	}
	
	// Draws the ball using GOval and returns that object
	private GOval drawBall() {
		double diameter = 2 * BALL_RADIUS;
		GOval ball = new GOval(diameter, diameter);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		return ball;
	}

	// Tracks the horizontal movement of the mouse to control the paddle
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX(); // retrieves the x location
		double mouseY = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET); // y location remains constant
		if(mouseX < getWidth() - PADDLE_WIDTH) { // paddle cannot extend beyond the walls
			paddle.setLocation(mouseX, mouseY);
		}
	}

	/* Initializes the x and y velocities of the ball
	 * While loop continues until the terminating condition
	 * If ball bounces off top wall, sign of vy is reversed
	 * If ball bounces off left or right wall, sign of vx is reversed
	 */
	private void moveBall() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); // vx is a random double in given range
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		while(hitBottomWall(ball) != true) { // terminating condition: ball hits bottom wall
			displayBrickCount();
			ball.move(vx, vy); // update visualization
			if(hitTopWall(ball)) { // update y velocity
				vy = -vy; 
			}
			if(hitLeftWall(ball) || hitRightWall(ball)) { // update x velocity
				vx = -vx;
			}
			pause(DELAY);
			whenBallCollides();
			if(BRICK_COUNT == 0) { // player wins if brick count reaches 0 before all three turns are used
				makeLabel("YOU WIN", Color.GREEN);
				remove(ball);
			}
			remove(brickCount);
		}
	}

	/* General method to make a label
	 * Takes in parameters of label name and color of the label
	 * Used to make "win" and "lose" labels
	 */
	private void makeLabel(String name, Color color) {
		GLabel label = new GLabel(name);
		label.setFont("Century Gothic-48");
		label.setColor(color);
		add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getAscent()/2); // places label in center of screen
	}
	
	// Makes a label that displays the number of bricks left
	private void displayBrickCount() {
		brickCount = new GLabel("Bricks Left: " + BRICK_COUNT);
		brickCount.setFont("Century Gothic-12");
		brickCount.setColor(Color.BLACK);
		add(brickCount, getWidth() - brickCount.getWidth(), getHeight() - brickCount.getAscent());
	}

	/* Assigns object involved in collision to variable collider
	 * Collider distinguishes between paddle, bricks, and brick count label
	 * 		Paddle: sign of vy is reversed
	 * 		Bricks: bounce clip is played and brick is removed, brick count is reduced by one
	 */
	private void whenBallCollides() {
		GObject collider = getCollidingObject();
		if(collider == paddle) {
			vy = -1 * Math.abs(vy); // fixes sticky paddle with absolute value
		}
		else if (collider != paddle && collider != brickCount && collider != null) {
			bounceClip.play();
			remove(collider);
			BRICK_COUNT--;
			vy = -1.02 * vy;	// speeds up velocity of ball after a brick is hit
		}
	}

	/* Checks if the ball has collided with an object
	 * Checks all four corners of the ball 
	 * If a corner returns true, then take that value as the GObject with which collision occured
	 * If all four corners return false, then no collision exists 
	 */
	private GObject getCollidingObject() {		
		if(getBallCoordinates(0,0) != null) {
			return getBallCoordinates(0,0);
		}
		else if(getBallCoordinates(2*BALL_RADIUS, 0) != null) {
			return getBallCoordinates(2*BALL_RADIUS,0);
		}
		else if(getBallCoordinates(0, 2*BALL_RADIUS) != null) {
			return getBallCoordinates(0, 2*BALL_RADIUS);
		}
		else if(getBallCoordinates(2*BALL_RADIUS, 2*BALL_RADIUS) != null) {
			return getBallCoordinates(2*BALL_RADIUS, 2*BALL_RADIUS);
		}
		else {
			return null;
		}		
	}
	
	/* General method to get the x and y location of the ball
	 * Adjusted in getCollidingObjects to account for all four corners
	 * Returns the collided object
	 */
	private GObject getBallCoordinates(double x, double y) {
		double ball_X = ball.getX() + x;
		double ball_Y = ball.getY() + y;		
		GObject collidedObject = getElementAt(ball_X, ball_Y);
		return collidedObject;
	}
	
	// Boolean methods to check if the ball has hit the walls
	private boolean hitBottomWall(GOval ball) { // Y location accounts for top left coordinates
		return ball.getY() > getHeight() - ball.getHeight();
	}

	private boolean hitTopWall(GOval ball) { // Y location will be 0 or less
		return ball.getY() <= 0;
	}

	private boolean hitLeftWall(GOval ball) { // X location will be 0 or less 
		return ball.getX() <= 0;
	}

	private boolean hitRightWall(GOval ball) { // X location accounts for top left coordinates 
		return ball.getX() >= getWidth() - ball.getWidth();
	}
}
