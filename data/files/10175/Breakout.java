/*
 * File: Breakout.java
 * -------------------
 * Name: Alexis Ivec
 * Section Leader: Brahm
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
	public static final int NBRICK_COLUMNS = 1;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 3;

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
	public static final double DELAY = 1000.0 / 100.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//sets up the paddle
	public GRect paddle= new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);

	//sets up the ball
	public GOval ball=new GOval(BALL_RADIUS*2,BALL_RADIUS*2);

	//sets up random generator to determine which way ball bouncesRandomGenerator rgen = RandomGenerator.getInstance()
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//sets up the initial brick number
	private int brickCount = NBRICK_COLUMNS*NBRICK_ROWS;
	
	

	public void run() {
		addMouseListeners();
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();

		// t is the number of turns a player has before the game is over
		for (int t=0;t<NTURNS;t++) {
			if (brickCount != 0) {
				/** at the start of each turn, the player clicks once to set up the ball.
				 * this also gets rid of the previous ball at the bottom of the screen if the 
				 * player is on a new turn
				 */
				waitForClick();
				setUpBall();
				// by clicking again, the player starts each turn by setting the ball into motion
				waitForClick();
				moveBall();
			}
		}
		remove(ball);
		GRect banner=new GRect(getWidth()/2,getHeight()/2);
		banner.setFilled(false);
		if (brickCount !=0) {
			add(createLabel("You Lose.",banner));
		}else {
			add(createLabel("You Win!",banner));
		}
	}
	
	//creates a label to be shown at the end of the game.
	private GLabel createLabel(String str,GRect r) {
		GLabel label= new GLabel(str,r.getX()+ r.getWidth()/2,r.getY()+r.getHeight()/2);
		label.setLocation(r.getX()+ r.getWidth()/2-label.getWidth()/2,r.getY()+r.getHeight()/2+label.getAscent()/2);
		return label;
	}

	/**
	 * returns if the given ball should bounce off of the 
	 * left wall of the window
	 */
	private boolean hitLeftWall(GOval ball, double vx) {
		if (vx>0) return false;
		return ball.getX()<=0;
		
	}

	/**
	 * returns if the given ball should bounce off of the 
	 * left wall of the window
	 */
	private boolean hitRightWall(GOval ball, double vx) {
		if (vx<0) return false;
		return ball.getX()>=getWidth()-BALL_RADIUS*2;

	}

	/** this method sets up the initial world of breakout by placing the colored 
	 * bricks and the paddle
	 */
	private void setUpGame() {
		setUpBricks();
		setUpPaddle();
		
	}

	//moves the ball and determines what to do when ball encounters walls, the paddle, and bricks
	private void moveBall() {
		double vy= 3.0;
		double vx= rgen.nextDouble (1.0,3.0);
		if (rgen.nextBoolean(.5)) vx=-vx;
		while (true) {
			
			//before ball moves again, it checks to see if it collided with an object
			getCollidingObject();
			ball.move (vx,vy);
			pause(DELAY);
			
			//below are methods of what the ball does if it hits a wall
			if (hitLeftWall(ball,vx)||hitRightWall(ball,vx)) {
				vx=-vx;
			}
			if (hitTopWall(ball,vy)) {
				vy=-vy;
			}
			
			/**if ball ever hits bottom wall, the turn is over, the ball remains at the 
			 * bottom of the wall until the player clicks to start a new turn 
			 */
			if (hitBottomWall(ball,vy)) {
				vy=0;
				vx=0;
				break;
			}
			
			GObject collider= getCollidingObject();
			if (collider == paddle) {
				if(ball.getY()+2*BALL_RADIUS<=paddle.getY()) {
					vx=-vx;
				}else {
					vy=-vy;
				}
			}else if (collider!= null) {
				remove(collider);
				vy=-vy;
				brickCount--;
				if (brickCount==0) {
					break;
				}
			}
		}
	}
	
	private GObject getCollidingObject() {
		//checks to see if there is a collision at any of the four corners of the ball
		if (getElementAt(ball.getX(),ball.getY())!=null) {
			return getElementAt(ball.getX(),ball.getY());
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY())!=null) {
			return getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY());
		} else if (getElementAt(ball.getX(),ball.getY()+2*BALL_RADIUS)!=null) {
			return getElementAt(ball.getX(),ball.getY()+2*BALL_RADIUS);
		} else if (getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS)!=null) {
			return getElementAt(ball.getX()+2*BALL_RADIUS,ball.getY()+2*BALL_RADIUS);
			//if there is not anything at the point, nothing is returned
		}else {
			return null;
		}
	}

	/**
	 * Returns whether or not the given ball should bounce off
	 * of the top wall of the window.
	 */
	
	private boolean hitTopWall(GOval ball, double vy) {
		if(vy > 0) return false;
		return ball.getY() <= 0;
		
	}
	
	/**
	 * Returns whether or not the given ball should bounce off
	 * of the bottom wall of the window.
	 */
	private boolean hitBottomWall(GOval ball, double vy) {
		if(vy < 0) return false;
		return ball.getY() > getHeight() - BALL_RADIUS*2;
		
	}

	/**sets up the ball at the start of each turn to be used to play the game 
	 * in the middle of the screen
	 */ 
	private void setUpBall() {
		ball.setFilled(true);
		add(ball,getWidth()/2-BALL_RADIUS,getHeight()/2-BALL_RADIUS);
		
	}

	//if mouse moves, the paddle moves with the mouse to match the x location
	public void mouseMoved (MouseEvent e) {
		double mouseX= e.getX();
		if(mouseX>=0+.5*PADDLE_WIDTH && mouseX<=getWidth()-.5*PADDLE_WIDTH) {
			paddle.setLocation(mouseX-(.5*PADDLE_WIDTH), getHeight()-PADDLE_Y_OFFSET);
		}
	}

	// this method sets up the paddle so that the player can play the game
	private void setUpPaddle() {
		paddle.setFilled(true);
		add(paddle,getWidth()/2-PADDLE_WIDTH/2,getHeight()-PADDLE_Y_OFFSET);

	}

	/** this method sets up the multiple rows of colored bricks evenly counted out
	 *  seen initially at the top of the game. The variable y is the number of doubled rows
	 *  there are. i.e y= 0 means that there is one block of a doubled row. if the number of 
	 *  rows called is an odd number, this method makes up for the odd row by adding a single
	 *  row at the beginning of the color sequence to make up for it
	 */
	private void setUpBricks() {
		
		/**used if there is an odd number of rows by first setting up a single row and then
		 * setting up the remaining rows as doubles
		 */
		if(isOdd(NBRICK_ROWS)) {
			setUpSingleColoredRow();
			for(int y=1;y<(NBRICK_ROWS/2)+1;y++) {
				setUpDoubleColoredRow(y);
			}
		}else{
			//if NBRICK_ROWS is even
			for (int y=0;y<(NBRICK_ROWS/2);y++) {
				setUpDoubleColoredRow(y);
			}
		}
	}

	//This method checks to see if a number is odd
	private boolean isOdd(int num) {
		// if the remainder of number divided by 2 is 0
		if(num%2 ==1) {
			return true;
		}
		return false;
	}

	//if only one row is available to be created, a red single row is created with the # of bricks in a row
	private void setUpSingleColoredRow() {

		//b== the number brick currently being added
		for (int b=0;b<NBRICK_COLUMNS; b++) {
			GRect brick= new GRect (findX(b),BRICK_Y_OFFSET,BRICK_WIDTH,BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(Color.RED);
			add(brick);
		}
	}

	/** finds the color that the game should implement using the integer describing how many
	 * far into the program of setUpBricks is.
	 * @return color needed to make the bricks. it implements the remainder function to 
	 * understand the number of doubled rows has been made in order to figure out the color.
	 */
	private Color findColor(int a) {
		if ((a*6)%5==0) {
			return Color.RED;
		} else if ((a*6)%5==1) {
			return Color.ORANGE;
		} else if ((a*6)%5==2) {
			return Color.YELLOW;
		}else if ((a*6)%5==3) {
			return Color.GREEN;
		}else

			/* the way that the division works, only even numbers and zeros are remainders
			 * so the last remaining value must be CYAN. without the else, the remainder for this
			 * would be 4.
			 */
			return Color.CYAN;
	}

	/**sets up a single color block row (two rows of a color) given the int y which is 
	 * is the number of doubled bricks
	 */
	private void setUpDoubleColoredRow(int y) {
		double b;
		//for two rows, set an identical colored row
		for (int r=0; r<2;r++) {
			//set up number of bricks in each column given by nbrick_columns
			for (b=0;b<NBRICK_COLUMNS; b++) {
				GRect brick= new GRect (findX(b),findY(y,r),BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(findColor(y));
				add(brick);

			}
		}
	}

	/** this method finds the y value needed to place the bricks for each double row using
	 *
	 * @param y is the number of doubled rows that has been placed
	 * @param r is the number row in the doubled row sequence that has been placed (ie the 
	 * first or second row of the doubled row)
	 * @return the y value to place the brick
	 */
	private double findY(int y,int r) {
		double yc = 0;
		//odd number rows accounts for the single red brick row at the beginning by shifting up one brick
		if(isOdd(NBRICK_ROWS)) {
			y--;
			yc=(BRICK_Y_OFFSET)+BRICK_HEIGHT+BRICK_SEP+((y+r)*(BRICK_HEIGHT+BRICK_SEP)+(y*(BRICK_HEIGHT+BRICK_SEP)));
			return yc;
		}
		yc=(BRICK_Y_OFFSET)+((y+r)*(BRICK_HEIGHT+BRICK_SEP))+(y*(BRICK_HEIGHT+BRICK_SEP));
		return yc;
	}

	/** this method finds the x value needed to place the bricks
	 * 
	 * @param b is the number given in setUpDoubleRow that is the number of bricks per row
	 * @return x value that is used to find where to put a brick
	 */
	private double findX(double b) {
		b= (getWidth()/2)-(BRICK_WIDTH+BRICK_SEP)*(.5*NBRICK_COLUMNS)+(b*(BRICK_WIDTH+BRICK_SEP));
		return b;

	}
}
