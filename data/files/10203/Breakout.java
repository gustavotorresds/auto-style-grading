/*
 * File: Breakout.java
 * -------------------
 * Name: Sharon Tran
 * Section Leader: Avery Wang
 * 
 * This file implements the game of Breakout, in which
 * the player attempts to remove all of the colored bricks
 * on the screen by hitting them with a ball. The ball can 
 * only be controlled by bouncing off of bricks, walls, and 
 * a paddle controlled by the user. 
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
	public static final double PADDLE_WIDTH = 60; //60
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

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";
	
	// Private instance variables
	private GRect paddle = null; 
	private GOval ball = null;
	private double vx, vy; 
	private int bricksLeft = NBRICK_COLUMNS * NBRICK_ROWS;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpGame();
		
		addMouseListeners();
		
		playGame();
	}
	
	/*
	 * Method: Gameplay
	 * -----------------
	 * Generates ball and sets it in motion. Also
	 * checks ball's collisions with walls, bricks, 
	 * and paddle to determine next action. 
	 */
	private void playGame() {
		ball = createBall();
		
		GLabel direction = new GLabel("Click to begin.");
		add(direction, getWidth()/2-direction.getWidth()/2, getHeight()/2-BALL_RADIUS*2);
		waitForClick(); 
		remove(direction);
		
		// Sets vx as a random double between min velocity and max velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		// Makes vx direction negative half of the time
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		
		vy = VELOCITY_Y;
		
		int turns = NTURNS;
		
		while (true) {
			
			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx; 
			}
			if (hitTopWall(ball)) {
				vy = -vy; 
			}
			 
			checkCollisions();
			
			if (hitBottomWall(ball)) { 
				remove(ball);
				turns--; 
				if (turns > 0 && turns <= 3) {
					pause(500);
					ball = createBall();
					pause(1000);
				} else {
					GLabel loss = new GLabel ("You lose.");
					add(loss, getWidth()/2-loss.getWidth()/2, getHeight()-PADDLE_Y_OFFSET+PADDLE_HEIGHT+BRICK_SEP);
				}
			}
			
			ball.move(vx, vy);
			
			pause(DELAY);
			
		}
	}

	
	/*
	 * Method: Hit Bottom Wall
	 * -----------------
	 * Returns whether or not the ball hits
	 * the bottom wall, which means the loss 
	 * of a turn.
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - ball.getWidth(); 
	}

	/*
	 * Method: Check Collisions
	 * -----------------------
	 * Checks the object with which the ball collided. 
	 * If the collider is a paddle, the ball will simply reverse its y-direction. 
	 * If the collider is a brick, the ball will remove the brick before
	 * it reverses its y-direction. 
	 */
	private void checkCollisions() { 
		GObject collider = getCollidingObjects();
		if (collider != null) {
			if (collider == paddle) { 
				vy = -vy;
			} else {
				remove(collider); 
				vy = -vy;
				bricksLeft--; 
			}
			if (bricksLeft == 0) {
				GLabel win = new GLabel("You win!");
				add(win, getWidth()/2-win.getWidth()/2, getHeight()-PADDLE_Y_OFFSET+PADDLE_HEIGHT+BRICK_SEP);
			}
		}
		
	}

	/*
	 * Method: Get Colliding Objects
	 * -------------------
	 * Returns an object named "collider" to method
	 * checkCollisions() so that it may check 
	 * if the collider is a paddle or brick. 
	 * Checks all four corners of GOval for 
	 * colliders.
	 */
	private GObject getCollidingObjects() {
		GObject collider = getElementAt(ball.getX(), ball.getY());
		if (collider == null) {
			collider = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		}
		if (collider == null) {
			collider = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);
		}
		if (collider == null) {
			collider = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
		}
		return collider;
	}

	/*
	 * Method: Hit Top Wall
	 * ------------------
	 * Returns whether or not the ball should bounce 
	 * off of the top wall. 
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0; 
	}

	/*
	 * Method: Hit Right Wall
	 * ---------------------
	 * Returns whether or not the ball should bounce off
	 * the right wall. 
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	/*
	 * Method: Hit Left Wall
	 * -----------------
	 * Returns whether or not the ball should bounce off
	 * the left wall. 
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0; 
	}

	/*
	 * When mouse is moved, the method gets the x coordinate of the mouse's
	 * position and sets it as the x-coordinate of the paddle's top left
	 * corner.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX <= getWidth()-PADDLE_WIDTH) {
			paddle.setLocation(mouseX, getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
		}
	}

	/*
	 * Method: Sets up game.
	 * ----------------
	 * Creates the colored bricks and the paddle for gameplay. 
	 */
	private void setUpGame() {
		createBricks();
		paddle = createPaddle(); 
		add(paddle, getWidth()/2-PADDLE_WIDTH/2, getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
	}

	/*
	 * Method: Make a ball. 
	 * ------------------
	 * Create a ball in the center of the canvas.
	 */
	private GOval createBall() {
		GOval ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball, getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS);
		return ball;
	}

	/*
	 * Method: Make a paddle
	 * --------------------
	 * Creates a filled paddle. 
	 */
	private GRect createPaddle() { 
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle; 
	}

	/*
	 * Method: Make Bricks
	 * ------------------
	 * Creates bricks and colors them according to 5 given colors. 
	 */
	private void createBricks() {
		// The space between first brick and left wall needed to center the rows of bricks
		double starterSpace = (getWidth()-((NBRICK_ROWS*BRICK_WIDTH)+((NBRICK_ROWS-1)*BRICK_SEP)))/2;
		
		for (double j=0; j<NBRICK_ROWS; j++) {  
			for(double i=0; i<NBRICK_COLUMNS; i++) {
				double col = starterSpace + (i*BRICK_WIDTH + i*BRICK_SEP); 
				double row = BRICK_Y_OFFSET+j*(BRICK_HEIGHT+BRICK_SEP);
				GRect brick = new GRect(col, row, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick); 
				brick.setFilled(true);
				if (j==0 || j==1) { 
					brick.setColor(Color.RED);
				}
				if (j==2 || j==3) {
					brick.setColor(Color.ORANGE);
				}
				if (j==4 || j==5) {
					brick.setColor(Color.YELLOW);
				}
				if (j==6 || j==7) {
					brick.setColor(Color.GREEN);
				}
				if (j==8 || j==9) {
					brick.setColor(Color.CYAN);
				}
			}
		}		
	}
}
