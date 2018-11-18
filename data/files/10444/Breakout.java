/*
 * File: Breakout.java
 * -------------------
 * Name: Chuyi Yang
 * Section Leader: Andrew Davis
 * 
 * This file contains the game Breakout. The user controls the paddle by moving the mouse left
 * or right. The goal of the game is to deflect the boucing ball from the paddle into the row of bricks.
 * You win the game once you destroy all the bricks. You will lose if you fail to do so before you 
 * lose all of your lives.
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

	private double vx, vy;

	private GOval ball;

	private int brickCounter = NBRICK_COLUMNS * NBRICK_ROWS;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		addMouseListeners();
		buildAllRows();
		for (int i = 0; i < NTURNS; i++) {
			createBall();
			playGame();
		}
		if (brickCounter > 0) {
			loserScreen();
		}
	}


	/*Method: buildAllRows
	 * ---------------------
	 * This method contains a for loop within a for loop that creates all of the bricks for the game
	 * with dimensions NBRICKS_COLUMNS by numberOfRowsBuilt. The if/else statements are with respect
	 * to the numberOfRowsBuilt so that the rows get colored, not the columns.
	 */

	private void buildAllRows() {
		for (int numberOfRowsBuilt = 0; numberOfRowsBuilt < NBRICK_COLUMNS; numberOfRowsBuilt++) {
			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				double x = (BRICK_SEP * 1.5) + (BRICK_WIDTH * i) + (BRICK_SEP * i);
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT * numberOfRowsBuilt) + (BRICK_SEP * numberOfRowsBuilt);
				GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				x = (BRICK_SEP * 1.5) + (BRICK_WIDTH * i) + (BRICK_SEP * i);
				y = BRICK_Y_OFFSET + (BRICK_HEIGHT * numberOfRowsBuilt) + (BRICK_SEP * numberOfRowsBuilt);
				add(brick);
				if (numberOfRowsBuilt < 2) {
					brick.setFilled(true);
					brick.setColor(Color.RED);
				} else if (numberOfRowsBuilt >= 2 && numberOfRowsBuilt < 4){
					brick.setFilled(true);
					brick.setColor(Color.ORANGE);
				}
				if (numberOfRowsBuilt >=4 && numberOfRowsBuilt < 6) {
					brick.setFilled(true);
					brick.setColor(Color.YELLOW);
				} else if (numberOfRowsBuilt >= 6 && numberOfRowsBuilt < 8) {
					brick.setFilled(true);
					brick.setColor(Color.GREEN);
				}
				if (numberOfRowsBuilt >= 8 && numberOfRowsBuilt < 10) {
					brick.setFilled(true);
					brick.setColor(Color.CYAN);
				}
			}
		}
	}


	/*private GRect paddle
	 * ---------------------
	 * Creates an instance "rectangle" that has dimensions PADDLE_WIDTH and PADDLE_HEIGHT.
	 * This rectangle is fed x and y coordinates from our mouse location. The paddle is black.
	 */
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT); {
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
	}


	/*Method: createBall
	 * ------------------
	 * This method creates a black game ball that starts in the center of the screen.
	 */

	private void createBall() {
		vx = getWidth()/2 - BALL_RADIUS - BRICK_SEP/2;
		vy = getHeight()/2;
		ball = new GOval (vx, vy, BALL_RADIUS * 2, BALL_RADIUS * 2);
		add(ball);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
	}



	/*Method: ballVelocity
	 * --------------------
	 * This method calculates the ball's velocity and randomly generates a 
	 * velocity value between two numbers.
	 */
	private RandomGenerator rgen = RandomGenerator.getInstance();

	private void ballVelocity() {
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		vy = 3.0;
	}


	/*Method: playGame
	 * --------------------
	 * This method begins the actual game of Breakout. Players should click the mouse
	 * once to activate the ball and begin the game.
	 */
	private void playGame() {
		waitForClick();
		ballVelocity();
		while (true) {
			ball.move(vx, vy);
			pause(DELAY);
			if (ball.getX() > getWidth() - BALL_RADIUS * 2 || ball.getX() < 0) {
				vx = -vx;
			}
			if (ball.getY() < 0) {
				vy = -vy;
			}
			if (ball.getY() > getHeight()) {
				break;
			}
			if (brickCounter == 0) {
				winnerScreen();
				ball.setVisible(false);
				break;
			}
			checkingForCollisions();
		}
	}




	/* private GObject getCollidingObject
	 * ----------------------------------
	 * Checks the corners of the ball for surrounding GObjects in which the ball will have to react to
	 * via bouncing once it comes in contact with it.
	 */

	private GObject getCollidingObject() {
		if ((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS * 2)) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		}else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		} else {
			return null;
		}
	}

	/*Method: checkingForCollisions
	 * -----------------------------
	 * This method checks what kind of object the ball collides with. It checks it the ball
	 * is coming in contact with the paddle or a brick, and follows the following velocity changes
	 * accordingly.
	 */
	private void checkingForCollisions() {
		GObject collider = getCollidingObject(); {
			if (collider == paddle) {
				vy = -Math.abs(vy);
			}
			if (collider != paddle && collider != null) {
				remove(collider);
				brickCounter--;
				vy = -vy;
			}
		}
	}

	/*Method: loserScreen
	 * ------------------
	 * Prints the label "You Lose!" when you lose all of your lives.
	 */

	private void loserScreen() {
		GLabel msg = new GLabel ("You Lose!");
		msg.setFont("Helvetica-24");
		msg.setColor(Color.BLACK);
		add(msg, getWidth()/2 - 0.5 * msg.getWidth(), getHeight()/2);
	}

	/*Method: winnerScreen
	 * --------------------
	 * Prints the label "You Win!" when you hit all of the blocks with the ball.
	 */

	private void winnerScreen() {
		GLabel msg = new GLabel ("You Win!");
		msg.setFont("Helvetica-24");
		msg.setColor(Color.BLACK);
		add(msg, getWidth()/2 - 0.5 * msg.getWidth(), getHeight()/2);
	}

	/*Public Void mouseMoved
	 * ----------------------
	 * This method allows us to pass in x and y values for our instance rectangle. In combination
	 * we create the paddle for the game.
	 */
	public void mouseMoved(MouseEvent z) {
		double mouseX = z.getX() - PADDLE_WIDTH/2;
		double mouseY = getHeight() - PADDLE_Y_OFFSET;
		if ((mouseX > 0) && (mouseX < getWidth() - PADDLE_WIDTH)) {
			add(paddle, mouseX, mouseY);
			paddle.setLocation(mouseX, mouseY);
		}
	}
}

