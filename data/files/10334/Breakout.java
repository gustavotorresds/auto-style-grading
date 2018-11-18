/*
 * File: Breakout.java
 * -------------------
 * Name: Leah Slang
 * Section Leader: Cat Xu
 * 
 * This program plays the game of Breakout!
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.javafx.font.directwrite.RECT;

public class Breakout extends GraphicsProgram {
	
	// Tracks paddle location
	GRect paddle = null;
	
	// Tracks ball location
	GOval ball = null;
	
	// Tracks brick location
	GRect brick = null;
	
	// Track ball velocities
	private double vx, vy;

	// Generates random numbers
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
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
	public static final double PADDLE_WIDTH = 30;
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

	public void run() {
		setUp();
		play();
	}
	
	private void setUp(){
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// Establishes initial x and y coordinates of first brick row
		double y = BRICK_Y_OFFSET;
		double x = (CANVAS_WIDTH-((NBRICK_COLUMNS*BRICK_WIDTH)+((NBRICK_COLUMNS-1)*BRICK_SEP)))/2;
		// Establishes number of rows and columns to be used in the filling row for loops
		int r = NBRICK_ROWS;
		int c = NBRICK_COLUMNS;
		// Creating the brick set up
		for (int i=0; i<r;i++){
			// Loop establishes bottom to top filling of bricks
			for (int n=0; n<c;n++){
				// Loop establishes left to right filling of bricks
				brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				// Changes fill color for each two rows
				brick.setFilled(true);
				if (i==0 || i==1) {
					brick.setColor(Color.RED);
				}
				if (i==2 || i==3) {
					brick.setColor(Color.ORANGE);
				}
				if (i==4 || i==5) {
					brick.setColor(Color.YELLOW);
				}
				if (i==6 || i==7) {
					brick.setColor(Color.GREEN);
				}
				if (i==8 || i==9) {
					brick.setColor(Color.CYAN);
				}
				// Moves to location of next brick in same row
				x=x+BRICK_WIDTH+BRICK_SEP;
			}
			// Moves to location of first brick in new brick row
			y = y+BRICK_HEIGHT+BRICK_SEP;
			x = (getWidth()-((NBRICK_COLUMNS*BRICK_WIDTH)+((NBRICK_COLUMNS-1)*BRICK_SEP)))/2;
		}
		// Generate paddle
		paddle=makePaddle();
		// Place paddle
		addPaddleToBottomCenter();
		// Asks the program to listen for mouse movements
		addMouseListeners();
	}
	
	private void addPaddleToBottomCenter(){
		// Places the paddle at the bottom center of the screen
		double x = (getWidth()-PADDLE_WIDTH)/2;
		double y = (getHeight()-PADDLE_Y_OFFSET);
		add(paddle, x, y);
	}
	private GRect makePaddle(){
		// Create paddle
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT); 
		paddle.setFilled(true);
		return paddle;
	}
	
	public void mouseMoved(MouseEvent e){
		// Allows the paddle to track the mouse location
		double mousex=e.getX();
		if (mousex <= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(mousex, getHeight()-PADDLE_Y_OFFSET);
		}
	}
	
	private void play(){
		// The game starts with the hasWon condition false so that the end of the 
		// game can be recognized when the boolean changes to true
		boolean hasWon = false;
		// Count checks the number of bricks that have been removed from the screen 
		// to allow the program to sense when the end condition has been met
		int count = 0;
		// Loop is run until all turns have been used or the game has been won
		for (int n=0;n<NTURNS;n++){
			// Generates initial speed of ball
			vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
			vy = VELOCITY_Y;
			// Randomly selects starting x direction of ball
			if (rgen.nextBoolean(0.5)) vx = -vx;
			// If the game has not been won, a new ball is created at the start of each turn
			if (hasWon==false){
				makeBall();	
			}
			// Checks if the ball is in play
			boolean hitBottom = false;
			// Moves the ball so long as it has not hit the bottom and the game hasn't been won
			while(!hitBottom && count<(NBRICK_COLUMNS*NBRICK_ROWS)){
				// Moves the ball
				ball.move(vx, vy);
				// Checks for collisions
				getCollidingObject();
				// Creates GObject if getCollidingObject does not return null
				GObject collider = getCollidingObject();
				// If the ball hits the paddle, it reverses y direction, bouncing off
				if (collider == paddle) {
					vy= -vy;
				} 
				else {
					// If the ball hits something else, it is a brick
					if (collider != null) { 
						// Removes the brick from the screen
						remove(collider);
						// Creates a bounce
						vy= -vy;
						// Adds another removed brick to the counter
						count++;
						// Checks if game is won by comparing counter to number of bricks to clear
						// If condition is met, game ends and displays end message
						if (count==NBRICK_COLUMNS*NBRICK_ROWS){
							hasWon=true;
							youWon();
						}
					}	
				}
				// Allows ball to bounce off left and right walls
				if (ball.getX()<=0 || ball.getX()>=getWidth()-2*BALL_RADIUS){
					vx = -vx;
				}
				// Allows ball to bounce off top wall
				if (ball.getY()<=0){
					vy = -vy;
				}
				// Recognizes if ball has gone past the paddle and hit the back wall, ends turn
				if (ball.getY()>=getHeight()-2*BALL_RADIUS){
					hitBottom=true;
					remove(ball);
				}
				// Pause to allow animation to be visible
				pause(DELAY);
			}
		}
		// Displays end message if game is lost
		if (count<NBRICK_COLUMNS*NBRICK_ROWS){
			youLost();
		}
	}
	
	private GOval makeBall(){
		// Places black ball in center
		double ballCenterX = (getWidth()-(2*BALL_RADIUS))/2;
		double ballCenterY = (getHeight()-(2*BALL_RADIUS))/2;
		ball = new GOval(ballCenterX,ballCenterY,2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		return ball;
	}
	
	private GObject getCollidingObject(){
		// Get colliding object allows the ball to sense if it is touching any object at the four "corners" of the ball
		// Looks at current x and y coordinates
		double x = ball.getX();
		double y = ball.getY();
		// Returns if there is an object at the top left corner of the ball
		if (getElementAt(x,y)!=null){
			GObject collider=(getElementAt(x,y));
			return(collider);
		}
		// Returns if there is an object at the top right corner of the ball
		if (getElementAt(x+2*BALL_RADIUS,y)!=null){
			GObject collider=(getElementAt(x+2*BALL_RADIUS,y));
			return(collider);
		}
		// Returns if there is an object at the bottom left corner of the ball
		if (getElementAt(x,y+2*BALL_RADIUS)!=null){
			GObject collider=(getElementAt(x, y+2*BALL_RADIUS));
			return(collider);
		}
		// Returns if there is an object at the bottom right of the ball
		if (getElementAt(x+2*BALL_RADIUS,y+2*BALL_RADIUS)!=null){
			GObject collider=(getElementAt(x+2*BALL_RADIUS,y+2*BALL_RADIUS));
			return(collider);
		}
		// Returns if there is no object present
		else {
			return(null);
		}
	}	
	
	private void youWon(){
		// Displays end message
		GLabel label = new GLabel("You won!");
		label.setFont("Courier-24");
		add(label, (CANVAS_WIDTH-label.getWidth())/2, (BRICK_Y_OFFSET-label.getAscent())/2);
	}
	
	private void youLost(){
		// Displays end message
		GLabel label = new GLabel ("You lost!");
		label.setFont("Courier-24");
		add(label, (CANVAS_WIDTH-label.getWidth())/2, (BRICK_Y_OFFSET-label.getAscent())/2);
	}
}

