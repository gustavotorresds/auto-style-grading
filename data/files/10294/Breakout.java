/*
 * File: Breakout.java
 * -------------------
 * Name: Colton Swingle
 * Section Leader: Esteban
 * 
 * This program draws the bricks to play Breakout and then allows the mouse to be used
 * to control the paddle to play the game.
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

	public static final int NBRICK_COLUMNS = 3;
	
	public static final int NBRICK_ROWS = 1;
	
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

	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	public static final double DELAY = 1000.0 / 60.0;

	public static final int NTURNS = 3;
	
	private GRect paddle = null;
	private GOval ball = null;
	private int totalBrickNum = NBRICK_COLUMNS * NBRICK_ROWS;
	private double vx;
	private double vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); 

		setUpGame();
		//Runs the game only if there are still bricks. Gives 3 balls (3 chances to play)
		
			for (int numRounds = 0; numRounds < NTURNS; numRounds++) {
				if (totalBrickNum >0) {
					playGame();
				}
			}
		addMouseListeners();
	}
	
	/* Sets up the game before starting the game to play. Creates the bricks and the paddle and 
	 * adds them to the canvas. 
	 */
	private void setUpGame() {
		setUpBricks();
		paddle = createPaddle();
		addPaddle();
		
	}
	private void setUpBricks() {
		for (int numRows = 0; numRows < NBRICK_ROWS; numRows++) {
			fillRow(numRows);
		}
	}
	
	private void fillRow(int numRows) {
		for (int numBricks = 0; numBricks < NBRICK_COLUMNS; numBricks++) {
			GRect brick = new GRect(BRICK_WIDTH,BRICK_HEIGHT);
			brick.setFilled(true);		
			//Colors the bricks row by row. Takes parameter numRows and object brick.
			setRowColors(numRows, brick);
			brick.setLocation(1.5 * BRICK_SEP + numBricks * (BRICK_WIDTH + BRICK_SEP), 
							  BRICK_Y_OFFSET + numRows * (BRICK_HEIGHT + BRICK_SEP));
			add(brick);
		}
	}
	
	/* Colors the rows by checking the row number modulus 10. Alternates color every 2 rows and runs
	 * the same pattern: red, orange, yellow, green, cyan. 
	 */
	private void setRowColors(int numRows, GRect brick) {
		if (numRows % 10 < 2) {
			brick.setColor(Color.RED);
		} else if (numRows % 10 < 4){
			brick.setColor(Color.ORANGE);
		} else if (numRows % 10 < 6) {
			brick.setColor(Color.YELLOW);
		} else if (numRows % 10 < 8) {
			brick.setColor(Color.GREEN);
		} else {
			brick.setColor(Color.CYAN);
		}
	}

	private GRect createPaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	private void addPaddle() {
		double paddleX = (getWidth() - PADDLE_WIDTH)/2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, paddleX, paddleY);
	}
	
	/* Tracks the mouse and moves the paddle with the mouse in the middle of the paddle.
	 * The paddle cannot move off the screen. If the paddle is going to move off the screen,
	 * the location is locked at the ends of it (so it cannot move past). 
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double mouseY = e.getY();
		if (mouseX >= getWidth() - PADDLE_WIDTH/2) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		} else if (mouseX <= PADDLE_WIDTH/2){
			paddle.setLocation(0,getHeight() - PADDLE_Y_OFFSET);
		} else {
		paddle.setLocation(mouseX - PADDLE_WIDTH/2,getHeight() - PADDLE_Y_OFFSET);
		}
	}
	
	/* This method plays the game and is called each time a ball is added. 
	 */
	private void playGame() {
		ball = createBall();
		addBall();
		waitForClick();
		animationLoop();
		remove(ball);
	}
	
	private GOval createBall() {
		GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		return ball;
	}
	
	/* Adds the ball to the middle of the screen. Uses the random number generator to assign an 
	 * initial x_velocity between the minimum x velocity and the maximum x velocity with a 
	 * 50% chance to be negative or positive. 
	 */
	private void addBall() {
		ball.setLocation((getWidth() - 2 * BALL_RADIUS)/2,(getHeight() - 2 * BALL_RADIUS)/2);
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = - vx;
		}
		vy = VELOCITY_Y;
		add(ball);
	}
	
	/* The ball changes x velocity if it hits the side walls, and changes y velocity if it hits
	 * the top wall. 
	 */
	private void addWallBounce() {
		if (ball.getX() <= 0 || ball.getX() >= getWidth() - 2 * BALL_RADIUS) {
			vx = -vx;
		}
		if (ball.getY() <= 0) {
			//ball.getY() >= getHeight() - 2 * BALL_RADIUS is bottom of wall -> game over(add later)
			vy = -vy;
		}
	}
	
	/* Checks the four corners of the object for collisions with another object. Starts with
	 * The top left corner and top right corner and then checks the bottom two.
	 * It moves to check the next corner only if the current corner has no object (is null). 
	 */
	private GObject getCollidingObject() {
		GObject collidingObject = getElementAt(ball.getX(),ball.getY());
		if (collidingObject == null) {
			collidingObject = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		}
		if (collidingObject == null) {
			collidingObject = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		}
		if (collidingObject == null) {
			collidingObject = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		}
		return collidingObject;
	}
	
	/* This method runs the actual animation loop with the delay given so the ball can be seen.
	 * It stops if the ball goes off the bottom of the screen (and if the player still has 
	 * balls left, another turn will begin). This method also checks if there are no bricks left
	 * and ends the game with a message if this is the case. 
	 */
	private void animationLoop() {
		while (ball.getY() <= getHeight() && totalBrickNum > 0) {
			ball.move(vx, vy);
			addWallBounce();
			collisionResponse();
			pause(DELAY);
			if (totalBrickNum == 0) {
				remove(ball);
			}
		}
	}
	
	/* This method checks if the ball has collided with bricks or the paddle. It will bounce
	 * off the paddle back up the screen. It will bounce off bricks and then remove the brick. 
	 */
	private void collisionResponse() {
		GObject collider = getCollidingObject();
		if (collider != null) {
			/* Checks if the ball is hitting the side of the paddle. If so it will bounce of the side
			 * so that it does not get "stuck" to the paddle.
			 */
			if (collider == paddle) {
				if (ball.getY() > paddle.getY() - BALL_RADIUS) {
					vx = -vx;
				} else {
					vy = -vy;
				}
			//If the object is not the paddle it is a brick. Bounces and removes the brick.	
			} else {
				vy = -vy;
				remove(collider);
				//Subtracts one from the total number of bricks remaining to keep track of how many are left.
				totalBrickNum--;
			}
		}
	}
}
