/*
 * File: Breakout.java
 * -------------------
 * Name: Alexa Thomson
 * Section Leader: Rachel Gardner
 * This file implements the game of Breakout, in which the user
 * moves the paddle to hit a ball towards a wall of bricks.
 * When the ball hits a brick, the brick disappears
 * The user wins by removing all the bricks from the screen
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

	// The ball's minimum and maximum horizontal velocity;
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//instance variables
	private GRect brick;
	private GRect paddle;
	private GOval ball;
	private double vx;
	private double vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	public AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	//this is the starting number of bricks on the screen
	private int countBricks = 100;

	public void run() {
		setUpGame();
		for(int i=0; i<NTURNS; i++) {
			playGame();
			if(countBricks == 0) {
				printYouWon();
				break;
			}
		}
		if(countBricks > 0) {
			printGameOver();
		}
	}
	// this method creates the opening screen for the game
	// the screen includes a rainbow wall of brick and a single paddle
	private void setUpGame() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		createPaddle();
	}
	// this method builds the bricks one at a time, a row at a time, and colors the bricks by row 
	private void setUpBricks() {
		for(int row = 0; row < NBRICK_ROWS; row++) {
			for(int addBrick = 0; addBrick < NBRICK_COLUMNS; addBrick++) {
				// multiplying addBrick with the BRICK_WIDTH and BRICK_SEP ensures that the program is aware of which brick to build next in the row
				double x = getWidth()/2 - (BRICK_WIDTH*NBRICK_COLUMNS + BRICK_SEP*(NBRICK_COLUMNS - 1))/2 + addBrick * BRICK_WIDTH + addBrick * BRICK_SEP;
				// the y-coordinates are measured from the top of the canvas down
				double y = BRICK_Y_OFFSET + row*BRICK_HEIGHT + row*BRICK_SEP;
				brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);

				// the following sets the colors to switch every two rows
				if(row < 2) {
					brick.setColor(Color.RED);
				}
				if(row == 2 || row == 3) {
					brick.setColor(Color.ORANGE);
				}
				if(row == 4 || row == 5) {
					brick.setColor(Color.YELLOW);
				}
				if(row == 6 || row == 7) {
					brick.setColor(Color.GREEN);
				}
				if(row == 8 || row == 9) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}
	//this method builds the paddle and ensures that it follows the mouse
	private void createPaddle() {
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		double paddlex = getWidth()/2 - PADDLE_WIDTH/2;
		double paddley = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, paddlex, paddley);
		//the following will make the paddle follow the mouse
		addMouseListeners();
	}
	// this method will cause the paddle to follow the mouse in the x direction
	public void mouseMoved(MouseEvent e) {
		double paddlex = e.getX() - PADDLE_WIDTH/2;
		double paddley = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle.setLocation(paddlex, paddley);
		if(paddlex >= getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, paddley);
		}
		if(paddlex <= 0) {
			paddle.setLocation(0, paddley);
		}
	}
	// this method draws the solid black ball and adds it to the center of the screen
	private void createBall() {
		ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		double ballx = getWidth()/2 - BALL_RADIUS*2;
		double bally = getHeight()/2 - BALL_RADIUS*2;
		add(ball,ballx,bally);
	}
	// this method controls where the ball goes and how it moves
	private void playGame() {
		createBall();
		waitForClick();
		setBallVelocity();
		while(true) {
			moveBall();
			// if the ball passes through the bottom of the screen, that turn of the game ends
			if(ball.getY() >= getHeight()) {
				break;
			}
			// if the user has succeeded in clearing all of the bricks, the program ends
			if(countBricks == 0) {
				break;
			}
		}
	}
	// this method defines the ball's velocity and directionality
	private void setBallVelocity() {
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if(rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
	}
	// this method makes the ball move in both the x and y directions
	// if the ball hits a wall, it switches the corresponding x or y direction
	private void moveBall() {
		ball.move(vx, vy);
		if(hitLeftWall(ball)||hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball)) {
			vy = -vy;
		}
		// the ball can either collide with bricks or the paddle, and each will have different effects
		GObject collider = getCollidingObject();
		// if the ball collides with the paddle, the ball will switch y directions
		if(collider == paddle) {
			// this ensures that the ball does not get stuck to the paddle
			if(ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 + VELOCITY_Y && ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2) {
				vy = -vy;
			}
		}
		// if the ball collides with anything besides the paddle, it is colliding with a brick
		// the brick that the ball hits is then removed from the screen
		// then the ball changes directions
		else if(collider != null) {
			bounceClip.play();
			remove(collider);
			vy =-vy;
			countBricks--;
		}
		//pause
		pause(DELAY);
	}
	//the following methods ensure that the ball stays within the bounds of the screen
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}
	private boolean hitTopWall(GOval ball) {
		return ball.getY() < getHeight() - getHeight();
	}
	// this method tests whether the ball is colliding with an object at each of the
	// four corners of the ball (when pretending the ball is inside of a square)
	private GObject getCollidingObject() {
		if(getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if(getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
		}
		else if(getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
		}
		else if(getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
		}
		else {
			return null;
		}		
	}
	// this method prints "Game Over" when the user uses all 3 turns and still has bricks let
	private void printGameOver() {
		GLabel gameOver = new GLabel("Game Over! :(");
		double x = getWidth()/2 - gameOver.getWidth()/2;
		double y = getHeight()/2 - gameOver.getHeight()/2;
		add(gameOver,x,y);
	}
	//this method prints "You Won!" when the user updates all the blocks
	private void printYouWon() {
		GLabel youWon = new GLabel("You Won!");
		double x = getWidth()/2 - youWon.getWidth()/2;
		double y = getWidth()/2 - youWon.getHeight()/2;
		add(youWon,x,y);
	}
}
