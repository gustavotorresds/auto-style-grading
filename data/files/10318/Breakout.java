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

	//Number of colors of bricks
	public static final int NUM_BRICK_COLORS = 5;

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
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 2.0;
	public static final double VELOCITY_X_MAX = 4.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	//intializes the paddle in the game
	private GRect paddle = null;

	//makes ball an instance variable
	private GOval ball = null;

	//makes bricks an instance variable
	private GRect brick = null;

	//declaring the velocities
	private double vx,vy;
	
	//changes in velocity as bricks are removed
	private double VELOCITY_CHANGE= 1.15;

	//creates a random generator to set the vx
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//creates a counter to determine how many times the player had a turn
	private int turn;
	
	//creates a counter for the number of bricks in the world
	private int BRICK_COUNTER; 	
		
	//creates a counter for the number of times the ball hits the paddle.
	private int PADDLE_COUNTER;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp();
		playBall();		
	}
	/*
	 * Sets up the world for the user to play breakout.
	 */
	private void setUp() {
		buildBricks();
		buildPaddle();
		addMouseListeners();
	}
	/*
	 * Method builds the paddle we will use to beat the game.
	 */
	private void buildPaddle() {
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle,getWidth()/2.0-PADDLE_WIDTH/2.0, getHeight()- PADDLE_Y_OFFSET);	
	}
	/*
	 * This method forces the paddle to move according to the movement of the 
	 * mouse in the X direction. However, the paddle cannot move beyond the width of 
	 * the window.
	 */
	public void mouseMoved(MouseEvent e){
		double x = e.getX()-PADDLE_WIDTH/2.0;
		double y = getHeight()- PADDLE_Y_OFFSET;		
		paddle.setLocation(x,y);
		if(x >= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH,y);
		}
		else if(x <= 0) {
			paddle.setLocation(0,y);
		}
	}
	/*
	 * The buildBrick command allows the user to input a color and the order (0-x) 
	 * of colors.
	 */
	private void buildBricks() {
		brickRow(Color.RED,0);
		brickRow(Color.ORANGE,1.0);
		brickRow(Color.YELLOW,2.0);
		brickRow(Color.GREEN,3.0);
		brickRow(Color.CYAN,4.0);
	}
	/*
	 * This method creates the rows of bricks at top of the screen in alternating colors
	 */
	private void brickRow(Color color, double num) {
		BRICK_COUNTER = 0;
		// j counts to give us two rows of bricks at a time
		for(int j = 0; j < (NBRICK_ROWS/NUM_BRICK_COLORS) ; j++) {
			// for each row, the number of columns of each brick will be added.
			for (int i= 0; i< NBRICK_COLUMNS; i++) {
				double start_X = (getWidth()-(BRICK_SEP*(NBRICK_COLUMNS+1) + BRICK_WIDTH*NBRICK_COLUMNS))/2;
				//change X accounts for the change between each brick in a row
				double change_X= (BRICK_SEP*(i+1)+BRICK_WIDTH*(i));
				double start_Y = BRICK_Y_OFFSET;
				//changeY accounts for the change in Y between a row 
				double change_Y = ((BRICK_HEIGHT+BRICK_SEP)*j);
				//newColorSetY changes the Y value according to how many rows (sets of colors) came before it.
				double newColorSetY = num*(BRICK_HEIGHT+BRICK_SEP)*(NBRICK_ROWS/NUM_BRICK_COLORS);
				brick = new GRect(start_X + change_X,start_Y+change_Y+newColorSetY,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick);
				BRICK_COUNTER++;
			}
		}
	}
	/*
	 * Establishes the basic 
	 */
	private void playBall() {
		//the turns/lives a player has starts at 0
		turn = 0;
		setUpBall();
		PADDLE_COUNTER = 1;
		waitForClick();
		while(turn < NTURNS) {
			//BRICK_COOUNTER counters how many bricks are left as brick/ball collisions occur.
			if(BRICK_COUNTER !=0) {
				checkWalls();
				if(turn == NTURNS) {
					youLose();
					}
				else {
				checkCollider();
				}
				if(turn <NTURNS) {
					ball.move(vx, vy);
					pause(DELAY);
				}
			}else {
				youWon();
			}
			
		}
	}
	/* Alerts user if successfully won the game!
	 */
	private void youWon() {
		GImage youWon = new GImage("youWon.jpg");
		youWon.setSize(getWidth(),getHeight());
		add(youWon,0,0);
	}
	
	/*
	 * Lose message if you run out of lives
	 */
	private void youLose() {
		GLabel youLose = new GLabel("LOSER");
		add(youLose,getWidth()/8,getHeight()/2);
		youLose.setFont("Courier-88");	
	}
	/*
	 * Adds the ball we'll use to play with into the world
	 * vx and vy represent velcoities in respective directions
	 */
	private void setUpBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y =  getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x,y,BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if(rgen.nextBoolean(0.5)) vx = -vx;
	}
	/*
	 * A collision is detected by checking each corner of the ball.
	 */
	private GObject getCollidingObject() {
		GObject topLeft = getElementAt(ball.getX(),ball.getY());
		if(topLeft != null) {
			return topLeft;
		}
		GObject topRight = getElementAt(ball.getX()+2.0*BALL_RADIUS,ball.getY());
		if(topRight != null) {
			return topRight;
		}
		GObject bottomLeft = getElementAt(ball.getX(),ball.getY()+2.0*BALL_RADIUS);
		if(bottomLeft != null) {
			return bottomLeft;
		}
		GObject bottomRight = getElementAt(ball.getX()+2.0*BALL_RADIUS,ball.getY()+2.0*BALL_RADIUS);
		if(bottomRight != null) {
			return bottomRight;
		}
		return null;
	}
	/*
	 * Each time the ball encounters a collision, it will respond based
	 * on the type of collider & play a bounce clip.
	 */
	
	private void checkCollider() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		GObject collider = getCollidingObject();
		if(collider == paddle) {
			bounceClip.play();
			//The number 7 is the increment value for increasing paddle speed/ every 7 time the paddle speed increases
			if(PADDLE_COUNTER % 7.0 == 0) {
				vy = -VELOCITY_Y+ (PADDLE_COUNTER/7)*0.2;
			}else {
				vy = -VELOCITY_Y;
			}
			PADDLE_COUNTER++;
		}
		if(collider != paddle && collider != null) {
			bounceClip.play();
			vy = -vy;
			remove(collider);
			BRICK_COUNTER--; 
		}
	}
	/*
	 * If the ball hits a wall in the world, it will bound off.
	 * Except if the ball hits the bottom wall; the game deducts a life and 
	 * the ball is positioned back to the starting position
	 */
	private void checkWalls() {
		//top
		if(ball.getY() <= 0) {
			vy = -vy;
		}
		//left
		if(ball.getX() <= 0) {
			vx = -vx;
		}
		//right
		if(ball.getX() >= getWidth()-BALL_RADIUS*2) {
			vx = -vx;
		}
		//bottom
		if(ball.getY() >= getHeight()-BALL_RADIUS*2) {
			turn++;
			remove(ball);
			add(ball, getWidth()/2 - BALL_RADIUS,getHeight()/2 - BALL_RADIUS);
			waitForClick();
		}
	}
}


