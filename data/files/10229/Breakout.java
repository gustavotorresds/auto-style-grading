/*
 * File: Breakout.java
 * -------------------
 * Name: Joel Ramirez
 * Section Leader: Esteban Rey
 * 
 * This File is a super fun game that is called Breakout. It is a game that uses a ball, a paddle 
 * and rows of bricks to break each one of them until the ball eventually "breaks out". Think you can handle it? 
 * 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;


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
	public static final int NBRICK_ROWS = 10
			;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70.0;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60.;
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
	;

	// Number of turns 
	public static final int NTURNS = 3;

	// Creates a starting point for the first row of bricks 
	private GRect paddle;
	private GOval ball ;
	private GRect brick;
	private int startingAmountOfBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	private int amountOfBricksRemoved = 0;
	double vx, vy;
	private GLabel score = new GLabel("");
	private int numberOfTries = 0;
	public void run() {



		RandomGenerator rgen = new RandomGenerator();	
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		addMouseListeners();
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		// The screen before the game loads

		preGame();

		// makes all the objects before the game starts
		paddle = makePaddle();
		addPaddleToCenter(paddle);	
		ball = makeBall();
		makeTheRowOfBricks(); 
		waitForClick();

		// the code for the game itself
		while (numberOfTries < NTURNS && amountOfBricksRemoved < startingAmountOfBricks) { 
			//for score label
			score.setLabel("Your Score: " + amountOfBricksRemoved);
			add(score, BRICK_Y_OFFSET/2 , BRICK_Y_OFFSET/2);
			score.setFont("Courier-20");
			score.setColor(Color.BLACK);
			//makes sure the ball changes direction if it touches a wall
			if ( ballHitLeftWall(ball) || ballHitRightWall(ball)) {
				vx = -vx;
			}

			if( ballHitTopWall(ball)) {
				vy = -vy;
			}
			if ( ballHitBottomWall(ball)) {
				remove(ball);
				numberOfTries = numberOfTries + 1;
				ball = makeBall();
				if ( numberOfTries == NTURNS) {
					remove(ball);
					vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
					if (rgen.nextBoolean(0.5)) vx = -vx;
				}

				addMouseListeners();
				waitForClick();

			}
			// let the ball move
			ball.move(vx, vy);
			// asks if there is anything in contact with the ball 
			GObject collider = getCollidingObjectTopLeft();
			if (collider == null) {
				collider = getCollidingObjectLowerLeft();
				if (collider == null) {
					collider = getCollidingObjectLowerRight();

					if  ( collider == null ) {
						collider = getCollidingObjectUpperRight();
					} 
				}
			}

			// identifies what the ball has come in contact with and
			// how it should behave

			if (collider != null) {
				if(collider == paddle) {
					vy = -vy;
				} else if( collider != null) {
					if(collider!= score) {
						remove(collider);
						vy = -vy;
						amountOfBricksRemoved++;
					}
				}

			}
			pause(DELAY);
			// establishes the end result of the game


			// if you lose
			if ( numberOfTries == NTURNS) {
				remove(score);
				GLabel loser = new GLabel ("You Lost!");
				loser.setFont("Courier-24");
				double xcordlabel =  getWidth()/2 - loser.getWidth()/2;
				double ycordlabel = getHeight()/2 - loser.getAscent();
				add(loser, xcordlabel, ycordlabel);


				GLabel yourScore = new GLabel ("Your score was: " + amountOfBricksRemoved);
				yourScore.setFont("Courier-20");
				double xcordlabel2 =  getWidth()/2  - yourScore.getWidth()/2;
				double ycordlabel2 = getHeight()/2 + loser.getAscent() * 2;
				add(yourScore, xcordlabel2, ycordlabel2);
			} 

			// if you win
			if (amountOfBricksRemoved == startingAmountOfBricks) {
				remove(ball);
				remove(paddle);
				remove(score);
				GLabel winner = new GLabel ("You Won! Congradulations!");
				winner.setFont("Courier-24");
				double xcordlabel =  getWidth()/2 - winner.getWidth()/2;
				double ycordlabel = getHeight()/2 - winner.getAscent();
				add(winner, xcordlabel, ycordlabel);
			}



		}

	}

	private void preGame() {
		GLabel preGame = new GLabel ("Click anywhere to begin!");
		preGame.setFont("Courier-24");
		double xcordlabel =  getWidth()/2 - preGame.getWidth()/2;
		double ycordlabel = getHeight()/2 - preGame.getAscent();
		add(preGame, xcordlabel, ycordlabel);
		waitForClick();
		remove(preGame);
		GLabel preGame2 = new GLabel ("I meant click three times...");
		preGame2.setFont("Courier-24");
		double xcordlabel2 =  getWidth()/2 - preGame2.getWidth()/2;
		double ycordlabel2= getHeight()/2 - preGame2.getAscent();
		add(preGame2, xcordlabel2, ycordlabel2);
		waitForClick();
		remove(preGame2);
	}

	private GObject getCollidingObjectUpperRight() {
		double ballXUpperRight = ball.getX() + BALL_RADIUS * 2;
		double ballYUpperRight = ball. getY(); 
		GObject r = getElementAt( ballXUpperRight, ballYUpperRight);
		return r;
	}


	private GObject getCollidingObjectLowerRight() {
		double ballXLowerRight = ball.getX() + 2 * BALL_RADIUS;
		double ballYLowerRight = ball.getY() + 2 * BALL_RADIUS;
		GObject r = getElementAt(ballXLowerRight, ballYLowerRight);
		return r;
	}


	private GObject getCollidingObjectLowerLeft() {
		double ballXLowerLeft = ball.getX();
		double ballYLowerLeft = ball.getY() + 2 * BALL_RADIUS;
		GObject r = getElementAt( ballXLowerLeft,ballYLowerLeft);
		return r;
	}


	private GObject getCollidingObjectTopLeft() {

		double ballXUpperLeft = ball.getX();
		double ballYUpperLeft = ball.getY() + 2 * BALL_RADIUS;

		GObject r = getElementAt( ballXUpperLeft,ballYUpperLeft);

		return r; 




	}

	private boolean ballHitTopWall(GOval ball2) {

		return ball.getY() <= 0;
	}

	private boolean ballHitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - ball.getWidth();
	}

	private boolean ballHitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean ballHitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private GOval makeBall() {
		double size = BALL_RADIUS * 2;
		GOval r = new GOval (size, size);
		r.setFilled(true);
		add(r, getWidth()/2- BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
		return r;
	}

	public void  mouseMoved(MouseEvent e) { 
		double y = (getHeight() - (PADDLE_HEIGHT + PADDLE_Y_OFFSET));
		double x = e.getX() - PADDLE_WIDTH / 2;
		if ( x > getWidth() - PADDLE_WIDTH) {
			x = getWidth() - PADDLE_WIDTH;
		}
		if ( x < getWidth() - getWidth()) {
			x = getWidth() - getWidth();
		}
		paddle.setLocation ( x, y);
	}

	private GRect makePaddle() {
		GRect paddle = new GRect( PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	private void addPaddleToCenter(GRect paddle) {
		double x = (getWidth()- PADDLE_WIDTH) / 2;
		double y = (getHeight() - (PADDLE_HEIGHT + PADDLE_Y_OFFSET));
		add(paddle, x, y);

	}

	private void makeTheRowOfBricks() {
		double YCORD = BRICK_Y_OFFSET;
		for (double k = 0; k < NBRICK_ROWS; k ++ ) {
			// Creates a starting point for the first row of bricks although the space in 
			// between the start of each brick is actually the width of the brick and their 
			// separation 
			double XCORD = (getWidth() - ((NBRICK_COLUMNS * BRICK_WIDTH) + (BRICK_SEP * (NBRICK_COLUMNS - 1))))/2;


			for (int  i = 0; i < NBRICK_COLUMNS; i ++ ) {
				brick = new GRect( XCORD, YCORD, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);

				// this string of code from line 114 to 124 will change the color according to the row 

				if ( k % NBRICK_ROWS < 2 ) { 
					brick.setColor(Color.RED);
				} else if (k % NBRICK_ROWS< 4 ) {
					brick.setColor(Color.ORANGE);
				} else if (k % NBRICK_ROWS < 6) { 
					brick.setColor(Color.YELLOW);
				} else if (k % NBRICK_ROWS < 8 ) {
					brick.setColor(Color.GREEN);
				} else if ( k % NBRICK_ROWS < NBRICK_COLUMNS) {
					brick.setColor(Color.CYAN);
				}
				XCORD = XCORD + BRICK_WIDTH + BRICK_SEP;	

			}

			YCORD = YCORD + BRICK_HEIGHT + BRICK_SEP;
			XCORD = (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH) + (BRICK_SEP * (NBRICK_COLUMNS - 1)))/2;
		}

	}




}
