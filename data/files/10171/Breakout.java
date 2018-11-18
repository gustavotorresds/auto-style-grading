/*
 * File: Breakout.java
 * -------------------
 * Name: Aaron Han
 * Section Leader: Robbie Jones
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
	public static final double VELOCITY_Y = 2.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 2.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	//instance variables
	private GRect paddle;
	private GOval ball;
	private double vx, vy;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int brickBroken = 0;
	private int turns = NTURNS;
	private int nBricks = NBRICK_COLUMNS * NBRICK_ROWS;

	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		paddle = makePaddle();
		ball = makeBall();
		setup();
		waitForClick();
		while(turns > 0 && brickBroken < nBricks ) {
			play();
		}
		if(brickBroken == nBricks) {
			displayWinMessage();
		}
		if(turns == 0) {
			displayLoseMessage();
		}
	}
	//mouseMoved method to track the paddle along the cursor
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		paddle.setLocation(mouseX-PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET);
		//next two if statements prevent parts of the paddle from going off the canvas when it's on the edge
		if(paddle.getX() < 0) {
			paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
		}
		if(paddle.getX() > getWidth() - PADDLE_WIDTH) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
		}
	}
	//setup portion of the code, adds the blocks, adds mouse listeners, and creates the paddle on the canvas
	private void setup() {
		addBricks();
		addMouseListeners();
		add(paddle);
	}
	//adding the bricks to the canvas
	private void addBricks() {
		for(int i = 0; i < NBRICK_COLUMNS; i++) {
			for(int j = 0; j < NBRICK_ROWS; j++) {
				GRect brick = new GRect(getWidth()/2.0 - NBRICK_ROWS*BRICK_WIDTH/2.0 - (NBRICK_ROWS-1)*BRICK_SEP/2.0 + (BRICK_WIDTH + BRICK_SEP) * j, BRICK_Y_OFFSET+(BRICK_HEIGHT + BRICK_SEP) * i, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(i % 10 < 2) {
					brick.setColor(Color.RED);
				}
				if(2 <= i % 10 && i % 10 < 4) {
					brick.setColor(Color.ORANGE);
				}
				if(4 <= i % 10 && i % 10 < 6) {
					brick.setColor(Color.YELLOW);
				}
				if(6 <= i % 10 && i % 10 < 8) {
					brick.setColor(Color.GREEN);
				}
				if(8 <= i % 10 && i % 10 < 10) {
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}
	public GRect makePaddle() {
		GRect paddle = new GRect(getWidth()/2-PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.GRAY);
		return(paddle);
	}	
	public GOval makeBall() {
		double size = BALL_RADIUS * 2;
		GOval ball = new GOval(size, size);
		ball.setFilled(true);
		ball.setColor(Color.BLUE);
		add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
		return ball;
	}
	private void play() {
		ball.move(vx, vy);
		if(topWall(ball)) {
			vy = -vy;
		}
		if(rightWall(ball) || leftWall(ball)) {
			vx = -vx;
		}
		if(bottomWall(ball)) {
			turns--;
			if(turns > 0) {
				remove(ball);
				add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
				waitForClick();
			}
		}
		GObject collider = getCollidingObject(ball);
		if(collider == paddle) {
				vy = -Math.abs(vy);
		} else if (collider != null) {
			remove(collider);
			vy = -vy;
			brickBroken++;
		}
		pause(DELAY);
	}
	//methods for checking if the ball touches the walls
	private boolean bottomWall(GOval ball) {
		return ball.getY() > getHeight() - ball.getHeight();
	}
	private boolean topWall(GOval ball) {
		return ball.getY() <= 0;
	}
	private boolean rightWall(GOval ball) {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	private boolean leftWall(GOval ball) {
		return ball.getX() <= 0;
	}
	//method for checking if the ball is hitting an object
	private GObject getCollidingObject(GOval ball) {
		double x = ball.getX();
		double y = ball.getY();
		//if the ball touches an object on any of its corners, then it returns the object touched
		//followed instructions from the handout, however has limitations due to the four corners of the bounding rectangle
		//not being the actual locations of the ball
		//better way to do this would be using radians, which we didn't learn how to use
		if(getElementAt(x , y) != null) {
			return(getElementAt(x,y));
		}
		if(getElementAt(x + 2 * BALL_RADIUS, y) != null) {
			return(getElementAt(x + 2 * BALL_RADIUS, y));
		}
		if(getElementAt(x , y + 2 * BALL_RADIUS) != null) {
			return(getElementAt(x , y + 2 * BALL_RADIUS));
		}
		if(getElementAt(x + 2 * BALL_RADIUS, y+ 2 * BALL_RADIUS) != null) {
			return(getElementAt(x + 2 * BALL_RADIUS, y+ 2 * BALL_RADIUS));
		}
		return null;
	}
	private void displayWinMessage() {
		removeAll();
		GLabel congrats = new GLabel("Congratulations, you won!");
		add(congrats, getWidth()/2.0 - congrats.getWidth() / 2.0, getHeight() / 2.0 + congrats.getAscent() / 2.0);
	}
	private void displayLoseMessage() {
		removeAll();
		GLabel loser = new GLabel("Loser, try again.");
		add(loser, getWidth()/2.0 - loser.getWidth() / 2.0, getHeight() / 2.0 + loser.getAscent() / 2.0);
	}
}
