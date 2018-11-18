
/*
 * File: Breakout.java
 * -------------------
 * Name: Laainam (Best) Chaipornkaew
 * Section Leader: Tessera Chin
 * 
 * This file implements the game of Breakout.
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

	public static final double NOTIFICATION_WIDTH = 200;
	public static final double NOTIFICATION_HEIGHT = 60;

	// Number of turns
	public static final int NTURNS = 3;

	/* Private instance variable */
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private int brickGone = 0;
	private int turns = NTURNS;

	public void run() {
		setup();
		// need to check for
		while (!gameOver() && !brickAllGone()) {
			addBall();
			waitForClick();
			vy = VELOCITY_Y;
			// vx is created with random generator to have value between 1 and 3
			// with 50% chance to be positive and 50% chance negative
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.2))
				vx = -vx;
			// animation loop continue until ball hit bottom wall or all brick
			// are gone
			while (!hitBottomWall(ball) && !brickAllGone()) {
				double x = ball.getX();
				double y = ball.getY();
				collidingActionReaction(x, y);
				// check if wall hit side walls or top walls and adjust velocity
				if (hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				}
				if (hitTopWall(ball)) {
					vy = -vy;
				}
				// update velocity of the ball in each animation loop
				ball.move(vx, vy);
				pause(DELAY);
			}
			// keep track of turns after each turn end
			// with ball hitting the bottom wall
			turns = turns - 1;
			remove(ball);
		}
		if (brickAllGone()) {
			conGratulationYouWin();
		} else {
			sorryNotThisTime();
		}
	}

	private void setup() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		Object color;
		// makeBrickRow(Color.RED);
		displayBrick();
		paddle = makePaddle();
		addPaddle();
		ball = makeBall();
		// addMouseListeners();
	}

	public void mouseMoved(MouseEvent e) {
		double paddleX = e.getX() - PADDLE_WIDTH / 2;
		double paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		if (paddleX > 0 && paddleX < getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(paddleX, paddleY);
		}
	}

	private GRect makePaddle() {
		GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		return paddle;
	}

	private void addPaddle() {
		double paddleInY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = paddleInY;
		add(paddle, x, y);
	}

	private GOval makeBall() {
		GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		return ball;
	}

	private void addBall() {
		ball.setLocation(getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
		add(ball);
	}
	
	//create the brick display setup
	private void displayBrick() {
		double centerX = getWidth() / 2;
		for (double row = 0; row < NBRICK_ROWS; row++) {
			for (double col = 0; col < (NBRICK_COLUMNS); col++) {
				double x = col * BRICK_WIDTH;
				double y = row * BRICK_HEIGHT;
				GRect rect = new GRect(
						centerX - (NBRICK_COLUMNS * BRICK_WIDTH / 2) - (NBRICK_COLUMNS + 1) * BRICK_SEP / 2 + x
								+ BRICK_SEP * (col + 1),
						BRICK_Y_OFFSET + y + BRICK_SEP * row, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				fillBrickColor(row, rect);
				add(rect);
			}
		}
	}
	//create the rainbow color scheme, changing 
	//the shade every two rows. 
	private void fillBrickColor(double row, GRect rect) {
		rect.setFilled(true);

		if (row % 10 == 0 || row % 10 == 1) {
			rect.setColor(Color.RED);
		}
		if (row % 10 == 2 || row % 10 == 3) {
			rect.setColor(Color.ORANGE);
		}
		if (row % 10 == 4 || row % 10 == 5) {
			rect.setColor(Color.YELLOW);
		}
		if (row % 10 == 6 || row % 10 == 7) {
			rect.setColor(Color.GREEN);
		}
		if (row % 10 == 8 || row % 10 == 9) {
			rect.setColor(Color.CYAN);
		}
	}

	private RandomGenerator rgen = RandomGenerator.getInstance();

	// determines if game is over -- true if turns is reduced to zero
	private boolean gameOver() {
		return (turns == 0);
	}

	// determines if bricks are all gone
	// true if #brick gone = total #bricks
	private boolean brickAllGone() {
		return (brickGone == NBRICK_COLUMNS * NBRICK_ROWS);
	}

	//Returns whether or not the given ball hit the bottom wall
	//of the window (true if position reach the bottom wall)
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}

	//Returns whether or not the given ball hit the top wall
	//of the window (true if position reach the top wall)
	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	//Returns whether or not the given ball hit the right wall
	//of the window (true if position reach the right wall)
	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	//Returns whether or not the given ball hit the left wall
	//of the window (true if position reach the left wall)
	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	
	//this method is different than what's recommended but works to test
	//for colliding object and create consequence including velocity change
	//and remove brick that is being hit. 
	private void collidingActionReaction(double x, double y) {
		GObject collider = getElementAt(x, y);
		if (collider == null) {
			collider = getElementAt(x + 2 * BALL_RADIUS, y);
			if (collider == null) {
				collider = getElementAt(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS);
				if (collider == null) {
					collider = getElementAt(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS);
					if (collider == null) {
						collider = getElementAt(x, y + 2 * BALL_RADIUS);
					}
				}
			}
		}

		if (collider != null) {
			if (collider != paddle) {
				//condition of collider not being paddle, it must be brick, so have to be removed
				remove(collider);
				vy = -vy;
				
				int totalBrick = NBRICK_COLUMNS * NBRICK_ROWS;
				//keep track of number of brick being destroyed 
				brickGone = brickGone + 1;
				

			} else {
				//the else condition take care of collider being paddle
				//and sometime the paddle has dimension that make velocity
				//going up and down when detecting ball stuck in paddle
				//absolute value help redirecting ball into one preferred direction 
				vy = -Math.abs(vy);
			}
		}

	}
	//method create the box with text that give feedback to player
	//it is centered and place roughly the middle of the screen
	private void makeFeedbackBox(String feedback, Color color) {
		GRect rect = new GRect(getWidth() / 2 - NOTIFICATION_WIDTH / 2,
				getHeight()/2, NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
		rect.setColor(color);
		rect.setFilled(true);
		add(rect);
		GLabel label = new GLabel(feedback);
		double x = (rect.getCenterX() - label.getWidth() / 2);
		double y = (rect.getCenterY() + label.getAscent() / 2);
		add(label, x, y);
	}

	private void conGratulationYouWin() {
		makeFeedbackBox("CONGRATULATIONS YOU WIN", Color.GREEN);
	}

	private void sorryNotThisTime() {
		makeFeedbackBox("SORRY, BUT NICE TRY! ", Color.RED);
	}

}