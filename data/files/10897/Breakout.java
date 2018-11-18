/*
 /* File: Breakout.java
 * -------------------
 * Name: Roopa Som
 * Section Leader: Meng Zhang
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
	public static final double VELOCITY_Y = 2.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 10;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// PRIVATE VARIABLES I CAME UP WITH 
	// private Component e;
	private int brickTotal = 100;

	// declare ball velocity and make the x velocity random  
	private double vx, vy; 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private GRect paddle; 
	private GOval ball;
	int turns = NTURNS;
	// private GRect brick;
	// GOval ball = makeBall();
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");	
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		setUpGame(); // setup phase 
		playGame(); // play phase
		if (brickTotal > 0) { // end game conditions 
			removeAll(); // how do I remove everything on the screen? 
		}
		if (brickTotal == 0) { // breakout condition
			// print some comment to indicate that the user won 
			printWinningMessage();
			// get rid of the ball??? 
		}
		if (brickTotal > 0) { // end game conditions 
			// print some comment to indicate the game is over 
			printLosingMessage();
		}		
	}

	// setup phase : MAKING COLORED BRICKS and PADDLE and BALL
	private void setUpGame() {     
		makeBricks(); 
		makePaddle(); 
		makeBall(); 	
	}
	
	private void makeBricks() { // makes bricks, colored 
		// makes 10 by 10 matrix of bricks w/ specified dim above 
		double x = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH - BRICK_SEP * (NBRICK_COLUMNS - 1) )/2; // first (top left) brick x 
		double y = BRICK_Y_OFFSET ; // first brick y 
		for(int i = 0; i < NBRICK_COLUMNS; i ++) { // build for 10 rows 
			for(int j = 0; j < NBRICK_ROWS ; j++) { // builds 10 bricks horizontally 
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT); 
				brick.setFilled(true);
				add(brick); 
				colorBrick(brick, i);
				x += BRICK_WIDTH; // moves x position by width of brick 
				x += BRICK_SEP; // moves x position by brick separation 
			}
			x = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH - BRICK_SEP * (NBRICK_COLUMNS - 1) )/2 ; // same x position
			y += BRICK_HEIGHT; // moves y position by height of brick 
			y += BRICK_SEP;  // moves y position by brick separation 
		}
	}
	
	// coloring of bricks 
	private void colorBrick(GRect brick, int i) { //coloring every two rows with a different color  
		if (i == 0 || i == 1) { // top two rows colored red 
			brick.setColor(Color.RED);
		}
		if (i == 2 || i == 3 ) { // rows 3 and 4 colored orange
			brick.setColor(Color.ORANGE);
		}
		if (i == 4 || i == 5) { // rows 5 and 6 colored yellow 
			brick.setColor(Color.YELLOW);
		}
		if (i == 6 || i == 7) { // rows 7 and 8 colored green 
			brick.setColor(Color.GREEN);
		}
		if (i == 8 || i == 9) { // rows 9 and 10 colored cyan 
			brick.setColor(Color.CYAN);
		}
	}

	private void makePaddle() { // make paddle 
		double x = (getWidth() - PADDLE_WIDTH)/2.0;
		double y = getHeight() - PADDLE_Y_OFFSET;	
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle); 
		addMouseListeners();
	}

	// sets mouse location to paddle 
	public void mouseMoved(MouseEvent e) { 
		double mouseX = e.getX(); // finds x position of mouse, accounting for paddle 
		double y = getHeight() - PADDLE_Y_OFFSET; // finds y position of mouse, accounting for paddle 
		double rightSide = getWidth() - PADDLE_WIDTH;
		paddle.setLocation(mouseX, y);
		if (mouseX > rightSide) { // resets paddle to right end if it leaves right side of screen
			paddle.setLocation(rightSide, y);
		} else if (mouseX < 0) { // resets paddle to left end if it leaves left side of screen 
			paddle.setLocation(0, y); 
		} 
	}

	public void makeBall() {
		double x = getWidth()/2 - BALL_RADIUS/2 ; // x position 
		double y = getHeight()/2 - BALL_RADIUS/2 ; // y position  
		ball = new GOval(x, y, BALL_RADIUS, BALL_RADIUS); // makes oval 
		ball.setFilled(true);
		ball.setColor(Color.BLACK); 
		add(ball); // adds ball to center screen
	}
	
	// resets ball location when it passes screen boundaries 
	// except for bottom wall, in which case the ball just falls through 
	private boolean hitTopWall(GOval ball) {
		return ball.getY() < 0; 
	}
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - BALL_RADIUS*2; 
	}
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0; 
	}

	private void playGame() {
		//play phase 
		// time to get the ball moving
		waitForClick();
		setVelocity();		
		while (brickTotal != 0 && turns != 0) {
			ballMove();
			if (ball.getY() >= getHeight()) { // when ball falls to bottom of screen make life count go down
				turns--;
				remove(ball);
				makeBall();
				waitForClick();
			}
		}	
	}
	
	// initialize ball velocity
	private void setVelocity() {
		vy = VELOCITY_Y; 
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
		if (rgen.nextBoolean(0.5)) {
			vx = -vx; // moves in + or - x direction w/ equal likelihood
		}
	}
	
	// specify velocity change conditions within ball moving & collisions   
	private void ballMove() {
		ball.move(vx, vy);
		// check for right and left walls, updates velocity in x direction  
		// check for top and bottom walls, updates velocity in y direction 
		if(hitLeftWall(ball) || hitRightWall(ball)) { // if hits left or right wall, switch x direction
			vx = -vx;
		}
		if(hitTopWall(ball)) { // if hits top wall, switch y direction 
			vy = - vy;
		}

		// check for collisions 
		GObject collider = getCollidingObject(); 
		// if ball collides with paddle, ball should bounce up from the paddle 
		if (collider == paddle) {	
			// FIX FOR STICKY PADDLE CONDITIONS 
			// fix the absolute value so that the ball doesn't keep hitting the paddle 
			vy = - Math.abs(vy);
		} else if (collider != null) { // if the ball collided w/ something and it's not the paddle, it's a brick 
			vy = -vy;
			remove(collider);
			brickTotal--; 
		}
		// pause
		pause(DELAY);
	}
	
	// Checking for collisions
	private GObject getCollidingObject() {
		// find colliding object; if there is none return null
		// checks at all four corners of the ball for an object 
		if((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if((getElementAt(ball.getX() + (BALL_RADIUS*2), ball.getY())) != null) {
			return getElementAt(ball.getX() + (BALL_RADIUS*2), ball.getY());
		} else if((getElementAt(ball.getX(), ball.getY() + (BALL_RADIUS*2))) != null) {
			return getElementAt(ball.getX(), ball.getY() + (BALL_RADIUS*2));
		} else if((getElementAt(ball.getX() + (BALL_RADIUS*2), ball.getY() + (BALL_RADIUS*2))) != null) {
			return getElementAt(ball.getX() + (BALL_RADIUS*2), ball.getY() + (BALL_RADIUS*2));  
		} else {
			return null; 
		}	
	}

	// prints winning message if user achieves breakout 
	private void printWinningMessage() {
		GLabel winningMessage = new GLabel ("You won Breakout!");
		double a = getWidth()/2 - winningMessage.getWidth()/2;
		double b = getHeight()/2 - winningMessage.getAscent()/2; 
		winningMessage.setColor(Color.BLACK);
		add(winningMessage, a, b);
	}
	
	// prints a losing message after 3 turns and breakout has not been achieved 
	private void printLosingMessage() {
		GLabel losingMessage = new GLabel ("Game Over.");
		double a = getWidth()/2 - losingMessage.getWidth()/2;
		double b = getHeight()/2 - losingMessage.getAscent()/2; 
		losingMessage.setColor(Color.BLACK);
		add(losingMessage, a, b);
	}

}


