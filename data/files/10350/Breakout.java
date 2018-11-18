/*
 * File: Breakout.java
 * -------------------
 * Name:Kevin Palma
 * Section Leader:Garrick
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

	//List of variables and objects for the program. 
	private GRect paddle;
	private GOval ball;
	private double vx;
	private double vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double brickCounter = NBRICK_ROWS*NBRICK_COLUMNS;
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		playGame();
		if(brickCounter == 0) {
			remove(ball);
			winner();
		}else {
			remove(ball);
			gameOver();
		}
	}
	public void mouseMoved(MouseEvent e) {
		if((e.getX()<(getWidth()-PADDLE_WIDTH)) && (e.getX()>(0))){
			double x = (e.getX());
			double y = CANVAS_HEIGHT-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
			paddle.setLocation(x, y);
		}
	}
	private void setUpGame() {
		addBricks();
		addPaddle();
		addMouseListeners();
	}

	private void addBricks() {
		for(int row = 0; row<NBRICK_ROWS; row ++) {
			for(int col = 0; col < NBRICK_COLUMNS; col++) { 
				//set x & y, first we need a variable to help center the bricks. 
				double canvasMidX = CANVAS_WIDTH/2;
				/*
				 * set x first by adjusting for the number of columns, brick width, and spacing 
				 * or taking out the max value (accounting for the fence post)
				 * Then add on value based on the round of the for loop we are on
				 */
				double x = (canvasMidX)-((NBRICK_COLUMNS*BRICK_WIDTH)/2)-
						(((NBRICK_COLUMNS-1)*BRICK_SEP)/2)+(col*BRICK_SEP)
						+(col*BRICK_WIDTH);
				//get Y by starting with the offset, then adjust further based on the row. 
				double y = BRICK_Y_OFFSET +(row*BRICK_HEIGHT)+(row*BRICK_SEP);
				//Make GRect. 
				GRect brick = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				//Set color based on the remainder of the row when divided by 10. 0-1 red, 2-3 orange, ...etc. 
				if(row %10 <2) {
					brick.setColor(Color.RED);
				}
				else if(row %10 <4) {
					brick.setColor(Color.ORANGE);
				}
				else if(row %10 <6) {
					brick.setColor(Color.YELLOW);
				}
				else if(row %10 <8) {
					brick.setColor(Color.GREEN);
				}
				else if(row %10 <10) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}
	private void addPaddle() {
		//set x & y.
		double x = (CANVAS_WIDTH/2)- (PADDLE_WIDTH/2);
		double y = (CANVAS_HEIGHT)- (PADDLE_Y_OFFSET)-(PADDLE_HEIGHT);
		//Made the paddle available to the whole program. 
		paddle = new GRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}
	private void addBall() {
		//set x & y.
		double x = (CANVAS_WIDTH/2)-(2*BALL_RADIUS);
		double y = (CANVAS_HEIGHT/2)-(2*BALL_RADIUS);
		//make ball an instance object. 
		ball = new GOval(x,y,(BALL_RADIUS*2),(BALL_RADIUS*2));
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);	
		//Sets the balls velocity and then waits for the click to start. 
		ballVelocity();
		addMouseListeners();
		waitForClick();

	}
	//This method will set the velocity of the ball when called. 
	private void ballVelocity() {
		//from handout, vy is constant. 
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}
	//MileStone 1: Setup complete, Added bricks, paddle, and ball with velocity. 
	/*Make ball respond to top, left and right walls. If it hits the bottom wall, 
	 *we want to go to the next turn.
	 */
	private void playGame() {
		for(int i=0;i<NTURNS;i++) {
			addBall();
			while(true) {
				bounce();
				if(ball.getY()>=CANVAS_HEIGHT) {
					break;
				}
				if(brickCounter==0) {
					return;
				}
			}
		}
	}
	private void bounce() {
		bounceOffWall();
		bounceOffObject();
	}
	private void bounceOffWall() {
		ball.move(vx, vy);
		//Checks left condition first, then right
		if((ball.getX()-vx <=0 && vx<0)||(ball.getX()+ vx >= (2*BALL_RADIUS + CANVAS_WIDTH)&& vx>0)) {
			vx=-vx;
		}
		//only the top wall
		if((ball.getY()-vy <=0 && vy<0)) {
			vy=-vy;
		}
	}
	private void bounceOffObject() {
		GObject collider = getCollidingObject();
		if(collider == paddle) {
			//checks for top and bottom of paddle. 
			if(ball.getY()>=getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT-(BALL_RADIUS*2) && ball.getY()<getHeight()-PADDLE_Y_OFFSET) {
				vy=-vy;
			}
			/*If it collides with anything else, it will be removed. 
			 *This will also keep track of the number of bricks left.
			 *when the counter hits zero we will exit the game and print winner.  
			 */
		}else if(collider != null){
			remove(collider);
			brickCounter--;
			vy=-vy;
		}
		pause(DELAY);
	}
	//Return element at the x & y, return null if no object.
	//Checking the four corners of the ball.
	private GObject getCollidingObject() {
		if((getElementAt(ball.getX(),ball.getY()))!=null){
			return getElementAt(ball.getX(),ball.getY());
		}
		else if (getElementAt((ball.getX()+(2*BALL_RADIUS)), ball.getY())!= null){
			return getElementAt((ball.getX()+(2*BALL_RADIUS)), ball.getY());
		}
		else if(getElementAt((ball.getX()), ball.getY()+(2*BALL_RADIUS))!= null){
			return((getElementAt((ball.getX()), ball.getY()+(2*BALL_RADIUS))));
		}
		else if(getElementAt((ball.getX()+(2*BALL_RADIUS)), ball.getY()+(2*BALL_RADIUS))!= null){
			return((getElementAt((ball.getX()+(2*BALL_RADIUS)), ball.getY()+(2*BALL_RADIUS))));
		}else {
			return null;
		}
	}
	private void winner() {
		GLabel winner = new GLabel ("You Win!!");
		double x= (CANVAS_WIDTH-winner.getWidth())/2;
		double y= (CANVAS_HEIGHT-winner.getHeight())/2;
		winner.setLocation(x, y);
		add(winner);
	}
	private void gameOver() {
		GLabel gameOver =new GLabel ("Game Over");
		double x= (CANVAS_WIDTH-gameOver.getWidth())/2;
		double y= (CANVAS_HEIGHT-gameOver.getHeight())/2;
		gameOver.setLocation(x, y);
		add(gameOver);
	}
}



