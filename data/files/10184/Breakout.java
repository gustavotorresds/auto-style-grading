/*
 * File: Breakout.java
 * -------------------
 * Name: Terence Theisen
 * Section Leader: Cat Xu
 * 
 * This file will implement the game of Breakout. This is made to meet the requirements 
 * of the assignment. Also I read the extension thing too late so this has extensions:
 * 1) bounce noise upon contact
 * 2) text to say what happened at end game
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
	public static final double CANVAS_WIDTH = 400;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 3;

	// Width of each brick, in pixels
	//	public static final double BRICK_WIDTH = Math.floor(
	//	(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);
	//the Math.floor definition seems to throw off the spacing of my last column
	//I'm sure there is a sound rationale for this but I can't seem to get it to work
	public static final double BRICK_WIDTH = (
			( (CANVAS_WIDTH - BRICK_SEP) / (NBRICK_COLUMNS) ) - BRICK_SEP );

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
	public static final double VELOCITY_Y = 3;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;
	//Pause before new ball in added between turns
	public static final double BALL_RELEASE_TIME = 500;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	//instance variables
	private RandomGenerator rgen = RandomGenerator.getInstance();

	private GRect paddle = null;
	private GOval ball = null;
	private double vx, vy;
	private double bx, by;
	private boolean movingBall = true;
	private boolean gameover = false;
	private int brickcounter = NBRICK_ROWS * NBRICK_COLUMNS;
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp();
		addMouseListeners();
		runGame();
	}

	/*
	 * This method runs the game by adding the ball, checking for collisions
	 * and tracking for ending conditions
	 */
	private void runGame() {
		for (int i = 0; i < NTURNS; i++ ) {
			addBall();
			pause(BALL_RELEASE_TIME);
			while(movingBall) {
				moveBall();
				//deals with the paddle collision issue
				//collisions are only checked for when above the paddle
				if (ball.getY() < (getHeight() - PADDLE_Y_OFFSET) ) {
					collisionChecker();
				}
				pause(DELAY);
				//when all the bricks are removed this triggers endgame
				if (brickcounter < 1) {
					movingBall = false;
					gameover = true;
					i = NTURNS;
				}
			}
		}
		//makes GLabel for end game conditions
		GLabel text = new GLabel("You lost. Bummer.");
		double tx = getWidth()/2 - text.getWidth()/2;
		double ty = getHeight()/2 - text.getHeight()/2;
		text.setFont(SCREEN_FONT);

		//deals with text in case of winner
		if(gameover) {
			text.setLabel("You won! You da real MVP");
			tx = getWidth()/2 - text.getWidth()/2;
			ty = getHeight()/2 - text.getHeight()/2;
			add(text, tx, ty);
		} else {
			add(text, tx, ty);
		}
	}

	/*
	 * this method look for collisions using getCollidingObject() 
	 * and then deals with the collision
	 * If collision is with paddle, it flips the direction
	 * If collision is with bricks it removes the bricks and tracks the amount
	 * of bricks removed
	 */
	private void collisionChecker() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			bounceClip.play();
			vy = -vy;
		} else if (collider != null) {
			bounceClip.play();
			remove(collider);
			brickcounter = brickcounter - 1;
			vy = -vy;
		}
	}

	/*
	 * This method looks at the four corners of the ball object and checks for
	 * collisions with objects at all of them
	 * It returns the object if there is a collision
	 */
	private GObject getCollidingObject() {
		//coordinators for the four corners of the ball
		double ballRightEdge = bx;
		double ballLeftEdge = (bx + (2 * BALL_RADIUS) ) ;
		double ballTopEdge = by; 
		double ballBottomEdge = (by + (2 * BALL_RADIUS) );

		//deals with whether collisions happen at each of the ball corners
		if (getElementAt(ballRightEdge, ballTopEdge) != null) {
			GObject obj = getElementAt(ballRightEdge, ballTopEdge);
			return obj;
		} else if (getElementAt(ballRightEdge, ballBottomEdge) != null) {
			GObject obj = getElementAt(ballRightEdge, ballBottomEdge);
			return obj;
		} else if (getElementAt(ballLeftEdge, ballTopEdge) != null) {
			GObject obj = getElementAt(ballLeftEdge, ballTopEdge);
			return obj;
		} else if (getElementAt(ballLeftEdge, ballBottomEdge) != null) {
			GObject obj = getElementAt(ballLeftEdge, ballBottomEdge);
			return obj;
		}
		GObject obj = null;
		return obj;
	}

	/*
	 * This checks if a ball is either at the top, the sides, or the 
	 * bottom of the canvas. If at the top or sides, it will change the direction
	 * If at the bottom it will remove the ball
	 */
	private void moveBall() {
		//removes the ball if it hits the bottom
		if (ball.getY() > (getHeight()) ) {
			remove(ball);
			movingBall = false;
		}
		//flips the direction of the ball if it hits the top
		if (ball.getY() < 0 ) {
			vy = -vy;
		}
		//flips the direction of the ball if it hits a side
		if (ball.getX() < 0 || ball.getX() > getWidth() - BALL_RADIUS * 2) {
			vx = -vx;
		}
		ball.move(vx, vy);
		bx = ball.getX();
		by = ball.getY();
	}

	/*
	 * This method adds a ball centered in the canvas and gives 
	 * it a random angle of descent
	 */
	private void addBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		movingBall = true;
		//generates starting x coordinate for ball
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		add (ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
	}

	/*
	 * This method does the setup part
	 * it makes the bricks and the paddle
	 */
	private void setUp() {
		makeAndColorRows();
		makePaddle();
	}

	/*
	 * This method makes the paddle 
	 */
	private void makePaddle() {
		double px = (getWidth() / 2) - (PADDLE_WIDTH / 2);
		double py = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(px, py, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
	}

	/*
	 * This method makes the rows and columns of bricks, positions them and
	 * determines their parameters
	 */
	private void makeAndColorRows() {
		for (int row = 0; row < NBRICK_ROWS; row++ ) {
			for (int col = 0; col < NBRICK_COLUMNS; col++ ) { 

				//determine coordinates
				double blockWidth = BRICK_WIDTH + BRICK_SEP;
				double blockHeight = BRICK_HEIGHT + BRICK_SEP;
				double bx = BRICK_SEP + (col * blockWidth);
				double by = BRICK_Y_OFFSET + (row * blockHeight);

				//parameters of a brick
				GRect brick = new GRect(bx, by, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setLineWidth(0);
				brick.setColor( getBrickColor(row) );
				add(brick);	
			}
		}
	}

	/*
	 * This method determines the color of the bricks
	 * it takes as input the row values that the for loop is on
	 * RED, ORANGE, YELLOW, GREEN, CYAN
	 */
	private Color getBrickColor(int row) {
		//determines that every 2 rows the color will change 
		int colorTracker = row/2;
		//need to make a variable to hold the color information 
		Color brickColor = Color.WHITE;
		//these if statements determine the color of a brick based on the row it is in
		if ( colorTracker % 5 == 0 ) {
			brickColor = Color.RED;
		} else if ( colorTracker % 5 == 1 ) {
			brickColor = Color.ORANGE;
		} else if ( colorTracker % 5 == 2 ) {
			brickColor = Color.YELLOW;
		} else if ( colorTracker % 5 == 3 ) {
			brickColor = Color.GREEN;
		} else if ( colorTracker % 5 == 4 ) {
			brickColor = Color.CYAN;
		}
		return brickColor;
	}

	public void mouseMoved(MouseEvent e) {
		int mx = e.getX();
		double rightEdge = PADDLE_WIDTH / 2;
		double leftEdge = CANVAS_WIDTH - (PADDLE_WIDTH / 2);
		if(mx < leftEdge && mx > rightEdge) {
			paddle.setX(mx - (PADDLE_WIDTH/2));
		}
	}
}