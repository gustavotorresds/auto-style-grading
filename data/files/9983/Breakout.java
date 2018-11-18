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
	public static final double PADDLE_Y_OFFSET = 100;
	
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
	//Paddle for the whole program 
	private GRect Paddle = null;
	//Ball for the whole program 
	private GOval ball = null;
	// Ball velocity 
	private double vx,vy;
	// Variable to tell if Winner or loser
	private boolean winOrLose = false;
	// counter of number of brickers
	private double counter =  NBRICK_ROWS*NBRICK_COLUMNS;
	// number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	// variable to avoid the case where paddle and ball are colling multiples times  
	private double gracePeriod = 0;

		public void run() {
			// Set the window's title bar text
			setTitle("CS 106A Breakout");
	
			// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
			// and getHeight() to get the screen dimensions, not these constants!
			setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
	
			/* You fill this in, along with any subsidiary methods */
			// 1. setup
			buildSetup();
			// 2. addlisteners 
			addMouseListeners();
			//setting the x speed random 
			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			//setting vy easy
			vy = 3;
			// variable to plot the win or lose label
			double a = getWidth()/2 ;
			double b = getHeight()/2;
			
			// while that runs the animation. Counter is the variable that keeps the program running
			while (counter != 0) {
					collision();
					moveBall();					
					pause (DELAY);
			}
			// if to define if win of lose 		
			if (winOrLose = false) {
					GLabel win = new GLabel ("WINNER");
					add (win,a,b);
			}
			else {
					GLabel lose = new GLabel ("LOSER");
					add (lose,a,b);
			}
			
		}
	// collision is the method that verifies what is each corner of the ball
		
	private void collision(){
			// Grace period is to avoid that the ball reach the paddle 2 
		
			if (gracePeriod >0) {
				gracePeriod--;
				println ("grace"+gracePeriod);
				;
			} 
		
			else { 
				// getelement checks if the ball has hit something
				if (getElementAt (ball.getX(),ball.getY()) != null) {
					GObject a = getCollidingObject(ball.getX(),ball.getY());
					// if the ball reachs the paddle it only changes the direction and adds a grace period
					if (a == Paddle) {
						vy =-vy;
						gracePeriod = 10;
					}
					// if the ball reachs the brick it changes direction, remove the brick 
					else {remove(a);
						vy =-vy;
						counter -= 1;
					}
				}
				else if(getElementAt (ball.getX()+2*BALL_RADIUS,ball.getY()) != null) {
					GObject b = getCollidingObject(ball.getX()+2*BALL_RADIUS,ball.getY());
		
					if (b == Paddle) {
						vy =-vy;
						gracePeriod = 10;
					}
					else {remove(b);
						vy =-vy;	
						counter -= 1;
					}
				}
				else if (getElementAt (ball.getX(),ball.getY()+2*BALL_RADIUS) != null) {
					GObject c = getCollidingObject(ball.getX(),ball.getY()+2*BALL_RADIUS);
		
					if (c == Paddle) {
						vy =-vy;
						gracePeriod = 10;
					}
					else {remove(c);
						vy =-vy;	
						counter -= 1;
					}
				}
				else if (getElementAt (ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS) != null) {
					GObject d = getCollidingObject(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS);
		
					if (d == Paddle) {
						vy =-vy;
						gracePeriod = 10;
					}
					else {remove(d);
						vy =-vy;	
						counter -= 1;
					}
		
				}
			}		
	}
	// getColliding gets the element that the ball is hitting 
	private GObject getCollidingObject(double a, double b) {
		GObject collider = getElementAt (a,b);
		return collider;
	}
	
	// Move Ball keep the ball moving 	
	private void moveBall() {;
		//returning once reaching bottom
		if ((ball.getY() + 2*BALL_RADIUS) > getHeight()) {
			vy = -vy;
			counter = 0;
			winOrLose = true;
		}
		//returning once reaching top
		if ((ball.getY() + 2*BALL_RADIUS) < 0) {
			vy = -vy;
		}
		//returning once reaching right side
		if ((ball.getX() + 2*BALL_RADIUS) > getWidth()) {
			vx = -vx;
		}
		//returning once reaching left side
		if ((ball.getX() + 2*BALL_RADIUS) < 0) {
			vx = -vx;
		}
		ball.move(vx,vy);
		
	}	
		
	// build setup is building all the elements without moving 
	private void buildSetup() {
		buildBricks ();
		buildBall();
		buildPaddle();	
	}
	private void buildBricks(){
		//For each color call build brick row twice RED,ORANGE, YELLOW, GREEN, CYAN
		// Array with the colours 
		Color[] anArray; 
		anArray = new Color[5];		
		anArray[0] = Color.RED;
        anArray[1] = Color.ORANGE;
        anArray[2] = Color.YELLOW;
        anArray[3] = Color.GREEN;
        anArray[4] = Color.CYAN;
        double b = BRICK_Y_OFFSET;
		
        // The for below changes the color in the array and the begin of the row of that color 
		// b is the y position of the start of each color 

        	for (int i = 0 ; i < 5 ; i++) {
        		buildBrickRow(anArray[i],b);
        		b = BRICK_Y_OFFSET;
        		b += 2*(i+1)*(BRICK_SEP+BRICK_HEIGHT);
        	}
        
	}
	
	private void buildBrickRow (Color a, double b) {

		double y = b; 	
			//For each color call buildbrickrow writes two lines  
			for ( int z = 0; z <2; z++) {
				double x = BRICK_SEP;
				for (double i = 0 ; i < NBRICK_COLUMNS; i++) {
					GRect rect = new GRect(x,y, BRICK_WIDTH, BRICK_HEIGHT);
					rect.setFilled(true);
					rect.setColor(a);
					add(rect);
					x += BRICK_WIDTH + BRICK_SEP;
				}
				// y guarantees that we are applying the right space between lines
				y += BRICK_HEIGHT + BRICK_SEP;
			}	
	}
	// mouseMoved captures the move movement and sends to the method that builds the moving Paddle
	
	public void mouseMoved(MouseEvent e) {
		// Maxpaddle limit the movement of the paddle 
		double Maxpaddle = getWidth() - PADDLE_WIDTH;
		// the if statement below guarantee that the memory is erase and only one paddle is shown
		
		if (e.getX()<= Maxpaddle) {
			if (Paddle != null) {
					remove (Paddle);
					Paddle = null;
			}
			 if(Paddle == null){
			 		MovingPaddle(e.getX());
			 }
		}	 
	}
	
	// buildMovingPaddles builds the paddle following the mouse
		private void MovingPaddle(double a){
			buildPaddle();
			add(Paddle,a, getHeight()-PADDLE_Y_OFFSET);
		}
		
	
	//buildPaddle builds the paddle for the first time
	private void buildPaddle(){
		Paddle = new GRect(0,getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		Paddle.setFilled(true);
		Paddle.setColor(Color.BLACK);
		add(Paddle);
	}
	
	// buildMovingPaddles builds the paddle following the mouse
	private void buildBall() {
		double BallX = (getWidth() - 2*BALL_RADIUS)/2;
		double BallY = (getHeight() - 2*BALL_RADIUS)/2;
		ball = new GOval (BallX,BallY,2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setColor (Color.BLUE);
		ball.setFilled (true);
		add (ball);
	}
}
