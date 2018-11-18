/*
 * File: Breakout.java
 * -------------------
 * Name: Emilia Porubcin
 * Section Leader: Rachel Gardner
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
	
	private double totalBrickWidth = BRICK_SEP + BRICK_WIDTH;
	private double totalBrickHeight = BRICK_SEP + BRICK_HEIGHT;
	private GRect paddle, brick;
	private GOval ball;
	private double vx, vy, ballX, ballY;
	private int brickCounter;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	GLabel lose = new GLabel("You Lost :(");
	GLabel win = new GLabel("You Won! :)");
	GLabel click = new GLabel("Please Click to Start");
	
	public void run() {
		buildWorld();
		// loops for number of turns
		for(int i=0; i<NTURNS; i++) {
			play();
			// breaks loop if all bricks hit
			if(brickCounter==0) {
				break;
			}
		}
		removeAll();
	}
	
	/*
	 * Sets up world. 
	 * Precondition: nothing
	 * Postcondition: Window titled, canvas sized, bricks laid, paddle and ball built.
	 */
	private void buildWorld() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		layBricks();
		buildPaddle();
		buildBall();
	}
	
	/*
	 * Starts gameplay, producing paddle and ball and allowing
	 * user to start moving.
	 */
	private void play() {
		brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		
		// moves ball if bricks aren't all gone
		while(true && brickCounter > 0) {
			ball.move(vx, vy);
			
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			if(hitBottomWall(ball)) {
				remove(ball);
				buildBall();
				break;
			}
			
			GObject collider = getCollidingObject();
			if(collider == paddle) {
				vy = -Math.abs(vy);
			}
			else if(collider != null) {
				vy = -vy;
				brickCounter--;
				remove(collider);
			}
			pause(DELAY);
		}
	}
	
	/*
	 * Sets up world with full rows of bricks.
	 */
	private void layBricks() {
		for(int i = 0; i < NBRICK_COLUMNS; i++) {
			for(int j = 0; j < NBRICK_ROWS; j++) {
				brick = new GRect(
					(getWidth() - NBRICK_COLUMNS * totalBrickWidth + BRICK_SEP) / 2 + totalBrickWidth * j, // x
					BRICK_Y_OFFSET + i * (totalBrickHeight), // y
					BRICK_WIDTH, // width
					BRICK_HEIGHT // height
				);
				brick.setFilled(true);
				brick.setLineWidth(0);
				if(i < 2) {
					brick.setFillColor(Color.RED);
				}
				else if(i < 4) {
					brick.setFillColor(Color.ORANGE);
				}
				else if(i < 6) {
					brick.setFillColor(Color.YELLOW);
				}
				else if(i < 8) {
					brick.setFillColor(Color.GREEN);
				}
				else if(i < 10) {
					brick.setFillColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}
	
	/*
	 * builds paddle, centers
	 */
	private void buildPaddle() {
		// defines dimensions of paddle
		double paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		// makes and colors paddle
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		// adds paddle and mouse listeners
		add(paddle);
		addMouseListeners();
	}
	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		// keeps paddle within bounds of screen
		if(mouseX > (PADDLE_WIDTH / 2) && mouseX < (getWidth() - (PADDLE_WIDTH / 2))) {
			paddle.setCenterX(mouseX);
		}
	}
	
	/*
	 * builds ball
	 */
	private void buildBall() {
		ballX = (getWidth() - BALL_RADIUS) / 2;
		ballY = (getHeight() - BALL_RADIUS) / 2;
		ball = new GOval(ballX, ballY, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}
	
	/*
	 * returns an object that ball collides with, if applicable
	 */
	private GObject getCollidingObject() {
		double x = ball.getX();
		double y = ball.getY();
		double r = BALL_RADIUS;
		
		// checks upper left corner
		if(getElementAt(x, y) != null) {
			return getElementAt(x,y);
		}
		// checks upper right corner
		else if(getElementAt(x + 2 * r, y) != null) {
			return getElementAt(x + 2 * r, y);
		}
		// checks bottom left corner
		else if(getElementAt(x, y + 2 * r) != null) {
			return getElementAt(x, y + 2 * r);
		}
		// checks bottom right corner
		else if(getElementAt(x + 2 * r, y + 2 * r) != null) {
			return getElementAt(x + 2 * r, y + 2 * r);
		}
		else return null;
	}
	
	/*
	 * checks if ball hits top wall
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	/*
	 * checks if ball hits bottom wall
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - ball.getHeight();
	}
	
	/*
	 * checks if ball hits right wall
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	
	/*
	 * checks if ball hits left wall
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
}
