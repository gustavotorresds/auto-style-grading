/*
 * File: Breakout.java (extension)
 * -------------------
 * Name: Leila Doty
 * Section Leader:
 * 
 * This file implements the extension for Breakout */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
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

	//Number of rows per colored stripe
	public static final double ROW_INC  = 2;

	// Dimensions of the paddle, in pixels
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom, in pixels
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball, in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Number of bricks on screen
	public static final int NBRICK = NBRICK_ROWS * NBRICK_COLUMNS;

	// Adds bounce sounds for when ball collides
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	
	// Creates breakout label
	GLabel breakout = new GLabel("BREAKOUT!");

	/* Instance variable for x, y position and vx, vy velocity */
	private double x, y;
	private double vx, vy;

	/* Initializes random generator */
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/* Initializes paddle and ball */
	GRect paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
	GOval ball = new GOval (x, y, BALL_RADIUS, BALL_RADIUS);

	/* Initializes the instance variable 'lives' which is the 
	 * number of turns the user has left */
	int lives = NTURNS;

	/* Initializes the instance variables 'rembricks' which is 
	 * the number of bricks remaining on the screen */
	int rembricks = NBRICK;

	/* Run method; creates canvas, sets up the game, and plays
	 * the game */
	public void run() {
		setTitle("BREAKOUT!");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUp();
		addMouseListeners();
		playGame();
	}

	/* Adds the rows of bricks to the screen by calling on the 
	 * addRow method and uses if loops to set the changing row colors,
	 * add paddle to the screen */
	private void setUp() {
		int count = 0;
		for(int i = 0; i < NBRICK_ROWS + 1; i++) {
			y = BRICK_Y_OFFSET + BRICK_HEIGHT * count + count * BRICK_SEP;
			if(i <= 1) {
				addRow(Color.RED);
				count++;
			} else if (i > ROW_INC && i <= ROW_INC*2) {
				addRow(Color.ORANGE);
				count++;	
			} else if (i > ROW_INC*2 && i <= ROW_INC*3) {
				addRow(Color.YELLOW);
				count++;	
			} else if (i > ROW_INC*3 && i <= ROW_INC*4) {
				addRow(Color.GREEN);
				count++;	
			} else if (i > ROW_INC*4 && i <= NBRICK_ROWS) {
				addRow(Color.BLUE);
				count++;	
			}		
		}
		addPaddle();
		addBreakout();
	}

	/* Fills, adds, and sets paddle location */
	private void addPaddle() {
		paddle.setFilled(true);
		add(paddle);
		paddle.setColor(Color.LIGHT_GRAY);
		paddle.setLocation(getWidth()/2 - BRICK_WIDTH/2, getHeight() - BRICK_Y_OFFSET);
	}

	/* Adds rows of bricks to the screen by filling the bricks and 
	 * setting their location */
	private void addRow(Color color) {
		int count = 0;
		double m = getWidth()/2 - NBRICK_COLUMNS/2 * (BRICK_WIDTH+BRICK_SEP) + BRICK_SEP/2;
		for(int i = 0; i < NBRICK_COLUMNS; i++) {
			GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
			x = BRICK_WIDTH * count + count * BRICK_SEP + m;
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
			brick.setLocation(x, y);
			count++;
		}
	}

	/* Adds name title to top of screen */
	private void addBreakout() {
		breakout.setFont("Arial-24");
		add(breakout);
		breakout.setLocation(getWidth()/2 - breakout.getWidth()/2, BRICK_SEP*10);
	}

	/* Sets y-velocity, randomizes vx between established range,
	 * calls bounce and checkCollision to make ball move
	 * and remove bricks from screen when touched by the ball. 
	 * Removes ball from the screen when all bricks removed and
	 * prints a line in console to tell the user they won the game.
	 * If user loses, prints line in console to tell them they lost
	 * the game */
	private void playGame() {
		addBall();
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while (rembricks > 0 && lives > 0) {
			ball.move(vx,  vy);
			bounce();
			pause(DELAY);
			checkCollision();
			if (rembricks > 0 && lives == 0) {
				losingLabels();
			} 
		}
		if (rembricks == 0 && lives > 0) {
			remove(ball);
			winningLabels();
		}
	}

	/* Fills, adds, and sets ball location */
	private void addBall() {
		ball.setFilled(true);
		add(ball);
		ball.setLocation(getWidth()/2 - BALL_RADIUS/2, getHeight()/2 - BALL_RADIUS/2);
	}

	/* Makes the ball bounce and move in opposite composite x/y
	 * direction when hits the wall, subtracts a life when the
	 * ball hits the bottom wall and re-adds the ball to the screen
	 * if a life is lost. Ball changes color when it hits brick*/
	private void bounce() {
		if (ball.getX() < 0 || (ball.getX() + BALL_RADIUS) > getWidth()) {
			ball.setColor(Color.CYAN);
			vx = - vx;
			bounceClip.play();
		}
		if (ball.getY() < 0) {
			ball.setColor(Color.LIGHT_GRAY);
			vy = - vy;
			bounceClip.play();
		}
		if (ball.getY() + BALL_RADIUS > getHeight()) {
			remove(ball);
			lives--;
			if (lives > 0) {
				addBall();
			}
		}
	}

	/* Checks for collisions with the ball; if ball collides with paddle
	 * it bounces up, if the ball hits a brick its vy is reversed and the
	 * brick is removed and the count of bricks onscreen decreases by one.
	 * Speeds up the velocity of the ball after it hits the paddle 5 times.
	 * Ball changes colors when it hits the paddle or bricks*/
	private void checkCollision( ) {
		GObject collider = getCollidingObject();
		int paddleBounces = 0;
		if (collider == paddle) {
			ball.setColor(Color.PINK);
			vy = - vy;
			bounceClip.play();
			if (paddleBounces == 5) {
				vx = 10*vx;
				vy = 3*vy;
			}
		}
		if (collider != null && collider != paddle && collider != ball && collider != breakout) {
			ball.setColor(Color.MAGENTA);
			vy = - vy;
			remove(collider);
			rembricks--;
			bounceClip.play();
		}
	}

	/* Adds losing labels to the screen */
	private void losingLabels() {
		GLabel failed = new GLabel ("Mission: FAILED.");
		failed.setFont("SansSerif-18");
		add(failed);
		failed.setLocation(getWidth()/2 - failed.getWidth()/2, getHeight()/2);
		GLabel loser = new GLabel ("Too bad, you lose.");
		loser.setFont("SansSerif-18");
		loser.setLocation(getWidth()/2 - loser.getWidth()/2, getHeight()/2 + failed.getHeight());
		add(loser);
	}
	
	/* Adds winning labels to the screen */
	private void winningLabels() {
		GLabel success = new GLabel ("Misson: SUCCESS");
		success.setFont("SansSerif-18");
		add(success);
		success.setLocation(getWidth()/2 - success.getWidth()/2, getHeight()/2);
		GLabel winner = new GLabel ("Huzzah, you won!");
		winner.setFont("SansSerif-18");
		add(winner);
		winner.setLocation(getWidth()/2 - winner.getWidth()/2, getHeight()/2 + success.getHeight());

	}

	/* Checks the four corners of the ball and returns the x, y coordinates
	 * if there is an object at one of the corners */
	private GObject getCollidingObject() {
		GObject ltcor = getElementAt(ball.getX(), ball.getY());
		GObject lbcor = getElementAt(ball.getX() , ball.getY() + 2*BALL_RADIUS);
		GObject rtcor = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		GObject rbcor = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		if (ltcor != null) {
			return(ltcor);
		} else if (lbcor != null) {
			return(lbcor);
		} else if (rtcor != null) {
			return(rtcor);
		} else if (rbcor != null) {
			return(rbcor);
		}
		return ball; 
	}

	/* Sets the x coordinate of the paddle to the x coordinate of the mouse,
	 * stops paddle from going offscreen */
	public void mouseMoved(MouseEvent e) {
		double mx = e.getX();
		if (mx > getWidth() - PADDLE_WIDTH) {
			mx = getWidth() - PADDLE_WIDTH;
			paddle.setLocation(mx, getHeight() - BRICK_Y_OFFSET);
		}
		paddle.setLocation(mx, getHeight() - BRICK_Y_OFFSET);
	}	
}

