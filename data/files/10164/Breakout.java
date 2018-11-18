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

	//Instance variable for the paddle
	GRect paddle = null;

	//Instance variable for the ball
	GOval ball = null;

	//Private instance variables
	private RandomGenerator rg = new RandomGenerator();
	private double vx, vy;
	private int lives;
	private int bricks;
	
	//Audio clip for sound effects
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		setUpGame();
		playGame();
	}

	private void setUpGame() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setUpBricks();
		paddle = createPaddle();
		addPaddle();
		addMouseListeners();

	}

	//This method creates the bricks for the program.  It makes each row the appropriate color
	private void setUpBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double x = getWidth() - (BRICK_WIDTH * NBRICK_COLUMNS + 
						BRICK_SEP * (NBRICK_COLUMNS + 1)) + col * (BRICK_WIDTH + BRICK_SEP);
				double y = row * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET;
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(row <= 1) {
					brick.setColor(Color.RED);
				} else if(row <= 3) {
					brick.setColor(Color.ORANGE);
				} else if (row <= 5) {
					brick.setColor(Color.YELLOW);
				} else if (row <= 7) {
					brick.setColor(Color.GREEN);
				} else {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}	
		}	
	}

	//This method creates the paddle
	private GRect createPaddle() {
		GRect paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	//This method adds the paddle to the screen
	private void addPaddle() {
		double x = (getWidth() / 2) - (PADDLE_WIDTH / 2);
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}

	// This method makes the paddle move where the mouse moves
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		if (x < 0) {
			x = 0;
		}
		if (x > getWidth() - PADDLE_WIDTH) {
			x = getWidth() - PADDLE_WIDTH;
		}
		double y = (getHeight() - PADDLE_Y_OFFSET) - (BRICK_HEIGHT / 2);
		paddle.setLocation(x,y);
	}

	/**
	 * This method declares the amount of lives the user has, and then allows
	 * the user to play the game.  The method waits for the user to click, and 
	 * then releases the ball from the center of the screen.  The method 
	 * declares where the ball should move when it hits any of the four walls.
	 */
	private void playGame() {
		lives = NTURNS;
		bricks = NBRICK_ROWS * NBRICK_COLUMNS;
		
		//adds message to tell user to click to begin the game
		GLabel start = new GLabel("Click to Start");
		start.setLocation((getWidth() / 2) - (start.getWidth() / 2), (getHeight() / 2.6));
			add(start);;
		
		setUpBall();
		//update animation to make the ball bounce
		waitForClick();
		remove(start);
		while (bricks > 0) {
			checkForCollisions();
			if(hitLeftWall(ball) || hitRightWall(ball)) { 
				vx = -vx;
			}
			if (hitTopWall(ball)) {
				vy = - vy;
			}
			//move the ball
			ball.move(vx,  vy);
			//pause
			pause(DELAY);
		}
		if(bricks == 0) {
			GLabel glabel = new GLabel("Winner!");
			glabel.setLocation((getWidth() / 2) - (glabel.getWidth() / 2), (getHeight() / 2));
			add(glabel);
		}
	}

	/**This method sets up the ball in the center of the screen.  It sets
	 * a y coordinate velocity for the ball, and it generates a random x
	 * coordinate velocity for the ball when released.  
	 *  
	 */
	private void setUpBall() {
		//set up
		ball = makeBall();
		vx = rg.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rg.nextBoolean()) {
			vx *= -1;
		}
		vy = VELOCITY_Y;			
	}

	//returns whether the ball should hit off of the bottom wall
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	//returns whether the ball should hit off of the top wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	//returns whether the ball should hit off of the right wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	//returns whether the ball should hit off of the left wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	// makes the GOval for the ball
	public GOval makeBall() {
		GOval ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball, (getWidth() / 2) - BALL_RADIUS, (getHeight() / 2)
				- BALL_RADIUS);
		return ball;

	}

	/**This method checks to see if the ball has collided with something.
	 * If the ball collides with the bottom wall, the method takes away
	 * a life, and then sets the ball back up at its initial position.  If 
	 * the ball hits the paddle, the ball keeps going.  If the ball hits
	 * a brick, it removes the brick.
	 */
	private void checkForCollisions() {
		//hitBrick();
		if(hitBottomWall(ball)) {
			remove(ball);
			lives --;
			if (lives > 0) {
				setUpBall();
				waitForClick();
			}
		}

		GObject collider = getCollidingObject();
		if (collider == paddle) {
			bounceClip.play();
			vy *= -1;
		} else if (collider != null) {
			remove(collider);
			bounceClip.play();
			vy *= -1;
			bricks --;
		}
	}

	private GObject getCollidingObject() {
		GObject collObj = getElementAt(ball.getX(), ball.getY());
		if (collObj == paddle) {
			collObj = null;
		}
		if (collObj == null) {
			collObj = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
			if (collObj == paddle) {
				collObj = null;
			}
		}
		if (collObj == null) {
			collObj = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		}
		if (collObj == null) {
			collObj = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return collObj;
	}
}



