/*
 * File: Breakout.java
 * -------------------
 * Name: Alicja Cygan 
 * Section Leader: Cat Xu 
 * 
 * This file implements the game of Breakout.
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

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Text to display at beginning and end of game 
	public static final String WIN = "You won!";
	public static final String LOSE = "You lost!";
	public static final String CLICK = "Click the mouse to begin";

	private GRect paddle = null; 
	private GOval ball = null; 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private int turncount = NTURNS; 


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Set up the game (bricks, paddle, instructions)
		setup(); 

		// Enables the paddle to be tracked and the ball to be added by the mouse 
		addMouseListeners();

		// Play the game
		playGame();

	}

	// Method: Setup
	// ------------------
	// Adds bricks and a paddle to the canvas. 
	private void setup() {

		// setup centered rainbow brick rows on top of canvas
		for (int i = 0; i < NBRICK_ROWS; i++) {
			double y = BRICK_Y_OFFSET + i*BRICK_HEIGHT + i*BRICK_SEP;
			Color color = getColorFromRow(i); 
			drawBrickRow(y, color); 
		}

		// setup paddle in the bottom middle of the canvas
		double x = 0.5*(getWidth()-PADDLE_WIDTH);
		double y = getHeight()-PADDLE_Y_OFFSET; 
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT); 
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);

		// display text to tell player to start the game 
		if (ball == null) {
			addLabel(CLICK); 
		}

	}

	// Method: Mouse Moved
	// ------------------
	// Tracks the paddle with the mouse. 
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX();

		if(mouseX < getWidth()-PADDLE_WIDTH) { 
			paddle.setX(mouseX);
		}
	}

	// Method: Mouse Clicked
	// ------------------
	// Adds a ball to the screen when the mouse is clicked. 
	public void mouseClicked (MouseEvent e) {
		addBall(); 
	}

	// Method: Play Game 
	// ------------------
	// Play the Breakout game! 
	// Moves the ball around the canvas and checks
	// if it hits objects or if it goes out of bounds 
	private void playGame() {

		// tracks the # of bricks remaining on the canvas
		int brickcount = NBRICK_ROWS*NBRICK_COLUMNS; 

		// velocity of the ball; the horizantal velocity is randomly generated
		double vy = VELOCITY_Y; 
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}

		// animation loop 
		while(true) {

			// checks for collisions with objects 
			if (ball != null) {
				GObject collider = getCollidingObject();

				// if the ball collides with the paddle, reverse the vertical direction 
				// sound is played 
				if(collider != null) {
					if(collider == paddle) {
						// use abs value of vy to prevent the ball from getting stuck in paddle
						vy = -(Math.abs(vy));
						bounceClip.play();

						// if the ball collides with another object, it must be a brick and therefore
						// is removed and the remaining # of bricks is decreased by 1
						// sound is played 
					} else  {
						remove(collider); 
						vy = -vy;
						bounceClip.play();
						brickcount = brickcount -1; 

						// if no bricks remain, add a winner label 
						if(brickcount == 0) {
							addLabel(WIN); 
							break;
						}
					}
				}

				// reverses direction of ball if it hits the top of the canvas
				if (ball.getY() <= 0) {
					vy = -vy; 	
				}

				// reverses direction of ball if it hits the sides of the canvas
				if (ball.getX() >= (getWidth() - 2*BALL_RADIUS) || ball.getX() <= 0) {
					vx = -vx;	
				}

				// if the ball hits the bottom of the canvas, it is removed and the number of
				// turns is decreased by 1. 
				if (ball.getY() >= (getHeight() - 2*BALL_RADIUS)) {
					remove(ball); 
					ball = null; 
					turncount = turncount - 1; 

					// if no turns remain, remove the paddle and display a loser label
					if(turncount == 0) {
						remove(paddle);
						addLabel(LOSE); 
						break; 
					}

					// this makes the ball move
				} else {
					ball.move(vx,vy);
				}
			}
			pause(DELAY); 
		}
	}

	// Method: Get Colliding Objects
	// ------------------
	// Checks if the 4 corners surrounding the ball have touched another object
	private GObject getCollidingObject() {

		// checks top left corner
		GObject topLeftCornerObject = getElementAt(ball.getX(), ball.getY());
		if(topLeftCornerObject != null) {
			return topLeftCornerObject;  
		}

		// checks top right corner
		GObject topRightCornerObject = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		if(topRightCornerObject != null) {
			return topRightCornerObject;  
		}

		// checks bottom left corner
		GObject bottomLeftCornerObject = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		if(bottomLeftCornerObject != null) {
			return bottomLeftCornerObject;  
		}

		//checks bottom right corner 
		GObject bottomRightCornerObject = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		if(bottomRightCornerObject != null) {
			return bottomRightCornerObject; 
		}

		// return null if no object has been touched by the ball
		return null;  
	}


	// Method: Add Ball 
	// ------------------
	// Adds a ball to the screen. 
	private void addBall() {

		// if a ball doesn't already exist on the screen and there are still play turns remaining,
		// a ball will be added to the canvas
		if (ball == null && turncount != 0) {
			double a = (getWidth()-2*BALL_RADIUS)/2 ; 
			double b = (getHeight()-2*BALL_RADIUS)/2; 
			ball = new GOval(a, b, 2*BALL_RADIUS, 2*BALL_RADIUS); 
			ball.setFilled(true);
			add(ball);
		}
	}

	// Method: Draw Brick Row 
	// ------------------
	// Draws a row of colored bricks 
	private void drawBrickRow(double y, Color color) {
		double x = 0.5*(getWidth() - ((NBRICK_COLUMNS*BRICK_WIDTH) + ((NBRICK_COLUMNS - 1)*BRICK_SEP))); 
		for (int i = 0; i<NBRICK_COLUMNS; i++) {
			GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT); 
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);

			x = x + BRICK_WIDTH + BRICK_SEP; 
		}
	}

	// Method: Get Color From Row 
	// ------------------
	// Determines the color of a row of bricks (default = black)
	private Color getColorFromRow(int i) {
		Color color = Color.BLACK; 

		if(i == 0 || i == 1) {
			color = Color.RED; 
		}
		if(i == 2 || i == 3) {
			color = Color.ORANGE;
		}
		if(i == 4 || i == 5) {
			color = Color.YELLOW;
		}
		if(i == 6 || i == 7) {
			color = Color.GREEN;
		}
		if(i == 8 || i == 9) {
			color = Color.CYAN;
		}
		return color;
	}


	//Method: Add Label 
	//------------------
	//Generates a centered label.  
	private void addLabel(String TEXT) {
		GLabel text = new GLabel(TEXT);
		double textWidth = text.getWidth();
		double textHeight = text.getAscent(); 
		double textCenteredX = ((getWidth() - textWidth)/2); 
		double textCenteredY = ((getHeight() - textHeight)/2); 
		text.setLocation(textCenteredX, textCenteredY);
		add(text);
	}
}




