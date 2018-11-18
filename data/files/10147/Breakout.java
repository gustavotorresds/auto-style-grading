/*
 * File: Breakout.java
 * -------------------
 * Name: Cat Davis
 * SUnet ID: catdavis
 * Section Leader: Ben Barnett
 * -------------------
 * The Breakout class implements the game of 
 * Breakout.
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

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that are chosen (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Velocity components
	private double vx, vy;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Animation delay or pause time when ball is reset and new turn initiates
	public static final double BALL_RESET = 45.0;

	// Number of turns 
	private int NTURNS = 3;

	// Random-number generator
	private RandomGenerator rgen = RandomGenerator.getInstance();

	//Determines if a collision has occurred
	private boolean isCollision = false;

	// Keeps count of the bricks
	private int brickCount = (NBRICK_COLUMNS * NBRICK_ROWS);

	//Determines state of game
	private boolean gameOver = false;
	private boolean playerLost = false;

	// Creates instance variables of the paddle and ball
	public static GRect paddle = null;
	public static GOval ball = null;

	public void run() {
		setTitle("CS 106A Breakout");	// Sets the window's title bar text
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT); 	// Sets the canvas size 
		addMouseListeners();
		setUp();
		playGame();
	}

	/*
	 * Creates the contents of the Breakout game: the colored bricks, a black 
	 * paddle, and a ball.
	 */
	private void setUp () {
		createBricks();
		addPaddle();
		createBall();
	}

	/* 
	 * Allows the user to play the Breakout game and determines if the
	 * user wins or loses the game.
	 */
	private void playGame () {
		while (gameOver == false) {
			moveBall();
		} 
		if (gameOver == true) {
			if (playerLost == true) {
				giveResult ("GAME OVER. YOU LOST!", Color.RED);
			} else if (playerLost == false) {
				giveResult ("GAME OVER. YOU WIN!", Color.GREEN);
			}
		}
	}

	// Creates the rows of colored bricks.
	private void createBricks () {
		for (int j = 0; j <= NBRICK_ROWS; j++) { 			//creates multiple rows of bricks
			for (int i = 0; i < NBRICK_COLUMNS; i++) { 		// creates one row of bricks
				double x = getWidth() /2 - (NBRICK_COLUMNS * BRICK_WIDTH + (NBRICK_COLUMNS - 1) * BRICK_SEP) / 2;
				double y = BRICK_Y_OFFSET + BRICK_HEIGHT * j + ((BRICK_SEP) * j);
				GRect brick = new GRect (x + (BRICK_WIDTH + BRICK_SEP) * i, y , BRICK_WIDTH, BRICK_HEIGHT);
				if (j == 1 || j == 2 || j % 10 == 1 || j % 10 == 2) { 						//determines the color of each pair of rows
					brick.setColor(Color.RED);
					brick.setFilled(true);
					add(brick);
				}
				if (j == 3 || j == 4 || j % 10 == 3 || j % 10 == 4) {
					brick.setColor(Color.ORANGE);
					brick.setFilled(true);
					add(brick);
				}
				if (j == 5 || j == 6 || j % 10 == 5 || j % 10 == 6) {
					brick.setColor(Color.YELLOW);
					brick.setFilled(true);
					add(brick);
				}
				if (j == 7 || j == 8 || j % 10 == 7 || j % 10 == 8) {
					brick.setColor(Color.GREEN);
					brick.setFilled(true);
					add(brick);
				}
				if (j == 9 || j == 10 || j % 10 == 9) {
					brick.setColor(Color.CYAN);
					brick.setFilled(true);
					add(brick);
				}
				if (j >10) {								// ensures that larger numbered rows are cyan without changing color of initial row
					if (j % 10 == 0) {
						brick.setColor(Color.CYAN);
						brick.setFilled(true);
						add(brick);
					}
				}
			}
		}
	}

	// Creates the paddle and colors it black
	private void addPaddle() {
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	// Makes the paddle follow the movement of the user's mouse
	public void mouseMoved (MouseEvent e) {
		double mouseX = e.getX() - PADDLE_WIDTH / 2 ;		// ensures mouse follows center of paddle
		double y = getHeight() - PADDLE_Y_OFFSET;		
		if (mouseX > 0 && mouseX < getWidth() - PADDLE_WIDTH) {		//ensures paddle does not go beyond screen dimensions
			paddle.setLocation (mouseX, y);
		}	
	}

	// Creates the ball and colors it black
	private void createBall () {
		double x = (getWidth() - BALL_RADIUS * 2) / 2;
		double y = (getHeight() - BALL_RADIUS * 2) / 2;
		ball = new GOval (x, y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	/*Animates the ball, makes the ball bounce off of walls, and ensures
	 * a turn or the game is terminated when the ball hits the bottom wall or 
	 * all bricks are removed.
	 */
	private void moveBall () {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble (VELOCITY_X_MIN, VELOCITY_X_MAX); // randomizes the value of vx
		if (rgen.nextBoolean(0.5)) {  // makes the vx value negative half of the time
			vx = -vx;
		} 
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);
			double y = ball.getY();
			double x = ball.getX();
			if (x >= getWidth() - BALL_RADIUS * 2 ||x <= 0 + BALL_RADIUS * 2 ) {		// ensures ball bounces off of left and right walls
				vx = -vx;
			}
			if (y <= 0 + BALL_RADIUS * 2) {			// ensures ball bounces off of top wall
				vy = -vy;
			}	
			if (y >= getHeight() - BALL_RADIUS * 2) {	// ensures game or turn ends when ball hits bottom wall
				if (NTURNS > 1) {				// if turns are left, resets the ball
					NTURNS = NTURNS - 1;		// calculates and tracks number of turns left
					gameOver = false;
					ball.setLocation ((getWidth() - BALL_RADIUS * 2) / 2, (getHeight() - BALL_RADIUS * 2) / 2);
					pause(DELAY * BALL_RESET);		// pauses game when ball is reset
					break;
				} else {
					gameOver = true;
					playerLost = true;
					break;
				}
			}
			checkForCollisions(ball.getX(), ball.getY());		//checks for collisions at all end points of ball (surrounding rectangle)
			if (isCollision == false) {
				checkForCollisions(ball.getX() + (BALL_RADIUS *2), ball.getY());
				if (isCollision == false) {
					checkForCollisions(ball.getX(), ball.getY() + (BALL_RADIUS *2));
					if (isCollision == false) {
						checkForCollisions(ball.getX() + (BALL_RADIUS *2), ball.getY() + (BALL_RADIUS *2));	
					}
				}
			}
			if (brickCount == 0) {		//determines if user wins game
				gameOver = true;
				playerLost = false;
				break;
			}
		}
	}

	/*
	 * Checks for collision between the ball and an object.  If the object
	 * is a brick, brick is removed and the ball bounces reverses vertical 
	 * direction.  If object is the top of the paddle, ball bounces in the
	 * upward direction off of the paddle.
	 */
	private void checkForCollisions(double x, double y) {
		isCollision = false;		// resets state of collision determinant
		GObject possibleCollider = getElementAt(x, y);
		if (possibleCollider != null) {
			isCollision = true;
			if (possibleCollider != paddle) {
				remove(possibleCollider);
				brickCount = brickCount - 1;		//tracks number of bricks left
				vy = -vy;
			} else if (possibleCollider == paddle) {
				if (vy > 0) {		// ensures ball only bounces off of top of paddle
					vy = -vy;
				}
			} else {
				isCollision = false;
			}
		} 
	}

	// Tells user if game is won or lost and adds game result label to screen
	private void giveResult (String labelText, Color color) {
		GLabel label = new GLabel (labelText);
		label.setFont("Courier-32");
		label.setColor(color);
		label.setLocation((getWidth() - label.getWidth()) / 2, (getHeight() - label.getAscent()) / 2);
		add(label);
	}
}