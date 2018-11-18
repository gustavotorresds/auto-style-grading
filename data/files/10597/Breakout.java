/*
 * File: Breakout.java
 * -------------------
 * Name: Tiffany Cartagena
 * Section Leader: James Mayclin
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

	public static final int totalBricks = NBRICK_ROWS * NBRICK_COLUMNS;

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

	GRect paddle = null;

	GOval ball = null;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private double vx = rgen.nextDouble(3.0, 5.0);

	private double vy = 5.0;

	int numberOfTurns = NTURNS;

	int bricksRemoved = 0;


	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		setup();
		addMouseListeners();
		while(gameOver() != true) {
		playGame();
		}
		// Displays "Game over." message if turns run out.
		if(numberOfTurns == 0) {
		GLabel gameover = new GLabel("Game Over.");
		gameover.setLocation(CANVAS_WIDTH/2 - gameover.getWidth()/2, CANVAS_HEIGHT/2 + gameover.getAscent());
		remove(ball);
		add(gameover);
		}
		// Displays "You win!" message if all bricks are removed.
		if(bricksRemoved == totalBricks) {
		GLabel youwin = new GLabel("You win!");
		youwin.setLocation(CANVAS_WIDTH/2 - youwin.getWidth()/2, CANVAS_HEIGHT/2 + youwin.getAscent());
		remove(ball);
		remove(paddle);
		add(youwin);
		}
	}

	// This method determines if the game is over.
	private boolean gameOver() {
	if (numberOfTurns == 0 || bricksRemoved == totalBricks) {
	return true;
	} else {
	return false;
	}
	}

	// This method sets up the game.
	private void setup() {
	createBrickRow();
	createPaddle();
	createBall();
	}

	//This method created and colors the rows.
	private void createBrickRow() {
	for(int i = 0; i < NBRICK_ROWS; i ++) {
	double y = BRICK_Y_OFFSET + (BRICK_HEIGHT+BRICK_SEP)*i;
	for( int j = 0; j< NBRICK_ROWS; j++) {
	double x = (CANVAS_WIDTH/2 - (NBRICK_ROWS/2*(BRICK_WIDTH + BRICK_SEP)) + BRICK_SEP/2 + (BRICK_WIDTH+BRICK_SEP) * j);
	GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
	brick.setFilled(true);
	brick.setColor(rowColor(i));
	add(brick);
	}
	}
	}

	// This method determines the color for each row.
	private Color rowColor(int i) {
	switch(i) {
	case 0:
	case 1:
	return Color.RED;
	case 2:
	case 3:
	return Color.ORANGE;
	case 4:
	case 5:
	return Color.YELLOW;
	case 6:
	case 7:
	return Color.GREEN;
	default:
	return Color.CYAN;
	}
	}

	// This method creates the paddle.
	private void createPaddle() {
	paddle = new GRect(CANVAS_WIDTH/2 - PADDLE_WIDTH/2, CANVAS_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
	paddle.setFilled(true);
	add(paddle);
	}

	// This method creates the ball.
	private void createBall() {
	ball = new GOval(CANVAS_WIDTH/2 - BALL_RADIUS, CANVAS_HEIGHT/2 - 2*BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
	ball.setFilled(true);
	add(ball);
	setVelocity();
	}

	 
	// This method moves the paddle when mouse moved.
	public void mouseMoved(MouseEvent e) {
	double mouseX = e.getX();
	if(mouseX <= CANVAS_WIDTH-PADDLE_WIDTH)
	paddle.setLocation(mouseX, CANVAS_HEIGHT - PADDLE_Y_OFFSET);
	pause(DELAY);

	}

	// This method plays the game.
	private void playGame(){
	ballMovement();
	}
	 
	// This method randomly sets initial velocity.
	private void setVelocity() {
	vx = rgen.nextDouble(3.0, 5.0);
	vy = 5.0;
	if(rgen.nextBoolean(.5)) {
	vx = -vx;
	}
	}

	 
	//  This method moves controls the ball's movement.
	private void ballMovement() {
	ball.move(vx,  vy);
	pause(DELAY);
	bounce();
	}

	// This method causes the ball to change velocity when colliding with walls or objects on the canvas.
	private void bounce() {
	GObject collider = getCollidingObject();
	if (ball.getX() <= 0 || ball.getX() >= CANVAS_WIDTH - 2*BALL_RADIUS) {
	vx = -vx;
	} else if (ball.getY() <= 0) {
	vy = -vy;
	} else if (collider == paddle) {
	if (vy > 0) vy = -vy;
	} else if (collider != null) {
	remove(collider);
	bricksRemoved++;
	vy = -vy;
	} else if (ball.getBottomY() >= CANVAS_HEIGHT) {
	numberOfTurns --;
	ball.setLocation(CANVAS_WIDTH/2 - BALL_RADIUS, CANVAS_HEIGHT/2 - 2*BALL_RADIUS);
	setVelocity();
	}
	}

	// This method check if the ball has collided with an object.
	private GObject getCollidingObject() {
	GObject collider = null;
	if(getElementAt(ball.getX(), ball.getY()) != null) {
	collider = getElementAt(ball.getX(), ball.getY());
	} else if(getElementAt(ball.getX(), ball.getBottomY()) != null) {
	collider = getElementAt(ball.getX(), ball.getBottomY());
	} else if (getElementAt(ball.getRightX(), ball.getY()) != null) {
	collider = getElementAt(ball.getRightX(), ball.getY());
	} else if(getElementAt(ball.getRightX(), ball.getBottomY()) != null) {
	collider = getElementAt(ball.getRightX(), ball.getBottomY());
	}
	return collider;
	}
}
