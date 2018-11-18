/*
 * File: Breakout.java
 * -------------------
 * Name: Jan Chen
 * Section Leader: Rhea Karuturi
 * 
 * This file implements the basics of the game of Breakout.
 * Please see supporting file for extensions.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// instance variables //
	private GRect paddle = null;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GOval ball = null;
	private GObject collObj = null;
	private double vx = 0;
	private double vy = 0;
	private int remBricks = NBRICK_COLUMNS * NBRICK_ROWS;
		
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

	// Paddle Y-axis
	public static final double PADDLE_Y = CANVAS_HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
	
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

	public void run() {
		setUpGame();
		playGame();
	}

	// method capture the key steps in playing the game //
	private void playGame() {
		for (int turnNumber = 0; turnNumber < NTURNS; turnNumber ++) { //can only play the number of turns as specified//
			ball = drawBall();
			checkForBounces();
		}
		if(remBricks > 0) {
			gameOverLoss();
		} else {
			gameOverWon();
		}
	}

	// method capture the key steps in setting up the game //
	private void setUpGame() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setupBricks();
		paddle = drawPaddle();
	}

	// method to track paddle with mouse //
	public void mouseMoved (MouseEvent e) {
		int mouseX = e.getX();
		double paddleX = mouseX;
		if (mouseX > CANVAS_WIDTH - PADDLE_WIDTH) {
			paddleX = CANVAS_WIDTH - PADDLE_WIDTH;
		}
		paddle.setLocation(paddleX,PADDLE_Y);
	}
	
	// method to colour the bricks and create the number of rows //
	private void setupBricks() {
		for (int rowNumber = 0; rowNumber < NBRICK_ROWS; rowNumber++) {
			Color rowColor = null;
			
			// changing the colours of the rows depending on row number //
			if (rowNumber%10==0 || rowNumber%10==1) {
				rowColor=Color.RED;
			} else if (rowNumber%10==2 || rowNumber%10==3) {
				rowColor=Color.ORANGE;
			} else if (rowNumber%10==4 || rowNumber%10==5) {
				rowColor=Color.YELLOW;
			} else if (rowNumber%10==6 || rowNumber%10==7) {
				rowColor=Color.GREEN;	
			} else {
				rowColor=Color.CYAN;	
			}

			//draw row//
			drawBrickRow(rowColor, rowNumber);
			}
		}
		
	// draws a roll of bricks in the colour requested //	
	private void drawBrickRow(Color rowColor, int rowNumber) {
		// calculates the x position of the left most brick //
		double leftMostX = (CANVAS_WIDTH/2.0) - (((NBRICK_COLUMNS * BRICK_WIDTH) + ((NBRICK_COLUMNS - 1) * BRICK_SEP))/2.0);	
		for (int brickNumber = 0; brickNumber < NBRICK_COLUMNS; brickNumber++) {
			double brickX = leftMostX + (brickNumber * BRICK_WIDTH) + (brickNumber * BRICK_SEP);	
			double rowY = BRICK_Y_OFFSET + rowNumber * BRICK_HEIGHT + rowNumber * BRICK_SEP;
			GRect brick = new GRect (brickX, rowY, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setColor(rowColor);
			brick.setFilled(true);
			add(brick);				
		}	
	}
	
	// draws paddle //	
	private GRect drawPaddle() {
		GRect paddle = new GRect ((CANVAS_WIDTH - PADDLE_WIDTH)/2, PADDLE_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
		return paddle;
	}

	// draws ball //	
	private GOval drawBall() {
		double initialBallX = (CANVAS_WIDTH / 2) - (BALL_RADIUS / 2);
		double initialBallY = (CANVAS_HEIGHT / 2) - (BALL_RADIUS / 2);
		GOval ball = new GOval (initialBallX, initialBallY, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
		return ball;
	}
	
	// draws Game Over Loss sign //	
	private void gameOverLoss() {
		double boxWidth = 0.75 * CANVAS_WIDTH;
		double boxHeight = 0.2 * CANVAS_HEIGHT;
		GRect gameOverBox = new GRect (CANVAS_WIDTH/2.0 - boxWidth/2.0, CANVAS_HEIGHT/2.0 - boxHeight/2.0, boxWidth, boxHeight);
		gameOverBox.setFilled(true);
		gameOverBox.setColor(Color.PINK);
		add(gameOverBox);
		
		GLabel gameOverLabel = new GLabel ("Game Over. You Lost :(");
		gameOverLabel.setFont("Courier-18");
		double labelPosX = gameOverLabel.getWidth();
		double labelPosY = gameOverLabel.getAscent();
		add(gameOverLabel, CANVAS_WIDTH/2.0 - labelPosX/2.0, CANVAS_HEIGHT/2.0 + labelPosY/2.0);
	}
	
	// draws Game Over Won sign //	
	private void gameOverWon() {
		double boxWidth = 0.75 * CANVAS_WIDTH;
		double boxHeight = 0.2 * CANVAS_HEIGHT;
		GRect gameWinBox = new GRect (CANVAS_WIDTH/2.0 - boxWidth/2.0, CANVAS_HEIGHT/2.0 - boxHeight/2.0, boxWidth, boxHeight);
		gameWinBox.setFilled(true);
		gameWinBox.setColor(Color.GREEN);
		add(gameWinBox);
		
		GLabel gameWinLabel = new GLabel ("Game Over. You Won!! :)");
		gameWinLabel.setFont("Courier-18");
		double labelPosX = gameWinLabel.getWidth();
		double labelPosY = gameWinLabel.getAscent();
		add(gameWinLabel, CANVAS_WIDTH/2.0 - labelPosX/2.0, CANVAS_HEIGHT/2.0 + labelPosY/2.0);
	}
		
	// method for getting the ball to bounce around the screen (and check for collisions) //
	private void checkForBounces() {
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean (0.5)) {
			vx = -vx;
		}
		vy = VELOCITY_Y;
		
		// update velocity & visualisation//
		while(true){
			if(remBricks==0) {
				break;
			}
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}
			if(hitTopWall(ball)) {
				vy = -vy;
			}
			if (hitBottomWall(ball)){
				break;
			}
			ball.move(vx, vy);
			pause(DELAY);
			checkForCollisions();
		}
		remove(ball);
	}
	
	// hits left wall //
	private boolean hitLeftWall (GOval ball) {
		return ball.getX() <= 0;
	}

	// hits right wall //
	private boolean hitRightWall (GOval ball) {
		return ball.getX() >= CANVAS_WIDTH - ball.getWidth();
	}

	// hits bottom wall //
	private boolean hitBottomWall (GOval ball) {
		return ball.getY() >= CANVAS_HEIGHT - ball.getHeight();
	}

	private boolean hitTopWall (GOval ball) {
		return ball.getY() <= 0;
	}

	// method to look for and act if collisions occur //
	private void checkForCollisions() {
		GObject collObj = getCollidingObject();
		if (collObj == paddle) {
			vy = -vy;
			ball.move(vx, vy);
		} else if (collObj != null) {
			remove(collObj);
			remBricks = remBricks - 1;
			vy = -vy;
			ball.move(vx, vy);
		}
	}
	
	// defines collision object //
	private GObject getCollidingObject(){
		double x = ball.getX();
		double y = ball.getY();
		if(getElementAt(x, y) !=  null){ // test top right corner //
			collObj = getElementAt(x, y);
		} else if (getElementAt(x + 2 * BALL_RADIUS, y) !=  null){ // test top left corner //
			collObj = getElementAt(x + 2 * BALL_RADIUS, y);
		} else if (getElementAt(x, y + 2 * BALL_RADIUS) !=  null){ // test bottom right corner //
			collObj = getElementAt(x, y + 2 * BALL_RADIUS);
		} else if (getElementAt(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS) != null) { // test bottom left corner //
			collObj = getElementAt(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS);
		} else {
			return null;
		}
		return collObj;
	}
	
}
