/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */
//B
//
//
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

	//Buddy Nelson 
	//Section leader:Akua McLeod

	// Instance variables 
	private static final int NTURNS = 3;
	private static int TURNSTAKEN = 0;

	private static int NBRICKS = 100;
	private GRect paddle;
	private GOval ball; 
	private GRect rect;
	private RandomGenerator rGenerator=
			RandomGenerator.getInstance();
	private double yVelocity, xVelocity;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

	
		//This program runs the breakout game
		SetUpgame();
		if (NTURNS > TURNSTAKEN) {
			playGame();
		} else {
			losingMessage();
		}

	}

	private void SetUpgame() {
		placeBricks();
		addPaddle();
		addMouseListeners();
		addBall();

	}
	private void playGame() {
		addMouseListeners(); 
		makeBallMove();
		while (ball.getY() < getHeight() && ball != null) {
			makeBallMove();
			collisions();
			bounce();
		}
	}

	private void addPaddle() {
		//This method adds the paddle to the world
		double y = getHeight() - PADDLE_Y_OFFSET;
		double x = getWidth()/2 - PADDLE_WIDTH/2;
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
	}
	//Mouse tracks location of the paddle so the paddle follows the mouse
	public void mouseMoved(MouseEvent e) {
		double xPosition = e.getX();
		double paddleLocation= xPosition-PADDLE_WIDTH/2;
		paddle.setLocation (paddleLocation,paddle.getY());
		if (paddle.getX() < 0) {
			paddle.setLocation(0, paddle.getY()) ;
		}
		if (paddle.getX()+PADDLE_WIDTH > getWidth()) {
			paddle.setLocation(getWidth()-PADDLE_WIDTH, paddle.getY());
		}
	}
	//Method places starting bricks for the game with different colors
	private void placeBricks() {
		double x= BRICK_SEP /2;
		double y= BRICK_Y_OFFSET;
		for (int a = 0; a<NBRICK_COLUMNS; a++) {
			x = BRICK_SEP /2;
			y += (BRICK_SEP + BRICK_HEIGHT);
			for (int b = 0; b < NBRICK_ROWS; b++) {
				GRect brick= new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				x += (BRICK_WIDTH+BRICK_SEP);
				if (a < 2){
					brick.setFilled(true);
					brick.setColor(Color.RED);
				} else if (a<4) { 
					brick.setFilled(true);
					brick.setColor(Color.ORANGE);
				} else if (a<6) {
					brick.setFilled(true);
					brick.setColor(Color.YELLOW);
				} else if (a<8) {
					brick.setFilled(true);
					brick.setColor(Color.GREEN);
				} else if (a<10) {
					brick.setFilled(true);
					brick.setColor(Color.CYAN);
				}
			}
		}
	}
	//This method adds the ball to the middle of the screen
	private void addBall() {
		double x= (getWidth() / 2) - BALL_RADIUS;
		double y= (getHeight() / 2) - BALL_RADIUS;
		ball= new GOval(x, y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor((Color.BLACK));
		add(ball);
	}
	//this method makes the ball move when the user clicks. Sets the ball speed as well 
	private void makeBallMove() {
		yVelocity =+ 3.0;
		xVelocity = rGenerator.nextDouble(1.0, 3.0);
		if(rGenerator.nextBoolean(.5)) {
			xVelocity= -xVelocity;	
		}
		addMouseListeners(); 
		waitForClick(); 
		while (NBRICKS > 0) { 
			ball.move(xVelocity,yVelocity);
			pause(DELAY);
			bounce();
			collisions();
			if (NBRICKS == 0) {
				winningMessage();
			}
		}
	}
	//This method registers a collision.  If the ball hits the paddle it bounces back and if the ball hits a brick the ball bounces and the brick disappears  
	private void collisions() {
		GObject brickHit = getCollidingObject();
		if (brickHit == paddle) {
			yVelocity = -yVelocity; 
		} else if (brickHit != null) {
			yVelocity = -yVelocity;
			remove(brickHit);
			NBRICKS--;
		}
	}
	//this method checks if the ball has collided with something at each of the ball's 4 corners.
	private GObject getCollidingObject() {
		GObject brickHit = getElementAt(ball.getX(), ball.getY());
		if (brickHit != null) {
			return (brickHit);
		}
		brickHit = getElementAt (ball.getX(), ball.getY()+ 2 * BALL_RADIUS);
		if (brickHit != null) {
			return (brickHit);
		}
		brickHit = getElementAt (ball.getX() + 2 * BALL_RADIUS, ball.getY());
		if (brickHit != null) {
			return (brickHit);
		}
		brickHit = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		if (brickHit != null) {
			return (brickHit);
		}
		return null;
	}


	//this method occurs when the user loses a life 
	private void lifeTaken() {
		remove(ball);
		ball=null;
		TURNSTAKEN++;
		pause(DELAY);
		if (NTURNS > TURNSTAKEN) {
			addBall();
			waitForClick(); }
		else {
			losingMessage();
		}

	}
	//this method makes the ball bounce of walls by changing the x or y velocity 
	private void bounce() {
		if ((ball.getX() > getWidth() - BALL_RADIUS * 2) || ( ball.getX() <= 0) ) {
			xVelocity = -xVelocity;
		}
		else if (ball.getY() < 0 ){
			yVelocity = -yVelocity;
		}
		else if (ball.getY() > getHeight() - BALL_RADIUS * 2) {
			lifeTaken();
		}
	}
	// displays message if player loses 
	private void losingMessage() {
		GLabel lose = new GLabel ("you lose");
		lose.setLocation(getWidth() / 2 + lose.getWidth() / 2, getHeight() / 2);
		add (lose);
		remove(ball);
		remove(paddle);
	}
	//displays message if player wins
	private void winningMessage() {
		GLabel win = new GLabel ("you win!");
		win.setLocation(getWidth() / 2 + win.getWidth() / 2, getHeight() / 2);
		add(win);
		remove(ball);
		remove(paddle);
	}
}









