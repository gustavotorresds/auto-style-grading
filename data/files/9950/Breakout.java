
/*
 * File: Breakout.java
 * -------------------
 * Name: Tyler Su
 * Section Leader: Robbie Jones
 * 
 * Facebook rejected my internship application, so I took revenge. 
 * This program is an extended version of Breakout where you can now help
 * to free the world from the evil powers of all elite $ tech $ powers. Can you
 * save the day from Mark Zuckerberg's reign?
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
	public static final int NBRICK_ROWS = 5;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math
			.floor((CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = BRICK_WIDTH;

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
	public static final double VELOCITY_Y = 6.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns
	public static final int NTURNS = 3;

	private static final RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	private int lives = NTURNS;
	private int kicker = 0;
	private int totalBricks = NBRICK_COLUMNS * NBRICK_ROWS;
	private int score = 0;
	private boolean gameOn = true;

	private AudioClip bounceClip;
	private GRect paddle;
	private GOval ball;
	private GLabel scoreDisplay;
	private GLabel livesDisplay;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addMouseListeners();
		setUpGame();
		playGame();
		pause(3000);
		exit();

	}

	/* Sets up the audio, score, and lives display. Builds the brick and paddle */
	private void setUpGame() {
		
		bounceClip = MediaTools.loadAudioClip("bounce.au");
		livesDisplay = new GLabel("Lives: " + lives);
		scoreDisplay = new GLabel("Your current score is: " + score);
		add(scoreDisplay, getWidth() / 2 - scoreDisplay.getWidth() / 2, getHeight() - 5);
		add(livesDisplay, 10, 15);

		setUpBricks();
		setUpPaddle();
		makeBall();
	}

	/* Sets up the bricks */
	private void setUpBricks() {
		for (int j = 0; j < NBRICK_ROWS; j++) {
			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				GImage img = new GImage(rgen.nextInt(10, 22) + ".png");
				img.scale(BRICK_WIDTH / img.getWidth());
				add(img, (BRICK_SEP / 2) + (i * BRICK_WIDTH) + (i * BRICK_SEP),
						BRICK_Y_OFFSET + (j * (BRICK_HEIGHT + BRICK_SEP)));
			}
		}
	}

	/* Sets up the paddle */
	private void setUpPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		add(paddle, getWidth() / 2 - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET);
	}

	/* Makes a ball with a given vy */
	private void makeBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setFillColor(Color.BLACK);
		add(ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
		vy = VELOCITY_Y;
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
	}

	/* Plays game so long as game is on */
	private void playGame() {
		while (gameOn) {
			checkWalls();
			checkCollisions();
			moveBall();
		}
	}

	/* Checks walls for collisions */
	private void checkWalls() {
		if (ball.getX() + BALL_RADIUS * 2 > getWidth()) {
			vx = -vx;
		}
		if (ball.getX() < 0) {
			vx = -vx;
		}
		if (ball.getY() < 0) {
			vy = -vy;
		}
		if (ball.getY() > getHeight()) {
			lives--;
			livesDisplay.setLabel("Lives: " + lives);
			kicker = 0;
			if (lives > 0) {
				makeBall();
			} else {
				GLabel loser = new GLabel("Game Over. Loser.");
				add(loser, getWidth() / 2 - loser.getWidth() / 2, getHeight() / 2 - loser.getAscent() / 2);
				gameOn = false;
			}
		}
	}

	/* Moves ball forward */
	private void moveBall() {
		ball.move(vx, vy);
		pause(30);
	}

	/* Gets the colliding object by checking all four corners of the ball */
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		}
		if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
			return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		}
		if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
			return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		} else {
			return null;
		}
	}

	/*
	 * Gets the colliding object and program responds depending on whether it is
	 * the paddle, the score display or live display, or a block.
	 */
	private void checkCollisions() {
		GObject collider = getCollidingObject();
		if (collider != null) {
			if (collider == paddle) {
				vy = -vy;
				bounceClip.play();
				if (kicker == 2) {
					double multiplier = rgen.nextDouble(1.0, 1.6);
					vx *= multiplier;
					vy *= multiplier;
					kicker = 0;
				} else {
					kicker++;
				}
			} else if (collider == scoreDisplay || collider == livesDisplay) {
			} else {
				vy = -vy;
				bounceClip.play();
				remove(collider);
				score++;
				scoreDisplay.setLabel("Your current score is: " + score);
				totalBricks--;
				if (totalBricks == 0) {
					winnerCrazy();
				}
			}
		}
	}

	/* Graphics and animation displays if the user wins game */
	private void winnerCrazy() {
		clearScreen();

		for (int j = 0; j < getHeight() / (BRICK_HEIGHT + BRICK_SEP); j++) {
			pause(100);
			for (int i = 0; i < NBRICK_COLUMNS; i++) {
				GImage img = new GImage(rgen.nextInt(10, 22) + ".png");
				img.scale(BRICK_WIDTH / img.getWidth());
				add(img, (BRICK_SEP / 2) + (i * BRICK_WIDTH) + (i * BRICK_SEP), (j * (BRICK_HEIGHT + BRICK_SEP)));
			}
		}

		GImage chickenDinner = new GImage("mark.png");
		chickenDinner.scale(getWidth() / chickenDinner.getWidth());
		add(chickenDinner, 0, getHeight() / 2 - chickenDinner.getHeight() / 2);
		pause(5000);
		exit();
	}

	/* Covers screen with a white rectangle for the winner animation */
	private void clearScreen() {
		remove(paddle);
		remove(ball);
		GRect clear = new GRect(0, 0, getWidth(), getHeight());
		clear.setFilled(true);
		clear.setColor(Color.WHITE);
		clear.setFillColor(Color.WHITE);
		add(clear);
	}

	/* Detects mouse movement events and moves paddle accordingly */
	public void mouseMoved(MouseEvent e) {
		double dx = e.getX() - paddle.getX() - PADDLE_WIDTH / 2;
		paddle.move(dx, 0);
	}

}