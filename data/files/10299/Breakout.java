/*
 * File: Breakout.java
 * -------------------
 * Name:Esther Omole
 * Section Leader: Kaitlyn
 * 
 * This program displays and runs the game "Breakout", in which the user controls a 
 * paddle and tries to clear the world's bricks using a ball.
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
	public static final double DELAY = 700.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
		
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//Sets up the bricks at the top of the canvas, centered in 
		//the window.
		
		makeRowPair(0, Color.RED);
		makeRowPair((2*(BRICK_HEIGHT + BRICK_SEP)), Color.ORANGE);
		makeRowPair((4*(BRICK_HEIGHT + BRICK_SEP)),Color.YELLOW);
		makeRowPair((6*(BRICK_HEIGHT + BRICK_SEP)),Color.GREEN);
		makeRowPair((8*(BRICK_HEIGHT + BRICK_SEP)), Color.CYAN);

		paddle = makePaddle();
		centerPaddle(paddle);
		ball= makeBall(); 	
		addMouseListeners();
		waitForClick();
		ballMove(ball);
	}
	
	// Method that makes a single row of bricks. 
	private void makeBrick(double bx, double by, Color color) {
		GRect brick = new GRect (bx, by, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add (brick);
	}		

	//Method that creates rows of bricks two at a time, grouping them based on color.
	private void makeRowPair(double ry, Color c) {
		for (int b=0; b<2; b++)	{
			double x = (( getWidth()-((BRICK_WIDTH * NBRICK_ROWS) + (NBRICK_ROWS-1)*BRICK_SEP))/2);
			double y = (BRICK_Y_OFFSET + b*(BRICK_SEP+BRICK_HEIGHT));
			for (int i=0; i<NBRICK_ROWS; i++) {
				makeBrick(x,ry + y, c);
				x += (BRICK_WIDTH + BRICK_SEP);				
			}
		}  

	}
	
	
	//Generates black rectangle, representing the paddle.
	private GRect makePaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	
	//Adds the paddle to the center of the player's window.
	private void centerPaddle(GRect paddle) {
		double px= (getWidth() - PADDLE_WIDTH)/2;
		double py= (getHeight() - (PADDLE_HEIGHT + PADDLE_Y_OFFSET));		
		add (paddle, px, py);
	}
	
	//Makes the ball and add's it to the center of the player's window.
	private GOval makeBall() {
		double bx= getWidth()/2 - BALL_RADIUS;
		double by= getHeight()/2 - BALL_RADIUS;
		double diameter = BALL_RADIUS*2;
		ball = new GOval(diameter, diameter);
		ball.setFilled(true);
		add (ball,bx, by);
		return ball;
	}

	//Allows the ball to bounce off the boundaries of the screen containing the world.
	private void ballMove(GOval ball) { 
		vx =rgen.nextDouble(1.0, 3.0);
		vy = 3.0;
		if (rgen.nextBoolean(0.5)) vx = -vx;{	
			while(true) {
			    ball.move(vx, vy);
			    checkForCollider();	
				if(bottomWall(ball)) {	   
		//Appears once the ball moves passed the bottom wall to indicate the game's end.		
				GLabel label = new GLabel ("YOU LOSE");
				double x = (getWidth()-label.getWidth())/2;;
				double y = (getHeight()- label.getHeight())/2;
				label.setFont("Lucida Blackletter-28");
				label.setColor (Color.MAGENTA);
				add(label, x,y);	
			}   
			if (topWall(ball)) {
				vy = -vy;	
			}
			if (rightWall(ball)) {
				vx = - vx;
			}
			if ( leftWall(ball)) {
				vx = -vx;	
			}
			pause(DELAY);
	  }
	}
  }

	private boolean leftWall(GOval ball) {
		return ball.getX()<0;
	}

	private boolean rightWall(GOval ball) {
		return ball.getX()>getWidth()-ball.getWidth();
	}

	private boolean topWall(GOval ball) {
		return ball.getY()< 0;
	}

	private boolean bottomWall(GOval ball) {
		return ball.getY()>getHeight()-ball.getHeight();
	}

	//Allows the ball to check if it has collided with another object.
	private GObject getCollision() {	
		if (getElementAt(ball.getX(), ball.getY())!=null) {
			return (getElementAt(ball.getX(), ball.getY()));
		} else if (getElementAt(ball.getX() + ball.getWidth(), ball.getY())!=null) {
			return (getElementAt(ball.getX() + ball.getWidth(), ball.getY()));
		} else if (getElementAt(ball.getX(), ball.getY()+ ball.getWidth())!=null) {
			return (getElementAt(ball.getX(), ball.getY()+ ball.getWidth()));
		} else if (getElementAt(ball.getX() + ball.getWidth(), ball.getY()+ ball.getHeight())!=null) {
			return (getElementAt(ball.getX() + ball.getWidth(), ball.getY()+ ball.getHeight()));
		} else
			return null;
    }

	//Indicates which object the ball collides into, if any, and the world's response.
	private void checkForCollider() {
		GObject collider = getCollision();
		if (getCollision()!=null) {
			if (getCollision() == paddle) {
				vy = -vy;
			} else if (getCollision() == collider) {
				vy = -vy;
				remove(collider);
				} 
			  else {
				GLabel label = new GLabel ("YOU WIN!");
				double x = (getWidth()-label.getWidth())/2;;
				double y = (getHeight()- label.getHeight())/2;
				label.setFont("Helvetica-24");
				label.setColor (Color.MAGENTA);
				add(label, x,y);
				}
		 }
		
	}

	/* Moves paddle left and right with user's mouse in the center */	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX() - PADDLE_WIDTH/2 ;
		double py = (getHeight() - (PADDLE_HEIGHT + PADDLE_Y_OFFSET));
		double maxX = getWidth() - (PADDLE_WIDTH);
		double minX = 0 ;
		if (mouseX> minX && maxX> mouseX) {
			paddle.setLocation (mouseX, py);
		}

	} 

}
