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

	//Number of blocks
	public static final int NUMBER_OF_BLOCKS = NBRICK_ROWS*NBRICK_COLUMNS;

	// Number of turns 
	public static final int NTURNS = 3;

	//creates an instance variable GRECT which functions as the paddle for Breakout 
	private GRect paddle = new GRect(0, (getHeight() - PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT);
	
	//creates an instance variable GOval which functions as the ball for Breakout
	private GOval ball = new GOval (0,0, 2*BALL_RADIUS, 2*BALL_RADIUS);

	//creates an instance variable for the velocity of the ball (GOval)
	private double vx, vy;

	//creates an instance variable that creates random numbers
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//creates an audio clip to play when the ball bounces off of the walls or the paddle
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");


	//This program plays the game of Breakout
	public void run() {
		addMouseListeners();
		setUp(); 
		clickToStart();
		play();
	}

	//This method is used to display GLabels "YOU WIN" if the player wins the game
	//and "GAME OVER" if the player looses the game. 
	private void gameOver(boolean win) {
		GLabel label = new GLabel("");
		//set font and color //
		label.setFont("Courier-60");
		label.setColor(Color.BLUE);
		if (win) {
			label.setLabel("YOU WIN");
		} else {
			label.setLabel("GAME OVER");
		}
		label.setLocation(getWidth()/2-label.getWidth()/2, getHeight()/2);
		add(label);
	}

	//determines if the ball hits the block with any of its four corners 
	//and if it does it returns the brick it hit, if not, it returns nothing.
	private GObject getCollidingObj(GOval ball) {
		double x = ball.getX();
		double y = ball.getY();
		//The cascading if statements determine if the ball has hit one of the bricks 
		//with one of its four corners of the GObject box it is in. 
		if (getElementAt(x, y) != null) {
			GObject obj = getElementAt(x, y);
			return(obj);
		} else if (getElementAt(x + 2*BALL_RADIUS, y) != null) {
			GObject obj = getElementAt(x+ 2*BALL_RADIUS, y);
			return(obj);
		} else if (getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS) != null) {
			GObject obj = getElementAt(x + 2*BALL_RADIUS, y + 2*BALL_RADIUS);
			return(obj);
		} else if (getElementAt(x, y + 2*BALL_RADIUS) != null) {
			GObject obj = getElementAt(x, y+ 2*BALL_RADIUS);
			return(obj);
		}
		return(null); 
	}


	//Plays the game of Breakout by moving a black ball around the display 
	//and bouncing it off the walls. However, if the ball hits the bottom wall, 
	//you loose one of your "lives" (or NTURNS). If the ball hits a brick 
	//it removes the brick from the display. If you hit all the bricks before you 
	//use up all of your lives you win the game. 
	private void play() {
		GObject collider = null;
		GOval ball = makeBall();
		//Sets the y velocity of the ball 
		vy = VELOCITY_Y;
		//Sets the x velocity of the ball 
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		//count counts the number of blocks the ball hits
		int count = NUMBER_OF_BLOCKS;
		//ballCount counts the number of turns the player has used up
		int ballCount = NTURNS;
		while(true) {
			ball.move(vx,vy);
			collider = getCollidingObj(ball);
			//If the ball collides with the paddle it changes direction
			if (collider == paddle) {
				vy = -vy;
				bounceClip.play();
				//If the ball collides with a brick, then it removes the brick 
				//and then adds it to the count of bricks it has broken. 
				//Once the ball hits all the bricks determined by NUMBER_OF_BLOCKS
				//then the game is over and the player wins
			} else if (collider != null) {
				bounceClip.play();
				remove(collider);
				count--;
				if (count == 0) {
					gameOver(true);
					break;
				}
				vy = -vy;
				collider = null;
			} else {
				//determines if the ball has hit right or left wall and reverses its velocity
				if (atLeftWall(ball, vx) || atRightWall(ball, vx)) {
					vx = -vx;
				}
				//determines if the ball has hit top wall and reverses its velocity
				else if(atTopWall(ball,vy)) {
					vy = -vy;
				}
				//determines if the ball has hit bottom wall and creates a new turn
				//if all turns are expired it breaks out of the game
				//and displays the message: "GAME OVER"
				else if (atBottomWall(ball, vy)) {
					remove(ball);
					ballCount--;
					livesCountDisplay(ballCount);
					if (ballCount > 0) {
						ball = makeBall();
					} else {
						gameOver(false);
						break;
					}
				}
			}
			pause(DELAY);
		}
	}

	//This method displays the number of lives the user has left 
	//after the ball hits the bottom of the window.
	private void livesCountDisplay(int ballCount) {
		GLabel label = new GLabel("");
		label.setLabel("YOU HAVE " + ballCount + " LIVES LEFT");
		label.setFont("Courier-25");
		label.setColor(Color.BLUE);
		label.setLocation(getWidth()/2 -label.getWidth()/2, getHeight()/2);
		add(label);
		pause(2000);
		remove(label);
	}

	//used code from animation lecture: bouncing ball 
	//checks if the ball is at the Left Wall
	private boolean atLeftWall(GOval ball, double vx) {
		if(vx > 0) {
			return false;
		}
		return ball.getX() <= 0;
	}


	//used code from animation lecture: bouncing ball 
	//checks if the ball is at the RIGHT Wall
	private boolean atRightWall(GOval ball, double vx) {
		if(vx < 0) {
			return false;
		}
		return ball.getX() >= getWidth() - ball.getWidth();
	}


	//used code from animation lecture: bouncing ball 
	//checks if the ball is at the Top Wall
	private boolean atTopWall(GOval ball, double vy) {
		if(vy > 0) {
			return false;
		}
		return ball.getY() <= 0;
	}


	//used code from animation lecture: bouncing ball 
	//checks if the ball is at the bottom Wall
	private boolean atBottomWall(GOval ball, double vy) {
		if(vy < 0) {
			return false;
		}
		return ball.getY() > getHeight() - ball.getHeight();
	}

	//makes a GOval called ball with radius BALL_RADIUS
	public GOval makeBall() {
		double x = getWidth()/2.0 - BALL_RADIUS;
		double y = getHeight()/2.0 - BALL_RADIUS;
		GOval ball = new GOval (x,y,2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		waitForClick();
		add(ball);
		return (ball);
	}


	//This method creates a row of equal rectangles with the width: BRICK_WIDTH and the height BRICK_HEIGHT.
	//The rectangles have a separation of BRICK_SEP between each neighboring rectangle.  
	//The row is centered at the center of the screen.
	private void makeRow(int row) {
		for (int brick = 0; brick<NBRICK_COLUMNS ; brick++) {
			double x = ((getWidth()/2.0) - ((NBRICK_COLUMNS/2.0)*(BRICK_WIDTH+BRICK_SEP)) +(BRICK_SEP/2.0) + brick*(BRICK_WIDTH+BRICK_SEP));
			double y = (BRICK_Y_OFFSET + row*(BRICK_SEP+BRICK_HEIGHT));
			GRect rect = new GRect (x,y, BRICK_WIDTH, BRICK_HEIGHT);
			rect.setFilled(true);
			add(rect);
			if (row == 0 || row == 1) {
				rect.setColor(Color.RED);
			} else if (row == 2 || row == 3) {
				rect.setColor(Color.ORANGE);
			} else if (row == 4 || row == 5){
				rect.setColor(Color.YELLOW);
			} else if (row == 6 || row == 7) {
				rect.setColor(Color.GREEN);
			} else if (row == 8 || row == 9){
				rect.setColor(Color.CYAN);
			}
		}
	}

	//This method moves the paddle when the user moves their mouse
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double y = (getHeight() - PADDLE_Y_OFFSET);
		double x = mouseX;
		add(paddle);
		if (x <= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(x, y);	
		}
	}

	//This method creates a GLabel that tells the user that to begin 
	//the game Breakout, they must click their keyboard to start.
	//This message stays on the screen for 3 seconds, then it is erased.
	private void clickToStart() {
		GLabel label = new GLabel("");
		//set font and color //
		label.setLabel("CLICK TO START");
		label.setFont("Courier-25");
		label.setColor(Color.BLUE);
		label.setLocation(getWidth()/2 -label.getWidth()/2, getHeight()/2);
		add(label);
		pause(3000);
		remove(label);
	}
	

	//This method sets up the game with all the bricks
	private void setUp() {
		// Set the window's title bar text //
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// makes 10 rows and 10 columns of multicolored blocks //
		for (int row = 0; row<NBRICK_ROWS; row++) {
			makeRow(row);
		}
	}



}
