/*
 * File: BreakoutExtended.java
 * -------------------
* Name: Mel Guo
 * Section Leader: Luciano
 * 
 * This file will eventually implement the game of BreakoutExtended.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtended extends GraphicsProgram {

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
	
	//Diameter of the ball in pixels
	public static final double BALL_DIAM = BALL_RADIUS * 2;

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
	
	// brick 
	GRect brick;
	
	// paddle
	GRect paddle; 
	
	// ball
	GOval ball; 
	
	// x velocity of ball
	double vx;
	
	// y velocity of ball
	double vy;
	
	// initialize random-number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// counts number of bricks left
	private int brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;
	
	//number of turns gone
	private int turnsGone;
	
	// sets up Breakout, plays game while there are turns and bricks left, and displays results of the game
	public void run() {
		setTitle("CS 106A Breakout"); // Set the window's title bar text
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); 		// Set the canvas size.
		addMouseListeners();
		setupGame(); // sets up bricks, ball and paddle
		for(int turn = 0; turn < NTURNS; turn++) { // play the game while there are still lives left
			playGame();
			if(brickCounter == 0) { // stop the game if all bricks are removed
				break;
			}
			turnsGone++;
		}
		winOrLose(); // displays Win Or Lose
	}
	
	// horizontally move the paddle according to changes in x coordinates of the mouse
	public void mouseMoved(MouseEvent e)	{
		double mouseX = e.getX(); // get x coordinate of where the mouse moves
		double mouseFixedY = getHeight() - (PADDLE_HEIGHT + PADDLE_Y_OFFSET);
		if(mouseX > 0 && mouseX < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(mouseX, mouseFixedY); // paddle moves horizontally according to mouse movement
		}
	}

	// set up all parts of the game before playing
	private void setupGame() {
		setupBricks();
		createPaddle();
		createBall();
	}
	
	// setup the bricks in their orientation and color
		private void setupBricks() {
			for(int row = 0; row < NBRICK_ROWS; row ++) {
				for(int col = 0; col < NBRICK_COLUMNS; col ++) {
					double x = (getWidth() - (BRICK_WIDTH * NBRICK_COLUMNS) - (BRICK_SEP * (NBRICK_COLUMNS-1)))/2 + (col * (BRICK_WIDTH + BRICK_SEP));			
					double y = BRICK_Y_OFFSET + (row * (BRICK_HEIGHT + BRICK_SEP));
					brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
					brick.setFilled(true);
					colorBricks(row);
					add(brick);
				}
			}
		}
		
	// create paddle in a centered position near the bottom of the screen
	private void createPaddle() {
		double x = (getWidth() / 2) - (PADDLE_WIDTH / 2);
		double y = getHeight() - (PADDLE_HEIGHT + PADDLE_Y_OFFSET);
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	// create ball using given coordinates and dimensions and add it to the screen
	private void createBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_DIAM, BALL_DIAM);
		ball.setFilled(true);
		add(ball);
	}
	
	//color the bricks accordingly
	private void colorBricks(int row) {
		if(row < 2) {
			brick.setColor(Color.RED);
		}
		if(row == 2 || row == 3) {
			brick.setColor(Color.ORANGE);
		}
		if(row == 4 || row == 5) {
			brick.setColor(Color.YELLOW);
		}
		if(row == 6 || row == 7) {
			brick.setColor(Color.GREEN);
		}
		if(row == 8 || row == 9) {
			brick.setColor(Color.CYAN);
		}
	}

	private void playGame() {
		setBallVelocity();
		waitForClick();
		while(true) {
			moveBall();
			pause(DELAY);
			if(ball.getY() >= getHeight()) { // if the paddle fails to catch the ball, remove and reset the ball for the next round (if any lives left)
				remove(ball);
				createBall();
				break;
			}
			if(brickCounter == 0) { // end the game if player removes all bricks
				break;
			}
		}
	}
	
	// moves a ball that correctly bounces off walls and paddles as well as removes colliding bricks
	private void moveBall() {
		ball.move(vx, vy); 
		bounceOffWalls();
		bounceOffCollisions();
	}

	// reverse the x or y velocities accordingly when the ball bounces off the side walls or top wall
		private void bounceOffWalls() {
			if(ball.getX() + BALL_DIAM >= getWidth() || ball.getX() <= 0) { // reverse x velocity of the ball when it hits the right or left wall
				vx = - vx;
			}
			if(ball.getY() <= 0) { // reverse the y velocity of the ball when it hits the ceiling
				vy = -vy;
			}
		}
		
	// ball bounces off 2 types of collisions: paddle and brick
	// if ball collides with a brick, the brick is removed
	private void bounceOffCollisions() {
		GObject collider = getCollidingObject();
		if(collider == paddle) { // when the ball collides with the paddle, it always moves up
			vy = Math.abs(vy) * -1;
			}
		else if(collider != null) { // when the ball collide with the brick, it removes the brick and reverses the y velocity
			remove(collider);
			vy = -vy;
			brickCounter--; // update number of remaining bricks after each brick removal
		}
	}

	// returns the object involved in the collision
	private GObject getCollidingObject() {
		double leftX = ball.getX();
		double rightX = ball.getX() + BALL_DIAM;
		double topY = ball.getY();
		double bottomY = ball.getY() + BALL_DIAM;

		if(getElementAt(leftX, topY) != null) { //looks for a collision at the top left corner of the GOval
			return getElementAt(leftX, topY);
		}
		else if(getElementAt(rightX, topY) != null) { //looks for a collision at the top right corner of the GOval
			return getElementAt(rightX, topY);
		}
		else if(getElementAt(leftX, bottomY) != null) { //looks for a collision at the bottom left corner of the GOval
			return getElementAt(leftX, bottomY);
		}
		else if(getElementAt(rightX, bottomY) != null) { //looks for a collision at the bottom right corner of the GOval
			return getElementAt(rightX, bottomY);
		}
		else { 
			return null; // returns null if there are no collisions
		}
	}
	
	// sets given y velocity and randomized x velocity
	private void setBallVelocity() {
		vy = VELOCITY_Y; // sets y velocity to a constant
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); // sets x velocity to a randomly generated double within bounds of min and max
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}
	
	// displays result of the game
	private void winOrLose() {
		double x;
		double y;
		if(turnsGone == NTURNS) { // if all turns have been used before all bricks are removed, the player loses the game
			remove(ball);
			remove(paddle);
			GLabel youLose = new GLabel("rip you lose");
			x = getWidth()/2 - youLose.getWidth()/2;
			y = getHeight()/2 - youLose.getHeight()/2;
			add(youLose, x, y);
		}
		if(brickCounter == 0) { // player wins the game if all bricks are removed
			GLabel youWin = new GLabel("congrats! you're a brickbreaker :^)", getWidth()/2, getHeight()/2);
			x = getWidth()/2 - youWin.getWidth()/2;
			y = getHeight()/2 - youWin.getHeight()/2;
			add(youWin, x, y);
		}
	}

}