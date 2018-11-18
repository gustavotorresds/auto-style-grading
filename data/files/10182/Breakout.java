/*
 * File: Breakout.java

 * -------------------
 * Name: Brent Armistead
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
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	// variables for velocity of the ball in x and y directions
	private double vx, vy = 0;
	// rectangle for the paddle
	private GRect paddle = null;
	// circle for the ball
	private GOval ball = null;
	//number of bricks
	private int nbricks = 100;
	//label that shows number of bricks left
	GLabel count = null;
	

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();
		setUp();
		for(int turn = 0; turn < NTURNS; turn++) {
			ball.setCenterLocation(getWidth() / 2, getHeight() / 2);
			waitForClick();
			moveBall();
			while(true) {
				ball.move(vx, vy);
				
				collisionHandler();
				if(hitBottomWall()) {
					break;
				}
				
				count.setLabel("Brick count:" + nbricks);
				pause(DELAY);
				
				if(nbricks == 0) {
					GLabel win = new GLabel("You win!");
					win.setCenterLocation(getWidth() / 2, getHeight() / 2);
					add(win);
					return;
				}
			}
		}
		GLabel lose = new GLabel("You lose!");
		lose.setCenterLocation(getWidth() / 2, getHeight() / 2);
		add(lose);
	}

	//moves the ball in the specific velocity as stated in the constants at the beginning of the turn
	private void moveBall() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
	}

	//boolean to test if the collision was with the bottom wall (the end of a turn)
	private boolean hitBottomWall() {
		return ball.getY() + (2 * BALL_RADIUS) > getHeight();
	}

	//this method checks the four corners of the ball to see if the ball has collided with any object
	//and then it changes the direction of the ball once a collision occurs
	private void collisionHandler() {
		//four corners of ball with extensions to avoid oscillations in the paddle
		double lx = ball.getX();
		double rx = ball.getX() + (2 * BALL_RADIUS);
		double ty = ball.getY();
		double by = ball.getY() + (2 * BALL_RADIUS);
		//adds a middle point so that if it hits the side of anything it will bounce sideways 
		double my = ball.getY() + BALL_RADIUS;
		GObject collider = getElementAt(lx,ty);  // top left
		//checking to see if there is an element present and making sure it is not the label
		if(collider != null && collider != count) {
			if(collider == paddle) {
				vy = -vy;
			}else {
				vy = -vy;
				remove(collider);
				nbricks = nbricks - 1;
				
			}
			return;
		}
		
		//changes the corner it is looking at
		collider = getElementAt(rx,ty);  // top right
		if(collider != null && collider != count) {
			if(collider == paddle) {
				vy = -vy;
			}else {
				vy = -vy;
				remove(collider);
				nbricks = nbricks - 1;
			}
			return;
		}
		
		collider = getElementAt(lx,by);  // bottom left
		if(collider != null && collider != count) {
			if(collider == paddle) {
				vy = -vy;
			}else {
				vy = -vy;
				remove(collider);
				nbricks = nbricks - 1;
			}
			return;
		}
		
		collider = getElementAt(rx,by);  // bottom right
		if(collider != null && collider != count) {
			if(collider == paddle) {
				vy = -vy;
			}else {
				vy = -vy;
				remove(collider);
				nbricks = nbricks - 1;
			}
			return;
		}
		
		collider = getElementAt(lx,my);  // middle left
		if(collider != null && collider != count) {
			if(collider == paddle) {
				vx = -vx;
			}else {
				vx = -vx;
				remove(collider);
				nbricks = nbricks - 1;
			}
			return;
		}
		
		collider = getElementAt(rx,my);  // middle right
		if(collider != null && collider != count) {
			if(collider == paddle) {
				vx = -vx;
			}else {
				vx = -vx;
				remove(collider);
				nbricks = nbricks - 1;
			}
			return;
		}
		hitWall();
	}

	//changes the direction of the ball's velocity when it hits one of the side walls or the top wall
	//extension: every time the ball hits a wall, the speed in one or both directions changes
	//the top wall slows it down as for more motivation to break out
	private void hitWall() {
		if(ball.getY() < 0) {
			vy = -0.95 * vy;
		}
		
		if(ball.getX() + (2 * BALL_RADIUS) > getWidth() || ball.getX() < (0 * BALL_RADIUS)) {
			vx = -1.1 * vx;
			vy = 1.1 * vy;
		}
	}

	//sets up the game
	private void setUp() {
		addPaddle();
		addAllBricks();
		addBall();
		addBrickCount();
	}

	//adds the label that counts the number of bricks
	private void addBrickCount() {
		count = new GLabel("Bricks left:" + nbricks);
		count.setCenterLocation(getWidth() / 2, getHeight() - (PADDLE_Y_OFFSET / 2));
		add(count);
	}

	//places the ball in the middle of the window
	private void addBall() {
		ball = new GOval((getWidth() - BALL_RADIUS) / 2, (getHeight() - BALL_RADIUS) / 2, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	//places the paddle 
	private void addPaddle() {
		paddle = new GRect((getWidth() / 2) - (PADDLE_WIDTH / 2), getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	//places and puts color on all the bricks
	private void addAllBricks() {
		// default color
		Color bcolor = Color.WHITE;
		for(int row = 0; row < NBRICK_ROWS; row++) {
			double distance = (BRICK_HEIGHT + BRICK_SEP) * row;
			if(row % 2 == 0) {
				bcolor = changeColor(bcolor);
			}
			buildRowOfBricks(distance, bcolor);
		}
	}

	//part of the nested for loop that builds the row of bricks
	private void buildRowOfBricks(double distance, Color bcolor) {
		for(int column = 0; column < NBRICK_COLUMNS; column++) {
			GRect brick = new GRect(BRICK_SEP + ((BRICK_WIDTH + BRICK_SEP) * column), BRICK_Y_OFFSET + distance, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(bcolor);
			add(brick);
		}
	}

	//a series of if statements to put color in two rows at a time and then change the color
	private Color changeColor(Color bcolor) {
		if(bcolor == Color.RED) {
			return Color.ORANGE;
		}
		if(bcolor == Color.ORANGE) {
			return Color.YELLOW;
		}
		if(bcolor == Color.YELLOW) {
			return Color.GREEN;
		}
		if(bcolor == Color.GREEN) {
			return Color.CYAN;
		}
		return Color.RED;
	}

	//moves the paddle while keeping it on the same y coordinate
	public void mouseMoved(MouseEvent e) {
		paddle.setCenterX(e.getX());
		//stops the paddle when it hits the left wall
		if(e.getX() < (PADDLE_WIDTH / 2)) {
			paddle.setCenterX(PADDLE_WIDTH / 2);
		}
		
		//stops the paddle when it hits the right wall
		if(e.getX() > getWidth() - (PADDLE_WIDTH / 2)) {
			paddle.setCenterX(getWidth() - (PADDLE_WIDTH / 2));
		}
	}
}
