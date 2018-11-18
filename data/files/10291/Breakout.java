/*
 * File: Breakout.java
 * -------------------
 * Name: Jessica Seng
 * Section Leader: Niki Agrawal
 * 
 * This is the game of breakout. The ball bounces and collides. 
 * If all the bricks are gone. You win! You have three turns. 
 * If the ball goes past the paddle, you lose a turn 
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

	//ball diameter
	public static final double BALL_DIAM = 2*BALL_RADIUS;

	/** Added VARIABLES **/	
	//paddle 
	private GRect paddle = null;
	//ball
	private GOval ball;
	//generates random 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	//x velocity 
	private double vx;
	//y velocity
	private double vy;
	//brick counter to know when we win or lose 
	private int brick_counter;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setup();
		for (int i =0; i<NTURNS; i++) {
			moveBall();
			if (brick_counter == 0) {
				break;
			}
		}
	}

	public void init() {
		addMouseListeners();
		vx = rgen.nextDouble(1.0,3.0);
		vy = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		if (rgen.nextBoolean(0.5)) vy = -vy;

	}

	//moves paddle with mouse
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();

		if (mouseX < PADDLE_WIDTH/2.0) {
			paddle.setLocation(0, getHeight()-PADDLE_Y_OFFSET);
		} else if (mouseX > getWidth()-PADDLE_WIDTH/2.0) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH,
					getHeight()-PADDLE_Y_OFFSET);
		}else {
			paddle.setLocation(mouseX-PADDLE_WIDTH/2.0,
					getHeight()-PADDLE_Y_OFFSET);
		}
	}

	//sets up the game
	private void setup() {
		drawBricks();
		drawPaddle();
	}

	//creates ball and moves it 
	private void moveBall() {
		ball = new GOval(getWidth()/2.0, getHeight()/2.0, BALL_DIAM, BALL_DIAM);
		ball.setFilled(true);
		add(ball);

		while (true) {
			ball.move(vx, vy);
			paddleCollision();
			wallCollision();
			//ball goes past bottom wall. lose turn and remove ball
			if (ball.getY() >= getHeight()) {
				remove(ball);
				break;
			}
			pause(DELAY);
			if (brick_counter == 0) {
				break;
			}
		}
	}

	private void wallCollision() {
		//bounces ball off right wall
		if (ball.getX() >= getWidth()-BALL_DIAM) {
			vx = -vx;	
		}
		//bounces ball off the left wall
		if (ball.getX()<=0) {
			vx = -vx;
		}
		//bounces off the top wall
		if (ball.getY()<=0) {
			vy = -vy;
		}
	}

	private void paddleCollision() {
		GObject collider = checkForCollision();
		if (collider == paddle) {
			vy = -Math.abs(vy);
		}else if (collider != null) {
			remove(collider);
			vy = -vy;
			brick_counter--;
		}	
	}

	private GObject checkForCollision() {
		//checks corners of top left part of ball
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		//checks corner of top right part of the ball 
		if (getElementAt(ball.getX()+ BALL_DIAM, ball.getY()) != null) {
			return getElementAt(ball.getX()+ BALL_DIAM, ball.getY());
		}
		//checks corner of bottom left of the ball
		if (getElementAt(ball.getX(), ball.getY() + BALL_DIAM) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_DIAM);
		}
		//checks corner of bottom right of the ball 
		if (getElementAt(ball.getX() + BALL_DIAM, ball.getY() +BALL_DIAM) != null) {
			return getElementAt(ball.getX() + BALL_DIAM, ball.getY() +BALL_DIAM);
		}
		return null;
		// why doesn't this work?
		//		//checks right midpoint 
		//		if (getElementAt(ball.getX()+ BALL_DIAM, ball.getY()+BALL_RADIUS) != null) {
		//			return getElementAt(ball.getX()+ BALL_DIAM, ball.getY()+BALL_RADIUS);
		//		}
		//		//checks left midpoint
		//		if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS) != null) {
		//			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS);
		//		}

	}

	//draws the paddle
	private void drawPaddle() {
		paddle = new GRect(getWidth() - PADDLE_WIDTH/2.0, 
				getHeight()- PADDLE_Y_OFFSET-PADDLE_HEIGHT/2.0,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}
	
	//draws bricks and add color (fix color decomposition later)
	private void drawBricks() {
		brick_counter = 0;
		for (int i = 0; i<NBRICK_ROWS; i++) {
			double rowHeight = ((BRICK_HEIGHT+BRICK_SEP)*i + BRICK_Y_OFFSET);
			for (int j=0; j<NBRICK_COLUMNS; j++) {
				GRect brick = new GRect((BRICK_WIDTH + BRICK_SEP)*j + (BRICK_SEP/2.0), 
						rowHeight,BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick_counter++;
				brick.setFilled(true);
				brick.setColor(chooseColor(i));
			}
		}
	}
	
	//chooses color scheme
	private Color chooseColor(int i) {
		if (i % 10 == 0 || i % 10 == 1) {
			return Color.RED;
		}

		if (i % 10 == 2 || i % 10 == 3) {
			return Color.ORANGE;
		}

		if (i % 10 == 4 || i % 10 == 5) {
			return Color.YELLOW;
		}
		if (i % 10 == 6 || i % 10 == 7) {
			return Color.GREEN;
		}
		if (i % 10 == 8 || i % 10 == 9) {
			return Color.CYAN;
		}
		return null;
	}

}