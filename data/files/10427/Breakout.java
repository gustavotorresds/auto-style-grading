/*
 * File: Breakout.java
 * -------------------
 * Name: Priyanka Multani 
 * Section Leader: Ben Barnett 
 * 
 * This file will implement the game of Breakout. The goal of the game is to eliminate all of the
 * bricks on the screen without letting the ball fall off the bottom of the screen. The player has
 * three tries before the game is over. This particular version includes interactive messages and sounds to 
 * make the game more realistic. It will also keep a score that is based on what color brick
 * the player hit. In order to start the game, the player will have to click the screen. 
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
	
	//private instance variables 
	private GRect brick;
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GLabel label = new GLabel("");
	private GLabel brickLabel = new GLabel("");
	private int brickScore;
	
	//load sound file
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		addMouseListeners();
		gameSetup();
		gamePlay();	
	}
	
	//set up the bricks and the paddle 
	private void gameSetup() {
		setupBricks();
		makePaddle();
		setupPaddle();	
	}
	
	//create and animate the ball
	//keep track of the number of turns the player has left
	//add text to make the game more interactive 
	private void gamePlay() {
		addLabel( "Click the ball to start!");
		pause(50*DELAY);
		remove(label);
		for(int i = NTURNS; i > 0; i--) {
			createBall();
			waitForClick();
			moveBall();
			addLabel("Lives Left: " + (i-1));
			pause(35*DELAY);
			remove(label);
		} 
		addLabel("Game Over!");
	}
	
/*
 * 	This method controls how the ball moves, tracks the number of bricks left, and keeps score.
 * 	The x-velocity of the ball is chosen randomly. 
 * 	It controls how the ball moves by changing the y component of its velocity. When the ball collides 
 * 	against something, it will either remove that object if it is the bricks, bounce off if its 
 * 	the paddle, or ignore it if it is the total score label. 
 * 	Each time a collision happens, the brick score is updated and if appropriate, the collider is removed,
 * 	the brick count decreases by 1, and the terminating condition is checked. 
 */
	private void moveBall() {
		int brickCount = NBRICK_COLUMNS*NBRICK_ROWS;
		brickScore = 0;
		addbrickLabel(brickScore);
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx; 
		}
		while(ball.getY() <= getHeight()) {
			bounceBall(ball);
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				bounceClip.play();
				vy = -vy; 
			} 
			else if (collider != paddle && collider != null && collider != brickLabel) {
				bounceClip.play();
				remove(collider);
				brickCount--;
				brickScore(collider);
				brickLabel.setLabel("Brick Score: " + brickScore);
				terminatingConditions(brickCount);
				vy = -vy; 
			}
			ball.move(vx, vy);
			pause(DELAY);
		}	
	}
	
	//determines which wall the ball hit and changes its x or y velocity accordingly. 
	//If the bottom wall is hit, the ball is removed. 
	private void bounceBall(GOval ball) {
		if(ball.getX() > getWidth() - 2*BALL_RADIUS || ball.getX() < 0) {
			vx = -vx;
		}
		if(ball.getY() < 0) {
			vy = -vy;
		}
		if(ball.getY() > getHeight() - PADDLE_HEIGHT) {
			remove(ball);	
		}
	}
	
	//checks the four corners of the ball to see if there is a collision anywhere. 
	//returns the element that the ball collided with to then check if it is a brick, paddle, or score
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY())!= null) {
			return getElementAt(ball.getX(), ball.getY()); 
		} 
		else if(getElementAt((ball.getX() + 2*BALL_RADIUS), ball.getY()) !=null) {
			return getElementAt((ball.getX() + 2*BALL_RADIUS), ball.getY());
		}
		else if(getElementAt(ball.getX(), (ball.getY() + 2*BALL_RADIUS)) != null) {
			return getElementAt(ball.getX(), (ball.getY() + 2*BALL_RADIUS));
		}
		else if(getElementAt((ball.getX() + 2*BALL_RADIUS), (ball.getY() + 2*BALL_RADIUS)) != null) {
			return getElementAt((ball.getX() + 2*BALL_RADIUS), (ball.getY() + 2*BALL_RADIUS));
		} else {
			return null; 
		}
	}
	
	//when there are no more bricks, the ball is removed and the player wins 
	private void terminatingConditions(int brickCount) {
		if(brickCount == 0) {
			remove(ball);
			addLabel("Congratulations! You won!!");
		}
	}
	
	//keeps player's score
	//cyan and green bricks are 1 point, yellow and orange are 2 and red is 5
	private void brickScore(GObject collider) {
		if(collider.getColor() == Color.CYAN || collider.getColor() == Color.GREEN) {
			brickScore ++;
		}
		else if (collider.getColor() == Color.YELLOW || collider.getColor() == Color.ORANGE) {
			brickScore +=2;
		} else {
			brickScore += 5;
		}	
	}
	
	//adds a label to keep the total score
	private void addbrickLabel(int brickScore) {
		brickLabel = new GLabel("Brick Score: " + 0);
		add(brickLabel, getWidth() - brickLabel.getWidth()-10, getHeight() - brickLabel.getAscent()/2);
	}
	
	//general method for the labels that pops up throughout the game. 
	//used for number of lives, start message, and ending messages (win or lose)
	private void addLabel(String x) {
		label = new GLabel(x);
		label.setFont("Courier-18");
		add(label, getWidth()/2 - label.getWidth()/2, getHeight()/2.5);
		
	}
	
	//create a ball and place it in the center of the screen
	private void createBall() {
		ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball, getWidth()/2 - (BALL_RADIUS), getHeight()/2 - (BALL_RADIUS)); 
	}

	//create a paddle 
	private GRect makePaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	//place the paddle in the center of the bottom of the screen
	private void setupPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);	
	}
	
	//keeps track of how the x-coordinate of the mouse moves and moves the paddle accordingly
	public void mouseMoved(MouseEvent e) {
		if(e.getX() < getWidth()-PADDLE_WIDTH && e.getX() > 0) {
			double mouseX = e.getX();
			double mouseY = getHeight() - PADDLE_Y_OFFSET;
			paddle.setLocation(mouseX, mouseY);
		}	
	}
	
	/*
	 * Set up bricks using nested for loops. The outer for loop specifies which layer 
	 * is being constructed. The inner loop specifies how to center each layer and how 
	 * many bricks to put and space accordingly. 
	 */
	
	private void setupBricks() {
		for(int layer = 0; layer < NBRICK_ROWS; layer++) {
			for(int col = 0; col < NBRICK_COLUMNS; col++){
				
				double x = getWidth()/2 - (NBRICK_COLUMNS*BRICK_WIDTH)/2 -((NBRICK_COLUMNS-1) * BRICK_SEP)/2 + ((col * BRICK_WIDTH) + (BRICK_SEP * col));
				double y = BRICK_Y_OFFSET + (layer)* BRICK_HEIGHT + (BRICK_SEP * layer);
				brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);
				brick.setColor(colorBricks(layer));
			}		
		}				
	}
	
	/*
	 * Colors each row based on how many rows there are. Divides number of rows by 5 to 
	 * determine how many to color that color. 
	 * For example with 10 rows: colors 10 rows cyan, then 8 rows green, then 6 rows yellow
	 * then 4 rows orange and 2 rows red. Top most color is what is shown. This method works
	 * for any number of rows.    
	 */
	private Color colorBricks(int layer) {
		while(layer < NBRICK_ROWS/5) {
			return (Color.RED);
		}
		while(layer < NBRICK_ROWS/2.50) {
			return(Color.ORANGE);
		}
		while(layer < NBRICK_ROWS/1.67) {
			return(Color.YELLOW);
		}
		while(layer < NBRICK_ROWS/1.25) {
			return(Color.GREEN);
		}
		while(layer < NBRICK_ROWS) {
			return(Color.CYAN);
		}
		return null;
	}
}
