/*
 * File: Breakout.java
 * -------------------
 * Name: Jessie Dalman
 * Section Leader: Esteban Rey
 * 
 * This file implements the game of Breakout.
 * (Breakout is essentially BrickBreaker).
 * Players have three turns, or 'lives,' to make all of the bricks on the canvas disappear.
 * Players remove bricks by moving a paddle to direct a bouncing ball around the screen.
 * This paddle is moved by moving the mouse.
 * 
 * Note: Because I couldn't figure out how to open a copy of this file in Eclipse (my bad), my extensions are included in this file. 
 * They're commented on to show where they are :) 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

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
	public static final double VELOCITY_X_MIN = 	5.0;
	public static final double VELOCITY_X_MAX = 7.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// (Extension) Instance variable, sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	// Instance variable, paddle
	private GRect paddle = null;

	// Instance variable, ball 
	private GOval ball = null; 

	//Instance variables, velocity of ball
	private double vx, vy; 

	//Instance variable, random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		addMouseListeners();
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		playGame();
	}

	/**
	 * Method: Set Up Game
	 * Sets up Breakout by placing the bricks, making the paddle, and creating the ball.
	 * Creation of paddle includes mouse event method, so that the mouse can control the movement of the paddle.
	 */

	private void setUpGame() {
		layBricks();
		getPaddle();
		makeBall();
	}

	private void layBricks() {
		for (int rows = 0; rows < NBRICK_ROWS; rows++) {
			for (int columns = 0; columns < NBRICK_COLUMNS; columns++) {
				double startX = getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) + ((NBRICK_COLUMNS - 1) * BRICK_SEP)); 
				double x = 	startX/2 + (columns * (BRICK_WIDTH + BRICK_SEP)); 
				double y = BRICK_Y_OFFSET + (rows *(BRICK_HEIGHT + BRICK_SEP)); 
				GRect brick = new GRect(x,y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				Color color = Color.white;
				if (rows == 0 || rows == 1) {
					color = color.RED; 
				}
				else if (rows == 2 || rows == 3) {
					color = color.ORANGE;
				}
				else if (rows == 4 || rows == 5) {
					color = color.YELLOW; 
				}
				else if (rows == 6 || rows == 7) {
					color = color.GREEN;
				}
				else if (rows == 8 || rows == 9) {
					color = color.CYAN; 
				}
				brick.setColor(color);
				add(brick); 
			}
		}
	}

	private void getPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2; 
		double y = (getHeight() - PADDLE_HEIGHT) - PADDLE_Y_OFFSET;
		paddle = new GRect(getWidth(), PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setLocation(x,y);
		paddle.setFilled(true);
		add(paddle);
	}

	public void mouseMoved (MouseEvent e) {
		if (e.getX() < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(e.getX(), (getHeight() - PADDLE_HEIGHT) - PADDLE_Y_OFFSET);
		} 
	}

	private void makeBall() {
		ball = new GOval ((getWidth()/2) - BALL_RADIUS, (getHeight()/2) - (BALL_RADIUS), (BALL_RADIUS *2), (BALL_RADIUS*2));
		ball.setFilled(true);
		add(ball);
	}

	/**
	 * Method: Play Game
	 * After setting up the game, this is where the action begins!
	 * The user clicks the mouse to begin.
	 * Every life lost counts as a 'turn,' which is recorded by turnCounter (in the getBallGoing method).
	 * Users lose a life by letting the ball fall without catching it with the paddle.
	 */

	private void playGame() {
		// label extension
		GLabel startingMessage = new GLabel ("You have 3 lives to BREAKOUT. Ready? Click to Play!");
		startingMessage.setFont("Helvetica-14");
		add(startingMessage, (getWidth() - startingMessage.getWidth())/2, getHeight()/2.5);
		waitForClick();
		remove(startingMessage);
		getBallGoing();
	}

	/**
	 * Method: Get Ball Going
	 * This method is where the bulk of the action is, honestly.
	 * The ball is directed to bounce off of the ceiling and side walls of the canvas.
	 * If the ball falls to the floor of the canvas, turnCounter increases by one.
	 */

	private void getBallGoing() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		// turnCounter keeps track of the lives that players have remaining by tracking the amount of turns that have been played
		double turnCounter = 0;
		// number of bricks keeps track of how many bricks remain
		int numberOfBricks = (NBRICK_ROWS*NBRICK_COLUMNS);
		// while players have died less than three times and have not yet won by getting rid of all of the bricks
		while (turnCounter < NTURNS && numberOfBricks > 0) {
			ball.move(vx, vy);
			pause(DELAY);
			// the if statements below make the ball bounce off the canvas walls 
			if ((ball.getX() + (2*BALL_RADIUS)) > getWidth()) {
				vx = -vx;
				// sound extension
				bounceClip.play();
			}
			if (ball.getX() < 0) {
				vx = -vx;
				// sound extension
				bounceClip.play();
			}	 
			if ((ball.getY() < 0)) {
				vy = -vy;
				// sound extension
				bounceClip.play();
			}
			// if the ball isn't caught by the paddle, the variable turnCounter increases by one, signifying that the player has lost a life
			if ((ball.getY() + (2*BALL_RADIUS)) > getHeight()) {
				turnCounter++;
				remove(ball);
				if (turnCounter != NTURNS) {
					makeBall();
					// players are encouraged to try again as long as they have lives left
					// label extension
					GLabel tryAgain = new GLabel ("Don't give up yet, try again!");
					double x = (getWidth() - tryAgain.getWidth())/2;
					double y = getHeight()/2.5;
					tryAgain.setFont("Helvetica-14");
					add(tryAgain, x, y);
					waitForClick();
					remove(tryAgain);
				}
			}
			numberOfBricks = numberOfBricks + checkCollision();
		}
		// if the player has used up NTURNS, in this case, 3
		if (turnCounter == NTURNS) {
			remove(ball);
			// label extension
			GLabel losingMessage = new GLabel ("Bummer, you lose!");
			double x = (getWidth() - losingMessage.getWidth())/2;
			double y = getHeight()/2;
			losingMessage.setFont("helvetica-14");
			add(losingMessage, x, y);
		} else {
			remove(ball);
			// Chris quote, "Good times!"
			// label extension
			GLabel winningMessage = new GLabel ("Good times, you win!!!");
			double x = (getWidth() - winningMessage.getWidth())/2;
			double y = (getHeight()/2);
			winningMessage.setFont("Helvetica-14");
			add(winningMessage, x, y);
		}
	}


	private int checkCollision() {
		GObject collider = getCollidingObject ();
		if (collider==paddle) {
			// absolute value fixes sticky paddle problem
			vy = -(Math.abs(vy));
			// sound extension
			bounceClip.play();
		} else if (collider !=null) {
			remove(collider);
			vy = -vy;
			// sound extension
			bounceClip.play();
			// subtracts one brick from the total number of bricks remaining by returning above
			return -1;
		}
		return 0; 
	}

	private GObject getCollidingObject () {
		// the if statements below check all four corners of the ball
		// if no objects are found at any corner, the method returns null
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} 
		if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		}
		if (getElementAt (ball.getX(), ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		}
		if (getElementAt (ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS); 
		} else {
			return null;
		}

	}
}



