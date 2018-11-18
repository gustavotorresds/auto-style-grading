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
	public static final double DELAY = 720.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		setUpBricks();
		createPaddle();
		setScore();
		setLife();
		createBall();
		// If there are still bricks on the screen or player still have turns,
		// continue the game
		while (remainBricks > 0 && life > 0) {
			moveBall();
			checkCollision();
			// If the score is higher than 500, speed up the ball
			if (score < 500) pause(DELAY);
			else pause(DELAY / 1.5);
		}
	}
	
	private GRect paddle;
	private double PADDLE_Y;
	private GOval ball;
	private double vx, vy;
	private double ballX, ballY;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int remainBricks;
	// The bounce sound
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private int score;
	private GLabel scoreBoard = new GLabel("");
	private GLabel lifeBoard = new GLabel("");
	private int life;
	
	// Creating the rows of bricks at the top of the game
	private void setUpBricks() {
		Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};
		double x_left = (CANVAS_WIDTH - (BRICK_WIDTH + BRICK_SEP) * NBRICK_COLUMNS + BRICK_SEP) / 2;
		double x = x_left;
		double y = BRICK_Y_OFFSET;
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(colors[i / 2]);
				add(brick, x, y);
				x += BRICK_WIDTH + BRICK_SEP;
			}
			x = x_left;
			y += BRICK_HEIGHT + BRICK_SEP;
		}
		remainBricks = NBRICK_ROWS * NBRICK_COLUMNS;
	}
	
	// Create the paddle
	private void createPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		PADDLE_Y = CANVAS_HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, (CANVAS_WIDTH - PADDLE_WIDTH) / 2, PADDLE_Y);
		addMouseListeners();
	}
	
	// Let the paddle track the mouse
	public void mouseMoved(MouseEvent e) {
		double x = e.getX();
		double minX = PADDLE_WIDTH / 2;
		double maxX = CANVAS_WIDTH - PADDLE_WIDTH / 2;
		if (x < minX) x = minX;
		else if (x > maxX) x = maxX;
		paddle.setLocation(x - minX, PADDLE_Y);
	}
	
	// Create a ball and initialize the velocity of the ball
	private void createBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS *2);
		ball.setFilled(true);
		add(ball, CANVAS_WIDTH / 2 - BALL_RADIUS, CANVAS_HEIGHT / 2 - BALL_RADIUS);
		pause(DELAY * 30);
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;	
	}
	
	// Let the ball bounce off the walls
	// (hitting the bottom wall signifies the end of a turn)
	private void moveBall() {
		ball.move(vx, vy);
		ballX = ball.getX();
		ballY = ball.getY();
		if (ballX < 0) {
			vx = -vx;
		} else if (ballX > CANVAS_WIDTH - BALL_RADIUS * 2) {
			vx = -vx;
		} if (ballY < 0) {
			vy = -vy;
		} else if (ballY > CANVAS_HEIGHT - BALL_RADIUS * 2) {
			// If the ball hit the bottom,
			life--; // player lose one turn
			restartBall(); // serve next ball
			lifeBoard.setLabel("Life: " + life);
		}
	}
	
	// Tell whether the ball is colliding with another object in the window
	// If the ball hit a brick, remove the brick and bounce off
	// If the ball hit the paddle, bounce off
	private void checkCollision() {
		GObject collider = getCollidingObject();
		double ballBottomY = ballY + BALL_RADIUS * 2;
		if (collider == paddle) {
			// If the bottom of the ball lies between the bottom and the top of the paddle,
			// move it to the top of the paddle and let it bounce off (avoid sticky paddle)
			if (ballBottomY >= PADDLE_Y && ballBottomY <= PADDLE_Y + PADDLE_HEIGHT) {
				ball.move(0, -(ballBottomY - PADDLE_Y));
				vy = -vy;
			}
			// If the top of the ball is lower than the top of the paddle,
			// move it to the bottom of the paddle and don't let it bounce off (avoid sticky paddle)
			else if (ballY > PADDLE_Y) {
				ball.move(0, (PADDLE_Y + PADDLE_HEIGHT - ballY));
			}
			bounceClip.play();
		} else if (collider != null && collider != scoreBoard && collider != lifeBoard) {
			// If ball hit bricks,
			refreshScore(collider); // add on points
			remove(collider);
			vy = -vy;
			bounceClip.play();
			remainBricks--;
		}
	}
	
	// Return the object involved in the collision, if any, and null otherwise.
	private GObject getCollidingObject() {
		GObject obj;
		for (int i = 0; i < 4; i++) {
			obj = getElementAt(ballX + (i % 1) * BALL_RADIUS * 2, ballY + (i / 2) * BALL_RADIUS * 2);
			if (obj != null) return obj;
		}
		return null;
	}
	
	// Display and initialize the score
	private void setScore() {
		score = 0;
		scoreBoard.setFont("Courier-24");
		scoreBoard.setColor(Color.BLUE);
		scoreBoard.setLabel("Score: " + score);
		add(scoreBoard, 10, getHeight() / 2);
	}
	
	// Update the score
	private void refreshScore(GObject obj) {
		if (obj.getColor() == Color.CYAN) score += 5;
		else if (obj.getColor() == Color.GREEN) score += 10;
		else if (obj.getColor() == Color.YELLOW) score += 15;
		else if (obj.getColor() == Color.ORANGE) score += 20;
		else if (obj.getColor() == Color.RED) score += 25;
		scoreBoard.setLabel("Score: " + score);
	}
	
	// Display and initialize the turns left
	private void setLife() {
		life = NTURNS;
		lifeBoard.setFont("Courier-24");
		lifeBoard.setColor(Color.RED);
		lifeBoard.setLabel("Life: " + life);
		add(lifeBoard, 10, getHeight() / 2 + scoreBoard.getAscent());
		
	}
	
	// Remove the old ball and serve a new ball
	private void restartBall() {
		remove(ball);
		createBall();
	}
}
