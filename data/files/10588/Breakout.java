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

import com.sun.xml.internal.bind.v2.runtime.Location;

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
	private int turns = NTURNS;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;



	GRect paddle = new GRect (getWidth()/2-PADDLE_WIDTH, getHeight()-PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);

	GOval oval = new GOval ((CANVAS_WIDTH/2-BALL_RADIUS), (CANVAS_HEIGHT/2-BALL_RADIUS), BALL_RADIUS, BALL_RADIUS);

	public void run() {

		// Set the window's title bar text
		setCanvasSize(CANVAS_WIDTH-BRICK_SEP, CANVAS_HEIGHT);	
		setTitle("CS 106A Breakout");
		//Place the bricks
		add(paddle);
		for(int i=NBRICK_ROWS; i>0; i--) {
			makelayer(i);
		}
		placeBall();

		//addMouseListeners();
		waitForClick();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;

		//
		while(turns > 0) {
			collision();
			moveBall();
		}



		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		//setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);	
	}


	//**making the rows of colored bricks**//


	private void makelayer(int level){
		for(int i = 0; i<NBRICK_COLUMNS; i++){
			GRect brick = new GRect ((BRICK_SEP+i*(BRICK_WIDTH+BRICK_SEP)), BRICK_Y_OFFSET + level*(BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT); 
			brick.setFilled(true);

			Color col = Color.BLACK;
			if (level == 1 || level == 2) col = Color.RED;
			if (level == 3 || level == 4) col = Color.ORANGE;
			if (level == 5 || level == 6) col = Color.YELLOW;
			if (level == 7 || level == 8) col = Color.GREEN;
			if (level == 9 || level == 10) col = Color.CYAN;

			brick.setColor(col);
			add(brick);
		}
	}

	//**making the paddle**//

	public void mouseMoved(MouseEvent event) { 
		// Java runs this when mouse is moved
		int x = event.getX();
		if(x <= getWidth()-PADDLE_WIDTH) {
			updatePaddle(x);
		}
	}

	private void updatePaddle(int g) {
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		paddle.setLocation(g, getHeight()-PADDLE_Y_OFFSET);
		remove (paddle);
		add(paddle);	

	}

	//** Creating a moving ball **//

	private void placeBall() {
		oval.setFilled(true);
		oval.setColor(Color.BLACK);
		add(oval);
	}

	public void mouseCliked(MouseEvent event) { 
		moveBall();
	}


	private void moveBall() {
		double Y = oval.getY();
		if (Y < 0) {
			vy = -vy;
		} else if(Y > getHeight()) {
			turns--;
			newTurn();
		}

		double X = oval.getX();
		if (X < 0) {
			vx = -vx;
		}

		if (X+(2*BALL_RADIUS) > getWidth()) {
			vx = -vx;
		}
		oval.move(vx, vy);
		oval.pause(DELAY);

	}


	public void collision(){
		if(getCollidingObject() != null) {
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				double Y = collider.getY();
				if( Y > getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT/2) {
					Math.abs(vy);
					vy = -vy;
					//** change direction **//
				}
			}else if (collider != null) {
				vy = -vy;
				remove(collider);
			}
		}
	} 


	private GObject getCollidingObject() {
		if(getElementAt(oval.getX(), oval.getY()) != null) {
			GObject collidingObject = getElementAt(oval.getX(), oval.getY());
			return(collidingObject);
		}
		else if(getElementAt(oval.getX(), oval.getY()+2*BALL_RADIUS) != null) {
			GObject collidingObject = getElementAt(oval.getX(), oval.getY()+2*BALL_RADIUS);
			return(collidingObject);
		}
		else if(getElementAt(oval.getX()+2*BALL_RADIUS, oval.getY()) != null) {
			GObject collidingObject = getElementAt(oval.getX()+2*BALL_RADIUS, oval.getY());
			return(collidingObject);
		}
		else if(getElementAt(oval.getX()+2*BALL_RADIUS, oval.getY()+2*BALL_RADIUS) != null) {
			GObject collidingObject = getElementAt(oval.getX()+2*BALL_RADIUS, oval.getY()+2*BALL_RADIUS);
			return(collidingObject);
		}
		return(null);
	}

	private void newTurn() {
		GLabel newTurnString = new GLabel("New Turn");
		for(int i=NTURNS; i>0; i--) {
			double labelX = (getWidth()/2-newTurnString.getWidth()/2);		
			double labelY = (getHeight()/2);		
			newTurnString.setLocation(labelX, labelY); 
			add(newTurnString);
			oval.setLocation(getWidth()/2-BALL_RADIUS/2, getHeight()/2);
			waitForClick();
			oval.move(vx, vy);
			oval.pause(DELAY);

		}
	}
}




