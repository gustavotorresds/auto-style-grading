/*
 * File: Breakout.java
 * -------------------
 * Name: Eric Brandon Kam
 * Section Leader: Avery Wang
 * 
 * This file will eventually implement the game of Breakout. Breakout is essentially the
 * arcade game "Brick Breaker." The player gets three lives to destroy all the bricks on the
 * screen by bouncing the ball off of the paddle. If the ball contacts the wall under the paddle,
 * the player loses a life and the ball resets.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {
	
	// given constants
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;
	
	public static final int NBRICK_COLUMNS = 10;
	public static final int NBRICK_ROWS = 10;
	public static final double BRICK_SEP = 4;
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);
	public static final double BRICK_HEIGHT = 8;
	public static final double BRICK_Y_OFFSET = 70;
	
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;
	public static final double PADDLE_Y_OFFSET = 30;
	
	public static final double BALL_RADIUS = 10;
	public static final double VELOCITY_Y = 3.0;
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;
	
	public static final double DELAY = 1000.0 / 60.0;
	public static final int NTURNS = 3;
	
	// added constants
	private static final String MESSAGE_IF_WIN = "YOU WIN!!!";
	private static final String MESSAGE_IF_LOSE = "GAME OVER";

	// added instance variables
	private GRect paddle = null;
	private GOval ball = null;
	private int bricksRemaining = NBRICK_COLUMNS * NBRICK_ROWS;
	private int turnsRemaining = NTURNS;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	/* The game will set up all the graphics on the screen and then proceed to run an animation
	 * loop until the game is over: Either the player loses and runs out of lives or wins and
	 * breaks every brick. A final message is displayed according to the outcome.
	 */
	public void run() {
		setUp();
		while (!gameOver()) {
			moveBall();
			checkForCollisions();
			pause(DELAY);
		}
		endResult();
	}

	private void setUp() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		addBricks();
		addPaddle();
		addBall();
		addMouseListeners();
	}

	/* The outside "for loop" builds rows of bricks, while the nested "for loop" builds columns
	 * of bricks. The color of a brick is chosen based on the row the brick is in.
	 */
	private void addBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICK_COLUMNS; j++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				double xposition = (getWidth() / 2.0 -
						(NBRICK_COLUMNS / 2.0 * BRICK_WIDTH) -
						((NBRICK_COLUMNS - 1.0) / 2.0) * BRICK_SEP) +
						(j * (BRICK_WIDTH + BRICK_SEP));
				double yposition = BRICK_Y_OFFSET + (i * (BRICK_HEIGHT + BRICK_SEP));
				add(brick, xposition, yposition);
				
				if (i == 0 || i == 1) {
					brick.setColor(Color.RED);
				}
				if (i == 2 || i == 3) {
					brick.setColor(Color.ORANGE);
				}
				if (i == 4 || i == 5) {
					brick.setColor(Color.YELLOW);
				}
				if (i == 6 || i == 7) {
					brick.setColor(Color.GREEN);
				}
				if (i == 8 || i == 9) {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}
	
	private void addPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		double xposition = getWidth() / 2.0 - PADDLE_WIDTH / 2.0;
		double yposition = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, xposition, yposition);
	}
	
	// adds ball to the center of the screen
	private void addBall() {
		ball = new GOval(BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
		ball.setFilled(true);
		double xposition = getWidth() / 2.0 - BALL_RADIUS;
		double yposition = getHeight() / 2.0 - BALL_RADIUS;
		add(ball, xposition, yposition);
	}
	
	// the middle of the paddle stays with the point of the cursor as the mouse moves
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if (mouseX >= (PADDLE_WIDTH / 2.0) && mouseX <= (getWidth() - PADDLE_WIDTH / 2.0)) {
			paddle.setX(mouseX - PADDLE_WIDTH / 2.0);
		}
	}
	
	// clicking the mouse gives the ball initial velocity components
	public void mouseClicked(MouseEvent e) {
		if (vx == 0 && vy == 0) {
			vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			vy = VELOCITY_Y;
		}
	}
	
	// if the ball encounters a wall, the correct velocity component will adjust accordingly
	// this obeys the laws of incidence and reflection
	private void moveBall() {
		ball.move(vx, vy);
		if (hitLeftWall() || hitRightWall()) {
			vx = -vx;
		}
		if (hitTopWall() || hitBottomWall()) {
			vy = -vy;
		}
	}
	
	private boolean hitLeftWall() {
		return(ball.getX() <= 0);
	}
	
	private boolean hitRightWall() {
		return(ball.getRightX() >= getWidth());
	}
	
	private boolean hitTopWall() {
		return(ball.getY() <= 0);
	}
	
	private boolean hitBottomWall() {
		return(ball.getBottomY() >= getHeight());
	}
	
	// this method decides whether if and how to behave if the ball contacts an object on the canvas
	private void checkForCollisions() {
		GObject collider = getCollidingObject();  // contacted object will generically be named "collider"
		if (collider != null) {
			if (hitColliderTop(collider) || hitColliderBottom(collider)) {
				if (collider == paddle) {
					vy = -Math.abs(vy);  // allows more accurate user control of paddle
				} else {
					vy = -vy;
				}
			}
			if (collider != paddle) {  // objects that aren't the paddle will be bricks
				remove(collider);
				bricksRemaining--;
			}
		}
		if (hitBottomWall()) {  // the ball will reset after hitting the bottom of the screen
			vx = 0;
			vy = 0;
			turnsRemaining--;
			if (turnsRemaining != 0) {
				remove(ball);
				addBall();
			}
		}
	}
	
	
	// individually tests the four corners of the ball for objects until one is found 
	private GObject getCollidingObject() {
		GObject collider = null;
		if (collider == null) {
			collider = getElementAt(ball.getRightX(), ball.getY());
		}
		if (collider == null) {
			collider = getElementAt(ball.getX(), ball.getY());
		}
		if (collider == null) {
			collider = getElementAt(ball.getX(), ball.getBottomY());
		}
		if (collider == null) {
			collider = getElementAt(ball.getRightX(), ball.getBottomY());
		}
		return(collider);
	}
	
	
	/* IMPORTANT: The ball will only react when colliding with the top and bottom of objects.
	 * Therefore, the ball is able to pass horizontally through objects.
	 */
	private boolean hitColliderTop(GObject collider) {
		return(ball.getBottomY() >= collider.getY());
	}
	
	private boolean hitColliderBottom(GObject collider) {
		return(ball.getY() <= collider.getBottomY());
	}
	
	private boolean gameOver() {
		return(bricksRemaining == 0 || turnsRemaining == 0);
	}
	
	// centered text informing the player of a win or loss
	private void endResult() {
		GLabel winOrLose = new GLabel("");
		if (bricksRemaining == 0) {
			winOrLose.setLabel(MESSAGE_IF_WIN);
		} else {
			winOrLose.setLabel(MESSAGE_IF_LOSE);
		}
		double xposition = getWidth() /2.0 - winOrLose.getWidth() / 2.0;
		double yposition = getHeight() / 2.0 + winOrLose.getAscent() / 2.0;
		add(winOrLose, xposition, yposition);
	}
	
}
