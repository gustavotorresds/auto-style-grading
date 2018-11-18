/*
 * File: Breakout.java
 * -------------------
 * Name: Emily Yang 
 * Section Leader: Adam Mosharrafa
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
	
	// Number of bricks 
	public static final int NBRICKS = NBRICK_COLUMNS * NBRICK_ROWS; 
	
	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;
	
	//Label Offset (for Y and X)
	public static final double LABEL_OFFSET = 4;

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
	
	//Creates rectangle to be knocked down
	private GRect rect;
	
	//Creates rectangle to be the paddle
	private GRect paddle;
	
	//Creates ball 
	private GOval ball;
	
	//Creates horizontal velocity of ball 
	private double vx, vy;
	
	//Tracks number of bricks in game 
	private int n = NBRICK_ROWS * NBRICK_COLUMNS;
	
	//Tracks number of lives left
	private int lives = NTURNS;
	
	//Tracks number of points earned 
	private int p = 0;
	
	//Labels score 
	private GLabel score;
	
	//Labels lives
	private GLabel life;
	
	//Creates random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		createPaddle();
		displayInitialScore();
		displayInitialLives();
		while(lives > 0 && n!= 0) {
			createBall();        
			startSign();
			moveBall();
		}
		endGame();
		addMouseListeners();
	}
/*
 * Sets up bricks to be knocked down during the game
 */
	private void setUpBricks() {          
		double cx = getWidth()/2;
		for (int i = 0; i<NBRICK_ROWS;i++) {   
			double x = (getWidth() - NBRICK_COLUMNS*BRICK_WIDTH - (NBRICK_COLUMNS-1)*BRICK_SEP)/2;
			double y = BRICK_Y_OFFSET + i*(BRICK_HEIGHT+BRICK_SEP);    
			for (int j = 0; j<NBRICK_COLUMNS; j++) {
				rect = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				add(rect);
				rect.setFilled(true);
				double x1 = x;
				x = x1 + BRICK_WIDTH + BRICK_SEP;
				colorRects(i);
			}
		}
	}

/*
 * Colors the rectangles one of five colors
 */
	private void colorRects(int i) {  
		if (i< NBRICK_ROWS/5) {
			rect.setColor(Color.RED);
		} else if (NBRICK_ROWS/5 <= i && i < 2*NBRICK_ROWS/5) {
			rect.setColor(Color.ORANGE);
		} else if (2*NBRICK_ROWS/5 <= i && i < 3*NBRICK_ROWS/5) {
			rect.setColor(Color.YELLOW);
		} else if (3*NBRICK_ROWS/5 <= i && i < 4*NBRICK_ROWS/5) {
			rect.setColor(Color.GREEN);
		} else if (4*NBRICK_ROWS/5 <= i && i < NBRICK_ROWS) {
			rect.setColor(Color.CYAN);
		}
	}

/*
 * Creates paddle to be moved around 
 */
	private void createPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2;
		double y = (getHeight() - PADDLE_Y_OFFSET);
		paddle = new GRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

/*
 * Moves paddle according to user's mouse movements
 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double y = (getHeight() - PADDLE_Y_OFFSET);
		double dx = x - paddle.getX();
		double limit = getWidth() - PADDLE_WIDTH;
		if (x < limit) {
			paddle.move(dx, 0);
			paddle.setLocation(x,y);
		}
	}
	
/*
 * Displays initial amount of lives
 */
	private void displayInitialLives() {
		life = new GLabel("Lives: " +lives);
		double x = LABEL_OFFSET;
		double y = LABEL_OFFSET + life.getAscent();
		add(life, x, y);
	}

/*
 * Creates ball and places it in the center of the screen
 */
	private void createBall() {
		double x = (getWidth() - 2*BALL_RADIUS)/2;
		double y = (getHeight() - 2*BALL_RADIUS)/2;
		ball = new GOval(x,y,2*BALL_RADIUS,2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

/*
 * Displays score
 */
	private void displayInitialScore() {
		score = new GLabel ("Score: "+p);
		double x = getWidth() - (score.getWidth()+LABEL_OFFSET);
		double y = LABEL_OFFSET + score.getAscent();
		add(score,x,y);
	}
	
	
/*
 * Creates a starting sign 	
 */
	private void startSign() {
		GLabel start = new GLabel("");
		if (lives == NTURNS) {
			start = new GLabel("Press to Start");
		} else {
			start = new GLabel("Press to Try Again");
		}
		double x = (getWidth() - start.getWidth())/2;
		double y = getHeight()/2 - 2*BALL_RADIUS;
		add(start, x, y);
		waitForClick();
		remove(start);
	}
	
	
/*
 * Moves ball 
 */
	private void moveBall() {   
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		ballAnimation();
	}

/*
 * Animates ball to move, interacting with the walls of the game and the bricks
 */
	private void ballAnimation() {
		while (n > 0) {
			if (ball.getX() >= (getWidth()-ball.getWidth())){
				vx = -vx; 
			}
			if (ball.getX() <= 0 ) {
				vx = -vx; 
			}
			if (ball.getY() >= (getHeight() - ball.getHeight())) {
				vy = -vy;
				lives--;
				life.setLabel("Lives: " + lives);
				remove(ball);
				break;
			}
			if (ball.getY() <= 0) {
				vy = -vy; 
			}
			ball.move(vx, vy);
			pause(DELAY);
			GObject collider = getCollidingObject();
			responseToCollision(collider); 
		}
	}

/*
 * Identifies the object the ball has collided into 
 */
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		GObject obj = getElementAt(x,y);
		if (obj == null) {
			obj = getElementAt(x+2*BALL_RADIUS,y);
		}
		if (obj == null) {
			obj = getElementAt(x, y+2*BALL_RADIUS);
		}
		if (obj == null) {
			obj = getElementAt(x+2*BALL_RADIUS,y+2*BALL_RADIUS);
		}
		return obj; 
	}

/*
 * Game responds to the object the ball has collided into. If the object is a rectangle, game removes it and tracks the number of remaining bricks.
 * If the object is the paddle, the ball bounces off of it. 
 */
	private void responseToCollision(GObject collider) {
		if (collider != null) {
			if (collider == paddle) {
				vy = -Math.abs(vy);
				if (ball.getX() > (paddle.getX() + 3*PADDLE_WIDTH/4) && vx < 0){        
					vx = -vx;
				}
				if (ball.getX() < (paddle.getX() + PADDLE_WIDTH/4) && vx > 0) {
					vx = -vx;
				}
			} else if(!(collider.getY() < BRICK_Y_OFFSET)){
				remove (collider);
				bounceClip.play();
				vy = -vy;
				n--;
				p++;
				updateScore();
				if (p > 0 && p % (NBRICKS/5) == 0) {
					vx = 2*vx;
					vy = 2*vy; 
				}
			}
		}
	}

/*
 * Updates score after point is scored
 */
	private void updateScore() {
		score.setLabel("Score: "+p);
		double x = getWidth() - (score.getWidth()+LABEL_OFFSET);
		double y = LABEL_OFFSET + score.getAscent();
		score.setLocation(x,y);
	}

/*
 * At the end of the program, announces whether user has won or lost the game
 */
	private void endGame() {
		if (n == 0) {
			winGame();
		} else {
			loseGame();
		}
	}

/*
 * Announces user has lost game
 */
	private void loseGame() {
		GLabel lose = new GLabel("You Lose");            
		double x = (getWidth() - lose.getWidth())/2;
		double y = (getHeight() + lose.getAscent())/2;
		lose.setColor(Color.RED);
		add(lose, x, y);	
	}

/*
 * Announces user has won game 
 */
	private void winGame() {
		GLabel win = new GLabel("You Win!");
		double x = (getWidth() - win.getWidth())/2;
		double y = (getHeight() + win.getAscent())/2;
		win.setColor(Color.GREEN);
		add(win,x,y);
	}
}
