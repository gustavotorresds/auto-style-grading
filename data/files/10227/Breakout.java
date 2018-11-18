/*
 * File: Breakout.java
 * -------------------
 * Name: Adam Gurary
 * Section Leader: Meng Zhang
 * 
 * This file implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
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
	public static final double PADDLE_Y_OFFSET = 60;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Font to use for on-screen text 
	public static final String SCREEN_FONT = "SansSerif-BOLD-18";

	//Instance variables:
	private GRect paddle = null;
	private GRect brick = null;
	private GOval ball = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int brickCounter;
	private int lifeCounter = NTURNS;
	private GLabel score = new GLabel ("Score = " + brickCounter);


	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		setUpPaddle();
		playBall();
		addMouseListeners();
	}

	//This method stacks the bricks at the top of the screen.
	private void setUpBricks() {
		for (int rows = 0; rows < NBRICK_ROWS; rows++)
			for (int columns = 0; columns < NBRICK_COLUMNS; columns++) {
				double x = ((getWidth() - NBRICK_COLUMNS*BRICK_WIDTH - ((NBRICK_COLUMNS-1)*BRICK_SEP)) / 2) + columns*(BRICK_WIDTH + BRICK_SEP);
				double y = rows*(BRICK_HEIGHT + BRICK_SEP);
				makeBrick();
				if (rows % 10 == 0 || rows % 10 == 1) {
					brick.setColor(Color.RED);
				} else if (rows % 10 == 2 || rows % 10 == 3) {
					brick.setColor(Color.ORANGE);
				} else if (rows % 10 == 4 || rows % 10 == 5) {
					brick.setColor(Color.YELLOW);
				} else if (rows % 10 == 6 || rows % 10 == 7) {
					brick.setColor(Color.GREEN);
				} else if (rows % 10 == 8 || rows % 10 == 9) {
					brick.setColor(Color.CYAN);
				}
				add (brick, x, y);
			}
	}

	//Creates the GRect that will be all the bricks.
	private GRect makeBrick() {
		brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		return brick;
	}

	//Places the paddle in the center of the screen to begin the game.
	private void setUpPaddle() {
		paddle = makePaddle();
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		add (paddle, x, y);
	}	

	//Creates the paddle.
	private GRect makePaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	//This method allows the user to control the paddle with the mouse.
	public void mouseMoved (MouseEvent e) {
		double x = e.getX() - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		if (x > 0) {
			paddle.setLocation(x, y);
		}
		if (x < 0) {
			paddle.setLocation(0 , y);
		}
		if (x + PADDLE_WIDTH > getWidth()) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, y);
		}
	}

	//This method handles all of the gameplay.
	private void playBall() {
		add(score);
		//This "for" loop ensures that the game is played up to NTURNS times.
		for (int i=0; i < NTURNS; i++) {
			if (brickCounter != NBRICK_COLUMNS*NBRICK_ROWS) {
				clickToBegin();
			}
			makeBall();
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			vy = VELOCITY_Y;
			if (rgen.nextBoolean(0.5)) vx = -vx;
			while (true) {
				AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
				GObject collider = getCollidingObject(ball);
				if (hitLeftWall(ball) || hitRightWall(ball)) {
					bounceClip.play();
					vx = -vx;
				} else if (hitTopWall(ball)) {
					bounceClip.play();
					vy = -vy;
				} else if (collider == paddle) {
					bounceClip.play();
					//This if statement stops the ball from sticking to the paddle.
					if (ball.getY() < PADDLE_Y_OFFSET) {
						vy = -VELOCITY_Y;
					} else if ((ball.getY() <= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - 2*BALL_RADIUS) || ball.getY() >= getHeight() - PADDLE_Y_OFFSET - 2*BALL_RADIUS) {
						vy = -vy;
					}
				} else if (collider != null && collider != score) {
					bounceClip.play();
					remove (collider);
					vy = -vy;
					brickCounter ++;
					//This is the kicker! The ball's speed increases by 2% every time it breaks a brick.
					//The player can enjoy an increasingly difficult game while learning about compound growth.
					vx = 1.02*vx;
				}
				ball.move(vx, vy);
				pause(DELAY);	
				if (hitBottomWall(ball)) {
					lifeCounter--;
					remove(ball);
					break;
				}
				if (brickCounter == NBRICK_COLUMNS*NBRICK_ROWS) {
					remove (ball);
					endGameWin();
					break;
				}
				scoreKeeper();
			}
		}
		if (brickCounter != NBRICK_COLUMNS*NBRICK_ROWS) {
			endGameLose();
		}
	}






	//This method creates a "Click to Begin" label that disappears when the player clicks.
	private void clickToBegin() {
		GLabel startGame = new GLabel ("Click to Begin (" + lifeCounter + " Lives Left)");
		startGame.setFont(SCREEN_FONT);
		double x = (getWidth() - startGame.getWidth()) / 2;
		double y = (getHeight() - startGame.getHeight()) / 2;
		add (startGame, x, y);
		waitForClick();
		remove(startGame);
	}

	//This method creates the ball.
	private void makeBall() {
		ball = new GOval (BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		double x = (getWidth() - BALL_RADIUS) / 2;
		double y = (getHeight() - BALL_RADIUS) / 2;
		add (ball, x, y);
	}

	//Checks for the ball hitting the left wall.
	private boolean hitLeftWall(GOval ball) {
		return (ball.getX() <= 0);
	}

	//Checks for the ball hitting the right wall.
	private boolean hitRightWall(GOval ball) {
		return (ball.getX() >= getWidth() - 2*BALL_RADIUS);
	}

	//Checks for the ball hitting the ceiling.
	private boolean hitTopWall (GOval ball) {
		return (ball.getY() <= 0);
	}

	//Checks for the ball hitting the floor.
	private boolean hitBottomWall (GOval ball) {
		return (ball.getY() >= getHeight() - 2*BALL_RADIUS);
	}

	//This GObject checks the ball's four corners for an object and returns that object.
	private GObject getCollidingObject(GOval ball) {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX(), (ball.getY() + 2*BALL_RADIUS)) != null) {
			return getElementAt(ball.getX(), (ball.getY() + 2*BALL_RADIUS));
		} else if (getElementAt((ball.getX() + 2*BALL_RADIUS), ball.getY()) != null) {
			return getElementAt((ball.getX() + 2*BALL_RADIUS), ball.getY());
		} else if (getElementAt((ball.getX() + 2*BALL_RADIUS), (ball.getY() + 2*BALL_RADIUS)) != null) {
			return getElementAt((ball.getX() + 2*BALL_RADIUS), (ball.getY() + 2*BALL_RADIUS));
		} else {
			return null;
		}
	}

	//This method creates a GLabel that lets the player know his/her score.
	private void scoreKeeper() {
		remove (score);
		score = new GLabel ("Score = " + brickCounter);
		score.setFont(SCREEN_FONT);
		double x = (getWidth() - score.getWidth()) / 2;
		double y = (getHeight() - ((PADDLE_Y_OFFSET - score.getHeight()) / 2));
		add (score, x, y);
	}

	//This method creates a GLabel that lets the player know they have lost.
	private void endGameLose() {
		GLabel gameOver = new GLabel ("Game Over - You Lose");
		gameOver.setFont(SCREEN_FONT);
		double x = (getWidth() - gameOver.getWidth()) / 2;
		double y = (getHeight() - gameOver.getHeight()) / 2;
		add (gameOver, x, y);
	}

	//This method creates a GLabel that lets the player know they have won.
	private void endGameWin() {
		GLabel gameOver = new GLabel ("You Win - Congratulations");
		gameOver.setFont(SCREEN_FONT);
		double x = (getWidth() - gameOver.getWidth()) / 2;
		double y = (getHeight() - gameOver.getHeight()) / 2;
		add (gameOver, x, y);
	}
}



