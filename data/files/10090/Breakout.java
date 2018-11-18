/*
 * File: Breakout.java
 * -------------------
 * Name: Abla Ghaleb
 * Section Leader: Thariq Ridha
 * 
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
	public static final double DELAY = 500.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	/*
	 * Sets up game and makes it run until no brick is left
	 * or the number of turns has reached its maximum.
	 */
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp();
		for (int turnNumber = 0 ; (turnNumber < NTURNS) && (!gameIsWon()) ; turnNumber ++) {
			createBall();
			waitForClick();	
			vy = VELOCITY_Y; 
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);			
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			while((!turnIsOver()) && (!gameIsWon())) {
				moveBall(vx, vy);	
				bounceOffWalls();
				checkForCollisions();
				pause(DELAY);
			}
			remove(ball);
			ball = null;
		}
		
		
	}
		
	/*
	 * Sets the screen up before beginning of the game by adding the bricks, the paddle, 
	 * and the ball.
	 * 
	 */

	private void setUp() {
		makeBricks();
		addPaddle();
		addMouseListeners();
			
	}
	
	/*
	 * Sets up the bricks so that, if more bricks are added, the coloring is
	 * "rebooted".
	 */
	
	private void makeBricks() {
		for (int rowNumber = 0; rowNumber < NBRICK_ROWS; rowNumber++) {
			for (int columnNumber = 0; columnNumber < NBRICK_COLUMNS; columnNumber++) {
				double x0 = getWidth()/2 - BRICK_SEP*(0.5+((NBRICK_COLUMNS/2.0) - 1)) - (NBRICK_COLUMNS*BRICK_WIDTH)/2.0;
				double x = x0 + columnNumber*(BRICK_WIDTH +BRICK_SEP);
				double y = BRICK_Y_OFFSET + rowNumber*(BRICK_HEIGHT +BRICK_SEP);
				drawBrick(x,y);
					if(((rowNumber % 10.0) == 0) || ((rowNumber%10.0) == 1)) {
					brick.setColor(Color.RED);
					}
					if(((rowNumber % 10.0) == 2) || ((rowNumber%10.0) == 3)) {
					brick.setColor(Color.ORANGE);
					}
					if(((rowNumber % 10.0) == 4) || ((rowNumber%10.0) == 5)) {
					brick.setColor(Color.YELLOW);
					}
					if(((rowNumber % 10.0) == 6) || ((rowNumber%10.0) == 7)) {
					brick.setColor(Color.GREEN);
					}
					if(((rowNumber % 10.0) == 8) || ((rowNumber%10.0) == 9)) {
					brick.setColor(Color.CYAN);
					}
			}
		}
	}
	
	/*
	 * Method: draws a brick
	 * Input: coordinates
	 */
	
	private void drawBrick(double x, double y) {
		brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		add(brick);
		brick.setFilled(true);
	}
	
	/*
	 * Method: adds paddle to screen
	 * 
	 */
	private void addPaddle() {
		double x = getWidth()/2.0 - PADDLE_WIDTH/2.0;
		double y = getHeight()- PADDLE_Y_OFFSET;
		paddle = new GRect (x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		add(paddle);
		paddle.setFilled(true);
		
	}
	/*
	 * Methods: create a ball and adds it to screen.
	 */
	private void createBall() {
		double x = getWidth()/2.0 - BALL_RADIUS;
		double y = getHeight()/2.0 - BALL_RADIUS;
		ball = new GOval (x, y, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);

	}
	
	/*
	 * Mouse even that makes the paddle follow the mouse.
	 * 
	 */
	
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if ((x < (getWidth() - PADDLE_WIDTH)) && (x > 0)) {
			paddle.setLocation(x,getHeight()- PADDLE_Y_OFFSET);
		}
	}
	
	/*
	 * Method: makes ball move
	 * Inputs: velocity of the ball in both coordinates.
	 */
	private void moveBall(double vx, double vy) {
		if (ball != null) {
			ball.move(vx,vy);
		}
			
	}
	
	/*
	 * Method: makes ball bounce off all walls except the bottom one.
	 * Also sets the boolean that checks what object was last hit by the paddle to false.
	 */
	private void bounceOffWalls() {
		if(ball.getY() < 0) {
			vy = - vy;
			lastHitWasPaddle = false;
		}
		if((ball.getX() < 0) || (ball.getX() + 2*BALL_RADIUS > getWidth())) {
			vx = - vx;
			lastHitWasPaddle = false; 
		}
		
	}
	
	/*
	 * Get the colliding object, changes y direction if the collider is the paddle, 
	 * removes it otherwise.
	 */
	
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		if (collider != null) {
			if (collider == paddle) {
				if(!lastHitWasPaddle) {
					vy = -vy;
					lastHitWasPaddle = true;
				}
			} else { 
				/*
				 *  We should remove the collider, not
				 * the brick, since brick refers to the last brick stored, i.e.
				 * the brick on the far right of the last row.
				 */
				
				remove(collider);
				bricksPresent = bricksPresent-1;
				vy = - vy;
				lastHitWasPaddle = false;
			}
		}
			
	}
		
	/*
	 * Method that checks if the ball has collided with any object and
	 * returns the colliding object.
	 */
	
	private GObject getCollidingObject() {
		double x1 = ball.getX();
		double y1 = ball.getY();
		double x2 = ball.getX() + 2*BALL_RADIUS;
		double y2 = ball.getY() + 2*BALL_RADIUS;
		GObject collider1 = getElementAt(x1,y1);
		GObject collider2 = getElementAt(x1,y2);
		GObject collider3 = getElementAt(x2,y1);
		GObject collider4 = getElementAt(x2,y2);
		if (collider1 != null) {
			return collider1;
		} else if(collider2 != null) {
				return collider2;
			} else if(collider3 != null) {
					return collider3;
				} else if(collider4 != null) {
						return collider4;
					} else {
						return null;
					}
	}
	
	/*
	 * Returns whether the player has ended their turn.
	 */
	
	private boolean turnIsOver() {
		return (ball.getY()  > getHeight());
						
	}
	
	/*
	 * Returns whether the player has won the game.
	 */
	
	private boolean gameIsWon() {
		return (bricksPresent == 0);
	}
	

	private GRect brick;
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx;
	private double vy;
	private int bricksPresent = NBRICK_COLUMNS * NBRICK_ROWS ;
	private boolean lastHitWasPaddle = false;
}
