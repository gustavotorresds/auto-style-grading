/*
 * File: Breakout.java
 * -------------------
 * Name: Gunner Dongieux
 * Section Leader: Ruiqi Chen
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

	// Number of rows of bricks for each color 
	public static final int NBRICK_COLOR = 2;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			((CANVAS_WIDTH) - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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
	public static final double BALL_RADIUS = 2*10;

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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	//instance variable
	private double bricksleft=NBRICK_COLUMNS*NBRICK_ROWS;
	private double vx,vy;
	private GRect paddle = null;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GObject collider = null;
	private GRect brick = null;
	private GLabel lives = null;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();
		addMouseListeners();
		for (int i=0;i<NTURNS;i++) {
			lives = new GLabel("Lives:"+(NTURNS-i));
			lives.move((getWidth()/2)-(lives.getWidth()/2), getHeight()-5);
			lives.setFont("Cooper Black");
			add (lives);
			waitForClick();
			playGame();
			//resets game
			clearGame();
			setUpGame();
		}
		if(bricksleft!=0) {
			GLabel over= new GLabel ("Game Over");
			over.move((getWidth()/2)-(over.getWidth()/2), 50);
			over.setFont("Cooper Black");
			add (over);	
		}
	}

	/*
	 * Makes (NBRICK_ROWS) rows of multicolored bricks
	 * (NBRICK_COLUMNS) bricks in each row
	 * 2 rows per color
	 */
	private void setUpGame() {
		//sets position to start rows (from top)
		double x=0;
		double y=BRICK_Y_OFFSET;
		makeAllBricks(x,y);
		makePaddle();
		//ball starts in middle,
		//space above paddle = paddle height + 1
		ball = new GOval ((getWidth()/2),(getHeight()/2),BALL_RADIUS,BALL_RADIUS);
		ball.setFilled(true);
		add (ball);
		//resets brick count
		bricksleft = NBRICK_COLUMNS*NBRICK_ROWS;
	}
	/*
	 * basically undoes the set up
	 * similar code, removes instead
	 */
	private void clearGame() {
		for(int y=0;y<getHeight();y++) {
			//clear brick row
			for(int x=0;x<getWidth();x++) {
				GObject removed=getElementAt(x,y);
				if (removed!=null) {
					remove (removed);
				}
			}
		}
	}	

	//Makes brick configuration
	private void makeAllBricks(double x,double y) {
		x+=BRICK_SEP/2;
		for(int i=0;i<NBRICK_ROWS;i++) {
			makeBrickRow(x,y);
			//moves y down to start of next row
			y+=(BRICK_SEP+BRICK_HEIGHT);
		}
	}
	//makes a row of bricks
	private void makeBrickRow(double x,double y) {
		for(int i=0;i<NBRICK_COLUMNS;i++) {
			x+=BRICK_SEP;
			makeBrick(x,y);
			//moves to next brick
			x+=(BRICK_WIDTH);
		}
	}
	/*
	 * Makes a brick
	 * Fills with color based on which row it is in
	 */
	private void makeBrick(double x,double y) {
		brick = new GRect (x,y,BRICK_WIDTH,BRICK_HEIGHT);
		brick.setFilled(true);

		if(y<(BRICK_Y_OFFSET+1*((NBRICK_COLOR*BRICK_SEP)+(NBRICK_COLOR*BRICK_HEIGHT)))) {
			brick.setColor(Color.RED);
		} else if (y<(BRICK_Y_OFFSET+2*((NBRICK_COLOR*BRICK_SEP)+(NBRICK_COLOR*BRICK_HEIGHT)))) {
			brick.setColor(Color.ORANGE);
		} else if (y<(BRICK_Y_OFFSET+3*((NBRICK_COLOR*BRICK_SEP)+(NBRICK_COLOR*BRICK_HEIGHT)))) {
			brick.setColor(Color.YELLOW);
		} else if (y<(BRICK_Y_OFFSET+4*((NBRICK_COLOR*BRICK_SEP)+(NBRICK_COLOR*BRICK_HEIGHT)))) {
			brick.setColor(Color.GREEN);
		} else if (y<(BRICK_Y_OFFSET+5*((NBRICK_COLOR*BRICK_SEP)+(NBRICK_COLOR*BRICK_HEIGHT)))) {
			brick.setColor(Color.CYAN);
		} else 
			brick.setColor(Color.BLACK);
		add (brick);
	}
	private void makePaddle() {
		paddle = new GRect ((getWidth()/2)-(PADDLE_WIDTH/2),(getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET),PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle);

	}
	public void mouseMoved (MouseEvent e) {
		//moves the paddle along x plane where the mouse is
		double paddleX=e.getX();
		double paddleY=getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET;
		if((paddleX>PADDLE_WIDTH/2)&&(paddleX<getWidth()-PADDLE_WIDTH/2)) {
			paddle.setLocation(paddleX- (PADDLE_WIDTH/2),paddleY);
			/*
			 * repeated from run method
			 * fixes a bug where ball wont detect paddle
			 * while moving paddle
			 */
			if (ball.getY() < 0) {
				vy=-vy;
			}
			//checks for paddle collisions
			if (collider==paddle && vy>0) {
				vy=-vy;
			}
		}
	}
	private void playGame() {
		//sets ball motion 
		vx=rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy=VELOCITY_Y;
		//plays game 3 times
		//sets ball movement 
		while(! turnOver() && bricksleft!=0) {
			//ball bouncing top
			if (ball.getY() < 0) {
				vy=-vy;
			}
			if (collider==paddle && vy>0) {
				vy=-vy;
			}	
			//ball hitting bricks
			if (collider!=null && collider!= paddle && collider != lives) {
				remove (collider);
				bricksleft-=1;
				vy=-vy;
			}	
			//ball bouncing sides
			if (ball.getX() < 0||ball.getX() > getWidth() - ball.getWidth()) {
				vx=-vx;
			}
			//moves ball
			ball.move(vx,vy);
			pause(DELAY);
			//checks for collider in new position
			getCollidingObject();
			//checks if game is won
			if (bricksleft==0) {
				GLabel won= new GLabel ("You Win!");
				won.move((getWidth()/2)-(won.getWidth()/2), 50);
				won.setFont("Cooper Black");
				add (won);
				waitForClick();
				break;
			}
		}
	}
	/*
	 * checks four points around the ball
	 * if an object is at any of those points
	 * will return a GObject
	 * otherwise, returns null
	 */
	private GObject getCollidingObject() {
		collider = getElementAt(ball.getX(), ball.getY());
		//checks to see if there is a collider
		//makes sure collider is not the lives bar at the bottom
		if(collider !=null && collider != lives){
			return collider;
		} else {
			collider = getElementAt(ball.getX()+BALL_RADIUS, ball.getY()+BALL_RADIUS);
			if(collider !=null && collider != lives){
				return collider;
			}	else {
				collider = getElementAt(ball.getX()+BALL_RADIUS, ball.getY());
				if(collider !=null && collider != lives){
					return collider;
				} else {
					collider = getElementAt(ball.getX(), ball.getY()+BALL_RADIUS);
					if(collider !=null && collider != lives){
						return collider;
					}
				}
			}
		}
		return collider;
	}
	//checks if ball goes through bottom wall
	public boolean turnOver() {
		return (ball.getY() > getHeight());
	}	
}
