/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file allows the user to play the breakout game
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
	public static final double PADDLE_WIDTH = 420;
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
//	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;


	//INSTANCE VARIABLES
	private GRect paddle;
	private double vx, vy;
	private RandomGenerator rgen=RandomGenerator.getInstance();
	private GOval ball;
	private int collisionCounter=NBRICK_COLUMNS*NBRICK_ROWS;
	private int lives=3;




	public void run() {
		addMouseListeners();
		setup();
		playGame();

	}

	/*
	 * This sets up the game for the player to use. This includes creating
	 * the bricks, paddle, and ball
	 */
	private void setup() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		constructBricks();
		paddle=createPaddle();
		addPaddleToCenter();
		createBall();

	}


	/*
	 * This method creates the correct amount of bricks and centers the block of
	 * bricks in the middle of the screen.
	 */
	private void constructBricks() {
		double initialX=getWidth()-(BRICK_WIDTH*NBRICK_ROWS+BRICK_SEP*(NBRICK_ROWS+1));
		for (int col=0; col<NBRICK_COLUMNS; col++){
			for (int row=0; row<NBRICK_ROWS; row++){
				double x=initialX+row*(BRICK_WIDTH+BRICK_SEP);
				double y=BRICK_Y_OFFSET+col*(BRICK_HEIGHT+BRICK_SEP);
				createBrick(col, x,y);
			}
		}

	}

	/*
	 * This method creates the bricks, properly colors each row and adds
	 * them to the screen.
	 */
	private void createBrick(int col, double x, double y) {

		GRect rect=new GRect (x,y, BRICK_WIDTH,BRICK_HEIGHT);
		rect.setFilled(true);

		//the mod operator is used to properly color each of the rows
		if (col%10==1||col%10==0) {		//rows 1 and 2
			rect.setColor(Color.RED);
		}
		if (col%10==3 || col%10==2) {	//rows 3 and 4
			rect.setColor(Color.ORANGE);
		}
		if (col%10==5||col%10==4) {		//rows 5 and 6
			rect.setColor(Color.YELLOW);
		}
		if (col%10==7 || col%10==6) {	//rows 7 and 8
			rect.setColor(Color.GREEN);
		}
		if (col%10==9||col%10==8) {		//rows 9 and 10
			rect.setColor(Color.CYAN);
		}
		add(rect);
	}

	/*
	 * This method creates a paddle
	 */
	private GRect createPaddle() {

		GRect paddle= new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		return (paddle);
	}
	/*
	 * This method centers the paddle in the middle of the screen to start
	 */
	private void addPaddleToCenter() {
		double xCenter=(getWidth()-PADDLE_WIDTH)/2;
		double yCenter=getHeight()-PADDLE_Y_OFFSET;
		paddle.setLocation(xCenter, yCenter);
		add (paddle);
	}

	/*
	 * This method runs when the mouse moves and it works to move the
	 * paddle back and forth while not letting it exceed the boundaries
	 * of the canvas
	 */
	public void mouseMoved(MouseEvent e) {
		double x=e.getX()-PADDLE_WIDTH*1/2;
		double y=getHeight()-PADDLE_Y_OFFSET;
		if (x<0) {
			x=0;
		}
		if(x>=getWidth()-PADDLE_WIDTH) {
			x=getWidth()-PADDLE_WIDTH;
		}
		paddle.setLocation(x,y);
	}
	/*
	 * This method creates the ball
	 */
	private GOval createBall() {
		double xCenter=getWidth()/2-BALL_RADIUS;
		double yCenter=getHeight()/2-BALL_RADIUS;
		ball=new GOval(xCenter, yCenter, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add (ball);
		return ball;
	}

	/*
	 * The method allows the user to play the game of breakout
	 */
	private void playGame() {
		waitForClick();
		setVelocity();	
		while (lives>0) {
			collision();
			bouncingBall();
			if (collisionCounter<=0) {
				break;
			}
		}
	}

	/*
	 * This method sets both the x and y velocity. The x velocity
	 * is randomly generated, and the y velocity is fixed
	 */
	private void setVelocity() {
		vx=rgen.nextDouble(1.0,3.0);
		vy=VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) { 
			vx=-vx;
		}
	}

	/*
	 * This method moves the ball around the screen, without taking into
	 * account collisions with the bricks or paddle. However, if the ball
	 * hits the bottom wall, it will subtract a life
	 */

	private void bouncingBall() {

		if(hitTopWall()) {
			vy=-vy;
		}
		if(hitLeftWall()|| hitRightWall()){
			vx=-vx;
		}
		if(hitBottomWall() ) {
			remove(ball);
			lives--;
			//each time a life is lost, the ball is reset and the user can try again until user runs out of lives
			if (lives>0) {
				createBall();
				waitForClick();
			}
		}
		ball.move(vx, vy);
	//	pause(DELAY);
	}


	/*
	 * Checks to see if the ball hits the bottom wall 
	 */
	private boolean hitBottomWall() {
		return ball.getY()>getHeight()-ball.getHeight();
	}

	/*
	 * Checks to see if the ball hits the top wall 
	 */
	private boolean hitTopWall() {
		return ball.getY()<=0;
	}
	/*
	 * Checks to see if the ball hits the right wall 
	 */

	private boolean hitRightWall() {
		return ball.getX()>=getWidth()-ball.getWidth();
	}

	/*
	 * Checks to see if the ball hits the left wall 
	 */

	private boolean hitLeftWall() {
		return ball.getX()<=0;
	}
	
	/*
	 * This method says what to do once the ball encounters an object.
	 * Colliding with the paddle bounces the ball back up, and hitting
	 * a brick removes it and sends the ball back down 
	 */
	private void collision() {
		GObject collider=getCollidingObject();
		if (collider==paddle) {
			if (vy==Math.abs(vy)) {	//absolute value eliminates the sticky paddle bug
				vy=-vy;
			}

		}else if(collider!=null) {
			vy=-vy;
			remove(collider);
			collisionCounter--;	//each time a brick is hit, that information is stored
		}
	}

	/*
	 * This method detects if the ball collides with an object,
	 * either a brick or the paddle
	 */
	private GObject getCollidingObject() {
		GObject object;

		object=getElementAt(ball.getX(),ball.getY());
		if (object != null) {
			return object;
		}
		object=getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());

		if (object !=null) {	
			return object;
		}
		object=getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		if (object !=null) {	
			return object;
		}
		object=getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		if (object !=null){
			return object;
		}
		return null;
	}


}



























