/*
 * File: Breakout.java
 * -------------------
 * Name: Sun (Woo) Lee
 * Section Leader: Maggie Davis
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
	public static final int NBRICK_ROWS = 1;

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

	//global variables
	private GRect Paddle;
	private GOval Ball;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	//velocity components of the ball
	private double vx, vy;
	//total number of bricks
	private int NBRICKS = NBRICK_COLUMNS*NBRICK_ROWS;
	private int BrickCount = NBRICKS;
	private int Lives = NTURNS;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);	
		setUpGame();
		playGame();
	}



	private void setUpGame() {
		setUpBricks();
		createPaddle();
	}

	/*makes bricks*/
	private void setUpBricks() {
		double x = (CANVAS_WIDTH-(BRICK_WIDTH*10 + BRICK_SEP*9))/2;
		double y = BRICK_Y_OFFSET;

		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {	
				GRect Brick = new GRect (x + j*(BRICK_WIDTH + BRICK_SEP), 
						y + i*(BRICK_HEIGHT + BRICK_SEP), 
						BRICK_WIDTH, 
						BRICK_HEIGHT); 
				Brick.setFilled(true);
				//using remainder when divided by 10 so that the program can color accommodate more than 10 rows   
				if (i % 10 < 2) {
					Brick.setColor(Color.RED);
				} else if (i % 10 < 4) {
					Brick.setColor(Color.ORANGE);
				} else if (i % 10 < 6) {
					Brick.setColor(Color.YELLOW);
				} else if (i % 10 < 8) {
					Brick.setColor(Color.GREEN);
				} else {
					Brick.setColor(Color.CYAN);
				}
				add(Brick);
			}
		}
	}

	/*makes paddle and allows it to move by adding mouse listeners*/
	private void createPaddle() {
		double x = CANVAS_WIDTH/2-PADDLE_WIDTH/2;
		double y = CANVAS_HEIGHT-PADDLE_HEIGHT-PADDLE_Y_OFFSET;
		Paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		Paddle.setFilled(true);
		Paddle.setColor(Color.BLACK);
		add(Paddle);
		addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		if (e.getX() > PADDLE_WIDTH/2 && e.getX() < CANVAS_WIDTH-PADDLE_WIDTH/2) {
			Paddle.setLocation(e.getX()-PADDLE_WIDTH/2, CANVAS_HEIGHT-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
		}
	}

	private void playGame() {
		//this loop runs while there are lives left (you didn't lose yet) and there is at least one brick left (you didn't win yet) 
		while (Lives > 0 && BrickCount > 0) {
			waitForClick();
			ballAppears();
			setBallVelocity();
			play();
		}
		//when you either lose or win, message is displayed accordingly 
		displayMessage();
	}

	private void ballAppears() {
		double x = CANVAS_WIDTH/2-BALL_RADIUS;
		double y = CANVAS_HEIGHT/2-BALL_RADIUS;
		Ball = new GOval (x, y, BALL_RADIUS, BALL_RADIUS);
		Ball.setFilled(true);
		Ball.setColor(Color.BLACK);
		add(Ball);
	}	

	private void setBallVelocity() {
		vy = 3.0; 
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
	}

	/*starting here, we're moving the ball and actually playing the game*/
	private void play() { 
		//while the ball is above the very bottom of the canvas
		while(Ball.getY() < CANVAS_HEIGHT) {
			Ball.move(vx, vy);
			//if Ball hits left OR right side walls, vx is reversed
			if (Ball.getX() < 0 || Ball.getX() > CANVAS_WIDTH - 2*BALL_RADIUS) vx = -vx;
			//if Ball hits upper wall, vy is reversed
			if (Ball.getY() < 0) vy = -vy;
			pause(DELAY);
			//now we consider collisions
			GObject collider = getCollidingObject();

			if (collider == Paddle) {
				//if brick hits paddle, ball goes back up after collision
				//the absolute value ensures that new vy is defined to be a negative velocity (i.e. ball will always move upwards)
				//the aboslute value prevents the ball from sticking to the paddle, bouncing on it multiple times
				vy = -Math.abs(vy);
				//if it hits something else than the paddle, that must be it hitting a brick
				// in this case ball not only bounces off brick but also makes brick disappear
			} else if (collider != null) {
				//reverses direction
				vy = -vy;
				//removes brick
				remove(collider);
				//one is subtracted from brickcount (every time a brick is removed) 
				BrickCount--;
				//once brickcount reaches 0, implying all the bricks have been removed, ball is removed and we exit the loop
				if (BrickCount == 0) {
					remove(Ball);
					break;
				}
			}
		}
		//if ball is no longer above the bottom of canvas, which was the condition of the loop,the player loses a life 
		Lives--;
		//this removes balls that have went below the canvas.
		//this fixes a minor bug--very rarely the ball bounced off a ball that was still down there
		remove(Ball);
	}


	//checking for whether ball is touching any object 
	private GObject getCollidingObject() {
		if (getElementAt(Ball.getX(), Ball.getY()) != null) return getElementAt(Ball.getX(), Ball.getY());
		if (getElementAt(Ball.getX() + 2*BALL_RADIUS, Ball.getY()) != null) return getElementAt(Ball.getX()+ 2*BALL_RADIUS, Ball.getY());
		if (getElementAt(Ball.getX(), Ball.getY()+ 2*BALL_RADIUS) != null) return getElementAt(Ball.getX(), Ball.getY()+ 2*BALL_RADIUS);
		if (getElementAt(Ball.getX() + 2*BALL_RADIUS, Ball.getY()+ 2*BALL_RADIUS) != null) return getElementAt(Ball.getX() + 2*BALL_RADIUS, Ball.getY()+ 2*BALL_RADIUS);
		else {
			return null;
		}
	}

	private void displayMessage() {
		GLabel message = new GLabel("");
		//if player eliminates all bricks, victory message appears
		if (BrickCount == 0) {
			message = new GLabel ("YOU WIN!");
			message.setFont("Courier-24");
			add(message, CANVAS_WIDTH/2-message.getWidth()/2, CANVAS_HEIGHT/2-message.getAscent()/2);
		} else {
			//the other possibility is the player losing all lives. Then, gameover message appears
			message = new GLabel ("GAMEOVER... YOU LOSE");
			message.setFont("Courier-24");
			add(message, CANVAS_WIDTH/2-message.getWidth()/2, CANVAS_HEIGHT/2-message.getAscent()/2);
		}
	}
}