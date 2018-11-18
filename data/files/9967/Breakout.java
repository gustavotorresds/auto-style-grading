/*
 * File: Breakout.java
 * -------------------
 * Name:Xiaoxi Zeng
 * Section Leader:Ben Allen
 * 
 * This Breakout program plays the game of breaking out the ball. 
 * of a pile of bricks. The ball would be bouncing back and forth 
 * on canvas between the bricks on the top and bottom paddle. When
 * it hits a brick, the brick would disappear. Once the bricks are 
 * all removed, the player wins the game. When the paddle fails
 * to catch the ball and let the ball touch the bottom line, the 
 * player would get another chance to resume the game. Once three
 * chances are used up, the game ends in a loss of the player.
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

	//Create instance variables: the paddle and the ball
	private GRect paddle;
	private GOval ball;
	
	//Create instance variable: the remaining number of the bricks
	private int remainingBrick = NBRICK_COLUMNS * NBRICK_ROWS;
	
	//Create a random number generator rgen
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Create instance variables:feedback to players at the end of
	//the game
	private GLabel win;
	private GLabel lose;
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();
		setUp();
		playTheGame();
		giveTheResult();	
	}
	
	/**
	 * Method:Set Up
	 * --------------------
	 * Put the bricks and create the paddle
	 */
	private void setUp() {
		setUpBricks();
		createPaddle();
	}

	/**
	 * Method:Set Up the Bricks
	 * --------------------
	 * Makes a 10 × 10 brick matrix at the top of the window. 
	 * The color of the bricks remains the same for two rows 
	 * and changes in a sequence: red, orange, yellow, green 
	 * and cyan
	 */
	private void setUpBricks() {
		double dx = BRICK_WIDTH + BRICK_SEP;
		double dy = BRICK_HEIGHT + BRICK_SEP;
		double initialX = getWidth() / 2 - dx * 5 + BRICK_SEP / 2;
		double initialY = BRICK_Y_OFFSET;
		
		//Create a 10 × 10 brick matrix
		for(int i = 0; i < NBRICK_ROWS; i++) {
			for(int j = 0 ; j < NBRICK_COLUMNS; j++) {
				double x = initialX + j * dx;
				double y = initialY + i * dy;
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				
				//Color the bricks and change the color for 
				//every two rows
				brick.setFilled(true);
				if(i % NBRICK_ROWS == 0 || i % NBRICK_ROWS == 1) {
					brick.setColor(Color.RED);
				}else if(i % NBRICK_ROWS == 2 || i % NBRICK_ROWS == 3) {
					brick.setColor(Color.ORANGE);
				}else if(i % NBRICK_ROWS == 4 || i % NBRICK_ROWS == 5) {
					brick.setColor(Color.YELLOW);
				}else if(i % NBRICK_ROWS == 6 || i % NBRICK_ROWS == 7) {
					brick.setColor(Color.GREEN);
				}else if(i % NBRICK_ROWS == 8 || i % NBRICK_ROWS == 9) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}
	
	/**
	 * Method:Create Paddle
	 * ---------------------
	 * Creates a paddle at the bottom of the window
	 */
	private void createPaddle() {
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect(0, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	/**
	 * Method: mouseMoved
	 * --------------------
	 * Make the paddle track the mouse as it moves
	 */
	public void mouseMoved(MouseEvent e) {
		double changedX = e.getX();
		double maxX = getWidth() - PADDLE_WIDTH;
		
		//Make sure the paddle always stay in the window entirely
		if(changedX < maxX && changedX > 0) {
			paddle.setX(changedX);
		}else if(changedX > maxX){
			paddle.setX(maxX);
		}else {
			paddle.setX(0);
		}
		
	}
	
	/**
	 * Method: Play The Game
	 * -----------------------
	 * Start to play the game. Every play is given three
	 * times to fail. Once three times use up, the game
	 * ends. When the player hits the last brick, the player
	 * win the game.
	 */
	private void playTheGame() {
		for(int turn = 0; turn < NTURNS; turn ++) {
			if(remainingBrick != 0) {
				bouncingBall();
			}else {
				remove(ball);
			}
		}
		
	}
	
	/**
	 * Method:Bouncing Ball
	 * --------------------
	 * Creates a ball and make it bouncing back and forth 
	 * in the window
	 */
	private void bouncingBall() {
		
		//Initialize vx and vy
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		double vy = VELOCITY_Y;
		
		//Wait for a click from the mouse to create the ball
		//and get the game started
		waitForClick();
		createTheBall(BALL_RADIUS);	
		
		while(true) {
			
			//Check whether the ball collide with a brick or
			//the paddle.If the ball collides with a brick, then 
			//remove the brick and bounce the ball. If the ball
			//collides with the paddle, then bounce the ball.
			if(getCollidingObject() != null) {
				GObject collider = getCollidingObject();
				if(collider == paddle) {
					vy = -Math.abs(vy);
				}else {
					remove(collider);
					remainingBrick --;
					vy = -vy;
				}
			}
			
			//If the player hits the last brick, then
			//exist this loop.
			if(remainingBrick == 0) {
				break;
			}
			
			//If the ball hits the bottom, then exist this 
			//loop.
			if(hitBottomWall(ball)) {
				remove(ball);
				break;
			}
			//Test the position of the ball and keep
			//it stay in the window
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			
			//Update the position of the ball
			ball.move(vx, vy);
			
			pause(DELAY);
		}
		
	}
	
	/**
	 * Method: Get Colliding Object
	 * ----------------------------
	 * Examine the four corners of the constraint rectangle
	 * of the ball to check if the ball collides with any object.
	 * If it does,then return that object back. If it does not,
	 * return null.
	 */
	private GObject getCollidingObject() {
		double upLeftX = ball.getX();
		double upY = ball.getY();
		double upRightX = upLeftX + 2 * BALL_RADIUS;
		double downLeftX = upLeftX;
		double downY = upY + 2 * BALL_RADIUS;
		double downRightX = downLeftX + 2 * BALL_RADIUS;
		if(getElementAt(upLeftX, upY) != null) {
			return getElementAt(upLeftX, upY);
		}else if(getElementAt(upRightX, upY) != null) {
			return getElementAt(upRightX, upY);
		}else if(getElementAt(downLeftX, downY) != null) {
			return getElementAt(downLeftX, downY);
		}else if(getElementAt(downRightX, downY) != null) {
			return getElementAt(downRightX, downY);
		}else {
			return null;
		}

	}
    
	/**
	 * Method: Give The Result
	 * ------------------------
	 * Check the result of the game and give the player
	 * feedback about the result.
	 */
	private void giveTheResult() {
		if(remainingBrick == 0) {
			winLabel();
		}else {
			loseLabel();
		}
	}
	
	/**
	 * Method: Create The Ball
	 * -----------------------
	 * Create a ball and put it in the center of the window
	 */
	private void createTheBall(double radius) {
		double ovalX = getWidth() / 2 - BALL_RADIUS;
		double ovalY = getHeight() / 2 - BALL_RADIUS;
		double size = radius * 2;
		ball = new GOval(ovalX, ovalY, size, size);
		ball.setFilled(true);
		add(ball);	
	}

	/**
	 * Method: Hit Left Wall
	 * ----------------------
	 * Test whether a given ball hit the left wall
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <=0;
	}
	
	/**
	 * Method: Hit Right Wall
	 * ----------------------
	 * Test whether a given ball hit the Right wall
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - 2 * BALL_RADIUS;
	}

	/**
	 * Method: Hit Top Wall
	 * ----------------------
	 * Test whether a given ball hit the Top wall
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/**
	 * Method: Hit Bottom Wall
	 * ----------------------
	 * Test whether a given ball hit the Bottom wall
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - 2 * BALL_RADIUS;
	}
	
	
	/**
	 * Method: Win Label
	 * ----------------------
	 * Create win label and show the label when the player
	 * hits the last brick
	 */
	private void winLabel() {
		win = new GLabel("YOU WIN!");
		double winWidth = win.getWidth();
		double winHeight = win.getAscent();
		win.setLocation((getWidth() - winWidth) / 2, (getHeight() - winHeight) / 2);
		add(win);
	}
	
	/**
	 * Method: Lose Label
	 * ----------------------
	 * Create lose label and show the label when the player
	 * hits the bottom wall
	 */	
	private void loseLabel() {
		lose = new GLabel("YOU LOSE :(");
		double loseWidth = lose.getWidth();
		double loseHeight = lose.getAscent();
		lose.setLocation((getWidth() - loseWidth) / 2, (getHeight() - loseHeight) / 2);
		add(lose);
	}
}
