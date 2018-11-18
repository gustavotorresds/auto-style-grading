
/*
  * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

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

	// Adding for Ball Diametre to facilitate GOval construction//
	private static final double BALL_DIAMETER = 20;

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

	// Instance variables for brick, paddle and ball//

	private GRect paddle = new GRect((CANVAS_WIDTH / 2) - (PADDLE_WIDTH / 2), CANVAS_HEIGHT - PADDLE_Y_OFFSET,
			PADDLE_WIDTH, PADDLE_HEIGHT);

	private GOval ball = new GOval(0, 0, BALL_DIAMETER, BALL_DIAMETER);

	// instance variable expression for ball velocity//

	private double vx, vy;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private int lives = NTURNS;

	// RUN METHOD STARTS HERE//

	public void run() {
		setUp();
		waitForClick();

		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;

		// brick count here//

		int numberbricks = NBRICK_ROWS * NBRICK_COLUMNS;

		// animation loop starts here//
		GLabel end;

		for (int i = 0; i < NTURNS; i++) {

			while (true) {

				ball.move(vx, vy);
				checkWall();

				// checks type of collision and reverse velocities and components accordingly//

				GObject collider = checkCollisions();
				if (collider == paddle) {
					vy = -vy;
				} else if (collider != null && collider != paddle) {
					remove(collider);
					vy = -vy;
					numberbricks--;
				}

				pause(DELAY);

				// losing lives, losing and winning conditions all under here with messages to
				// player//

				if (ball.getY() >= getHeight() - BALL_DIAMETER) {

					end = new GLabel("Dam son!   " + (lives - 1) + " lives left - click again noob");
					end.setFont(CENTER);
					add(end, getWidth() / 4, getHeight() / 3);
					lives--;
					break;
				}

				if (numberbricks == 0) {
					remove(ball);
					end = new GLabel("AWESOME VICTORY!");
					end.setFont(CENTER);
					add(end, getWidth() / 2, getHeight() / 2);
					break;

				}
			}
			if (lives != 0) {
				remove(ball);
				ball = new GOval(0, 0, BALL_DIAMETER, BALL_DIAMETER);
				buildBall();
				waitForClick();
				remove(end);
			}

			if (lives == 0) {
				remove(end);
				remove(paddle);
				remove(ball);
				end = new GLabel("PWNED");
				end.setFont(CENTER);
				add(end, getWidth() / 2, getHeight() / 2);
			}

		}
	}

	// allows player access to mouse commands, if loops make sure the paddle doesnt
	// go beyond the edges //

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		paddle.setCenterX(x);
		if (paddle.getX() <= 0) {
			paddle.setX(0);
		} else if (paddle.getX() >= getWidth() - PADDLE_WIDTH) {
			paddle.setX(getWidth() - PADDLE_WIDTH);
		}
	}

	// public void mouseClicked(MouseEvent e) When animation start (i.e mouse
	// clicked), make title disappear and launch
	// ball//

	private void setUp() {

		// Setting up the world's physical appearance//

		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		buildColouredBricks();
		buildColoredPaddle();
		buildBall();

	}

	// evaluating all the ways in which ball can collide with objects, specifically
	// points of ball//

	private GObject checkCollisions() {

		GObject topleft = getElementAt(ball.getX(), ball.getY());
		if (topleft != null) {
			return (topleft);
		}

		GObject topright = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		if (topleft != null) {
			return (topright);
		}

		GObject bottomleft = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		if (bottomleft != null) {
			return (bottomleft);
		}

		GObject bottomright = getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		if (bottomleft != null) {
			return (bottomright);
		}

		return null;

	}

	// this method reflects the ball against all walls except the bottom one//

	private void checkWall() {

		double right = getWidth() - BALL_DIAMETER;
		if (ball.getX() >= right || ball.getX() <= 0) {
			vx = -vx;
		}

		if (ball.getY() <= 0) {
			vy = -vy;
		}

	}

	private void buildColouredBricks() {

		/*
		 * This methods adds the coloured blocks at the top// Nested for-loop necessary
		 * here to create rows and columns, r and c represent rows and columns
		 */

		for (int r = 0; r < NBRICK_ROWS; r++) {

			for (int c = 0; c < NBRICK_COLUMNS; c++) {

				double x = (CANVAS_WIDTH / NBRICK_ROWS) - (BRICK_WIDTH - BRICK_SEP / 2) + (BRICK_WIDTH * c);
				double y = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * r;
				GRect brick = new GRect(x + BRICK_SEP * c, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setColor(java.awt.Color.black);
				add(brick);

				// if statement to color bricks two rows at a time//
				if (r == 1 || r == 0) {
					brick.setColor(Color.RED);
					brick.setFilled(true);
				}
				if (r == 2 || r == 3) {
					brick.setColor(Color.ORANGE);
					brick.setFilled(true);
				}
				if (r == 4 || r == 5) {
					brick.setColor(Color.YELLOW);
					brick.setFilled(true);
				}
				if (r == 6 || r == 7) {
					brick.setColor(Color.GREEN);
					brick.setFilled(true);
				}
				if (r == 8 || r == 9) {
					brick.setColor(Color.CYAN);
					brick.setFilled(true);

				}
			}

		}

	}

	// builds OG paddle//

	private void buildColoredPaddle() {
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}

	// builds ball//

	private void buildBall() {
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		add(ball, (getWidth() / 2) - BALL_RADIUS, (getHeight() / 2) - BALL_RADIUS);
	}

}
