/*
 * File: Breakout.java
 * -------------------
 * Name: Zhuoer Gu
 * Section Leader: Esteban Rey
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

	// Number of brick columns
	public static final int NBRICK_COLUMNS = 10;

	// Number of brick rows
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
	
	// instance variable paddle
	private GRect paddle= new GRect (PADDLE_WIDTH, PADDLE_HEIGHT); 	
	
	// number of bricks initially
	public int NBrick= NBRICK_COLUMNS* NBRICK_ROWS;
	
	private double vx,vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Build the bricks in different columns.
		for (int n=0; n< NBRICK_COLUMNS ; n++) {
			// Calculate the x-coordinates of the bricks.
			double x= (getWidth()- NBRICK_COLUMNS * BRICK_WIDTH- (NBRICK_COLUMNS-1)* BRICK_SEP)/2 + n*(BRICK_WIDTH+BRICK_SEP);
			
			// Build bricks in different rows. Calculate the y-coordinates of the bricks.
			for (int m=0; m< NBRICK_ROWS; m++) {
				GRect brick= new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				double y= BRICK_Y_OFFSET + m* (BRICK_HEIGHT+BRICK_SEP);
				brick.setFilled(true);
				
				// **manually set the colors according to the number of row.
				if (m==0 || m==1) {
					brick.setColor(Color.RED);
				}
				if (m==2 || m==3) {
					brick.setColor(Color.ORANGE);
				}
				if (m==4 || m==5) {
					brick.setColor(Color.YELLOW);
				}
				if (m==6 || m==7) {
					brick.setColor(Color.GREEN);
				}
				if (m==8 || m==9) {
					brick.setColor(Color.CYAN);
				}
				add(brick, x, y);
			}
			
		}
		ball();
	}
	
	// **Create the paddle. Calculate the initial position of the paddle.
		//* But why the initial paddle does not appear?**//
		public void createPaddle() {
			double xpaddle= (getWidth()-PADDLE_WIDTH)/2;
			double ypaddle= getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
			paddle.setFilled(true);
			paddle.setColor(Color.BLACK);
		    paddle.setLocation(xpaddle, ypaddle);
			add(paddle);
			addMouseListeners();
	}
		
		// the paddle moves to the mouse location.
		public void mouseMoved (MouseEvent e) {
			paddle.setFilled(true);
			paddle.setColor(Color.BLACK);
			double xpaddle= e.getX();
			double ypaddle= getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
			if (e.getX()>getWidth()-60) {
				xpaddle=getWidth()-60;
		}
			paddle.setLocation(xpaddle, ypaddle);
			add(paddle);
	}
	

	    //Create the ball. I can't see why the ball is missing...
		public void ball() {
			GOval ball= new GOval((getWidth()-BALL_RADIUS)/2, (getHeight()-BALL_RADIUS)/2, BALL_RADIUS, BALL_RADIUS);
			ball.setFilled(true);
			ball.setColor(Color.BLACK);
			add(ball);
			vy=VELOCITY_Y;
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			int turnsPlayed =0;
			// Set three turns.
			while (turnsPlayed< NTURNS) {
				ball.move(vx, vy); 
				// When the ball passes bottom of canvas, the game ends and starts with a new ball.
				if (ball.getY()>getHeight()) {
					pause(DELAY);
					turnsPlayed++;
					add(ball);
					vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
					if (rgen.nextBoolean(0.5)) vx = -vx;
	                ball.move(vx, vy);
					
				// Now the ball is not passing bottom of canvas. 
			}else {
					// if the ball hits a brick, the break will be eliminated.
					collisionCheck(ball);
					//if the ball hits a wall or the paddle, it will bounce back.
					wallPaddleCheck (ball);
					if (NBrick==0) break;
			}
		}
		
	}
		
		//check the four corners of the outer square of the ball to see if it collides with something.
		private void collisionCheck(GOval object) {
			vy= VELOCITY_Y;
			if (NBrick!=0) {
				
				// When the top left corner hits a brick.
				if (getElementAt(object.getX(),object.getY())!= null&& getElementAt(object.getX(),object.getY())!= paddle ) {
					vx=-vx;
					remove(getElementAt(object.getX(),object.getY()));
					NBrick= NBrick--;
			}
					
			
			    // When right bottom corner hits a brick.
				if (getElementAt(object.getX()+BALL_RADIUS, object.getY()+BALL_RADIUS)!=null && getElementAt(object.getX(),object.getY())!= paddle) {
					vx=-vx;
					remove(getElementAt(object.getX()+BALL_RADIUS,object.getY()+BALL_RADIUS));
					NBrick=NBrick--;
			}
		
				// When top right corner hits. Similar to the case of top left corner.
				if (getElementAt(object.getX()+BALL_RADIUS,object.getY())!= null && getElementAt(object.getX()+BALL_RADIUS,object.getY())!= paddle) {
					vy=-vy;
					remove(getElementAt(object.getX()+BALL_RADIUS,object.getY()));
					NBrick=NBrick--;
			}
			
				// When left bottom corner hits.
				if (getElementAt(object.getX(), object.getY()+BALL_RADIUS)!=null && getElementAt(object.getX(), object.getY()+BALL_RADIUS)!= paddle) {
					vy=-vy;
					remove(getElementAt(object.getX(), object.getY()+BALL_RADIUS));
					NBrick=NBrick--;
			}

		}
	}
			
		
		// Check if the ball hits a wall or the paddle.
		private void wallPaddleCheck(GOval object) {
			// When the ball hits the left or the right wall, the vy changes sign.
			if (object.getX()==0 || object.getX()>= getWidth()-2*BALL_RADIUS ) {
				vy=-vy;
		}
			// When the ball hits the top wall, vx changes sign.
			if (object.getY()==0 || getElementAt(object.getX(), object.getY()+2*BALL_RADIUS)==paddle) {
				vx=-vx;
		}
	}

}
