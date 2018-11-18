/*
 * File: Breakout.java
 * -------------------
 * Name: Cassidy McCleary
 * Section Leader: Meng Zhang
 * 
 * this program runs the game Breakout. the user bounces a ball with the paddle that they 
 * control at the bottom of the window.The object of the game is to get rid of all of the bricks
 * at the top of the screen, and the user has three turns to do so. In the event that the user does not 
 * get rid of all the bricks in three turns, because they miss the ball as it falls, the game is over. 
 * Precondition: a screen with 100 bricks of varying colors is centered partway down the window. A paddle 
 * is at the bottom of the screen, able to follow the mouse. a ball is in the center of the screen, as well.
 * Postcondition: the player has either won, which means a winning message is displayed in the center of the screen
 * and all of the bricks are gone, or the player has lost in which case the screen displays a losing message below the
 * bricks still remaining. 
 * 
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
	
	//Diameter of the ball in pixels
	public static final double BALL_DIAMETER = 20;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 7.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//instance variable for the paddle
	private GRect paddle = null;
	
	//instance variable for the ball
	private GOval ball = null;
	
	//instance variable for the brick
	private GRect brick = null;
	
	//instance variable for the velocity of the ball
	private double vx,vy;
	
	//instance variable for the number of bricks
	private int numberofbricks;
	
	//instance variable for the number of turns used
	private int turnsused;
	
	//allows the program to use a random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	

	public void run() {
		/*
		 * precondition: a blank canvas, before the bricks, paddle, and ball are added. 
		 * postcondition: the player has either won, which means a winning message is displayed in the center of the screen
		 * and all of the bricks are gone, or the player has lost in which case the screen displays a losing message below the
		 * bricks still remaining. 
		 */
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. 
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		//sets up the game, including the bricks and the paddle. 
		setUpGame();
		
		// sets up the number of turns.
		turnsused = 0;
		
		//while the player still has turns left, they play the game. 
		while (turnsused < NTURNS && numberofbricks != 0) {
			playGame();
			}
		// if the player wins displays a winning message. 
		if (numberofbricks == 0) {
			GLabel youWin = new GLabel ("You Win!");
			add (youWin,(getWidth()-youWin.getWidth())/2, (getHeight()-youWin.getAscent())/2);
		}
			
		//if the player loses, displays a message to that effect. 
		if (turnsused == NTURNS && numberofbricks != 0) {
			 GLabel gameOver = new GLabel ("Game Over--You Lose!" );
			 add(gameOver, (getWidth()-gameOver.getWidth())/2, (getHeight()-gameOver.getAscent())/2);
		}
	}
	
	
	private void setUpGame() {
		//This method sets up the game play before any interaction occurs. 
		//adds the bricks to the screen at the top. 
		makeBricks();
		//adds the paddle to the screen
		makePaddle();
		//enables the method that will allow the paddle to follow the mouse.
		addMouseListeners();
	}

	private void makePaddle() {
		// creates a filled GRect to serve as the paddle and centers it at the bottom of the screen.
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, 0, getHeight() - BRICK_Y_OFFSET);
		
	}

	private void makeBricks() {
		//creates the block of bricks that the ball will remove.
		for (int r = 0; r < NBRICK_ROWS; r++) {
			for (int c = 0; c < NBRICK_COLUMNS; c++) {
				double x = (getWidth() - (NBRICK_COLUMNS*(BRICK_WIDTH + BRICK_SEP) - BRICK_SEP))/2 + ((BRICK_WIDTH+BRICK_SEP)*c) ;
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT*r) + BRICK_SEP*r;
				brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				//adds the bricks to the screen. 
				add(brick);
				//sets the color of the bricks based on the row to create a rainbow effect. 
				brick.setFilled(true);
				if (r < 2) {
					brick.setColor(Color.RED);
				} else if (r < 4) {
					brick.setColor(Color.ORANGE);
				} else if ( r < 6) {
					brick.setColor(Color.YELLOW);
				} else if (r < 8) {
					brick.setColor(Color.GREEN);
				} else {
					brick.setColor(Color.CYAN);
				}
			}
		}
		//establishes a count for the number of bricks on the screen. 
		numberofbricks = NBRICK_ROWS * NBRICK_COLUMNS; 
	}
	
	public void mouseMoved (MouseEvent e) {
		//enables the paddle to follow the mouse by setting the mouse location as the paddle location,
		//as long as the mouse is on the screen.
		double x = e.getX();
		double y =  getHeight() - BRICK_Y_OFFSET;
		if (x < (getWidth()-(PADDLE_WIDTH))) {
		paddle.setLocation(x,y);
		}
	}
	
	private void playGame() {
		//this method makes up the body of the game play. It creates a filled ball. 
		ball = new GOval ((getWidth()-BALL_DIAMETER)/2, (getHeight()-BALL_DIAMETER)/2, BALL_DIAMETER, BALL_DIAMETER );
		ball.setFilled(true);
		//the ball is added to the screen.
		add(ball);
		//the animation loop of the ball game play starts after the user clicks. 
		waitForClick();
		//the ball speed and direction are set using predetermined constants with a randomly
		//generated x direction to heighten the game play. 
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		//while conditions for the game being over are not met, the ball bounces around the 
		//screen and removes bricks.
		while (!gameOver()) {
			bounceBall();
		}
		//at the end of a turn, the ball is removed from the screen. 
		remove(ball);
		//the amount of turns the user as played increases, once they terminate the animation loop.
		turnsused++;
		}
	
	private boolean gameOver() {
		//sets the conditions for the game to be terminated, which include the ball hitting the
		//bottom of the screen, or the user removing all of the bricks. 
		return  (ball.getY() >= getHeight()-BALL_DIAMETER) || (numberofbricks == 0); 
	}
	
	private boolean touchLeftWall(GOval ball) {
		//this boolean tests whether the ball has hit the left wall
		//by taking in the ball as input, checking its x location, and returns true if it has.
		return (ball.getX() <= 0); 
	}
	
	private boolean touchRightWall(GOval ball) {
		//this boolean tests whether the ball has hit the right wall
		//by taking in the ball as input, checking its x location, and returning true if it has. 
		return (ball.getX() >= getWidth()-BALL_DIAMETER);
	}
	
	private boolean touchTopWall(GOval ball) {
		//this boolean tests whether the ball has hit the top of the window,
		//by taking in the ball as input, checking its y location, and returning true if it has.
		return (ball.getY() <= 0);
	}
	
	private void bounceBall() {
		//this method checks if a ball has hit a surface and if it has, reverses the x or y 
		//velocity to create a "bounce", and then moves the ball. 
		//if the ball touches the left or right wall, the ball reverses its x velocity. 
		if (touchLeftWall(ball) || touchRightWall(ball)) {
			vx = -vx;
		}
		//if the ball touches the top, it reverses its y velocity. 
		if (touchTopWall(ball)) {
			vy= -vy;
		}
		//this method tests whether the ball hits an object rather than a wall,
		GObject collider = getCollidingObject();
		//if the ball has hit the paddle, the ball is reflected in the opposite direction. 
		if (collider == paddle) {
			vy = -Math.abs(vy);
		// if the ball hits something that is not the paddle, it has hit a brick, 
		//so it removes the brick and then bounces in the opposite direction. the brick count is then decreased. 
		} else if (collider != null) {
			remove(collider);
			vy = -vy;
			numberofbricks = numberofbricks - 1; 
		}
		//then the ball moves at the preset velocity.
		ball.move(vx, vy);
		//this pause enables the human eye to see the moving ball. 
		pause(DELAY);
	}
	
	private GObject getCollidingObject() {
		//this method enables the ball to sense if it has hit an object.
		//these variables represent the four corners of the ball.
		double leftX = ball.getX();
		double rightX = ball.getX() + BALL_DIAMETER;
		double topY = ball.getY();
		double bottomY = ball.getY() + BALL_DIAMETER;
		//this tests if there is an object at the first corner of the ball. 
		GObject object = getElementAt(leftX,topY);
		//if there is, it returns that object to the collider in the bounceBall method.
		if (getElementAt(leftX,topY) != null) {
			return object;
		//if there isn't, it tests the next corner. 
		} else {
			object = getElementAt(leftX,bottomY);
		}
		//the process repeats for all of the corners
		if (getElementAt(leftX,bottomY) != null) {
			return object;
		} else { 
			object = getElementAt(rightX,bottomY);
		}
		if (getElementAt(rightX,bottomY) != null) {
			return object;
		} else {
			object = getElementAt(rightX,topY);
		}
		if (getElementAt(rightX,topY) != null) {
			return object;
		} else {
			//if there is nothing, the method returns null, which means the ball continues to move uninterrupted. 
			return null;
		}
		}
}
