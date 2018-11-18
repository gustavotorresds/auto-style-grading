/*
 * File: Breakout_Extension.java
 * -------------------
 * Name: Jimmy Serrano
 * Section Leader: Adam Mosharrafa
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout_Extension extends GraphicsProgram {


	//	This extension:
	//		waits for a click from the user before beginning
	//		has sound effects for the ball when it bounces off walls, bricks or the paddle
	//		has a kicker, when the player has broken 5 bricks, the ball speeds up
	//		displays messages telling the player whether they won or lost


	//creates an instance variable for the paddle
	private GRect paddle = null;
	//creates an instance variable for the ball
	private GOval ball = null;

	//creates instance variables to keep track of the balls velocity in the x and y directions
	private double vx;
	private double vy;

	//Loads audioclip to be used for bounce sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	//creates an instance variable that keeps track of the player's lives left
	private int lives = NTURNS;

	//creates an instance variable to keep track of the bricks left, when this reaches 0 the player has won
	private int bricksRemaining = NBRICK_COLUMNS*NBRICK_ROWS;

	//creates a random number generator used to determin the balls initial x velocity
	private RandomGenerator rgen = RandomGenerator.getInstance();

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

	//this is the main program, basically runs everything by calling all other methods
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout_Extension");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		//Sets up the game field by creating the bricks, paddle and ball in their proper positions
		setupGame();
		playGame();
		//adds a mouse listener so that the paddle can track the motion of the mouse
		addMouseListeners();

	}

	//calls methods to set up the rows of bricks and the paddle
	private void setupGame() {
		createRows();
		createPaddle();
	}

	//creates and adds the paddle centered on the screen
	private void createPaddle() {
		paddle = new GRect(getWidth()/2 - PADDLE_WIDTH/2, (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT), PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	//moves the paddle to match the x coordinate of the mouse
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		if (x > getWidth() - PADDLE_WIDTH) {
			x = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(x, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		add(paddle);
	}

	//this method draws each individual brick and assigns it a color based on its position which in turn is
	//determined by the index of the iteration of the nested for loop in the method createRows()
	private void drawBrick(double px, double py, int i) {
		GRect rect = new GRect(px,py, BRICK_WIDTH, BRICK_HEIGHT);
		rect.setFilled(true);
		add(rect);
		if (i==0 || i==1) {
			rect.setColor(Color.RED);
		}
		if (i==2 || i==3) {
			rect.setColor(Color.ORANGE);
		}
		if (i==4 || i==5) {
			rect.setColor(Color.YELLOW);
		}
		if (i==6 || i==7) {
			rect.setColor(Color.GREEN);
		}
		if (i==8 || i==9) {
			rect.setColor(Color.CYAN);
		}
	}

	//this method uses a nested for loop to build all the rows of bricks. it passes the position and iteration to drawBrick()
	//which draws each individual brick
	private void createRows() {
		double px = (getWidth() - (NBRICK_COLUMNS*BRICK_WIDTH + (NBRICK_COLUMNS-1)*BRICK_SEP))/2;
		double py = BRICK_Y_OFFSET;
		for (int j = 0; j<10; j++) {
			for(int i = 0; i<10; i++) {
				drawBrick(px,py, j);
				px = px + (BRICK_WIDTH + BRICK_SEP);
			}
			py = py + BRICK_HEIGHT + BRICK_SEP;
			px = (getWidth() - (NBRICK_COLUMNS*BRICK_WIDTH + (NBRICK_COLUMNS-1)*BRICK_SEP))/2;
		}
	}

	//this method creates the ball, waits for a click from the user then calls a new animation method which animates ball and
	//has the game logic
	private void playGame() {
		createBall();
		waitForClick();
		ballInMotionAnimation();
	}

	//this method detects when the ball contacts the bottom wall. Used ballInMotionAnimation() in for hit detection with walls
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > (getHeight() - ball.getHeight());
	}

	//this method detects when the ball contacts the top wall. Used ballInMotionAnimation() in for hit detection with walls
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	//this method detects when the ball contacts the left wall. Used ballInMotionAnimation() in for hit detection with walls
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <=0;
	}

	//this method detects when the ball contacts the right wall. Used ballInMotionAnimation() in for hit detection with walls
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	//this is where the magic happens. This method animates the ball, bounces it off walls and deletes bricks when the ball hits them.
	//if the player misses the ball, it resets the ball and deducts one life. when no balls or lives are left the appropriate message is displayed.
	private void ballInMotionAnimation() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;

		while (true) {

			ball.move(vx, vy);

			pause(DELAY);

			if(hitTopWall(ball)) {
				vy = -vy;
				bounceClip.play();
			}

			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
				bounceClip.play();
			}

			if(hitBottomWall(ball)) {
				lives = lives - 1;
				remove(ball);
				createBall();
			}

			if (lives == 0) {
				GLabel label = new GLabel("You Lose!");
				label.setFont("Courier-24");
				label.setColor(Color.RED);
				add(label, (getWidth()/2 - label.getWidth()/2), (getHeight()/2 - label.getHeight()/2));
				break;
			}

			GObject collider = getCollidingObject();

			if (collider == paddle) {
				vy = -Math.abs(vy);
				bounceClip.play();
			} 

			if(collider != null && collider != paddle) {
				remove(collider);
				vy = -(vy);
				bricksRemaining = bricksRemaining -1;
				bounceClip.play();
			}

			if(bricksRemaining == 0) {
				GLabel label = new GLabel("You Won!");
				label.setFont("Courier-24");
				label.setColor(Color.GREEN);
				add(label, (getWidth()/2 - label.getWidth()/2), (getHeight()/2 - label.getHeight()/2));
				break;
			}

			if (bricksRemaining == 95) {
				vy =  1.005*vy;
			}

		}
	}

	//this method creates the ball and places it centered on the screen
	private void createBall() {
		ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	//this method detects whether the ball has collided with an object and returns it to ballInMotionAnimation()
	//if no collision is detected, it returns a null value
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}

		if(getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		}

		if(getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		}

		if(getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		}

		return null;
	}

}
