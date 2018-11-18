
/*
 * File: Breakout.java
 * -------------------
 * Name: Kylie Holland
 * Section Leader: Ben Barnett
 * 
 * This files implements the game Breakout. First it creates a set of bricks,a paddle, and a ball. The paddle moves to the x position of the mouse
 * and the ball moves according to a set of coded rules, destroying the bricks as it collides with them. 
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
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

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
	private RandomGenerator rgen = RandomGenerator.getInstance();
	GRect paddle;
	GOval ball;
	GObject collision;
	boolean win = false; 
	boolean lose = false;
	int ballNum = NTURNS;
	public void run() {
		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);		
		// Methods
		makeBricks();
		makePaddle();
		addMouseListeners();
		makeBall();
		moveBall();

		if (win == true) {
			GLabel win = new GLabel("You Won!", 210, getHeight()/2);
			add(win);
		}



		// Set the window's title bar text
		setTitle("CS 106A Breakout");



	}

/**
 * Method: moveBall()
 * Controls animation for the  ball object, including collisions and removals of bricks. 
 */
	
	private void moveBall() {
		//variables 
		double vy = VELOCITY_Y;
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5))
			vx = -vx;
		int brickNum = NBRICK_COLUMNS * NBRICK_ROWS;
		//while loop continues if there are bricks on the screen
		while (brickNum!=0) {

			ball.move(vx, vy);
			//calls collision helper
			GObject collider = getCollidingObject();
			//if there is an object at the x,y
			if (collider != null) {
				//if the object isn't a paddle (brick)
				if (collider != paddle) {
					remove(collider);
					brickNum--; 
					vy = -vy;
					//speeds up ball as game progresses
					if (brickNum%5 == 0) {
						vx = vx*1.1; 						
					}
				}
				else if (collider == paddle) {
					vy = -vy;
				}
				
			}
			//hitting the wall
			if ((ball.getX() + (BALL_RADIUS * 2)) > getWidth() || ball.getX() < 0) {
				vx = -vx;
			}  if (ball.getY() < 0) {
				vy = -vy;
			}
			//if ball falls past paddle, call newBall();	
			else if ( ball.getY() + (BALL_RADIUS * 2)> getHeight()) {
					remove(ball); 
					newBall();
					
			}
			pause(DELAY);
			

		} 
		//triggers win label
		win = true; 
	

	}
//if there are balls left (3 balls total), reduce the number and make another, else, end the game
	private void newBall() {
		if (ballNum > 1) {
		ballNum--; 
		makeBall();
		}
		else if (ballNum ==1){
			endGame();
		}
		
		
	}
	//label if balls run out
	private void endGame() {
		GLabel loss = new GLabel("You Lost :(", 210, getHeight()/2);
		add(loss);

		
	}
	//collision function
	private GObject getCollidingObject() {
		//top left corner check
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			collision = getElementAt(ball.getX(), ball.getY());
			return collision;
			//top right corner check
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			collision = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());

			return collision;
//bottom left corner check
		} else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			collision = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);

			return collision;
			//bottom right corner check
		} else if ((getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null)) {
			collision = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
			return collision;
		} else {

			return null;
		}
	}
//makes the ball
	private void makeBall() {
		double ballX = ((getWidth()/2) - BALL_RADIUS);
		double ballY = getHeight() - (PADDLE_Y_OFFSET + 100);
		ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball, ballX, ballY);

	}
//paddle moves to mouse if the mouse moves
	public void mouseMoved(MouseEvent e) {
		double paddleY = getHeight() - PADDLE_Y_OFFSET;

		double paddleX = e.getX();
		if (e.getX() <= (getWidth() - PADDLE_WIDTH)) {
			paddleX = e.getX();
			paddle.setLocation(paddleX, paddleY);

		} else {
			paddleX = (getWidth() - PADDLE_WIDTH);
			paddle.setLocation(paddleX, paddleY);
		}

	}
//makes the paddle
	private void makePaddle() {
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		double paddleX = getWidth() / 2 - (PADDLE_WIDTH / 2);
		paddle = new GRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		paddle.setFillColor(Color.black);
		add(paddle);

	}
//makes bricks using two for loops, and a series of if statements
	private void makeBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			double brickY = (BRICK_Y_OFFSET + (BRICK_HEIGHT * row) + (BRICK_SEP * row));
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				double brickX = ((col * BRICK_WIDTH) + (BRICK_SEP * col));
				GRect brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				if (row == 0 || row == 1) {
					brick.setColor(Color.RED);
					brick.setFillColor(Color.RED);
				} else if (row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
					brick.setFillColor(Color.ORANGE);
				} else if (row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
					brick.setFillColor(Color.YELLOW);
				} else if (row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
					brick.setFillColor(Color.GREEN);
				} else if (row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
					brick.setFillColor(Color.CYAN);
				}
				brick.setFilled(true);
				 add(brick);

			}
		}

	}

}
