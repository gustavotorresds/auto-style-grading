/*
 * File: Breakout.java


 * -------------------
 * Name: Natalie Chun
 * Section Leader: Thariq
 * 
 * This file will eventually implement the game of Breakout.
 * The game begins with the ball in the middle of the screen.
 * Once it is clicked, the ball drops and begins the game.
 * The objective is to hit all of the bricks by bouncing the ball off of the paddle until none are left.
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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	int row = 0;
	int col = 0;
	String event;
	int mouseX, mouseY;

	double vx, vy;

	GRect paddle; 
	GOval ball;
	GRect brick;

	public static final double BRICK_X_OFFSET = (CANVAS_WIDTH -(BRICK_SEP*(NBRICK_ROWS-1)+BRICK_WIDTH*NBRICK_ROWS))/2;

	private int brickTracker = NBRICK_COLUMNS*NBRICK_ROWS;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() { 

		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setup();

		playBreakout();

		endBreakout();
	}

	private void endBreakout() {
		remove(ball);
		GLabel label;
		if(brickTracker == 0) {
			label = new GLabel("You Won!");
		}else {
			label = new GLabel("You Lose!");
		}
		label.setFont("Courier-24");
		add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2 - label.getAscent()/2);
	}

	//creates the bricks, the paddle, and the ball
	private void setup() {
		printBricks();
		addPaddle();
		makeBall();
	}

	//bricks are printed out based on row modulus 10, which is the number of bricks per row
	private void printBricks() {
		for(int row = 1; row<=NBRICK_ROWS; row++) {
			for(int col = 1; col<= NBRICK_COLUMNS; col++) {	
				brick = new GRect (BRICK_X_OFFSET + (col-1) * (BRICK_WIDTH + BRICK_SEP), BRICK_Y_OFFSET + (row-1) * 
						(BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				if(row%10 == 1 || row%10 ==2) brick.setColor(Color.RED);
				if(row%10 == 3 || row%10 ==4) brick.setColor(Color.ORANGE);
				if(row%10 == 5 || row%10 ==6) brick.setColor(Color.YELLOW);
				if(row%10 == 7 || row%10 ==8) brick.setColor(Color.GREEN);
				if(row%10 == 9 || row%10 ==0) brick.setColor(Color.CYAN);
				brick.setFilled(true);
				add(brick);		
			}
		}
	}

	//mouse tracks paddle from the middle of the paddle
	//the middle of paddle is the X value assigned to the mouse to move the paddle back and forth
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX();
		if(mouseX < getWidth()-PADDLE_WIDTH/2) {
			if(mouseX>PADDLE_WIDTH/2) {
				paddle.setLocation(mouseX-PADDLE_WIDTH/2, getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
			}
		}
	}

	//paddle is centered and placed the Y offset distance away from the bottom of the canvas
	private void addPaddle() {
		double x = BRICK_X_OFFSET+ getWidth()/2 - PADDLE_WIDTH/2;
		double y = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle,x,y);
	}

	//ball is made and colored black
	private void makeBall() {
		double x = getWidth()/2- BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval (2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball, x, y);
	}

	//ball is centered in middle of screen
	private void setBall() {
		double x = getWidth()/2- BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball.setLocation(x, y);
	}

	//The ball is assigned a random direction upon the users' click.
	//Once the ball drops, the user can play until they run out of their three turns.
	private void playBreakout() {
		for(int i = 0; i<3 && brickTracker != 0; i++) {
			setBall();
			waitForClick();
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			vy = VELOCITY_Y;
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			while (ball.getY() < getHeight() &&  brickTracker != 0) {
				ballMove();
				pause(DELAY);
			}
		}
	}

	//ball moves and checks for collisions with the walls, the paddle, and the bricks
	private void ballMove() {
		ball.move(vx, vy);
		checkWallCollisions();
		checkPaddleAndBrickCollisions();
	}

	//ball moves and is constrained by the left, right, and top walls of the canvas.
	//each time the ball bounces off of a wall, its velocity is changed to the opposite
	//of what it was before the collision
	private void checkWallCollisions() {
		//left wall
		if(ball.getX()-vx <= 0 && vx <0 ) {
			vx = -vx;
		}
		//right wall
		if(ball.getX() + vx >= (getWidth() - BALL_RADIUS*2) && vx>0) {
			vx = -vx;
		}
		//top wall
		if(ball.getY() - vy <= 0 && vy < 0) {
			vy = -vy;
		}
	}

	//identifies if the collision is with the paddle or with a brick
	//if the ball collides with the paddle, it's direction is reversed
	//if the ball hits a brick, the brick is removed, and its direction is reversed
	private void checkPaddleAndBrickCollisions() {
		GObject collider = getCollidingObject();
		if(collider == paddle) {
			vy = -Math.abs(vy);
		}

		//is null instead of brick because brick would return the last remaining brick
		else if (collider != null) {
			brickTracker--;
			remove(collider);
			vy = -vy;
		}
	}

	//detects whether or not there is an object present at 
	//either of the four points on the circle
	private GObject getCollidingObject() { 
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()) != null) {
			return (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()));
		}
		else if (getElementAt (ball.getX(), ball.getY() + 2*BALL_RADIUS) != null) {
			return (getElementAt (ball.getX(), ball.getY()+2*BALL_RADIUS));
		}
		else if(getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+ 2*BALL_RADIUS) != null) {
			return (getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+ 2*BALL_RADIUS));
		}else {
			return null;
		}
	}
}