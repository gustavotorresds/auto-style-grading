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
	
	//Offset of the leftmost brick column from the left, in pixels
	public static final double BRICK_X_OFFSET = (CANVAS_WIDTH -
			(BRICK_WIDTH*NBRICK_COLUMNS + BRICK_SEP*(NBRICK_COLUMNS-1)))/2;
	
	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;
	
	//Offset of the paddle from the left/right side 
	public static final double PADDLE_X_OFFSET = (CANVAS_WIDTH - 
			PADDLE_WIDTH)/2;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 500.0 / 60.0;
	
	//Audio Clip for bounces 
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// Number of turns 
	public static final int NTURNS = 3;
	
	//Paddle instance variable
	private GObject paddle= null; 
	
	//Ball instance variable
	private GObject ball=null; 
	
	//Mouse instance varaible
	private double mouseX;
	
	//Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	//Establishes collider to track what the ball collides with 
	private GObject collider = null; 
	
	//Number of turns
	private int t=3; 
	
	//Number of bricks/counter for bricks
	private int bricks=NBRICK_ROWS* NBRICK_COLUMNS ;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		
		setup();
		addMouseListeners();
		playGame(); 
		
		//This makes the game play until the user wins or runs out of turns
		if (bricks == 0) {
			winner();
		} else {
			loser();
		}
	}

	
	// This method prints the losing message when the user runs out of balls or turns. 
	private void loser() {
		removeAll(); 
		GLabel win= new GLabel ("You are out of balls. You lose. ");
		add (win, getWidth()/2 - win.getWidth()/2, getHeight()/2 - win.getAscent()/2); 
		}

	//This method starts the game play by starting the movement after a click. It also keeps
	// track of the number of turns and only continues until the user either wins by elimiating
	// all of the bricks or loses by running out of turns. 
	private void playGame() {
		for (int t=NTURNS; t>0; t--) {
			if (bricks >0) {
			GOval ball = createBall();
			waitForClick(); 
			moveBall(ball);
			}	
		}	
	}

	//This method prints the winning text after the user eliminates all of the bricks
	private void winner() {
		removeAll(); 
		GLabel win= new GLabel ("WINNER WINNER CHICKEN DINNER!");
		add (win, getWidth()/2 - win.getWidth()/2, getHeight()/2 - win.getAscent()/2); 	
	}

	//This method moves the ball while checking for walls and collisions. If there is a collision or wall, 
	// the ball reverses direction. It releases the ball at a random angle and velocity. It also adds 
	// sound for the collisions. This method also only allows for movement if the user has not won yet. Additionally, 
	// when the ball hits the bottom, this method subtracts a turn and resets the program. 
	private void moveBall(GObject ball) { 
		double vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) vx = -vx;
		double vy= 1; 
		while (bricks>0) {	
			ball.move(vx, vy);
			pause (DELAY);
			if (isTopWall(ball)) {
				vy = -vy; 
			}
			if (isRightWall(ball) || isLeftWall(ball)) {
			vx = -vx;
			} 
			if (isBottomWall(ball)) {
				t-- ; 
				remove (ball);
				break;
			}
		GObject collider = getCollidingObject(ball);
		if (collider == paddle) {
			bounceClip.play();
			vy= -vy;
		} else if (collider != null) {
			bricks--; 
			bounceClip.play();
			remove (collider); 
			vy = -vy; 
			}
		}
	}

	//This method detects collisions with the ball 
	private GObject getCollidingObject(GObject ball) {
		if (getElementAt (ball.getX(), ball.getY()) != null) {
			return getElementAt (ball.getX(), ball.getY()); 
		} else if ((getElementAt ((ball.getX()+ 2*BALL_RADIUS), ball.getY())) != null) {
			return getElementAt ((ball.getX()+ 2*BALL_RADIUS), ball.getY());
		} else if ((getElementAt(ball.getX(), (ball.getY()+ 2*BALL_RADIUS))) != null) {
			return getElementAt(ball.getX(), (ball.getY()+ 2*BALL_RADIUS)); 
		} else if ((getElementAt((ball.getX()+ 2*BALL_RADIUS), (ball.getY()+ 2*BALL_RADIUS))) != null) {
			return  getElementAt((ball.getX()+ 2*BALL_RADIUS), (ball.getY()+ 2*BALL_RADIUS));
		}  
		return null ; 
	}
		
		
	//Detects the bottom wall		
	private boolean isBottomWall(GObject ball) {
		return ball.getY() > getHeight()- ball.getHeight();
	}	

	//Detects the left wall
	private boolean isLeftWall(GObject ball) {
		return ball.getX() < 0;
	}

	//Detects the right wall
	private boolean isRightWall(GObject ball) {
		return ball.getX() > getWidth()- ball.getWidth();
	}

	//Detects the top
	private boolean isTopWall(GObject ball) {
		return ball.getY() < 0;
	}

	//This method creates the ball and returns the ball as an object
	public GOval createBall() {
		double size = BALL_RADIUS *2; 
		GOval r = new GOval (size, size);
		r.setFilled(true); 
		add (r, getWidth()/2, getHeight()/2); 
		return r;
		
	}
	
	//This is a mouse event methoud which allows for the paddle to track the X movement of the mousement, but not the Y
	public void mouseMoved(MouseEvent e) { 
		double mouseX = e.getX() - PADDLE_WIDTH/2; 
		if (mouseX >= 0 && mouseX <= CANVAS_WIDTH - PADDLE_WIDTH) {
		paddle.setLocation(mouseX,(getHeight()-PADDLE_Y_OFFSET));}
	}
	
	//This method adds the paddle to the game
	private void addPaddleToBreakout (GObject paddle) {
		double paddleX = PADDLE_X_OFFSET; 
		double paddleY = getHeight()-PADDLE_Y_OFFSET; 
		add (paddle, paddleX, paddleY); 	
	}
	
	//This method returns the paddle
	private GRect createPaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}
	
	//This method sets up the entire game including the bricks 
	private void setup() {
		paddle = createPaddle();
		addPaddleToBreakout(paddle);
		for (int row=0; row<NBRICK_ROWS; row++) {
		for (int col =0; col< NBRICK_COLUMNS; col++) { 
			double x = BRICK_WIDTH+BRICK_SEP;
			double y = BRICK_HEIGHT+BRICK_SEP;
			GRect rect = new GRect(BRICK_X_OFFSET+(x*col),BRICK_Y_OFFSET+(row*y), BRICK_WIDTH, BRICK_HEIGHT);
			rect.setFilled(true);
			if (row < 2) {
				rect.setColor(Color.RED);}
			if (row <4 && row >=2) {
				rect.setColor(Color.ORANGE);}
			if (row <6 && row >=4) {
				rect.setColor(Color.YELLOW);}
			if (row <8 && row >=6) {
				rect.setColor(Color.GREEN);}
			if (row <10 && row >=8) {
				rect.setColor(Color.CYAN);}
			add (rect);
			}
		}
	}
}
