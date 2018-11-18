/*
 * File: Breakout.java
 * -------------------
 * Name:Asa Kohrman
 * Section Leader:
 * Ruiqi Chen
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
	
	private GRect paddle = new GRect (0,0, PADDLE_WIDTH, PADDLE_HEIGHT);

	private GOval ball = new GOval (0, 0,BALL_RADIUS*2, BALL_RADIUS*2);
	
	private double vx = 0;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private double vy = VELOCITY_Y;
	
	private double bricks = NBRICK_ROWS*NBRICK_COLUMNS;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//build the bricks, place the ball and the paddle
		createSetUp();
		addMouseListeners();
		//controls how many times the game runs and the results of removing all bricks vs. failing all three tries
		gameRounds();
	}
	
	private void gameRounds() {
		for (int i=0; i<NTURNS; i++) {
			//has the game run NTURNS # of rounds
			waitForClick();
			runGame();
			addBall();
			if (bricks==0) {
				break;
			}
		}
		if (bricks==0) {
			//ends the game if all the bricks are removed
			remove(ball);
			GLabel youWin = new GLabel ("YOU WIN!");
			youWin.setCenterLocation(getWidth()/2, getHeight()/2);
			add(youWin);
		}
		if (bricks!=0) {
			//reports a loss if all bricks are not removed in NTURNS
			remove(ball);
			GLabel youLose = new GLabel ("YOU LOSE");
			youLose.setCenterLocation(getWidth()/2, getHeight()/2);
			add(youLose);
		}
	}
	private void checkForColliders() {
		//checks if there is a "collider" or GObject at a location 
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			//if the collider is a GObject it changes the velocity of the ball
			vy=-vy;
		}else {
			if (collider != null) {
				//if there is a collider that is not the paddle it must be a brick so the ball changes y velocity and the collider (brick) is removed
				vy=-vy;
				remove(collider);
				bricks = bricks-1;
			}
		}
	}

	private void runGame() {
		//checks if the ball is at any of the walls
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);
			if (ball.getX()+2*BALL_RADIUS>=getWidth()) {
				vx=-vx;
			}
			if (ball.getX()<=0) {
				vx=-vx;
			}
			if (ball.getY()<=0) {
				vy=-vy;
			}
			if(ball.getY()>=getHeight()) {
				//breaks from the animation loop if the ball hits the bottom wall (if the game is lost)
				pause(DELAY);
				break;
			}
			checkForColliders();
			//checks if the ball has hit a non-wall collider (brick or paddle)
			if (bricks == 0) {
				//ends the game if the bricks are gone
				remove(ball);
				break;
			}
		}
	}			
	public void mouseMoved(MouseEvent e) {
		//sets the x coordinate of the paddle to the x coordinate of the mouse
		double x = e.getX();
		paddle.setLocation(x, getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
	}
	private GObject getCollidingObject() {
		//checks each corner of the ball for a colliding object and returns the object at that point
		double x = ball.getX();
		double y = ball.getY();
		GObject check1 = getElementAt(x,y);
		GObject check2 = getElementAt(x+2*BALL_RADIUS,y);
		GObject check3 = getElementAt(x,y+2*BALL_RADIUS);
		GObject check4 = getElementAt(x+2*BALL_RADIUS,y+2*BALL_RADIUS);
		if (check1!=null) {
			return(check1);
		}
		if (check2!=null) {
			return(check2);
		}
		if (check3!=null) {
			return(check3);
		}
		if (check4!=null) {
			return(check4);
		}else {
			return (null);
		}
	}
	private void createSetUp() {
		//adds in the graphics
		buildRows();
		addPaddle();
		addBall();
	}
	private void addBall() {
		//creates the ball
		double x = getWidth()/2-BALL_RADIUS;
		double y = getHeight()/2-BALL_RADIUS;
		ball.setLocation(x, y);
		ball.setFilled(true);
		add(ball);
	}
	private void addPaddle() {
		//creates the paddle
		double x = (getWidth()-PADDLE_WIDTH)/2; 
		double y = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle.setLocation(x, y); 
		paddle.setFilled(true);
		add(paddle);
	}
	private void buildRows(){
		//creates rows and sets colors for them
		double initialXPosition = getWidth()-(getWidth()-BRICK_SEP);
		double xPosition = getWidth()-(getWidth()-BRICK_SEP);
		double yPosition = BRICK_Y_OFFSET;
		Color c = Color.RED;
		for (int h=0; h<NBRICK_ROWS; h++) {
			for (int i=0; i<NBRICK_COLUMNS; i++) {
				brick(xPosition,yPosition, c);
				xPosition = xPosition + BRICK_WIDTH + BRICK_SEP;
			}
			if (yPosition >= BRICK_Y_OFFSET+ (BRICK_HEIGHT+BRICK_SEP)) {
				c = Color.ORANGE;
			}
			if (yPosition >= BRICK_Y_OFFSET+ (BRICK_HEIGHT+BRICK_SEP)*3) {
				c = Color.yellow;
			}
			if (yPosition >= BRICK_Y_OFFSET+ (BRICK_HEIGHT+BRICK_SEP)*5) {
				c = Color.green;
			}
			if (yPosition >= BRICK_Y_OFFSET+ (BRICK_HEIGHT+BRICK_SEP)*7) {
				c = Color.blue;
			}
			xPosition = initialXPosition;
			yPosition = yPosition+BRICK_HEIGHT+BRICK_SEP;
		}
	}
	private void brick(double x, double y, Color c) {
		//creates a brick
		GRect brick = new GRect (x,y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setColor(c);
		brick.setFilled(true);
		add(brick);
	}
}