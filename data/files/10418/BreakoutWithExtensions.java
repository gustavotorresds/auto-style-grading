/*
 * File: Breakout.java
 * -------------------
 * Name: Cami Katz
 * Section Leader: Jonathan Kula
 * 
 * This file plays the Breakout game until all tries are used up or no bricks are left.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutWithExtensions extends GraphicsProgram {

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
	
	// Number of total bricks
	private int numberBricks = NBRICK_COLUMNS * NBRICK_ROWS;

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
	
	// Paddle
	private GRect paddle;
	
	// Ball
	private GOval ball;
	
	// Ball velocity
	private double vx;
	private double vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		setUp();
		addMouseListeners();
		
		// Runs game for a certain number of turns. Ends if no bricks left
		for (int numberTries = 0; numberTries < NTURNS; numberTries++) {
			GLabel beginRound = new GLabel("Click to begin. You have " + (3 - numberTries) + " turn(s) left");
			beginRound.setFont("Courier-12");
			add(beginRound, getWidth()/2 - beginRound.getWidth()/2, getHeight()/2);
			waitForClick();
			remove(beginRound);
			playGame();
			if (numberBricks == 0) {
				GLabel win = new GLabel("You Win!");
				win.setFont("Courier-24");
				add(win, getWidth()/2 - win.getWidth()/2, getHeight()/2);
				break;

			}
		}
		
		if (numberBricks != 0) {
			GLabel lose = new GLabel("You lost. Sorry :(");
			lose.setFont("Courier-24");
			lose.setColor(Color.RED);
			add(lose, getWidth()/2 - lose.getWidth()/2, getHeight()/2);
		}
	}
	
	private void setUp() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		
		// Set the canvas size 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Sets up the bricks
		setBricks();
		
		//Sets up paddle
		setPaddle();
	}
	
	private void playGame() {
		// Sets up ball in middle of screen;
		setBall();
		
		// Sets up speed of ball;
		setSpeed();
		
		// Plays game until all bricks have been hit or the bottom wall is hit
		while(true) {
			// Moves ball;
			moveBall();	
			pause(DELAY);
			if (numberBricks == 0)
				break;
			if (hitSWall() == true) {
				remove(ball);
				break;
			}
		}
	}
	
	// Places ball in middle of screen for it to start, fills and colors it.
	private void setBall() {
		double startX = getWidth()/2 - BALL_RADIUS;
		double startY = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(BALL_RADIUS, BALL_RADIUS);
		ball.setLocation(startX, startY);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}
	
	// Sets speed of ball, y speed stays same, x speed is generated randomly between 1 and 3.
	private void setSpeed() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = 3.0;
	}
	
	// Moves ball while there are still bricks, if hits paddle or top or side walls, bounces back in opposite direction.
	// Breaks if hits bottom or there are no bricks left
	private void moveBall() {
		GObject collider = getCollidingObject();
		if (hitNWall() == true || hitSWall() == true) {
			vy = -vy;
		}
		if (hitEWall() == true || hitWWall() == true) {
			vx = -vx;
		}
		if (collider == paddle) {
			if (ball.getY() == paddle.getY()) {
				vy = -vy;
				//AudioClip bounceClip = (MediaTools.loadAudioClip("bounce.au");
				//bounceClip.play();
			}
		}
		if (collider != null && collider != paddle) {
			vy = -vy;
			remove(collider);
			numberBricks --;
			}
		ball.move(vx, vy);	
	}
	
	private GObject getCollidingObject() {
		GObject collObj1 = getElementAt(ball.getX(), ball.getY());
		if (collObj1 != null) {
			return collObj1;
		}
		else {
			GObject collObj2 = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
			if (collObj2 != null) {
				return collObj2;
			}
			else {
				GObject collObj3 = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
				if (collObj3 != null) {
					return collObj3;
				}
				else {
					GObject collObj4 = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
					if (collObj4 != null) {
						return collObj4;
					}	
					else {
						return null;
					}
				}
			}
		}
	}
	
	// Returns true if hits north wall
	private boolean hitNWall() {
		if (ball.getY() <= 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// Returns true if hits south wall
	private boolean hitSWall() {
		if (ball.getY() >= getHeight() - (BALL_RADIUS)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// Returns true if hits west wall
	private boolean hitWWall() {
		if (ball.getX() >= getWidth() - (BALL_RADIUS)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// Returns true if hits east wall
	private boolean hitEWall() {
		if (ball.getX() <= 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// Creates the paddle block, adds color, location, fills.
	private void setPaddle() {
		double startX = (getWidth() - PADDLE_WIDTH)/2;
		double startY = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setLocation(startX, startY);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
		addMouseListeners();
	}
	
	// Tracks mouse and sees if it moved. Will change paddle x location depending on x location of mouse.
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();
		if(tooFarLeft(mouseX) == true) {
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
		else if(tooFarRight(mouseX) == true) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
		else {
			paddle.setLocation(mouseX - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}
	
	// Returns true if paddle is too far to the right
	private boolean tooFarRight(double mouseX) {
		if (mouseX >= getWidth() - PADDLE_WIDTH)
			return true;
		else
			return false;
	}

	// Returns true if paddle is too far to the left
	private boolean tooFarLeft(double mouseX) {
		if (mouseX <= PADDLE_WIDTH/2)
			return true;
		else
			return false;
	}

	// Makes 10 rows of 10 bricks in each column, spaced a certain distance apart.
	// Fills and colors bricks.
	private void setBricks() {
		double xStart = (getWidth() - (BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS + BRICK_SEP)/2;
		for (int level = 0; level < NBRICK_ROWS; level++) {			
			for(int brick = 0; brick < NBRICK_COLUMNS; brick++) {
				double xBrick =  xStart + (BRICK_WIDTH + BRICK_SEP) * brick; // The X coordinate of where each brick starts
				double yBrick = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * level; // The Y coordinate of where each brick starts
				GRect rect = new GRect (xBrick, yBrick, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				setColor(rect, level);
				add(rect);
			}
		}
	}
	
	// Sets color of all bricks. 
	// Top 2 rows are red, next 2 are orange, next 2 are yellow, next 2 are green, bottom 2 are cyan
	private void setColor(GRect rect, int level){
		if(level == 0 || level == 1)
			rect.setColor(Color.RED);
		else if(level == 2 || level == 3)
			rect.setColor(Color.ORANGE);
		else if(level == 4 || level == 5)
			rect.setColor(Color.YELLOW);
		else if(level == 6 || level == 7)
			rect.setColor(Color.GREEN);
		else
			rect.setColor(Color.CYAN);
	}

}

