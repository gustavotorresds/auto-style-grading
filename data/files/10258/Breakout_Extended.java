
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

public class Breakout_Extended extends GraphicsProgram {

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

	// Number of turns
	public static final int NTURNS = 3;

	// specifies height at which rows should be formed. defined in bricksSetup
	// method.
	private double y;

	// specifies position of paddle, follows x coordinate of mouse.
	private double x;

	// Creates paddle, sets it as instance variable so position can be modified by
	// mouseMoved method
	private GRect paddle = new GRect(x, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);

	// Creates ball, sets it as instance variable so it can be modified by velocity
	// vectors
	private GOval ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);

	// Creates brick, sets it as instance variable so different methods can
	// form and place them as well as check whether any remain in game.
	private GRect brick;

	// velocity components for ball
	private double vx, vy;

	// randomly determines a number for ball's velocity in the x direction
	private RandomGenerator rgen = RandomGenerator.getInstance();

	// imports bounce sound audio clip to be played upon ball's collision
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size. In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		bricksSetup();
		addMouseListeners();
		createPaddle();
		createBall();
		waitForClick();
		ballMechanics();
		for (int n = 0; n < NTURNS - 1; n++) {
			add(ball, (getWidth() / 2) - BALL_RADIUS, (getHeight() / 2) - BALL_RADIUS);
			ballMechanics();
		}
	}

	// lays rows of bricks and sets colors for each row of bricks
	private void bricksSetup() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			y = (BRICK_HEIGHT * i + BRICK_Y_OFFSET + BRICK_SEP * (i - 1));
			if (i < 2) {
				colorBricks(Color.RED);
			} else if (i < 4) {
				colorBricks(Color.ORANGE);
			} else if (i < 6) {
				colorBricks(Color.YELLOW);
			} else if (i < 8) {
				colorBricks(Color.GREEN);
			} else if (i < NBRICK_ROWS) {
				colorBricks(Color.CYAN);
			}
		}
	}

	// positions bricks within row
	private void colorBricks(Color color) {
		for (int t = 0; t < NBRICK_COLUMNS; t++) {
			brick = new GRect(BRICK_WIDTH * t + BRICK_SEP * (t + 1), y, BRICK_WIDTH, BRICK_HEIGHT);
			brick.setColor(color);
			brick.setFilled(true);
			add(brick);
		}
	}

	// adds paddle to game
	private void createPaddle() {
		paddle.setColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
	}

	// ties movement of paddle to mouse
	public void mouseMoved(MouseEvent e) {
		x = e.getX() - PADDLE_WIDTH / 2;
		if (x < 0) {
			paddle.setBounds(0, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		} else if (x < (getWidth() - PADDLE_WIDTH)) {
			paddle.setBounds(x, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		} else {
			paddle.setBounds(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		}
	}

	// adds ball to game
	private void createBall() {
		ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball, (getWidth() / 2) - BALL_RADIUS, (getHeight() / 2) - BALL_RADIUS);
	}

	// determines ball velocity and how collisions affect the ball's direction
	private void ballMechanics() {
		vy = (VELOCITY_Y);
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5))
			vx = -vx;
		while (true) {
			ball.move(vx, vy);
			if (ball.getX() < 0)
				vx = -vx;
			if (ball.getX() > getWidth() - BALL_RADIUS * 2)
				vx = -vx;
			if (ball.getY() < 0)
				vy = -vy;
			if (ball.getY() > getHeight() - BALL_RADIUS * 2) {
				remove(ball);
				break;
			}
			paddleCollision();
			brickCollision();
			pause(DELAY);
		}
	}

	// lets ball find paddle and invert its velocity in the y direction upon
	// collision. plays bounce sound upon collision.
	private void paddleCollision() {
		if (ball.getY() < getHeight() - PADDLE_Y_OFFSET) {
			GObject isPaddlePresentOnLeft = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
			GObject isPaddlePresentOnRight = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
			if ((isPaddlePresentOnLeft == paddle) || (isPaddlePresentOnRight == paddle)) {
				vy = -vy;
				bounceClip.play();
			}
		}
	}

	// tests for presence of bricks, removes brick that ball is touching and inverts
	// ball's velocity in the y direction. plays bounce sound upon collision.
	private void brickCollision() {
		GObject isBrickPresentOnUpperLeft = getElementAt(ball.getX(), ball.getY());
		GObject isBrickPresentOnUpperRight = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		GObject isBrickPresentOnLowerLeft = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		GObject isBrickPresentOnLowerRight = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		if ((isBrickPresentOnUpperLeft != null) && (isBrickPresentOnUpperLeft != paddle)) {
			remove(isBrickPresentOnUpperLeft);
			vy = -vy;
			bounceClip.play();
		} else if ((isBrickPresentOnUpperRight != null) && (isBrickPresentOnUpperRight != paddle)) {
			remove(isBrickPresentOnUpperRight);
			vy = -vy;
			bounceClip.play();
		} else if ((isBrickPresentOnLowerLeft != null) && (isBrickPresentOnLowerLeft != paddle)) {
			remove(isBrickPresentOnLowerLeft);
			vy = -vy;
			bounceClip.play();
		} else if ((isBrickPresentOnLowerRight != null) && (isBrickPresentOnLowerRight != paddle)) {
			remove(isBrickPresentOnLowerRight);
			vy = -vy;
			bounceClip.play();
		}
	}
}