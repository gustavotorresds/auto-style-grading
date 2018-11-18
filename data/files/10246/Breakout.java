/*
 * File: Breakout.java
 * -------------------
 * Name: Aaron Wingad
 * Section Leader: Julia Daniels
 * 
 * This program creates the classic brick-breaking game: Breakout!
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
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	// instance variables needed to run the game
	private int turns = NTURNS;
	private int bricks = NBRICK_COLUMNS*NBRICK_ROWS;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);	
	private GOval ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);
	
	// the basic run method is separated into setup and game play
	// this also adds the mouse event listeners
	public void run() {
		breakoutSetup();
		breakoutPlay();
		addMouseListeners();
	}

	// this sets up play though setting the title, canvas size, creating the bricks and paddle
	private void breakoutSetup() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		createBricks();
		createPaddle();
	}

	// the bricks are created using a the double for loop below
	private void createBricks() {
		for(double currentRow = 0; currentRow < NBRICK_ROWS; currentRow++) {
			for(double currentColumn = 0; currentColumn < NBRICK_COLUMNS; currentColumn++) {
				GRect rect = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				double xCoordinate = 1.5*BRICK_SEP+currentColumn*(BRICK_WIDTH+BRICK_SEP);
				double yCoordinate = BRICK_Y_OFFSET+currentRow*(BRICK_HEIGHT+BRICK_SEP);
				add(rect, xCoordinate, yCoordinate);
				rect.setFilled(true);

				// this creates the multi-colored rows
				if (currentRow < 2) {
					rect.setColor(Color.RED);
				} else if (currentRow < 4) {
					rect.setColor(Color.ORANGE);	
				} else if (currentRow < 6) {
					rect.setColor(Color.YELLOW);	
				} else if (currentRow < 8) {
					rect.setColor(Color.GREEN);
				} else if (currentRow < 10){
					rect.setColor(Color.CYAN);
				}
			}
		}
	}

	// adds the paddle
	private void createPaddle() {
		double px = 0.5*getWidth()-0.5*PADDLE_WIDTH;
		double py = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle.setFilled(true);
		add(paddle, px, py);
		addMouseListeners();
	}

	// tracks the paddle with the mouse
	public void mouseMoved(MouseEvent e) {
		double checkx = e.getX();
		if (checkx > getWidth()-PADDLE_WIDTH); {
			double px = getWidth()-PADDLE_WIDTH;
			double py = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
			paddle.setLocation(px,py);
		}
		if (checkx <= getWidth()-PADDLE_WIDTH) {
			double px = e.getX();
			double py = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
			paddle.setLocation(px,py);
		}
	}

	// creates the ball in the middle of the screen
	private void createBall() {
		double bx = 0.5*getWidth();
		double by = 0.5*getHeight();
		ball.setFilled(true);
		add(ball, bx, by);
	}

	// main while loop of game play
	private void breakoutPlay() {
		while(turns > 0 && bricks > 0) {
			double vx = rgen.nextDouble(VELOCITY_X_MIN,VELOCITY_X_MAX);
			if (rgen.nextBoolean(0.5)) vx = -vx;
			double vy = VELOCITY_Y;
			waitForClick();
			createBall();
			while (hitBottomWall(ball) == false && bricks > 0) {
				if(hitLeftWall(ball) || hitRightWall(ball)) {
					vx = -vx;
				}
				if(hitTopWall(ball)) {
					vy = -vy;
				}
				ball.move(vx, vy);
				pause(DELAY);
				GObject collObj = getCollidingObject();
				if(collObj != null && collObj != paddle) {
					remove(collObj);
					bricks --;
					vy = -vy;
					ball.move(vx, vy);
				}
				if(collObj == paddle) {
					vy = -vy;
					ball.move(vx, vy);
				}	
			}
			turns --;
			remove(ball);
			}
		if (bricks < 1) {
			gameWin();
		}
		if (turns < 1) {
			gameOver();
		}
	}
	
	private void gameWin() {
		GLabel winning = new GLabel("CONGRATULATIONS YOU WON!");
		add(winning, getWidth()/2-winning.getWidth()/2, getHeight()/2);
	}
	
	private void gameOver() {
		GLabel lose = new GLabel("OH NO! YOU LOST!");
		add(lose, getWidth()/2-lose.getWidth()/2, getHeight()/2);
	}

	// checks all four corners of the ball for a collision
	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX()+BALL_RADIUS*2.0, ball.getY()) !=null) {
			return getElementAt(ball.getX()+BALL_RADIUS*2.0, ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2.0) != null) {
			return getElementAt(ball.getX(), ball.getY()+BALL_RADIUS*2.0);
		} else if (getElementAt(ball.getX()+BALL_RADIUS*2.0, ball.getY()+BALL_RADIUS*2.0) !=null) {
			return getElementAt(ball.getX()+BALL_RADIUS*2.0, ball.getY()+BALL_RADIUS*2.0);
		} else {
			return null;
		}
	}

	// four methods below create the walls that the ball bounces off from
	private boolean hitBottomWall(GOval ball) {
		return ball.getY() > getHeight();
	}

	private boolean hitTopWall(GOval ball) {
		return ball.getY() <= 0;
	}

	private boolean hitRightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}

	private boolean hitLeftWall(GOval ball) {
		return ball.getX() <= 0;
	}
}
