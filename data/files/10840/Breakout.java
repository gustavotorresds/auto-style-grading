/*
 * File: Breakout.java
 * -------------------
 * Name: Corinne Zanolli
 * CS 106A
 * Section Leader: Peter Maldonaldo.
 * 
 * This program sets up the game Breakout on a blank screen and then 
 * allows the user to play the game it has set up until either the 
 * user wins the game  by hitting all of the bricks with the ball or
 * runs out of lives by missing the ball with the paddle. 
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
	public static int NTURNS = 3;

	/*Instance variable for the paddle*/
	GRect paddle = null;

	/*Instance variable for the ball*/ 
	GOval ball = null;

	/*Instance variable for brick color*/
	Color color = null;
	
	/*Instance variable for the y position of the ball*/
	double y = getHeight()/2 - BALL_RADIUS/2;

	/*Instance variable for the y component of the ball's velocity*/
	double vy = 3.0;

	/*Instance variable for the x component of the ball's velocity*/
	double vx = 0.0;

	/*Instance variable for the random-numberGenerator*/
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*Instance variable for the game playing*/
	private boolean gameIsPlaying = true;


	/*Instance variable for the number of bricks in the game*/
	public static double BRICK_COUNT = NBRICK_ROWS * NBRICK_COLUMNS;
	
	/*Instance variable for sound*/
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		playGame();
		doesGameContinueOrEnd();
	}

	//This method makes the paddle move with the mouse. 
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX > getWidth()-PADDLE_WIDTH) {
			mouseX = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
	}	

	//This method sets up the game. 
	private void setUpGame() {
		makeInitialBrickLayout();
		paddle = makePaddle();
		addMouseListeners();
	}

	//This method allows the user to play the game.
	private void playGame() {
		ball = makeBall();
		waitForClick();
		vy = 3.0;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		while(gameIsPlaying) {
			ball.move(vx, vy);
			bounceOffWalls();
			GObject collider = getCollidingObject();
			determineResponseToCollider(collider);
			if(BRICK_COUNT ==0) {
				break;
			}
			pause(DELAY);
		}
		println("end:" + NTURNS);
	}

	//Once the playGame() loop exits, this method determines if the game continues, is won or is over. 
	private void doesGameContinueOrEnd() {
		while (BRICK_COUNT !=0 && NTURNS > 0) {
			NTURNS = NTURNS - 1;
			println(NTURNS);
			if (NTURNS != 0) {
				gameIsPlaying = true;
				playGame();
			} else {
				GLabel loser = new GLabel ("YOU LOSE");
				add(loser, CANVAS_WIDTH/2 - loser.getWidth()/2, CANVAS_HEIGHT/2);
			} 
		}
		if (BRICK_COUNT == 0) {
			GLabel winner = new GLabel ("CONGRATULATIONS, YOU WIN!");
			add(winner,CANVAS_WIDTH/2 - winner.getWidth()/2, CANVAS_HEIGHT/2);
			}
		}


	//This method makes the initial brick layout of the game on a blank screen.
	private void makeInitialBrickLayout() {
		for (int i=0; i< NBRICK_COLUMNS; i++) {
			for (int j=0; j < NBRICK_ROWS; j++) {
			determineBrickColor(j);
			makeColoredBrick(CANVAS_WIDTH/2 - (BRICK_WIDTH * (NBRICK_COLUMNS/2) + BRICK_SEP *((NBRICK_COLUMNS/2)-1)) + (BRICK_WIDTH + BRICK_SEP)*i,BRICK_Y_OFFSET+(BRICK_HEIGHT + BRICK_SEP)*j,color);
			}
		}
	}

	//This method makes a brick of a certain color.
	private void makeColoredBrick (double x, double y, Color color) {
		GRect brick = new GRect(x,y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		add(brick);
	}
	
	//This method determines the color of the brick.
	private Color determineBrickColor(double i) {
			if (i == 0 || i == 1) {
				color = Color.RED;
			}
			if (i == 2 || i == 3) {
				color = Color.ORANGE;
			}
			if (i == 4 || i == 5) {
				color = Color.YELLOW;
			}
			if (i == 6 || i == 7) {
				color = Color.GREEN;
			}
			if (i == 8 || i == 9) {
				color = Color.CYAN;
			}
		return color;
	}

	//This method makes the paddle and puts it onto the screen.
	private GRect makePaddle() {
		paddle = new GRect (getWidth()/2-PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET,PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		return paddle;
	}

	//This method makes the ball and puts it onto the screen. 
	private GOval makeBall() {
		ball = new GOval (getWidth()/2 + BALL_RADIUS/2, getHeight()/2 - BALL_RADIUS/2, BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball);
		return(ball);
	}

	//This method makes the ball bounce off of the walls. 
	private void bounceOffWalls() {
		if(hitRightWall(ball)) {
			vx = -vx;
			gameIsPlaying = true;
		}
		if(hitLeftWall(ball)) {
			vx = -vx;
			gameIsPlaying = true;
		}
		if(hitTopWall(ball)) {
			vy = Math.abs(vy);
			gameIsPlaying = true;
		}
		if(hitBottomWall(ball)) {
			remove(ball);
			gameIsPlaying = false;
		}
	}

	//This method checks to see if the ball his the right wall.
	private boolean hitRightWall(GOval ball) {
		return getWidth() < ball.getX() + ball.getWidth();
	}

	//This method checks to see if the ball hit the left wall.
	private boolean hitLeftWall(GOval ball) {
		return 0 > ball.getX();
	}

	//This method checks to see if the ball his the top wall.
	private boolean hitTopWall(GOval ball) {
		return 0 > ball.getY();
	}

	//This method checks to see if the ball hit the bottom wall.
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	//This method tests for collisions with objects other than walls. 
	private GObject getCollidingObject() {
		GObject collider = null;
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			collider = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) !=null) {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return collider;
	}


	//This method determines the correct response to the collider. 
	private void determineResponseToCollider (GObject collider) {
		if (collider != null) {
				if (collider == paddle) {
					vy = - Math.abs(vy);
					bounceClip.play();
				} else {
				remove(collider);
				bounceClip.play();
				BRICK_COUNT --;
				vy = -vy;
			}
		}
	}
}



