/*
 * File: Breakout.java  
 * -------------------
 * Name: Ildemaro Gonzalez
 * Section Leader: Rachel Gardner
 * 
 * This file will implements the game Breakout. In this game the player
 * controls a paddle to bounce a ball at a set of bricks in order to 
 * destroy the bricks. Additionally the player must prevent the ball from
 * going past their paddle and hitting the bottom wall.
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
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 500.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// Added instance variables

	// Makes paddle visible across methods
	private GRect paddle = null;

	// Makes ball visible across methods
	private GOval ball = null;

	// Makes ball x and y velocity visible across methods
	private double vx, vy;

	// Makes random generator visible across methods
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		// Set the window's title bar text
		setTitle("Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();

		setup();
		play();
	}

	/**
	 * Method: Set Up
	 * ____________
	 * Sets up the screen for play. Creates all the bricks and the paddle.
	 */
	private void setup() {
		setBricks();
		setPaddle();
	}

	/**
	 * Method: Set Bricks
	 * _______________
	 * Creates layers of bricks on screen according to constants set for number of rows
	 * and columns of bricks. Bricks are colored in a pattern up to the tenth row; rows
	 * after that are colored randomly. 
	 */
	private void setBricks() {

		// variable establishes left edge of first column of bricks
		double firstColumnXValue = getWidth()/2.0 - 5 * BRICK_WIDTH - 4.5 * BRICK_SEP;

		// Variable establishes y distance separating rows
		double brickYSeparation = BRICK_SEP + BRICK_HEIGHT;

		// Loop creates number of rows according to constant
		for(int j = 0; j < NBRICK_ROWS; j++) {
			Color color = null;

			// Sets color pattern for rows
			if(j < 2) {
				color = Color.RED;
			} else if(j >= 2 && j < 4) {
				color = Color.ORANGE;
			} else if(j >= 4 && j < 6) {
				color = Color.YELLOW;
			} else if(j >= 6 && j < 8) {
				color = Color.GREEN;
			} else if(j >= 8 && j < 10) {
				color = Color.CYAN;

				// Beyond row ten, row color is randomly generated
			} else if(j >= 10) {
				color = rgen.nextColor();
			}
			addRow(firstColumnXValue, BRICK_Y_OFFSET + brickYSeparation * j, color);
		}
	}

	/**
	 * Creates row of bricks, taking in parameters of initial x and y coordinates,
	 * and brick color. Number of bricks in row is equal to constant for number of 
	 * columns.
	 */
	private void addRow(double x, double y, Color color) {
		for(int i = 0; i < NBRICK_COLUMNS; i++) {
			GRect brick = new GRect(x + i * BRICK_WIDTH + i * BRICK_SEP , y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(color);
			add(brick);
		}
	}

	/**
	 * Creates paddle and centers it near bottom of screen.
	 */
	private void setPaddle() {
		double paddleCenterX = getWidth()/2.0 -PADDLE_WIDTH/2.0;
		paddle = new GRect(paddleCenterX, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	/**
	 * Calls mouse location to make the paddle track the mouse.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();

		// Paddle will only move in the x direction and only within the bounds of the canvas
		if(mouseX < getWidth() - PADDLE_WIDTH/2.0 && mouseX > PADDLE_WIDTH/2.0) {
			paddle.setCenterX(mouseX);
		}
	}

	/**
	 * Method: Play
	 * -----------
	 * This method creates a ball and allows the player to click to launch the game
	 * with all its rules and conditions.
	 * 
	 * Pre: The bricks and paddle in the world are set up, but there is no ball and 
	 * the game has not started.
	 * Post: The ball is created, the game can be played, resulting in a win or loss. 
	 */
	private void play() {
		createBall();

		// Waits for player to click before launching the ball and starting the game
		waitForClick();
		playBall();
	}

	/**
	 * Creates ball used to play the game and places it at center of screen.
	 */
	private void createBall() {
		double centerX = getWidth()/2.0;
		double centerY = getHeight()/2.0;

		ball = new GOval (centerX - BALL_RADIUS, centerY - BALL_RADIUS, 2.0 * BALL_RADIUS, 2.0 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball);
	}

	/**
	 * Method: Play Ball
	 * -----------
	 * Launches the ball towards the bottom wall at a random angle and begins the 
	 * game while implementing its rules and conditions. 
	 * 
	 * Rules: ball bounces off walls, the paddle, and bricks; the ball deletes any bricks it 
	 * hits; if the ball crosses the bottom wall, the player loses a life and the ball is reset;
	 * if the player loses all their lives, the game ends, displaying a loss message; if the player 
	 * deletes all the bricks, the player wins, displaying a victory message.
	 */
	private void playBall() {

		// Keeps track of remaining bricks on screen
		int totalBricks = NBRICK_ROWS * NBRICK_COLUMNS;

		// Keeps track of lives left
		int lives = NTURNS;

		// Launches ball towards bottom wall with y velocity
		vy = VELOCITY_Y;

		// Gives ball random x velocity, launching it at random angle
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);

		// Ensures x velocity is negative half the time
		if (rgen.nextBoolean(0.5)) vx = -vx;

		while(ball != null) {

			// Ball continues to move after it's launched
			ball.move(vx, vy);
			pause(DELAY);

			// Ball bounces with correct trajectory when it hits side walls
			if(hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
			}

			// Ball bounces with correct trajectory when it hits top wall
			if(hitTopWall(ball)) {
				vy = -vy;
			}

			// Checks if ball has collided with any object and declares it as "collider"
			GObject collider = checkForCollision();

			// If ball collides with paddle, ball bounces up (preventing sticky paddle bug)
			if(collider == paddle) {
				vy = Math.abs(vy) * -1;

				// If the ball hits a brick, it bounces off and deletes the brick
			} else if(collider != null) {
				vy = -vy;
				remove(collider);

				// Keeps track of number of bricks remaining 
				totalBricks = totalBricks - 1;
			}

			// The following occurs if player wins the game
			if(playerWins(totalBricks)) {

				// Ball is removed from screen
				remove(ball);

				// Message informs player they have won
				winMessage();

				// Game stops playing
				break;
			}

			// Player loses lives if ball hits bottom wall
			if(hitBottomWall(ball)) {

				// Ball is removed after hitting bottom wall
				remove(ball);

				// Player loses a life
				lives = lives - 1;
				ball = null;

				// Creates new ball if player has lives remaining
				if(lives > 0) {
					createBall();

					// Upon click, ball launches with new random x velocity, resuming play
					waitForClick();
					vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX); 
					if (rgen.nextBoolean(0.5)) vx = -vx;

					// If player has no lives remaining, they lose
				} else {

					// Message informs player they have won
					loseMessage();

					// Game stops playing
					break;
				}
			}
		}
	}

	/**
	 * Method: Check For Collision
	 * __________________
	 * Checks if ball has collided with any object. This method looks for 
	 * objects at the four corners of the square that inscribes the ball
	 * and returns whatever object exists there. 
	 */
	private GObject checkForCollision() {
		GObject object = getElementAt(ball.getX(), ball.getY());
		if(object != null) {
			return object;
		} else { 
			object = getElementAt(ball.getRightX(), ball.getY());
			if(object != null) {
				return object;
			} else {
				object = getElementAt(ball.getRightX(), ball.getBottomY());
				if(object != null) {
					return object;
				} else {
					object = getElementAt(ball.getX(), ball.getBottomY());
					if(object != null) {
						return object;
					} else {
						return null;
					}
				}
			}
		}
	}

	/**
	 * Player wins the game if they have successfully deleted all the bricks on the screen. 
	 */
	private boolean playerWins(int totalBricks) {
		return totalBricks == 0;
	}

	/**
	 * Displays message informing player they won the game. 
	 */
	private void winMessage() {
		GLabel label = new GLabel("You Win! :O");
		label.setFont("SansSerif-40");
		label.setColor(Color.MAGENTA);
		label.setLocation(getWidth()/2.0 - label.getWidth()/2.0, getHeight()/2.0 - label.getHeight()/2.0);
		add(label);
	}

	/**
	 * Displays message informing player they lost the game. 
	 */
	private void loseMessage() {
		GLabel label = new GLabel("You Lose :c");
		label.setFont("SansSerif-40");
		label.setColor(Color.GRAY);
		label.setLocation(getWidth()/2.0 - label.getWidth()/2.0, getHeight()/2.0 - label.getHeight()/2.0);
		add(label);
	}

	/**
	 * (Hit Right Wall) condition is met when right edge of ball reaches right wall of the canvas
	 */
	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - ball.getWidth();
	}

	/**
	 * (Hit Left Wall) condition is met when left edge of ball reaches left wall of the canvas
	 */
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() < 0;
	}

	/**
	 * (Hit Top Wall) condition is met when top edge of ball reaches top wall of the canvas
	 */
	private boolean hitTopWall(GOval ball) {
		return ball.getY() < 0;
	}

	/**
	 * (Hit Bottom Wall) condition is met when bottom edge of ball reaches bottom wall of the canvas
	 */
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}
}
