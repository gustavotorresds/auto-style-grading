/*
 * File: Breakout.java
 * -------------------
 * Name: Will Shao
 * Section Leader: Chase Davis
 * 
 * The file starts off with an initial opening screen. Once the mouse is clicked, 
 * the game template will be set up. Upon clicking another time, the game will begin.
 * The ball will bounce off the walls and paddle upon contact and will do likewise with
 * the bricks (as well as remove the brick it collides with). If the ball goes off the 
 * bottom wall, the player loses one life out of their initial three and the ball is reset. 
 * The game is won by the player destroying all of the bricks.
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
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		double x = CANVAS_WIDTH/2 - NBRICK_COLUMNS*BRICK_WIDTH/2 - BRICK_SEP*NBRICK_COLUMNS/2 + BRICK_SEP/2; //Gives coordinates for the bricks
		double y = BRICK_Y_OFFSET;
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX); //Randomizes the speed of the ball
		vy = VELOCITY_Y;
		if (rgen.nextBoolean (0.5)) vx = -vx; //Gives the vx value a 50% chance of changing its value from positive to negative
		
		GImage opening = openingImage();
		waitForClick(); //Creates the initial starting screen
		remove(opening);
		addMouseListeners(); //Triggers mouse listener function
		setUpGame(x, y); //Sets up the game 
		playGame(); //allows game to be played
	}

	public void mouseMoved(MouseEvent e) { //Triggers Mouse Event for if the mouse is moved
		double x = e.getX();
		double y = CANVAS_HEIGHT - PADDLE_HEIGHT/2 - PADDLE_Y_OFFSET;  
		if (x + PADDLE_WIDTH/2 < CANVAS_WIDTH && x - PADDLE_WIDTH/2 > 0) {
			paddle.setLocation(x-PADDLE_WIDTH/2, y-PADDLE_HEIGHT/2); //This moves the paddle with the mouse to the edges of the canvas
		}
	}
	
	private GImage openingImage() {
		GImage opening = new GImage ("Brick Breaker.png");
		double x = (getWidth() - opening.getWidth())/2;
		double y = (getHeight() - opening.getHeight())/2; //Inserts an image into the opening screen
		add(opening, x, y);
		return opening;
	}
	
	private void playGame() {
		for (int i = 0; i < NTURNS; i++) {
			waitForClick();
			while (true) {
				ball.move(vx, vy); //Allows the ball to move

				collideWithObjects();  //Allows the ball to collide with objects on the screen
				
				if(ball.getX() > CANVAS_WIDTH - ball.getWidth()) {
					vx = -vx;
				} else if(ball.getX() < 0) {
					vx = -vx; //These allow the ball to bounce off the left, right and top walls of the canvas
				} else if (ball.getY() < 0) {
					vy = - vy;
				} else if (ball.getY() > CANVAS_HEIGHT) { //If the ball goes off the bottom of the screen, the game will reset
					createBall();
					break;
				}
				pause (DELAY);
			}
		}
	}
	
	private void collideWithObjects() {  //Allows the ball to collide with objects on the screen
		AudioClip bounceClip = MediaTools.loadAudioClip ("bounce.au"); //Creates the bounce noise effect
		GObject collider = getCollidingObject();
		if (collider == paddle) { //Changes velocity of the ball if collision occurs
			bounceClip.play();
			vy = -vy;
		} else if (collider != null){ //Changes velocity of the ball if collision occurs
			remove(collider); //Removes object if object is a brick
			bounceClip.play();
			vy = -vy;
		}
	}

	private void setUpGame(double x, double y) { 
		makeGrid(x, y); 
		createPaddle(); //These construct the bricks, paddle and ball needed to play the game
		createBall();
	}


	private void makeBrickRow(double x, double y, Color color) {
		for (int i=0; i<NBRICK_COLUMNS; i++) {
			double a = x + BRICK_WIDTH + BRICK_SEP;
			GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true); 					//Makes a first row of bricks
			brick.setColor(color); 
			add(brick);
			x = a;
		}
	}

	private void makeGrid(double x, double y) {
		for (int j=0; j<NBRICK_ROWS; j++) {
			double b = y + BRICK_HEIGHT + BRICK_SEP; //Creates a whole brick grid
			int onesDigit = j % 10;
			if (onesDigit == 0 || onesDigit == 1) {
				makeBrickRow(x, y, Color.RED);
			} else if (onesDigit == 2 || onesDigit == 3) {
				makeBrickRow(x, y, Color.ORANGE);
			} else if (onesDigit == 4 || onesDigit == 5) { //Colors the brick rows in accordingly
				makeBrickRow(x, y, Color.YELLOW);
			} else if (onesDigit == 6 || onesDigit == 7) {
				makeBrickRow(x, y, Color.GREEN);
			} else if (onesDigit == 8 || onesDigit == 9) {
				makeBrickRow(x, y, Color.CYAN);
			}
			y = b;
		} 
	}

	private void createPaddle() {
		double x = CANVAS_WIDTH/2 - PADDLE_WIDTH/2;
		double y = CANVAS_HEIGHT - PADDLE_HEIGHT/2 - PADDLE_Y_OFFSET;
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT); //Creates the paddle at the bottom of the canvas
		paddle.setFilled(true);
		paddle.setColor(Color.BLUE);
		add(paddle);
	}

	private void createBall() {
		double x = CANVAS_WIDTH/2 - BALL_RADIUS/2;
		double y = CANVAS_HEIGHT/2 - BALL_RADIUS/2;
		ball = new GOval (x, y, BALL_RADIUS, BALL_RADIUS); //Creates the ball in the center of the screen
		ball.setFilled(true);
		add(ball);
	}

	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		GObject ballnorth = getElementAt (x + BALL_RADIUS, y);
		GObject balleast = getElementAt (x + BALL_RADIUS*2, y + BALL_RADIUS);
		GObject ballwest = getElementAt (x - BALL_RADIUS, y + BALL_RADIUS*2); //These get the coordinates of the four points of the ball
		GObject ballsouth = getElementAt (x + BALL_RADIUS*2, y + BALL_RADIUS);
		if (ballnorth != null) {
			return ballnorth;
		} else if (balleast != null) {
			return balleast;
		} else if (ballwest != null) { 	//Returns the ball if there is an object present at any of the four coordinates
			return ballwest;
		} else if (ballsouth != null) {
			return ballsouth;
		}
		return null;
	}

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx; 
	private double vy;  //Instance variables that can be read throughout the entire program
	private GOval ball;
	private GRect paddle;
}

