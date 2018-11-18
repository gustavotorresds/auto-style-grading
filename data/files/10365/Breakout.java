/*
 * File: Breakout.java
 * -------------------
 * Name: Annika
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
	public static final double VELOCITY_Y = 5.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;
	
	//Welcome message display time
	public static final double INTRO = 1500.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Paddle variables
	private double paddleX;
	private double paddleY;
	private GRect paddle;
	
	//Ball velocity components
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;

	//Counter for number of bricks
	private int numBricks=NBRICK_COLUMNS*NBRICK_ROWS;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		welcomeMessage();
		setUpGame();
		playGame();
	}
	
	//Welcome message for user
	private void welcomeMessage() {
		GLabel welcome = new GLabel("WELCOME TO BREAKOUT");
		welcome.setFont("Courier-35");
		add(welcome, getWidth()/2 - welcome.getWidth()/2, getHeight()/2 - welcome.getHeight()/2);
		pause(INTRO);
		remove(welcome);
	}

	public void playGame() {
		//Coordinates for middle of screen
		double cx = getWidth()/2;
		double cy = getHeight()/2;
		
		//Ball information
		GOval ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball,cx-BALL_RADIUS, cy-BALL_RADIUS);

		//Outer for loop gives user NTURNS to win the game
		for (int i=0; i<NTURNS; i++) {
			
			//ends the Turns for loop if the user has already won
			if (numBricks==0) {
				break;
			}
			
			//Sets initial ball position and resets between turns
			ball.setLocation(cx-BALL_RADIUS, cy-BALL_RADIUS);
			
			waitForClick();
			
			//Ball velocities
			vx = rgen.nextDouble(1.0, 3.0);
			vy = VELOCITY_Y;
			
			//Animation loop
			while(true) {								//!hitBottomWall(ball) && numBricks>0
				ball.move(vx, vy);
				pause(DELAY);
				if(hitLeftWall(ball)) {
					if (rgen.nextBoolean(0.5)) vx = Math.abs(vx);
				}
				if(hitRightWall(ball)) {
					if (rgen.nextBoolean(0.5)) vx = -Math.abs(vx);
				}
				if (hitTopWall(ball)) {
					vy = Math.abs(vy);
				}
				if (hitBottomWall(ball)) {
					break;
				}
				GObject collider = getCollidingObject(ball);
				if (collider == paddle) {
					vy=-Math.abs(vy);
				} else if (collider != null) {
					vy=-vy;
					remove(collider);
					numBricks = numBricks-1;
					if (numBricks ==0) {
						break;
					}
				}	
			}
		}
		
		//Game Over
		if (numBricks==0) {
			GLabel win = new GLabel("YOU WIN!!!");
			win.setFont("Courier-40");
			add(win, getWidth()/2 - win.getWidth()/2, getHeight()/2 - win.getHeight()/2);
		}
		if (numBricks != 0) {
			GLabel lose = new GLabel("YOU LOSE :(", getWidth()/2, getHeight()/2);
			lose.setFont("Courier-40");
			add(lose, getWidth()/2 - lose.getWidth()/2, getHeight()/2 - lose.getHeight()/2);
		}
	}	
	
	//Takes in ball and returns collider as either null or the object the ball has collided with
	private GObject getCollidingObject(GOval ball) {
		double ballX = ball.getX();
		double ballY = ball.getY();
		GObject collider = null;
		if (getElementAt(ballX, ballY) != null) {
			collider = getElementAt(ballX, ballY);

		} else if (getElementAt(ballX+2*BALL_RADIUS, ballY) != null) {
			collider = getElementAt(ballX+2*BALL_RADIUS,ballY);

		} else if (getElementAt(ballX, ballY+2*BALL_RADIUS) != null) {
			collider = getElementAt(ballX, ballY+2*BALL_RADIUS);

		} else if (getElementAt(ballX+2*BALL_RADIUS, ballY+2*BALL_RADIUS) != null) {
			collider = getElementAt(ballX+ 2*BALL_RADIUS, ballY+2*BALL_RADIUS);	
		}
		return collider;	
	}

	//Conditions for ball bouncing off walls
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - ball.getHeight();
	}
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
//Sets up bricks and paddle
	public void setUpGame() {
		for (int i=0; i<NBRICK_ROWS; i++) { 					
			int counter = i+1;
			for(int n=0; n<NBRICK_COLUMNS; n++) {
				double cx = getWidth()/2;
				GRect brick = new GRect(cx - 0.5*BRICK_WIDTH*NBRICK_COLUMNS - 0.5*BRICK_SEP*(NBRICK_COLUMNS-1)+ n*BRICK_SEP + n*BRICK_WIDTH, 
						BRICK_Y_OFFSET+i*BRICK_HEIGHT + i*BRICK_SEP, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (counter%10 == 1 || counter%10== 2) {
					brick.setColor(Color.RED);
				} 
				if (counter%10 == 3 || counter%10 == 4) {
					brick.setColor(Color.ORANGE);
				}
				if (counter%10 == 5 || counter%10 == 6) {
					brick.setColor(Color.YELLOW);
				}
				if (counter%10 == 7 || counter%10 == 8) {
					brick.setColor(Color.GREEN);
				}
				if (counter%10 == 9 || counter%10 == 0) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}	
		}
		paddleX = getWidth()/2 - PADDLE_WIDTH/2;
		paddleY = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	//Sets mouse controls for paddle
	public void mouseMoved(MouseEvent e) {												
		paddleX = e.getX()-PADDLE_WIDTH/2;
		if (paddleX<PADDLE_WIDTH/2) {
			paddleX = 0;																
		}
		if (paddleX>getWidth()-PADDLE_WIDTH) {
			paddleX = getWidth()-PADDLE_WIDTH;
		}
		paddle.setLocation(paddleX, paddleY);					
	}

}
