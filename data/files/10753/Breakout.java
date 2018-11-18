/*
 * File: Breakout.java
 * -------------------
 * Name: Maddy	
 * Section Leader:Brahm 
 * 
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
	
	// Brick total 
	public static final int BRICK_TOTAL = NBRICK_COLUMNS * NBRICK_ROWS;
	

	GRect brick;
	GRect paddle; 
	GOval ball; 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	double vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
	double vy = VELOCITY_Y;
	int nturns;
	int brickcount;



	public void run() {
		setTitle("CS 106A Breakout");
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setUpGame();
		waitForClick();
		playGame();
	}
	// includes method that moves ball and adds the label "YOU WON!" if all bricks are removed 
	private void playGame() {
		if (rgen.nextBoolean(0.5)) vx = -vx;
		moveBall();	
		remove(ball);
		GLabel winner = new GLabel("YOU WON!");
		winner.setFont("Courier-36");
		winner.setColor(Color.PINK);
		add(winner, getWidth()/2 - winner.getWidth()/2, getHeight()/2 - winner.getAscent()/2);
	}

	// animation loop that bounces ball around the game
	private void moveBall() {
		while(brickcount < BRICK_TOTAL) {
			if(bumpedLeftWall(ball) || bumpedRightWall(ball)) {
				vx = -vx;
			}
			if (bumpedTopWall(ball)) {
				vy = -vy;
			}
			// 
			if (bumpedBottomWall(ball)) {
				remove(ball);
				nturns = nturns + 1;
				if (nturns < NTURNS) {	
					add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS );
					waitForClick();
				} else {
					// if player has used all of their turns adds "GAME OVER" label
					GLabel loser = new GLabel("GAME OVER");
					loser.setFont("Courier-36");
					loser.setColor(Color.BLACK);
					add(loser, getWidth()/2 - loser.getWidth()/2, getHeight()/2 - loser.getAscent()/2);
				}
			}
			ball.move(vx, vy);
			respondToCollisions();
			pause(DELAY);
		}
	}
	
	// determines whether or not the object was a brick or the paddle
	// if collider is a brick the brick is removed
	private void respondToCollisions() {
		GObject collider = getCollidingObject();
		if (collider != null && collider != paddle) {
			brickcount = brickcount + 1;   
			remove(collider);
			vy = -vy;
		}
		if(collider != null && collider == paddle) {
			vy = -vy;
		}
		
	}

	// gets the element with which the ball collided
	private GObject getCollidingObject() {
		//returns the element at each of the four corners of the ball 
		if (getElementAt(ball.getX(),ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} 
		if (getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS) != null){
			return getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
		}
		if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY()) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
		}
		if (getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS) != null) {
			return getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		}
		return null;
	}

	private boolean bumpedLeftWall(GOval ball) {
		return ball.getX() <= BALL_RADIUS*2;
	}
	
	private boolean bumpedRightWall(GOval ball) {
		return ball.getX() >= getWidth() - BALL_RADIUS*2;
	}
	
	private boolean bumpedTopWall(GOval ball) {
		return ball.getY() <= BALL_RADIUS*2; 
	}
	
	private boolean bumpedBottomWall(GOval ball) {
		return ball.getY() >= getHeight() - BALL_RADIUS*2;
	}

	private void setUpGame() {
		setUpBricks();
		makePaddle();
		addMouseListeners();
		makeBall();
	
	}
	
	private void makeBall() {
		// makes ball
		ball = new GOval(BALL_RADIUS*2, BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball, getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
	}

	private void makePaddle() {
		// makes paddle
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);	
	}
	
	public void mouseMoved(MouseEvent e) {
		double mouseX = e.getX();
		if(mouseX < getWidth() - PADDLE_WIDTH) {
		add(paddle, mouseX, getWidth() - PADDLE_Y_OFFSET);
		}
	}

	
	private void setUpBricks() {
		// two for loops that indicate what column and row 
		for (int column = 0; column < NBRICK_COLUMNS; column++) {
			for (int row = 0; row < NBRICK_ROWS; row++) {
				double x = (BRICK_SEP + (column * (BRICK_WIDTH + BRICK_SEP)));
				double y = BRICK_Y_OFFSET + (row * (BRICK_HEIGHT + BRICK_SEP));
				brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(getColor(row));
				add(brick);
			}
		}
	}

	private Color getColor(int row) {
		// specifies what brick color corresponds with certain row numbers
		if (row == 0 || row == 1 ) {
			return Color.RED;
		} if (row == 2 || row == 3) {
			return Color.ORANGE;
		} if (row == 4 || row == 5) {
			return Color.YELLOW;
		} if (row == 6 || row == 7) {
			return Color.GREEN; 
		} if (row == 8 || row == 9) {
			return Color.CYAN;
		}
		return null;
	}
}
