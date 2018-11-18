/*
 * File: Breakout.java
 * -------------------
 * Name: Glenn Langdon
 * Section Leader: Rhea Karuturi
 * 
 * This file implements the game of Breakout with the following extensions:
 * winner's message, loser's message, sound,  and a start and resume game message
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExt extends GraphicsProgram {

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

	// Top label offset
	public static final int MSG_Y_OFFSET = 50;

	// Limit of games before end
	public static final int NUM_GAME_LIMIT = 3;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		// Creates bricks and paddle.
		gameSetup(); 
		while (turn < NTURNS && brickCounter < NBRICK_ROWS * NBRICK_COLUMNS) {
			playGame();
		}
		if (turn == NTURNS) {
			displayMsg("GAME OVER");
		} else {
			displayMsg("YOU WIN");
		}
		addMouseListeners();		
	}

	// Called on mouse to reposition paddle.
	public void mouseMoved(MouseEvent e) {
		if (paddle != null && (e.getX() < getWidth()) && e.getX() > 0) {
			paddle.setCenterX(e.getX());
		}
	}

	// creates a centered groups of bricks
	private void createBricks() {
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			for (int j = 0; j < NBRICK_ROWS; j++) {
				double x = (getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) + (NBRICK_COLUMNS * BRICK_SEP))) / 2 + (j * (BRICK_WIDTH + BRICK_SEP));				
				double y = BRICK_Y_OFFSET + (i * (BRICK_HEIGHT + BRICK_SEP));
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				colorBricks(i, brick);					
				add(brick);					
			}				
		}
	}

	// chooses a brick color according to row number
	private void colorBricks(int i, GRect brick) {
		if (i == 0 || i == 1 ) {
			brick.setFilled(true);
			brick.setColor(Color.RED);
		}
		else if (i == 2 || i == 3) {
			brick.setFilled(true);
			brick.setColor(Color.ORANGE);
		}
		else if (i == 4 || i == 5) {
			brick.setFilled(true);
			brick.setColor(Color.YELLOW);
		}
		else if (i == 6 || i == 7) {
			brick.setFilled(true);
			brick.setColor(Color.GREEN);
		}
		else {
			brick.setFilled(true);
			brick.setColor(Color.CYAN);
		}
	}

	// Creates setup method with the brick, paddle and creation methods.
	private void gameSetup() {
		createBricks();
		createPaddle();		
	}

	// Creates a paddle centered in the canvas.
	private void createPaddle() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = (getHeight() - PADDLE_Y_OFFSET);
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	// Create a ball and center in the window if no ball exists.
	private void createBall() {
		ball = new GOval(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	/* Moves ball downward in random direction, changes direction of ball
	 * upon impact, tracks number of times ball is missed by paddle, ends games
	 * when missed balls exceed number of turns, end games when all bricks
	 * are removed
	 */
	public void playGame() {
		turn++;
		displayMsg("Click to begin. You have " + ((NTURNS + 1) - turn) + " turn(s) left.");
		createBall();

		dx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); // generates pseudo random number between given x velocities
		if (rgen.nextBoolean(0.5)) dx = -dx;  // changes starting x direction 50% of the time
		dy = VELOCITY_Y;

		// Keep looping as long as we have not hit the bottom or destroyed all bricks.
		while(!hitBottom() && brickCounter < NBRICK_ROWS * NBRICK_COLUMNS) {

			// Changes direction when opposite wall is hit.
			if (ball.getX() <= 0 || ball.getX() >= getWidth() - BALL_RADIUS * 2) {
				dx = -dx;
			}
			if (ball.getY() <= 0) {
				dy = -dy;
			}

			// Handle object collisions.
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				
				// If collides with paddle, ball changes direction.
				dy = -dy;
				bounceClip.play();
			}
			else if (collider != null) {

				// If ball collides with brick, brick is removed, brick is counted, ball changes direction.
				remove(collider);
				brickCounter += 1;
				dy = -dy;
				bounceClip.play();
			}

			ball.move(dx, dy);
			pause(DELAY);
		}	
		remove(ball);
	}

	/* if object found under x and y coordinates, object is returned with those coordinates.
	 * Checks for object at all points of the GOval square
	 */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			GObject collObj = getElementAt(ball.getX(), ball.getY());
			return collObj; 
		}
		else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
			GObject collObj = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
			return collObj;
		}
		else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
			GObject collObj = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
			return collObj;
		}
		else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
			GObject collObj = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
			return collObj;
		}
		else {
			// always needs to return something; should return that's nothing's there, if nothing's there
			return null; 
		}		
	}

	// boolean in movelBall method for hitting bottom wall
	private boolean hitBottom() {
		return ball.getY() >= getHeight();
	}

	// displays a message to start the game or start a turn reading in the number of balls created
	private void displayMsg(String text) {
		GLabel msg = new GLabel(text);
		msg.setFont("Courier-16");
		add(msg, ((getWidth() - msg.getWidth()) / 2), (getHeight() - msg.getAscent()) / 2);
		waitForClick();
		remove(msg);
	}

	// method that increases the speed of the ball as more bricks are removed
	private double gameAccelerator(double dy) {	
		if (brickCounter > 5) {
			return dy = dy * 10;
		}
		if (brickCounter > 40) {
			return dy = dy * 2.5;
		}
		if (brickCounter > 70) {
			return dy = dy * 3;
		}
		if (brickCounter > 95) {
			return dy = dy * 4;
		}
		else return dy;
	}


	// Private instance variables
	private GRect paddle;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double dx, dy;
	private GOval ball = null;
	private int turn = 0;
	private int brickCounter = 0;

	// Load audio clip
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
}



