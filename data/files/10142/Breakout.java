/*
 * File: Breakout
.java
 * -------------------
 * Name: Sabrina Halper
 * Section Leader: Ella Tessier-Lavigne
 * This program runs the game Breakout. The goal of the game is to remove all of the bricks from the screen.
 * The ball breaks the bricks and the paddle bounces the ball back up towards the bricks, preventing it from falling 
 * through to the bottom. Each time the paddle misses the ball, the player loses a life. The player gets three lives.
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
	//adds mouse listeners

	//main run method
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		//counts the amount of bricks on the screen to establish when the game is won
		count = NBRICK_COLUMNS * NBRICK_ROWS;
		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// creates the set up for the game
		setUpGame();
		//plays the game
		playGame();
	}
	//established paddle location 
	public void mouseMoved (MouseEvent e) {
		double c = e.getX() - (PADDLE_WIDTH / 2);
		if (c > getWidth() - PADDLE_WIDTH) {
			c = getWidth() - PADDLE_WIDTH;
		}
		if (c < 0 )	 {
			c = 0;
		}
		//tells paddle to track x location of mouse
		paddle.setLocation(c, getHeight()-PADDLE_Y_OFFSET );
	}
	private void setBricks() {
		//sets up brick formation
		for (int i=0; i<NBRICK_ROWS ; i++) { 
			for (int a=0; a< NBRICK_COLUMNS; a++) {
				double x = (getWidth()/ 2 - (NBRICK_COLUMNS * (BRICK_WIDTH +BRICK_SEP) /2)) + (BRICK_SEP +BRICK_WIDTH)*a ; 
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT+BRICK_SEP)*i; 
				makeBricks(x, y, i);
			}
		}
	}
	//assigns colors to the bricks
	private void makeBricks(double x, double y, int i) {
		GRect brick= new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		if (i % 10 == 0 || i % 10 == 1)
			brick.setColor(Color.RED);
		if (i % 10 == 2 || i % 10 == 3)
			brick.setColor(Color.ORANGE);
		if (i % 10 == 4 || i % 10 == 5)
			brick.setColor(Color.YELLOW);
		if (i % 10 == 6 || i % 10 == 7)
			brick.setColor(Color.GREEN);
		if (i % 10 == 8 || i % 10 == 9)
			brick.setColor(Color.CYAN);
		add(brick);
	}
	//creates the paddle
	private void createPaddle() {
		double a = getWidth() / 2 - PADDLE_WIDTH / 2;
		double b = getHeight() - PADDLE_Y_OFFSET;
		paddle= new GRect(a, b, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	//creates the ball
	private void createBall() {
		double e = getWidth() / 2 - BALL_RADIUS;
		double f = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval(e, f, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}
	//sets up the game with the bricks, paddle, ball, and tracks the mouse
	private void setUpGame() {
		setBricks();
		createPaddle();
		createBall();
		addMouseListeners();
	}
	//runs the program to play the game
	private boolean playGame() {
		for (int i= NTURNS; i>0; i--) {
			waitForClick();
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			vy = 3.0;
			if (rgen.nextBoolean(0.5)) vx = -vx;
			while (ball.getY() <= getHeight() -BALL_RADIUS * 2 ) {
				ball.move (vx, vy);
				pause(DELAY);
				wallCollision();
				getCollidingObject();
				GObject collider = getCollidingObject();
				paddleAndBrickCollision (collider);
				if (count == 0) {
					youWin();
					return (true);
				}
			}
			//ends the lost round and resets ball to begin the new round of the game
			remove(ball);
			createBall();
		}
		//establishes losing the game ( to remove the ball and write "YOU LOSE:(")
		remove(ball);
		youLose();
		return(false);
	}
	//creates response to wall collisions: to reverse x velocity if the ball bounces of the side walls and two reverse y velocity if the ball bounces off the top wall
	private void wallCollision() {
		if (ball.getX() <= 0) {
			vx= -vx;	
		}
		if (ball.getX() >= getWidth() - BALL_RADIUS * 2) {
			vx= -vx;
		}
		if (ball.getY() <= 0) {
			vy= -vy;
		}
	}
	//establishes the four corner coordinates of the ball and returns any objects present at the points
	private GObject getCollidingObject() {
		GObject a = getElementAt(ball.getX(), ball.getY());
		if (a!= null) {
			return a;
		}
		a = getElementAt(ball.getX(), ball.getY());
		if (a!= null) {
			return a;
		} 
		a = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		if (a!= null) {
			return a;
		} 
		a = getElementAt(BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		if (a!= null) {
			return a;
		} else {
			return null; 
		}
	}
	//creates a method to instruct program to have ball "bounce off of" bricks and the paddle
	private void paddleAndBrickCollision(GObject collider) {
		AudioClip ballBounceSound = MediaTools.loadAudioClip("bounce.au");
		if (collider == paddle) {
			//if ball "hits" paddle, reverses the y velocity of the ball
			vy = -(Math.abs(vy));
			//tells the ball to make a sound when it hits the paddle
			ballBounceSound.play();
		} else if (collider!= null) {
			//if ball "hits brick"-removes brick, reverses y velocity of the ball, and subtracts one brick from the count
			remove(collider);
			vy = -vy;
			//tells the ball to make a sound when it hits a brick
			ballBounceSound.play();
			count--;
		}
	}
	//writes "YOU WIN:)" on the screen when the game is lost
	private void youWin() {
		GLabel glabel = new GLabel("YOU WIN:)");
		double a = (getWidth() / 2);
		double b = (getHeight() / 2);
		glabel.setColor(Color.RED);
		glabel.setLocation(a - glabel.getWidth() / 2,b);
		add(glabel);
	}
	//writes "YOU LOSE:(" on the screen when the game is lost
	private void youLose() {
		GLabel glabel = new GLabel("YOU LOSE:(");
		double a = (getWidth() / 2);
		double b = (getHeight() / 2);
		glabel.setColor(Color.RED);
		glabel.setLocation(a - glabel.getWidth() / 2,b);
		add(glabel);
	}
	//instance variable for paddle
	private GRect paddle;
	//instance variable for random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	//instance variable for velocity
	private double vx, vy;
	//instance variable for ball
	private GOval ball;
	//instance variable for count
	private int count;
}

