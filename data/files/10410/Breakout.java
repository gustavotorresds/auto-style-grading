/*
 * File: Breakout.java
 * -------------------
 * Name: Wen Ren
 * Section Leader: Kathryn Rydberg
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

	// define the acceleration, add speed to vy each time hitting paddle.
	private static final double ACCELLERATION = 3;

	//instance variable for the paddle
	private GRect paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
	

	//instance variable for the ball
	private GOval ball = new GOval(BALL_RADIUS,BALL_RADIUS);

	//instance variable for the random generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//instance variable for the velocity of the ball
	private double vx;
	private double vy;

	//instance variable for the count of remaining bricks
	private int countbricks = NBRICK_COLUMNS * NBRICK_ROWS;

	//instance variable for the count of turns
	private int countturns = NTURNS;


	public void run() {
		setUp();	
		while(!gameFinish()) {
			moveBall();
			checkCollide();
			checkHitBottom();
		}
		addGameFinishLabel();
	}


	//check if the ball hit the bottom; reset the canvas and display "try again" if still in game/
	private void checkHitBottom() {
		if (hitBottomWall(ball)){
			countturns += -1;	
			if (countturns != 0) {
				addTryAgainLabel();
				pause(500);
			}	
			clear();
			setUp();
		}
	}


	//Add the finishing message either "Game Over!!" or " Winner!!" depending on the outcome.	
	private void addGameFinishLabel() {
		if(gameOver()) {
			clear();
			addGameOverLabel();
		}else if(allClear()) {
			addYouWinLabel();
		}

	}

	//add "Winner!!" label
	private void addYouWinLabel() {
		GLabel youWinLabel= new GLabel("Winner!!");
		youWinLabel.setFont("SansSerif-bold-40");
		add(youWinLabel, (getWidth()- youWinLabel.getWidth())/2 , getHeight()/2);
		youWinLabel.setColor(Color.PINK);

	}


	//add "GAME OVER!!" label
	private void addGameOverLabel() {
		GLabel gameOverlabel= new GLabel("GAME OVER!!");
		gameOverlabel.setFont("SansSerif-bold-30");
		add(gameOverlabel, (getWidth()- gameOverlabel.getWidth())/2 , getHeight()/2);
		gameOverlabel.setColor(Color.RED);

	}

	//add "Failed! Try again!" label
	private void addTryAgainLabel() {
		GLabel tryagainlabel = new GLabel("Failed! Try again!");
		tryagainlabel.setFont("SansSerif-bold-28");
		add(tryagainlabel, (getWidth()- tryagainlabel.getWidth())/2 , getHeight()/2);
		tryagainlabel.setColor(Color.ORANGE);
	}


	//check if the game is finished, either the player failed all turns or the play won.
	private boolean gameFinish() {
		return allClear() || gameOver();
	}

	//return true if the play failed all turns.
	private boolean gameOver() {
		return(countturns == 0);
	}
	//return true if all bricks are cleared and the player wins.
	private boolean allClear() {
		return (countbricks == 0);
	}


	//update the velocity if the ball hits walls
	private void updateVelocity() {
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball)) {
			vy = -vy;
		}
	}

	//check if the collide object is the brick, remove the brick and update the y velocity
	//if the collide object is the paddle, just update the y velocity plus an acceleration.

	private void checkCollide() {
		GObject collider = getCollideObject();
		if (collider != null & collider != paddle) {
			bounceClip.play();
			ball.setColor(collider.getColor());
			remove(collider);
			vy= -vy;
			countbricks += - 1;
		
		}
		if(collider == paddle & (ball.getY() + 2*BALL_RADIUS) <= paddle.getY()) {
			bounceClip.play();
			vy= -vy - 1/countbricks* ACCELLERATION;
		}
	}
	

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// this method return the collier object the ball get, check each corner in a sequence.

	private GObject getCollideObject() {
		GObject collObj= getElementAt(ball.getX(), ball.getY());

		if (collObj == null) {
			collObj= getElementAt(ball.getX() + 2* BALL_RADIUS, ball.getY());
		}

		if (collObj == null) {
			collObj= getElementAt(ball.getX(), ball.getY() + 2* BALL_RADIUS);
		}

		if (collObj == null) {
			collObj= getElementAt(ball.getX() + 2* BALL_RADIUS, ball.getY() + 2* BALL_RADIUS);
		}

		return collObj;
	}


	//this method initialize the game
	private void setUp() {
		setTitle("CS 106A Breakout");
		// Set the canvas size.  
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		// create the colorful bricks
		setBricks();
		// add paddle to the canvas
		addPaddle();
		//add the ball
		addBall();
		addMouseListeners();	
		//initialize velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}

	}


	//create the animation to move the ball at (vx,vy)
	private void moveBall() {
		updateVelocity();
		if (ball != null) {
			ball.move(vx, vy);
			pause(DELAY);
		}
	}


	//return true if ball hits top wall
	private boolean hitTopWall(GOval ball2) {
		return ball.getY() <= 0;
	}
	//return true if ball hits right wall
	private boolean hitRightWall(GOval ball2) {
		return ball.getX() >= getWidth()- 2*BALL_RADIUS;
	}
	//return true if ball hits left wall
	private boolean hitLeftWall(GOval ball2) {
		return ball.getX() <= 0;
	}
	//return true if ball drops out of the canvas
	private boolean hitBottomWall(GOval ball2) {
		return ball.getY() >= getHeight();
	}

	//add ball to the canvas
	private void addBall() {
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		waitForClick();
		add(ball,(getWidth()-BALL_RADIUS)/2, (getHeight()-BALL_RADIUS)/2);
	}


	//move the paddle with mouse, set the boundary on the right side
	public void mouseMoved(MouseEvent e) {
		double x = (getWidth()- PADDLE_WIDTH)/2;
		if (e.getX() < getWidth()-PADDLE_WIDTH) {
			x = e.getX();
		}else {
			x = getWidth()- PADDLE_WIDTH;
		}
		paddle.setLocation(x,getHeight()-PADDLE_Y_OFFSET);
	
	}

	//this method add a paddle and track the mouse movement to the screen
	private void addPaddle() {
		double x = (getWidth()- PADDLE_WIDTH)/2;
		add(paddle,x, getHeight()- PADDLE_Y_OFFSET);
		paddle.setFilled(true);
		paddle.setColor(Color.PINK);
	}

	//initialize the bricks
	private void setBricks() {
		// TODO Auto-generated method stub
		for (int r = 0 ; r < NBRICK_ROWS; r++) {
			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				double x = i * (BRICK_WIDTH +BRICK_SEP) + (getWidth() - BRICK_WIDTH * NBRICK_COLUMNS -BRICK_SEP * (NBRICK_COLUMNS -1))/2;
				double y = r * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET;
				GRect brick = new GRect (BRICK_WIDTH,BRICK_HEIGHT);

				brick.setFilled(true);
				if (r % 10 == 0 || r % 10 ==1 ) {
					brick.setColor(Color.RED);
				}else if (r % 10 == 2 || r % 10 == 3) {
					brick.setColor(Color.ORANGE);
				}else if (r % 10 == 4|| r % 10 == 5) {
					brick.setColor(Color.YELLOW);
				}else if (r % 10 == 6 || r % 10 == 7) {
					brick.setColor(Color.GREEN);
				}else if (r % 10 == 8 || r % 10 == 9) {
					brick.setColor(Color.CYAN);
				}
				add(brick,x,y);

			}			

		}
	}
}