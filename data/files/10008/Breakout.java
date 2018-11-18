/*
 * File: Breakout.java
 * -------------------
 * Name: Giovanna Pinciroli
 * Section Leader: Julia Daniel
 * 
 * This file implements the game of Breakout without extensions. The objective of the game is to hit all bricks. 
 * The user has three turns. Once the user clicks to start the game, the ball starts moving. The user can make
 * the ball hit the bricks, while at the same time preventing the ball from going down the bottom of the screen
 * (and thus losing one turn) by moving the paddle. If the user successfully finishes the game, the ball disappears. 
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

	// Instance variables representing the paddle, the ball, the x and y velocities of the ball
	// and a variable accounting for the number of bricks remaining on the screen.
	private GRect paddle; 
	private GOval ball;
	private double vx, vy;
	private int countBricks = (NBRICK_ROWS * NBRICK_COLUMNS);

	// Instance variable representing a random generator for the horizontal velocity of the ball.
	private RandomGenerator rgen = RandomGenerator.getInstance();

	public void run() {
		initializeBreakout();
		playBreakout();
	}

	/*
	 * Method: Initialize the game of Breakout.
	 * Precondition: The canvas is empty.
	 * Postcondition: There are bricks, a paddle and a ball in the canvas, and the game is ready to be played.
	 */
	private void initializeBreakout() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createBricks();
		createPaddle(); 
		createBall();
	}

	/*
	 * Method: Play the game Breakout.
	 * Precondition: The game is setup.
	 * Postcondition: The game has three turns. 
	 * At the end of the three turns, the player loses if there still are bricks in the canvas.
	 * The player wins the game if all the bricks are hit before the game ends. 
	 */
	private void playBreakout() {
		int turns = 3;
		displayStart();
		while (turns > 0 && !checkWin()) {
			while (true) {
				moveAndBounceBall();
				checkCollisions();
				if (checkWin() == true) break;
				pause (DELAY);
				if (ball.getY() >= getHeight()) {
					remove(ball);
					if (checkWin() != true) {
						turns--;
					} else {
						turns = 0;
					}
					if (turns > 0 && countBricks != 0) nextTurn();
					if (turns == 0 && countBricks != 0) {
						displayLoss();
					}
				}
			}
		}
	}			

	/*
	 * Method: Create and color the bricks
	 * Precondition: The canvas is clear.
	 * Postcondition: The canvas has n columns and n rows of bricks - both n have a value of 10 but can be changed, 
	 * as they are constants at the top of the program. These bricks are in a colored pattern: the rows are divided in 
	 * equal proportions among the colors of red, orange, yellow, green and cyan, in that order top to bottom.
	 */
	private void createBricks() {
		double y = BRICK_Y_OFFSET;
		for (int i = 0; i < NBRICK_ROWS; i++) {
			double x = (CANVAS_WIDTH - (NBRICK_COLUMNS * (BRICK_WIDTH + BRICK_SEP) - BRICK_SEP)) / 2;
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (i < (NBRICK_ROWS / 5)) brick.setColor(Color.RED);
				else if (i < (NBRICK_ROWS * 2 / 5)) brick.setColor(Color.ORANGE);
				else if (i < (NBRICK_ROWS * 3 / 5)) brick.setColor(Color.YELLOW);
				else if (i < (NBRICK_ROWS * 4 / 5)) brick.setColor(Color.GREEN);
				else if (i < (NBRICK_ROWS)) brick.setColor(Color.CYAN);
				add(brick);
				x = x + BRICK_WIDTH + BRICK_SEP;	
			}
			y = y + BRICK_HEIGHT + BRICK_SEP;	
		}
	}

	/*
	 * Method: Create the paddle.
	 * Precondition: The canvas has colored bricks as described above.
	 * Postcondition: A rectangular black paddle has been added to the bottom of the screen.
	 */
	private void createPaddle() {
		paddle = new GRect (0, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	/*
	 * Method: Move the mouse in accordance with the paddle.
	 * Precondition: The paddle exists but does not move in accordance to the mouse movements.
	 * Postcondition: The paddle moves in accordance with the mouse, in a horizontal manner. 
	 */
	public void mouseMoved(MouseEvent e) {
		double x_paddle = 0; 
		if (e.getX() < (getWidth() - PADDLE_WIDTH)) {
			x_paddle = e.getX();
		} else {
			x_paddle = getWidth() - PADDLE_WIDTH;
		}
		paddle.setLocation(x_paddle, getHeight() - PADDLE_Y_OFFSET);
	}

	/*
	 * Method: Add a still, black ball to the center of the canvas and initialized vx and vy, the velocities of the ball. 
	 * Precondition: The canvas currently includes colored bricks and a paddle moving in accordance
	 * to the mouse movements. 
	 * Postcondition: A still, circular ball has been added to the center of the canvas; both vx and vy have been initialized.
	 */
	private void createBall() {
		ball = new GOval((getWidth() - 2 * BALL_RADIUS) / 2, (getHeight() - 2 * BALL_RADIUS) / 2, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = 3.0;
	} 

	/*
	 * Method: This method displays a message, indicating the user to click the mouse to start the game.
	 * Precondition: The game is initialized and ready to begin.
	 * Postcondition: As the mouse is clicked, the game can begin.
	 */
	private void displayStart() {
		GLabel label_Start = new GLabel("Click to play Breakout!");
		label_Start.setFont("Courier-20");
		label_Start.setColor(Color.BLACK);
		add(label_Start, (getWidth() - label_Start.getWidth())/ 2, (getHeight() - label_Start.getHeight())/ 2);
		waitForClick();
		remove(label_Start);
	}

	/*
	 * Method: Move the ball around the screen, with particular attention to both the horizontal 
	 * and the vertical speed of the ball.
	 * Precondition: The ball is still and is located at the center of the canvas.
	 * Postcondition: The ball has the possibility to move and bounce off the edges of the canvas.
	 * However, the ball will fall off the bottom edge of the canvas, for the game of Breakout to work.
	 */
	private void moveAndBounceBall() {
		ball.move(vx, vy);
		if (ball.getX() >= (getWidth() - 2 * BALL_RADIUS) || ball.getX() <= 0) vx = -vx;
		if (ball.getY() <= 0) vy = -vy;
	}

	/*
	 * Method: Check for collisions with bricks or with the paddle. If a collision with a brick occurs,
	 * remove the brick and bounce off the brick. If a collision with the paddle occurs, bounce off the paddle.
	 * Precondition: The ball bounces off the sides of the canvas.
	 * Postcondition: The ball also bounces off the paddle and bricks if those are hit.
	 */
	private void checkCollisions() {
		GObject collider = getCollidingObject();
		if (collider != null) {
			if (collider == paddle) {
				if (ball.getY() <= (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET) && vy >= 0) {
					vy = -Math.abs(vy);
				} 
			} else {
				vy = -vy;
				remove(collider);
				countBricks = countBricks - 1;
			}
		}
	}

	/*
	 * Method: This object identifies whether the ball overlaps with another object in the canvas.
	 * Precondition: The program does not identify the presence of a possible overlap between two objects.
	 * Postcondition: The program now returns elements when the ball collides with an object.
	 */
	private GObject getCollidingObject() {
		double x_min = ball.getX();
		double y_min = ball.getY();
		double x_max = ball.getX()  + 2 * BALL_RADIUS;
		double y_max = ball.getY() + 2 * BALL_RADIUS;
		if (getElementAt(x_min, y_min) != null) {
			return getElementAt(x_min , y_min);
		} else if (getElementAt(x_max, y_min) != null) {
			return getElementAt(x_max, y_min);
		} else if (getElementAt(x_min, y_max) != null) {
			return getElementAt(x_min, y_max);
		} else if (getElementAt(x_max, y_max) != null) {
			return getElementAt(x_max, y_max);
		} else return null;
	}

	/*
	 * Method: Running this method will check whether you have won the game or not, 
	 * by looking at whether there are still bricks in the game.
	 * Precondition: The program is running in a while loop.
	 * Postcondition: If no bricks are present, the ball is removed and a celebratory message is displayed.
	 */	
	private boolean checkWin() {
		if (countBricks == 0) {
			remove(ball);
			GLabel label_Win = new GLabel("Congratulations, you won!");
			label_Win.setFont("Courier-20");
			label_Win.setColor(Color.GREEN);
			add(label_Win, (getWidth() - label_Win.getWidth())/ 2, (getHeight() - label_Win.getHeight())/ 2);
			return true;
		}
		return false;
	}

	/*
	 * Method: This method is implemented if the user loses a turn, to transition from one turn to the next.
	 * Precondition: The user has lost one turn.
	 * Postcondition: the user is ready to play the following turn.
	 */
	private void nextTurn() { 
		GLabel label_nextTurn = new GLabel("Click to play the next turn.");
		label_nextTurn.setFont("Courier-20");
		label_nextTurn.setColor(Color.BLACK);
		add(label_nextTurn, (getWidth() - label_nextTurn.getWidth())/ 2, (getHeight() - label_nextTurn.getHeight())/ 2);
		waitForClick();
		remove(label_nextTurn);
		createBall();
	}

	/*
	 * Method: This method displays a message if the game is lost. 
	 * Precondition: The ball is removed as there are no turns remaining, but still some bricks on the canvas.
	 * Postcondition: A loss message is displayed in the canvas.
	 */	
	private void displayLoss() {
		GLabel label_Loss = new GLabel("Oh no, you lost :(");
		label_Loss.setFont("Courier-20");
		label_Loss.setColor(Color.RED);
		add(label_Loss, (getWidth() - label_Loss.getWidth())/ 2, (getHeight() - label_Loss.getHeight())/ 2);
	}
}









