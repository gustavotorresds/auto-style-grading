/*
 * File: Breakout.java
 * -------------------
 * Name: Nick Hubbard
 * Section Leader: Julia Daniel
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
	public static final int ROWCOLORREPEAT = 2;
	Color color = null;

	// Add the paddle in the middle of the screen at the bottom
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	// Add the ball in the middle of the screen
	private GOval ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);

	// Add velocity variables for the ball
	private double vx = 0;
	private double vy = 0;

	// Add counter for number of bricks and turns left
	private int bricksLeft = 0;
	private int bricksHit = 0;
	private int turns = NTURNS;
	
	// Add bounce sound clip
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	private GLabel message = null;
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Set up rows of bricks with specified colors
		lay2BrickRows(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN);

		// Set up the paddle
		addMouseListeners();
		addPaddle();

		// Set up the ball
		addNewBall();

		// Ball-moving engine
		while(bricksLeft > 0) {
			moveBall();
		}
		winGame();
	}

	/* Moves the ball around the screen based on its current x- and y-velocities
	 * Bounces the ball off of the top, left, and right walls
	 * Also bounces ball off of paddle and bricks.
	 */
	private void moveBall() {
			ball.move(vx, vy);
			pause(DELAY);
			checkForWall();
			collisionBouncing();
	}
	
	// Adds the paddle, per specs, in the bottom center of the screen
	private void addPaddle() {
		paddle.setCenterX(getWidth()/2);
		paddle.setBottomY(getHeight());
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}
	
	// Adds the ball, per specs, in the middle of the screen 2/3 of the way down (to allow for messages)
	private void addNewBall() {
		ball.setCenterX(getWidth()/2);
		ball.setCenterY(2*getHeight()/3);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}
	
	/* Checks to see whether the ball has collided with any objects by seeing if 
	 * objects exist at the NE corner of the ball when ball is moving NE, the
	 * NW corner when it is moving NW, etc.
	 */
	private void collisionBouncing() {
		double x = ball.getX();
		double y = ball.getY();

		int i = 0;
		int j = 0;
		
		if(vx < 0 && vy < 0) {
			i = 0;
			j = 0;
		}
		if(vx > 0 && vy < 0) {
			i = 1;
			j = 0;
		}
		if (vx < 0 && vy > 0) {
			i = 0;
			j = 1;
		}
		if (vx > 0 && vy > 0) {
			i = 1;
			j = 1;
		}
		
		ballCollision(x + i*2*BALL_RADIUS, y + j*2*BALL_RADIUS);

	}

	/* Removes bricks if there's a collision with a brick and bounces off
	 * the paddle if there is a collision with the paddle.
	 */
	private void ballCollision(double x, double y) {
		GObject collider = getCollidingObject(x, y);
		if (collider == null){
		}
		if (collider == paddle) {
			vy = -vy;
			bounceClip.play();
		}
		if ((collider != null) && (collider != paddle)) {
			remove (collider);
			bricksLeft -= 1;
			bricksHit += 1;
			vy = -vy;
			bounceClip.play();
		}
	}

	// Checks for a collision at given points x, y	
	private GObject getCollidingObject(double x, double y) {
		if(getElementAt(x, y) == null) {
			return null;
		}
		else {
		GObject collider = getElementAt(x, y);
		return collider;
		}
	}

	// Bounces the ball off the 3 walls
	private void checkForWall() {
		
		// if the ball hits the left wall, send it right
		if(vx < 0 && ball.getX() <= 0) {
			vx = -vx;
		}
		
		// if the ball hits the right wall, send it left
		if (vx > 0 && ball.getX() >= getWidth()-(BALL_RADIUS*2)) {
			vx = -vx;
		}
		
		// if the ball hits the top wall, send it down
		if (vy < 0 && ball.getY() <= 0) {

			vy = -vy;
		}
		
		// if the ball hits the ground, display relevant error message
		if (vy >0 && ball.getY() >= getHeight()-(BALL_RADIUS*2)) {	
			ball.setCenterX(getWidth()/2);
			ball.setCenterY(2*getHeight()/3);
			vx = 0;
			vy = 0; 
			loseTurn();
		}
	}

	// Displays a message if the game is over because the user removed all of the bricks
	private void winGame() {
		int turnsUsed = 3 - turns;
		GLabel winGame = new GLabel("Game over! You won on " + turnsUsed + " turns. Congratulations!");
		add(winGame);
		double x = getWidth()/2 - winGame.getWidth()/2;
		double y = getHeight()/2 - winGame.getAscent()/2;
		winGame.setLocation(x, y);
	}
	
	// Displays messages if the ball hits the floor
	private void loseTurn() {
		turns -= 1;
		if (turns == 0) {
			GLabel loseGame = new GLabel("Game over! You hit " + bricksHit + " and had " + bricksLeft + " remaining. Click to replay.");
			add(loseGame);
			double x = getWidth()/2 - loseGame.getWidth()/2;
			double y = getHeight()/2 - loseGame.getAscent()/2;
			loseGame.setLocation(x, y);
		}
		if (turns > 0) {
			GLabel loseTurn = new GLabel("You missed! You have " + turns + " turns remaining. Click to start next ball.");
			add(loseTurn);
			double x = getWidth()/2 - loseTurn.getWidth()/2;
			double y = getHeight()/2 - loseTurn.getAscent()/2;
			loseTurn.setLocation(x, y);
		}

		
	}
	
	private void startBall() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
	}

	// Random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	
	// Removes messages and starts the ball when the mouse is clicked
	public void mouseClicked(MouseEvent e) {
		if(turns == 0) {
			turns = 3;
		}
		startBall();
	}
	
	public void mouseMoved(MouseEvent e) {

		// Get x-coordinate of paddle. If paddle is close to edge, make sure it doesn't go off screen
		double x = e.getX();

		if(x <= PADDLE_WIDTH/2) {
			x = PADDLE_WIDTH/2;
		}
		if(x >= getWidth() - PADDLE_WIDTH/2) {
			x = getWidth() - PADDLE_WIDTH/2;
		}	
		paddle.setCenterX(x);
	}

	// Lay 5 x 2 rows of brick columns per specs, in 5 different colors
	private void lay2BrickRows(Color color1, Color color2, Color color3, Color color4, Color color5) {
		for (int i = 0; i<(NBRICK_ROWS); i++) {
			if (i/ROWCOLORREPEAT < 1) {
				color = color1;
			}
			else {
				if	(i/ROWCOLORREPEAT < 2) {
					color = color2;
				}
				else {
					if(i/ROWCOLORREPEAT < 3) {
						color = color3;
					}
					else {
						if(i/ROWCOLORREPEAT < 4) {
							color = color4;
						}
						else {
							color = color5;
						}
					}
				}
			}									
			layBricks(BRICK_SEP, BRICK_Y_OFFSET + i*(BRICK_HEIGHT + BRICK_SEP), color);
		}
	}

	// Lay a row of bricks across, starting at position x, y and continuing for an integer number of bricks
	private void layBricks(double xStart, double yStart, Color color) {
		for (int i = 0; i<NBRICK_COLUMNS;i++) {
			GRect brick = new GRect(xStart + i*(BRICK_WIDTH + BRICK_SEP), yStart, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
			bricksLeft += 1;
		}
	}
}
