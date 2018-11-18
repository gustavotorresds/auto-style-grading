/*
 * File: Breakout.java
 * -------------------
 * Name: Amy Hoemeke
 * Section Leader:Vineet
 * 
 * This file implements the game breakout
 * The player wins the game by removing all the bricks on the screen
 * The player has a paddle that can be operated with the mouse pad, which they will use to bounce the ball
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
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
	public static final double BALL_WIDTH = BALL_RADIUS*2;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	private static int NTURNS = 3;


	//these are instance variables created to be used throughout the program
	private GRect paddle; 
	private GOval ball;
	private double vx,vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GObject collidingObject;
	private int bricksRemaining = (NBRICK_COLUMNS*NBRICK_ROWS);

	//the game is set up and played when there are still bricks and turns remaining
	public void run() {
		setUpBreakout(); 
		while(NTURNS != 0 && bricksRemaining !=0) {
			playBreakout();
		}
		endGame(); 
	}	

	//this combines the various set-up helper methods to build the game
	private void setUpBreakout() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBricks();
		addPaddle();
		addMouseListeners();
	}
	
	//this method detects whether the player won or lost the game
	//all of the objects of the game are removed and then display the corresponding message to the player
	private void endGame() {
		removeAll();
		if(bricksRemaining == 0) {
			GLabel winner = new GLabel ("CONGRATS!!! You Brokeout!");
			add(winner);
			winner.setLocation(getWidth()/2-winner.getWidth()/2, getHeight()/2);
		} else if (NTURNS == 0) {
			GLabel loser = new GLabel ("You did not win this time, but that's okay!!!");
			loser.setLocation(getWidth()/2-loser.getWidth()/2, getHeight()/2);
			add(loser);
		}
	}
	
	//this sets the bricks by alternating the color every 2 rows
	//the bricks are built from top to bottom, with the starting point being the top left corner of first red brick
	private void setBricks() {
		double x= getWidth()/2-(NBRICK_ROWS*BRICK_WIDTH)/2 - (NBRICK_ROWS-1)*BRICK_SEP/2;
		double y= BRICK_Y_OFFSET;
		for(int rows=0; rows<NBRICK_ROWS; rows++) {
			for(int columns=0; columns<NBRICK_COLUMNS; columns++) {
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				if (rows==0 || rows==1) {
					brick.setColor(Color.RED);
				}
				if (rows==2 || rows==3) {
					brick.setColor(Color.ORANGE);
				}
				if (rows==4 || rows==5) {
					brick.setColor(Color.YELLOW);
				}
				if (rows==6 || rows==7) {
					brick.setColor(Color.GREEN);
				}
				if (rows==8 || rows==9) {
					brick.setColor(Color.CYAN);
				}
				brick.setFilled(true);
				add(brick);
				x+=BRICK_SEP+BRICK_WIDTH;
			}
			y+=BRICK_HEIGHT+BRICK_SEP;
			x=getWidth()/2-(NBRICK_ROWS*BRICK_WIDTH)/2 - (NBRICK_ROWS-1)*BRICK_SEP/2;
		}
	}
	
	//this adds the paddle that will be used to play the game
	private void addPaddle() {
		paddle = new GRect (getWidth()/2-PADDLE_WIDTH/2,getHeight()-PADDLE_Y_OFFSET,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);	
	}
	
	//this will make paddle move along with the mouse, but only from left to right
	//the if statement stops the paddle from going off of the screen
	public void mouseMoved(MouseEvent e) {
		double paddleX = e.getX();
		double paddleY = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle.setLocation(paddleX,paddleY);
		if (paddleX+PADDLE_WIDTH > getWidth()) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, paddleY);
		}
	}
	
	//this adds the ball to the center of the window, where it will start for any turn
	private void addBall() {
		ball = new GOval (getWidth()/2-(2*BALL_RADIUS)/2,getHeight()/2-(2*BALL_RADIUS)/2,2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	//this method is what allows the game to be played
	//the add ball method remains outside of the loop so that it resets for each turn
	//it teaches the game what to do when it hits bricks, walls, and the paddle
	//the velocity increases as collisions happen
	private void playBreakout() {
		addBall();
		waitForClick();
		velocityOfBall();	
		while (NTURNS != 0 && bricksRemaining != 0) {	
			ball.move(vx,vy);
			checkForCollision();
			GObject collider = getCollidingObject();
			if (ball.getY() >= getHeight()) {
				NTURNS--;
				remove(ball);
				break;
			} 
			if(collider != paddle && collider != null) {
				remove (collider);
				vy= -vy;
				bricksRemaining --;
			}
			if (collider == paddle) {
				if (ball.getY() <= (getHeight()-PADDLE_Y_OFFSET-BALL_WIDTH) && vy > 0) {
					vy= -vy;
				}
			}
			pause(DELAY);
		}
	}

	//a helper method to initialize the velocities and the random generator for velocities
	private void velocityOfBall() {
		vx = rgen.nextDouble(2.0, 4.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = 4.0;
	}
	
	//this checks for collision so that the direction/velocity can change accordingly (by negation)
	private void checkForCollision() {
		if (ball.getX() < 0 || ball.getX() + BALL_WIDTH > getWidth()) {
			vx = -vx;
		}
		if (ball.getY() < 0) {
			vy = -vy;
		}
	}

	//this method checks all of the "corners" of the ball to check for collisions
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			collidingObject = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt (ball.getX()+BALL_WIDTH, ball.getY())!= null) {
			collidingObject = getElementAt(ball.getX() + BALL_WIDTH, ball.getY());
		} else if (getElementAt (ball.getX(), ball.getY()+BALL_WIDTH) != null) {
			collidingObject = getElementAt (ball.getX(), ball.getY()+BALL_WIDTH);
		} else if (getElementAt (ball.getX()+BALL_WIDTH, ball.getY()+BALL_WIDTH) != null) {
			collidingObject = getElementAt (ball.getX()+BALL_WIDTH, ball.getY()+BALL_WIDTH);
		} else {
			collidingObject = null;
		}
		return collidingObject;
	}
}



