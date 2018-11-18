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

	// List of instance variables
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();	
	private double vx;
	private double vy;
	private int count=100;
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private GLabel bricksLeft = new GLabel("BRICKS LEFT: "+count); 
	private double totalSpeed=0;
	private GLabel ballSpeed = new GLabel("CURRENT SPEED: "+totalSpeed); 

	/* Extensions are:
	 * Changing velocity
	 * Noise when hitting bricks
	 * Count of number of bricks left in top left corner
	 * Total velocity printed on top right
	 */
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		buildBricks();
		buildPaddle();
		for (int attempts = 0; attempts < NTURNS; attempts++) {
			remove(bricksLeft);
			remove(ballSpeed);
			createBall();
			moveBall();
		}
		if(count>0) {
			losingMessage();
		}
	}
	/* Wait for click to move ball
	 * Set velocity in the x and y directions
	 * bricksLeft displays number of bricks left in the top left corner
	 * currentSpeed displays the speed of the ball in the top right corner
	 * bounce deals with what to do after a collision
	 * if statements change the direction of the ball after hitting walls
	 * Velocity increases a bit after hitting walls to make the game gradually harder
	 * If the ball hits the bottom wall remove ball and break loop
	 * If there are zero bricks left remove everything off screen, write a winning message and break loop
	 * Final two statements keep the labels updating by deleting the previous ones*/
	private void moveBall() {
		waitForClick();
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy=VELOCITY_Y;
		while(true) {
			bricksLeftLabel();
			currentSpeedLabel();
			pause(DELAY);
			ball.move(vx, vy);
			bounce();
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -1.01*vx;
			}
			if(hitTopWall(ball)) {
				vy = -1.02*vy;
			}
			if(hitBottomWall(ball)) {
				remove(ball);
				break;
			}
			if(count==0) {
				remove(ball);
				remove(ballSpeed);
				remove(bricksLeft);
				winningMessage();
				break;
			}
			remove(bricksLeft);
			remove(ballSpeed);
		}
	}
	// Write "You lose" in red the in center of the screen
	private void losingMessage() {
		GLabel lose = new GLabel("YOU LOSE"); 
		lose.setFont("Impact-100");
		lose.setColor(Color.RED);
		add(lose,getWidth()/2-lose.getWidth()/2, getHeight()/2);
	}

	// Write "You lose" in red the in center of the screen	 
	private void winningMessage() {
		GLabel win = new GLabel("YOU WIN!");
		win.setFont("Impact-90");
		win.setColor(Color.BLUE);
		add(win,getWidth()/2-win.getWidth()/2, getHeight()/2);
	}

	/* Build rows and columns of bricks using for loops
	 * double x and double y find where the first brick should be placed for the whole thing to be centered
	 * if statements define what color each row should be */
	private void buildBricks () {
		for (int row = 1; row <= NBRICK_ROWS; row++) {
			for (int column = 0; column < NBRICK_COLUMNS; column++) {
				double x=BRICK_SEP+column*(BRICK_WIDTH+BRICK_SEP)+BRICK_SEP/2;
				double y=row*(BRICK_HEIGHT+BRICK_SEP)+BRICK_Y_OFFSET;
				GRect brick = new GRect(x,y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick);
				if (row<=2) {
					brick.setColor(Color.RED);
				}
				if (row>2 && row<=4) {
					brick.setColor(Color.ORANGE);
				}
				if (row>4 && row<=6) {
					brick.setColor(Color.YELLOW);
				}
				if (row>6 && row<=8) {
					brick.setColor(Color.GREEN);
				}
				if (row>8 && row<=10) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}

	//Build paddle in the bottom center of the screen
	private void buildPaddle () {
		double x= getWidth()/2-PADDLE_WIDTH/2;
		double y=getHeight()-PADDLE_Y_OFFSET;
		paddle = new GRect(x,y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*Find where the mouse is
	Place paddle so that its center follows the mouse
	if statement ensures that the paddle does not move off screen*/
	public void mouseMoved(MouseEvent e) {
		double x = e.getX()-PADDLE_WIDTH/2;
		double y=getHeight()-PADDLE_Y_OFFSET;
		if (x+PADDLE_WIDTH<getWidth()&&x>0) {
			add(paddle,x, y);
		}
	}

	//Draw ball on top of paddle
	private void createBall () {
		double x= getWidth()/2-BALL_RADIUS;
		double y=getHeight()/2-BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
	}
	//booleans define where the walls are
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	/* Find the coordinates of the corners of the ball
	 * Find if there is a collision at each corner of the ball
	 * If there is no collision at any corner return null */
	private GObject getCollidingObject() {
		double leftX=ball.getX();
		double rightX=ball.getX()+(2*BALL_RADIUS);
		double topY=ball.getY();
		double bottomY=ball.getY()+(2*BALL_RADIUS);
		GObject topLeft = getElementAt(leftX,topY);
		GObject topRight = getElementAt(rightX,topY);
		GObject bottomLeft = getElementAt(leftX,bottomY);
		GObject bottomRight = getElementAt(rightX,bottomY);
		if(topLeft!= null) {
			return topLeft;
		}
		else if(topRight!= null) {
			return topRight;	
		}
		else if(bottomLeft!= null) {
			return bottomLeft;	
		}
		else if(bottomRight!= null) {
			return bottomRight;
		}
		return null;
	}

	/* If the collider is not a paddle, null or any label then it is a brick
	 * When a brick is hit, remove it, play a sound, switch the velocity sign and decrease brick count by one
	 * Velocity increases slightly to increase difficulty as game progresses
	 * If the collider is a paddle switch directions and use absolute value to remove sticky paddle bug*/
	private void bounce() {
		GObject collider = getCollidingObject();
		if (collider != paddle && collider!=null && collider!=ballSpeed && collider!=bricksLeft) {
			remove(collider);
			bounceClip.play();
			vy=-1.01*vy;
			count--;
		}
		if (collider == paddle) {
			vy=-1*Math.abs(vy);
		}
	}

	//Extension writes how many bricks are left 
	//Placed in the top left corner
	private void bricksLeftLabel() {
		bricksLeft = new GLabel("BRICKS LEFT: "+count); 
		bricksLeft.setFont("Courier-16");
		bricksLeft.setColor(Color.BLACK);
		add(bricksLeft,0,bricksLeft.getAscent());
	}

	//Define the total speed and round it to two decimal places
	//Write the speed in a label on the top right of the screen
	private void currentSpeedLabel() {
		totalSpeed=Math.sqrt(vx*vx+vy*vy);
		totalSpeed=Math.round(totalSpeed*Math.pow(10, 2))/Math.pow(10, 2);
		ballSpeed = new GLabel("CURRENT SPEED: "+totalSpeed); 
		ballSpeed.setFont("Courier-16");
		ballSpeed.setColor(Color.BLACK);
		add(ballSpeed,getWidth()-ballSpeed.getWidth(),ballSpeed.getAscent());
	}
}

