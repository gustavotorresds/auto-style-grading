/*
 * File: Breakout.java
 * -------------------
 * Name: Taylor Spann
 * Section Leader: Jonathan Kula
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

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
	
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private GRect brick;
	private GRect paddle;
	private GOval ball;
	
	private int remainingBrick = NBRICK_ROWS*NBRICK_COLUMNS;
	
	private GObject getCollidingObject() {
		if((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}	
		if(getElementAt((ball.getX()+BALL_RADIUS*2), ball.getY()) != null) {
			return getElementAt((ball.getX()+BALL_RADIUS*2), ball.getY());
		}
		if (getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2)) != null) {
			return getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2));
		}
		if (getElementAt((ball.getX()+BALL_RADIUS*2),(ball.getY() + BALL_RADIUS*2)) != null) {
			return getElementAt((ball.getX()+BALL_RADIUS*2),(ball.getY() + BALL_RADIUS*2));
		}
		else {
			return null;
		}
	}

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		
		for(int i=0; i<NTURNS; i++) {
			
			//SETUP GAME
			gameSetup();
			//PLAY GAME
			playGame();
			
			if(remainingBrick == 0) {
				//win
			}
		}
		if(remainingBrick>0) {
			//lose game
			//play sad music
		}
		
	}
	
	//SETUP GAME
	private void gameSetup() {
		// 1. Bricks		
		brickSetup(getWidth()/2, BRICK_Y_OFFSET);
		// 2. Paddle
		paddleSetup();
		// 3. Ball
		ballSetup();
	}
	
	//PLAY GAME
	private void playGame() {
		
		//1.click
		waitForClick();
		//2.move ball
		ballVelocity();
		
		while (true) {
			moveBall();
			if (ball.getY()>= getHeight()) {
				break;
			}
			if(remainingBrick == 0) {
				break;
			}
		}
		//3.move paddle X
		//4.break bricks
		
		
		
		
		
		
	}
	
	private void brickSetup(double centerX, double centerY) {
		
		for(int row = 0; row < NBRICK_ROWS; row++) {
			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				
				//brick location
				double brickX = centerX - (BRICK_WIDTH*NBRICK_COLUMNS)/2 - ((NBRICK_COLUMNS-1)*BRICK_SEP)/2 + column*BRICK_WIDTH + column*BRICK_SEP;
				double brickY = centerY  + (BRICK_HEIGHT*row) + (row*BRICK_SEP);
				
				//adding bricks
				brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				add (brick);
				brick.setFilled(true);
		
				//Brick colors
				if (row == 0 || row == 1) {
					brick.setColor(Color.RED);
				}
				if (row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
				}
				if (row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				}
				if (row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				}
				if (row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
				}
			
			}

		}

	}
	
	private void paddleSetup() {
		paddle = new GRect ((getWidth()/2)-PADDLE_WIDTH/2, (getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);
		addMouseListeners();
	
	}
	
	private void ballSetup() {
		ball = new GOval (getWidth()/2-BALL_RADIUS, getHeight()/2-PADDLE_Y_OFFSET-PADDLE_HEIGHT-BALL_RADIUS , BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add (ball);
	}
	
	//mouse setup + paddle move
	public void mouseMoved(MouseEvent e) {
		
		if(e.getX()<getWidth()-PADDLE_WIDTH/2 && e.getX()>PADDLE_WIDTH/2) {
			paddle.setLocation(e.getX()-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		}
		
	}
	
	private void ballVelocity() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}
	
	private void moveBall() {
		ball.move(vx, vy);
		if ((ball.getX() - vx <= 0 && vx < 0)|| (ball.getX() + vx >= (getWidth() - BALL_RADIUS*2) && vx > 0)) {
			vx = -vx;
		}
		if((ball.getY() - vy <= 0 && vy < 0)) {
			vy = -vy;
		}
		
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 && ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 + 2) {
				vy = -vy;
			}
		}
		else if (collider != null) {
			 remove(collider);
			 remainingBrick--;
			 vy = -vy;
		}
		pause(DELAY);
		
	}
	private void winnerScreen() {

		
	}
}

	

	


