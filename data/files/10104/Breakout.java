/*
 * File: Breakout.java

 * -------------------
 * Name: Katina Mattingly
 * Section Leader: Thariq Ridha
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
	
	private int numBricks = NBRICK_COLUMNS*NBRICK_ROWS;

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
	private int count = 0; 
	
	// Instance variable for paddle so can use with MouseListeners
	private GRect paddle;
	private double mouseX;
	private GOval ball;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy = 3.0;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		addMouseListeners();

		createBricks();
		createPaddle();	
		createBall(); 
		
		while (true) {
			movePaddle();
			ballLauncher();
			checkForCollisions();
		}
	}
	
	// Calls method for if the ball hits the wall, a brick, or the paddle
	private void checkForCollisions() {
		hitWall();
		hitBrickOrPaddle();
	}

	private void hitWall() {
		// Ball hits left wall
		if (ball.getX() <= 0) { 
			vx = -vx;
		}
		 //Ball hits right wall
		if (ball.getX() >= getWidth()-2.0*BALL_RADIUS) {
			vx = -vx;
		}
		// Ball hits ceiling 
		if (ball.getY() <= 0) { 
			vy = -vy; 
		}
		//Ball hits floor
		if (ball.getY() >= getHeight()-2.0*BALL_RADIUS) { 
			count++;
			loseTurn(count);
			remove(ball);
			createBall();
		}
	}
	
	// Displays "YOU LOST :(" once 3 turns are lost
	private void loseTurn(int c) {
		while (c == NTURNS) {
			GLabel banner = new GLabel ("YOU LOST :(",getWidth()/2.0,getHeight()/2.0);
			add (banner);
		}
	}
	
	// Removes brick and changes velocity direction when hit by ball
	// Ball changes velocity direction if hit by paddle 
	private void hitBrickOrPaddle() {
		GObject brickBottom = getElementAt(ball.getX()+BALL_RADIUS,ball.getY());
		GObject brickTop = getElementAt(ball.getX()+BALL_RADIUS,ball.getY()+2.0*BALL_RADIUS);
		GObject brickLeft = getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()+BALL_RADIUS);
		GObject brickRight = getElementAt(ball.getX(),ball.getY()+BALL_RADIUS);

		if (numBricks != 0) {
			if (brickBottom != null && brickBottom != paddle) {
				vy = - vy; 
				remove(brickBottom);
				numBricks--;
			}
			if (brickTop != null && brickTop != paddle) {
				vy = -vy;
				remove(brickTop);
				numBricks--;
			}
			if (brickTop!= null && brickTop == paddle) {
				vy = -vy;
			}
			if (brickBottom != null && brickBottom == paddle) {
				vy = - vy; 
			}
		}
		while (numBricks == 0) {
			GLabel banner = new GLabel ("Congrats you won!",getWidth()/2.0,getHeight()/2.0);
			add (banner);
		}
	}
	
	// Creates ball and sets initial random x velocity
	private void createBall() {  
		ball = new GOval (getWidth()/2.0-BALL_RADIUS, getHeight()/2.0-BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add (ball);
		vx = rgen.nextDouble(1.0,3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	} 

	// Moves ball
	private void ballLauncher() { 
		ball.move(vx,vy);
		pause(DELAY);
	} 

	// Creates paddle and adds to center of screen
	private void createPaddle() { 
		double x = getWidth()/2.0 - PADDLE_WIDTH/2.0;
		double y = getHeight() - (PADDLE_Y_OFFSET + PADDLE_HEIGHT);
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		((GRect) paddle).setFilled(true);
		add (paddle);
	}
	
	// Makes paddle follow x movements of mouse
	private void movePaddle() { 
		paddle.setX(mouseX); 
	}
	
	// Sets value of mouseX to X value of mouse while within console
	public void mouseMoved(MouseEvent e) { 
		if (e.getX() <= getWidth()-PADDLE_WIDTH) {
			mouseX = e.getX();	
		}
	}

	//Creates bricks of game with bricks centered in middle of screen
	private void createBricks(){
		
		//adds bricks to next column 
		for (int i=0; i<10; i++) { 
			
			double x = getWidth()/2.0 - NBRICK_COLUMNS/2.0*BRICK_WIDTH - NBRICK_COLUMNS/2*BRICK_SEP+BRICK_SEP/2.0;
			double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP)*i;
			 //adds bricks along row
			for (int j=0; j<10; j++) {
				GRect r = new GRect(x+(BRICK_SEP + BRICK_WIDTH)*j, y, BRICK_WIDTH,BRICK_HEIGHT);
				if (i<2) {
					r.setColor(Color.RED);
				}
				if (i>=2 && i<4) {
					r.setColor(Color.ORANGE);
				}
				if (i>=4 && i<6) {
					r.setColor(Color.YELLOW);
				}
				if (i>=6 && i<8) {
					r.setColor(Color.GREEN);
				}
				if (i>=8) {
					r.setColor(Color.CYAN);
				}
				r.setFilled(true);
				add (r);	
			} 	
		}
	}

}
