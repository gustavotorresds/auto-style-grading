/*
 * File: Breakout.java
 * -------------------
 * Name: Andrew Bempah
 * Section Leader: Vineet Kosaraju 
 * 
 * The objective of this program is to replicate the popular game of breakout where the user has 
 * a preset number of turns to use a paddle and bouncing ball to get rid of the colored bricks on the screen
 * 
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
	// Number of rows of bricks

	
	private double vx, vy;
	
	double startX;
	double startY;
	int bricksLeft = NBRICK_ROWS*NBRICK_COLUMNS ;
	double paddleYPosition=CANVAS_HEIGHT-PADDLE_Y_OFFSET;
	private GRect paddle= new GRect(startX,paddleYPosition,PADDLE_WIDTH, PADDLE_HEIGHT);
	private GOval ball = new GOval((CANVAS_WIDTH/2)-BALL_RADIUS, (CANVAS_HEIGHT/2)-BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2); 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	/*
	 * The run method controls the setup of the game and how many times the game runs
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		for(int i =0;i<NTURNS; i++){
			setUpPlay();
			playBreakout();
			if(bricksLeft==0){
				removeAll();
				GLabel labelText= new GLabel("Congrats You won!");
				add(labelText);
				break;
			}
			if(bricksLeft>0){
				GLabel labelText= new GLabel("Sorry you Lose!");
				add(labelText);

			}
			
		}

		
	
	}

	private void setUpPlay() {
		for(int row=0; row<NBRICK_ROWS;row++){
			initBrick(row);
			for(int column=1; column<NBRICK_COLUMNS; column++){
				layDownRow(row, column);
			}
		}
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle, getWidth()/2,paddleYPosition);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
	}
// PreCOndition: The first column brick has been layed
// Post Condition: By the time this method has been called all the times it needs to fill a row, the row will be complcomplete
	
	
	private void layDownRow(int row, int column) {
		GRect brick= new GRect(startX+((BRICK_SEP+BRICK_WIDTH)*column),startY,BRICK_WIDTH, BRICK_HEIGHT);	
		adjustBrickColor(row,brick);
		add(brick);	
		
	}
	// PreCOndition: EMpty Canvas
	// Post Condition: The first column will be laid
		
	private void initBrick(int t) {
		GRect brick= new GRect(BRICK_SEP,BRICK_Y_OFFSET+(t*(BRICK_HEIGHT+BRICK_SEP)),BRICK_WIDTH, BRICK_HEIGHT);
		adjustBrickColor(t,brick);
		add(brick);
		startX= brick.getX();
		startY= brick.getY();
		
		
	}
	// This method takes in the rownumber and brick object and adjusts its color
	private void adjustBrickColor(int rowNumber, GRect brickColor) {
		if(rowNumber<2) {
			brickColor.setFillColor(Color.RED);
			brickColor.setFilled(true);
		}else if(rowNumber<4){
			brickColor.setFillColor(Color.ORANGE);
			brickColor.setFilled(true);
		}else if(rowNumber<6){
			brickColor.setFillColor(Color.YELLOW);
			brickColor.setFilled(true);
		}else if(rowNumber<8){
			brickColor.setFillColor(Color.GREEN);
			brickColor.setFilled(true);
		}else if(rowNumber<10){
			brickColor.setFillColor(Color.CYAN);
			brickColor.setFilled(true);
		}	
	}
	
	public void mouseMoved(MouseEvent e) {
		double xCoordinate =e.getX();
		//I put the update paddle inside the mouse moved event so that whenever the mouse is tracked 
		// the x coordinates can be adjusted on the paddle
		add(paddle, xCoordinate,paddleYPosition);
		if(e.getX()+PADDLE_WIDTH>=getWidth()) {
			add(paddle,getWidth()-PADDLE_WIDTH ,paddleYPosition);
		}

	}
/*
 * Pre Condition: The entire playing area is setup and the ball moves and user can play
 * Post Condition: Either all the balls have been eliminated or the ball has hit the bottom of the screen
 */

	private void playBreakout() {
		vx = rgen.nextDouble(1.0, 5.0);
		vy= 5.0;
		if (rgen.nextBoolean(0.5)) vx = -vx;

		while(true) {
			GObject collider = getCollidingObject();
			ball.move(vx, vy);
			pause(DELAY);
			if(checkWestEast()) {
				vx=-vx;
			}
			if(checkNorth()) {
				vy=-vy;
			}
			if(checkSouth()) {
				ball.setLocation((getWidth()-PADDLE_WIDTH)/2, (CANVAS_HEIGHT/2)-BALL_RADIUS);
				pause(DELAY*100.0);
				break;
			}
			if(collider == paddle) {
				if(vy<0) {
					vy=vy;
				}else if(vy>0) {
					vy=-vy;
				}
			}else if(collider != null){
				remove(collider);
				vy=-vy;
				bricksLeft--;
			}
			if(bricksLeft ==0) {
				break;
			}
		}
		
		
	}
// This method checks to see if the south wall has been hit
	private boolean checkSouth() {
		if(ball.getY()+2*BALL_RADIUS>=getHeight()) {
			return true;
		}
		return false;
	}
	// This method checks to see if the east and west wall have been hit
	private boolean checkWestEast() {
		if((ball.getX()+2*BALL_RADIUS>=getWidth()) || (ball.getX()<=0)) {
			return true;
		}
		return false;
	}
	// This method checks to see if the north wall has been hit

	private boolean checkNorth() {
		if(ball.getY()<=0) {
			return true;
		}
		return false;
	}
// This method checks all four corners of the ball to see if it has come in contact with 
// anything, if it does come in contact with something it returns the Gobject at that position
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(),ball.getY()) != null) {
			return getElementAt(ball.getX(),ball.getY());
		}else if(getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()) != null){
			return getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY());
		}else if(getElementAt(ball.getX(),ball.getY()+2*BALL_RADIUS) != null){
			return getElementAt(ball.getX(),ball.getY()+2*BALL_RADIUS);
		}else if(getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS) != null){
			return getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS);
		}else {
			return null;
		}
		
		
	}



}
