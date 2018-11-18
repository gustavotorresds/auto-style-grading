/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 900;

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
	public static final double BRICK_HEIGHT = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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

	private GImage ball = new GImage ("png mouth.png");
	
	private double vx = 0;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private double vy = VELOCITY_Y;
	
	private double bricks = NBRICK_ROWS*NBRICK_COLUMNS;

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//build the bricks, place the ball and the paddle
		createSetUp();
		addMouseListeners();
		gameRounds();
		//controls how many times the game runs and the results of removing all bricks vs. failing all three tries
	}
	//note! this extension was not working for me and I took it lair and they couldn't find the issue. all the important code is exactly the same as in my original which is completely functional
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
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			bounceClip.play();
			vy=-vy;
		}else {
			if (collider != null) {
				vy=-vy;
				remove(collider);
				bricks = bricks-1;
			}
		}
	}
	private void runGame() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);
			if (ball.getX()+2*BALL_RADIUS>=getWidth()) {
				bounceClip.play();
				vx=-vx;
			}
			if (ball.getX()<=0) {
				bounceClip.play();
				vx=-vx;
			}
			if (ball.getY()<=0) {
				bounceClip.play();
				vy=-vy;
			}
			if(ball.getY()>=getHeight()) {
				pause(DELAY);
				break;
			}
			checkForColliders();
			if (bricks == 0) {
				remove(ball);
				break;
			}
		}
	}			
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		paddle.setLocation(x, getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
	}
	private GObject getCollidingObject() {
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
		buildRows();
		addPaddle();
		addBall();
	}
	private void addBall() {
		//creates the ball
		double x = getWidth()/2-BALL_RADIUS;
		double y = getHeight()*0.75-BALL_RADIUS;
		ball.setSize(BALL_RADIUS*3, BALL_RADIUS*3);
		ball.setLocation(x, y);
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
			xPosition = initialXPosition;
			yPosition = yPosition+BRICK_HEIGHT+BRICK_SEP;
		}
	}
	private void brick(double x, double y, Color c) {
		//creates a brick
		GImage tidePod = new GImage ("tidepod.png");
		tidePod.setSize(BRICK_WIDTH, BRICK_HEIGHT);
		tidePod.setLocation(x, y);
		add(tidePod);
	}
}