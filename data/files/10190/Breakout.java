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

//* This program lets the user play the Breakout game! 
public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels:
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row:
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks:
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels:
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels:
	public static final double BRICK_WIDTH = Math.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels:
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels:
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle:
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom: 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels:
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity:
	public static final double VELOCITY_Y = 4.0; 

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 4.0; 

	// Animation delay or pause time between ball moves (ms):
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns:
	public static final int NTURNS = 3; 
	private int turn = 1; // This keeps track of which turn the player is on.
	
	// Instance variables:
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT); 
	private GOval ball = new GOval(2*BALL_RADIUS,2*BALL_RADIUS);
	private double vx, vy;
	private double rightX, leftX, upperY, lowerY; 
	private GObject collider = null;
	private int nBricks = 100; // This will keep track of how many bricks are left.
	
	// Coordinates of the paddle:
	double xp = 0;
	double yp = 0;
	
	// This variable creates random numbers: 
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// This sets up the canvas and begins once the user clicks the mouse:
	public void run() {
		addMouseListeners();
		setUpScreen();
		welcome();
		setUpNewGame();
		play();
	}
	
	// This sets up the screen:
	private void setUpScreen() {
		setTitle("CS 106A Breakout"); // This sets the window's title bar text.
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); // This sets the canvas size.
		setUpBricks();
	}
	// This sets up the colored bricks at the top of the screen: 
 	private void setUpBricks() {
		
		// Useful values: 
		double center = getWidth()/2;
		double baseLength = (BRICK_WIDTH + BRICK_SEP)*NBRICK_COLUMNS - BRICK_SEP;
		// This gives x-coordinate of the leftmost corner of the brick ensemble's base:
		double leftmostCorner = center - baseLength/2; 
		// This builds the bricks at the top of the screen:
		for(int row = 0; row < NBRICK_ROWS; row++) {	
			for(double col = 0; col < NBRICK_COLUMNS; col++) {
				double x = leftmostCorner + col*(BRICK_WIDTH + BRICK_SEP); 
				double y = BRICK_Y_OFFSET + row*(BRICK_HEIGHT + BRICK_SEP);
				GRect rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				// This ensures the bricks are colored:
				if (row >= 8) { 
					rect.setColor(Color.CYAN);
				} else if ( row >= 6) {
					rect.setColor(Color.GREEN);	
				} else if (row >= 4) {
					rect.setColor(Color.YELLOW);
				} else if (row >= 2) {
					rect.setColor(Color.ORANGE);
				} else if (row >= 0) {
					rect.setColor(Color.RED);
				}
				add(rect);
			}
		}
	}
	
 	// This displays a welcoming message: 
 	private void welcome() {
 		// This displays the name of the game:
 		GLabel gameName = new GLabel("BREAKOUT");
 		gameName.setFont("MONOSPACED-50");
		double x1 = getWidth()/2 - gameName.getWidth()/2;
		double y1 = getHeight()/2 - gameName.getAscent();
		gameName.setLocation(x1,y1);
		gameName.setColor(Color.RED);
		add(gameName);
		// This displays an opening message:
		GLabel instructions1 = new GLabel("Can you make all the bricks disappear before your turns run out?");
		double x2 = getWidth()/2 - instructions1.getWidth()/2;
		double y2 = getHeight()/2;
		instructions1.setLocation(x2,y2);
		instructions1.setFont("SANS_SERIF-12");
		add(instructions1);
		GLabel instructions2 = new GLabel("Click to play!");
		double x3 = getWidth()/2 - instructions2.getWidth()/2;
		double y3 = getHeight()/2 + instructions2.getAscent()*2;
		instructions2.setLocation(x3,y3);
		instructions2.setFont(Font.SANS_SERIF);
		add(instructions2);
		waitForClick();
		remove(instructions1);
		remove(instructions2);
		remove(gameName);
 	}
 	
	// This sets up the game so that it's ready to be played: 
	private void setUpNewGame() {
		nextTurn();	
		setUpBall();
		setUpPaddle();
	}
	 // This sets up the ball:
	private void setUpBall() {	
			double centerX = getWidth()/2 - BALL_RADIUS;
			double centerY = getHeight()/2 - BALL_RADIUS; 
			ball.setLocation(centerX, centerY);
			ball.setFilled(true);
			add(ball);
	}
	// This sets up the paddle: 	
	private void setUpPaddle() { 
		xp = getWidth()/2 - PADDLE_WIDTH/2;
		yp = getHeight() - PADDLE_Y_OFFSET; 
		paddle.setLocation(xp, yp);
		paddle.setFilled(true);
		add(paddle);
	}
	// This makes the paddle follow the horizontal movements of the mouse: 
	public void mouseMoved(MouseEvent e) {
		double dx = e.getX();
		if (dx <= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(dx,yp);
		} else {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, yp);
		}
	}
	
	// This plays the game:
	private void play() {
		while (true) {
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			vy = VELOCITY_Y;
			while (true) {
				ball.move(vx, vy);
				pause(DELAY);
				checkBounds();
				collision();
			}	
		} 
	}
	
	// This ensures the ball stays within the bounds of the screen:
	private void checkBounds() {
	// This gives the coordinates of the ball's bounding square:
		leftX = ball.getX();
		rightX = ball.getRightX();
		upperY = ball.getY(); 
		lowerY = ball.getBottomY();	
		if (lowerY > getHeight()-PADDLE_Y_OFFSET*0.8) { // Makes the player move on to the next turn if the ball goes beyond the paddle.
			remove (ball);
			pause(100);
			turn = turn + 1;
			if (nBricks == 0) { // This ends the game and displays the winning message if all the bricks are eliminated. 
				youWin();	
			} else if (turn < 4) {
				setUpNewGame();
			} else if (nBricks > 0) { // This ends the game and displays the losing message bricks remain after the 3 turns. 
				youLose();
			}
		} else if (upperY <= 0) { // Makes the ball bouce off the top wall.
			vy = VELOCITY_Y; 
		} else if (rightX >= getWidth()) { // Makes the ball bounce off the right wall.
			vx = -vx;	
		} else if (leftX <= 0) { // Makes the ball bounce off the left wall.
			vx = Math.abs(vx);
		}
	}
	
	// This displays a message to tell the player they are starting the next turn:
 	private void nextTurn() {
		GLabel label = new GLabel("Turn " +turn);
		double x = getWidth()/2 - label.getWidth()/2;
		double y = getHeight()/2 - label.getAscent()/2;
		label.setLocation(x,y);
		label.setFont(Font.SANS_SERIF);
		add(label);
		pause(2000);
		remove(label);
		vy = VELOCITY_Y; 
	}
	
 	// This establishes what happens once the ball starts moving and colliding with objects:
 	private void collision() {
		collisionCheck();
		collisionConsequence();
	}
	// This gets an object, should a collision occur:
	private void collisionCheck() {
		GObject getCollidingObject = getElementAt(rightX, lowerY);
		if (getCollidingObject != null) {
			collider = getCollidingObject;
		} else {
			getCollidingObject = getElementAt(leftX, lowerY);
			if (getCollidingObject != null) {
				collider = getCollidingObject;
			} else {
				getCollidingObject = getElementAt(leftX, upperY);
				if (getCollidingObject != null) {
					collider = getCollidingObject;
				} else {
					getCollidingObject = getElementAt(rightX, upperY);
					if (getCollidingObject != null) {
						collider = getCollidingObject;
					}
				}
			}
		}
	}
	// This decides what to do if the ball collides with something:
	private void collisionConsequence() {
		if (collider == paddle) {
		collider = null;
			vy = -vy;
		} else if (collider != null) {
			remove(collider);
			collider = null;
			nBricks = nBricks - 1; // This makes sure the program keeps track of how many bricks are left after the collision. 
			if (nBricks == 0) { // This checks to see if all the bricks have been removed.  
				turn = 4;
				remove(paddle);
				youWin();	
			} else {
			vy = -vy;
			}
		}
	}
	
	// If there are no bricks left, this displays a message to tell the player they have won:
 	private void youWin() {
		turn = 4;
 		GLabel label = new GLabel("You win!");
		double x = getWidth()/2 - label.getWidth()/2;
		double y = getHeight()/2 - label.getAscent()/2;
		label.setLocation(x,y);
		label.setFont(Font.SANS_SERIF);
		label.setColor(rgen.nextColor());
		add(label);
	}
	// If there are still bricks left at the end of three turns, this displays a message to tell the player they have lost:
 	private void youLose() {
		GLabel label = new GLabel("You lose!");
		double x = getWidth()/2 - label.getWidth()/2;
		double y = getHeight()/2 - label.getAscent()/2;
		label.setLocation(x,y);
		label.setFont(Font.SANS_SERIF);
		add(label);
	}
}
