/*
 * File: Breakout.java
 * -------------------
 * Name: Anuraag Nallapati
 * Section Leader: Julia Daniel
 * 
 * This file implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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

	//creates a paddle instance variable
	private GRect paddle = null;

	//creates a ball instance variable
	private GOval ball = null;

	//instance variables for velocity of ball
	private double vx, vy;

	private int counter = 0;

	//random number generator variable 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setupBricks();							//Setup the brick layout on top
		counter = NBRICK_ROWS*NBRICK_COLUMNS;
		addMouseListeners(); 					// add mouse listener to identify location of mouse
		createPaddle();							//create the paddle
		for(int turns=0;turns<3;turns++) {
			animateBall();
		}
	}


	// This method is used to lay out the bricks
	private void setupBricks() {
		for(int level=0; level<NBRICK_ROWS; level++) {															//Loops equals # of rows to be built
			for(int tile=0;tile<NBRICK_COLUMNS;tile++) {														//Loops equals # of columns in each row
				double x = getWidth()/2 - NBRICK_COLUMNS*(BRICK_WIDTH+BRICK_SEP)/2;								//To center the bricks, x coordinate is calculated as shown
				double y = BRICK_Y_OFFSET+level*(BRICK_HEIGHT+BRICK_SEP);										//y-coordinate changes with each level based on height and separation
				GRect rect = new GRect(x+tile*(BRICK_WIDTH+BRICK_SEP), y, BRICK_WIDTH, BRICK_HEIGHT);			//x-coordinate changes with each tile. Rest based on defined constants
				rect.setFilled(true);
				if(level==0||level==1) {
					rect.setColor(Color.RED);	
				}
				if(level==2||level==3) {
					rect.setColor(Color.ORANGE);
				}
				if(level==4||level==5) {
					rect.setColor(Color.YELLOW);
				}
				if(level==6||level==7) {
					rect.setColor(Color.GREEN);
				}
				if(level==8||level==9) {
					rect.setColor(Color.CYAN);
				}
				add(rect);
			}
		}
	}

	//This method is used to create the Paddle
	private void createPaddle() {
		paddle = new GRect (PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	//This method is to identify the position of the Paddle
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		x = Math.min(x,getWidth()-PADDLE_WIDTH);
		double y = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle.setLocation(x,y);
	}

	//This method is to create the Ball animation
	public GOval createBall() {
		double size = BALL_RADIUS * 2;
		ball = new GOval(size, size);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		double x = getWidth()/2-BALL_RADIUS;
		double y = getHeight()/2-BALL_RADIUS;
		add(ball, x, y);
		return ball;

	}

	/**
	 * Method: Hit Bottom Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the bottom wall of the window.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/**
	 * Method: Hit Top Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	/**
	 * Method: Hit Right Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the right wall of the window.
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/**
	 * Method: Hit Left Wall
	 * -----------------------
	 * Returns whether or not the given ball should bounce off
	 * of the left wall of the window.
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	//Animates the ball based on rules of the game	
	private void animateBall() {
		ball = createBall();					//create ball
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		while(true) {
			// update velocity
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			if(hitBottomWall(ball)) {
				remove(ball);
				break;		
			}
			ball.move(vx, vy);
			GObject collider = getCollidingObject();
			if(collider!=null) {
				vy=-vy;
				if(collider!=paddle) {
					remove(collider);
					counter = counter -1;
				}
			}
			if(counter==0) {
				break;
			}
			pause(DELAY);											// pause
		}
	}

	//Solving for collisions
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		GObject obj1 = getElementAt(x, y);
		GObject obj2 = getElementAt(x+2*BALL_RADIUS, y);
		GObject obj3 = getElementAt(x, y+2*BALL_RADIUS);
		GObject obj4 = getElementAt(x+2*BALL_RADIUS, y+2*BALL_RADIUS);
		if(obj1!=null) {
			return obj1;
		}
		if(obj2!=null) {
			return obj2;
		}
		if(obj3!=null) {
			return obj3;
		}
		if(obj4!=null) {
			return obj4;
		}
		return obj4;
	}

}