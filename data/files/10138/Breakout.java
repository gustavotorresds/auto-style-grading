/*
 * File: Breakout.java
 * -------------------
 * Name: Jung-Won Ha	
 * Section Leader: Shanon Reckinger
 * 
 * This file will eventually implement the game of Breakout.
 * The core setup of the game consists of creating and adding bricks, a paddle, and ball to the screen.
 * The functions of the game allow you to move the paddle to hit the ball which will bounce of the walls 
 * to subsequently hit and remove bricks. If the paddle misses the ball and falls beneath the bottom of the screen, 
 * a life is subtracted. The user is provided with three lives to try and remove all the bricks. 
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
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 140.0;

	// Number of turns 
	public static final int NTURNS = 3;

	/** Private Instance Variables**/
	private double bricks = NBRICK_COLUMNS*NBRICK_ROWS;

	private GRect brick = null;

	private GRect paddle = null;

	private GOval ball = null;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;



	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		//Sets up elements of game
		setup();
		//Plays the game
		for(int turns = 0; turns < NTURNS; turns++) {
			addBall();
			waitForClick();
			setBallMovement();
			if(bricks == 0) {
				congratulations();
				break;
			}
			remove(ball);
		}
		if(bricks > 0) {
			gameOver();
		}
	}

	//Adds the bricks necessary for the startup of the game.
	private void setup() {
		addBricks();
		addPaddle();
	}
	private void addBricks() {
		for(int rows = 0; rows < NBRICK_ROWS; rows++) {
			for(int columns = 0; columns < NBRICK_COLUMNS; columns++) {
				
				double y = BRICK_Y_OFFSET + rows * (BRICK_HEIGHT + BRICK_SEP);
				double x = BRICK_SEP*1.5 + columns * (BRICK_SEP + BRICK_WIDTH);
				
				//Initially colors all the bricks cyan
				createBrick(x,y,Color.CYAN);
				
				//Divides the total number of rows by 5 and colors each of them a different color
				if(rows < NBRICK_ROWS / 1.25) {
					brick.setColor(Color.GREEN);
				}
				if(rows < NBRICK_ROWS / 1.67) {
					brick.setColor(Color.YELLOW);
				}
				if(rows < NBRICK_ROWS / 2.50) {
					brick.setColor(Color.ORANGE);
				}
				if(rows < NBRICK_ROWS / 5.00) {
					brick.setColor(Color.RED);
				}
			}
		}
	}
	//Creates private instance variable for creating a brick
	private void createBrick(double brickX, double brickY, Color c) {
		brick = new GRect(brickX,brickY,BRICK_WIDTH,BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(c);
		add(brick);
	}
	//Creates private instance variable for creating a paddle
	private void addPaddle() {
		//Initial x and y coordinates for the paddle
		double paddleX = getWidth() / 2 - PADDLE_WIDTH / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle = new GRect(paddleX,paddleY,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	public void mouseMoved (MouseEvent e) {
		//Sets so that the mouse moves with the center of the paddle
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double paddleX = e.getX() - PADDLE_WIDTH / 2;
		/* 
		 * If the x position of the mouse is less than half the width of the paddle from the right,
		 * or if its greater than half a paddle width from the left of the screen,
		 * then it sets the x coordinate of the center of the paddle to be the x coordinate of the mouse.
		 */
		if(e.getX() < getWidth() - PADDLE_WIDTH / 2 && e.getX() > PADDLE_WIDTH / 2) {
			paddle.setLocation(paddleX,paddleY);
		}
	}

	private void addBall() {
		double ballX = getWidth() / 2 - BALL_RADIUS;
		double ballY = getHeight() / 2 - BALL_RADIUS;
		ball = new GOval (ballX,ballY,BALL_RADIUS * 2,BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	private void setBallMovement() {
		//Randomly sets the ball's initial x velocity
		vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;

		//Changes the ball's velocity when colliding with walls
		while(ball.getY() <= getHeight()) {
			//Checks for Walls
			wallCollision();

			//Conditions for how the ball should react when colliding with certain objects
			GObject collider = getCollidingObject();
			if(collider == paddle) {
				double topPaddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
				double bottomBallY=ball.getY() + BALL_RADIUS * 2;

				if(bottomBallY >= topPaddleY && bottomBallY < topPaddleY + 3) {
					vy = -vy;
				}
			}
			//If the ball collides with something that is neither a wall nor paddle, it must be a brick
			else if(collider != null) {
				remove(collider);
				bricks--;
				vy = -vy;
				//Exits the loop when all the bricks have been removed
				if(bricks == 0) {
					removeAll();
					break;
				}
			}
			//Updates the ball's movement
			ball.move(vx, vy);
			pause(DELAY);
		}		
	}
	private void wallCollision() {
		if(hitLeftWall(ball)||hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball)){
			vy = -vy;
		}
	}
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - 2 * BALL_RADIUS;	
	}
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	//Checks if any of 4 corners of the square the ball is inscribed in has collided with something
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(),ball.getY()) != null){
			return(getElementAt(ball.getX(),ball.getY()));
		} 
		else if(getElementAt(ball.getX(),ball.getY() + 2 * BALL_RADIUS) != null) {
			return(getElementAt(ball.getX(),ball.getY() + 2 * BALL_RADIUS));
		} 
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY()) != null){
			return(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY()));
		} 
		else if(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + 2 * BALL_RADIUS) != null) {
			return(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + 2 * BALL_RADIUS));
		} 
		//Return null if no objects are present
		else {
			return null;
		}
	}
	//Prints a message when the user has won the game
	private void congratulations(){
		GLabel winner = new GLabel("YOU WON!!!");
		winner.setFont("Courier-52");
		winner.setColor(Color.GREEN);
		winner.setLocation(getWidth() / 2 - winner.getWidth() / 2 , getHeight() / 2 - winner.getAscent() / 2);
		add(winner);
	}

	//Prints a message when the user has lost the game
	private void gameOver() {
		GLabel loser = new GLabel("GAME OVER");
		loser.setFont("Courier-52");
		loser.setColor(Color.RED);
		loser.setLocation(getWidth() / 2 - loser.getWidth() / 2 , getHeight() / 2 - loser.getAscent() / 2);
		add(loser);
	}
}


