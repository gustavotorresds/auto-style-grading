/*
 * File: Breakout.java
 * -------------------
 * Name: Daniela Cuneo
 * Section Leader: Julia Daniel
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

	//Instance variable paddle, ball, velocity of ball, collider
	private GRect paddle = null;
	private GOval ball = null;
	private double vx = 0;
	private double vy = VELOCITY_Y;
	private GObject collider = null;

	//Random variable generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setup();
		addMouseListeners();
		play();
	}

	//Method for playing game
	private void play() {
		//Introducing counter for number of bricks
		int counter = 0;

		//Loop that repeats game until player loses NTURNS or wins the game
		for(int i = 0 ; i < NTURNS ; i++) {
			waitForClick();
			addStartingBall();
			vx = rgen.nextDouble(VELOCITY_X_MIN , VELOCITY_X_MAX);
			if(rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			while(true) {
				ball.move(vx,vy);
				GObject collider = getCollidingObject();
				if(collider == null) {
					changeDirection();
				}
				else if(collider == paddle) {
					vy = -vy;
				}
				else {
					vy = -vy;
					remove(collider);
					counter ++;
					if(counter == NBRICK_COLUMNS * NBRICK_ROWS) {
						break;
					}
				}
				pause(DELAY);

				//break if ball touches bottom wall
				if(ball.getY() > getHeight() - ball.getHeight()) {
					break;
				}
			}
			remove(ball);
			if(counter == NBRICK_COLUMNS * NBRICK_ROWS) {
				break;
			}	
		}

		//Print outcome
		if(counter == NBRICK_COLUMNS * NBRICK_ROWS) {
			addLabelWon();
		}
		else {
			addLabelLost();
		}
	}

	//Method that adds label when player wins
	private void addLabelWon() {
		GLabel labelWon = new GLabel ("You won!");
		add(labelWon, getWidth() / 2 - labelWon.getWidth() / 2, getHeight() / 2);		
	}

	//Method that adds label when player loses
	private void addLabelLost() {
		GLabel labelLost = new GLabel ("You lost!");
		add(labelLost, getWidth() / 2 - labelLost.getWidth() / 2, getHeight() / 2);
	}

	//Method that makes ball bounce off walls
	private void changeDirection() {
		if(ball.getY() < 0) {
			vy = -vy;
		}
		if(ball.getX() > getWidth() - ball.getWidth() || ball.getX() < 0) {
			vx = -vx;
		}
	}

	//Method that finds colliding object
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(),ball.getY()) != null) {
			collider = getElementAt(ball.getX(),ball.getY());	
		}
		else if(getElementAt(ball.getX(),ball.getY() + 2 * BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX(),ball.getY()+ 2 * BALL_RADIUS);	
		}
		else if(getElementAt(ball.getX() + 2 * BALL_RADIUS ,ball.getY() + 2 * BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX()+ 2 * BALL_RADIUS , ball.getY()+ 2 * BALL_RADIUS);	
		}
		else if(getElementAt(ball.getX() + 2 * BALL_RADIUS ,ball.getY()) != null) {
			collider = getElementAt(ball.getX()+ 2 * BALL_RADIUS , ball.getY());	
		}
		else if(getElementAt(ball.getX() - 1 , ball.getY() + BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX() - 1 , ball.getY() + BALL_RADIUS);
		}	
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2 + 1 , ball.getY() + BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX() + BALL_RADIUS * 2 + 1 , ball.getY() + BALL_RADIUS);
		}
		else {
			collider = null;
		}	
		return collider;
	}

	//Method that adds ball to the center
	private void addStartingBall() {
		ball = new GOval (getWidth() / 2 - BALL_RADIUS , getHeight() / 2 - BALL_RADIUS , BALL_RADIUS * 2 , BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);	
	}

	//Method that moves paddle
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if(mouseX < getWidth() - PADDLE_WIDTH && mouseX > 0) {
			paddle.setLocation(mouseX , getHeight() - PADDLE_Y_OFFSET);
		}
	}

	//Method that creates bricks and paddle
	private void setup() {
		createBricks();
		createPaddle();
	}

	//Method that creates paddle
	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH , PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, getWidth()/2 - PADDLE_WIDTH/2 , getHeight() - PADDLE_Y_OFFSET);
	}

	//Method that creates bricks 
	private void createBricks() {
		for(int row = 0 ; row < NBRICK_ROWS ; row ++) {
			for(int col = 0 ; col < NBRICK_COLUMNS ; col ++) {
				double brickX = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH - NBRICK_COLUMNS * BRICK_SEP) / 2 + col * BRICK_SEP + col * BRICK_WIDTH ;
				double brickY = BRICK_Y_OFFSET + row * BRICK_SEP + row * BRICK_HEIGHT ;
				GRect brick = new GRect(brickX , brickY , BRICK_WIDTH , BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);

				//Paint bricks with rainbow sequence
				if(row % 10 == 0 || row % 10 == 1 ) {
					brick.setColor(Color.RED);
				}
				else if(row % 10 == 2 || row % 10 == 3 ){
					brick.setColor(Color.ORANGE);
				}
				else if(row % 10 == 4 || row % 10 == 5 ){
					brick.setColor(Color.YELLOW);
				}
				else if(row % 10 == 6 || row % 10 == 7 ){
					brick.setColor(Color.GREEN);
				}
				else {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}
}

