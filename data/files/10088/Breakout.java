/*
 * File: Breakout.java
 * -------------------
 * Name: Senem Onen
 * Section Leader: Thariq
 * 
 * This file implement the game of Breakout. See the comment above run method for details.
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
	public static final int NBRICK_COLUMNS = 2;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 2; 

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
	public static final double VELOCITY_Y = 4.0; 

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 3.0; 
	public static final double VELOCITY_X_MAX = 6.0; 

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3; 

	// Total Row Width
	public static final double TOTAL_ROW_WIDTH = BRICK_WIDTH * NBRICK_COLUMNS + BRICK_SEP * (NBRICK_COLUMNS-1);


	/* The user clicks to start playing the game. As long as there are bricks remaining and the user is still in the
	 * limits of turns s/he has, the game continues. Each time the ball hits the bottom wall, user has one fewer turns and 
	 * clicks to continue playing the game. Once user has no remaining bricks left or the ball hits the bottom wall
	 * in the last turn user has, the game terminates.
	 */
	
	public void run() {

		// Setup the Game - Basics
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setTitle("CS 106A Breakout");
		setUpGame();
		createPaddle();
		addMouseListeners();
		createBall();

		// Set the Initial Velocity
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx=-vx;

		// Initialize Number of Remaining Bricks
		int remainingBricks = NBRICK_ROWS * NBRICK_COLUMNS;
		int turnsRemaining = NTURNS - 1;

		//Play the Game
		waitForClick();	
		while(true) {
			while(turnsRemaining >= 0 && remainingBricks >= 0){
				
				if (hitBottomWall() && turnsRemaining > 0) { 
					remove(ball);
					createBall(); // to restart the game
					vy = VELOCITY_Y;
					vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
					if (rgen.nextBoolean(0.5)) vx = -vx;
					ball.move(vx, vy);
					turnsRemaining --; 
					waitForClick();
				}

				if( hitTopWall()) { // if the ball hits the top wall, y direction needs to be reversed.
					vy = -vy;	
				}
				if( hitLeftWall() || hitRightWall()) { // if the ball hits left or right wall, x direction needs to be reversed.
					vx = -vx;	
				}

				// Checking for Collusions
				GObject collider = getCollidingObject();
				if(collider != null) {
					if (collider == paddle) { 
						vy = (-1) * (Math.abs(vy)); // when the ball collides with the paddle, it ALWAYS moves in the (-) y direction
					}
					if (collider != paddle) { 
						vy = -vy;
						remove (collider); 
						remainingBricks --; 
					}
				}

				// Terminating Conditions for the Game: makes the Ball stop and display appropriate message
				if ((hitBottomWall() && turnsRemaining == 0) || remainingBricks == 0) {
					// make the ball stop moving
					vx = 0;
					vy = 0;
				}
				// Add appropriate Final Message
				if (remainingBricks == 0) { 
					finalMessage ("CONGRATZ- You WON!");
					add(finalMessage);
				}
				if (hitBottomWall() && turnsRemaining == 0) {
					finalMessage("YOU LOST");
					add(finalMessage);
				}	
				
				// if there are no colliders, no need to update vx or vy for ball to move.
				ball.move(vx,vy);
				pause(DELAY);
			}

		}
	}
	
	// Getting the Object that collides with the Ball (if any)
	private GObject getCollidingObject() {
		GObject collider_object= objectAtTopRight();
		if (collider_object != null) {
			return collider_object;
		}
		collider_object=objectAtTopLeft();
		if (collider_object != null) {
			return collider_object;
		}
		collider_object=objectAtBottomLeft();
		if (collider_object != null) {
			return collider_object;
		}
		collider_object=objectAtBottomRight();
		if (collider_object != null) {
			return collider_object;
		}
		return null;
	}

	// Checking the objects at each 4 corner of the ball
	private GObject objectAtTopRight() {
		GObject object= getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		return object;
	}
	private GObject objectAtBottomRight() {
		GObject object = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		return object; 
	}
	private GObject objectAtTopLeft() {
		GObject object= getElementAt(ball.getX(), ball.getY());
		return object;
	}
	private GObject objectAtBottomLeft() {
		GObject collider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		return collider; 
	}

	// Final Message: to be displayed at the end of the game
	private GLabel finalMessage(String str) {
		finalMessage = new GLabel (str);
		finalMessage.setFont("Courier-24");
		finalMessage.setColor(Color.RED);
		finalMessage.setLocation((getWidth()-finalMessage.getWidth())/2, (getHeight()-finalMessage.getHeight())/2);
		return finalMessage;
	}

	// Create the Ball
	private void createBall() {
		double size = BALL_RADIUS * 2;
		double x= getWidth()/2 - BALL_RADIUS;
		double y= getHeight()/2 - BALL_RADIUS;
		ball = new GOval (size, size);
		ball.setFilled(true);
		ball.setColor(Color.blue);
		add(ball, x,y);
	}

	// Ball Checkers for Hitting the Walls
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}
	private boolean hitBottomWall() {
		return (ball.getY() + 2 * BALL_RADIUS) >= getHeight();
	}
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}
	private boolean hitRightWall() {
		return (ball.getX() + 2 * BALL_RADIUS) >= getWidth();
	}

	// Create the paddle and place it at the middle of the screen
	private void createPaddle() {
		double x = (getWidth()-PADDLE_WIDTH)/2;
		double y= getHeight()-PADDLE_Y_OFFSET;
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.black);
		add(paddle,x,y);
	}

	// Mouse Event- Moving the Paddle within the Window Boundary Limits
	public void mouseMoved(MouseEvent e) {
		double mouse_x = e.getX();
		double x = mouse_x;
		double y= getHeight()- PADDLE_Y_OFFSET;
		if (mouse_x < (getWidth()-TOTAL_ROW_WIDTH)/2 ) { //adjust for left side of the screen
			x = (getWidth()-TOTAL_ROW_WIDTH)/2;
		} else if(mouse_x > (getWidth()+TOTAL_ROW_WIDTH)/2-paddle.getWidth()) { // adjust for right side of the screen
			x = (getWidth()+TOTAL_ROW_WIDTH)/2-paddle.getWidth();
		} 
		paddle.setLocation(x,y);
	}

	// Set up the Game: Fills the rows with bricks with the appropriate color (depending on the row number)
	private void setUpGame() {
		for (int j=0; j<NBRICK_ROWS; j++) {
			int mod_j= j % 10; // to generalize the color  of the bricks
			if (mod_j==0 || mod_j==1) {
				fillRow(Color.red, j);
			} else if (mod_j==2 || mod_j==3) {
				fillRow(Color.orange, j);
			} else if (mod_j==4 || mod_j==5) {
				fillRow(Color.yellow, j);
			} else if (mod_j==6 || mod_j==7) {
				fillRow(Color.green, j);
			} else if (mod_j==8 || mod_j==9) {
				fillRow(Color.cyan,j);
			}
		}
	}

	// FillRow method fills the row with appropriate number of bricks with specified colors depending on the row number.
	private void fillRow(Color color, int k) {
		for (int i=0; i<NBRICK_COLUMNS; i++) {
			double x= (getWidth()-TOTAL_ROW_WIDTH)/2 + i *(BRICK_WIDTH + BRICK_SEP) ;
			double y= BRICK_Y_OFFSET + k * (BRICK_HEIGHT + BRICK_SEP);
			GRect brick = new GRect (x, y, BRICK_WIDTH,BRICK_HEIGHT); 
			brick.setFilled(true);
			brick.setFillColor(color);
			add(brick);
		}
	}

	// Paddle
	private GRect paddle;	

	// Ball
	private GOval ball;

	// Velocity of the Ball
	private double vx;
	private double vy;

	// Final Label to be Displayed after Game Ends
	private GLabel finalMessage =new GLabel ("");

	// Random Number Generator
	private RandomGenerator rgen= RandomGenerator.getInstance();
}


