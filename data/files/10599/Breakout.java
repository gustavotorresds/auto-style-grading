/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Breakout extends GraphicsProgram {

	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;
	public static final int NBRICK_COLUMNS = 10;
	public static final int NBRICK_ROWS = 10;
	public static final double BRICK_SEP = 4;
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);
	public static final double BRICK_HEIGHT = 8;
	public static final double BRICK_Y_OFFSET = 70;
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;
	public static final double PADDLE_Y_OFFSET = 30;
	public static final double BALL_RADIUS = 10;
	public static final double VELOCITY_Y = 3.0;
	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;
	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;
	// Number of turns 
	public static final int NTURNS = 3;
	//Creates the ball as a global object 
	private GOval ball = null;
	//Generates random velocity in the X direction for the ball
	private RandomGenerator randomXVelocity = new RandomGenerator();
	//Creates the paddle that will be used and will be moved when the mouse is dragged 
	private GRect paddle = null;	
	//stores velocity values to use throughout the game
	double xVel = 0;
	double yVel = VELOCITY_Y;
	//stores total number of bricks to determine when the game is over 
	int bricksTotal = 0;
	//aids in the making of the rectangles by keeping track of spacing  
	int xCounter = 0;
	int yCounter = 0;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		makeBricks();
		makePaddle();
		makeBall();
		playGame();
	}
	//alotts for number of lives for the player and for the game to be played smoothely
	private void playGame() {
		for(int gameNum = 0; gameNum <NTURNS; gameNum++) {
			add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
			waitForClick();
			while(!over()) {
				pause(DELAY);
				bounceBack();
				moveBall();
			}
		}
	}
	//checks for either up/down or left/right collisions to redirect the ball using the correct velocities 
	private void bounceBack() {
		if(ball.getX() >= getWidth() ||  ball.getX() <= 0) {
			xVel = -xVel;	
		}
		if(collision()) {
			yVel = -yVel;
		}
		if(ball.getY() <= 0) {
			xVel = -xVel;
			yVel = -yVel;
		}
	}
	//checks to see what collisions are being made using all 4 corners of the ball
	private boolean collision() {
		if(getElementAt(ball.getX(),ball.getY()) != null) {
			remove(getElementAt(ball.getX(), ball.getY()));
			return true;
		}
		if(getElementAt(ball.getX(),ball.getY()+BALL_RADIUS*2) != null) {
			remove(getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2));
			return true;
		}
		if(getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY()) != null) {
			remove(getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()));
			return true;
		}
		if(getElementAt(ball.getX()+BALL_RADIUS*2,ball.getY()+BALL_RADIUS*2) != null) {
			remove(getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2));
			return true;
		}
		return false;
	}
	private void moveBall() {		
		ball.move(xVel, yVel);	
	}
	//checks for if the player has won or lost the game and terminates it 
	private boolean over() {
		if(ball.getY()+BALL_RADIUS*2 >= getHeight()) {
			return true;
		}
		if(winnerWinnerChickenDinner() == true) {
			return true;
		}
		return false;
	}
	//chekcs for if the player has won the game indicated by whether the number of bricks collected is equal to the starting number of bricks
	private boolean winnerWinnerChickenDinner() {
		if(bricksTotal == NBRICK_COLUMNS * NBRICK_ROWS ) {
			return true;
		}
		return false;
	}
	private void makeBall() {
		xVel = randomXVelocity.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX) ;
		ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2-PADDLE_Y_OFFSET-PADDLE_HEIGHT);
	}
	private void makeBricks() {
		for(int row = 0; row < NBRICK_ROWS; row ++ ) {				
			yCounter += BRICK_HEIGHT + BRICK_SEP;			
			for(int col = 0; col < NBRICK_COLUMNS; col++) {							
				GRect brick = new GRect(xCounter, yCounter, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick);
				xCounter += BRICK_WIDTH + BRICK_SEP;
				if(col == NBRICK_COLUMNS-1) {
					xCounter = 0;
				}	
				if(row == 0 || row == 1) {
					brick.setColor(Color.RED);
				}
				if(row == 2 || row == 3) {
					brick.setFillColor(Color.ORANGE);
				}
				if(row == 4 || row == 5) {
					brick.setFillColor(Color.YELLOW);
				}
				if(row == 6 || row == 7) {
					brick.setFillColor(Color.GREEN);
				}
				if(row == 8 || row == 9) {
					brick.setFillColor(Color.CYAN);
				}	
			}
		}
	}
	//creates a random double for x velocity
	private double getRandomX() {
		return randomXVelocity.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	}
	public void makePaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		addMouseListeners();
	}
	//when the mouse is moved the paddle is dragged across the screen
	public void mouseMoved(MouseEvent e) {
		add(paddle, getWidth()/2-PADDLE_WIDTH/2, getHeight()-PADDLE_Y_OFFSET);
		if(e.getX() < getWidth() - PADDLE_WIDTH || e.getX() > 0) {
			paddle.setLocation(e.getX(), getHeight()-PADDLE_Y_OFFSET);
		}
	}
}



