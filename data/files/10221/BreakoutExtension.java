/*
 * File: BreakoutExtension.java
 * -------------------
 * Name:Shiyu Li
 * Section Leader:Marilyn Zhang
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtension extends GraphicsProgram {

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
	
	//Instance variables
	private GRect paddle = null;
	private GPoint last = null;
	private GOval ball = null;
	private double vx , vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GObject collider;
	private static final int NUMBER_OF_BRICK = 100;
	private int counter=NUMBER_OF_BRICK;//keep track of the remaining number of bricks
	private GLabel message = null;
	private int point = 0;
	private GLabel score = null;
	
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		setUp();
		playGame();
		gameOver();
	
	}

	
	
	private void playGame() {
		waitForClick();//Game starts when mouse clicks;
		ballMoving();
	}


	/*
	 * Showing a message on the screen to tell the player whether game is over or he win.
	 */
	private void gameOver() {
		if(ball.getY()+BALL_RADIUS*2.0>=getHeight()) {
			message = new GLabel ("GAME OVER!");
			message.setLocation(getWidth()/2.0-message.getWidth()/2.0,getHeight()/2.0);
			add (message);
		}else if(counter == 0) {
			message = new GLabel ("CONGRATULATIONS!");
			message.setLocation(getWidth()/2.0-message.getWidth()/2.0,getHeight()/2.0);
			add( message);
		}
	}


	/*
	 * This method is to set up the initial settings
	 */
	private void setUp() {
		setUpBricks();
		createThePaddle();	
		creatTheBall();
	}

	/*
	 * This method is to make the ball bounce back whenever it hits an object, and if the object is a brick, 
	 * the brick disappears.
	 */
	private void checkCollision() {
		collider = getCollidingObject();
		if (collider != null) {
			vy=-vy;
			if ((collider != paddle)&&(collider != score)) {
			AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");//Add sounds
			bounceClip.play();
			remove (collider);
			counter= counter-1;//keep track of the remaining number of bricks
			point = point+1;
			showScore();
			}
		}
		
		
	}
	
	
	/*
	 * This method is to return the object that hit by either corner of the square in which 
	 * the ball is inscribed.
	 */
	private GObject getCollidingObject() {
		double ballX=ball.getX();
		double ballY=ball.getY();
		if(getElementAt(ballX, ballY) != null) {
			GObject obj = getElementAt(ballX, ballY);
			return obj;
		}
		else if (getElementAt(ballX+2.0*BALL_RADIUS, ballY) != null) {
			GObject obj = getElementAt(ballX+2.0*BALL_RADIUS, ballY);
			return obj;
		}
		else if(getElementAt(ballX, ballY+2.0*BALL_RADIUS) != null) {
			GObject obj = getElementAt(ballX, ballY+2.0*BALL_RADIUS);
			return obj;
		}
		else if(getElementAt(ballX+2.0*BALL_RADIUS,ballY+2.0*BALL_RADIUS) != null) {
			GObject obj=getElementAt(ballX+2.0*BALL_RADIUS,ballY+2.0*BALL_RADIUS);
			return obj;
		}
		else {
			return null;
		}
	}
		
	/*
	 * Get the ball bouncing around the world. 
	 */
	private void ballMoving() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		
		if (rgen.nextBoolean(0.5)) { //Make the random double negative half the time;
			vx = -vx;
		}
		while (gameIsNotOver()) {
			if ((ball.getX()+BALL_RADIUS*2.0>=getWidth()) || (ball.getX()<=0)){
				vx=-vx;
			}else if((ball.getY()+BALL_RADIUS*2.0>=getHeight()) || (ball.getY()<=0)){
				vy=-vy;
			}
			ball.move(vx, vy);
			checkCollision();
			pause(DELAY);
		}

	}

	
	/*
	 * Set up the terminating condition(the ball hit and the bottom wall or no more brick appears)
	 */
	private boolean gameIsNotOver() {
		return (ball.getY()+BALL_RADIUS*2.0<getHeight()) && (counter>0);
	}
	
	/*
	 * Showing score on screen, each brick collision add one more score
	 */
	private void showScore() {
			score = new GLabel ("SCORE = " + point, getWidth()-100,10);
			add(score);
		}

	private void creatTheBall() {
		ball = new GOval((getWidth()-BALL_RADIUS)/2.0,(getHeight()-BALL_RADIUS)/2.0,BALL_RADIUS*2.0,BALL_RADIUS*2.0);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	/*
	 * Set up a paddle in the middle of the bottom of the widow
	 */
	private void createThePaddle() {
		paddle = new GRect((getWidth()-PADDLE_WIDTH)/2.0,getHeight()-PADDLE_Y_OFFSET,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
		addMouseListeners();
	
	}
	
	public void mouseMoved(MouseEvent e) {
		//Record the coordinates of the previous paddle location
		last = new GPoint(paddle.getLocation());
		double paddleX = e.getX();
		// Ensure the entire paddle visible in the widow
		if ((paddleX>0) && (paddleX+PADDLE_WIDTH<getWidth())){
			paddle.move(e.getX()-last.getX(),0);
		}
	}
	/*
	 * The method is to set up the bricks in the game,which follows a rainbow-sequence
	 */
	private void setUpBricks() {
		brickRow(Color.RED,BRICK_Y_OFFSET);
		brickRow(Color.ORANGE,BRICK_Y_OFFSET+2.0*(BRICK_HEIGHT+BRICK_SEP));
		brickRow(Color.YELLOW,BRICK_Y_OFFSET+4.0*(BRICK_HEIGHT+BRICK_SEP));
		brickRow(Color.GREEN,BRICK_Y_OFFSET+6.0*(BRICK_HEIGHT+BRICK_SEP));
		brickRow(Color.CYAN,BRICK_Y_OFFSET+8.0*(BRICK_HEIGHT+BRICK_SEP));
	}

	/*
	 * The method is to set up each two brick rows that are in the same color. 
	 * There are two inputs in the method, one is the color, the other is the y coordinate of the first row for each color.
	 */
	private void brickRow(Color color, double OffsetY) {
		for (int row=1; row<=2; row++) {
			for (int col=1; col<=NBRICK_COLUMNS;col=col+1) {
				double brickX=(getWidth()-NBRICK_COLUMNS*BRICK_WIDTH-(NBRICK_COLUMNS-1)*BRICK_SEP)/2.0+(col-1)*(BRICK_WIDTH+BRICK_SEP);
				double brickY=OffsetY+(row-1)*(BRICK_HEIGHT+BRICK_SEP);
				GRect brick= new GRect(brickX,brickY,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick);
			}

		}
	}
}

