/*
 * File: Breakout.java
 * -------------------
 * Name: Sofia Carrillo
 * Section Leader: Julia Daniel 
 * 
 * This file creates a playable game of Breakout!
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
	
	//Creates the paddle
	GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
	
	//Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Tracks velocity of ball
	private double vx, vy;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setupGame(); 
		playGame();
	}

	/*
	 * Method: endGame
	 * ----------------
	 * This ends the game after either all the bricks
	 * have been removed or there are no more turns left.
	 */
	private void endGame(int brickCount) {
		removeAll();
		if(brickCount == (NBRICK_COLUMNS*NBRICK_ROWS)) {
			drawWin();
		}else {
			drawLose();
		}
	}
	
	/*
	 * Displays winning image.
	 */
	private void drawWin() {
		GImage img = new GImage("happybaby.jpg");
		add(img);
	}
	
	/*
	 * Displays losing image.
	 */
	private void drawLose() {
		GImage img = new GImage("sad baby.jpg");
		add(img);
	}
	
	/*
	 * Method: playGame
	 * ------------------
	 * This plays the game by creating a ball,
	 * allowing it to bouncing around the world 
	 * removing bricks along the way.
	 */
	private void playGame() {
		int brickCount = 0; //this counts the bricks removed
		int turnCount = NTURNS;  //this counts the number of remaining turns
		GOval ball = makeBall(); //adds ball to screen
		waitForClick(); //begins game after mouse click
		vx = rgen.nextDouble(1.0,1.0);
		vy = 3.0;
		if(rgen.nextBoolean(0.5)) vx = -vx; //randomizes starting x velocity
		while(brickCount < NBRICK_COLUMNS*NBRICK_ROWS && turnCount > 0) { 
			ball.move(vx, vy);
			pause (DELAY);
			if (ball.getX() <=0 || ball.getX() >= (getWidth()-BALL_RADIUS*2)) { //collisions Left/Right
				vx = -vx;
			}
			if(ball.getY() <= 0) { //collisions Top
				vy = -vy;
			}
			if(ball.getY() >=(getHeight()-BALL_RADIUS*2) ) { //collision Bottom
				remove(ball); //ends turn by removing ball
				turnCount = turnCount-1; //and reducing turn count by 1
				if (turnCount > 0) { //this restarts the game
					ball = makeBall();
					waitForClick(); 
					vx = rgen.nextDouble(1.0,1.0);
					vy = 3.0;
					if(rgen.nextBoolean(0.5)) vx = -vx;
					ball.move(vx, vy);
				}
			}
			GObject collider = getCollidingObject(ball); //this checks if the ball collided with 
			if (collider == paddle) {					//a brick or the paddle
				vy = -vy;
			}else if (collider !=null) {
				vy = -vy;
				remove(collider);
				brickCount = brickCount + 1;
			}
		}	
		endGame(brickCount); //this ends the game after all the bricks are gone or turns used
	}

	/*
	 *Method: getCollidingObject
	 *---------------------------
	 * This checks each "corner" of the ball and returns the object at that 
	 * location as the variable "collider".
	 */
	private GObject getCollidingObject (GOval ball) {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider == null) {
			collider = getElementAt(ball.getX()+(BALL_RADIUS*2), ball.getY());
			if (collider == null) {
				collider = getElementAt(ball.getX(), ball.getY()+ (BALL_RADIUS*2));
				if (collider == null) {
					collider = getElementAt(ball.getX()+(BALL_RADIUS*2), ball.getY()+ (BALL_RADIUS*2));
				} else {
					return collider;
				}
			} else {
				return collider;
			}
		}else { 
			return collider;
		}
		return collider;
	}

	/*
	 * Method: makeBall
	 * ---------------
	 * This adds the ball to the screen. 
	 */
	private GOval makeBall() {
		double xBall = (getWidth()/2)-BALL_RADIUS;
		double yBall = (getHeight()/2)-BALL_RADIUS;
		GOval ball = new GOval (BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, xBall, yBall);
		return ball;
	}

		/*
		 * Method: setupGame
		 * -------------------
		 *This creates the world of the game (the bricks and paddle).
		 */
	private void setupGame() {
		setBricks();
		setPaddle();
	}

	/*
	 * Method: setPaddle
	 * -------------------
	 * This creates the paddle and adds it to the center
	 * of the screen at the correct height and with
	 * the correct color.  
	 */
	private void setPaddle() {
		double xPaddle = getWidth()/2-(PADDLE_WIDTH/2);
		double yPaddle = findPaddleY();
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, xPaddle, yPaddle);
	}
	
	/*
	 * Method: mouseMoved
	 * --------------------
	 *This allows the paddle to track the mouse by changing 
	 *the x location of the paddle based on the x location
	 *of the mouse. 
	 */
	public void mouseMoved(MouseEvent e) {
		double xPaddle = e.getX();
		double yPaddle = findPaddleY();
		paddle.setLocation(xPaddle, yPaddle);
	}
	
	/*
	 * Method: findPaddleY
	 * ---------------------
	 * This finds the y coordinate of the paddle.
	 */
	private double findPaddleY() {
		return(getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT) ;
	}

	
	/*
	 * Method: setBricks
	 * ---------------------
	 * This calls one row of bricks 10 times to build the full set. 
	 */
	private void setBricks() {
		for (int rowNum=0; rowNum < NBRICK_ROWS; rowNum++) {
			buildOneRow(rowNum);
		}
	}
	
	/*
	 * Method: buildOneRow
	 * --------------------
	* This Builds one row of bricks.
	*/
	private void buildOneRow(int rowNum) {
		for (int BrickNum=0; BrickNum< NBRICK_COLUMNS; BrickNum++) {
			double xRow = findRowX(BrickNum);
			double yRow = findRowY(rowNum);
			Color color = getColor(rowNum);
			GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick, xRow, yRow);
		}
	}
	/*
	 * This determines the color of each row.
	 */
	private Color getColor(int rowNum) {
		if(rowNum==0|| rowNum ==1 ) {
			return Color.RED;
		} else if(rowNum==2 || rowNum ==3 ) {
			return Color.ORANGE;
		}else if(rowNum==4 || rowNum ==5) {
			return Color.YELLOW;
		}else if(rowNum==6 || rowNum ==7) {
			return Color.GREEN;
		}else if(rowNum==8 || rowNum ==9){
			return Color.CYAN;
		}
		return null;
	}

	/*
	 * This finds the y for each brick in each row. 
	 */
	private double findRowY(int rowNum) {
		return (BRICK_Y_OFFSET + rowNum*(BRICK_HEIGHT + BRICK_SEP));
	}

	/*
	 * This finds the x for each brick in each row.
	 */
	private double findRowX(int brickNum) {
		return ((getWidth()/2)-((NBRICK_COLUMNS/2)*BRICK_WIDTH)-BRICK_SEP*((NBRICK_COLUMNS/2)-1)-BRICK_SEP/2)+brickNum*(BRICK_WIDTH+BRICK_SEP);
		
	}

}
