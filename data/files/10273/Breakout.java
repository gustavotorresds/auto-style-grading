/*
 * File: Breakout.java
 * -------------------
 * Name: Gene Tanaka
 * Section Leader: Peter Maldonado
 * 
 * This code runs the game Breakout. User clicks to start playing. User is given three
 * lives. The number of lives remaining is shown on the bottom left. The game displays
 * the score at the top. The ball changes to the color of the brick that it breaks.
 * The ball speeds up every 5 bricks that are broken. The ball makes sound effects every
 * time it hits the paddle or a brick. Large text tells the user when they have lost or 
 * won the game.
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
	public static final double VELOCITY_Y = 4.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 2.0;
	public static final double VELOCITY_X_MAX = 4.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;



	/* Instance variable must be created to track paddle, ball, bricks and colliders
	 ** throughout entire program. */
	private GRect paddle = null;
	private GOval ball = null;
	private GRect brick = null;
	private GObject collider = null;
	/* It is helpful for us to refer to the counter and velocities throughout the entire program */
	private int counter = 0;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GLabel score = new GLabel("Bricks Broken: " + 0 + " of " + NBRICK_COLUMNS*NBRICK_ROWS);
	private GLabel lives = new GLabel("Lives Remaining: " + 3);

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setBricks();
		addMouseListeners();
		makePaddle();

		// For loop used for the game to have three turns/lives.
		for (int i = 0; i < 3; i++) {
			lives.setFont("SansSerif-12");
			lives.setLocation(lives.getWidth()/8,getHeight()-lives.getAscent()/2);
			// Updates label with number of lives left every turn.
			lives.setLabel("Lives Remaining: " + (3-i));
			add(lives);
			makeBall();
			moveBall();
		}
		lives.setLabel("Lives Remaining: " + 0);
		if (youLose()) {
			GLabel lose = new GLabel("YOU LOSE");
			lose.setFont("SansSerif-30");
			lose.setLocation(getWidth()/2-lose.getWidth()/2,getHeight()-lose.getAscent()*6);
			add(lose);
		}
	}

	private boolean topWall() {
		return ball.getY() < 0;
	}

	private boolean leftWall() {
		return ball.getX() < 0;
	}

	private boolean rightWall() {
		return ball.getX() > getWidth() - 2*BALL_RADIUS;
	}

	private void setBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double x = (getWidth()/2 - (BRICK_WIDTH+BRICK_SEP)*(NBRICK_COLUMNS)/2 + BRICK_SEP/2 + (BRICK_WIDTH + BRICK_SEP)*col);
				double y = BRICK_Y_OFFSET + BRICK_HEIGHT*row + BRICK_SEP*row;
				brick = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);
				// This part is tricky to decompose, but essentially here I ensure that the ratio of colors
				// for the bricks will be the same no matter the number of rows and columns.
				if (row < (NBRICK_ROWS*.2)) {
					brick.setColor(Color.RED);
				}if (row < (NBRICK_ROWS*.4) && row >= (NBRICK_ROWS*.2)) {
					brick.setColor(Color.ORANGE);
				} if (row < (NBRICK_ROWS*.6) && row >= (NBRICK_ROWS*.4)) {
					brick.setColor(Color.YELLOW);
				} if (row < (NBRICK_ROWS*.8) && row >= (NBRICK_ROWS*.6)) {
					brick.setColor(Color.GREEN);
				} if (row < (NBRICK_ROWS) && row >= (NBRICK_ROWS*.8)) {
					brick.setColor(Color.CYAN);
				}
			}
		}

	}

	private void makePaddle() {
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setLocation((getWidth()-PADDLE_WIDTH)/2,getHeight()-PADDLE_Y_OFFSET);
		add(paddle);
	}

	public void mouseMoved(MouseEvent e) {
		int x_paddle = e.getX();
		// If-statement ensures that paddle does not go off the screen. 
		if (x_paddle < getWidth()-PADDLE_WIDTH/2 && x_paddle > PADDLE_WIDTH/2) {
			paddle.setLocation(x_paddle - PADDLE_WIDTH/2,getHeight()-PADDLE_Y_OFFSET);
		} else {
			if (x_paddle < getWidth()/2) {
				paddle.setLocation(0,getHeight()-PADDLE_Y_OFFSET);
			} else {
				paddle.setLocation(getWidth()-PADDLE_WIDTH,getHeight()-PADDLE_Y_OFFSET);
			}	
		}
	}

	private void makeBall() {
		ball = new GOval (BALL_RADIUS*2,BALL_RADIUS*2);	
		ball.setFilled(true);
		ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
		add(ball);
	}

	private void moveBall() {
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;

		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

		GLabel start = new GLabel("CLICK TO START");
		start.setFont("SansSerif-30");
		start.setLocation(getWidth()/2-start.getWidth()/2,getHeight()-start.getAscent()*6);
		add(start);
		waitForClick();
		remove(start);

		// Game goes on as long as ball does not hit bottom of window, and bricks remain.
		while (!youLose() && counter < NBRICK_COLUMNS*NBRICK_ROWS) {
			ball.move(vx,vy);
			if (topWall()) {
				vy = -vy;
			} 
			if  (rightWall() || leftWall()) {
				vx = -vx;
			}
			collider = getCollidingObject();
			if (collider != null) {
				bounceClip.play();
				if (collider == paddle) {
					/* Ball should only hit top of paddle, during which velocity in 
					y direction would always be positive. Due to absolute value, ball
					will never change directions to go down. */
					vy = -Math.abs(vy);
				} else {
					// If-statement ensures that score and lives labels don't interact with ball.
					if (collider != score && collider != lives) {
						remove(collider);
						vy = -vy;
						ball.setColor(collider.getColor());
						counter++;
						// Every 5 blocks, vx multiplies by 1.25 to make game challenging.
						if (counter%5 == 0) {
							vx = 1.25*vx;
						}
					}
				}
			}
			pause(DELAY);
			
			// Score updates every time brick is broken
			score.setText("Bricks Broken: " + counter + " of " + NBRICK_COLUMNS*NBRICK_ROWS);
			score.setLocation((getWidth()-score.getWidth())/2, score.getAscent());
			add(score);
		}
		
		// Ball disappears when bottom of screen is hit.
		remove(ball);
		if (counter == NBRICK_COLUMNS*NBRICK_ROWS) {
			GLabel win = new GLabel("YOU WIN");
			win.setFont("SansSerif-30");
			win.setLocation(getWidth()/2-win.getWidth()/2,getHeight()-score.getAscent()*6);
			add(win);
		}
	}

	private boolean youLose() {
		return ball.getY() > getHeight() - 2*BALL_RADIUS;
	}

	private GObject getCollidingObject() {
		// Accounts for all 4 corners of ball in collision, and produces object that collides
		// at each corner accordingly.
		GObject topLeft = getElementAt(ball.getX(),ball.getY());
		GObject topRight = getElementAt(ball.getX() + 2*BALL_RADIUS,ball.getY());
		GObject bottomLeft = getElementAt(ball.getX(),ball.getY() + 2*BALL_RADIUS);
		GObject bottomRight = getElementAt(ball.getX() + 2*BALL_RADIUS,ball.getY() + 2*BALL_RADIUS);
		if (topLeft != null) {
			return topLeft;
		} else if (topRight != null) {
			return topRight;
		} else if (bottomLeft != null) {
			return bottomLeft;
		} else if (bottomRight != null) {
			return bottomRight;
		} else {
			return null;
		}
	}

}
