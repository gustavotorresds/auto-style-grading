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
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpBricks();
		playGameAndCheckForWinOrLose();


	}
/*
 * This command sets up the bricks of the game with the correct
 * sequencing of colors for any number of bricks.
 * (sequence is red, orange, yellow, green, cyan)
 * Pre-Condition: The canvas is blank.
 * Post-Condition: The canvas has NROWS by NCOLUMNS of colored bricks.
 */
	private void setUpBricks() {
		Color brickColor = null;
		for (int i = 0; i < NBRICK_ROWS; i++) {
			int colorCounter = i + 1 -  10*((i)/10); // This integer takes advantage of integer division to accommodate for games with over 10 rows or columns of bricks
			if (colorCounter == 1) {
				brickColor = Color.RED;
			}else if (colorCounter == 3) {
				brickColor = Color.ORANGE;
			}else if (colorCounter == 5) {
				brickColor = Color.YELLOW;
			}else if (colorCounter == 7) {
				brickColor = Color.GREEN;
			}else if (colorCounter == 9) {
				brickColor = Color.CYAN;
			}
			for (int j = 0; j< NBRICK_COLUMNS; j++) {
				double brickX= (getWidth() - (BRICK_WIDTH + (BRICK_SEP) )*NBRICK_COLUMNS)/2 + j*(BRICK_SEP + BRICK_WIDTH);
				double brickY = BRICK_Y_OFFSET + i*(BRICK_HEIGHT + BRICK_SEP );
				GRect brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setFillColor(brickColor);
				add(brick);
			}
		}
	}
	/*
	 * This command executes the gameplay for breakout, keeps track of the
	 * number of lives remaining in a session, and lets the user know if 
	 * they have won or lost when appropriate.
	 * Pre-Condition: The canvas has only the multi-colored bricks set up.
	 * Post-Condition: The user has played through the BreakOut game, and
	 * if the user won, a "YOU WON" message is displayed; if the user lost,
	 * their remaining bricks and a "YOU LOST" message is displayed.
	 */
	private void playGameAndCheckForWinOrLose() {
		for (int i =0; i<NTURNS; i++) {
			setUpBall();
			setUpPaddle();
			startBallMovement();
			if (bounceBall() > 0) {
				remove(paddle);
				remove(ball);
				if (i == NTURNS -1) {
					GLabel lost = new GLabel("YOU LOST", getWidth()/2 , getHeight()/2);
					lost.move(-lost.getWidth()/2, -lost.getHeight()/2);
					add(lost);
				}
			}else {
				removeAll();
				i = NTURNS;
				GLabel won = new GLabel("YOU WON", getWidth()/2 , getHeight()/2);
				won.move(-won.getWidth()/2, -won.getHeight()/2);
				add(won);
			}
		}
		
	}

	GOval ball; // This is an instance variable of the ball so it can be used across functions.
	/*
	 * This command builds the ball used to play the BreakOutGame
	 * It is executed in playGameAndCheckForWinOrLose()
	 */
	private void setUpBall() {
		double ballX = getWidth()/2 - BALL_RADIUS;
		double ballY = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(ballX, ballY, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	GRect paddle;// this is an instance variable of the paddle so it can be used across functions.
	/*
	 * This command builds the paddle used to play the BreakOutGame and
	 * also it calls for the mouse listeners to be applied so the 
	 * user may move the paddle horizontally as they wish.
	 * It is executed in playGameAndCheckForWinOrLose()
	 */
	private void setUpPaddle() {
		double paddleX = getWidth()/2 - PADDLE_WIDTH/2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect( paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	/*
	 *This command constantly finds the location of the user's horizontal
	 *mouse location and moves the paddle to the same x-value within the bounds
	 *of the canvas.
	 *Pre-condition: The paddle is at some given horizontal location.
	 *Post-Condition: the paddle is at the same horizontal location as
	 *the user's mouse as long as it is within the canvas.
	 */
	public void mouseMoved( MouseEvent e) {
		if ((e.getX() < getWidth() - PADDLE_WIDTH/2) && (e.getX() > PADDLE_WIDTH/2)) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	private double vx, vy; // These are instance variables of the ball's component velocities for use across functions.

	private RandomGenerator rgen = RandomGenerator.getInstance();
	/*
	 * This function generates a random vx between the given min and max and sets vy
	 * as the given variable.  It then prompts the user to click the canvas to start
	 * the BreakOut game and waits for said input.
	 */
	private void startBallMovement() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		GLabel start = new GLabel("CLICK TO START", getWidth()/2 , getHeight()/2);
		start.move(-start.getWidth()/2, -start.getHeight()/2);
		add(start);
		waitForClick();
		remove(start);
	}
	/*
	 * This GObject tells other commands what object the ball is currently in contact with
	 * by checking the four corners of our ball and returning the first GObject it finds at
	 * that location.
	 */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(),ball.getY()) !=null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2*BALL_RADIUS,ball.getY()) !=null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(),ball.getY() + 2*BALL_RADIUS) !=null) {
			return getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2*BALL_RADIUS,ball.getY() + 2*BALL_RADIUS) !=null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		}
		return null;
	}
	
	/*
	 * This integer actually executes the main part of the gameplay by moving the ball,
	 * changing its direction of movement upon impact, and deleting any bricks it collides with.
	 * The reason it is an integer is because it also keeps track of the number of bricks remaining
	 * and returns that value after the user has let the ball pass to the bottom wall of the canvas.
	 * Pre-Condition: The ball is moving downward on the canvas with some vx and vy
	 * Post-Condition: 
	 * -If the user has destoryed all of the bricks, bounceBall() returns 0 to
	 * playGameAndCheckForWinOrLose() letting it know the user has won.
	 * -If we are on a turn < NTURNS and the user lost, the command returns the 
	 * number of bricks remaining to playGameAndCheckForWinOrLose() and the user plays the game again.
	 * -If we are on a turn = NTURNS and the user lost, the command returns the number of bricks remaining
	 * to playGameAndCheckForWinOrLose() letting it know the user has lost.
	 */
	private int bounceBall() {
		boolean hitBottom = (ball.getY() + 2*BALL_RADIUS) >= getHeight();
		int bricksRemaining = NBRICK_ROWS*NBRICK_COLUMNS;
		while (! hitBottom && bricksRemaining > 0) {
			boolean hitTop = (ball.getY() <= 0) ;
			boolean hitLeft = (ball.getX()  <= 0);
			boolean hitRight = (ball.getX() + 2*BALL_RADIUS) >= getWidth();
			ball.move(vx, vy);
			if (hitTop && vy < 0) {
				vy = -vy;
			}
			if ((hitLeft && vx < 0) || hitRight && vx > 0) {
				vx = -vx;	
			}
			if (getCollidingObject() == paddle && vy >= 0) {
				vy = -vy;
			} else if(getCollidingObject() != null && getCollidingObject() != paddle ) {
				vy = -vy;
				remove(getCollidingObject());
				bricksRemaining --;
			}
			hitBottom = (ball.getY() + 2*BALL_RADIUS) >= getHeight();
			pause(DELAY);
		}
		return bricksRemaining;
	}
	
	
	
	



}
