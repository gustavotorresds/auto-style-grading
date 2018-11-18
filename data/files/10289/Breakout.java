/*
 * File: Breakout.java
 * -------------------
 * Name: Naya Yassin
 * Section Leader: Jonathan D. Kula
 * 
 * This program builds the game Breakout. 
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
	public static final double DELAY = 1000.0 / 100.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	//An instance variable for the paddle
	private GRect paddle;
	
	//An instance variable for the ball
	private GOval ball;
	
	//An instance variable for the colors
	private Color color;
	
	//An instance variable for counting the bricks
	private static int count;
	
	//Instance variables for the velocities of the ball
	private double vx, vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	private GLabel label;

	//This run method runs the entire code of breakout startGame().
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		addMouseListeners();
		// Calling this method starts the game by setting it up and then allowing the user to play with it.
		startGame();
	}
	
	//THis method sets up the entire game and rules, checks for collisons with the paddle or a brick, and adds messgaes to the 
	// canvas according to what fits the count of lives/bricks.
	private void startGame() {
		int i = NTURNS;
		while(i>0) {
			count = 0;
			setUp(i);
			while(true) {
				ball.move(vx, vy);
				pause(DELAY);
				if(hitRightWall() || hitLeftWall()) {
					vx = -vx;
				}
				else if(hitUpperWall()) {
					vy = -vy;
				}
				else if(hitBottomWall()) {
					vx = 0;
					vy = 0;
					i--;
					clear();
					break;
				}
				checkForCollisions();
				checkForWin();
			}
		}
		if (i == 0) {
			createLabel("YOU SUCK A--!"); 
		}
	}

	// A method that sets up the bricks and the rest of the game, and gets the ball to move after it receives a click from the user.
	private void setUp(int i) {
		buildBricks();
		buildPaddle();
		addMessage(i);
		buildBall();
		waitForClick();
		removeMessage();
		moveBall();
	}
	
	//This method removes the label in the middle of the canvas 
	private void removeMessage() {
		remove(label);
		
	}

	//This method adds an appropriate method in response to the bumber of lives [i] the player has left.
	private void addMessage(int i) {
		if(i == 3) {
			createLabel("Click to start");
		}
		if(i == 2) {
			createLabel("You have two lives left!");
		}
		if(i == 1) {
			createLabel("Omg you're losing, you have one life left!");
		}
	}
	
	//This method checks if the ball's hit the upper wall and returns yes if it did, otherwise it returns false.
	private boolean hitUpperWall() {
		return ball.getY() < 0;
	}

	//This method checks if the ball's hit the right wall and returns yes if it did, otherwise it returns false.
	private boolean hitRightWall() {
		return ball.getX()+BALL_RADIUS*2 > getWidth();
	}
	
	//This method checks if the ball's hit the left wall and returns yes if it did, otherwise it returns false.
	private boolean hitLeftWall() {
		return ball.getX() < 0;
	}

	//This method checks if the ball's hit the bottom wall and returns yes if it did, otherwise it returns false.
	private boolean hitBottomWall() {
		return ball.getY()+BALL_RADIUS*2 > getHeight();
	}

	//This method builds the bricks by building one row at a time, and then fills them with colors accordingly.
	private void buildBricks() {
		double x = coordinateX();
		double y = BRICK_Y_OFFSET;
		for(int i=0; i<NBRICK_ROWS; i++) {
			assignColor(i);
			buildRow(x, y);
			y += BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	//This method decides what color to use for the row. It receives a variable that represents the 
	// the number of the row being built, and then assigns the right color to the instance variable color.
	private void assignColor(int i) {
		if(i==0 || i==1) {
			color = Color.RED;
		}
		else if(i==2 || i==3) {
			color = Color.ORANGE;
		}
		else if(i==4 || i==5) {
			color = Color.YELLOW;
		}
		else if(i==6 || i==7) {
			color = Color.GREEN;
		}
		else {
			color = Color.CYAN;
		}
	}

	//This method returns the x coordinate for the starting point for building the bricks.
	private double coordinateX() {
		return (getWidth() - NBRICK_COLUMNS*BRICK_WIDTH - (NBRICK_COLUMNS-1)*BRICK_SEP) / 2;
	}
	
	//This method builds one row of bricks and uses the x and y it's given as parameters and then fills them with color.
	private void buildRow(double x, double y) {
		for(int i=0; i<NBRICK_COLUMNS; i++) {
			GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
			x += BRICK_WIDTH + BRICK_SEP;
		}
	}
	
	//This method builds the paddle and paints it black.
	private void buildPaddle() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = (getHeight() - (PADDLE_HEIGHT+PADDLE_Y_OFFSET));
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}
	
	//This method keeps tracks of the movement of the mouse and moves the paddle accordingly.
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if(x+PADDLE_WIDTH/2 >= getWidth()) {
			paddle.setX(getWidth() - PADDLE_WIDTH);
		}
		else if(x-PADDLE_WIDTH/2 <= 0) {
			paddle.setX(0.0);
		}
		else{
			paddle.setCenterX(x);
		}
	}
	
	//This method generates a randomized horizontal velocity and stores it in an instance variable.
	//It also assigns the value VELOCITY_Y to an instance variable vy. 
	public void moveBall() {
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
	}
	
	//This method creates the ball and sets it in the center of the canvas.s
	private void buildBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}
	
	//This method creates a label of the text that is passed to it a s parameter, and places it above the ball location by the ball radius.
	private void createLabel(String str) {
		label = new GLabel(str);
		double x = (getWidth() - label.getWidth())/2;
		double y = (getHeight() - label.getAscent())/2 - BALL_RADIUS;
		label.setLocation(x, y);
		label.setColor(Color.RED);
		add(label);
	}
	
	//This method checks if there's been collision between the ball and any other object in the canvas.
	// If the object is the paddle, it makes the ball bounce off of it. However if it's a brick,
	// it removes the brick and changes the direction of the y-velocity while counting the number of bricks 
	// that are being removed.
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		if(collider == paddle) {
			vy = -Math.abs(vy);
			bounceClip.play();
		}
		else if(collider != null) {
			vy = -vy;
			bounceClip.play();
			remove(collider);
			count++;
		}
	}
	
	//This method checks the four corner of the ball and returns the object that's colliding with any of the corners, if such object exists.
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		if(getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()) != null) {
			return getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY());
		}
		if(getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2) != null) {
			return getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2);
		}
		if(getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2) != null) {
			return getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2);
		}
		return null;
	}
	
	//This method checks if the player had removed all of the bricks and prints a statement accordingly
	private void checkForWin() {
		if(count == NBRICK_ROWS*NBRICK_COLUMNS) {
			createLabel("CONGRATS YOU LITTLE HOE!");
		}
	}
}
