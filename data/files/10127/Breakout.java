/*
 * File: Breakout.java
 * -------------------
 * Name: Jasmine Sun
 * Section Leader: Semir Shafi
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
	public static final double VELOCITY_Y = 7.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;
	
	// Creates some useful graphics
	GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);	
	GOval ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
	GLabel youWin = new GLabel("grats u won!! :)");
	GLabel youLose = new GLabel("sad reax :( u lost");
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		addMouseListeners();

		setBricks();
		setBall();
		playBall();
	}
	/*
	 * This method places the ball in the center of the screen.
	 * Pre: none
	 * Post: Ball in center of screen, ready to be launched. 
	 */
	private void setBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball.setFilled(true);
		add(ball, x, y);
	}
	
	/*
	 * This method shows how the ball interacts with the game world. The ball 
	 * moves toward the bottom of the screen and bounces off objects like the
	 * paddle, bricks, or walls. Bricks disappear when hit, and win/loss messages
	 * are displayed when all bricks are eliminated or the ball hits the bottom
	 * of the screen.
	 * Pre: Ball, bricks, and paddle are set up.
	 * Post: Player can play game of Breakout.
	 */
	private void playBall() {
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		double vy = VELOCITY_Y;
		if (rgen.nextBoolean(0.5)) vx = -vx;
		int count = 0;
		int nTurn = 0;
		while(true) {
			if (ball.getY() <= getHeight() - 2*BALL_RADIUS) {
				if (ball.getX() <= 0 || ball.getX() >= getWidth() - 2*BALL_RADIUS) {
					vx = -vx;
				} else if (ball.getY() <= 0) {
					vy = -vy;
				} else if (getCollider() == paddle) {
					bounceClip.play();
					vy = -vy;
				} else if (getCollider() != null && getCollider() != youWin && getCollider() != youLose){
					bounceClip.play();
					vy = -vy;
					remove(getCollider());
					count = count + 1;
				} 
				if (count == NBRICK_COLUMNS * NBRICK_ROWS) {
					remove(ball);
					showWinMessage();
					nTurn = nTurn + 1;
				} else {
					ball.move(vx, vy);	
					pause(DELAY);
				}
			} else {
				remove(ball);
				nTurn = nTurn + 1;
				if (nTurn < 3) {
					setBall();
				} else if (nTurn == 3) {
					showLossMessage();
				}
			}
		}
	}
	
	/*
	 * Displays a message to notify the player they have won their
	 * previous turn. 
	 * Pre: Player eliminates all bricks in a turn.
	 * Post: Green "grats you won!! :)" message appears on screen center.
	 */
	private void showWinMessage() {
		remove(youLose);
		youWin.setFont("Courier-45");
		youWin.setColor(Color.GREEN);
		double x = (getWidth() - youWin.getWidth()) / 2;
		double y = (getHeight() - youWin.getAscent()) / 2;
		add(youWin, x, y);		
	}

	/*
	 * Displays a message to notify the player they have lost their
	 * previous turn.
	 * Pre: Player allows ball to reach bottom of screen.
	 * Post: Red "sad reax :( you lost" appears on screen center.
	 */
	private void showLossMessage() {
		remove(youWin);
		youLose.setFont("Courier-45");
		youLose.setColor(Color.RED);
		double x = (getWidth() - youLose.getWidth()) / 2;
		double y = (getHeight() - youLose.getAscent()) / 2;
		add(youLose, x, y);		
	}

	/*
	 * Method returns what object the ball has collided with (e.g.
	 * paddle, brick, or message). 
	 * Pre: Ball is moving around screen.
	 * Post: Returns object to playBall(); method. 
	 */
	private GObject getCollider() {
		GObject collider = null;
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			collider = getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null) {
			collider = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		}
		return collider;
	}

	/*
	 * Allows paddle to follow mouse movements across the screen.
	 * Pre: Paddle is set up in screen lower center. 
	 * Post: Paddle follows the mouse movements along the x-axis, 
	 * but has a fixed y position.
	 */
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		double paddleY = getHeight() - PADDLE_Y_OFFSET;
		paddle.setFilled(true);
		if (mouseX >= 0 && mouseX <= getWidth() - PADDLE_WIDTH) {
			if(paddle != null) {
				add(paddle, mouseX, paddleY);
			}
		}
	}

	/*
	 * Sets up centered grid of bricks at the top of the screen. 
	 * Each 2 rows has a different color. 
	 * Pre: none
	 * Post: Grid of multicolored bricks show up centered at top of screen.
	 */
	private void setBricks() {	
		for (int c = 0; c < NBRICK_COLUMNS; c++) {
			for (int r = 0; r < NBRICK_ROWS; r++) {
				setBrick(r, c);
			}
		}
	}

	/*
	 * Creates brick and outlines position and color of bricks in
	 * grid of top of screen.
	 * Pre: none
	 * Post: Brick is added to screen, with position and color corresponding
	 * to the row and column. 
	 */
	private void setBrick(int rowNumber, int colNumber) {
		double centerX = (getWidth() - NBRICK_ROWS*(BRICK_WIDTH + BRICK_SEP) + BRICK_SEP)/2;
		double x = centerX + colNumber * (BRICK_WIDTH + BRICK_SEP);
		GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
		double y = BRICK_Y_OFFSET + rowNumber * (BRICK_HEIGHT + BRICK_SEP);
		if (rowNumber == 0 || rowNumber == 1) {
			brick.setColor(Color.RED);
		} else if (rowNumber == 2 || rowNumber == 3) {
			brick.setColor(Color.ORANGE);
		} else if (rowNumber == 4|| rowNumber == 5) {
			brick.setColor(Color.YELLOW);
		} else if (rowNumber == 6 || rowNumber == 7) {
			brick.setColor(Color.GREEN);
		} else if (rowNumber == 8 || rowNumber == 9) {
			brick.setColor(Color.CYAN);
		}
		brick.setFilled(true);
		add(brick, x, y);
	}

	
}
