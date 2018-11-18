/*
 * File: Breakout.java
 * -------------------
 * Name: Larsen Jones
 * Section Leader: Julia Daniel
 * 
 * This program runs the game Breakout (aka brick breaker for those who used to have a BB). The player has
 * horizontal control of the paddle (using the mouse) and tries to reflect the ball off the paddle into a wall of bricks.
 * If the ball passes the paddle, then the player loses that round. The player is given a certain number of rounds to try and break all the bricks.
 * 
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
	
	// creates instance variables for the paddle and ball
	private GRect paddle = null;
	private GOval ball = null;

	// instance variable to create collider counter; this is used to track when the game
	// has been won
	private double colliderCount;
	
	// instance variables for the ball location
	private double vx;
	private double vy;
	
	// instance variable for random number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setupGame();
		playGame();
	}
	
	// this method creates the ball, starts moving the ball and then runs a while loop that will 
	// navigate the ball around the screen (monitoring for collisions) until either the round is over
	// or the player wins. When the round ends, it will remove the ball and then jump back to a for loop to 
	// create a new ball and re-run. After the player has played NTURNS, then the game is over. 
	private void playGame() {
		for (int i = 0; i < NTURNS; i++) {
			createBall();
			startBall();
			while(!roundOver()) {
				moveBall();
				checkForCollisions();
				pause(DELAY);
				if(gameOver()) {
					return;
				}
			}
			remove(ball);
			if(gameOver()) {
				return;
			}
		}
		if(gameOver()) {
			winningScreen();
		}
		else {
			losingScreen();
		}
	}

	// prints red screen with LOSER when the player loses
	private void losingScreen() {
		GRect background = new GRect(getWidth(), getHeight());
		background.setFilled(true);
		add(background, 0, 0);
		background.setColor(Color.RED);
		GLabel label = new GLabel ("LOSER :(");
		label.setFont("Courier-60");
		label.setColor(Color.WHITE);
		add(label, (getWidth() - label.getWidth()) / 2, (getHeight() - label.getHeight()) / 2);
	}

	// prints blue screen with WINNER when the player wins
	private void winningScreen() {
		GRect background = new GRect(getWidth(), getHeight());
		background.setFilled(true);
		add(background, 0, 0);
		background.setColor(Color.BLUE);
		GLabel label = new GLabel ("WINNER =)");
		label.setFont("Courier-60");
		label.setColor(Color.WHITE);
		add(label, (getWidth() - label.getWidth()) / 2, (getHeight() - label.getHeight()) / 2);
	}

	// round ends when the ball passes the y coordinate of the paddle
	private boolean roundOver() {
		return (ball.getY() >= paddle.getY());
	}
	
	// game is over when the player has broken every brick
	private boolean gameOver() {
		return (colliderCount == NBRICK_ROWS * NBRICK_COLUMNS);	
	}
	
	// this checks whether the ball hits the paddle or a brick.
	// if it hits a brick, the ball reverses Y velocity, removes the brick, and adds one to the colliderCount.
	// if hits paddle, then ball just reverses Y velocity.
	private void checkForCollisions() {
		GObject collider = getCollidingObject();
		if(collider != null) {
			if(collider == paddle) {
				vy = -vy;
			}
			else {
				vy = -vy;
				if(collider != null) {
					remove(collider);
					colliderCount++;
				}
			}
		}
	}

	// returns the object that the ball is colliding with based on corner points
	private GObject getCollidingObject() {
		GObject collObj = getElementAt(ball.getX(), ball.getY());
		GObject collObj2 = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		GObject collObj3 = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		GObject collObj4 = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		if(collObj != null) {
			return collObj;
		}
		if(collObj2 != null) {
			return collObj2;
		}
		if(collObj3 != null) {
			return collObj3;
		}
		if(collObj4 != null) {
			return collObj4;
		}
		return null;
	}

	// keeps the ball bouncing between the walls; this is from class (BouncingBallSoln)
	private void moveBall() {
		if(hitLeftWall(ball) || hitRightWall(ball)) {
			vx = -vx;
		}
		if(hitTopWall(ball) || hitBottomWall(ball)) {
			vy = -vy;
		}
			ball.move(vx, vy);
	}

	// when the user clicks, then the ball starts in a random x velocity (between parameters) 
	// and in a given y velocity
	private void startBall() {
		waitForClick();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vy = -vy;
	}

	// returns true if the ball hits the bottom wall
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	// returns true if the ball hits the top wall
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}
	
	// returns true if the ball hits the right wall
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	// returns true if the ball hits the left wall
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	// creates a black ball and places in middle of the screen
	private void createBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		add(ball, x, y);
	}

	// sets window title, creates colored rows, adds paddle, adds ball
	private void setupGame() {
		setTitle("CS 106A Breakout");
		createRows();
		addPaddle();
	}
	
	// adds a black paddle to the screen based on a given offset from the bottom
	private void addPaddle() {	
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		double x = getWidth() / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);
	}

	public void mouseMoved(MouseEvent e) {
		double X = e.getX();
		double Y = getHeight() - PADDLE_Y_OFFSET;
		if(X <= getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(X, Y);
		}
	}
	
	// uses color and row number (starting from top) to create colored rows at the top of the screen
	// [Note: this seems repetitive, but don't know how to create a for loop with the given colors]
	private void createRows() {
		drawRow(Color.RED, 1);
		drawRow(Color.RED, 2);
		drawRow(Color.ORANGE, 3);
		drawRow(Color.ORANGE, 4);
		drawRow(Color.YELLOW, 5);
		drawRow(Color.YELLOW, 6);
		drawRow(Color.GREEN, 7);
		drawRow(Color.GREEN, 8);
		drawRow(Color.CYAN, 9);
		drawRow(Color.CYAN, 10);
	}

	// creates the method to draw a single row based on color and # from top
	private void drawRow(Color color, int heightDown) {
		double xCoor = (getWidth() - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP))/2;
		for (int i = 0; i < NBRICK_COLUMNS; i++) {
			GRect rect = new GRect(xCoor + i * (BRICK_WIDTH + BRICK_SEP), BRICK_Y_OFFSET + (BRICK_SEP + BRICK_HEIGHT) * (heightDown - 1), BRICK_WIDTH, BRICK_HEIGHT);
			rect.setFilled(true);
			rect.setColor(color);
			add(rect);	
		}
	}	
}

