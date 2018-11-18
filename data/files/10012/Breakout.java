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
	public static final double CANVAS_WIDTH = 700;
	public static final double CANVAS_HEIGHT = 1000;

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
	public static final double DELAY = 700.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	
	
	GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
	GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	GOval ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
	int brickTracker = 0;
	
	private double vx, vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		addMouseListeners();
		//SETUP adds all the elements that are visible at the beginning of the game
		setUp();
		
		waitForClick();
		
		//in lines 97-99 the VELOCITY is defined in the run method for
		//later use in the following methods. (Could be instance variables?) 
		vy= -VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx =-vx;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		
		//lines 104- 110 are the animation sequences for the ball
		//only when there are still bricks and the ball hasn't hit the bottom wall
		// it checks for collisions and then moves the ball by the velocities previously established
		while((brickTracker != NBRICK_COLUMNS*NBRICK_ROWS) && (hitBottomWall(ball) != true)) {
			collisionCheck();
			ball.move(vx, vy);
			
			//uncomment the following line for movement stats for trouble shooting
			//println("vx:" + vx + "  vy:" + vy + "  ball y:" + ball.getY() + "  paddle y:" + paddle.getY());
			pause(DELAY);
		}
		
		//BRICKTRACKER will keep track of the numbers of bricks broken
		//if the number equals the amount of bricks total, it begins the win screen
		// if not, it pals the lose screen
		if (brickTracker == NBRICK_COLUMNS*NBRICK_ROWS) {
			winAndTryAgain();	
		}
		else {
			loseAndTryAgain();
		}
	}
	
	//COLLISIONCHECK will check if the ball has collided with anything 
	//if it hhas collided with something, if its a brick it removes it and bounces back
	//if its a paddle, it will bounce back.
	private void collisionCheck() {
		if (getCollidingObject() != null) {
			GObject coll = getCollidingObject();
			if (paddle == coll){
					vy= -vy;
					
					//to make sure that the ball does not get stck in the paddle,
					//this if loop makes sure that as long as the ball has hit(or is hitting) the paddle
					//it moves upscreen (avoids a sticky paddle)
					if (vy >  0) {
						vy = -vy;
					}
			}
			
			//removes the brick and adds a counter for the numer fo bricks broken
			else {
				remove(coll);
				vy=-vy;
				brickTracker++;
			}
		}	
		
		// LINES 150-156 will check for a collisio with a wall (note, hittinf the bottom wall starts LOSEANDPLAYAGAIN();
		if (hitTopWall(ball))  {
			vy= -vy;
		}
		if ((hitLeftWall(ball)) || (hitRightWall(ball))) {
			vx= -vx;
		}
	}
	
	//setUp will add the the starting elements to the world
	private void setUp() {
		setUpBricks();
		paddle = makePaddle();
		addPaddle();
		addBall();
	}
	
	//set up bricks will add all the bricks and changes the row colors every two rows
	private void setUpBricks() {
		for (int row = 0; row<= NBRICK_ROWS-1; row++) {
				for (int col = 0; col <= NBRICK_COLUMNS-1; col++) {
					brick =new GRect(BRICK_WIDTH, BRICK_HEIGHT);
					double x= (BRICK_SEP+(col*(BRICK_WIDTH+BRICK_SEP)));
					double y= (BRICK_Y_OFFSET+ (row*(BRICK_HEIGHT+BRICK_SEP)));
					brick.setFilled(true);
					if (row <= 1) {
						brick.setColor(Color.RED);
					}
					if ((row <=3) && (row >1)) {
						brick.setColor(Color.ORANGE);
					}
					if ((row <=5) && (row >3)) {
						brick.setColor(Color.YELLOW);
					}
					if ((row <=7) && (row >5)) {
						brick.setColor(Color.GREEN);
					}
					if ((row <=10) && (row >7)) {
						brick.setColor(Color.CYAN);
					}
					add(brick, x, y);
				}
		}
	}
	
	//MOUSE EVENT E will read the mouse and have the paddle track the mouse's movement on the x axis (y axis stays the same)
	public void  mouseMoved(MouseEvent e) {
		double x = e.getX() - (PADDLE_WIDTH/2);
		double y = (getHeight()-(PADDLE_HEIGHT + PADDLE_Y_OFFSET));
		paddle.setLocation(x,y);
	}
	
	//ADDPADDLE adds the paddle and estabilshes its y coordinate (static)
	private void addPaddle() {
		double x =(getWidth() - PADDLE_WIDTH) /2;
		double y = (getHeight()-(PADDLE_HEIGHT + PADDLE_Y_OFFSET));
		add (paddle, x, y);
	}
	
	//MAKEPADDLE establishes the parameters for PADDLE
	private GRect makePaddle() {
		paddle.setFilled(true);
		return paddle;
	}
	
	//ADDBALL will add the ball in the center of the screen
	private void addBall() {
		ball.setFilled(true);
		add (ball, getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS);
	}
	
	//HITTOPWALL creates the conditional to test if the ball is past the wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	// HITRIGHTWALL does  the same but for the right wall
	private boolean hitRightWall(GOval ball) {
			return ball.getX() >= getWidth() - ball.getWidth();
	}
	
	//HITBOTTOMWALL does the same but for the bottom wall
	private boolean hitBottomWall(GOval ball) {
			return getHeight() - ball.getHeight() <= ball.getY();
	}
	
	// HITLEFTWALL does the same but for the... you get it
	private boolean hitLeftWall(GOval ball) {
			return ball.getX() <= 0;
	}
	
	
	// getcolliding object will check all four corners of the ball to test if it has interacted with an object
	private  GObject getCollidingObject() {
		GObject coll= getElementAt(ball.getX() + (2*BALL_RADIUS), ball.getY());
		if (coll == null) {
			coll= getElementAt(ball.getX()+ (2*BALL_RADIUS), ball.getY() + (2*BALL_RADIUS));
		}
		if (coll == null ) {
			coll= getElementAt(ball.getX(), ball.getY() + (2*BALL_RADIUS));
		}
			
		if (coll == null) {
			coll= getElementAt(ball.getX(), ball.getY());
		}
		return coll;
	}

	//win and try again gives the player a win messag and asks them to play again
	private void winAndTryAgain() {
		clear();
		screenText("You won, Sucker!");
		pause(1500);
		clear();
		screenText("Click to try again?");
		waitForClick();
		clear();
		run();
		
	}
	
	//LOSEANDTRYAGAIN gives the player a loss mesage and asks them to play again 
	private void loseAndTryAgain() {
		clear();
		screenText("You lose, sucker");
		pause(1500);
		clear();
		screenText("Click to try again?");
		waitForClick();
		clear();
		run();
	}
	
	//sets the text that will display on screen
	//a string input is needed
	private GLabel screenText(String alpha) { 
		GLabel text = new GLabel (alpha);
		text.setFont("Monospaced-Bold-20");
		double x = (getWidth() - text.getWidth()) / 2;
	    double y = (getHeight() + text.getAscent()) / 2;
		add (text, x, y);
		return text;
	}
}
	
	

