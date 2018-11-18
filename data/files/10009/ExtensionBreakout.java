
/*
 * File: BreakoutExtension.java
 * -------------------
 * Name: Minha Kim
 * Section Leader: Robbie Jones
 * 
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class ExtensionBreakout extends GraphicsProgram {
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

	// Number of lives
	public static final int NTURNS = 3;

	// paddle variable
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	// ball variable
	private GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);

	// number of bricks (player wins when no bricks remain
	int BRICK_NUMBER = NBRICK_COLUMNS * NBRICK_ROWS;

	// number of chances
	int CHANCES = NTURNS;

	private RandomGenerator rgen = RandomGenerator.getInstance();
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		setUpGame();
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		double vy = VELOCITY_Y;

		addMouseListeners();
		waitForClick();

		while (BRICK_NUMBER > 0) {

			if (hitLeftWall(ball) || hitRightWall(ball)) {
				vx = -vx;
				bounceClip.play();
			}
			if (hitTopWall(ball)) {
				vy = -vy;
				bounceClip.play();
			}

			ball.move(vx, vy);
			pause(DELAY);

			GObject collider = getColliderObject(ball);
			if (collider != null) {
				bounceClip.play();
				if (collider != paddle) {
					remove(collider);
					BRICK_NUMBER--;
					vy = -vy;

				} else { // when it hits the paddle, even on the side, always go back up.
					vy = -Math.abs(vy);
				}
			}

			// When the ball dies, display the message and resets the ball for the next try
			// (with click).
			if (hitBottom(ball)) {
				CHANCES--;
				if (CHANCES > 0) { // When the life runs out, ends the game
					GLabel retry = new GLabel("Click to Retry. You have " + (CHANCES) + " more chance(s).");
					retry.setFont("Courier-14");
					double x = (getWidth() - retry.getWidth()) / 2;
					double y = (getHeight() - 3 * retry.getHeight());
					retry.setLocation(x, y);
					add(retry);
					ball.setLocation((getWidth() - BALL_RADIUS) / 2, (getHeight() - BALL_RADIUS) / 2);
					vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
					if (rgen.nextBoolean(0.5))
						vx = -vx;
					waitForClick();
					remove(retry);
				} else {
					remove(ball);
					GLabel lose = new GLabel("You LOSE");
					lose.setFont("Courier-36");
					lose.setColor(Color.MAGENTA);
					lose.setLocation((getWidth() - lose.getWidth()) / 2, (getHeight() - lose.getHeight()) / 2);
					add(lose);
				}
			}
		}
		GLabel complete = new GLabel("You Win!");
		complete.setFont("Courier36");
		complete.setLocation((getWidth() - complete.getWidth()) / 2, (getHeight() - complete.getHeight()) / 2);
		add(complete);
	}

	private void setUpGame() {
		// setup bricks
		for (int num = 0; num < NBRICK_COLUMNS; num++) {
			for (int row = 0; row < NBRICK_ROWS; row++) {
				double x = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH - (NBRICK_COLUMNS - 1) * BRICK_SEP) / 2
						+ row * (BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + BRICK_HEIGHT * num + BRICK_SEP * num;
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);

				// color of bricks
				if (num % 10 == 0 || num % 10 == 1) {
					Color rainbow = Color.RED;
					brick.setColor(rainbow);
				}
				if (num % 10 == 2 || num % 10 == 3) {
					Color rainbow = Color.ORANGE;
					brick.setColor(rainbow);
				}
				if (num % 10 == 4 || num % 10 == 5) {
					Color rainbow = Color.YELLOW;
					brick.setColor(rainbow);
				}
				if (num % 10 == 6 || num % 10 == 7) {
					Color rainbow = Color.GREEN;
					brick.setColor(rainbow);
				}
				if (num % 10 == 8 || num % 10 == 9) {
					Color rainbow = Color.CYAN;
					brick.setColor(rainbow);
				}
				brick.setFilled(true);
				add(brick);
			}
		}

		// setup-paddle
		paddle.setLocation((getWidth() - PADDLE_WIDTH) / 2, getHeight() - PADDLE_Y_OFFSET);
		paddle.setFilled(true);
		add(paddle);

		// setup- ball
		ball.setFilled(true);
		ball.setLocation((getWidth() - BALL_RADIUS) / 2, (getHeight() - BALL_RADIUS) / 2);
		add(ball);
	}

	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();

		// preventing the paddle to exiting the canvas
		if (mouseX <= PADDLE_WIDTH / 2) {
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
		} else {
			if (mouseX >= (getWidth() - PADDLE_WIDTH / 2)) {
				paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
			} else {
				paddle.setLocation(mouseX - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET);
			}
		}
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() > getWidth() - BALL_RADIUS * 2;
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() < -1;
	}

	private boolean hitBottom(GOval ball) {
		return ball.getY() >= (getHeight() - 2 * BALL_RADIUS);
	}

	private GObject getColliderObject(GOval ball) {

		// Four corners of the ball for collision
		double bx = ball.getX();
		double by = ball.getY();
		double bxr = ball.getX() + 2 * BALL_RADIUS;
		double byb = ball.getY() + 2 * BALL_RADIUS;

		GObject topLeft = getElementAt(bx, by);
		if (topLeft != null) {
			return (topLeft);
		}
		GObject topRight = getElementAt(bxr, by);
		if (topRight != null) {
			return (topRight);
		}
		GObject bottomLeft = getElementAt(bx, byb);
		if (bottomLeft != null) {
			return (bottomLeft);
		}
		GObject bottomRight = getElementAt(bxr, byb);
		if (bottomRight != null) {
			return (bottomRight);
		}
		return null;
	}

}
