/*
 * File: Breakout.java
 * -------------------
 * Name: Alisha Birk
 * Section Leader: Marilyn Zhang
 * Sources: The Art & Science of Java by Eric Roberts 
 * 
 * Program that builds the game of Breakout including
 * bricks, paddle and ball with the objective of the player 
 * to remove all of the bricks off the screen using the ball and 
 * paddle. The player only has three lives, which are lost if the 
 * player lets the ball touch the bottom of the screen. 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Year;

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
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 6.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//Instance variables
	GRect paddle = null;
	GRect brick = null;
	GOval ball = null;
	GLabel label = null;
	private double vx;
	private double vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		addMouseListeners();
		initalizeBreakout();
		playBreakout();
	}

	/*
	 * Method: Initialize
	 * ----------------------
	 * Sets up Breakout by making the bricks, paddle, and ball.  
	 */
	private void initalizeBreakout() {
		makeAllBricks();
		makePaddle();
		makeBall();
	}
	
	/*
	 * Method: Make All Bricks
	 * ----------------------
	 * Draws all bricks for Breakout with their necessary color.
	 */
	private void makeAllBricks() { 
		double x = (getWidth() - (NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)))/2 + BRICK_SEP/2;
		double y = BRICK_Y_OFFSET;
		y = makeBrickRow(x, y, Color.RED);
		y = makeBrickRow(x, y, Color.ORANGE);
		y = makeBrickRow(x, y, Color.YELLOW);
		y = makeBrickRow(x, y, Color.GREEN);
		y = makeBrickRow(x, y, Color.CYAN);
	}

	/*
	 * Method: Make Brick
	 * ----------------------
	 * Draws one brick at any x,y location 
	 * of any width and height given.  
	 */
	private void makeBrick(double x, double y, Color color) {
		brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);
	}

	/*
	 * Method: Make Brick Row
	 * ----------------------
	 * Makes two rows of bricks for each given color and
	 * returns y so that subsequent rows will know where to begin.
	 */
	private double makeBrickRow(double x, double y, Color color) {
		for (int j = 0; j<2; j++) {
			for (int i = 0; i< NBRICK_COLUMNS; i++) {
				makeBrick(x, y, color);
				x = x + BRICK_WIDTH + BRICK_SEP;
			}
			y = y + BRICK_HEIGHT + BRICK_SEP;
			x = (getWidth() - (NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP)))/2 + BRICK_SEP/2;
		}
		return y;
	}
	
	/*
	 * Method: Make Paddle
	 * ----------------------
	 * Draws the paddle at the bottom of the screen
	 * based on the paddle y offset. 
	 */
	private void makePaddle() {
		double paddleX = getWidth()/ 2;
		double paddleY = getHeight()- PADDLE_Y_OFFSET;
		paddle = new GRect (paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * Method: Mouse Moved
	 * ----------------------
	 * Allows the mouse to determine the x location of the
	 * paddle without allowing the paddle to go off of the 
	 * screen. 
	 */
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX < 0) { //prevents the paddle from going off the left side of the screen
			mouseX = 0;
		}
		if (mouseX > getWidth() - PADDLE_WIDTH) { //prevents paddle from going off the right side of the screen
			mouseX = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(mouseX, paddle.getY());
	}

	/*
	 * Method: Make Ball
	 * ----------------------
	 * Draws the ball in the middle of the screen which is
	 * where it should be before the player starts the game. 
	 */
	private void makeBall() {
		double ballX = getWidth() / 2 - BALL_RADIUS;
		double ballY = getHeight()  / 2 + BALL_RADIUS;
		ball = new GOval (ballX, ballY, BALL_RADIUS * 2, BALL_RADIUS * 2); 
		ball.setFilled(true);
		add(ball);
	}

	/*
	 * Method: Play Breakout
	 * ----------------------
	 * Contains the animations required to make the ball bounce 
	 * and collide with objects so the player can play the game.
	 * Also includes some music for the player's enjoyment.  
	 */
	private void playBreakout() {
		AudioClip dankTunes = MediaTools.loadAudioClip("future.wav");
		waitForClick();
		dankTunes.loop();
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}		
		bounceBall();
	}

	/*
	 * Method: Bounce Ball
	 * ----------------------
	 * Runs the animation to bounce the ball off of the 
	 * paddle and the sides of the screen
	 * and interact with other objects 
	 */
	private void bounceBall() {
		int lives = NTURNS;
		int numBricks = NBRICK_COLUMNS * NBRICK_ROWS;
		while(lives != 0 && numBricks !=0) { 
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			if (hitBottomWall(ball)) { 
				lives--;
				remove(ball); //ball starts back in its original starting position
				makeBall();
				if (lives != 0) { //so the player doesn't have to click for "you lose" to appear
				waitForClick(); //player can control when to play again
				}
			}
			ball.move(vx, vy);
			pause(DELAY);
			numBricks = checkForCollision(numBricks);
		}
		if (numBricks == 0) {
			makeLabel("You win!!");
		}
		if (lives == 0) {
			makeLabel("You lose :(");
		}
	}
	
	/*
	 * Method: Check for Collision
	 * ----------------------
	 * Checks to see if the ball has collided with an object has collided 
	 * with an object. If the ball collides with the paddle it
	 * changes direction, but if it collides with a brick it removes
	 * it from the screen. 
	 */
	private int checkForCollision(int numBricks) {
		AudioClip brickBounce = MediaTools.loadAudioClip("molly.wav");
		AudioClip paddleBounce = MediaTools.loadAudioClip("Perercocet.wav");
		GObject collider = getCollidingObject();
		if (collider == paddle) { //change direction when ball hits paddle
			vy = -vy;
			paddleBounce.play();
		} else if (collider != null){ //removes bricks 
			remove(collider);
			vy = -vy;
			brickBounce.play();
			numBricks--;
		}
		return numBricks;
	}
	
	/*
	 * Method: Hit Left Wall
	 * ----------------------
	 * Checks to see if the ball has hit the 
	 * left wall of the screen. 
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	/*
	 * Method: Hit Left Wall
	 * ----------------------
	 * Checks to see if the ball has hit the 
	 * right wall of the screen. 
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	
	/*
	 * Method: Hit Left Wall
	 * ----------------------
	 * Checks to see if the ball has hit the 
	 * top wall of the screen. 
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	

	/*
	 * Method: Hit Left Wall
	 * ----------------------
	 * Checks to see if the ball has hit the 
	 * bottom wall of the screen. 
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}
	
	/*
	 * Method: Get Colliding Object
	 * ----------------------
	 * Checks each corner of the ball to see if it has interacted with
	 * another object on the screen and if a collision has occurred.  
	 */
	private GObject getCollidingObject() {
		GObject topLeftCorner = getElementAt(ball.getX(), ball.getY());
		if (topLeftCorner != null) {
			return topLeftCorner;
		}
		GObject topRightCorner = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		if (topRightCorner != null) {
			return topRightCorner;
		}
		GObject bottomLeftCorner = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		if (bottomLeftCorner != null) {
			return bottomLeftCorner;
		}
		GObject bottomRightCorner = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		if (bottomRightCorner != null) {
			return bottomRightCorner;
		}
		return null;
	}
	
	/*
	 * Method: Make Label
	 * ----------------------
	 * Makes a label that will be printed at the end of the 
	 * game to tell the player if they won or lost.   
	 */
	private void makeLabel(String str) {
		double xLabel = getWidth() / 2;
		double yLabel = getHeight() / 2;
		label = new GLabel(str, xLabel, yLabel);
		label.setFont("Courier-40");
		label.setLocation(xLabel - label.getWidth()/2, yLabel - label.getHeight()/2);
		add(label);
	}
}


