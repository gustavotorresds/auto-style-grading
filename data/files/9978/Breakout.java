
/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This program extends GraphicsProgram and lets the user interactively play
 * the game brick breaker. The user clicks to start the game and moves the paddle
 * at the bottom of the srceen trying to bounce a ball to hit and remove the bricks.
 * the game ends when the user uses all three lives or gets rid of all the bricks. 
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

	// Number of turns
	public static final int NTURNS = 3;

	// these are instance variable used through out the program
	GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	GOval ball = null;
	int startingbricks = NBRICK_ROWS * NBRICK_COLUMNS;
	int nturns = NTURNS;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBricks();
		addMouseListeners();
		addPaddle();
		// allows the user to have three lives
		int turns = 0;
		while (turns < NTURNS && (startingbricks != 0)) {
			addBall();
			moveBall();
			turns++;
		}
		// condition for when the user collides and removes all the bricks with
		// their three lives
		if (startingbricks == 0) {
			remove(ball);
		}
	}
	// condition for when the user has yet to collide and remove all the
	// bricks before their three lives are gone

	// places all the bricks on the screen with vertical rainbow
	private void setBricks() {
		// initial y coordinate value
		double yCoordinate = BRICK_Y_OFFSET;
		for (int row = 0; row < NBRICK_ROWS; row++) {
			// initial x coordinate value
			double xCoordinate = ((CANVAS_WIDTH - (BRICK_WIDTH * 10 + BRICK_SEP * 9)) / 2);
			for (int col = 0; col < NBRICK_COLUMNS; col++) {
				GRect bricks = new GRect(xCoordinate, yCoordinate, BRICK_WIDTH, BRICK_HEIGHT);
				bricks.setFilled(true);
				add(bricks);
				if (row == 0 || row == 1) {
					bricks.setColor(Color.RED);
				} else if (row == 2 || row == 3) {
					bricks.setColor(Color.ORANGE);
				} else if (row == 4 || row == 5) {
					bricks.setColor(Color.YELLOW);
				} else if (row == 6 || row == 7) {
					bricks.setColor(Color.GREEN);
				} else if (row == 8 || row == 9) {
					bricks.setColor(Color.CYAN);
				}
				// adjusting the x coordinate
				xCoordinate += BRICK_WIDTH + BRICK_SEP;
			}
			// adjusting the y coordinate
			yCoordinate += BRICK_HEIGHT + BRICK_SEP;
		}
	}

	// adds the paddle to the center of the screen elevated a little above the
	// bottom of the canvas
	private void addPaddle() {
		double x = (getWidth() / 2) - (PADDLE_WIDTH / 2);
		double y = (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, x, y);
	}

	// allows the paddle to move and sets the boundaries for the paddle on the
	// canvas
	public void mouseMoved(MouseEvent e) {
		if (e.getX() >= PADDLE_WIDTH && e.getX() <= (getWidth())) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH, (getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET));
		}
	}

	// adds ball to the screen
	private void addBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		double x = getWidth() / 2 - BALL_RADIUS;
		double y = getHeight() / 2 - BALL_RADIUS;
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, x, y);
	}

	double vx;
	double vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	// allows the ball to move horizontally within the boundaries of the canvas
	private void moveBall() {
		waitForClick();
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		vy = 4.0;
		while (true) {
			ball.move(vx, vy);
			checkForCollisions(ball.getX(), ball.getY());
			if (startingbricks == 0) {
				break;
			}
			// check for the left wall
			if (ball.getX() < 0) {
				vx = -vx;
			}
			// check for the right wall
			if (ball.getX() >= getWidth() - BALL_RADIUS * 2) {
				vx = -vx;
			}
			// check for the top wall
			if (ball.getY() <= 0) {
				vy = -vy;
			}
			// breaks when the ball goes past the bottom wall
			if (ball.getY() >= getHeight()) {
				vy = 0;
				vx = 0;
				remove(ball);
				break;
			}
			pause(DELAY);
		}
	}


	// this checks for the collisions with the paddle and the bricks. Inside the
	// method it checks to four corners of the square boundary of the ball.
	private void checkForCollisions(double x, double y) {
		GObject object = getElementAt(x, y);
		GObject object2 = getElementAt(x + BALL_RADIUS * 2, y);
		GObject object3 = getElementAt(x, BALL_RADIUS * 2 + y);
		GObject object4 = getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2);
		if (object == paddle) {
			vy = -Math.abs(vy);
		} else if (object2 == paddle) {
			vy = -Math.abs(vy);
		} else if (object3 == paddle) {
			vy = -Math.abs(vy);
		} else if (object4 == paddle) {
			vy = -Math.abs(vy);
		} else if (object != null) {
			remove(object);
			changeDirection();
			startingbricks--;
		} else if (object2 != null) {
			remove(object2);
			changeDirection();
			startingbricks--;
		} else if (object3 != null) {
			remove(object3);
			changeDirection();
			startingbricks--;
		} else if (object4 != null) {
			remove(object4);
			changeDirection();
			startingbricks--;
		}
	}

	// when the ball collides with the bricks the ball changes direction and makes
	// a short bounce sound
	private void changeDirection() {
		vy = -vy;
	}
}