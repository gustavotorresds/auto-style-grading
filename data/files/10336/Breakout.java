/*
 * File: Breakout.java
 * -------------------
 * Name:Jack Ramalia
 * Section Leader: Jordan Rosen-Kaplan
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
	public static final double DELAY = 750.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int lives = NTURNS;
	private GOval ball;
	private int totalBricks = NBRICK_COLUMNS*NBRICK_ROWS;
	private int bricksLeft = totalBricks; 
	private double centerX = getWidth()/2;
	private GRect paddle;
	private GLabel welcome;
	private GRect brick;


	public void run() {
		setUpGame();
		ballOut();
	}
	/* This method builds bricks, paddle, opening graphic*/
	private void setUpGame() {
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		makeBricks(centerX, BRICK_Y_OFFSET);
		makePaddle();
		openingScreen();
	}
	/* This method prints "Welcome To BREAKOUT" in the beginning then is removed when game starts */
	private void openingScreen() {
		welcome = new GLabel("Welcome To BREAKOUT", CANVAS_WIDTH/14, CANVAS_HEIGHT/20);
		welcome.setFont("SansSerif-bold-30");
		welcome.setColor(Color.RED);
		add(welcome);	
	}
	/* This method builds the bricks in game according to constant amounts and alternates color of bricks every 2 rows for the first 10 rows*/
	private void makeBricks(double centerX, double yStart) {
		for(int row = 0; row < NBRICK_ROWS; row++) {
			for(int brickNum = 0; brickNum < NBRICK_COLUMNS; brickNum++) {
				double x = getWidth()/2 -(NBRICK_COLUMNS*BRICK_WIDTH)/2-(NBRICK_COLUMNS-1*BRICK_SEP)/2+(brickNum*BRICK_WIDTH+(brickNum*BRICK_SEP))-BRICK_WIDTH*.5+BRICK_SEP;
				double y = yStart+(BRICK_HEIGHT*row)+(row*BRICK_SEP);
				brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(row == 0 || row == 1) {
					brick.setColor(Color.red);
				}
				if(row == 2 || row == 3) {
					brick.setColor(Color.orange);
				}
				if(row == 4 || row == 5) {
					brick.setColor(Color.yellow);
				}
				if(row == 6 || row == 7) {
					brick.setColor(Color.green);
				}
				if(row == 8 || row == 9) {
					brick.setColor(Color.cyan);
				}if (row >= 10) {
					brick.setColor(Color.gray);
				}
				add(brick);
			}
		}
	}
	/*This method makes the paddle and adds it to the center of the screen*/
	private void makePaddle() {
		double sx = centerX-PADDLE_WIDTH/2;
		double sy = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle = new GRect(sx,sy, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	/*Allows the paddle location to be set to the x coord of the mouse when it's moved*/
	public void mouseMoved(MouseEvent e) { 
		double x = e.getX()-PADDLE_WIDTH/2;
		double y = getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET; 		
		if(e.getX()>PADDLE_WIDTH/2 && e.getX()<getWidth()-PADDLE_WIDTH/2)
			paddle.setLocation(x, y);
	}
	/*This method start the game and result is printed at end*/
	private void ballOut() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		makeBall();
		waitForClick();
		remove(welcome);
		while(lives > 0 && bricksLeft>0) {  //enters while loop if user still has turns and they haven't destroyed all the bricks yet
			moveBall();		
			getCollidingObject();
			wallCheck();
			pause(DELAY);
			if(lives == 0) { //user lost game
				userLost();
			}else if(bricksLeft == 0) {//user won game
				winnerWinner();
			}
		}
	}
	/*This method prints "YOU WON" if user destroys all bricks*/
	private void winnerWinner() {
		GLabel label  = new GLabel("YOU WON!");
		label.setFont("SansSerif-bold-25");	
		label.setLocation(getWidth()/2-label.getWidth()/2, 20);
		add(label);		
	}
	/*This method prints "GAME OVER" if user has zero turns left*/
	private void userLost() {
		GLabel label  = new GLabel("GAME OVER");
		label.setFont("SansSerif-bold-25");	
		label.setLocation(getWidth()/2-label.getWidth()/2, 20);
		add(label);
	}
	/* This method makes ball and adds it to the center of the screen*/
	private void makeBall() {
		ball = new GOval(getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}
	/*This method moves ball according variable value*/
	private void moveBall() {
		ball.move(vx, vy);	
	}
	/*This method allows the ball ball to bounce around walls.
	 * If a left of right wall is hit, the velocity of the X val will change signs
	 * If the top wall is hit, the velocity of Y val will change signs
	 * If the bottom wall is hit, ball is removed and one life is lost
	 */
	private void wallCheck() {
		if(ball.getX()<=0) { //left wall
			vx = -vx;
		}
		else if(ball.getX()>=getWidth()-2*BALL_RADIUS) { //right wall
			vx = -vx;
		}
		else if(ball.getY()>=getHeight()-BALL_RADIUS*2) { //bottom wall
			remove(ball);
			lives--;	
			ballOut();
		}
		else if(ball.getY()<=0) { //top wall
			vy = -vy;
		}
	}
	/*This method creates a GObject and checks if it is hitting a brick or the paddle
	 * If it's not null, then the GObject is returned at that location
	 */
	private GObject checkCorners() {
		GObject corner;
		corner = getElementAt(ball.getX(), ball.getY()); 
		if( corner!= null ) {
			return(corner);
		}corner = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		if( corner != null) {
			return(corner);
		}corner = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		if(corner != null) {
			return(corner);
		}corner = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS); 
		if(corner!= null) {
			return(corner);
		} else {
			return null;
		}
	}
	/*Now that the ball has hit something, this method determines whether it was a brick or the paddle
	 * If paddle, Y direction is reversed
	 * If brick, Y direction is reversed, brick is removed from screen, and one brick is removed from bricks left
	 */
	private GObject getCollidingObject() {
		GObject collider = checkCorners();
		if(collider == paddle) {
			vy = Math.abs(vy)*-1; //fixes "sticky paddle" bug
		}else if(collider != null) {
			remove(collider);
			vy = -vy;
			bricksLeft--;
		}
		return collider;
	}
}





