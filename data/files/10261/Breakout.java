/*
 * File: Breakout.java
 * -------------------
 * Name: Matthew Smith 2/5/18
 * Section Leader: James Mayclin
 * 
 * This file is my version of the game Breakout, producing a
 * paddle (mouse-tracked), destructible blocks, and an animated
 * ball for real-time play.
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
	public static final double DELAY = 1000.0 / 120.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	// Instance Variables: Paddle and Ball; Ball's vx and vy; rgen for vx
	
	GRect paddle = null;
	
	GOval ball = null;
	
	double ball_vx, ball_vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	

	public void run() {
		
		// Mouse functionality for paddle
		addMouseListeners();
		
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Game Set Up - creates static paddle and blocks
		setUpGame();
		// Game Play - creates moving ball and paddle
		playGame();
	}
	/** Method: Set Up Game
	 * --------------------
	 * This method sets up the game for play.
	 * It generates the wall of bricks and the
	 * paddle.
	 */
	
	private void setUpGame() {
		
		// These are the bricks
		createColoredRows(Color.RED, 0);
		createColoredRows(Color.ORANGE, 2);
		createColoredRows(Color.YELLOW, 4);
		createColoredRows(Color.GREEN, 6);
		createColoredRows(Color.CYAN, 8);
		
		// And now the paddle
		createPaddle();		
}
	/** Method: Create Colored Rows
	 * ----------------------------
	 * This method creates two rows 
	 * of colored bricks when given
	 * a specified parameter color.
	 */
	
	private void createColoredRows (Color color, double y_multi) {
		double block_y = BRICK_Y_OFFSET + (y_multi * ( BRICK_HEIGHT + BRICK_SEP));
		for ( int numColoredRows = NBRICK_ROWS / 5; numColoredRows > 0; numColoredRows--) {
			double block_x = (CANVAS_WIDTH - ((10 * BRICK_WIDTH) + (10 * BRICK_SEP))) / 2;
			for ( int N_BRICK_COLUMNS = 10; N_BRICK_COLUMNS > 0; N_BRICK_COLUMNS--) {
				GRect block = new GRect (block_x, block_y, BRICK_WIDTH, BRICK_HEIGHT);
				block.setColor(color);
				block.setFilled(true);
				add(block);
				block_x += (BRICK_WIDTH + BRICK_SEP);
			}
			block_y += (BRICK_HEIGHT + BRICK_SEP);
		}
}
	
	/** Method: Create Paddle
	 * ----------------------
	 * This method creates a 
	 * paddle and centers it 
	 * at the bottom of the
	 * screen.
	 */
	
	private void createPaddle() {
		double init_x = (CANVAS_WIDTH - PADDLE_WIDTH) / 2;
		double init_y = (CANVAS_HEIGHT - PADDLE_Y_OFFSET);
		paddle = new GRect (init_x, init_y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);	
	}
	
	/**
	 * Method: Move Paddle
	 * -------------------
	 * This method allows for
	 * the x-position of the 
	 * paddle to change with
	 * the mouse cursor's 
	 * movements, bounded within
	 * the canvas width.
	 */
	
	public void mouseMoved(MouseEvent e) {
		double paddle_x = (CANVAS_WIDTH - PADDLE_WIDTH) / 2;
		double paddle_y = (CANVAS_HEIGHT - (PADDLE_HEIGHT + PADDLE_Y_OFFSET));
		paddle_x = e.getX() - PADDLE_WIDTH / 2;
		if (paddle_x <= (CANVAS_WIDTH - PADDLE_WIDTH) && paddle_x >= 0) {
		paddle.setLocation(paddle_x, paddle_y);
		}
		else {
		}
	}
	
	/**
	 * Method: Play Game
	 * ----------------
	 * This method basically
	 * allows for the playing of
	 * the game, creating a ball,
	 * animating that ball, allowing
	 * for collisions between ball,
	 * paddle, and block, and the removal
	 * of hit blocks.
	 */
	
	private void playGame() {
		
		// Creation and centering of ball
		makeBall();
		
		// Initialization of ball vx and vy
		ball_vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) ball_vx = -ball_vx;
		
		ball_vy = VELOCITY_Y;
		
		// Blocks hit variable
		int block_killcount = 100;
		
		// Turns number variable
		int turn_count = NTURNS;
		
		while (turn_count > 0 && block_killcount > 0) {
			
			// The initial movement of the ball
			ball.move(ball_vx, ball_vy);
			
			
			// Bounce Conditionals for when ball strikes wall
			if (ball.getX() <= 0 || ball.getX() >= (CANVAS_WIDTH - (2 * BALL_RADIUS))) {
				ball_vx = -ball_vx;
			}
			if (ball.getY() <= 0) {
				ball_vy = -ball_vy;
			}
			
			// Lose turn condition for when ball passes bottom of screen
			if (ball.getY() >= (CANVAS_HEIGHT - (2 * BALL_RADIUS))) {
				remove(ball);
				makeBall();
				
				// Turn subtractor
				turn_count--;
				
			}
			
			// Object Collision Checker
			GObject collider = getCollidingObject();
			if (collider != null) {
				if (collider == paddle) {
					ball_vy = -ball_vy;
				} else {
					ball_vy = -ball_vy;
					remove(collider);
					block_killcount--;
				}
			}
			pause(DELAY);
		}
		
		// Lose Condition :(
		if (turn_count <= 0 ) {
			youLose();
		}
								
		// Win Condition :)
		if (block_killcount <= 0) {
			youWin();
		}
		
			
		}
	
	/**
	 * Method: Get Colliding Object
	 * ----------------------------
	 * This method sequentially tests the 
	 * four corners of the hitbox of the 
	 * ball for contact with any GObjects. 
	 * If there is contact at any of the
	 * corners, the GObject that has been
	 * hit by the ball is returned. If there
	 * is no contact at all, null is returned.
	 */
	
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return(getElementAt(ball.getX(), ball.getY()));
		} else {
			if (getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY()) != null) {
				return(getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY()));
			} else {
				if (getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS)) != null) {
					return(getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS)));
				} else {
					if (getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS)) != null) {
						return(getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS)));
		
					} else {
						return(null);
					}
				}
			}	
		}			
	}
	
	/**
	 * Method: Make Ball
	 * -----------------
	 * This method makes a
	 * ball and centers it
	 * in the middle of the
	 * screen.
	 */
	
	private void makeBall() {
		double ball_x = (getWidth() - (2 * BALL_RADIUS)) / 2;
		double ball_y = (getHeight() - (2 * BALL_RADIUS)) / 2;
		ball = new GOval (ball_x, ball_y, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}
	
	/**
	 * Method: You Win
	 * ---------------
	 * This method covers
	 * up the screen with 
	 * a white GRect and
	 * displays a happy
	 * victory message!
	 */
	
	private void youWin() {
		
		// Removes ball and paddle
		remove(ball);
		remove(paddle);
		
		// Fresh Canvas
		GRect YouWin = new GRect (0,0,getWidth(),getHeight());
		YouWin.setFilled(true);
		add(YouWin);
		
		// Happy Win Message
		GLabel Victory = new GLabel ("Hurray! You've Won!", getWidth() / 2, getHeight() / 2);
		Victory.setFont(SCREEN_FONT);
		Victory.setColor(Color.WHITE);
		add(Victory);
		
	}
	
	/**
	 * Method: You Lose
	 * ----------------
	 * This method covers
	 * up the screen with a 
	 * white GRect and displays
	 * a sad lose message.
	 */
	
	private void youLose() {
		
		// Removes paddle and ball
		remove(ball);
		remove(paddle);
		
		// Fresh Canvas
		GRect YouLose = new GRect (0,0,getWidth(),getHeight());
		YouLose.setFilled(true);
		add(YouLose);
		
		GLabel Defeat = new GLabel ("Yikes! You've Lost!", getWidth() / 2, getHeight() / 2);
		Defeat.setFont(SCREEN_FONT);
		Defeat.setColor(Color.WHITE);
		add(Defeat);
	}
}
